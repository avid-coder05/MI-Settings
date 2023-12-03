package miui.content.res;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.MiuiConfiguration;
import android.graphics.Bitmap;
import android.media.ExtraRingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import miui.app.constants.ThemeManagerConstants;
import miui.drm.DrmManager;
import miui.os.FileUtils;
import miui.reflect.Method;
import miui.system.R;

/* loaded from: classes3.dex */
public class ThemeRuntimeManager {
    public static final String BUILTIN_ALARM_PATH = "/system/media/audio/alarms/";
    public static final String BUILTIN_LOCKSCREEN_PATH = "/system/media/lockscreen/";
    public static final String BUILTIN_NOTIFICATION_PATH = "/system/media/audio/notifications/";
    public static final String BUILTIN_RINGTONE_PATH = "/system/media/audio/ringtones/";
    public static final String BUILTIN_ROOT_PATH = "/system/media/";
    public static final String BUILTIN_WALLPAPER_PATH = "/system/media/wallpaper/";
    public static final int DEFAULT_ALARM_FILE_PATH_RES_ID;
    public static final int DEFAULT_NOTIFICATION_FILE_PATH_RES_ID;
    public static final int DEFAULT_RINGTONE_FILE_PATH_RES_ID;
    public static final int DEFAULT_SMS_DELIVERED_SOUND_FILE_PATH_RES_ID;
    public static final int DEFAULT_SMS_RECEIVED_SOUND_FILE_PATH_RES_ID;
    public static final String RUNTIME_PATH_BOOT_ANIMATION;
    public static final String RUNTIME_PATH_LOCKSCREEN = "/data/system/theme/lock_wallpaper";
    public static final String RUNTIME_PATH_WALLPAPER = "/data/system/theme/wallpaper";
    public static final String RUNTIME_PIC_FOLDER = "/data/system/theme/";
    private static final int SAVE_ICON_MAX_SIZE = 163840;
    private static final String TAG = "ThemeRuntimeManager";
    private static final String TEMP_ICON_FOLDER;
    private static final String THEME_PACKAGE_NAME = "com.android.thememanager";
    private static Set<String> sWhiteList;
    private Context mContext;
    private Object mSecurityManager;
    private byte[] mServiceLocker = new byte[0];
    private Stack<Pair<String, Bitmap>> mPendingJobs = new Stack<>();
    private boolean mThreadFinished = true;
    private byte[] mJobLocker = new byte[0];

    /* loaded from: classes3.dex */
    private class ThemeServiceThread extends Thread {
        private ThemeServiceThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (true) {
                ThemeRuntimeManager.this.bindService();
                synchronized (ThemeRuntimeManager.this.mJobLocker) {
                    while (!ThemeRuntimeManager.this.mPendingJobs.isEmpty()) {
                        synchronized (ThemeRuntimeManager.this.mServiceLocker) {
                            if (ThemeRuntimeManager.this.mSecurityManager != null) {
                                Pair pair = (Pair) ThemeRuntimeManager.this.mPendingJobs.pop();
                                ThemeRuntimeManager.this.saveIconInner((String) pair.first, (Bitmap) pair.second);
                            }
                        }
                    }
                    try {
                        ThemeRuntimeManager.this.mJobLocker.wait(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (ThemeRuntimeManager.this.mPendingJobs.isEmpty()) {
                        ThemeRuntimeManager.this.unbindService();
                        ThemeRuntimeManager.this.mThreadFinished = true;
                        return;
                    }
                }
            }
        }
    }

    static {
        RUNTIME_PATH_BOOT_ANIMATION = Build.VERSION.SDK_INT > 19 ? "/data/system/theme/boots/bootanimation.zip" : "/data/local/bootanimation.zip";
        DEFAULT_RINGTONE_FILE_PATH_RES_ID = R.string.def_ringtone;
        DEFAULT_NOTIFICATION_FILE_PATH_RES_ID = R.string.def_notification_sound;
        DEFAULT_ALARM_FILE_PATH_RES_ID = R.string.def_alarm_alert;
        DEFAULT_SMS_DELIVERED_SOUND_FILE_PATH_RES_ID = R.string.def_sms_delivered_sound;
        DEFAULT_SMS_RECEIVED_SOUND_FILE_PATH_RES_ID = R.string.def_sms_received_sound;
        TEMP_ICON_FOLDER = ThemeResources.THEME_MAGIC_PATH + "tempIcon/";
        sWhiteList = new HashSet();
    }

