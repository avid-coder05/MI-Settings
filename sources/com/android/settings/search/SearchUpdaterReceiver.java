package com.android.settings.search;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.Log;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public class SearchUpdaterReceiver extends BroadcastReceiver {
    private static final String TAG = "SearchUpdater";
    private final IntentFilter filter;

    public SearchUpdaterReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        this.filter = intentFilter;
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (!"android.intent.action.HEADSET_PLUG".equals(intent.getAction()) || isInitialStickyBroadcast()) {
            return;
        }
        try {
            SoundUpdateHelper.headsetPlug(context.getApplicationContext(), new ArrayList(), intent.getIntExtra("state", 0) != 0);
        } catch (Exception e) {
            Log.e(TAG, "error occurs when updating headset", e);
        }
    }

    public void register(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService("audio");
        try {
            SoundUpdateHelper.headsetPlug(context.getApplicationContext(), new ArrayList(), audioManager.isWiredHeadsetOn());
        } catch (Exception e) {
            Log.e(TAG, "error occurs when updating headset", e);
        }
        context.registerReceiver(this, this.filter);
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }
}
