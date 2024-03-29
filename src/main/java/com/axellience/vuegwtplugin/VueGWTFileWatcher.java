package com.axellience.vuegwtplugin;

import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.compiler.ex.CompilerPathsEx;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class VueGWTFileWatcher implements FileDocumentManagerListener {

  private static final Logger LOGGER = Logger.getInstance(VueGWTFileWatcher.class);

  @Override
  public void beforeDocumentSaving(@NotNull Document document) {
    getProjectAsync().onSuccess(project -> {
      PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
      if (psiFile == null) {
        return;
      }

      processFile(psiFile.getVirtualFile());
    });
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
    getProjectAsync().onSuccess(project -> {
      ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
      final Module module = projectFileIndex.getModuleForFile(javaComponent);
      if (module == null) {
        return;
      }
      var sourceRoot = projectFileIndex.getSourceRootForFile(javaComponent);
      if(sourceRoot == null)
        return;

      var htmlPath = htmlTemplate.getPath();
      var relativeHtmlPath = htmlPath.substring(sourceRoot.getPath().length());
      var outputs = CompilerPaths.getOutputPaths(new Module[] { module });
      if(outputs.length == 0)
        return;

      var targetPath = Path.of(outputs[0], relativeHtmlPath);
      try {
        Files.copy(Path.of(htmlPath), targetPath, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        return;
      }

      CompilerManager compilerManager = CompilerManager.getInstance(project);
      if (!compilerManager.isCompilationActive()
          && !compilerManager.isExcludedFromCompilation(javaComponent)) {
        compilerManager.compile(new VirtualFile[] {javaComponent}, null);
      }
    });
  }

  @Nullable
  private VirtualFile getSibling(VirtualFile file, String siblingName) {
    VirtualFile parent = file.getParent();
    if (parent == null) {
      return null;
    }

    return parent.findChild(siblingName);
  }

  private Promise<Project> getProjectAsync() {
    return DataManager.getInstance().getDataContextFromFocusAsync()
        .then(CommonDataKeys.PROJECT::getData)
        .onError(throwable -> LOGGER.warn("Unable to get project from focus", throwable));
  }
}
