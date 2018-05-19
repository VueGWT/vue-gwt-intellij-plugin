package com.axellience.vuegwtplugin;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.xml.TagNameReference;

public class VueGWTTagNameReference extends TagNameReference
{
    private final PsiClass componentClass;

    public VueGWTTagNameReference(ASTNode nameElement, PsiClass componentClass,
        boolean startTagFlag)
    {
        super(nameElement, startTagFlag);
        this.componentClass = componentClass;
    }

    @Override
    public PsiElement resolve()
    {
        return componentClass;
    }
}
