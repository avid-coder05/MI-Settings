package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.R$drawable;
import com.android.settingslib.bluetooth.BluetoothUtils;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.util.HashMap;
import miui.os.Build;
import miuix.appcompat.app.AlertDialog;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class BluetoothBlacklistFragment extends DashboardFragment {
    private AlertDialog.Builder mBuilder;
    private Context mContext;
    PreferenceGroup mDeviceListGroup;
    private AlertDialog mDialog;
    LocalBluetoothManager mLocalManager;
    private View mRootView;

    private Drawable getHidClassDrawable(int i) {
        if (i != 1344) {
            if (i == 1408) {
                return this.mContext.getDrawable(R$drawable.ic_bt_pointing_hid);
            }
            if (i != 1472) {
                return this.mContext.getDrawable(R$drawable.ic_bt_misc_hid);
            }
        }
        return this.mContext.getDrawable(R$drawable.ic_lockscreen_ime);
    }

    private Drawable getIconByDeviceType(int i, int i2) {
        return i != 256 ? i != 512 ? i != 1280 ? i != 1536 ? BluetoothUtils.getBluetoothDrawable(this.mContext, R$drawable.ic_bt_bluetooth) : BluetoothUtils.getBluetoothDrawable(this.mContext, R$drawable.ic_bt_imaging) : getHidClassDrawable(i2) : BluetoothUtils.getBluetoothDrawable(this.mContext, R$drawable.ic_bt_cellphone) : BluetoothUtils.getBluetoothDrawable(this.mContext, R$drawable.ic_bt_laptop);
    }

    private int getScreenHeight() {
        WindowManager windowManager = (WindowManager) getActivity().getSystemService("window");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private void readyInfo() {
        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("BlackfileForBluetoothDevice", 0);
            for (String str : sharedPreferences.getAll().keySet()) {
                String string = sharedPreferences.getString(str, null);
                if (str != null) {
                    JSONObject jSONObject = new JSONObject(string);
                    String string2 = jSONObject.has("DeviceName") ? jSONObject.getString("DeviceName") : null;
                    Drawable iconByDeviceType = getIconByDeviceType(jSONObject.has("DeviceType") ? Integer.parseInt(jSONObject.getString("DeviceType")) : -1, jSONObject.has("DeviceClass") ? Integer.parseInt(jSONObject.getString("DeviceClass")) : -1);
                    Context context = this.mContext;
                    if (context == null) {
                        return;
                    }
                    Preference preference = new Preference(context);
                    preference.setLayoutResource(R.xml.preference_bt_device_blacklist);
                    if (string2 != null) {
                        preference.setTitle(string2);
                    } else {
                        preference.setTitle(str);
                    }
                    preference.setKey(str);
                    preference.setIcon(iconByDeviceType);
                    this.mDeviceListGroup.addPreference(preference);
                }
            }
        } catch (Exception e) {
            Log.d("BluetoothBlacklistFragment", "error when readyInfo", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refresh() {
        ViewGroup viewGroup = (ViewGroup) this.mRootView.findViewById(R.id.prefs_container);
        ViewGroup viewGroup2 = (ViewGroup) this.mRootView.findViewById(R.id.blank_screen);
        if (this.mDeviceListGroup.getPreferenceCount() != 0) {
            viewGroup.setVisibility(0);
            viewGroup2.setVisibility(4);
            return;
        }
        viewGroup.setVisibility(4);
        viewGroup2.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "BluetoothBlacklistFragment";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.bluetooth_device_blackfile;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LocalBluetoothManager localBtManager = Utils.getLocalBtManager(getActivity());
        this.mLocalManager = localBtManager;
        if (localBtManager == null) {
            Log.e("BluetoothBlacklistFragment", "Bluetooth is not supported on this device");
            return;
        }
        this.mContext = getActivity().getApplicationContext();
        this.mDeviceListGroup = (PreferenceGroup) findPreference("bluetooth_device_blacklist");
        this.mBuilder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog_Theme_DayNight);
        HashMap hashMap = new HashMap();
        hashMap.put("Manage_blocked_Bluetooth_devices", Boolean.TRUE);
        if ("CN".equals(Build.getRegion())) {
            OneTrackInterfaceUtils.track("bluetooth_blocklist", hashMap);
        }
        readyInfo();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.no_device_icon, viewGroup, false);
        this.mRootView = inflate;
        ((ViewGroup) inflate.findViewById(R.id.prefs_container)).addView(super.onCreateView(layoutInflater, viewGroup, bundle));
        ViewGroup viewGroup2 = (ViewGroup) this.mRootView.findViewById(R.id.blank_screen);
        int screenHeight = getScreenHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewGroup2.getLayoutParams();
        layoutParams.bottomMargin = (screenHeight - layoutParams.height) / 2;
        viewGroup2.setLayoutParams(layoutParams);
        refresh();
        return this.mRootView;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && defaultAdapter.isEnabled()) {
            Log.d("BluetoothBlacklistFragment", "set scan mode connectable");
            defaultAdapter.setScanMode(21);
        }
        LocalBluetoothManager localBluetoothManager = this.mLocalManager;
        if (localBluetoothManager != null) {
            localBluetoothManager.setForegroundActivity(null);
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(final Preference preference) {
        final String key = preference.getKey();
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences("BlackfileForBluetoothDevice", 0);
        String str = null;
        try {
            str = new JSONObject(sharedPreferences.getString(key, null)).getString("DeviceName");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mBuilder.setTitle(R.string.bluetooth_move_out_of_blacklist);
        if (str != null) {
            this.mBuilder.setMessage(getString(R.string.bluetooth_message_move_out_of_blacklist, str));
        } else {
            this.mBuilder.setMessage(getString(R.string.bluetooth_message_move_out_of_blacklist, key));
        }
        this.mBuilder.setPositiveButton(R.string.bluetooth_blacklist_remove_device, new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothBlacklistFragment.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.remove(key);
                    edit.apply();
                    BluetoothBlacklistFragment.this.mDeviceListGroup.removePreference(preference);
                    if (BluetoothBlacklistFragment.this.mDeviceListGroup.getPreferenceCount() == 0) {
                        BluetoothBlacklistFragment.this.refresh();
                    }
                } catch (Exception e2) {
                    Log.d("BluetoothBlacklistFragment", "error when readyInfo", e2);
                }
            }
        });
        this.mBuilder.setNegativeButton(R.string.bluetooth_blacklist_cancel_remove_device, new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothBlacklistFragment.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog create = this.mBuilder.create();
        this.mDialog = create;
        create.setCanceledOnTouchOutside(false);
        this.mDialog.show();
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && defaultAdapter.isEnabled()) {
            Log.d("BluetoothBlacklistFragment", "set scan mode connectable and discoverable");
            defaultAdapter.setScanMode(23);
        }
        LocalBluetoothManager localBluetoothManager = this.mLocalManager;
        if (localBluetoothManager != null) {
            localBluetoothManager.setForegroundActivity(getActivity());
        }
    }
}
