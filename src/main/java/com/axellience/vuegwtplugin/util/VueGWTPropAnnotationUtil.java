package com.axellience.vuegwtplugin.util;

import static com.axellience.vuegwtplugin.util.VueGWTPluginUtil.PROP_QUALIFIED_NAME;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import java.util.Optional;

public class VueGWTPropAnnotationUtil {

  public static Optional<PsiAnnotation> getComponentAnnotationFromPsiField(
      PsiField psiField) {
    PsiAnnotation[] annotations = psiField.getAnnotations();
    for (PsiAnnotation annotation : annotations) {
      if (PROP_QUALIFIED_NAME.equals(annotation.getQualifiedName())) {
        return Optional.of(annotation);
      }
    }

    return Optional.empty();
  }
}
