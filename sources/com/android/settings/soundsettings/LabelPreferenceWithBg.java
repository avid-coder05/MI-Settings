package com.android.settings.soundsettings;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.android.settings.R;
import com.android.settings.R$styleable;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes2.dex */
public class LabelPreferenceWithBg extends ValuePreference {
    private int mBackground;
    private Context mContext;
    private boolean mIsFirst;
    private String mLabel;

    public LabelPreferenceWithBg(Context context) {
        super(context);
        initTypedArray(context, null);
    }

    public LabelPreferenceWithBg(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initTypedArray(context, attributeSet);
    }

    private void initTypedArray(Context context, AttributeSet attributeSet) {
        this.mContext = context;
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.LabelPreferenceWithBg);
            this.mBackground = obtainStyledAttributes.getResourceId(R$styleable.LabelPreferenceWithBg_backgroundRes, 0);
            this.mIsFirst = obtainStyledAttributes.getBoolean(R$styleable.LabelPreferenceWithBg_first, false);
            obtainStyledAttributes.recycle();
        }
    }

    @Override // com.android.settingslib.miuisettings.preference.ValuePreference
    public void onBindView(View view) {
        super.onBindView(view);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        Resources resources = this.mContext.getResources();
        int i = R.dimen.auto_rule_margin_left;
        layoutParams.setMargins(resources.getDimensionPixelOffset(i), this.mIsFirst ? this.mContext.getResources().getDimensionPixelOffset(R.dimen.auto_rule_first_margin_top) : 0, this.mContext.getResources().getDimensionPixelOffset(i), 0);
        view.setLayoutParams(layoutParams);
        int dimensionPixelOffset = this.mContext.getResources().getDimensionPixelOffset(R.dimen.auto_rule_padding_left);
        view.setPadding(dimensionPixelOffset, 0, dimensionPixelOffset, 0);
        int i2 = this.mBackground;
        if (i2 != 0) {
            view.setBackground(this.mContext.getDrawable(i2));
        }
    }

    public void setLabel(String str) {
        if (TextUtils.equals(this.mLabel, str)) {
            return;
        }
        this.mLabel = str;
        setValue(str);
    }
}
