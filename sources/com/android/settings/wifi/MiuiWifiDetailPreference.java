package com.android.settings.wifi;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;

/* loaded from: classes2.dex */
public class MiuiWifiDetailPreference extends Preference {
    private int mColorResId;

    public MiuiWifiDetailPreference(Context context) {
        super(context, null);
        init();
    }

    public MiuiWifiDetailPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
        init();
    }

    public MiuiWifiDetailPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        setLayoutResource(R.layout.preference_wifi_detail);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(16908310);
        if (textView != null) {
            textView.setTextColor(this.mColorResId);
        }
    }

    public void setTitleColorRes(int i) {
        this.mColorResId = i;
    }
}
