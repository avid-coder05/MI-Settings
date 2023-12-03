package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.util.Pair;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.RadioButtonPreference;

/* loaded from: classes2.dex */
public class ZenRuleDefaultPolicyPreferenceController extends AbstractZenCustomRulePreferenceController {
    private RadioButtonPreference mPreference;

    public ZenRuleDefaultPolicyPreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str, lifecycle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$0(RadioButtonPreference radioButtonPreference) {
        this.mRule.setZenPolicy(null);
        this.mBackend.updateZenRule(this.mId, this.mRule);
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        RadioButtonPreference radioButtonPreference = (RadioButtonPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = radioButtonPreference;
        radioButtonPreference.setOnClickListener(new RadioButtonPreference.OnClickListener() { // from class: com.android.settings.notification.zen.ZenRuleDefaultPolicyPreferenceController$$ExternalSyntheticLambda0
            @Override // com.android.settingslib.widget.RadioButtonPreference.OnClickListener
            public final void onRadioButtonClicked(RadioButtonPreference radioButtonPreference2) {
                ZenRuleDefaultPolicyPreferenceController.this.lambda$displayPreference$0(radioButtonPreference2);
            }
        });
    }

    @Override // com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public /* bridge */ /* synthetic */ boolean isAvailable() {
        return super.isAvailable();
    }

    @Override // com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController
    public /* bridge */ /* synthetic */ void onResume(AutomaticZenRule automaticZenRule, String str) {
        super.onResume(automaticZenRule, str);
    }

    @Override // com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (this.mId == null || this.mRule == null) {
            return;
        }
        this.mMetricsFeatureProvider.action(this.mContext, 1606, Pair.create(1603, this.mId));
        this.mPreference.setChecked(this.mRule.getZenPolicy() == null);
    }
}
