package com.android.settings.network;

import android.app.FragmentManager;
import android.app.PendingIntent;
import com.android.settings.SidecarFragment;
import com.android.settings.network.telephony.EuiccOperationSidecar;

/* loaded from: classes.dex */
public class SwitchToEuiccSubscriptionSidecar extends EuiccOperationSidecar {
    private PendingIntent mCallbackIntent;

    public static SwitchToEuiccSubscriptionSidecar get(FragmentManager fragmentManager) {
        return (SwitchToEuiccSubscriptionSidecar) SidecarFragment.get(fragmentManager, "SwitchToEuiccSubscriptionSidecar", SwitchToEuiccSubscriptionSidecar.class, null);
    }

    @Override // com.android.settings.network.telephony.EuiccOperationSidecar
    public String getReceiverAction() {
        return "com.android.settings.network.SWITCH_TO_SUBSCRIPTION";
    }

    public void run(int i) {
        setState(1, 0);
        PendingIntent createCallbackIntent = createCallbackIntent();
        this.mCallbackIntent = createCallbackIntent;
        this.mEuiccManager.switchToSubscription(i, createCallbackIntent);
    }
}
