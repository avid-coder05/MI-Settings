package com.android.settingslib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.miuisettings.preference.Preference;
import com.android.settingslib.miuisettings.preference.PreferenceFeature;

/* loaded from: classes2.dex */
public class TwoTargetPreference extends Preference implements PreferenceFeature {
    private boolean mHasIcon;
    private int mIconSize;
    private int mMediumIconSize;
    private int mSmallIconSize;

    public TwoTargetPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mHasIcon = false;
        init(context);
    }

    public TwoTargetPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mHasIcon = false;
        init(context);
    }

    public TwoTargetPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mHasIcon = false;
        init(context);
    }

    private void init(Context context) {
        this.mSmallIconSize = context.getResources().getDimensionPixelSize(R$dimen.two_target_pref_small_icon_size);
        this.mMediumIconSize = context.getResources().getDimensionPixelSize(R$dimen.two_target_pref_medium_icon_size);
        int secondTargetResId = getSecondTargetResId();
        if (secondTargetResId != 0) {
            setWidgetLayoutResource(secondTargetResId);
        }
    }

    protected int getSecondTargetResId() {
        return 0;
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFeature
    public boolean hasIcon() {
        return this.mHasIcon;
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ImageView imageView = (ImageView) preferenceViewHolder.itemView.findViewById(16908294);
        if (imageView != null) {
            int i = this.mIconSize;
            if (i == 1) {
                int i2 = this.mMediumIconSize;
                imageView.setLayoutParams(new LinearLayout.LayoutParams(i2, i2));
            } else if (i == 2) {
                int i3 = this.mSmallIconSize;
                imageView.setLayoutParams(new LinearLayout.LayoutParams(i3, i3));
            }
        }
        View findViewById = preferenceViewHolder.findViewById(R$id.two_target_divider);
        View findViewById2 = preferenceViewHolder.findViewById(16908312);
        boolean shouldHideSecondTarget = shouldHideSecondTarget();
        if (findViewById != null) {
            findViewById.setVisibility(shouldHideSecondTarget ? 8 : 0);
        }
        if (findViewById2 != null) {
            findViewById2.setVisibility(shouldHideSecondTarget ? 8 : 0);
        }
    }

    protected boolean shouldHideSecondTarget() {
        return getSecondTargetResId() == 0;
    }
}
