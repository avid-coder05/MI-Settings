package com.android.settings.restriction;

import android.content.Context;
import android.os.UserHandle;
import android.widget.TextView;
import com.android.settings.BaseSettingsController;
import com.android.settings.R;
import com.android.settingslib.RestrictedLockUtilsInternal;

/* loaded from: classes2.dex */
public class SimManagementRestrictionController extends BaseSettingsController {
    public SimManagementRestrictionController(Context context, TextView textView) {
        super(context, textView);
    }

    @Override // com.android.settings.BaseSettingsController
    public void pause() {
    }

    @Override // com.android.settings.BaseSettingsController
    public void resume() {
    }

    @Override // com.android.settings.BaseSettingsController
    public void updateStatus() {
        if (this.mStatusView != null) {
            if (RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_config_mobile_networks", UserHandle.myUserId()) != null) {
                this.mStatusView.setText(this.mContext.getText(R.string.disabled_by_admin_summary_text));
            } else {
                this.mStatusView.setText("");
            }
        }
    }
}
