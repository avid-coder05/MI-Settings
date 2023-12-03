package com.android.settings.search;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.PreferenceManager;
import com.android.settings.aidl.IReverseSearchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.provider.ExtraContacts;

/* loaded from: classes2.dex */
public class ReverseSearchService extends Service {
    public static final String EXTRAKEY = "ReverseSearchExtraKey";
    public static final String KEYWORDS = "keywords";
    public static final String NAME = "name";
    private static final int NOTIFICATION_ID = 1023;
    private static final String STOREKEY = "ReverseSearchStoreKey";
    private static final String TAG = "ReverseSearchService";
    private final IReverseSearchService.Stub mBinder = new IReverseSearchService.Stub() { // from class: com.android.settings.search.ReverseSearchService.1
        @Override // com.android.settings.aidl.IReverseSearchService
        public List<String> getResults(String str) {
            return ReverseSearchService.this.getResults(str);
        }
    };

    public static void createIndex() {
    }

    private String getStoredKeys() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (defaultSharedPreferences == null) {
            return null;
        }
        return defaultSharedPreferences.getString(STOREKEY, null);
    }

    public List<String> getResults(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        HashMap hashMap = new HashMap();
        String storedKeys = getStoredKeys();
        if (TextUtils.isEmpty(storedKeys)) {
            return null;
        }
        for (String str2 : storedKeys.split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (str2.contains(",")) {
                String[] split = str2.split(",");
                if (!hashMap.containsKey(split[0])) {
                    hashMap.put(split[0], split[1]);
                }
            } else {
                Log.w(TAG, "invalid key: " + str2);
            }
        }
        if (hashMap.size() == 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList(0);
        for (Map.Entry entry : hashMap.entrySet()) {
            if (entry.getValue() != null && str.toLowerCase().trim().contains(((String) entry.getKey()).toLowerCase().trim()) && !arrayList.contains(entry.getValue())) {
                arrayList.add((String) entry.getValue());
            }
        }
        return arrayList;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null) {
            intent.getBooleanExtra(EXTRAKEY, false);
        }
        return super.onStartCommand(intent, i, i2);
    }
}
