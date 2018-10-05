package com.axellience.vuegwtplugin.intentions;

import com.axellience.vuegwtplugin.language.htmltemplate.HtmlTemplateFileType;
import com.axellience.vuegwtplugin.util.VueGWTPluginUtil;
import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.NotNull;

public class CreateTemplateIntention extends BaseIntentionAction {

  @Nls(capitalization = Capitalization.Sentence)
  @NotNull
  @Override
  public String getFamilyName() {
    return "HTML Template";
  }

  @NotNull
  @Override
  public String getText() {
    return "Create VueGWT HTML Template";
  }

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile psiFile) {
    return true;
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile)
      throws IncorrectOperationException {
    ApplicationManager.getApplication().invokeLater(() -> createTemplate(project, editor, psiFile));
  }

  private void createTemplate(Project project, Editor editor, PsiFile javaFile) {
    String templateName = VueGWTPluginUtil.getTemplateNameFrom(javaFile);
    PsiFile htmlFile = PsiFileFactory.getInstance(project).
        createFileFromText(templateName, HtmlTemplateFileType.INSTANCE, "<div></div>");

    new WriteCommandAction.Simple(project, "Create HTML VueGwt Template '" + templateName + "'",
        htmlFile) {
      @Override
      protected void run() {
        PsiDirectory directory = javaFile.getContainingDirectory();
        directory.add(htmlFile);
      }
    }.execute();
  }
}
