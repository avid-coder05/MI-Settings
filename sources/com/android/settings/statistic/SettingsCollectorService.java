package com.android.settings.statistic;

import android.app.ExtraNotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.ContactsContract;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.applications.DefaultAppsHelper;
import com.android.settings.display.FontFragment;
import com.android.settings.search.provider.SettingsProvider;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import miui.app.constants.ThemeManagerConstants;
import miui.provider.ExtraTelephony;
import miui.provider.Notes;
import miui.util.MiuiFeatureUtils;
import miui.util.NotificationFilterHelper;
import miui.yellowpage.YellowPageStatistic;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class SettingsCollectorService extends JobService {
    private List<SettingsProviderData> mData;
    private JobParameters mJobParameters;
    private HashMap<String, Callable<String>> mSpecialSettingsHashMap = new HashMap<>();

    /* loaded from: classes2.dex */
    public class SettingsProviderData {
        private final String mCategory;
        private final String mKey;
        private final String mType;
        private final String mValue;

        public SettingsProviderData(String str, String str2, String str3, String str4) {
            this.mType = str;
            this.mCategory = str2;
            this.mKey = str3;
            this.mValue = str4;
        }

        public void upload() {
            HashMap hashMap = new HashMap();
            hashMap.put(this.mKey, this.mValue);
            OneTrackInterfaceUtils.track(this.mCategory, hashMap);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void collectData() {
        downloadSettingsKey();
        uploadSettingsValue();
    }

    private void downloadSettingsKey() {
        ArrayList arrayList = new ArrayList();
        this.mData = arrayList;
        arrayList.addAll(getSettings("Settings.System"));
        this.mData.addAll(getSettings("Settings.Secure"));
        this.mData.addAll(getSettings("Settings.Global"));
        this.mData.addAll(getSettings("MiuiSettings.System"));
        this.mData.addAll(getSettings("SystemProperties"));
        this.mData.addAll(getSettings("PreferredSettings"));
        this.mData.addAll(getSettings("Special.Keys"));
    }

    private String getCloudDataString(String str, String str2) {
        return MiuiSettings.SettingsCloudData.getCloudDataString(getContentResolver(), "SettingsCollector2", str, str2);
    }

    private SettingsProviderData getPreferredSettings(String str, String str2) {
        int i;
        String lowerCase = str2.toLowerCase();
        if (ThemeManagerConstants.COMPONENT_CODE_LAUNCHER.equals(lowerCase)) {
            i = 0;
        } else if ("dialer".equals(lowerCase)) {
            i = 1;
        } else if ("message".equals(lowerCase)) {
            i = 2;
        } else if ("browser".equals(lowerCase)) {
            i = 3;
        } else if ("camera".equals(lowerCase)) {
            i = 4;
        } else if ("gallery".equals(lowerCase)) {
            i = 5;
        } else if ("music".equals(lowerCase)) {
            i = 6;
        } else if ("email".equals(lowerCase)) {
            i = 7;
        } else if (!"video".equals(lowerCase)) {
            return null;
        } else {
            i = 8;
        }
        ActivityInfo activityInfo = getPackageManager().resolveActivity(DefaultAppsHelper.getIntent(DefaultAppsHelper.getIntentFilter(i)), 0).activityInfo;
        if (activityInfo == null) {
            return null;
        }
        return new SettingsProviderData("PreferredSettings", str, lowerCase, new ComponentName(activityInfo.packageName, activityInfo.name).flattenToShortString());
    }

    private SettingsProviderData getSetting(String str, String str2, String str3, String str4) {
        if (str.equals("Settings.System")) {
            String string = Settings.System.getString(getContentResolver(), str3);
            if (string != null && !TextUtils.isEmpty(string)) {
                return new SettingsProviderData("Settings.System", str2, str3, string);
            }
        } else if (str.equals("Settings.Secure")) {
            String string2 = Settings.Secure.getString(getContentResolver(), str3);
            if (string2 != null && !TextUtils.isEmpty(string2)) {
                return new SettingsProviderData("Settings.Secure", str2, str3, string2);
            }
        } else if (str.equals("Settings.Global")) {
            String string3 = Settings.Global.getString(getContentResolver(), str3);
            if (string3 != null && !TextUtils.isEmpty(string3)) {
                return new SettingsProviderData("Settings.Global", str2, str3, string3);
            }
        } else if (str.equals("MiuiSettings.System")) {
            String string4 = MiuiSettings.System.getString(getContentResolver(), str3);
            if (string4 != null && !TextUtils.isEmpty(string4)) {
                return new SettingsProviderData("MiuiSettings.System", str2, str3, string4);
            }
        } else if (str.equals("SystemProperties")) {
            String str5 = SystemProperties.get(str3);
            if (str5 != null && !TextUtils.isEmpty(str5)) {
                return new SettingsProviderData("SystemProperties", str2, str3, str5);
            }
        } else if (str.equals("PreferredSettings")) {
            return getPreferredSettings(str2, str3);
        } else {
            if (str.equals("Special.Keys")) {
                return getSpecialSetting(str2, str3);
            }
        }
        Log.w("SettingsCollector", "get null for module:\"" + str + "\", key:\"" + str3 + "\"");
        return new SettingsProviderData(str, str2, str3, str4);
    }

    private List<SettingsProviderData> getSettings(String str) {
        ArrayList arrayList = new ArrayList();
        String cloudDataString = getCloudDataString(str, null);
        if (cloudDataString != null) {
            try {
                JSONArray jSONArray = new JSONArray(cloudDataString);
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONObject optJSONObject = jSONArray.optJSONObject(i);
                    String optString = optJSONObject.optString(SettingsProvider.ARGS_KEY);
                    arrayList.add(getSetting(str, optJSONObject.optString(YellowPageStatistic.Display.CATEGORY, optString), optString, optJSONObject.optString("def", "empty")));
                }
            } catch (JSONException unused) {
                Log.e("SettingsCollector", "analyze JSON failed.");
            }
        }
        return arrayList;
    }

    private SettingsProviderData getSpecialSetting(String str, String str2) {
        try {
            return new SettingsProviderData("Special.Keys", str, str2, this.mSpecialSettingsHashMap.get(str2).call());
        } catch (Exception e) {
            Log.e("SettingsCollector", "", e);
            return new SettingsProviderData("Special.Keys", str, str2, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initializeSpecialSettings() {
        this.mSpecialSettingsHashMap.put("font", new Callable<String>() { // from class: com.android.settings.statistic.SettingsCollectorService.2
            @Override // java.util.concurrent.Callable
            public String call() {
                return FontFragment.getCurrentUsingFontName(SettingsCollectorService.this);
            }
        });
        this.mSpecialSettingsHashMap.put("font_size", new Callable<String>() { // from class: com.android.settings.statistic.SettingsCollectorService.3
            @Override // java.util.concurrent.Callable
            public String call() {
                return String.valueOf(Resources.getSystem().getConfiguration().uiMode & 15);
            }
        });
        this.mSpecialSettingsHashMap.put("app_notifications", new Callable<String>() { // from class: com.android.settings.statistic.SettingsCollectorService.4
            @Override // java.util.concurrent.Callable
            public String call() {
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.LAUNCHER");
                List<ResolveInfo> queryIntentActivities = SettingsCollectorService.this.getPackageManager().queryIntentActivities(intent, 0);
                HashMap hashMap = new HashMap();
                Iterator<ResolveInfo> it = queryIntentActivities.iterator();
                while (it.hasNext()) {
                    ActivityInfo activityInfo = it.next().activityInfo;
                    String str = activityInfo.packageName;
                    String str2 = activityInfo.name;
                    if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2) && !hashMap.containsKey(str)) {
                        hashMap.put(str, Integer.valueOf(NotificationFilterHelper.getAppFlag(SettingsCollectorService.this, str, true)));
                    }
                }
                return new JSONObject(hashMap).toString();
            }
        });
        this.mSpecialSettingsHashMap.put("vip_call_settings", new Callable<String>() { // from class: com.android.settings.statistic.SettingsCollectorService.5
            @Override // java.util.concurrent.Callable
            public String call() {
                return String.valueOf(ExtraNotificationManager.getZenModeConfig(SettingsCollectorService.this).allowCallsFrom);
            }
        });
        this.mSpecialSettingsHashMap.put("vip_list", new Callable<String>() { // from class: com.android.settings.statistic.SettingsCollectorService.6
            private HashMap<String, String> readContacts() {
                HashMap<String, String> hashMap = new HashMap<>();
                Cursor query = SettingsCollectorService.this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{Notes.Data.DATA4, "display_name"}, null, null, null);
                if (query != null) {
                    while (query.moveToNext()) {
                        try {
                            try {
                                String string = query.getString(0);
                                String string2 = query.getString(1);
                                if (!TextUtils.isEmpty(string)) {
                                    hashMap.put(string, string2);
                                }
                            } catch (Exception e) {
                                Log.e("SettingsCollector", "Err in queryContacts: " + e);
                            }
                        } finally {
                            query.close();
                        }
                    }
                }
                return hashMap;
            }

            private HashMap<String, String> readVIPs(HashMap<String, String> hashMap) {
                HashMap<String, String> hashMap2 = new HashMap<>();
                Cursor query = SettingsCollectorService.this.getContentResolver().query(ExtraTelephony.Phonelist.CONTENT_URI, new String[]{"_id", "number"}, "type = ? and sync_dirty <> ?", new String[]{ExtraTelephony.Phonelist.TYPE_VIP, String.valueOf(1)}, null);
                if (query != null) {
                    while (query.moveToNext()) {
                        try {
                            try {
                                query.getLong(0);
                                String string = query.getString(1);
                                String str = null;
                                if (hashMap.containsKey(string)) {
                                    str = hashMap.get(string);
                                }
                                hashMap2.put(string, str);
                            } catch (Exception e) {
                                Log.e("SettingsCollector", "Err in queryVIPs: " + e);
                            }
                        } finally {
                            query.close();
                        }
                    }
                }
                return hashMap2;
            }

            @Override // java.util.concurrent.Callable
            public String call() {
                return new JSONObject(readVIPs(readContacts())).toString();
            }
        });
        this.mSpecialSettingsHashMap.put("ring_volume", new Callable<String>() { // from class: com.android.settings.statistic.SettingsCollectorService.7
            @Override // java.util.concurrent.Callable
            public String call() {
                return String.valueOf(((AudioManager) SettingsCollectorService.this.getSystemService("audio")).getStreamVolume(2));
            }
        });
        this.mSpecialSettingsHashMap.put("alarm_volume", new Callable<String>() { // from class: com.android.settings.statistic.SettingsCollectorService.8
            @Override // java.util.concurrent.Callable
            public String call() {
                return String.valueOf(((AudioManager) SettingsCollectorService.this.getSystemService("audio")).getStreamVolume(4));
            }
        });
        this.mSpecialSettingsHashMap.put("music_volume", new Callable<String>() { // from class: com.android.settings.statistic.SettingsCollectorService.9
            @Override // java.util.concurrent.Callable
            public String call() {
                return String.valueOf(((AudioManager) SettingsCollectorService.this.getSystemService("audio")).getStreamVolume(3));
            }
        });
        this.mSpecialSettingsHashMap.put("owner_info", new Callable<String>() { // from class: com.android.settings.statistic.SettingsCollectorService.10
            @Override // java.util.concurrent.Callable
            public String call() {
                return new LockPatternUtils(SettingsCollectorService.this).getOwnerInfo(UserHandle.myUserId());
            }
        });
        this.mSpecialSettingsHashMap.put("lite_mode", new Callable<String>() { // from class: com.android.settings.statistic.SettingsCollectorService.11
            @Override // java.util.concurrent.Callable
            public String call() {
                return String.valueOf(MiuiFeatureUtils.isLiteMode());
            }
        });
    }

    private void uploadSettingsValue() {
        List<SettingsProviderData> list = this.mData;
        if (list == null) {
            return;
        }
        Iterator<SettingsProviderData> it = list.iterator();
        while (it.hasNext()) {
            it.next().upload();
        }
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        if (jobParameters.getJobId() != 44005) {
            return false;
        }
        Log.v("SettingsCollector", "start service, version: 2");
        this.mJobParameters = jobParameters;
        new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.statistic.SettingsCollectorService.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... voidArr) {
                SettingsCollectorService.this.collectData();
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Void r2) {
                SettingsCollectorService settingsCollectorService = SettingsCollectorService.this;
                settingsCollectorService.jobFinished(settingsCollectorService.mJobParameters, false);
            }

            @Override // android.os.AsyncTask
            protected void onPreExecute() {
                SettingsCollectorService.this.initializeSpecialSettings();
            }
        }.execute(new Void[0]);
        return true;
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        Log.w("SettingsCollector", "force stopped");
        return false;
    }
}
