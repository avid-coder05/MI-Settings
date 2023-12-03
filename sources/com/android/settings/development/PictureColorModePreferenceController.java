package com.android.settings.development;

import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

/* loaded from: classes.dex */
public class PictureColorModePreferenceController extends DeveloperOptionsPreferenceController implements LifecycleObserver, OnResume, OnPause, PreferenceControllerMixin {
    private ColorModePreference mPreference;

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ColorModePreference colorModePreference = (ColorModePreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = colorModePreference;
        if (colorModePreference != null) {
            colorModePreference.updateCurrentAndSupported();
        }
    }

    int getColorModeDescriptionsSize() {
        return ColorModePreference.getColorModeDescriptions(this.mContext).size();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "picture_color_mode";
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return getColorModeDescriptionsSize() > 1 && !isWideColorGamut();
    }

    boolean isWideColorGamut() {
        return this.mContext.getResources().getConfiguration().isScreenWideColorGamut();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        ColorModePreference colorModePreference = this.mPreference;
        if (colorModePreference == null) {
            return;
        }
        colorModePreference.stopListening();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        ColorModePreference colorModePreference = this.mPreference;
        if (colorModePreference == null) {
            return;
        }
        colorModePreference.startListening();
        this.mPreference.updateCurrentAndSupported();
    }
}
