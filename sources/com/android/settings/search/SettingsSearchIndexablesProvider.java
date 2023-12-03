package com.android.settings.search;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.SearchIndexableData;
import android.provider.SearchIndexableResource;
import android.provider.SearchIndexablesContract;
import android.provider.SearchIndexablesProvider;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import androidx.slice.SliceViewManager;
import com.android.settings.R;
import com.android.settings.dashboard.CategoryManager;
import com.android.settings.dashboard.DashboardFeatureProvider;
import com.android.settings.dashboard.DashboardFragmentRegistry;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.provider.SettingsProvider;
import com.android.settingslib.drawer.ActivityTile;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.Tile;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
public class SettingsSearchIndexablesProvider extends SearchIndexablesProvider {
    public static final boolean DEBUG = false;
    private static final Collection<String> INVALID_KEYS;
    public static final String SYSPROP_CRASH_ON_ERROR = "debug.com.android.settings.search.crash_on_error";
    private static final String TAG = "SettingsSearchProvider";
    private Map<String, Boolean> mSearchEnabledByCategoryKeyMap;

    static {
        ArraySet arraySet = new ArraySet();
        INVALID_KEYS = arraySet;
        arraySet.add(null);
        arraySet.add("");
    }

    private static Object[] createIndexableRawColumnObjects(SearchIndexableRaw searchIndexableRaw) {
        Object[] objArr = new Object[SearchIndexablesContract.INDEXABLES_RAW_COLUMNS.length];
        objArr[1] = searchIndexableRaw.title;
        objArr[2] = searchIndexableRaw.summaryOn;
        objArr[3] = searchIndexableRaw.summaryOff;
        objArr[4] = searchIndexableRaw.entries;
        objArr[5] = searchIndexableRaw.keywords;
        objArr[6] = searchIndexableRaw.screenTitle;
        objArr[7] = ((SearchIndexableData) searchIndexableRaw).className;
        objArr[8] = Integer.valueOf(((SearchIndexableData) searchIndexableRaw).iconResId);
        objArr[9] = ((SearchIndexableData) searchIndexableRaw).intentAction;
        objArr[10] = ((SearchIndexableData) searchIndexableRaw).intentTargetPackage;
        objArr[11] = ((SearchIndexableData) searchIndexableRaw).intentTargetClass;
        objArr[12] = ((SearchIndexableData) searchIndexableRaw).key;
        objArr[13] = Integer.valueOf(((SearchIndexableData) searchIndexableRaw).userId);
        return objArr;
    }

    private List<SearchIndexableRaw> getDynamicSearchIndexableRawData(Context context, com.android.settingslib.search.SearchIndexableData searchIndexableData) {
        List<SearchIndexableRaw> dynamicRawDataToIndex = searchIndexableData.getSearchIndexProvider().getDynamicRawDataToIndex(context, true);
        if (dynamicRawDataToIndex == null) {
            return new ArrayList();
        }
        Iterator<SearchIndexableRaw> it = dynamicRawDataToIndex.iterator();
        while (it.hasNext()) {
            ((SearchIndexableData) it.next()).className = searchIndexableData.getTargetClass().getName();
        }
        return dynamicRawDataToIndex;
    }

    private List<String> getNonIndexableKeysFromProvider(Context context) {
        Collection<com.android.settingslib.search.SearchIndexableData> providerValues = FeatureFactory.getFactory(context).getSearchFeatureProvider().getSearchIndexableResources().getProviderValues();
        ArrayList arrayList = new ArrayList();
        for (com.android.settingslib.search.SearchIndexableData searchIndexableData : providerValues) {
            System.currentTimeMillis();
            Indexable$SearchIndexProvider searchIndexProvider = searchIndexableData.getSearchIndexProvider();
            try {
                List<String> nonIndexableKeys = searchIndexProvider.getNonIndexableKeys(context);
                if (nonIndexableKeys != null && !nonIndexableKeys.isEmpty()) {
                    if (nonIndexableKeys.removeAll(INVALID_KEYS)) {
                        Log.v(TAG, searchIndexProvider + " tried to add an empty non-indexable key");
                    }
                    arrayList.addAll(nonIndexableKeys);
                }
            } catch (Exception e) {
                if (System.getProperty(SYSPROP_CRASH_ON_ERROR) != null) {
                    throw new RuntimeException(e);
                }
                Log.e(TAG, "Error trying to get non-indexable keys from: " + searchIndexableData.getTargetClass().getName(), e);
            }
        }
        return arrayList;
    }

