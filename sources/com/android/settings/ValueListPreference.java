package com.android.settings;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/* loaded from: classes.dex */
public class ValueListPreference extends CustomListPreference {
    private String mRightValue;

    public ValueListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(R.layout.miuix_preference_layout);
    }

    @Override // com.android.settingslib.miuisettings.preference.ListPreference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        TextView textView = (TextView) view.findViewById(R.id.value_right);
        if (textView != null) {
            if (TextUtils.isEmpty(this.mRightValue)) {
                textView.setVisibility(8);
            } else {
                textView.setText(this.mRightValue);
                textView.setVisibility(0);
            }
        }
        TextView textView2 = (TextView) view.findViewById(16908304);
        CharSequence summary = getSummary();
        if (textView2 != null) {
            if (TextUtils.isEmpty(summary)) {
                textView2.setVisibility(8);
            } else {
                textView2.setVisibility(0);
            }
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.arrow_right);
        if (imageView != null) {
            imageView.setVisibility(0);
        }
    }
}
