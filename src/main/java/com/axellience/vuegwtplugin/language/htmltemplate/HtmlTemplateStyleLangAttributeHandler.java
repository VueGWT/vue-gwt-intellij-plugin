package com.axellience.vuegwtplugin.language.htmltemplate;

import com.intellij.lexer.BaseHtmlLexer.TokenHandler;
import com.intellij.lexer.Lexer;

public class HtmlTemplateStyleLangAttributeHandler implements TokenHandler {

  @Override
  public void handleElement(Lexer lexer) {
    HtmlTemplateHandledLexer htmlLexer = (HtmlTemplateHandledLexer) lexer;

    if (!htmlLexer.seenTag() && !htmlLexer.inTagState()) {
      if (htmlLexer.seenStyle() && "lang".equals(lexer.getTokenText())) {
        htmlLexer.setSeenStyleType();
      }
    }
  }
}
