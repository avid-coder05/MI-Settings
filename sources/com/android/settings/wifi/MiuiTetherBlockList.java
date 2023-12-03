package com.android.settings.wifi;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.MacAddress;
import android.net.wifi.MiuiWifiManager;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import java.util.Set;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class MiuiTetherBlockList extends SettingsPreferenceFragment {
    private Set<String> mBlockList;
    private SharedPreferences mBlockListPrefs;
    private RemoveDeviceToBlockListDialog mDialog;
    private boolean mDialogShow;
    private MiuiWifiManager mMiuiWifiManager;
    private SoftApConfiguration mSoftApConfig;
    private WifiManager mWifiManager;

    /* loaded from: classes2.dex */
    private class RemoveDeviceToBlockListDialog implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
        private boolean mConfigureConfirmed;
        private AlertDialog mDialog;
        private MacAddress macAddress;

        private RemoveDeviceToBlockListDialog(MacAddress macAddress) {
            FragmentActivity activity = MiuiTetherBlockList.this.getActivity();
            this.macAddress = macAddress;
            String macAddress2 = macAddress.toString();
            AlertDialog create = new AlertDialog.Builder(activity).setTitle(activity.getString(R.string.block_list_remove_dialog_title)).setMessage(String.format(activity.getString(R.string.block_list_remove_dialog_content), macAddress2)).setIconAttribute(16843605).setPositiveButton(17039370, this).setNegativeButton(17039360, this).create();
            this.mDialog = create;
            create.setOnDismissListener(this);
        }

        private void removePreference() {
            PreferenceScreen preferenceScreen = MiuiTetherBlockList.this.getPreferenceScreen();
            for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
                Preference preference = preferenceScreen.getPreference(i);
                if (MiuiTetherBlockList.this.getPreferenceMacAddress(preference).equals(getDeviceInfo())) {
                    preferenceScreen.removePreference(preference);
                    return;
                }
            }
        }

        public String getDeviceInfo() {
            return this.macAddress.toString();
        }

        public boolean isShowing() {
            return this.mDialog.isShowing();
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            this.mConfigureConfirmed = i == -1;
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialogInterface) {
            MiuiTetherBlockList.this.mDialogShow = false;
            if (this.mConfigureConfirmed) {
                removePreference();
                if (MiuiUtils.getInstance().isSapBlacklistOffloadSupport(MiuiTetherBlockList.this.getContext())) {
                    MiuiTetherBlockList.this.mMiuiWifiManager.delHotSpotMacBlackListOffload(this.macAddress.toString());
                }
                MiuiTetherBlockList.this.mBlockList.remove(this.macAddress.toString());
                MiuiUtils.getInstance().setHotSpotMacBlackSet(MiuiTetherBlockList.this.getContext(), MiuiTetherBlockList.this.mBlockList);
                this.mConfigureConfirmed = false;
            }
        }

        public void show() {
            MiuiTetherBlockList.this.mDialogShow = true;
            this.mDialog.show();
        }
    }

    private void addBlockListPreferences() {
        getPreferenceScreen().removeAll();
        for (String str : this.mBlockList) {
            String string = this.mBlockListPrefs.getString(str, null);
            if (string == null) {
                Log.w("MiuiTetherBlockList", "something wrong, no device name, mac = " + str);
            }
            ValuePreference valuePreference = new ValuePreference(getPrefContext());
            if (TextUtils.isEmpty(string)) {
                valuePreference.setTitle(str);
            } else {
                valuePreference.setTitle(string);
                valuePreference.setSummary(str);
            }
            valuePreference.setShowRightArrow(false);
            getPreferenceScreen().addPreference(valuePreference);
        }
    }

    private void cleanDeviceInfoInSharedPreferences() {
        for (String str : this.mBlockListPrefs.getAll().keySet()) {
            if (!this.mBlockList.contains(str)) {
                removeInfoFromSharedPreferences(str);
            }
        }
    }

    private void removeInfoFromSharedPreferences(String str) {
        SharedPreferences.Editor edit = this.mBlockListPrefs.edit();
        edit.remove(str);
        edit.commit();
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiTetherBlockList.class.getName();
    }

    public String getPreferenceMacAddress(Preference preference) {
        return (preference.getSummary() == null ? preference.getTitle() : preference.getSummary()).toString();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.miui_tether_block_list);
        this.mWifiManager = (WifiManager) getActivity().getSystemService("wifi");
        this.mMiuiWifiManager = (MiuiWifiManager) getSystemService("MiuiWifiService");
        this.mSoftApConfig = this.mWifiManager.getSoftApConfiguration();
        this.mBlockList = MiuiUtils.getInstance().getHotSpotMacBlackSet(getContext());
        this.mBlockListPrefs = getActivity().getApplicationContext().getSharedPreferences("tetherBlockListPrefs", 0);
        cleanDeviceInfoInSharedPreferences();
        addBlockListPreferences();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        RemoveDeviceToBlockListDialog removeDeviceToBlockListDialog = new RemoveDeviceToBlockListDialog(MacAddress.fromString(getPreferenceMacAddress(preference)));
        this.mDialog = removeDeviceToBlockListDialog;
        removeDeviceToBlockListDialog.show();
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        Bundle bundle = getArguments() != null ? getArguments().getBundle("saved_bundle") : null;
        if (bundle != null) {
            boolean z = bundle.getBoolean("show_dialog");
            this.mDialogShow = z;
            if (z) {
                RemoveDeviceToBlockListDialog removeDeviceToBlockListDialog = this.mDialog;
                if (removeDeviceToBlockListDialog == null || !removeDeviceToBlockListDialog.isShowing()) {
                    RemoveDeviceToBlockListDialog removeDeviceToBlockListDialog2 = new RemoveDeviceToBlockListDialog(MacAddress.fromString(bundle.getString("save_device_mac")));
                    this.mDialog = removeDeviceToBlockListDialog2;
                    removeDeviceToBlockListDialog2.show();
                }
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.mDialog != null) {
            bundle.putBoolean("show_dialog", this.mDialogShow);
            bundle.putString("save_device_mac", this.mDialog.getDeviceInfo());
            getArguments().putBundle("saved_bundle", bundle);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        View inflate = View.inflate(getActivity(), R.layout.tether_no_device_connected, null);
        ((TextView) inflate.findViewById(R.id.tether_no_device)).setText(R.string.block_list_no_device);
        ((ViewGroup) getListView().getParent()).addView(inflate);
        setEmptyView(inflate);
    }
}
