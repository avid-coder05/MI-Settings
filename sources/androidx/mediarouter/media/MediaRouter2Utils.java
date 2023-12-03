package androidx.mediarouter.media;

import android.annotation.SuppressLint;
import android.media.MediaRoute2Info;
import android.media.RouteDiscoveryPreference;
import android.net.Uri;
import android.os.Bundle;
import androidx.mediarouter.media.MediaRouteDescriptor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressLint({"NewApi"})
/* loaded from: classes.dex */
class MediaRouter2Utils {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<String> getRouteIds(List<MediaRoute2Info> routes) {
        if (routes == null) {
            return new ArrayList();
        }
        ArrayList arrayList = new ArrayList();
        for (MediaRoute2Info mediaRoute2Info : routes) {
            if (mediaRoute2Info != null) {
                arrayList.add(mediaRoute2Info.getId());
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static RouteDiscoveryPreference toDiscoveryPreference(MediaRouteDiscoveryRequest discoveryRequest) {
        if (discoveryRequest == null || !discoveryRequest.isValid()) {
            return new RouteDiscoveryPreference.Builder(new ArrayList(), false).build();
        }
        boolean isActiveScan = discoveryRequest.isActiveScan();
        ArrayList arrayList = new ArrayList();
        Iterator<String> it = discoveryRequest.getSelector().getControlCategories().iterator();
        while (it.hasNext()) {
            arrayList.add(toRouteFeature(it.next()));
        }
        return new RouteDiscoveryPreference.Builder(arrayList, isActiveScan).build();
    }

    public static MediaRouteDescriptor toMediaRouteDescriptor(MediaRoute2Info fwkMediaRoute2Info) {
        if (fwkMediaRoute2Info == null) {
            return null;
        }
        MediaRouteDescriptor.Builder canDisconnect = new MediaRouteDescriptor.Builder(fwkMediaRoute2Info.getId(), fwkMediaRoute2Info.getName().toString()).setConnectionState(fwkMediaRoute2Info.getConnectionState()).setVolumeHandling(fwkMediaRoute2Info.getVolumeHandling()).setVolumeMax(fwkMediaRoute2Info.getVolumeMax()).setVolume(fwkMediaRoute2Info.getVolume()).setExtras(fwkMediaRoute2Info.getExtras()).setEnabled(true).setCanDisconnect(false);
        CharSequence description = fwkMediaRoute2Info.getDescription();
        if (description != null) {
            canDisconnect.setDescription(description.toString());
        }
        Uri iconUri = fwkMediaRoute2Info.getIconUri();
        if (iconUri != null) {
            canDisconnect.setIconUri(iconUri);
        }
        Bundle extras = fwkMediaRoute2Info.getExtras();
        if (extras != null && extras.containsKey("androidx.mediarouter.media.KEY_EXTRAS") && extras.containsKey("androidx.mediarouter.media.KEY_DEVICE_TYPE") && extras.containsKey("androidx.mediarouter.media.KEY_CONTROL_FILTERS")) {
            canDisconnect.setExtras(extras.getBundle("androidx.mediarouter.media.KEY_EXTRAS"));
            canDisconnect.setDeviceType(extras.getInt("androidx.mediarouter.media.KEY_DEVICE_TYPE", 0));
            canDisconnect.setPlaybackType(extras.getInt("androidx.mediarouter.media.KEY_PLAYBACK_TYPE", 1));
            ArrayList parcelableArrayList = extras.getParcelableArrayList("androidx.mediarouter.media.KEY_CONTROL_FILTERS");
            if (parcelableArrayList != null) {
                canDisconnect.addControlFilters(parcelableArrayList);
            }
            return canDisconnect.build();
        }
        return null;
    }

    static String toRouteFeature(String controlCategory) {
        controlCategory.hashCode();
        char c = 65535;
        switch (controlCategory.hashCode()) {
            case -2065577523:
                if (controlCategory.equals("android.media.intent.category.REMOTE_PLAYBACK")) {
                    c = 0;
                    break;
                }
                break;
            case 956939050:
                if (controlCategory.equals("android.media.intent.category.LIVE_AUDIO")) {
                    c = 1;
                    break;
                }
                break;
            case 975975375:
                if (controlCategory.equals("android.media.intent.category.LIVE_VIDEO")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return "android.media.route.feature.REMOTE_PLAYBACK";
            case 1:
                return "android.media.route.feature.LIVE_AUDIO";
            case 2:
                return "android.media.route.feature.LIVE_VIDEO";
            default:
                return controlCategory;
        }
    }
}
