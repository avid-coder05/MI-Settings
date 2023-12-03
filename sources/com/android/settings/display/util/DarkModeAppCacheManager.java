package com.android.settings.display.util;

import android.app.ActivityThread;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
import com.google.gson.Gson;
import com.miui.darkmode.DarkModeAppData;
import com.miui.darkmode.DarkModeAppDetailInfo;
import com.miui.maml.util.AppIconsHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class DarkModeAppCacheManager {
    public static final String TAG = "com.android.settings.display.util.DarkModeAppCacheManager";
    private static DarkModeAppCacheManager sInstance;
    private Context mContext;
    private SoftReference<DarkModeAppData> mDarkModeAppDataReference;
    private HashMap<String, SoftReference<Drawable>> mMemoryDrawableCache = new HashMap<>();
    private final List<DarkModeAppDetailInfo> emptyDarkModeAppDetailInfo = new ArrayList();
    private IPackageManager mIPackageManager = ActivityThread.getPackageManager();
    private IBinder mUiModeService = ServiceManager.getService("uimode");

    private DarkModeAppCacheManager(Context context) {
        this.mContext = context.getApplicationContext();
    }

    private DarkModeAppData getCacheDarkModeAppData() {
        return parseJson(readDataFromFile("dark_mode_app_data.json"));
    }

    public static synchronized DarkModeAppCacheManager getInstance(Context context) {
        DarkModeAppCacheManager darkModeAppCacheManager;
        synchronized (DarkModeAppCacheManager.class) {
            if (sInstance == null) {
                sInstance = new DarkModeAppCacheManager(context);
            }
            darkModeAppCacheManager = sInstance;
        }
        return darkModeAppCacheManager;
    }

    private DarkModeAppData parseJson(String str) {
        try {
            return (DarkModeAppData) new Gson().fromJson(str, DarkModeAppData.class);
        } catch (Exception e) {
            Log.e(TAG, "parseJson error:" + e.toString());
            return null;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:34:0x009a, code lost:
    
        if (r8 == null) goto L36;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r2v1 */
    /* JADX WARN: Type inference failed for: r2v2 */
    /* JADX WARN: Type inference failed for: r2v3, types: [java.io.FileReader] */
    /* JADX WARN: Type inference failed for: r2v4, types: [java.io.FileReader] */
    /* JADX WARN: Type inference failed for: r2v5, types: [java.io.FileReader, java.io.Reader] */
    /* JADX WARN: Type inference failed for: r8v1, types: [java.io.File] */
    /* JADX WARN: Type inference failed for: r8v11 */
    /* JADX WARN: Type inference failed for: r8v3 */
    /* JADX WARN: Type inference failed for: r8v6, types: [java.io.BufferedReader] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String readDataFromFile(java.lang.String r8) {
        /*
            r7 = this;
            java.lang.String r0 = "close file error!"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            android.content.Context r7 = r7.mContext
            android.content.Context r7 = r7.getApplicationContext()
            java.io.File r7 = r7.getFilesDir()
            java.lang.String r7 = r7.getAbsolutePath()
            r2.append(r7)
            java.lang.String r7 = "/"
            r2.append(r7)
            r2.append(r8)
            java.lang.String r7 = r2.toString()
            java.io.File r8 = new java.io.File
            r8.<init>(r7)
            boolean r7 = r8.exists()
            if (r7 != 0) goto L37
            java.lang.String r7 = ""
            return r7
        L37:
            r7 = 0
            java.io.FileReader r2 = new java.io.FileReader     // Catch: java.lang.Throwable -> L6b java.io.IOException -> L70
            r2.<init>(r8)     // Catch: java.lang.Throwable -> L6b java.io.IOException -> L70
            java.io.BufferedReader r8 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> L61 java.io.IOException -> L66
            r8.<init>(r2)     // Catch: java.lang.Throwable -> L61 java.io.IOException -> L66
        L42:
            java.lang.String r7 = r8.readLine()     // Catch: java.io.IOException -> L5f java.lang.Throwable -> La2
            if (r7 == 0) goto L4c
            r1.append(r7)     // Catch: java.io.IOException -> L5f java.lang.Throwable -> La2
            goto L42
        L4c:
            r2.close()     // Catch: java.io.IOException -> L50
            goto L55
        L50:
            java.lang.String r7 = com.android.settings.display.util.DarkModeAppCacheManager.TAG
            android.util.Log.e(r7, r0)
        L55:
            r8.close()     // Catch: java.io.IOException -> L59
            goto L9d
        L59:
            java.lang.String r7 = com.android.settings.display.util.DarkModeAppCacheManager.TAG
            android.util.Log.e(r7, r0)
            goto L9d
        L5f:
            r7 = move-exception
            goto L74
        L61:
            r8 = move-exception
            r6 = r8
            r8 = r7
            r7 = r6
            goto La3
        L66:
            r8 = move-exception
            r6 = r8
            r8 = r7
            r7 = r6
            goto L74
        L6b:
            r8 = move-exception
            r2 = r7
            r7 = r8
            r8 = r2
            goto La3
        L70:
            r8 = move-exception
            r2 = r7
            r7 = r8
            r8 = r2
        L74:
            java.lang.String r3 = com.android.settings.display.util.DarkModeAppCacheManager.TAG     // Catch: java.lang.Throwable -> La2
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> La2
            r4.<init>()     // Catch: java.lang.Throwable -> La2
            java.lang.String r5 = "read file error:"
            r4.append(r5)     // Catch: java.lang.Throwable -> La2
            java.lang.String r7 = r7.toString()     // Catch: java.lang.Throwable -> La2
            r4.append(r7)     // Catch: java.lang.Throwable -> La2
            java.lang.String r7 = r4.toString()     // Catch: java.lang.Throwable -> La2
            android.util.Log.e(r3, r7)     // Catch: java.lang.Throwable -> La2
            if (r2 == 0) goto L9a
            r2.close()     // Catch: java.io.IOException -> L95
            goto L9a
        L95:
            java.lang.String r7 = com.android.settings.display.util.DarkModeAppCacheManager.TAG
            android.util.Log.e(r7, r0)
        L9a:
            if (r8 == 0) goto L9d
            goto L55
        L9d:
            java.lang.String r7 = r1.toString()
            return r7
        La2:
            r7 = move-exception
        La3:
            if (r2 == 0) goto Lae
            r2.close()     // Catch: java.io.IOException -> La9
            goto Lae
        La9:
            java.lang.String r1 = com.android.settings.display.util.DarkModeAppCacheManager.TAG
            android.util.Log.e(r1, r0)
        Lae:
            if (r8 == 0) goto Lb9
            r8.close()     // Catch: java.io.IOException -> Lb4
            goto Lb9
        Lb4:
            java.lang.String r8 = com.android.settings.display.util.DarkModeAppCacheManager.TAG
            android.util.Log.e(r8, r0)
        Lb9:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.display.util.DarkModeAppCacheManager.readDataFromFile(java.lang.String):java.lang.String");
    }

    private void saveDarkModeToCacheFile(DarkModeAppData darkModeAppData) {
        if (darkModeAppData != null) {
            writeFile(this.mContext.getApplicationContext().getFilesDir().getAbsolutePath() + "/dark_mode_app_data.json", new Gson().toJson(darkModeAppData));
        }
    }

    private void writeFile(String str, String str2) {
        FileOutputStream fileOutputStream;
        File file = new File(str);
        FileOutputStream fileOutputStream2 = null;
        try {
            try {
                try {
                    fileOutputStream = new FileOutputStream(file);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            } catch (IOException e2) {
                e = e2;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            fileOutputStream.write(str2.getBytes());
            fileOutputStream.close();
        } catch (IOException e3) {
            e = e3;
            fileOutputStream2 = fileOutputStream;
            e.printStackTrace();
            if (fileOutputStream2 != null) {
                fileOutputStream2.close();
            }
        } catch (Throwable th2) {
            th = th2;
            fileOutputStream2 = fileOutputStream;
            if (fileOutputStream2 != null) {
                try {
                    fileOutputStream2.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    public List<DarkModeAppDetailInfo> getDarkModeAppInfoList() {
        List<DarkModeAppDetailInfo> darkModeAppDetailInfoList;
        SoftReference<DarkModeAppData> softReference = this.mDarkModeAppDataReference;
        if (softReference == null || softReference.get() == null) {
            this.mDarkModeAppDataReference = new SoftReference<>(getCacheDarkModeAppData());
        }
        DarkModeAppData darkModeAppData = this.mDarkModeAppDataReference.get();
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            try {
                obtain.writeInterfaceToken("android.app.IUiModeManager");
                obtain.writeLong(darkModeAppData == null ? 0L : darkModeAppData.getCreateTime());
                obtain.writeInt(UserHandle.myUserId());
                IBinder iBinder = this.mUiModeService;
                if (iBinder != null && iBinder.transact(16777210, obtain, obtain2, 0)) {
                    obtain2.readException();
                    DarkModeAppData darkModeAppData2 = (DarkModeAppData) obtain2.readParcelable(null);
                    if (darkModeAppData2 == null) {
                        darkModeAppDetailInfoList = darkModeAppData != null ? darkModeAppData.getDarkModeAppDetailInfoList() : this.emptyDarkModeAppDetailInfo;
                    } else {
                        this.mDarkModeAppDataReference = new SoftReference<>(darkModeAppData2);
                        saveDarkModeToCacheFile(darkModeAppData2);
                        darkModeAppDetailInfoList = darkModeAppData2.getDarkModeAppDetailInfoList();
                    }
                    return darkModeAppDetailInfoList;
                }
            } catch (RemoteException e) {
                Log.e(TAG, "callBinderTransact android.app.IUiModeManager 16777210 failed. " + e);
            }
            return this.emptyDarkModeAppDetailInfo;
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    public Drawable loadAppIcon(Context context, String str, int i, ApplicationInfo applicationInfo, PackageManager packageManager) {
        SoftReference<Drawable> softReference = this.mMemoryDrawableCache.get(str);
        if (softReference == null || softReference.get() == null) {
            if (applicationInfo == null) {
                try {
                    applicationInfo = this.mIPackageManager.getApplicationInfo(str, 0, i);
                } catch (Exception unused) {
                    return null;
                }
            }
            if (packageManager == null) {
                packageManager = context.getPackageManager();
            }
            Drawable iconDrawable = applicationInfo != null ? AppIconsHelper.getIconDrawable(context, applicationInfo, packageManager) : null;
            if (iconDrawable != null) {
                this.mMemoryDrawableCache.put(str, new SoftReference<>(iconDrawable));
            }
            return iconDrawable;
        }
        return softReference.get();
    }

    public void setAppDarkMode(String str, boolean z) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            try {
                obtain.writeInterfaceToken("android.app.IUiModeManager");
                obtain.writeString(str);
                obtain.writeBoolean(z);
                obtain.writeInt(0);
                IBinder iBinder = this.mUiModeService;
                if (iBinder != null && iBinder.transact(16777214, obtain, obtain2, 0)) {
                    obtain2.readException();
                }
            } catch (RemoteException e) {
                Slog.e(TAG, "callBinderTransact android.app.IUiModeManager 16777214 failed. " + e);
            }
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }
}
