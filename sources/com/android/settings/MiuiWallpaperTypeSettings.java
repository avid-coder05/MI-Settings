package com.android.settings;

import android.content.Intent;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import miui.app.constants.ThemeManagerConstants;

/* loaded from: classes.dex */
public class MiuiWallpaperTypeSettings extends SettingsPreferenceFragment {
    private Preference getDesktopPreference() {
        Intent intent = new Intent();
        intent.putExtra(ThemeManagerConstants.REQUEST_RESOURCE_CODE, "wallpaper");
        intent.setClassName("com.android.thememanager", "com.android.thememanager.activity.ThemeTabActivity");
        Preference preference = new Preference(getPrefContext());
        preference.setIntent(intent);
        preference.setTitle(R.string.wallpaper_desktop);
        return preference;
    }

    private Preference getLiveWallpaperPreference() {
        Intent intent = new Intent();
        intent.setClassName("com.android.wallpaper.livepicker", "com.android.wallpaper.livepicker.LiveWallpaperActivity");
        Preference preference = new Preference(getPrefContext());
        preference.setIntent(intent);
        preference.setTitle(R.string.wallpaper_live);
        return preference;
    }

    private Preference getLockscreenPreference() {
        Intent intent = new Intent();
        intent.putExtra(ThemeManagerConstants.REQUEST_RESOURCE_CODE, "lockscreen");
        intent.setClassName("com.android.thememanager", "com.android.thememanager.activity.ThemeTabActivity");
        Preference preference = new Preference(getPrefContext());
        preference.setIntent(intent);
        preference.setTitle(R.string.wallpaper_lockscreen);
        return preference;
    }

    private void populateWallpaperTypes() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.addPreference(getDesktopPreference());
        preferenceScreen.addPreference(getLockscreenPreference());
        preferenceScreen.addPreference(getLiveWallpaperPreference());
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiWallpaperTypeSettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.wallpaper_settings);
        populateWallpaperTypes();
    }
}
