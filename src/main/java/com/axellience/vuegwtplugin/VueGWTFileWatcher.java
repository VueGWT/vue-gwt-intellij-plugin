package com.axellience.vuegwtplugin;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class VueGWTFileWatcher extends FileDocumentManagerAdapter
{
    private static final Logger LOGGER = Logger.getInstance(VueGWTFileWatcher.class);
    private final Project project;

    VueGWTFileWatcher(Project project)
    {
        this.project = project;
    }

    @Override
    public void beforeDocumentSaving(@NotNull Document document)
    {
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (psiFile == null)
            return;

        processFile(psiFile.getVirtualFile());
    }

    private void processFile(VirtualFile changedFile)
    {
        if ("html".equals(changedFile.getExtension()))
            processHtmlFile(changedFile);
        else if ("java".equals(changedFile.getExtension()))
            processJavaFile(changedFile);
    }

    private void processHtmlFile(VirtualFile htmlFile)
    {
        String javaClassFileName = htmlFile.getNameWithoutExtension() + ".java";
        VirtualFile parent = htmlFile.getParent();
        if (parent == null)
            return;

        VirtualFile javaFile = parent.findChild(javaClassFileName);
        if (javaFile == null)
            return;

        ApplicationManager.getApplication().invokeLater(() -> compileComponent(javaFile, htmlFile));
    }

    private void processJavaFile(VirtualFile javaFile)
    {
        String htmlTemplateName = javaFile.getNameWithoutExtension() + ".html";

        VirtualFile parent = javaFile.getParent();
        if (parent == null)
            return;

        VirtualFile htmlTemplateFile = parent.findChild(htmlTemplateName);
        if (htmlTemplateFile == null)
            return;

        ApplicationManager
            .getApplication()
            .invokeLater(() -> compileComponent(javaFile, htmlTemplateFile));
    }

    private void compileComponent(VirtualFile javaComponent, VirtualFile htmlTemplate)
    {
        try
        {
            ProjectFileIndex projectFileIndex =
                ProjectRootManager.getInstance(project).getFileIndex();
            final Module module = projectFileIndex.getModuleForFile(javaComponent);
            if (module == null)
                return;

            CompilerManager compilerManager = CompilerManager.getInstance(project);
            if (!compilerManager.isCompilationActive()
                && !compilerManager.isExcludedFromCompilation(javaComponent))
            {
                compilerManager.compile(new VirtualFile[] { javaComponent, htmlTemplate }, null);
            }
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
