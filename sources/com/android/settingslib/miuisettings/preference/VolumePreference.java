package com.android.settingslib.miuisettings.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

/* loaded from: classes2.dex */
public class VolumePreference extends SeekBarDialogPreference {
    private PreferenceDelegate mDelegate;

    public VolumePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public VolumePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public VolumePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    private void init() {
        this.mDelegate = new PreferenceDelegate(this, this);
    }

    @Override // com.android.settingslib.miuisettings.preference.SeekBarDialogPreference, com.android.settingslib.miuisettings.preference.DialogPreference, androidx.preference.Preference
    public void onAttached() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.miuisettings.preference.SeekBarDialogPreference, com.android.settingslib.miuisettings.preference.DialogPreference, androidx.preference.Preference
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        this.mDelegate.onAttachedToHierarchy(preferenceManager);
    }

    @Override // com.android.settingslib.miuisettings.preference.SeekBarDialogPreference, com.android.settingslib.miuisettings.preference.DialogPreference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        this.mDelegate.onBindViewStart(view);
        super.onBindView(view);
        this.mDelegate.onBindViewEnd(view);
    }

    @Override // com.android.settingslib.miuisettings.preference.SeekBarDialogPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
    }

    @Override // com.android.settingslib.miuisettings.preference.SeekBarDialogPreference, com.android.settingslib.miuisettings.preference.DialogPreference, androidx.preference.Preference
    public void onDetached() {
    }
}
