package com.android.settingslib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.miuisettings.preference.PreferenceFeature;

/* loaded from: classes2.dex */
public class RadioButtonPreference extends com.android.settingslib.miuisettings.preference.RadioButtonPreference implements PreferenceFeature {
    private View mAppendix;
    private int mAppendixVisibility;
    private ImageView mExtraWidget;
    private View mExtraWidgetContainer;
    private View.OnClickListener mExtraWidgetOnClickListener;
    private OnClickListener mListener;

    /* loaded from: classes2.dex */
    public interface OnClickListener {
        void onRadioButtonClicked(RadioButtonPreference radioButtonPreference);
    }

    public RadioButtonPreference(Context context) {
        this(context, null);
    }

    public RadioButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mListener = null;
        this.mAppendixVisibility = -1;
    }

    public RadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mListener = null;
        this.mAppendixVisibility = -1;
    }

    public boolean hasIcon() {
        return true;
    }

    @Override // com.android.settingslib.miuisettings.preference.RadioButtonPreference, miuix.preference.RadioButtonPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mExtraWidget = (ImageView) preferenceViewHolder.findViewById(R$id.radio_extra_widget);
        this.mExtraWidgetContainer = preferenceViewHolder.findViewById(R$id.radio_extra_widget_container);
        setExtraWidgetOnClickListener(this.mExtraWidgetOnClickListener);
    }

    @Override // miuix.preference.RadioButtonPreference, androidx.preference.TwoStatePreference, androidx.preference.Preference
    public void onClick() {
        OnClickListener onClickListener = this.mListener;
        if (onClickListener != null) {
            onClickListener.onRadioButtonClicked(this);
        }
    }

    public void setAppendixVisibility(int i) {
        View view = this.mAppendix;
        if (view != null) {
            view.setVisibility(i);
        }
        this.mAppendixVisibility = i;
    }

    public void setExtraWidgetOnClickListener(View.OnClickListener onClickListener) {
        this.mExtraWidgetOnClickListener = onClickListener;
        ImageView imageView = this.mExtraWidget;
        if (imageView == null || this.mExtraWidgetContainer == null) {
            return;
        }
        imageView.setOnClickListener(onClickListener);
        this.mExtraWidgetContainer.setVisibility(this.mExtraWidgetOnClickListener != null ? 0 : 8);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mListener = onClickListener;
    }
}
