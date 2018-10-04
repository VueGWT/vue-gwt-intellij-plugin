package com.axellience.vuegwtplugin.language.htmltemplate;

import com.intellij.ide.highlighter.HtmlFileHighlighter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HtmlTemplateHighlightingLexerFactory extends SyntaxHighlighterFactory {

  @NotNull
  @Override
  public SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project,
      @Nullable VirtualFile virtualFile) {

    HtmlTemplateHighlightingLexer lexer = new HtmlTemplateHighlightingLexer();
    return new HtmlFileHighlighter() {
      @NotNull
      @Override
      public Lexer getHighlightingLexer() {
        return lexer;
      }
    };
  }
}
