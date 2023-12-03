package com.android.settings.msim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SmsCbMessage;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.internal.telephony.Phone;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.Utils;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import miui.telephony.SubscriptionManager;
import miui.telephony.TelephonyManager;

/* loaded from: classes.dex */
public class SimFragment extends SettingsPreferenceFragment {
    private static final String[] PHONE_RELATED_ENTRIES = {"data_state", "service_state", "operator_name", "roaming_state", "network_type", "latest_area_info", "number", "signal_strength"};
    private Resources mRes;
    private boolean mShowLatestAreaInfo;
    private Preference mSignalStrength;
    private int mSlotId;
    private String sUnknown;
    private Phone mPhone = null;
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() { // from class: com.android.settings.msim.SimFragment.1
        @Override // android.telephony.PhoneStateListener
        public void onDataConnectionStateChanged(int i) {
            SimFragment.this.updateDataState();
            SimFragment.this.updateNetworkType();
        }

        @Override // android.telephony.PhoneStateListener
        public void onServiceStateChanged(ServiceState serviceState) {
            SimFragment.this.updateServiceState(serviceState);
        }

        @Override // android.telephony.PhoneStateListener
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            SimFragment.this.updateSignalStrength(signalStrength);
        }
    };
    private BroadcastReceiver mAreaInfoReceiver = new BroadcastReceiver() { // from class: com.android.settings.msim.SimFragment.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Bundle extras;
            SmsCbMessage smsCbMessage;
            if (!"android.cellbroadcastreceiver.CB_AREA_INFO_RECEIVED".equals(intent.getAction()) || (extras = intent.getExtras()) == null || (smsCbMessage = (SmsCbMessage) extras.get("message")) == null || smsCbMessage.getServiceCategory() != 50) {
                return;
            }
            SimFragment.this.updateAreaInfo(smsCbMessage.getMessageBody());
        }
    };

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

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAreaInfo(String str) {
        if (str != null) {
            setSummaryText("latest_area_info", str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDataState() {
        Resources resources = this.mRes;
        int i = R.string.radioInfo_data_disconnected;
        String string = resources.getString(i);
        int dataStateForSlot = TelephonyManager.getDefault().getDataStateForSlot(this.mSlotId);
        if (dataStateForSlot == 0) {
            string = this.mRes.getString(i);
        } else if (dataStateForSlot == 1) {
            string = this.mRes.getString(R.string.radioInfo_data_connecting);
        } else if (dataStateForSlot == 2) {
            string = this.mRes.getString(R.string.radioInfo_data_connected);
        } else if (dataStateForSlot == 3) {
            string = this.mRes.getString(R.string.radioInfo_data_suspended);
        }
        setSummaryText("data_state", string);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNetworkType() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateServiceState(ServiceState serviceState) {
        int state = serviceState.getState();
        String string = this.mRes.getString(R.string.radioInfo_unknown);
        if (state == 0) {
            string = this.mRes.getString(R.string.radioInfo_service_in);
        } else if (state == 1 || state == 2) {
            string = this.mRes.getString(R.string.radioInfo_service_out);
        } else if (state == 3) {
            string = this.mRes.getString(R.string.radioInfo_service_off);
        }
        setSummaryText("service_state", string);
        if (serviceState.getRoaming()) {
            setSummaryText("roaming_state", this.mRes.getString(R.string.radioInfo_roaming_in));
        } else {
            setSummaryText("roaming_state", this.mRes.getString(R.string.radioInfo_roaming_not));
        }
        setSummaryText("operator_name", serviceState.getOperatorAlphaLong());
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSlotId = SubscriptionManager.getSlotId(getArguments(), 0);
        addPreferencesFromResource(R.xml.status_sim);
        Resources resources = getResources();
        this.mRes = resources;
        this.sUnknown = resources.getString(R.string.device_info_default);
        if (UserHandle.myUserId() == 0) {
            this.mPhone = MiuiUtils.getInstance().getPhone(this.mSlotId);
        }
        this.mSignalStrength = findPreference("signal_strength");
        if (this.mPhone == null || Utils.isWifiOnly(getActivity().getApplicationContext())) {
            for (String str : PHONE_RELATED_ENTRIES) {
                removePreferenceFromScreen(str);
            }
            return;
        }
        if (!this.mPhone.getPhoneName().equals("CDMA") && "br".equals(TelephonyManager.getDefault().getSimCountryIsoForSlot(this.mSlotId))) {
            this.mShowLatestAreaInfo = true;
        }
        String line1Number = this.mPhone.getLine1Number();
        setSummaryText("number", TextUtils.isEmpty(line1Number) ? null : PhoneNumberUtils.formatNumber(line1Number));
        if (this.mShowLatestAreaInfo) {
            return;
        }
        removePreferenceFromScreen("latest_area_info");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        if (this.mPhone != null && !Utils.isWifiOnly(getActivity().getApplicationContext())) {
            TelephonyManager.getDefault().listenForSlot(this.mSlotId, this.mPhoneStateListener, 0);
        }
        if (this.mShowLatestAreaInfo) {
            getActivity().unregisterReceiver(this.mAreaInfoReceiver);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mPhone == null || Utils.isWifiOnly(getActivity().getApplicationContext())) {
            return;
        }
        TelephonyManager.getDefault().listenForSlot(this.mSlotId, this.mPhoneStateListener, 321);
        if (this.mShowLatestAreaInfo) {
            getActivity().registerReceiver(this.mAreaInfoReceiver, new IntentFilter("android.cellbroadcastreceiver.CB_AREA_INFO_RECEIVED"), "android.permission.RECEIVE_EMERGENCY_BROADCAST", null);
            getActivity().sendBroadcastAsUser(new Intent("android.cellbroadcastreceiver.GET_LATEST_CB_AREA_INFO"), UserHandle.ALL, "android.permission.RECEIVE_EMERGENCY_BROADCAST");
        }
    }

    void updateSignalStrength(SignalStrength signalStrength) {
        if (this.mSignalStrength == null || !isAdded()) {
            return;
        }
        int state = this.mPhone.getServiceState().getState();
        getResources();
        if (1 == state || 3 == state) {
            ((ValuePreference) this.mSignalStrength).setValue("0");
        }
        signalStrength.getDbm();
        signalStrength.getAsuLevel();
    }
}
