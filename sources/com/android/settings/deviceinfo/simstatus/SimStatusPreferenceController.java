package com.android.settings.deviceinfo.simstatus;

import android.content.Context;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.deviceinfo.AbstractSimStatusImeiInfoPreferenceController;
import com.android.settingslib.miuisettings.preference.PreferenceUtils;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SimStatusPreferenceController extends AbstractSimStatusImeiInfoPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnCreate, OnDestroy {
    private static final String TAG = "SimStatusPreferenceController";
    private final Fragment mFragment;
    private final SubscriptionManager.OnSubscriptionsChangedListener mOnSubscriptionChangeListener;
    private final List<Preference> mPreferenceList;
    private final SubscriptionManager mSubscriptionManager;
    private final TelephonyManager mTelephonyManager;

    public SimStatusPreferenceController(Context context, Fragment fragment) {
        super(context);
        this.mPreferenceList = new ArrayList();
        this.mOnSubscriptionChangeListener = new SubscriptionManager.OnSubscriptionsChangedListener() { // from class: com.android.settings.deviceinfo.simstatus.SimStatusPreferenceController.1
            @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
            public void onSubscriptionsChanged() {
                super.onSubscriptionsChanged();
                Log.i(SimStatusPreferenceController.TAG, "onSubscriptionsChanged");
                SimStatusPreferenceController.this.updatePhoneNumeberStatus();
            }
        };
        this.mTelephonyManager = (TelephonyManager) context.getSystemService("phone");
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService("telephony_subscription_service");
        this.mFragment = fragment;
    }

    public SimStatusPreferenceController(Context context, Fragment fragment, Lifecycle lifecycle) {
        this(context, fragment);
        lifecycle.addObserver(this);
    }

    private CharSequence getCarrierName(int i) {
        List<SubscriptionInfo> activeSubscriptionInfoList = this.mSubscriptionManager.getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList != null) {
            for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                if (subscriptionInfo.getSimSlotIndex() == i) {
                    return subscriptionInfo.getCarrierName();
                }
            }
        }
        return this.mContext.getText(R.string.device_info_not_available);
    }

    private String getPreferenceTitle(int i) {
        return SettingsFeatures.isShowSlotDevice() ? (this.mTelephonyManager.getPhoneCount() <= 1 || miui.telephony.TelephonyManager.isCustSingleSimDevice()) ? this.mContext.getString(R.string.sim_status_title) : this.mContext.getString(R.string.sim_status_title_sim_softbank, Integer.valueOf(i + 1)) : (this.mTelephonyManager.getPhoneCount() <= 1 || miui.telephony.TelephonyManager.isCustSingleSimDevice()) ? this.mContext.getString(R.string.sim_status_title) : this.mContext.getString(R.string.sim_status_title_sim_slot, Integer.valueOf(i + 1));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePhoneNumeberStatus() {
        for (int i = 0; i < this.mPreferenceList.size(); i++) {
            updateState(this.mPreferenceList.get(i));
        }
    }

    Preference createNewPreference(Context context) {
        return new com.android.settingslib.miuisettings.preference.Preference(context, true);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        if (isAvailable() && findPreference != null && PreferenceUtils.isVisible(findPreference)) {
            PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference("device_detail_category");
            this.mPreferenceList.add(findPreference);
            int order = findPreference.getOrder();
            for (int i = 1; i < this.mTelephonyManager.getPhoneCount() && !miui.telephony.TelephonyManager.isCustSingleSimDevice(); i++) {
                Preference createNewPreference = createNewPreference(preferenceScreen.getContext());
                createNewPreference.setOrder(order + i);
                createNewPreference.setKey("sim_status" + i);
                preferenceCategory.addPreference(createNewPreference);
                this.mPreferenceList.add(createNewPreference);
            }
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "sim_status";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        int indexOf = this.mPreferenceList.indexOf(preference);
        if (indexOf == -1) {
            return false;
        }
        SimStatusDialogFragment.show(this.mFragment, indexOf, getPreferenceTitle(indexOf));
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnCreate
    public void onCreate(Bundle bundle) {
        SubscriptionManager subscriptionManager = this.mSubscriptionManager;
        if (subscriptionManager != null) {
            subscriptionManager.addOnSubscriptionsChangedListener(this.mOnSubscriptionChangeListener);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        SubscriptionManager subscriptionManager = this.mSubscriptionManager;
        if (subscriptionManager != null) {
            subscriptionManager.removeOnSubscriptionsChangedListener(this.mOnSubscriptionChangeListener);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        for (int i = 0; i < this.mPreferenceList.size(); i++) {
            Preference preference2 = this.mPreferenceList.get(i);
            preference2.setTitle(getPreferenceTitle(i));
            preference2.setSummary(getCarrierName(i));
        }
    }
}
