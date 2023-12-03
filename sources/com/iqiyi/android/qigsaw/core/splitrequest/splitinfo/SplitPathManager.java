package com.iqiyi.android.qigsaw.core.splitrequest.splitinfo;

import android.content.Context;
import com.iqiyi.android.qigsaw.core.common.FileUtil;
import com.iqiyi.android.qigsaw.core.common.SplitBaseInfoProvider;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import java.io.File;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes2.dex */
public final class SplitPathManager {
    private static final String COMMON_SO_DIR_NAME = "common_so";
    private static final String TAG = "SplitPathManager";
    private static final AtomicReference<SplitPathManager> sSplitPathManagerRef = new AtomicReference<>();
    private final File baseRootDir;
    private File commonSoDir;
    private final String qigsawId;
    private final File rootDir;

    private SplitPathManager(File file, String str) {
        this.baseRootDir = file;
        this.rootDir = new File(file, str);
        this.qigsawId = str;
    }

    private static SplitPathManager create(Context context) {
        return new SplitPathManager(context.getDir(SplitConstants.QIGSAW, 0), SplitBaseInfoProvider.getQigsawId());
    }

    public static void install(Context context) {
        sSplitPathManagerRef.compareAndSet(null, create(context));
    }

    public static SplitPathManager require() {
        AtomicReference<SplitPathManager> atomicReference = sSplitPathManagerRef;
        if (atomicReference.get() != null) {
            return atomicReference.get();
        }
        throw new RuntimeException("SplitPathManager must be initialized firstly!");
    }

    public void cleanMiCache(Context context) {
        int i;
        Collection<SplitInfo> allSplitInfo = SplitInfoManagerService.getInstance().getAllSplitInfo(context);
        int i2 = 0;
        if (allSplitInfo == null) {
            SplitLog.e(TAG, "cleanMiCache splitInfoList is null!", new Object[0]);
            return;
        }
        File[] listFiles = this.rootDir.getParentFile().listFiles();
        if (listFiles == null || listFiles.length <= 0) {
            return;
        }
        for (File file : listFiles) {
            for (SplitInfo splitInfo : allSplitInfo) {
                File file2 = new File(file, splitInfo.getSplitName());
                if (file2.exists() && file2.isDirectory()) {
                    File[] listFiles2 = file2.listFiles();
                    if (listFiles2 != null && listFiles2.length > 0) {
                        int length = listFiles2.length;
                        for (int i3 = i2; i3 < length; i3++) {
                            File file3 = listFiles2[i3];
                            String name = file3.getName();
                            if (!name.equals(splitInfo.getSplitVersion())) {
                                SplitLog.i(TAG, "cleanMiCache clean dir '" + name + "' in '" + file2 + "'because it is not current version.", new Object[0]);
                                FileUtil.deleteDir(file3);
                            }
                        }
                    }
                    i = 0;
                } else {
                    i = 0;
                    SplitLog.e(TAG, "cleanMiCache " + splitInfo.getSplitName() + " dir not found!!", new Object[0]);
                }
                i2 = i;
            }
        }
    }

    public void clearCache() {
        File[] listFiles = this.rootDir.getParentFile().listFiles();
        if (listFiles == null || listFiles.length <= 0) {
            return;
        }
        for (File file : listFiles) {
            String name = file.getName();
            if (file.isDirectory() && !name.equals(this.qigsawId) && !name.equals(COMMON_SO_DIR_NAME)) {
                FileUtil.deleteDir(file);
                SplitLog.i(TAG, "Success to delete all obsolete splits for current app version!", new Object[0]);
            }
        }
    }

    public File getCommonSoDir() {
        if (this.commonSoDir == null) {
            File file = new File(this.baseRootDir, COMMON_SO_DIR_NAME);
            this.commonSoDir = file;
            if (!file.exists()) {
                this.commonSoDir.mkdirs();
            }
        }
        return this.commonSoDir;
    }

    public File getSplitCodeCacheDir(SplitInfo splitInfo) {
        File file = new File(getSplitDir(splitInfo), "code_cache");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public File getSplitDir(SplitInfo splitInfo) {
        File file = new File(getSplitRootDir(splitInfo), splitInfo.getSplitVersion());
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public File getSplitLibDir(SplitInfo splitInfo, String str) {
        File file = new File(getSplitDir(splitInfo), "nativeLib" + File.separator + str);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public File getSplitMarkFile(SplitInfo splitInfo, String str) {
        return new File(getSplitDir(splitInfo), str);
    }

    public File getSplitOptDir(SplitInfo splitInfo) {
        File file = new File(getSplitDir(splitInfo), "oat");
        if (!file.exists() && file.mkdirs()) {
            file.setWritable(true);
            file.setReadable(true);
        }
        return file;
    }

    public File getSplitRootDir(SplitInfo splitInfo) {
        File file = new File(this.rootDir, splitInfo.getSplitName());
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public File getSplitSpecialLockFile(SplitInfo splitInfo) {
        return new File(getSplitDir(splitInfo), "ov.lock");
    }

    public File getSplitSpecialMarkFile(SplitInfo splitInfo, String str) {
        return new File(getSplitDir(splitInfo), str + ".ov");
    }

    public File getSplitTmpDir() {
        File file = new File(this.rootDir, "tmp");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public File getUninstallSplitsDir() {
        File file = new File(this.rootDir, "uninstall");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }
}
