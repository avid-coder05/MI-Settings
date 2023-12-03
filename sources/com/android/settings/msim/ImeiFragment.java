package com.android.settings.msim;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.internal.telephony.Phone;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.Utils;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import miui.telephony.SubscriptionManager;

/* loaded from: classes.dex */
public class ImeiFragment extends SettingsPreferenceFragment {
    private static final String[] PHONE_RELATED_ENTRIES = {"imei", "imei_sv", "prl_version", "min_number", "meid_number", "icc_id"};
    private Phone mPhone = null;
    private Resources mRes;
    private int mSlotId;
    private TelephonyManager mTelephonyManager;
    private String sUnknown;

    private void removePreferenceFromScreen(String str) {
        Preference findPreference = findPreference(str);
        if (findPreference != null) {
            getPreferenceScreen().removePreference(findPreference);
        }
    }

    private void setSummaryText(String str, String str2) {
        if (TextUtils.isEmpty(str2)) {
            str2 = this.sUnknown;
        }
        if (findPreference(str) != null) {
            ((ValuePreference) findPreference(str)).setValue(str2);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSlotId = SubscriptionManager.getSlotId(getArguments(), 0);
        this.mTelephonyManager = TelephonyManager.from(getActivity());
        addPreferencesFromResource(R.xml.status_imei);
        Resources resources = getResources();
        this.mRes = resources;
        this.sUnknown = resources.getString(R.string.device_info_default);
        if (UserHandle.myUserId() == 0) {
            this.mPhone = MiuiUtils.getInstance().getPhone(this.mSlotId);
        }
        if (this.mPhone == null || Utils.isWifiOnly(getActivity().getApplicationContext())) {
            for (String str : PHONE_RELATED_ENTRIES) {
                removePreferenceFromScreen(str);
            }
        } else if (this.mPhone.getPhoneName().equals("CDMA")) {
            setSummaryText("meid_number", this.mPhone.getMeid());
            setSummaryText("min_number", this.mPhone.getCdmaMin());
            if (getResources().getBoolean(R.bool.config_msid_enable)) {
                findPreference("min_number").setTitle(R.string.status_msid_number);
            }
            setSummaryText("prl_version", this.mPhone.getCdmaPrlVersion());
            removePreferenceFromScreen("imei_sv");
        } else {
            setSummaryText("imei", this.mPhone.getDeviceId());
            setSummaryText("imei_sv", this.mTelephonyManager.getDeviceSoftwareVersion());
            removePreferenceFromScreen("prl_version");
            removePreferenceFromScreen("meid_number");
            removePreferenceFromScreen("min_number");
            removePreferenceFromScreen("icc_id");
        }
    }
}
