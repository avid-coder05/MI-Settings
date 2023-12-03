package miui.yellowpage;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import miui.payment.PaymentManager;
import miui.provider.ExtraTelephony;
import miui.yellowpage.Tag;
import miui.yellowpage.YellowPageContract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes4.dex */
public class MiPubUtils {
    private static final int COLUMN_THUMBNAIL_URL = 1;
    private static final int COLUMN_YELLOWPAGE_NAME = 2;
    private static final int COLUMN_YID = 0;
    private static final String EXTRA_MULTI_MODULE_ENTRY_RAW = "com.miui.yellowpage.extra.MULTI_MODULE_ENTRY_RAW";
    private static final String FOLLOW_CONFIRM_IN_YP_DETAIL = "follow_confirm_in_yp_detail";
    private static final String FOLLOW_HAS_CONFIRMED_IN_YP_DETAIL = "follow_has_confirmed_in_yp_detail";
    private static final String MENU_HAS_BEEN_FIRSTLY_READ = "pref_menu_has_been_firstly_read";
    private static final String MENU_READ_IN_SMS_CONVERSATION = "pref_menu_read_in_sms_conversation";
    private static final String MIPUB_DEVICE_ID = "pref_mipub_random_device_id";
    private static final String RANDOM_BASE_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String TAG = "MipubUtils";
    private static final String[] YELLOWPAGE_PROJECTION = {"yid", YellowPageContract.PhoneLookup.THUMBNAIL_URL, YellowPageContract.PhoneLookup.YELLOW_PAGE_NAME};
    private static final Pattern XM_ACCOUNT_PATTERN = Pattern.compile(".*@xiaomi.com(/.*)?");

    /* loaded from: classes4.dex */
    public static class NetworkAccessDeniedException extends Exception {
    }

    private MiPubUtils() {
    }

    private static ArrayList<ModuleIntent> convertBundleToModules(Context context, Bundle bundle, long j, int i) {
        if (bundle.getSerializable(PaymentManager.KEY_INTENT) != null) {
            ArrayList<ModuleIntent> arrayList = new ArrayList<>();
            ArrayList<String> stringArrayList = bundle.getStringArrayList("title");
            ArrayList<Integer> integerArrayList = bundle.getIntegerArrayList("id");
            ArrayList<Integer> integerArrayList2 = bundle.getIntegerArrayList("scope");
            ArrayList<Integer> integerArrayList3 = bundle.getIntegerArrayList("hotId");
            ArrayList<Integer> integerArrayList4 = bundle.getIntegerArrayList("hotShowCount");
            ArrayList parcelableArrayList = bundle.getParcelableArrayList(PaymentManager.KEY_INTENT);
            boolean[] booleanArray = bundle.getBooleanArray("subItemsFlag");
            for (int i2 = 0; i2 < stringArrayList.size(); i2++) {
                if (i == 0 || (integerArrayList2.get(i2).intValue() & i) > 0) {
                    Intent intent = (Intent) parcelableArrayList.get(i2);
                    ModuleIntent moduleIntent = new ModuleIntent(stringArrayList.get(i2), intent, integerArrayList.get(i2).intValue(), booleanArray[i2], integerArrayList3.get(i2).intValue(), integerArrayList4.get(i2).intValue());
                    if (booleanArray[i2]) {
                        try {
                            try {
                                moduleIntent.setSubModuleIntent(getYellowPageMenu(context, new JSONObject(intent.getStringExtra(EXTRA_MULTI_MODULE_ENTRY_RAW)).optString("subItems"), j, i));
                            } catch (JSONException e) {
                                e = e;
                                e.printStackTrace();
                                arrayList.add(moduleIntent);
                            }
                        } catch (JSONException e2) {
                            e = e2;
                        }
                    }
                    arrayList.add(moduleIntent);
                }
            }
            return arrayList;
        }
        return null;
    }

    private static ArrayList<ModuleIntent> convertBundleToModules(Bundle bundle) {
        if (bundle.getSerializable(PaymentManager.KEY_INTENT) != null) {
            ArrayList<ModuleIntent> arrayList = new ArrayList<>();
            ArrayList<String> stringArrayList = bundle.getStringArrayList("title");
            ArrayList<Integer> integerArrayList = bundle.getIntegerArrayList("id");
            ArrayList parcelableArrayList = bundle.getParcelableArrayList(PaymentManager.KEY_INTENT);
            for (int i = 0; i < stringArrayList.size(); i++) {
                arrayList.add(new ModuleIntent(stringArrayList.get(i), (Intent) parcelableArrayList.get(i), integerArrayList.get(i).intValue()));
            }
            return arrayList;
        }
        return null;
    }

