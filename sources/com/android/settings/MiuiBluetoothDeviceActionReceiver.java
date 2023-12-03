package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.security.MiuiLockPatternUtils;

/* loaded from: classes.dex */
public class MiuiBluetoothDeviceActionReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        MiuiLockPatternUtils miuiLockPatternUtils = new MiuiLockPatternUtils(context);
        miuiLockPatternUtils.setBluetoothUnlockEnabled(false);
        miuiLockPatternUtils.setBluetoothAddressToUnlock("");
        miuiLockPatternUtils.setBluetoothNameToUnlock("");
        miuiLockPatternUtils.setBluetoothKeyToUnlock("");
    }
}
