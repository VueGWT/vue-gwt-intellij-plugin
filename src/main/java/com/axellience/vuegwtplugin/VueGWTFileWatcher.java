package com.axellience.vuegwtplugin;

import org.jetbrains.annotations.NotNull;

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
        if (!"html".equals(changedFile.getExtension()))
            return;

        String javaClassFileName = changedFile.getNameWithoutExtension() + ".java";
        VirtualFile parent = changedFile.getParent();
        if (parent == null)
            return;

        for (VirtualFile siblingFile : parent.getChildren())
        {
            if (javaClassFileName.equals(siblingFile.getName()))
            {
                ApplicationManager
                    .getApplication()
                    .invokeLater(() -> compileComponent(changedFile, siblingFile));
                return;
            }
        }
    }

    private void compileComponent(VirtualFile htmlTemplate, VirtualFile javaComponent)
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
