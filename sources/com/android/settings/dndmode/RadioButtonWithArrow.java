package com.android.settings.dndmode;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.RadioButtonPreference;

/* loaded from: classes.dex */
public class RadioButtonWithArrow extends RadioButtonPreference implements View.OnClickListener {
    private ImageView arrow;
    private int arrowVisibility;
    private View.OnClickListener clickListener;

    public RadioButtonWithArrow(Context context) {
        super(context, null);
        this.arrowVisibility = -1;
    }

    public RadioButtonWithArrow(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.arrowVisibility = -1;
    }

    @Override // com.android.settingslib.miuisettings.preference.RadioButtonPreference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        this.arrow = (ImageView) view.findViewById(R.id.right_arrow);
        String str = ((Object) getTitle()) + getContext().getResources().getString(R.string.accessibility_more_settings);
        ImageView imageView = this.arrow;
        if (imageView != null) {
            imageView.setContentDescription(str);
            int i = this.arrowVisibility;
            if (i == -1) {
                this.arrow.setOnClickListener(this);
            } else {
                this.arrow.setVisibility(i);
            }
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.clickListener != null) {
            view.setTag(getKey());
            this.clickListener.onClick(view);
        }
    }

    public void setArrowVisibility(int i) {
        this.arrowVisibility = i;
        ImageView imageView = this.arrow;
        if (imageView != null) {
            imageView.setVisibility(i);
        }
    }

    public void setOnClickListeners(View.OnClickListener onClickListener) {
        this.clickListener = onClickListener;
    }
}
