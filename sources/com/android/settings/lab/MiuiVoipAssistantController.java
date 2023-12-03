package com.android.settings.lab;

import android.content.ComponentName;
import android.content.Intent;
import android.os.SystemProperties;
import android.provider.Settings;
import androidx.preference.PreferenceGroup;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import java.util.Locale;
import miui.os.Build;

/* loaded from: classes.dex */
public class MiuiVoipAssistantController extends MiuiLabBaseController<ValuePreference> {
    public MiuiVoipAssistantController(PreferenceGroup preferenceGroup) {
        super(preferenceGroup);
        T t = this.mPreference;
        if (t != 0) {
            ((ValuePreference) t).setShowRightArrow(true);
            if (isNotSupported()) {
                preferenceGroup.removePreference(this.mPreference);
                this.mPreference = null;
            }
        }
    }

    public static Intent buildDriveModeIntent() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.miui.audiomonitor", "com.miui.audiomonitor.VoipAssistantActivity"));
        return intent;
    }

    public static boolean isNotSupported() {
        return (!Build.IS_GLOBAL_BUILD && SystemProperties.getBoolean("ro.vendor.audio.voip.assistant", false) && Locale.getDefault().equals(Locale.SIMPLIFIED_CHINESE)) ? false : true;
    }

    @Override // com.android.settings.lab.MiuiLabBaseController
    protected String getPreferenceKey() {
        return "miui_voip_assistant_screen";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.lab.MiuiLabBaseController
    public void onClick() {
        super.onClick();
        ((ValuePreference) this.mPreference).getContext().startActivity(buildDriveModeIntent());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.lab.MiuiLabBaseController
    public void onResume() {
        super.onResume();
        T t = this.mPreference;
        if (t != 0) {
            ((ValuePreference) this.mPreference).setValue(Settings.Global.getInt(((ValuePreference) t).getContext().getContentResolver(), "persist.sys.voip_record.open", 0) != 0 ? R.string.voip_assistant_settings_on : R.string.voip_assistant_settings_off);
        }
    }
}
