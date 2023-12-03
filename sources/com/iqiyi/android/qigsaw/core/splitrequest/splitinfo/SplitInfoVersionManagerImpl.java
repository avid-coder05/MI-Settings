package com.iqiyi.android.qigsaw.core.splitrequest.splitinfo;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import com.iqiyi.android.qigsaw.core.common.FileUtil;
import com.iqiyi.android.qigsaw.core.common.SplitBaseInfoProvider;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitreport.SplitUpdateReporter;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class SplitInfoVersionManagerImpl implements SplitInfoVersionManager {
    public static final int PROC_USER_ID = Process.myUserHandle().hashCode();
    private static final String TAG = "SplitInfoVersionManager";
    private String currentVersion;
    private String defaultVersion;
    private boolean isMainProcess;
    private File rootDir;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class RunableTask implements Runnable {
        private static final String SETTINGS_QIGSAW_VERSION = "bt_plugin_settings_qigsaw";
        private WeakReference<Context> mContext;
        private String mCurrentVersion;

        public RunableTask(Context context, String str) {
            this.mContext = null;
            this.mCurrentVersion = null;
            this.mContext = new WeakReference<>(context);
            this.mCurrentVersion = str;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                WeakReference<Context> weakReference = this.mContext;
                if (weakReference == null || weakReference.get() == null) {
                    return;
                }
                Context context = this.mContext.get();
                ContentResolver contentResolver = context.getContentResolver();
                StringBuilder sb = new StringBuilder();
                int i = SplitInfoVersionManagerImpl.PROC_USER_ID;
                sb.append(i);
                sb.append("#");
                sb.append(SETTINGS_QIGSAW_VERSION);
                String string = Settings.Global.getString(contentResolver, sb.toString());
                if (TextUtils.isEmpty(this.mCurrentVersion)) {
                    return;
                }
                if (TextUtils.isEmpty(string) || !string.equals(this.mCurrentVersion)) {
                    Settings.Global.putString(context.getContentResolver(), i + "#" + SETTINGS_QIGSAW_VERSION, this.mCurrentVersion);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private SplitInfoVersionManagerImpl(Context context, boolean z, String str, String str2) {
        this.defaultVersion = str;
        this.isMainProcess = z;
        this.rootDir = new File(new File(context.getDir(SplitConstants.QIGSAW, 0), str2), SplitInfoVersionManager.SPLIT_ROOT_DIR_NAME);
        processVersionData(context);
        reportNewSplitInfoVersionLoaded();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SplitInfoVersionManager createSplitInfoVersionManager(Context context, boolean z) {
        return new SplitInfoVersionManagerImpl(context, z, SplitBaseInfoProvider.getDefaultSplitInfoVersion(), SplitBaseInfoProvider.getQigsawId());
    }

    private int customStrCompare(String str, String str2) {
        int length;
        int length2;
        if (TextUtils.isEmpty(str) && TextUtils.isEmpty(str2)) {
            return 0;
        }
        if (TextUtils.isEmpty(str)) {
            return 1;
        }
        if (!TextUtils.isEmpty(str2) && (length = str.length()) >= (length2 = str2.length())) {
            if (length > length2) {
                return 1;
            }
            return str.compareTo(str2);
        }
        return -1;
    }

    private void processVersionData(Context context) {
        SplitInfoVersionData readVersionData = readVersionData();
        if (readVersionData == null) {
            SplitLog.i(TAG, "No new split info version, just use default version.", new Object[0]);
            this.currentVersion = this.defaultVersion;
        } else {
            String str = readVersionData.oldVersion;
            String str2 = readVersionData.newVersion;
            if (str.equals(str2)) {
                SplitLog.i(TAG, "Splits have been updated, so we use new split info version %s.", str2);
                this.currentVersion = str2;
            } else if (!this.isMainProcess) {
                this.currentVersion = str;
            } else if (updateVersionData(new SplitInfoVersionData(str2, str2))) {
                this.currentVersion = str2;
                SplitLog.i(TAG, "Splits have been updated, start to kill other processes!", new Object[0]);
            } else {
                this.currentVersion = str;
                SplitLog.w(TAG, "Failed to update new split info version: " + str2, new Object[0]);
            }
        }
        if (useBasePlugin(this.defaultVersion, this.currentVersion)) {
            this.currentVersion = this.defaultVersion;
        }
        new Thread(new RunableTask(context, this.currentVersion)).start();
    }

    private SplitInfoVersionData readVersionData() {
        try {
            SplitInfoVersionDataStorageImpl splitInfoVersionDataStorageImpl = new SplitInfoVersionDataStorageImpl(this.rootDir);
            SplitInfoVersionData readVersionData = splitInfoVersionDataStorageImpl.readVersionData();
            FileUtil.closeQuietly(splitInfoVersionDataStorageImpl);
            return readVersionData;
        } catch (IOException unused) {
            return null;
        }
    }

    private void reportNewSplitInfoVersionLoaded() {
        SplitUpdateReporter updateReporter;
        if (!this.isMainProcess || TextUtils.equals(this.currentVersion, this.defaultVersion) || (updateReporter = SplitUpdateReporterManager.getUpdateReporter()) == null) {
            return;
        }
        updateReporter.onNewSplitInfoVersionLoaded(this.currentVersion);
    }

    private boolean updateVersionData(SplitInfoVersionData splitInfoVersionData) {
        try {
            SplitInfoVersionDataStorageImpl splitInfoVersionDataStorageImpl = new SplitInfoVersionDataStorageImpl(this.rootDir);
            boolean updateVersionData = splitInfoVersionDataStorageImpl.updateVersionData(splitInfoVersionData);
            FileUtil.closeQuietly(splitInfoVersionDataStorageImpl);
            return updateVersionData;
        } catch (IOException unused) {
            return false;
        }
    }

    private boolean useBasePlugin(String str, String str2) {
        if (str == null || str2 == null) {
            SplitLog.e(TAG, "useBasePlugin invalid paras:" + str + " " + str2, new Object[0]);
            return true;
        } else if (str.length() < 7 || str2.length() < 7) {
            SplitLog.e(TAG, "useBasePlugin invalid paras:\"" + str + "\" \"" + str2 + "\"", new Object[0]);
            return true;
        } else {
            try {
                String[] split = str.split("_");
                if (split.length != 2) {
                    SplitLog.e(TAG, "useBasePlugin invalid base version:" + str, new Object[0]);
                    return true;
                }
                String[] split2 = str2.split("_");
                if (split2.length != 2) {
                    SplitLog.e(TAG, "useBasePlugin invalid new version:" + str2, new Object[0]);
                    return true;
                }
                if (split[0].equals(split2[0])) {
                    String[] split3 = split[1].split("\\.");
                    if (split3.length != 2) {
                        SplitLog.e(TAG, "useBasePlugin invalid base split version:" + split[1], new Object[0]);
                        return true;
                    }
                    String[] split4 = split2[1].split("\\.");
                    if (split4.length != 2) {
                        SplitLog.e(TAG, "useBasePlugin invalid new split version:" + split2[1], new Object[0]);
                        return true;
                    } else if (customStrCompare(split3[0], split4[0]) < 0) {
                        return false;
                    } else {
                        if (customStrCompare(split3[0], split4[0]) <= 0 && customStrCompare(split3[1], split4[1]) < 0) {
                            return false;
                        }
                    }
                }
                return true;
            } catch (Exception e) {
                SplitLog.e(TAG, "checkVersion split failed:" + e, new Object[0]);
                return true;
            }
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoVersionManager
    public String getCurrentVersion() {
        return this.currentVersion;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoVersionManager
    public String getDefaultVersion() {
        return this.defaultVersion;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoVersionManager
    public File getRootDir() {
        return this.rootDir;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoVersionManager
    public boolean updateVersion(Context context, String str, File file) {
        boolean z;
        if (!this.rootDir.exists() && !this.rootDir.mkdirs()) {
            SplitLog.w(TAG, "Failed to make dir for split info file!", new Object[0]);
            return false;
        }
        try {
            FileUtil.copyFile(file, new File(this.rootDir, SplitConstants.QIGSAW_PREFIX + str + SplitConstants.DOT_JSON));
            z = true;
            if (updateVersionData(new SplitInfoVersionData(this.currentVersion, str))) {
                SplitLog.i(TAG, "Success to update split info version, current version %s, new version %s", this.currentVersion, str);
            } else {
                z = false;
            }
        } catch (IOException e) {
            e = e;
            z = false;
        }
        try {
            if (file.exists() && !file.delete()) {
                SplitLog.w(TAG, "Failed to delete temp split info file: " + file.getAbsolutePath(), new Object[0]);
            }
        } catch (IOException e2) {
            e = e2;
            SplitLog.printErrStackTrace(TAG, e, "Failed to rename file : " + file.getAbsolutePath(), new Object[0]);
            return z;
        }
        return z;
    }
}
