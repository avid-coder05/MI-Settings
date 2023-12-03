package com.android.settings.display;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.android.settings.R;

/* loaded from: classes.dex */
public class PaperModePreference extends com.android.settingslib.miuisettings.preference.RadioButtonPreference implements View.OnClickListener {
    private OnRightArrowClickListener mRightArrowClickListener;
    private boolean mShowRightArrow;

    /* loaded from: classes.dex */
    public interface OnRightArrowClickListener {
        void onRightArrowClick(com.android.settingslib.miuisettings.preference.RadioButtonPreference radioButtonPreference);
    }

    public PaperModePreference(Context context) {
        this(context, null);
    }

    public PaperModePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mRightArrowClickListener = null;
        setWidgetLayoutResource(R.layout.preference_widget_detail);
    }

    @Override // com.android.settingslib.miuisettings.preference.RadioButtonPreference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        ImageView imageView = (ImageView) view.findViewById(R.id.detail_arrow);
        if (imageView == null) {
            return;
        }
        imageView.setOnClickListener(this);
        imageView.setVisibility(this.mShowRightArrow ? 0 : 8);
        imageView.setContentDescription(((Object) getTitle()) + getContext().getResources().getString(R.string.accessibility_more_settings));
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        OnRightArrowClickListener onRightArrowClickListener = this.mRightArrowClickListener;
        if (onRightArrowClickListener != null) {
            onRightArrowClickListener.onRightArrowClick(this);
        }
    }

    public void setOnRightArrowClickListener(OnRightArrowClickListener onRightArrowClickListener) {
        this.mRightArrowClickListener = onRightArrowClickListener;
    }

    public void setShowRightArrow(boolean z) {
        this.mShowRightArrow = z;
    }
}