    public static YellowPage getCloudYellowPage(Context context, String str) {
        try {
            return getCloudYellowPageThrowException(context, str);
        } catch (NetworkAccessDeniedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static YellowPage getCloudYellowPageThrowException(Context context, String str) throws NetworkAccessDeniedException {
        Bundle invoke = InvocationHandler.invoke(context, YellowPageContract.Method.REQUEST_REMOTE_YELLOWPAGE_BY_MIID, str);
        String string = invoke.getString("status");
        android.util.Log.d(TAG, "getYellowPage:The status is " + string);
        if ("ok".equals(string)) {
            return parseYellowPage(context, invoke.getString("data"));
        }
        if ("network_access_denied".equals(string)) {
            throw new NetworkAccessDeniedException();
        }
        return null;
    }

    public static String getDeviceId(Context context) {
        String string = Settings.System.getString(context.getContentResolver(), MIPUB_DEVICE_ID);
        if (TextUtils.isEmpty(string)) {
            String hashedDeviceInfo = getHashedDeviceInfo(context);
            android.util.Log.d(TAG, "The random device id is " + hashedDeviceInfo);
            Settings.System.putString(context.getContentResolver(), MIPUB_DEVICE_ID, hashedDeviceInfo);
            return hashedDeviceInfo;
        }
        return string;
    }

    private static SharedPreferences getFollowConfirmPref(Context context) {
        return context.getSharedPreferences(FOLLOW_CONFIRM_IN_YP_DETAIL, 0);
    }

    private static String getHashedDeviceInfo(Context context) {
        return InvocationHandler.invoke(context, YellowPageContract.Method.DEVICE_HASHED_DEVICE_INFO).getString("hashed_device_info");
    }

    public static YellowPage getLocalYellowPage(Context context, String str) {
        Cursor query;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        Uri uri = YellowPageContract.YellowPage.CONTENT_URI;
        if (YellowPageUtils.isContentProviderInstalled(context, uri) && (query = context.getContentResolver().query(uri, new String[]{"content"}, "miid=?", new String[]{String.valueOf(str)}, null)) != null) {
            try {
                if (query.moveToFirst()) {
                    return YellowPage.fromJson(query.getString(0));
                }
            } finally {
                query.close();
            }
        }
        return null;
    }

    public static String getLocalYellowPageNavContent(Context context, int i) {
        String str;
        Uri uri = YellowPageContract.NavigationTab.CONTENT_URI;
        if (YellowPageUtils.isContentProviderInstalled(context, uri)) {
            Cursor query = context.getContentResolver().query(uri, new String[]{"value"}, "type=?", new String[]{String.valueOf(i)}, null);
            str = "";
            if (query != null) {
                try {
                    str = query.moveToFirst() ? query.getString(query.getColumnIndex("value")) : "";
                } finally {
                    query.close();
                }
            }
            return str;
        }
        return null;
    }

    @Deprecated
    public static HashMap<String, YellowPage> getLocalYellowPages(final Context context, final Set<String> set) {
        Cursor query;
        if (set == null) {
            return null;
        }
        Uri uri = YellowPageContract.YellowPage.CONTENT_URI;
        if (YellowPageUtils.isContentProviderInstalled(context, uri) && (query = context.getContentResolver().query(uri, new String[]{"miid", "content"}, "miid IS NOT NULL", null, null)) != null) {
            HashMap<String, YellowPage> hashMap = new HashMap<>();
            try {
                android.util.Log.d(TAG, "Query miids count " + set.size());
                while (query.moveToNext()) {
                    String string = query.getString(0);
                    YellowPage fromJson = YellowPage.fromJson(query.getString(1));
                    if (set.contains(string)) {
                        hashMap.put(string, fromJson);
                        set.remove(string);
                    }
                }
                android.util.Log.d(TAG, "Query remote yellowpage by miid with cout " + set.size());
                if (set.size() > 0) {
                    ThreadPool.execute(new Runnable() { // from class: miui.yellowpage.MiPubUtils.1
                        @Override // java.lang.Runnable
                        public void run() {
                            for (String str : set) {
                                if (MiPubUtils.getCloudYellowPage(context, str) == null) {
                                    android.util.Log.e(MiPubUtils.TAG, "No yellowpage matched miid " + str);
                                }
                            }
                        }
                    });
                }
                return hashMap;
            } finally {
                query.close();
            }
        }
        return null;
    }

    private static SharedPreferences getMenuReadInSmsPref(Context context) {
        return context.getSharedPreferences(MENU_READ_IN_SMS_CONVERSATION, 0);
    }

    @Deprecated
    public static YellowPageMipub getMipub(Context context, String str) {
        Cursor query;
        Uri withAppendedPath = Uri.withAppendedPath(YellowPageContract.MiPub.CONTENT_URI_YELLOWPAGE, Uri.encode(str));
        if (YellowPageUtils.isContentProviderInstalled(context, withAppendedPath) && (query = context.getContentResolver().query(withAppendedPath, YELLOWPAGE_PROJECTION, null, null, null)) != null) {
            try {
                if (query.moveToFirst()) {
                    return new YellowPageMipub(str, query.getString(2), query.getLong(0), query.getString(1));
                }
            } finally {
                query.close();
            }
        }
        return null;
    }

    public static String getMipubName(Context context, String str) {
        Cursor query;
        Uri withAppendedPath = Uri.withAppendedPath(YellowPageContract.MiPub.CONTENT_URI_YELLOWPAGE, Uri.encode(str));
        if (YellowPageUtils.isContentProviderInstalled(context, withAppendedPath) && (query = context.getContentResolver().query(withAppendedPath, YELLOWPAGE_PROJECTION, null, null, null)) != null) {
            try {
                if (query.moveToFirst()) {
                    return query.getString(2);
                }
            } finally {
                query.close();
            }
        }
        return null;
    }

    public static Map<String, Service> getRechargeMap(JSONObject jSONObject) {
        HashMap hashMap = new HashMap();
        JSONObject optJSONObject = jSONObject.optJSONObject(Tag.TagServicesData.DATA_RECHARGE);
        if (optJSONObject != null) {
            hashMap.put(Tag.TagServicesData.DATA_RECHARGE, Service.fromJson(optJSONObject));
        }
        JSONObject optJSONObject2 = jSONObject.optJSONObject(Tag.TagServicesData.PHONE_RECHAGE);
        if (optJSONObject2 != null) {
            hashMap.put(Tag.TagServicesData.PHONE_RECHAGE, Service.fromJson(optJSONObject2));
        }
        return hashMap;
    }

    public static String getRemoteYellowPageNavContent(Context context, int i) {
        Bundle invoke = InvocationHandler.invoke(context, YellowPageContract.Method.REQUEST_REMOTE_NAVIGATION_INFO, String.valueOf(i));
        if ("ok".equals(invoke.getString("status"))) {
            String string = invoke.getString("data");
            Uri uri = YellowPageContract.NavigationTab.CONTENT_URI;
            if (YellowPageUtils.isContentProviderInstalled(context, uri)) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("value", string);
                if (context.getContentResolver().update(uri, contentValues, "type = ? ", new String[]{String.valueOf(i)}) < 1) {
                    context.getContentResolver().insert(uri, contentValues);
                    return string;
                }
                return string;
            }
            return string;
        }
        return "";
    }

    public static String getSearchHint(JSONObject jSONObject) {
        return jSONObject.optString(Tag.TagServicesData.SEARCH_HINT);
    }

    public static List<ServicesDataEntry> getServicesList(String str) {
        ArrayList arrayList = new ArrayList();
        try {
            JSONArray jSONArray = new JSONObject(str).getJSONArray(Tag.TagServicesData.NAV_GROUPS);
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(ServicesDataEntry.fromJson(jSONArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            android.util.Log.e(TAG, "", e);
        }
        return arrayList;
    }

    public static List<ServicesDataEntry> getServicesList(JSONObject jSONObject) {
        ArrayList arrayList = new ArrayList();
        try {
            JSONArray jSONArray = jSONObject.getJSONArray(Tag.TagServicesData.NAV_GROUPS);
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(ServicesDataEntry.fromJson(jSONArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            android.util.Log.e(TAG, "", e);
        }
        return arrayList;
    }

    public static boolean getShowPhoneRecharge(JSONObject jSONObject) {
        return jSONObject.optBoolean(Tag.TagServicesData.SHOW_PHONE_RECHARGE, false);
    }

    public static YellowPage getYellowPage(Context context, String str) {
        YellowPage localYellowPage = getLocalYellowPage(context, str);
        return localYellowPage == null ? getCloudYellowPage(context, str) : localYellowPage;
    }

    @Deprecated
    public static ArrayList<ModuleIntent> getYellowPageMenu(Context context, long j, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", j);
        bundle.putBoolean(YellowPageContract.Search.REMOTE_SEARCH, z);
        bundle.putBoolean("hasScope", false);
        return convertBundleToModules(InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_MENU, null, bundle));
    }

    @Deprecated
    public static ArrayList<ModuleIntent> getYellowPageMenu(Context context, long j, boolean z, int i) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", j);
        bundle.putBoolean(YellowPageContract.Search.REMOTE_SEARCH, z);
        bundle.putBoolean("hasScope", true);
        return convertBundleToModules(context, InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_MENU, null, bundle), j, i);
    }

    @Deprecated
    public static ArrayList<ModuleIntent> getYellowPageMenu(Context context, String str, long j) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", j);
        bundle.putString("jsonString", str);
        return convertBundleToModules(InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_PARSE_MENU, null, bundle));
    }

