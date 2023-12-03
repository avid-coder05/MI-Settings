package com.android.settings.controlcenter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.utils.StatusBarUtils;
import miuix.os.Build;
import miuix.preference.R$id;
import miuix.preference.TextPreference;

/* loaded from: classes.dex */
public class ControlCenterPreference extends TextPreference {
    public ControlCenterPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setWidgetLayoutResource(R.layout.control_center_style_widget);
    }

    private int getWidgetDrawableRes() {
        return StatusBarUtils.isUseControlPanel(getContext()) ? R.drawable.ic_control_center_modern : Build.IS_INTERNATIONAL_BUILD ? R.drawable.ic_control_center_legacy_international : R.drawable.ic_control_center_legacy;
    }

    @Override // miuix.preference.TextPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(16908312);
        if (findViewById != null) {
            ((ViewGroup.MarginLayoutParams) findViewById.getLayoutParams()).setMarginEnd(0);
        }
        ImageView imageView = (ImageView) preferenceViewHolder.itemView.findViewById(R.id.control_center_style_icon);
        if (imageView != null) {
            imageView.setImageResource(getWidgetDrawableRes());
        }
        ImageView imageView2 = (ImageView) preferenceViewHolder.itemView.findViewById(R$id.arrow_right);
        if (imageView2 != null) {
            imageView2.setImageDrawable(null);
            ((ViewGroup.MarginLayoutParams) imageView2.getLayoutParams()).setMarginStart(0);
        }
    }
}
