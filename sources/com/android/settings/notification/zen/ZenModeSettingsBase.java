package com.android.settings.notification.zen;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import miui.provider.ExtraTelephony;

/* loaded from: classes2.dex */
public abstract class ZenModeSettingsBase extends RestrictedDashboardFragment {
    protected static final boolean DEBUG = Log.isLoggable("ZenModeSettings", 3);
    protected ZenModeBackend mBackend;
    protected Context mContext;
    private final Handler mHandler;
    private final SettingsObserver mSettingsObserver;
    protected int mZenMode;

    /* loaded from: classes2.dex */
    private final class SettingsObserver extends ContentObserver {
        private final Uri ZEN_MODE_CONFIG_ETAG_URI;
        private final Uri ZEN_MODE_URI;

        private SettingsObserver() {
            super(ZenModeSettingsBase.this.mHandler);
            this.ZEN_MODE_URI = Settings.Global.getUriFor(ExtraTelephony.ZEN_MODE);
            this.ZEN_MODE_CONFIG_ETAG_URI = Settings.Global.getUriFor("zen_mode_config_etag");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (this.ZEN_MODE_URI.equals(uri)) {
                ZenModeSettingsBase.this.updateZenMode(true);
            }
            if (this.ZEN_MODE_CONFIG_ETAG_URI.equals(uri)) {
                ZenModeSettingsBase.this.mBackend.updatePolicy();
                ZenModeSettingsBase.this.onZenModeConfigChanged();
            }
        }

        public void register() {
            ZenModeSettingsBase.this.getContentResolver().registerContentObserver(this.ZEN_MODE_URI, false, this);
            ZenModeSettingsBase.this.getContentResolver().registerContentObserver(this.ZEN_MODE_CONFIG_ETAG_URI, false, this);
        }

        public void unregister() {
            ZenModeSettingsBase.this.getContentResolver().unregisterContentObserver(this);
        }
    }

    public ZenModeSettingsBase() {
        super("no_adjust_volume");
        this.mHandler = new Handler();
        this.mSettingsObserver = new SettingsObserver();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateZenMode(boolean z) {
        int i = Settings.Global.getInt(getContentResolver(), ExtraTelephony.ZEN_MODE, this.mZenMode);
        if (i == this.mZenMode) {
            return;
        }
        this.mZenMode = i;
        if (DEBUG) {
            Log.d("ZenModeSettings", "updateZenMode mZenMode=" + this.mZenMode + " " + z);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ZenModeSettings";
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mBackend = ZenModeBackend.getInstance(context);
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        updateZenMode(false);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mSettingsObserver.unregister();
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateZenMode(true);
        this.mSettingsObserver.register();
        if (isUiRestricted()) {
            if (isUiRestrictedByOnlyAdmin()) {
                getPreferenceScreen().removeAll();
            } else {
                finish();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onZenModeConfigChanged() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updatePreference(AbstractPreferenceController abstractPreferenceController) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (abstractPreferenceController.isAvailable()) {
            String preferenceKey = abstractPreferenceController.getPreferenceKey();
            Preference findPreference = preferenceScreen.findPreference(preferenceKey);
            if (findPreference == null) {
                Log.d("ZenModeSettings", String.format("Cannot find preference with key %s in Controller %s", preferenceKey, abstractPreferenceController.getClass().getSimpleName()));
            } else {
                abstractPreferenceController.updateState(findPreference);
            }
        }
    }
}
