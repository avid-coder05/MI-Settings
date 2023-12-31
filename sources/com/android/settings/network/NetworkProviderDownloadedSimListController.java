package com.android.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.ArrayMap;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.network.telephony.MobileNetworkActivity;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class NetworkProviderDownloadedSimListController extends AbstractPreferenceController implements LifecycleObserver, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private SubscriptionsChangeListener mChangeListener;
    final BroadcastReceiver mDataSubscriptionChangedReceiver;
    private PreferenceCategory mPreferenceCategory;
    private Map<Integer, Preference> mPreferences;
    private SubscriptionManager mSubscriptionManager;

    public NetworkProviderDownloadedSimListController(Context context, Lifecycle lifecycle) {
        super(context);
        this.mDataSubscriptionChangedReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.NetworkProviderDownloadedSimListController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent.getAction().equals("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED")) {
                    NetworkProviderDownloadedSimListController.this.update();
                }
            }
        };
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mChangeListener = new SubscriptionsChangeListener(context, this);
        this.mPreferences = new ArrayMap();
        lifecycle.addObserver(this);
    }

    private List<SubscriptionInfo> getAvailableDownloadedSubscriptions() {
        ArrayList arrayList = new ArrayList();
        for (SubscriptionInfo subscriptionInfo : SubscriptionUtil.getAvailableSubscriptions(this.mContext)) {
            if (subscriptionInfo.isEmbedded()) {
                arrayList.add(subscriptionInfo);
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$update$0(SubscriptionInfo subscriptionInfo, Preference preference) {
        Intent intent = new Intent(this.mContext, MobileNetworkActivity.class);
        intent.putExtra("android.provider.extra.SUB_ID", subscriptionInfo.getSubscriptionId());
        this.mContext.startActivity(intent);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update() {
        if (this.mPreferenceCategory == null) {
            return;
        }
        Map<Integer, Preference> map = this.mPreferences;
        this.mPreferences = new ArrayMap();
        for (final SubscriptionInfo subscriptionInfo : getAvailableDownloadedSubscriptions()) {
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            Preference remove = map.remove(Integer.valueOf(subscriptionId));
            if (remove == null) {
                remove = new Preference(this.mPreferenceCategory.getContext());
                this.mPreferenceCategory.addPreference(remove);
            }
            remove.setTitle(SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, this.mContext));
            remove.setSummary(getSummary(subscriptionId));
            remove.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.network.NetworkProviderDownloadedSimListController$$ExternalSyntheticLambda0
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    boolean lambda$update$0;
                    lambda$update$0 = NetworkProviderDownloadedSimListController.this.lambda$update$0(subscriptionInfo, preference);
                    return lambda$update$0;
                }
            });
            this.mPreferences.put(Integer.valueOf(subscriptionId), remove);
        }
        Iterator<Preference> it = map.values().iterator();
        while (it.hasNext()) {
            this.mPreferenceCategory.removePreference(it.next());
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceCategory = (PreferenceCategory) preferenceScreen.findPreference("provider_model_downloaded_sim_category");
        preferenceScreen.findPreference("add_more").setVisible(MobileNetworkUtils.showEuiccSettings(this.mContext));
        update();
    }

    protected int getDefaultDataSubscriptionId() {
        return SubscriptionManager.getDefaultDataSubscriptionId();
    }

    protected int getDefaultSmsSubscriptionId() {
        return SubscriptionManager.getDefaultSmsSubscriptionId();
    }

    protected int getDefaultVoiceSubscriptionId() {
        return SubscriptionManager.getDefaultVoiceSubscriptionId();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "provider_model_downloaded_sim_list";
    }

    public CharSequence getSummary(int i) {
        if (this.mSubscriptionManager.isActiveSubscriptionId(i)) {
            CharSequence defaultSimConfig = SubscriptionUtil.getDefaultSimConfig(this.mContext, i);
            String string = this.mContext.getResources().getString(R.string.sim_category_active_sim);
            if (defaultSimConfig == null) {
                return string;
            }
            StringBuilder sb = new StringBuilder();
            sb.append((CharSequence) string);
            sb.append(defaultSimConfig);
            return sb;
        }
        return this.mContext.getString(R.string.sim_category_inactive_sim);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !getAvailableDownloadedSubscriptions().isEmpty();
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mChangeListener.stop();
        BroadcastReceiver broadcastReceiver = this.mDataSubscriptionChangedReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mChangeListener.start();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        this.mContext.registerReceiver(this.mDataSubscriptionChangedReceiver, intentFilter);
        update();
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        update();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        refreshSummary(this.mPreferenceCategory);
        update();
    }
}
