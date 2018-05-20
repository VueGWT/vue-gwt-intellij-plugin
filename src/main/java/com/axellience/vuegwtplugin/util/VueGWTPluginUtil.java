package com.axellience.vuegwtplugin.util;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import java.util.Arrays;
import java.util.Optional;

public class VueGWTPluginUtil {

  private VueGWTPluginUtil() {

  }

  public static Optional<PsiFile> findHtmlTemplate(PsiFile javaFile) {
    PsiDirectory parentDirectory = javaFile.getContainingDirectory();
    if (parentDirectory == null) {
      return Optional.empty();
    }

    String templateName = getTemplateNameFrom(javaFile);
    return Arrays.stream(parentDirectory.getFiles())
        .filter(file -> templateName.equals(file.getName()))
        .findFirst();
  }

  public static String getTemplateNameFrom(PsiFile javaFile) {
    return javaFile.getName().replaceAll("(.*)\\.java", "$1.html");
  }
}
