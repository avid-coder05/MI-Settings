package com.android.settings;

import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Bundle;
import android.security.MiuiLockPatternUtils;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.util.Arrays;
import java.util.List;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class MiuiSecurityBluetoothSettings extends AppCompatActivity {
    private final List<String> fragmentDescriptors = Arrays.asList("com.android.settings.MiuiSecurityBluetoothSettingsFragment", "com.android.settings.MiuiSecurityBluetoothMatchDeviceFragment", "com.android.settings.MiuiSecurityChooseUnlock$MiuiSecurityChooseUnlockFragment");
    private boolean mEraseBluetoothUnlockSettings;

    @Override // miuix.appcompat.app.AppCompatActivity, android.app.Activity
    public void finish() {
        if (this.mEraseBluetoothUnlockSettings && ((KeyguardManager) getSystemService("keyguard")).isKeyguardSecure()) {
            MiuiLockPatternUtils miuiLockPatternUtils = new MiuiLockPatternUtils(this);
            miuiLockPatternUtils.setBluetoothUnlockEnabled(false);
            miuiLockPatternUtils.setBluetoothAddressToUnlock("");
            miuiLockPatternUtils.setBluetoothNameToUnlock("");
            miuiLockPatternUtils.setBluetoothKeyToUnlock("");
        }
        super.finish();
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mEraseBluetoothUnlockSettings = false;
        Intent intent = new Intent(super.getIntent());
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        if (TextUtils.isEmpty(intent.getStringExtra(":android:show_fragment"))) {
            if (!((KeyguardManager) getSystemService("keyguard")).isKeyguardSecure() || TextUtils.isEmpty(intent.getStringExtra("android.bluetooth.device.extra.NAME"))) {
                Intent intent2 = new Intent();
                intent2.setClass(this, MiuiSecurityChooseUnlock.class);
                startActivity(intent2);
                finish();
                return;
            }
            Bundle bundle2 = new Bundle();
            bundle2.putString("device_address", intent.getStringExtra("android.bluetooth.device.extra.NAME"));
            bundle2.putBoolean("password_confirmed", false);
            bundle2.putBoolean(":android:no_headers", true);
            try {
                bundle2.putString("DEVICE_TYPE", intent.getStringExtra("DEVICE_TYPE"));
                bundle2.putString("DEVICE_TYPE_MAJOR", intent.getStringExtra("DEVICE_TYPE_MAJOR"));
                bundle2.putString("DEVICE_TYPE_MINOR", intent.getStringExtra("DEVICE_TYPE_MINOR"));
            } catch (Exception e) {
                Log.e("MiuiSecurityBluetoothSettings", "error " + e);
            }
            if (((MiuiSecurityBluetoothMatchDeviceFragment) supportFragmentManager.findFragmentByTag("security_bluetooth_match_device_fragment")) == null) {
                MiuiSecurityBluetoothMatchDeviceFragment miuiSecurityBluetoothMatchDeviceFragment = new MiuiSecurityBluetoothMatchDeviceFragment();
                miuiSecurityBluetoothMatchDeviceFragment.setArguments(bundle2);
                beginTransaction.add(16908290, miuiSecurityBluetoothMatchDeviceFragment, "security_bluetooth_match_device_fragment");
                beginTransaction.commit();
            }
        }
    }
}
