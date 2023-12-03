package com.android.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Slog;
import android.view.View;
import androidx.fragment.app.Fragment;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.faceunlock.KeyguardSettingsFaceUnlockUtils;
import com.android.settings.faceunlock.MiuiFaceDataInput;
import java.util.List;

/* loaded from: classes.dex */
public class FaceUnlockStateController extends BaseCardViewController {
    private Activity mActivity;
    private Fragment mFragment;
    private boolean mHasClickFaceUnlock;
    private LockPatternUtils mLockPatternUtils;

    public FaceUnlockStateController(Context context, CardInfo cardInfo, Fragment fragment) {
        super(context, cardInfo);
        this.mLockPatternUtils = new LockPatternUtils(this.mContext);
        this.mFragment = fragment;
        this.mActivity = fragment.getActivity();
    }

    private void addFaceData(boolean z) {
        Intent intent = new Intent(this.mActivity, MiuiFaceDataInput.class);
        intent.putExtra("input_facedata_need_skip_password", z);
        intent.putExtra(":android:show_fragment_title", R.string.empty_title);
        this.mActivity.startActivity(intent);
    }

    private int getFaceDataSize() {
        List<String> enrolledFaceList = KeyguardSettingsFaceUnlockUtils.getEnrolledFaceList(this.mContext);
        if (enrolledFaceList == null) {
            return 0;
        }
        return enrolledFaceList.size();
    }

    private void handleClick() {
        if (getFaceDataSize() == 0) {
            addFaceData(false);
        } else if (!keyguardPasswordExisted()) {
            toFaceManageFragment();
        } else {
            Intent intent = new Intent(this.mContext, MiuiConfirmCommonPassword.class);
            intent.putExtra(":android:show_fragment_title", R.string.empty_title);
            this.mFragment.startActivityForResult(intent, 1002);
        }
    }

    private boolean keyguardPasswordExisted() {
        return this.mLockPatternUtils.getActivePasswordQuality(UserHandle.myUserId()) != 0;
    }

    private void toFaceManageFragment() {
        MiuiKeyguardSettingsUtils.startFragment(this.mFragment, "com.android.settings.faceunlock.MiuiFaceDataManage$FaceManageFragment", -1, null, R.string.face_unlock);
    }

    public void handleActivityResult(int i, int i2, Intent intent) {
        if (i == 1002 && i2 == -1) {
            toFaceManageFragment();
        } else if (i == 1 && i2 == -1) {
            addFaceData(true);
        }
    }

    public boolean isAvailable() {
        return KeyguardSettingsFaceUnlockUtils.isSupportFaceUnlock(this.mContext) && UserHandle.myUserId() == 0;
    }

    @Override // com.android.settings.BaseCardViewController, android.view.View.OnClickListener
    public void onClick(View view) {
        handleClick();
    }

    @Override // com.android.settings.BaseCardViewController, com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        super.onResume();
        this.mHasClickFaceUnlock = false;
        boolean z = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "face_unlcok_apply_for_lock", 1, UserHandle.myUserId()) == 1;
        boolean hasEnrolledFaces = KeyguardSettingsFaceUnlockUtils.hasEnrolledFaces(this.mContext);
        Slog.i("miui_face", "face unlock enable: " + z + " hasEnrolledFaces: " + hasEnrolledFaces);
        if (z && hasEnrolledFaces) {
            this.mCard.setChecked(true);
            this.mCard.setValueResId(R.string.on);
            return;
        }
        this.mCard.setChecked(false);
        this.mCard.setValueResId(R.string.off);
    }
}
