package com.android.settings.development.featureflags;

import android.content.Context;
import android.util.FeatureFlagUtils;
import com.android.settingslib.miuisettings.preference.SwitchPreference;

/* loaded from: classes.dex */
public class FeatureFlagPreference extends SwitchPreference {
    private final boolean mIsPersistent;
    private final String mKey;

    public FeatureFlagPreference(Context context, String str) {
        super(context);
        this.mKey = str;
        setKey(str);
        setTitle(str);
        boolean isPersistent = FeatureFlagPersistent.isPersistent(str);
        this.mIsPersistent = isPersistent;
        super.setChecked(isPersistent ? FeatureFlagPersistent.isEnabled(context, str) : FeatureFlagUtils.isEnabled(context, str));
    }

    @Override // androidx.preference.TwoStatePreference
    public void setChecked(boolean z) {
        super.setChecked(z);
        if (this.mIsPersistent) {
            FeatureFlagPersistent.setEnabled(getContext(), this.mKey, z);
        } else {
            FeatureFlagUtils.setEnabled(getContext(), this.mKey, z);
        }
    }
}
