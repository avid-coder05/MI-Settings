package com.android.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.Preference;

/* loaded from: classes.dex */
public class MiuiProgressCategory extends MiuiProgressCategoryBase {
    private int mEmptyTextRes;
    private boolean mNoDeviceFoundAdded;
    private Preference mNoDeviceFoundPreference;
    private boolean mProgress;

    public MiuiProgressCategory(Context context) {
        super(context);
        this.mProgress = false;
        setLayoutResource(R.layout.preference_progress_category);
    }

    public MiuiProgressCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mProgress = false;
        setLayoutResource(R.layout.preference_progress_category);
    }

    public MiuiProgressCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mProgress = false;
        setLayoutResource(R.layout.preference_progress_category);
    }

    public MiuiProgressCategory(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mProgress = false;
        setLayoutResource(R.layout.preference_progress_category);
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceCategory, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        View findViewById = view.findViewById(R.id.scanning_progress);
        boolean z = getPreferenceCount() == 0 || (getPreferenceCount() == 1 && getPreference(0) == this.mNoDeviceFoundPreference);
        findViewById.setVisibility(this.mProgress ? 0 : 8);
        if (this.mProgress || !z) {
            if (this.mNoDeviceFoundAdded) {
                removePreference(this.mNoDeviceFoundPreference);
                this.mNoDeviceFoundAdded = false;
            }
        } else if (this.mNoDeviceFoundAdded) {
        } else {
            if (this.mNoDeviceFoundPreference == null) {
                Preference preference = new Preference(getContext());
                this.mNoDeviceFoundPreference = preference;
                preference.setLayoutResource(R.layout.preference_empty_list);
                this.mNoDeviceFoundPreference.setTitle(this.mEmptyTextRes);
                this.mNoDeviceFoundPreference.setSelectable(false);
            }
            addPreference(this.mNoDeviceFoundPreference);
            this.mNoDeviceFoundAdded = true;
        }
    }

    public void setEmptyTextRes(int i) {
        this.mEmptyTextRes = i;
    }

    public void setProgress(boolean z) {
        this.mProgress = z;
        notifyChanged();
    }
}
