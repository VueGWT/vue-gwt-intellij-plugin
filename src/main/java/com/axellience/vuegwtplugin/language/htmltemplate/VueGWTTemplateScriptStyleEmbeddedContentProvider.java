package com.axellience.vuegwtplugin.language.htmltemplate;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.Language;
import com.intellij.lexer.BaseHtmlLexer;
import com.intellij.lexer.HtmlScriptStyleEmbeddedContentProvider;

public class VueGWTTemplateScriptStyleEmbeddedContentProvider extends HtmlScriptStyleEmbeddedContentProvider {

  public VueGWTTemplateScriptStyleEmbeddedContentProvider(@NotNull BaseHtmlLexer lexer) {
    super(lexer);
  }

  @Override
  protected boolean isInterestedInAttribute(@NotNull CharSequence attributeName) {
    return super.isInterestedInAttribute(attributeName) || namesEqual(attributeName, "lang");
  }

  @Override
  protected Language styleLanguage(String styleLang) {
    if ("scss".equals(styleLang)) {
      return Language.findLanguageByID("SCSS");
    }

    return super.styleLanguage(styleLang);
  }
}
