package com.android.settings.accessibility.haptic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import androidx.preference.PreferenceViewHolder;
import miuix.preference.R$attr;
import miuix.preference.RadioButtonPreference;

/* loaded from: classes.dex */
public class CustomRadioButtonPreference extends RadioButtonPreference {
    private RadioButton radioButton;
    private View view;

    public CustomRadioButtonPreference(Context context) {
        super(context);
    }

    public CustomRadioButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, R$attr.radioButtonPreferenceStyle);
    }

    public CustomRadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // miuix.preference.RadioButtonPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        this.view = view;
        RadioButton radioButton = (RadioButton) view.findViewById(16908289);
        this.radioButton = radioButton;
        if (radioButton != null) {
            radioButton.setEnabled(true);
            this.radioButton.setButtonDrawable(17170445);
        }
    }
}
