package com.android.settings.sound;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.text.TextUtils;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.Utils;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.HeadsetProfile;
import com.android.settingslib.bluetooth.HearingAidProfile;
import java.util.List;

/* loaded from: classes2.dex */
public class HandsFreeProfileOutputPreferenceController extends AudioSwitchPreferenceController implements Preference.OnPreferenceChangeListener {
    private static final int INVALID_INDEX = -1;

    public HandsFreeProfileOutputPreferenceController(Context context, String str) {
        super(context, str);
    }

    private int getConnectedDeviceIndex(String str) {
        List<BluetoothDevice> list = this.mConnectedDevices;
        if (list != null) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                if (TextUtils.equals(this.mConnectedDevices.get(i).getAddress(), str)) {
                    return i;
                }
            }
            return -1;
        }
        return -1;
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController
    public BluetoothDevice findActiveDevice() {
        BluetoothDevice findActiveHearingAidDevice = findActiveHearingAidDevice();
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        return (findActiveHearingAidDevice != null || headsetProfile == null) ? findActiveHearingAidDevice : headsetProfile.getActiveDevice();
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    int getDefaultDeviceIndex() {
        return this.mConnectedDevices.size();
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onAclConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        super.onAclConnectionStateChanged(cachedBluetoothDevice, i);
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        super.onConnectionStateChanged(cachedBluetoothDevice, i);
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        super.onDeviceBondStateChanged(cachedBluetoothDevice, i);
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onDeviceDeleted(CachedBluetoothDevice cachedBluetoothDevice) {
        super.onDeviceDeleted(cachedBluetoothDevice);
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onHearingAidAdded(CachedBluetoothDevice cachedBluetoothDevice) {
        super.onHearingAidAdded(cachedBluetoothDevice);
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onHearingAidDeleted(CachedBluetoothDevice cachedBluetoothDevice) {
        super.onHearingAidDeleted(cachedBluetoothDevice);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String str = (String) obj;
        if (preference instanceof ListPreference) {
            CharSequence text = this.mContext.getText(R.string.media_output_default_summary);
            ListPreference listPreference = (ListPreference) preference;
            if (TextUtils.equals(str, text)) {
                this.mSelectedIndex = getDefaultDeviceIndex();
                setActiveBluetoothDevice(null);
                listPreference.setSummary(text);
                return true;
            }
            int connectedDeviceIndex = getConnectedDeviceIndex(str);
            if (connectedDeviceIndex == -1) {
                return false;
            }
            BluetoothDevice bluetoothDevice = this.mConnectedDevices.get(connectedDeviceIndex);
            this.mSelectedIndex = connectedDeviceIndex;
            setActiveBluetoothDevice(bluetoothDevice);
            listPreference.setSummary(bluetoothDevice.getAlias());
            return true;
        }
        return false;
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onScanningStateChanged(boolean z) {
        super.onScanningStateChanged(z);
    }

    public void setActiveBluetoothDevice(BluetoothDevice bluetoothDevice) {
        if (Utils.isAudioModeOngoingCall(this.mContext)) {
            HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
            HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
            if (hearingAidProfile != null && headsetProfile != null && bluetoothDevice == null) {
                headsetProfile.setActiveDevice(null);
                hearingAidProfile.setActiveDevice(null);
            } else if (hearingAidProfile != null && bluetoothDevice != null && hearingAidProfile.getHiSyncId(bluetoothDevice) != 0) {
                hearingAidProfile.setActiveDevice(bluetoothDevice);
            } else if (headsetProfile != null) {
                headsetProfile.setActiveDevice(bluetoothDevice);
            }
        }
    }

    void setPreference(CharSequence[] charSequenceArr, CharSequence[] charSequenceArr2, Preference preference) {
        ListPreference listPreference = (ListPreference) preference;
        listPreference.setEntries(charSequenceArr);
        listPreference.setEntryValues(charSequenceArr2);
        listPreference.setValueIndex(this.mSelectedIndex);
        listPreference.setSummary(charSequenceArr[this.mSelectedIndex]);
        this.mAudioSwitchPreferenceCallback.onPreferenceDataChanged(listPreference);
    }

    void setupPreferenceEntries(CharSequence[] charSequenceArr, CharSequence[] charSequenceArr2, BluetoothDevice bluetoothDevice) {
        this.mSelectedIndex = getDefaultDeviceIndex();
        CharSequence text = this.mContext.getText(R.string.media_output_default_summary);
        int i = this.mSelectedIndex;
        charSequenceArr[i] = text;
        charSequenceArr2[i] = text;
        int size = this.mConnectedDevices.size();
        for (int i2 = 0; i2 < size; i2++) {
            BluetoothDevice bluetoothDevice2 = this.mConnectedDevices.get(i2);
            charSequenceArr[i2] = bluetoothDevice2.getAlias();
            charSequenceArr2[i2] = bluetoothDevice2.getAddress();
            if (bluetoothDevice2.equals(bluetoothDevice)) {
                this.mSelectedIndex = i2;
            }
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference == null) {
            return;
        }
        if (!Utils.isAudioModeOngoingCall(this.mContext)) {
            setVisible(this.mPreference, false);
            preference.setSummary(this.mContext.getText(R.string.media_output_default_summary));
            return;
        }
        this.mConnectedDevices.clear();
        this.mConnectedDevices.addAll(getConnectedHfpDevices());
        this.mConnectedDevices.addAll(getConnectedHearingAidDevices());
        int size = this.mConnectedDevices.size();
        if (size == 0) {
            setVisible(this.mPreference, false);
            CharSequence text = this.mContext.getText(R.string.media_output_default_summary);
            CharSequence[] charSequenceArr = {text};
            this.mSelectedIndex = getDefaultDeviceIndex();
            preference.setSummary(text);
            setPreference(charSequenceArr, charSequenceArr, preference);
            return;
        }
        setVisible(this.mPreference, true);
        int i = size + 1;
        CharSequence[] charSequenceArr2 = new CharSequence[i];
        CharSequence[] charSequenceArr3 = new CharSequence[i];
        setupPreferenceEntries(charSequenceArr2, charSequenceArr3, findActiveDevice());
        setPreference(charSequenceArr2, charSequenceArr3, preference);
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
