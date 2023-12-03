package com.android.settings.lab;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.PreferenceGroup;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import java.util.Locale;
import miui.os.Build;

/* loaded from: classes.dex */
public class MiuiDriveModeController extends MiuiLabBaseController<ValuePreference> {
    public MiuiDriveModeController(PreferenceGroup preferenceGroup) {
        super(preferenceGroup);
        T t = this.mPreference;
        if (t != 0) {
            ((ValuePreference) t).setShowRightArrow(true);
            if (isNeedHideDriveMode(((ValuePreference) this.mPreference).getContext())) {
                preferenceGroup.removePreference(this.mPreference);
                this.mPreference = null;
            }
        }
    }

    public static Intent buildDriveModeIntent() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.xiaomi.drivemode", "com.xiaomi.drivemode.MiuiLabDriveModeActivity"));
        return intent;
    }

    public static boolean isDriveModeInstalled(Context context) {
        ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(buildDriveModeIntent(), 0);
        return (resolveActivity == null || resolveActivity.activityInfo == null) ? false : true;
    }

    public static boolean isNeedHideDriveMode(Context context) {
        return (UserHandle.myUserId() == 0 && !Build.IS_TABLET && !Build.IS_GLOBAL_BUILD && Locale.getDefault().equals(Locale.SIMPLIFIED_CHINESE) && isDriveModeInstalled(context)) ? false : true;
    }

    @Override // com.android.settings.lab.MiuiLabBaseController
    protected String getPreferenceKey() {
        return "miui_lab_drive_mode";
    }

    @Override // com.android.settings.lab.MiuiLabBaseController
    protected void onClick() {
        super.onClick();
        ((ValuePreference) this.mPreference).getContext().startActivity(buildDriveModeIntent());
    }

    @Override // com.android.settings.lab.MiuiLabBaseController
    protected void onResume() {
        super.onResume();
        T t = this.mPreference;
        if (t != 0) {
            ((ValuePreference) this.mPreference).setValue(Settings.System.getInt(((ValuePreference) t).getContext().getContentResolver(), "drive_mode_drive_mode", -1) != -1 ? R.string.miui_lab_feature_on : R.string.miui_lab_feature_off);
        }
    }
}
