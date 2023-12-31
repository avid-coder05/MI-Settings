package com.android.settingslib;

import android.content.Context;
import android.os.UserHandle;
import android.util.AttributeSet;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.RestrictedLockUtils;

/* loaded from: classes2.dex */
public class RestrictedTopLevelPreference extends Preference {
    private RestrictedPreferenceHelper mHelper;

    public RestrictedTopLevelPreference(Context context) {
        this(context, null);
    }

    public RestrictedTopLevelPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, TypedArrayUtils.getAttr(context, R$attr.preferenceStyle, 16842894));
    }

    public RestrictedTopLevelPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public RestrictedTopLevelPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mHelper = new RestrictedPreferenceHelper(context, this, attributeSet);
    }

    public void checkRestrictionAndSetDisabled(String str) {
        this.mHelper.checkRestrictionAndSetDisabled(str, UserHandle.myUserId());
    }

    public boolean isDisabledByAdmin() {
        return this.mHelper.isDisabledByAdmin();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        this.mHelper.onAttachedToHierarchy();
        super.onAttachedToHierarchy(preferenceManager);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mHelper.onBindViewHolder(preferenceViewHolder);
    }

    @Override // androidx.preference.Preference
    public void performClick() {
        if (this.mHelper.performClick()) {
            return;
        }
        super.performClick();
    }

    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        if (this.mHelper.setDisabledByAdmin(enforcedAdmin)) {
            notifyChanged();
        }
    }

    @Override // androidx.preference.Preference
    public void setEnabled(boolean z) {
        if (z && isDisabledByAdmin()) {
            this.mHelper.setDisabledByAdmin(null);
        } else {
            super.setEnabled(z);
        }
    }
}
