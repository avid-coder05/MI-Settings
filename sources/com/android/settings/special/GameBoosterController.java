package com.android.settings.special;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.MiuiUtils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.SettingsFeatures;
import java.net.URISyntaxException;

/* loaded from: classes2.dex */
public class GameBoosterController extends BasePreferenceController {
    public static final String JUMP_GAME_ACTION = "#Intent;action=com.miui.gamebooster.action.ACCESS_MAINACTIVITY;S.jump_target=gamebox;end";
    private static final String TAG = "GameBoosterController";

    public GameBoosterController(Context context, String str) {
        super(context, str);
    }

    private static Intent getGameBoosterIntent() {
        try {
            if (SettingsFeatures.sNotSupportToolBoxDevices.contains(Build.DEVICE)) {
                return null;
            }
            return Intent.parseUri(JUMP_GAME_ACTION, 0);
        } catch (URISyntaxException unused) {
            Log.e(TAG, "URI invalid");
            return null;
        }
    }

    private boolean isIntentValid(Intent intent) {
        return intent != null && MiuiUtils.isIntentActivityExistAsUser(this.mContext, intent, UserHandle.myUserId());
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!isIntentValid(getGameBoosterIntent()) || SettingsFeatures.isSupportDock(this.mContext)) ? 2 : 0;
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
        if (TextUtils.equals(this.mPreferenceKey, preference.getKey())) {
            Intent gameBoosterIntent = getGameBoosterIntent();
            if (isIntentValid(gameBoosterIntent)) {
                this.mContext.startActivity(gameBoosterIntent);
                return true;
            }
            return true;
        }
        return super.handlePreferenceTreeClick(preference);
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
