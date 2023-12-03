package com.android.settings.wifi.calling;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.telephony.ims.ImsMmTelManager;
import android.telephony.ims.ProvisioningManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.widget.OnMainSwitchChangeListener;

/* loaded from: classes2.dex */
public class WifiCallingSettingsForSub extends SettingsPreferenceFragment implements OnMainSwitchChangeListener, Preference.OnPreferenceChangeListener {
    @VisibleForTesting
    static final int REQUEST_CHECK_WFC_DISCLAIMER = 2;
    @VisibleForTesting
    static final int REQUEST_CHECK_WFC_EMERGENCY_ADDRESS = 1;
    private ListWithEntrySummaryPreference mButtonWfcMode;
    private ListWithEntrySummaryPreference mButtonWfcRoamingMode;
    private TextView mEmptyView;
    private ImsMmTelManager mImsMmTelManager;
    private IntentFilter mIntentFilter;
    private ProvisioningManager mProvisioningManager;
    private SettingsMainSwitchBar mSwitchBar;
    private TelephonyManager mTelephonyManager;
    private Preference mUpdateAddress;
    private boolean mValidListener = false;
    private boolean mEditableWfcMode = true;
    private boolean mEditableWfcRoamingMode = true;
    private boolean mUseWfcHomeModeForRoaming = false;
    private int mSubId = -1;
    private final PhoneTelephonyCallback mTelephonyCallback = new PhoneTelephonyCallback();
    private final Preference.OnPreferenceClickListener mUpdateAddressListener = new Preference.OnPreferenceClickListener() { // from class: com.android.settings.wifi.calling.WifiCallingSettingsForSub$$ExternalSyntheticLambda0
        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public final boolean onPreferenceClick(Preference preference) {
            boolean lambda$new$0;
            lambda$new$0 = WifiCallingSettingsForSub.this.lambda$new$0(preference);
            return lambda$new$0;
        }
    };
    private final ProvisioningManager.Callback mProvisioningCallback = new ProvisioningManager.Callback() { // from class: com.android.settings.wifi.calling.WifiCallingSettingsForSub.1
        public void onProvisioningIntChanged(int i, int i2) {
            if (i == 28 || i == 10) {
                WifiCallingSettingsForSub.this.updateBody();
            }
        }
    };
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.calling.WifiCallingSettingsForSub.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.telephony.ims.action.WFC_IMS_REGISTRATION_ERROR")) {
                setResultCode(0);
                WifiCallingSettingsForSub.this.showAlert(intent);
            }
        }
    };

    /* loaded from: classes2.dex */
    private class PhoneTelephonyCallback extends TelephonyCallback implements TelephonyCallback.CallStateListener {
        private PhoneTelephonyCallback() {
        }

        @Override // android.telephony.TelephonyCallback.CallStateListener
        public void onCallStateChanged(int i) {
            boolean z;
            boolean z2;
            PersistableBundle configForSubId;
            SettingsActivity settingsActivity = (SettingsActivity) WifiCallingSettingsForSub.this.getActivity();
            WifiCallingSettingsForSub wifiCallingSettingsForSub = WifiCallingSettingsForSub.this;
            boolean isAllowUserControl = wifiCallingSettingsForSub.queryImsState(wifiCallingSettingsForSub.mSubId).isAllowUserControl();
            boolean z3 = WifiCallingSettingsForSub.this.mSwitchBar.isChecked() && isAllowUserControl;
            WifiCallingSettingsForSub wifiCallingSettingsForSub2 = WifiCallingSettingsForSub.this;
            boolean z4 = wifiCallingSettingsForSub2.getTelephonyManagerForSub(wifiCallingSettingsForSub2.mSubId).getCallState() == 0;
            WifiCallingSettingsForSub.this.mSwitchBar.setEnabled(z4 && isAllowUserControl);
            CarrierConfigManager carrierConfigManager = (CarrierConfigManager) settingsActivity.getSystemService("carrier_config");
            if (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(WifiCallingSettingsForSub.this.mSubId)) == null) {
                z = true;
                z2 = false;
            } else {
                z = configForSubId.getBoolean("editable_wfc_mode_bool");
                z2 = configForSubId.getBoolean("editable_wfc_roaming_mode_bool");
            }
            Preference findPreference = WifiCallingSettingsForSub.this.getPreferenceScreen().findPreference("wifi_calling_mode");
            if (findPreference != null) {
                findPreference.setEnabled(z3 && z && z4);
            }
            Preference findPreference2 = WifiCallingSettingsForSub.this.getPreferenceScreen().findPreference("wifi_calling_roaming_mode");
            if (findPreference2 != null) {
                findPreference2.setEnabled(z3 && z2 && z4);
            }
        }
    }

    private Intent getCarrierActivityIntent() {
        PersistableBundle configForSubId;
        ComponentName unflattenFromString;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) getActivity().getSystemService(CarrierConfigManager.class);
        if (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(this.mSubId)) == null) {
            return null;
        }
        String string = configForSubId.getString("wfc_emergency_address_carrier_app_string");
        if (TextUtils.isEmpty(string) || (unflattenFromString = ComponentName.unflattenFromString(string)) == null) {
            return null;
        }
        Intent intent = new Intent();
        intent.setComponent(unflattenFromString);
        intent.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", this.mSubId);
        return intent;
    }

    private CharSequence getWfcModeSummary(int i) {
        int i2;
        if (queryImsState(this.mSubId).isEnabledByUser()) {
            if (i == 0) {
                i2 = 17041728;
            } else if (i == 1) {
                i2 = 17041727;
            } else if (i != 2) {
                Log.e("WifiCallingForSub", "Unexpected WFC mode value: " + i);
            } else {
                i2 = 17041729;
            }
            return getResourcesForSubId().getString(i2);
        }
        i2 = 17041760;
        return getResourcesForSubId().getString(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$0(Preference preference) {
        Intent carrierActivityIntent = getCarrierActivityIntent();
        if (carrierActivityIntent != null) {
            carrierActivityIntent.putExtra("EXTRA_LAUNCH_CARRIER_APP", 1);
            startActivity(carrierActivityIntent);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBody() {
        boolean z;
        PersistableBundle configForSubId;
        if (!isWfcProvisionedOnDevice()) {
            finish();
            return;
        }
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) getSystemService("carrier_config");
        boolean z2 = false;
        if (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(this.mSubId)) == null) {
            z = true;
        } else {
            this.mEditableWfcMode = configForSubId.getBoolean("editable_wfc_mode_bool");
            this.mEditableWfcRoamingMode = configForSubId.getBoolean("editable_wfc_roaming_mode_bool");
            this.mUseWfcHomeModeForRoaming = configForSubId.getBoolean("use_wfc_home_network_mode_in_roaming_network_bool", false);
            z = configForSubId.getBoolean("carrier_wfc_supports_wifi_only_bool", true);
        }
        Resources resourcesForSubId = getResourcesForSubId();
        this.mButtonWfcMode.setTitle(resourcesForSubId.getString(R.string.wifi_calling_mode_title));
        this.mButtonWfcMode.setDialogTitle(resourcesForSubId.getString(R.string.wifi_calling_mode_dialog_title));
        this.mButtonWfcRoamingMode.setTitle(resourcesForSubId.getString(R.string.wifi_calling_roaming_mode_title));
        this.mButtonWfcRoamingMode.setDialogTitle(resourcesForSubId.getString(R.string.wifi_calling_roaming_mode_dialog_title));
        if (z) {
            this.mButtonWfcMode.setEntries(resourcesForSubId.getStringArray(R.array.wifi_calling_mode_choices));
            ListWithEntrySummaryPreference listWithEntrySummaryPreference = this.mButtonWfcMode;
            int i = R.array.wifi_calling_mode_values;
            listWithEntrySummaryPreference.setEntryValues(resourcesForSubId.getStringArray(i));
            ListWithEntrySummaryPreference listWithEntrySummaryPreference2 = this.mButtonWfcMode;
            int i2 = R.array.wifi_calling_mode_summaries;
            listWithEntrySummaryPreference2.setEntrySummaries(resourcesForSubId.getStringArray(i2));
            this.mButtonWfcRoamingMode.setEntries(resourcesForSubId.getStringArray(R.array.wifi_calling_mode_choices_v2));
            this.mButtonWfcRoamingMode.setEntryValues(resourcesForSubId.getStringArray(i));
            this.mButtonWfcRoamingMode.setEntrySummaries(resourcesForSubId.getStringArray(i2));
        } else {
            this.mButtonWfcMode.setEntries(resourcesForSubId.getStringArray(R.array.wifi_calling_mode_choices_without_wifi_only));
            ListWithEntrySummaryPreference listWithEntrySummaryPreference3 = this.mButtonWfcMode;
            int i3 = R.array.wifi_calling_mode_values_without_wifi_only;
            listWithEntrySummaryPreference3.setEntryValues(resourcesForSubId.getStringArray(i3));
            ListWithEntrySummaryPreference listWithEntrySummaryPreference4 = this.mButtonWfcMode;
            int i4 = R.array.wifi_calling_mode_summaries_without_wifi_only;
            listWithEntrySummaryPreference4.setEntrySummaries(resourcesForSubId.getStringArray(i4));
            this.mButtonWfcRoamingMode.setEntries(resourcesForSubId.getStringArray(R.array.wifi_calling_mode_choices_v2_without_wifi_only));
            this.mButtonWfcRoamingMode.setEntryValues(resourcesForSubId.getStringArray(i3));
            this.mButtonWfcRoamingMode.setEntrySummaries(resourcesForSubId.getStringArray(i4));
        }
        WifiCallingQueryImsState queryImsState = queryImsState(this.mSubId);
        if (queryImsState.isEnabledByUser() && queryImsState.isAllowUserControl()) {
            z2 = true;
        }
        this.mSwitchBar.setChecked(z2);
        int voWiFiModeSetting = this.mImsMmTelManager.getVoWiFiModeSetting();
        int voWiFiRoamingModeSetting = this.mImsMmTelManager.getVoWiFiRoamingModeSetting();
        this.mButtonWfcMode.setValue(Integer.toString(voWiFiModeSetting));
        this.mButtonWfcRoamingMode.setValue(Integer.toString(voWiFiRoamingModeSetting));
        updateButtonWfcMode(z2, voWiFiModeSetting, voWiFiRoamingModeSetting);
    }

    private void updateButtonWfcMode(boolean z, int i, int i2) {
        this.mButtonWfcMode.setSummary(getWfcModeSummary(i));
        this.mButtonWfcMode.setEnabled(z && this.mEditableWfcMode);
        this.mButtonWfcRoamingMode.setEnabled(z && this.mEditableWfcRoamingMode);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        boolean z2 = getCarrierActivityIntent() != null;
        if (!z) {
            preferenceScreen.removePreference(this.mButtonWfcMode);
            preferenceScreen.removePreference(this.mButtonWfcRoamingMode);
            preferenceScreen.removePreference(this.mUpdateAddress);
            return;
        }
        if (this.mEditableWfcMode) {
            preferenceScreen.addPreference(this.mButtonWfcMode);
        } else {
            preferenceScreen.removePreference(this.mButtonWfcMode);
        }
        if (!this.mEditableWfcRoamingMode || this.mUseWfcHomeModeForRoaming) {
            preferenceScreen.removePreference(this.mButtonWfcRoamingMode);
        } else {
            preferenceScreen.addPreference(this.mButtonWfcRoamingMode);
        }
        if (z2) {
            preferenceScreen.addPreference(this.mUpdateAddress);
        } else {
            preferenceScreen.removePreference(this.mUpdateAddress);
        }
    }

    private void updateWfcMode(boolean z) {
        Log.i("WifiCallingForSub", "updateWfcMode(" + z + ")");
        this.mImsMmTelManager.setVoWiFiSettingEnabled(z);
        int voWiFiModeSetting = this.mImsMmTelManager.getVoWiFiModeSetting();
        updateButtonWfcMode(z, voWiFiModeSetting, this.mImsMmTelManager.getVoWiFiRoamingModeSetting());
        if (z) {
            this.mMetricsFeatureProvider.action(getActivity(), getMetricsCategory(), voWiFiModeSetting);
        } else {
            this.mMetricsFeatureProvider.action(getActivity(), getMetricsCategory(), -1);
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    @VisibleForTesting
    ImsMmTelManager getImsMmTelManager() {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return ImsMmTelManager.createForSubscriptionId(this.mSubId);
        }
        return null;
    }

    @VisibleForTesting
    ProvisioningManager getImsProvisioningManager() {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return ProvisioningManager.createForSubscriptionId(this.mSubId);
        }
        return null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1230;
    }

    @VisibleForTesting
    Resources getResourcesForSubId() {
        return SubscriptionManager.getResourcesForSubId(getContext(), this.mSubId);
    }

    @VisibleForTesting
    TelephonyManager getTelephonyManagerForSub(int i) {
        if (this.mTelephonyManager == null) {
            this.mTelephonyManager = (TelephonyManager) getContext().getSystemService(TelephonyManager.class);
        }
        return this.mTelephonyManager.createForSubscriptionId(i);
    }

    @VisibleForTesting
    boolean isWfcProvisionedOnDevice() {
        return queryImsState(this.mSubId).isWifiCallingProvisioned();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        TextView textView = (TextView) getView().findViewById(16908292);
        this.mEmptyView = textView;
        setEmptyView(textView);
        Resources resourcesForSubId = getResourcesForSubId();
        this.mEmptyView.setText(resourcesForSubId.getString(R.string.wifi_calling_off_explanation, resourcesForSubId.getString(R.string.wifi_calling_off_explanation_2)));
        SettingsMainSwitchBar settingsMainSwitchBar = (SettingsMainSwitchBar) getView().findViewById(R.id.switch_bar);
        this.mSwitchBar = settingsMainSwitchBar;
        settingsMainSwitchBar.show();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        Log.d("WifiCallingForSub", "WFC activity request = " + i + " result = " + i2);
        if (i == 1) {
            if (i2 == -1) {
                updateWfcMode(true);
            }
        } else if (i != 2) {
            Log.e("WifiCallingForSub", "Unexpected request: " + i);
        } else if (i2 == -1) {
            Intent carrierActivityIntent = getCarrierActivityIntent();
            if (carrierActivityIntent == null) {
                updateWfcMode(true);
                return;
            }
            carrierActivityIntent.putExtra("EXTRA_LAUNCH_CARRIER_APP", 0);
            startActivityForResult(carrierActivityIntent, 1);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.wifi_calling_settings);
        if (getArguments() != null && getArguments().containsKey("subId")) {
            this.mSubId = getArguments().getInt("subId");
        } else if (bundle != null) {
            this.mSubId = bundle.getInt("subId", -1);
        }
        this.mProvisioningManager = getImsProvisioningManager();
        this.mImsMmTelManager = getImsMmTelManager();
        ListWithEntrySummaryPreference listWithEntrySummaryPreference = (ListWithEntrySummaryPreference) findPreference("wifi_calling_mode");
        this.mButtonWfcMode = listWithEntrySummaryPreference;
        listWithEntrySummaryPreference.setOnPreferenceChangeListener(this);
        ListWithEntrySummaryPreference listWithEntrySummaryPreference2 = (ListWithEntrySummaryPreference) findPreference("wifi_calling_roaming_mode");
        this.mButtonWfcRoamingMode = listWithEntrySummaryPreference2;
        listWithEntrySummaryPreference2.setOnPreferenceChangeListener(this);
        Preference findPreference = findPreference("emergency_address_key");
        this.mUpdateAddress = findPreference;
        findPreference.setOnPreferenceClickListener(this.mUpdateAddressListener);
        IntentFilter intentFilter = new IntentFilter();
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("android.telephony.ims.action.WFC_IMS_REGISTRATION_ERROR");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.wifi_calling_settings_preferences, viewGroup, false);
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(R.id.prefs_container);
        Utils.prepareCustomPreferencesList(viewGroup, inflate, viewGroup2, false);
        viewGroup2.addView(super.onCreateView(layoutInflater, viewGroup2, bundle));
        return inflate;
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mSwitchBar.hide();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        FragmentActivity activity = getActivity();
        if (this.mValidListener) {
            this.mValidListener = false;
            getTelephonyManagerForSub(this.mSubId).unregisterTelephonyCallback(this.mTelephonyCallback);
            this.mSwitchBar.removeOnSwitchChangeListener(this);
        }
        activity.unregisterReceiver(this.mIntentReceiver);
        unregisterProvisioningChangedCallback();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference != this.mButtonWfcMode) {
            ListWithEntrySummaryPreference listWithEntrySummaryPreference = this.mButtonWfcRoamingMode;
            if (preference == listWithEntrySummaryPreference) {
                String str = (String) obj;
                listWithEntrySummaryPreference.setValue(str);
                int intValue = Integer.valueOf(str).intValue();
                if (intValue != this.mImsMmTelManager.getVoWiFiRoamingModeSetting()) {
                    this.mImsMmTelManager.setVoWiFiRoamingModeSetting(intValue);
                    this.mMetricsFeatureProvider.action(getActivity(), getMetricsCategory(), intValue);
                    return true;
                }
                return true;
            }
            return true;
        }
        Log.d("WifiCallingForSub", "onPreferenceChange mButtonWfcMode " + obj);
        String str2 = (String) obj;
        this.mButtonWfcMode.setValue(str2);
        int intValue2 = Integer.valueOf(str2).intValue();
        if (intValue2 != this.mImsMmTelManager.getVoWiFiModeSetting()) {
            this.mImsMmTelManager.setVoWiFiModeSetting(intValue2);
            this.mButtonWfcMode.setSummary(getWfcModeSummary(intValue2));
            this.mMetricsFeatureProvider.action(getActivity(), getMetricsCategory(), intValue2);
            if (this.mUseWfcHomeModeForRoaming) {
                this.mImsMmTelManager.setVoWiFiRoamingModeSetting(intValue2);
                return true;
            }
            return true;
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateBody();
        FragmentActivity activity = getActivity();
        if (queryImsState(this.mSubId).isWifiCallingSupported()) {
            getTelephonyManagerForSub(this.mSubId).registerTelephonyCallback(activity.getMainExecutor(), this.mTelephonyCallback);
            this.mSwitchBar.addOnSwitchChangeListener(this);
            this.mValidListener = true;
        }
        activity.registerReceiver(this.mIntentReceiver, this.mIntentFilter);
        Intent intent = getActivity().getIntent();
        if (intent.getBooleanExtra("alertShow", false)) {
            showAlert(intent);
        }
        registerProvisioningChangedCallback();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("subId", this.mSubId);
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r3, boolean z) {
        Log.d("WifiCallingForSub", "onSwitchChanged(" + z + ")");
        if (!z) {
            updateWfcMode(false);
            return;
        }
        FragmentActivity activity = getActivity();
        Bundle bundle = new Bundle();
        bundle.putInt("EXTRA_SUB_ID", this.mSubId);
        new SubSettingLauncher(activity).setDestination(WifiCallingDisclaimerFragment.class.getName()).setArguments(bundle).setTitleRes(R.string.wifi_calling_settings_title).setSourceMetricsCategory(getMetricsCategory()).setResultListener(this, 2).launch();
    }

    @VisibleForTesting
    WifiCallingQueryImsState queryImsState(int i) {
        return new WifiCallingQueryImsState(getContext(), i);
    }

    @VisibleForTesting
    void registerProvisioningChangedCallback() {
        ProvisioningManager provisioningManager = this.mProvisioningManager;
        if (provisioningManager == null) {
            return;
        }
        try {
            provisioningManager.registerProvisioningChangedCallback(getContext().getMainExecutor(), this.mProvisioningCallback);
        } catch (Exception unused) {
            Log.w("WifiCallingForSub", "onResume: Unable to register callback for provisioning changes.");
        }
    }

    @VisibleForTesting
    void showAlert(Intent intent) {
        FragmentActivity activity = getActivity();
        CharSequence charSequenceExtra = intent.getCharSequenceExtra("android.telephony.ims.extra.WFC_REGISTRATION_FAILURE_TITLE");
        CharSequence charSequenceExtra2 = intent.getCharSequenceExtra("android.telephony.ims.extra.WFC_REGISTRATION_FAILURE_MESSAGE");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(charSequenceExtra2).setTitle(charSequenceExtra).setIcon(17301543).setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        builder.create().show();
    }

    @VisibleForTesting
    void unregisterProvisioningChangedCallback() {
        ProvisioningManager provisioningManager = this.mProvisioningManager;
        if (provisioningManager == null) {
            return;
        }
        provisioningManager.unregisterProvisioningChangedCallback(this.mProvisioningCallback);
    }
}
