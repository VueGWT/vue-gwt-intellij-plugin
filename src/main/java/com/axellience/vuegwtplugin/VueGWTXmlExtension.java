package com.axellience.vuegwtplugin;

import static com.axellience.vuegwtplugin.util.VueGWTPluginUtil.COMPONENT_QUALIFIED_NAME;

import com.axellience.vuegwtplugin.language.htmltemplate.HtmlTemplateLanguage;
import com.axellience.vuegwtplugin.util.VueGWTPluginUtil;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiImplUtil;
import com.intellij.psi.impl.source.xml.SchemaPrefix;
import com.intellij.psi.impl.source.xml.TagNameReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.HtmlXmlExtension;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class VueGWTXmlExtension extends HtmlXmlExtension {

  @Override
  public boolean isAvailable(PsiFile psiFile) {
    return psiFile.getLanguage() == HtmlTemplateLanguage.INSTANCE;
  }

  @Override
  public SchemaPrefix getPrefixDeclaration(XmlTag context, String namespacePrefix) {
    // From Vue.js plugin
    if ("v-bind".equals(namespacePrefix) || "v-on".equals(namespacePrefix)) {
      SchemaPrefix attribute = findAttributeSchema(context, namespacePrefix, 0);
      if (attribute != null) {
        return attribute;
      }
    }
    return super.getPrefixDeclaration(context, namespacePrefix);
  }

  private SchemaPrefix findAttributeSchema(XmlTag context, String namespacePrefix, int offset) {
    for (XmlAttribute xmlAttribute : context.getAttributes()) {
      if (xmlAttribute.getName().startsWith(namespacePrefix)) {
        return new SchemaPrefix(xmlAttribute,
            TextRange.create(offset, namespacePrefix.length()),
            namespacePrefix.substring(offset));
      }
    }
    return null;
  }

  @Nullable
  @Override
  public String[][] getNamespacesFromDocument(XmlDocument parent, boolean declarationsExist) {
    // Register vue-gwt namespace for vue-gwt:import tags
    String[][] namespaces = super.getNamespacesFromDocument(parent, false);
    if (namespaces == null) {
      return null;
    }

    String[][] newNamespaces = new String[namespaces.length + 1][2];
    System.arraycopy(namespaces, 0, newNamespaces, 0, namespaces.length);
    newNamespaces[namespaces.length] =
        new String[]{"vue-gwt", "http://axellience.com/vue-gwt"};
    return newNamespaces;
  }

  @Nullable
  @Override
  public TagNameReference createTagNameReference(ASTNode astNode, boolean b) {
    // Ugly but working code that find all components
    VirtualFile templateFile =
        astNode.getTreeParent().getPsi().getContainingFile().getVirtualFile();

    // Get the Java file for the template
    Optional<VirtualFile> optionalJavaFile =
        VueGWTPluginUtil.getJavaFileForTemplate(templateFile);
    if (!optionalJavaFile.isPresent()) {
      return super.createTagNameReference(astNode, b);
    }

    // Find the project for the current file
    Project project = astNode.getPsi().getProject();
    PsiFile file = PsiManager.getInstance(project).findFile(optionalJavaFile.get());
    if (!(file instanceof PsiJavaFile)) {
      return super.createTagNameReference(astNode, b);
    }

    PsiJavaFile psiJavaFile = (PsiJavaFile) file;
    String tagName = astNode.getText(); // Tag name of the current element

    // For all the classes in the Java file
    for (PsiClass psiClass : psiJavaFile.getClasses()) {
      PsiAnnotation[] annotations = psiClass.getAnnotations();
      PsiAnnotation componentAnnotation = null;
      for (PsiAnnotation annotation : annotations) {
        if (COMPONENT_QUALIFIED_NAME.equals(annotation.getQualifiedName())) {
          componentAnnotation = annotation;
          break;
        }
      }

      if (componentAnnotation == null) {
        continue;
      }

      PsiAnnotationMemberValue componentsValue =
          PsiImplUtil.findAttributeValue(componentAnnotation, "components");

      // TODO: Manage multiple values
      if (!(componentsValue instanceof PsiClassObjectAccessExpression)) {
        continue;
      }

      // classType is Class<MyComponent>
      PsiClassType classType =
          (PsiClassType) PsiImplUtil.getType((PsiClassObjectAccessExpression) componentsValue);

      // componentClassType is MyComponent
      PsiClassType componentClassType = (PsiClassType) classType.getParameters()[0];

      String componentTagName = VueGWTPluginUtil.componentToTagName(componentClassType);
      if (tagName.equals(componentTagName)) {
        return new VueGWTTagNameReference(astNode, componentClassType.resolve(), b);
      }
    }

    return super.createTagNameReference(astNode, b);
  }
}
