package com.android.settings.bluetooth;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import miui.provider.Weather;

/* loaded from: classes.dex */
public class MiuiOnSavedDeviceDataUtils {
    private Context mContext;
    private ContentResolver mResolver;
    private static final Uri URI_UNSYNCED = Uri.parse("content://com.android.bluetooth.ble.app.headsetdata.provider/unsynceddata");
    private static final Uri URI_SYNCED = Uri.parse("content://com.android.bluetooth.ble.app.headsetdata.provider/synceddata");

    public MiuiOnSavedDeviceDataUtils(Context context) {
        this.mContext = context;
        this.mResolver = context.getContentResolver();
    }

    public static boolean checkValidity(String str) {
        Matcher matcher = Pattern.compile("[0-9a-zA-Z]{2}:[0-9a-zA-Z]{2}:[0-9a-zA-Z]{2}:[0-9a-zA-Z]{2}:[0-9a-zA-Z]{2}:[0-9a-zA-Z]{2}").matcher(str);
        if (matcher != null) {
            return matcher.matches();
        }
        return false;
    }

    public static ContentValues getRecordFromSyncTable(Cursor cursor) {
        ContentValues contentValues = new ContentValues();
        if (cursor == null) {
            return contentValues;
        }
        String string = cursor.getString(cursor.getColumnIndex("syncId"));
        long j = cursor.getLong(cursor.getColumnIndex("eTag"));
        String string2 = cursor.getString(cursor.getColumnIndex("mac"));
        String string3 = cursor.getString(cursor.getColumnIndex("name"));
        String string4 = cursor.getString(cursor.getColumnIndex("version"));
        String string5 = cursor.getString(cursor.getColumnIndex("accountKey"));
        String string6 = cursor.getString(cursor.getColumnIndex("codecs"));
        String string7 = cursor.getString(cursor.getColumnIndex("manufacturer"));
        String string8 = cursor.getString(cursor.getColumnIndex("lmpVer"));
        String string9 = cursor.getString(cursor.getColumnIndex("lmpSubVer"));
        String string10 = cursor.getString(cursor.getColumnIndex("linkKeyType"));
        String string11 = cursor.getString(cursor.getColumnIndex("pinLength"));
        String string12 = cursor.getString(cursor.getColumnIndex("linkKey"));
        String string13 = cursor.getString(cursor.getColumnIndex("service"));
        String string14 = cursor.getString(cursor.getColumnIndex("aliase"));
        String string15 = cursor.getString(cursor.getColumnIndex("lbsLongitude"));
        String string16 = cursor.getString(cursor.getColumnIndex("lbsLatitude"));
        String string17 = cursor.getString(cursor.getColumnIndex("lbsTimestamp"));
        String string18 = cursor.getString(cursor.getColumnIndex("extend"));
        String string19 = cursor.getString(cursor.getColumnIndex("devId"));
        String string20 = cursor.getString(cursor.getColumnIndex("devType"));
        String string21 = cursor.getString(cursor.getColumnIndex("hasGps"));
        String string22 = cursor.getString(cursor.getColumnIndex("gps"));
        int i = cursor.getInt(cursor.getColumnIndex("btDevType"));
        int i2 = cursor.getInt(cursor.getColumnIndex("addrType"));
        long j2 = cursor.getLong(cursor.getColumnIndex(Weather.WeatherBaseColumns.TIMESTAMP));
        long j3 = cursor.getLong(cursor.getColumnIndex("devClass"));
        Double valueOf = Double.valueOf(cursor.getDouble(cursor.getColumnIndex("avrcpCtVersion")));
        Double valueOf2 = Double.valueOf(cursor.getDouble(cursor.getColumnIndex("avrcpFeatures")));
        Double valueOf3 = Double.valueOf(cursor.getDouble(cursor.getColumnIndex("a2dpVersion")));
        Double valueOf4 = Double.valueOf(cursor.getDouble(cursor.getColumnIndex("avdtpVersion")));
        Double valueOf5 = Double.valueOf(cursor.getDouble(cursor.getColumnIndex("hfpVersion")));
        Double valueOf6 = Double.valueOf(cursor.getDouble(cursor.getColumnIndex("avrcpTgVersion")));
        contentValues.put("syncId", string);
        contentValues.put("eTag", Long.valueOf(j));
        contentValues.put("mac", string2);
        contentValues.put("name", string3);
        contentValues.put("version", string4);
        contentValues.put("accountKey", string5);
        contentValues.put("codecs", string6);
        contentValues.put("manufacturer", string7);
        contentValues.put("lmpVer", string8);
        contentValues.put("lmpSubVer", string9);
        contentValues.put("linkKeyType", string10);
        contentValues.put("pinLength", string11);
        contentValues.put("linkKey", string12);
        contentValues.put("service", string13);
        contentValues.put("aliase", string14);
        contentValues.put("lbsLongitude", string15);
        contentValues.put("lbsLatitude", string16);
        contentValues.put("lbsTimestamp", string17);
        contentValues.put("extend", string18);
        contentValues.put("devId", string19);
        contentValues.put("devType", string20);
        contentValues.put("hasGps", string21);
        contentValues.put("gps", string22);
        contentValues.put("btDevType", Integer.valueOf(i));
        contentValues.put("addrType", Integer.valueOf(i2));
        contentValues.put(Weather.WeatherBaseColumns.TIMESTAMP, Long.valueOf(j2));
        contentValues.put("devClass", Long.valueOf(j3));
        contentValues.put("avrcpCtVersion", valueOf);
        contentValues.put("avrcpFeatures", valueOf2);
        contentValues.put("a2dpVersion", valueOf3);
        contentValues.put("avdtpVersion", valueOf4);
        contentValues.put("hfpVersion", valueOf5);
        contentValues.put("avrcpTgVersion", valueOf6);
        return contentValues;
    }

