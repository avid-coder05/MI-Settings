package com.iqiyi.android.qigsaw.core.splitload;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitload.compat.NativePathMapper;
import com.iqiyi.android.qigsaw.core.splitload.compat.NativePathMapperImpl;
import com.iqiyi.android.qigsaw.core.splitreport.SplitBriefInfo;
import com.iqiyi.android.qigsaw.core.splitreport.SplitLoadError;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManagerService;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitPathManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class SplitLoadHandler {
    private static final String TAG = "SplitLoadHandler";
    private final SplitActivator activator;
    private final SplitLoadManager loadManager;
    private final NativePathMapper mapper;
    private final List<Intent> splitFileIntents;
    private final SplitLoaderWrapper splitLoader;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final SplitInfoManager infoManager = SplitInfoManagerService.getInstance();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public interface OnSplitLoadFinishListener {
        void onLoadFinish(List<SplitBriefInfo> list, List<SplitLoadError> list2, String str, long j);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitLoadHandler(SplitLoaderWrapper splitLoaderWrapper, SplitLoadManager splitLoadManager, List<Intent> list) {
        this.splitLoader = splitLoaderWrapper;
        this.loadManager = splitLoadManager;
        this.splitFileIntents = list;
        this.activator = new SplitActivator(splitLoadManager.getContext());
        this.mapper = new NativePathMapperImpl(splitLoadManager.getContext());
    }

    private void activateSplit(String str, String str2, Application application, ClassLoader classLoader) throws SplitLoadException {
        try {
            this.splitLoader.loadResources(str2);
        } catch (SplitLoadException e) {
            SplitLog.printErrStackTrace(TAG, e, "Failed to load %s resources", str2);
        }
        try {
            this.activator.attachSplitApplication(application);
            try {
                this.activator.createAndActivateSplitContentProviders(classLoader, str);
                try {
                    this.activator.invokeOnCreateForSplitApplication(application);
                } catch (SplitLoadException e2) {
                    SplitLog.printErrStackTrace(TAG, e2, "Failed to invoke onCreate for %s application", str);
                    throw e2;
                }
            } catch (SplitLoadException e3) {
                SplitLog.printErrStackTrace(TAG, e3, "Failed to create %s content-provider ", str);
                throw e3;
            }
        } catch (SplitLoadException e4) {
            SplitLog.printErrStackTrace(TAG, e4, "Failed to attach %s application", str);
            throw e4;
        }
    }

    private boolean checkSplitLoaded(String str) {
        Iterator<Split> it = this.loadManager.getLoadedSplits().iterator();
        while (it.hasNext()) {
            if (it.next().splitName.equals(str)) {
                return true;
            }
        }
        return false;
    }

    private void loadSplits(OnSplitLoadFinishListener onSplitLoadFinishListener) {
        SplitBriefInfo splitBriefInfo;
        char c;
        SplitBriefInfo splitBriefInfo2;
        String str;
        int i;
        SplitLoaderWrapper splitLoaderWrapper;
        File file;
        File file2;
        List<String> dependencies;
        ArrayList arrayList;
        long currentTimeMillis = System.currentTimeMillis();
        HashSet hashSet = new HashSet();
        char c2 = 0;
        ArrayList arrayList2 = new ArrayList(0);
        ArrayList arrayList3 = new ArrayList(this.splitFileIntents.size());
        Iterator<Intent> it = this.splitFileIntents.iterator();
        while (it.hasNext()) {
            Intent next = it.next();
            long currentTimeMillis2 = System.currentTimeMillis();
            String stringExtra = next.getStringExtra(SplitConstants.KET_NAME);
            SplitInfo splitInfo = this.infoManager.getSplitInfo(getContext(), stringExtra);
            if (splitInfo == null) {
                Object[] objArr = new Object[1];
                if (stringExtra == null) {
                    stringExtra = "null";
                }
                objArr[c2] = stringExtra;
                SplitLog.w(TAG, "Unable to get info for %s, just skip!", objArr);
            } else {
                Iterator<Intent> it2 = it;
                SplitBriefInfo splitBriefInfo3 = new SplitBriefInfo(splitInfo.getSplitName(), splitInfo.getSplitVersion(), splitInfo.isBuiltIn());
                if (checkSplitLoaded(stringExtra)) {
                    SplitLog.i(TAG, "Split %s has been loaded!", stringExtra);
                    c2 = 0;
                    it = it2;
                } else {
                    String stringExtra2 = next.getStringExtra(SplitConstants.KEY_APK);
                    long j = currentTimeMillis;
                    if (stringExtra2 == null) {
                        SplitLog.w(TAG, "Failed to read split %s apk path", stringExtra);
                        arrayList2.add(new SplitLoadError(splitBriefInfo3, -100, new Exception("split apk path " + stringExtra + " is missing!")));
                    } else {
                        String stringExtra3 = next.getStringExtra(SplitConstants.KEY_DEX_OPT_DIR);
                        if (splitInfo.hasDex() && stringExtra3 == null) {
                            SplitLog.w(TAG, "Failed to %s get dex-opt-dir", stringExtra);
                            arrayList2.add(new SplitLoadError(splitBriefInfo3, -100, new Exception("dex-opt-dir of " + stringExtra + " is missing!")));
                        } else {
                            String stringExtra4 = next.getStringExtra(SplitConstants.KEY_NATIVE_LIB_DIR);
                            try {
                                if (splitInfo.getPrimaryLibData(getContext()) == null || stringExtra4 != null) {
                                    ArrayList<String> stringArrayListExtra = next.getStringArrayListExtra(SplitConstants.KEY_ADDED_DEX);
                                    SplitLog.d(TAG, "split name: %s, origin native path: %s", stringExtra, stringExtra4);
                                    String map = this.mapper.map(stringExtra, stringExtra4);
                                    SplitLog.d(TAG, "split name: %s, mapped native path: %s", stringExtra, map);
                                    try {
                                        splitLoaderWrapper = this.splitLoader;
                                        file = stringExtra3 == null ? null : new File(stringExtra3);
                                        file2 = map == null ? null : new File(map);
                                        dependencies = splitInfo.getDependencies();
                                        splitBriefInfo2 = splitBriefInfo3;
                                        str = TAG;
                                        arrayList = arrayList2;
                                    } catch (SplitLoadException e) {
                                        e = e;
                                        splitBriefInfo2 = splitBriefInfo3;
                                        str = TAG;
                                        i = 1;
                                    }
                                    try {
                                        ClassLoader loadCode = splitLoaderWrapper.loadCode(stringExtra, stringArrayListExtra, file, file2, dependencies);
                                        try {
                                            try {
                                                activateSplit(stringExtra, stringExtra2, this.activator.createSplitApplication(loadCode, stringExtra), loadCode);
                                                if (!SplitPathManager.require().getSplitDir(splitInfo).setLastModified(System.currentTimeMillis())) {
                                                    SplitLog.w(str, "Failed to set last modified time for " + stringExtra, new Object[0]);
                                                }
                                                arrayList3.add(splitBriefInfo2.setTimeCost(System.currentTimeMillis() - currentTimeMillis2));
                                                hashSet.add(new Split(stringExtra, stringExtra2));
                                                it = it2;
                                                currentTimeMillis = j;
                                                arrayList2 = arrayList;
                                            } catch (SplitLoadException e2) {
                                                arrayList2 = arrayList;
                                                arrayList2.add(new SplitLoadError(splitBriefInfo2, e2.getErrorCode(), e2.getCause()));
                                                this.splitLoader.unloadCode(loadCode);
                                            }
                                        } catch (SplitLoadException e3) {
                                            arrayList2 = arrayList;
                                            SplitLog.printErrStackTrace(str, e3, "Failed to create %s application ", stringExtra);
                                            arrayList2.add(new SplitLoadError(splitBriefInfo2, e3.getErrorCode(), e3.getCause()));
                                            this.splitLoader.unloadCode(loadCode);
                                        }
                                        c2 = 0;
                                    } catch (SplitLoadException e4) {
                                        e = e4;
                                        arrayList2 = arrayList;
                                        i = 1;
                                        Object[] objArr2 = new Object[i];
                                        c = 0;
                                        objArr2[0] = stringExtra;
                                        SplitLog.printErrStackTrace(str, e, "Failed to load split %s code!", objArr2);
                                        arrayList2.add(new SplitLoadError(splitBriefInfo2, e.getErrorCode(), e.getCause()));
                                        c2 = c;
                                        it = it2;
                                        currentTimeMillis = j;
                                    }
                                } else {
                                    Object[] objArr3 = new Object[1];
                                    try {
                                        objArr3[0] = stringExtra;
                                        SplitLog.w(TAG, "Failed to get %s native-lib-dir", objArr3);
                                        arrayList2.add(new SplitLoadError(splitBriefInfo3, -100, new Exception("native-lib-dir of " + stringExtra + " is missing!")));
                                    } catch (IOException e5) {
                                        e = e5;
                                        c = 0;
                                        splitBriefInfo = splitBriefInfo3;
                                        arrayList2.add(new SplitLoadError(splitBriefInfo, -100, e));
                                        c2 = c;
                                        it = it2;
                                        currentTimeMillis = j;
                                    }
                                }
                            } catch (IOException e6) {
                                e = e6;
                                splitBriefInfo = splitBriefInfo3;
                                c = 0;
                            }
                        }
                    }
                    it = it2;
                    currentTimeMillis = j;
                    c2 = 0;
                }
            }
        }
        long j2 = currentTimeMillis;
        this.loadManager.putSplits(hashSet);
        if (onSplitLoadFinishListener != null) {
            onSplitLoadFinishListener.onLoadFinish(arrayList3, arrayList2, this.loadManager.currentProcessName, System.currentTimeMillis() - j2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Context getContext() {
        return this.loadManager.getContext();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Handler getMainHandler() {
        return this.mainHandler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void loadSplitsSync(OnSplitLoadFinishListener onSplitLoadFinishListener) {
        loadSplits(onSplitLoadFinishListener);
    }
}
