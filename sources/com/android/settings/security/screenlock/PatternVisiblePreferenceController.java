package com.android.settings.security.screenlock;

import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.TwoStatePreference;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

/* loaded from: classes2.dex */
public class PatternVisiblePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private final LockPatternUtils mLockPatternUtils;
    private final int mUserId;

    public PatternVisiblePreferenceController(Context context, int i, LockPatternUtils lockPatternUtils) {
        super(context);
        this.mUserId = i;
        this.mLockPatternUtils = lockPatternUtils;
    }

    private boolean isPatternLock() {
        return this.mLockPatternUtils.isSecure(this.mUserId) && this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mUserId) == 65536;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "visiblepattern";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return isPatternLock();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        this.mLockPatternUtils.setVisiblePatternEnabled(((Boolean) obj).booleanValue(), this.mUserId);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((TwoStatePreference) preference).setChecked(this.mLockPatternUtils.isVisiblePatternEnabled(this.mUserId));
    }
}
