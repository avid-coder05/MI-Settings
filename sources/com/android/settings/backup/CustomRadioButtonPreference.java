package com.android.settings.backup;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import miuix.preference.R$attr;
import miuix.preference.RadioButtonPreference;

/* loaded from: classes.dex */
public class CustomRadioButtonPreference extends RadioButtonPreference {
    private Drawable icon;
    private RadioButton radioButton;
    private View view;

    public CustomRadioButtonPreference(Context context) {
        super(context);
    }

    public CustomRadioButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, R$attr.radioButtonPreferenceStyle);
    }

    public CustomRadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private void setInternalItemIcon(Drawable drawable) {
        this.icon = drawable;
        if (this.radioButton == null || drawable == null) {
            return;
        }
        Resources resources = getContext().getResources();
        int i = R.dimen.icon_size;
        drawable.setBounds(0, 0, resources.getDimensionPixelSize(i), getContext().getResources().getDimensionPixelSize(i));
        this.radioButton.setCompoundDrawables(this.icon, null, null, null);
        this.radioButton.setEnabled(true);
        this.radioButton.setButtonDrawable(17170445);
    }

    @Override // miuix.preference.RadioButtonPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        this.view = view;
        RadioButton radioButton = (RadioButton) view.findViewById(16908289);
        this.radioButton = radioButton;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) radioButton.getLayoutParams();
        layoutParams.rightMargin = getContext().getResources().getDimensionPixelSize(R.dimen.backup_item_icon_margin_right);
        this.radioButton.setLayoutParams(layoutParams);
        setInternalItemIcon(this.icon);
    }

    public void setCustomItemIcon(Drawable drawable) {
        setInternalItemIcon(drawable);
    }
}
