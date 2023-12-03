package miui.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;

/* loaded from: classes3.dex */
public final class ExtraNetwork {
    private static final String ACTION_NETWORK_ASSISTANT_SMS_REPORT = "miui.intent.action.NETWORKASSISTANT_SMS_REPORT";
    public static final String ACTION_NETWORK_BLOCKED = "miui.intent.action.NETWORK_BLOCKED";
    public static final String ACTION_NETWORK_CONNECTED = "miui.intent.action.NETWORK_CONNECTED";
    private static final String ACTION_TRAFFIC_SETTING = "miui.intent.action.NETWORKASSISTANT_OPERATOR_SETTING";
    private static final String ACTION_TRAFFIC_SETTING_INTERNATIONAL = "miui.intent.action.NETWORKASSISTANT_MONTH_PACKAGE_SETTING";
    public static final String BUNDLE_KEY_COMMON = "bundle_key_com";
    public static final String BUNDLE_KEY_HAS_MENU = "bundle_key_has_menu";
    private static final String BUNDLE_KEY_OTHER_APP = "bundle_key_from_other_task";
    public static final String BUNDLE_KEY_PURCHASE_FROM = "bundle_key_purchase_from";
    public static final String BUNDLE_KEY_SLOTID = "bundle_key_slotid";
    private static final String BUNDLE_KEY_SLOT_ID = "sim_slot_num_tag";
    public static final String BUNDLE_KEY_TITLE = "bundle_key_title";
    public static final String BUNDLE_KEY_URL = "bundle_key_url";
    private static final String COLUMN_NAME_MONTH_USED = "month_used";
    private static final String COLUMN_NAME_MONTH_WARNING = "month_warning";
    private static final String COLUMN_NAME_PACKAGE_REMAINED = "package_remained";
    private static final String COLUMN_NAME_PACKAGE_TOTAL = "package_total";
    private static final String COLUMN_NAME_PACKAGE_USED = "package_used";
    private static final String COLUMN_NAME_SLOT_NUM = "slot_num";
    private static final String COLUMN_NAME_SUPPORT = "package_setted";
    private static final String COLUMN_NAME_TODAY_USED = "today_used";
    private static final String COLUMN_NAME_TOTAL_LIMIT = "total_limit";
    public static final int CORRECTION_TYPE_BILL = 2;
    public static final int CORRECTION_TYPE_CALLTIME = 4;
    public static final int CORRECTION_TYPE_TRAFFIC = 1;
    private static final String EXTRA_MIUI_STARTING_WINDOW_LABEL = ":miui:starting_window_label";
    public static final String FIREWALL_MOBILE_RULE = "mobile_rule";
    private static final String FIREWALL_MOBILE_RULE_SLOTNUM = "mobile_rule_slot";
    public static final String FIREWALL_PACKAGE_NAME = "package_name";
    private static final String FIREWALL_SOURCE_PACKAGE_NAME = "source_package_name";
    private static final String FIREWALL_TEMP_MOBILE_RULE = "temp_mobile_rule";
    private static final String FIREWALL_TEMP_MOBILE_RULE_SLOTNUM = "temp_mobile_rule_slot";
    private static final String FIREWALL_TEMP_WIFI_RULE = "temp_wifi_rule";
    private static final String FIREWALL_URI_STR = "content://com.miui.networkassistant.provider/firewall/%s";
    public static final String FIREWALL_WIFI_RULE = "wifi_rule";
    public static final String FROM_PKGNAME = "from_pkgname";
    public static final String IMSI = "imsi";
    private static final String KEY_CORRECTION_TYPE = "correction_type";
    private static final String MOBILE_FIREWALL_URI_STR = "content://com.miui.networkassistant.provider/mobile_firewall/%s/%s";
    public static final String MOBILE_RXBYTES = "mobile_rxbytes";
    public static final String MOBILE_TXBYTES = "mobile_txbytes";
    private static final String NETWORKASSISTANT_PURCHASE_ACTION = "miui.intent.action.NETWORKASSISTANT_TRAFFIC_PURCHASE";
    public static final String STORAGE_TIME = "storage_time";
    private static final String TAG = "ExtraNetwork";
    private static final String TEMP_MOBILE_FIREWALL_URI_STR = "content://com.miui.networkassistant.provider/temp_mobile_firewall/%s/%s";
    private static final String TEMP_WIFI_FIREWALL_URI_STR = "content://com.miui.networkassistant.provider/temp_wifi_firewall/%s";
    public static final String TO_PKGNAME = "to_pkgname";
    public static final String TRACK_PURCHASE_FROM_LOCK_SCREEN_TRAFFIC = "100010";
    public static final String TRACK_PURCHASE_FROM_NETWORK_ASSISTANT_MAIN_PAGE = "100002";
    public static final String TRACK_PURCHASE_FROM_NETWORK_ASSISTANT_MAIN_TOOLBAR = "100001";
    public static final String TRACK_PURCHASE_FROM_PUSH = "100007";
    public static final String TRACK_PURCHASE_FROM_SERCURITY_CENTER_EXAM = "100008";
    public static final String TRACK_PURCHASE_FROM_STATUS_BAR = "100003";
    public static final String TRACK_PURCHASE_FROM_TRAFFIC_OVER_LIMIT_DIALOG = "100006";
    public static final String TRACK_PURCHASE_FROM_TRAFFIC_OVER_LIMIT_NOTIFY = "100005";
    public static final String TRACK_PURCHASE_FROM_TRAFFIC_SORTED = "100009";
    public static final String TRACK_PURCHASE_FROM_TRAFFIC_WARNING_NOTIFY = "100004";
    private static final String TRAFFIC_DISTRIBUTION_URI_STR = "content://com.miui.networkassistant.provider/traffic_distribution";
    public static final String TRAFFIC_PURCHASE_ENABLED = "traffic_purchase_enabled";
    private static final String TRAFFIC_PURCHASE_STATUS_URI_STR = "content://com.miui.networkassistant.provider/na_traffic_purchase";
    private static final String TRAFFIC_PURCHASE_STATUS_URI_STR_ISMI = "content://com.miui.networkassistant.provider/na_traffic_purchase/slotId/%d";
    private static final String URI_BILL_PACKAGE_DETAIL = "content://com.miui.networkassistant.provider/bill_detail";
    private static final String URI_CALL_TIME_PACKAGE_DETAIL = "content://com.miui.networkassistant.provider/calltime_detail";
    private static final String URI_NETWORK_TRAFFIC_INFO = "content://com.miui.networkassistant.provider/datausage_status";
    private static final String URI_SMS_CORRECTION = "content://com.miui.networkassistant.provider/sms_correction";
    private static final String WIFI_FIREWALL_URI_STR = "content://com.miui.networkassistant.provider/wifi_firewall/%s";
    public static final String WIFI_RXBYTES = "wifi_rxbytes";
    public static final String WIFI_TXBYTES = "wifi_txbytes";

