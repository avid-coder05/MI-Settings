package com.android.settings.search.appseparate;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import com.android.settings.search.SearchUpdater;
import com.android.settingslib.search.SettingsTree;

/* loaded from: classes2.dex */
public class PathProcessor {
    Context mContext;

    public PathProcessor(Context context) {
        this.mContext = context;
    }

    public static int getIcon(Context context, int i, String str) {
        String str2 = (String) SettingsTree.sSettingsEntryToIconMap.get(str);
        if (!TextUtils.isEmpty(str2)) {
            try {
                return context.getResources().getIdentifier(str2, "drawable", "com.android.settings");
            } catch (Exception unused) {
            }
        }
        return i;
    }

    public static String process(Context context, SearchRawData searchRawData, Intent intent) {
        if (TextUtils.isEmpty(searchRawData.summaryOn)) {
            try {
                ActivityInfo resolveActivityInfo = intent.resolveActivityInfo(context.getPackageManager(), SearchUpdater.GOOGLE);
                Intent component = new Intent().setComponent(new ComponentName(resolveActivityInfo.packageName, resolveActivityInfo.name));
                ComponentName component2 = component.getComponent();
                if (component2 == null) {
                    return searchRawData.summaryOn;
                }
                String str = (String) SettingsTree.sSettingsEntryToPathMap.get(component2.flattenToString());
                if (TextUtils.isEmpty(str)) {
                    return searchRawData.summaryOn;
                }
                searchRawData.iconResId = getIcon(context, searchRawData.iconResId, component.getComponent().flattenToString());
                if (TextUtils.isEmpty(searchRawData.other)) {
                    return str;
                }
                return str + "/" + searchRawData.other;
            } catch (Exception unused) {
                return searchRawData.summaryOn;
            }
        }
        return searchRawData.summaryOn;
    }
}
