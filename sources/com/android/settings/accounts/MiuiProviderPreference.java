package com.android.settings.accounts;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes.dex */
public class MiuiProviderPreference extends Preference {
    private String mAccountType;

    public MiuiProviderPreference(Context context, String str, Drawable drawable, CharSequence charSequence) {
        super(context, true);
        this.mAccountType = str;
        setIcon(drawable);
        setPersistent(false);
        setTitle(charSequence);
    }

    public String getAccountType() {
        return this.mAccountType;
    }
}
