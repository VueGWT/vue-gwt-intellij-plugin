package com.axellience.vuegwtplugin.codeinsight.attributes;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.XmlPatterns.xmlAttribute;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.xml.XmlTokenType;

public class VueGWTCompletionContributor extends CompletionContributor {

  public VueGWTCompletionContributor() {
    this.extend(CompletionType.BASIC, psiElement(XmlTokenType.XML_NAME).withParent(xmlAttribute()),
        new VueGWTPropBindingCompletionProvider());
  }
}
