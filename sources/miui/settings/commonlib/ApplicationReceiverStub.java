package miui.settings.commonlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/* loaded from: classes4.dex */
public class ApplicationReceiverStub extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.i(MemoryOptimizationUtil.TAG, "ApplicationReceiverStub start");
    }
}