    /* loaded from: classes3.dex */
    public static final class DataUsageDetail {
        public long monthTotal;
        public long monthUsed;
        public long monthWarning;
        public long todayUsed;

        public DataUsageDetail(long j, long j2, long j3, long j4) {
            this.monthTotal = j;
            this.monthUsed = j2;
            this.monthWarning = j3;
            this.todayUsed = j4;
        }

        public String toString() {
            return String.format("monthTotal:%s, monthUsed:%s, monthWarning:%s, todayUsed:%s", Long.valueOf(this.monthTotal), Long.valueOf(this.monthUsed), Long.valueOf(this.monthWarning), Long.valueOf(this.todayUsed));
        }
    }

    /* loaded from: classes3.dex */
    public static final class PackageDetail {
        public boolean isSupport;
        public long packageRemained;
        public long packageTotal;
        public long packageUsed;
        public int slotNum;

        public PackageDetail(long j, long j2, long j3, int i, boolean z) {
            this.packageTotal = j;
            this.packageUsed = j2;
            this.packageRemained = j3;
            this.slotNum = i;
            this.isSupport = z;
        }

        public String toString() {
            return String.format("packageTotal:%s, packageUsed:%s, packageRemained:%s, slotNum:%s, isSupport:%s", Long.valueOf(this.packageTotal), Long.valueOf(this.packageUsed), Long.valueOf(this.packageRemained), Integer.valueOf(this.slotNum), Boolean.valueOf(this.isSupport));
        }
    }

