package com.android.settings.lab;

import android.provider.Settings;
import android.util.MiuiMultiWindowUtils;
import androidx.preference.PreferenceGroup;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import miui.os.Build;
import miui.util.DeviceLevel;

/* loaded from: classes.dex */
public class MiuiFlashbackController extends MiuiLabBaseController<ValuePreference> {
    public MiuiFlashbackController(PreferenceGroup preferenceGroup) {
        super(preferenceGroup);
        if (this.mPreference == 0 || !isNotSupported()) {
            return;
        }
        preferenceGroup.removePreference(this.mPreference);
        this.mPreference = null;
    }

    public static boolean isNotSupported() {
        if (MiuiMultiWindowUtils.supportFreeform() && !Build.IS_INTERNATIONAL_BUILD && !Build.IS_TABLET) {
            String str = android.os.Build.DEVICE;
            if (!"cetus".equals(str) && !DeviceLevel.IS_MIUI_LITE_VERSION && !"zizhan".equals(str)) {
                return false;
            }
        }
        return true;
    }

    @Override // com.android.settings.lab.MiuiLabBaseController
    protected String getPreferenceKey() {
        return "flashback_entrance_preference";
    }

    @Override // com.android.settings.lab.MiuiLabBaseController
    public void onResume() {
        try {
            int i = Settings.System.getInt(((ValuePreference) this.mPreference).getContext().getContentResolver(), "FlashBackMainSwitch", -1);
            if (i == -1) {
                i = 1;
            }
            ((ValuePreference) this.mPreference).setText(i == 1 ? R.string.flashback_status_open : R.string.flashback_status_close);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
