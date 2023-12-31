package com.android.settings.widget;

import android.content.Context;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.RestrictedPreferenceHelper;

/* loaded from: classes2.dex */
public class RestrictedAppPreference extends AppPreference {
    private RestrictedPreferenceHelper mHelper;
    private String userRestriction;

    public RestrictedAppPreference(Context context, String str) {
        super(context);
        initialize(null, str);
    }

    private void initialize(AttributeSet attributeSet, String str) {
        setWidgetLayoutResource(R.layout.restricted_icon);
        this.mHelper = new RestrictedPreferenceHelper(getContext(), this, attributeSet);
        this.userRestriction = str;
    }

    public void checkRestrictionAndSetDisabled() {
        if (TextUtils.isEmpty(this.userRestriction)) {
            return;
        }
        this.mHelper.checkRestrictionAndSetDisabled(this.userRestriction, UserHandle.myUserId());
    }

    public boolean isDisabledByAdmin() {
        return this.mHelper.isDisabledByAdmin();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        this.mHelper.onAttachedToHierarchy();
        super.onAttachedToHierarchy(preferenceManager);
    }

    @Override // com.android.settings.widget.AppPreference, com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mHelper.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(R.id.restricted_icon);
        if (findViewById != null) {
            findViewById.setVisibility(isDisabledByAdmin() ? 0 : 8);
        }
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void performClick() {
        if (this.mHelper.performClick()) {
            return;
        }
        super.performClick();
    }

    @Override // androidx.preference.Preference
    public void setEnabled(boolean z) {
        if (isDisabledByAdmin() && z) {
            return;
        }
        super.setEnabled(z);
    }
}
