package com.android.settings.cust;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.utils.SignaturesUtils;
import java.util.HashSet;
import miui.content.res.ThemeResources;

/* loaded from: classes.dex */
public class MiHomeManager {
    private static MiHomeManager sInstance;
    public boolean isMiHomeManagerInstalled;
    private Context mContext;
    private volatile boolean mIsInitingSettingsForbiddenList;
    private PackageInfo mPackageInfo;
    private HashSet<String> mPackageSet;
    private PackageManager mPm;
    private HashSet<String> mPreferenceSet;
    private static final Uri CONTENT_URI_WHITELIST = Uri.parse("content://com.xiaomi.mihomemanager.whitelistProvider/packageName");
    private static final Uri CONTENT_URI_SETTINGS = Uri.parse("content://com.xiaomi.mihomemanager.settingsProvider/settings");
    private static final Object sLock = new Object();

    private MiHomeManager(Context context) {
        this.isMiHomeManagerInstalled = true;
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        PackageManager packageManager = applicationContext.getPackageManager();
        this.mPm = packageManager;
        try {
            this.mPackageInfo = packageManager.getPackageInfo("com.xiaomi.mihomemanager", 8768);
        } catch (PackageManager.NameNotFoundException unused) {
            this.isMiHomeManagerInstalled = false;
            Log.w("MiHomeManager", "Exception when retrieving package:com.xiaomi.mihomemanager");
        }
    }

    public static MiHomeManager getInstance(Context context) {
        MiHomeManager miHomeManager;
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new MiHomeManager(context);
            }
            miHomeManager = sInstance;
        }
        return miHomeManager;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initSettingsForbiddenList() {
        Cursor query;
        this.mPreferenceSet = new HashSet<>();
        if (isAppExist(this.mContext, "com.xiaomi.mihomemanager") && SignaturesUtils.isSignaturesSameCurrentApp(this.mContext, "com.xiaomi.mihomemanager") && isSystemSignature() && (query = this.mContext.getContentResolver().query(CONTENT_URI_SETTINGS, null, null, null, null)) != null) {
            while (query.moveToNext()) {
                this.mPreferenceSet.add(query.getString(1));
            }
            query.close();
        }
    }

    private void initWhiteList() {
        Cursor query;
        this.mPackageSet = new HashSet<>();
        if (isAppExist(this.mContext, "com.xiaomi.mihomemanager") && SignaturesUtils.isSignaturesSameCurrentApp(this.mContext, "com.xiaomi.mihomemanager") && isSystemSignature() && (query = this.mContext.getContentResolver().query(CONTENT_URI_WHITELIST, null, null, null, null)) != null) {
            while (query.moveToNext()) {
                this.mPackageSet.add(query.getString(1));
            }
            query.close();
        }
    }

    public static boolean isAppExist(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            return context.getPackageManager().getApplicationInfo(str, 0) != null;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    private boolean isSystemSignature() {
        Signature[] signatureArr;
        try {
            PackageInfo packageInfo = this.mPm.getPackageInfo(ThemeResources.FRAMEWORK_PACKAGE, 64);
            PackageInfo packageInfo2 = this.mPackageInfo;
            if (packageInfo2 == null || (signatureArr = packageInfo2.signatures) == null) {
                return false;
            }
            return packageInfo.signatures[0].equals(signatureArr[0]);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("MiHomeManager", "Exception when getting system signature", e);
            return false;
        }
    }

    public boolean isForbidden(String str) {
        if (isSetEmpty(this.mPreferenceSet) && !this.mIsInitingSettingsForbiddenList) {
            this.mIsInitingSettingsForbiddenList = true;
            new Thread() { // from class: com.android.settings.cust.MiHomeManager.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    MiHomeManager.this.initSettingsForbiddenList();
                    MiHomeManager.this.mIsInitingSettingsForbiddenList = false;
                }
            }.start();
        }
        if (isSetEmpty(this.mPreferenceSet)) {
            return false;
        }
        return this.mPreferenceSet.contains(str);
    }

    public boolean isSetEmpty(HashSet<String> hashSet) {
        return hashSet == null || hashSet.size() == 0;
    }

    public boolean isWhiteListPackage(String str) {
        if (this.mPackageSet == null) {
            initWhiteList();
        }
        return this.mPackageSet.contains(str);
    }
}
