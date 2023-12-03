package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.utils.AodUtils;

/* loaded from: classes.dex */
public class AodValuePreference extends KeyguardRestrictedPreference {
    private String mValueString;
    private TextView mValueView;

    public AodValuePreference(Context context) {
        super(context);
        init();
    }

    public AodValuePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public AodValuePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public AodValuePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    private void init() {
        setWidgetLayoutResource(R.layout.aod_value_preference);
        setShowRightArrow(true);
    }

    @Override // com.android.settings.KeyguardRestrictedPreference, com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mValueView = (TextView) preferenceViewHolder.itemView.findViewById(R.id.text_right);
        Log.i("AodValuePreference", "onBindViewHolder: " + this.mValueView + "  " + this.mValueString);
        TextView textView = this.mValueView;
        if (textView != null) {
            textView.setText(this.mValueString);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onClick() {
        super.onClick();
        if ((AodNotificationPrefController.AOD_KEYGUARD_NOTIFICATION_STATUS.equals(getKey()) || AodModePreferenceController.KEY_AOD_MODE.equals(getKey())) && AodUtils.supportSettingSplit(((KeyguardRestrictedPreference) this).mContext)) {
            Intent intent = getIntent();
            if (intent == null) {
                Log.e("AodValuePreference", "onClick(): AOD intent == null");
            } else {
                intent.setFlags(268435456);
            }
        }
    }

    public void setAodValue(String str) {
        this.mValueString = str;
        Log.i("AodValuePreference", "setAodValue: " + this.mValueView + "  " + this.mValueString);
        TextView textView = this.mValueView;
        if (textView != null) {
            textView.setText(this.mValueString);
        }
    }

    @Override // com.android.settings.KeyguardRestrictedPreference, com.android.settingslib.RestrictedPreference
    public void setValue(CharSequence charSequence) {
        super.setValue(charSequence);
        if (charSequence != null) {
            setAodValue(charSequence.toString());
        }
    }

    @Override // com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference
    protected boolean shouldHideSecondTarget() {
        return false;
    }
}
