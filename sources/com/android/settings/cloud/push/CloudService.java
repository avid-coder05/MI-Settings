package com.android.settings.cloud.push;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class CloudService extends IntentService {
    static final String TAG = CloudService.class.getSimpleName();

    public CloudService() {
        super(TAG);
    }

    private void checkExistCompats(Context context, List<ExistCompatibility> list) {
        CompatChecker.getInstance(context).checkExistCompats(list);
    }

    private void checkRunningCompats(Context context, List<RunningCompatibility> list) {
        CompatChecker.getInstance(context).checkRunningCompats(list);
    }

    private void insertExistCompats(Context context, JSONArray jSONArray) {
        ArrayList arrayList = new ArrayList();
        int length = jSONArray.length();
        for (int i = 0; i < length; i++) {
            JSONObject optJSONObject = jSONArray.optJSONObject(i);
            ExistCompatibility existCompatibility = new ExistCompatibility();
            String optString = optJSONObject.optString("pkg_name");
            String optString2 = optJSONObject.optString("message");
            String optString3 = optJSONObject.optString("title");
            String optString4 = optJSONObject.optString("ticker");
            boolean optBoolean = optJSONObject.optBoolean("flag_precise", false);
            existCompatibility.setPackageName(optString);
            existCompatibility.setMessage(optString2);
            existCompatibility.setTitle(optString3);
            existCompatibility.setTicker(optString4);
            existCompatibility.setPrecise(optBoolean);
            JSONArray optJSONArray = optJSONObject.optJSONArray("versions");
            if (optJSONArray != null && optJSONArray.length() > 0) {
                HashSet hashSet = new HashSet();
                for (int i2 = 0; i2 < optJSONArray.length(); i2++) {
                    hashSet.add(Integer.valueOf(optJSONArray.optInt(i2)));
                }
                existCompatibility.setVersions(hashSet);
            }
            arrayList.add(existCompatibility);
        }
        checkExistCompats(context, arrayList);
    }

    private void insertInstallCompats(Context context, JSONArray jSONArray) {
        ArrayList arrayList = new ArrayList();
        int length = jSONArray.length();
        for (int i = 0; i < length; i++) {
            JSONObject optJSONObject = jSONArray.optJSONObject(i);
            InstallCompatibility installCompatibility = new InstallCompatibility();
            String optString = optJSONObject.optString("pkg_name");
            String optString2 = optJSONObject.optString("message");
            boolean optBoolean = optJSONObject.optBoolean("flag_precise", false);
            installCompatibility.setPackageName(optString);
            installCompatibility.setMessage(optString2);
            installCompatibility.setPrecise(optBoolean);
            JSONArray optJSONArray = optJSONObject.optJSONArray("versions");
            if (optJSONArray != null && optJSONArray.length() > 0) {
                HashSet hashSet = new HashSet();
                for (int i2 = 0; i2 < optJSONArray.length(); i2++) {
                    hashSet.add(Integer.valueOf(optJSONArray.optInt(i2)));
                }
                installCompatibility.setVersions(hashSet);
            }
            arrayList.add(installCompatibility);
        }
        CloudManager.clearInstallCompatData(context);
        CloudManager.insertInstallCompatData(context, arrayList);
    }

    private void insertRunningCompats(Context context, JSONArray jSONArray) {
        ArrayList arrayList = new ArrayList();
        int length = jSONArray.length();
        for (int i = 0; i < length; i++) {
            JSONObject optJSONObject = jSONArray.optJSONObject(i);
            RunningCompatibility runningCompatibility = new RunningCompatibility();
            String optString = optJSONObject.optString("pkg_name");
            String optString2 = optJSONObject.optString("message");
            boolean optBoolean = optJSONObject.optBoolean("flag_precise", false);
            runningCompatibility.setPackageName(optString);
            runningCompatibility.setMessage(optString2);
            runningCompatibility.setPrecise(optBoolean);
            JSONArray optJSONArray = optJSONObject.optJSONArray("versions");
            if (optJSONArray != null && optJSONArray.length() > 0) {
                HashSet hashSet = new HashSet();
                for (int i2 = 0; i2 < optJSONArray.length(); i2++) {
                    hashSet.add(Integer.valueOf(optJSONArray.optInt(i2)));
                }
                runningCompatibility.setVersions(hashSet);
            }
            arrayList.add(runningCompatibility);
        }
        CloudManager.clearRunningCompatData(context);
        CloudManager.insertRunningCompatData(context, arrayList);
        checkRunningCompats(context, arrayList);
    }

    private void parseContent(Context context, String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            JSONArray optJSONArray = jSONObject.optJSONArray("exist_compatibility");
            if (optJSONArray != null) {
                insertExistCompats(context, optJSONArray);
            }
            JSONArray optJSONArray2 = jSONObject.optJSONArray("install_compatibility");
            if (optJSONArray2 != null) {
                insertInstallCompats(context, optJSONArray2);
            }
            JSONArray optJSONArray3 = jSONObject.optJSONArray("running_compatibility");
            if (optJSONArray3 != null) {
                insertRunningCompats(context, optJSONArray3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // android.app.IntentService
    protected void onHandleIntent(Intent intent) {
        String stringExtra = intent.getStringExtra("push_content");
        if (TextUtils.isEmpty(stringExtra)) {
            return;
        }
        parseContent(this, stringExtra);
    }
}