    public ThemeRuntimeManager(Context context) {
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bindService() {
        Object systemService;
        synchronized (this.mServiceLocker) {
            systemService = this.mContext.getSystemService("security");
            this.mSecurityManager = systemService;
        }
        if (systemService == null) {
            Log.e(TAG, "can't bind SecurityManager");
        }
    }

    public static String createTempIconFile(Context context, String str, Bitmap bitmap) {
        String str2;
        FileOutputStream fileOutputStream;
        try {
            if (ThemeResources.FRAMEWORK_PACKAGE.equals(context.getPackageName())) {
                str2 = null;
                fileOutputStream = null;
            } else {
                str2 = context.getCacheDir() + "/" + str;
                fileOutputStream = getFileOutputStream(str2);
            }
            if (fileOutputStream == null) {
                String str3 = TEMP_ICON_FOLDER;
                new File(str3).mkdirs();
                str2 = str3 + str;
                fileOutputStream = getFileOutputStream(str2);
            }
            if (fileOutputStream == null) {
                Log.e(TAG, "can't get icon cache folder");
                return null;
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            if (new File(str2).exists()) {
                return str2;
            }
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    private boolean existIntentService(Intent intent) {
        List<ResolveInfo> queryIntentServices = this.mContext.getPackageManager().queryIntentServices(intent, 0);
        return (queryIntentServices == null || queryIntentServices.isEmpty()) ? false : true;
    }

    private static FileOutputStream getFileOutputStream(String str) {
        File file = new File(str);
        FileOutputStream fileOutputStream = null;
        try {
            FileOutputStream fileOutputStream2 = new FileOutputStream(file);
            try {
                FileUtils.chmod(file.getPath(), 436);
                return fileOutputStream2;
            } catch (FileNotFoundException unused) {
                fileOutputStream = fileOutputStream2;
                return fileOutputStream;
            }
        } catch (FileNotFoundException unused2) {
        }
    }

    private boolean isRestoreIndependentComponents() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveIconInner(String str, Bitmap bitmap) {
        Log.i(TAG, "saving icon for " + str);
        Method of = Method.of(this.mSecurityManager.getClass(), "saveIcon", Void.TYPE, new Class[]{String.class, Bitmap.class});
        if (of != null) {
            of.invoke(this.mSecurityManager.getClass(), this.mSecurityManager, new Object[]{str, bitmap});
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unbindService() {
        synchronized (this.mServiceLocker) {
            this.mSecurityManager = null;
        }
    }

    public void markGadgetUpdated() {
        long currentTimeMillis = System.currentTimeMillis();
        Settings.System.putLong(this.mContext.getContentResolver(), "clock_changed_time_" + ThemeManagerConstants.GADGET_SIZE_1x2, currentTimeMillis);
        Settings.System.putLong(this.mContext.getContentResolver(), "clock_changed_time_" + ThemeManagerConstants.GADGET_SIZE_2x2, currentTimeMillis);
        Settings.System.putLong(this.mContext.getContentResolver(), "clock_changed_time_" + ThemeManagerConstants.GADGET_SIZE_2x4, currentTimeMillis);
        Settings.System.putLong(this.mContext.getContentResolver(), "clock_changed_time_" + ThemeManagerConstants.GADGET_SIZE_4x4, currentTimeMillis);
        Settings.System.putLong(this.mContext.getContentResolver(), "clock_changed_time_" + ThemeManagerConstants.GADGET_SIZE_3x4, currentTimeMillis);
    }

    public void restoreDefault() {
        File file = new File("/data/system/theme/");
        if (file.exists() && file.isDirectory()) {
            for (File file2 : file.listFiles()) {
                if (!sWhiteList.contains(file2.getAbsolutePath())) {
                    ThemeNativeUtils.remove(file2.getAbsolutePath());
                }
            }
        }
        ThemeNativeUtils.remove(RUNTIME_PATH_BOOT_ANIMATION);
        Intent intent = new Intent(ThemeManagerConstants.ACTION_CLEAR_THEME_RUNTIME_DATA);
        intent.setPackage(THEME_PACKAGE_NAME);
        this.mContext.sendBroadcast(intent);
        try {
            ((WallpaperManager) this.mContext.getSystemService("wallpaper")).clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isRestoreIndependentComponents()) {
            ExtraRingtoneManager.saveDefaultSound(this.mContext, 1, Uri.fromFile(new File(this.mContext.getString(DEFAULT_RINGTONE_FILE_PATH_RES_ID))));
            ExtraRingtoneManager.saveDefaultSound(this.mContext, 2, Uri.fromFile(new File(this.mContext.getString(DEFAULT_NOTIFICATION_FILE_PATH_RES_ID))));
            ExtraRingtoneManager.saveDefaultSound(this.mContext, 4, Uri.fromFile(new File(this.mContext.getString(DEFAULT_ALARM_FILE_PATH_RES_ID))));
            ExtraRingtoneManager.saveDefaultSound(this.mContext, 8, Uri.fromFile(new File(this.mContext.getString(DEFAULT_SMS_DELIVERED_SOUND_FILE_PATH_RES_ID))));
            ExtraRingtoneManager.saveDefaultSound(this.mContext, 16, Uri.fromFile(new File(this.mContext.getString(DEFAULT_SMS_RECEIVED_SOUND_FILE_PATH_RES_ID))));
        }
        IconCustomizer.clearCustomizedIcons(null);
        ThemeResources.getSystem().resetIcons();
        markGadgetUpdated();
        DrmManager.setSupportAd(this.mContext, false);
        MiuiConfiguration.sendThemeConfigurationChangeMsg(268466329L);
    }

    public void saveIcon(String str, Bitmap bitmap) {
        try {
            if (Process.myUid() != 1000) {
                ApplicationInfo applicationInfo = this.mContext.getPackageManager().getApplicationInfo(this.mContext.getPackageName(), 0);
                if (applicationInfo == null) {
                    return;
                }
                if ((applicationInfo.flags & 1) != 1) {
                    return;
                }
            }
            if (bitmap != null && bitmap.getByteCount() > SAVE_ICON_MAX_SIZE) {
                Log.d(TAG, "saveIcon fail because icon bitmap is too large " + str);
                return;
            }
            synchronized (this.mJobLocker) {
                Log.i(TAG, "add pending job " + str);
                this.mPendingJobs.push(new Pair<>(str, bitmap));
                this.mJobLocker.notifyAll();
                if (this.mThreadFinished) {
                    this.mThreadFinished = false;
                    new ThemeServiceThread().start();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "fail to find package: " + this.mContext.getPackageName(), e);
        }
    }
}
