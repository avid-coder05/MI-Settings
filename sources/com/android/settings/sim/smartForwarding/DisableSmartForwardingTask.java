package com.android.settings.sim.smartForwarding;

import android.telephony.CallForwardingInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

/* loaded from: classes2.dex */
public class DisableSmartForwardingTask implements Runnable {
    private final CallForwardingInfo[] callForwardingInfo;
    private final boolean[] callWaitingStatus;
    private final TelephonyManager tm;

    public DisableSmartForwardingTask(TelephonyManager telephonyManager, boolean[] zArr, CallForwardingInfo[] callForwardingInfoArr) {
        this.tm = telephonyManager;
        this.callWaitingStatus = zArr;
        this.callForwardingInfo = callForwardingInfoArr;
    }

    @Override // java.lang.Runnable
    public void run() {
        for (int i = 0; i < this.tm.getActiveModemCount(); i++) {
            if (this.callWaitingStatus != null) {
                Log.d("SmartForwarding", "Restore call waiting to " + this.callWaitingStatus[i]);
                this.tm.setCallWaitingEnabled(this.callWaitingStatus[i], null, null);
            }
            CallForwardingInfo[] callForwardingInfoArr = this.callForwardingInfo;
            if (callForwardingInfoArr != null && callForwardingInfoArr[i] != null && callForwardingInfoArr[i].getTimeoutSeconds() > 0) {
                Log.d("SmartForwarding", "Restore call waiting to " + this.callForwardingInfo);
                this.tm.setCallForwarding(this.callForwardingInfo[i], null, null);
            }
        }
    }
}
