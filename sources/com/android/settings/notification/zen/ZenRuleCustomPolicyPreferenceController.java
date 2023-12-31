package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.service.notification.ZenPolicy;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.RadioButtonPreference;

/* loaded from: classes2.dex */
public class ZenRuleCustomPolicyPreferenceController extends AbstractZenCustomRulePreferenceController {
    private RadioButtonPreference mPreference;

    public ZenRuleCustomPolicyPreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str, lifecycle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$0(View view) {
        setCustomPolicy();
        launchCustomSettings();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$1(RadioButtonPreference radioButtonPreference) {
        setCustomPolicy();
        launchCustomSettings();
    }

    private void launchCustomSettings() {
        new SubSettingLauncher(this.mContext).setDestination(ZenCustomRuleConfigSettings.class.getName()).setArguments(createBundle()).setSourceMetricsCategory(1605).launch();
    }

    private void setCustomPolicy() {
        if (this.mRule.getZenPolicy() == null) {
            this.mRule.setZenPolicy(this.mBackend.setDefaultZenPolicy(new ZenPolicy()));
            this.mBackend.updateZenRule(this.mId, this.mRule);
        }
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        RadioButtonPreference radioButtonPreference = (RadioButtonPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = radioButtonPreference;
        radioButtonPreference.setExtraWidgetOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.zen.ZenRuleCustomPolicyPreferenceController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ZenRuleCustomPolicyPreferenceController.this.lambda$displayPreference$0(view);
            }
        });
        this.mPreference.setOnClickListener(new RadioButtonPreference.OnClickListener() { // from class: com.android.settings.notification.zen.ZenRuleCustomPolicyPreferenceController$$ExternalSyntheticLambda1
            @Override // com.android.settingslib.widget.RadioButtonPreference.OnClickListener
            public final void onRadioButtonClicked(RadioButtonPreference radioButtonPreference2) {
                ZenRuleCustomPolicyPreferenceController.this.lambda$displayPreference$1(radioButtonPreference2);
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
        AutomaticZenRule automaticZenRule;
        super.updateState(preference);
        if (this.mId == null || (automaticZenRule = this.mRule) == null) {
            return;
        }
        this.mPreference.setChecked(automaticZenRule.getZenPolicy() != null);
    }
}
