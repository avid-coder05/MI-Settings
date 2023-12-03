package com.android.settings.development;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.security.AdbUtils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.AbstractEnableAdbPreferenceController;

/* loaded from: classes.dex */
public class AdbPreferenceController extends AbstractEnableAdbPreferenceController implements PreferenceControllerMixin, OnActivityResultListener {
    private final int REQUEST_CODE;
    private final DevelopmentSettingsDashboardFragment mFragment;

    public AdbPreferenceController(Context context, DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment) {
        super(context);
        this.REQUEST_CODE = 12;
        this.mFragment = developmentSettingsDashboardFragment;
    }

    @Override // com.android.settings.development.OnActivityResultListener
    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (i == 12) {
            if (i2 == -1) {
                this.mFragment.onEnableAdbDialogConfirmed();
                return true;
            }
            this.mFragment.onEnableAdbDialogDismissed();
            return true;
        }
        return false;
    }

    public void onAdbDialogConfirmed() {
        writeAdbSetting(true);
    }

    public void onAdbDialogDismissed() {
        updateState(((AbstractEnableAdbPreferenceController) this).mPreference);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        writeAdbSetting(false);
        ((AbstractEnableAdbPreferenceController) this).mPreference.setChecked(false);
    }

    @Override // com.android.settingslib.core.ConfirmationDialogController
    public void showConfirmationDialog(Preference preference) {
        EnableAdbWarningDialog.show(this.mFragment);
    }

    @Override // com.android.settingslib.development.AbstractEnableAdbPreferenceController
    public boolean showMiuiInterceptPage() {
        Intent interceptIntent = AdbUtils.getInterceptIntent("", "miui_open_debug", "");
        FragmentActivity activity = this.mFragment.getActivity();
        if (AdbUtils.isIntentEnable(activity, interceptIntent)) {
            activity.startActivityForResult(interceptIntent, 12);
            return true;
        }
        return false;
    }
}
