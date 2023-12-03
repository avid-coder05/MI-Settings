package com.android.settings.connecteddevice.usb;

import android.content.Context;
import android.os.SystemProperties;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R;
import com.android.settings.Utils;

/* loaded from: classes.dex */
public class UsbDetailsTranscodeMtpController extends UsbDetailsController implements Preference.OnPreferenceClickListener {
    private PreferenceCategory mPreferenceCategory;
    private SwitchPreference mSwitchPreference;

    public UsbDetailsTranscodeMtpController(Context context, UsbDetailsFragment usbDetailsFragment, UsbBackend usbBackend) {
        super(context, usbDetailsFragment, usbBackend);
    }

    private static boolean isDeviceInFileTransferMode(long j, int i) {
        return i == 2 && !((4 & j) == 0 && (j & 16) == 0);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreferenceCategory = preferenceCategory;
        SwitchPreference switchPreference = new SwitchPreference(preferenceCategory.getContext());
        this.mSwitchPreference = switchPreference;
        switchPreference.setTitle(R.string.usb_transcode_files);
        this.mSwitchPreference.setOnPreferenceClickListener(this);
        this.mSwitchPreference.setSummaryOn(R.string.usb_transcode_files_summary);
        this.mPreferenceCategory.addPreference(this.mSwitchPreference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "usb_transcode_mtp";
    }

    @Override // com.android.settings.connecteddevice.usb.UsbDetailsController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !Utils.isMonkeyRunning();
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        SystemProperties.set("sys.fuse.transcode_mtp", Boolean.toString(this.mSwitchPreference.isChecked()));
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.connecteddevice.usb.UsbDetailsController
    public void refresh(boolean z, long j, int i, int i2) {
        if (this.mUsbBackend.areFunctionsSupported(20L)) {
            this.mFragment.getPreferenceScreen().addPreference(this.mPreferenceCategory);
        } else {
            this.mFragment.getPreferenceScreen().removePreference(this.mPreferenceCategory);
        }
        boolean z2 = false;
        this.mSwitchPreference.setChecked(SystemProperties.getBoolean("sys.fuse.transcode_mtp", false));
        PreferenceCategory preferenceCategory = this.mPreferenceCategory;
        if (z && isDeviceInFileTransferMode(j, i2)) {
            z2 = true;
        }
        preferenceCategory.setEnabled(z2);
    }
}
