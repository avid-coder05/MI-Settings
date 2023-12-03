package com.android.settings.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.usagestats.utils.CommonUtils;
import com.android.settingslib.miuisettings.preference.PreferenceActivity;
import java.util.List;

/* loaded from: classes2.dex */
public class HomeListUtils {
    public static void addAmazonAlexa(Context context, List<PreferenceActivity.Header> list, int i) {
        Intent amazonAlexIntent = getAmazonAlexIntent();
        if (shouldAddAmazonAlex(context, amazonAlexIntent)) {
            int i2 = 0;
            int i3 = 0;
            while (true) {
                if (i3 >= list.size()) {
                    break;
                } else if (list.get(i3).id == i) {
                    i2 = i3 + 1;
                    break;
                } else {
                    i3++;
                }
            }
            list.add(i2, createAlexaHeader(context, amazonAlexIntent));
        }
    }

    private static PreferenceActivity.Header createAlexaHeader(Context context, Intent intent) {
        PreferenceActivity.Header header = new PreferenceActivity.Header();
        header.title = getAlexaAppName(context);
        header.iconRes = R.drawable.ic_alexa_widget_icon;
        header.intent = intent;
        return header;
    }

    public static void ensureReplaceTimer(Context context, PreferenceActivity.Header header) {
        Intent intentTimerIntent = CommonUtils.getIntentTimerIntent();
        if (CommonUtils.hasIndependentTimer(context, intentTimerIntent)) {
            header.fragment = null;
            header.intent = intentTimerIntent;
        }
    }

    public static void ensureShowWellbeing(Context context, List<PreferenceActivity.Header> list, PreferenceActivity.Header header) {
        Intent wellbeingIntent = getWellbeingIntent();
        if (!MiuiUtils.getInstance().canFindActivity(context, wellbeingIntent)) {
            list.remove(header);
            return;
        }
        header.intent = wellbeingIntent;
        header.fragment = null;
        header.titleRes = R.string.wellbing_title;
        header.iconRes = R.drawable.ic_google_wellbeing;
    }

    public static String getAlexaAppName(Context context) {
        return getAppName(context, "com.amazon.dee.app");
    }

    public static Intent getAmazonAlexIntent() {
        Intent intent = new Intent();
        intent.setAction("com.amazon.alexa.handsfree.SETTINGS");
        return intent;
    }

    public static String getAppName(Context context, String str) {
        if (context != null) {
            PackageManager packageManager = context.getPackageManager();
            try {
                return packageManager.getApplicationLabel(packageManager.getApplicationInfo(str, 128)).toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static Intent getWellbeingIntent() {
        Intent intent = new Intent();
        intent.setPackage("com.google.android.apps.wellbeing");
        intent.setClassName("com.google.android.apps.wellbeing", "com.google.android.apps.wellbeing.settings.TopLevelSettingsActivity");
        return intent;
    }

    public static boolean shouldAddAmazonAlex(Context context, Intent intent) {
        if ("begoniain".equals(Build.DEVICE)) {
            return MiuiUtils.getInstance().canFindActivity(context, intent);
        }
        return false;
    }
}
