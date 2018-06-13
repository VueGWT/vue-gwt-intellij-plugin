package com.axellience.vuegwtplugin.language;

import com.intellij.lang.html.HTMLLanguage;
import com.intellij.lang.xml.XMLLanguage;

public class VueGWTLanguage extends XMLLanguage
{
    public static VueGWTLanguage INSTANCE = new VueGWTLanguage();

    VueGWTLanguage()
    {
        super(HTMLLanguage.INSTANCE, "VueGWT");
    }
}
