package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import com.android.settings.network.GlobalSettingsChangeListener;
import com.android.settings.network.ProxySubscriptionManager;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.WirelessUtils;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class AirplaneModeEnabler extends GlobalSettingsChangeListener {
    private final Context mContext;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private WeakReference<OnAirplaneModeChangedListener> mOnAirplaneModeChangedListener;
    PhoneStateListener mPhoneStateListener;
    private TelephonyManager mTelephonyManager;

    /* loaded from: classes.dex */
    public interface OnAirplaneModeChangedListener {
        void onAirplaneModeChanged(boolean z);
    }

    public AirplaneModeEnabler(Context context, OnAirplaneModeChangedListener onAirplaneModeChangedListener) {
        super(context, "airplane_mode_on");
        this.mContext = context;
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        this.mOnAirplaneModeChangedListener = new WeakReference<>(onAirplaneModeChangedListener);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mPhoneStateListener = new PhoneStateListener(Looper.getMainLooper()) { // from class: com.android.settings.AirplaneModeEnabler.1
            public void onRadioPowerStateChanged(int i) {
                AirplaneModeEnabler.this.onAirplaneModeChanged();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAirplaneModeChanged() {
        WeakReference<OnAirplaneModeChangedListener> weakReference = this.mOnAirplaneModeChangedListener;
        if (weakReference == null || weakReference.get() == null) {
            return;
        }
        this.mOnAirplaneModeChangedListener.get().onAirplaneModeChanged(isAirplaneModeOn());
    }

    private void setAirplaneModeOn(boolean z) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "airplane_mode_on", z ? 1 : 0);
        WeakReference<OnAirplaneModeChangedListener> weakReference = this.mOnAirplaneModeChangedListener;
        if (weakReference != null && weakReference.get() != null) {
            this.mOnAirplaneModeChangedListener.get().onAirplaneModeChanged(z);
        }
        Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
        intent.putExtra("state", z);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    public boolean isAirplaneModeOn() {
        return WirelessUtils.isAirplaneModeOn(this.mContext);
    }

    public boolean isInEcmMode() {
        if (this.mTelephonyManager.getEmergencyCallbackMode()) {
            return true;
        }
        List<SubscriptionInfo> activeSubscriptionsInfo = ProxySubscriptionManager.getInstance(this.mContext).getActiveSubscriptionsInfo();
        if (activeSubscriptionsInfo == null) {
            return false;
        }
        Iterator<SubscriptionInfo> it = activeSubscriptionsInfo.iterator();
        while (it.hasNext()) {
            TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(it.next().getSubscriptionId());
            if (createForSubscriptionId != null && createForSubscriptionId.getEmergencyCallbackMode()) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.settings.network.GlobalSettingsChangeListener
    public void onChanged(String str) {
        onAirplaneModeChanged();
    }

    public void setAirplaneMode(boolean z) {
        if (!isInEcmMode()) {
            this.mMetricsFeatureProvider.action(this.mContext, 177, z);
            setAirplaneModeOn(z);
            return;
        }
        Log.d("AirplaneModeEnabler", "ECM airplane mode=" + z);
    }

    public void setAirplaneModeInECM(boolean z, boolean z2) {
        Log.d("AirplaneModeEnabler", "Exist ECM=" + z + ", with airplane mode=" + z2);
        if (z) {
            setAirplaneModeOn(z2);
        } else {
            onAirplaneModeChanged();
        }
    }

    public void start() {
        this.mTelephonyManager.listen(this.mPhoneStateListener, MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_NO_SCREENSHOT);
    }

    public void stop() {
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    }
}
