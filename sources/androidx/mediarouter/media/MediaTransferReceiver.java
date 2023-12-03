package androidx.mediarouter.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* loaded from: classes.dex */
public final class MediaTransferReceiver extends BroadcastReceiver {
    public static boolean isDeclared(Context applicationContext) {
        Intent intent = new Intent(applicationContext, MediaTransferReceiver.class);
        intent.setPackage(applicationContext.getPackageName());
        return applicationContext.getPackageManager().queryBroadcastReceivers(intent, 0).size() > 0;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
    }
}
