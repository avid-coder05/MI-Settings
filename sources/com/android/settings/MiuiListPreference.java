package com.android.settings;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/* loaded from: classes.dex */
public class MiuiListPreference extends CustomListPreference {
    public MiuiListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MiuiListPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.android.settingslib.miuisettings.preference.ListPreference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        TextView textView = (TextView) view.findViewById(16908304);
        TextView textView2 = (TextView) view.findViewById(R.id.value_right);
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
