package com.iqiyi.android.qigsaw.core.splitinstall.remote;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.RemoteException;
import com.iqiyi.android.qigsaw.core.common.FileUtil;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitinstall.SplitPendingUninstallManager;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManagerService;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitPathManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import miui.util.HashUtils;

/* loaded from: classes2.dex */
public abstract class SplitInstallSupervisor {
    private static final String TAG = "SplitInstallSupervisor";

    /* loaded from: classes2.dex */
    public interface Callback {
        void onCancelInstall(int i, Bundle bundle);

        void onDeferredInstall(Bundle bundle);

        void onDeferredUninstall(Bundle bundle);

        void onError(Bundle bundle);

        void onGetSession(int i, Bundle bundle);

        void onGetSessionStates(List<Bundle> list);

        void onStartInstall(int i, Bundle bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static Bundle bundleErrorCode(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("error_code", i);
        bundle.putStringArray("module_names", new String[]{"Unknown"});
        return bundle;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static Bundle bundleErrorInfo(int i, String[] strArr) {
        Bundle bundle = new Bundle();
        bundle.putInt("error_code", i);
        bundle.putStringArray("module_names", strArr);
        return bundle;
    }

    private static int createSessionId(String str) {
        try {
            byte[] digest = MessageDigest.getInstance(HashUtils.MD5).digest((str).getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                int i = b & 255;
                if (i < 16) {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(i));
            }
            return sb.toString().hashCode();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException("NoSuchAlgorithmException", e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int createSessionId(Collection<SplitInfo> collection) {
        int i = 0;
        for (SplitInfo splitInfo : collection) {
            i += createSessionId(splitInfo.getSplitName() + "@" + splitInfo.getAppVersion() + "@" + splitInfo.getSplitVersion());
        }
        return i;
    }

    protected static boolean isMobileAvailable(Context context) {
        NetworkInfo activeNetworkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        return (connectivityManager == null || (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) == null || activeNetworkInfo.getType() != 0) ? false : true;
    }

    protected static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager != null) {
            NetworkInfo[] allNetworkInfo = connectivityManager.getAllNetworkInfo();
            if (allNetworkInfo.length > 0) {
                for (NetworkInfo networkInfo : allNetworkInfo) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static List<String> unBundleModuleNames(Collection<Bundle> collection) {
        ArrayList arrayList = new ArrayList(collection.size());
        Iterator<Bundle> it = collection.iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().getString("module_name"));
        }
        return arrayList;
    }

    public abstract void cancelInstall(int i, Callback callback) throws RemoteException;

    public abstract boolean cancelInstallWithoutUserConfirmation(int i);

    public abstract boolean continueInstallWithUserConfirmation(int i);

    public abstract void deferredInstall(List<Bundle> list, Callback callback) throws RemoteException;

    public abstract void deferredUninstall(List<Bundle> list, Callback callback) throws RemoteException;

    public abstract void getSessionState(int i, Callback callback) throws RemoteException;

    public abstract void getSessionStates(Callback callback) throws RemoteException;

    public abstract void startInstall(List<Bundle> list, Callback callback) throws RemoteException;

    public final void startUninstall(Context context) {
        ArrayList arrayList;
        Collection<SplitInfo> allSplitInfo;
        List<SplitInfo> splitInfos;
        List<String> readPendingUninstallSplits = new SplitPendingUninstallManager().readPendingUninstallSplits();
        SplitInfoManager splitInfoManagerService = SplitInfoManagerService.getInstance();
        if (readPendingUninstallSplits == null || splitInfoManagerService == null || (splitInfos = splitInfoManagerService.getSplitInfos(context, readPendingUninstallSplits)) == null) {
            arrayList = null;
        } else {
            arrayList = new ArrayList(splitInfos.size());
            for (SplitInfo splitInfo : splitInfos) {
                try {
                    if (FileUtil.deleteFileSafely(SplitPathManager.require().getSplitMarkFile(splitInfo, splitInfo.obtainInstalledMark(context)))) {
                        arrayList.add(splitInfo);
                    }
                } catch (IOException unused) {
                }
            }
        }
        if (arrayList == null || arrayList.isEmpty()) {
            SplitLog.d(TAG, "No splits need to uninstall!", new Object[0]);
        } else {
            SplitInstallService.getHandler(context.getPackageName()).post(new SplitStartUninstallTask(arrayList));
        }
        SplitInfoManager splitInfoManagerService2 = SplitInfoManagerService.getInstance();
        if (splitInfoManagerService2 == null || (allSplitInfo = splitInfoManagerService2.getAllSplitInfo(context)) == null) {
            return;
        }
        SplitInstallService.getHandler(context.getPackageName()).post(new SplitDeleteRedundantVersionTask(context, allSplitInfo));
    }
}
