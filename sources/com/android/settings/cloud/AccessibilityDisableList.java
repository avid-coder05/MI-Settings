package com.android.settings.cloud;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.MiuiSettings;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.utils.ThreadUtils;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class AccessibilityDisableList {
    public static final Set<String> sCloudDisableSet = new CopyOnWriteArraySet();
    private static final AtomicBoolean sFinishCache = new AtomicBoolean();

    public static Set<String> getCacheDisableSet(Context context) {
        if (sFinishCache.get()) {
            return sCloudDisableSet;
        }
        Log.w("AccessibilityDisableLis", "get accessibility disable list too slow");
        return getDisableSet(context);
    }

    public static Set<String> getDisableSet(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        HashSet hashSet = new HashSet();
        List cloudDataList = MiuiSettings.SettingsCloudData.getCloudDataList(contentResolver, "accessibility_disable");
        if (cloudDataList != null && cloudDataList.size() != 0) {
            try {
                Iterator it = cloudDataList.iterator();
                while (it.hasNext()) {
                    String cloudData = ((MiuiSettings.SettingsCloudData.CloudData) it.next()).toString();
                    if (!TextUtils.isEmpty(cloudData)) {
                        String optString = new JSONObject(cloudData).optString("pkg");
                        if (!TextUtils.isEmpty(optString)) {
                            hashSet.add(optString);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return hashSet;
    }

    public static void updateDisableSet(final Context context) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.cloud.AccessibilityDisableList.1
            @Override // java.lang.Runnable
            public void run() {
                AccessibilityDisableList.sFinishCache.set(false);
                List cloudDataList = MiuiSettings.SettingsCloudData.getCloudDataList(context.getContentResolver(), "accessibility_disable");
                AccessibilityDisableList.sCloudDisableSet.clear();
                if (cloudDataList == null || cloudDataList.size() == 0) {
                    AccessibilityDisableList.sFinishCache.set(true);
                    return;
                }
                try {
                    Iterator it = cloudDataList.iterator();
                    while (it.hasNext()) {
                        String cloudData = ((MiuiSettings.SettingsCloudData.CloudData) it.next()).toString();
                        if (!TextUtils.isEmpty(cloudData)) {
                            String optString = new JSONObject(cloudData).optString("pkg");
                            if (!TextUtils.isEmpty(optString)) {
                                AccessibilityDisableList.sCloudDisableSet.add(optString);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AccessibilityDisableList.sFinishCache.set(true);
            }
        });
    }
}
