package com.google.android.play.core.splitinstall;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.android.settings.search.SearchUpdater;
import com.google.android.play.core.remote.RemoteManager;
import com.google.android.play.core.splitcompat.util.PlayCore;
import com.google.android.play.core.splitinstall.protocol.ISplitInstallServiceProxy;
import com.google.android.play.core.tasks.Task;
import com.google.android.play.core.tasks.TaskWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class SplitInstallService {
    static final PlayCore playCore = new PlayCore(SplitInstallService.class.getSimpleName());
    private final Context mContext;
    final String mPackageName;
    final RemoteManager<ISplitInstallServiceProxy> mSplitRemoteManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitInstallService(Context context) {
        this(context, context.getPackageName());
    }

    private SplitInstallService(Context context, String str) {
        OnBinderDiedListenerImpl onBinderDiedListenerImpl = new OnBinderDiedListenerImpl(this);
        this.mContext = context;
        this.mPackageName = str;
        this.mSplitRemoteManager = new RemoteManager<>(context.getApplicationContext(), playCore, "SplitInstallService", new Intent("com.iqiyi.android.play.core.splitinstall.BIND_SPLIT_INSTALL_SERVICE").setPackage(str), SplitRemoteImpl.sInstance, onBinderDiedListenerImpl);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<Bundle> wrapModuleNames(Collection<String> collection) {
        ArrayList arrayList = new ArrayList(collection.size());
        for (String str : collection) {
            Bundle bundle = new Bundle();
            bundle.putString("module_name", str);
            arrayList.add(bundle);
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Bundle wrapVersionCode() {
        Bundle bundle = new Bundle();
        bundle.putInt("playcore_version_code", 10010);
        return bundle;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Task<Void> cancelInstall(int i) {
        playCore.info("cancelInstall(%d)", Integer.valueOf(i));
        TaskWrapper taskWrapper = new TaskWrapper();
        this.mSplitRemoteManager.bindService(new CancelInstallTask(this, taskWrapper, i, taskWrapper));
        return taskWrapper.getTask();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Task<Void> deferredInstall(List<String> list) {
        playCore.info("deferredInstall(%s)", list);
        TaskWrapper taskWrapper = new TaskWrapper();
        this.mSplitRemoteManager.bindService(new DeferredInstallTask(this, taskWrapper, list, taskWrapper));
        return taskWrapper.getTask();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Task<Void> deferredUninstall(List<String> list) {
        playCore.info("deferredUninstall(%s)", list);
        TaskWrapper taskWrapper = new TaskWrapper();
        this.mSplitRemoteManager.bindService(new DeferredUninstallTask(this, taskWrapper, list, taskWrapper));
        return taskWrapper.getTask();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Task<SplitInstallSessionState> getSessionState(int i) {
        playCore.info("getSessionState(%d)", Integer.valueOf(i));
        TaskWrapper taskWrapper = new TaskWrapper();
        this.mSplitRemoteManager.bindService(new GetSessionStateTask(this, taskWrapper, i, taskWrapper));
        return taskWrapper.getTask();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Task<List<SplitInstallSessionState>> getSessionStates() {
        playCore.info("getSessionStates", new Object[0]);
        TaskWrapper taskWrapper = new TaskWrapper();
        this.mSplitRemoteManager.bindService(new GetSessionStatesTask(this, taskWrapper, taskWrapper));
        return taskWrapper.getTask();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onBinderDied() {
        playCore.info("onBinderDied", new Object[0]);
        Bundle bundle = new Bundle();
        bundle.putInt("session_id", -1);
        bundle.putInt("status", 6);
        bundle.putInt("error_code", -9);
        Intent intent = new Intent();
        intent.setPackage(this.mPackageName);
        intent.setAction("com.google.android.play.core.splitinstall.receiver.SplitInstallUpdateIntentService");
        intent.putExtra("session_state", bundle);
        intent.addFlags(SearchUpdater.SIM);
        if (Build.VERSION.SDK_INT >= 26) {
            intent.addFlags(2097152);
        }
        this.mContext.sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Task<Integer> startInstall(List<String> list) {
        playCore.info("startInstall(%s)", list);
        TaskWrapper taskWrapper = new TaskWrapper();
        this.mSplitRemoteManager.bindService(new StartInstallTask(this, taskWrapper, list, taskWrapper));
        return taskWrapper.getTask();
    }
}
