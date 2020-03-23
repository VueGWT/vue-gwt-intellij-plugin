package com.axellience.vuegwtplugin.codeinsight.attributes;

import com.axellience.vuegwtplugin.VueGWTIcons;
import com.axellience.vuegwtplugin.codeinsight.tags.VueGWTElementDescriptor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.util.ProcessingContext;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

public class VueGWTCompletionProvider extends CompletionProvider<CompletionParameters> {

  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters,
      @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
    PsiElement parent = parameters.getPosition().getParent();
    if (!(parent instanceof XmlAttribute)) {
      return;
    }

    XmlAttribute attribute = (XmlAttribute) parent;
    if (!(attribute.getParent().getDescriptor() instanceof VueGWTElementDescriptor)) {
      return;
    }

    String prefix = result.getPrefixMatcher().getPrefix();
    if (!prefix.startsWith(":") && !prefix.startsWith("v-bind:")) {
      return;
    }

    CompletionResultSet newResults =
        prefix.equals("v-bind:") ? result.withPrefixMatcher("") : result;
    String lookupItemPrefix = prefix.startsWith(":") ? ":" : "";

    ((VueGWTElementDescriptor) attribute.getParent().getDescriptor())
        .getProps()
        .forEach(prop -> {
          PsiField field = (PsiField) prop.getDeclaration();
          newResults.addElement(
              PrioritizedLookupElement.withPriority(
                  LookupElementBuilder.create(
                      new Pair<>(
                          lookupItemPrefix + field.getName(),
                          SmartPointerManager
                              .getInstance(field.getProject())
                              .createSmartPsiElementPointer(field)),
                      lookupItemPrefix + field.getName()
                  )
                      .withIcon(VueGWTIcons.VUE),
                  10)
          );
        });
  }
}