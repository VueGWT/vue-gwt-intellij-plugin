package com.axellience.vuegwtplugin.language;

import com.axellience.vuegwtplugin.language.htmltemplate.HtmlTemplateFileType;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class VueGWTFileTypeFactory extends FileTypeFactory
{
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer)
    {
        fileTypeConsumer.consume(HtmlTemplateFileType.INSTANCE);
    }
}
