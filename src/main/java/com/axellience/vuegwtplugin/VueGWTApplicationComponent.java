package com.axellience.vuegwtplugin;

import static com.intellij.AppTopics.FILE_DOCUMENT_SYNC;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;

public class VueGWTApplicationComponent implements ProjectComponent
{
    private final Project project;
    private MessageBusConnection messageBusConnection;

    public VueGWTApplicationComponent(Project project)
    {
        this.project = project;
    }

    @Override
    public void projectOpened()
    {
        messageBusConnection = ApplicationManager.getApplication().getMessageBus().connect(project);
        messageBusConnection.subscribe(FILE_DOCUMENT_SYNC, new VueGWTFileWatcher(project));
    }

    @Override
    public void projectClosed()
    {
        messageBusConnection.disconnect();
    }

    @Override
    public void initComponent()
    {
    }

    @Override
    public void disposeComponent()
    {
    }

    @NotNull
    @Override
    public String getComponentName()
    {
        return "Vue GWT";
    }
}
