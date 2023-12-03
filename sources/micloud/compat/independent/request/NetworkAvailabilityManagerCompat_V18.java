package micloud.compat.independent.request;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.xiaomi.micloudsdk.provider.MiCloudSettings;

/* loaded from: classes2.dex */
class NetworkAvailabilityManagerCompat_V18 extends NetworkAvailabilityManagerCompat_Base {
    private void sendNetworkAvailabilityChangedBroadcast(Context context, boolean z) {
        Intent intent = new Intent("miui.intent.action.MICLOUD_NETWORK_AVAILABILITY_CHANGED");
        intent.putExtra("active", z);
        context.sendBroadcast(intent);
        Log.d("NetworkAvailabilityManager", "sendNetworkAvailabilityChangedBroadcast availability: " + z);
    }

    public boolean getAvailability(Context context) {
        if (context != null) {
            return MiCloudSettings.getInt(context.getContentResolver(), "micloud_network_availability", 0) != 0;
        }
        throw new IllegalArgumentException("context cannot be null");
    }

    @Override // micloud.compat.independent.request.NetworkAvailabilityManagerCompat_Base, micloud.compat.independent.request.INetworkAvailabilityManagerCompat
    public void setAvailability(Context context, boolean z) {
        if (context == null) {
            Log.d("NetworkAvailabilityManager", "context is null, ignore");
            return;
        }
        boolean availability = getAvailability(context);
        if (availability != z) {
            MiCloudSettings.putInt(context.getContentResolver(), "micloud_network_availability", z ? 1 : 0);
            sendNetworkAvailabilityChangedBroadcast(context, z);
            Log.d("NetworkAvailabilityManager", "micloud network state changed from " + availability + " to " + z);
        }
    }
}
