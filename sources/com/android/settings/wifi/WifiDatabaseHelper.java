package com.android.settings.wifi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/* loaded from: classes2.dex */
public class WifiDatabaseHelper extends SQLiteOpenHelper {
    private static WifiDatabaseHelper sDeInstance;

    public WifiDatabaseHelper(Context context) {
        super(context, "wifi_settings.db", (SQLiteDatabase.CursorFactory) null, 8);
    }

    private void addWifiShare(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("ALTER TABLE wifi ADD COLUMN share_state INTEGER DEFAULT 0");
        createShareTable(sQLiteDatabase);
    }

    private void createShareTable(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE wifi_share(_id INTEGER PRIMARY KEY AUTOINCREMENT,uuid TEXT ,marker BIGINT DEFAULT 0,longitude TEXT,latitude TEXT,deleted INTEGER DEFAULT 0,sync_state INTEGER DEFAULT 0,share_state INTEGER DEFAULT 0,share_count INTEGER DEFAULT 0,share_connect_state INTEGER DEFAULT 0,share_feedback INTEGER DEFAULT 0,share_upate_time INTEGER DEFAULT 0,account TEXT,ssid TEXT,bssid TEXT,psk TEXT,wepkey0 TEXT,wepkey1 TEXT,wepkey2 TEXT,wepkey3 TEXT,wep_tx_keyidx INTEGER,priority INTEGER,scan_ssid INTEGER,adhoc INTEGER,keyMgmt TEXT,proto TEXT,authAlg TEXT,pairwise TEXT,groupCipher TEXT,eap TEXT,phase2 TEXT,identity TEXT,anonymousIdentity TEXT,password TEXT,clientCert TEXT,privateKey TEXT,caCert TEXT,clientCertFile TEXT,privateKeyFile TEXT,caCertFile TEXT)");
    }

    private void createTable(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE wifi(_id INTEGER PRIMARY KEY AUTOINCREMENT,uuid TEXT ,marker INTEGER DEFAULT 0,longitude TEXT,latitude TEXT,deleted INTEGER DEFAULT 0,sync_state INTEGER DEFAULT 0,share_state INTEGER DEFAULT 0,account TEXT,ssid TEXT,bssid TEXT,psk TEXT,wepkey0 TEXT,wepkey1 TEXT,wepkey2 TEXT,wepkey3 TEXT,wep_tx_keyidx INTEGER,priority INTEGER,scan_ssid INTEGER,adhoc INTEGER,keyMgmt TEXT,proto TEXT,authAlg TEXT,pairwise TEXT,groupCipher TEXT,eap TEXT,phase2 TEXT,identity TEXT,anonymousIdentity TEXT,password TEXT,clientCert TEXT,privateKey TEXT,caCert TEXT,clientCertFile TEXT,privateKeyFile TEXT,caCertFile TEXT)");
        sQLiteDatabase.execSQL("CREATE TABLE wifi_sync(_id INTEGER PRIMARY KEY AUTOINCREMENT,account_name TEXT NOT NULL,marker INTEGER DEFAULT 0,sync_extra_info TEXT)");
        createShareTable(sQLiteDatabase);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static synchronized WifiDatabaseHelper getInstanceForDe(Context context) {
        WifiDatabaseHelper wifiDatabaseHelper;
        synchronized (WifiDatabaseHelper.class) {
            if (sDeInstance == null) {
                sDeInstance = new WifiDatabaseHelper(WifiProviderUtils.getDeviceEncryptedContext(context));
            }
            wifiDatabaseHelper = sDeInstance;
        }
        return wifiDatabaseHelper;
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        createTable(sQLiteDatabase);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        if (i < 3) {
            addWifiShare(sQLiteDatabase);
            i = 3;
        }
        if (i == 3) {
            sQLiteDatabase.execSQL("update wifi set deleted = 1, sync_state=0 where keyMgmt = \"NONE\" ");
            i = 6;
        }
        if (i == 6) {
            try {
                Cursor rawQuery = sQLiteDatabase.rawQuery("select * from wifi_share", null);
                if (rawQuery != null) {
                    rawQuery.close();
                }
            } catch (SQLiteException unused) {
                this.addWifiShare(sQLiteDatabase);
            }
            i++;
        }
        if (i == 7) {
            sQLiteDatabase.execSQL("ALTER TABLE wifi_sync ADD COLUMN sync_extra_info TEXT");
            i++;
        }
        if (i == i2) {
            return;
        }
        throw new IllegalStateException("Upgrade wifi database to version " + i2 + "fails");
    }
}
