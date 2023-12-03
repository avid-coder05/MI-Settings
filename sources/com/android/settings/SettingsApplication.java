package com.android.settings;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Process;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.PreferenceManager;
import com.android.settings.ShortcutHelper;
import com.android.settings.dangerousoptions.DangerousOptionsUtil;
import com.android.settings.device.MiuiAboutPhoneUtils;
import com.android.settings.deviceinfo.SDCardStateUploader;
import com.android.settings.personal.FullScreenDisplayController;
import com.android.settings.recommend.RecommendManager;
import com.android.settings.report.InternationalCompat;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.utils.TabletUtils;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import com.iqiyi.android.qigsaw.core.Qigsaw;
import java.util.List;
import miui.os.Build;
import miuix.autodensity.MiuixApplication;

/* loaded from: classes.dex */
public class SettingsApplication extends MiuixApplication {
    private static final String TAG = SettingsApplication.class.getSimpleName();
    private static boolean ENABLEQIGSAW = Log.isLoggable("HeadsetPluginDefault", 2);
    private static String HEADSETPLUGIN = "BLUETOOTHHEADSETPLUGIN";
    private static String HEADSETPLUGIN_INITED_NOTIFY = "BLUETOOTHHEADSETPLUGIN_INITED";
    private static int HEADSETPLUGIN_ENABLE = 1;
    private static int HEADSETPLUGIN_NOTSET = -1;
    private static int HEADSETPLUGIN_INITED = 1;
    private static String SP_QIGSAW_ENABLE = "sp_qigsaw_enable";
    public static final int PROC_USER_ID = Process.myUserHandle().hashCode();
    private final String TOPIC = "SECURITY_TOPIC";
    public boolean ENABLEQIGSAWINITED = false;
    public boolean mMainProcess = false;
    public int mQigsawStarted = HEADSETPLUGIN_NOTSET;

    /* loaded from: classes.dex */
    class InitTask extends AsyncTask<Void, Void, Boolean> {
        private Context mContext;

