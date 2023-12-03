package miui.yellowpage;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import miui.os.Build;
import miui.provider.ExtraTelephony;
import miui.yellowpage.Tag;
import miui.yellowpage.YellowPageContract;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes4.dex */
public class YellowPageUtils {
    private static final int ANTISPAM_COLUMN_CID = 1;
    private static final int ANTISPAM_COLUMN_MARKED_COUNT = 3;
    private static final int ANTISPAM_COLUMN_NORMALIZED_NUMBER = 4;
    private static final int ANTISPAM_COLUMN_NUMBER_TYPE = 5;
    private static final int ANTISPAM_COLUMN_PID = 0;
    private static final int ANTISPAM_COLUMN_TYPE = 2;
    private static final String CLOUD_ANTISPAM = "cloud_antispam";
    private static final int CLOUD_ANTISPAM_DISABLE = 0;
    private static final int CLOUD_ANTISPAM_ENANBLE = 1;
    private static final int PHONE_COLUMN_ATD_COUNT = 15;
    private static final int PHONE_COLUMN_ATD_ID = 13;
    private static final int PHONE_COLUMN_ATD_PROVIDER = 16;
    private static final int PHONE_COLUMN_CALL_MENU = 11;
    private static final int PHONE_COLUMN_CREDIT_IMG = 17;
    private static final int PHONE_COLUMN_NORMALIZED_NUMBER = 9;
    private static final int PHONE_COLUMN_NUMBER_TYPE = 18;
    private static final int PHONE_COLUMN_PHOTO_URL = 3;
    private static final int PHONE_COLUMN_PROVIDER_ID = 1;
    private static final int PHONE_COLUMN_SLOGAN = 14;
    private static final int PHONE_COLUMN_SUSPECT = 10;
    private static final int PHONE_COLUMN_T9_RANK = 12;
    private static final int PHONE_COLUMN_TAG = 2;
    private static final int PHONE_COLUMN_TAG_PINYIN = 7;
    private static final int PHONE_COLUMN_THUMBNAIL_URL = 4;
    private static final int PHONE_COLUMN_VISIBLE = 8;
    private static final int PHONE_COLUMN_YID = 0;
    private static final int PHONE_COLUMN_YP_NAME = 5;
    private static final int PHONE_COLUMN_YP_NAME_PINYIN = 6;
    private static final String TAG = "YellowPageUtils";
    private static final String[] PHONE_PROJECTION = {"yid", YellowPageContract.PhoneLookup.PROVIDER_ID, "tag", YellowPageContract.PhoneLookup.PHOTO_URL, YellowPageContract.PhoneLookup.THUMBNAIL_URL, YellowPageContract.PhoneLookup.YELLOW_PAGE_NAME, YellowPageContract.PhoneLookup.YELLOW_PAGE_NAME_PINYIN, YellowPageContract.PhoneLookup.TAG_PINYIN, "hide", "normalized_number", YellowPageContract.PhoneLookup.SUSPECT, YellowPageContract.PhoneLookup.CALL_MENU, "t9_rank", YellowPageContract.PhoneLookup.ATD_ID, "slogan", YellowPageContract.PhoneLookup.ATD_COUNT, YellowPageContract.PhoneLookup.ATD_PROVIDER, "credit_img", "number_type"};
    private static final String[] ANTISPAM_PROJECTION = {"pid", "cid", "type", YellowPageContract.AntispamNumber.MARKED_COUNT, "normalized_number", "number_type"};
    @SuppressLint({"UseSparseArrays"})
    private static final HashMap<Integer, YellowPageProvider> sProviders = new HashMap<>();
    @SuppressLint({"UseSparseArrays"})
    private static final ConcurrentHashMap<Integer, AntispamCategory> sCidCategories = new ConcurrentHashMap<>();

