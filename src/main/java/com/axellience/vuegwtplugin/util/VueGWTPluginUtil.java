package com.axellience.vuegwtplugin.util;

import static com.intellij.psi.impl.PsiImplUtil.findAttributeValue;

import com.google.common.base.CaseFormat;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlTag;
import java.util.Arrays;
import java.util.Optional;

public class VueGWTPluginUtil {

  public static final String COMPONENT_QUALIFIED_NAME =
      "com.axellience.vuegwt.core.annotations.component.Component";

  public static final String PROP_QUALIFIED_NAME =
      "com.axellience.vuegwt.core.annotations.component.Prop";

  public static final String VUE_GWT_NAMESPACE = "http://axellience.com/vue-gwt";

  private VueGWTPluginUtil() {

  }

  public static boolean isVueContext(XmlTag context) {
    return findJavaFromTemplate(context.getContainingFile().getOriginalFile())
        .flatMap(VueGWTComponentAnnotationUtil::getComponentAnnotationFromJavaFile)
        .isPresent();
  }

  public static Optional<PsiFile> findHtmlTemplate(PsiFile javaFile) {
    PsiDirectory parentDirectory = javaFile.getContainingDirectory();
    if (parentDirectory == null) {
      return Optional.empty();
    }

    String templateName = getTemplateNameFrom(javaFile);
    return findFileWithName(parentDirectory, templateName);
  }

  public static Optional<PsiJavaFile> findJavaFromTemplate(PsiFile templateFile) {
    PsiDirectory parentDirectory = templateFile.getOriginalFile().getContainingDirectory();
    if (parentDirectory == null) {
      return Optional.empty();
    }

    String javaName = getJavaNameFromTemplate(templateFile);
    Optional<PsiFile> optionalJavaFile = findFileWithName(parentDirectory, javaName);

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

  public static String getTemplateNameFrom(PsiFile javaFile) {
    return javaFile.getName().replaceAll("(.*)\\.java", "$1.html");
  }

  public static String getJavaNameFromTemplate(PsiFile templateFile) {
    return templateFile.getName().replaceAll("(.*)\\.html", "$1.java");
  }

  private static Optional<PsiFile> findFileWithName(PsiDirectory directory, String name) {
    return Arrays.stream(directory.getFiles())
        .filter(file -> name.equals(file.getName()))
        .findFirst();
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

  public static String componentToTagName(PsiClass componentClass) {
    Optional<PsiAnnotation> componentAnnotation = VueGWTComponentAnnotationUtil
        .getComponentAnnotationFromPsiClass(componentClass);

    if (componentAnnotation.isPresent()) {
      PsiAnnotationMemberValue name = findAttributeValue(componentAnnotation.get(), "name");
      if (name != null && !"".equals(name.getText())) {
        return name.getText();
      }
    }

    String componentClassName = componentClass.getName().replaceAll("Component$", "");
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, componentClassName).toLowerCase();
  }
}
