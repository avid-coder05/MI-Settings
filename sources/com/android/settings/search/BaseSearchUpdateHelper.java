package com.android.settings.search;

import android.content.ContentProviderOperation;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
class BaseSearchUpdateHelper {
    private static final String TAG = "BaseSearchUpdateHelper";

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void disableByResource(Context context, ArrayList<ContentProviderOperation> arrayList, String str, boolean z) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public static long getAdditionalSettingsValue(Context context, String str) {
        return 3L;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public static List<String> getIdWithResource(Context context, String str) {
        return new ArrayList();
    }

    @Deprecated
    static List<String> getTreeById(Context context, String str) {
        return new ArrayList();
    }

    @Deprecated
    static String getValueFromDatabase(Context context, String str, String str2) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void hideByResource(Context context, ArrayList<ContentProviderOperation> arrayList, String str) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void hideTreeByRootResource(Context context, ArrayList<ContentProviderOperation> arrayList, String str) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void insertSearchItem(ArrayList<ContentProviderOperation> arrayList, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, long j) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public static void updateItemAdditionalData(Context context, ArrayList<ContentProviderOperation> arrayList, String str, long j) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public static void updateItemData(Context context, ArrayList<ContentProviderOperation> arrayList, String str, String str2, String str3) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void updatePath(Context context, ArrayList<ContentProviderOperation> arrayList, String str, String str2) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public static void updateSearchItem(ArrayList<ContentProviderOperation> arrayList, String str, int i, String[] strArr, String[] strArr2) {
    }

    @Deprecated
    static void updateTreeAdditionalData(Context context, ArrayList<ContentProviderOperation> arrayList, List<String> list, long j) {
    }
}
