package com.android.settings.bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.MiuiSettingsPreferenceFragment;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.EditTextPreference;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public final class MiuiHeadsetVirtualDeviceFragment extends MiuiSettingsPreferenceFragment {
    private EditTextPreference mDeviceNamePref;
    private MiuiHeadsetActivity mHeadSetAct;
    private View mRootView;
    private String mVirtualDeviceAddress;
    private String mVirtualDeviceName;

    /* JADX INFO: Access modifiers changed from: private */
    public void unpairDevice() {
        Log.e("MiuiHeadsetVirtualDeviceFragment", "unpair device!");
        FragmentActivity activity = getActivity();
        new MiuiOnSavedDeviceDataUtils(activity).deleteDeviceData(this.mVirtualDeviceAddress);
        Settings.Global.putString(activity.getContentResolver(), "virtual_bluetooth_device_delete", this.mVirtualDeviceAddress);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiHeadsetVirtualDeviceFragment.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.bluetooth_virtualdevice_unpaire_device;
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MiuiHeadsetActivity miuiHeadsetActivity = (MiuiHeadsetActivity) activity;
        this.mHeadSetAct = miuiHeadsetActivity;
        this.mVirtualDeviceAddress = miuiHeadsetActivity.getVirtualDeviceAddress();
        this.mVirtualDeviceName = this.mHeadSetAct.getVirtualDeviceName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getPreferenceScreen().setOrderingAsAdded(false);
        this.mDeviceNamePref = (EditTextPreference) findPreference("fake_rename_device");
        if (TextUtils.isEmpty(this.mVirtualDeviceName)) {
            return;
        }
        this.mDeviceNamePref.setSummary(this.mVirtualDeviceName);
        this.mDeviceNamePref.setText(this.mVirtualDeviceName);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.bluetooth_virtualdevice_unpaire_device, viewGroup, false);
        this.mRootView = inflate;
        ((ViewGroup) inflate.findViewById(R.id.prefs_container)).addView(super.onCreateView(layoutInflater, viewGroup, bundle));
        ActionBar appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar();
        if (appCompatActionBar != null) {
            appCompatActionBar.setTitle(R.string.bluetooth_device_advanced_title);
        }
        View view = this.mRootView;
        if (view != null) {
            ((CheckedTextView) view.findViewById(R.id.button_delete)).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetVirtualDeviceFragment.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    MiuiHeadsetVirtualDeviceFragment.this.unpairDevice();
                    MiuiHeadsetVirtualDeviceFragment.this.finish();
                }
            });
        }
        return this.mRootView;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals("fake_unpair")) {
            unpairDevice();
            finish();
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
