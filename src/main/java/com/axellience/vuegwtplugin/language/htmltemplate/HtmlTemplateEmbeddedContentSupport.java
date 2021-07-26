package com.axellience.vuegwtplugin.language.htmltemplate;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import com.intellij.html.embedding.HtmlEmbeddedContentProvider;
import com.intellij.html.embedding.HtmlEmbeddedContentSupport;
import com.intellij.lexer.BaseHtmlLexer;
import com.intellij.lexer.HtmlRawTextTagContentProvider;

public class HtmlTemplateEmbeddedContentSupport implements HtmlEmbeddedContentSupport {

  @NotNull @Override
  public List<HtmlEmbeddedContentProvider> createEmbeddedContentProviders(@NotNull BaseHtmlLexer lexer) {
    return List.of(new HtmlRawTextTagContentProvider(lexer), new HtmlVueGWTTemplateScriptStyleEmbeddedContentProvider(lexer));
  }
}
