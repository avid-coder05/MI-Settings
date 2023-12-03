package com.android.settings.connectivity;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManagerService;
import java.io.Closeable;
import java.util.Arrays;

/* loaded from: classes.dex */
public class MiuiBluetoothDataBaseOperaterUtil {
    private static final Uri URI_DEVICE_PLUGIN_INFO = Uri.parse("content://com.android.bluetooth.ble.app.headsetdata.provider/deviceplugininfo");
    private static final Uri URI_DEVICE_GRAY_RELEASE_PLUGIN_INFO = Uri.parse("content://com.android.bluetooth.ble.app.headsetdata.provider/grayreleaseplugininfo");

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean comparePluginversion(Cursor cursor, String str, String str2) {
        while (cursor.moveToNext()) {
            try {
                String string = cursor.getString(cursor.getColumnIndex("pluginversion"));
                String string2 = cursor.getString(cursor.getColumnIndex("deviceid"));
                Log.d("MiuiBluetoothDataBaseOperaterUtil", "versionBase: " + str + ",version:" + string + ", " + string2);
                if (containValue(string2, str2) && !TextUtils.isEmpty(str) && !TextUtils.isEmpty(string)) {
                    String[] split = str.split("\\_", -1);
                    String[] split2 = string.split("\\_", -1);
                    if (split.length == 2 && split2.length == 2 && !TextUtils.isEmpty(split[0]) && !TextUtils.isEmpty(split[1]) && !TextUtils.isEmpty(split2[0]) && !TextUtils.isEmpty(split2[1]) && split[0].equals(split2[0])) {
                        String[] split3 = split[1].split("\\.", -1);
                        String[] split4 = split2[1].split("\\.", -1);
                        Log.d("MiuiBluetoothDataBaseOperaterUtil", "minversion: " + split[1] + ", minversionLocal: " + split2[1]);
                        if (split3.length == 2 && split4.length == 2 && !TextUtils.isEmpty(split3[0]) && !TextUtils.isEmpty(split3[1]) && !TextUtils.isEmpty(split4[0]) && !TextUtils.isEmpty(split4[1])) {
                            int parseInt = Integer.parseInt(split3[0]);
                            int parseInt2 = Integer.parseInt(split4[0]);
                            if (parseInt2 < parseInt) {
                                return true;
                            }
                            int parseInt3 = Integer.parseInt(split3[1]);
                            int parseInt4 = Integer.parseInt(split4[1]);
                            if (parseInt2 == parseInt && parseInt4 <= parseInt3) {
                                return true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static boolean containValue(String str, String str2) {
        try {
            Log.d("MiuiBluetoothDataBaseOperaterUtil", "deviceId: " + str2 + ", deviceIdAll:" + str);
            if (TextUtils.isEmpty(str2) || TextUtils.isEmpty(str)) {
                return false;
            }
            String[] split = str.split("\\,");
            if (split.length > 0) {
                return Arrays.asList(split).contains(str2);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean queryPluginSupport(Context context, String str) {
        try {
            Log.d("MiuiBluetoothDataBaseOperaterUtil", "queryPluginSupport");
            SplitInfoManager splitInfoManagerService = SplitInfoManagerService.getInstance();
            if (splitInfoManagerService != null) {
                String currentSplitInfoVersion = splitInfoManagerService.getCurrentSplitInfoVersion();
                if (TextUtils.isEmpty(currentSplitInfoVersion)) {
                    return false;
                }
                return queryPluginSupport(context, currentSplitInfoVersion, str);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x002d, code lost:
    
        if (comparePluginversion(r3, r12, r13) != false) goto L14;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean queryPluginSupport(android.content.Context r11, java.lang.String r12, java.lang.String r13) {
        /*
            r0 = 0
            java.lang.String r1 = "MiuiBluetoothDataBaseOperaterUtil"
            r2 = 0
            if (r11 != 0) goto L12
            java.lang.String r11 = "context is null!"
            android.util.Log.d(r1, r11)     // Catch: java.lang.Throwable -> L7e java.lang.Exception -> L81
            closeQuietly(r2)
            closeQuietly(r2)
            return r0
        L12:
            android.content.ContentResolver r3 = r11.getContentResolver()     // Catch: java.lang.Throwable -> L7e java.lang.Exception -> L81
            android.net.Uri r4 = com.android.settings.connectivity.MiuiBluetoothDataBaseOperaterUtil.URI_DEVICE_PLUGIN_INFO     // Catch: java.lang.Throwable -> L7e java.lang.Exception -> L81
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            android.database.Cursor r3 = r3.query(r4, r5, r6, r7, r8)     // Catch: java.lang.Throwable -> L7e java.lang.Exception -> L81
            r4 = 1
            if (r3 == 0) goto L36
            int r5 = r3.getCount()     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L7a
            if (r5 <= 0) goto L36
            boolean r5 = comparePluginversion(r3, r12, r13)     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L7a
            if (r5 == 0) goto L36
        L2f:
            closeQuietly(r3)
            closeQuietly(r2)
            return r4
        L36:
            android.content.ContentResolver r5 = r11.getContentResolver()     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L7a
            android.net.Uri r6 = com.android.settings.connectivity.MiuiBluetoothDataBaseOperaterUtil.URI_DEVICE_GRAY_RELEASE_PLUGIN_INFO     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L7a
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            android.database.Cursor r2 = r5.query(r6, r7, r8, r9, r10)     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L7a
            if (r2 == 0) goto L53
            int r11 = r2.getCount()     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L7a
            if (r11 <= 0) goto L53
            boolean r11 = comparePluginversion(r2, r12, r13)     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L7a
            if (r11 == 0) goto L53
            goto L2f
        L53:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L7a
            r11.<init>()     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L7a
            java.lang.String r12 = "cursor@"
            r11.append(r12)     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L7a
            r11.append(r3)     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L7a
            java.lang.String r12 = ",cursorGary@"
            r11.append(r12)     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L7a
            r11.append(r2)     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L7a
            java.lang.String r11 = r11.toString()     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L7a
            android.util.Log.d(r1, r11)     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L7a
            closeQuietly(r3)
            closeQuietly(r2)
            goto L8c
        L76:
            r11 = move-exception
            r12 = r2
            r2 = r3
            goto L8e
        L7a:
            r11 = move-exception
            r12 = r2
            r2 = r3
            goto L83
        L7e:
            r11 = move-exception
            r12 = r2
            goto L8e
        L81:
            r11 = move-exception
            r12 = r2
        L83:
            r11.printStackTrace()     // Catch: java.lang.Throwable -> L8d
            closeQuietly(r2)
            closeQuietly(r12)
        L8c:
            return r0
        L8d:
            r11 = move-exception
        L8e:
            closeQuietly(r2)
            closeQuietly(r12)
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.connectivity.MiuiBluetoothDataBaseOperaterUtil.queryPluginSupport(android.content.Context, java.lang.String, java.lang.String):boolean");
    }
}
