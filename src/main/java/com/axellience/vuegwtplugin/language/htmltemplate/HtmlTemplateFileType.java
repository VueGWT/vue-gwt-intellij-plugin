package com.axellience.vuegwtplugin.language.htmltemplate;

import com.axellience.vuegwtplugin.VueGWTIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.ex.FileTypeIdentifiableByVirtualFile;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class HtmlTemplateFileType extends LanguageFileType implements FileTypeIdentifiableByVirtualFile
{
    public static final HtmlTemplateFileType INSTANCE = new HtmlTemplateFileType();

    HtmlTemplateFileType()
    {
        super(HtmlTemplateLanguage.INSTANCE);
    }

    @Override
    public boolean isMyFileType(@NotNull VirtualFile virtualFile)
    {
        if (!"html".equals(virtualFile.getExtension()))
            return false;

        VirtualFile parent = virtualFile.getParent();
        if (parent == null)
            return false;

        return parent.findChild(virtualFile.getNameWithoutExtension() + ".java") != null;
    }

    @NotNull
    @Override
    public String getName()
    {
        return "VueGWT";
    }

    @NotNull
    @Override
    public String getDescription()
    {
        return "VueGWT Template";
    }

    @NotNull
    @Override
    public String getDefaultExtension()
    {
        return "";
    }

    @Nullable
    @Override
    public Icon getIcon()
    {
        return VueGWTIcons.VUE;
    }
}
