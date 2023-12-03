package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes2.dex */
public class LTRValuePreference extends ValuePreference {
    public LTRValuePreference(Context context) {
        super(context);
    }

    public LTRValuePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public LTRValuePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.android.settingslib.miuisettings.preference.ValuePreference, miuix.preference.TextPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(R.id.value_right);
        if (textView != null) {
            textView.setTextDirection(3);
        }
        super.onBindViewHolder(preferenceViewHolder);
    }

    @Override // com.android.settingslib.miuisettings.preference.ValuePreference
    public void setValue(String str) {
        super.setValue(str);
    }
}
