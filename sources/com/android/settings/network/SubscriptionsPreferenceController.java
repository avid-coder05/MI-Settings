package com.android.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.UserManager;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.ArraySet;
import android.util.Log;
import androidx.collection.ArrayMap;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.network.MobileDataEnabledListener;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.network.telephony.DataConnectivityListener;
import com.android.settings.network.telephony.MobileNetworkActivity;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.network.telephony.SignalStrengthListener;
import com.android.settings.network.telephony.TelephonyDisplayInfoListener;
import com.android.settings.widget.GearPreference;
import com.android.settings.widget.MutableGearPreference;
import com.android.settings.wifi.WifiPickerTrackerHelper;
import com.android.settingslib.SignalIcon$MobileIconGroup;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.mobile.MobileMappings;
import com.android.settingslib.mobile.TelephonyIcons;
import com.android.settingslib.net.SignalStrengthUtil;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/* loaded from: classes.dex */
public class SubscriptionsPreferenceController extends AbstractPreferenceController implements LifecycleObserver, SubscriptionsChangeListener.SubscriptionsChangeListenerClient, MobileDataEnabledListener.Client, DataConnectivityListener.Client, SignalStrengthListener.Callback, TelephonyDisplayInfoListener.Callback {
    private MobileMappings.Config mConfig;
    final BroadcastReceiver mConnectionChangeReceiver;
    private DataConnectivityListener mConnectivityListener;
    private MobileDataEnabledListener mDataEnabledListener;
    private PreferenceGroup mPreferenceGroup;
    private String mPreferenceGroupKey;
    private SignalStrengthListener mSignalStrengthListener;
    private int mStartOrder;
    private MutableGearPreference mSubsGearPref;
    private SubsPrefCtrlInjector mSubsPrefCtrlInjector;
    private SubscriptionManager mSubscriptionManager;
    private Map<Integer, Preference> mSubscriptionPreferences;
    private SubscriptionsChangeListener mSubscriptionsListener;
    private TelephonyDisplayInfo mTelephonyDisplayInfo;
    private TelephonyDisplayInfoListener mTelephonyDisplayInfoListener;
    private TelephonyManager mTelephonyManager;
    private UpdateListener mUpdateListener;
    private WifiPickerTrackerHelper mWifiPickerTrackerHelper;

    /* loaded from: classes.dex */
    public static class SubsPrefCtrlInjector {
        public boolean canSubscriptionBeDisplayed(Context context, int i) {
            return SubscriptionUtil.getAvailableSubscription(context, ProxySubscriptionManager.getInstance(context), i) != null;
        }

        public MobileMappings.Config getConfig(Context context) {
            return MobileMappings.Config.readConfig(context);
        }

        public int getDefaultDataSubscriptionId() {
            return SubscriptionManager.getDefaultDataSubscriptionId();
        }

        public int getDefaultSmsSubscriptionId() {
            return SubscriptionManager.getDefaultSmsSubscriptionId();
        }

        public int getDefaultVoiceSubscriptionId() {
            return SubscriptionManager.getDefaultVoiceSubscriptionId();
        }

        public Drawable getIcon(Context context, int i, int i2, boolean z) {
            return MobileNetworkUtils.getSignalStrengthIcon(context, i, i2, 0, z);
        }

        public String getNetworkType(Context context, MobileMappings.Config config, TelephonyDisplayInfo telephonyDisplayInfo, int i) {
            SignalIcon$MobileIconGroup signalIcon$MobileIconGroup = MobileMappings.mapIconSets(config).get(MobileMappings.getIconKey(telephonyDisplayInfo));
            int i2 = signalIcon$MobileIconGroup != null ? signalIcon$MobileIconGroup.dataContentDescription : 0;
            return i2 != 0 ? SubscriptionManager.getResourcesForSubId(context, i).getString(i2) : "";
        }

        public String getNetworkType(Context context, MobileMappings.Config config, TelephonyDisplayInfo telephonyDisplayInfo, int i, boolean z) {
            if (z) {
                int i2 = TelephonyIcons.CARRIER_MERGED_WIFI.dataContentDescription;
                return i2 != 0 ? SubscriptionManager.getResourcesForSubId(context, i).getString(i2) : "";
            }
            return getNetworkType(context, config, telephonyDisplayInfo, i);
        }

