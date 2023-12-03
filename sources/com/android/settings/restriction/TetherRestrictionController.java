package com.android.settings.restriction;

import android.content.Context;
import android.os.UserHandle;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.wifi.TetherStatusController;
import com.android.settingslib.RestrictedLockUtilsInternal;

/* loaded from: classes2.dex */
public class TetherRestrictionController extends TetherStatusController {
    public TetherRestrictionController(Context context, TextView textView) {
        super(context, textView);
    }

    @Override // com.android.settings.wifi.TetherStatusController, com.android.settings.BaseSettingsController
    public void pause() {
        super.pause();
    }

    @Override // com.android.settings.wifi.TetherStatusController, com.android.settings.BaseSettingsController
    public void resume() {
        super.resume();
    }

    @Override // com.android.settings.wifi.TetherStatusController, com.android.settings.BaseSettingsController
    public void updateStatus() {
        super.updateStatus();
        if (this.mStatusView == null || RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_config_tethering", UserHandle.myUserId()) == null) {
            return;
        }
        this.mStatusView.setText(this.mContext.getText(R.string.disabled_by_admin_summary_text));
    }
}
