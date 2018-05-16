package com.axellience.vuegwtplugin;

import javax.swing.Icon;

import com.intellij.openapi.util.IconLoader;

public class VueGWTIcons
{
    public static final Icon VUE = load("/icons/vue.png");

    private static Icon load(String path)
    {
        return IconLoader.getIcon(path, VueGWTIcons.class);
    }
}
