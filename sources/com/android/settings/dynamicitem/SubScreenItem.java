package com.android.settings.dynamicitem;

import android.content.Context;
import android.os.Build;
import com.android.settings.MiuiUtils;
import com.android.settingslib.miuisettings.preference.PreferenceActivity;

/* loaded from: classes.dex */
public class SubScreenItem extends DynamicItem {
    @Override // com.android.settings.dynamicitem.DynamicItem
    public void setDetail(Context context, PreferenceActivity.Header header) {
        header.title = MiuiUtils.getStringByResName(context, "com.android.settings", "subscreen_title");
        header.intent.setClassName("com.xiaomi.misubscreenui", "com.xiaomi.misubscreenui.SubscreenSettingsActivity");
    }

    @Override // com.android.settings.dynamicitem.DynamicItem
    public boolean shouldShow(Context context) {
        return "star".equals(Build.DEVICE);
    }
}
