package com.android.settings.lab;

import android.content.Context;
import android.provider.MiuiSettings;
import androidx.preference.PreferenceGroup;
import com.android.settings.widget.MiuiIconCheckBoxPreference;
import miui.os.DeviceFeature;

/* loaded from: classes.dex */
public class MiuiLabGestureController extends MiuiLabBaseController<MiuiIconCheckBoxPreference> {
    public MiuiLabGestureController(PreferenceGroup preferenceGroup) {
        super(preferenceGroup);
        if (this.mPreference == 0 || !isNotSupported()) {
            return;
        }
        preferenceGroup.removePreference(this.mPreference);
    }

    public static boolean isNotSupported() {
        return !DeviceFeature.SUPPORT_LAB_GESTURE;
    }

    @Override // com.android.settings.lab.MiuiLabBaseController
    protected String getPreferenceKey() {
        return "miui_lab_gesture";
    }

    @Override // com.android.settings.lab.MiuiLabBaseController
    protected void onClick() {
        Context context = ((MiuiIconCheckBoxPreference) this.mPreference).getContext();
        if (((MiuiIconCheckBoxPreference) this.mPreference).isChecked()) {
            MiuiSettings.Global.putBoolean(context.getContentResolver(), "force_fsg_nav_bar", true);
        } else {
            MiuiSettings.Global.putBoolean(context.getContentResolver(), "force_fsg_nav_bar", false);
        }
    }

    @Override // com.android.settings.lab.MiuiLabBaseController
    public void onResume() {
        T t = this.mPreference;
        ((MiuiIconCheckBoxPreference) t).setChecked(MiuiSettings.Global.getBoolean(((MiuiIconCheckBoxPreference) t).getContext().getContentResolver(), "force_fsg_nav_bar"));
    }
}
