package com.axellience.vuegwtplugin.language;

import com.intellij.lang.html.HTMLParserDefinition;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.html.HtmlFileImpl;
import com.intellij.psi.stubs.PsiFileStub;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IStubFileElementType;

public class VueGWTParserDefinition extends HTMLParserDefinition
{
    public static IFileElementType HTML_FILE =
        new IStubFileElementType<PsiFileStub<HtmlFileImpl>>(VueGWTLanguage.INSTANCE);

    @Override
    public IFileElementType getFileNodeType()
    {
        return HTML_FILE;
    }

    @Override
    public PsiFile createFile(FileViewProvider fileViewProvider)
    {
        return new HtmlFileImpl(fileViewProvider, HTML_FILE);
    }
}
