package com.axellience.vuegwtplugin;

import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

public class VueGWTFileWatcher implements FileDocumentManagerListener {

  private static final Logger LOGGER = Logger.getInstance(VueGWTFileWatcher.class);
  private final Project project;

  VueGWTFileWatcher() {
    this.project = Arrays.stream(ProjectManager.getInstance().getOpenProjects())
        .findFirst()
        .orElseThrow(RuntimeException::new);
  }

  @Override
  public void beforeDocumentSaving(@NotNull Document document) {
    PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
    if (psiFile == null) {
      return;
    }

    processFile(psiFile.getVirtualFile());
  }

  private void processFile(VirtualFile changedFile) {
    if ("html".equals(changedFile.getExtension())) {
      processHtmlFile(changedFile);
    } else if ("java".equals(changedFile.getExtension())) {
      processJavaFile(changedFile);
    }
  }

  private void processHtmlFile(VirtualFile htmlFile) {
    VirtualFile javaFile = getSibling(htmlFile, htmlFile.getNameWithoutExtension() + ".java");
    if (javaFile == null) {
      return;
    }

    ApplicationManager.getApplication().invokeLater(() -> compileComponent(javaFile, htmlFile));
  }

  private void processJavaFile(VirtualFile javaFile) {
    VirtualFile htmlFile = getSibling(javaFile, javaFile.getNameWithoutExtension() + ".html");
    if (htmlFile == null)
      return;

    ApplicationManager.getApplication().invokeLater(() -> compileComponent(javaFile, htmlFile));
  }

  private void compileComponent(VirtualFile javaComponent, VirtualFile htmlTemplate) {
    try {
      ProjectFileIndex projectFileIndex =
          ProjectRootManager.getInstance(project).getFileIndex();
      final Module module = projectFileIndex.getModuleForFile(javaComponent);
      if (module == null) {
        return;
      }

      CompilerManager compilerManager = CompilerManager.getInstance(project);
      if (!compilerManager.isCompilationActive()
          && !compilerManager.isExcludedFromCompilation(javaComponent)) {
        compilerManager.compile(new VirtualFile[] {javaComponent, htmlTemplate}, null);
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  @Nullable
  private VirtualFile getSibling(VirtualFile file, String siblingName) {
    VirtualFile parent = file.getParent();
    if (parent == null) {
      return null;
    }

    return parent.findChild(siblingName);
  }
}
