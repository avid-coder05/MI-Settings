package com.android.settings.development;

import android.content.Context;
import android.os.UserHandle;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.connecteddevice.usb.UsbBackend;
import com.android.settings.connecteddevice.usb.UsbDetailsFunctionsController;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

/* loaded from: classes.dex */
public class DefaultUsbConfigurationPreferenceController extends DeveloperOptionsPreferenceController {
    private Context mContext;
    private RestrictedPreference mPreference;
    private UsbBackend mUsbBackend;

    public DefaultUsbConfigurationPreferenceController(Context context) {
        super(context);
        this.mUsbBackend = new UsbBackend(context);
        this.mContext = context;
    }

    private void refreshPreferenceValue() {
        RestrictedPreference restrictedPreference = this.mPreference;
        if (restrictedPreference != null) {
            restrictedPreference.setShowRightArrow(true);
            long currentFunctions = this.mUsbBackend.getCurrentFunctions();
            try {
                this.mPreference.setValue(this.mContext.getString(UsbDetailsFunctionsController.getFunction(currentFunctions)));
            } catch (Exception e) {
                Log.e("DefaultUsbConfigurationPreferenceController", "refreshPreferenceValue error: " + e.getMessage() + "; option: " + currentFunctions);
            }
        }
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedPreference) preferenceScreen.findPreference(getPreferenceKey());
        refreshPreferenceValue();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "default_usb_configuration";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchEnabled() {
        super.onDeveloperOptionsSwitchEnabled();
        this.mPreference.setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfUsbDataSignalingIsDisabled(this.mContext, UserHandle.myUserId()));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mPreference.setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfUsbDataSignalingIsDisabled(this.mContext, UserHandle.myUserId()));
        refreshPreferenceValue();
    }
}
