package com.android.settings.cloudbackup;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.telecom.DefaultDialerManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.telephony.SmsApplication;
import com.android.settings.applications.DefaultAppsHelper;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class DefaultAppSettingsCloudBackupHelper {
    private static PackageManager mPm;
    private static int mUserId = UserHandle.myUserId();

    private static String getDefaultAppData(int i) {
        ResolveInfo resolveActivity = mPm.resolveActivity(DefaultAppsHelper.getIntent(DefaultAppsHelper.getIntentFilter(i)), 0);
        if (isResolveInfoValid(resolveActivity)) {
            ActivityInfo activityInfo = resolveActivity.activityInfo;
            return new ComponentName(activityInfo.packageName, activityInfo.name).flattenToString();
        }
        return "";
    }

    private static boolean isAppExist(String str) {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = mPm.getApplicationInfo(str, 0);
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e("DefaultAppCloudBackupHelper", "pkgName not exist: " + str);
            CloudBackupException.trackException("PkgNameNotFoundException");
            applicationInfo = null;
        }
        return applicationInfo != null;
    }

    private static boolean isResolveInfoValid(ResolveInfo resolveInfo) {
        return (resolveInfo == null || resolveInfo.activityInfo == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void restoreFromCloud(Context context, JSONObject jSONObject) {
        if (jSONObject == null) {
            return;
        }
        mPm = context.getPackageManager();
        if (jSONObject.has("CKDefaultHome")) {
            setDefaultApp(jSONObject.optString("CKDefaultHome"), 0);
        }
        if (jSONObject.has("CKDefaultPhone")) {
            String optString = jSONObject.optString("CKDefaultPhone");
            if (!TextUtils.isEmpty(optString)) {
                DefaultDialerManager.setDefaultDialerApplication(context, optString, mUserId);
            }
        }
        if (jSONObject.has("CKDefaultSms")) {
            String optString2 = jSONObject.optString("CKDefaultSms");
            if (!TextUtils.isEmpty(optString2)) {
                SmsApplication.setDefaultApplication(optString2, context);
            }
        }
        if (jSONObject.has("CKDefaultBrowser")) {
            String optString3 = jSONObject.optString("CKDefaultBrowser");
            if (!TextUtils.isEmpty(optString3) && isAppExist(optString3)) {
                mPm.setDefaultBrowserPackageNameAsUser(optString3, mUserId);
            }
        }
        if (jSONObject.has("CKDefaultCamera")) {
            setDefaultApp(jSONObject.optString("CKDefaultCamera"), 4);
        }
        if (jSONObject.has("CKDefaultGallery")) {
            setDefaultApp(jSONObject.optString("CKDefaultGallery"), 5);
        }
        if (jSONObject.has("CKDefaultMusic")) {
            setDefaultApp(jSONObject.optString("CKDefaultMusic"), 6);
        }
        if (jSONObject.has("CKDefaultEmail")) {
            setDefaultApp(jSONObject.optString("CKDefaultEmail"), 7);
        }
        if (jSONObject.has("CKDefaultVideo")) {
            setDefaultApp(jSONObject.optString("CKDefaultVideo"), 8);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static JSONObject saveToCloud(Context context) {
        JSONObject jSONObject = new JSONObject();
        mPm = context.getPackageManager();
        try {
            String defaultAppData = getDefaultAppData(0);
            if (!TextUtils.isEmpty(defaultAppData)) {
                jSONObject.put("CKDefaultHome", defaultAppData);
            }
            String defaultDialerApplication = DefaultDialerManager.getDefaultDialerApplication(context, mUserId);
            if (!TextUtils.isEmpty(defaultAppData)) {
                jSONObject.put("CKDefaultPhone", defaultDialerApplication);
            }
            ComponentName defaultSmsApplication = SmsApplication.getDefaultSmsApplication(context, true);
            if (defaultSmsApplication != null && !TextUtils.isEmpty(defaultSmsApplication.getPackageName())) {
                jSONObject.put("CKDefaultSms", defaultSmsApplication.getPackageName());
            }
            String defaultBrowserPackageNameAsUser = mPm.getDefaultBrowserPackageNameAsUser(mUserId);
            if (!TextUtils.isEmpty(defaultBrowserPackageNameAsUser)) {
                jSONObject.put("CKDefaultBrowser", defaultBrowserPackageNameAsUser);
            }
            String defaultAppData2 = getDefaultAppData(4);
            if (!TextUtils.isEmpty(defaultAppData2)) {
                jSONObject.put("CKDefaultCamera", defaultAppData2);
            }
            String defaultAppData3 = getDefaultAppData(5);
            if (!TextUtils.isEmpty(defaultAppData3)) {
                jSONObject.put("CKDefaultGallery", defaultAppData3);
            }
            String defaultAppData4 = getDefaultAppData(6);
            if (!TextUtils.isEmpty(defaultAppData4)) {
                jSONObject.put("CKDefaultMusic", defaultAppData4);
            }
            String defaultAppData5 = getDefaultAppData(7);
            if (!TextUtils.isEmpty(defaultAppData5)) {
                jSONObject.put("CKDefaultEmail", defaultAppData5);
            }
            String defaultAppData6 = getDefaultAppData(8);
            if (!TextUtils.isEmpty(defaultAppData6)) {
                jSONObject.put("CKDefaultVideo", defaultAppData6);
            }
        } catch (JSONException unused) {
            Log.e("DefaultAppCloudBackupHelper", "Build JSON failed.");
            CloudBackupException.trackException();
        }
        return jSONObject;
    }

    private static void setDefaultApp(String str, int i) {
        if (TextUtils.isEmpty(str)) {
            Log.e("DefaultAppCloudBackupHelper", "setDefaultApp, data is null, type: " + i);
            return;
        }
        ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
        Intent intent = new Intent();
        intent.setComponent(unflattenFromString);
        if (mPm.resolveActivity(intent, 0) == null) {
            Log.e("DefaultAppCloudBackupHelper", "Component not exist, data: " + str + ", type: " + i);
            return;
        }
        IntentFilter intentFilter = DefaultAppsHelper.getIntentFilter(i);
        Intent intent2 = DefaultAppsHelper.getIntent(intentFilter);
        String str2 = null;
        ResolveInfo resolveActivity = mPm.resolveActivity(intent2, 0);
        if (isResolveInfoValid(resolveActivity)) {
            str2 = resolveActivity.activityInfo.packageName;
            if (unflattenFromString.getPackageName().equals(str2) && unflattenFromString.getClassName().equals(resolveActivity.activityInfo.name)) {
                return;
            }
        }
        List<ResolveInfo> queryIntentActivities = mPm.queryIntentActivities(intent2, 131072);
        int size = queryIntentActivities.size();
        ComponentName[] componentNameArr = new ComponentName[size];
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            ResolveInfo resolveInfo = queryIntentActivities.get(i3);
            if (isResolveInfoValid(resolveInfo)) {
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                componentNameArr[i3] = new ComponentName(activityInfo.packageName, activityInfo.name);
                int i4 = resolveInfo.match;
                if (i4 > i2) {
                    i2 = i4;
                }
            }
        }
        intentFilter.addCategory("android.intent.category.DEFAULT");
        intentFilter.addCategory("android.intent.category.BROWSABLE");
        if (intentFilter.countDataAuthorities() == 0 && intentFilter.countDataPaths() == 0 && intentFilter.countDataSchemes() <= 1 && intentFilter.countDataTypes() == 0 && i != 0) {
            mPm.replacePreferredActivity(intentFilter, i2, componentNameArr, new ComponentName("com.no.such.packagename", "com.no.such.packagename.no.such.class"));
            mPm.clearPackagePreferredActivities("com.no.such.packagename");
        } else if (!TextUtils.isEmpty(str2)) {
            mPm.clearPackagePreferredActivities(str2);
        }
        mPm.addPreferredActivity(intentFilter, i2, componentNameArr, unflattenFromString);
    }
}
