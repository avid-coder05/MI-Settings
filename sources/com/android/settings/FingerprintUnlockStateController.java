package com.android.settings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.View;
import androidx.fragment.app.Fragment;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.NewFingerprintInternalActivity;
import com.android.settings.utils.MiuiGxzwUtils;
import com.android.settings.utils.TabletUtils;
import java.util.List;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class FingerprintUnlockStateController extends BaseCardViewController {
    private Activity mActivity;
    private FingerprintHelper mFingerprintHelper;
    private Fragment mFragment;
    private LockPatternUtils mLockPatternUtils;

    public FingerprintUnlockStateController(Context context, CardInfo cardInfo, Fragment fragment) {
        super(context, cardInfo);
        this.mLockPatternUtils = new LockPatternUtils(this.mContext);
        this.mFingerprintHelper = new FingerprintHelper(this.mContext);
        this.mFragment = fragment;
        this.mActivity = fragment.getActivity();
    }

    private void addFingerprint(int i) {
        if (i >= 5) {
            showInformationDialog(this.mContext.getString(R.string.max_fingerprint_number_reached));
        } else if (TabletUtils.IS_TABLET) {
            MiuiKeyguardSettingsUtils.startFragment(this.mFragment, MiuiGxzwUtils.isGxzwSensor() ? GxzwNewFingerprintFragment.class.getName() : NewFingerprintInternalActivity.NewFingerprintFragment.class.getName(), 0, null, R.string.add_fingerprint_text);
        } else {
            Intent intent = new Intent(this.mActivity, NewFingerprintInternalActivity.class);
            intent.putExtra(":android:show_fragment_title", R.string.empty_title);
            this.mActivity.startActivity(intent);
        }
    }

    private int getFingerprintSize() {
        List<String> fingerprintIds = this.mFingerprintHelper.getFingerprintIds();
        if (fingerprintIds == null) {
            return 0;
        }
        return fingerprintIds.size();
    }

    private void handleClick() {
        int fingerprintSize = getFingerprintSize();
        if (fingerprintSize == 0) {
            addFingerprint(fingerprintSize);
        } else if (!keyguardPasswordExisted()) {
            toFingerprintManageFragment();
        } else {
            Intent intent = new Intent(this.mContext, MiuiConfirmCommonPassword.class);
            int i = R.string.empty_title;
            intent.putExtra(":android:show_fragment_title", i);
            intent.putExtra("confirm_password_request_code", 1001);
            if (TabletUtils.IS_TABLET) {
                MiuiKeyguardSettingsUtils.startFragment(this.mFragment, MiuiConfirmCommonPassword.getExtraFragmentName(), 1001, intent.getExtras(), i);
            } else {
                this.mFragment.startActivityForResult(intent, 1001);
            }
        }
    }

    private boolean keyguardPasswordExisted() {
        return this.mLockPatternUtils.getActivePasswordQuality(UserHandle.myUserId()) != 0;
    }

    private void showInformationDialog(String str) {
        new AlertDialog.Builder(this.mActivity).setCancelable(false).setIconAttribute(16843605).setMessage(str).setPositiveButton(R.string.information_dialog_button_text, (DialogInterface.OnClickListener) null).create().show();
    }

    private void toFingerprintManageFragment() {
        MiuiKeyguardSettingsUtils.startFragment(this.mFragment, "com.android.settings.FingerprintManageSetting$FingerprintManageFragment", -1, null, R.string.privacy_password_use_finger_dialog_title);
    }

    public void handleActivityResult(int i, int i2, Intent intent) {
        if (i == 1001) {
            if (TabletUtils.IS_TABLET) {
                if (i2 != 0) {
                    return;
                }
            } else if (i2 != -1) {
                return;
            }
            toFingerprintManageFragment();
        }
    }

    public boolean isAvailable() {
        return this.mFingerprintHelper.isHardwareDetected() && UserHandle.myUserId() == 0;
    }

    @Override // com.android.settings.BaseCardViewController, android.view.View.OnClickListener
    public void onClick(View view) {
        handleClick();
    }

    @Override // com.android.settings.BaseCardViewController, com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        super.onResume();
        if (!(Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "miui_keyguard", 2, UserHandle.myUserId()) == 2) || getFingerprintSize() <= 0) {
            this.mCard.setChecked(false);
            this.mCard.setValueResId(R.string.off);
            return;
        }
        this.mCard.setChecked(true);
        this.mCard.setValueResId(R.string.on);
    }
}
