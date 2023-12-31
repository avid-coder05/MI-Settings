package com.android.settings.applications.specialaccess.premiumsms;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settings.applications.AppStateSmsPremBridge;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.EmptyTextSettings;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import com.android.settingslib.widget.FooterPreference;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class PremiumSmsAccess extends EmptyTextSettings implements AppStateBaseBridge.Callback, ApplicationsState.Callbacks, Preference.OnPreferenceChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.premium_sms_settings);
    private ApplicationsState mApplicationsState;
    private ApplicationsState.Session mSession;
    private AppStateSmsPremBridge mSmsBackend;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PremiumSmsPreference extends DropDownPreference {
        private final ApplicationsState.AppEntry mAppEntry;

        public PremiumSmsPreference(ApplicationsState.AppEntry appEntry, Context context) {
            super(context);
            this.mAppEntry = appEntry;
            appEntry.ensureLabel(context);
            setTitle(appEntry.label);
            Drawable drawable = appEntry.icon;
            if (drawable != null) {
                setIcon(drawable);
            }
            setEntries(R.array.security_settings_premium_sms_values);
            setEntryValues(new CharSequence[]{String.valueOf(1), String.valueOf(2), String.valueOf(3)});
            setValue(String.valueOf(getCurrentValue()));
            setSummary("%s");
        }

        private int getCurrentValue() {
            Object obj = this.mAppEntry.extraInfo;
            if (obj instanceof AppStateSmsPremBridge.SmsState) {
                return ((AppStateSmsPremBridge.SmsState) obj).smsState;
            }
            return 0;
        }

        @Override // miuix.preference.DropDownPreference, androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            if (getIcon() == null) {
                preferenceViewHolder.itemView.post(new Runnable() { // from class: com.android.settings.applications.specialaccess.premiumsms.PremiumSmsAccess.PremiumSmsPreference.1
                    @Override // java.lang.Runnable
                    public void run() {
                        PremiumSmsPreference premiumSmsPreference = PremiumSmsPreference.this;
                        premiumSmsPreference.setIcon(premiumSmsPreference.mAppEntry.icon);
                    }
                });
            }
            super.onBindViewHolder(preferenceViewHolder);
        }
    }

    private void update() {
        updatePrefs(this.mSession.rebuild(AppStateSmsPremBridge.FILTER_APP_PREMIUM_SMS, ApplicationsState.ALPHA_COMPARATOR));
    }

    private void updatePrefs(ArrayList<ApplicationsState.AppEntry> arrayList) {
        if (arrayList == null) {
            return;
        }
        setEmptyText(R.string.premium_sms_none);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        preferenceScreen.setOrderingAsAdded(true);
        for (int i = 0; i < arrayList.size(); i++) {
            PremiumSmsPreference premiumSmsPreference = new PremiumSmsPreference(arrayList.get(i), getPrefContext());
            premiumSmsPreference.setOnPreferenceChangeListener(this);
            preferenceScreen.addPreference(premiumSmsPreference);
        }
        if (arrayList.size() != 0) {
            FooterPreference footerPreference = new FooterPreference(getPrefContext());
            footerPreference.setTitle(R.string.premium_sms_warning);
            preferenceScreen.addPreference(footerPreference);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 388;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.premium_sms_settings;
    }

    void logSpecialPermissionChange(int i, String str) {
        int i2 = i != 1 ? i != 2 ? i != 3 ? 0 : 780 : 779 : 778;
        if (i2 != 0) {
            MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
            metricsFeatureProvider.action(metricsFeatureProvider.getAttribution(getActivity()), i2, getMetricsCategory(), str, i);
        }
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onAllSizesComputed() {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ApplicationsState applicationsState = ApplicationsState.getInstance((Application) getContext().getApplicationContext());
        this.mApplicationsState = applicationsState;
        this.mSession = applicationsState.newSession(this, getSettingsLifecycle());
        this.mSmsBackend = new AppStateSmsPremBridge(getContext(), this.mApplicationsState, this);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mSmsBackend.release();
        this.mSession.onDestroy();
        super.onDestroy();
    }

    @Override // com.android.settings.applications.AppStateBaseBridge.Callback
    public void onExtraInfoUpdated() {
        update();
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLauncherInfoChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLoadEntriesCompleted() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageIconChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageListChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageSizeChanged(String str) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        this.mSmsBackend.pause();
        super.onPause();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        PremiumSmsPreference premiumSmsPreference = (PremiumSmsPreference) preference;
        int parseInt = Integer.parseInt((String) obj);
        logSpecialPermissionChange(parseInt, premiumSmsPreference.mAppEntry.info.packageName);
        this.mSmsBackend.setSmsState(premiumSmsPreference.mAppEntry.info.packageName, parseInt);
        return true;
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
        updatePrefs(arrayList);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mSmsBackend.resume();
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRunningStateChanged(boolean z) {
    }

    @Override // com.android.settings.widget.EmptyTextSettings, com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }
}
