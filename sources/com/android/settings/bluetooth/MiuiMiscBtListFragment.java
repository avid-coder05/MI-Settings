package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.RestrictedSettingsFragment;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import miui.bluetooth.ble.MiBleDeviceManager;
import miui.yellowpage.Tag;

/* loaded from: classes.dex */
public class MiuiMiscBtListFragment extends RestrictedSettingsFragment implements BluetoothCallback {
    static boolean mShowDevicesWithoutNames;
    private LocalBluetoothManager mLocalManager;
    private MiBleDeviceManager mMiBleDeviceManager;
    private boolean mMiBleDeviceManagerInited;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CheckAsyncTask extends AsyncTask<ArrayList<CachedBluetoothDevice>, Void, ArrayList<BluetoothDevicePreference>> {
        private WeakReference<MiuiMiscBtListFragment> fragmentWeakReference;
        private WeakReference<Context> preferenceWeakReference;
        private WeakReference<Context> weakReference;

        CheckAsyncTask(Context context, Context context2, MiuiMiscBtListFragment miuiMiscBtListFragment) {
            this.weakReference = new WeakReference<>(context);
            this.preferenceWeakReference = new WeakReference<>(context2);
            this.fragmentWeakReference = new WeakReference<>(miuiMiscBtListFragment);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        @SafeVarargs
        public final ArrayList<BluetoothDevicePreference> doInBackground(ArrayList<CachedBluetoothDevice>... arrayListArr) {
            ArrayList<BluetoothDevicePreference> arrayList = new ArrayList<>();
            ArrayList<CachedBluetoothDevice> arrayList2 = arrayListArr[0];
            Context context = this.preferenceWeakReference.get();
            if (context != null) {
                for (int i = 0; i < arrayList2.size(); i++) {
                    CachedBluetoothDevice cachedBluetoothDevice = arrayList2.get(i);
                    if (MiuiBTUtils.isRarelyUsedBluetoothDevice(cachedBluetoothDevice) && cachedBluetoothDevice.getBondState() != 12 && !GattProfile.isBond(cachedBluetoothDevice.getDevice()) && !MiuiBTUtils.isNearByBluetoothDevice(cachedBluetoothDevice)) {
                        if (MiuiMiscBtListFragment.mShowDevicesWithoutNames || cachedBluetoothDevice.hasHumanReadableName()) {
                            arrayList.add(new BluetoothDevicePreference(context, cachedBluetoothDevice, MiuiMiscBtListFragment.mShowDevicesWithoutNames));
                        } else {
                            Log.w("MiuiMiscBtListFragment", "mShowDevicesWithoutNames=" + MiuiMiscBtListFragment.mShowDevicesWithoutNames + " device=" + cachedBluetoothDevice.getAddress() + " hasHumanReadableName=" + cachedBluetoothDevice.hasHumanReadableName());
                        }
                    }
                }
            }
            return arrayList;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(ArrayList<BluetoothDevicePreference> arrayList) {
            MiuiMiscBtListFragment miuiMiscBtListFragment;
            WeakReference<Context> weakReference = this.weakReference;
            if (weakReference == null || weakReference.get() == null || (miuiMiscBtListFragment = this.fragmentWeakReference.get()) == null) {
                return;
            }
            Iterator<BluetoothDevicePreference> it = arrayList.iterator();
            while (it.hasNext()) {
                miuiMiscBtListFragment.addPreference(it.next());
            }
            Intent intent = new Intent();
            intent.putExtra(Tag.TagPhone.MARKED_COUNT, arrayList.size());
            miuiMiscBtListFragment.setResult(0, intent);
            miuiMiscBtListFragment.updatePreferences();
        }
    }

    public MiuiMiscBtListFragment() {
        super(null);
        this.mMiBleDeviceManagerInited = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addPreference(BluetoothDevicePreference bluetoothDevicePreference) {
        if (getPreferenceScreen() != null) {
            getPreferenceScreen().addPreference(bluetoothDevicePreference);
        }
    }

    private void addPreferencesForActivity() {
        createDevicePreferences(new ArrayList<>(this.mLocalManager.getCachedDeviceManager().getCachedDevicesCopy()));
    }

    private void removeBondedDevices(CachedBluetoothDevice cachedBluetoothDevice) {
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if (preference != null && (preference instanceof BluetoothDevicePreference)) {
                BluetoothDevicePreference bluetoothDevicePreference = (BluetoothDevicePreference) preference;
                if (bluetoothDevicePreference.getCachedDevice().getAddress().equals(cachedBluetoothDevice.getAddress())) {
                    getPreferenceScreen().removePreference(bluetoothDevicePreference);
                    return;
                }
            }
        }
    }

    void createDevicePreferences(ArrayList<CachedBluetoothDevice> arrayList) {
        new CheckAsyncTask(getActivity(), getPrefContext(), this).execute(arrayList);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onActiveDeviceChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onAudioModeChanged() {
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onBluetoothStateChanged(int i) {
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LocalBluetoothManager localBtManager = Utils.getLocalBtManager(getActivity());
        this.mLocalManager = localBtManager;
        if (localBtManager == null) {
            Log.e("MiuiMiscBtListFragment", "Bluetooth is not supported on this device");
            return;
        }
        mShowDevicesWithoutNames = SystemProperties.getBoolean("persist.bluetooth.showdeviceswithoutnames", false);
        this.mMiBleDeviceManager = MiBleDeviceManager.createManager(getActivity(), new MiBleDeviceManager.MiBleDeviceManagerListener() { // from class: com.android.settings.bluetooth.MiuiMiscBtListFragment.1
            @Override // miui.bluetooth.ble.MiBleDeviceManager.MiBleDeviceManagerListener
            public void onDestroy() {
                MiuiMiscBtListFragment.this.mMiBleDeviceManagerInited = false;
            }

            @Override // miui.bluetooth.ble.MiBleDeviceManager.MiBleDeviceManagerListener
            public void onInit(MiBleDeviceManager miBleDeviceManager) {
                MiuiMiscBtListFragment.this.mMiBleDeviceManagerInited = true;
                MiuiMiscBtListFragment.this.updatePreferences();
            }
        });
        addPreferencesFromResource(R.xml.device_picker);
        getPreferenceScreen().setOrderingAsAdded(false);
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mMiBleDeviceManager.close();
        super.onDestroy();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceAdded(CachedBluetoothDevice cachedBluetoothDevice) {
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        if (cachedBluetoothDevice.getBondState() == 12) {
            removeBondedDevices(cachedBluetoothDevice);
        }
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceDeleted(CachedBluetoothDevice cachedBluetoothDevice) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mLocalManager.setForegroundActivity(null);
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null || !defaultAdapter.isEnabled()) {
            return;
        }
        Log.d("MiuiMiscBtListFragment", "set scan mode connectable");
        defaultAdapter.setScanMode(21);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference instanceof BluetoothDevicePreference) {
            if (!(preference instanceof MiuiBluetoothDevicePreference)) {
                ((BluetoothDevicePreference) preference).onClicked();
                return true;
            }
            CachedBluetoothDevice cachedDevice = ((MiuiBluetoothDevicePreference) preference).getCachedDevice();
            if (this.mMiBleDeviceManager.getDeviceType(cachedDevice.getDevice().getAddress()) != 0) {
                MiuiBTUtils.gotoBleProfile(getActivity(), cachedDevice.getDevice());
                return true;
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mLocalManager.setForegroundActivity(getActivity());
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null || !defaultAdapter.isEnabled()) {
            return;
        }
        Log.d("MiuiMiscBtListFragment", "set scan mode connectable and discoverable");
        defaultAdapter.setScanMode(23);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onScanningStateChanged(boolean z) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        getListView().setItemAnimator(null);
        addPreferencesForActivity();
        this.mLocalManager.getEventManager().registerCallback(this);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        getPreferenceScreen().removeAll();
        super.onStop();
        this.mLocalManager.getEventManager().unregisterCallback(this);
    }

    protected void updatePreferences() {
        MiBleDeviceManager miBleDeviceManager;
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if (preference instanceof BluetoothDevicePreference) {
                CachedBluetoothDevice cachedDevice = ((BluetoothDevicePreference) preference).getCachedDevice();
                if (this.mMiBleDeviceManagerInited && (miBleDeviceManager = this.mMiBleDeviceManager) != null && miBleDeviceManager.getDeviceType(cachedDevice.getDevice().getAddress()) != 0) {
                    MiuiBluetoothDevicePreference miuiBluetoothDevicePreference = new MiuiBluetoothDevicePreference(getPrefContext(), cachedDevice, this.mMiBleDeviceManager, mShowDevicesWithoutNames);
                    getPreferenceScreen().removePreference(preference);
                    getPreferenceScreen().addPreference(miuiBluetoothDevicePreference);
                }
            }
        }
    }
}
