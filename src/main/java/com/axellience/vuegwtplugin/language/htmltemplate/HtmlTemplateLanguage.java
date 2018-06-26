package com.axellience.vuegwtplugin.language.htmltemplate;

import com.intellij.lang.html.HTMLLanguage;
import com.intellij.lang.xml.XMLLanguage;

public class HtmlTemplateLanguage extends XMLLanguage
{
    public static HtmlTemplateLanguage INSTANCE = new HtmlTemplateLanguage();

    HtmlTemplateLanguage()
    {
        super(HTMLLanguage.INSTANCE, "VueGWT-Template");
    }
}
