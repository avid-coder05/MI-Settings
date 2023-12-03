package com.android.settings.search.appseparate;

import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class PreMatchData {
    private static final String ITEMS = "items";
    static final String PACKAGE_LIST = "packageList";
    static final String PACKAGE_NAME = "packageName";
    private static final String TAG = "PreMatchData";
    static final String VERSION_CODE = "versionCode";
    private boolean mIsQueryProviderComplete = true;
    private final Map<String, List<SearchRawData>> mPreMatchDataMap = new ConcurrentHashMap();
    private final Map<String, Long> mVersionCodeMap = new ConcurrentHashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static PreMatchData fromJson(JSONObject jSONObject) {
        PreMatchData preMatchData = new PreMatchData();
        JSONArray optJSONArray = jSONObject.optJSONArray(PACKAGE_LIST);
        Gson gson = new Gson();
        if (isJSONArrayEmpty(optJSONArray)) {
            return preMatchData;
        }
        for (int i = 0; i < optJSONArray.length(); i++) {
            LinkedList linkedList = new LinkedList();
            try {
                JSONArray optJSONArray2 = optJSONArray.getJSONObject(i).optJSONArray(ITEMS);
                String optString = optJSONArray.getJSONObject(i).optString("packageName");
                if (!isJSONArrayEmpty(optJSONArray2)) {
                    for (int i2 = 0; i2 < optJSONArray2.length(); i2++) {
                        try {
                            linkedList.add((SearchRawData) gson.fromJson(optJSONArray2.getString(i2), SearchRawData.class));
                        } catch (JSONException unused) {
                        }
                    }
                    preMatchData.putPreMatchDataToMap(optString, linkedList);
                }
            } catch (JSONException unused2) {
            }
        }
        return preMatchData;
    }

    public static boolean isEmpty(PreMatchData preMatchData) {
        return preMatchData == null || preMatchData.mPreMatchDataMap.isEmpty();
    }

    private static boolean isJSONArrayEmpty(JSONArray jSONArray) {
        return jSONArray == null || jSONArray.length() <= 0;
    }

    public void addVersionCodeToMap(String str, Long l) {
        this.mVersionCodeMap.put(str, l);
    }

    public boolean getIsQueryProviderComplete() {
        return this.mIsQueryProviderComplete;
    }

    public Map<String, List<SearchRawData>> getPreMatchDataMap() {
        return this.mPreMatchDataMap;
    }

    public void putPreMatchDataToMap(String str, List<SearchRawData> list) {
        this.mPreMatchDataMap.put(str, list);
    }

    public void setIsQueryProviderComplete(boolean z) {
        this.mIsQueryProviderComplete = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        Gson gson = new Gson();
        try {
            for (Map.Entry<String, List<SearchRawData>> entry : this.mPreMatchDataMap.entrySet()) {
                String key = entry.getKey();
                JSONObject jSONObject2 = new JSONObject();
                if (!TextUtils.isEmpty(key)) {
                    jSONObject2.put("packageName", key);
                }
                jSONObject2.put(VERSION_CODE, this.mVersionCodeMap.get(key));
                JSONArray jSONArray2 = new JSONArray();
                Iterator<SearchRawData> it = entry.getValue().iterator();
                while (it.hasNext()) {
                    jSONArray2.put(gson.toJson(it.next()));
                }
                jSONObject2.put(ITEMS, jSONArray2);
                jSONArray.put(jSONObject2);
            }
            if (!isJSONArrayEmpty(jSONArray)) {
                jSONObject.put(PACKAGE_LIST, jSONArray);
            }
        } catch (JSONException e) {
            Log.e(TAG, "write json error!" + e);
        }
        return jSONObject;
    }
}
