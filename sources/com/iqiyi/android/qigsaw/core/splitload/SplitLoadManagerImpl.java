package com.iqiyi.android.qigsaw.core.splitload;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import com.iqiyi.android.qigsaw.core.common.FileUtil;
import com.iqiyi.android.qigsaw.core.common.OEMCompat;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitload.listener.OnSplitLoadListener;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManagerService;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitPathManager;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class SplitLoadManagerImpl extends SplitLoadManager {
    private final String[] forbiddenWorkProcesses;
    private final boolean qigsawMode;
    private final String[] workProcesses;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitLoadManagerImpl(Context context, int i, boolean z, boolean z2, String str, String[] strArr, String[] strArr2) {
        super(context, str, i);
        this.qigsawMode = z;
        this.workProcesses = strArr;
        this.forbiddenWorkProcesses = strArr2;
        SplitInfoManagerService.install(context, z2);
        SplitPathManager.install(context);
    }

    private boolean canBeWorkedInThisProcessForSplit(SplitInfo splitInfo) {
        List<String> workProcesses = splitInfo.getWorkProcesses();
        if (workProcesses == null || workProcesses.isEmpty()) {
            return true;
        }
        return workProcesses.contains(this.currentProcessName.replace(getContext().getPackageName(), ""));
    }

    private List<Intent> createInstalledSplitFileIntents(Collection<SplitInfo> collection) {
        ArrayList arrayList = new ArrayList();
        for (SplitInfo splitInfo : collection) {
            if (!canBeWorkedInThisProcessForSplit(splitInfo)) {
                SplitLog.i("SplitLoadManager", "Split %s do not need work in process %s", splitInfo.getSplitName(), this.currentProcessName);
            } else if (getLoadedSplitNames().contains(splitInfo.getSplitName())) {
                SplitLog.i("SplitLoadManager", "Split %s has been loaded, ignore it!", splitInfo.getSplitName());
            } else {
                try {
                    SplitInfo.ApkData apkDataForMaster = splitInfo.getApkDataForMaster();
                    SplitInfo.LibData primaryLibData = splitInfo.getPrimaryLibData(getContext());
                    Intent createLastInstalledSplitFileIntent = createLastInstalledSplitFileIntent(splitInfo.isBuiltIn() && apkDataForMaster.getUrl().startsWith(SplitConstants.URL_NATIVE), splitInfo.obtainInstalledMark(getContext()), primaryLibData != null ? SplitPathManager.require().getSplitLibDir(splitInfo, primaryLibData.getAbi()) : null, splitInfo);
                    if (createLastInstalledSplitFileIntent != null) {
                        arrayList.add(createLastInstalledSplitFileIntent);
                    }
                    Object[] objArr = new Object[4];
                    objArr[0] = splitInfo.getSplitName();
                    objArr[1] = this.currentProcessName;
                    objArr[2] = createLastInstalledSplitFileIntent == null ? "but" : "and";
                    objArr[3] = createLastInstalledSplitFileIntent == null ? "not installed" : "installed";
                    SplitLog.i("SplitLoadManager", "Split %s will work in process %s, %s it is %s", objArr);
                } catch (IOException unused) {
                }
            }
        }
        return arrayList;
    }

    private Intent createLastInstalledSplitFileIntent(boolean z, String str, File file, SplitInfo splitInfo) {
        ArrayList<String> arrayList;
        String splitName = splitInfo.getSplitName();
        File splitDir = SplitPathManager.require().getSplitDir(splitInfo);
        File splitMarkFile = SplitPathManager.require().getSplitMarkFile(splitInfo, str);
        File splitSpecialMarkFile = SplitPathManager.require().getSplitSpecialMarkFile(splitInfo, str);
        File file2 = z ? new File(getContext().getApplicationInfo().nativeLibraryDir, System.mapLibraryName(SplitConstants.SPLIT_PREFIX + splitInfo.getSplitName())) : new File(splitDir, splitName + "-" + SplitConstants.MASTER + SplitConstants.DOT_APK);
        if (splitSpecialMarkFile.exists() && !splitMarkFile.exists()) {
            SplitLog.v("SplitLoadManager", "In vivo & oppo, we need to check oat file when split is going to be loaded.", new Object[0]);
            File oatFilePath = OEMCompat.getOatFilePath(file2, SplitPathManager.require().getSplitOptDir(splitInfo));
            if (FileUtil.isLegalFile(oatFilePath)) {
                boolean checkOatFile = OEMCompat.checkOatFile(oatFilePath);
                SplitLog.v("SplitLoadManager", "Check result of oat file %s is " + checkOatFile, oatFilePath.getAbsoluteFile());
                File splitSpecialLockFile = SplitPathManager.require().getSplitSpecialLockFile(splitInfo);
                if (checkOatFile) {
                    try {
                        FileUtil.createFileSafelyLock(splitMarkFile, splitSpecialLockFile);
                    } catch (IOException unused) {
                        SplitLog.w("SplitLoadManager", "Failed to create installed mark file " + oatFilePath.exists(), new Object[0]);
                    }
                } else {
                    try {
                        FileUtil.deleteFileSafelyLock(oatFilePath, splitSpecialLockFile);
                    } catch (IOException unused2) {
                        SplitLog.w("SplitLoadManager", "Failed to delete corrupted oat file " + oatFilePath.exists(), new Object[0]);
                    }
                }
            } else {
                SplitLog.v("SplitLoadManager", "Oat file %s is still not exist in vivo & oppo, system continue to use interpreter mode.", oatFilePath.getAbsoluteFile());
            }
        }
        boolean exists = splitMarkFile.exists();
        File file3 = null;
        if (exists || splitSpecialMarkFile.exists()) {
            List<String> dependencies = splitInfo.getDependencies();
            if (dependencies != null) {
                SplitLog.i("SplitLoadManager", "Split %s has dependencies %s !", splitName, dependencies);
                for (String str2 : dependencies) {
                    SplitInfo splitInfo2 = SplitInfoManagerService.getInstance().getSplitInfo(getContext(), str2);
                    if (!SplitPathManager.require().getSplitMarkFile(splitInfo2, splitInfo2.obtainInstalledMark(getContext())).exists()) {
                        SplitLog.i("SplitLoadManager", "Dependency %s mark file is not existed!", str2);
                        return null;
                    }
                }
            }
            if (splitInfo.hasDex()) {
                file3 = SplitPathManager.require().getSplitOptDir(splitInfo);
                arrayList = new ArrayList<>();
                arrayList.add(file2.getAbsolutePath());
                File[] listFiles = SplitPathManager.require().getSplitCodeCacheDir(splitInfo).listFiles(new FilenameFilter() { // from class: com.iqiyi.android.qigsaw.core.splitload.SplitLoadManagerImpl.1
                    @Override // java.io.FilenameFilter
                    public boolean accept(File file4, String str3) {
                        return str3.endsWith(SplitConstants.DOT_ZIP);
                    }
                });
                if (listFiles != null && listFiles.length > 0) {
                    for (File file4 : listFiles) {
                        arrayList.add(file4.getAbsolutePath());
                    }
                }
            } else {
                arrayList = null;
            }
            Intent intent = new Intent();
            intent.putExtra(SplitConstants.KET_NAME, splitName);
            intent.putExtra(SplitConstants.KEY_APK, file2.getAbsolutePath());
            if (file3 != null) {
                intent.putExtra(SplitConstants.KEY_DEX_OPT_DIR, file3.getAbsolutePath());
            }
            if (file != null) {
                intent.putExtra(SplitConstants.KEY_NATIVE_LIB_DIR, file.getAbsolutePath());
            }
            if (arrayList != null) {
                intent.putStringArrayListExtra(SplitConstants.KEY_ADDED_DEX, arrayList);
            }
            return intent;
        }
        return null;
    }

    private List<Intent> filterIntentsCanWorkInThisProcess(List<Intent> list) {
        ArrayList arrayList = new ArrayList(list.size());
        SplitInfoManager splitInfoManagerService = SplitInfoManagerService.getInstance();
        if (splitInfoManagerService == null) {
            return list;
        }
        for (Intent intent : list) {
            SplitInfo splitInfo = splitInfoManagerService.getSplitInfo(getContext(), intent.getStringExtra(SplitConstants.KET_NAME));
            if (canBeWorkedInThisProcessForSplit(splitInfo)) {
                arrayList.add(intent);
                SplitLog.i("SplitLoadManager", "Split %s need load in process %s", splitInfo.getSplitName(), this.currentProcessName);
            } else {
                SplitLog.i("SplitLoadManager", "Split %s do not need load in process %s", splitInfo.getSplitName(), this.currentProcessName);
            }
        }
        return arrayList;
    }

    private Context getBaseContext() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        return context;
    }

    private String getCompleteProcessName(String str) {
        String packageName = getContext().getPackageName();
        if (TextUtils.isEmpty(str)) {
            return packageName;
        }
        if (str.startsWith(packageName)) {
            return str;
        }
        return packageName + str;
    }

    private void injectClassLoader(ClassLoader classLoader) {
        try {
            SplitDelegateClassloader.inject(classLoader, getBaseContext());
        } catch (Exception e) {
            SplitLog.printErrStackTrace("SplitLoadManager", e, "Failed to hook PathClassloader", new Object[0]);
        }
    }

    private boolean isInjectPathClassloaderNeeded() {
        return Build.VERSION.SDK_INT < 29 ? this.qigsawMode : !(getContext().getClassLoader() instanceof SplitDelegateClassloader) && this.qigsawMode;
    }

    private boolean isProcessAllowedToWork() {
        if ((this.workProcesses == null && this.forbiddenWorkProcesses == null) || getContext().getPackageName().equals(this.currentProcessName)) {
            return true;
        }
        String[] strArr = this.forbiddenWorkProcesses;
        if (strArr != null) {
            for (String str : strArr) {
                if (getCompleteProcessName(str).equals(this.currentProcessName)) {
                    return false;
                }
            }
        }
        String[] strArr2 = this.workProcesses;
        if (strArr2 != null) {
            int length = strArr2.length;
            for (int i = 0; i < length && !getCompleteProcessName(strArr2[i]).equals(this.currentProcessName); i++) {
            }
            return true;
        }
        return true;
    }

    private void loadInstalledSplitsInternal(Collection<String> collection) {
        SplitInfoManager splitInfoManagerService = SplitInfoManagerService.getInstance();
        if (splitInfoManagerService == null) {
            SplitLog.w("SplitLoadManager", "Failed to get SplitInfoManager instance, have you invoke Qigsaw#install(...) method?", new Object[0]);
            return;
        }
        Collection<SplitInfo> allSplitInfo = collection == null ? splitInfoManagerService.getAllSplitInfo(getContext()) : splitInfoManagerService.getSplitInfos(getContext(), collection);
        if (allSplitInfo == null || allSplitInfo.isEmpty()) {
            SplitLog.w("SplitLoadManager", "Failed to get Split-Info list!", new Object[0]);
            return;
        }
        List<Intent> createInstalledSplitFileIntents = createInstalledSplitFileIntents(allSplitInfo);
        if (createInstalledSplitFileIntents.isEmpty()) {
            SplitLog.w("SplitLoadManager", "There are no installed splits!", new Object[0]);
        } else {
            createSplitLoadTask(createInstalledSplitFileIntents, null).run();
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.SplitLoadManager
    public Runnable createSplitLoadTask(List<Intent> list, OnSplitLoadListener onSplitLoadListener) {
        List<Intent> filterIntentsCanWorkInThisProcess = filterIntentsCanWorkInThisProcess(list);
        return filterIntentsCanWorkInThisProcess.isEmpty() ? new SkipSplitLoadTaskImpl() : splitLoadMode() == 1 ? new SplitLoadTaskImpl(this, filterIntentsCanWorkInThisProcess, onSplitLoadListener) : new SplitLoadTaskImpl2(this, filterIntentsCanWorkInThisProcess, onSplitLoadListener);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.SplitLoadManager
    public void getResources(Resources resources) {
        try {
            SplitCompatResourcesLoader.loadResources(getContext(), resources);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.SplitLoadManager
    public void injectPathClassloader() {
        if (isInjectPathClassloaderNeeded() && isProcessAllowedToWork()) {
            injectClassLoader(getContext().getClassLoader());
        }
        ClassLoader classLoader = getContext().getClassLoader();
        if (classLoader instanceof SplitDelegateClassloader) {
            ((SplitDelegateClassloader) classLoader).setClassNotFoundInterceptor(new DefaultClassNotFoundInterceptor(getContext(), SplitLoadManagerImpl.class.getClassLoader(), splitLoadMode()));
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.SplitLoadManager
    public void loadInstalledSplits() {
        loadInstalledSplitsInternal(null);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.SplitLoadManager
    public void preloadInstalledSplits(Collection<String> collection) {
        if (this.qigsawMode && isProcessAllowedToWork()) {
            loadInstalledSplitsInternal(collection);
        }
    }
}
