package com.android.settings.dynamicitem;

import android.content.Context;
import android.content.Intent;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.PreferenceActivity;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class UWBItem extends DynamicItem {
    private Intent getLauncherIntent() {
        Intent intent = new Intent();
        intent.setClassName("com.miui.smarthomeplus", "com.miui.smarthomeplus.settings.uwb.UwbSettingsActivity");
        intent.putExtra("source", "systemSettings");
        return intent;
    }

    @Override // com.android.settings.dynamicitem.DynamicItem
    public void setDetail(Context context, PreferenceActivity.Header header) {
        header.titleRes = R.string.launch_smarthome;
        header.intent = getLauncherIntent();
    }

    @Override // com.android.settings.dynamicitem.DynamicItem
    public boolean shouldShow(Context context) {
        return MiuiUtils.isUWBSupport(context) && FeatureParser.getBoolean("support_show_in_main_settings", false);
    }
}
