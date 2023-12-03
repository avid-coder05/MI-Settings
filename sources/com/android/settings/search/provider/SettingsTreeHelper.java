package com.android.settings.search.provider;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.search.KeywordsCloudConfigHelper;
import com.android.settingslib.search.RankedCursor;
import com.android.settingslib.search.SearchUtils;
import com.android.settingslib.search.SettingsTree;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
class SettingsTreeHelper {
    private static final String ASSET_FILENAME = "assets/index.json";
    private static final String BUILD_TREE = "tree";
    private static final String BUILD_UTC = "ro.build.date.utc";
    private static final String FILENAME = "index.json";
    private static final String TAG = "SettingsTreeHelper";
    private static SettingsTreeHelper sInstance;
    private File mFile;
    private String mLocale;
    private SettingsTree mTree;

    private SettingsTreeHelper() {
    }

    private SettingsTreeHelper(Context context) {
        this.mLocale = Locale.getDefault().toString();
        File filesDir = context.getFilesDir();
        if (!filesDir.exists()) {
            filesDir.mkdir();
        }
        this.mFile = new File(filesDir, FILENAME);
        Exception exc = null;
        Thread.currentThread().setContextClassLoader(context.getClassLoader());
        if (this.mFile.exists()) {
            try {
                JSONObject readJSONObject = SearchUtils.readJSONObject(new FileInputStream(this.mFile));
                if (readJSONObject.optInt(BUILD_UTC, 0) == Integer.parseInt(SystemProperties.get(BUILD_UTC))) {
                    this.mTree = SettingsTree.newInstance(context, readJSONObject.optJSONObject(BUILD_TREE), false);
                }
            } catch (Exception e) {
                exc = e;
            }
        }
        if (this.mTree == null) {
            try {
                this.mTree = SettingsTree.newInstance(context, SearchUtils.readJSONObject(context.getAssets().open(FILENAME)), true);
            } catch (Exception e2) {
                RuntimeException runtimeException = new RuntimeException(e2);
                if (exc == null) {
                    throw runtimeException;
                }
                runtimeException.addSuppressed(exc);
                throw runtimeException;
            }
        }
        commit(true);
        KeywordsCloudConfigHelper.getInstance(context);
    }

    private void commit(boolean z) {
        FileOutputStream fileOutputStream;
        Throwable th;
        String str;
        String str2;
        synchronized (this) {
            if (this.mTree.needCommit() || z) {
                byte[] bArr = null;
                FileOutputStream fileOutputStream2 = null;
                try {
                    try {
                        fileOutputStream = new FileOutputStream(this.mFile);
                    } catch (Throwable th2) {
                        fileOutputStream = bArr;
                        th = th2;
                    }
                } catch (IOException | JSONException unused) {
                }
                try {
                    StringBuilder sb = new StringBuilder();
                    this.mTree.dispatchCommit(sb);
                    byte[] bytes = new JSONObject().put(BUILD_TREE, new JSONObject(sb.toString())).put(BUILD_UTC, SystemProperties.get(BUILD_UTC)).toString().getBytes();
                    fileOutputStream.write(bytes);
                    fileOutputStream.flush();
                    try {
                        fileOutputStream.close();
                        bArr = bytes;
                    } catch (IOException unused2) {
                        str = TAG;
                        str2 = "close file error!";
                        Log.e(str, str2);
                    }
                } catch (IOException | JSONException unused3) {
                    fileOutputStream2 = fileOutputStream;
                    Log.e(TAG, "commit error!");
                    bArr = fileOutputStream2;
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.close();
                            bArr = fileOutputStream2;
                        } catch (IOException unused4) {
                            str = TAG;
                            str2 = "close file error!";
                            Log.e(str, str2);
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException unused5) {
                            Log.e(TAG, "close file error!");
                        }
                    }
                    throw th;
                }
            }
        }
    }

    public static SettingsTreeHelper getInstance(Context context) {
        return getInstance(context, true);
    }

    public static synchronized SettingsTreeHelper getInstance(Context context, boolean z) {
        synchronized (SettingsTreeHelper.class) {
            SettingsTreeHelper settingsTreeHelper = sInstance;
            if (settingsTreeHelper == null || localeHasChange(settingsTreeHelper.mLocale)) {
                if (!z) {
                    return null;
                }
                if (context == null) {
                    throw new RuntimeException("instance not prepared yet");
                }
                long currentTimeMillis = System.currentTimeMillis();
                sInstance = new SettingsTreeHelper(context.getApplicationContext());
                SearchUtils.logCost(currentTimeMillis, System.currentTimeMillis(), "-");
            }
            return sInstance;
        }
    }

    private static boolean localeHasChange(String str) {
        return !Locale.getDefault().toString().equals(str);
    }

    public static void releaseInstance() {
        sInstance = null;
    }

    public int delete(String str, String[] strArr) {
        int dispatchDelete;
        if (this.mTree == null) {
            return 0;
        }
        synchronized (this) {
            dispatchDelete = this.mTree.dispatchDelete(str, strArr, false);
        }
        commit(false);
        return dispatchDelete;
    }

    public String insert(ContentValues contentValues) {
        String dispatchInsert;
        if (this.mTree == null) {
            return null;
        }
        synchronized (this) {
            dispatchInsert = this.mTree.dispatchInsert(contentValues);
        }
        commit(!TextUtils.isEmpty(dispatchInsert));
        return dispatchInsert;
    }

    public void onReceive(Context context, Intent intent) {
        if (this.mTree == null) {
            return;
        }
        synchronized (this) {
            this.mTree.dispatchOnReceive(context, intent);
        }
        commit(false);
    }

    public Cursor query(String[] strArr, String str, String str2, String[] strArr2, String str3, boolean z) {
        if (this.mTree == null) {
            return null;
        }
        commit(false);
        RankedCursor rankedCursor = new RankedCursor(strArr);
        synchronized (this) {
            this.mTree.dispatchQuery(rankedCursor, str, str2, strArr2, str3, z);
        }
        return rankedCursor;
    }

    public int update(ContentValues contentValues, String str, String[] strArr) {
        int dispatchUpdate;
        if (this.mTree == null) {
            return 0;
        }
        synchronized (this) {
            dispatchUpdate = this.mTree.dispatchUpdate(contentValues, str, strArr);
        }
        commit(false);
        return dispatchUpdate;
    }
}
