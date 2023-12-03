package com.iqiyi.android.qigsaw.core.splitinstall;

import com.iqiyi.android.qigsaw.core.common.FileUtil;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitPathManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/* loaded from: classes2.dex */
public final class SplitPendingUninstallManager {
    private static final String PENDING_UNINSTALL_SPLITS = "pendingUninstallSplits";
    private static final String TAG = "PendingUninstallSplitsManager";
    private static final String VERSION_DATA_NAME = "uninstallsplits.info";
    private static final Object sLock = new Object();
    private final File pendingUninstallSplitsFile = new File(SplitPathManager.require().getUninstallSplitsDir(), VERSION_DATA_NAME);

    private List<String> readPendingUninstallSplitsInternal(File file) {
        FileInputStream fileInputStream;
        Throwable th;
        ArrayList arrayList;
        IOException e;
        ArrayList arrayList2 = null;
        int i = 0;
        boolean z = false;
        while (i < 3 && !z) {
            i++;
            Properties properties = new Properties();
            try {
                fileInputStream = new FileInputStream(file);
                try {
                    try {
                        properties.load(fileInputStream);
                        String property = properties.getProperty(PENDING_UNINSTALL_SPLITS);
                        if (property != null) {
                            String[] split = property.split(",");
                            arrayList = new ArrayList();
                            try {
                                Collections.addAll(arrayList, split);
                                arrayList2 = arrayList;
                            } catch (IOException e2) {
                                e = e2;
                                SplitLog.w(TAG, "read property failed, e:" + e, new Object[0]);
                                FileUtil.closeQuietly(fileInputStream);
                                arrayList2 = arrayList;
                            }
                        }
                        FileUtil.closeQuietly(fileInputStream);
                        z = true;
                    } catch (IOException e3) {
                        e = e3;
                        arrayList = arrayList2;
                        e = e;
                        SplitLog.w(TAG, "read property failed, e:" + e, new Object[0]);
                        FileUtil.closeQuietly(fileInputStream);
                        arrayList2 = arrayList;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    FileUtil.closeQuietly(fileInputStream);
                    throw th;
                }
            } catch (IOException e4) {
                e = e4;
                fileInputStream = null;
            } catch (Throwable th3) {
                fileInputStream = null;
                th = th3;
            }
        }
        return arrayList2;
    }

    /* JADX WARN: Removed duplicated region for block: B:50:0x00ef A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0080 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean recordPendingUninstallSplitsInternal(java.io.File r9, java.util.List<java.lang.String> r10) {
        /*
            Method dump skipped, instructions count: 249
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.iqiyi.android.qigsaw.core.splitinstall.SplitPendingUninstallManager.recordPendingUninstallSplitsInternal(java.io.File, java.util.List):boolean");
    }

    public boolean deletePendingUninstallSplitsRecord() {
        synchronized (sLock) {
            if (this.pendingUninstallSplitsFile.exists()) {
                return FileUtil.deleteFileSafely(this.pendingUninstallSplitsFile);
            }
            return true;
        }
    }

    public List<String> readPendingUninstallSplits() {
        synchronized (sLock) {
            if (this.pendingUninstallSplitsFile.exists()) {
                return readPendingUninstallSplitsInternal(this.pendingUninstallSplitsFile);
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean recordPendingUninstallSplits(List<String> list) {
        boolean recordPendingUninstallSplitsInternal;
        synchronized (sLock) {
            recordPendingUninstallSplitsInternal = recordPendingUninstallSplitsInternal(this.pendingUninstallSplitsFile, list);
        }
        return recordPendingUninstallSplitsInternal;
    }
}