    private YellowPageUtils() {
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0016, code lost:
    
        if (r3.getInt(2) != 5) goto L13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0018, code lost:
    
        return true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x001e, code lost:
    
        if (r3.moveToNext() != false) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x000e, code lost:
    
        if (r3.moveToFirst() != false) goto L9;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static boolean antispamNumberNotInPresetFile(android.database.Cursor r3) {
        /*
            r0 = 0
            if (r3 == 0) goto L20
            int r1 = r3.getCount()
            if (r1 != 0) goto La
            goto L20
        La:
            boolean r1 = r3.moveToFirst()
            if (r1 == 0) goto L20
        L10:
            r1 = 2
            int r1 = r3.getInt(r1)
            r2 = 5
            if (r1 != r2) goto L1a
            r3 = 1
            return r3
        L1a:
            boolean r1 = r3.moveToNext()
            if (r1 != 0) goto L10
        L20:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.yellowpage.YellowPageUtils.antispamNumberNotInPresetFile(android.database.Cursor):boolean");
    }

    private static String buildAntispamCategorySelection(Integer... numArr) {
        StringBuilder sb = new StringBuilder();
        for (Integer num : numArr) {
            if (sb.length() > 0) {
                sb.append(" OR ");
            }
            sb.append("type");
            sb.append("=");
            sb.append(num);
        }
        sb.insert(0, "(");
        sb.append(")");
        return sb.toString();
    }

    private static YellowPagePhone buildAntispamInfoFromCursor(Context context, Cursor cursor, String str) {
        YellowPagePhone yellowPagePhone = null;
        while (cursor.moveToNext()) {
            int i = cursor.getInt(1);
            if (i == 0) {
                android.util.Log.d(TAG, "invalid cid");
            } else {
                String cidName = getCidName(context, i);
                if (TextUtils.isEmpty(cidName)) {
                    continue;
                } else {
                    int i2 = cursor.getInt(0);
                    int i3 = cursor.getInt(2) == 3 ? 3 : 2;
                    int i4 = cursor.getInt(3);
                    String string = cursor.getString(4);
                    int i5 = cursor.getInt(5);
                    if (2 == i3 && i5 != 0) {
                        cidName = "";
                    }
                    int i6 = i3;
                    YellowPagePhone yellowPagePhone2 = new YellowPagePhone(-1L, (String) null, cidName, str, string, i3, i2, i4, true, (String) null, (String) null, i);
                    yellowPagePhone2.setNumberType(i5);
                    if (yellowPagePhone == null || i6 == 3) {
                        yellowPagePhone = yellowPagePhone2;
                        if (i6 == 3) {
                            break;
                        }
                    }
                }
            }
        }
        return yellowPagePhone;
    }

    private static YellowPagePhone buildYellowPagePhoneFromCursor(Cursor cursor, String str) {
        int i = cursor.getInt(1);
        String string = cursor.getString(2);
        String string2 = cursor.getString(5);
        long j = cursor.getLong(0);
        String string3 = cursor.getString(6);
        String string4 = cursor.getString(7);
        String string5 = cursor.getString(9);
        boolean z = cursor.getInt(8) > 0;
        boolean z2 = cursor.getInt(10) > 0;
        boolean z3 = cursor.getInt(11) > 0;
        long j2 = cursor.getLong(12);
        int i2 = cursor.getInt(13);
        String string6 = cursor.getString(14);
        int i3 = cursor.getInt(15);
        int i4 = cursor.getInt(16);
        String string7 = cursor.getString(17);
        int i5 = cursor.getInt(18);
        YellowPagePhone yellowPagePhone = new YellowPagePhone(j, string2, string, str, string5, j != -1 ? 1 : i2 > 0 ? 2 : 0, i, i3, z, string3, string4, z2, z3);
        yellowPagePhone.setT9Rank(j2);
        yellowPagePhone.setRawSlogan(string6);
        yellowPagePhone.setCreditImg(string7);
        yellowPagePhone.setCid(i2);
        yellowPagePhone.setAntispamProviderId(i4);
        yellowPagePhone.setNumberType(i5);
        return yellowPagePhone;
    }

    public static int createAntispamCategory(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            android.util.Log.d(TAG, "The category name must not be null");
        }
        Uri uri = YellowPageContract.AntispamCategory.CONTENT_URI;
        if (isContentProviderInstalled(context, uri)) {
            Cursor query = context.getContentResolver().query(uri, new String[]{"MAX(cid)"}, null, null, null);
            int i = 10000;
            if (query != null) {
                try {
                    try {
                        if (query.moveToFirst()) {
                            int i2 = query.getInt(0);
                            if (i2 >= 10000) {
                                i = i2 + 1;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } finally {
                    query.close();
                }
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put("cid", Integer.valueOf(i));
            contentValues.put("names", str);
            contentValues.put("type", (Integer) 1);
            insert(context, YellowPageContract.AntispamCategory.CONTENT_URI, contentValues);
            return i;
        }
        return 0;
    }

    public static String formatPreferenceKey(String str) {
        return String.format(Tag.TagPreference.PREF_FORMAT, str);
    }

    private static AntispamCustomCategory getAntispamCategoryFromCursor(Context context, Cursor cursor, int i, String str) {
        if (i != -1 && cursor.moveToPosition(i)) {
            int i2 = cursor.getInt(1);
            int i3 = cursor.getInt(2);
            int i4 = cursor.getInt(3);
            int i5 = cursor.getInt(5);
            boolean z = i3 == 3;
            if (z) {
                getCategories(context);
            }
            AntispamCategory antispamCategory = sCidCategories.get(Integer.valueOf(i2));
            if (antispamCategory != null) {
                AntispamCustomCategory antispamCustomCategory = new AntispamCustomCategory(antispamCategory.getCategoryId(), antispamCategory.getCategoryAllNames(), antispamCategory.getCategoryType(), antispamCategory.getIcon(), antispamCategory.getOrder(), str, i4, z);
                antispamCustomCategory.setNumberType(i5);
                return antispamCustomCategory;
            }
        }
        return null;
    }

    public static AntispamCustomCategory getAntispamNumberCategory(Context context, String str) {
        return getAntispamNumberCategory(context, str, true);
    }

    public static AntispamCustomCategory getAntispamNumberCategory(Context context, String str, boolean z) {
        AntispamCustomCategory antispamCustomCategory = null;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        Uri withAppendedPath = Uri.withAppendedPath(YellowPageContract.AntispamNumber.CONTENT_PHONE_LOOKUP_URI, str);
        if (isContentProviderInstalled(context, withAppendedPath)) {
            Cursor query = context.getContentResolver().query(withAppendedPath, ANTISPAM_PROJECTION, buildAntispamCategorySelection(3, 1, 2, 5), null, null, null);
            if (query != null) {
                int i = -1;
                int i2 = -1;
                int i3 = -1;
                int i4 = -1;
                while (query.moveToNext()) {
                    try {
                        try {
                            int i5 = query.getInt(2);
                            if (i5 == 1 || i5 == 2) {
                                i3 = query.getPosition();
                            } else if (i5 == 3) {
                                i2 = query.getPosition();
                            } else if (i5 == 5) {
                                i4 = query.getPosition();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } finally {
                        query.close();
                    }
                }
                if (i2 != -1) {
                    android.util.Log.d(TAG, "getAntispamNumberCategory(): found type user marked in db");
                    i = i2;
                } else if (i3 != -1) {
                    android.util.Log.d(TAG, "getAntispamNumberCategory(): found type preset/cloud in db");
                    i = i3;
                } else if (i4 != -1) {
                    android.util.Log.d(TAG, "getAntispamNumberCategory(): found type not in preset in db");
                    return null;
                }
                antispamCustomCategory = getAntispamCategoryFromCursor(context, query, i, str);
                if (antispamCustomCategory == null) {
                    android.util.Log.d(TAG, "getAntispamNumberCategory(): find nothing in db");
                }
            } else {
                android.util.Log.d(TAG, "getAntispamNumberCategory(): find nothing in db");
            }
            if (antispamCustomCategory == null) {
                Cursor query2 = context.getContentResolver().query(Uri.withAppendedPath(YellowPageContract.AntispamNumber.CONTENT_PRESET_PHONE_LOOKUP_URI, str), ANTISPAM_PROJECTION, null, null, null);
                if (query2 != null) {
                    try {
                        try {
                            antispamCustomCategory = getAntispamCategoryFromCursor(context, query2, 0, str);
                            if (antispamCustomCategory != null) {
                                android.util.Log.d(TAG, "getAntispamNumberCategory(): found in preset");
                            } else {
                                android.util.Log.d(TAG, "getAntispamNumberCategory(): not found in preset");
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    } finally {
                        query2.close();
                    }
                } else {
                    android.util.Log.d(TAG, "getAntispamNumberCategory(): find nothing in preset");
                }
            }
            if (antispamCustomCategory != null) {
                android.util.Log.d(TAG, String.format("getAntispamNumberCategory(): number=%s, numberType=%s, category=%s", getLogNumber(str), Integer.valueOf(antispamCustomCategory.getNumberType()), antispamCustomCategory.getCategoryName()));
            } else {
                android.util.Log.d(TAG, String.format("getAntispamNumberCategory(): number=%s, not found", getLogNumber(str)));
            }
            return antispamCustomCategory;
        }
        return null;
    }

    private static boolean getBooleanSettings(Context context, String str) {
        Cursor query;
        Uri withAppendedPath = Uri.withAppendedPath(YellowPageContract.Settings.CONTENT_URI, str);
        if (isContentProviderInstalled(context, withAppendedPath) && (query = context.getContentResolver().query(withAppendedPath, null, null, null, null)) != null) {
            try {
                if (query.moveToFirst()) {
                    return query.getInt(0) > 0;
                }
            } finally {
                query.close();
            }
        }
        return false;
    }

    public static List<AntispamCategory> getCategories(Context context) {
        Uri uri = YellowPageContract.AntispamCategory.CONTENT_URI;
        if (isContentProviderInstalled(context, uri)) {
            Cursor query = context.getContentResolver().query(uri, new String[]{"cid", "names", "type", "icon", YellowPageContract.AntispamCategory.ORDER}, null, null, null);
            if (query != null) {
                while (query.moveToNext()) {
                    try {
                        try {
                            int i = query.getInt(0);
                            sCidCategories.put(Integer.valueOf(i), new AntispamCategory(i, query.getString(1), query.getInt(2), query.getString(3), query.getInt(4)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } finally {
                        query.close();
                    }
                }
            }
            ConcurrentHashMap<Integer, AntispamCategory> concurrentHashMap = sCidCategories;
            if (concurrentHashMap.values() == null) {
                return null;
            }
            return new ArrayList(concurrentHashMap.values());
        }
        return Collections.emptyList();
    }

    public static String getCidName(Context context, int i) {
        ConcurrentHashMap<Integer, AntispamCategory> concurrentHashMap = sCidCategories;
        if (concurrentHashMap.containsKey(Integer.valueOf(i))) {
            AntispamCategory antispamCategory = concurrentHashMap.get(Integer.valueOf(i));
            if (antispamCategory == null) {
                return null;
            }
            return antispamCategory.getCategoryName();
        }
        getCategories(context);
        AntispamCategory antispamCategory2 = concurrentHashMap.get(Integer.valueOf(i));
        if (antispamCategory2 == null) {
            return null;
        }
        return antispamCategory2.getCategoryName();
    }

    public static String getIvrMenuByNumber(Context context, String str) {
        Cursor query;
        Uri withAppendedPath = Uri.withAppendedPath(YellowPageContract.Ivr.CONTENT_URI, str);
        if (isContentProviderInstalled(context, withAppendedPath) && (query = context.getContentResolver().query(withAppendedPath, new String[]{"data"}, null, null, null)) != null) {
            try {
                if (query.moveToFirst()) {
                    return query.getString(0);
                }
            } finally {
                query.close();
            }
        }
        return null;
    }

    public static HashMap<String, YellowPagePhone> getLocalYellowPagePhones(Context context, List<String> list) {
        return getLocalYellowPagePhones(context, list, null);
    }

    public static HashMap<String, YellowPagePhone> getLocalYellowPagePhones(Context context, List<String> list, HashMap<String, String> hashMap) {
        String normalizedNumber;
        HashMap<String, YellowPagePhone> hashMap2 = null;
        if (isYellowPageAvailable(context) && list != null && list.size() != 0) {
            if (!isContentProviderInstalled(context, YellowPageContract.PhoneLookup.CONTENT_URI)) {
                return null;
            }
            HashMap hashMap3 = new HashMap();
            hashMap2 = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                String str = list.get(i);
                if (hashMap == null || TextUtils.isEmpty(hashMap.get(str))) {
                    normalizedNumber = getNormalizedNumber(context, str);
                    if (!TextUtils.isEmpty(normalizedNumber)) {
                        if (hashMap != null) {
                            hashMap.put(str, normalizedNumber);
                        }
                    }
                } else {
                    normalizedNumber = hashMap.get(str);
                }
                hashMap3.put(normalizedNumber, str);
                if (hashMap3.size() > 50 || i == list.size() - 1) {
                    StringBuilder sb = new StringBuilder();
                    for (String str2 : hashMap3.keySet()) {
                        if (sb.length() > 0) {
                            sb.append(",");
                        }
                        sb.append("'");
                        sb.append(str2);
                        sb.append("'");
                    }
                    Cursor query = context.getContentResolver().query(YellowPageContract.PhoneLookup.CONTENT_URI, PHONE_PROJECTION, "normalized_number IN (" + sb.toString() + ")", null, null);
                    if (query != null) {
                        while (query.moveToNext()) {
                            try {
                                try {
                                    String str3 = (String) hashMap3.get(query.getString(9));
                                    hashMap2.put(str3, buildYellowPagePhoneFromCursor(query, str3));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } finally {
                                query.close();
                            }
                        }
                        hashMap3.clear();
                    }
                }
            }
        }
        return hashMap2;
    }

    private static String getLogNumber(String str) {
        if (str == null || str.length() == 0) {
            return "[empty number]";
        }
        int length = str.length();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        if (length <= 4) {
            for (int i2 = 0; i2 < length - 2; i2++) {
                sb.append(str.charAt(i2));
            }
            int min = Math.min(length, 2);
            while (i < min) {
                sb.append("*");
                i++;
            }
        } else if (length == 5 || length == 6) {
            while (i < length - 3) {
                sb.append(str.charAt(i));
                i++;
            }
            sb.append(ExtraTelephony.PrefixCode);
        } else if (length > 6) {
            while (i < length - 4) {
                sb.append(str.charAt(i));
                i++;
            }
            sb.append("****");
        }
        return sb.toString();
    }

    public static String getNormalizedNumber(Context context, String str) {
        return getNormalizedNumber(context, str, true, null);
    }

    public static String getNormalizedNumber(Context context, String str, boolean z, String str2) {
        Cursor query;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        Uri.Builder buildUpon = Uri.withAppendedPath(YellowPageContract.PhoneLookup.CONTENT_NORMALIZED_NUMBER, str).buildUpon();
        buildUpon.appendQueryParameter("withCountryCode", z ? "true" : "false");
        if (!TextUtils.isEmpty(str2)) {
            buildUpon.appendQueryParameter("defaultCountryCode", str2);
        }
        Uri build = buildUpon.build();
        if (isContentProviderInstalled(context, build) && (query = context.getContentResolver().query(build, null, null, null, null)) != null) {
            try {
                if (query.moveToFirst()) {
                    return query.getString(0);
                }
            } finally {
                query.close();
            }
        }
        return null;
    }

    public static YellowPagePhone getPhoneInfo(Context context, String str, boolean z) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        YellowPagePhone queryPhoneInfo = queryPhoneInfo(context, str, false);
        if (queryPhoneInfo == null && z) {
            return queryPhoneInfo(context, str, true);
        }
        if (z) {
            updateCloudPhoneInfo(context, str);
            return queryPhoneInfo;
        }
        return queryPhoneInfo;
    }

    public static YellowPageProvider getProvider(Context context, int i) {
        YellowPageProvider yellowPageProvider = sProviders.get(Integer.valueOf(i));
        if (yellowPageProvider == null || TextUtils.isEmpty(yellowPageProvider.getName())) {
            Uri uri = YellowPageContract.Provider.CONTENT_URI;
            if (isContentProviderInstalled(context, uri)) {
                Cursor query = context.getContentResolver().query(uri, new String[]{"name", "icon", "pid", YellowPageContract.Provider.ICON_BIG}, null, null, null);
                if (query != null) {
                    while (query.moveToNext()) {
                        try {
                            try {
                                String string = query.getString(0);
                                byte[] blob = query.getBlob(1);
                                Bitmap decodeByteArray = blob == null ? null : BitmapFactory.decodeByteArray(blob, 0, blob.length);
                                byte[] blob2 = query.getBlob(3);
                                Bitmap decodeByteArray2 = blob2 == null ? null : BitmapFactory.decodeByteArray(blob2, 0, blob2.length);
                                int i2 = query.getInt(2);
                                sProviders.put(Integer.valueOf(i2), new YellowPageProvider(i2, string, decodeByteArray, decodeByteArray2));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } finally {
                            query.close();
                        }
                    }
                }
                YellowPageProvider yellowPageProvider2 = sProviders.get(Integer.valueOf(i));
                return yellowPageProvider2 == null ? YellowPageProvider.DEFAULT_PROVIDER : yellowPageProvider2;
            }
            return null;
        }
        return yellowPageProvider;
    }

    public static String getUserAreaCode(Context context) {
        Uri uri = YellowPageContract.UserArea.CONTENT_URI;
        if (isContentProviderInstalled(context, uri)) {
            Cursor query = context.getContentResolver().query(uri, new String[]{YellowPageContract.UserArea.AREA_CODE}, null, null, null);
            try {
                if (query != null) {
                    try {
                        if (query.moveToFirst()) {
                            return query.getString(0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            } finally {
                query.close();
            }
        }
        return null;
    }

    @Deprecated
    public static void informYellowpagePhoneEvent(Context context, long j, long j2, String str, String str2, int i, String str3, int i2) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(YellowPageContract.MipubPhoneEvent.EXTRA_DATA_DIRECTION, i);
            jSONObject.put("number", str3);
            jSONObject.put(YellowPageContract.MipubPhoneEvent.EXTRA_DATA_CONNECTED, i2);
            Uri build = YellowPageContract.MipubPhoneEvent.CONTENT_URI_MIPUB_PHONE_EVENT.buildUpon().appendQueryParameter(YellowPageContract.MipubPhoneEvent.URI_PARAM_START_TIME, String.valueOf(j)).appendQueryParameter(YellowPageContract.MipubPhoneEvent.URI_PARAM_END_TIME, String.valueOf(j2)).appendQueryParameter(YellowPageContract.MipubPhoneEvent.URI_PARAM_SOURCE, str).appendQueryParameter(YellowPageContract.MipubPhoneEvent.URI_PARAM_TYPE, str2).appendQueryParameter(YellowPageContract.MipubPhoneEvent.URI_PARAM_EXTRA_DATA, Uri.encode(jSONObject.toString())).build();
            if (isContentProviderInstalled(context, build)) {
                try {
                    Cursor query = context.getContentResolver().query(build, null, null, null, null);
                    if (query != null) {
                        query.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    private static Uri insert(Context context, Uri uri, ContentValues contentValues) {
        if (isContentProviderInstalled(context, uri)) {
            return context.getContentResolver().insert(uri, contentValues);
        }
        android.util.Log.d(TAG, "insert-The provider is not installed");
        return null;
    }

    private static boolean isCloudAntispamEnable(Context context) {
        return Settings.System.getInt(context.getContentResolver(), CLOUD_ANTISPAM, 0) == 1;
    }

    public static boolean isContentProviderInstalled(Context context, Uri uri) {
        if (context == null || uri == null) {
            return false;
        }
        ContentProviderClient acquireUnstableContentProviderClient = context.getContentResolver().acquireUnstableContentProviderClient(uri);
        if (acquireUnstableContentProviderClient == null) {
            android.util.Log.e(TAG, "The content provider is not installed");
            return false;
        }
        acquireUnstableContentProviderClient.release();
        return true;
    }

    public static boolean isFraudIncomingNumber(Context context, int i, String str, String str2) {
        Cursor query;
        Uri.Builder buildUpon = YellowPageContract.PhoneLookup.CONTENT_FRAUD_VERIFY.buildUpon();
        buildUpon.appendQueryParameter("simIndex", String.valueOf(i));
        buildUpon.appendQueryParameter("incoming", str);
        buildUpon.appendQueryParameter("yid", str2);
        Uri build = buildUpon.build();
        if (isContentProviderInstalled(context, build) && (query = context.getContentResolver().query(build, null, null, null, null)) != null) {
            try {
                if (query.moveToFirst()) {
                    return query.getInt(0) > 0;
                }
            } finally {
                query.close();
            }
        }
        return false;
    }

    public static boolean isFraudNumOnlineIdentificationEnabled(Context context) {
        return getBooleanSettings(context, YellowPageContract.Settings.ONLINE_FRAUD_ENABLE);
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x003d, code lost:
    
        if (r8.moveToNext() == false) goto L31;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0047, code lost:
    
        if (android.text.TextUtils.equals(r9, r8.getString(0)) != false) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x004f, code lost:
    
        if (r9.startsWith(miui.provider.ExtraTelephony.BANK_CATEGORY_NUMBER_PREFIX_106) == false) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0051, code lost:
    
        r8.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0055, code lost:
    
        return true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x005a, code lost:
    
        r9 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x005c, code lost:
    
        r9 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x005d, code lost:
    
        r9.printStackTrace();
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0061, code lost:
    
        r8.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0064, code lost:
    
        throw r9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0065, code lost:
    
        return false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0037, code lost:
    
        if (r8 != null) goto L27;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean isInSmsWhiteList(android.content.Context r8, java.lang.String r9) {
        /*
            boolean r0 = android.text.TextUtils.isEmpty(r9)
            r1 = 0
            if (r0 == 0) goto L8
            return r1
        L8:
            android.net.Uri r3 = miui.yellowpage.YellowPageContract.AntispamWhiteList.CONTNET_URI
            boolean r0 = isContentProviderInstalled(r8, r3)
            if (r0 != 0) goto L11
            return r1
        L11:
            android.content.ContentResolver r2 = r8.getContentResolver()
            java.lang.String r8 = "number"
            java.lang.String[] r4 = new java.lang.String[]{r8}
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r0 = "number LIKE '"
            r8.append(r0)
            r8.append(r9)
            java.lang.String r0 = "%'"
            r8.append(r0)
            java.lang.String r5 = r8.toString()
            r6 = 0
            r7 = 0
            android.database.Cursor r8 = r2.query(r3, r4, r5, r6, r7)
            if (r8 == 0) goto L65
        L39:
            boolean r0 = r8.moveToNext()     // Catch: java.lang.Throwable -> L5a java.lang.Exception -> L5c
            if (r0 == 0) goto L56
            java.lang.String r0 = r8.getString(r1)     // Catch: java.lang.Throwable -> L5a java.lang.Exception -> L5c
            boolean r0 = android.text.TextUtils.equals(r9, r0)     // Catch: java.lang.Throwable -> L5a java.lang.Exception -> L5c
            if (r0 != 0) goto L51
            java.lang.String r0 = "106"
            boolean r0 = r9.startsWith(r0)     // Catch: java.lang.Throwable -> L5a java.lang.Exception -> L5c
            if (r0 == 0) goto L39
        L51:
            r9 = 1
            r8.close()
            return r9
        L56:
            r8.close()
            goto L65
        L5a:
            r9 = move-exception
            goto L61
        L5c:
            r9 = move-exception
            r9.printStackTrace()     // Catch: java.lang.Throwable -> L5a
            goto L56
        L61:
            r8.close()
            throw r9
        L65:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.yellowpage.YellowPageUtils.isInSmsWhiteList(android.content.Context, java.lang.String):boolean");
    }

    public static boolean isIvrMenuExist(Context context, String str) {
        Cursor query;
        Uri withAppendedPath = Uri.withAppendedPath(YellowPageContract.Ivr.CONTENT_URI, str);
        if (isContentProviderInstalled(context, withAppendedPath) && (query = context.getContentResolver().query(withAppendedPath, new String[]{"exist"}, null, null, null)) != null) {
            try {
                if (query.moveToFirst()) {
                    return query.getInt(0) == 1;
                }
            } finally {
                query.close();
            }
        }
        return false;
    }

    public static boolean isNeverRemindSmartAntispamEnable(Context context) {
        return getBooleanSettings(context, YellowPageContract.Settings.NEVER_REMIND_ENABLE_ANTISPAM);
    }

    public static boolean isRemindIgnoredUserSuspectNumber(Context context) {
        return getBooleanSettings(context, YellowPageContract.Settings.REMIND_USER_SUSPECT_NUMBER);
    }

    public static boolean isYellowPageAvailable(Context context) {
        Locale locale = Locale.getDefault();
        return (!Build.IS_INTERNATIONAL_BUILD && (Locale.SIMPLIFIED_CHINESE.equals(locale) || Locale.TRADITIONAL_CHINESE.equals(locale) || Locale.US.equals(locale))) || "IN".equals(Build.getRegion());
    }

    public static boolean isYellowPageEnable(Context context) {
        return isYellowPageAvailable(context) && isCloudAntispamEnable(context);
    }

    public static void markAntiSpam(Context context, String str, int i, boolean z) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", str);
        contentValues.put("categoryId", Integer.valueOf(i));
        contentValues.put("delete", Boolean.valueOf(z));
        update(context, YellowPageContract.AntispamNumber.CONTENT_MARK_NUMBER_URI, contentValues, null, null);
    }

    private static void queryCloudExpressInfo(Context context, String str, String str2) {
        if (!isYellowPageAvailable(context) || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("bizName", str);
        bundle.putString("serialNumber", str2);
        InvocationHandler.invoke(context, YellowPageContract.Method.REQUEST_REMOTE_EXPRESS_INFO, null, bundle);
    }

    public static void queryExpressInfo(Context context, String str, String str2) {
        if (queryLocalExpressInfo(context, str, str2)) {
            return;
        }
        queryCloudExpressInfo(context, str, str2);
    }

    private static boolean queryLocalExpressInfo(Context context, String str, String str2) {
        if (!isYellowPageAvailable(context) || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putString("bizName", str);
        bundle.putString("serialNumber", str2);
        return InvocationHandler.invoke(context, YellowPageContract.Method.REQUEST_LOCAL_EXPRESS_INFO, null, bundle).getBoolean("res");
    }

    private static YellowPagePhone queryPhoneInfo(Context context, String str, boolean z) {
        YellowPagePhone buildAntispamInfoFromCursor;
        Uri withAppendedPath = Uri.withAppendedPath(z ? YellowPageContract.PhoneLookup.CONTENT_URI_CLOUD : YellowPageContract.PhoneLookup.CONTENT_URI, str);
        if (isContentProviderInstalled(context, withAppendedPath)) {
            Cursor query = context.getContentResolver().query(withAppendedPath, PHONE_PROJECTION, null, null, null);
            try {
                if (query != null) {
                    try {
                        if (query.moveToFirst()) {
                            return buildYellowPagePhoneFromCursor(query, str);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!z) {
                    query = context.getContentResolver().query(Uri.withAppendedPath(YellowPageContract.AntispamNumber.CONTENT_PHONE_LOOKUP_URI, str), ANTISPAM_PROJECTION, "type<>4", null, null);
                    if (query != null) {
                        try {
                            try {
                                buildAntispamInfoFromCursor = buildAntispamInfoFromCursor(context, query, str);
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                            if (buildAntispamInfoFromCursor != null) {
                                return buildAntispamInfoFromCursor;
                            }
                            if (antispamNumberNotInPresetFile(query)) {
                                return null;
                            }
                        } finally {
                        }
                    }
                    query = context.getContentResolver().query(Uri.withAppendedPath(YellowPageContract.AntispamNumber.CONTENT_PRESET_PHONE_LOOKUP_URI, str), ANTISPAM_PROJECTION, null, null, null);
                    if (query != null) {
                        try {
                            try {
                                YellowPagePhone buildAntispamInfoFromCursor2 = buildAntispamInfoFromCursor(context, query, str);
                                if (buildAntispamInfoFromCursor2 != null) {
                                    return buildAntispamInfoFromCursor2;
                                }
                            } catch (Exception e3) {
                                e3.printStackTrace();
                            }
                        } finally {
                        }
                    }
                }
                return null;
            } finally {
            }
        }
        return null;
    }

    private static int update(Context context, Uri uri, ContentValues contentValues, String str, String[] strArr) {
        if (isContentProviderInstalled(context, uri)) {
            return context.getContentResolver().update(uri, contentValues, str, strArr);
        }
        android.util.Log.d(TAG, "update-The provider is not installed");
        return 0;
    }

    private static void updateCloudPhoneInfo(Context context, String str) {
        update(context, Uri.withAppendedPath(YellowPageContract.PhoneLookup.CONTENT_URI_CLOUD, str), new ContentValues(), null, null);
    }

    public static void updatePhoneInfo(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        android.util.Log.d(TAG, "updatePhoneInfo updateCloud");
        updateCloudPhoneInfo(context, str);
    }
}