    public void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                Log.d("MiuiOnSavedDeviceDataUtils", "closeQuietly operation is abnormal" + e.toString());
            }
        }
    }

    public void createAndUpdateData(ContentValues contentValues) {
        Cursor cursor;
        Closeable closeable;
        Log.d("MiuiOnSavedDeviceDataUtils", "create or update BtData of datebase");
        String[] strArr = {contentValues.getAsString("mac")};
        contentValues.put(Weather.WeatherBaseColumns.TIMESTAMP, Long.valueOf(System.currentTimeMillis()));
        try {
            ContentResolver contentResolver = this.mResolver;
            Uri uri = URI_SYNCED;
            Cursor query = contentResolver.query(uri, null, "mac = ?", strArr, null, null);
            try {
                ContentResolver contentResolver2 = this.mResolver;
                Uri uri2 = URI_UNSYNCED;
                cursor = contentResolver2.query(uri2, null, "mac = ?", strArr, null, null);
                if (query != null) {
                    try {
                        if (query.getCount() > 0) {
                            if (this.mResolver.update(uri, contentValues, "mac = ?", strArr) > 0) {
                                Log.d("MiuiOnSavedDeviceDataUtils", "update bt data to synced-table success");
                            }
                        } else if (this.mResolver.insert(uri, contentValues) == null) {
                            Log.e("MiuiOnSavedDeviceDataUtils", "insert synced error");
                        } else {
                            Log.d("MiuiOnSavedDeviceDataUtils", "insert bt data to synced-table success");
                        }
                    } catch (Exception e) {
                        e = e;
                        closeable = query;
                        try {
                            Log.d("MiuiOnSavedDeviceDataUtils", "update or insert operation is abnormal" + e.toString());
                            closeQuietly(closeable);
                            closeQuietly(cursor);
                        } catch (Throwable th) {
                            th = th;
                            closeQuietly(closeable);
                            closeQuietly(cursor);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        closeable = query;
                        closeQuietly(closeable);
                        closeQuietly(cursor);
                        throw th;
                    }
                }
                if (cursor != null) {
                    contentValues.put("status", (Integer) 2);
                    if (cursor.getCount() <= 0) {
                        contentValues.put("status", (Integer) 3);
                        if (this.mResolver.insert(uri2, contentValues) == null) {
                            Log.e("MiuiOnSavedDeviceDataUtils", "insert unsynced error");
                        } else {
                            Log.d("MiuiOnSavedDeviceDataUtils", "insert bt data to unsynced-table success status: add");
                        }
                    } else if (this.mResolver.update(uri2, contentValues, "mac = ?", strArr) > 0) {
                        Log.d("MiuiOnSavedDeviceDataUtils", "update bt data to unsynced-table success, status: update");
                    }
                }
                closeQuietly(query);
            } catch (Exception e2) {
                e = e2;
                closeable = query;
                cursor = null;
            } catch (Throwable th3) {
                th = th3;
                closeable = query;
                cursor = null;
            }
        } catch (Exception e3) {
            e = e3;
            cursor = null;
            closeable = null;
        } catch (Throwable th4) {
            th = th4;
            cursor = null;
            closeable = null;
        }
        closeQuietly(cursor);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0083 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r19v0, types: [com.android.settings.bluetooth.MiuiOnSavedDeviceDataUtils] */
    /* JADX WARN: Type inference failed for: r4v6, types: [int] */
    /* JADX WARN: Type inference failed for: r4v7, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r4v8 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void deleteDeviceData(java.lang.String r20) {
        /*
            Method dump skipped, instructions count: 290
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.MiuiOnSavedDeviceDataUtils.deleteDeviceData(java.lang.String):void");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v0, types: [com.android.settings.bluetooth.MiuiOnSavedDeviceDataUtils] */
    /* JADX WARN: Type inference failed for: r2v2 */
    /* JADX WARN: Type inference failed for: r2v3, types: [java.io.Closeable] */
    public ContentValues queryDeviceByMac(String str) {
        Exception exc;
        ContentValues contentValues;
        Cursor cursor;
        Throwable th;
        Log.d("MiuiOnSavedDeviceDataUtils", "query BtData in datebase through address");
        String[] strArr = {str};
        ContentValues contentValues2 = null;
        r11 = null;
        ContentValues contentValues3 = null;
        Closeable closeable = null;
        try {
            try {
                Cursor query = this.mResolver.query(URI_SYNCED, null, "mac = ?", strArr, null, null);
                if (query != null) {
                    try {
                        if (query.getCount() == 1) {
                            while (query.moveToNext()) {
                                contentValues3 = getRecordFromSyncTable(query);
                            }
                            closeQuietly(query);
                            return contentValues3;
                        }
                    } catch (Exception e) {
                        contentValues = null;
                        closeable = query;
                        exc = e;
                        Log.d("MiuiOnSavedDeviceDataUtils", "query operation is abnormal" + exc.toString());
                        exc.printStackTrace();
                        closeQuietly(closeable);
                        contentValues2 = contentValues;
                        return contentValues2;
                    } catch (Throwable th2) {
                        th = th2;
                        cursor = query;
                        closeQuietly(cursor);
                        throw th;
                    }
                }
                Log.e("MiuiOnSavedDeviceDataUtils", "error queryDeviceByMac: cursorSync data not single");
                closeQuietly(query);
                return contentValues3;
            } catch (Throwable th3) {
                cursor = contentValues2;
                th = th3;
            }
        } catch (Exception e2) {
            exc = e2;
            contentValues = null;
        }
    }

    public List<ContentValues> queryDeviceData() {
        Log.d("MiuiOnSavedDeviceDataUtils", "query BtData in datebase");
        ArrayList arrayList = new ArrayList();
        Cursor cursor = null;
        try {
            try {
                cursor = this.mResolver.query(URI_SYNCED, null, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        arrayList.add(getRecordFromSyncTable(cursor));
                    }
                }
            } catch (Exception e) {
                Log.d("MiuiOnSavedDeviceDataUtils", "query operation is abnormal" + e.toString());
            }
            return arrayList;
        } finally {
            closeQuietly(cursor);
        }
    }
}
