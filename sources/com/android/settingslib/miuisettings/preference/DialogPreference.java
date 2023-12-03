package com.android.settingslib.miuisettings.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.PreferenceManager;

/* loaded from: classes2.dex */
public class DialogPreference extends androidx.preference.DialogPreference implements PreferenceApiDiff {
    private PreferenceDelegate mDelegate;

    public DialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public DialogPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public DialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    private void init() {
        this.mDelegate = new PreferenceDelegate(this, this);
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
