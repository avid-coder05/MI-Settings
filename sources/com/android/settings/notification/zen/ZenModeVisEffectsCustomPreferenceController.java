package com.android.settings.notification.zen;

import android.app.NotificationManager;
import android.content.Context;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.RadioButtonPreference;

/* loaded from: classes2.dex */
public class ZenModeVisEffectsCustomPreferenceController extends AbstractZenModePreferenceController {
    private RadioButtonPreference mPreference;

    public ZenModeVisEffectsCustomPreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str, lifecycle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$0(View view) {
        launchCustomSettings();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$1(RadioButtonPreference radioButtonPreference) {
        launchCustomSettings();
    }

    private void launchCustomSettings() {
        select();
        new SubSettingLauncher(this.mContext).setDestination(ZenModeBlockedEffectsSettings.class.getName()).setTitleRes(R.string.zen_mode_what_to_block_title).setSourceMetricsCategory(1400).launch();
    }

    protected boolean areCustomOptionsSelected() {
        return (NotificationManager.Policy.areAllVisualEffectsSuppressed(this.mBackend.mPolicy.suppressedVisualEffects) || (this.mBackend.mPolicy.suppressedVisualEffects == 0)) ? false : true;
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        RadioButtonPreference radioButtonPreference = (RadioButtonPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = radioButtonPreference;
        radioButtonPreference.setExtraWidgetOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.zen.ZenModeVisEffectsCustomPreferenceController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ZenModeVisEffectsCustomPreferenceController.this.lambda$displayPreference$0(view);
            }
        });
        this.mPreference.setOnClickListener(new RadioButtonPreference.OnClickListener() { // from class: com.android.settings.notification.zen.ZenModeVisEffectsCustomPreferenceController$$ExternalSyntheticLambda1
            @Override // com.android.settingslib.widget.RadioButtonPreference.OnClickListener
            public final void onRadioButtonClicked(RadioButtonPreference radioButtonPreference2) {
                ZenModeVisEffectsCustomPreferenceController.this.lambda$displayPreference$1(radioButtonPreference2);
            }
        });
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    protected void select() {
        this.mMetricsFeatureProvider.action(this.mContext, 1399, true);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mPreference.setChecked(areCustomOptionsSelected());
    }
}
