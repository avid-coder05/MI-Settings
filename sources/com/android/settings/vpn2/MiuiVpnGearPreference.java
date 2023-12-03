package com.android.settings.vpn2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.RadioButtonPreference;

/* loaded from: classes2.dex */
public class MiuiVpnGearPreference extends RadioButtonPreference implements View.OnClickListener {
    private OnGearClickListener mOnGearClickListener;

    /* loaded from: classes2.dex */
    public interface OnGearClickListener {
        void onGearClick(MiuiVpnGearPreference miuiVpnGearPreference);
    }

    public MiuiVpnGearPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.android.settingslib.miuisettings.preference.RadioButtonPreference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        View findViewById = view.findViewById(R.id.settings_button);
        findViewById.setOnClickListener(this);
        findViewById.setEnabled(true);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        OnGearClickListener onGearClickListener;
        if (view.getId() != R.id.settings_button || (onGearClickListener = this.mOnGearClickListener) == null) {
            return;
        }
        onGearClickListener.onGearClick(this);
    }
}
