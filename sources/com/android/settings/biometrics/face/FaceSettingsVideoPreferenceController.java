package com.android.settings.biometrics.face;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.PreferenceScreen;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.VideoPreference;
import com.android.settings.widget.VideoPreferenceController;

/* loaded from: classes.dex */
public class FaceSettingsVideoPreferenceController extends VideoPreferenceController {
    private static final String KEY_VIDEO = "security_settings_face_video";
    private VideoPreference mVideoPreference;

    public FaceSettingsVideoPreferenceController(Context context) {
        this(context, KEY_VIDEO);
    }

    public FaceSettingsVideoPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.widget.VideoPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.widget.VideoPreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        VideoPreference videoPreference = (VideoPreference) preferenceScreen.findPreference(KEY_VIDEO);
        this.mVideoPreference = videoPreference;
        videoPreference.onViewVisible();
    }

    @Override // com.android.settings.widget.VideoPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.widget.VideoPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.widget.VideoPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.widget.VideoPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.widget.VideoPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.widget.VideoPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.widget.VideoPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
