package com.iqiyi.android.qigsaw.core.splitinstall;

import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import java.io.File;
import java.util.List;

/* loaded from: classes2.dex */
abstract class SplitInstaller {

    /* loaded from: classes2.dex */
    static final class InstallException extends Exception {
        private final int errorCode;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Illegal instructions before constructor call */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public InstallException(int r3, java.lang.Throwable r4) {
            /*
                r2 = this;
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r1 = 32
                r0.<init>(r1)
                java.lang.String r1 = "Split Install Error: "
                r0.append(r1)
                r0.append(r3)
                java.lang.String r0 = r0.toString()
                r2.<init>(r0, r4)
                r2.errorCode = r3
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.iqiyi.android.qigsaw.core.splitinstall.SplitInstaller.InstallException.<init>(int, java.lang.Throwable):void");
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public int getErrorCode() {
            return this.errorCode;
        }
    }

    /* loaded from: classes2.dex */
    static class InstallResult {
        final List<String> addedDexPaths;
        final File apkFile;
        final boolean firstInstalled;
        final File splitDexOptDir;
        final File splitLibDir;
        final String splitName;

        /* JADX INFO: Access modifiers changed from: package-private */
        public InstallResult(String str, File file, File file2, File file3, List<String> list, boolean z) {
            this.splitName = str;
            this.apkFile = file;
            this.splitDexOptDir = file2;
            this.splitLibDir = file3;
            this.addedDexPaths = list;
            this.firstInstalled = z;
        }
    }

    protected abstract void checkSplitMD5(File file, String str) throws InstallException;

    protected abstract boolean createInstalledMark(File file) throws InstallException;

    protected abstract boolean createInstalledMarkLock(File file, File file2) throws InstallException;

    protected abstract void extractLib(File file, File file2, SplitInfo.LibData libData) throws InstallException;

    protected abstract List<String> extractMultiDex(File file, File file2, SplitInfo splitInfo) throws InstallException;

    public abstract InstallResult install(boolean z, SplitInfo splitInfo) throws InstallException;

    protected abstract void verifySignature(File file) throws InstallException;
}
