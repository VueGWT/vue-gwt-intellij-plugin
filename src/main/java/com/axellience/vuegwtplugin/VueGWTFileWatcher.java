package com.axellience.vuegwtplugin;

import java.io.File;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

public class VueGWTFileWatcher extends FileDocumentManagerAdapter
{
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

    private void processFile(VirtualFile virtualFile)
    {
        if (!"html".equals(virtualFile.getExtension()))
            return;

        String javaClassFileName = virtualFile.getNameWithoutExtension() + ".java";
        VirtualFile parent = virtualFile.getParent();
        if (parent == null)
            return;

        for (VirtualFile file : parent.getChildren())
        {
            if (javaClassFileName.equals(file.getName()))
            {
                saveFileIfNeeded(FileDocumentManager.getInstance(), file);
                new File(file.getPath()).setLastModified(System.currentTimeMillis());
                file.refresh(false, false);
                return;
            }
        }
    }

    private static void saveFileIfNeeded(@NotNull FileDocumentManager fileDocumentManager,
        @NotNull VirtualFile virtualFile)
    {
        // Gets the document to force its saving when the editor is closed.
        // If it is not cached, no editor was opened to edit it => nothing to be done
        final Document fileDocument = fileDocumentManager.getCachedDocument(virtualFile);

        if (fileDocument != null && fileDocumentManager.isDocumentUnsaved(fileDocument))
            fileDocumentManager.saveDocument(fileDocument);
    }

    private final Project project;
}
