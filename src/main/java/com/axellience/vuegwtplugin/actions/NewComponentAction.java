package com.axellience.vuegwtplugin.actions;

import static com.axellience.vuegwtplugin.VueGWTBundle.message;

import com.axellience.vuegwtplugin.VueGWTIcons;
import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog.Builder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;

public class NewComponentAction extends CreateFileFromTemplateAction
{
    private static final String VUE_GWT_TEMPLATE_NAME = "VueGWT Component";

    public NewComponentAction()
    {
        super(message("newcomponent.menu.action.text"),
              message("newcomponent.menu.action.description"), VueGWTIcons.VUE);
    }

    @Override
    protected void buildDialog(Project project, PsiDirectory psiDirectory, Builder builder)
    {
        builder.setTitle(message("newcomponent.dialog.title"))
               .addKind(message("newcomponent.dialog.prompt"),
                        VueGWTIcons.VUE,
                        VUE_GWT_TEMPLATE_NAME);
    }

    @Override
    protected String getActionName(PsiDirectory directory, String newName, String templateName)
    {
        return message("newcomponent.menu.action.name", newName);
    }
}
