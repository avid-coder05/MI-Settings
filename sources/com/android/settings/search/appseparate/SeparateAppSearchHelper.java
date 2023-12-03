package com.android.settings.search.appseparate;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.os.SystemProperties;
import android.util.Log;
import com.android.settings.MiuiUtils;
import com.android.settings.search.SearchResultItem;
import com.android.settings.utils.MiuiSharedPreferencesUtils;
import com.android.settingslib.search.SearchUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class SeparateAppSearchHelper {
    private static List<String> APP_WHITE_LIST = new ArrayList();
    private static final String BUILD_SEPARATE_APP_INDEX_LIST = "separate.app.index.list";
    private static final String BUILD_UTC = "ro.build.date.utc";
    private static final String FILENAME = "separate_app_index.json";
    public static final String FORCE_UPDATE = "force_update";
    private static final String LOCALE = "locale";
    private static final String TAG = "SeparateAppSearchHelper";
    private static final long TIME_INTERVAL = 604800000;
    private static final String UPDATE_TIME = "update.time";
    private static volatile SeparateAppSearchHelper mInstance;
    private CollectResultProcessor mCollectResultProcessor;
    private DataCollector mCollector;
    private Context mContext;
    private File mFile;
    private String mLocale;
    private PreMatchData mPreMatchData;

    private SeparateAppSearchHelper(Context context) {
        this.mContext = context;
        performCollecting();
        this.mLocale = Locale.getDefault().toString();
    }

    private void authProviders(List<ResolveInfo> list) {
        Iterator<ResolveInfo> it = list.iterator();
        Context context = this.mContext;
        Signature[] signatures = getSignatures(context, context.getPackageName());
        while (it.hasNext()) {
            String str = it.next().getComponentInfo().packageName;
            if (!MiuiUtils.isSystemApp(this.mContext.getApplicationContext(), str) && !isSignaturesSame(signatures, getSignatures(this.mContext, str))) {
                Log.w(TAG, "app filtered out:" + str);
                it.remove();
            }
        }
    }

    public static void forceUpdate(Context context, boolean z) {
        MiuiSharedPreferencesUtils.setBooleanPreference(context, FORCE_UPDATE, z);
        releaseInstance();
    }

    private PreMatchData getDataFromProviders(List<ResolveInfo> list) {
        if (this.mCollector == null) {
            this.mCollector = new DataCollector(this.mContext);
        }
        return this.mCollector.collectData(list);
    }

    public static SeparateAppSearchHelper getInstance(Context context) {
        if (mInstance == null || localeHasChange(mInstance.mLocale)) {
            synchronized (SeparateAppSearchHelper.class) {
                if (mInstance == null || localeHasChange(mInstance.mLocale)) {
                    mInstance = new SeparateAppSearchHelper(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    private static Signature[] getSignatures(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 64).signatures;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    private boolean isSignaturesSame(Signature[] signatureArr, Signature[] signatureArr2) {
        if (signatureArr == null || signatureArr2 == null) {
            return false;
        }
        HashSet hashSet = new HashSet();
        for (Signature signature : signatureArr) {
            hashSet.add(signature);
        }
        HashSet hashSet2 = new HashSet();
        for (Signature signature2 : signatureArr2) {
            hashSet2.add(signature2);
        }
        return hashSet.equals(hashSet2);
    }

    private static boolean localeHasChange(String str) {
        return !Locale.getDefault().toString().equals(str);
    }

    public static boolean needForceUpdate(Context context) {
        boolean booleanPreference = MiuiSharedPreferencesUtils.getBooleanPreference(context, FORCE_UPDATE, false);
        if (booleanPreference) {
            MiuiSharedPreferencesUtils.setBooleanPreference(context, FORCE_UPDATE, false);
        }
        return booleanPreference;
    }

    private boolean needToUpdate(JSONObject jSONObject) {
        return ((((jSONObject.optInt(BUILD_UTC, 0) != Integer.parseInt(SystemProperties.get(BUILD_UTC))) || ((System.currentTimeMillis() - jSONObject.optLong(UPDATE_TIME)) > 604800000L ? 1 : ((System.currentTimeMillis() - jSONObject.optLong(UPDATE_TIME)) == 604800000L ? 0 : -1)) > 0) || localeHasChange(jSONObject.optString("locale"))) || versionCodeHasChanged(jSONObject)) || needForceUpdate(this.mContext);
    }

    private synchronized void performCollecting() {
        long currentTimeMillis = System.currentTimeMillis();
        File filesDir = this.mContext.getFilesDir();
        if (!filesDir.exists()) {
            filesDir.mkdirs();
        }
        File file = new File(filesDir, FILENAME);
        this.mFile = file;
        if (file.exists()) {
            try {
                JSONObject readJSONObject = SearchUtils.readJSONObject(new FileInputStream(this.mFile));
                if (needToUpdate(readJSONObject)) {
                    this.mPreMatchData = null;
                } else {
                    this.mPreMatchData = PreMatchData.fromJson(readJSONObject.optJSONObject(BUILD_SEPARATE_APP_INDEX_LIST));
                }
            } catch (Exception unused) {
                this.mPreMatchData = null;
            }
        }
        if (this.mPreMatchData == null) {
            List<ResolveInfo> queryIntentContentProviders = this.mContext.getPackageManager().queryIntentContentProviders(new Intent("miui.intent.action.SETTINGS_SEARCH_PROVIDER"), 0);
            if (queryIntentContentProviders != null && !queryIntentContentProviders.isEmpty()) {
                authProviders(queryIntentContentProviders);
                this.mPreMatchData = getDataFromProviders(queryIntentContentProviders);
                int intPreference = MiuiSharedPreferencesUtils.getIntPreference(this.mContext, DataCollector.PREFERENCE_QUERY_PROVIDER_TIMEOUT_TIME, 0);
                if (!PreMatchData.isEmpty(this.mPreMatchData) && (this.mPreMatchData.getIsQueryProviderComplete() || intPreference > 3)) {
                    if (intPreference != 0) {
                        MiuiSharedPreferencesUtils.setIntPreference(this.mContext, DataCollector.PREFERENCE_QUERY_PROVIDER_TIMEOUT_TIME, 0);
                    }
                    writeJSONFile();
                }
            }
            Log.d(TAG, "No providers found for action: miui.intent.action.SETTINGS_SEARCH_PROVIDER");
            return;
        }
        SearchUtils.logCost(currentTimeMillis, System.currentTimeMillis(), "-");
    }

    public static void releaseInstance() {
        mInstance = null;
    }

    private boolean versionCodeHasChanged(JSONObject jSONObject) {
        long currentTimeMillis = System.currentTimeMillis();
        JSONArray optJSONArray = jSONObject.optJSONObject(BUILD_SEPARATE_APP_INDEX_LIST).optJSONArray("packageList");
        for (int i = 0; i < optJSONArray.length(); i++) {
            try {
                if (optJSONArray.getJSONObject(i).optLong("versionCode") != MiuiUtils.getAppLongVersionCode(this.mContext, optJSONArray.getJSONObject(i).optString("packageName"))) {
                    return true;
                }
            } catch (JSONException unused) {
                return true;
            }
        }
        SearchUtils.logCost(currentTimeMillis, System.currentTimeMillis(), "-");
        return false;
    }

    private void writeJSONFile() {
        FileOutputStream fileOutputStream;
        Object e;
        String str;
        String str2;
        synchronized (this) {
            try {
            } catch (Throwable th) {
                th = th;
            }
            try {
                fileOutputStream = new FileOutputStream(this.mFile);
                try {
                    fileOutputStream.write(new JSONObject().put(BUILD_SEPARATE_APP_INDEX_LIST, this.mPreMatchData.toJson()).put(BUILD_UTC, SystemProperties.get(BUILD_UTC)).put("locale", Locale.getDefault().toString()).put(UPDATE_TIME, System.currentTimeMillis()).toString().getBytes());
                    fileOutputStream.flush();
                    try {
                        fileOutputStream.close();
                    } catch (IOException e2) {
                        str = TAG;
                        str2 = "close file error!" + e2;
                        Log.e(str, str2);
                    }
                } catch (IOException | OutOfMemoryError | JSONException e3) {
                    e = e3;
                    Log.e(TAG, "write error!" + e);
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e4) {
                            str = TAG;
                            str2 = "close file error!" + e4;
                            Log.e(str, str2);
                        }
                    }
                }
            } catch (IOException | OutOfMemoryError | JSONException e5) {
                fileOutputStream = null;
                e = e5;
            } catch (Throwable th2) {
                fileOutputStream = null;
                th = th2;
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e6) {
                        Log.e(TAG, "close file error!" + e6);
                    }
                }
                throw th;
            }
        }
    }

    public synchronized List<SearchResultItem> getSearchResult(String str) {
        if (this.mCollectResultProcessor == null) {
            this.mCollectResultProcessor = new CollectResultProcessor(this.mContext);
        }
        if (PreMatchData.isEmpty(this.mPreMatchData)) {
            return Collections.emptyList();
        }
        return this.mCollectResultProcessor.getMatchData(this.mPreMatchData, str);
    }
}
