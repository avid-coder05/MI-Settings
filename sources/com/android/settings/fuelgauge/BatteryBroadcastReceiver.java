package com.android.settings.fuelgauge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.android.settings.Utils;
import com.android.settings.homepage.contextualcards.slices.BatteryFixSlice;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* loaded from: classes.dex */
public class BatteryBroadcastReceiver extends BroadcastReceiver {
    int mBatteryHealth;
    String mBatteryLevel;
    private OnBatteryChangedListener mBatteryListener;
    String mBatteryStatus;
    private Context mContext;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface BatteryUpdateType {
    }

    /* loaded from: classes.dex */
    public interface OnBatteryChangedListener {
        void onBatteryChanged(int i);
    }

    public BatteryBroadcastReceiver(Context context) {
        this.mContext = context;
    }

    private void updateBatteryStatus(Intent intent, boolean z) {
        if (intent != null && this.mBatteryListener != null) {
            if ("android.intent.action.BATTERY_CHANGED".equals(intent.getAction())) {
                String batteryPercentage = Utils.getBatteryPercentage(intent);
                String batteryStatus = com.android.settingslib.Utils.getBatteryStatus(this.mContext, intent);
                int intExtra = intent.getIntExtra("health", 1);
                if (!Utils.isBatteryPresent(intent)) {
                    Log.w("BatteryBroadcastRcvr", "Problem reading the battery meter.");
                    this.mBatteryListener.onBatteryChanged(5);
                } else if (z) {
                    this.mBatteryListener.onBatteryChanged(0);
                } else if (intExtra != this.mBatteryHealth) {
                    this.mBatteryListener.onBatteryChanged(4);
                } else if (!batteryPercentage.equals(this.mBatteryLevel)) {
                    this.mBatteryListener.onBatteryChanged(1);
                } else if (!batteryStatus.equals(this.mBatteryStatus)) {
                    this.mBatteryListener.onBatteryChanged(3);
                }
                this.mBatteryLevel = batteryPercentage;
                this.mBatteryStatus = batteryStatus;
                this.mBatteryHealth = intExtra;
            } else if ("android.os.action.POWER_SAVE_MODE_CHANGED".equals(intent.getAction())) {
                this.mBatteryListener.onBatteryChanged(2);
            }
        }
        BatteryFixSlice.updateBatteryTipAvailabilityCache(this.mContext);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        updateBatteryStatus(intent, false);
    }

    public void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        updateBatteryStatus(this.mContext.registerReceiver(this, intentFilter), true);
    }

    public void setBatteryChangedListener(OnBatteryChangedListener onBatteryChangedListener) {
        this.mBatteryListener = onBatteryChangedListener;
    }

    public void unRegister() {
        this.mContext.unregisterReceiver(this);
    }
}
