package com.android.settings.notification;

import android.content.Context;
import android.text.TextUtils;
import com.android.settings.R;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes2.dex */
public class AlarmVolumePreferenceController extends VolumeSeekBarPreferenceController {
    private static final String KEY_ALARM_VOLUME = "alarm_volume";

    public AlarmVolumePreferenceController(Context context) {
        super(context, KEY_ALARM_VOLUME);
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController
    public int getAudioStream() {
        return 4;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!this.mContext.getResources().getBoolean(R.bool.config_show_alarm_volume) || ((VolumeSeekBarPreferenceController) this).mHelper.isSingleVolume()) ? 3 : 0;
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController
    public int getMuteIcon() {
        return 17302320;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_ALARM_VOLUME;
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), KEY_ALARM_VOLUME);
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public boolean useDynamicSliceSummary() {
        return true;
    }
}
