package com.android.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.settings.Utils;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes.dex */
public class SwipeToNotificationPreferenceController extends GesturePreferenceController {
    private static final int OFF = 0;
    private static final int ON = 1;
    private static final String PREF_KEY_VIDEO = "gesture_swipe_down_fingerprint_video";
    private static final String SECURE_KEY = "system_navigation_keys_enabled";

    public SwipeToNotificationPreferenceController(Context context, String str) {
        super(context, str);
    }

    public static boolean isAvailable(Context context) {
        return isGestureAvailable(context);
    }

    private static boolean isGestureAvailable(Context context) {
        return Utils.hasFingerprintHardware(context) && context.getResources().getBoolean(17891658);
    }

    public static boolean isSuggestionComplete(Context context, SharedPreferences sharedPreferences) {
        return !isGestureAvailable(context) || sharedPreferences.getBoolean("pref_swipe_to_notification_suggestion_complete", false);
    }

    public static boolean isSwipeToNotificationOn(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), SECURE_KEY, 0) == 1;
    }

    public static boolean setSwipeToNotification(Context context, boolean z) {
        return Settings.Secure.putInt(context.getContentResolver(), SECURE_KEY, z ? 1 : 0);
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isAvailable(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.gestures.GesturePreferenceController
    protected String getVideoPrefKey() {
        return PREF_KEY_VIDEO;
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return isSwipeToNotificationOn(this.mContext);
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), "gesture_swipe_down_fingerprint");
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        setSwipeToNotification(this.mContext, z);
        return true;
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
