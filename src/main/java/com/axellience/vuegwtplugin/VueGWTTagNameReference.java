package com.axellience.vuegwtplugin;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.xml.TagNameReference;

public class VueGWTTagNameReference extends TagNameReference
{
    private final PsiElement componentTemplate;

    public VueGWTTagNameReference(ASTNode nameElement, PsiElement componentTemplate,
        boolean startTagFlag)
    {
        super(nameElement, startTagFlag);
        this.componentTemplate = componentTemplate;
    }

    @Override
    public PsiElement resolve()
    {
        return componentTemplate;
    }
}
