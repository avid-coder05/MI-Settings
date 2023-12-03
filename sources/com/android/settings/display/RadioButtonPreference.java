package com.android.settings.display;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Checkable;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.settings.R;

/* loaded from: classes.dex */
public class RadioButtonPreference extends com.android.settingslib.miuisettings.preference.RadioButtonPreference {
    private ColorStateList mColorStateList;
    private ColorStateList mDefaultColorStateList;

    public RadioButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public RadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private ColorStateList generateTitleColorStateList() {
        Resources resources = getContext().getResources();
        Resources.Theme theme = getContext().getTheme();
        TypedValue typedValue = new TypedValue();
        int[][] iArr = {new int[]{16842912}, new int[1]};
        theme.resolveAttribute(R.attr.preferencePrimaryTextColor, typedValue, true);
        return new ColorStateList(iArr, new int[]{resources.getColor(typedValue.resourceId), resources.getColor(typedValue.resourceId)});
    }

    protected void init() {
        this.mColorStateList = generateTitleColorStateList();
        setLayoutResource(R.layout.preference_radiobutton);
        setWidgetLayoutResource(0);
    }

    @Override // com.android.settingslib.miuisettings.preference.RadioButtonPreference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        int i;
        super.onBindView(view);
        int layoutDirectionFromLocale = TextUtils.getLayoutDirectionFromLocale(getContext().getResources().getConfiguration().locale);
        TextView textView = (TextView) view.findViewById(16908310);
        View findViewById = view.findViewById(16908304);
        if (findViewById instanceof Checkable) {
            ((Checkable) findViewById).setChecked(isChecked());
        }
        if (textView != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();
            layoutParams.addRule(layoutDirectionFromLocale == 0 ? 1 : 0, 16908289);
            textView.setLayoutParams(layoutParams);
            if (this.mDefaultColorStateList == null) {
                this.mDefaultColorStateList = textView.getTextColors();
            }
            i = Color.alpha(this.mDefaultColorStateList.getColorForState(textView.getDrawableState(), 0));
            boolean isEnabled = isEnabled();
            ColorStateList colorStateList = this.mColorStateList;
            if (!isEnabled) {
                colorStateList = colorStateList.withAlpha(i);
            }
            textView.setTextColor(colorStateList);
        } else {
            i = 255;
        }
        RadioButton radioButton = (RadioButton) view.findViewById(16908289);
        if (radioButton != null) {
            radioButton.getButtonDrawable().setAlpha(i);
        }
    }
}
