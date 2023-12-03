package com.android.settings.search.cloud;

import android.content.Context;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import org.json.JSONException;

/* loaded from: classes2.dex */
public class SearchCloudSortUtils {
    private static String SEARCH_CLOUD_SORT_FILE = "search_cloud_sort_data.json";
    private static String TAG = "SearchCloudSortUtils";
    private static volatile SearchCloudSortUtils mInstance;
    private Double cloudWeight;
    private WeakReference<Context> contextWeakReference;
    private String extra;
    private HashMap<String, Double> mSeachCouldCache;
    private String version;

    private SearchCloudSortUtils(Context context) {
        if (this.mSeachCouldCache == null) {
            this.mSeachCouldCache = new HashMap<>();
        }
        long currentTimeMillis = System.currentTimeMillis();
        this.mSeachCouldCache.clear();
        if (context == null) {
            return;
        }
        try {
            WeakReference<Context> weakReference = new WeakReference<>(context);
            this.contextWeakReference = weakReference;
            CloudSortData cloudSortData = (CloudSortData) new Gson().fromJson(readJSONObject(weakReference.get().getAssets().open(SEARCH_CLOUD_SORT_FILE)), CloudSortData.class);
            this.mSeachCouldCache = cloudSortData.sortResources;
            this.version = cloudSortData.version;
            this.extra = cloudSortData.extra;
            this.cloudWeight = cloudSortData.cloudWeight;
            long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
            Log.i(TAG, "read file named search_cloud_sort, init search Cloud data, " + currentTimeMillis2 + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SearchCloudSortUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SearchCloudSortUtils.class) {
                if (mInstance == null) {
                    mInstance = new SearchCloudSortUtils(context);
                }
            }
        }
        return mInstance;
    }

    private static String readJSONObject(InputStream inputStream) throws IOException, JSONException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        StringBuilder sb = new StringBuilder();
        char[] cArr = new char[MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE];
        while (true) {
            try {
                int read = inputStreamReader.read(cArr, 0, MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE);
                if (read <= 0) {
                    break;
                }
                sb.append(cArr, 0, read);
            } finally {
                try {
                    inputStreamReader.close();
                } catch (IOException unused) {
                    Log.e(TAG, "close InputStream failed");
                }
            }
        }
        return sb.toString();
    }

    public boolean contain(String str) {
        return get(str) != null;
    }

    public Double get(String str) {
        HashMap<String, Double> hashMap = this.mSeachCouldCache;
        if (hashMap != null) {
            return hashMap.get(str);
        }
        return null;
    }

    public HashMap<String, Double> getCacheMap() {
        return this.mSeachCouldCache;
    }

    public Double getCloudWeight() {
        return this.cloudWeight;
    }

    public void set(String str, Double d) {
        this.mSeachCouldCache.put(str, d);
    }
}
