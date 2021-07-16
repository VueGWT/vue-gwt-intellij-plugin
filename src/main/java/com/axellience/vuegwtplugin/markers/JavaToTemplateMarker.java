package com.axellience.vuegwtplugin.markers;

import static com.axellience.vuegwtplugin.util.VueGWTPluginUtil.COMPONENT_QUALIFIED_NAME;
import static com.axellience.vuegwtplugin.util.VueGWTPluginUtil.findHtmlTemplate;

import com.axellience.vuegwtplugin.VueGWTBundle;
import com.axellience.vuegwtplugin.VueGWTIcons;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public class JavaToTemplateMarker extends RelatedItemLineMarkerProvider {

  @Override
  protected void collectNavigationMarkers(@NotNull PsiElement element,
      @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
    if (!(element instanceof PsiAnnotation)) {
      return;
    }

    String annotationQualifiedName = ((PsiAnnotation) element).getQualifiedName();
    if (!COMPONENT_QUALIFIED_NAME.equals(annotationQualifiedName)) {
      return;
    }

    PsiFile javaFile = element.getContainingFile();
    findHtmlTemplate(javaFile)
        .ifPresent(templateFile -> addTemplateNavigatorMarker(element.getFirstChild(), result,
            templateFile));
  }

  private void addTemplateNavigatorMarker(PsiElement element,
      Collection<? super RelatedItemLineMarkerInfo<PsiElement>> result,
      PsiFile templateFile) {
    result.add(NavigationGutterIconBuilder.create(VueGWTIcons.VUE)
        .setTarget(templateFile)
        .setTooltipText(VueGWTBundle.message("navigatemarker.tooltip"))
        .createLineMarkerInfo(element));
  }
}
