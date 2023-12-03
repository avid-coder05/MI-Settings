package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CheckedTextView;
import androidx.preference.PreferenceViewHolder;
import miuix.preference.R$attr;
import miuix.preference.RadioButtonPreference;

/* loaded from: classes2.dex */
public class CustomRadioButtonPreference extends RadioButtonPreference {
    private CheckedTextView mSummaryCheckedTextView;

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
        CheckedTextView checkedTextView = (CheckedTextView) preferenceViewHolder.itemView.findViewById(16908304);
        this.mSummaryCheckedTextView = checkedTextView;
        if (checkedTextView != null) {
            checkedTextView.setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: com.android.settings.widget.CustomRadioButtonPreference.1
                @Override // android.view.View.AccessibilityDelegate
                public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                    super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                    accessibilityNodeInfo.setCheckable(false);
                }
            });
        }
    }
}
