package com.axellience.vuegwtplugin.language.htmltemplate;

import com.intellij.lang.Language;
import com.intellij.lexer.HtmlLexer;
import com.intellij.lexer._HtmlLexer;
import com.intellij.psi.xml.XmlTokenType;
import org.jetbrains.annotations.Nullable;

public class HtmlTemplateLexer extends HtmlLexer implements HtmlTemplateHandledLexer {

  HtmlTemplateLexer() {
    super();
    this.registerHandler(XmlTokenType.XML_NAME, new HtmlTemplateStyleLangAttributeHandler());
  }

  @Nullable
  @Override
  protected Language getStyleLanguage() {
    if ("scss".equals(this.styleType)) {
      return Language.findLanguageByID("SCSS");
    }

    return super.getStyleLanguage();
  }

  @Override
  public boolean seenTag() {
    return seenTag;
  }

  @Override
  public boolean inTagState() {
    return (getState() & BASE_STATE_MASK) == _HtmlLexer.START_TAG_NAME;
  }

  @Override
  public boolean seenStyle() {
    return seenStyle;
  }

  @Override
  public void setSeenStyleType() {
    seenStylesheetType = true;
  }
}