        public boolean isActiveCellularNetwork(Context context) {
            return MobileNetworkUtils.activeNetworkIsCellular(context);
        }

        public boolean isProviderModelEnabled(Context context) {
            return Utils.isProviderModelEnabled(context);
        }
    }

    /* loaded from: classes.dex */
    public interface UpdateListener {
        void onChildrenUpdated();
    }

    public SubscriptionsPreferenceController(Context context, Lifecycle lifecycle, UpdateListener updateListener, String str, int i) {
        super(context);
        this.mConnectionChangeReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.SubscriptionsPreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (action.equals("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED")) {
                    SubscriptionsPreferenceController subscriptionsPreferenceController = SubscriptionsPreferenceController.this;
                    subscriptionsPreferenceController.mConfig = subscriptionsPreferenceController.mSubsPrefCtrlInjector.getConfig(((AbstractPreferenceController) SubscriptionsPreferenceController.this).mContext);
                    SubscriptionsPreferenceController.this.update();
                } else if (action.equals("android.net.wifi.supplicant.CONNECTION_CHANGE")) {
                    SubscriptionsPreferenceController.this.update();
                }
            }
        };
        this.mConfig = null;
        this.mTelephonyDisplayInfo = new TelephonyDisplayInfo(0, 0);
        this.mUpdateListener = updateListener;
        this.mPreferenceGroupKey = str;
        this.mStartOrder = i;
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mSubscriptionPreferences = new ArrayMap();
        this.mSubscriptionsListener = new SubscriptionsChangeListener(context, this);
        this.mDataEnabledListener = new MobileDataEnabledListener(context, this);
        this.mConnectivityListener = new DataConnectivityListener(context, this);
        this.mSignalStrengthListener = new SignalStrengthListener(context, this);
        this.mTelephonyDisplayInfoListener = new TelephonyDisplayInfoListener(context, this);
        lifecycle.addObserver(this);
        SubsPrefCtrlInjector createSubsPrefCtrlInjector = createSubsPrefCtrlInjector();
        this.mSubsPrefCtrlInjector = createSubsPrefCtrlInjector;
        this.mConfig = createSubsPrefCtrlInjector.getConfig(this.mContext);
    }

    private Drawable getIcon(int i) {
        WifiPickerTrackerHelper wifiPickerTrackerHelper;
        TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(i);
        SignalStrength signalStrength = createForSubscriptionId.getSignalStrength();
        boolean z = false;
        int level = signalStrength == null ? 0 : signalStrength.getLevel();
        int i2 = 5;
        if (shouldInflateSignalStrength(i)) {
            level++;
            i2 = 6;
        }
        Drawable icon = this.mSubsPrefCtrlInjector.getIcon(this.mContext, level, i2, false);
        if (this.mSubsPrefCtrlInjector.isActiveCellularNetwork(this.mContext) || ((wifiPickerTrackerHelper = this.mWifiPickerTrackerHelper) != null && wifiPickerTrackerHelper.isCarrierNetworkActive())) {
            icon.setTint(com.android.settingslib.Utils.getColorAccentDefaultColor(this.mContext));
            return icon;
        }
        ServiceState serviceState = createForSubscriptionId.getServiceState();
        NetworkRegistrationInfo networkRegistrationInfo = serviceState == null ? null : serviceState.getNetworkRegistrationInfo(2, 1);
        boolean isRegistered = networkRegistrationInfo == null ? false : networkRegistrationInfo.isRegistered();
        if (serviceState != null && serviceState.getState() == 0) {
            z = true;
        }
        return (isRegistered || z) ? icon : this.mContext.getDrawable(R.drawable.ic_signal_strength_zero_bar_no_internet);
    }

    private CharSequence getMobilePreferenceSummary(int i) {
        TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(i);
        if (createForSubscriptionId.isDataEnabled()) {
            ServiceState serviceState = createForSubscriptionId.getServiceState();
            NetworkRegistrationInfo networkRegistrationInfo = serviceState == null ? null : serviceState.getNetworkRegistrationInfo(2, 1);
            boolean isRegistered = networkRegistrationInfo == null ? false : networkRegistrationInfo.isRegistered();
            WifiPickerTrackerHelper wifiPickerTrackerHelper = this.mWifiPickerTrackerHelper;
            boolean z = wifiPickerTrackerHelper != null && wifiPickerTrackerHelper.isCarrierNetworkActive();
            String networkType = this.mSubsPrefCtrlInjector.getNetworkType(this.mContext, this.mConfig, this.mTelephonyDisplayInfo, i, z);
            if (this.mSubsPrefCtrlInjector.isActiveCellularNetwork(this.mContext) || z) {
                Log.i("SubscriptionsPrefCntrlr", "Active cellular network or active carrier network.");
                Context context = this.mContext;
                networkType = context.getString(R.string.preference_summary_default_combination, context.getString(R.string.mobile_data_connection_active), networkType);
            } else if (!isRegistered) {
                networkType = this.mContext.getString(R.string.mobile_data_no_connection);
            }
            return Html.fromHtml(networkType, 0);
        }
        return this.mContext.getString(R.string.mobile_data_off_summary);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$isAvailable$3(SubscriptionInfo subscriptionInfo) {
        return this.mSubsPrefCtrlInjector.canSubscriptionBeDisplayed(this.mContext, subscriptionInfo.getSubscriptionId());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateForBase$2(int i, Preference preference) {
        startMobileNetworkActivity(this.mContext, i);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateForProvider$0(Preference preference) {
        connectCarrierNetwork();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateForProvider$1(SubscriptionInfo subscriptionInfo, GearPreference gearPreference) {
        startMobileNetworkActivity(this.mContext, subscriptionInfo.getSubscriptionId());
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        intentFilter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        this.mContext.registerReceiver(this.mConnectionChangeReceiver, intentFilter);
    }

    private void resetProviderPreferenceSummary() {
        MutableGearPreference mutableGearPreference = this.mSubsGearPref;
        if (mutableGearPreference == null) {
            return;
        }
        mutableGearPreference.setSummary("");
    }

    private static void startMobileNetworkActivity(Context context, int i) {
        Intent intent = new Intent(context, MobileNetworkActivity.class);
        intent.putExtra("android.provider.extra.SUB_ID", i);
        context.startActivity(intent);
    }

    private void unRegisterReceiver() {
        BroadcastReceiver broadcastReceiver = this.mConnectionChangeReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update() {
        if (this.mPreferenceGroup == null) {
            return;
        }
        if (isAvailable()) {
            if (this.mSubsPrefCtrlInjector.isProviderModelEnabled(this.mContext)) {
                updateForProvider();
                return;
            } else {
                updateForBase();
                return;
            }
        }
        MutableGearPreference mutableGearPreference = this.mSubsGearPref;
        if (mutableGearPreference != null) {
            this.mPreferenceGroup.removePreference(mutableGearPreference);
        }
        Iterator<Preference> it = this.mSubscriptionPreferences.values().iterator();
        while (it.hasNext()) {
            this.mPreferenceGroup.removePreference(it.next());
        }
        this.mSubscriptionPreferences.clear();
        this.mSignalStrengthListener.updateSubscriptionIds(Collections.emptySet());
        this.mTelephonyDisplayInfoListener.updateSubscriptionIds(Collections.emptySet());
        this.mUpdateListener.onChildrenUpdated();
    }

    private void updateForBase() {
        Map<Integer, Preference> map = this.mSubscriptionPreferences;
        this.mSubscriptionPreferences = new ArrayMap();
        int i = this.mStartOrder;
        ArraySet arraySet = new ArraySet();
        int defaultDataSubscriptionId = this.mSubsPrefCtrlInjector.getDefaultDataSubscriptionId();
        for (SubscriptionInfo subscriptionInfo : SubscriptionUtil.getActiveSubscriptions(this.mSubscriptionManager)) {
            final int subscriptionId = subscriptionInfo.getSubscriptionId();
            if (this.mSubsPrefCtrlInjector.canSubscriptionBeDisplayed(this.mContext, subscriptionId)) {
                arraySet.add(Integer.valueOf(subscriptionId));
                Preference remove = map.remove(Integer.valueOf(subscriptionId));
                if (remove == null) {
                    remove = new Preference(this.mPreferenceGroup.getContext());
                    this.mPreferenceGroup.addPreference(remove);
                }
                remove.setTitle(SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, this.mContext));
                boolean z = subscriptionId == defaultDataSubscriptionId;
                remove.setSummary(getSummary(subscriptionId, z));
                setIcon(remove, subscriptionId, z);
                remove.setOrder(i);
                remove.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.network.SubscriptionsPreferenceController$$ExternalSyntheticLambda1
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        boolean lambda$updateForBase$2;
                        lambda$updateForBase$2 = SubscriptionsPreferenceController.this.lambda$updateForBase$2(subscriptionId, preference);
                        return lambda$updateForBase$2;
                    }
                });
                this.mSubscriptionPreferences.put(Integer.valueOf(subscriptionId), remove);
                i++;
            }
        }
        this.mSignalStrengthListener.updateSubscriptionIds(arraySet);
        Iterator<Preference> it = map.values().iterator();
        while (it.hasNext()) {
            this.mPreferenceGroup.removePreference(it.next());
        }
        this.mUpdateListener.onChildrenUpdated();
    }

    private void updateForProvider() {
        final SubscriptionInfo defaultDataSubscriptionInfo = this.mSubscriptionManager.getDefaultDataSubscriptionInfo();
        if (defaultDataSubscriptionInfo == null) {
            this.mPreferenceGroup.removeAll();
            return;
        }
        if (this.mSubsGearPref == null) {
            this.mPreferenceGroup.removeAll();
            MutableGearPreference mutableGearPreference = new MutableGearPreference(this.mContext, null);
            this.mSubsGearPref = mutableGearPreference;
            mutableGearPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.network.SubscriptionsPreferenceController$$ExternalSyntheticLambda0
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    boolean lambda$updateForProvider$0;
                    lambda$updateForProvider$0 = SubscriptionsPreferenceController.this.lambda$updateForProvider$0(preference);
                    return lambda$updateForProvider$0;
                }
            });
            this.mSubsGearPref.setOnGearClickListener(new GearPreference.OnGearClickListener() { // from class: com.android.settings.network.SubscriptionsPreferenceController$$ExternalSyntheticLambda2
                @Override // com.android.settings.widget.GearPreference.OnGearClickListener
                public final void onGearClick(GearPreference gearPreference) {
                    SubscriptionsPreferenceController.this.lambda$updateForProvider$1(defaultDataSubscriptionInfo, gearPreference);
                }
            });
        }
        if (!((UserManager) this.mContext.getSystemService(UserManager.class)).isAdminUser()) {
            this.mSubsGearPref.setGearEnabled(false);
        }
        this.mSubsGearPref.setTitle(SubscriptionUtil.getUniqueSubscriptionDisplayName(defaultDataSubscriptionInfo, this.mContext));
        this.mSubsGearPref.setOrder(this.mStartOrder);
        this.mSubsGearPref.setSummary(getMobilePreferenceSummary(defaultDataSubscriptionInfo.getSubscriptionId()));
        this.mSubsGearPref.setIcon(getIcon(defaultDataSubscriptionInfo.getSubscriptionId()));
        this.mPreferenceGroup.addPreference(this.mSubsGearPref);
        ArraySet arraySet = new ArraySet();
        arraySet.add(Integer.valueOf(defaultDataSubscriptionInfo.getSubscriptionId()));
        this.mSignalStrengthListener.updateSubscriptionIds(arraySet);
        this.mTelephonyDisplayInfoListener.updateSubscriptionIds(arraySet);
        this.mUpdateListener.onChildrenUpdated();
    }

    boolean canSubscriptionBeDisplayed(Context context, int i) {
        return SubscriptionUtil.getAvailableSubscription(context, ProxySubscriptionManager.getInstance(context), i) != null;
    }

    public void connectCarrierNetwork() {
        WifiPickerTrackerHelper wifiPickerTrackerHelper;
        if (MobileNetworkUtils.isMobileDataEnabled(this.mContext) && (wifiPickerTrackerHelper = this.mWifiPickerTrackerHelper) != null) {
            wifiPickerTrackerHelper.connectCarrierNetwork(null);
        }
    }

    SubsPrefCtrlInjector createSubsPrefCtrlInjector() {
        return new SubsPrefCtrlInjector();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(this.mPreferenceGroupKey);
        update();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return null;
    }

    protected String getSummary(int i, boolean z) {
        int defaultVoiceSubscriptionId = this.mSubsPrefCtrlInjector.getDefaultVoiceSubscriptionId();
        int defaultSmsSubscriptionId = this.mSubsPrefCtrlInjector.getDefaultSmsSubscriptionId();
        String str = null;
        String string = (i == defaultVoiceSubscriptionId && i == defaultSmsSubscriptionId) ? this.mContext.getString(R.string.default_for_calls_and_sms) : i == defaultVoiceSubscriptionId ? this.mContext.getString(R.string.default_for_calls) : i == defaultSmsSubscriptionId ? this.mContext.getString(R.string.default_for_sms) : null;
        if (z) {
            boolean isDataEnabled = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(i).isDataEnabled();
            str = (isDataEnabled && this.mSubsPrefCtrlInjector.isActiveCellularNetwork(this.mContext)) ? this.mContext.getString(R.string.mobile_data_active) : !isDataEnabled ? this.mContext.getString(R.string.mobile_data_off) : this.mContext.getString(R.string.default_for_mobile_data);
        }
        return (string == null || str == null) ? string != null ? string : str != null ? str : this.mContext.getString(R.string.subscription_available) : String.join(System.lineSeparator(), string, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        List<SubscriptionInfo> activeSubscriptions;
        if (this.mSubscriptionsListener.isAirplaneModeOn() || (activeSubscriptions = SubscriptionUtil.getActiveSubscriptions(this.mSubscriptionManager)) == null) {
            return false;
        }
        return activeSubscriptions.stream().filter(new Predicate() { // from class: com.android.settings.network.SubscriptionsPreferenceController$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isAvailable$3;
                lambda$isAvailable$3 = SubscriptionsPreferenceController.this.lambda$isAvailable$3((SubscriptionInfo) obj);
                return lambda$isAvailable$3;
            }
        }).count() >= ((long) (this.mSubsPrefCtrlInjector.isProviderModelEnabled(this.mContext) ? 1 : 2));
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
        update();
    }

    @Override // com.android.settings.network.telephony.DataConnectivityListener.Client
    public void onDataConnectivityChange() {
        update();
    }

    @Override // com.android.settings.network.MobileDataEnabledListener.Client
    public void onMobileDataEnabledChange() {
        update();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mSubscriptionsListener.stop();
        this.mDataEnabledListener.stop();
        this.mConnectivityListener.stop();
        this.mSignalStrengthListener.pause();
        this.mTelephonyDisplayInfoListener.pause();
        unRegisterReceiver();
        resetProviderPreferenceSummary();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mSubscriptionsListener.start();
        this.mDataEnabledListener.start(this.mSubsPrefCtrlInjector.getDefaultDataSubscriptionId());
        this.mConnectivityListener.start();
        this.mSignalStrengthListener.resume();
        this.mTelephonyDisplayInfoListener.resume();
        registerReceiver();
        update();
    }

    @Override // com.android.settings.network.telephony.SignalStrengthListener.Callback
    public void onSignalStrengthChanged() {
        update();
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        int defaultDataSubscriptionId = this.mSubsPrefCtrlInjector.getDefaultDataSubscriptionId();
        if (defaultDataSubscriptionId != this.mDataEnabledListener.getSubId()) {
            this.mDataEnabledListener.stop();
            this.mDataEnabledListener.start(defaultDataSubscriptionId);
        }
        update();
    }

    @Override // com.android.settings.network.telephony.TelephonyDisplayInfoListener.Callback
    public void onTelephonyDisplayInfoChanged(TelephonyDisplayInfo telephonyDisplayInfo) {
        this.mTelephonyDisplayInfo = telephonyDisplayInfo;
        update();
    }

    void setIcon(Preference preference, int i, boolean z) {
        TelephonyManager createForSubscriptionId = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        SignalStrength signalStrength = createForSubscriptionId.getSignalStrength();
        int level = signalStrength == null ? 0 : signalStrength.getLevel();
        int i2 = 5;
        if (shouldInflateSignalStrength(i)) {
            level++;
            i2 = 6;
        }
        preference.setIcon(this.mSubsPrefCtrlInjector.getIcon(this.mContext, level, i2, (z && createForSubscriptionId.isDataEnabled()) ? false : true));
    }

    public void setWifiPickerTrackerHelper(WifiPickerTrackerHelper wifiPickerTrackerHelper) {
        this.mWifiPickerTrackerHelper = wifiPickerTrackerHelper;
    }

    boolean shouldInflateSignalStrength(int i) {
        return SignalStrengthUtil.shouldInflateSignalStrength(this.mContext, i);
    }
}
