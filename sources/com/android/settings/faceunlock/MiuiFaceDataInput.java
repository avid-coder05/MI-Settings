package com.android.settings.faceunlock;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.face.FaceManager;
import android.os.Bundle;
import android.os.UserHandle;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.MiuiSecuritySettings;
import com.android.settings.R;
import com.android.settingslib.util.ToastUtil;

/* loaded from: classes.dex */
public class MiuiFaceDataInput extends Activity {
    private byte[] mFaceEnrollToken;
    private boolean mIsKeyguardPasswordSecured;
    private LockPatternUtils mLockPatternUtils;
    private boolean mConfirmLockLaunched = false;
    private boolean mNeedSkipConfirmPassword = true;

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startFaceOrConfirmPwd$0(Intent intent, int i, int i2, long j) {
        intent.putExtra("challenge", j);
        intent.putExtra(":android:show_fragment_title", R.string.empty_title);
        intent.putExtra("has_challenge", false);
        startActivityForResult(intent, 2);
    }

    private void startFaceEnroll() {
        Intent intent = new Intent();
        if (KeyguardSettingsFaceUnlockUtils.isSupportMultiFaceInput(getApplicationContext())) {
            intent.setClassName("com.android.settings", "com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput");
        } else {
            intent.setClassName("com.android.settings", "com.android.settings.faceunlock.MiuiNormalCameraFaceInput");
        }
        intent.putExtra("for_face_enroll", this.mFaceEnrollToken);
        intent.putExtra("for_face_enroll_from_normal", true);
        startActivityForResult(intent, 101);
    }

    private void startFaceOrConfirmPwd() {
        Intent intent = getIntent();
        LockPatternUtils lockPatternUtils = new LockPatternUtils(this);
        this.mLockPatternUtils = lockPatternUtils;
        this.mIsKeyguardPasswordSecured = lockPatternUtils.isSecure(UserHandle.myUserId());
        boolean booleanExtra = intent.getBooleanExtra("input_facedata_need_skip_password", false);
        this.mNeedSkipConfirmPassword = booleanExtra;
        boolean z = this.mIsKeyguardPasswordSecured;
        if (!z || this.mConfirmLockLaunched) {
            if (z) {
                return;
            }
            Intent intent2 = new Intent();
            intent2.setClassName("com.android.settings", MiuiFaceDataIntroduction.class.getName());
            startActivityForResult(intent2, 1);
            this.mConfirmLockLaunched = true;
        } else if (booleanExtra) {
            startFaceEnroll();
        } else {
            this.mConfirmLockLaunched = true;
            final Intent intent3 = new Intent();
            intent3.setClassName("com.android.settings", "com.android.settings.MiuiConfirmCommonPassword");
            KeyguardSettingsFaceUnlockManager.getInstance(this).generateFaceEnrollChallenge(new FaceManager.GenerateChallengeCallback() { // from class: com.android.settings.faceunlock.MiuiFaceDataInput$$ExternalSyntheticLambda0
                public final void onGenerateChallengeResult(int i, int i2, long j) {
                    MiuiFaceDataInput.this.lambda$startFaceOrConfirmPwd$0(intent3, i, i2, j);
                }
            });
        }
    }

    private void startFacePrompt() {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.faceunlock.MiuiFaceDataPrompt");
        startActivityForResult(intent, 102);
    }

    @Override // android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 2 || i == 1) {
            if (i2 != -1) {
                setResult(0);
                finish();
            } else if (KeyguardSettingsFaceUnlockUtils.isLargeScreen(this)) {
                startFacePrompt();
            } else {
                if (intent != null) {
                    this.mFaceEnrollToken = intent.getByteArrayExtra("hw_auth_token");
                }
                startFaceEnroll();
            }
        } else if (i == 101) {
            if (i2 == 103) {
                startFacePrompt();
                return;
            }
            if (!KeyguardSettingsFaceUnlockUtils.isDeviceProvisioned(this)) {
                i2 = -1;
            }
            setResult(i2);
            finish();
        } else if (i == 102) {
            if (i2 == -1) {
                this.mConfirmLockLaunched = false;
                startFaceOrConfirmPwd();
                return;
            }
            setResult(0);
            finish();
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (MiuiSecuritySettings.isMiShowMode(this)) {
            ToastUtil.show(this, R.string.mishow_disable_password_setting, 0);
            finish();
        } else if (KeyguardSettingsFaceUnlockUtils.isLargeScreen(this)) {
            startFacePrompt();
        } else {
            if (bundle != null) {
                this.mConfirmLockLaunched = bundle.getBoolean("key_confirm_lock_launched");
            }
            startFaceOrConfirmPwd();
        }
    }

    @Override // android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("key_confirm_lock_launched", this.mConfirmLockLaunched);
    }
}
