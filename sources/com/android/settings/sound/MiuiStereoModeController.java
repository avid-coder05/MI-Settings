package com.android.settings.sound;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnResume;
import java.util.List;

/* loaded from: classes2.dex */
public class MiuiStereoModeController extends BasePreferenceController implements LifecycleObserver, OnResume, OnDestroy, Preference.OnPreferenceChangeListener {
    public static final boolean IS_SUPPORT_STEREO = SystemProperties.getBoolean("ro.vendor.audio.sfx.spk.stereo", false);
    private static final int STEREO_MODE_CLOSE = 0;
    private static final int STEREO_MODE_OPEN = 1;
    private static final String TAG = "MiuiStereoMode";
    private BluetoothStatusRecevier mBluetoothStatusRecevier;
    private HeadsetReceiver mHeadsetReceiver;
    private CheckBoxPreference mPreference;

    /* loaded from: classes2.dex */
    public class BluetoothStatusRecevier extends BroadcastReceiver {
        private boolean isBluetoothSpeakerConnected = false;
        private BluetoothProfile.ServiceListener mBluetoothA2dpServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settings.sound.MiuiStereoModeController.BluetoothStatusRecevier.1
            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                List<BluetoothDevice> connectedDevices = bluetoothProfile.getConnectedDevices();
                Log.d(MiuiStereoModeController.TAG, "onServiceConnected, deviceList size:" + connectedDevices.size());
                BluetoothStatusRecevier.this.isBluetoothSpeakerConnected = connectedDevices.size() != 0;
                BluetoothStatusRecevier.this.mBluetoothAdapter.closeProfileProxy(2, bluetoothProfile);
                MiuiStereoModeController.this.updateUI();
            }

            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceDisconnected(int i) {
                Log.d(MiuiStereoModeController.TAG, "onServiceDisconnected()");
            }
        };
        private BluetoothAdapter mBluetoothAdapter;
        private Context mContext;

        public BluetoothStatusRecevier(Context context) {
            this.mContext = context;
            isA2dpConnected();
        }

        private void isA2dpConnected() {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            this.mBluetoothAdapter = defaultAdapter;
            if (defaultAdapter == null || !defaultAdapter.isEnabled()) {
                return;
            }
            this.mBluetoothAdapter.getProfileProxy(this.mContext.getApplicationContext(), this.mBluetoothA2dpServiceListener, 2);
        }

        public boolean isBluetoothSpeakerConnected() {
            return this.isBluetoothSpeakerConnected;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED".equals(action)) {
                int intExtra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", -1);
                Log.i(MiuiStereoModeController.TAG, "onReceive action: " + action + " state=" + intExtra);
                this.isBluetoothSpeakerConnected = intExtra == 2;
                MiuiStereoModeController.this.updateUI();
            }
        }

        public void register() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED");
            this.mContext.registerReceiver(this, intentFilter);
        }

        public void unregister() {
            this.mContext.unregisterReceiver(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class HeadsetReceiver extends BroadcastReceiver {
        private boolean isHeadsetConnected = false;
        private Context mContext;

        public HeadsetReceiver(Context context) {
            this.mContext = context;
        }

        public boolean isHeadsetConnected() {
            return this.isHeadsetConnected;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.HEADSET_PLUG".equals(intent.getAction()) && intent.hasExtra("state")) {
                this.isHeadsetConnected = intent.getIntExtra("state", 0) == 1;
                Log.i(MiuiStereoModeController.TAG, "onReceive action: android.intent.action.HEADSET_PLUG state=" + this.isHeadsetConnected);
                MiuiStereoModeController.this.updateUI();
            }
        }

        public void register() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.HEADSET_PLUG");
            this.mContext.registerReceiver(this, intentFilter);
        }

        public void unregister() {
            this.mContext.unregisterReceiver(this);
        }
    }

    public MiuiStereoModeController(Context context, String str) {
        super(context, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUI() {
        HeadsetReceiver headsetReceiver = this.mHeadsetReceiver;
        if (headsetReceiver == null || this.mBluetoothStatusRecevier == null) {
            return;
        }
        if (headsetReceiver.isHeadsetConnected() || this.mBluetoothStatusRecevier.isBluetoothSpeakerConnected()) {
            this.mPreference.setEnabled(false);
        } else {
            this.mPreference.setEnabled(true);
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            this.mPreference = (CheckBoxPreference) preferenceScreen.findPreference(this.mPreferenceKey);
            this.mBluetoothStatusRecevier = new BluetoothStatusRecevier(this.mPreference.getContext());
            HeadsetReceiver headsetReceiver = new HeadsetReceiver(this.mPreference.getContext());
            this.mHeadsetReceiver = headsetReceiver;
            headsetReceiver.register();
            this.mBluetoothStatusRecevier.register();
            this.mPreference.setOnPreferenceChangeListener(this);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return IS_SUPPORT_STEREO ? 0 : 2;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        if (isAvailable()) {
            HeadsetReceiver headsetReceiver = this.mHeadsetReceiver;
            if (headsetReceiver != null) {
                headsetReceiver.unregister();
                this.mHeadsetReceiver = null;
            }
            BluetoothStatusRecevier bluetoothStatusRecevier = this.mBluetoothStatusRecevier;
            if (bluetoothStatusRecevier != null) {
                bluetoothStatusRecevier.unregister();
                this.mBluetoothStatusRecevier = null;
            }
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return false;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (isAvailable()) {
            updateUI();
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
