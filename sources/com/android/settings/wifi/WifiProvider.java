package com.android.settings.wifi;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Binder;
import android.os.Process;
import android.text.TextUtils;
import miui.provider.Wifi;

/* loaded from: classes2.dex */
public class WifiProvider extends ContentProvider {
    private static final UriMatcher sMatcher;
    private WifiDatabaseHelper mHelper;

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sMatcher = uriMatcher;
        uriMatcher.addURI("wifi", "wifi", 1);
        uriMatcher.addURI("wifi", "wifi/#", 2);
        uriMatcher.addURI("wifi", "wifi_sync", 3);
        uriMatcher.addURI("wifi", "wifi_sync/#", 4);
        uriMatcher.addURI("wifi", "wifi_share", 5);
        uriMatcher.addURI("wifi", "wifi_share/#", 6);
    }

    private ContentValues addSyncState(Uri uri, ContentValues contentValues) {
        if (contentValues.containsKey("sync_state")) {
            return contentValues;
        }
        String queryParameter = uri.getQueryParameter("caller_is_syncadapter");
        contentValues.put("sync_state", Integer.valueOf((queryParameter == null || !Boolean.valueOf(queryParameter).booleanValue()) ? 0 : 1));
        return contentValues;
    }

    private void enforcePermission(Uri uri) {
        if (Binder.getCallingUid() != Process.myUid()) {
            getContext().enforceCallingPermission("com.xiaomi.permission.ACCESS_WIFI", "Opening uri " + uri);
        }
    }

    private String parseSelection(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return " AND (" + str + ')';
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        enforcePermission(uri);
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        switch (sMatcher.match(uri)) {
            case 1:
                int delete = writableDatabase.delete("wifi", str, strArr);
                Uri uri2 = Wifi.CONTENT_URI;
                return delete;
            case 2:
                int delete2 = writableDatabase.delete("wifi", "_id=" + uri.getPathSegments().get(1) + parseSelection(str), strArr);
                Uri uri3 = Wifi.CONTENT_URI;
                return delete2;
            case 3:
                int delete3 = writableDatabase.delete("wifi_sync", str, strArr);
                Uri uri4 = Wifi.SyncState.CONTENT_URI;
                return delete3;
            case 4:
                int delete4 = writableDatabase.delete("wifi_sync", "_id=" + uri.getPathSegments().get(1) + parseSelection(str), strArr);
                Uri uri5 = Wifi.SyncState.CONTENT_URI;
                return delete4;
            case 5:
                int delete5 = writableDatabase.delete("wifi_share", str, strArr);
                Uri uri6 = Wifi.WifiShare.CONTENT_URI;
                return delete5;
            case 6:
                int delete6 = writableDatabase.delete("wifi_share", "_id=" + uri.getPathSegments().get(1) + parseSelection(str), strArr);
                Uri uri7 = Wifi.WifiShare.CONTENT_URI;
                return delete6;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        long insert;
        enforcePermission(uri);
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        int match = sMatcher.match(uri);
        if (match == 1) {
            insert = writableDatabase.insert("wifi", null, addSyncState(uri, contentValues));
        } else if (match == 3) {
            insert = writableDatabase.insert("wifi_sync", null, contentValues);
        } else if (match != 5) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        } else {
            contentValues.put("share_upate_time", Long.valueOf(System.currentTimeMillis()));
            insert = writableDatabase.insert("wifi_share", null, contentValues);
        }
        return ContentUris.withAppendedId(uri, insert);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        if (WifiProviderUtils.isFileBasedEncryptionEnabled()) {
            this.mHelper = WifiDatabaseHelper.getInstanceForDe(getContext());
            return true;
        }
        this.mHelper = new WifiDatabaseHelper(getContext());
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        Cursor query;
        enforcePermission(uri);
        SQLiteDatabase readableDatabase = this.mHelper.getReadableDatabase();
        String queryParameter = uri.getQueryParameter("limit");
        String queryParameter2 = uri.getQueryParameter("distinct");
        boolean booleanValue = !TextUtils.isEmpty(queryParameter2) ? Boolean.valueOf(queryParameter2).booleanValue() : false;
        switch (sMatcher.match(uri)) {
            case 1:
                query = readableDatabase.query(booleanValue, "wifi", strArr, str, strArr2, null, null, str2, queryParameter);
                break;
            case 2:
                query = readableDatabase.query("wifi", strArr, "_id=" + uri.getPathSegments().get(1) + parseSelection(str), strArr2, null, null, str2);
                break;
            case 3:
                query = readableDatabase.query("wifi_sync", strArr, str, strArr2, null, null, str2, queryParameter);
                break;
            case 4:
                query = readableDatabase.query("wifi_sync", strArr, "_id=" + uri.getPathSegments().get(1) + parseSelection(str), strArr2, null, null, str2);
                break;
            case 5:
                query = readableDatabase.query(booleanValue, "wifi_share", strArr, str, strArr2, null, null, str2, queryParameter);
                break;
            case 6:
                query = readableDatabase.query("wifi_share", strArr, "_id=" + uri.getPathSegments().get(1) + parseSelection(str), strArr2, null, null, str2);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (query != null) {
            query.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return query;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        enforcePermission(uri);
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        switch (sMatcher.match(uri)) {
            case 1:
                int update = writableDatabase.update("wifi", addSyncState(uri, contentValues), str, strArr);
                Uri uri2 = Wifi.CONTENT_URI;
                return update;
            case 2:
                int update2 = writableDatabase.update("wifi", addSyncState(uri, contentValues), "_id=" + uri.getPathSegments().get(1) + parseSelection(str), strArr);
                Uri uri3 = Wifi.CONTENT_URI;
                return update2;
            case 3:
                int update3 = writableDatabase.update("wifi_sync", contentValues, str, strArr);
                Uri uri4 = Wifi.SyncState.CONTENT_URI;
                return update3;
            case 4:
                int update4 = writableDatabase.update("wifi_sync", contentValues, "_id=" + uri.getPathSegments().get(1) + parseSelection(str), strArr);
                Uri uri5 = Wifi.SyncState.CONTENT_URI;
                return update4;
            case 5:
                contentValues.put("share_upate_time", Long.valueOf(System.currentTimeMillis()));
                int update5 = writableDatabase.update("wifi_share", contentValues, str, strArr);
                Uri uri6 = Wifi.WifiShare.CONTENT_URI;
                return update5;
            case 6:
                String str2 = uri.getPathSegments().get(1);
                contentValues.put("share_upate_time", Long.valueOf(System.currentTimeMillis()));
                int update6 = writableDatabase.update("wifi_share", contentValues, "_id=" + str2 + parseSelection(str), strArr);
                Uri uri7 = Wifi.WifiShare.CONTENT_URI;
                return update6;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
}
