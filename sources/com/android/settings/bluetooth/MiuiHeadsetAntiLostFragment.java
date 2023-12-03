package com.android.settings.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.bluetooth.ble.app.IMiuiHeadsetService;
import com.android.settings.MiuiSettingsPreferenceFragment;
import com.android.settings.R;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public final class MiuiHeadsetAntiLostFragment extends MiuiSettingsPreferenceFragment {
    private BluetoothDevice mDevice;
    private MiuiHeadsetActivity mHeadSetAct;
    private CheckBoxPreference mHeadsetAntiLostSwitch;
    private View mRootView;
    private IMiuiHeadsetService mService = null;
    private final Preference.OnPreferenceChangeListener mPrefChangeListener = new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetAntiLostFragment.1
        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            try {
                String key = preference.getKey();
                char c = 65535;
                if (key.hashCode() == -889731601 && key.equals("switch_mi_headset_loss_dialog")) {
                    c = 0;
                }
                MiuiHeadsetAntiLostFragment.this.updateLossDialog();
                return true;
            } catch (Exception e) {
                Log.e("MiuiHeadsetAntiLostFragment", "error " + e);
            }
            return false;
        }
    };

    private void gotoFindDeviceFragment() {
        this.mHeadSetAct.changeFragment(new MiuiHeadsetFindDeviceFragment());
    }

    private boolean isCheckOpen() {
        BluetoothDevice bluetoothDevice;
        IMiuiHeadsetService iMiuiHeadsetService;
        try {
            bluetoothDevice = this.mDevice;
        } catch (Exception e) {
            Log.w("MiuiHeadsetAntiLostFragment", "set checkbox failed " + e);
        }
        if (bluetoothDevice != null && (iMiuiHeadsetService = this.mService) != null) {
            String commonCommand = iMiuiHeadsetService.setCommonCommand(101, "", bluetoothDevice);
            Log.d("MiuiHeadsetAntiLostFragment", "isCheckOpen(): anti-lost tag is: " + commonCommand);
            if ("1".equals(commonCommand)) {
                return true;
            }
            if (TextUtils.isEmpty(commonCommand) || "".equals(commonCommand)) {
                Log.e("MiuiHeadsetAntiLostFragment", "device anti-lost tag is wrong");
                this.mService.setCommonCommand(100, "0", this.mDevice);
            }
            return false;
        }
        Log.d("MiuiHeadsetAntiLostFragment", "can not get device or service");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLossDialog() {
        IMiuiHeadsetService iMiuiHeadsetService;
        Log.d("MiuiHeadsetAntiLostFragment", "Update if Bluetooth headset will show dialog. ");
        try {
            BluetoothDevice bluetoothDevice = this.mDevice;
            if (bluetoothDevice != null && (iMiuiHeadsetService = this.mService) != null) {
                if (TextUtils.isEmpty(iMiuiHeadsetService.setCommonCommand(101, "", bluetoothDevice))) {
                    Log.d("MiuiHeadsetAntiLostFragment", "loss tag is not set, set it to open.");
                    this.mService.setCommonCommand(100, "1", this.mDevice);
                }
                if (this.mHeadsetAntiLostSwitch.isChecked()) {
                    Log.d("MiuiHeadsetAntiLostFragment", "Bluetooth headset is open, set to close. ");
                    this.mService.setCommonCommand(100, "0", this.mDevice);
                    return;
                }
                Log.d("MiuiHeadsetAntiLostFragment", "Bluetooth headset is close, set to open. ");
                this.mService.setCommonCommand(100, "1", this.mDevice);
                return;
            }
            Log.d("MiuiHeadsetAntiLostFragment", "can not get device or service");
        } catch (Exception e) {
            Log.e("MiuiHeadsetAntiLostFragment", "error " + e);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiHeadsetAntiLostFragment.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.headsetAntiLostLayout;
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MiuiHeadsetActivity miuiHeadsetActivity = (MiuiHeadsetActivity) activity;
        this.mDevice = miuiHeadsetActivity.getDevice();
        this.mHeadSetAct = miuiHeadsetActivity;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getPreferenceScreen().setOrderingAsAdded(false);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.headsetAntiLostLayout, viewGroup, false);
        this.mRootView = inflate;
        ((ViewGroup) inflate.findViewById(R.id.prefs_container)).addView(super.onCreateView(layoutInflater, viewGroup, bundle));
        ActionBar appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar();
        if (appCompatActionBar != null) {
            appCompatActionBar.setTitle(R.string.switch_headset_anti_lost_title);
        }
        this.mService = this.mHeadSetAct.getService();
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("switch_mi_headset_loss_dialog");
        this.mHeadsetAntiLostSwitch = checkBoxPreference;
        checkBoxPreference.setSummary(String.format(getResources().getQuantityString(R.plurals.switch_headset_anti_lost_summary, 3, 3), new Object[0]));
        this.mHeadsetAntiLostSwitch.setOnPreferenceChangeListener(this.mPrefChangeListener);
        if (this.mService != null) {
            this.mHeadsetAntiLostSwitch.setChecked(isCheckOpen());
        }
        return this.mRootView;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // androidx.fragment.app.Fragment
    public void onHiddenChanged(boolean z) {
        super.onHiddenChanged(z);
        ActionBar appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar();
        if (z || appCompatActionBar == null) {
            return;
        }
        appCompatActionBar.setTitle(R.string.switch_headset_anti_lost_title);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        key.hashCode();
        if (key.equals("find_device")) {
            gotoFindDeviceFragment();
            return false;
        }
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    public void onServiceConnected() {
        this.mService = this.mHeadSetAct.getService();
        this.mDevice = this.mHeadSetAct.getDevice();
        this.mHeadsetAntiLostSwitch.setChecked(isCheckOpen());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
    }
}
