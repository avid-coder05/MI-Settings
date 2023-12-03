package com.android.settingslib;

import android.content.Context;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.RestrictedLockUtils;

/* loaded from: classes2.dex */
public class RestrictedPreference extends com.android.settingslib.widget.TwoTargetPreference {
    RestrictedPreferenceHelper mHelper;
    private CharSequence mRightValue;
    private boolean mShowRightArrow;
    private boolean mSummary2Value;

    public RestrictedPreference(Context context) {
        super(context);
        this.mShowRightArrow = true;
        this.mHelper = new RestrictedPreferenceHelper(context, this, null);
    }

    public RestrictedPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mShowRightArrow = true;
        this.mHelper = new RestrictedPreferenceHelper(context, this, attributeSet);
    }

    public RestrictedPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mShowRightArrow = true;
        this.mHelper = new RestrictedPreferenceHelper(context, this, attributeSet);
    }

    public RestrictedPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mShowRightArrow = true;
        this.mHelper = new RestrictedPreferenceHelper(context, this, attributeSet);
    }

    public RestrictedPreference(Context context, AttributeSet attributeSet, int i, int i2, boolean z) {
        super(context, attributeSet, i, i2, z);
        this.mShowRightArrow = true;
        this.mHelper = new RestrictedPreferenceHelper(context, this, attributeSet);
    }

    public RestrictedPreference(Context context, AttributeSet attributeSet, int i, boolean z) {
        super(context, attributeSet, i, z);
        this.mShowRightArrow = true;
        this.mHelper = new RestrictedPreferenceHelper(context, this, attributeSet);
    }

    public RestrictedPreference(Context context, AttributeSet attributeSet, boolean z) {
        super(context, attributeSet, z);
        this.mShowRightArrow = true;
        this.mHelper = new RestrictedPreferenceHelper(context, this, attributeSet);
    }

    public RestrictedPreference(Context context, boolean z) {
        super(context, z);
        this.mShowRightArrow = true;
        this.mHelper = new RestrictedPreferenceHelper(context, this, null);
    }

    public void checkRestrictionAndSetDisabled(String str) {
        this.mHelper.checkRestrictionAndSetDisabled(str, UserHandle.myUserId());
    }

    public void checkRestrictionAndSetDisabled(String str, int i) {
        this.mHelper.checkRestrictionAndSetDisabled(str, i);
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference
    protected int getSecondTargetResId() {
        return R$layout.restricted_icon;
    }

    public CharSequence getValue() {
        return this.mRightValue;
    }

    public boolean isDisabledByAdmin() {
        return this.mHelper.isDisabledByAdmin();
    }

    public boolean isSummary2Value() {
        return this.mSummary2Value;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        this.mHelper.onAttachedToHierarchy();
        super.onAttachedToHierarchy(preferenceManager);
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference, com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mHelper.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(R$id.restricted_icon);
        if (findViewById != null) {
            findViewById.setVisibility(isDisabledByAdmin() ? 0 : 8);
        }
        View view = preferenceViewHolder.itemView;
        View findViewById2 = view.findViewById(R$id.arrow_right);
        if (findViewById2 != null) {
            findViewById2.setVisibility((isDisabledByAdmin() || !this.mShowRightArrow) ? 8 : 0);
        }
        TextView textView = (TextView) view.findViewById(R$id.text_right);
        TextView textView2 = (TextView) view.findViewById(16908304);
        if (textView == null || !this.mSummary2Value) {
            if (textView == null || TextUtils.isEmpty(this.mRightValue)) {
                return;
            }
            textView.setText(this.mRightValue);
            return;
        }
        CharSequence summary = getSummary();
        if (TextUtils.isEmpty(summary)) {
            textView.setVisibility(8);
        } else {
            textView.setText(summary);
            textView.setVisibility(0);
        }
        if (textView2 != null) {
            textView2.setVisibility(8);
        }
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
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

    @Override // com.android.settingslib.miuisettings.preference.Preference
    public void setShowRightArrow(boolean z) {
        this.mShowRightArrow = z;
    }

    public void setSummary2Value(boolean z) {
        this.mSummary2Value = z;
    }

    public void setValue(CharSequence charSequence) {
        this.mRightValue = charSequence;
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference
    protected boolean shouldHideSecondTarget() {
        return !isDisabledByAdmin();
    }

    public void useAdminDisabledSummary(boolean z) {
        this.mHelper.useAdminDisabledSummary(z);
    }
}
