package com.android.settings.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes2.dex */
public class CustomValuePreference extends ValuePreference {
    private int iconRes;
    private int mSignalType;
    private TextView mValueView;

    public CustomValuePreference(Context context) {
        this(context, null);
    }

    public CustomValuePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.textPreferenceStyle);
    }

    public CustomValuePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSignalType = 0;
        this.iconRes = -1;
    }

    @Override // com.android.settingslib.miuisettings.preference.ValuePreference, miuix.preference.TextPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        this.mValueView = (TextView) view.findViewById(R.id.value_right);
        setShowRightArrow(true);
        int i = this.mSignalType;
        Drawable drawable = i != 1 ? i != 2 ? i != 3 ? null : getContext().getResources().getDrawable(R.drawable.account_unlogin_tip) : getContext().getResources().getDrawable(R.drawable.device_update_signal1) : getContext().getResources().getDrawable(R.drawable.device_update_signal);
        if (this.mValueView != null && drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            this.mValueView.setCompoundDrawables(null, null, drawable, null);
            this.mValueView.setVisibility(0);
        }
        if (this.iconRes > 0) {
            ((ImageView) view.findViewById(16908294)).setImageResource(this.iconRes);
        }
    }

    public void showRedPoint(int i) {
        if (i == 1) {
            setValue(((Object) getValue()) + " ");
        }
        this.mSignalType = i;
    }
}
