package com.android.settings.network;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.os.UserHandle;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.settings.search.SearchUpdater;

/* loaded from: classes.dex */
public class TetherProvisioningActivity extends Activity {
    private static final boolean DEBUG = Log.isLoggable("TetherProvisioningAct", 3);
    static final String EXTRA_TETHER_SUBID = "android.net.extra.TETHER_SUBID";
    public static final String EXTRA_TETHER_UI_PROVISIONING_APP_NAME = "android.net.extra.TETHER_UI_PROVISIONING_APP_NAME";
    static final int PROVISION_REQUEST = 0;
    private ResultReceiver mResultReceiver;

    @Override // android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 0) {
            if (DEBUG) {
                Log.d("TetherProvisioningAct", "Got result from app: " + i2);
            }
            this.mResultReceiver.send(i2 == -1 ? 0 : 11, null);
            finish();
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mResultReceiver = (ResultReceiver) getIntent().getParcelableExtra("extraProvisionCallback");
        int intExtra = getIntent().getIntExtra("extraAddTetherType", -1);
        int intExtra2 = getIntent().getIntExtra("android.net.extra.TETHER_SUBID", -1);
        int activeDataSubscriptionId = SubscriptionManager.getActiveDataSubscriptionId();
        if (intExtra2 != activeDataSubscriptionId) {
            Log.e("TetherProvisioningAct", "This Provisioning request is outdated, current subId: " + activeDataSubscriptionId);
            this.mResultReceiver.send(11, null);
            finish();
            return;
        }
        String[] stringArrayExtra = getIntent().getStringArrayExtra(EXTRA_TETHER_UI_PROVISIONING_APP_NAME);
        if (stringArrayExtra == null || stringArrayExtra.length != 2) {
            Log.e("TetherProvisioningAct", "Unexpected provision app configuration");
            return;
        }
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService("phone");
        if (telephonyManager != null && !"44020".equals(telephonyManager.getSimOperatorNumeric(activeDataSubscriptionId))) {
            this.mResultReceiver.send(0, null);
            finish();
            Log.e("TetherProvisioningAct", "Not operator sim, normal SIM! return!");
            return;
        }
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClassName(stringArrayExtra[0], stringArrayExtra[1]);
        intent.putExtra("extraAddTetherType", intExtra);
        intent.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", activeDataSubscriptionId);
        intent.putExtra("extraProvisionCallback", this.mResultReceiver);
        if (DEBUG) {
            Log.d("TetherProvisioningAct", "Starting provisioning app: " + stringArrayExtra[0] + "." + stringArrayExtra[1]);
        }
        if (!getPackageManager().queryIntentActivities(intent, SearchUpdater.GOOGLE).isEmpty()) {
            startActivityForResultAsUser(intent, 0, UserHandle.CURRENT);
            return;
        }
        Log.e("TetherProvisioningAct", "Provisioning app is configured, but not available.");
        this.mResultReceiver.send(11, null);
        finish();
    }
}
