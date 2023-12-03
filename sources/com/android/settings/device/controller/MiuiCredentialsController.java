package com.android.settings.device.controller;

import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.device.MiuiAboutPhoneUtils;
import java.util.ArrayList;
import java.util.Arrays;
import miui.os.Build;

/* loaded from: classes.dex */
public class MiuiCredentialsController extends BaseDeviceInfoController {
    private final String VERFICATION_DEVICE_LIST;
    private ArrayList<String> mVerfDeviceList;

    public MiuiCredentialsController(Context context) {
        super(context);
        this.VERFICATION_DEVICE_LIST = "show_verification_device_list";
    }

    private void setCredentialTitle(Preference preference) {
        if (this.mVerfDeviceList == null) {
            this.mVerfDeviceList = new ArrayList<>();
            this.mVerfDeviceList.addAll(Arrays.asList(MiuiAboutPhoneUtils.queryStringArray(this.mContext, "show_verification_device_list")));
        }
        preference.setTitle((Build.IS_INTERNATIONAL_BUILD && this.mVerfDeviceList.contains(android.os.Build.DEVICE)) ? R.string.credentials_title_verification : R.string.approve_title);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        if (findPreference != null) {
            setCredentialTitle(findPreference);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "credentials";
    }

    @Override // com.android.settings.device.controller.BaseDeviceInfoController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return MiuiAboutPhoneUtils.enableShowCredentials();
    }
}
