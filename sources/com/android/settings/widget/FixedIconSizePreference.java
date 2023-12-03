package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;
import miui.os.Build;

/* loaded from: classes2.dex */
public class FixedIconSizePreference extends Preference {
    public FixedIconSizePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public FixedIconSizePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public FixedIconSizePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public FixedIconSizePreference(Context context, boolean z) {
        super(context, z);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ImageView imageView = (ImageView) preferenceViewHolder.itemView.findViewById(16908294);
        if (Build.IS_TABLET) {
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
