package com.android.settings;

import android.content.Context;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.security.MiuiLockPatternUtils;
import android.view.View;
import androidx.fragment.app.Fragment;

/* loaded from: classes.dex */
public class BluetoothUnlockStateController extends BaseCardViewController {
    private Context mContext;
    private Fragment mFragment;

    public BluetoothUnlockStateController(Context context, CardInfo cardInfo, Fragment fragment) {
        super(context, cardInfo);
        this.mContext = context;
        this.mFragment = fragment;
    }

    private void updateBluetoothState() {
        MiuiLockPatternUtils miuiLockPatternUtils = new MiuiLockPatternUtils(this.mContext);
        this.mCard.setDisable(!MiuiSettings.Secure.hasCommonPassword(this.mContext) || MiuiSecuritySettings.isMiShowMode(this.mContext));
        if (miuiLockPatternUtils.getBluetoothUnlockEnabled()) {
            this.mCard.setChecked(true);
            this.mCard.setValueResId(R.string.on);
            return;
        }
        this.mCard.setChecked(false);
        this.mCard.setValueResId(R.string.off);
    }

    public boolean isAvailable() {
        return UserHandle.myUserId() == 0;
    }

    @Override // com.android.settings.BaseCardViewController, android.view.View.OnClickListener
    public void onClick(View view) {
        MiuiKeyguardSettingsUtils.startFragment(this.mFragment, "com.android.settings.MiuiSecurityBluetoothSettingsFragment", -1, null, R.string.bluetooth_unlock_title);
    }

    @Override // com.android.settings.BaseCardViewController, com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        super.onResume();
        updateBluetoothState();
    }
}
