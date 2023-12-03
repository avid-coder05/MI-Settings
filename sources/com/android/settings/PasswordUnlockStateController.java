package com.android.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.View;
import androidx.fragment.app.Fragment;
import com.android.internal.widget.LockPatternUtils;
import java.util.List;
import miui.securityspace.CrossUserUtils;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class PasswordUnlockStateController extends BaseCardViewController {
    private FingerprintHelper mFingerprintHelper;
    private Fragment mFragment;
    private LockPatternUtils mLockPatternUtils;

    public PasswordUnlockStateController(Context context, CardInfo cardInfo, Fragment fragment) {
        super(context, cardInfo);
        this.mLockPatternUtils = new LockPatternUtils(this.mContext);
        this.mFingerprintHelper = new FingerprintHelper(this.mContext);
        this.mFragment = fragment;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createNewPassword() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("add_keyguard_password_then_add_fingerprint", !keyguardPasswordExisted());
        MiuiKeyguardSettingsUtils.startFragment(this.mFragment, "com.android.settings.MiuiSecurityChooseUnlock$MiuiSecurityChooseUnlockFragment", 0, bundle, keyguardPasswordExisted() ? R.string.change_lock_screen_password_title : R.string.password_entrance_title);
    }

    private int getFingerprintSize() {
        List<String> fingerprintIds = this.mFingerprintHelper.getFingerprintIds();
        if (fingerprintIds == null) {
            return 0;
        }
        return fingerprintIds.size();
    }

    private boolean keyguardPasswordExisted() {
        return this.mLockPatternUtils.getActivePasswordQuality(UserHandle.myUserId()) != 0;
    }

    public void handleActivityResult(int i, int i2, Intent intent) {
    }

    public boolean isAvailable() {
        return UserHandle.myUserId() == 0 || CrossUserUtils.isAirSpace(this.mContext, UserHandle.myUserId());
    }

    @Override // com.android.settings.BaseCardViewController, android.view.View.OnClickListener
    public void onClick(View view) {
        if (keyguardPasswordExisted() || !this.mFingerprintHelper.isHardwareDetected() || getFingerprintSize() <= 0) {
            createNewPassword();
            return;
        }
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.PasswordUnlockStateController.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    PasswordUnlockStateController.this.mFingerprintHelper.removeAllFingerprint(null);
                }
                PasswordUnlockStateController.this.createNewPassword();
            }
        };
        new AlertDialog.Builder(this.mContext).setCancelable(false).setIconAttribute(16843605).setMessage(R.string.delete_or_keep_legacy_passwords_confirm_msg).setPositiveButton(R.string.delete_legacy_fingerprint, onClickListener).setNegativeButton(R.string.keep_legacy_fingerprint, onClickListener).create().show();
    }

    @Override // com.android.settings.BaseCardViewController, com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        super.onResume();
        if (keyguardPasswordExisted()) {
            this.mCard.setChecked(true);
            this.mCard.setValueResId(R.string.on);
            MiuiSecureFlagUtils.openSecureFlag();
            return;
        }
        this.mCard.setChecked(false);
        this.mCard.setValueResId(R.string.off);
        MiuiSecureFlagUtils.closeSecureFlag();
    }
}
