package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import miui.os.Build;
import miuix.preference.RadioButtonPreference;

/* loaded from: classes2.dex */
public class FixedSizeRadioButtonPreference extends RadioButtonPreference {
    public FixedSizeRadioButtonPreference(Context context) {
        super(context);
    }

    public FixedSizeRadioButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public FixedSizeRadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // miuix.preference.RadioButtonPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ImageView imageView = (ImageView) preferenceViewHolder.itemView.findViewById(16908294);
        if (imageView == null || Build.IS_TABLET) {
            return;
        }
        int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.miuix_preference_icon_min_width);
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.width = dimensionPixelSize;
            layoutParams.height = dimensionPixelSize;
        }
        imageView.setLayoutParams(layoutParams);
    }
}
