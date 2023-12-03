package com.android.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.privacypassword.ModifyAndInstructionPrivacyPassword;
import com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl;
import com.android.settings.privacypassword.PrivacyPasswordManager;
import com.android.settings.privacypassword.PrivacyPasswordSetting;
import com.android.settings.privacypassword.PrivacyPasswordUtils;
import com.android.settings.privacypassword.TransparentHelper;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes.dex */
public class PrivacyPasswordUnlockStateController extends BasePreferenceController {
    private static final int REQUEST_CONFIRM_PRIVACY_PASSWORD = 290224;
    private Context mContext;
    private String mPreferenceKey;

    public PrivacyPasswordUnlockStateController(Context context, String str) {
        super(context, str);
        this.mContext = context;
        this.mPreferenceKey = str;
    }

    private void handleClick() {
        if (!PrivacyPasswordUtils.isFoldInternalScreen(this.mContext)) {
            Intent intent = new Intent(this.mContext, TransparentHelper.class);
            intent.putExtra("enter_from_settings", true);
            this.mContext.startActivity(intent);
        } else if (!PrivacyPasswordManager.getInstance(this.mContext).havePattern()) {
            this.mContext.startActivity(new Intent(this.mContext, PrivacyPasswordSetting.class));
        } else {
            Intent intent2 = new Intent(this.mContext, PrivacyPasswordConfirmAccessControl.class);
            intent2.putExtra("enter_from_settings", true);
            ((Activity) this.mContext).startActivityForResult(intent2, REQUEST_CONFIRM_PRIVACY_PASSWORD);
        }
    }

    private void updateOpenState(Preference preference, boolean z) {
        if (z) {
            preference.setSummary(R.string.on);
        } else {
            preference.setSummary(R.string.off);
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public boolean handleActivityResult(int i, int i2) {
        if (i == REQUEST_CONFIRM_PRIVACY_PASSWORD && i2 == -1) {
            this.mContext.startActivity(new Intent(this.mContext, ModifyAndInstructionPrivacyPassword.class));
            return true;
        }
        return false;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (this.mPreferenceKey.equals(preference.getKey())) {
            handleClick();
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

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mPreferenceKey.equals(preference.getKey())) {
            if ("privacy_password".equals(this.mPreferenceKey)) {
                updateOpenState(preference, PrivacyPasswordManager.getInstance(this.mContext).havePattern());
            }
            super.updateState(preference);
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
