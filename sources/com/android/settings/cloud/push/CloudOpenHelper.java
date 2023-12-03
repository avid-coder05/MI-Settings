package com.android.settings.cloud.push;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* loaded from: classes.dex */
public class CloudOpenHelper extends SQLiteOpenHelper {
    public CloudOpenHelper(Context context) {
        super(context, "compatibility_settings.db", (SQLiteDatabase.CursorFactory) null, 1);
    }

    private void createExistCompatTable(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE exist_compatibility(_id INTEGER PRIMARY KEY,c_precise INTEGER,c_versions TEXT,c_package_name TEXT,c_message TEXT,c_title TEXT,c_ticker TEXT); ");
    }

    private void createInstallCompatTable(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE install_compatibility(_id INTEGER PRIMARY KEY,c_precise INTEGER,c_versions TEXT,c_package_name TEXT,c_message TEXT); ");
    }

    private void createRunningCompatTable(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE running_compatibility(_id INTEGER PRIMARY KEY,c_precise INTEGER,c_versions TEXT,c_package_name TEXT,c_message TEXT); ");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        createExistCompatTable(sQLiteDatabase);
        createInstallCompatTable(sQLiteDatabase);
        createRunningCompatTable(sQLiteDatabase);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }
}
