package com.android.settingslib.miuisettings.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.PreferenceManager;

/* loaded from: classes2.dex */
public class CheckBoxPreference extends androidx.preference.CheckBoxPreference implements PreferenceApiDiff {
    private PreferenceDelegate mDelegate;

    public CheckBoxPreference(Context context) {
        super(context);
        init(null);
    }

    public CheckBoxPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet);
    }

    public CheckBoxPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(attributeSet);
    }

    public CheckBoxPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(attributeSet);
    }

    private void init(AttributeSet attributeSet) {
        this.mDelegate = new PreferenceDelegate(this, this, attributeSet != null ? attributeSet.getAttributeBooleanValue("http://schemas.android.com/apk/miuisettings", "showIcon", false) : false);
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        this.mDelegate.onAttachedToHierarchy(preferenceManager);
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
    }
}
