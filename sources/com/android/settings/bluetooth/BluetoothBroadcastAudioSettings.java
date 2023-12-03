package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import androidx.fragment.app.Fragment;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.core.AbstractPreferenceController;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class BluetoothBroadcastAudioSettings extends DashboardFragment {
    private static boolean mBroadcastEnabled;
    private static boolean mBroadcastPropertyChecked;
    LocalBluetoothManager mLocalManager;

    private static boolean isBroadcastEnabled() {
        return mBroadcastEnabled;
    }

    private static boolean isBroadcastPropertyChecked() {
        return mBroadcastPropertyChecked;
    }

    private static void setBroadcastEnabled(boolean z) {
        mBroadcastEnabled = z;
    }

    private static void setBroadcastProperty(boolean z) {
        mBroadcastPropertyChecked = z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        if (!isBroadcastPropertyChecked()) {
            setBroadcastEnabled((SystemProperties.getInt("persist.vendor.service.bt.adv_audio_mask", 0) & 4) == 4 && SystemProperties.getBoolean("persist.bluetooth.broadcast_ui", true));
            setBroadcastProperty(true);
        }
        if (!isBroadcastEnabled()) {
            Log.d("BluetoothBroadcastAudioSettings", "[ZZQ] createPreferenceControllers mBroadcastEnabled is false");
            return arrayList;
        }
        Log.d("BluetoothBroadcastAudioSettings", "createPreferenceControllers for Broadcast");
        try {
            try {
                Class<?> cls = Class.forName("com.android.settings.bluetooth.BluetoothBroadcastPinController");
                Class<?> cls2 = Class.forName("com.android.settings.bluetooth.BluetoothBroadcastEnableController");
                Constructor<?> declaredConstructor = cls.getDeclaredConstructor(Context.class);
                Constructor<?> declaredConstructor2 = cls2.getDeclaredConstructor(Context.class, String.class);
                Object newInstance = declaredConstructor.newInstance(context);
                Object newInstance2 = declaredConstructor2.newInstance(context, "bluetooth_broadcast_enable");
                newInstance.getClass().getMethod("setFragment", Fragment.class).invoke(newInstance, this);
                arrayList.add((AbstractPreferenceController) newInstance);
                arrayList.add((AbstractPreferenceController) newInstance2);
                return arrayList;
            } catch (Fragment.InstantiationException | ClassNotFoundException | ExceptionInInitializerError | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException e) {
                setBroadcastEnabled(false);
                e.printStackTrace();
                return arrayList;
            }
        } catch (Throwable unused) {
            return arrayList;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "BluetoothBroadcastAudioSettings";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.bluetooth_broadcast_audio_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LocalBluetoothManager localBtManager = Utils.getLocalBtManager(getActivity());
        this.mLocalManager = localBtManager;
        if (localBtManager == null) {
            Log.e("BluetoothBroadcastAudioSettings", "Bluetooth is not supported on this device");
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && defaultAdapter.isEnabled()) {
            Log.d("BluetoothBroadcastAudioSettings", "set scan mode connectable");
            defaultAdapter.setScanMode(21);
        }
        LocalBluetoothManager localBluetoothManager = this.mLocalManager;
        if (localBluetoothManager != null) {
            localBluetoothManager.setForegroundActivity(null);
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && defaultAdapter.isEnabled()) {
            Log.d("BluetoothBroadcastAudioSettings", "set scan mode connectable and discoverable");
            defaultAdapter.setScanMode(23);
        }
        LocalBluetoothManager localBluetoothManager = this.mLocalManager;
        if (localBluetoothManager != null) {
            localBluetoothManager.setForegroundActivity(getActivity());
        }
    }
}
