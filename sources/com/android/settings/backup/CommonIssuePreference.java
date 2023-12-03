package com.android.settings.backup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes.dex */
public class CommonIssuePreference extends Preference implements View.OnClickListener {
    TextView mContent;
    CheckBox mExpandContent;
    TextView mTitle;

    public CommonIssuePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CommonIssuePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public CommonIssuePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        this.mContent = (TextView) view.findViewById(16908304);
        this.mTitle = (TextView) view.findViewById(16908310);
        this.mContent.setVisibility(8);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.expand_content);
        this.mExpandContent = checkBox;
        this.mContent.setVisibility(checkBox.isChecked() ? 0 : 8);
        this.mExpandContent.setOnClickListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == R.id.expand_content) {
            if (this.mContent.getVisibility() == 8) {
                this.mContent.setVisibility(0);
            } else {
                this.mContent.setVisibility(8);
            }
        }
    }
}