    private List<SearchIndexableRaw> getSearchIndexableRawFromProvider(Context context) {
        Collection<com.android.settingslib.search.SearchIndexableData> providerValues = FeatureFactory.getFactory(context).getSearchFeatureProvider().getSearchIndexableResources().getProviderValues();
        ArrayList arrayList = new ArrayList();
        for (com.android.settingslib.search.SearchIndexableData searchIndexableData : providerValues) {
            List<SearchIndexableRaw> rawDataToIndex = searchIndexableData.getSearchIndexProvider().getRawDataToIndex(context, true);
            if (rawDataToIndex != null) {
                Iterator<SearchIndexableRaw> it = rawDataToIndex.iterator();
                while (it.hasNext()) {
                    ((SearchIndexableData) it.next()).className = searchIndexableData.getTargetClass().getName();
                }
                arrayList.addAll(rawDataToIndex);
            }
        }
        return arrayList;
    }

    private List<SearchIndexableResource> getSearchIndexableResourcesFromProvider(Context context) {
        Collection<com.android.settingslib.search.SearchIndexableData> providerValues = FeatureFactory.getFactory(context).getSearchFeatureProvider().getSearchIndexableResources().getProviderValues();
        ArrayList arrayList = new ArrayList();
        for (com.android.settingslib.search.SearchIndexableData searchIndexableData : providerValues) {
            List<SearchIndexableResource> xmlResourcesToIndex = searchIndexableData.getSearchIndexProvider().getXmlResourcesToIndex(context, true);
            if (xmlResourcesToIndex != null) {
                for (SearchIndexableResource searchIndexableResource : xmlResourcesToIndex) {
                    searchIndexableResource.className = TextUtils.isEmpty(searchIndexableResource.className) ? searchIndexableData.getTargetClass().getName() : searchIndexableResource.className;
                }
                arrayList.addAll(xmlResourcesToIndex);
            }
        }
        return arrayList;
    }

    List<SearchIndexableRaw> getInjectionIndexableRawData(Context context) {
        DashboardFeatureProvider dashboardFeatureProvider = FeatureFactory.getFactory(context).getDashboardFeatureProvider(context);
        ArrayList arrayList = new ArrayList();
        String packageName = context.getPackageName();
        for (DashboardCategory dashboardCategory : dashboardFeatureProvider.getAllCategories()) {
            if (!this.mSearchEnabledByCategoryKeyMap.containsKey(dashboardCategory.key) || this.mSearchEnabledByCategoryKeyMap.get(dashboardCategory.key).booleanValue()) {
                for (Tile tile : dashboardCategory.getTiles()) {
                    if (isEligibleForIndexing(packageName, tile)) {
                        SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
                        CharSequence title = tile.getTitle(context);
                        String charSequence = TextUtils.isEmpty(title) ? null : title.toString();
                        searchIndexableRaw.title = charSequence;
                        if (!TextUtils.isEmpty(charSequence)) {
                            ((SearchIndexableData) searchIndexableRaw).key = dashboardFeatureProvider.getDashboardKeyForTile(tile);
                            CharSequence summary = tile.getSummary(context);
                            String charSequence2 = TextUtils.isEmpty(summary) ? null : summary.toString();
                            searchIndexableRaw.summaryOn = charSequence2;
                            searchIndexableRaw.summaryOff = charSequence2;
                            ((SearchIndexableData) searchIndexableRaw).className = DashboardFragmentRegistry.CATEGORY_KEY_TO_PARENT_MAP.get(tile.getCategory());
                            arrayList.add(searchIndexableRaw);
                        }
                    }
                }
            } else {
                Log.i(TAG, "Skip indexing category: " + dashboardCategory.key);
            }
        }
        return arrayList;
    }

    boolean isEligibleForIndexing(String str, Tile tile) {
        return (TextUtils.equals(str, tile.getPackageName()) && (tile instanceof ActivityTile)) ? false : true;
    }

    public boolean onCreate() {
        this.mSearchEnabledByCategoryKeyMap = new ArrayMap();
        return true;
    }

