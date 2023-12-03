package com.android.settings.lab;

import android.content.Context;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.R;
import com.android.settings.widget.MiuiIconCheckBoxPreference;

/* loaded from: classes.dex */
public class MiuiAiPreloadController extends MiuiLabBaseController<MiuiIconCheckBoxPreference> {
    public MiuiAiPreloadController(PreferenceGroup preferenceGroup) {
        super(preferenceGroup);
        if (this.mPreference != 0) {
            if (isNotSupported()) {
                preferenceGroup.removePreference(this.mPreference);
            } else {
                ((MiuiIconCheckBoxPreference) this.mPreference).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.lab.MiuiAiPreloadController.1
                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public boolean onPreferenceChange(Preference preference, Object obj) {
                        MiuiAiPreloadController.this.backgroundStartSwitchPreferenceOnChange(obj);
                        return true;
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void backgroundStartSwitchPreferenceOnChange(Object obj) {
        Settings.System.putInt(((MiuiIconCheckBoxPreference) this.mPreference).getContext().getContentResolver(), "ai_preload_user_state", !((Boolean) obj).booleanValue() ? 0 : 1);
    }

    private void close() {
        ((MiuiIconCheckBoxPreference) this.mPreference).setChecked(false);
        Settings.System.putIntForUser(((MiuiIconCheckBoxPreference) this.mPreference).getContext().getContentResolver(), "ai_preload_user_state", 0, UserHandle.myUserId());
    }

    public static boolean isCloudDisabled() {
        return SystemProperties.getInt("persist.sys.ai_preload_cloud", 0) == 2;
    }

    public static boolean isNotSupported() {
        return SystemProperties.getInt("persist.sys.ai_preload_cloud", 0) == 0;
    }

    @Override // com.android.settings.lab.MiuiLabBaseController
    protected String getPreferenceKey() {
        return "miui_lab_ai_preload";
    }

    @Override // com.android.settings.lab.MiuiLabBaseController
    protected void onClick() {
        Context context = ((MiuiIconCheckBoxPreference) this.mPreference).getContext();
        if (!((MiuiIconCheckBoxPreference) this.mPreference).isChecked()) {
            Settings.System.putIntForUser(context.getContentResolver(), "ai_preload_user_state", 0, UserHandle.myUserId());
        } else if (!isCloudDisabled()) {
            Settings.System.putIntForUser(context.getContentResolver(), "ai_preload_user_state", 1, UserHandle.myUserId());
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.miui_lab_ai_cloud_closed), 0).show();
            ((MiuiIconCheckBoxPreference) this.mPreference).setChecked(false);
            Settings.System.putIntForUser(((MiuiIconCheckBoxPreference) this.mPreference).getContext().getContentResolver(), "ai_preload_user_state", 0, UserHandle.myUserId());
        }
    }

    @Override // com.android.settings.lab.MiuiLabBaseController
    public void onResume() {
        if (isCloudDisabled()) {
            close();
            return;
        }
        T t = this.mPreference;
        ((MiuiIconCheckBoxPreference) t).setChecked(Settings.System.getIntForUser(((MiuiIconCheckBoxPreference) t).getContext().getContentResolver(), "ai_preload_user_state", UserHandle.myUserId(), 0) == 1);
    }
}
