package com.android.settings.network.ims;

import android.content.Context;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.telephony.ims.ImsException;
import android.util.Log;

/* loaded from: classes.dex */
public class VtQueryImsState extends ImsQueryController {
    private Context mContext;
    private int mSubId;

    public VtQueryImsState(Context context, int i) {
        super(2, 0, 1);
        this.mContext = context;
        this.mSubId = i;
    }

    public boolean isAllowUserControl() {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return !isTtyEnabled(this.mContext) || isTtyOnVolteEnabled(this.mSubId);
        }
        return false;
    }

    public boolean isEnabledByUser() {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return isEnabledByUser(this.mSubId);
        }
        return false;
    }

    boolean isEnabledByUser(int i) {
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            return new ImsQueryVtUserSetting(i).query();
        }
        return false;
    }

    public boolean isReadyToVideoCall() {
        if (isProvisionedOnDevice(this.mSubId)) {
            try {
                if (isEnabledByPlatform(this.mSubId)) {
                    return isServiceStateReady(this.mSubId);
                }
                return false;
            } catch (ImsException | IllegalArgumentException | InterruptedException e) {
                Log.w("VtQueryImsState", "fail to get Vt ready. subId=" + this.mSubId, e);
                return false;
            }
        }
        return false;
    }

    boolean isTtyEnabled(Context context) {
        return ((TelecomManager) context.getSystemService(TelecomManager.class)).getCurrentTtyMode() != 0;
    }
}
