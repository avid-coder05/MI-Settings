package com.android.settings.search.appseparate;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.MiuiUtils;
import com.android.settings.search.appseparate.SearchRawData;
import com.android.settings.utils.LogUtil;
import com.android.settings.utils.MiuiSharedPreferencesUtils;
import com.android.settingslib.search.SearchContract;
import com.android.settingslib.search.SearchUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* loaded from: classes2.dex */
public class DataCollector {
    private static final int CORE_POOL_SIZE;
    private static final int CPU_COUNT;
    private static final int MAXIMUM_POOL_SIZE;
    public static final String PREFERENCE_QUERY_PROVIDER_TIMEOUT_TIME = "query_provider_timeout_time";
    private static final String TAG = "DataCollector";
    private static final long TIMEOUT = 2;
    private ThreadPoolExecutor mCollectDataExecutor;
    private Context mContext;
    private PreMatchData mPreMatchData;

    /* loaded from: classes2.dex */
    private class QueryProviderTask implements Callable<String> {
        private String mAuthority;
        private String mPackageName;

        private QueryProviderTask(String str, String str2) {
            this.mPackageName = str;
            this.mAuthority = str2;
        }

        @Override // java.util.concurrent.Callable
        public String call() throws Exception {
            DataCollector.this.addDataFromRemoteProvider(this.mPackageName, this.mAuthority);
            return this.mPackageName;
        }
    }

    static {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        CPU_COUNT = availableProcessors;
        CORE_POOL_SIZE = availableProcessors + 1;
        MAXIMUM_POOL_SIZE = (availableProcessors * 2) + 1;
    }

