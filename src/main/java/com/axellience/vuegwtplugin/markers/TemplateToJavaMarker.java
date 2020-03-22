package com.axellience.vuegwtplugin.markers;

import com.axellience.vuegwtplugin.VueGWTBundle;
import com.axellience.vuegwtplugin.VueGWTIcons;
import com.axellience.vuegwtplugin.util.VueGWTPluginUtil;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.html.HtmlTag;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.axellience.vuegwtplugin.util.VueGWTPluginUtil.findJavaFromTemplate;

public class TemplateToJavaMarker extends RelatedItemLineMarkerProvider {

  @Override
  protected void collectNavigationMarkers(@NotNull PsiElement element,
      @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {

    if (!(element instanceof HtmlTag)) {
      return;
    }

    HtmlTag tag = (HtmlTag) element;
    if (tag.getParentTag() != null || isVueGwtNamespace(tag)) {
      return;
    }

    PsiFile templateFile = tag.getContainingFile();
    findJavaFromTemplate(templateFile)
        .ifPresent(javaFile -> addMarker(element.getFirstChild(), result, javaFile));
  }

  private boolean isVueGwtNamespace(HtmlTag tag) {
    return VueGWTPluginUtil.VUE_GWT_NAMESPACE.equals(tag.getNamespace());
  }

  private void addMarker(PsiElement element,
      Collection<? super RelatedItemLineMarkerInfo<PsiElement>> result,
      PsiFile javaFile) {
    result.add(NavigationGutterIconBuilder.create(VueGWTIcons.VUE)
        .setTarget(javaFile)
        .setTooltipText(VueGWTBundle.message("navigatemarker.tooltip"))
        .createLineMarkerInfo(element));
  }
}