        public InitTask(Context context) {
            this.mContext = context;
        }

        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... voidArr) {
            if (SettingsFeatures.isAlienTablet()) {
                TabletUtils.attachApplication(this.mContext);
            }
            MiStatInterfaceUtils.initMiStatistics(SettingsApplication.this.getApplicationContext());
            OneTrackInterfaceUtils.init(SettingsApplication.this.getApplicationContext());
            SettingsApplication.this.ensureOpenSmMonitor();
            FullScreenDisplayController.initInfinityDisplaySettings(SettingsApplication.this.getApplicationContext());
            SettingsApplication.this.deleteV5Shortcuts(this.mContext);
            if (!"com.android.settings:remote".equals(SettingsApplication.this.getProcessName(this.mContext))) {
                RecommendManager.getInstance(SettingsApplication.this.getApplicationContext()).loadRecommendList();
            }
            if (!Build.IS_INTERNATIONAL_BUILD) {
                JobDispatcher.addJobToSchedule(44005);
                JobDispatcher.addJobToSchedule(44013);
            }
            if (SDCardStateUploader.canUploadSDCardState(this.mContext)) {
                new SDCardStateUploader(this.mContext).upload();
            }
            if (MiuiAboutPhoneUtils.enableShowCredentials()) {
                JobDispatcher.addJobToSchedule(44004);
            }
            if (DangerousOptionsUtil.isDangerousOptionsHintEnabled(this.mContext)) {
                JobDispatcher.addJobToSchedule(44011);
            }
            JobDispatcher.commit(this.mContext);
            ContentResolver contentResolver = this.mContext.getContentResolver();
            StringBuilder sb = new StringBuilder();
            sb.append(SettingsApplication.PROC_USER_ID);
            sb.append("#");
            sb.append(SettingsApplication.HEADSETPLUGIN);
            SettingsApplication.this.updateCloud(Settings.Global.getInt(contentResolver, sb.toString(), SettingsApplication.HEADSETPLUGIN_NOTSET) == SettingsApplication.HEADSETPLUGIN_ENABLE, this.mContext);
            return Boolean.TRUE;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean bool) {
        }
    }

    private boolean checkEnableQigsaw(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_QIGSAW_ENABLE, 0);
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean("enableQigsaw", false);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteV5Shortcuts(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (defaultSharedPreferences.getBoolean("key_delete_v5_shortcuts", false)) {
            return;
        }
        ShortcutHelper shortcutHelper = ShortcutHelper.getInstance(context.getApplicationContext());
        shortcutHelper.removeShortcut(ShortcutHelper.Shortcut.OPTIMIZE_CENTER);
        shortcutHelper.removeShortcut(ShortcutHelper.Shortcut.POWER_CENTER);
        shortcutHelper.removeShortcut(ShortcutHelper.Shortcut.VIRUS_CENTER);
        shortcutHelper.removeShortcut(ShortcutHelper.Shortcut.PERM_CENTER);
        shortcutHelper.removeShortcut(ShortcutHelper.Shortcut.NETWORK_ASSISTANT);
        shortcutHelper.removeShortcut(ShortcutHelper.Shortcut.ANTISPAM);
        defaultSharedPreferences.edit().putBoolean("key_delete_v5_shortcuts", true).commit();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureOpenSmMonitor() {
        Context applicationContext = getApplicationContext();
        if (!(Settings.System.getInt(applicationContext.getContentResolver(), "misettings_st_enable_sm", 0) == 1)) {
            Settings.System.putInt(applicationContext.getContentResolver(), "misettings_st_enable_sm", 1);
        }
        InternationalCompat.init(getApplicationContext());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCloud(boolean z, Context context) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SP_QIGSAW_ENABLE, 0);
            if (sharedPreferences != null) {
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putBoolean("enableQigsaw", z);
                edit.commit();
            }
            if (z) {
                ContentResolver contentResolver = context.getContentResolver();
                StringBuilder sb = new StringBuilder();
                int i = PROC_USER_ID;
                sb.append(i);
                sb.append("#");
                sb.append("bt_plugin_settings_miuix");
                String string = Settings.Global.getString(contentResolver, sb.toString());
                if (TextUtils.isEmpty(string) || !string.equals("settings_miuix_version_1")) {
                    Settings.Global.putString(context.getContentResolver(), i + "#bt_plugin_settings_miuix", "settings_miuix_version_1");
                }
            } else if (this.mMainProcess && (ENABLEQIGSAW || this.ENABLEQIGSAWINITED)) {
            } else {
                ContentResolver contentResolver2 = context.getContentResolver();
                StringBuilder sb2 = new StringBuilder();
                int i2 = PROC_USER_ID;
                sb2.append(i2);
                sb2.append("#");
                sb2.append("bt_plugin_settings_miuix");
                if (!TextUtils.isEmpty(Settings.Global.getString(contentResolver2, sb2.toString()))) {
                    Settings.Global.putString(context.getContentResolver(), i2 + "#bt_plugin_settings_miuix", "");
                }
                if (TextUtils.isEmpty(Settings.Global.getString(context.getContentResolver(), i2 + "#bt_plugin_settings_qigsaw"))) {
                    return;
                }
                Settings.Global.putString(context.getContentResolver(), i2 + "#bt_plugin_settings_qigsaw", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0025 A[Catch: Exception -> 0x0094, TRY_LEAVE, TryCatch #0 {Exception -> 0x0094, blocks: (B:3:0x0003, B:5:0x0011, B:7:0x0019, B:12:0x0021, B:14:0x0025), top: B:19:0x0003 }] */
    /* JADX WARN: Removed duplicated region for block: B:22:? A[RETURN, SYNTHETIC] */
    @Override // android.content.ContextWrapper
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void attachBaseContext(android.content.Context r6) {
        /*
            r5 = this;
            super.attachBaseContext(r6)
            java.lang.String r0 = "com.android.settings"
            java.lang.String r1 = android.app.Application.getProcessName()     // Catch: java.lang.Exception -> L94
            boolean r0 = r0.equals(r1)     // Catch: java.lang.Exception -> L94
            r5.mMainProcess = r0     // Catch: java.lang.Exception -> L94
            if (r0 == 0) goto L98
            boolean r0 = r5.checkEnableQigsaw(r6)     // Catch: java.lang.Exception -> L94
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L20
            boolean r0 = com.android.settings.SettingsApplication.ENABLEQIGSAW     // Catch: java.lang.Exception -> L94
            if (r0 == 0) goto L1e
            goto L20
        L1e:
            r0 = r1
            goto L21
        L20:
            r0 = r2
        L21:
            com.android.settings.SettingsApplication.ENABLEQIGSAW = r0     // Catch: java.lang.Exception -> L94
            if (r0 == 0) goto L98
            com.iqiyi.android.qigsaw.core.SplitConfiguration$Builder r0 = com.iqiyi.android.qigsaw.core.SplitConfiguration.newBuilder()     // Catch: java.lang.Exception -> L94
            com.iqiyi.android.qigsaw.core.SplitConfiguration$Builder r0 = r0.splitLoadMode(r2)     // Catch: java.lang.Exception -> L94
            com.android.settings.bluetooth.plugin.reporter.SampleLogger r3 = new com.android.settings.bluetooth.plugin.reporter.SampleLogger     // Catch: java.lang.Exception -> L94
            r3.<init>()     // Catch: java.lang.Exception -> L94
            com.iqiyi.android.qigsaw.core.SplitConfiguration$Builder r0 = r0.logger(r3)     // Catch: java.lang.Exception -> L94
            com.iqiyi.android.qigsaw.core.SplitConfiguration$Builder r0 = r0.verifySignature(r1)     // Catch: java.lang.Exception -> L94
            com.android.settings.bluetooth.plugin.reporter.SampleSplitLoadReporter r1 = new com.android.settings.bluetooth.plugin.reporter.SampleSplitLoadReporter     // Catch: java.lang.Exception -> L94
            r1.<init>(r5)     // Catch: java.lang.Exception -> L94
            com.iqiyi.android.qigsaw.core.SplitConfiguration$Builder r0 = r0.loadReporter(r1)     // Catch: java.lang.Exception -> L94
            com.android.settings.bluetooth.plugin.reporter.SampleSplitInstallReporter r1 = new com.android.settings.bluetooth.plugin.reporter.SampleSplitInstallReporter     // Catch: java.lang.Exception -> L94
            r1.<init>(r5)     // Catch: java.lang.Exception -> L94
            com.iqiyi.android.qigsaw.core.SplitConfiguration$Builder r0 = r0.installReporter(r1)     // Catch: java.lang.Exception -> L94
            com.android.settings.bluetooth.plugin.reporter.SampleSplitUninstallReporter r1 = new com.android.settings.bluetooth.plugin.reporter.SampleSplitUninstallReporter     // Catch: java.lang.Exception -> L94
            r1.<init>(r5)     // Catch: java.lang.Exception -> L94
            com.iqiyi.android.qigsaw.core.SplitConfiguration$Builder r0 = r0.uninstallReporter(r1)     // Catch: java.lang.Exception -> L94
            com.android.settings.bluetooth.plugin.reporter.SampleSplitUpdateReporter r1 = new com.android.settings.bluetooth.plugin.reporter.SampleSplitUpdateReporter     // Catch: java.lang.Exception -> L94
            r1.<init>(r5)     // Catch: java.lang.Exception -> L94
            com.iqiyi.android.qigsaw.core.SplitConfiguration$Builder r0 = r0.updateReporter(r1)     // Catch: java.lang.Exception -> L94
            com.iqiyi.android.qigsaw.core.SplitConfiguration r0 = r0.build()     // Catch: java.lang.Exception -> L94
            com.android.settings.bluetooth.plugin.downloader.SampleDownloader r1 = new com.android.settings.bluetooth.plugin.downloader.SampleDownloader     // Catch: java.lang.Exception -> L94
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L94
            r3.<init>()     // Catch: java.lang.Exception -> L94
            java.io.File r6 = r6.getFilesDir()     // Catch: java.lang.Exception -> L94
            java.lang.String r6 = r6.getAbsolutePath()     // Catch: java.lang.Exception -> L94
            r3.append(r6)     // Catch: java.lang.Exception -> L94
            java.lang.String r6 = java.io.File.separator     // Catch: java.lang.Exception -> L94
            r3.append(r6)     // Catch: java.lang.Exception -> L94
            java.lang.String r4 = "bluetooth"
            r3.append(r4)     // Catch: java.lang.Exception -> L94
            r3.append(r6)     // Catch: java.lang.Exception -> L94
            java.lang.String r6 = "plugins"
            r3.append(r6)     // Catch: java.lang.Exception -> L94
            java.lang.String r6 = r3.toString()     // Catch: java.lang.Exception -> L94
            r1.<init>(r6)     // Catch: java.lang.Exception -> L94
            com.iqiyi.android.qigsaw.core.Qigsaw.install(r5, r1, r0)     // Catch: java.lang.Exception -> L94
            r5.ENABLEQIGSAWINITED = r2     // Catch: java.lang.Exception -> L94
            goto L98
        L94:
            r5 = move-exception
            r5.printStackTrace()
        L98:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.SettingsApplication.attachBaseContext(android.content.Context):void");
    }

    public String getProcessName(Context context) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return null;
        }
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            if (runningAppProcessInfo.pid == myPid) {
                return runningAppProcessInfo.processName;
            }
        }
        return null;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Resources getResources() {
        if (this.mMainProcess && (ENABLEQIGSAW || this.ENABLEQIGSAWINITED)) {
            Qigsaw.onApplicationGetResources(super.getResources());
        }
        return super.getResources();
    }

    @Override // android.app.Application, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if ((configuration.screenLayout & 15) == 3) {
            TabletUtils.changeDeviceForm(1);
        } else {
            TabletUtils.changeDeviceForm(0);
        }
    }

    @Override // miuix.autodensity.MiuixApplication, android.app.Application
    public void onCreate() {
        super.onCreate();
        if (UserHandle.getAppId(Process.myUid()) != 1000) {
            return;
        }
        new InitTask(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        try {
            if (this.mMainProcess) {
                if (ENABLEQIGSAW || this.ENABLEQIGSAWINITED) {
                    Qigsaw.onApplicationCreated();
                    Settings.Global.putInt(getApplicationContext().getContentResolver(), PROC_USER_ID + "#" + HEADSETPLUGIN_INITED_NOTIFY, HEADSETPLUGIN_INITED);
                    this.mQigsawStarted = HEADSETPLUGIN_INITED;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // miuix.autodensity.IDensity
    public boolean shouldAdaptAutoDensity() {
        return true;
    }
}
