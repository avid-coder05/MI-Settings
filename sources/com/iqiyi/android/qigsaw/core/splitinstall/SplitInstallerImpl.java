package com.iqiyi.android.qigsaw.core.splitinstall;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.iqiyi.android.qigsaw.core.common.FileUtil;
import com.iqiyi.android.qigsaw.core.common.OEMCompat;
import com.iqiyi.android.qigsaw.core.common.SplitBaseInfoProvider;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitinstall.SplitInstaller;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitPathManager;
import dalvik.system.DexClassLoader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
final class SplitInstallerImpl extends SplitInstaller {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final boolean IS_VM_MULTIDEX_CAPABLE = isVMMultiDexCapable(System.getProperty("java.vm.version"));
    private static final String TAG = "SplitInstallerImpl";
    private final Context appContext;
    private final boolean verifySignature;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitInstallerImpl(Context context, boolean z) {
        this.appContext = context;
        this.verifySignature = z;
    }

    private void deleteCorruptedFiles(List<File> list) {
        Iterator<File> it = list.iterator();
        while (it.hasNext()) {
            FileUtil.deleteFileSafely(it.next());
        }
    }

    private boolean isVMMultiDexCapable() {
        return IS_VM_MULTIDEX_CAPABLE;
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0029, code lost:
    
        if (r1 < 1) goto L12;
     */
    /* JADX WARN: Removed duplicated region for block: B:15:0x003c  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x003f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static boolean isVMMultiDexCapable(java.lang.String r5) {
        /*
            r0 = 0
            if (r5 == 0) goto L2c
            java.lang.String r1 = "(\\d+)\\.(\\d+)(\\.\\d+)?"
            java.util.regex.Pattern r1 = java.util.regex.Pattern.compile(r1)
            java.util.regex.Matcher r1 = r1.matcher(r5)
            boolean r2 = r1.matches()
            if (r2 == 0) goto L2c
            r2 = 1
            java.lang.String r3 = r1.group(r2)     // Catch: java.lang.NumberFormatException -> L2c
            int r3 = java.lang.Integer.parseInt(r3)     // Catch: java.lang.NumberFormatException -> L2c
            r4 = 2
            java.lang.String r1 = r1.group(r4)     // Catch: java.lang.NumberFormatException -> L2c
            int r1 = java.lang.Integer.parseInt(r1)     // Catch: java.lang.NumberFormatException -> L2c
            if (r3 > r4) goto L2d
            if (r3 != r4) goto L2c
            if (r1 < r2) goto L2c
            goto L2d
        L2c:
            r2 = r0
        L2d:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "VM with version "
            r1.append(r3)
            r1.append(r5)
            if (r2 == 0) goto L3f
            java.lang.String r5 = " has multidex support"
            goto L41
        L3f:
            java.lang.String r5 = " does not have multidex support"
        L41:
            r1.append(r5)
            java.lang.String r5 = r1.toString()
            java.lang.Object[] r0 = new java.lang.Object[r0]
            java.lang.String r1 = "Split:MultiDex"
            com.iqiyi.android.qigsaw.core.common.SplitLog.i(r1, r5, r0)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.iqiyi.android.qigsaw.core.splitinstall.SplitInstallerImpl.isVMMultiDexCapable(java.lang.String):boolean");
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstaller
    protected void checkSplitMD5(File file, String str) throws SplitInstaller.InstallException {
        String md5 = FileUtil.getMD5(file);
        if (str.equals(md5)) {
            return;
        }
        deleteCorruptedFiles(Collections.singletonList(file));
        throw new SplitInstaller.InstallException(-13, new IOException("Failed to check split apk md5, expect " + str + " but " + md5));
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstaller
    protected boolean createInstalledMark(File file) throws SplitInstaller.InstallException {
        if (file.exists()) {
            return false;
        }
        try {
            FileUtil.createFileSafely(file);
            return true;
        } catch (IOException e) {
            throw new SplitInstaller.InstallException(-16, e);
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstaller
    protected boolean createInstalledMarkLock(File file, File file2) throws SplitInstaller.InstallException {
        if (file.exists()) {
            return false;
        }
        try {
            FileUtil.createFileSafelyLock(file, file2);
            return true;
        } catch (IOException e) {
            throw new SplitInstaller.InstallException(-16, e);
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstaller
    protected void extractLib(File file, File file2, SplitInfo.LibData libData) throws SplitInstaller.InstallException {
        try {
            SplitLibExtractor splitLibExtractor = new SplitLibExtractor(file, file2);
            try {
                try {
                    SplitLog.i(TAG, "Succeed to extract libs:  %s", splitLibExtractor.load(libData, false).toString());
                } catch (IOException e) {
                    SplitLog.w(TAG, "Failed to load or extract lib files", e);
                    throw new SplitInstaller.InstallException(-15, e);
                }
            } finally {
                FileUtil.closeQuietly(splitLibExtractor);
            }
        } catch (IOException e2) {
            throw new SplitInstaller.InstallException(-15, e2);
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstaller
    protected List<String> extractMultiDex(File file, File file2, SplitInfo splitInfo) throws SplitInstaller.InstallException {
        SplitLog.w(TAG, "VM do not support multi-dex, but split %s has multi dex files, so we need install other dex files manually", file.getName());
        String str = splitInfo.getSplitName() + "@" + SplitBaseInfoProvider.getVersionName() + "@" + splitInfo.getSplitVersion();
        try {
            SplitMultiDexExtractor splitMultiDexExtractor = new SplitMultiDexExtractor(file, file2);
            try {
                try {
                    List<? extends File> load = splitMultiDexExtractor.load(this.appContext, str, false);
                    ArrayList arrayList = new ArrayList(load.size());
                    Iterator<? extends File> it = load.iterator();
                    while (it.hasNext()) {
                        arrayList.add(it.next().getAbsolutePath());
                    }
                    SplitLog.w(TAG, "Succeed to load or extract dex files", load.toString());
                    return arrayList;
                } catch (IOException e) {
                    SplitLog.w(TAG, "Failed to load or extract dex files", e);
                    throw new SplitInstaller.InstallException(-14, e);
                }
            } finally {
                FileUtil.closeQuietly(splitMultiDexExtractor);
            }
        } catch (IOException e2) {
            throw new SplitInstaller.InstallException(-14, e2);
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstaller
    public SplitInstaller.InstallResult install(boolean z, SplitInfo splitInfo) throws SplitInstaller.InstallException {
        File file;
        File splitDir = SplitPathManager.require().getSplitDir(splitInfo);
        try {
            List<SplitInfo.ApkData> apkDataList = splitInfo.getApkDataList(this.appContext);
            SplitInfo.LibData primaryLibData = splitInfo.getPrimaryLibData(this.appContext);
            String obtainInstalledMark = splitInfo.obtainInstalledMark(this.appContext);
            File splitMarkFile = SplitPathManager.require().getSplitMarkFile(splitInfo, obtainInstalledMark);
            File file2 = null;
            File file3 = null;
            ArrayList arrayList = null;
            File file4 = null;
            for (SplitInfo.ApkData apkData : apkDataList) {
                File file5 = (splitInfo.isBuiltIn() && apkData.getUrl().startsWith(SplitConstants.URL_NATIVE)) ? new File(this.appContext.getApplicationInfo().nativeLibraryDir, System.mapLibraryName(SplitConstants.SPLIT_PREFIX + splitInfo.getSplitName())) : new File(splitDir, splitInfo.getSplitName() + "-" + apkData.getAbi() + SplitConstants.DOT_APK);
                if (!FileUtil.isLegalFile(file5)) {
                    throw new SplitInstaller.InstallException(-11, new FileNotFoundException("Split apk " + file5.getAbsolutePath() + " is illegal!"));
                }
                if (this.verifySignature) {
                    SplitLog.d(TAG, "Need to verify split %s signature!", file5.getAbsolutePath());
                    verifySignature(file5);
                }
                checkSplitMD5(file5, apkData.getMd5());
                if (SplitConstants.MASTER.equals(apkData.getAbi())) {
                    if (splitInfo.hasDex()) {
                        File splitOptDir = SplitPathManager.require().getSplitOptDir(splitInfo);
                        ArrayList arrayList2 = new ArrayList();
                        arrayList2.add(file5.getAbsolutePath());
                        if (!isVMMultiDexCapable() && splitInfo.isMultiDex()) {
                            arrayList2.addAll(extractMultiDex(file5, SplitPathManager.require().getSplitCodeCacheDir(splitInfo), splitInfo));
                        }
                        String join = TextUtils.join(File.pathSeparator, arrayList2);
                        String absolutePath = file4 == null ? null : file4.getAbsolutePath();
                        if (!splitMarkFile.exists()) {
                            try {
                                new DexClassLoader(join, splitOptDir.getAbsolutePath(), absolutePath, SplitInstallerImpl.class.getClassLoader());
                            } catch (Throwable th) {
                                throw new SplitInstaller.InstallException(-17, th);
                            }
                        }
                        if (OEMCompat.shouldCheckOatFileInCurrentSys()) {
                            SplitLog.v(TAG, "Start to check oat file, current api level is " + Build.VERSION.SDK_INT, new Object[0]);
                            boolean isSpecialManufacturer = OEMCompat.isSpecialManufacturer();
                            File oatFilePath = OEMCompat.getOatFilePath(file5, splitOptDir);
                            if (FileUtil.isLegalFile(oatFilePath)) {
                                boolean checkOatFile = OEMCompat.checkOatFile(oatFilePath);
                                file = splitDir;
                                SplitLog.v(TAG, "Result of oat file %s is " + checkOatFile, oatFilePath.getAbsoluteFile());
                                if (!checkOatFile) {
                                    SplitLog.w(TAG, "Failed to check oat file " + oatFilePath.getAbsolutePath(), new Object[0]);
                                    if (isSpecialManufacturer) {
                                        try {
                                            FileUtil.deleteFileSafelyLock(oatFilePath, SplitPathManager.require().getSplitSpecialLockFile(splitInfo));
                                        } catch (IOException unused) {
                                            SplitLog.w(TAG, "Failed to delete corrupted oat file " + oatFilePath.exists(), new Object[0]);
                                        }
                                    } else {
                                        FileUtil.deleteFileSafely(oatFilePath);
                                    }
                                    throw new SplitInstaller.InstallException(-18, new FileNotFoundException("System generate split " + splitInfo.getSplitName() + " oat file failed!"));
                                }
                            } else {
                                file = splitDir;
                                if (isSpecialManufacturer) {
                                    SplitLog.v(TAG, "Oat file %s is not exist in vivo & oppo, system would use interpreter mode.", oatFilePath.getAbsoluteFile());
                                    File splitSpecialMarkFile = SplitPathManager.require().getSplitSpecialMarkFile(splitInfo, obtainInstalledMark);
                                    if (!splitMarkFile.exists() && !splitSpecialMarkFile.exists()) {
                                        return new SplitInstaller.InstallResult(splitInfo.getSplitName(), file5, splitOptDir, file4, arrayList2, createInstalledMarkLock(splitSpecialMarkFile, SplitPathManager.require().getSplitSpecialLockFile(splitInfo)));
                                    }
                                }
                            }
                        } else {
                            file = splitDir;
                        }
                        file3 = splitOptDir;
                        arrayList = arrayList2;
                    } else {
                        file = splitDir;
                    }
                    file2 = file5;
                } else if (primaryLibData != null) {
                    File splitLibDir = SplitPathManager.require().getSplitLibDir(splitInfo, primaryLibData.getAbi());
                    extractLib(file5, splitLibDir, primaryLibData);
                    file = splitDir;
                    file4 = splitLibDir;
                } else {
                    file = splitDir;
                }
                splitDir = file;
            }
            return new SplitInstaller.InstallResult(splitInfo.getSplitName(), file2, file3, file4, arrayList, createInstalledMark(splitMarkFile));
        } catch (IOException e) {
            throw new SplitInstaller.InstallException(-100, e);
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstaller
    protected void verifySignature(File file) throws SplitInstaller.InstallException {
        if (SignatureValidator.validateSplit(this.appContext, file)) {
            return;
        }
        deleteCorruptedFiles(Collections.singletonList(file));
        throw new SplitInstaller.InstallException(-12, new SignatureException("Failed to check split apk " + file.getAbsolutePath() + " signature!"));
    }
}