    public DataCollector(Context context) {
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addDataFromRemoteProvider(String str, String str2) {
        try {
            List<SearchRawData> rawDataFromUri = getRawDataFromUri(this.mContext.createPackageContext(str, 0), str, buildUriForRawData(str2), SearchContract.SEARCH_RESULT_COLUMNS);
            if (rawDataFromUri != null && !rawDataFromUri.isEmpty()) {
                if (!Thread.currentThread().isInterrupted()) {
                    this.mPreMatchData.putPreMatchDataToMap(str, rawDataFromUri);
                    this.mPreMatchData.addVersionCodeToMap(str, Long.valueOf(MiuiUtils.getAppLongVersionCode(this.mContext, str)));
                    return;
                }
                Log.e(TAG, "Task was timeout! abandoned result packageName:" + str);
                return;
            }
            Log.e(TAG, "No raw data found for authorities: " + str2);
        } catch (Exception e) {
            Log.w(TAG, "Could add data from remote provider " + str + ": " + Log.getStackTraceString(e));
        }
    }

    private Uri buildUriForRawData(String str) {
        return Uri.parse("content://" + str);
    }

    private List<SearchRawData> getRawDataFromUri(Context context, String str, Uri uri, String[] strArr) {
        String str2;
        String str3 = TAG;
        long currentTimeMillis = System.currentTimeMillis();
        ContentResolver contentResolver = context.getContentResolver();
        LinkedList linkedList = new LinkedList();
        Cursor cursor = null;
        try {
            try {
                cursor = contentResolver.query(uri, strArr, null, null, null);
                if (cursor == null) {
                    Log.w(TAG, "Cannot add data for Uri: " + uri.toString());
                    List<SearchRawData> emptyList = Collections.emptyList();
                    if (cursor != null) {
                        cursor.close();
                    }
                    return emptyList;
                }
                try {
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            String string = cursor.getString(0);
                            String string2 = cursor.getString(1);
                            String string3 = cursor.getString(2);
                            String string4 = cursor.getString(3);
                            int i = cursor.getInt(4);
                            String string5 = cursor.getString(5);
                            String string6 = cursor.getString(6);
                            String string7 = cursor.getString(7);
                            String string8 = cursor.getString(8);
                            String string9 = cursor.getString(9);
                            String string10 = cursor.getString(10);
                            str2 = str3;
                            String string11 = cursor.isNull(11) ? "" : cursor.getString(11);
                            long j = currentTimeMillis;
                            Cursor cursor2 = cursor;
                            try {
                                if (isValidItems(string, string5, string6, string7, string11)) {
                                    SearchRawData.Builder builder = new SearchRawData.Builder();
                                    builder.setPackageName(str).setExtras(string9).setTitle(string).setSummaryOff(string3).setSummaryOn(string2).setOther(string10).setIntentAction(string5).setIntentTargetClass(string7).setIntentTargetPackage(string6).setIconResId(i).setKeywords(string4).setUriString(string8).setIntentUri(string11);
                                    linkedList.add(builder.build());
                                }
                                str3 = str2;
                                currentTimeMillis = j;
                                cursor = cursor2;
                            } catch (Exception e) {
                                e = e;
                                cursor = cursor2;
                                Log.e(str2, "Fail to query raw data from Uri:" + uri.toString(), e);
                                List<SearchRawData> emptyList2 = Collections.emptyList();
                                if (cursor != null) {
                                    cursor.close();
                                }
                                return emptyList2;
                            } catch (Throwable th) {
                                th = th;
                                cursor = cursor2;
                                if (cursor != null) {
                                    cursor.close();
                                }
                                throw th;
                            }
                        }
                    }
                    cursor.close();
                    LogUtil.logCost(TAG, "getRawDataFromUri", currentTimeMillis, System.currentTimeMillis(), str);
                    return linkedList;
                } catch (Exception e2) {
                    e = e2;
                    str2 = str3;
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (Exception e3) {
            e = e3;
            str2 = TAG;
        }
    }

    private boolean isValidItems(String str, String str2, String str3, String str4, String str5) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return (TextUtils.isEmpty(str5) && TextUtils.isEmpty(str2) && (TextUtils.isEmpty(str3) || TextUtils.isEmpty(str4))) ? false : true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v1, types: [com.android.settings.search.appseparate.DataCollector$1] */
    /* JADX WARN: Type inference failed for: r4v11 */
    /* JADX WARN: Type inference failed for: r4v3, types: [java.util.List] */
    public PreMatchData collectData(List<ResolveInfo> list) {
        ?? r4;
        this.mPreMatchData = new PreMatchData();
        this.mCollectDataExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        ArrayList arrayList = new ArrayList();
        long currentTimeMillis = System.currentTimeMillis();
        Iterator<ResolveInfo> it = list.iterator();
        while (true) {
            r4 = 0;
            if (it.hasNext()) {
                ProviderInfo providerInfo = it.next().providerInfo;
                arrayList.add(new QueryProviderTask(providerInfo.packageName, providerInfo.authority));
            } else {
                try {
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        r4 = this.mCollectDataExecutor.invokeAll(arrayList, 2L, TimeUnit.SECONDS);
        Iterator it2 = arrayList.iterator();
        for (Future future : r4) {
            QueryProviderTask queryProviderTask = (QueryProviderTask) it2.next();
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e2) {
                e2.printStackTrace();
            } catch (CancellationException e3) {
                Log.e(TAG, "CancellationException： :" + queryProviderTask.mPackageName + e3);
                MiuiSharedPreferencesUtils.setIntPreference(this.mContext, PREFERENCE_QUERY_PROVIDER_TIMEOUT_TIME, MiuiSharedPreferencesUtils.getIntPreference(this.mContext, PREFERENCE_QUERY_PROVIDER_TIMEOUT_TIME, 0) + 1);
                this.mPreMatchData.setIsQueryProviderComplete(false);
            }
        }
        this.mCollectDataExecutor.shutdown();
        double d = currentTimeMillis;
        LogUtil.logCost(TAG, "collectData", d, System.currentTimeMillis(), "-");
        SearchUtils.logCost(d, System.currentTimeMillis(), "-");
        return this.mPreMatchData;
    }
}
