package com.android.settings.lab;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.PreferenceGroup;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes.dex */
public class MiuiAiAsstCallScreenController extends MiuiLabBaseController<ValuePreference> {
    public MiuiAiAsstCallScreenController(PreferenceGroup preferenceGroup) {
        super(preferenceGroup);
        T t = this.mPreference;
        if (t != 0) {
            ((ValuePreference) t).setShowRightArrow(true);
            if (isNeedHideCallScreen(((ValuePreference) this.mPreference).getContext())) {
                preferenceGroup.removePreference(this.mPreference);
                this.mPreference = null;
            }
        }
    }

    public static Intent buildIntent() {
        Intent intent = new Intent("com.xiaomi.aiasst.service.lab.launcher");
        intent.setPackage("com.xiaomi.aiasst.service");
        return intent;
    }

    public static final boolean isNeedHideCallScreen(Context context) {
        boolean z = Settings.Secure.getInt(context.getContentResolver(), "com.xiaomi.aiasst.service.preferences.key_call_screen_visible", 1) == 1;
        boolean canFindActivity = MiuiUtils.getInstance().canFindActivity(context, buildIntent());
        if (z && canFindActivity) {
            return false;
        }
        Log.i("MiuiAiAsstCallScreenController", "MiuiAiAsstCallScreenController is need hide");
        return true;
    }

    @Override // com.android.settings.lab.MiuiLabBaseController
    protected String getPreferenceKey() {
        return "miui_aiasst_call_screen";
    }

    @Override // com.android.settings.lab.MiuiLabBaseController
    protected void onClick() {
        super.onClick();
        Intent buildIntent = buildIntent();
        if (this.mPreference == 0 || !MiuiUtils.getInstance().canFindActivity(((ValuePreference) this.mPreference).getContext(), buildIntent)) {
            return;
        }
        ((ValuePreference) this.mPreference).getContext().startActivity(buildIntent);
    }

    @Override // com.android.settings.lab.MiuiLabBaseController
    protected void onResume() {
        super.onResume();
        T t = this.mPreference;
        if (t != 0) {
            ((ValuePreference) t).setValue(Settings.Secure.getInt(((ValuePreference) t).getContext().getContentResolver(), "com.xiaomi.aiasst.service.preferences.key_can_use_call_screen", 0) == 1 ? R.string.miui_lab_feature_on : R.string.miui_lab_feature_off);
        }
    }
}
