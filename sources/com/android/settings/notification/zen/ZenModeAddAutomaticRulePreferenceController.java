package com.android.settings.notification.zen;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.notification.zen.ZenRuleSelectionDialog;
import com.android.settings.utils.ZenServiceListing;
import com.android.settingslib.core.lifecycle.Lifecycle;

/* loaded from: classes2.dex */
public class ZenModeAddAutomaticRulePreferenceController extends AbstractZenModeAutomaticRulePreferenceController implements Preference.OnPreferenceClickListener {
    private final ZenServiceListing mZenServiceListing;

    /* loaded from: classes2.dex */
    public class RuleSelectionListener implements ZenRuleSelectionDialog.PositiveClickListener {
        public RuleSelectionListener() {
        }

        @Override // com.android.settings.notification.zen.ZenRuleSelectionDialog.PositiveClickListener
        public void onExternalRuleSelected(ZenRuleInfo zenRuleInfo, Fragment fragment) {
            fragment.startActivity(new Intent().setComponent(zenRuleInfo.configurationActivity));
        }

        @Override // com.android.settings.notification.zen.ZenRuleSelectionDialog.PositiveClickListener
        public void onSystemRuleSelected(ZenRuleInfo zenRuleInfo, Fragment fragment) {
            ZenModeAddAutomaticRulePreferenceController.this.showNameRuleDialog(zenRuleInfo, fragment);
        }
    }

    public ZenModeAddAutomaticRulePreferenceController(Context context, Fragment fragment, ZenServiceListing zenServiceListing, Lifecycle lifecycle) {
        super(context, "zen_mode_add_automatic_rule", fragment, lifecycle);
        this.mZenServiceListing = zenServiceListing;
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference("zen_mode_add_automatic_rule");
        findPreference.setPersistent(false);
        findPreference.setOnPreferenceClickListener(this);
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "zen_mode_add_automatic_rule";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        ZenRuleSelectionDialog.show(this.mContext, this.mParent, new RuleSelectionListener(), this.mZenServiceListing);
        return true;
    }
}
