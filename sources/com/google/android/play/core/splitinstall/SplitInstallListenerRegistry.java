package com.google.android.play.core.splitinstall;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import com.google.android.play.core.listener.StateUpdateListenerRegister;
import com.google.android.play.core.splitcompat.util.PlayCore;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class SplitInstallListenerRegistry extends StateUpdateListenerRegister<SplitInstallSessionState> {
    private final SplitSessionLoader mLoader;
    final Handler mMainHandler;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitInstallListenerRegistry(Context context) {
        this(context, SplitSessionLoaderSingleton.get());
    }

    private SplitInstallListenerRegistry(Context context, SplitSessionLoader splitSessionLoader) {
        super(new PlayCore("SplitInstallListenerRegistry"), new IntentFilter("com.iqiyi.android.play.core.splitinstall.receiver.SplitInstallUpdateIntentService"), context);
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mLoader = splitSessionLoader;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.play.core.listener.StateUpdateListenerRegister
    public void onReceived(Intent intent) {
        SplitSessionLoader splitSessionLoader;
        SplitInstallSessionState createFrom = SplitInstallSessionState.createFrom(intent.getBundleExtra("session_state"));
        this.playCore.info("ListenerRegistryBroadcastReceiver.onReceive: %s", createFrom);
        if (createFrom.status() != 10 || (splitSessionLoader = this.mLoader) == null) {
            notifyListeners(createFrom);
        } else {
            splitSessionLoader.load(createFrom.splitFileIntents, new SplitSessionStatusChanger(this, createFrom));
        }
    }
}
