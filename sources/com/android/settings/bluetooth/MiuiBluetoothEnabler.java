package com.android.settings.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import androidx.preference.CheckBoxPreference;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothManager;

/* loaded from: classes.dex */
public final class MiuiBluetoothEnabler {
    private final Context mContext;
    private final IntentFilter mIntentFilter;
    private final LocalBluetoothAdapter mLocalAdapter;
    private CheckBoxPreference mPreference;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.MiuiBluetoothEnabler.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            MiuiBluetoothEnabler.this.handleStateChanged(intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE));
        }
    };

    public MiuiBluetoothEnabler(Context context, CheckBoxPreference checkBoxPreference) {
        this.mContext = context;
        setPreference(checkBoxPreference);
        LocalBluetoothManager localBtManager = Utils.getLocalBtManager(context);
        if (localBtManager == null) {
            this.mLocalAdapter = null;
            this.mPreference.setEnabled(false);
        } else {
            this.mLocalAdapter = localBtManager.getBluetoothAdapter();
        }
        this.mIntentFilter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStateChanged(int i) {
        switch (i) {
            case 11:
                CheckBoxPreference checkBoxPreference = this.mPreference;
                if (checkBoxPreference != null) {
                    checkBoxPreference.setEnabled(false);
                    return;
                }
                return;
            case 12:
                CheckBoxPreference checkBoxPreference2 = this.mPreference;
                if (checkBoxPreference2 != null) {
                    checkBoxPreference2.setChecked(true);
                    this.mPreference.setEnabled(true);
                    return;
                }
                return;
            case 13:
                CheckBoxPreference checkBoxPreference3 = this.mPreference;
                if (checkBoxPreference3 != null) {
                    checkBoxPreference3.setEnabled(false);
                    return;
                }
                return;
            default:
                CheckBoxPreference checkBoxPreference4 = this.mPreference;
                if (checkBoxPreference4 != null) {
                    checkBoxPreference4.setChecked(false);
                    this.mPreference.setEnabled(true);
                    return;
                }
                return;
        }
    }

    private boolean maybeEnforceRestrictions() {
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_bluetooth", UserHandle.myUserId());
        if (checkIfRestrictionEnforced == null) {
            checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_config_bluetooth", UserHandle.myUserId());
        }
        if (checkIfRestrictionEnforced != null && this.mPreference != null) {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mContext, checkIfRestrictionEnforced);
        }
        return checkIfRestrictionEnforced != null;
    }

    public void checkedChanged(boolean z) {
        if (maybeEnforceRestrictions()) {
            return;
        }
        LocalBluetoothAdapter localBluetoothAdapter = this.mLocalAdapter;
        if (localBluetoothAdapter != null) {
            localBluetoothAdapter.setBluetoothEnabled(z);
        }
        CheckBoxPreference checkBoxPreference = this.mPreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setEnabled(false);
        }
    }

    public void pause() {
        if (this.mLocalAdapter == null) {
            return;
        }
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    public void resume() {
        LocalBluetoothAdapter localBluetoothAdapter = this.mLocalAdapter;
        if (localBluetoothAdapter != null) {
            handleStateChanged(localBluetoothAdapter.getBluetoothState());
            this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
            return;
        }
        CheckBoxPreference checkBoxPreference = this.mPreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setEnabled(false);
        }
    }

    public void setPreference(CheckBoxPreference checkBoxPreference) {
        this.mPreference = checkBoxPreference;
        LocalBluetoothAdapter localBluetoothAdapter = this.mLocalAdapter;
        int bluetoothState = localBluetoothAdapter != null ? localBluetoothAdapter.getBluetoothState() : 10;
        boolean z = true;
        boolean z2 = bluetoothState == 12;
        boolean z3 = bluetoothState == 10;
        this.mPreference.setChecked(z2);
        CheckBoxPreference checkBoxPreference2 = this.mPreference;
        if (!z2 && !z3) {
            z = false;
        }
        checkBoxPreference2.setEnabled(z);
    }
}
