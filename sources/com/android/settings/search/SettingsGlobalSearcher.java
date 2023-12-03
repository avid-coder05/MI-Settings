package com.android.settings.search;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.R;
import com.android.settings.device.MiuiAboutPhoneUtils;
import com.android.settings.search.SearchResultItem;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.search.SearchUtils;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;
import miui.provider.ExtraContacts;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class SettingsGlobalSearcher {
    private static final String ACTION_SYNC_GLOBAL_SEARCH = "com.android.settings.action.SYNC_GLOBAL_SEARCH";
    private static final String AUTHORITY = "com.android.settings.globalsearch";
    private static final String KEYWORDS_CLOUD_CONFIG_MODULE_NAME = "GlobalSearch";
    private static final String KEY_CLOUD_DATA_DESTPACKAGE = "destPackage";
    private static final String KEY_CLOUD_DATA_INTERVAL = "interval";
    private static final String KEY_LAST_UPDATE_TIMESTAMP = "key_last_update_timestamp_" + UserHandle.myUserId();
    private static final String KEY_UPDATE_INTERVAL = "key_update_interval_" + UserHandle.myUserId();
    private static final String METHOD_UPDATE_GLOBAL_SEARCH = "updateGlobalSearch";
    private static final String SCHEME = "content";
    private static final String SEARCH_RESULT_ACTION = "intent_action";
    private static final String SEARCH_RESULT_DATA = "intent_data";
    private static final String SEARCH_RESULT_DESCRIPTION = "description";
    private static final String SEARCH_RESULT_DEST_CLASS = "dest_class";
    private static final String SEARCH_RESULT_DEST_FRAGMENT = "dest_fragment";
    private static final String SEARCH_RESULT_ICON = "icon";
    private static final String SEARCH_RESULT_KEYWORDS = "keywords";
    private static final String SEARCH_RESULT_MAX_VERSION = "max_version";
    private static final String SEARCH_RESULT_MIN_VERSION = "min_version";
    private static final String SEARCH_RESULT_PKG = "pkg";
    private static final String SEARCH_RESULT_TITLE = "title";
    private static final String TAG = "SettingsGlobalSearcher";
    private List<MiuiSettings.SettingsCloudData.CloudData> mCloudData;
    private Context mContext;
    private PackageManager mPm;
    private SyncDataCompletedReceiver mReceiver = new SyncDataCompletedReceiver();
    private List<GlobalSearchItem> mSearchResult;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public final class SyncDataCompletedReceiver extends BroadcastReceiver {
        private SyncDataCompletedReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), SettingsGlobalSearcher.ACTION_SYNC_GLOBAL_SEARCH)) {
                Log.d(SettingsGlobalSearcher.TAG, "onReceive: " + intent.getAction());
                SettingsGlobalSearcher.this.loadDataAsync();
            }
        }
    }

    public SettingsGlobalSearcher(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mPm = applicationContext.getPackageManager();
        registerSyncGlobalSearchCompleted();
    }

    private List<SearchResultItem> buildSearchResult(String str, List<GlobalSearchItem> list) {
        ArrayList arrayList = new ArrayList();
        if (list == null) {
            arrayList.add(SearchResultItem.EMPTY);
            return arrayList;
        }
        String string = this.mContext.getResources().getString(R.string.app_function);
        for (GlobalSearchItem globalSearchItem : list) {
            if (match(str, globalSearchItem)) {
                arrayList.add(new SearchResultItem.Builder(0).setGlobalSearch(true).setTitle(globalSearchItem.title).setIntent(getIntent(globalSearchItem)).setPath(TextUtils.isEmpty(globalSearchItem.appName) ? string : globalSearchItem.appName + "/" + string).setGlobalSearchIcon(globalSearchItem.appIcon).setSummary(globalSearchItem.description).build());
            }
        }
        if (arrayList.isEmpty()) {
            arrayList.add(SearchResultItem.EMPTY);
        }
        return arrayList;
    }

    private Drawable getAppIcon(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return this.mPm.getApplicationIcon(str);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getAppIcon: ", e);
            return null;
        }
    }

    private String getAppName(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        try {
            PackageManager packageManager = this.mPm;
            return packageManager.getApplicationLabel(packageManager.getApplicationInfo(str, 128)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getAppName: ", e);
            return "";
        }
    }

    private List<MiuiSettings.SettingsCloudData.CloudData> getCloudData() {
        return MiuiSettings.SettingsCloudData.getCloudDataList(this.mContext.getContentResolver(), KEYWORDS_CLOUD_CONFIG_MODULE_NAME);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getCloudUpdateInterval() {
        long j;
        List<MiuiSettings.SettingsCloudData.CloudData> cloudData = getCloudData();
        this.mCloudData = cloudData;
        if (cloudData != null) {
            Iterator<MiuiSettings.SettingsCloudData.CloudData> it = cloudData.iterator();
            while (it.hasNext()) {
                JSONObject json = it.next().json();
                if (json != null) {
                    try {
                        j = Long.parseLong(json.getString(KEY_CLOUD_DATA_INTERVAL));
                    } catch (Exception e) {
                        Log.e(TAG, "getUpdateInterval: ", e);
                        j = 0;
                    }
                    if (j > 0) {
                        return j;
                    }
                }
            }
        }
        return 0L;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getDestPackageCloudData() {
        if (this.mCloudData == null) {
            this.mCloudData = getCloudData();
        }
        List<MiuiSettings.SettingsCloudData.CloudData> list = this.mCloudData;
        if (list != null) {
            Iterator<MiuiSettings.SettingsCloudData.CloudData> it = list.iterator();
            while (it.hasNext()) {
                JSONObject json = it.next().json();
                if (json != null) {
                    try {
                        return json.get(KEY_CLOUD_DATA_DESTPACKAGE).toString();
                    } catch (JSONException e) {
                        Log.e(TAG, "getDestPackageCloudData: ", e);
                    }
                }
            }
            return "";
        }
        return "";
    }

    private Intent getIntent(GlobalSearchItem globalSearchItem) {
        Intent intent = new Intent();
        if (!TextUtils.isEmpty(globalSearchItem.pkg)) {
            intent.setPackage(globalSearchItem.pkg);
        }
        if (!TextUtils.isEmpty(globalSearchItem.intentAction)) {
            intent.setAction(globalSearchItem.intentAction);
        }
        if (!TextUtils.isEmpty(globalSearchItem.intentData)) {
            intent.setData(Uri.parse(globalSearchItem.intentData));
        }
        if (!TextUtils.isEmpty(globalSearchItem.destClass)) {
            intent.setClassName(globalSearchItem.pkg, globalSearchItem.destClass);
        }
        if (SettingsFeatures.isSplitTablet(this.mContext)) {
            intent.addMiuiFlags(16);
        }
        return intent;
    }

    private ResolveInfo getResolveInfo(Intent intent) {
        PackageManager packageManager = this.mPm;
        if (packageManager == null || intent == null) {
            return null;
        }
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 1);
        if (queryIntentActivities.isEmpty()) {
            return null;
        }
        return queryIntentActivities.get(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Uri getUri() {
        return new Uri.Builder().authority(AUTHORITY).scheme("content").build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadData() {
        this.mSearchResult = loadGlobalSearchResult();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadDataAsync() {
        new Thread(new Runnable() { // from class: com.android.settings.search.SettingsGlobalSearcher.2
            @Override // java.lang.Runnable
            public void run() {
                Log.i(SettingsGlobalSearcher.TAG, "loadDataAsync");
                SettingsGlobalSearcher.this.loadData();
            }
        }).start();
    }

    private List<GlobalSearchItem> loadGlobalSearchResult() {
        Cursor queryUri = queryUri(getUri());
        if (queryUri == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        while (queryUri.moveToNext()) {
            GlobalSearchItem globalSearchItem = new GlobalSearchItem();
            globalSearchItem.title = queryUri.getString(queryUri.getColumnIndex("title"));
            globalSearchItem.pkg = queryUri.getString(queryUri.getColumnIndex(SEARCH_RESULT_PKG));
            globalSearchItem.icon = queryUri.getString(queryUri.getColumnIndex("icon"));
            globalSearchItem.description = queryUri.getString(queryUri.getColumnIndex("description"));
            globalSearchItem.keywords = queryUri.getString(queryUri.getColumnIndex("keywords"));
            globalSearchItem.intentAction = queryUri.getString(queryUri.getColumnIndex("intent_action"));
            globalSearchItem.intentData = queryUri.getString(queryUri.getColumnIndex("intent_data"));
            globalSearchItem.destClass = queryUri.getString(queryUri.getColumnIndex("dest_class"));
            globalSearchItem.destFragment = queryUri.getString(queryUri.getColumnIndex(SEARCH_RESULT_DEST_FRAGMENT));
            globalSearchItem.maxVersion = queryUri.getString(queryUri.getColumnIndex(SEARCH_RESULT_MIN_VERSION));
            globalSearchItem.minVersion = queryUri.getString(queryUri.getColumnIndex("max_version"));
            String pinyin = SearchUtils.getPinyin(this.mContext, globalSearchItem.keywords);
            globalSearchItem.keywordsPinyin = pinyin;
            if (!TextUtils.isEmpty(pinyin)) {
                globalSearchItem.keywordsPinyinArray = globalSearchItem.keywordsPinyin.split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
            }
            globalSearchItem.titlePinyin = SearchUtils.getPinyin(this.mContext, globalSearchItem.title);
            globalSearchItem.resolveIntent = resolveIntent(globalSearchItem);
            globalSearchItem.appName = getAppName(globalSearchItem.pkg);
            globalSearchItem.appIcon = getAppIcon(globalSearchItem.pkg);
            arrayList.add(globalSearchItem);
        }
        queryUri.close();
        return arrayList;
    }

    private boolean match(String str, GlobalSearchItem globalSearchItem) {
        if (globalSearchItem.resolveIntent && !TextUtils.isEmpty(globalSearchItem.title)) {
            String str2 = globalSearchItem.keywords;
            if ((str2 == null || !str2.contains(str)) && !globalSearchItem.title.contains(str)) {
                String lowerCase = str.toLowerCase();
                if (pinyinContains(globalSearchItem.keywordsPinyinArray, str)) {
                    return true;
                }
                String str3 = globalSearchItem.titlePinyin;
                return str3 != null && str3.startsWith(lowerCase);
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean needUpdate() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        long j = Settings.Global.getLong(contentResolver, KEY_LAST_UPDATE_TIMESTAMP, 0L);
        long j2 = Settings.Global.getLong(contentResolver, KEY_UPDATE_INTERVAL, 0L);
        return j == 0 || j2 == 0 || System.currentTimeMillis() - j > j2;
    }

    private boolean pinyinContains(String[] strArr, String str) {
        if (strArr != null && strArr.length != 0 && !TextUtils.isEmpty(str)) {
            for (String str2 : strArr) {
                if (!TextUtils.isEmpty(str2) && str2.startsWith(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Cursor queryUri(Uri uri) {
        Cursor query = this.mContext.getContentResolver().query(uri, null, null, null, null);
        if (query == null || query.isClosed()) {
            return null;
        }
        return query;
    }

    private void registerSyncGlobalSearchCompleted() {
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter(ACTION_SYNC_GLOBAL_SEARCH));
    }

    private boolean resolveIntent(GlobalSearchItem globalSearchItem) {
        return getResolveInfo(getIntent(globalSearchItem)) != null;
    }

    private SearchResultItem sameTarget(SearchResultItem searchResultItem, SearchResultItem searchResultItem2) {
        if (searchResultItem == null || searchResultItem2 == null || searchResultItem.intent == null || searchResultItem2.intent == null || !TextUtils.equals(searchResultItem.title, searchResultItem2.title)) {
            return null;
        }
        ResolveInfo resolveInfo = getResolveInfo(searchResultItem.intent);
        ResolveInfo resolveInfo2 = getResolveInfo(searchResultItem2.intent);
        ActivityInfo activityInfo = resolveInfo != null ? resolveInfo.activityInfo : null;
        ActivityInfo activityInfo2 = resolveInfo2 != null ? resolveInfo2.activityInfo : null;
        if (activityInfo != null && activityInfo2 != null && TextUtils.equals(activityInfo.name, activityInfo2.name)) {
            return !searchResultItem.isGlobalSearch ? searchResultItem : searchResultItem2;
        }
        return null;
    }

    private boolean supportGlobalSearch() {
        return !Build.IS_INTERNATIONAL_BUILD && MiuiAboutPhoneUtils.isLocalCnAndChinese();
    }

    public boolean isOnlyEmptyList(List<SearchResultItem> list) {
        SearchResultItem searchResultItem;
        return list != null && list.size() == 1 && (searchResultItem = list.get(0)) != null && searchResultItem.type == 1;
    }

    public void removeDuplicateSearchResult(List<SearchResultItem> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        int size = list.size();
        boolean[] zArr = new boolean[size];
        Arrays.fill(zArr, false);
        for (int i = 0; i < size; i++) {
            if (!zArr[i]) {
                for (int i2 = i + 1; i2 < size; i2++) {
                    if (!zArr[i2]) {
                        zArr[i2] = sameTarget(list.get(i), list.get(i2)) != null;
                    }
                }
            }
        }
        for (int i3 = size - 1; i3 >= 0; i3--) {
            if (zArr[i3]) {
                list.remove(i3);
            }
        }
    }

    public void requestGlobalSearchUpdate() {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.search.SettingsGlobalSearcher.1
            @Override // java.lang.Runnable
            public void run() {
                SettingsGlobalSearcher.this.loadData();
                if (SettingsGlobalSearcher.this.needUpdate() && !Build.IS_INTERNATIONAL_BUILD) {
                    SettingsGlobalSearcher settingsGlobalSearcher = SettingsGlobalSearcher.this;
                    if (settingsGlobalSearcher.queryUri(settingsGlobalSearcher.getUri()) != null) {
                        String destPackageCloudData = SettingsGlobalSearcher.this.getDestPackageCloudData();
                        Bundle bundle = new Bundle();
                        bundle.putString("cloud_data_dest_package", destPackageCloudData);
                        bundle.putLong("cloud_data_interval", SettingsGlobalSearcher.this.getCloudUpdateInterval());
                        Bundle call = SettingsGlobalSearcher.this.mContext.getContentResolver().call(SettingsGlobalSearcher.this.getUri(), SettingsGlobalSearcher.METHOD_UPDATE_GLOBAL_SEARCH, (String) null, bundle);
                        if (call != null) {
                            ContentResolver contentResolver = SettingsGlobalSearcher.this.mContext.getContentResolver();
                            Settings.Global.putLong(contentResolver, SettingsGlobalSearcher.KEY_LAST_UPDATE_TIMESTAMP, call.getLong(SettingsGlobalSearcher.KEY_LAST_UPDATE_TIMESTAMP, 0L));
                            Settings.Global.putLong(contentResolver, SettingsGlobalSearcher.KEY_UPDATE_INTERVAL, call.getLong(SettingsGlobalSearcher.KEY_UPDATE_INTERVAL, 0L));
                            return;
                        }
                        return;
                    }
                }
                Log.d(SettingsGlobalSearcher.TAG, "ContentProvider not exist or is international build or need not update");
            }
        });
    }

    public List<SearchResultItem> search(String str) {
        if (TextUtils.isEmpty(str) || !supportGlobalSearch()) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(SearchResultItem.EMPTY);
            return arrayList;
        }
        long currentTimeMillis = System.currentTimeMillis();
        List<SearchResultItem> buildSearchResult = buildSearchResult(str, this.mSearchResult);
        Log.d(TAG, "Settings Global Search takes " + (System.currentTimeMillis() - currentTimeMillis) + "ms");
        return buildSearchResult;
    }

    public void unregisterSyncGlobalSearchCompleted() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }
}
