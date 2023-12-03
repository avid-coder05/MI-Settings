package com.android.settings.widget;

import android.view.View;
import android.widget.ImageView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.widget.RadioButtonPreference;

/* loaded from: classes2.dex */
public class RadioButtonPreferenceWithExtraWidget extends RadioButtonPreference {
    private ImageView mExtraWidget;
    private View mExtraWidgetDivider;
    private View.OnClickListener mExtraWidgetOnClickListener;
    private int mExtraWidgetVisibility;

    @Override // com.android.settingslib.widget.RadioButtonPreference, com.android.settingslib.miuisettings.preference.RadioButtonPreference, miuix.preference.RadioButtonPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mExtraWidget = (ImageView) preferenceViewHolder.findViewById(R.id.radio_extra_widget);
        this.mExtraWidgetDivider = preferenceViewHolder.findViewById(R.id.radio_extra_widget_divider);
        setExtraWidgetVisibility(this.mExtraWidgetVisibility);
        View.OnClickListener onClickListener = this.mExtraWidgetOnClickListener;
        if (onClickListener != null) {
            setExtraWidgetOnClickListener(onClickListener);
        }
    }

    @Override // com.android.settingslib.widget.RadioButtonPreference
    public void setExtraWidgetOnClickListener(View.OnClickListener onClickListener) {
        this.mExtraWidgetOnClickListener = onClickListener;
        ImageView imageView = this.mExtraWidget;
        if (imageView != null) {
            imageView.setEnabled(true);
            this.mExtraWidget.setOnClickListener(onClickListener);
        }
    }

    public void setExtraWidgetVisibility(int i) {
        this.mExtraWidgetVisibility = i;
        ImageView imageView = this.mExtraWidget;
        if (imageView == null || this.mExtraWidgetDivider == null) {
            return;
        }
        if (i == 0) {
            imageView.setClickable(false);
            this.mExtraWidget.setVisibility(8);
            this.mExtraWidgetDivider.setVisibility(8);
            return;
        }
        imageView.setClickable(true);
        this.mExtraWidget.setVisibility(0);
        this.mExtraWidgetDivider.setVisibility(0);
        int i2 = this.mExtraWidgetVisibility;
        if (i2 == 1) {
            this.mExtraWidget.setImageResource(R.drawable.ic_settings_about);
            this.mExtraWidget.setContentDescription(getContext().getResources().getText(R.string.information_label));
        } else if (i2 == 2) {
            this.mExtraWidget.setImageResource(R.drawable.ic_settings_accent);
            this.mExtraWidget.setContentDescription(getContext().getResources().getText(R.string.settings_label));
        }
    }
}
