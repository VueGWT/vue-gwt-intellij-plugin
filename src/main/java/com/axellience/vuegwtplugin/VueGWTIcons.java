package com.axellience.vuegwtplugin;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class VueGWTIcons
{
    private static Icon load(String path)
    {
        return IconLoader.getIcon(path, VueGWTIcons.class);
    }

    public static final Icon VueGWT = load("/icons/vue.png"); // 16x16
}
