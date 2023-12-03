package com.android.settings.dynamicitem;

import android.content.Context;
import com.android.settingslib.miuisettings.preference.PreferenceActivity;

/* loaded from: classes.dex */
public abstract class DynamicItem {
    public abstract void setDetail(Context context, PreferenceActivity.Header header);

    public boolean shouldShow(Context context) {
        return true;
    }
}
