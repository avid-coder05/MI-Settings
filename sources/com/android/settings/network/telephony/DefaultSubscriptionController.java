package com.android.settings.network.telephony;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public abstract class DefaultSubscriptionController extends TelephonyBasePreferenceController implements LifecycleObserver, Preference.OnPreferenceChangeListener, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private static final String EMERGENCY_ACCOUNT_HANDLE_ID = "E";
    private static final ComponentName PSTN_CONNECTION_SERVICE_COMPONENT = new ComponentName("com.android.phone", "com.android.services.telephony.TelephonyConnectionService");
    private static final String TAG = "DefaultSubController";
    protected SubscriptionsChangeListener mChangeListener;
    protected SubscriptionManager mManager;
    protected DropDownPreference mPreference;
    protected TelecomManager mTelecomManager;

    public DefaultSubscriptionController(Context context, String str) {
        super(context, str);
        this.mManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mChangeListener = new SubscriptionsChangeListener(context, this);
    }

    private void updateEntries() {
        if (this.mPreference == null) {
            return;
        }
        if (!isAvailable()) {
            setVisible(this.mPreference, false);
            return;
        }
        setVisible(this.mPreference, true);
        this.mPreference.setOnPreferenceChangeListener(this);
        List<SubscriptionInfo> activeSubscriptions = SubscriptionUtil.getActiveSubscriptions(this.mManager);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        if (Utils.isProviderModelEnabled(this.mContext) && activeSubscriptions.size() == 1) {
            this.mPreference.setEnabled(false);
            this.mPreference.setSummary(SubscriptionUtil.getUniqueSubscriptionDisplayName(activeSubscriptions.get(0), this.mContext));
            return;
        }
        int defaultSubscriptionId = getDefaultSubscriptionId();
        boolean z = false;
        for (SubscriptionInfo subscriptionInfo : activeSubscriptions) {
            if (!subscriptionInfo.isOpportunistic()) {
                arrayList.add(SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, this.mContext));
                int subscriptionId = subscriptionInfo.getSubscriptionId();
                arrayList2.add(Integer.toString(subscriptionId));
                if (subscriptionId == defaultSubscriptionId) {
                    z = true;
                }
            }
        }
        if (isAskEverytimeSupported()) {
            arrayList.add(this.mContext.getString(R.string.calls_and_sms_ask_every_time));
            arrayList2.add(Integer.toString(-1));
        }
        this.mPreference.setEntries((CharSequence[]) arrayList.toArray(new CharSequence[0]));
        this.mPreference.setEntryValues((CharSequence[]) arrayList2.toArray(new CharSequence[0]));
        if (z) {
            this.mPreference.setValue(Integer.toString(defaultSubscriptionId));
        } else {
            this.mPreference.setValue(Integer.toString(-1));
        }
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (DropDownPreference) preferenceScreen.findPreference(getPreferenceKey());
        updateEntries();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        return (SubscriptionUtil.getActiveSubscriptions(this.mManager).size() > 1 || Utils.isProviderModelEnabled(this.mContext)) ? 0 : 2;
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public PhoneAccountHandle getDefaultCallingAccountHandle() {
        PhoneAccountHandle userSelectedOutgoingPhoneAccount = getTelecomManager().getUserSelectedOutgoingPhoneAccount();
        if (userSelectedOutgoingPhoneAccount == null) {
            return null;
        }
        List callCapablePhoneAccounts = getTelecomManager().getCallCapablePhoneAccounts(false);
        if (userSelectedOutgoingPhoneAccount.equals(new PhoneAccountHandle(PSTN_CONNECTION_SERVICE_COMPONENT, EMERGENCY_ACCOUNT_HANDLE_ID))) {
            return null;
        }
        Iterator it = callCapablePhoneAccounts.iterator();
        while (it.hasNext()) {
            if (userSelectedOutgoingPhoneAccount.equals((PhoneAccountHandle) it.next())) {
                return userSelectedOutgoingPhoneAccount;
            }
        }
        return null;
    }

    protected abstract int getDefaultSubscriptionId();

    protected abstract SubscriptionInfo getDefaultSubscriptionInfo();

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public CharSequence getLabelFromCallingAccount(PhoneAccountHandle phoneAccountHandle) {
        PhoneAccount phoneAccount = getPhoneAccount(phoneAccountHandle);
        CharSequence label = phoneAccount != null ? phoneAccount.getLabel() : null;
        if (label != null) {
            label = this.mContext.getPackageManager().getUserBadgedLabel(label, phoneAccountHandle.getUserHandle());
        }
        return label != null ? label : "";
    }

    PhoneAccount getPhoneAccount(PhoneAccountHandle phoneAccountHandle) {
        return getTelecomManager().getPhoneAccount(phoneAccountHandle);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        PhoneAccountHandle defaultCallingAccountHandle = getDefaultCallingAccountHandle();
        if (defaultCallingAccountHandle == null || isCallingAccountBindToSubscription(defaultCallingAccountHandle)) {
            SubscriptionInfo defaultSubscriptionInfo = getDefaultSubscriptionInfo();
            return defaultSubscriptionInfo != null ? SubscriptionUtil.getUniqueSubscriptionDisplayName(defaultSubscriptionInfo, this.mContext) : isAskEverytimeSupported() ? this.mContext.getString(R.string.calls_and_sms_ask_every_time) : "";
        }
        return getLabelFromCallingAccount(defaultCallingAccountHandle);
    }

    TelecomManager getTelecomManager() {
        if (this.mTelecomManager == null) {
            this.mTelecomManager = (TelecomManager) this.mContext.getSystemService(TelecomManager.class);
        }
        return this.mTelecomManager;
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public void init(Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    protected boolean isAskEverytimeSupported() {
        return true;
    }

    public boolean isCallingAccountBindToSubscription(PhoneAccountHandle phoneAccountHandle) {
        PhoneAccount phoneAccount = getPhoneAccount(phoneAccountHandle);
        if (phoneAccount == null) {
            return false;
        }
        return phoneAccount.hasCapabilities(4);
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mChangeListener.stop();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        setDefaultSubscription(Integer.parseInt((String) obj));
        refreshSummary(this.mPreference);
        return true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mChangeListener.start();
        updateEntries();
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        if (this.mPreference != null) {
            updateEntries();
            refreshSummary(this.mPreference);
        }
    }

    protected abstract void setDefaultSubscription(int i);

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
