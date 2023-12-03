package com.android.settings.settingspanel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import androidx.preference.Preference;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.CustomCheckBoxPreference;
import java.util.List;
import miui.telephony.SubscriptionInfo;
import miui.telephony.SubscriptionManager;
import miui.telephony.TelephonyManager;
import miuix.springback.view.SpringBackLayout;

/* loaded from: classes2.dex */
public class OtherSettingPanelFragment extends SettingsPreferenceFragment implements SubscriptionManager.OnSubscriptionsChangedListener {
    private String mAction;
    private CustomCheckBoxPreference mMobileNetwork;
    private ContentObserver mMobileNetworkContentObserver;
    private CustomCheckBoxPreference mNFC;
    private NfcAdapter mNfcAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.settingspanel.OtherSettingPanelFragment.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!"android.nfc.action.ADAPTER_STATE_CHANGED".equals(action)) {
                if ("android.intent.action.AIRPLANE_MODE".equals(action)) {
                    OtherSettingPanelFragment.this.setMobileNetworkAvailable();
                    return;
                }
                return;
            }
            boolean z = true;
            int intExtra = intent.getIntExtra("android.nfc.extra.ADAPTER_STATE", 1);
            if (OtherSettingPanelFragment.this.mNFC != null) {
                CustomCheckBoxPreference customCheckBoxPreference = OtherSettingPanelFragment.this.mNFC;
                if (intExtra != 3 && intExtra != 2) {
                    z = false;
                }
                customCheckBoxPreference.setChecked(z);
            }
        }
    };

    private CustomCheckBoxPreference getPreference(final String str) {
        CustomCheckBoxPreference customCheckBoxPreference = new CustomCheckBoxPreference(getPrefContext());
        customCheckBoxPreference.setIsDialogStyle(true);
        customCheckBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.settingspanel.OtherSettingPanelFragment.3
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                OtherSettingPanelFragment.this.preferenceChange(str, ((Boolean) obj).booleanValue());
                return true;
            }
        });
        if (TextUtils.equals(str, "android.settings.panel.action.INTERNET_CONNECTIVITY")) {
            this.mMobileNetwork = customCheckBoxPreference;
            customCheckBoxPreference.setTitle(R.string.network_settings_title);
            customCheckBoxPreference.setEnabled(isMobileNetworkAvailable());
            registerMobileDataObserver();
            SubscriptionManager.getDefault().addOnSubscriptionsChangedListener(this);
            setMobileDataState();
        } else {
            this.mNFC = customCheckBoxPreference;
            customCheckBoxPreference.setTitle(R.string.nfc_quick_toggle_title);
            this.mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
            customCheckBoxPreference.setEnabled(isNfcAvailable());
            registerReceiver();
            this.mNFC.setChecked(isNfcAvailable() ? this.mNfcAdapter.isEnabled() : false);
        }
        return customCheckBoxPreference;
    }

    private boolean isMobileNetworkAvailable() {
        List<SubscriptionInfo> subscriptionInfoList = SubscriptionManager.getDefault().getSubscriptionInfoList();
        return (subscriptionInfoList == null || subscriptionInfoList.size() <= 0 || (Settings.System.getInt(getContentResolver(), "airplane_mode_on", -1) == 1)) ? false : true;
    }

    private boolean isMultiSimEnabled() {
        return TelephonyManager.getDefault().getPhoneCount() > 1;
    }

    private boolean isNfcAvailable() {
        return this.mNfcAdapter != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void preferenceChange(String str, boolean z) {
        if (TextUtils.equals(str, "android.settings.panel.action.INTERNET_CONNECTIVITY")) {
            android.telephony.TelephonyManager.from(getActivity()).setDataEnabled(z);
            return;
        }
        NfcAdapter nfcAdapter = this.mNfcAdapter;
        if (nfcAdapter != null) {
            if (z) {
                nfcAdapter.enable();
            } else {
                nfcAdapter.disable();
            }
        }
    }

    private void registerMobileDataObserver() {
        this.mMobileNetworkContentObserver = new ContentObserver(new Handler(getActivity().getMainLooper())) { // from class: com.android.settings.settingspanel.OtherSettingPanelFragment.2
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                super.onChange(z);
                OtherSettingPanelFragment.this.setMobileDataState();
            }
        };
        if (!isMultiSimEnabled()) {
            getContentResolver().registerContentObserver(Settings.Global.getUriFor("mobile_data"), true, this.mMobileNetworkContentObserver);
            return;
        }
        for (int i = 0; i < TelephonyManager.getDefault().getPhoneCount(); i++) {
            getContentResolver().registerContentObserver(Settings.Global.getUriFor("mobile_data" + i), false, this.mMobileNetworkContentObserver);
        }
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED");
        intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        getActivity().registerReceiver(this.mReceiver, intentFilter);
    }

    private void removeSubscriptionsChangedListener() {
        if (SubscriptionManager.getDefault() != null) {
            SubscriptionManager.getDefault().removeOnSubscriptionsChangedListener(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMobileDataState() {
        CustomCheckBoxPreference customCheckBoxPreference = this.mMobileNetwork;
        if (customCheckBoxPreference != null) {
            customCheckBoxPreference.setChecked(MiuiUtils.getMobileDataEnabled(getPrefContext()));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMobileNetworkAvailable() {
        CustomCheckBoxPreference customCheckBoxPreference = this.mMobileNetwork;
        if (customCheckBoxPreference != null) {
            customCheckBoxPreference.setEnabled(isMobileNetworkAvailable());
        }
    }

    private void unregisterObserver() {
        if (this.mMobileNetworkContentObserver != null) {
            getContentResolver().unregisterContentObserver(this.mMobileNetworkContentObserver);
            this.mMobileNetworkContentObserver = null;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setThemeRes(R.style.Theme_Provision_Notitle_WifiSettings);
        addPreferencesFromResource(R.xml.miui_settings_panel);
        getPreferenceScreen().addPreference(getPreference(this.mAction));
        registerReceiver();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(this.mReceiver);
        }
        removeSubscriptionsChangedListener();
        unregisterObserver();
    }

    @Override // miui.telephony.SubscriptionManager.OnSubscriptionsChangedListener
    public void onSubscriptionsChanged() {
        setMobileNetworkAvailable();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        RecyclerView listView = getListView();
        if (TextUtils.equals(this.mAction, "android.settings.panel.action.INTERNET_CONNECTIVITY")) {
            listView.setPadding(listView.getPaddingLeft(), 0, listView.getPaddingRight(), 0);
        }
        View view2 = (View) listView.getParent();
        if (view2 instanceof SpringBackLayout) {
            view2.setEnabled(false);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void setArguments(Bundle bundle) {
        super.setArguments(bundle);
        if (bundle != null) {
            this.mAction = bundle.getString("action");
        }
    }
}
