package com.android.settings.ringtone;

import android.content.Intent;
import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import miui.telephony.SubscriptionManager;
import miui.util.SimRingtoneUtils;

/* loaded from: classes2.dex */
public class MultiSimRingtoneSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private CheckBoxPreference mSlotSetting = null;
    private PreferenceCategory mRingtoneCategory = null;
    private MultiSimRingtonePreference mRingtone0 = null;
    private MultiSimRingtonePreference mRingtone1 = null;
    private MultiSimRingtonePreference mRingtone2 = null;
    private int mRingtoneType = 0;
    private int mSlot1ExtraType = 0;
    private int mSlot2ExtraType = 0;
    private SubscriptionManager.OnSubscriptionsChangedListener mSimInfoChangeListener = new SubscriptionManager.OnSubscriptionsChangedListener() { // from class: com.android.settings.ringtone.MultiSimRingtoneSettings.1
        @Override // miui.telephony.SubscriptionManager.OnSubscriptionsChangedListener
        public void onSubscriptionsChanged() {
            MultiSimRingtoneSettings.this.updateUI();
        }
    };

    private void setupUI() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("ringtone_slot_setting");
        this.mSlotSetting = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this);
        this.mSlotSetting.setChecked(!SimRingtoneUtils.isDefaultSoundUniform(getActivity(), this.mRingtoneType));
        this.mRingtoneCategory = (PreferenceCategory) findPreference("ringtone_category");
        MultiSimRingtonePreference multiSimRingtonePreference = (MultiSimRingtonePreference) findPreference("ringtone_0");
        this.mRingtone0 = multiSimRingtonePreference;
        multiSimRingtonePreference.setOnPreferenceClickListener(this);
        MultiSimRingtonePreference multiSimRingtonePreference2 = (MultiSimRingtonePreference) findPreference("ringtone_1");
        this.mRingtone1 = multiSimRingtonePreference2;
        multiSimRingtonePreference2.setOnPreferenceClickListener(this);
        MultiSimRingtonePreference multiSimRingtonePreference3 = (MultiSimRingtonePreference) findPreference("ringtone_2");
        this.mRingtone2 = multiSimRingtonePreference3;
        multiSimRingtonePreference3.setOnPreferenceClickListener(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUI() {
        if (!this.mSlotSetting.isChecked()) {
            this.mRingtoneCategory.addPreference(this.mRingtone0);
            this.mRingtoneCategory.removePreference(this.mRingtone1);
            this.mRingtoneCategory.removePreference(this.mRingtone2);
            this.mRingtone0.updateUI(this.mRingtoneType);
            return;
        }
        this.mRingtoneCategory.removePreference(this.mRingtone0);
        this.mRingtoneCategory.addPreference(this.mRingtone1);
        this.mRingtoneCategory.addPreference(this.mRingtone2);
        this.mRingtone1.updateUI(this.mSlot1ExtraType);
        this.mRingtone2.updateUI(this.mSlot2ExtraType);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MultiSimRingtoneSettings.class.getName();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        int i = getArguments().getInt("android.intent.extra.ringtone.TYPE", 0);
        this.mRingtoneType = i;
        if (i != 1 && i != 8 && i != 16) {
            finish();
            return;
        }
        this.mSlot1ExtraType = SimRingtoneUtils.getExtraRingtoneTypeBySlot(i, 0);
        this.mSlot2ExtraType = SimRingtoneUtils.getExtraRingtoneTypeBySlot(this.mRingtoneType, 1);
        addPreferencesFromResource(R.xml.multi_sim_ringtone_settings);
        setupUI();
        updateUI();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        SubscriptionManager.getDefault().removeOnSubscriptionsChangedListener(this.mSimInfoChangeListener);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        CheckBoxPreference checkBoxPreference = this.mSlotSetting;
        if (preference == checkBoxPreference) {
            checkBoxPreference.setChecked(!checkBoxPreference.isChecked());
            SimRingtoneUtils.setDefaultSoundUniform(getActivity(), this.mRingtoneType, !this.mSlotSetting.isChecked());
            updateUI();
        }
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        MultiSimRingtonePreference multiSimRingtonePreference = this.mRingtone0;
        if (preference == multiSimRingtonePreference) {
            startActivityForResult(multiSimRingtonePreference.getRingtonePickerIntent(), 0);
        } else {
            MultiSimRingtonePreference multiSimRingtonePreference2 = this.mRingtone1;
            if (preference == multiSimRingtonePreference2) {
                startActivityForResult(multiSimRingtonePreference2.getRingtonePickerIntent(), 1);
            } else {
                MultiSimRingtonePreference multiSimRingtonePreference3 = this.mRingtone2;
                if (preference == multiSimRingtonePreference3) {
                    startActivityForResult(multiSimRingtonePreference3.getRingtonePickerIntent(), 2);
                }
            }
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        SubscriptionManager.getDefault().addOnSubscriptionsChangedListener(this.mSimInfoChangeListener);
        updateUI();
    }
}
