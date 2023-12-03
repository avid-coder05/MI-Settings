package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.widget.TwoTargetPreference;

/* loaded from: classes2.dex */
public class PrimaryCheckBoxPreference extends TwoTargetPreference {
    private CheckBox mCheckBox;
    private boolean mChecked;
    private boolean mEnableCheckBox;

    public PrimaryCheckBoxPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mEnableCheckBox = true;
    }

    public PrimaryCheckBoxPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mEnableCheckBox = true;
    }

    public PrimaryCheckBoxPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mEnableCheckBox = true;
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference
    protected int getSecondTargetResId() {
        return R.layout.preference_widget_primary_checkbox;
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference, com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(16908312);
        if (findViewById != null) {
            findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.PrimaryCheckBoxPreference.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (PrimaryCheckBoxPreference.this.mCheckBox == null || PrimaryCheckBoxPreference.this.mCheckBox.isEnabled()) {
                        PrimaryCheckBoxPreference.this.setChecked(!r2.mChecked);
                        PrimaryCheckBoxPreference primaryCheckBoxPreference = PrimaryCheckBoxPreference.this;
                        if (primaryCheckBoxPreference.callChangeListener(Boolean.valueOf(primaryCheckBoxPreference.mChecked))) {
                            PrimaryCheckBoxPreference primaryCheckBoxPreference2 = PrimaryCheckBoxPreference.this;
                            primaryCheckBoxPreference2.persistBoolean(primaryCheckBoxPreference2.mChecked);
                            return;
                        }
                        PrimaryCheckBoxPreference.this.setChecked(!r1.mChecked);
                    }
                }
            });
        }
        CheckBox checkBox = (CheckBox) preferenceViewHolder.findViewById(R.id.checkboxWidget);
        this.mCheckBox = checkBox;
        if (checkBox != null) {
            checkBox.setContentDescription(getTitle());
            this.mCheckBox.setChecked(this.mChecked);
            this.mCheckBox.setEnabled(this.mEnableCheckBox);
        }
    }

    public void setCheckBoxEnabled(boolean z) {
        this.mEnableCheckBox = z;
        CheckBox checkBox = this.mCheckBox;
        if (checkBox != null) {
            checkBox.setEnabled(z);
        }
    }

    public void setChecked(boolean z) {
        this.mChecked = z;
        CheckBox checkBox = this.mCheckBox;
        if (checkBox != null) {
            checkBox.setChecked(z);
        }
    }

    @Override // androidx.preference.Preference
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        setCheckBoxEnabled(z);
    }
}