    public static List<PackageDetail> getBillPackageDetail(Context context) {
        ArrayList arrayList = new ArrayList();
        Cursor cursor = null;
        try {
            try {
                cursor = context.getContentResolver().query(Uri.parse(URI_BILL_PACKAGE_DETAIL), null, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        arrayList.add(new PackageDetail(cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_PACKAGE_TOTAL)), cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_PACKAGE_USED)), cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_PACKAGE_REMAINED)), cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_SLOT_NUM)), "true".equals(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SUPPORT)))));
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
                return arrayList;
            } catch (Exception e) {
                Log.e(TAG, "getBillPackageDetail", e);
                if (cursor != null) {
                    cursor.close();
                }
                return arrayList;
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public static List<PackageDetail> getCallTimePackageDetail(Context context) {
        ArrayList arrayList = new ArrayList();
        Cursor cursor = null;
        try {
            try {
                cursor = context.getContentResolver().query(Uri.parse(URI_CALL_TIME_PACKAGE_DETAIL), null, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        arrayList.add(new PackageDetail(cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_PACKAGE_TOTAL)), cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_PACKAGE_USED)), cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_PACKAGE_REMAINED)), cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_SLOT_NUM)), "true".equals(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SUPPORT)))));
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
                return arrayList;
            } catch (Exception e) {
                Log.e(TAG, "getCallTimePackageDetail", e);
                if (cursor != null) {
                    cursor.close();
                }
                return arrayList;
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0055, code lost:
    
        if (r12 != null) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0066, code lost:
    
        if (r12 == null) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0068, code lost:
    
        r12.close();
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r12v0, types: [android.content.Context] */
    /* JADX WARN: Type inference failed for: r12v1, types: [android.database.Cursor] */
    /* JADX WARN: Type inference failed for: r12v3 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static miui.provider.ExtraNetwork.DataUsageDetail getUserDataUsageDetail(android.content.Context r12) {
        /*
            r0 = 0
            if (r12 == 0) goto L73
            java.lang.String r1 = "content://com.miui.networkassistant.provider/datausage_status"
            android.net.Uri r3 = android.net.Uri.parse(r1)     // Catch: java.lang.Throwable -> L58 java.lang.Exception -> L5d
            android.content.ContentResolver r2 = r12.getContentResolver()     // Catch: java.lang.Throwable -> L58 java.lang.Exception -> L5d
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            android.database.Cursor r12 = r2.query(r3, r4, r5, r6, r7)     // Catch: java.lang.Throwable -> L58 java.lang.Exception -> L5d
            if (r12 == 0) goto L55
            boolean r1 = r12.moveToFirst()     // Catch: java.lang.Exception -> L53 java.lang.Throwable -> L6c
            if (r1 == 0) goto L55
            java.lang.String r1 = "total_limit"
            int r1 = r12.getColumnIndex(r1)     // Catch: java.lang.Exception -> L53 java.lang.Throwable -> L6c
            long r3 = r12.getLong(r1)     // Catch: java.lang.Exception -> L53 java.lang.Throwable -> L6c
            java.lang.String r1 = "month_used"
            int r1 = r12.getColumnIndex(r1)     // Catch: java.lang.Exception -> L53 java.lang.Throwable -> L6c
            long r5 = r12.getLong(r1)     // Catch: java.lang.Exception -> L53 java.lang.Throwable -> L6c
            java.lang.String r1 = "month_warning"
            int r1 = r12.getColumnIndex(r1)     // Catch: java.lang.Exception -> L53 java.lang.Throwable -> L6c
            long r7 = r12.getLong(r1)     // Catch: java.lang.Exception -> L53 java.lang.Throwable -> L6c
            java.lang.String r1 = "today_used"
            int r1 = r12.getColumnIndex(r1)     // Catch: java.lang.Exception -> L53 java.lang.Throwable -> L6c
            long r9 = r12.getLong(r1)     // Catch: java.lang.Exception -> L53 java.lang.Throwable -> L6c
            miui.provider.ExtraNetwork$DataUsageDetail r1 = new miui.provider.ExtraNetwork$DataUsageDetail     // Catch: java.lang.Exception -> L53 java.lang.Throwable -> L6c
            r2 = r1
            r2.<init>(r3, r5, r7, r9)     // Catch: java.lang.Exception -> L53 java.lang.Throwable -> L6c
            r12.close()
            return r1
        L53:
            r1 = move-exception
            goto L5f
        L55:
            if (r12 == 0) goto L73
            goto L68
        L58:
            r12 = move-exception
            r11 = r0
            r0 = r12
            r12 = r11
            goto L6d
        L5d:
            r1 = move-exception
            r12 = r0
        L5f:
            java.lang.String r2 = "ExtraNetwork"
            java.lang.String r3 = "getUserDataUsageDetail"
            android.util.Log.e(r2, r3, r1)     // Catch: java.lang.Throwable -> L6c
            if (r12 == 0) goto L73
        L68:
            r12.close()
            goto L73
        L6c:
            r0 = move-exception
        L6d:
            if (r12 == 0) goto L72
            r12.close()
        L72:
            throw r0
        L73:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.provider.ExtraNetwork.getUserDataUsageDetail(android.content.Context):miui.provider.ExtraNetwork$DataUsageDetail");
    }

    @Deprecated
    public static boolean insertTrafficDistribution(Context context, String str, long j, long j2, long j3, long j4) {
        if (context != null && !TextUtils.isEmpty(str)) {
            try {
                Uri parse = Uri.parse(TRAFFIC_DISTRIBUTION_URI_STR);
                if (parse != null) {
                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
                    String subscriberId = telephonyManager != null ? telephonyManager.getSubscriberId() : "";
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(FROM_PKGNAME, context.getPackageName());
                    contentValues.put(TO_PKGNAME, str);
                    contentValues.put(MOBILE_RXBYTES, Long.valueOf(j4));
                    contentValues.put(MOBILE_TXBYTES, Long.valueOf(j3));
                    contentValues.put(WIFI_RXBYTES, Long.valueOf(j2));
                    contentValues.put(WIFI_TXBYTES, Long.valueOf(j));
                    contentValues.put("imsi", subscriberId);
                    contentValues.put(STORAGE_TIME, Long.valueOf(System.currentTimeMillis()));
                    if (!TextUtils.isEmpty(context.getContentResolver().insert(parse, contentValues).getLastPathSegment())) {
                        return true;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "insertTrafficDistribution", e);
            }
        }
        return false;
    }

    public static boolean isMobileRestrict(Context context, String str) {
        return isMobileRestrict(context, str, -1);
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x004a, code lost:
    
        if (r1 != null) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0057, code lost:
    
        if (r1 == null) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0059, code lost:
    
        r1.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x005c, code lost:
    
        return false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean isMobileRestrict(android.content.Context r9, java.lang.String r10, int r11) {
        /*
            r0 = 0
            if (r9 == 0) goto L63
            boolean r1 = android.text.TextUtils.isEmpty(r10)
            if (r1 == 0) goto La
            goto L63
        La:
            r1 = 0
            java.lang.String r2 = "content://com.miui.networkassistant.provider/mobile_firewall/%s/%s"
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            r3[r0] = r11     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            r11 = 1
            r3[r11] = r10     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            java.lang.String r2 = java.lang.String.format(r2, r3)     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            android.net.Uri r4 = android.net.Uri.parse(r2)     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            android.content.ContentResolver r3 = r9.getContentResolver()     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            r5 = 0
            r6 = 0
            java.lang.String[] r7 = new java.lang.String[r11]     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            r7[r0] = r10     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            r8 = 0
            android.database.Cursor r1 = r3.query(r4, r5, r6, r7, r8)     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            if (r1 == 0) goto L4a
            boolean r9 = r1.moveToFirst()     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            if (r9 == 0) goto L4a
            java.lang.String r9 = "mobile_rule"
            int r9 = r1.getColumnIndex(r9)     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            int r9 = r1.getInt(r9)     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            if (r9 != r11) goto L46
            r0 = r11
        L46:
            r1.close()
            return r0
        L4a:
            if (r1 == 0) goto L5c
            goto L59
        L4d:
            r9 = move-exception
            goto L5d
        L4f:
            r9 = move-exception
            java.lang.String r10 = "ExtraNetwork"
            java.lang.String r11 = "isMobileRestrict"
            android.util.Log.e(r10, r11, r9)     // Catch: java.lang.Throwable -> L4d
            if (r1 == 0) goto L5c
        L59:
            r1.close()
        L5c:
            return r0
        L5d:
            if (r1 == 0) goto L62
            r1.close()
        L62:
            throw r9
        L63:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.provider.ExtraNetwork.isMobileRestrict(android.content.Context, java.lang.String, int):boolean");
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x004a, code lost:
    
        if (r1 != null) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0057, code lost:
    
        if (r1 == null) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0059, code lost:
    
        r1.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x005c, code lost:
    
        return false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean isMobileTempRestrict(android.content.Context r9, java.lang.String r10, int r11) {
        /*
            r0 = 0
            if (r9 == 0) goto L63
            boolean r1 = android.text.TextUtils.isEmpty(r10)
            if (r1 == 0) goto La
            goto L63
        La:
            r1 = 0
            java.lang.String r2 = "content://com.miui.networkassistant.provider/temp_mobile_firewall/%s/%s"
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            r3[r0] = r11     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            r11 = 1
            r3[r11] = r10     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            java.lang.String r2 = java.lang.String.format(r2, r3)     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            android.net.Uri r4 = android.net.Uri.parse(r2)     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            android.content.ContentResolver r3 = r9.getContentResolver()     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            r5 = 0
            r6 = 0
            java.lang.String[] r7 = new java.lang.String[r11]     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            r7[r0] = r10     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            r8 = 0
            android.database.Cursor r1 = r3.query(r4, r5, r6, r7, r8)     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            if (r1 == 0) goto L4a
            boolean r9 = r1.moveToFirst()     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            if (r9 == 0) goto L4a
            java.lang.String r9 = "temp_mobile_rule"
            int r9 = r1.getColumnIndex(r9)     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            int r9 = r1.getInt(r9)     // Catch: java.lang.Throwable -> L4d java.lang.Exception -> L4f
            if (r9 != r11) goto L46
            r0 = r11
        L46:
            r1.close()
            return r0
        L4a:
            if (r1 == 0) goto L5c
            goto L59
        L4d:
            r9 = move-exception
            goto L5d
        L4f:
            r9 = move-exception
            java.lang.String r10 = "ExtraNetwork"
            java.lang.String r11 = "isMobileTempRestrict"
            android.util.Log.e(r10, r11, r9)     // Catch: java.lang.Throwable -> L4d
            if (r1 == 0) goto L5c
        L59:
            r1.close()
        L5c:
            return r0
        L5d:
            if (r1 == 0) goto L62
            r1.close()
        L62:
            throw r9
        L63:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.provider.ExtraNetwork.isMobileTempRestrict(android.content.Context, java.lang.String, int):boolean");
    }

    public static boolean isTrafficPurchaseSupported(Context context) {
        if (context != null) {
            try {
                return queryTrafficPurchaseStatus(context, Uri.parse(TRAFFIC_PURCHASE_STATUS_URI_STR));
            } catch (Exception e) {
                Log.e(TAG, "isTrafficPurchaseSupported", e);
                return false;
            }
        }
        return false;
    }

    public static boolean isTrafficPurchaseSupported(Context context, int i) {
        if (context != null && i >= 0 && i < 2) {
            try {
                return queryTrafficPurchaseStatus(context, Uri.parse(String.format(TRAFFIC_PURCHASE_STATUS_URI_STR_ISMI, Integer.valueOf(i))));
            } catch (Exception e) {
                Log.e(TAG, "isTrafficPurchaseSupported", e);
            }
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x0043, code lost:
    
        if (r1 != null) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0050, code lost:
    
        if (r1 == null) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0052, code lost:
    
        r1.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0055, code lost:
    
        return false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean isWifiRestrict(android.content.Context r10, java.lang.String r11) {
        /*
            r0 = 0
            if (r10 == 0) goto L5c
            boolean r1 = android.text.TextUtils.isEmpty(r11)
            if (r1 == 0) goto La
            goto L5c
        La:
            r1 = 0
            java.lang.String r2 = "content://com.miui.networkassistant.provider/wifi_firewall/%s"
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            r4[r0] = r11     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            java.lang.String r2 = java.lang.String.format(r2, r4)     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            android.net.Uri r5 = android.net.Uri.parse(r2)     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            android.content.ContentResolver r4 = r10.getContentResolver()     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            r6 = 0
            r7 = 0
            java.lang.String[] r8 = new java.lang.String[r3]     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            r8[r0] = r11     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            r9 = 0
            android.database.Cursor r1 = r4.query(r5, r6, r7, r8, r9)     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            if (r1 == 0) goto L43
            boolean r10 = r1.moveToFirst()     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            if (r10 == 0) goto L43
            java.lang.String r10 = "wifi_rule"
            int r10 = r1.getColumnIndex(r10)     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            int r10 = r1.getInt(r10)     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            if (r10 != r3) goto L3f
            r0 = r3
        L3f:
            r1.close()
            return r0
        L43:
            if (r1 == 0) goto L55
            goto L52
        L46:
            r10 = move-exception
            goto L56
        L48:
            r10 = move-exception
            java.lang.String r11 = "ExtraNetwork"
            java.lang.String r2 = "isWifiRestrict"
            android.util.Log.e(r11, r2, r10)     // Catch: java.lang.Throwable -> L46
            if (r1 == 0) goto L55
        L52:
            r1.close()
        L55:
            return r0
        L56:
            if (r1 == 0) goto L5b
            r1.close()
        L5b:
            throw r10
        L5c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.provider.ExtraNetwork.isWifiRestrict(android.content.Context, java.lang.String):boolean");
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x0043, code lost:
    
        if (r1 != null) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0050, code lost:
    
        if (r1 == null) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0052, code lost:
    
        r1.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0055, code lost:
    
        return false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean isWifiTempRestrict(android.content.Context r10, java.lang.String r11) {
        /*
            r0 = 0
            if (r10 == 0) goto L5c
            boolean r1 = android.text.TextUtils.isEmpty(r11)
            if (r1 == 0) goto La
            goto L5c
        La:
            r1 = 0
            java.lang.String r2 = "content://com.miui.networkassistant.provider/temp_wifi_firewall/%s"
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            r4[r0] = r11     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            java.lang.String r2 = java.lang.String.format(r2, r4)     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            android.net.Uri r5 = android.net.Uri.parse(r2)     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            android.content.ContentResolver r4 = r10.getContentResolver()     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            r6 = 0
            r7 = 0
            java.lang.String[] r8 = new java.lang.String[r3]     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            r8[r0] = r11     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            r9 = 0
            android.database.Cursor r1 = r4.query(r5, r6, r7, r8, r9)     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            if (r1 == 0) goto L43
            boolean r10 = r1.moveToFirst()     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            if (r10 == 0) goto L43
            java.lang.String r10 = "temp_wifi_rule"
            int r10 = r1.getColumnIndex(r10)     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            int r10 = r1.getInt(r10)     // Catch: java.lang.Throwable -> L46 java.lang.Exception -> L48
            if (r10 != r3) goto L3f
            r0 = r3
        L3f:
            r1.close()
            return r0
        L43:
            if (r1 == 0) goto L55
            goto L52
        L46:
            r10 = move-exception
            goto L56
        L48:
            r10 = move-exception
            java.lang.String r11 = "ExtraNetwork"
            java.lang.String r2 = "isWifiTempRestrict"
            android.util.Log.e(r11, r2, r10)     // Catch: java.lang.Throwable -> L46
            if (r1 == 0) goto L55
        L52:
            r1.close()
        L55:
            return r0
        L56:
            if (r1 == 0) goto L5b
            r1.close()
        L5b:
            throw r10
        L5c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.provider.ExtraNetwork.isWifiTempRestrict(android.content.Context, java.lang.String):boolean");
    }

    public static void navigateToOperatorSettingActivity(Context context, int i) {
        Intent intent = Build.IS_INTERNATIONAL_BUILD ? new Intent(ACTION_TRAFFIC_SETTING_INTERNATIONAL) : new Intent(ACTION_TRAFFIC_SETTING);
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_SLOT_ID, i);
        bundle.putBoolean(BUNDLE_KEY_OTHER_APP, true);
        intent.putExtras(bundle);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    public static void navigateToRichWebActivity(Context context, String str, String str2, boolean z, String str3, boolean z2) {
        Intent intent = new Intent(NETWORKASSISTANT_PURCHASE_ACTION);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_URL, str);
        bundle.putString(BUNDLE_KEY_TITLE, str2);
        bundle.putBoolean(BUNDLE_KEY_HAS_MENU, z);
        bundle.putString(BUNDLE_KEY_PURCHASE_FROM, str3);
        intent.putExtra(BUNDLE_KEY_COMMON, bundle);
        intent.putExtra(EXTRA_MIUI_STARTING_WINDOW_LABEL, str2);
        if (z2) {
            intent.addFlags(268435456);
        }
        context.startActivity(intent);
    }

    public static void navigateToSmsReportActivity(Context context, int i, int i2) {
        Intent intent = new Intent(ACTION_NETWORK_ASSISTANT_SMS_REPORT);
        intent.putExtra(BUNDLE_KEY_SLOT_ID, i);
        intent.putExtra(KEY_CORRECTION_TYPE, i2);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    @Deprecated
    public static void navigateToTrafficPurchasePage(Context context) {
        Intent intent = new Intent(NETWORKASSISTANT_PURCHASE_ACTION);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    @Deprecated
    public static void navigateToTrafficPurchasePage(Context context, int i) {
        Intent intent = new Intent(NETWORKASSISTANT_PURCHASE_ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_SLOTID, i);
        intent.putExtra(BUNDLE_KEY_COMMON, bundle);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    public static void navigateToTrafficPurchasePage(Context context, int i, String str) {
        Intent intent = new Intent(NETWORKASSISTANT_PURCHASE_ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_SLOTID, i);
        bundle.putString(BUNDLE_KEY_PURCHASE_FROM, str);
        intent.putExtra(BUNDLE_KEY_COMMON, bundle);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    public static void navigateToTrafficPurchasePage(Context context, String str) {
        Intent intent = new Intent(NETWORKASSISTANT_PURCHASE_ACTION);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_PURCHASE_FROM, str);
        intent.putExtra(BUNDLE_KEY_COMMON, bundle);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x0039, code lost:
    
        if (r0 == null) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x003b, code lost:
    
        r0.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0045, code lost:
    
        if (r0 != null) goto L15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0048, code lost:
    
        return r1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static boolean queryTrafficPurchaseStatus(android.content.Context r8, android.net.Uri r9) {
        /*
            r0 = 0
            r1 = 0
            if (r9 == 0) goto L45
            android.content.ContentResolver r2 = r8.getContentResolver()     // Catch: java.lang.Throwable -> L2e java.lang.Exception -> L30
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r3 = r9
            android.database.Cursor r0 = r2.query(r3, r4, r5, r6, r7)     // Catch: java.lang.Throwable -> L2e java.lang.Exception -> L30
            if (r0 == 0) goto L45
            boolean r8 = r0.moveToFirst()     // Catch: java.lang.Throwable -> L2e java.lang.Exception -> L30
            if (r8 == 0) goto L45
            java.lang.String r8 = "traffic_purchase_enabled"
            int r8 = r0.getColumnIndex(r8)     // Catch: java.lang.Throwable -> L2e java.lang.Exception -> L30
            java.lang.String r8 = r0.getString(r8)     // Catch: java.lang.Throwable -> L2e java.lang.Exception -> L30
            java.lang.Boolean r8 = java.lang.Boolean.valueOf(r8)     // Catch: java.lang.Throwable -> L2e java.lang.Exception -> L30
            boolean r8 = r8.booleanValue()     // Catch: java.lang.Throwable -> L2e java.lang.Exception -> L30
            r1 = r8
            goto L45
        L2e:
            r8 = move-exception
            goto L3f
        L30:
            r8 = move-exception
            java.lang.String r9 = "ExtraNetwork"
            java.lang.String r2 = "queryTrafficPurchaseStatus"
            android.util.Log.e(r9, r2, r8)     // Catch: java.lang.Throwable -> L2e
            if (r0 == 0) goto L48
        L3b:
            r0.close()
            goto L48
        L3f:
            if (r0 == 0) goto L44
            r0.close()
        L44:
            throw r8
        L45:
            if (r0 == 0) goto L48
            goto L3b
        L48:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.provider.ExtraNetwork.queryTrafficPurchaseStatus(android.content.Context, android.net.Uri):boolean");
    }

    private static void registerContentObserver(Context context, String str, ContentObserver contentObserver) {
        try {
            Method declaredMethod = Class.forName("android.content.ContentResolver").getDeclaredMethod("registerContentObserver", Uri.class, Boolean.TYPE, ContentObserver.class, Integer.TYPE);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(context.getContentResolver(), Uri.parse(str), Boolean.TRUE, contentObserver, 0);
        } catch (Exception e) {
            Log.e(TAG, "registerContentObserver error", e);
        }
    }

    public static void registerFirewallContentObserver(Context context, ContentObserver contentObserver) {
        registerContentObserver(context, String.format(FIREWALL_URI_STR, ""), contentObserver);
    }

    public static void registerPackageContentObserver(Context context, ContentObserver contentObserver, int i) {
        registerContentObserver(context, i == 2 ? URI_BILL_PACKAGE_DETAIL : i == 4 ? URI_CALL_TIME_PACKAGE_DETAIL : URI_NETWORK_TRAFFIC_INFO, contentObserver);
    }

    public static boolean setMobileRestrict(Context context, String str, boolean z) {
        return setMobileRestrict(context, str, z, -1);
    }

    public static boolean setMobileRestrict(Context context, String str, boolean z, int i) {
        try {
            Uri parse = Uri.parse(String.format(MOBILE_FIREWALL_URI_STR, Integer.valueOf(i), str));
            if (parse != null) {
                ContentResolver contentResolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(FIREWALL_MOBILE_RULE_SLOTNUM, Integer.valueOf(i));
                contentValues.put(FIREWALL_MOBILE_RULE, Boolean.valueOf(z));
                contentValues.put(FIREWALL_SOURCE_PACKAGE_NAME, context.getPackageName());
                return contentResolver.update(parse, contentValues, null, null) == 1;
            }
        } catch (Exception e) {
            Log.e(TAG, "setMobileTempRestrict", e);
        }
        return false;
    }

    public static boolean setMobileTempRestrict(Context context, String str, int i, boolean z) {
        try {
            Uri parse = Uri.parse(String.format(TEMP_MOBILE_FIREWALL_URI_STR, Integer.valueOf(i), str));
            if (parse != null) {
                ContentResolver contentResolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(FIREWALL_TEMP_MOBILE_RULE_SLOTNUM, Integer.valueOf(i));
                contentValues.put(FIREWALL_TEMP_MOBILE_RULE, Boolean.valueOf(z));
                contentValues.put(FIREWALL_SOURCE_PACKAGE_NAME, context.getPackageName());
                return contentResolver.update(parse, contentValues, null, null) == 1;
            }
        } catch (Exception e) {
            Log.e(TAG, "setMobileTempRestrict", e);
        }
        return false;
    }

    public static boolean setWifiRestrict(Context context, String str, boolean z) {
        try {
            Uri parse = Uri.parse(String.format(WIFI_FIREWALL_URI_STR, str));
            if (parse != null) {
                ContentResolver contentResolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(FIREWALL_WIFI_RULE, Boolean.valueOf(z));
                contentValues.put(FIREWALL_SOURCE_PACKAGE_NAME, context.getPackageName());
                return contentResolver.update(parse, contentValues, null, null) == 1;
            }
        } catch (Exception e) {
            Log.e(TAG, "setWifiTempRestrict", e);
        }
        return false;
    }

    public static boolean setWifiTempRestrict(Context context, String str, boolean z) {
        try {
            Uri parse = Uri.parse(String.format(TEMP_WIFI_FIREWALL_URI_STR, str));
            if (parse != null) {
                ContentResolver contentResolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(FIREWALL_TEMP_WIFI_RULE, Boolean.valueOf(z));
                contentValues.put(FIREWALL_SOURCE_PACKAGE_NAME, context.getPackageName());
                return contentResolver.update(parse, contentValues, null, null) == 1;
            }
        } catch (Exception e) {
            Log.e(TAG, "setWifiTempRestrict", e);
        }
        return false;
    }

    public static boolean startCorrection(Context context, int i, int i2) {
        try {
            Uri parse = Uri.parse(URI_SMS_CORRECTION);
            if (parse != null) {
                ContentResolver contentResolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(BUNDLE_KEY_SLOT_ID, Integer.valueOf(i));
                contentValues.put(KEY_CORRECTION_TYPE, Integer.valueOf(i2));
                return contentResolver.update(parse, contentValues, null, null) == 1;
            }
        } catch (Exception e) {
            Log.e(TAG, "startCorrection", e);
        }
        return false;
    }

    public static void unRegisterFirewallContentObserver(Context context, ContentObserver contentObserver) {
        context.getContentResolver().unregisterContentObserver(contentObserver);
    }

    public static void unRegisterPackageContentObserver(Context context, ContentObserver contentObserver) {
        context.getContentResolver().unregisterContentObserver(contentObserver);
    }
}
