package com.axellience.vuegwtplugin.language.htmltemplate;

import com.intellij.lang.html.HTMLParserDefinition;
import com.intellij.lexer.HtmlLexer;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.html.HtmlFileImpl;
import com.intellij.psi.stubs.PsiFileStub;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IStubFileElementType;
import org.jetbrains.annotations.NotNull;

public class HtmlTemplateParserDefinition extends HTMLParserDefinition {

  private static final IFileElementType HTML_FILE = new IStubFileElementType<PsiFileStub<HtmlFileImpl>>(HtmlTemplateLanguage.INSTANCE);

  @NotNull @Override
  public IFileElementType getFileNodeType() {
    return HTML_FILE;
  }

  @NotNull @Override
  public PsiFile createFile(@NotNull FileViewProvider fileViewProvider) {
    return new HtmlFileImpl(fileViewProvider, HTML_FILE);
  }

  @NotNull @Override
  public Lexer createLexer(Project project) {
    return new HtmlLexer();
  }
}
