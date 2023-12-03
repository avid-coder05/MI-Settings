package com.android.settingslib.miuisettings.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import androidx.preference.Preference;

/* loaded from: classes2.dex */
public class ButtonPreference extends Preference {
    private Preference.OnPreferenceClickListener mListener;
    private String mText;

    public ButtonPreference(Context context) {
        super(context);
    }

    public ButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        view.setBackground(null);
        Button button = (Button) view.findViewById(16908313);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settingslib.miuisettings.preference.ButtonPreference.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    if (ButtonPreference.this.mListener != null) {
                        ButtonPreference.this.mListener.onPreferenceClick(ButtonPreference.this);
                    }
                }
            });
            button.setText(this.mText);
        }
    }

    @Override // androidx.preference.Preference
    public void setOnPreferenceClickListener(Preference.OnPreferenceClickListener onPreferenceClickListener) {
        this.mListener = onPreferenceClickListener;
    }

    public void setText(int i) {
        setText(getContext().getString(i));
    }

    public void setText(String str) {
        this.mText = str;
        notifyChanged();
    }
}
