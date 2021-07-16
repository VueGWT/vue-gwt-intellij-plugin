package com.axellience.vuegwtplugin;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;
import com.intellij.AbstractBundle;

public class VueGWTBundle
{
    @NonNls
    public static final String BUNDLE = "com.axellience.vuegwtplugin.VueGWTBundle";

    private static Reference<ResourceBundle> vueGWTBundle;

    private VueGWTBundle()
    {
    }

    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key,
                                 @NotNull Object... params)
    {
        return AbstractBundle.message(getBundle(), key, params);
    }

    private static ResourceBundle getBundle()
    {
        ResourceBundle bundle = com.intellij.reference.SoftReference.dereference(vueGWTBundle);

        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE);
            vueGWTBundle = new SoftReference<>(bundle);
        }

        return bundle;
    }
}
