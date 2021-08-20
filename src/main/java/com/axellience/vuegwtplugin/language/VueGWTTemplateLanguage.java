package com.axellience.vuegwtplugin.language;

import com.intellij.lang.html.HTMLLanguage;
import com.intellij.lang.xml.XMLLanguage;

public class VueGWTTemplateLanguage extends XMLLanguage
{
    public static VueGWTTemplateLanguage INSTANCE = new VueGWTTemplateLanguage();

    VueGWTTemplateLanguage()
    {
        super(HTMLLanguage.INSTANCE, "VueGWT-Template");
    }
}
