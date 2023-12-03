package com.android.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.UiccSlotInfo;
import android.util.Log;
import com.android.settingslib.utils.ThreadUtils;
import com.google.common.collect.ImmutableList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class UiccSlotUtil {
    public static ImmutableList<UiccSlotInfo> getSlotInfos(TelephonyManager telephonyManager) {
        UiccSlotInfo[] uiccSlotsInfo = telephonyManager.getUiccSlotsInfo();
        return uiccSlotsInfo == null ? ImmutableList.of() : ImmutableList.copyOf(uiccSlotsInfo);
    }

    private static void performSwitchToRemovableSlot(int i, Context context) throws UiccSlotsException {
        CountDownLatch countDownLatch;
        CarrierConfigChangedReceiver carrierConfigChangedReceiver;
        long j = Settings.Global.getLong(context.getContentResolver(), "euicc_switch_slot_timeout_millis", 25000L);
        BroadcastReceiver broadcastReceiver = null;
        try {
            try {
                countDownLatch = new CountDownLatch(1);
                carrierConfigChangedReceiver = new CarrierConfigChangedReceiver(countDownLatch);
            } catch (InterruptedException e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            carrierConfigChangedReceiver.registerOn(context);
            switchSlots(context, i);
            countDownLatch.await(j, TimeUnit.MILLISECONDS);
            context.unregisterReceiver(carrierConfigChangedReceiver);
        } catch (InterruptedException e2) {
            e = e2;
            broadcastReceiver = carrierConfigChangedReceiver;
            Thread.currentThread().interrupt();
            Log.e("UiccSlotUtil", "Failed switching to physical slot.", e);
            if (broadcastReceiver != null) {
                context.unregisterReceiver(broadcastReceiver);
            }
        } catch (Throwable th2) {
            th = th2;
            broadcastReceiver = carrierConfigChangedReceiver;
            if (broadcastReceiver != null) {
                context.unregisterReceiver(broadcastReceiver);
            }
            throw th;
        }
    }

    private static void switchSlots(Context context, int... iArr) throws UiccSlotsException {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (telephonyManager.isMultiSimEnabled()) {
            Log.i("UiccSlotUtil", "Multiple active slots supported. Not calling switchSlots.");
        } else if (!telephonyManager.switchSlots(iArr)) {
            throw new UiccSlotsException("Failed to switch slots");
        }
    }

    public static synchronized void switchToRemovableSlot(int i, Context context) throws UiccSlotsException {
        synchronized (UiccSlotUtil.class) {
            if (ThreadUtils.isMainThread()) {
                throw new IllegalThreadStateException("Do not call switchToRemovableSlot on the main thread.");
            }
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
            if (telephonyManager.isMultiSimEnabled()) {
                Log.i("UiccSlotUtil", "Multiple active slots supported. Not calling switchSlots.");
                return;
            }
            UiccSlotInfo[] uiccSlotsInfo = telephonyManager.getUiccSlotsInfo();
            if (i == -1) {
                for (int i2 = 0; i2 < uiccSlotsInfo.length; i2++) {
                    if (uiccSlotsInfo[i2].isRemovable() && !uiccSlotsInfo[i2].getIsActive() && uiccSlotsInfo[i2].getCardStateInfo() != 3 && uiccSlotsInfo[i2].getCardStateInfo() != 4) {
                        performSwitchToRemovableSlot(i2, context);
                        return;
                    }
                }
            } else if (i >= uiccSlotsInfo.length || !uiccSlotsInfo[i].isRemovable()) {
                throw new UiccSlotsException("The given slotId is not a removable slot: " + i);
            } else if (!uiccSlotsInfo[i].getIsActive()) {
                performSwitchToRemovableSlot(i, context);
            }
        }
    }
}
