package com.android.settings.cloud.push;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import java.util.HashMap;
import miuix.core.util.IOUtils;

/* loaded from: classes.dex */
public class InstallCompatibilityProvider extends ContentProvider {
    private static HashMap<String, String> sProjectionMap;
    private static final UriMatcher sUriMatcher;
    private CloudOpenHelper mOpenHelper;

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sUriMatcher = uriMatcher;
        uriMatcher.addURI("com.android.settings.cloud.compatibility.install", "install", 1);
        uriMatcher.addURI("com.android.settings.cloud.compatibility.install", "install/#", 2);
        HashMap<String, String> hashMap = new HashMap<>();
        sProjectionMap = hashMap;
        hashMap.put("_id", "_id");
        sProjectionMap.put("c_package_name", "c_package_name");
        sProjectionMap.put("c_message", "c_message");
        sProjectionMap.put("c_precise", "c_precise");
        sProjectionMap.put("c_versions", "c_versions");
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        int delete;
        String str2;
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        try {
            int match = sUriMatcher.match(uri);
            if (match == 1) {
                delete = writableDatabase.delete("install_compatibility", str, strArr);
            } else if (match != 2) {
                throw new IllegalArgumentException("Unnown URI" + uri);
            } else {
                String str3 = uri.getPathSegments().get(1);
                StringBuilder sb = new StringBuilder();
                sb.append("_id=");
                sb.append(str3);
                if (TextUtils.isEmpty(str)) {
                    str2 = "";
                } else {
                    str2 = " AND (" + str + ')';
                }
                sb.append(str2);
                delete = writableDatabase.delete("install_compatibility", sb.toString(), strArr);
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return delete;
        } finally {
            IOUtils.closeQuietly(writableDatabase);
        }
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        if (match != 1) {
            if (match == 2) {
                return "vnd.android.cursor.item/vnd.google.install";
            }
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return "vnd.android.cursor.dir/vnd.google.install";
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        if (sUriMatcher.match(uri) != 1) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        ContentValues contentValues2 = contentValues != null ? new ContentValues(contentValues) : new ContentValues();
        if (!contentValues2.containsKey("c_package_name")) {
            contentValues2.put("c_package_name", "");
        }
        if (!contentValues2.containsKey("c_message")) {
            contentValues2.put("c_message", "");
        }
        if (!contentValues2.containsKey("c_precise")) {
            contentValues2.put("c_precise", (Integer) (-1));
        }
        if (!contentValues2.containsKey("c_versions")) {
            contentValues2.put("c_versions", "");
        }
        long insert = this.mOpenHelper.getWritableDatabase().insert("install_compatibility", null, contentValues2);
        if (insert > 0) {
            Uri withAppendedId = ContentUris.withAppendedId(CloudEntity$InstallCompatibility.CONTENT_URI, insert);
            getContext().getContentResolver().notifyChange(withAppendedId, null);
            return withAppendedId;
        }
        throw new SQLException("Failed to insert row into" + uri);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        this.mOpenHelper = new CloudOpenHelper(getContext());
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        int match = sUriMatcher.match(uri);
        if (match == 1) {
            sQLiteQueryBuilder.setTables("install_compatibility");
            sQLiteQueryBuilder.setProjectionMap(sProjectionMap);
        } else if (match != 2) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        } else {
            sQLiteQueryBuilder.setTables("install_compatibility");
            sQLiteQueryBuilder.setProjectionMap(sProjectionMap);
            sQLiteQueryBuilder.appendWhere("_id=" + uri.getPathSegments().get(1));
        }
        if (TextUtils.isEmpty(str2)) {
            str2 = "_id DESC";
        }
        Cursor query = sQLiteQueryBuilder.query(this.mOpenHelper.getReadableDatabase(), strArr, str, strArr2, null, null, str2);
        query.setNotificationUri(getContext().getContentResolver(), uri);
        return query;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        int update;
        String str2;
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        try {
            int match = sUriMatcher.match(uri);
            if (match == 1) {
                update = writableDatabase.update("install_compatibility", contentValues, str, strArr);
            } else if (match != 2) {
                throw new IllegalArgumentException("Unknow URI " + uri);
            } else {
                String str3 = uri.getPathSegments().get(1);
                StringBuilder sb = new StringBuilder();
                sb.append("_id=");
                sb.append(str3);
                if (TextUtils.isEmpty(str)) {
                    str2 = "";
                } else {
                    str2 = " AND (" + str + ')';
                }
                sb.append(str2);
                update = writableDatabase.update("install_compatibility", contentValues, sb.toString(), strArr);
            }
            return update;
        } finally {
            IOUtils.closeQuietly(writableDatabase);
        }
    }
}
