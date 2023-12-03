package com.android.settingslib;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.provider.Settings;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;

/* loaded from: classes2.dex */
public class OldmanHelper {
    public static List<ApplicationInfo> filterOldmanModeApp(List<ApplicationInfo> list) {
        if (isOldmanMode()) {
            return list;
        }
        ArrayList arrayList = new ArrayList(list);
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            if (isHideOldModeApp(((ApplicationInfo) it.next()).packageName)) {
                it.remove();
            }
        }
        return arrayList;
    }

    private static boolean isHideOldModeApp(String str) {
        return "com.jeejen.family.miui".equals(str) || "com.jeejen.knowledge".equals(str) || "com.jeejen.store".equals(str);
    }

    public static boolean isOldmanMode() {
        return Build.getUserMode() == 1;
    }

    public static boolean isStatusBarSettingsHidden(Context context) {
        if (isOldmanMode()) {
            int i = Settings.System.getInt(context.getContentResolver(), "elder.settings.status_bar_settings_hidden", 1);
            if (i == 2) {
                i = (Settings.System.getInt(context.getContentResolver(), "elder.systemui.allow_status_expand", 0) != 0 ? 1 : 0) ^ 1;
            }
            return i != 0;
        }
        return false;
    }
}
