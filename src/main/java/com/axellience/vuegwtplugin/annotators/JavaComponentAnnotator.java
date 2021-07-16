package com.axellience.vuegwtplugin.annotators;

import static com.axellience.vuegwtplugin.util.VueGWTPluginUtil.COMPONENT_QUALIFIED_NAME;
import static com.axellience.vuegwtplugin.util.VueGWTPluginUtil.findHtmlTemplate;

import com.axellience.vuegwtplugin.intentions.CreateTemplateIntention;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationParameterList;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameValuePair;
import org.jetbrains.annotations.NotNull;

public class JavaComponentAnnotator implements Annotator {

  @Override
  public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
    if (!(psiElement instanceof PsiAnnotation)) {
      return;
    }

    annotatePsiAnnotation((PsiAnnotation) psiElement, annotationHolder);
  }

  private void annotatePsiAnnotation(PsiAnnotation psiAnnotation,
      AnnotationHolder annotationHolder) {
    String annotationQualifiedName = psiAnnotation.getQualifiedName();
    if (!COMPONENT_QUALIFIED_NAME.equals(annotationQualifiedName) || !shouldHaveTemplate(
        psiAnnotation)) {
      return;
    }

    PsiFile javaFile = psiAnnotation.getContainingFile();
    if (findHtmlTemplate(javaFile).isEmpty()) {
      addCreateTemplateIntention(annotationHolder);
    }
  }

  private void addCreateTemplateIntention(AnnotationHolder annotationHolder) {
    annotationHolder.newAnnotation(HighlightSeverity.ERROR, "Unresolved HTML Template")
        .withFix(new CreateTemplateIntention())
        .create();
  }

  private boolean shouldHaveTemplate(PsiAnnotation psiAnnotation) {
    PsiAnnotationParameterList parameterList = psiAnnotation.getParameterList();
    for (PsiNameValuePair attribute : parameterList.getAttributes()) {
      if ("hasTemplate".equals(attribute.getName())) {
        String literalValue = attribute.getLiteralValue();
        return !"false".equals(literalValue);
      }
    }

    return true;
  }
}
