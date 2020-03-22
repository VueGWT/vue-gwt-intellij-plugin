package com.axellience.vuegwtplugin;

import com.axellience.vuegwtplugin.language.htmltemplate.HtmlTemplateLanguage;
import com.axellience.vuegwtplugin.util.VueGWTComponentAnnotationUtil;
import com.axellience.vuegwtplugin.util.VueGWTPluginUtil;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.xml.SchemaPrefix;
import com.intellij.psi.impl.source.xml.TagNameReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.HtmlXmlExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VueGWTXmlExtension extends HtmlXmlExtension {

  @Override
  public boolean isAvailable(PsiFile psiFile) {
    if (psiFile.getLanguage() != HtmlTemplateLanguage.INSTANCE) {
      return false;
    }

    return VueGWTPluginUtil.findJavaFromTemplate(psiFile).isPresent();
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
    String tagName = astNode.getText(); // Tag name of the current element

    PsiFile templateFile = astNode.getTreeParent().getPsi().getContainingFile();
    return
        VueGWTPluginUtil
            .findJavaFromTemplate(templateFile)
            .flatMap(VueGWTComponentAnnotationUtil::getComponentAnnotationFromJavaFile)
            .flatMap(
                annotation -> VueGWTComponentAnnotationUtil
                    .getImportedComponentTemplateFromAnnotationComponent(annotation, tagName)
            )
            .map(
                componentTemplate ->
                    (TagNameReference) new VueGWTTagNameReference(astNode, componentTemplate, b)
            )
            .orElse(super.createTagNameReference(astNode, b));
  }

  @Override
  public boolean isSelfClosingTagAllowed(@NotNull XmlTag tag) {
    return true;
  }
}
