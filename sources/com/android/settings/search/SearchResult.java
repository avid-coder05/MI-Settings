package com.android.settings.search;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.TextUtils;
import com.android.settings.search.provider.SettingsProvider;
import com.android.settings.search.tree.SecuritySettingsTree;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.search.Function;
import com.android.settingslib.search.RankedCursor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import miui.os.Build;
import miui.payment.PaymentManager;
import miui.yellowpage.YellowPageStatistic;

/* loaded from: classes2.dex */
public class SearchResult {
    public static Set<String> ENABLED_REDUNDANT_ENTRIES_SET = null;
    private static final String TAG = "SearchResult";

    static {
        HashSet hashSet = new HashSet();
        ENABLED_REDUNDANT_ENTRIES_SET = hashSet;
        hashSet.add(SecuritySettingsTree.SIM_PIN_TOGGLE);
        ENABLED_REDUNDANT_ENTRIES_SET.add(SecuritySettingsTree.SIM_PIN_CHANGE);
        ENABLED_REDUNDANT_ENTRIES_SET.add(SecuritySettingsTree.SIM_RADIO_OFF);
    }

    private static boolean checkBundleEqual(Bundle bundle, Bundle bundle2) {
        for (String str : bundle.keySet()) {
            if (!bundle2.containsKey(str)) {
                return false;
            }
            Object obj = bundle.get(str);
            Object obj2 = bundle2.get(str);
            if (((obj instanceof Bundle) && (obj2 instanceof Bundle) && !checkBundleEqual((Bundle) obj, (Bundle) obj2)) || !Objects.equals(obj, obj2)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkIntentEqual(Intent intent, Intent intent2) {
        return (intent == null && intent2 == null) || (intent != null && intent2 != null && TextUtils.equals(intent.getAction(), intent2.getAction()) && Objects.equals(intent.getComponent(), intent2.getComponent()) && checkBundleEqual(intent.getExtras(), intent2.getExtras()));
    }

    public static HashSet<String> getSearchExcludeMap() {
        boolean z = false;
        boolean z2 = SystemProperties.getBoolean("ro.radio.noril", false);
        if (Build.IS_TABLET && z2) {
            z = true;
        }
        if (z) {
            HashSet<String> hashSet = new HashSet<>();
            hashSet.add("com.miui.antispam.ui.activity.MainActivity");
            hashSet.add("miui.intent.action.SET_FIREWALL");
            hashSet.add("com.miui.antispam.ui.activity.AntiSpamNewSettingsActivity");
            hashSet.add("com.miui.antispam.action.ANTISPAM_SETTINGS");
            return hashSet;
        }
        return null;
    }

    public static List<SearchResultItem> removeExcludeItem(HashSet<String> hashSet, List<SearchResultItem> list) {
        ComponentName component;
        ArrayList arrayList = new ArrayList();
        for (SearchResultItem searchResultItem : list) {
            Intent intent = searchResultItem.intent;
            if (intent == null || (!hashSet.contains(intent.getAction()) && ((component = intent.getComponent()) == null || !hashSet.contains(component.getClassName())))) {
                arrayList.add(searchResultItem);
            }
        }
        return arrayList;
    }

    public List getSearchResultList(Context context, String str) {
        Cursor query = context.getContentResolver().query(SettingsProvider.getSearchUri(str), Function.FUNCTIONS, null, null, null);
        LinkedList linkedList = new LinkedList();
        if (query == null) {
            return linkedList;
        }
        RankedCursor wrappedCursor = ((CursorWrapper) query).getWrappedCursor();
        String str2 = null;
        Intent intent = null;
        while (wrappedCursor.moveToNext()) {
            String string = wrappedCursor.getString(wrappedCursor.getColumnIndex("resource"));
            Intent intent2 = (Intent) wrappedCursor.getExtras().get(wrappedCursor.getString(wrappedCursor.getColumnIndex(PaymentManager.KEY_INTENT)));
            if (SettingsFeatures.isSplitTablet(context)) {
                intent2.addMiuiFlags(16);
            }
            if (!TextUtils.equals(str2, string) || !checkIntentEqual(intent2, intent) || ENABLED_REDUNDANT_ENTRIES_SET.contains(string)) {
                SearchResultItem searchResultItem = new SearchResultItem(0);
                searchResultItem.pkg = wrappedCursor.getString(wrappedCursor.getColumnIndex(FunctionColumns.PACKAGE));
                searchResultItem.resource = string;
                searchResultItem.title = wrappedCursor.getString(wrappedCursor.getColumnIndex("title"));
                searchResultItem.category = wrappedCursor.getString(wrappedCursor.getColumnIndex(YellowPageStatistic.Display.CATEGORY));
                searchResultItem.path = wrappedCursor.getString(wrappedCursor.getColumnIndex("path"));
                searchResultItem.keywords = wrappedCursor.getString(wrappedCursor.getColumnIndex("keywords"));
                searchResultItem.summary = wrappedCursor.getString(wrappedCursor.getColumnIndex(FunctionColumns.SUMMARY));
                searchResultItem.icon = wrappedCursor.getString(wrappedCursor.getColumnIndex("icon"));
                searchResultItem.checkbox = Boolean.parseBoolean(wrappedCursor.getString(wrappedCursor.getColumnIndex(FunctionColumns.IS_CHECKBOX)));
                searchResultItem.intent = intent2;
                searchResultItem.status = wrappedCursor.getInt(wrappedCursor.getColumnIndex("status"));
                searchResultItem.score = wrappedCursor.getScore();
                linkedList.add(searchResultItem);
                str2 = string;
                intent = intent2;
            }
        }
        if (linkedList.isEmpty()) {
            linkedList.add(SearchResultItem.EMPTY);
        }
        wrappedCursor.close();
        return linkedList;
    }
}
