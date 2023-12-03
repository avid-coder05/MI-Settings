package com.android.settings.freeform;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/* loaded from: classes.dex */
public class FlashBackContentProvider extends ContentProvider {
    private static UriMatcher uriMatcher;
    private SQLiteDatabase db;
    private FlashBackDataHelper mFlashBackDataHelper;

    static {
        UriMatcher uriMatcher2 = new UriMatcher(-1);
        uriMatcher = uriMatcher2;
        uriMatcher2.addURI("com.android.settings.freeform.provider", "FlashBack_Support_Apps", 0);
        uriMatcher.addURI("com.android.settings.freeform.provider", "FlashBack_Current_App", 1);
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        FlashBackDataHelper flashBackDataHelper = new FlashBackDataHelper(getContext(), "FlashBackSupportApps.db", null, 0);
        this.mFlashBackDataHelper = flashBackDataHelper;
        this.db = flashBackDataHelper.getReadableDatabase();
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        int match = uriMatcher.match(uri);
        if (match != 0) {
            if (match != 1) {
                return null;
            }
            return this.db.query("FlashBack_Current_App", null, null, null, null, null, null, null);
        }
        return this.db.query("FlashBack_Support_Apps", strArr, str, strArr2, null, null, str2, null);
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        if (uriMatcher.match(uri) == 1) {
            Cursor query = this.db.query("FlashBack_Current_App", null, null, null, null, null, null, null);
            query.moveToFirst();
            this.db.update("FlashBack_Current_App", contentValues, "packageName=?", new String[]{query.getString(query.getColumnIndex("packageName"))});
        }
        return 0;
    }
}
