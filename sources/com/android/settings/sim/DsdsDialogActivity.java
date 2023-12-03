package com.android.settings.sim;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.settings.R;
import com.android.settings.SidecarFragment;
import com.android.settings.network.EnableMultiSimSidecar;
import com.android.settings.network.telephony.ConfirmDialogFragment;
import com.android.settings.network.telephony.SubscriptionActionDialogActivity;

/* loaded from: classes2.dex */
public class DsdsDialogActivity extends SubscriptionActionDialogActivity implements SidecarFragment.Listener, ConfirmDialogFragment.OnConfirmListener {
    private EnableMultiSimSidecar mEnableMultiSimSidecar;

    private void showEnableDsdsConfirmDialog() {
        ConfirmDialogFragment.show(this, ConfirmDialogFragment.OnConfirmListener.class, 1, getString(R.string.sim_action_enable_dsds_title), getString(R.string.sim_action_enable_dsds_text), getString(R.string.sim_action_yes), getString(R.string.sim_action_no_thanks));
    }

    private void showRebootConfirmDialog() {
        ConfirmDialogFragment.show(this, ConfirmDialogFragment.OnConfirmListener.class, 2, getString(R.string.sim_action_restart_title), getString(R.string.sim_action_enable_dsds_text), getString(R.string.sim_action_reboot), getString(R.string.cancel));
    }

    private void startChooseSimActivity() {
        Intent intent = ChooseSimActivity.getIntent(this);
        intent.putExtra("has_psim", true);
        startActivity(intent);
        finish();
    }

    @Override // com.android.settings.network.telephony.ConfirmDialogFragment.OnConfirmListener
    public void onConfirm(int i, boolean z) {
        if (!z) {
            Log.i("DsdsDialogActivity", "User cancel the dialog to enable DSDS.");
            startChooseSimActivity();
            return;
        }
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TelephonyManager.class);
        if (i == 1) {
            if (telephonyManager.doesSwitchMultiSimConfigTriggerReboot()) {
                Log.i("DsdsDialogActivity", "Device does not support reboot free DSDS.");
                showRebootConfirmDialog();
                return;
            }
            Log.i("DsdsDialogActivity", "Enabling DSDS without rebooting.");
            showProgressDialog(getString(R.string.sim_action_enabling_sim_without_carrier_name));
            this.mEnableMultiSimSidecar.run(2);
        } else if (i == 2) {
            Log.i("DsdsDialogActivity", "User confirmed reboot to enable DSDS.");
            SimActivationNotifier.setShowSimSettingsNotification(this, true);
            telephonyManager.switchMultiSimConfig(2);
        } else {
            Log.e("DsdsDialogActivity", "Unrecognized confirmation dialog tag: " + i);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.network.telephony.SubscriptionActionDialogActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mEnableMultiSimSidecar = EnableMultiSimSidecar.get(getFragmentManager());
        if (bundle == null) {
            showEnableDsdsConfirmDialog();
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        this.mEnableMultiSimSidecar.removeListener(this);
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        this.mEnableMultiSimSidecar.addListener(this);
    }

    @Override // com.android.settings.SidecarFragment.Listener
    public void onStateChange(SidecarFragment sidecarFragment) {
        if (sidecarFragment == this.mEnableMultiSimSidecar) {
            int state = sidecarFragment.getState();
            if (state == 2) {
                this.mEnableMultiSimSidecar.reset();
                Log.i("DsdsDialogActivity", "Enabled DSDS successfully");
                dismissProgressDialog();
                finish();
            } else if (state != 3) {
            } else {
                this.mEnableMultiSimSidecar.reset();
                Log.e("DsdsDialogActivity", "Failed to enable DSDS");
                dismissProgressDialog();
                showErrorDialog(getString(R.string.dsds_activation_failure_title), getString(R.string.dsds_activation_failure_body_msg2));
            }
        }
    }
}
