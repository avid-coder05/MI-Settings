package com.android.settings.slices;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import java.util.Locale;

/* loaded from: classes2.dex */
public class SlicesDatabaseHelper extends SQLiteOpenHelper {
    private static SlicesDatabaseHelper sSingleton;
    private final Context mContext;

    private SlicesDatabaseHelper(Context context) {
        super(context, "slices_index.db", (SQLiteDatabase.CursorFactory) null, 8);
        this.mContext = context;
    }

    private void createDatabases(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE VIRTUAL TABLE slices_index USING fts4(key, slice_uri, title, summary, screentitle, keywords, icon, fragment, controller, slice_type, unavailable_slice_subtitle, public_slice INTEGER DEFAULT 0 );");
        Log.d("SlicesDatabaseHelper", "Created databases");
    }

    private void dropTables(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS slices_index");
    }

    public static synchronized SlicesDatabaseHelper getInstance(Context context) {
        SlicesDatabaseHelper slicesDatabaseHelper;
        synchronized (SlicesDatabaseHelper.class) {
            if (sSingleton == null) {
                sSingleton = new SlicesDatabaseHelper(context.getApplicationContext());
            }
            slicesDatabaseHelper = sSingleton;
        }
        return slicesDatabaseHelper;
    }

    private boolean isBuildIndexed() {
        return this.mContext.getSharedPreferences("slices_shared_prefs", 0).getBoolean(getBuildTag(), false);
    }

    private boolean isLocaleIndexed() {
        return this.mContext.getSharedPreferences("slices_shared_prefs", 0).getBoolean(Locale.getDefault().toString(), false);
    }

    private void setBuildIndexed() {
        this.mContext.getSharedPreferences("slices_shared_prefs", 0).edit().putBoolean(getBuildTag(), true).apply();
    }

    private void setLocaleIndexed() {
        this.mContext.getSharedPreferences("slices_shared_prefs", 0).edit().putBoolean(Locale.getDefault().toString(), true).apply();
    }

    String getBuildTag() {
        return Build.FINGERPRINT;
    }

    public boolean isSliceDataIndexed() {
        return isBuildIndexed() && isLocaleIndexed();
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        createDatabases(sQLiteDatabase);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        if (i < 8) {
            Log.d("SlicesDatabaseHelper", "Reconstructing DB from " + i + " to " + i2);
            reconstruct(sQLiteDatabase);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reconstruct(SQLiteDatabase sQLiteDatabase) {
        this.mContext.getSharedPreferences("slices_shared_prefs", 0).edit().clear().apply();
        dropTables(sQLiteDatabase);
        createDatabases(sQLiteDatabase);
    }

    public void setIndexedState() {
        setBuildIndexed();
        setLocaleIndexed();
    }
}
