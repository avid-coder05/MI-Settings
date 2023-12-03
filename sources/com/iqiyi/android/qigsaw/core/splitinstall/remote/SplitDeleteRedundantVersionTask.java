package com.iqiyi.android.qigsaw.core.splitinstall.remote;

import android.content.Context;
import com.iqiyi.android.qigsaw.core.common.FileUtil;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitPathManager;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class SplitDeleteRedundantVersionTask implements Runnable {
    private static final int MAX_SPLIT_CACHE_SIZE = 1;
    private static final String TAG = "SplitDeleteRedundantVersionTask";
    private final Collection<SplitInfo> allSplits;
    private final Context appContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitDeleteRedundantVersionTask(Context context, Collection<SplitInfo> collection) {
        this.allSplits = collection;
        this.appContext = context;
    }

    private void deleteRedundantSplitVersionDirs(final File file, File file2, final File file3) {
        final String name = file2.getName();
        File[] listFiles = file2.listFiles(new FileFilter() { // from class: com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitDeleteRedundantVersionTask.1
            @Override // java.io.FileFilter
            public boolean accept(File file4) {
                if (!file4.isDirectory() || file4.equals(file)) {
                    return false;
                }
                SplitLog.i(SplitDeleteRedundantVersionTask.TAG, "Split %s version %s has been installed!", name, file4.getName());
                return file3.exists();
            }
        });
        if (listFiles == null || listFiles.length <= 1) {
            return;
        }
        Arrays.sort(listFiles, new Comparator<File>() { // from class: com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitDeleteRedundantVersionTask.2
            @Override // java.util.Comparator
            public int compare(File file4, File file5) {
                if (file4.lastModified() < file5.lastModified()) {
                    return 1;
                }
                return file4.lastModified() == file5.lastModified() ? 0 : -1;
            }
        });
        for (int i = 1; i < listFiles.length; i++) {
            SplitLog.i(TAG, "Split %s version %s is redundant, so we try to delete it", name, listFiles[i].getName());
            FileUtil.deleteDir(listFiles[i]);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        Collection<SplitInfo> collection = this.allSplits;
        if (collection != null) {
            for (SplitInfo splitInfo : collection) {
                File splitDir = SplitPathManager.require().getSplitDir(splitInfo);
                File splitRootDir = SplitPathManager.require().getSplitRootDir(splitInfo);
                try {
                    deleteRedundantSplitVersionDirs(splitDir, splitRootDir, SplitPathManager.require().getSplitMarkFile(splitInfo, splitInfo.obtainInstalledMark(this.appContext)));
                } catch (IOException unused) {
                }
            }
        }
    }
}
