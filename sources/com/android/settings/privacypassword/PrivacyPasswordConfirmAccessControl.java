package com.android.settings.privacypassword;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.Vibrator;
import android.security.ChooseLockSettingsHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.widget.LockPatternView;
import com.android.settings.FingerprintHelper;
import com.android.settings.FingerprintIdentifyCallback;
import com.android.settings.LockPatternView;
import com.android.settings.R;
import com.android.settings.privacypassword.analytics.AnalyticHelper;
import java.util.List;
import miui.accounts.ExtraAccountManager;
import miui.security.SecurityManager;
import miui.securitycenter.applicationlock.MiuiLockPatternUtilsWrapper;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class PrivacyPasswordConfirmAccessControl extends AppCompatActivity implements View.OnClickListener {
    protected TextView bigTitle;
    private AlertDialog mAccountDialog;
    private View mBackgroundLayout;
    private ChooseLockSettingsHelper mChooseLockSettingsHelper;
    private CountDownTimer mCountdownTimer;
    private AlertDialog mFingerDialog;
    private FingerprintHelper mFingerprintHelper;
    private int mFingerprintId;
    private AlertDialog mForgetPassDialog;
    private View mFrameLockPattern;
    protected TextView mHeaderTextView;
    private ImageView mIconView;
    private boolean mIsEnterFromSetting;
    private boolean mIsInMultiWindow;
    private boolean mIsRegisterFingerprint;
    private boolean mKeyGruadLocked;
    protected MiuiLockPatternUtilsWrapper mLockPatternUtils;
    private int mNumWrongConfirmAttempts;
    private String mPackageName;
    private LinearLayout mPrivacyIconContainer;
    protected TextView mPrivacyPasswordFooterTextView;
    private TextView mPrivacyPasswordForgetPattern;
    private LockPatternView mPrivacyPasswordLockPatternView;
    protected PrivacyPasswordManager mPrivacyPasswordManager;
    private ImageView mPrivacyPasswordMore;
    private View mRelative;
    private Runnable mRunnable;
    private SecurityManager mSecurityManager;
    private RelativeLayout mSplitMaskView;
    private boolean mStop;
    private WindowManager mWindowManager;
    protected TextView privacyPasswordConfirmBack;
    protected TextView privacyPasswordConfirmBackTitle;
    private boolean mTimestart = false;
    private boolean mIsClickedDlg = false;
    private int mFingerErrorCount = 0;
    private boolean mCheckOnPcMode = false;
    private Handler mHandler = new Handler();
    private Runnable mClearPatternRunnable = new Runnable() { // from class: com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl.1
        @Override // java.lang.Runnable
        public void run() {
            PrivacyPasswordConfirmAccessControl.this.mPrivacyPasswordLockPatternView.clearPattern();
        }
    };
    private LockPatternView.OnPatternListener mPrivacyPasswordConfirmExistingLockPatternListener = new LockPatternView.OnPatternListener() { // from class: com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl.2
        @Override // com.android.settings.LockPatternView.OnPatternListener
        public void onPatternCellAdded(List<LockPatternView.Cell> list) {
            Log.i("PrivacyPasswordConfirmAccessControl", "onPatternCellAdded");
        }

        @Override // com.android.settings.LockPatternView.OnPatternListener
        public void onPatternCleared() {
            PrivacyPasswordConfirmAccessControl.this.mPrivacyPasswordLockPatternView.removeCallbacks(PrivacyPasswordConfirmAccessControl.this.mClearPatternRunnable);
        }

        @Override // com.android.settings.LockPatternView.OnPatternListener
        public void onPatternDetected(List<LockPatternView.Cell> list) {
            if (PrivacyPasswordConfirmAccessControl.this.checkPattern(list)) {
                PrivacyPasswordUtils.activateFingerprint(PrivacyPasswordConfirmAccessControl.this.mFingerprintId, UserHandle.myUserId(), PrivacyPasswordConfirmAccessControl.this);
                PrivacyPasswordConfirmAccessControl.this.accessLockPattern();
            } else if (list.size() < 4 || PrivacyPasswordConfirmAccessControl.access$304(PrivacyPasswordConfirmAccessControl.this) < 5) {
                PrivacyPasswordConfirmAccessControl.this.settingTextShake();
                PrivacyPasswordConfirmAccessControl.this.updateStage(Stage.NeedToUnlockWrong);
                PrivacyPasswordConfirmAccessControl.this.postClearPatternRunnable();
            } else {
                long elapsedRealtime = SystemClock.elapsedRealtime() + 30000;
                PrivacyPasswordConfirmAccessControl.this.mPrivacyPasswordManager.setLockoutAttepmpDeadline(elapsedRealtime);
                PrivacyPasswordConfirmAccessControl.this.unregisterFingerprint();
                PrivacyPasswordConfirmAccessControl.this.handleAttemptLockout(elapsedRealtime);
                AnalyticHelper.statsPrivateMistakeReachMax(PrivacyPasswordConfirmAccessControl.this.isBindAccount() ? "binding" : "no_binding");
            }
        }

        @Override // com.android.settings.LockPatternView.OnPatternListener
        public void onPatternStart() {
            PrivacyPasswordConfirmAccessControl.this.mPrivacyPasswordLockPatternView.removeCallbacks(PrivacyPasswordConfirmAccessControl.this.mClearPatternRunnable);
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (((KeyguardManager) PrivacyPasswordConfirmAccessControl.this.getSystemService("keyguard")).isKeyguardLocked()) {
                PrivacyPasswordConfirmAccessControl.this.unregisterFingerprint();
            } else if (PrivacyPasswordConfirmAccessControl.this.mStop) {
            } else {
                PrivacyPasswordConfirmAccessControl.this.registerFingerprintDelayed();
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl$16  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass16 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$privacypassword$PrivacyPasswordConfirmAccessControl$Stage;

        static {
            int[] iArr = new int[Stage.values().length];
            $SwitchMap$com$android$settings$privacypassword$PrivacyPasswordConfirmAccessControl$Stage = iArr;
            try {
                iArr[Stage.NeedToUnlock.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$privacypassword$PrivacyPasswordConfirmAccessControl$Stage[Stage.NeedToUnlockWrong.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$privacypassword$PrivacyPasswordConfirmAccessControl$Stage[Stage.LockedOut.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes2.dex */
    public enum Stage {
        NeedToUnlock,
        NeedToUnlockWrong,
        LockedOut
    }

    static /* synthetic */ int access$1204(PrivacyPasswordConfirmAccessControl privacyPasswordConfirmAccessControl) {
        int i = privacyPasswordConfirmAccessControl.mFingerErrorCount + 1;
        privacyPasswordConfirmAccessControl.mFingerErrorCount = i;
        return i;
    }

    static /* synthetic */ int access$304(PrivacyPasswordConfirmAccessControl privacyPasswordConfirmAccessControl) {
        int i = privacyPasswordConfirmAccessControl.mNumWrongConfirmAttempts + 1;
        privacyPasswordConfirmAccessControl.mNumWrongConfirmAttempts = i;
        return i;
    }

    private void bindAccountRemind() {
        if (isCreateAccountDialog()) {
            if (this.mAccountDialog == null) {
                createBindXiaomiAccountDialog();
                return;
            }
            return;
        }
        AlertDialog alertDialog = this.mAccountDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            return;
        }
        this.mAccountDialog.dismiss();
    }

    private void confirmAccount() {
        if (!PrivacyPasswordUtils.appCheckAccess(this.mSecurityManager, ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME)) {
            PrivacyPasswordUtils.verifyAccountCountDownTimer(this.mSecurityManager, ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME);
        }
        Account findAccounts = PrivacyPasswordUtils.findAccounts(this);
        if (findAccounts != null) {
            AccountManager.get(this).confirmCredentials(findAccounts, null, this, new AccountManagerCallback<Bundle>() { // from class: com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl.12
                @Override // android.accounts.AccountManagerCallback
                public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                    try {
                        boolean z = accountManagerFuture.getResult().getBoolean("booleanResult");
                        PrivacyPasswordConfirmAccessControl privacyPasswordConfirmAccessControl = PrivacyPasswordConfirmAccessControl.this;
                        PrivacyPasswordUtils.postOnCheckPasswordResult(z, privacyPasswordConfirmAccessControl, privacyPasswordConfirmAccessControl.getIntentOnSuccess());
                    } catch (Exception e) {
                        Log.e("PrivacyPasswordConfirmAccessControl", "setPasswordOfXiaomi error", e);
                    }
                }
            }, null);
        }
    }

    private void dealWithSplit() {
        this.mSplitMaskView.setVisibility(0);
        this.mRelative.setVisibility(8);
        this.mFrameLockPattern.setVisibility(8);
    }

    private int getMessageId() {
        return UserHandle.myUserId() == 0 ? R.string.privacy_factory_reset_dlg_message : R.string.privacy_delete_space_dlg_message;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleContentDescription(TextView textView, int i) {
        textView.setText(i);
        textView.announceForAccessibility(getResources().getString(i));
    }

    private void handleExternalScreen() {
        if (PrivacyPasswordUtils.isFoldInternalScreen(this) || this.mCheckOnPcMode) {
            return;
        }
        this.privacyPasswordConfirmBackTitle.setVisibility(4);
        this.bigTitle.setVisibility(0);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mPrivacyIconContainer.getLayoutParams();
        layoutParams.setMargins(0, PrivacyPasswordUtils.getDimen(this, R.dimen.px_189), 0, 0);
        this.mPrivacyIconContainer.setLayoutParams(layoutParams);
        this.mPrivacyIconContainer.requestLayout();
    }

    private void handleFingerprintPosition() {
        int fodPosition;
        if (!isFingerUseful() || PrivacyPasswordUtils.isSideFingerprint() || (fodPosition = PrivacyPasswordUtils.getFodPosition(getApplicationContext())) == 0) {
            return;
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mPrivacyPasswordLockPatternView.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, (PrivacyPasswordUtils.getScreenRealSize(getApplicationContext())[1] - fodPosition) + PrivacyPasswordUtils.getDimen(this, R.dimen.px_fod_margin));
    }

    private void hideNavigationBar() {
        if (PrivacyPasswordUtils.isFodFingerprint()) {
            getWindow().getDecorView().setSystemUiVisibility(12802);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initErrorFingerprintText() {
        this.mPrivacyPasswordFooterTextView.setVisibility(0);
        handleContentDescription(this.mPrivacyPasswordFooterTextView, R.string.fingerprint_not_identified_msg);
        handleContentDescription(this.mHeaderTextView, R.string.confirm_privacy_password);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initFirstUseFingerprintText() {
        this.mPrivacyPasswordFooterTextView.setVisibility(0);
        handleContentDescription(this.mPrivacyPasswordFooterTextView, R.string.privacy_failed_need_to_unlock_nofingerprint);
        handleContentDescription(this.mHeaderTextView, R.string.confirm_privacy_password);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isBindAccount() {
        return this.mPrivacyPasswordManager.getBindXiaoMiAccount() != null && XiaomiAccountUtils.isLoginXiaomiAccount(this) && TextUtils.equals(XiaomiAccountUtils.getLoginedAccountMd5(this), this.mPrivacyPasswordManager.getBindXiaoMiAccount());
    }

    private boolean isCreateAccountDialog() {
        return ((this.mPrivacyPasswordManager.getBindXiaoMiAccount() != null && XiaomiAccountUtils.isLoginXiaomiAccount(this) && TextUtils.equals(XiaomiAccountUtils.getLoginedAccountMd5(this), this.mPrivacyPasswordManager.getBindXiaoMiAccount())) || this.mPrivacyPasswordManager.isNeverRemind()) ? false : true;
    }

    private boolean isCreateFingerprintDialog() {
        return (this.mPrivacyPasswordManager.isNeverRemindOpenFinger() || !this.mFingerprintHelper.isHardwareDetected() || (TransparentHelper.isScreenLockOpen(this) && !this.mFingerprintHelper.getFingerprintIds().isEmpty() && this.mPrivacyPasswordManager.isFingerprintEnable())) ? false : true;
    }

    private boolean isFingerUseful() {
        return TransparentHelper.isScreenLockOpen(this) && this.mFingerprintHelper.isHardwareDetected() && !this.mFingerprintHelper.getFingerprintIds().isEmpty() && this.mPrivacyPasswordManager.isFingerprintEnable();
    }

    private boolean isRealInMultiWindow() {
        try {
            Boolean bool = (Boolean) getClass().getMethod("isInMultiWindowMode", new Class[0]).invoke(this, new Object[0]);
            if (Build.VERSION.SDK_INT >= 24) {
                if (!bool.booleanValue()) {
                    if (!this.mIsInMultiWindow) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e("PrivacyPasswordConfirmAccessControl", "isRealInMultiWindow", e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postClearPatternRunnable() {
        this.mPrivacyPasswordLockPatternView.removeCallbacks(this.mClearPatternRunnable);
        this.mPrivacyPasswordLockPatternView.postDelayed(this.mClearPatternRunnable, 2000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerFingerprint() {
        if (this.mIsInMultiWindow || this.mIsRegisterFingerprint || PrivacyPasswordUtils.getWrongFingerAttempts(this) == 5) {
            Log.d("PrivacyPasswordConfirmAccessControl", "return 1");
            return;
        }
        AlertDialog alertDialog = this.mAccountDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            Log.d("PrivacyPasswordConfirmAccessControl", "return 2");
            return;
        }
        AlertDialog alertDialog2 = this.mForgetPassDialog;
        if (alertDialog2 != null && alertDialog2.isShowing()) {
            Log.d("PrivacyPasswordConfirmAccessControl", "return 5");
        } else if (getLockoutAttepmpDeadline() != 0 || !isFingerUseful()) {
            handleContentDescription(this.mHeaderTextView, getDefaultUnlockString());
            Log.d("PrivacyPasswordConfirmAccessControl", "return 3");
        } else {
            Log.d("PrivacyPasswordConfirmAccessControl", "return 4");
            this.mIsRegisterFingerprint = true;
            FingerprintIdentifyCallback fingerprintIdentifyCallback = new FingerprintIdentifyCallback() { // from class: com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl.5
                @Override // com.android.settings.FingerprintIdentifyCallback
                public void onFailed() {
                    if (PrivacyPasswordConfirmAccessControl.this.getLockoutAttepmpDeadline() != 0) {
                        return;
                    }
                    int wrongFingerAttempts = PrivacyPasswordUtils.getWrongFingerAttempts(PrivacyPasswordConfirmAccessControl.this) + 1;
                    PrivacyPasswordUtils.setWrongFingerAttempts(PrivacyPasswordConfirmAccessControl.this, wrongFingerAttempts);
                    if (PrivacyPasswordConfirmAccessControl.access$1204(PrivacyPasswordConfirmAccessControl.this) >= 5 || wrongFingerAttempts >= 5) {
                        PrivacyPasswordConfirmAccessControl.this.mFingerErrorCount = 0;
                        PrivacyPasswordConfirmAccessControl.this.initErrorFingerprintText();
                        PrivacyPasswordConfirmAccessControl.this.mFingerprintHelper.cancelIdentify();
                        return;
                    }
                    PrivacyPasswordConfirmAccessControl privacyPasswordConfirmAccessControl = PrivacyPasswordConfirmAccessControl.this;
                    privacyPasswordConfirmAccessControl.handleContentDescription(privacyPasswordConfirmAccessControl.mHeaderTextView, R.string.lockpattern_need_to_unlock_wrong);
                    PrivacyPasswordConfirmAccessControl.this.settingTextShake();
                    Vibrator vibrator = (Vibrator) PrivacyPasswordConfirmAccessControl.this.getSystemService("vibrator");
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(200L);
                    }
                }

                @Override // com.android.settings.FingerprintIdentifyCallback
                public void onIdentified(int i) {
                    if (PrivacyPasswordConfirmAccessControl.this.getLockoutAttepmpDeadline() != 0) {
                        return;
                    }
                    if (PrivacyPasswordUtils.isVerifyAndActivate(i, UserHandle.myUserId(), PrivacyPasswordConfirmAccessControl.this)) {
                        PrivacyPasswordConfirmAccessControl privacyPasswordConfirmAccessControl = PrivacyPasswordConfirmAccessControl.this;
                        privacyPasswordConfirmAccessControl.handleContentDescription(privacyPasswordConfirmAccessControl.mHeaderTextView, R.string.confirm_privacy_password_fingerprint);
                        PrivacyPasswordConfirmAccessControl.this.accessLockPattern();
                        PrivacyPasswordConfirmAccessControl.this.mFingerprintHelper.cancelIdentify();
                        return;
                    }
                    PrivacyPasswordConfirmAccessControl.this.mFingerErrorCount = 0;
                    PrivacyPasswordConfirmAccessControl.this.initFirstUseFingerprintText();
                    PrivacyPasswordConfirmAccessControl.this.mFingerprintId = i;
                    PrivacyPasswordConfirmAccessControl.this.mFingerprintHelper.cancelIdentify();
                }
            };
            try {
                FingerprintHelper fingerprintHelper = this.mFingerprintHelper;
                fingerprintHelper.identify(fingerprintIdentifyCallback, fingerprintHelper.getFingerprintIds());
            } catch (Exception e) {
                Log.e("PrivacyPasswordConfirmAccessControl", "finger identify error", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerFingerprintDelayed() {
        Runnable runnable = this.mRunnable;
        if (runnable != null) {
            this.mHandler.removeCallbacks(runnable);
        }
        Handler handler = this.mHandler;
        Runnable runnable2 = new Runnable() { // from class: com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl.15
            @Override // java.lang.Runnable
            public void run() {
                PrivacyPasswordConfirmAccessControl.this.registerFingerprint();
            }
        };
        this.mRunnable = runnable2;
        handler.postDelayed(runnable2, 200L);
    }

    private void resetTopLayout() {
        this.mSplitMaskView.setVisibility(8);
        this.mRelative.setVisibility(0);
        this.mFrameLockPattern.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFingerprint() {
        if (this.mFingerprintHelper.isHardwareDetected()) {
            this.mFingerDialog.setOnDismissListener(null);
            boolean isScreenLockOpen = TransparentHelper.isScreenLockOpen(this);
            if (isScreenLockOpen && !this.mFingerprintHelper.getFingerprintIds().isEmpty()) {
                this.mPrivacyPasswordManager.setFingerprintEnable(true);
                Toast.makeText(this, R.string.privacy_password_use_finger_success, 0).show();
                finish();
            } else if (!isScreenLockOpen && !this.mFingerprintHelper.getFingerprintIds().isEmpty()) {
                Intent intent = new Intent("android.app.action.SET_NEW_PASSWORD");
                if (UserHandle.myUserId() != 0) {
                    PrivacyPasswordUtils.putIntentExtra(this, intent);
                }
                startActivityForResult(intent, 10010);
            } else {
                Intent intent2 = new Intent();
                intent2.setComponent(new ComponentName("com.android.settings", "com.android.settings.NewFingerprintInternalActivity"));
                if (UserHandle.myUserId() != 0) {
                    PrivacyPasswordUtils.putIntentExtra(this, intent2);
                }
                startActivityForResult(intent2, 10010);
            }
        }
    }

    private void setFingerprintRevive() {
        if (PrivacyPasswordUtils.getWrongFingerAttempts(this) != 0) {
            PrivacyPasswordUtils.setWrongFingerAttempts(this, 0);
            if (Build.VERSION.SDK_INT < 23 || !isFingerUseful()) {
                return;
            }
            PrivacyPasswordUtils.invokeResetTimeout(this);
        }
    }

    private void showFactoryResetDialog() {
        unregisterFingerprint();
        AlertDialog create = new AlertDialog.Builder(this).setTitle(R.string.privacy_factory_reset_dlg_title).setMessage(getMessageId()).setNegativeButton(R.string.privacy_dlg_button_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl.14
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                PrivacyPasswordConfirmAccessControl.this.registerFingerprintDelayed();
            }
        }).setPositiveButton(R.string.privacy_factory_reset_dlg_button_text, new DialogInterface.OnClickListener() { // from class: com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl.13
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (UserHandle.myUserId() != 0) {
                    PrivacyPasswordConfirmAccessControl.this.startActivity(new Intent("miui.intent.action.PRIVATE_SPACE_SETTING"));
                    return;
                }
                Intent intent = new Intent();
                intent.setClassName("com.android.settings", "com.android.settings.SubSettings");
                intent.putExtra(":android:show_fragment", "com.android.settings.MiuiMasterClear");
                PrivacyPasswordConfirmAccessControl.this.startActivity(intent);
            }
        }).setCancelable(false).create();
        this.mForgetPassDialog = create;
        create.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterFingerprint() {
        Runnable runnable;
        if (Build.VERSION.SDK_INT >= 24 && (runnable = this.mRunnable) != null) {
            this.mHandler.removeCallbacks(runnable);
        }
        this.mIsRegisterFingerprint = false;
        this.mFingerprintHelper.cancelIdentify();
    }

    private void updateResoureForPCMode() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mPrivacyIconContainer.getLayoutParams();
        layoutParams.topMargin = getResources().getDimensionPixelOffset(R.dimen.privacy_password_top_layout_margin_top_pc);
        this.mPrivacyIconContainer.setLayoutParams(layoutParams);
    }

    private void useFingerprintRemind() {
        if (this.mFingerDialog == null) {
            createOpenFingerprintDialog();
        }
    }

    protected void accessLockPattern() {
        setFingerprintRevive();
        if (!TextUtils.isEmpty(this.mPackageName) && (this.mPrivacyPasswordManager.getACLockMode() != 1 || !this.mPrivacyPasswordManager.isConvenientEnabled())) {
            this.mSecurityManager.addAccessControlPass(this.mPackageName);
        }
        this.mFingerprintHelper.cancelIdentify();
        setResult(-1);
        if (!this.mIsEnterFromSetting && this.mAccountDialog == null && isCreateFingerprintDialog()) {
            useFingerprintRemind();
        } else {
            finish();
        }
    }

    protected boolean checkPattern(List<LockPatternView.Cell> list) {
        return this.mChooseLockSettingsHelper.utils().checkMiuiLockPatternAsUser(list, UserHandle.myUserId());
    }

    protected void createBindXiaomiAccountDialog() {
        unregisterFingerprint();
        AlertDialog create = new AlertDialog.Builder(this).setTitle(R.string.privacy_password_remind_bindaccount_title).setMessage(R.string.privacy_password_remind_bind_notlogin_account).setCheckBox(false, getResources().getString(R.string.privacy_password_never_remind)).setNegativeButton(getResources().getString(R.string.privacy_dlg_button_cancel), new DialogInterface.OnClickListener() { // from class: com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                PrivacyPasswordConfirmAccessControl.this.mIsClickedDlg = true;
                boolean isChecked = ((AlertDialog) dialogInterface).isChecked();
                PrivacyPasswordConfirmAccessControl.this.mPrivacyPasswordManager.setNerverRemind(isChecked);
                PrivacyPasswordConfirmAccessControl.this.mPrivacyPasswordManager.bindXiaoMiAccount(null);
                if (isChecked) {
                    AnalyticHelper.statsApp1UnlockBindingPopup("cancel_forever");
                } else {
                    AnalyticHelper.statsApp1UnlockBindingPopup("cancel");
                }
            }
        }).setPositiveButton(R.string.privacy_password_bind_account_immediate, new DialogInterface.OnClickListener() { // from class: com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                PrivacyPasswordConfirmAccessControl.this.mIsClickedDlg = true;
                boolean isChecked = ((AlertDialog) dialogInterface).isChecked();
                PrivacyPasswordConfirmAccessControl.this.mPrivacyPasswordManager.setNerverRemind(isChecked);
                Intent intent = new Intent(PrivacyPasswordConfirmAccessControl.this, TransparentHelper.class);
                intent.putExtra("bind_account_extra", "bind_account");
                PrivacyPasswordConfirmAccessControl.this.startActivity(intent);
                if (isChecked) {
                    AnalyticHelper.statsApp1UnlockBindingPopup("binding_forever");
                } else {
                    AnalyticHelper.statsApp1UnlockBindingPopup("binding");
                }
            }
        }).create();
        this.mAccountDialog = create;
        create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl.8
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                PrivacyPasswordConfirmAccessControl.this.registerFingerprintDelayed();
                if (PrivacyPasswordConfirmAccessControl.this.mIsClickedDlg) {
                    return;
                }
                AnalyticHelper.statsApp1UnlockBindingPopup("cancel");
            }
        });
        this.mAccountDialog.show();
    }

    protected void createOpenFingerprintDialog() {
        this.mPrivacyPasswordLockPatternView.clearPattern();
        AlertDialog create = new AlertDialog.Builder(this).setTitle(R.string.privacy_password_use_finger_dialog_title).setMessage(R.string.privacy_password_use_finger_dialog_mess).setCheckBox(false, getResources().getString(R.string.privacy_password_never_remind)).setNegativeButton(getResources().getString(R.string.privacy_dlg_button_cancel), new DialogInterface.OnClickListener() { // from class: com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl.10
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                PrivacyPasswordConfirmAccessControl.this.mPrivacyPasswordManager.setFingerprintEnable(false);
                PrivacyPasswordConfirmAccessControl.this.mPrivacyPasswordManager.setIsNeverRemindOpenFinger(((AlertDialog) dialogInterface).isChecked());
            }
        }).setPositiveButton(R.string.privacy_password_use_finger_dialog_open, new DialogInterface.OnClickListener() { // from class: com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl.9
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                PrivacyPasswordConfirmAccessControl.this.mPrivacyPasswordManager.setIsNeverRemindOpenFinger(((AlertDialog) dialogInterface).isChecked());
                PrivacyPasswordConfirmAccessControl.this.setFingerprint();
            }
        }).create();
        this.mFingerDialog = create;
        create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl.11
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                PrivacyPasswordConfirmAccessControl.this.finish();
            }
        });
        this.mFingerDialog.show();
    }

    protected int getDefaultUnlockString() {
        if (isFingerUseful() && PrivacyPasswordUtils.getWrongFingerAttempts(this) < 5) {
            return R.string.confirm_privacy_password_fingerprint;
        }
        return R.string.confirm_privacy_password;
    }

    protected Intent getIntentOnSuccess() {
        Intent intent = new Intent(this, PrivacyPasswordChooseAccessControl.class);
        intent.putExtra("privacy_password_extra_data", "ModifyPassword");
        return intent;
    }

    protected long getLockoutAttepmpDeadline() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long lockoutAttepmDeadline = this.mPrivacyPasswordManager.getLockoutAttepmDeadline();
        if (lockoutAttepmDeadline < elapsedRealtime || lockoutAttepmDeadline > elapsedRealtime + 30000) {
            return 0L;
        }
        return lockoutAttepmDeadline;
    }

    protected boolean getTactileFeedbackEnabled() {
        return this.mLockPatternUtils.isTactileFeedbackEnabled();
    }

    protected void handleAttemptLockout(long j) {
        updateStage(Stage.LockedOut);
        this.mBackgroundLayout.setBackgroundColor(getResources().getColor(R.color.password_confirm_fail_bg_color));
        CountDownTimer countDownTimer = new CountDownTimer(j - SystemClock.elapsedRealtime(), 1000L) { // from class: com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl.4
            @Override // android.os.CountDownTimer
            public void onFinish() {
                PrivacyPasswordConfirmAccessControl.this.mTimestart = false;
                PrivacyPasswordConfirmAccessControl.this.mPrivacyPasswordLockPatternView.disableInput();
                PrivacyPasswordConfirmAccessControl.this.mNumWrongConfirmAttempts = 0;
                PrivacyPasswordConfirmAccessControl.this.updateStage(Stage.NeedToUnlock);
                PrivacyPasswordConfirmAccessControl.this.mBackgroundLayout.setBackgroundColor(PrivacyPasswordConfirmAccessControl.this.getResources().getColor(R.color.main_page_background));
                if (PrivacyPasswordConfirmAccessControl.this.mStop) {
                    return;
                }
                PrivacyPasswordConfirmAccessControl.this.registerFingerprintDelayed();
            }

            @Override // android.os.CountDownTimer
            public void onTick(long j2) {
                PrivacyPasswordConfirmAccessControl.this.mTimestart = true;
                PrivacyPasswordConfirmAccessControl.this.mPrivacyPasswordLockPatternView.disableInput();
                PrivacyPasswordConfirmAccessControl privacyPasswordConfirmAccessControl = PrivacyPasswordConfirmAccessControl.this;
                privacyPasswordConfirmAccessControl.handleContentDescription(privacyPasswordConfirmAccessControl.mHeaderTextView, R.string.lockpattern_too_many_failed_confirmation_attempts_header);
                int i = (int) (j2 / 1000);
                PrivacyPasswordConfirmAccessControl privacyPasswordConfirmAccessControl2 = PrivacyPasswordConfirmAccessControl.this;
                privacyPasswordConfirmAccessControl2.mPrivacyPasswordFooterTextView.setText(privacyPasswordConfirmAccessControl2.getResources().getQuantityString(R.plurals.lockpattern_too_many_failed_confirmation_attempts_footer, i, Integer.valueOf(i)));
            }
        };
        this.mCountdownTimer = countDownTimer;
        if (this.mTimestart) {
            return;
        }
        countDownTimer.start();
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 10000) {
            if (i2 == -1) {
                AnalyticHelper.statsPrivateForgetFinish();
                setFingerprintRevive();
            }
            finish();
        } else if (i != 10010) {
        } else {
            this.mPrivacyPasswordManager.setFingerprintEnable(i2 == -1);
            if (i2 == -1) {
                Toast.makeText(this, R.string.privacy_password_use_finger_success, 0).show();
            }
            finish();
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        setResult(0);
        super.onBackPressed();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view == this.mPrivacyPasswordForgetPattern) {
            if (isBindAccount()) {
                confirmAccount();
            } else if (this.mPrivacyPasswordManager.isUsedPrivacyInBussiness()) {
                showFactoryResetDialog();
            } else {
                Intent intent = new Intent(this, PrivacyPasswordChooseAccessControl.class);
                intent.putExtra("privacy_password_extra_data", "ModifyPassword");
                startActivityForResult(intent, 10000);
            }
            AnalyticHelper.statsClickPrivateForget(this.mNumWrongConfirmAttempts >= 5 ? "after_reach_max" : "before_reach_max");
        } else if (view == this.privacyPasswordConfirmBack) {
            setResult(0);
            finish();
        } else if (view == this.mPrivacyPasswordMore) {
            Intent intent2 = new Intent(this, TransparentHelper.class);
            intent2.putExtra("enter_from_settings", this.mIsEnterFromSetting);
            startActivity(intent2);
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        boolean z = (getResources().getConfiguration().uiMode & 8192) != 0;
        this.mCheckOnPcMode = z;
        if (z) {
            setContentView(R.layout.privacy_password_confirm_lock_pattern);
        } else {
            setContentView(R.layout.privacy_password_confirm_lock_pattern_cetus);
        }
        setTitle(R.string.privacy_password);
        getWindow().addFlags(8192);
        this.privacyPasswordConfirmBack = (TextView) findViewById(R.id.privacy_password_confirm_back);
        this.privacyPasswordConfirmBackTitle = (TextView) findViewById(R.id.privacy_password_confirm_back_title);
        this.privacyPasswordConfirmBack.setOnClickListener(this);
        this.privacyPasswordConfirmBackTitle.setSelected(true);
        this.privacyPasswordConfirmBack.setContentDescription(getResources().getString(R.string.setup_password_back));
        this.mFingerprintHelper = new FingerprintHelper(this);
        this.mPrivacyPasswordManager = PrivacyPasswordManager.getInstance(getApplicationContext());
        this.mChooseLockSettingsHelper = new ChooseLockSettingsHelper(this, 3);
        this.mSecurityManager = (SecurityManager) getSystemService("security");
        this.mLockPatternUtils = new MiuiLockPatternUtilsWrapper(this);
        this.mBackgroundLayout = findViewById(R.id.backgroundlayout);
        Intent intent = getIntent();
        if (intent != null) {
            String stringExtra = intent.getStringExtra("android.intent.action.CREATE_SHORTCUT");
            this.mPackageName = stringExtra;
            if (TextUtils.isEmpty(stringExtra)) {
                this.mPackageName = intent.getStringExtra("android.intent.extra.shortcut.NAME");
            }
        }
        if (!isFingerUseful()) {
            this.mPrivacyPasswordManager.setFingerprintEnable(false);
        }
        this.mPrivacyIconContainer = (LinearLayout) findViewById(R.id.privacy_password_icon_container);
        this.mHeaderTextView = (TextView) findViewById(R.id.privacy_password_header_text);
        this.mPrivacyPasswordLockPatternView = (com.android.settings.LockPatternView) findViewById(R.id.privacy_password_lockpatternView);
        this.mPrivacyPasswordFooterTextView = (TextView) findViewById(R.id.privacy_password_footer_text);
        TextView textView = (TextView) findViewById(R.id.privacy_password_forget_pattern);
        this.mPrivacyPasswordForgetPattern = textView;
        textView.setOnClickListener(this);
        this.mPrivacyPasswordLockPatternView.setTactileFeedbackEnabled(getTactileFeedbackEnabled());
        this.mPrivacyPasswordLockPatternView.setOnPatternListener(this.mPrivacyPasswordConfirmExistingLockPatternListener);
        this.mPrivacyPasswordLockPatternView.setInStealthMode(!this.mPrivacyPasswordManager.isVisibilePattern());
        updateStage(Stage.NeedToUnlock);
        if (bundle != null) {
            this.mNumWrongConfirmAttempts = bundle.getInt("privacy_num_wrong_attempts");
        } else {
            onCreateNoSaveState();
        }
        this.mKeyGruadLocked = getIntent().getBooleanExtra("miui.KEYGUARD_LOCKED", false);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.USER_PRESENT");
        intentFilter.addAction("miui.intent.action.APP_LOCK_CLEAR_STATE");
        registerReceiver(this.mReceiver, intentFilter);
        ImageView imageView = (ImageView) findViewById(R.id.privacy_password_more);
        this.mPrivacyPasswordMore = imageView;
        imageView.setOnClickListener(this);
        boolean booleanExtra = getIntent().getBooleanExtra("enter_from_settings", false);
        this.mIsEnterFromSetting = booleanExtra;
        if (!booleanExtra) {
            this.mPrivacyPasswordMore.setVisibility(0);
        }
        this.mRelative = findViewById(R.id.relativeBackground);
        this.mFrameLockPattern = findViewById(R.id.lockpattern_framelayout);
        this.mIconView = (ImageView) findViewById(R.id.privacy_password_icon);
        this.mSplitMaskView = (RelativeLayout) findViewById(R.id.split_screen_layout);
        this.mWindowManager = (WindowManager) getSystemService("window");
        if (PrivacyPasswordUtils.isNotch()) {
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.top_actionBar);
            PrivacyPasswordUtils.adapteNotch(this, this.mRelative);
        }
        hideNavigationBar();
        PrivacyPasswordUtils.upgradeFingerprints(this, UserHandle.myUserId(), this.mFingerprintHelper);
        if (this.mCheckOnPcMode) {
            updateResoureForPCMode();
        }
        this.bigTitle = (TextView) findViewById(R.id.big_title);
        handleExternalScreen();
        handleFingerprintPosition();
    }

    protected void onCreateNoSaveState() {
        if (this.mPrivacyPasswordManager.havePattern()) {
            return;
        }
        finish();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        CountDownTimer countDownTimer = this.mCountdownTimer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        AlertDialog alertDialog = this.mAccountDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        AlertDialog alertDialog2 = this.mFingerDialog;
        if (alertDialog2 != null) {
            alertDialog2.dismiss();
        }
        AlertDialog alertDialog3 = this.mForgetPassDialog;
        if (alertDialog3 != null) {
            alertDialog3.dismiss();
        }
        super.onDestroy();
        unregisterReceiver(this.mReceiver);
    }

    @Override // android.app.Activity
    public void onMultiWindowModeChanged(boolean z, Configuration configuration) {
        super.onMultiWindowModeChanged(z, configuration);
        if (Build.VERSION.SDK_INT >= 29 && PrivacyPasswordUtils.getCurrentWindowMode(configuration) == 1) {
            if (isFingerUseful()) {
                this.mIsRegisterFingerprint = false;
                registerFingerprintDelayed();
            }
            recreate();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        if (this.mCountdownTimer != null) {
            this.mTimestart = false;
        }
        unregisterFingerprint();
    }

    @Override // android.app.Activity
    protected void onRestart() {
        super.onRestart();
        this.mPrivacyPasswordFooterTextView.setText((CharSequence) null);
        handleContentDescription(this.mHeaderTextView, getDefaultUnlockString());
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        AlertDialog alertDialog;
        super.onResume();
        if (!this.mPrivacyPasswordManager.havePattern()) {
            finish();
        }
        if (!isRealInMultiWindow() || this.mCheckOnPcMode) {
            resetTopLayout();
        } else {
            this.mIsInMultiWindow = true;
            dealWithSplit();
        }
        long lockoutAttepmpDeadline = getLockoutAttepmpDeadline();
        if (lockoutAttepmpDeadline != 0) {
            handleAttemptLockout(lockoutAttepmpDeadline);
        } else if (!this.mPrivacyPasswordLockPatternView.isEnabled()) {
            this.mNumWrongConfirmAttempts = 0;
            updateStage(Stage.NeedToUnlock);
        }
        registerFingerprintDelayed();
        if (this.mPrivacyPasswordManager.isFingerprintEnable() && PrivacyPasswordUtils.getWrongFingerAttempts(this) == 5) {
            initErrorFingerprintText();
        }
        if (this.mIsEnterFromSetting || PrivacyPasswordUtils.getCurrentWindowMode(getResources().getConfiguration()) == 5 || this.mIsInMultiWindow) {
            return;
        }
        bindAccountRemind();
        if (this.mPrivacyPasswordManager.isFingerprintEnable() && (alertDialog = this.mFingerDialog) != null && alertDialog.isShowing()) {
            this.mFingerDialog.dismiss();
            finish();
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("privacy_num_wrong_attempts", this.mNumWrongConfirmAttempts);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        this.mStop = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        this.mStop = true;
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (Build.VERSION.SDK_INT < 24) {
            return;
        }
        if (!z) {
            unregisterFingerprint();
            return;
        }
        hideNavigationBar();
        if (isFingerUseful()) {
            this.mIsRegisterFingerprint = false;
            registerFingerprintDelayed();
        }
    }

    public void settingTextShake() {
        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, -30.0f, 0.0f, 0.0f);
        translateAnimation.setStartOffset(0L);
        translateAnimation.setDuration(50L);
        translateAnimation.setInterpolator(new DecelerateInterpolator());
        TranslateAnimation translateAnimation2 = new TranslateAnimation(-30.0f, 30.0f, 0.0f, 0.0f);
        translateAnimation2.setStartOffset(50L);
        translateAnimation2.setDuration(100L);
        translateAnimation2.setInterpolator(new AccelerateDecelerateInterpolator());
        TranslateAnimation translateAnimation3 = new TranslateAnimation(30.0f, 0.0f, 0.0f, 0.0f);
        translateAnimation3.setStartOffset(150L);
        translateAnimation3.setDuration(50L);
        translateAnimation3.setInterpolator(new AccelerateDecelerateInterpolator());
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setRepeatCount(2);
        animationSet.setRepeatMode(2);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(translateAnimation2);
        animationSet.addAnimation(translateAnimation3);
        this.mHeaderTextView.startAnimation(animationSet);
    }

    @TargetApi(16)
    protected void updateStage(Stage stage) {
        int i = AnonymousClass16.$SwitchMap$com$android$settings$privacypassword$PrivacyPasswordConfirmAccessControl$Stage[stage.ordinal()];
        if (i == 1) {
            if (!this.mPrivacyPasswordManager.isFingerprintEnable() || PrivacyPasswordUtils.getWrongFingerAttempts(this) < 5) {
                handleContentDescription(this.mHeaderTextView, getDefaultUnlockString());
                this.mPrivacyPasswordFooterTextView.setText((CharSequence) null);
            } else {
                initErrorFingerprintText();
            }
            this.mPrivacyPasswordLockPatternView.setEnabled(true);
            this.mPrivacyPasswordLockPatternView.enableInput();
        } else if (i == 2) {
            handleContentDescription(this.mHeaderTextView, R.string.lockpattern_need_to_unlock_wrong);
            this.mPrivacyPasswordFooterTextView.setText((CharSequence) null);
            this.mPrivacyPasswordLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
            this.mPrivacyPasswordLockPatternView.setEnabled(true);
            this.mPrivacyPasswordLockPatternView.enableInput();
        } else if (i == 3) {
            this.mPrivacyPasswordLockPatternView.clearPattern();
            this.mPrivacyPasswordLockPatternView.setEnabled(true);
        }
        TextView textView = this.mHeaderTextView;
        textView.announceForAccessibility(textView.getText());
    }
}
