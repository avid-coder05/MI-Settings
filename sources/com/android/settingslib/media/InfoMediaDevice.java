package com.android.settingslib.media;

import android.content.Context;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2Manager;
import com.android.settingslib.R$drawable;
import java.util.List;

/* loaded from: classes2.dex */
public class InfoMediaDevice extends MediaDevice {
    /* JADX INFO: Access modifiers changed from: package-private */
    public InfoMediaDevice(Context context, MediaRouter2Manager mediaRouter2Manager, MediaRoute2Info mediaRoute2Info, String str) {
        super(context, mediaRouter2Manager, mediaRoute2Info, str);
        initDeviceRecord();
    }

    int getDrawableResId() {
        int type = this.mRouteInfo.getType();
        return type != 1001 ? type != 2000 ? R$drawable.ic_media_speaker_device : R$drawable.ic_media_group_device : R$drawable.ic_media_display_device;
    }

    int getDrawableResIdByFeature() {
        List<String> features = this.mRouteInfo.getFeatures();
        return features.contains("android.media.route.feature.REMOTE_GROUP_PLAYBACK") ? R$drawable.ic_media_group_device : features.contains("android.media.route.feature.REMOTE_VIDEO_PLAYBACK") ? R$drawable.ic_media_display_device : R$drawable.ic_media_speaker_device;
    }

    @Override // com.android.settingslib.media.MediaDevice
    public String getId() {
        return MediaDeviceUtils.getId(this.mRouteInfo);
    }

    @Override // com.android.settingslib.media.MediaDevice
    public String getName() {
        return this.mRouteInfo.getName().toString();
    }

    @Override // com.android.settingslib.media.MediaDevice
    public boolean isConnected() {
        return true;
    }
}
