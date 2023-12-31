package com.android.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import java.util.concurrent.CountDownLatch;

/* loaded from: classes.dex */
public class CarrierConfigChangedReceiver extends BroadcastReceiver {
    private final CountDownLatch mLatch;

    public CarrierConfigChangedReceiver(CountDownLatch countDownLatch) {
        this.mLatch = countDownLatch;
    }

    private void checkSubscriptionIndex(Intent intent) {
        if (intent.hasExtra("android.telephony.extra.SUBSCRIPTION_INDEX")) {
            Log.i("CarrierConfigChangedReceiver", "subId from config changed: " + intent.getIntExtra("android.telephony.extra.SUBSCRIPTION_INDEX", -1));
            this.mLatch.countDown();
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (!isInitialStickyBroadcast() && "android.telephony.action.CARRIER_CONFIG_CHANGED".equals(intent.getAction())) {
            checkSubscriptionIndex(intent);
        }
    }

    public void registerOn(Context context) {
        context.registerReceiver(this, new IntentFilter("android.telephony.action.CARRIER_CONFIG_CHANGED"));
    }
}
