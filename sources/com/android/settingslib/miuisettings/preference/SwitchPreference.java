package com.android.settingslib.miuisettings.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

/* loaded from: classes2.dex */
public class SwitchPreference extends androidx.preference.SwitchPreference implements PreferenceApiDiff {
    private PreferenceDelegate mDelegate;

    public SwitchPreference(Context context) {
        super(context);
        init(context, null);
    }

    public SwitchPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    public SwitchPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet);
    }

    public SwitchPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
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

    @Override // androidx.preference.SwitchPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        this.mDelegate.onBindViewStart(preferenceViewHolder.itemView);
        super.onBindViewHolder(preferenceViewHolder);
        this.mDelegate.onBindViewEnd(preferenceViewHolder.itemView);
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
    }
}
