package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
import com.android.settingslib.miuisettings.preference.CheckBoxPreference;

/* loaded from: classes2.dex */
public class TogglePreference extends CheckBoxPreference {
    private OnBeforeCheckedChangeListener mOnBeforeListener;

    /* loaded from: classes2.dex */
    public interface OnBeforeCheckedChangeListener {
        boolean onBeforeCheckedChanged(TogglePreference togglePreference, boolean z);
    }

    public TogglePreference(Context context) {
        super(context);
    }

    public TogglePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public TogglePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public TogglePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    @Override // androidx.preference.TwoStatePreference
    public void setChecked(boolean z) {
        super.setChecked(z);
    }

    public void setCheckedInternal(boolean z) {
        super.setChecked(z);
    }

    public void setOnBeforeCheckedChangeListener(OnBeforeCheckedChangeListener onBeforeCheckedChangeListener) {
        this.mOnBeforeListener = onBeforeCheckedChangeListener;
        setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.widget.TogglePreference.1
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                TogglePreference.this.mOnBeforeListener.onBeforeCheckedChanged(TogglePreference.this, ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }
}
