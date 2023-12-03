package com.android.settings;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes.dex */
public class MiuiSummaryPreference extends Preference {
    private boolean mChartEnabled;
    private String mEndLabel;
    private float mLeftRatio;
    private float mMiddleRatio;
    private String mStartLabel;

    public MiuiSummaryPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mChartEnabled = true;
        setLayoutResource(R.layout.settings_summary_preference);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.color_bar);
        if (this.mChartEnabled) {
            progressBar.setVisibility(0);
            int i = (int) (this.mLeftRatio * 100.0f);
            progressBar.setProgress(i);
            progressBar.setSecondaryProgress(i + ((int) (this.mMiddleRatio * 100.0f)));
        } else {
            progressBar.setVisibility(8);
        }
        if (!this.mChartEnabled || (TextUtils.isEmpty(this.mStartLabel) && TextUtils.isEmpty(this.mEndLabel))) {
            view.findViewById(R.id.label_bar).setVisibility(8);
            return;
        }
        view.findViewById(R.id.label_bar).setVisibility(0);
        ((TextView) view.findViewById(16908308)).setText(this.mStartLabel);
        ((TextView) view.findViewById(16908309)).setText(this.mEndLabel);
    }
}
