package com.android.settings.connecteddevice.usb;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class MiUsbService extends Service {
    private boolean mShown = false;
    private BroadcastReceiver screenUnlockReceiver = new BroadcastReceiver() { // from class: com.android.settings.connecteddevice.usb.MiUsbService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (UsbModeChooserReceiver.mSoftSwitch || MiUsbService.this.mShown || !MiUsbService.this.isUsbConnected()) {
                return;
            }
            Intent intent2 = new Intent();
            intent2.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$UsbDetailsActivity"));
            intent2.addFlags(268435456);
            context.startActivity(intent2);
            MiUsbService.this.mShown = true;
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isUsbConnected() {
        Intent registerReceiver = registerReceiver(null, new IntentFilter("android.hardware.usb.action.USB_STATE"));
        if (registerReceiver != null) {
            return registerReceiver.getBooleanExtra(YellowPageContract.MipubPhoneEvent.EXTRA_DATA_CONNECTED, false);
        }
        return false;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        registerReceiver(this.screenUnlockReceiver, new IntentFilter("android.intent.action.USER_PRESENT"));
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.mShown = false;
        unregisterReceiver(this.screenUnlockReceiver);
    }
}
