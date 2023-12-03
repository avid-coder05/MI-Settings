package com.android.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.SystemProperties;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.telephony.SmsApplication;
import com.android.settings.applications.DefaultAppsHelper;
import java.util.List;

/* loaded from: classes.dex */
public class DefalutApplicationLoader {
    private static final String CUSTOMIZED_REGION = SystemProperties.get("ro.miui.customized.region", "");
    private static final String TAG = "DefalutApplicationLoader";

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.DefalutApplicationLoader$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$DefalutApplicationLoader$DefaultPackageConfig;

        static {
            int[] iArr = new int[DefaultPackageConfig.values().length];
            $SwitchMap$com$android$settings$DefalutApplicationLoader$DefaultPackageConfig = iArr;
            try {
                iArr[DefaultPackageConfig.MMS_ARRAY.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$DefalutApplicationLoader$DefaultPackageConfig[DefaultPackageConfig.BROWSER_ARRAY.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$DefalutApplicationLoader$DefaultPackageConfig[DefaultPackageConfig.DIALER_ARRAY.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum DefaultPackageConfig {
        MMS_ARRAY(R.array.default_mms_package, -1),
        DIALER_ARRAY(R.array.default_dialer_package, -1),
        BROWSER_ARRAY(R.array.defalut_browser_package, -1),
        GALLERY_ARRAY(R.array.default_gallery_package, 5),
        CAMERA_ARRAY(R.array.default_camera_package, 4),
        MUSIC_ARRAY(R.array.default_music_package, 6),
        EMAIL_ARRAY(R.array.default_email_package, 7),
        VIDEO_ARRAY(R.array.default_video_package, 8);

        public final int arrayID;
        public final int intentFlag;

        DefaultPackageConfig(int i, int i2) {
            this.arrayID = i;
            this.intentFlag = i2;
        }
    }

    private static String getConfigAppName(Context context, String str, String str2, int i) {
        if (TextUtils.isEmpty(str) && TextUtils.isEmpty(str2)) {
            return null;
        }
        for (String str3 : context.getResources().getStringArray(i)) {
            if (!TextUtils.isEmpty(str3) && str3.contains(":")) {
                String[] split = str3.split(":");
                if (split.length >= 2 && (str.equals(split[0]) || str2.equals(split[0]))) {
                    return split[1];
                }
            }
        }
        return null;
    }

    private static String getDefaultDialerPackage(Context context) {
        ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(getDialIntentWithTelScheme(), 0);
        if (resolveActivity != null) {
            return resolveActivity.activityInfo.packageName;
        }
        return null;
    }

    private static Intent getDialIntentWithTelScheme() {
        Intent intent = new Intent("android.intent.action.DIAL");
        intent.setData(Uri.fromParts("tel", "", null));
        return intent;
    }

    private static boolean isNeedLoadConfig(Context context) {
        String str = SystemProperties.get("persist.sys.cota.carrier", "");
        return ((TextUtils.isEmpty(str) || "XM".equals(str)) && TextUtils.isEmpty(CUSTOMIZED_REGION)) ? false : true;
    }

    public static void load(Context context) {
        if (isNeedLoadConfig(context)) {
            for (DefaultPackageConfig defaultPackageConfig : DefaultPackageConfig.values()) {
                setDefaultApp(context, defaultPackageConfig);
            }
        }
    }

    public static void setDefaultApp(Context context, DefaultPackageConfig defaultPackageConfig) {
        String configAppName = getConfigAppName(context, CUSTOMIZED_REGION, SystemProperties.get("persist.sys.cota.carrier", ""), defaultPackageConfig.arrayID);
        if (!DefaultAppsHelper.isAppInstalled(context, configAppName)) {
            Log.w(TAG, "the package is not installed or is empty:" + configAppName);
            return;
        }
        int i = AnonymousClass1.$SwitchMap$com$android$settings$DefalutApplicationLoader$DefaultPackageConfig[defaultPackageConfig.ordinal()];
        if (i == 1) {
            SmsApplication.setDefaultApplication(configAppName, context);
        } else if (i == 2) {
            context.getPackageManager().setDefaultBrowserPackageNameAsUser(configAppName, context.getUserId());
        } else if (i != 3) {
            setDefaultOtherApp(context, DefaultAppsHelper.getIntentFilter(defaultPackageConfig.intentFlag), configAppName);
        } else {
            setDefaultDialerApplication(context, configAppName);
        }
    }

    private static void setDefaultDialerApplication(Context context, String str) {
        String defaultDialerPackage = getDefaultDialerPackage(context);
        if (defaultDialerPackage != null) {
            context.getPackageManager().clearPackagePreferredActivities(defaultDialerPackage);
        }
        TelecomManager.from(context).setDefaultDialer(str);
        List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(getDialIntentWithTelScheme(), 0);
        if (queryIntentActivities == null) {
            return;
        }
        ComponentName[] componentNameArr = new ComponentName[queryIntentActivities.size()];
        int i = Integer.MIN_VALUE;
        ComponentName componentName = null;
        int i2 = 0;
        for (int i3 = 0; i3 < queryIntentActivities.size(); i3++) {
            ResolveInfo resolveInfo = queryIntentActivities.get(i3);
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            ComponentName componentName2 = new ComponentName(activityInfo.packageName, activityInfo.name);
            if (resolveInfo.activityInfo.packageName.equals(str) && (componentName == null || i < resolveInfo.priority)) {
                i = resolveInfo.priority;
                componentName = componentName2;
            }
            int i4 = resolveInfo.match;
            if (i4 > i2) {
                i2 = i4;
            }
            componentNameArr[i3] = componentName2;
        }
        if (componentName == null) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.DIAL");
        intentFilter.addAction("android.intent.action.VIEW");
        intentFilter.addCategory("android.intent.category.DEFAULT");
        intentFilter.addDataScheme("tel");
        context.getPackageManager().addPreferredActivity(intentFilter, i2, componentNameArr, componentName);
    }

    private static void setDefaultOtherApp(Context context, IntentFilter intentFilter, String str) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = DefaultAppsHelper.getIntent(intentFilter);
        ResolveInfo resolveActivity = packageManager.resolveActivity(intent, 0);
        if (resolveActivity == null) {
            Log.w(TAG, "Do not find such type app");
            return;
        }
        ActivityInfo activityInfo = resolveActivity.activityInfo;
        String str2 = activityInfo != null ? activityInfo.packageName : null;
        Intent intent2 = new Intent(intent);
        intent2.setPackage(str);
        ComponentName resolveActivity2 = intent2.resolveActivity(packageManager);
        if (resolveActivity2 == null) {
            Log.w(TAG, "Cannot resolve activity from current intent: " + intent2.toString());
            return;
        }
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 131072);
        int size = queryIntentActivities.size();
        if (size == 0) {
            Log.w(TAG, "ResolveInfo list is empty");
            return;
        }
        ComponentName[] componentNameArr = new ComponentName[size];
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            ResolveInfo resolveInfo = queryIntentActivities.get(i2);
            ActivityInfo activityInfo2 = resolveInfo.activityInfo;
            componentNameArr[i2] = new ComponentName(activityInfo2.packageName, activityInfo2.name);
            int i3 = resolveInfo.match;
            if (i3 > i) {
                i = i3;
            }
        }
        if (str2 != null) {
            packageManager.clearPackagePreferredActivities(str2);
        }
        intentFilter.addCategory("android.intent.category.DEFAULT");
        intentFilter.addCategory("android.intent.category.BROWSABLE");
        packageManager.addPreferredActivity(intentFilter, i, componentNameArr, new ComponentName(str, resolveActivity2.getClassName()));
    }
}
