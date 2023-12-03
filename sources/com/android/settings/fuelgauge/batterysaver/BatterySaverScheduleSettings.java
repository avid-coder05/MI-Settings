package com.android.settings.fuelgauge.batterysaver;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.fuelgauge.BatterySaverUtils;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.RadioButtonPreference;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class BatterySaverScheduleSettings extends RadioButtonPickerFragment {
    Context mContext;
    public BatterySaverScheduleRadioButtonsController mRadioButtonController;
    private BatterySaverScheduleSeekBarController mSeekBarController;
    final ContentObserver mSettingsObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.fuelgauge.batterysaver.BatterySaverScheduleSettings.1
        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            BatterySaverScheduleSettings.this.getPreferenceScreen().removeAll();
            BatterySaverScheduleSettings.this.updateCandidates();
        }
    };

    /* loaded from: classes.dex */
    static class BatterySaverScheduleCandidateInfo extends CandidateInfo {
        private final String mKey;
        private final CharSequence mLabel;
        private final CharSequence mSummary;

        BatterySaverScheduleCandidateInfo(CharSequence charSequence, CharSequence charSequence2, String str, boolean z) {
            super(z);
            this.mLabel = charSequence;
            this.mKey = str;
            this.mSummary = charSequence2;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public String getKey() {
            return this.mKey;
        }

        public CharSequence getSummary() {
            return this.mSummary;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public Drawable loadIcon() {
            return null;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public CharSequence loadLabel() {
            return this.mLabel;
        }
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected void addStaticPreferences(PreferenceScreen preferenceScreen) {
        this.mSeekBarController.updateSeekBar();
        this.mSeekBarController.addToScreen(preferenceScreen);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public void bindPreferenceExtra(RadioButtonPreference radioButtonPreference, String str, CandidateInfo candidateInfo, String str2, String str3) {
        CharSequence summary = ((BatterySaverScheduleCandidateInfo) candidateInfo).getSummary();
        if (summary != null) {
            radioButtonPreference.setSummary(summary);
            radioButtonPreference.setAppendixVisibility(8);
        }
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected List<? extends CandidateInfo> getCandidates() {
        Context context = getContext();
        ArrayList newArrayList = Lists.newArrayList();
        String string = getContext().getResources().getString(17039883);
        newArrayList.add(new BatterySaverScheduleCandidateInfo(context.getText(R.string.battery_saver_auto_no_schedule), null, "key_battery_saver_no_schedule", true));
        if (TextUtils.isEmpty(string)) {
            BatterySaverUtils.revertScheduleToNoneIfNeeded(context);
        } else {
            newArrayList.add(new BatterySaverScheduleCandidateInfo(context.getText(R.string.battery_saver_auto_routine), context.getText(R.string.battery_saver_auto_routine_summary), "key_battery_saver_routine", true));
        }
        newArrayList.add(new BatterySaverScheduleCandidateInfo(context.getText(R.string.battery_saver_auto_percentage), null, "key_battery_saver_percentage", true));
        return newArrayList;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected String getDefaultKey() {
        return this.mRadioButtonController.getDefaultKey();
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.battery_saver_schedule_settings;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        BatterySaverScheduleSeekBarController batterySaverScheduleSeekBarController = new BatterySaverScheduleSeekBarController(context);
        this.mSeekBarController = batterySaverScheduleSeekBarController;
        this.mRadioButtonController = new BatterySaverScheduleRadioButtonsController(context, batterySaverScheduleSeekBarController);
        this.mContext = context;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mSettingsObserver);
        super.onPause();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("low_power_warning_acknowledged"), false, this.mSettingsObserver);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected boolean setDefaultKey(String str) {
        return this.mRadioButtonController.setDefaultKey(str);
    }
}
