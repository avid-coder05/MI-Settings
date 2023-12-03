package com.android.settings.cloudbackup;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;
import com.android.settings.notification.NotificationSettingsHelper;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class NotificationCloudBackupHelper {
    private static ArrayList<String> getInstalledPackages(Context context) {
        ArrayList<String> arrayList = new ArrayList<>();
        Iterator<PackageInfo> it = context.getPackageManager().getInstalledPackages(0).iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().packageName);
        }
        return arrayList;
    }

    public static void restoreFromCloud(Context context, JSONObject jSONObject) {
        if (jSONObject == null) {
            return;
        }
        Iterator<String> keys = jSONObject.keys();
        while (keys.hasNext()) {
            String next = keys.next();
            JSONObject optJSONObject = jSONObject.optJSONObject(next);
            if (optJSONObject != null) {
                if (optJSONObject.has("CKNotificationEnable")) {
                    NotificationSettingsHelper.setNotificationsEnabledForPackage(context, next, optJSONObject.optBoolean("CKNotificationEnable"));
                }
                if (optJSONObject.has("CKFold")) {
                    NotificationSettingsHelper.setFoldImportance(context, next, optJSONObject.optInt("CKFold"));
                }
                if (optJSONObject.has("CKAggregate")) {
                    NotificationSettingsHelper.notifyAggregateConfig(context, next, optJSONObject.optInt("CKAggregate"), null);
                }
                if (optJSONObject.has("CKHomeMessage")) {
                    NotificationSettingsHelper.setShowBadge(context, next, optJSONObject.optBoolean("CKHomeMessage"));
                }
                if (optJSONObject.has("CKFloating")) {
                    NotificationSettingsHelper.setFloat(context, next, null, optJSONObject.optBoolean("CKFloating"));
                }
                if (optJSONObject.has("CKKeyguardOnly")) {
                    NotificationSettingsHelper.setShowKeyguard(context, next, null, optJSONObject.optBoolean("CKKeyguardOnly"));
                }
                if (optJSONObject.has("CKSound")) {
                    NotificationSettingsHelper.setSound(context, next, null, optJSONObject.optBoolean("CKSound"));
                }
                if (optJSONObject.has("CKVibrate")) {
                    NotificationSettingsHelper.setVibrate(context, next, null, optJSONObject.optBoolean("CKVibrate"));
                }
                if (optJSONObject.has("CKLed")) {
                    NotificationSettingsHelper.setLights(context, next, null, optJSONObject.optBoolean("CKLed"));
                }
            }
        }
    }

    public static JSONObject saveToCloud(Context context) {
        JSONObject jSONObject = new JSONObject();
        Iterator<String> it = getInstalledPackages(context).iterator();
        while (it.hasNext()) {
            String next = it.next();
            JSONObject jSONObject2 = new JSONObject();
            try {
                jSONObject.put(next, jSONObject2);
                jSONObject2.put("CKNotificationEnable", !NotificationSettingsHelper.isNotificationsBanned(context, next));
                int foldImportance = NotificationSettingsHelper.getFoldImportance(context, next);
                if (NotificationSettingsHelper.isFoldable(context, next) && foldImportance != 0) {
                    jSONObject2.put("CKFold", foldImportance);
                }
                int aggregateConfig = NotificationSettingsHelper.getAggregateConfig(context, next);
                if (aggregateConfig != 0) {
                    jSONObject2.put("CKAggregate", aggregateConfig);
                }
                jSONObject2.put("CKHomeMessage", NotificationSettingsHelper.canShowBadge(context, next));
                jSONObject2.put("CKFloating", NotificationSettingsHelper.canFloat(context, next, null));
                jSONObject2.put("CKKeyguardOnly", NotificationSettingsHelper.canShowKeyguard(context, next, null));
                jSONObject2.put("CKSound", NotificationSettingsHelper.canSound(context, next, null));
                jSONObject2.put("CKVibrate", NotificationSettingsHelper.canVibrate(context, next, null));
                jSONObject2.put("CKLed", NotificationSettingsHelper.canLights(context, next, null));
            } catch (JSONException unused) {
                Log.e("CloudBackupHelper", "Get " + next + " config JSON failed. ");
            }
        }
        return jSONObject;
    }
}
