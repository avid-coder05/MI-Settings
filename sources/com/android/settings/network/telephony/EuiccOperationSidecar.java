package com.android.settings.network.telephony;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.euicc.EuiccManager;
import android.util.Log;
import com.android.settings.SidecarFragment;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes2.dex */
public abstract class EuiccOperationSidecar extends SidecarFragment {
    private static AtomicInteger sCurrentOpId = new AtomicInteger((int) SystemClock.elapsedRealtime());
    private int mDetailedCode;
    protected EuiccManager mEuiccManager;
    private int mOpId;
    protected final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.telephony.EuiccOperationSidecar.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (EuiccOperationSidecar.this.getReceiverAction().equals(intent.getAction()) && EuiccOperationSidecar.this.mOpId == intent.getIntExtra("op_id", -1)) {
                EuiccOperationSidecar.this.mResultCode = getResultCode();
                EuiccOperationSidecar.this.mDetailedCode = intent.getIntExtra("android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_DETAILED_CODE", 0);
                EuiccOperationSidecar.this.mResultIntent = intent;
                Log.i("EuiccOperationSidecar", String.format("Result code : %d; detailed code : %d", Integer.valueOf(EuiccOperationSidecar.this.mResultCode), Integer.valueOf(EuiccOperationSidecar.this.mDetailedCode)));
                EuiccOperationSidecar.this.onActionReceived();
            }
        }
    };
    private int mResultCode;
    private Intent mResultIntent;

    /* JADX INFO: Access modifiers changed from: protected */
    public PendingIntent createCallbackIntent() {
        this.mOpId = sCurrentOpId.incrementAndGet();
        Intent intent = new Intent(getReceiverAction());
        intent.putExtra("op_id", this.mOpId);
        return PendingIntent.getBroadcast(getContext(), 0, intent, 335544320);
    }

    protected abstract String getReceiverAction();

    public int getResultCode() {
        return this.mResultCode;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onActionReceived() {
        int i = this.mResultCode;
        if (i == 0) {
            setState(2, 0);
        } else {
            setState(3, i);
        }
    }

    @Override // com.android.settings.SidecarFragment, android.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mEuiccManager = (EuiccManager) getContext().getSystemService(EuiccManager.class);
        getContext().getApplicationContext().registerReceiver(this.mReceiver, new IntentFilter(getReceiverAction()), "android.permission.WRITE_EMBEDDED_SUBSCRIPTIONS", null);
    }

    @Override // com.android.settings.SidecarFragment, android.app.Fragment
    public void onDestroy() {
        getContext().getApplicationContext().unregisterReceiver(this.mReceiver);
        super.onDestroy();
    }
}
