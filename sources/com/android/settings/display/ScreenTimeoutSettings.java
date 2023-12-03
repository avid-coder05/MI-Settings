package com.android.settings.display;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.SensorPrivacyManager;
import android.os.UserHandle;
import android.provider.SearchIndexableData;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.support.actionbar.HelpResourceProvider;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.FooterPreference;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ScreenTimeoutSettings extends RadioButtonPickerFragment implements HelpResourceProvider {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.screen_timeout_settings) { // from class: com.android.settings.display.ScreenTimeoutSettings.3
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            if (ScreenTimeoutSettings.isScreenAttentionAvailable(context)) {
                Resources resources = context.getResources();
                SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
                int i = R.string.adaptive_sleep_title;
                searchIndexableRaw.title = resources.getString(i);
                ((SearchIndexableData) searchIndexableRaw).key = "adaptive_sleep";
                searchIndexableRaw.keywords = resources.getString(i);
                ArrayList arrayList = new ArrayList(1);
                arrayList.add(searchIndexableRaw);
                return arrayList;
            }
            return null;
        }
    };
    AdaptiveSleepBatterySaverPreferenceController mAdaptiveSleepBatterySaverPreferenceController;
    AdaptiveSleepCameraStatePreferenceController mAdaptiveSleepCameraStatePreferenceController;
    AdaptiveSleepPreferenceController mAdaptiveSleepController;
    AdaptiveSleepPermissionPreferenceController mAdaptiveSleepPermissionController;
    RestrictedLockUtils.EnforcedAdmin mAdmin;
    Context mContext;
    Preference mDisableOptionsPreference;
    private CharSequence[] mInitialEntries;
    private CharSequence[] mInitialValues;
    private SensorPrivacyManager mPrivacyManager;
    private FooterPreference mPrivacyPreference;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.display.ScreenTimeoutSettings.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            ScreenTimeoutSettings.this.mAdaptiveSleepBatterySaverPreferenceController.updateVisibility();
            ScreenTimeoutSettings.this.mAdaptiveSleepController.updatePreference();
        }
    };
    private final MetricsFeatureProvider mMetricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TimeoutCandidateInfo extends CandidateInfo {
        private final String mKey;
        private final CharSequence mLabel;

        TimeoutCandidateInfo(CharSequence charSequence, String str, boolean z) {
            super(z);
            this.mLabel = charSequence;
            this.mKey = str;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public String getKey() {
            return this.mKey;
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

    private String getCurrentSystemScreenTimeout(Context context) {
        return context == null ? Long.toString(30000L) : Long.toString(Settings.System.getLong(context.getContentResolver(), "screen_off_timeout", 30000L));
    }

    private Long getMaxScreenTimeout(Context context) {
        DevicePolicyManager devicePolicyManager;
        if (context == null || (devicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class)) == null) {
            return Long.MAX_VALUE;
        }
        RestrictedLockUtils.EnforcedAdmin checkIfMaximumTimeToLockIsSet = RestrictedLockUtilsInternal.checkIfMaximumTimeToLockIsSet(context);
        this.mAdmin = checkIfMaximumTimeToLockIsSet;
        if (checkIfMaximumTimeToLockIsSet != null) {
            return Long.valueOf(devicePolicyManager.getMaximumTimeToLock(null, UserHandle.myUserId()));
        }
        return Long.MAX_VALUE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isScreenAttentionAvailable(Context context) {
        return AdaptiveSleepPreferenceController.isAdaptiveSleepSupported(context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$0(int i, boolean z) {
        this.mAdaptiveSleepController.updatePreference();
    }

    private void setCurrentSystemScreenTimeout(Context context, String str) {
        if (context != null) {
            try {
                long parseLong = Long.parseLong(str);
                this.mMetricsFeatureProvider.action(context, 1754, (int) parseLong);
                Settings.System.putLong(context.getContentResolver(), "screen_off_timeout", parseLong);
            } catch (NumberFormatException e) {
                Log.e("ScreenTimeout", "could not persist screen timeout setting", e);
            }
        }
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected List<? extends CandidateInfo> getCandidates() {
        ArrayList arrayList = new ArrayList();
        long longValue = getMaxScreenTimeout(getContext()).longValue();
        if (this.mInitialValues != null) {
            int i = 0;
            while (true) {
                CharSequence[] charSequenceArr = this.mInitialValues;
                if (i >= charSequenceArr.length) {
                    break;
                }
                if (Long.parseLong(charSequenceArr[i].toString()) <= longValue) {
                    arrayList.add(new TimeoutCandidateInfo(this.mInitialEntries[i], this.mInitialValues[i].toString(), true));
                }
                i++;
            }
        } else {
            Log.e("ScreenTimeout", "Screen timeout options do not exist.");
        }
        return arrayList;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected String getDefaultKey() {
        return getCurrentSystemScreenTimeout(getContext());
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_adaptive_sleep;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1852;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.screen_timeout_settings;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mInitialEntries = getResources().getStringArray(R.array.screen_timeout_entries);
        this.mInitialValues = getResources().getStringArray(R.array.screen_timeout_values);
        this.mAdaptiveSleepController = new AdaptiveSleepPreferenceController(context);
        this.mAdaptiveSleepPermissionController = new AdaptiveSleepPermissionPreferenceController(context);
        this.mAdaptiveSleepCameraStatePreferenceController = new AdaptiveSleepCameraStatePreferenceController(context);
        this.mAdaptiveSleepBatterySaverPreferenceController = new AdaptiveSleepBatterySaverPreferenceController(context);
        FooterPreference footerPreference = new FooterPreference(context);
        this.mPrivacyPreference = footerPreference;
        footerPreference.setIcon(R.drawable.ic_privacy_shield_24dp);
        this.mPrivacyPreference.setTitle(R.string.adaptive_sleep_privacy);
        this.mPrivacyPreference.setSelectable(false);
        this.mPrivacyPreference.setLayoutResource(R.layout.preference_footer);
        SensorPrivacyManager sensorPrivacyManager = SensorPrivacyManager.getInstance(context);
        this.mPrivacyManager = sensorPrivacyManager;
        sensorPrivacyManager.addSensorPrivacyListener(2, new SensorPrivacyManager.OnSensorPrivacyChangedListener() { // from class: com.android.settings.display.ScreenTimeoutSettings$$ExternalSyntheticLambda0
            public final void onSensorPrivacyChanged(int i, boolean z) {
                ScreenTimeoutSettings.this.lambda$onAttach$0(i, z);
            }
        });
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mAdaptiveSleepPermissionController.updateVisibility();
        this.mAdaptiveSleepCameraStatePreferenceController.updateVisibility();
        this.mAdaptiveSleepBatterySaverPreferenceController.updateVisibility();
        this.mAdaptiveSleepController.updatePreference();
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter("android.os.action.POWER_SAVE_MODE_CHANGED"));
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected boolean setDefaultKey(String str) {
        setCurrentSystemScreenTimeout(getContext(), str);
        return true;
    }

    void setupDisabledFooterPreference() {
        String string = getResources().getString(R.string.admin_disabled_other_options);
        String string2 = getResources().getString(R.string.admin_more_details);
        SpannableString spannableString = new SpannableString(string + System.lineSeparator() + System.lineSeparator() + string2);
        ClickableSpan clickableSpan = new ClickableSpan() { // from class: com.android.settings.display.ScreenTimeoutSettings.2
            @Override // android.text.style.ClickableSpan
            public void onClick(View view) {
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(ScreenTimeoutSettings.this.getContext(), ScreenTimeoutSettings.this.mAdmin);
            }
        };
        if (string != null && string2 != null) {
            spannableString.setSpan(clickableSpan, string.length() + 1, string.length() + string2.length() + 2, 33);
        }
        FooterPreference footerPreference = new FooterPreference(getContext());
        this.mDisableOptionsPreference = footerPreference;
        footerPreference.setLayoutResource(R.layout.preference_footer);
        this.mDisableOptionsPreference.setTitle(spannableString);
        this.mDisableOptionsPreference.setSelectable(false);
        this.mDisableOptionsPreference.setIcon(R.drawable.ic_info_outline_24dp);
        this.mDisableOptionsPreference.setOrder(2147483646);
        this.mPrivacyPreference.setOrder(2147483645);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public void updateCandidates() {
        String defaultKey = getDefaultKey();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        List<? extends CandidateInfo> candidates = getCandidates();
        if (candidates == null) {
            return;
        }
        for (CandidateInfo candidateInfo : candidates) {
            com.android.settingslib.widget.RadioButtonPreference radioButtonPreference = new com.android.settingslib.widget.RadioButtonPreference(getPrefContext());
            bindPreference(radioButtonPreference, candidateInfo.getKey(), candidateInfo, defaultKey);
            preferenceScreen.addPreference(radioButtonPreference);
        }
        long parseLong = Long.parseLong(defaultKey);
        long longValue = getMaxScreenTimeout(getContext()).longValue();
        if (!candidates.isEmpty() && parseLong > longValue) {
            ((com.android.settingslib.widget.RadioButtonPreference) preferenceScreen.getPreference(candidates.size() - 1)).setChecked(true);
        }
        FooterPreference footerPreference = new FooterPreference(this.mContext);
        this.mPrivacyPreference = footerPreference;
        footerPreference.setIcon(R.drawable.ic_privacy_shield_24dp);
        this.mPrivacyPreference.setTitle(R.string.adaptive_sleep_privacy);
        this.mPrivacyPreference.setSelectable(false);
        this.mPrivacyPreference.setLayoutResource(R.layout.preference_footer);
        if (isScreenAttentionAvailable(getContext())) {
            this.mAdaptiveSleepPermissionController.addToScreen(preferenceScreen);
            this.mAdaptiveSleepCameraStatePreferenceController.addToScreen(preferenceScreen);
            this.mAdaptiveSleepBatterySaverPreferenceController.addToScreen(preferenceScreen);
            this.mAdaptiveSleepController.addToScreen(preferenceScreen);
            preferenceScreen.addPreference(this.mPrivacyPreference);
        }
        if (this.mAdmin != null) {
            setupDisabledFooterPreference();
            preferenceScreen.addPreference(this.mDisableOptionsPreference);
        }
    }
}
