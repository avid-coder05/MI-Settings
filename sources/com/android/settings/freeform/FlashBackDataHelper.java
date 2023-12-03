package com.android.settings.freeform;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* loaded from: classes.dex */
public class FlashBackDataHelper extends SQLiteOpenHelper {
    public FlashBackDataHelper(Context context, String str, SQLiteDatabase.CursorFactory cursorFactory, int i) {
        super(context, str, cursorFactory, 1);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table FlashBack_Support_Apps (packageName text primary key, switch integer)");
        sQLiteDatabase.execSQL("create table FlashBack_Current_App (packageName text primary key)");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("drop table if exists FlashBack_Support_Apps");
        sQLiteDatabase.execSQL("drop table if exists FlashBack_Current_App");
        onCreate(sQLiteDatabase);
    }
}
