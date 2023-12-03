package com.android.settings.network.telephony;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SubscriptionManager;

/* loaded from: classes2.dex */
public class SubscriptionActionDialogActivity extends Activity {
    protected SubscriptionManager mSubscriptionManager;

    /* JADX INFO: Access modifiers changed from: protected */
    public void dismissProgressDialog() {
        ProgressDialogFragment.dismiss(getFragmentManager());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSubscriptionManager = (SubscriptionManager) getSystemService(SubscriptionManager.class);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showErrorDialog(String str, String str2) {
        AlertDialogFragment.show(this, str, str2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showProgressDialog(String str) {
        ProgressDialogFragment.show(getFragmentManager(), str, null);
    }
}
