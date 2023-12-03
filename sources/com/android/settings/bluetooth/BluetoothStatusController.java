package com.android.settings.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;
import com.android.settings.BaseSettingsController;
import com.android.settings.R;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;

/* loaded from: classes.dex */
public class BluetoothStatusController extends BaseSettingsController {
    private boolean mHasRegister;
    private final IntentFilter mIntentFilter;
    private final LocalBluetoothAdapter mLocalAdapter;
    private final BroadcastReceiver mReceiver;

    public BluetoothStatusController(Context context, TextView textView) {
        super(context, textView);
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.BluetoothStatusController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                BluetoothStatusController.this.handleStateChanged(intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE));
            }
        };
        this.mLocalAdapter = LocalBluetoothAdapter.getInstance();
        this.mIntentFilter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
    }

    void handleStateChanged(int i) {
        TextView textView = this.mStatusView;
        if (textView == null) {
            return;
        }
        textView.setVisibility(0);
        if (i == 11 || i == 12) {
            this.mStatusView.setText(R.string.wireless_on);
        } else {
            this.mStatusView.setText(R.string.wireless_off);
        }
    }

    @Override // com.android.settings.BaseSettingsController
    public void pause() {
        if (this.mLocalAdapter == null) {
            handleStateChanged(10);
        }
    }

    @Override // com.android.settings.BaseSettingsController
    public void resume() {
        LocalBluetoothAdapter localBluetoothAdapter = this.mLocalAdapter;
        if (localBluetoothAdapter == null) {
            handleStateChanged(10);
        } else {
            handleStateChanged(localBluetoothAdapter.getBluetoothState());
        }
    }

    @Override // com.android.settings.BaseSettingsController
    public void start() {
        this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
        this.mHasRegister = true;
    }

    @Override // com.android.settings.BaseSettingsController
    public void stop() {
        if (this.mHasRegister) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.mHasRegister = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.BaseSettingsController
    public void updateStatus() {
        LocalBluetoothAdapter localBluetoothAdapter = this.mLocalAdapter;
        if (localBluetoothAdapter == null) {
            handleStateChanged(10);
        } else {
            handleStateChanged(localBluetoothAdapter.getBluetoothState());
        }
    }
}
