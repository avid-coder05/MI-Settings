package com.android.settings;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes.dex */
public class MiuiValuePreference extends ValuePreference {
    private boolean mShowSummary;

    public MiuiValuePreference(Context context) {
        this(context, null);
    }

    public MiuiValuePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mShowSummary = false;
    }

    public MiuiValuePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mShowSummary = false;
    }

    @Override // com.android.settingslib.miuisettings.preference.ValuePreference, miuix.preference.TextPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        if (this.mShowSummary) {
            return;
        }
        TextView textView = (TextView) view.findViewById(16908304);
        TextView textView2 = (TextView) view.findViewById(R.id.text_right);
        CharSequence summary = getSummary();
        if (textView2 != null) {
            if (TextUtils.isEmpty(summary)) {
                textView2.setVisibility(8);
            } else {
                textView2.setText(summary);
                textView2.setVisibility(0);
            }
        }
        if (textView != null) {
            textView.setVisibility(8);
        }
    }
}
