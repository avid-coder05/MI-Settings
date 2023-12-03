package com.android.settings.accessibility.accessibilitymenu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/* loaded from: classes.dex */
public final class ScreenMonitor extends BroadcastReceiver {
    public static final IntentFilter STATE_CHANGE_FILTER;
    private final ScreenStateChangeListener screenStateMonitor;

    /* loaded from: classes.dex */
    interface ScreenStateChangeListener {
        void screenTurnedOff();
    }

    static {
        IntentFilter intentFilter = new IntentFilter();
        STATE_CHANGE_FILTER = intentFilter;
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
    }

    public ScreenMonitor(ScreenStateChangeListener screenStateChangeListener) {
        this.screenStateMonitor = screenStateChangeListener;
    }

    @Override // android.content.BroadcastReceiver
    public final void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals("android.intent.action.SCREEN_OFF")) {
            this.screenStateMonitor.screenTurnedOff();
        }
    }
}
