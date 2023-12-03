package com.google.android.play.core.listener;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.google.android.play.core.splitcompat.util.PlayCore;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes2.dex */
public abstract class StateUpdateListenerRegister<StateT> {
    private final Context context;
    private final IntentFilter intentFilter;
    protected final PlayCore playCore;
    private final Set<StateUpdatedListener<StateT>> mStateUpdatedListeners = Collections.newSetFromMap(new ConcurrentHashMap());
    private final StateUpdatedReceiver receiver = new StateUpdatedReceiver(this);
    private final Object mLock = new Object();

    /* JADX INFO: Access modifiers changed from: protected */
    public StateUpdateListenerRegister(PlayCore playCore, IntentFilter intentFilter, Context context) {
        this.playCore = playCore;
        this.intentFilter = intentFilter;
        this.context = context;
    }

    public final void notifyListeners(StateT statet) {
        Iterator<StateUpdatedListener<StateT>> it = this.mStateUpdatedListeners.iterator();
        while (it.hasNext()) {
            it.next().onStateUpdate(statet);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void onReceived(Intent intent);

    public final void registerListener(StateUpdatedListener<StateT> stateUpdatedListener) {
        synchronized (this.mLock) {
            this.playCore.debug("registerListener", new Object[0]);
            if (this.mStateUpdatedListeners.contains(stateUpdatedListener)) {
                this.playCore.debug("listener has been registered!", new Object[0]);
                return;
            }
            this.mStateUpdatedListeners.add(stateUpdatedListener);
            if (this.mStateUpdatedListeners.size() == 1) {
                try {
                    this.context.registerReceiver(this.receiver, this.intentFilter);
                } catch (Throwable unused) {
                }
            }
        }
    }

    public final void unregisterListener(StateUpdatedListener<StateT> stateUpdatedListener) {
        synchronized (this.mLock) {
            this.playCore.debug("unregisterListener", new Object[0]);
            boolean remove = this.mStateUpdatedListeners.remove(stateUpdatedListener);
            if (this.mStateUpdatedListeners.isEmpty() && remove) {
                try {
                    this.context.unregisterReceiver(this.receiver);
                } catch (IllegalArgumentException e) {
                    this.playCore.error(e, "Receiver not registered: " + this.intentFilter.getAction(0), new Object[0]);
                }
            }
        }
    }
}
