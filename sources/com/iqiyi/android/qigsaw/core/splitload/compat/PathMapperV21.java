package com.iqiyi.android.qigsaw.core.splitload.compat;

import android.content.Context;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.text.TextUtils;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitPathManager;
import java.io.File;
import java.io.FilenameFilter;

/* loaded from: classes2.dex */
class PathMapperV21 implements NativePathMapper {
    private static final int MAX_LIB_PATH = 128;
    private static final String TAG = "Split:PathMapper";
    private final File commonDir = SplitPathManager.require().getCommonSoDir();
    private final Context context;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PathMapperV21(Context context) {
        this.context = context;
    }

    private boolean checkIfNeedMapPath(String str) {
        File[] listFiles;
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        File file = new File(str);
        if (file.exists() && (listFiles = file.listFiles(new FilenameFilter() { // from class: com.iqiyi.android.qigsaw.core.splitload.compat.PathMapperV21.1
            @Override // java.io.FilenameFilter
            public boolean accept(File file2, String str2) {
                return !TextUtils.isEmpty(str2) && str2.endsWith(SplitConstants.DOT_SO);
            }
        })) != null && listFiles.length != 0) {
            for (File file2 : listFiles) {
                if (file2 != null && !TextUtils.isEmpty(file2.getAbsolutePath()) && file2.getAbsolutePath().length() >= 128) {
                    SplitLog.d(TAG, "need map native lib path: %s length >= %d", this.commonDir.getAbsolutePath(), 128);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSymlinkFileEqual(String str, String str2) {
        SplitLog.d(TAG, "isSymlinkFileEqual,  sourcePath: " + str + " targetPath: " + str2, new Object[0]);
        try {
            String readlink = Os.readlink(str2);
            SplitLog.d(TAG, "isSymlinkFileEqual,  sourcePath: " + str + " oldSourcePath: " + readlink, new Object[0]);
            if (!TextUtils.isEmpty(readlink)) {
                return readlink.equals(str);
            }
        } catch (ErrnoException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean symLink(File file, File file2, boolean z) {
        String absolutePath = file.getAbsolutePath();
        if (!file.exists()) {
            SplitLog.e(TAG, "symLink source: " + absolutePath + " not exist", new Object[0]);
            return false;
        }
        String absolutePath2 = file2.getAbsolutePath();
        if (file2.exists()) {
            if (isSymlinkFileEqual(absolutePath, absolutePath2)) {
                return true;
            }
            if (!file2.delete()) {
                SplitLog.e(TAG, "delete symLink target: " + absolutePath2 + " fail", new Object[0]);
                return false;
            }
        }
        try {
            Os.symlink(absolutePath, absolutePath2);
            SplitLog.d(TAG, "create symLink success from: " + absolutePath + " to: " + absolutePath2, new Object[0]);
            return true;
        } catch (Throwable th) {
            th.printStackTrace();
            if ((th instanceof ErrnoException) && th.errno == OsConstants.EEXIST) {
                SplitLog.d(TAG, "create symLink exist, from: " + absolutePath + " to: " + absolutePath2, new Object[0]);
                if (isSymlinkFileEqual(absolutePath, absolutePath2)) {
                    return true;
                }
                SplitLog.d(TAG, "delete exist symLink,  targetPath: " + absolutePath2, new Object[0]);
                file2.delete();
                if (!z) {
                    return symLink(file, file2, true);
                }
            }
            return false;
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.compat.NativePathMapper
    public String map(String str, String str2) {
        if (!checkIfNeedMapPath(str2)) {
            SplitLog.d(TAG, "do not need map native lib path: %s", str2);
            return str2;
        } else if (this.commonDir.exists() || this.commonDir.mkdirs()) {
            File file = new File(this.commonDir, str);
            return symLink(new File(str2), file, false) ? file.getAbsolutePath() : str2;
        } else {
            SplitLog.d(TAG, "mkdir: %s failed", this.commonDir.getAbsolutePath());
            return str2;
        }
    }
}
