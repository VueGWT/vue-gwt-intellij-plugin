package com.axellience.vuegwtplugin;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.editor.impl.TextRangeInterval;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;

public class VueCustomInjector implements MultiHostInjector
{
    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar,
        @NotNull PsiElement host)
    {
        if (!(host.getParent() instanceof XmlAttribute))
            return;

        XmlAttribute attribute = (XmlAttribute) host.getParent();
        String attr = attribute.getName();
        if (attr.startsWith(":") || attr.startsWith("@") || attr.startsWith("v-"))
        {
            registrar.startInjecting(JavaLanguage.INSTANCE);

            PsiFile templateFile = host.getContainingFile();
            String componentName = templateFile
                .getName()
                .substring(0, templateFile.getName().length() - ".html".length());

            String javaClassFileName = componentName + ".java";

            if (templateFile.getContainingDirectory() == null)
                return;

            PsiJavaFileImpl componentFile = null;
            for (PsiFile psiFile : templateFile.getContainingDirectory().getFiles())
            {
                if (psiFile.getName().equals(javaClassFileName))
                {
                    componentFile = (PsiJavaFileImpl) psiFile;
                    break;
                }
            }

            if (componentFile == null)
                return;

            String templateExpressionMethod;
            if (attr.startsWith("@"))
                templateExpressionMethod = "public void templateExpression() {\n\t\t";
            else
                templateExpressionMethod = "public Object templateExpression() {\n\t\treturn ";

            String expressionStart = "package "
                + componentFile.getPackageName()
                + ";\n"
                + "class "
                + componentName
                + "TemplateImpl extends "
                + componentName
                + "JsType {\n"
                + templateExpressionMethod;

            String expressionEnd = ";\n\t}\n}";

            registrar.addPlace(expressionStart,
                expressionEnd,
                (PsiLanguageInjectionHost) host,
                new TextRangeInterval(1, host.getTextLength() - 1));
            registrar.doneInjecting();
        }
    }

    @NotNull
    @Override
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn()
    {
        return Collections.singletonList(XmlAttributeValue.class);
    }
}
