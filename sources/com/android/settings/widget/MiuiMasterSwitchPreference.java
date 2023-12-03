package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Switch;
import com.android.settings.R;
import com.android.settingslib.TwoTargetPreference;

/* loaded from: classes2.dex */
public class MiuiMasterSwitchPreference extends TwoTargetPreference {
    private boolean mChecked;
    private boolean mEnableSwitch;
    private Switch mSwitch;

    public MiuiMasterSwitchPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mEnableSwitch = true;
    }

    public MiuiMasterSwitchPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mEnableSwitch = true;
    }

    public MiuiMasterSwitchPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mEnableSwitch = true;
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        View findViewById = view.findViewById(16908312);
        if (findViewById != null) {
            findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.MiuiMasterSwitchPreference.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    if (MiuiMasterSwitchPreference.this.mSwitch == null || MiuiMasterSwitchPreference.this.mSwitch.isEnabled()) {
                        MiuiMasterSwitchPreference.this.setChecked(!r2.mChecked);
                        MiuiMasterSwitchPreference miuiMasterSwitchPreference = MiuiMasterSwitchPreference.this;
                        if (miuiMasterSwitchPreference.callChangeListener(Boolean.valueOf(miuiMasterSwitchPreference.mChecked))) {
                            MiuiMasterSwitchPreference miuiMasterSwitchPreference2 = MiuiMasterSwitchPreference.this;
                            miuiMasterSwitchPreference2.persistBoolean(miuiMasterSwitchPreference2.mChecked);
                            return;
                        }
                        MiuiMasterSwitchPreference.this.setChecked(!r1.mChecked);
                    }
                }
            });
        }
        Switch r3 = (Switch) view.findViewById(R.id.switchWidget);
        this.mSwitch = r3;
        if (r3 != null) {
            r3.setContentDescription(getTitle());
            this.mSwitch.setChecked(this.mChecked);
            this.mSwitch.setEnabled(this.mEnableSwitch);
        }
    }

    public void setChecked(boolean z) {
        this.mChecked = z;
        Switch r0 = this.mSwitch;
        if (r0 != null) {
            r0.setChecked(z);
        }
    }
}