    @Deprecated
    public static ArrayList<ModuleIntent> getYellowPageMenu(Context context, String str, long j, int i) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", j);
        bundle.putString("jsonString", str);
        return convertBundleToModules(context, InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_PARSE_SUB_MENU, null, bundle), j, i);
    }

    @Deprecated
    public static YellowPage getYellowPageThrowException(Context context, String str) throws NetworkAccessDeniedException {
        YellowPage localYellowPage = getLocalYellowPage(context, str);
        return localYellowPage == null ? getCloudYellowPageThrowException(context, str) : localYellowPage;
    }

    @Deprecated
    public static boolean isFollowConfirmed(Context context) {
        return getFollowConfirmPref(context).getBoolean(FOLLOW_HAS_CONFIRMED_IN_YP_DETAIL, false);
    }

    private static boolean isNumeric(String str) {
        return Pattern.compile("[0-9]*").matcher(str).matches();
    }

    public static boolean isServiceNumber(Context context, String str) {
        return isServiceNumber(str) || isXiaomiJID(str);
    }

    private static boolean isServiceNumber(String str) {
        try {
            String str2 = ExtraTelephony.PrefixCode;
            Method declaredMethod = ExtraTelephony.class.getDeclaredMethod("isServiceNumber", String.class);
            declaredMethod.setAccessible(true);
            return ((Boolean) declaredMethod.invoke(null, str)).booleanValue();
        } catch (Exception e) {
            android.util.Log.w(TAG, "invoke ExtraTelephony.isServiceNumber failed", e);
            return false;
        }
    }

    public static boolean isSmsMenuRead(Context context, long j) {
        SharedPreferences menuReadInSmsPref = getMenuReadInSmsPref(context);
        return menuReadInSmsPref.getBoolean(String.valueOf(j), menuReadInSmsPref.getBoolean(MENU_HAS_BEEN_FIRSTLY_READ, false));
    }

    public static boolean isXiaomiAccount(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return XM_ACCOUNT_PATTERN.matcher(str).matches();
    }

    private static boolean isXiaomiJID(String str) {
        if (isXiaomiAccount(str)) {
            return isNumeric(trimDomainSuffix(str));
        }
        return false;
    }

    public static boolean isYellowPageNetworkAllowed(Context context) {
        return InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_IS_NETWORK_ALLOWED, null, null).getBoolean("networkAllowed", false);
    }

    private static YellowPage parseYellowPage(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_INSERT, jSONObject.getString(Tag.TagYellowPage.KEY));
            return YellowPage.fromJson(jSONObject.getString(Tag.TagYellowPage.KEY));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final String randomString(int i) {
        StringBuilder sb = new StringBuilder(i);
        Random random = new Random();
        for (int i2 = 0; i2 < i; i2++) {
            sb.append(RANDOM_BASE_STRING.charAt(random.nextInt(62)));
        }
        return sb.toString();
    }

    @Deprecated
    public static void setFollowConfirmed(Context context, boolean z) {
        getFollowConfirmPref(context).edit().putBoolean(FOLLOW_HAS_CONFIRMED_IN_YP_DETAIL, z).commit();
    }

    public static void setSmsMenuRead(Context context, long j, boolean z) {
        SharedPreferences menuReadInSmsPref = getMenuReadInSmsPref(context);
        menuReadInSmsPref.edit().putBoolean(String.valueOf(j), z).commit();
        menuReadInSmsPref.edit().putBoolean(MENU_HAS_BEEN_FIRSTLY_READ, true).commit();
    }

    public static boolean setYellowPageNetworkAllowed(Context context) {
        return InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_SET_NETWORK_ALLOWED, null, null).getBoolean("networkAllowed", false);
    }

    public static String trimDomainSuffix(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        int indexOf = str.indexOf("@");
        return indexOf > 0 ? str.substring(0, indexOf) : str;
    }
}
