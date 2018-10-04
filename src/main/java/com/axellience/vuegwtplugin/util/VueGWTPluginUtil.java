package com.axellience.vuegwtplugin.util;

import static com.intellij.psi.impl.PsiImplUtil.findAttributeValue;

import com.google.common.base.CaseFormat;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import java.util.Arrays;
import java.util.Optional;

public class VueGWTPluginUtil {

  public static final String COMPONENT_QUALIFIED_NAME =
      "com.axellience.vuegwt.core.annotations.component.Component";

  public static final String VUE_GWT_NAMESPACE = "http://axellience.com/vue-gwt";

  private VueGWTPluginUtil() {

  }

  public static Optional<PsiFile> findHtmlTemplate(PsiFile javaFile) {
    //Collection<VirtualFile> htmlFiles = FileTypeIndex.getFiles(HtmlTemplateFileType.INSTANCE, GlobalSearchScope.allScope(project));

    PsiDirectory parentDirectory = javaFile.getContainingDirectory();
    if (parentDirectory == null) {
      return Optional.empty();
    }

    String templateName = getTemplateNameFrom(javaFile);
    return findFileWithName(parentDirectory, templateName);
  }

  public static String getTemplateNameFrom(PsiFile javaFile) {
    return javaFile.getName().replaceAll("(.*)\\.java", "$1.html");
  }

  public static Optional<PsiFile> findJavaFromTemplate(PsiFile templateFile) {
    PsiDirectory parentDirectory = templateFile.getContainingDirectory();
    if (parentDirectory == null) {
      return Optional.empty();
    }

    String javaName = getJavaNameFromTemplate(templateFile);
    return findFileWithName(parentDirectory, javaName);
  }

  public static String getJavaNameFromTemplate(PsiFile templateFile) {
    return templateFile.getName().replaceAll("(.*)\\.html", "$1.java");
  }

  private static Optional<PsiFile> findFileWithName(PsiDirectory directory, String name) {
    return Arrays.stream(directory.getFiles())
        .filter(file -> name.equals(file.getName()))
        .findFirst();
  }

  // ------ OLD ------------------------------------------------------------------------------------

  public static Optional<VirtualFile> getJavaFileForTemplate(VirtualFile templateFile) {
    if (templateFile == null || !"html".equals(templateFile.getExtension())) {
      return Optional.empty();
    }

    String javaClassFileName = templateFile.getNameWithoutExtension() + ".java";
    VirtualFile parent = templateFile.getParent();
    if (parent == null) {
      return Optional.empty();
    }

    return Optional.ofNullable(parent.findChild(javaClassFileName));
  }

  public static String componentToTagName(PsiClassType componentClass) {
    PsiAnnotation componentAnnotation =
        componentClass.findAnnotation(COMPONENT_QUALIFIED_NAME);

    if (componentAnnotation != null) {
      PsiAnnotationMemberValue name = findAttributeValue(componentAnnotation, "name");

      if (name != null && !"".equals(name.getText())) {
        return name.getText();
      }
    }

    String componentClassName = componentClass.getName().replaceAll("Component$", "");
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, componentClassName).toLowerCase();
  }
}
