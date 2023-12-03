package com.android.settings.wifi.p2p;

import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

/* loaded from: classes2.dex */
public abstract class P2pCategoryPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    protected PreferenceGroup mCategory;

    public P2pCategoryPreferenceController(Context context) {
        super(context);
    }

    public void addChild(Preference preference) {
        PreferenceGroup preferenceGroup = this.mCategory;
        if (preferenceGroup != null) {
            preferenceGroup.addPreference(preference);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mCategory = (PreferenceGroup) preferenceScreen.findPreference(getPreferenceKey());
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public void removeAllChildren() {
        PreferenceGroup preferenceGroup = this.mCategory;
        if (preferenceGroup != null) {
            preferenceGroup.removeAll();
        }
    }

    public void setEnabled(boolean z) {
        PreferenceGroup preferenceGroup = this.mCategory;
        if (preferenceGroup != null) {
            preferenceGroup.setEnabled(z);
        }
    }
}
