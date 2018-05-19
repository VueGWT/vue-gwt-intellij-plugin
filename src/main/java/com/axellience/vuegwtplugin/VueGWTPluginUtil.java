package com.axellience.vuegwtplugin;

import com.google.common.base.CaseFormat;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClassType;

import java.util.Optional;

import static com.intellij.psi.impl.PsiImplUtil.findAttributeValue;

public class VueGWTPluginUtil
{
    public static String COMPONENT_ANNOTATION_NAME =
        "com.axellience.vuegwt.core.annotations.component.Component";

    public static Optional<VirtualFile> getJavaFileForTemplate(VirtualFile templateFile)
    {
        if (templateFile == null || !"html".equals(templateFile.getExtension()))
            return Optional.empty();

        String javaClassFileName = templateFile.getNameWithoutExtension() + ".java";
        VirtualFile parent = templateFile.getParent();
        if (parent == null)
            return Optional.empty();

        return Optional.ofNullable(parent.findChild(javaClassFileName));
    }

    public static String componentToTagName(PsiClassType componentClass)
    {
        PsiAnnotation componentAnnotation =
            componentClass.findAnnotation(COMPONENT_ANNOTATION_NAME);

        if (componentAnnotation != null)
        {
            PsiAnnotationMemberValue name = findAttributeValue(componentAnnotation, "name");

            if (name != null && !"".equals(name.getText()))
                return name.getText();
        }

        String componentClassName = componentClass.getName().replaceAll("Component$", "");
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, componentClassName).toLowerCase();
    }
}
