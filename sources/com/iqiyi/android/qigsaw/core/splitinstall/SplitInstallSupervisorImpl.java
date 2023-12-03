package com.iqiyi.android.qigsaw.core.splitinstall;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import com.iqiyi.android.qigsaw.core.common.FileUtil;
import com.iqiyi.android.qigsaw.core.common.SplitAABInfoProvider;
import com.iqiyi.android.qigsaw.core.common.SplitBaseInfoProvider;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitdownload.DownloadRequest;
import com.iqiyi.android.qigsaw.core.splitdownload.Downloader;
import com.iqiyi.android.qigsaw.core.splitinstall.SplitDownloadPreprocessor;
import com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManagerService;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitPathManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class SplitInstallSupervisorImpl extends SplitInstallSupervisor {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String TAG = "Split:SplitInstallSupervisorImpl";
    private final Context appContext;
    private final long downloadSizeThresholdValue;
    private final List<String> dynamicFeatures;
    private final Set<String> installedSplitForAAB;
    private final Class<?> obtainUserConfirmationActivityClass;
    private final SplitInstallSessionManager sessionManager;
    private final SplitInstaller splitInstaller;
    private final Downloader userDownloader;
    private final boolean verifySignature;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitInstallSupervisorImpl(Context context, SplitInstallSessionManager splitInstallSessionManager, Downloader downloader, Class<? extends Activity> cls, boolean z) {
        this.appContext = context;
        this.sessionManager = splitInstallSessionManager;
        this.userDownloader = downloader;
        long downloadSizeThresholdWhenUsingMobileData = downloader.getDownloadSizeThresholdWhenUsingMobileData();
        this.downloadSizeThresholdValue = downloadSizeThresholdWhenUsingMobileData < 0 ? Long.MAX_VALUE : downloadSizeThresholdWhenUsingMobileData;
        this.installedSplitForAAB = new SplitAABInfoProvider(context).getInstalledSplitsForAAB();
        this.obtainUserConfirmationActivityClass = cls;
        this.splitInstaller = new SplitInstallerImpl(context, z);
        this.verifySignature = z;
        String[] dynamicFeatures = SplitBaseInfoProvider.getDynamicFeatures();
        List<String> asList = dynamicFeatures == null ? null : Arrays.asList(dynamicFeatures);
        this.dynamicFeatures = asList;
        if (asList == null) {
            SplitLog.w(TAG, "Can't read dynamicFeatures from SplitBaseInfoProvider", new Object[0]);
        }
    }

    private int checkInternalErrorCode() {
        SplitInfoManager splitInfoManagerService = SplitInfoManagerService.getInstance();
        if (splitInfoManagerService == null) {
            SplitLog.w(TAG, "Failed to fetch SplitInfoManager instance!", new Object[0]);
            return -100;
        }
        Collection<SplitInfo> allSplitInfo = splitInfoManagerService.getAllSplitInfo(this.appContext);
        if (allSplitInfo == null || allSplitInfo.isEmpty()) {
            SplitLog.w(TAG, "Failed to parse json file of split info!", new Object[0]);
            return -100;
        }
        String qigsawId = splitInfoManagerService.getQigsawId(this.appContext);
        String qigsawId2 = SplitBaseInfoProvider.getQigsawId();
        if (TextUtils.isEmpty(qigsawId) || !qigsawId.equals(qigsawId2)) {
            SplitLog.w(TAG, "Failed to match base app qigsaw-version excepted %s but %s!", qigsawId2, qigsawId);
            return -100;
        }
        return 0;
    }

    private int checkRequestErrorCode(List<String> list) {
        SplitLog.e(TAG, "checkRequestErrorCode: ", new Object[0]);
        if (!isRequestInvalid(list)) {
            return !isModuleAvailable(list) ? -2 : 0;
        }
        SplitLog.e(TAG, "checkRequestErrorCode: return INVALID_REQUEST", new Object[0]);
        return -3;
    }

    private boolean checkSplitInfo(SplitInfo splitInfo) {
        return isCPUArchMatched(splitInfo) && isMinSdkVersionMatched(splitInfo);
    }

    private List<DownloadRequest> createDownloadRequests(Collection<SplitInfo> collection) throws IOException {
        ArrayList arrayList = new ArrayList(collection.size());
        for (SplitInfo splitInfo : collection) {
            for (SplitInfo.ApkData apkData : splitInfo.getApkDataList(this.appContext)) {
                arrayList.add(DownloadRequest.newBuilder().url(apkData.getUrl()).fileDir(SplitPathManager.require().getSplitDir(splitInfo).getAbsolutePath()).fileName(splitInfo.getSplitName() + "-" + apkData.getAbi() + SplitConstants.DOT_APK).fileMD5(apkData.getMd5()).size(apkData.getSize()).moduleName(splitInfo.getSplitName()).build());
            }
        }
        return arrayList;
    }

    private void deferredDownloadSplits(List<SplitInfo> list, SplitInstallSupervisor.Callback callback) {
        try {
            long[] onPreDownloadSplits = onPreDownloadSplits(list);
            callback.onDeferredInstall(null);
            long j = onPreDownloadSplits[1];
            int createSessionId = SplitInstallSupervisor.createSessionId(list);
            SplitLog.d(TAG, "DeferredInstall session id: " + createSessionId, new Object[0]);
            DeferredDownloadCallback deferredDownloadCallback = new DeferredDownloadCallback(this.splitInstaller, list);
            if (j == 0) {
                SplitLog.d(TAG, "Splits have been downloaded, install them directly!", new Object[0]);
                deferredDownloadCallback.onCompleted();
                return;
            }
            List<DownloadRequest> createDownloadRequests = createDownloadRequests(list);
            this.userDownloader.deferredDownload(createSessionId, createDownloadRequests, deferredDownloadCallback, this.userDownloader.calculateDownloadSize(createDownloadRequests, onPreDownloadSplits[1]) < this.downloadSizeThresholdValue && !this.userDownloader.isDeferredDownloadOnlyWhenUsingWifiData());
        } catch (IOException e) {
            callback.onError(SplitInstallSupervisor.bundleErrorCode(-99));
            SplitLog.printErrStackTrace(TAG, e, "Failed to copy builtin split apks(%s)", "onDeferredInstall");
        }
    }

    private Set<String> getInstalledSplitForAAB() {
        return this.installedSplitForAAB;
    }

    private List<SplitInfo> getNeed2BeInstalledSplits(List<String> list) {
        SplitInfoManager splitInfoManagerService = SplitInfoManagerService.getInstance();
        List<SplitInfo> splitInfos = splitInfoManagerService.getSplitInfos(this.appContext, list);
        HashSet hashSet = new HashSet(0);
        for (SplitInfo splitInfo : splitInfos) {
            if (splitInfo.getDependencies() != null) {
                hashSet.addAll(splitInfo.getDependencies());
            }
        }
        if (hashSet.isEmpty()) {
            return splitInfos;
        }
        hashSet.removeAll(list);
        SplitLog.i(TAG, "Add dependencies %s automatically for install splits %s!", hashSet.toString(), list.toString());
        List<SplitInfo> splitInfos2 = splitInfoManagerService.getSplitInfos(this.appContext, hashSet);
        splitInfos2.addAll(splitInfos);
        return splitInfos2;
    }

    private boolean isAllSplitsBuiltIn(List<SplitInfo> list) {
        Iterator<SplitInfo> it = list.iterator();
        while (it.hasNext()) {
            if (!it.next().isBuiltIn()) {
                return false;
            }
        }
        return true;
    }

    private boolean isCPUArchMatched(SplitInfo splitInfo) {
        try {
            splitInfo.getPrimaryLibData(this.appContext);
            return true;
        } catch (IOException unused) {
            return false;
        }
    }

    private boolean isMinSdkVersionMatched(SplitInfo splitInfo) {
        return splitInfo.getMinSdkVersion() <= Build.VERSION.SDK_INT;
    }

    private boolean isModuleAvailable(List<String> list) {
        Collection<SplitInfo> allSplitInfo = SplitInfoManagerService.getInstance().getAllSplitInfo(this.appContext);
        for (String str : list) {
            for (SplitInfo splitInfo : allSplitInfo) {
                if (splitInfo.getSplitName().equals(str) && !checkSplitInfo(splitInfo)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isRequestInvalid(List<String> list) {
        List<String> list2;
        SplitLog.e(TAG, "moduleNames = " + list, new Object[0]);
        SplitLog.e(TAG, "dynamicFeatures = " + this.dynamicFeatures, new Object[0]);
        SplitLog.e(TAG, "moduleNames = " + list, new Object[0]);
        SplitLog.e(TAG, "dynamicFeatures.containsAll = " + this.dynamicFeatures.containsAll(list), new Object[0]);
        return list == null || list.isEmpty() || (list2 = this.dynamicFeatures) == null || !list2.containsAll(list);
    }

    private long[] onPreDownloadSplits(Collection<SplitInfo> collection) throws IOException {
        long j = 0;
        long j2 = 0;
        for (SplitInfo splitInfo : collection) {
            SplitDownloadPreprocessor splitDownloadPreprocessor = new SplitDownloadPreprocessor(SplitPathManager.require().getSplitDir(splitInfo));
            try {
                List<SplitDownloadPreprocessor.SplitFile> load = splitDownloadPreprocessor.load(this.appContext, splitInfo, this.verifySignature);
                FileUtil.closeQuietly(splitDownloadPreprocessor);
                j += splitInfo.getApkTotalSize(this.appContext);
                for (SplitDownloadPreprocessor.SplitFile splitFile : load) {
                    if (!splitFile.exists()) {
                        j2 += splitFile.realSize;
                    }
                }
            } catch (Throwable th) {
                FileUtil.closeQuietly(splitDownloadPreprocessor);
                throw th;
            }
        }
        return new long[]{j, j2};
    }

    private int onPreInstallSplits(List<String> list) {
        if (getInstalledSplitForAAB().isEmpty()) {
            int checkInternalErrorCode = checkInternalErrorCode();
            return checkInternalErrorCode == 0 ? checkRequestErrorCode(list) : checkInternalErrorCode;
        } else if (getInstalledSplitForAAB().containsAll(list)) {
            return 0;
        } else {
            SplitLog.e(TAG, "onPreInstallSplits: return INVALID_REQUEST", new Object[0]);
            return -3;
        }
    }

    private void startDownloadSplits(List<String> list, List<SplitInfo> list2, SplitInstallSupervisor.Callback callback) {
        if (this.sessionManager.isActiveSessionsLimitExceeded()) {
            SplitLog.w(TAG, "Start install request error code: ACTIVE_SESSIONS_LIMIT_EXCEEDED", new Object[0]);
            callback.onError(SplitInstallSupervisor.bundleErrorInfo(-1, (String[]) list.toArray(new String[0])));
            return;
        }
        int createSessionId = SplitInstallSupervisor.createSessionId(list2);
        SplitInstallInternalSessionState sessionState = this.sessionManager.getSessionState(createSessionId);
        if (!(sessionState != null && sessionState.status() == 8) && this.sessionManager.isIncompatibleWithExistingSession(list)) {
            SplitLog.w(TAG, "Start install request error code: INCOMPATIBLE_WITH_EXISTING_SESSION", new Object[0]);
            callback.onError(SplitInstallSupervisor.bundleErrorInfo(-8, (String[]) list.toArray(new String[0])));
            return;
        }
        SplitLog.d(TAG, "startInstall session id: " + createSessionId, new Object[0]);
        try {
            List<DownloadRequest> createDownloadRequests = createDownloadRequests(list2);
            if (sessionState == null) {
                sessionState = new SplitInstallInternalSessionState(createSessionId, list, list2, createDownloadRequests);
            }
            long[] onPreDownloadSplits = onPreDownloadSplits(list2);
            callback.onStartInstall(createSessionId, null);
            this.sessionManager.setSessionState(createSessionId, sessionState);
            long j = onPreDownloadSplits[0];
            long calculateDownloadSize = this.userDownloader.calculateDownloadSize(createDownloadRequests, onPreDownloadSplits[1]);
            SplitLog.d(TAG, "totalBytesToDownload: %d, realTotalBytesNeedToDownload: %d ", Long.valueOf(j), Long.valueOf(calculateDownloadSize));
            sessionState.setTotalBytesToDownload(j);
            StartDownloadCallback startDownloadCallback = new StartDownloadCallback(this.splitInstaller, createSessionId, this.sessionManager, list2);
            if (calculateDownloadSize <= 0) {
                SplitLog.d(TAG, "Splits have been downloaded, install them directly!", new Object[0]);
                startDownloadCallback.onCompleted();
                return;
            }
            this.sessionManager.changeSessionState(createSessionId, 1);
            this.sessionManager.emitSessionState(sessionState);
            this.userDownloader.startDownload(createSessionId, createDownloadRequests, startDownloadCallback);
        } catch (IOException e) {
            SplitLog.w(TAG, "Failed to copy internal splits", e);
            callback.onError(SplitInstallSupervisor.bundleErrorInfo(-99, (String[]) list.toArray(new String[0])));
        }
    }

    private void startUserConfirmationActivity(SplitInstallInternalSessionState splitInstallInternalSessionState, long j, List<DownloadRequest> list) {
        Intent intent = new Intent();
        intent.putExtra("sessionId", splitInstallInternalSessionState.sessionId());
        intent.putParcelableArrayListExtra("downloadRequests", (ArrayList) list);
        intent.putExtra("realTotalBytesNeedToDownload", j);
        intent.putStringArrayListExtra("moduleNames", (ArrayList) splitInstallInternalSessionState.moduleNames());
        intent.setClass(this.appContext, this.obtainUserConfirmationActivityClass);
        splitInstallInternalSessionState.setUserConfirmationIntent(PendingIntent.getActivity(this.appContext, 0, intent, 201326592));
        this.sessionManager.changeSessionState(splitInstallInternalSessionState.sessionId(), 8);
        this.sessionManager.emitSessionState(splitInstallInternalSessionState);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor
    public void cancelInstall(int i, SplitInstallSupervisor.Callback callback) {
        SplitLog.i(TAG, "start to cancel session id %d installation", Integer.valueOf(i));
        SplitInstallInternalSessionState sessionState = this.sessionManager.getSessionState(i);
        if (sessionState == null) {
            SplitLog.i(TAG, "Session id is not found!", new Object[0]);
            callback.onError(SplitInstallSupervisor.bundleErrorCode(-4));
        } else if (sessionState.status() != 1 && sessionState.status() != 2) {
            callback.onError(SplitInstallSupervisor.bundleErrorInfo(-3, (String[]) sessionState.moduleNames().toArray(new String[0])));
        } else {
            boolean cancelDownloadSync = this.userDownloader.cancelDownloadSync(i);
            SplitLog.d(TAG, "result of cancel request : " + cancelDownloadSync, new Object[0]);
            if (cancelDownloadSync) {
                callback.onCancelInstall(i, null);
            } else {
                callback.onError(SplitInstallSupervisor.bundleErrorInfo(-3, (String[]) sessionState.moduleNames().toArray(new String[0])));
            }
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor
    public boolean cancelInstallWithoutUserConfirmation(int i) {
        SplitInstallInternalSessionState sessionState = this.sessionManager.getSessionState(i);
        if (sessionState != null) {
            this.sessionManager.changeSessionState(sessionState.sessionId(), 7);
            this.sessionManager.emitSessionState(sessionState);
            return true;
        }
        return false;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor
    public boolean continueInstallWithUserConfirmation(int i) {
        SplitInstallInternalSessionState sessionState = this.sessionManager.getSessionState(i);
        if (sessionState != null) {
            StartDownloadCallback startDownloadCallback = new StartDownloadCallback(this.splitInstaller, i, this.sessionManager, sessionState.needInstalledSplits);
            this.sessionManager.changeSessionState(i, 1);
            this.sessionManager.emitSessionState(sessionState);
            this.userDownloader.startDownload(sessionState.sessionId(), sessionState.downloadRequests, startDownloadCallback);
            return true;
        }
        return false;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor
    public void deferredInstall(List<Bundle> list, SplitInstallSupervisor.Callback callback) {
        List<String> unBundleModuleNames = SplitInstallSupervisor.unBundleModuleNames(list);
        int onPreInstallSplits = onPreInstallSplits(unBundleModuleNames);
        if (onPreInstallSplits != 0) {
            callback.onError(SplitInstallSupervisor.bundleErrorCode(onPreInstallSplits));
        } else if (getInstalledSplitForAAB().isEmpty()) {
            deferredDownloadSplits(getNeed2BeInstalledSplits(unBundleModuleNames), callback);
        } else if (getInstalledSplitForAAB().containsAll(unBundleModuleNames)) {
            callback.onDeferredInstall(null);
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor
    public void deferredUninstall(List<Bundle> list, SplitInstallSupervisor.Callback callback) {
        List<String> unBundleModuleNames = SplitInstallSupervisor.unBundleModuleNames(list);
        if (!getInstalledSplitForAAB().isEmpty()) {
            callback.onError(SplitInstallSupervisor.bundleErrorInfo(-98, (String[]) unBundleModuleNames.toArray(new String[0])));
            return;
        }
        int checkInternalErrorCode = checkInternalErrorCode();
        if (checkInternalErrorCode != 0) {
            callback.onError(SplitInstallSupervisor.bundleErrorInfo(checkInternalErrorCode, (String[]) unBundleModuleNames.toArray(new String[0])));
        } else if (isRequestInvalid(unBundleModuleNames)) {
            callback.onError(SplitInstallSupervisor.bundleErrorInfo(-3, (String[]) unBundleModuleNames.toArray(new String[0])));
        } else if (new SplitPendingUninstallManager().recordPendingUninstallSplits(unBundleModuleNames)) {
            SplitLog.w(TAG, "Succeed to record pending uninstall splits %s!", unBundleModuleNames.toString());
            callback.onDeferredUninstall(null);
        } else {
            SplitLog.w(TAG, "Failed to record pending uninstall splits!", new Object[0]);
            callback.onError(SplitInstallSupervisor.bundleErrorInfo(-100, (String[]) unBundleModuleNames.toArray(new String[0])));
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor
    public void getSessionState(int i, SplitInstallSupervisor.Callback callback) {
        SplitInstallInternalSessionState sessionState = this.sessionManager.getSessionState(i);
        if (sessionState == null) {
            callback.onError(SplitInstallSupervisor.bundleErrorCode(-4));
        } else {
            callback.onGetSession(i, SplitInstallInternalSessionState.transform2Bundle(sessionState));
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor
    public void getSessionStates(SplitInstallSupervisor.Callback callback) {
        List<SplitInstallInternalSessionState> sessionStates = this.sessionManager.getSessionStates();
        if (sessionStates.isEmpty()) {
            callback.onGetSessionStates(Collections.emptyList());
            return;
        }
        ArrayList arrayList = new ArrayList(0);
        Iterator<SplitInstallInternalSessionState> it = sessionStates.iterator();
        while (it.hasNext()) {
            arrayList.add(SplitInstallInternalSessionState.transform2Bundle(it.next()));
        }
        callback.onGetSessionStates(arrayList);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallSupervisor
    public void startInstall(List<Bundle> list, SplitInstallSupervisor.Callback callback) {
        List<String> unBundleModuleNames = SplitInstallSupervisor.unBundleModuleNames(list);
        int onPreInstallSplits = onPreInstallSplits(unBundleModuleNames);
        if (onPreInstallSplits != 0) {
            callback.onError(SplitInstallSupervisor.bundleErrorInfo(onPreInstallSplits, (String[]) unBundleModuleNames.toArray(new String[0])));
            return;
        }
        List<SplitInfo> need2BeInstalledSplits = getNeed2BeInstalledSplits(unBundleModuleNames);
        isAllSplitsBuiltIn(need2BeInstalledSplits);
        startDownloadSplits(unBundleModuleNames, need2BeInstalledSplits, callback);
    }
}
