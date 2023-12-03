package com.android.settings.backup;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import com.android.settings.search.provider.SettingsProvider;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import miui.cloud.Constants;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public class AccountRestoreManager {
    static final String ACCOUNTS_DB_FILE;
    static final String ACCOUNT_DB_PATH;
    private static final String[] COLUMNS_AUTHTOKENS_TYPE_AND_AUTHTOKEN;
    private static final String[] COLUMNS_EXTRAS_KEY_AND_VALUE;
    private static final int DATABASE_VERSION;
    private static HashSet<String> sSupportAccountType;
    private File mAttachDir;
    private Context mContext;
    private SQLiteDatabase mDb;
    private ArrayList<AccountItem> mAccountList = new ArrayList<>();
    private HashMap<Long, ArrayList<Authokens>> mAuthokensMap = new HashMap<>();
    private HashMap<Long, Bundle> mExtrasMap = new HashMap<>();
    private HashSet<String> mExistAccount = new HashSet<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AccountItem {
        public long id;
        public String name;
        public String password;
        public String type;

        private AccountItem() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Authokens {
        public String authtoken;
        public String type;

        private Authokens() {
        }
    }

    /* loaded from: classes.dex */
    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String str) {
            super(context, str, (SQLiteDatabase.CursorFactory) null, AccountRestoreManager.DATABASE_VERSION);
        }

        private void createAccountsDeletionTrigger(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL(" CREATE TRIGGER accountsDelete DELETE ON accounts BEGIN   DELETE FROM authtokens     WHERE accounts_id=OLD._id ;   DELETE FROM extras     WHERE accounts_id=OLD._id ;   DELETE FROM grants     WHERE accounts_id=OLD._id ; END");
        }

        private void createGrantsTable(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL("CREATE TABLE grants (  accounts_id INTEGER NOT NULL, auth_token_type STRING NOT NULL,  uid INTEGER NOT NULL,  UNIQUE (accounts_id,auth_token_type,uid))");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL("CREATE TABLE accounts ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, type TEXT NOT NULL, password TEXT, UNIQUE(name,type))");
            sQLiteDatabase.execSQL("CREATE TABLE authtokens (  _id INTEGER PRIMARY KEY AUTOINCREMENT,  accounts_id INTEGER NOT NULL, type TEXT NOT NULL,  authtoken TEXT,  UNIQUE (accounts_id,type))");
            createGrantsTable(sQLiteDatabase);
            sQLiteDatabase.execSQL("CREATE TABLE extras ( _id INTEGER PRIMARY KEY AUTOINCREMENT, accounts_id INTEGER, key TEXT NOT NULL, value TEXT, UNIQUE(accounts_id,key))");
            sQLiteDatabase.execSQL("CREATE TABLE meta ( key TEXT PRIMARY KEY NOT NULL, value TEXT)");
            createAccountsDeletionTrigger(sQLiteDatabase);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            Log.e("AccountRestoreManager", "upgrade from version " + i + " to version " + i2);
            if (i == 1) {
                i++;
            }
            if (i == 2) {
                createGrantsTable(sQLiteDatabase);
                sQLiteDatabase.execSQL("DROP TRIGGER accountsDelete");
                createAccountsDeletionTrigger(sQLiteDatabase);
                i++;
            }
            if (i == 3) {
                sQLiteDatabase.execSQL("UPDATE accounts SET type = 'com.google' WHERE type == 'com.google.GAIA'");
            }
        }
    }

    static {
        String str;
        int i = Build.VERSION.SDK_INT;
        ACCOUNTS_DB_FILE = i > 23 ? "accounts_ce.db" : "accounts.db";
        if (i > 23) {
            str = Environment.getDataSystemCeDirectory(0) + "/accounts_ce.db";
        } else {
            str = Environment.getUserSystemDirectory(0) + "/accounts.db";
        }
        ACCOUNT_DB_PATH = str;
        HashSet<String> hashSet = new HashSet<>();
        sSupportAccountType = hashSet;
        hashSet.add("com.xiaomi");
        sSupportAccountType.add("com.google");
        sSupportAccountType.add("com.android.email");
        sSupportAccountType.add("com.android.exchange");
        DATABASE_VERSION = getDatabaseVersion();
        COLUMNS_EXTRAS_KEY_AND_VALUE = new String[]{SettingsProvider.ARGS_KEY, "value"};
        COLUMNS_AUTHTOKENS_TYPE_AND_AUTHTOKEN = new String[]{"type", "authtoken"};
    }

    public AccountRestoreManager(Context context) {
        this.mContext = context;
    }

    private static int getDatabaseVersion() {
        int i = Build.VERSION.SDK_INT;
        if (i >= 24) {
            return 10;
        }
        if (i >= 23) {
            return 8;
        }
        if (i >= 21) {
            return 6;
        }
        return i >= 19 ? 5 : 4;
    }

    private void readAccountsTable() {
        this.mExistAccount.clear();
        Account[] accounts = AccountManager.get(this.mContext).getAccounts();
        HashSet hashSet = new HashSet();
        if (accounts != null && accounts.length > 0) {
            for (Account account : accounts) {
                this.mExistAccount.add(account.name);
                hashSet.add(account.type);
            }
        }
        this.mAccountList.clear();
        Cursor query = this.mDb.query("accounts", new String[]{"_id", "type", "name", "password"}, null, null, null, null, null);
        if (query == null) {
            return;
        }
        while (query.moveToNext()) {
            AccountItem accountItem = new AccountItem();
            accountItem.id = query.getLong(0);
            accountItem.type = query.getString(1);
            accountItem.name = query.getString(2);
            accountItem.password = query.getString(3);
            if (!this.mExistAccount.contains(accountItem.name) && (!"com.xiaomi".equals(accountItem.type) || !hashSet.contains(accountItem.type))) {
                if (sSupportAccountType.contains(accountItem.type)) {
                    if ("com.android.email".equals(accountItem.type) || "com.android.exchange".equals(accountItem.type)) {
                        this.mAccountList.add(0, accountItem);
                    } else {
                        this.mAccountList.add(accountItem);
                    }
                }
            }
        }
        query.close();
    }

    private void readAuthokensTable() {
        this.mAuthokensMap.clear();
        Iterator<AccountItem> it = this.mAccountList.iterator();
        while (it.hasNext()) {
            AccountItem next = it.next();
            long j = next.id;
            Cursor query = this.mDb.query("authtokens", COLUMNS_AUTHTOKENS_TYPE_AND_AUTHTOKEN, "accounts_id=(select _id FROM accounts WHERE name=? AND type=?)", new String[]{next.name, next.type}, null, null, null);
            if (query != null) {
                ArrayList<Authokens> arrayList = new ArrayList<>();
                while (query.moveToNext()) {
                    Authokens authokens = new Authokens();
                    authokens.type = query.getString(0);
                    authokens.authtoken = query.getString(1);
                    arrayList.add(authokens);
                }
                query.close();
                this.mAuthokensMap.put(Long.valueOf(j), arrayList);
            }
        }
    }

    private void readExtraTable() {
        this.mExtrasMap.clear();
        Iterator<AccountItem> it = this.mAccountList.iterator();
        while (it.hasNext()) {
            AccountItem next = it.next();
            if (!this.mExistAccount.contains(next.name)) {
                long j = next.id;
                Cursor query = this.mDb.query("extras", COLUMNS_EXTRAS_KEY_AND_VALUE, "accounts_id=(select _id FROM accounts WHERE name=? AND type=?)", new String[]{next.name, next.type}, null, null, null);
                if (query != null) {
                    Bundle bundle = new Bundle();
                    while (query.moveToNext()) {
                        bundle.putString(query.getString(0), query.getString(1));
                    }
                    query.close();
                    this.mExtrasMap.put(Long.valueOf(j), bundle);
                }
            }
        }
    }

    public void importData() {
        AccountManager accountManager = AccountManager.get(this.mContext);
        Iterator<AccountItem> it = this.mAccountList.iterator();
        while (it.hasNext()) {
            AccountItem next = it.next();
            long j = next.id;
            Account account = new Account(next.name, next.type);
            boolean z = false;
            try {
                z = accountManager.addAccountExplicitly(account, next.password, this.mExtrasMap.get(Long.valueOf(j)));
            } catch (Exception e) {
                Log.e("AccountRestoreManager", "add account error!", e);
            }
            if (z) {
                Iterator<Authokens> it2 = this.mAuthokensMap.get(Long.valueOf(j)).iterator();
                while (it2.hasNext()) {
                    Authokens next2 = it2.next();
                    accountManager.setAuthToken(account, next2.type, next2.authtoken);
                }
            }
        }
        Intent intent = new Intent("com.miui.backup.ACCOUNT_RESTORED");
        intent.setPackage(Constants.CLOUDSERVICE_PACKAGE_NAME);
        this.mContext.sendBroadcast(intent);
        this.mDb.close();
    }

    public void prepareImport(File file) {
        this.mDb = new DatabaseHelper(this.mContext, file.getAbsolutePath()).getReadableDatabase();
        readAccountsTable();
        readExtraTable();
        readAuthokensTable();
    }

    public void setActiveAdmin() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        ActivityInfo activityInfo = null;
        try {
            activityInfo = this.mContext.getPackageManager().getReceiverInfo(miui.os.Build.IS_MIONE ? new ComponentName("com.android.email", "com.android.email.SecurityPolicy$PolicyAdmin") : new ComponentName("com.android.email", "com.kingsoft.email.SecurityPolicy$PolicyAdmin"), 128);
        } catch (PackageManager.NameNotFoundException unused) {
        }
        if (activityInfo != null) {
            ResolveInfo resolveInfo = new ResolveInfo();
            resolveInfo.activityInfo = activityInfo;
            try {
                devicePolicyManager.setActiveAdmin(new DeviceAdminInfo(this.mContext, resolveInfo).getComponent(), true);
            } catch (IOException | XmlPullParserException unused2) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setAttachDir(File file) {
        this.mAttachDir = file;
    }
}
