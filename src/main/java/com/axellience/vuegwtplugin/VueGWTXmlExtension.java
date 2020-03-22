package com.axellience.vuegwtplugin;

import static com.axellience.vuegwtplugin.util.VueGWTPluginUtil.COMPONENT_QUALIFIED_NAME;
import static com.axellience.vuegwtplugin.util.VueGWTPluginUtil.findHtmlTemplate;

import com.axellience.vuegwtplugin.language.htmltemplate.HtmlTemplateLanguage;
import com.axellience.vuegwtplugin.util.VueGWTPluginUtil;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
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
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.HtmlXmlExtension;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VueGWTXmlExtension extends HtmlXmlExtension {

  @Override
  public boolean isAvailable(PsiFile psiFile) {
    if (psiFile.getLanguage() != HtmlTemplateLanguage.INSTANCE) {
      return false;
    }

    Optional<PsiJavaFile> optionalPsiJavaFile = getJavaFileForTemplate(psiFile);
    return optionalPsiJavaFile.isPresent();
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
    PsiFile templateFile = astNode.getTreeParent().getPsi().getContainingFile();

    Optional<PsiJavaFile> optionalPsiJavaFile = getJavaFileForTemplate(templateFile);
    if (!optionalPsiJavaFile.isPresent()) {
      return super.createTagNameReference(astNode, b);
    }

    PsiJavaFile psiJavaFile = optionalPsiJavaFile.get();
    String tagName = astNode.getText(); // Tag name of the current element

    return getComponentAnnotationFromJavaFile(psiJavaFile)
        .flatMap(annotation -> getComponentTemplateFromAnnotation(annotation, tagName))
        .map(componentTemplate ->
            (TagNameReference) new VueGWTTagNameReference(astNode, componentTemplate, b))
        .orElse(super.createTagNameReference(astNode, b));
  }

  private Optional<PsiAnnotation> getComponentAnnotationFromJavaFile(PsiJavaFile psiJavaFile) {
    for (PsiClass psiClass : psiJavaFile.getClasses()) {
      PsiAnnotation[] annotations = psiClass.getAnnotations();
      for (PsiAnnotation annotation : annotations) {
        if (COMPONENT_QUALIFIED_NAME.equals(annotation.getQualifiedName())) {
          return Optional.of(annotation);
        }
      }
    }

    return Optional.empty();
  }

  private Optional<PsiFile> getComponentTemplateFromAnnotation(PsiAnnotation componentAnnotation,
      String tagName) {
    PsiAnnotationMemberValue componentsValue =
        PsiImplUtil.findAttributeValue(componentAnnotation, "components");

    if (componentsValue instanceof PsiClassObjectAccessExpression) {
      return getComponentTemplateForClassObjectAccess(
          (PsiClassObjectAccessExpression) componentsValue, tagName);
    }

    if (componentsValue instanceof PsiArrayInitializerMemberValue) {
      return getComponentTemplateFromArrayInitializerMemberValue(
          (PsiArrayInitializerMemberValue) componentsValue, tagName);
    }

    return Optional.empty();
  }

  private Optional<PsiFile> getComponentTemplateFromArrayInitializerMemberValue(
      PsiArrayInitializerMemberValue arrayInitializer, String tagName) {
    for (PsiAnnotationMemberValue initializer : arrayInitializer.getInitializers()) {
      if (!(initializer instanceof PsiClassObjectAccessExpression)) {
        continue;
      }

      Optional<PsiFile> optionalComponentTemplate = getComponentTemplateForClassObjectAccess(
          (PsiClassObjectAccessExpression) initializer, tagName);
      if (optionalComponentTemplate.isPresent()) {
        return optionalComponentTemplate;
      }
    }

    return Optional.empty();
  }

  private Optional<PsiFile> getComponentTemplateForClassObjectAccess(
      PsiClassObjectAccessExpression componentsClassAccess, String tagName) {
    // classType is Class<MyComponent>
    PsiClassType classType = (PsiClassType) PsiImplUtil.getType(componentsClassAccess);

    // componentClassType is MyComponent
    PsiClassType componentClassType = (PsiClassType) classType.getParameters()[0];
    String componentTagName = VueGWTPluginUtil.componentToTagName(componentClassType);
    if (!tagName.equals(componentTagName)) {
      return Optional.empty();
    }
    PsiClass componentClass = componentClassType.resolve();
    if (componentClass == null) {
      return Optional.empty();
    }

    return findHtmlTemplate(componentClass.getContainingFile());
  }

  @Override
  public boolean isSelfClosingTagAllowed(@NotNull XmlTag tag) {
    return true;
  }

  private Optional<PsiJavaFile> getJavaFileForTemplate(PsiFile templateFile) {
    // Get the Java file for the template
    Optional<PsiFile> optionalJavaFile = VueGWTPluginUtil.findJavaFromTemplate(templateFile);
    if (!optionalJavaFile.isPresent()) {
      return Optional.empty();
    }

    // Find the project for the current file
    Project project = templateFile.getProject();
    PsiFile file = PsiManager.getInstance(project)
        .findFile(optionalJavaFile.get().getVirtualFile());
    if (!(file instanceof PsiJavaFile)) {
      return Optional.empty();
    }

    return Optional.of((PsiJavaFile) file);
  }
}
