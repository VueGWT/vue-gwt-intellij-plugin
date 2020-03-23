package com.axellience.vuegwtplugin.util;

import static com.axellience.vuegwtplugin.util.VueGWTPluginUtil.COMPONENT_QUALIFIED_NAME;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.PsiImplUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class VueGWTComponentAnnotationUtil {

  public static Optional<PsiAnnotation> getComponentAnnotationFromJavaFile(
      PsiJavaFile psiJavaFile) {
    for (PsiClass psiClass : psiJavaFile.getClasses()) {
      Optional<PsiAnnotation> annotation = getComponentAnnotationFromPsiClass(psiClass);
      if (annotation.isPresent()) {
        return annotation;
      }
    }

    return Optional.empty();
  }

  public static Optional<PsiAnnotation> getComponentAnnotationFromPsiClass(
      PsiClass psiClass) {
    PsiAnnotation[] annotations = psiClass.getAnnotations();
    for (PsiAnnotation annotation : annotations) {
      if (COMPONENT_QUALIFIED_NAME.equals(annotation.getQualifiedName())) {
        return Optional.of(annotation);
      }
    }

    return Optional.empty();
  }

  public static Optional<PsiFile> getImportedComponentTemplateFromAnnotationComponent(
      PsiAnnotation componentAnnotation,
      String tagName) {

    Set<PsiClassType> importedComponentsClassType = getImportedComponentsClassTypeFromComponentAnnotation(
        componentAnnotation);

    return importedComponentsClassType.stream()
        .filter(psiClassType -> VueGWTPluginUtil.componentToTagName(psiClassType).equals(tagName))
        .findFirst()
        .map(PsiClassType::resolve)
        .map(PsiClass::getContainingFile)
        .flatMap(VueGWTPluginUtil::findHtmlTemplate);
  }

  public static Optional<PsiClassType> getImportedComponentClassTypeFromComponentAnnotation(
      PsiAnnotation componentAnnotation,
      String tagName) {

    Set<PsiClassType> importedComponentsClassType = getImportedComponentsClassTypeFromComponentAnnotation(
        componentAnnotation);

    return importedComponentsClassType.stream()
        .filter(psiClassType -> VueGWTPluginUtil.componentToTagName(psiClassType).equals(tagName))
        .findFirst();
  }

  public static Set<PsiClassType> getImportedComponentsClassTypeFromComponentAnnotation(
      PsiAnnotation componentAnnotation) {
    PsiAnnotationMemberValue componentsValue =
        PsiImplUtil.findAttributeValue(componentAnnotation, "components");

    if (componentsValue instanceof PsiClassObjectAccessExpression) {
      return Collections.singleton(getComponentClassTypeFromClassObjectAccessExpression(
          (PsiClassObjectAccessExpression) componentsValue));
    }

    if (componentsValue instanceof PsiArrayInitializerMemberValue) {
      return getComponentsClassTypeFromPsiArrayInitializerMemberValue(
          (PsiArrayInitializerMemberValue) componentsValue);
    }

    return Collections.emptySet();
  }

  public static PsiClassType getComponentClassTypeFromClassObjectAccessExpression(
      PsiClassObjectAccessExpression componentsClassAccess) {
    // classType is Class<MyComponent>
    PsiClassType classType = (PsiClassType) PsiImplUtil.getType(componentsClassAccess);

    // componentClassType is MyComponent
    return (PsiClassType) classType.getParameters()[0];
  }

  public static Set<PsiClassType> getComponentsClassTypeFromPsiArrayInitializerMemberValue(
      PsiArrayInitializerMemberValue arrayInitializer) {
    return Arrays.stream(arrayInitializer.getInitializers())
        .map(PsiClassObjectAccessExpression.class::cast)
        .map(VueGWTComponentAnnotationUtil::getComponentClassTypeFromClassObjectAccessExpression)
        .collect(Collectors.toSet());
  }
}
