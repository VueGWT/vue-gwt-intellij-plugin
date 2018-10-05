package com.axellience.vuegwtplugin.language.htmltemplate;

public interface HtmlTemplateHandledLexer {
  boolean seenTag();
  boolean inTagState();
  boolean seenStyle();

  void setSeenStyleType();
}
