package com.android.settings.connection;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.milink.api.v1.MiLinkClientScanListCallback;
import com.milink.api.v1.MilinkClientManager;

/* loaded from: classes.dex */
public class ScreenProjectionController extends BasePreferenceController {
    public static final boolean MILINK_SETTING_ENTRANCE_COMPAT = true;
    public static final String TAG = "ScreenProjectionController";

    public ScreenProjectionController(Context context, String str) {
        super(context, str);
        milinkEntranceCompat();
    }

    private static Intent getDecoupleMiLink(Context context) {
        Intent intent = new Intent("miui.intent.action.MILINK_SETTING");
        if (context.getPackageManager().queryIntentActivities(intent, 1).isEmpty()) {
            return null;
        }
        return intent;
    }

    public static boolean hasDecoupleMiLink(Context context) {
        return getDecoupleMiLink(context) != null;
    }

    public static boolean isNeedRemoveScreenProjection() {
        try {
            return MilinkClientManager.class.getDeclaredMethod("showScanList", MiLinkClientScanListCallback.class, Integer.TYPE) == null;
        } catch (NoSuchMethodException unused) {
            Log.e(TAG, "cannot find method: showScanList");
            return true;
        }
    }

    private void milinkEntranceCompat() {
        Settings.Global.putInt(this.mContext.getContentResolver(), "milink_setting_entrance_compat", 1);
    }

    private boolean resolveMiLinkSettings() {
        Intent decoupleMiLink = getDecoupleMiLink(this.mContext);
        if (decoupleMiLink != null) {
            this.mContext.startActivity(decoupleMiLink);
            return true;
        }
        return false;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference("screen_projection");
        if (findPreference instanceof ValuePreference) {
            ((ValuePreference) findPreference).setShowRightArrow(true);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isNeedRemoveScreenProjection() ? 2 : 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), "screen_projection") || resolveMiLinkSettings()) {
            return false;
        }
        new SubSettingLauncher(this.mContext).setDestination("com.android.settings.projection.ScreenProjectionFragment").launch();
        return false;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
