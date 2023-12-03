package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.service.notification.ZenModeConfig;
import android.view.View;
import android.widget.CheckBox;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.utils.ManagedServiceSettings;
import com.android.settings.utils.ZenServiceListing;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.widget.TwoTargetPreference;
import java.util.Map;

/* loaded from: classes2.dex */
public class ZenRulePreference extends TwoTargetPreference {
    private static final ManagedServiceSettings.Config CONFIG = ZenModeAutomationSettings.getConditionProviderConfig();
    final ZenModeBackend mBackend;
    private CheckBox mCheckBox;
    private boolean mChecked;
    final Context mContext;
    final String mId;
    private Intent mIntent;
    final MetricsFeatureProvider mMetricsFeatureProvider;
    CharSequence mName;
    private View.OnClickListener mOnCheckBoxClickListener;
    final Fragment mParent;
    final PackageManager mPm;
    final Preference mPref;
    AutomaticZenRule mRule;
    final ZenServiceListing mServiceListing;

    public ZenRulePreference(Context context, Map.Entry<String, AutomaticZenRule> entry, Fragment fragment, MetricsFeatureProvider metricsFeatureProvider) {
        super(context);
        this.mOnCheckBoxClickListener = new View.OnClickListener() { // from class: com.android.settings.notification.zen.ZenRulePreference.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ZenRulePreference.this.mRule.setEnabled(!r3.mChecked);
                ZenRulePreference zenRulePreference = ZenRulePreference.this;
                zenRulePreference.mBackend.updateZenRule(zenRulePreference.mId, zenRulePreference.mRule);
                ZenRulePreference zenRulePreference2 = ZenRulePreference.this;
                zenRulePreference2.setChecked(zenRulePreference2.mRule.isEnabled());
                ZenRulePreference zenRulePreference3 = ZenRulePreference.this;
                zenRulePreference3.setAttributes(zenRulePreference3.mRule);
            }
        };
        setLayoutResource(R.layout.preference_checkable_two_target);
        this.mBackend = ZenModeBackend.getInstance(context);
        this.mContext = context;
        AutomaticZenRule value = entry.getValue();
        this.mRule = value;
        this.mName = value.getName();
        this.mId = entry.getKey();
        this.mParent = fragment;
        this.mPm = context.getPackageManager();
        ZenServiceListing zenServiceListing = new ZenServiceListing(context, CONFIG);
        this.mServiceListing = zenServiceListing;
        zenServiceListing.reloadApprovedServices();
        this.mPref = this;
        this.mMetricsFeatureProvider = metricsFeatureProvider;
        this.mChecked = this.mRule.isEnabled();
        setAttributes(this.mRule);
        setWidgetLayoutResource(getSecondTargetResId());
    }

    private String computeRuleSummary(AutomaticZenRule automaticZenRule) {
        return (automaticZenRule == null || !automaticZenRule.isEnabled()) ? this.mContext.getResources().getString(R.string.switch_off_text) : this.mContext.getResources().getString(R.string.switch_on_text);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setChecked(boolean z) {
        this.mChecked = z;
        CheckBox checkBox = this.mCheckBox;
        if (checkBox != null) {
            checkBox.setChecked(z);
        }
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference
    protected int getSecondTargetResId() {
        if (this.mIntent != null) {
            return R.layout.zen_rule_widget;
        }
        return 0;
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference, com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(16908312);
        View findViewById2 = preferenceViewHolder.findViewById(R.id.two_target_divider);
        if (this.mIntent != null) {
            findViewById2.setVisibility(0);
            findViewById.setVisibility(0);
            findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.zen.ZenRulePreference.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ZenRulePreference zenRulePreference = ZenRulePreference.this;
                    zenRulePreference.mContext.startActivity(zenRulePreference.mIntent);
                }
            });
        } else {
            findViewById2.setVisibility(8);
            findViewById.setVisibility(8);
            findViewById.setOnClickListener(null);
        }
        View findViewById3 = preferenceViewHolder.findViewById(R.id.checkbox_container);
        if (findViewById3 != null) {
            findViewById3.setOnClickListener(this.mOnCheckBoxClickListener);
        }
        CheckBox checkBox = (CheckBox) preferenceViewHolder.findViewById(16908289);
        this.mCheckBox = checkBox;
        if (checkBox != null) {
            checkBox.setChecked(this.mChecked);
        }
    }

    @Override // androidx.preference.Preference
    public void onClick() {
        this.mOnCheckBoxClickListener.onClick(null);
    }

    protected void setAttributes(AutomaticZenRule automaticZenRule) {
        boolean isValidScheduleConditionId = ZenModeConfig.isValidScheduleConditionId(automaticZenRule.getConditionId(), true);
        boolean isValidEventConditionId = ZenModeConfig.isValidEventConditionId(automaticZenRule.getConditionId());
        setSummary(computeRuleSummary(automaticZenRule));
        setTitle(this.mName);
        setPersistent(false);
        Intent ruleIntent = AbstractZenModeAutomaticRulePreferenceController.getRuleIntent(isValidScheduleConditionId ? "android.settings.ZEN_MODE_SCHEDULE_RULE_SETTINGS" : isValidEventConditionId ? "android.settings.ZEN_MODE_EVENT_RULE_SETTINGS" : "", AbstractZenModeAutomaticRulePreferenceController.getSettingsActivity(this.mPm, automaticZenRule, this.mServiceListing.findService(automaticZenRule.getOwner())), this.mId);
        this.mIntent = ruleIntent;
        if (ruleIntent.resolveActivity(this.mPm) == null) {
            this.mIntent = null;
        }
        setKey(this.mId);
    }

    public void updatePreference(AutomaticZenRule automaticZenRule) {
        if (!this.mRule.getName().equals(automaticZenRule.getName())) {
            String name = automaticZenRule.getName();
            this.mName = name;
            setTitle(name);
        }
        if (this.mRule.isEnabled() != automaticZenRule.isEnabled()) {
            setChecked(automaticZenRule.isEnabled());
            setSummary(computeRuleSummary(automaticZenRule));
        }
        this.mRule = automaticZenRule;
    }
}
