package com.android.settings.nfc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;

/* loaded from: classes2.dex */
public abstract class BaseNfcEnabler {
    protected final Context mContext;
    private final IntentFilter mIntentFilter;
    protected final NfcAdapter mNfcAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.nfc.BaseNfcEnabler.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.nfc.action.ADAPTER_STATE_CHANGED".equals(intent.getAction())) {
                BaseNfcEnabler.this.handleNfcStateChanged(intent.getIntExtra("android.nfc.extra.ADAPTER_STATE", 1));
            }
        }
    };

    public BaseNfcEnabler(Context context) {
        this.mContext = context;
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (isNfcAvailable()) {
            this.mIntentFilter = new IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED");
        } else {
            this.mIntentFilter = null;
        }
    }

    protected abstract void handleNfcStateChanged(int i);

    public boolean isNfcAvailable() {
        return this.mNfcAdapter != null;
    }

    public void pause() {
        if (isNfcAvailable()) {
            try {
                this.mContext.unregisterReceiver(this.mReceiver);
            } catch (IllegalArgumentException unused) {
            }
        }
    }

    public void resume() {
        if (isNfcAvailable()) {
            handleNfcStateChanged(this.mNfcAdapter.getAdapterState());
            this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
        }
    }
}
