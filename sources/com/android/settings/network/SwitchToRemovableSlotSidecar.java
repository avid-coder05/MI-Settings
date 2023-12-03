package com.android.settings.network;

import android.app.FragmentManager;
import android.os.Bundle;
import android.telephony.SubscriptionManager;
import android.util.Log;
import com.android.settings.SidecarFragment;
import com.android.settings.network.telephony.EuiccOperationSidecar;

/* loaded from: classes.dex */
public class SwitchToRemovableSlotSidecar extends EuiccOperationSidecar implements SidecarFragment.Listener {
    private int mPhysicalSlotId;
    private SwitchSlotSidecar mSwitchSlotSidecar;
    private SwitchToEuiccSubscriptionSidecar mSwitchToSubscriptionSidecar;

    public static SwitchToRemovableSlotSidecar get(FragmentManager fragmentManager) {
        return (SwitchToRemovableSlotSidecar) SidecarFragment.get(fragmentManager, "DisableSubscriptionAndSwitchSlotSidecar", SwitchToRemovableSlotSidecar.class, null);
    }

    private void onSwitchSlotSidecarStateChange() {
        int state = this.mSwitchSlotSidecar.getState();
        if (state == 2) {
            this.mSwitchSlotSidecar.reset();
            Log.i("DisableSubscriptionAndSwitchSlotSidecar", "Successfully switched to removable slot.");
            setState(2, 0);
        } else if (state != 3) {
        } else {
            this.mSwitchSlotSidecar.reset();
            Log.i("DisableSubscriptionAndSwitchSlotSidecar", "Failed to switch to removable slot.");
            setState(3, 0);
        }
    }

    private void onSwitchToSubscriptionSidecarStateChange() {
        int state = this.mSwitchToSubscriptionSidecar.getState();
        if (state == 2) {
            this.mSwitchToSubscriptionSidecar.reset();
            Log.i("DisableSubscriptionAndSwitchSlotSidecar", "Successfully disabled eSIM profile. Start to switch to Removable slot.");
            this.mSwitchSlotSidecar.runSwitchToRemovableSlot(this.mPhysicalSlotId);
        } else if (state != 3) {
        } else {
            this.mSwitchToSubscriptionSidecar.reset();
            Log.i("DisableSubscriptionAndSwitchSlotSidecar", "Failed to disable the active eSIM profile.");
            setState(3, 0);
        }
    }

    @Override // com.android.settings.network.telephony.EuiccOperationSidecar
    protected String getReceiverAction() {
        return "disable_subscription_and_switch_slot_sidecar";
    }

    @Override // com.android.settings.network.telephony.EuiccOperationSidecar, com.android.settings.SidecarFragment, android.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSwitchToSubscriptionSidecar = SwitchToEuiccSubscriptionSidecar.get(getChildFragmentManager());
        this.mSwitchSlotSidecar = SwitchSlotSidecar.get(getChildFragmentManager());
    }

    @Override // android.app.Fragment
    public void onPause() {
        this.mSwitchToSubscriptionSidecar.removeListener(this);
        this.mSwitchSlotSidecar.removeListener(this);
        super.onPause();
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        this.mSwitchToSubscriptionSidecar.addListener(this);
        this.mSwitchSlotSidecar.addListener(this);
    }

    @Override // com.android.settings.SidecarFragment.Listener
    public void onStateChange(SidecarFragment sidecarFragment) {
        if (sidecarFragment == this.mSwitchToSubscriptionSidecar) {
            onSwitchToSubscriptionSidecarStateChange();
        } else if (sidecarFragment == this.mSwitchSlotSidecar) {
            onSwitchSlotSidecarStateChange();
        } else {
            Log.wtf("DisableSubscriptionAndSwitchSlotSidecar", "Received state change from a sidecar not expected.");
        }
    }

    public void run(int i) {
        this.mPhysicalSlotId = i;
        if (SubscriptionUtil.getActiveSubscriptions((SubscriptionManager) getContext().getSystemService(SubscriptionManager.class)).stream().anyMatch(SwitchToRemovableSlotSidecar$$ExternalSyntheticLambda0.INSTANCE)) {
            Log.i("DisableSubscriptionAndSwitchSlotSidecar", "There is an active eSIM profile. Disable the profile first.");
            this.mSwitchToSubscriptionSidecar.run(-1);
            return;
        }
        Log.i("DisableSubscriptionAndSwitchSlotSidecar", "There is no active eSIM profiles. Start to switch to removable slot.");
        this.mSwitchSlotSidecar.runSwitchToRemovableSlot(this.mPhysicalSlotId);
    }
}
