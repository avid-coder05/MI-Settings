package com.android.settings.development;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import miui.content.res.ThemeResources;

/* loaded from: classes.dex */
public class DemoModePreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private CheckBoxPreference mDemoMode;
    private final Fragment mFragment;

    public DemoModePreferenceController(Context context, Fragment fragment) {
        super(context);
        this.mFragment = fragment;
    }

    private void enableDemoMode(boolean z) {
        Intent intent = new Intent("com.android.systemui.demo");
        intent.setPackage(ThemeResources.SYSTEMUI_NAME);
        intent.putExtra("command", z ? "enter" : "exit");
        this.mContext.getApplicationContext().sendBroadcast(intent);
        Settings.System.putInt(this.mContext.getContentResolver(), "sysui_tuner_demo_on", z ? 1 : 0);
    }

    private boolean isDemoModeEnabled() {
        return Settings.System.getInt(this.mContext.getContentResolver(), "sysui_tuner_demo_on", 0) != 0;
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mDemoMode = (CheckBoxPreference) preferenceScreen.findPreference("demo_mode");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "demo_mode";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        onEnableDemoModeDismissed();
    }

    public void onEnableDemoModeConfirmed() {
        enableDemoMode(true);
        this.mDemoMode.setChecked(true);
    }

    public void onEnableDemoModeDismissed() {
        if (isDemoModeEnabled()) {
            enableDemoMode(false);
        }
        this.mDemoMode.setChecked(false);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (this.mDemoMode.isChecked()) {
            onEnableDemoModeDismissed();
            return true;
        }
        DemoModeWarningDialog.show(this.mFragment);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mDemoMode.setChecked(isDemoModeEnabled());
    }
}
