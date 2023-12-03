package com.android.settings.network;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.network.InternetUpdater;
import com.android.settings.widget.SummaryUpdater;
import com.android.settings.wifi.WifiSummaryUpdater;
import com.android.settingslib.Utils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.utils.ThreadUtils;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class InternetPreferenceController extends AbstractPreferenceController implements LifecycleObserver, SummaryUpdater.OnSummaryChangeListener, InternetUpdater.InternetChangeListener {
    static Map<Integer, Integer> sIconMap;
    private static Map<Integer, Integer> sSummaryMap;
    private int mInternetType;
    private InternetUpdater mInternetUpdater;
    private Preference mPreference;
    private final WifiSummaryUpdater mSummaryHelper;

    static {
        HashMap hashMap = new HashMap();
        sIconMap = hashMap;
        hashMap.put(0, Integer.valueOf(R.drawable.ic_no_internet_unavailable));
        sIconMap.put(1, Integer.valueOf(R.drawable.ic_no_internet_available));
        sIconMap.put(2, Integer.valueOf(R.drawable.ic_wifi_signal_4));
        sIconMap.put(3, Integer.valueOf(R.drawable.ic_network_cell));
        sIconMap.put(4, Integer.valueOf(R.drawable.ic_settings_ethernet));
        HashMap hashMap2 = new HashMap();
        sSummaryMap = hashMap2;
        hashMap2.put(0, Integer.valueOf(R.string.condition_airplane_title));
        sSummaryMap.put(1, Integer.valueOf(R.string.networks_available));
        sSummaryMap.put(2, 0);
        sSummaryMap.put(3, 0);
        sSummaryMap.put(4, Integer.valueOf(R.string.to_switch_networks_disconnect_ethernet));
    }

    public InternetPreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        this.mSummaryHelper = new WifiSummaryUpdater(this.mContext, this);
        InternetUpdater internetUpdater = new InternetUpdater(context, lifecycle, this);
        this.mInternetUpdater = internetUpdater;
        this.mInternetType = internetUpdater.getInternetType();
        lifecycle.addObserver(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAirplaneModeChanged$1() {
        updateState(this.mPreference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onInternetTypeChanged$0() {
        updateState(this.mPreference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference("internet_settings");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "internet_settings";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settings.network.InternetUpdater.InternetChangeListener
    public void onAirplaneModeChanged(boolean z) {
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.network.InternetPreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                InternetPreferenceController.this.lambda$onAirplaneModeChanged$1();
            }
        });
    }

    @Override // com.android.settings.network.InternetUpdater.InternetChangeListener
    public void onInternetTypeChanged(int i) {
        boolean z = i != this.mInternetType;
        this.mInternetType = i;
        if (z) {
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.network.InternetPreferenceController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    InternetPreferenceController.this.lambda$onInternetTypeChanged$0();
                }
            });
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mSummaryHelper.register(false);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mSummaryHelper.register(true);
    }

    @Override // com.android.settings.widget.SummaryUpdater.OnSummaryChangeListener
    public void onSummaryChanged(String str) {
        Preference preference;
        if (this.mInternetType != 2 || (preference = this.mPreference) == null) {
            return;
        }
        preference.setSummary(str);
    }

    void updateCellularSummary() {
        SubscriptionInfo defaultDataSubscriptionInfo;
        SubscriptionManager subscriptionManager = (SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class);
        if (subscriptionManager == null || (defaultDataSubscriptionInfo = subscriptionManager.getDefaultDataSubscriptionInfo()) == null) {
            return;
        }
        this.mPreference.setSummary(SubscriptionUtil.getUniqueSubscriptionDisplayName(defaultDataSubscriptionInfo, this.mContext));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        Drawable drawable;
        if (this.mPreference == null) {
            return;
        }
        int intValue = sIconMap.get(Integer.valueOf(this.mInternetType)).intValue();
        if (intValue != 0 && (drawable = this.mContext.getDrawable(intValue)) != null) {
            drawable.setTintList(Utils.getColorAttr(this.mContext, 16843817));
            this.mPreference.setIcon(drawable);
        }
        int i = this.mInternetType;
        if (i == 2) {
            this.mPreference.setSummary(this.mSummaryHelper.getSummary());
        } else if (i == 3) {
            updateCellularSummary();
        } else {
            int intValue2 = sSummaryMap.get(Integer.valueOf(i)).intValue();
            if (intValue2 != 0) {
                this.mPreference.setSummary(intValue2);
            }
        }
    }
}
