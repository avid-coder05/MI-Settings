package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.annotation.Keep;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedPreference;

/* loaded from: classes2.dex */
public class PrimarySwitchPreference extends RestrictedPreference {
    private boolean mChecked;
    private boolean mCheckedSet;
    private boolean mEnableSwitch;
    private Switch mSwitch;

    public PrimarySwitchPreference(Context context) {
        super(context);
        this.mEnableSwitch = true;
    }

    public PrimarySwitchPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mEnableSwitch = true;
    }

    public PrimarySwitchPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mEnableSwitch = true;
    }

    public PrimarySwitchPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mEnableSwitch = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onBindViewHolder$0(View view, MotionEvent motionEvent) {
        return motionEvent.getActionMasked() == 2;
    }

    @Keep
    public Boolean getCheckedState() {
        if (this.mCheckedSet) {
            return Boolean.valueOf(this.mChecked);
        }
        return null;
    }

    @Override // com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference
    protected int getSecondTargetResId() {
        return R.layout.restricted_preference_widget_primary_switch;
    }

    public boolean isChecked() {
        return this.mSwitch != null && this.mChecked;
    }

    @Override // com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        int i = R.id.switchWidget;
        View findViewById = preferenceViewHolder.findViewById(i);
        if (findViewById != null) {
            findViewById.setVisibility(isDisabledByAdmin() ? 8 : 0);
            findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.PrimarySwitchPreference.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (PrimarySwitchPreference.this.mSwitch == null || PrimarySwitchPreference.this.mSwitch.isEnabled()) {
                        PrimarySwitchPreference.this.setChecked(!r2.mChecked);
                        PrimarySwitchPreference primarySwitchPreference = PrimarySwitchPreference.this;
                        if (primarySwitchPreference.callChangeListener(Boolean.valueOf(primarySwitchPreference.mChecked))) {
                            PrimarySwitchPreference primarySwitchPreference2 = PrimarySwitchPreference.this;
                            primarySwitchPreference2.persistBoolean(primarySwitchPreference2.mChecked);
                            return;
                        }
                        PrimarySwitchPreference.this.setChecked(!r1.mChecked);
                    }
                }
            });
            findViewById.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.settings.widget.PrimarySwitchPreference$$ExternalSyntheticLambda0
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    boolean lambda$onBindViewHolder$0;
                    lambda$onBindViewHolder$0 = PrimarySwitchPreference.lambda$onBindViewHolder$0(view, motionEvent);
                    return lambda$onBindViewHolder$0;
                }
            });
        }
        Switch r4 = (Switch) preferenceViewHolder.findViewById(i);
        this.mSwitch = r4;
        if (r4 != null) {
            r4.setContentDescription(getTitle());
            this.mSwitch.setChecked(this.mChecked);
            this.mSwitch.setEnabled(this.mEnableSwitch);
            this.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.widget.PrimarySwitchPreference.2
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundButton, final boolean z) {
                    compoundButton.post(new Runnable() { // from class: com.android.settings.widget.PrimarySwitchPreference.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (z == PrimarySwitchPreference.this.mChecked) {
                                return;
                            }
                            if (PrimarySwitchPreference.this.mSwitch == null || PrimarySwitchPreference.this.mSwitch.isEnabled()) {
                                PrimarySwitchPreference.this.setChecked(!r0.mChecked);
                                PrimarySwitchPreference primarySwitchPreference = PrimarySwitchPreference.this;
                                if (primarySwitchPreference.callChangeListener(Boolean.valueOf(primarySwitchPreference.mChecked))) {
                                    PrimarySwitchPreference primarySwitchPreference2 = PrimarySwitchPreference.this;
                                    primarySwitchPreference2.persistBoolean(primarySwitchPreference2.mChecked);
                                    return;
                                }
                                PrimarySwitchPreference.this.setChecked(!r2.mChecked);
                            }
                        }
                    });
                }
            });
        }
    }

    public void setChecked(boolean z) {
        if ((this.mChecked != z) || !this.mCheckedSet) {
            this.mChecked = z;
            this.mCheckedSet = true;
            Switch r2 = this.mSwitch;
            if (r2 != null) {
                r2.setChecked(z);
            }
        }
    }

    @Override // com.android.settingslib.RestrictedPreference
    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        super.setDisabledByAdmin(enforcedAdmin);
        setSwitchEnabled(enforcedAdmin == null);
    }

    public void setSwitchEnabled(boolean z) {
        this.mEnableSwitch = z;
        Switch r0 = this.mSwitch;
        if (r0 != null) {
            r0.setEnabled(z);
        }
    }

    @Override // com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference
    protected boolean shouldHideSecondTarget() {
        return getSecondTargetResId() == 0;
    }
}
