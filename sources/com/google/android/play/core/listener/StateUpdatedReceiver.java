package com.google.android.play.core.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* loaded from: classes2.dex */
public class StateUpdatedReceiver extends BroadcastReceiver {
    private final StateUpdateListenerRegister mRegister;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StateUpdatedReceiver(StateUpdateListenerRegister stateUpdateListenerRegister) {
        this.mRegister = stateUpdateListenerRegister;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        this.mRegister.onReceived(intent);
    }
}