    public Cursor queryDynamicRawData(String[] strArr) {
        Context context = getContext();
        ArrayList arrayList = new ArrayList();
        for (com.android.settingslib.search.SearchIndexableData searchIndexableData : FeatureFactory.getFactory(context).getSearchFeatureProvider().getSearchIndexableResources().getProviderValues()) {
            arrayList.addAll(getDynamicSearchIndexableRawData(context, searchIndexableData));
            Indexable$SearchIndexProvider searchIndexProvider = searchIndexableData.getSearchIndexProvider();
            if (searchIndexProvider instanceof BaseSearchIndexProvider) {
                refreshSearchEnabledState(context, (BaseSearchIndexProvider) searchIndexProvider);
            }
        }
        arrayList.addAll(getInjectionIndexableRawData(context));
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.INDEXABLES_RAW_COLUMNS);
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            matrixCursor.addRow(createIndexableRawColumnObjects((SearchIndexableRaw) it.next()));
        }
        return matrixCursor;
    }

    public Cursor queryNonIndexableKeys(String[] strArr) {
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS);
        for (String str : getNonIndexableKeysFromProvider(getContext())) {
            Object[] objArr = new Object[SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS.length];
            objArr[0] = str;
            matrixCursor.addRow(objArr);
        }
        return matrixCursor;
    }

    public Cursor queryRawData(String[] strArr) {
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.INDEXABLES_RAW_COLUMNS);
        Iterator<SearchIndexableRaw> it = getSearchIndexableRawFromProvider(getContext()).iterator();
        while (it.hasNext()) {
            matrixCursor.addRow(createIndexableRawColumnObjects(it.next()));
        }
        return matrixCursor;
    }

    public Cursor querySiteMapPairs() {
        CharSequence charSequence;
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.SITE_MAP_COLUMNS);
        Context context = getContext();
        for (DashboardCategory dashboardCategory : FeatureFactory.getFactory(context).getDashboardFeatureProvider(context).getAllCategories()) {
            String str = DashboardFragmentRegistry.CATEGORY_KEY_TO_PARENT_MAP.get(dashboardCategory.key);
            if (str != null) {
                for (Tile tile : dashboardCategory.getTiles()) {
                    String string = tile.getMetaData() != null ? tile.getMetaData().getString("com.android.settings.FRAGMENT_CLASS") : null;
                    if (string == null) {
                        string = tile.getComponentName();
                        charSequence = tile.getTitle(getContext());
                    } else {
                        charSequence = "";
                    }
                    if (string != null) {
                        matrixCursor.newRow().add("parent_class", str).add("child_class", string).add("child_title", charSequence);
                    }
                }
            }
        }
        for (String str2 : CustomSiteMapRegistry.CUSTOM_SITE_MAP.keySet()) {
            matrixCursor.newRow().add("parent_class", CustomSiteMapRegistry.CUSTOM_SITE_MAP.get(str2)).add("child_class", str2);
        }
        return matrixCursor;
    }

    public Cursor querySliceUriPairs() {
        SliceViewManager sliceViewManager = SliceViewManager.getInstance(getContext());
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.SLICE_URI_PAIRS_COLUMNS);
        String string = getContext().getString(R.string.config_non_public_slice_query_uri);
        Uri parse = !TextUtils.isEmpty(string) ? Uri.parse(string) : new Uri.Builder().scheme("content").authority("com.android.settings.slices").build();
        Uri build = new Uri.Builder().scheme("content").authority("android.settings.slices").build();
        Collection<Uri> sliceDescendants = sliceViewManager.getSliceDescendants(parse);
        sliceDescendants.addAll(sliceViewManager.getSliceDescendants(build));
        for (Uri uri : sliceDescendants) {
            matrixCursor.newRow().add(SettingsProvider.ARGS_KEY, uri.getLastPathSegment()).add("slice_uri", uri);
        }
        return matrixCursor;
    }

    public Cursor queryXmlResources(String[] strArr) {
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS);
        for (SearchIndexableResource searchIndexableResource : getSearchIndexableResourcesFromProvider(getContext())) {
            Object[] objArr = new Object[SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS.length];
            objArr[0] = Integer.valueOf(searchIndexableResource.rank);
            objArr[1] = Integer.valueOf(searchIndexableResource.xmlResId);
            objArr[2] = searchIndexableResource.className;
            objArr[3] = Integer.valueOf(searchIndexableResource.iconResId);
            objArr[4] = searchIndexableResource.intentAction;
            objArr[5] = searchIndexableResource.intentTargetPackage;
            objArr[6] = null;
            matrixCursor.addRow(objArr);
        }
        return matrixCursor;
    }

    void refreshSearchEnabledState(Context context, BaseSearchIndexProvider baseSearchIndexProvider) {
        DashboardCategory tilesByCategory;
        String name = baseSearchIndexProvider.getClass().getName();
        int lastIndexOf = name.lastIndexOf("$");
        if (lastIndexOf > 0) {
            name = name.substring(0, lastIndexOf);
        }
        String str = DashboardFragmentRegistry.PARENT_TO_CATEGORY_KEY_MAP.get(name);
        if (str == null || (tilesByCategory = CategoryManager.get(context).getTilesByCategory(context, str)) == null) {
            return;
        }
        this.mSearchEnabledByCategoryKeyMap.put(tilesByCategory.key, Boolean.valueOf(baseSearchIndexProvider.isPageSearchEnabled(context)));
    }
}
