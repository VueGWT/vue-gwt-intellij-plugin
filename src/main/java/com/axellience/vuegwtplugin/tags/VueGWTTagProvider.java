package com.axellience.vuegwtplugin.tags;

import com.axellience.vuegwtplugin.VueGWTIcons;
import com.axellience.vuegwtplugin.util.VueGWTComponentAnnotationUtil;
import com.axellience.vuegwtplugin.util.VueGWTPluginUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlTagNameProvider;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

public class VueGWTTagProvider implements XmlTagNameProvider {

  @Override
  public void addTagNameVariants(List<LookupElement> elements, @NotNull XmlTag tag, String prefix) {
    Optional<PsiJavaFile> optionalComponentJavaFile = VueGWTPluginUtil
        .findJavaFromTemplate(tag.getContainingFile().getOriginalFile());

    optionalComponentJavaFile
        .flatMap(VueGWTComponentAnnotationUtil::getComponentAnnotationFromJavaFile)
        .map(VueGWTComponentAnnotationUtil::getImportedComponentsClassTypeFromComponentAnnotation)
        .ifPresent(
            set -> elements
                .addAll(set.stream().map(this::createLookupElement).collect(Collectors.toList()))
        );
  }

  private LookupElement createLookupElement(PsiClassType componentClassType) {
    PsiClass componentClass = componentClassType.resolve();
    if (componentClass == null) {
      return null;
    }

    return LookupElementBuilder
        .create(
            new Pair<>(
                componentClass.getName(),
                SmartPointerManager
                    .getInstance(componentClass.getProject())
                    .createSmartPsiElementPointer(componentClass)),
            VueGWTPluginUtil.componentToTagName(componentClassType)
        )
        .withBoldness(true)
        .withTypeText(componentClass.getQualifiedName())
        .withIcon(VueGWTIcons.VUE);
  }
}
