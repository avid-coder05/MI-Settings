package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.widget.TextView;
import com.android.settings.BaseSettingsController;
import com.android.settings.R;

/* loaded from: classes2.dex */
public class TetherStatusController extends BaseSettingsController {
    private boolean mHasRegister;
    private IntentFilter mIntentFilter;
    private final BroadcastReceiver mReceiver;

    public TetherStatusController(Context context, TextView textView) {
        super(context, textView);
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.TetherStatusController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                TetherStatusController.this.updateStatus();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("android.net.conn.TETHER_STATE_CHANGED");
    }

    public boolean isWifiTetherEnabled(Context context) {
        return ((WifiManager) context.getSystemService("wifi")).getWifiApState() == 13;
    }

    @Override // com.android.settings.BaseSettingsController
    public void pause() {
    }

    @Override // com.android.settings.BaseSettingsController
    public void resume() {
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

    @Override // com.android.settings.BaseSettingsController
    public void updateStatus() {
        TextView textView = this.mStatusView;
        if (textView != null) {
            textView.setText(isWifiTetherEnabled(this.mContext) ? R.string.wireless_on : R.string.wireless_off);
        }
    }
}
