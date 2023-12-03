package com.android.settings.notification;

import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.service.notification.ZenModeConfig;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.notification.SilentModeSettingsBase;
import miui.provider.ExtraTelephony;

/* loaded from: classes2.dex */
public class SilentModeAutomationSettings extends SilentModeSettingsBase implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private MenuItem mMenuItemAdd;
    private PreferenceCategory mSilentModRulesCategory;

    public static boolean isValidScheduleConditionId(Uri uri) {
        return ZenModeConfig.tryParseScheduleConditionId(uri) != null;
    }

    private void updateControls() {
        this.mSilentModRulesCategory.removeAll();
        if (this.mConfig == null) {
            return;
        }
        SilentModeSettingsBase.ZenRuleInfo[] sortedRules = sortedRules();
        for (int i = 0; i < sortedRules.length; i++) {
            String str = sortedRules[i].id;
            ZenModeConfig.ZenRule zenRule = sortedRules[i].rule;
            Uri uri = zenRule.conditionId;
            if (isValidScheduleConditionId(uri)) {
                this.mSilentModRulesCategory.addPreference(new RuleItemPreference(getThemedContext(), zenRule.name, zenRule.enabled, str, uri, this, this));
            }
        }
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        String obj;
        AutomaticZenRule automaticZenRule;
        if (!(compoundButton instanceof CheckBox) || (automaticZenRule = NotificationManager.from(this.mContext).getAutomaticZenRule((obj = compoundButton.getTag().toString()))) == null) {
            return;
        }
        automaticZenRule.setEnabled(z);
        NotificationManager.from(this.mContext).updateAutomaticZenRule(obj, automaticZenRule);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view instanceof TextView) {
            Intent intent = new Intent(getContext(), SilentModeRuleSettings.class);
            intent.putExtra("rule_id", view.getTag().toString());
            intent.putExtra(ExtraTelephony.FirewallLog.MODE, 3);
            getContext().startActivity(intent);
        }
    }

    @Override // com.android.settings.notification.SilentModeSettingsBase, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.automation_rules_settings);
        this.mSilentModRulesCategory = (PreferenceCategory) findPreference("key_auto_rules");
        setHasOptionsMenu(true);
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem add = menu.add(0, 0, 0, R.string.add_new);
        this.mMenuItemAdd = add;
        add.setIcon(R.drawable.action_button_new).setShowAsAction(1);
        this.mMenuItemAdd.setVisible(true);
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 0) {
            Intent intent = new Intent(getContext(), SilentModeRuleSettings.class);
            intent.putExtra("rule_id", "new_rule");
            intent.putExtra(ExtraTelephony.FirewallLog.MODE, 2);
            getContext().startActivity(intent);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference instanceof RuleItemPreference) {
            Intent intent = new Intent(getContext(), SilentModeRuleSettings.class);
            intent.putExtra("rule_id", ((RuleItemPreference) preference).getmTitle().getTag().toString());
            intent.putExtra(ExtraTelephony.FirewallLog.MODE, 3);
            getContext().startActivity(intent);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen);
    }

    @Override // com.android.settings.notification.SilentModeSettingsBase, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateControls();
    }

    @Override // com.android.settings.notification.SilentModeSettingsBase
    protected void onZenModeChanged() {
    }

    @Override // com.android.settings.notification.SilentModeSettingsBase
    protected void onZenModeConfigChanged() {
        updateControls();
    }
}
