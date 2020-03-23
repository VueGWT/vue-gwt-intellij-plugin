package com.axellience.vuegwtplugin.codeinsight.tags;

import com.axellience.vuegwtplugin.VueGWTIcons;
import com.axellience.vuegwtplugin.util.VueGWTComponentAnnotationUtil;
import com.axellience.vuegwtplugin.util.VueGWTPluginUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlTagNameProvider;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VueGWTTagProvider implements XmlElementDescriptorProvider, XmlTagNameProvider {

  @Nullable
  @Override
  public XmlElementDescriptor getDescriptor(XmlTag tag) {
    String tagName = tag.getName(); // Tag name of the current element

    PsiFile templateFile = tag.getContainingFile();

    return VueGWTPluginUtil
        .findJavaFromTemplate(templateFile)
        .flatMap(VueGWTComponentAnnotationUtil::getComponentAnnotationFromJavaFile)
        .flatMap(
            annotation -> VueGWTComponentAnnotationUtil
                .getImportedComponentClassTypeFromComponentAnnotation(annotation, tagName)
        )
        .map(PsiClassType::resolve)
        .map(VueGWTElementDescriptor::new)
        .orElse(null);
  }

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
