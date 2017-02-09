package com.alexispounne.projectplatypusii;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Created by Alexandre on 22/01/2017.
 */
public class LangSupportContextWrapper extends ContextWrapper {
    public LangSupportContextWrapper(Context base) {
        super(base);
    }

    public static ContextWrapper wrap(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        context = context.createConfigurationContext(config);
        return new LangSupportContextWrapper(context);
    }
}
