package com.android.settings.vpn2;

import android.util.Log;
import android.widget.TextView;
import com.android.settings.BaseSettingsController;
import com.android.settings.R;

/* loaded from: classes2.dex */
public class VpnStatusController extends BaseSettingsController {
    private String TAG;
    private VpnManager mVpnManager;

    private void setVpnTitle(int i) {
        Log.v(this.TAG, "setVpnTitle, status = " + i);
        TextView textView = this.mStatusView;
        if (textView == null) {
            return;
        }
        if (i == 2) {
            textView.setText(R.string.connecting_to_vpn);
        } else if (i != 3) {
            textView.setText(R.string.vpn_off);
        } else {
            textView.setText(R.string.vpn_on);
        }
    }

    @Override // com.android.settings.BaseSettingsController
    public void pause() {
    }

    @Override // com.android.settings.BaseSettingsController
    public void resume() {
        setVpnTitle(this.mVpnManager.getVpnConnectionStatus());
    }

    @Override // com.android.settings.BaseSettingsController
    public void updateStatus() {
        setVpnTitle(this.mVpnManager.getVpnConnectionStatus());
    }
}
