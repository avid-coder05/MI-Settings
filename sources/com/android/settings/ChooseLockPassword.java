package com.android.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserManager;
import android.preference.PreferenceFrameLayout;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.security.MiuiLockPatternUtils;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.VerifyCredentialResponse;
import com.android.settings.ChooseLockPassword;
import com.android.settings.LockPatternChecker;
import com.android.settings.compat.LockPatternUtilsCompat;
import com.android.settings.faceunlock.KeyguardSettingsFaceUnlockManager;
import com.android.settings.faceunlock.KeyguardSettingsFaceUnlockUtils;
import com.android.settings.utils.MiuiSecurityUtils;
import com.android.settingslib.miuisettings.preference.PreferenceActivity;
import java.util.List;
import miui.securityspace.CrossUserUtils;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;
import miuix.provision.ProvisionBaseActivity;

/* loaded from: classes.dex */
public class ChooseLockPassword extends SettingsCompatActivity {

    /* loaded from: classes.dex */
    public static class ChooseLockPasswordFragment extends KeyguardSettingsPreferenceFragment implements View.OnClickListener, TextWatcher {
        protected ImageView mBackImage;
        protected TextView mCancelButton;
        private MiuiChooseLockSettingsHelper mChooseLockSettingsHelper;
        private boolean mEnableKeyguardPassword;
        private String mFirstPin;
        protected TextView mHeaderText;
        private InputMethodManager mInputMethodManager;
        protected boolean mIsAlphaMode;
        private boolean mIsSetPasswordForAirSpace;
        private boolean mIsSetPasswordForOwnerUser;
        private boolean mIsSetPasswordForSSUser;
        private ProgressDialog mLoadingDialog;
        private MiuiLockPatternUtils mLockPatternUtils;
        protected TextView mNextButton;
        protected ImageView mNextImage;
        protected TextView mPasswordEntry;
        private byte[] mPasswordHistoryHashFactor;
        private PasswordMetricsWrapper mPasswordMetricsWrapper;
        private AsyncTask<?, ?, ?> mPendingLockCheck;
        private int mSecuritySpaceId;
        private int mUserIdToSetPassword;
        private String mUserPassword;
        private int mPasswordMinLength = 4;
        private int mPasswordMaxLength = 16;
        private int mPasswordMinLetters = 0;
        private int mPasswordMinUpperCase = 0;
        private int mPasswordMinLowerCase = 0;
        private int mPasswordMinSymbols = 0;
        private int mPasswordMinNumeric = 0;
        private int mPasswordMinNonLetter = 0;
        private boolean mPasswordNumSequenceAllowed = true;
        private int mRequestedMinComplexity = 0;
        private int mRequestedQuality = 131072;
        private Stage mUiStage = Stage.Introduction;
        private FingerprintHelper mFingerprintHelper = null;
        private boolean mIsConfirmPasswordWrong = false;
        private int mSystemUserPasswordLength = 0;
        private int mSystemUserPasswordQuality = 0;
        private boolean mAddKeyguardpasswordThenAddFingerprint = false;
        private boolean mAddKeyguardpasswordThenAddFaceRecoginition = false;
        private Handler mHandler = new Handler() { // from class: com.android.settings.ChooseLockPassword.ChooseLockPasswordFragment.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == 1) {
                    ChooseLockPasswordFragment.this.updateStage((Stage) message.obj);
                }
            }
        };
        private ClipData mClipData = null;
        Runnable mShowSoftInputRunnable = new Runnable() { // from class: com.android.settings.ChooseLockPassword.ChooseLockPasswordFragment.4
            @Override // java.lang.Runnable
            public void run() {
                ChooseLockPasswordFragment.this.mInputMethodManager.showSoftInput(ChooseLockPasswordFragment.this.mPasswordEntry, 1);
            }
        };

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.settings.ChooseLockPassword$ChooseLockPasswordFragment$7  reason: invalid class name */
        /* loaded from: classes.dex */
        public class AnonymousClass7 extends AsyncTask<Void, Void, byte[]> {
            final /* synthetic */ boolean val$isNeedAddFace;
            final /* synthetic */ boolean val$isShowDialogToAddFingerprint;
            final /* synthetic */ boolean val$mChallenge;
            final /* synthetic */ String val$pin;
            final /* synthetic */ LockPatternUtils val$utils;

            AnonymousClass7(String str, boolean z, boolean z2, boolean z3, LockPatternUtils lockPatternUtils) {
                this.val$pin = str;
                this.val$isShowDialogToAddFingerprint = z;
                this.val$isNeedAddFace = z2;
                this.val$mChallenge = z3;
                this.val$utils = lockPatternUtils;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$doInBackground$0(boolean z) {
                ChooseLockPasswordFragment.this.onPasswordSaved(null, z);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$doInBackground$1(byte[] bArr, boolean z, LockPatternUtils lockPatternUtils, long j) {
                ChooseLockPasswordFragment.this.onPasswordSaved(bArr, z);
                LockPatternUtilsCompat.removeGatekeeperPasswordHandle(lockPatternUtils, j);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$doInBackground$2(VerifyCredentialResponse verifyCredentialResponse, final LockPatternUtils lockPatternUtils, Activity activity, final boolean z, int i, int i2, long j) {
                final long gatekeeperPasswordHandle = verifyCredentialResponse.getGatekeeperPasswordHandle();
                final byte[] verifyGatekeeperPasswordHandle = LockPatternUtilsCompat.verifyGatekeeperPasswordHandle(lockPatternUtils, gatekeeperPasswordHandle, j, ChooseLockPasswordFragment.this.mUserIdToSetPassword);
                activity.runOnUiThread(new Runnable() { // from class: com.android.settings.ChooseLockPassword$ChooseLockPasswordFragment$7$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChooseLockPassword.ChooseLockPasswordFragment.AnonymousClass7.this.lambda$doInBackground$1(verifyGatekeeperPasswordHandle, z, lockPatternUtils, gatekeeperPasswordHandle);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$doInBackground$3(byte[] bArr, boolean z, LockPatternUtils lockPatternUtils, long j) {
                ChooseLockPasswordFragment.this.onPasswordSaved(bArr, z);
                LockPatternUtilsCompat.removeGatekeeperPasswordHandle(lockPatternUtils, j);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$doInBackground$4(VerifyCredentialResponse verifyCredentialResponse, final LockPatternUtils lockPatternUtils, Activity activity, final boolean z, int i, int i2, long j) {
                final long gatekeeperPasswordHandle = verifyCredentialResponse.getGatekeeperPasswordHandle();
                final byte[] verifyGatekeeperPasswordHandle = LockPatternUtilsCompat.verifyGatekeeperPasswordHandle(lockPatternUtils, gatekeeperPasswordHandle, j, ChooseLockPasswordFragment.this.mUserIdToSetPassword);
                activity.runOnUiThread(new Runnable() { // from class: com.android.settings.ChooseLockPassword$ChooseLockPasswordFragment$7$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChooseLockPassword.ChooseLockPasswordFragment.AnonymousClass7.this.lambda$doInBackground$3(verifyGatekeeperPasswordHandle, z, lockPatternUtils, gatekeeperPasswordHandle);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public byte[] doInBackground(Void... voidArr) {
                final boolean z;
                final FragmentActivity activity = ChooseLockPasswordFragment.this.getActivity();
                if (activity == null) {
                    return null;
                }
                try {
                    MiuiLockPatternUtils miuiLockPatternUtils = new MiuiLockPatternUtils(activity);
                    String str = this.val$pin;
                    ChooseLockPasswordFragment chooseLockPasswordFragment = ChooseLockPasswordFragment.this;
                    LockPatternUtilsCompat.saveLockPassword(miuiLockPatternUtils, str, !chooseLockPasswordFragment.mIsAlphaMode, chooseLockPasswordFragment.mUserPassword, ChooseLockPasswordFragment.this.mRequestedQuality, ChooseLockPasswordFragment.this.mUserIdToSetPassword);
                    z = this.val$isShowDialogToAddFingerprint;
                } catch (Exception e) {
                    Log.d("ChooseLockPassword", "critical: no token returned for known good pattern", e);
                }
                if (!z && !this.val$isNeedAddFace && !this.val$mChallenge) {
                    activity.runOnUiThread(new Runnable() { // from class: com.android.settings.ChooseLockPassword$ChooseLockPasswordFragment$7$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChooseLockPassword.ChooseLockPasswordFragment.AnonymousClass7.this.lambda$doInBackground$0(z);
                        }
                    });
                    return null;
                }
                final VerifyCredentialResponse verifyPassword = LockPatternUtilsCompat.verifyPassword(this.val$utils, this.val$pin, ChooseLockPasswordFragment.this.mUserIdToSetPassword);
                if (this.val$isNeedAddFace) {
                    KeyguardSettingsFaceUnlockManager keyguardSettingsFaceUnlockManager = KeyguardSettingsFaceUnlockManager.getInstance(activity);
                    final LockPatternUtils lockPatternUtils = this.val$utils;
                    final boolean z2 = this.val$isShowDialogToAddFingerprint;
                    keyguardSettingsFaceUnlockManager.generateFaceEnrollChallenge(new FaceManager.GenerateChallengeCallback() { // from class: com.android.settings.ChooseLockPassword$ChooseLockPasswordFragment$7$$ExternalSyntheticLambda0
                        public final void onGenerateChallengeResult(int i, int i2, long j) {
                            ChooseLockPassword.ChooseLockPasswordFragment.AnonymousClass7.this.lambda$doInBackground$4(verifyPassword, lockPatternUtils, activity, z2, i, i2, j);
                        }
                    });
                } else {
                    FingerprintHelper fingerprintHelper = ChooseLockPasswordFragment.this.mFingerprintHelper;
                    int i = ChooseLockPasswordFragment.this.mUserIdToSetPassword;
                    final LockPatternUtils lockPatternUtils2 = this.val$utils;
                    final boolean z3 = this.val$isShowDialogToAddFingerprint;
                    fingerprintHelper.generateChallenge(i, new FingerprintManager.GenerateChallengeCallback() { // from class: com.android.settings.ChooseLockPassword$ChooseLockPasswordFragment$7$$ExternalSyntheticLambda1
                        public final void onChallengeGenerated(int i2, int i3, long j) {
                            ChooseLockPassword.ChooseLockPasswordFragment.AnonymousClass7.this.lambda$doInBackground$2(verifyPassword, lockPatternUtils2, activity, z3, i2, i3, j);
                        }
                    });
                }
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(byte[] bArr) {
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Enum visitor error
        jadx.core.utils.exceptions.JadxRuntimeException: Init of enum field 'Introduction' uses external variables
        	at jadx.core.dex.visitors.EnumVisitor.createEnumFieldByConstructor(EnumVisitor.java:451)
        	at jadx.core.dex.visitors.EnumVisitor.processEnumFieldByRegister(EnumVisitor.java:395)
        	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromFilledArray(EnumVisitor.java:324)
        	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromInsn(EnumVisitor.java:262)
        	at jadx.core.dex.visitors.EnumVisitor.convertToEnum(EnumVisitor.java:151)
        	at jadx.core.dex.visitors.EnumVisitor.visit(EnumVisitor.java:100)
         */
        /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
        /* loaded from: classes.dex */
        public static final class Stage {
            private static final /* synthetic */ Stage[] $VALUES;
            public static final Stage ConfirmWrong;
            public static final Stage Introduction;
            public static final Stage NeedToConfirm;
            public final int alphaHint;
            public final int buttonText;
            public final int numericHint;

            static {
                int i = R.string.lockpassword_choose_your_password_header;
                int i2 = R.string.lockpassword_choose_your_pin_title;
                int i3 = R.string.lockpassword_continue_label;
                Stage stage = new Stage("Introduction", 0, i, i2, i3);
                Introduction = stage;
                Stage stage2 = new Stage("NeedToConfirm", 1, R.string.lockpassword_confirm_your_password_header, R.string.lockpassword_confirm_your_pin_header, R.string.lockpassword_ok_label);
                NeedToConfirm = stage2;
                Stage stage3 = new Stage("ConfirmWrong", 2, R.string.lockpassword_confirm_passwords_dont_match, R.string.lockpassword_confirm_pins_dont_match, i3);
                ConfirmWrong = stage3;
                $VALUES = new Stage[]{stage, stage2, stage3};
            }

            private Stage(String str, int i, int i2, int i3, int i4) {
                this.alphaHint = i2;
                this.numericHint = i3;
                this.buttonText = i4;
            }

            public static Stage valueOf(String str) {
                return (Stage) Enum.valueOf(Stage.class, str);
            }

            public static Stage[] values() {
                return (Stage[]) $VALUES.clone();
            }
        }

        private void checkPassword(final String str) {
            if (this.mSecuritySpaceId == -10000 && !CrossUserUtils.hasAirSpace(getActivity())) {
                handleCorrectPassword(str);
                return;
            }
            AsyncTask<?, ?, ?> asyncTask = this.mPendingLockCheck;
            if (asyncTask != null) {
                asyncTask.cancel(false);
            }
            this.mPendingLockCheck = LockPatternChecker.checkPasswordForUsers(this.mLockPatternUtils, str, getUserList(getActivity()), getActivity(), new LockPatternChecker.OnCheckForUsersCallback() { // from class: com.android.settings.ChooseLockPassword.ChooseLockPasswordFragment.5
                @Override // com.android.settings.LockPatternChecker.OnCheckForUsersCallback
                public void onChecked(boolean z, int i, int i2) {
                    ChooseLockPasswordFragment.this.mPendingLockCheck = null;
                    if (!z || i == ChooseLockPasswordFragment.this.mUserIdToSetPassword) {
                        ChooseLockPasswordFragment.this.handleCorrectPassword(str);
                        return;
                    }
                    String string = i == 0 ? ChooseLockPasswordFragment.this.getString(R.string.lockpattern_pattern_same_with_owner) : i == ChooseLockPasswordFragment.this.mSecuritySpaceId ? ChooseLockPasswordFragment.this.getString(R.string.lockpattern_pattern_same_with_security_space) : ChooseLockPasswordFragment.this.getString(R.string.lockpattern_pattern_same_with_others);
                    ChooseLockPasswordFragment chooseLockPasswordFragment = ChooseLockPasswordFragment.this;
                    chooseLockPasswordFragment.showError(string, chooseLockPasswordFragment.mUiStage);
                }
            });
        }

        private void finishFragment(int i, byte[] bArr) {
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null) {
                fragmentManager.popBackStackImmediate();
            }
            Bundle bundle = new Bundle();
            if (bArr != null) {
                bundle.putByteArray("hw_auth_token", bArr);
            }
            bundle.putInt("miui_security_fragment_result", i);
            MiuiKeyguardSettingsUtils.onFragmentResult(getTargetFragment(), getTargetRequestCode(), bundle);
        }

        private byte[] getPasswordHistoryHashFactor() {
            if (this.mPasswordHistoryHashFactor == null) {
                this.mPasswordHistoryHashFactor = LockPatternUtilsCompat.getPasswordHistoryHashFactor(this.mLockPatternUtils, this.mUserPassword, this.mUserIdToSetPassword);
            }
            return this.mPasswordHistoryHashFactor;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleCorrectPassword(String str) {
            this.mFirstPin = str;
            this.mPasswordEntry.setText("");
            updateStage(Stage.NeedToConfirm);
        }

        private boolean isDeviceProvisioned(Context context) {
            return Settings.Secure.getInt(context.getContentResolver(), "device_provisioned", 0) == 1;
        }

        private void onPasswordChecked(byte[] bArr) {
            Intent intent = new Intent();
            if (bArr != null) {
                intent.putExtra("hw_auth_token", bArr);
            }
            getActivity().setResult(-1, intent);
            getActivity().finish();
        }

        private boolean requiresLettersOrSymbols() {
            return (((this.mPasswordMinLetters + this.mPasswordMinUpperCase) + this.mPasswordMinLowerCase) + this.mPasswordMinSymbols) + this.mPasswordMinNonLetter > 0;
        }

        private boolean requiresNumeric() {
            return this.mPasswordMinNumeric > 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void saveChosenPasswordnAndFinish(String str) {
            boolean z = Settings.System.getInt(getContentResolver(), "is_security_encryption_enabled", 0) == 1;
            setCredentialRequiredToDecrypt(false);
            if (z || getActivity().getIntent().getBooleanExtra("use_lock_password_to_encrypt_device", false)) {
                setCredentialRequiredToDecrypt(true);
            }
            setNextEnable(false);
            setCancelEnable(false);
            MiuiSettings.System.putBooleanForUser(getActivity().getContentResolver(), "new_numeric_password_type", true, this.mUserIdToSetPassword);
            FingerprintHelper fingerprintHelper = new FingerprintHelper(getActivity());
            this.mFingerprintHelper = fingerprintHelper;
            new AnonymousClass7(str, fingerprintHelper.isHardwareDetected() && this.mAddKeyguardpasswordThenAddFingerprint && this.mFingerprintHelper.getFingerprintIds().size() == 0, KeyguardSettingsFaceUnlockUtils.isSupportFaceUnlock(getActivity()) && this.mAddKeyguardpasswordThenAddFaceRecoginition && KeyguardSettingsFaceUnlockUtils.getEnrolledFacesNumber(getActivity()) == 0, MiuiKeyguardSettingsUtils.getBooolExtra(getArguments(), getActivity().getIntent(), "has_challenge"), this.mChooseLockSettingsHelper.utils()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }

        private void setCredentialRequiredToDecrypt(boolean z) {
            if (getActivity() == null || UserManager.get(getActivity()).getUserInfo(this.mUserIdToSetPassword).isPrimary()) {
                this.mLockPatternUtils.setCredentialRequiredToDecrypt(z);
            }
        }

        private void setFragmentResult(int i) {
            if (getTargetFragment() != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("miui_security_fragment_result", i);
                MiuiKeyguardSettingsUtils.onFragmentResult(getTargetFragment(), getTargetRequestCode(), bundle);
            }
        }

        private void showDialogToWaitUpdatePassword(final String str) {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            this.mLoadingDialog = progressDialog;
            progressDialog.setCancelable(false);
            this.mLoadingDialog.setMessage(getResources().getString(R.string.turn_update_keyguard_password_wait_dialog));
            this.mLoadingDialog.show();
            new Handler().postDelayed(new Runnable() { // from class: com.android.settings.ChooseLockPassword.ChooseLockPasswordFragment.6
                @Override // java.lang.Runnable
                public void run() {
                    ChooseLockPasswordFragment.this.saveChosenPasswordnAndFinish(str);
                }
            }, 5000L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void showError(String str, Stage stage) {
            this.mHeaderText.setText(str);
            TextView textView = this.mHeaderText;
            textView.announceForAccessibility(textView.getText());
            Message obtainMessage = this.mHandler.obtainMessage(1, stage);
            this.mHandler.removeMessages(1);
            this.mHandler.sendMessageDelayed(obtainMessage, 3000L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void showSoftInput(boolean z) {
            if (z) {
                this.mHandler.removeCallbacks(this.mShowSoftInputRunnable);
                this.mHandler.postDelayed(this.mShowSoftInputRunnable, 20L);
                return;
            }
            this.mHandler.removeCallbacks(this.mShowSoftInputRunnable);
            this.mInputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }

        private void updatePasswordMetricsRequirements(String str) {
            int i = this.mRequestedMinComplexity;
            if (i == 0) {
                return;
            }
            this.mPasswordMetricsWrapper.updatePasswordMetrics(str, i, this.mRequestedQuality, requiresNumeric(), requiresLettersOrSymbols(), this.mLockPatternUtils, this.mUserIdToSetPassword, this.mIsAlphaMode);
            this.mPasswordNumSequenceAllowed = this.mPasswordNumSequenceAllowed && this.mPasswordMetricsWrapper.getQuality() != 196608;
            this.mPasswordMinLength = Math.max(this.mPasswordMinLength, this.mPasswordMetricsWrapper.getMinLength());
            this.mPasswordMinLetters = Math.max(this.mPasswordMinLetters, this.mPasswordMetricsWrapper.getMinLetters());
            this.mPasswordMinUpperCase = Math.max(this.mPasswordMinUpperCase, this.mPasswordMetricsWrapper.getMinUpperCase());
            this.mPasswordMinLowerCase = Math.max(this.mPasswordMinLowerCase, this.mPasswordMetricsWrapper.getMinLowerCase());
            this.mPasswordMinNumeric = Math.max(this.mPasswordMinNumeric, this.mPasswordMetricsWrapper.getMinNumeric());
            this.mPasswordMinSymbols = Math.max(this.mPasswordMinSymbols, this.mPasswordMetricsWrapper.getMinSymbols());
            this.mPasswordMinNonLetter = Math.max(this.mPasswordMinNonLetter, this.mPasswordMetricsWrapper.getMinNonLetter());
            if (this.mPasswordMetricsWrapper.getQuality() == 262144 && !requiresLettersOrSymbols()) {
                this.mPasswordMinLetters = 1;
            }
            if (this.mPasswordMetricsWrapper.getQuality() == 327680) {
                if (!requiresLettersOrSymbols()) {
                    this.mPasswordMinLetters = 1;
                }
                if (requiresNumeric()) {
                    return;
                }
                this.mPasswordMinNumeric = 1;
            }
        }

        private void updateUi() {
            String quantityString;
            int i;
            int i2;
            String charSequence = this.mPasswordEntry.getText().toString();
            int length = charSequence.length();
            Stage stage = this.mUiStage;
            Stage stage2 = Stage.Introduction;
            if (stage != stage2 || length <= 0) {
                if (this.mIsSetPasswordForSSUser && stage == stage2) {
                    this.mHeaderText.setText(this.mIsAlphaMode ? R.string.lockpassword_choose_your_password_second_space : R.string.lockpassword_choose_your_pin_header_second_space);
                } else {
                    boolean z = this.mIsAlphaMode;
                    if (z || stage != stage2) {
                        this.mHeaderText.setText(z ? stage.alphaHint : stage.numericHint);
                    } else {
                        this.mHeaderText.setText(getString(R.string.lockpassword_choose_your_pin_title, Integer.valueOf(this.mPasswordMinLength), Integer.valueOf(this.mPasswordMaxLength)));
                    }
                }
                setNextEnable(length > 0);
            } else if (!this.mIsSetPasswordForSSUser || this.mIsAlphaMode || (!((i = this.mSystemUserPasswordQuality) == 131072 || i == 196608) || (i2 = this.mSystemUserPasswordLength) == 0)) {
                int i3 = this.mPasswordMinLength;
                if (length < i3) {
                    if (this.mIsAlphaMode) {
                        Resources resources = getResources();
                        int i4 = R.plurals.lockpassword_password_too_short;
                        int i5 = this.mPasswordMinLength;
                        quantityString = resources.getQuantityString(i4, i5, Integer.valueOf(i5));
                    } else if (i3 == this.mPasswordMaxLength) {
                        Resources resources2 = getResources();
                        int i6 = R.plurals.lockpassword_pin_fixed_length;
                        int i7 = this.mPasswordMinLength;
                        quantityString = resources2.getQuantityString(i6, i7, Integer.valueOf(i7));
                    } else {
                        Resources resources3 = getResources();
                        int i8 = R.plurals.lockpassword_pin_too_short;
                        int i9 = this.mPasswordMinLength;
                        quantityString = resources3.getQuantityString(i8, i9, Integer.valueOf(i9));
                    }
                    this.mHeaderText.setText(quantityString);
                    setNextEnable(false);
                } else {
                    String validatePassword = validatePassword(charSequence);
                    if (validatePassword != null) {
                        this.mHeaderText.setText(validatePassword);
                        setNextEnable(false);
                    } else {
                        this.mHeaderText.setText((CharSequence) null);
                        setNextEnable(true);
                    }
                }
            } else {
                setNextEnable(length == i2);
                this.mHeaderText.setText(R.string.lockpassword_choose_your_pin_header_second_space);
            }
            this.mNextButton.setText(this.mUiStage.buttonText);
        }

        private String validatePassword(String str) {
            int i;
            updatePasswordMetricsRequirements(str);
            int length = str.length();
            int i2 = this.mPasswordMinLength;
            if (length < i2) {
                if (this.mIsAlphaMode) {
                    Resources resources = getResources();
                    int i3 = R.plurals.lockpassword_password_too_short;
                    int i4 = this.mPasswordMinLength;
                    return resources.getQuantityString(i3, i4, Integer.valueOf(i4));
                } else if (i2 == this.mPasswordMaxLength) {
                    Resources resources2 = getResources();
                    int i5 = R.plurals.lockpassword_pin_fixed_length;
                    int i6 = this.mPasswordMinLength;
                    return resources2.getQuantityString(i5, i6, Integer.valueOf(i6));
                } else {
                    Resources resources3 = getResources();
                    int i7 = R.plurals.lockpassword_pin_too_short;
                    int i8 = this.mPasswordMinLength;
                    return resources3.getQuantityString(i7, i8, Integer.valueOf(i8));
                }
            } else if (str.length() > this.mPasswordMaxLength) {
                if (this.mIsAlphaMode) {
                    Resources resources4 = getResources();
                    int i9 = R.plurals.lockpassword_password_too_long;
                    int i10 = this.mPasswordMaxLength;
                    return resources4.getQuantityString(i9, i10, Integer.valueOf(i10));
                }
                Resources resources5 = getResources();
                int i11 = R.plurals.lockpassword_pin_too_long;
                int i12 = this.mPasswordMaxLength;
                return resources5.getQuantityString(i11, i12, Integer.valueOf(i12));
            } else if (this.mPasswordNumSequenceAllowed || requiresLettersOrSymbols() || (!(this.mPasswordMetricsWrapper.isPasswordLengthMatched(str) || (i = this.mRequestedMinComplexity) == 196608 || i == 327680) || this.mPasswordMetricsWrapper.getMaxLengthSequence(str) <= 3)) {
                if (LockPatternUtilsCompat.checkPasswordHistory(this.mLockPatternUtils, str, getPasswordHistoryHashFactor(), this.mUserIdToSetPassword)) {
                    return getString(this.mIsAlphaMode ? R.string.lockpassword_password_recently_used : R.string.lockpassword_pin_recently_used);
                }
                int i13 = 0;
                int i14 = 0;
                int i15 = 0;
                int i16 = 0;
                int i17 = 0;
                int i18 = 0;
                for (int i19 = 0; i19 < str.length(); i19++) {
                    char charAt = str.charAt(i19);
                    if (charAt < ' ' || charAt > 127) {
                        return getString(R.string.lockpassword_illegal_character);
                    }
                    if (charAt < '0' || charAt > '9') {
                        if (charAt >= 'A' && charAt <= 'Z') {
                            i13++;
                            i18++;
                        } else if (charAt < 'a' || charAt > 'z') {
                            i14++;
                        } else {
                            i13++;
                            i17++;
                        }
                    } else {
                        i15++;
                    }
                    i16++;
                }
                int i20 = this.mRequestedQuality;
                if ((131072 == i20 || i20 == 196608) && (i13 > 0 || i14 > 0)) {
                    return getString(R.string.lockpassword_pin_contains_non_digits);
                }
                if (393216 == i20 || 262144 == i20 || 327680 == i20) {
                    if (i13 < this.mPasswordMinLetters) {
                        return String.format(getResources().getQuantityString(R.plurals.lockpassword_password_requires_letters, this.mPasswordMinLetters), Integer.valueOf(this.mPasswordMinLetters));
                    }
                    if (i15 < this.mPasswordMinNumeric) {
                        return String.format(getResources().getQuantityString(R.plurals.lockpassword_password_requires_numeric, this.mPasswordMinNumeric), Integer.valueOf(this.mPasswordMinNumeric));
                    }
                    if (i17 < this.mPasswordMinLowerCase) {
                        return String.format(getResources().getQuantityString(R.plurals.lockpassword_password_requires_lowercase, this.mPasswordMinLowerCase), Integer.valueOf(this.mPasswordMinLowerCase));
                    }
                    if (i18 < this.mPasswordMinUpperCase) {
                        return String.format(getResources().getQuantityString(R.plurals.lockpassword_password_requires_uppercase, this.mPasswordMinUpperCase), Integer.valueOf(this.mPasswordMinUpperCase));
                    }
                    if (i14 < this.mPasswordMinSymbols) {
                        return String.format(getResources().getQuantityString(R.plurals.lockpassword_password_requires_symbols, this.mPasswordMinSymbols), Integer.valueOf(this.mPasswordMinSymbols));
                    }
                    if (i16 < this.mPasswordMinNonLetter) {
                        return String.format(getResources().getQuantityString(R.plurals.lockpassword_password_requires_nonletter, this.mPasswordMinNonLetter), Integer.valueOf(this.mPasswordMinNonLetter));
                    }
                    if (327680 == i20) {
                        if (i15 == 0) {
                            return getString(R.string.lockpassword_password_requires_digit);
                        }
                        if (i13 == 0) {
                            return getString(R.string.lockpassword_password_requires_alpha);
                        }
                        return null;
                    }
                    return null;
                }
                return null;
            } else {
                return getString(R.string.lockpassword_pin_no_sequential_digits);
            }
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            if (this.mUiStage == Stage.ConfirmWrong) {
                this.mUiStage = Stage.NeedToConfirm;
            }
            updateUi();
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // com.android.settings.SettingsPreferenceFragment
        public String getName() {
            return ChooseLockPasswordFragment.class.getName();
        }

        protected List<UserInfo> getUserList(Context context) {
            return ((UserManager) context.getSystemService("user")).getUsers();
        }

        protected void handleNext() {
            String charSequence = this.mPasswordEntry.getText().toString();
            if (TextUtils.isEmpty(charSequence)) {
                return;
            }
            Stage stage = this.mUiStage;
            if (stage == Stage.Introduction) {
                String validatePassword = validatePassword(charSequence);
                if (validatePassword == null) {
                    checkPassword(charSequence);
                } else {
                    showError(validatePassword, this.mUiStage);
                }
            } else if (stage == Stage.NeedToConfirm) {
                if (this.mFirstPin.equals(charSequence)) {
                    if (MiuiKeyguardSettingsUtils.showWaitTurnOffPassword(getActivity().getApplicationContext())) {
                        showDialogToWaitUpdatePassword(charSequence);
                        return;
                    } else {
                        saveChosenPasswordnAndFinish(charSequence);
                        return;
                    }
                }
                CharSequence text = this.mPasswordEntry.getText();
                if (text != null) {
                    Selection.setSelection((Spannable) text, 0, text.length());
                    if (this.mIsAlphaMode) {
                        this.mIsConfirmPasswordWrong = true;
                    }
                }
                updateStage(Stage.ConfirmWrong);
            }
        }

        protected boolean isSetUp() {
            return false;
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i == 58) {
                if (i2 != -1) {
                    getActivity().setResult(1);
                    getActivity().finish();
                }
            } else if (i != 107) {
            } else {
                if (i2 != -1) {
                    getActivity().setResult(1);
                    getActivity().finish();
                } else if (intent != null) {
                    this.mUserPassword = intent.getStringExtra("password");
                }
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view == this.mNextButton || view == this.mNextImage) {
                handleNext();
            } else if (view == this.mCancelButton || view == this.mBackImage) {
                if (getActivity() instanceof MiuiSettings) {
                    finishFragment(-1, null);
                } else {
                    getActivity().finish();
                }
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mLockPatternUtils = new MiuiLockPatternUtils(getActivity());
            this.mChooseLockSettingsHelper = new MiuiChooseLockSettingsHelper(getActivity());
            Bundle extras = getActivity().getIntent().getExtras();
            if (!(getActivity() instanceof ChooseLockPassword) && !(getActivity() instanceof MiuiSettings) && !(getActivity() instanceof ProvisionBaseActivity)) {
                throw new SecurityException("Fragment contained in wrong activity");
            }
            if (getActivity() instanceof MiuiSettings) {
                extras = getArguments();
            }
            if (getActivity() instanceof ChooseLockPassword) {
                setThemeRes(R.style.Theme_DayNight_Settings_NoTitle);
            }
            this.mSecuritySpaceId = Settings.Secure.getIntForUser(getContentResolver(), "second_user_id", -10000, 0);
            int i = extras.getInt("user_id_to_set_password", -10000);
            this.mUserIdToSetPassword = i;
            if (i == -10000) {
                i = MiuiKeyguardSettingsUtils.getUserId(getActivity(), extras);
            }
            this.mUserIdToSetPassword = i;
            int i2 = this.mSecuritySpaceId;
            boolean z = true;
            if (i2 != -10000 && i == i2) {
                this.mIsSetPasswordForSSUser = true;
                this.mSystemUserPasswordLength = (int) this.mLockPatternUtils.getLockPasswordLength(0);
                this.mSystemUserPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(0);
            }
            if (this.mUserIdToSetPassword == 0) {
                this.mIsSetPasswordForOwnerUser = true;
            }
            if (!this.mIsSetPasswordForSSUser && !this.mIsSetPasswordForOwnerUser) {
                this.mIsSetPasswordForAirSpace = CrossUserUtils.isAirSpace(getActivity(), this.mUserIdToSetPassword);
            }
            Context context = getContext();
            this.mRequestedQuality = Math.max(extras.getInt("lockscreen.password_type", this.mRequestedQuality), MiuiSecurityUtils.getRequestedPasswordQuality(context, this.mUserIdToSetPassword));
            int max = Math.max(extras.getInt("lockscreen.password_min", this.mPasswordMinLength), MiuiSecurityUtils.getRequestedMinimumPasswordLength(context, this.mUserIdToSetPassword));
            this.mPasswordMinLength = max;
            if (max < 4) {
                max = 4;
            }
            this.mPasswordMinLength = max;
            this.mPasswordMaxLength = extras.getInt("lockscreen.password_max", this.mPasswordMaxLength);
            this.mPasswordMinLetters = Math.max(extras.getInt("lockscreen.password_min_letters", this.mPasswordMinLetters), MiuiSecurityUtils.getRequestedPasswordMinimumLetters(context, this.mUserIdToSetPassword));
            this.mPasswordMinUpperCase = Math.max(extras.getInt("lockscreen.password_min_uppercase", this.mPasswordMinUpperCase), MiuiSecurityUtils.getRequestedPasswordMinimumUpperCase(context, this.mUserIdToSetPassword));
            this.mPasswordMinLowerCase = Math.max(extras.getInt("lockscreen.password_min_lowercase", this.mPasswordMinLowerCase), MiuiSecurityUtils.getRequestedPasswordMinimumLowerCase(context, this.mUserIdToSetPassword));
            this.mPasswordMinNumeric = Math.max(extras.getInt("lockscreen.password_min_numeric", this.mPasswordMinNumeric), MiuiSecurityUtils.getRequestedPasswordMinimumNumeric(context, this.mUserIdToSetPassword));
            this.mPasswordMinSymbols = Math.max(extras.getInt("lockscreen.password_min_symbols", this.mPasswordMinSymbols), MiuiSecurityUtils.getRequestedPasswordMinimumSymbols(context, this.mUserIdToSetPassword));
            this.mPasswordMinNonLetter = Math.max(extras.getInt("lockscreen.password_min_nonletter", this.mPasswordMinNonLetter), MiuiSecurityUtils.getRequestedPasswordMinimumNonLetter(context, this.mUserIdToSetPassword));
            int max2 = Math.max(extras.getInt("requested_min_complexity", 0), this.mLockPatternUtils.getRequestedPasswordComplexity(this.mUserIdToSetPassword));
            this.mRequestedMinComplexity = max2;
            int i3 = this.mRequestedQuality;
            if (i3 == 196608 || max2 == 196608 || max2 == 327680) {
                this.mPasswordNumSequenceAllowed = false;
            }
            if (262144 != i3 && 327680 != i3 && 393216 != i3) {
                z = false;
            }
            this.mIsAlphaMode = z;
            this.mPasswordMetricsWrapper = new PasswordMetricsWrapper();
            updatePasswordMetricsRequirements("");
            this.mEnableKeyguardPassword = extras.getBoolean("set_keyguard_password");
            this.mAddKeyguardpasswordThenAddFingerprint = extras.getBoolean("add_keyguard_password_then_add_fingerprint", false);
            this.mAddKeyguardpasswordThenAddFaceRecoginition = extras.getBoolean("add_keyguard_password_then_add_face_recoginition", false);
            this.mUserPassword = extras.getString("password");
            if (MiuiKeyguardSettingsUtils.instanceofSettingsPreFragment(getTargetFragment())) {
                this.mChooseLockSettingsHelper.launchConfirmWhenNecessary(this, 107, this.mUserIdToSetPassword);
            }
            this.mInputMethodManager = (InputMethodManager) getActivity().getSystemService("input_method");
            getActivity().getWindow().setSoftInputMode(16);
            if (MiuiKeyguardSettingsUtils.isInFullWindowGestureMode(getActivity().getApplicationContext())) {
                getActivity().getWindow().clearFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
            }
            getActivity().getWindow().addFlags(8192);
        }

        @Override // com.android.settings.KeyguardSettingsPreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            ActionBar appCompatActionBar;
            View inflate = isSetUp() ? isDeviceProvisioned(getActivity()) ? layoutInflater.inflate(R.layout.setup_choose_lock_password, viewGroup, false) : layoutInflater.inflate(R.layout.provision_setup_choose_lock_password, viewGroup, false) : layoutInflater.inflate(R.layout.choose_lock_password, viewGroup, false);
            setupViews(inflate);
            this.mCancelButton.setOnClickListener(this);
            this.mNextButton.setOnClickListener(this);
            this.mPasswordEntry.addTextChangedListener(this);
            this.mPasswordEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.android.settings.ChooseLockPassword.ChooseLockPasswordFragment.2
                @Override // android.widget.TextView.OnEditorActionListener
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == 0 || i == 6) {
                        if (ChooseLockPasswordFragment.this.mNextButton.isEnabled()) {
                            ChooseLockPasswordFragment.this.handleNext();
                        }
                        return true;
                    } else if (i == 5) {
                        ChooseLockPasswordFragment.this.showSoftInput(false);
                        if (ChooseLockPasswordFragment.this.mNextButton.isEnabled()) {
                            ChooseLockPasswordFragment.this.mNextButton.performClick();
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            int inputType = this.mPasswordEntry.getInputType();
            TextView textView = this.mPasswordEntry;
            if (!this.mIsAlphaMode) {
                inputType = 18;
            }
            textView.setInputType(inputType);
            if (bundle == null) {
                if (getTargetFragment() == null) {
                    this.mChooseLockSettingsHelper.launchConfirmWhenNecessary(this, 107, this.mUserIdToSetPassword);
                }
                updateStage(Stage.Introduction);
            } else {
                this.mFirstPin = bundle.getString("first_pin");
                String string = bundle.getString("ui_stage");
                if (string != null) {
                    Stage valueOf = Stage.valueOf(string);
                    this.mUiStage = valueOf;
                    updateStage(valueOf);
                }
                if (TextUtils.isEmpty(this.mUserPassword)) {
                    this.mUserPassword = bundle.getString("user_password");
                }
            }
            FragmentActivity activity = getActivity();
            if (activity instanceof PreferenceActivity) {
                PreferenceActivity preferenceActivity = (PreferenceActivity) activity;
                CharSequence text = getText(this.mIsAlphaMode ? R.string.lockpassword_choose_your_password_header : R.string.setup_choose_unlock_password_title);
                preferenceActivity.showBreadCrumbs(text, text);
            }
            if ((getActivity() instanceof ChooseLockPassword) && inflate.getLayoutParams() != null && (inflate.getLayoutParams() instanceof PreferenceFrameLayout.LayoutParams)) {
                inflate.getLayoutParams().removeBorders = true;
            }
            if (this.mIsSetPasswordForSSUser) {
                this.mHeaderText.setTextColor(getResources().getColor(17170443));
                this.mHeaderText.setTextSize(13.0f);
                this.mHeaderText.setMinLines(2);
                inflate.setBackgroundColor(getResources().getColor(R.color.set_second_space_background));
                if ((getActivity() instanceof AppCompatActivity) && (appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar()) != null) {
                    appCompatActionBar.setTitle(R.string.lockpassword_choose_for_second_user);
                    appCompatActionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_second_space));
                }
            }
            return inflate;
        }

        @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onDestroy() {
            if ((getActivity() instanceof MiuiSettings) && this.mIsAlphaMode) {
                getActivity().getWindow().clearFlags(131072);
            }
            super.onDestroy();
        }

        @Override // com.android.settings.SettingsPreferenceFragment
        public void onFragmentResult(int i, Bundle bundle) {
            boolean z = bundle != null && bundle.getInt("miui_security_fragment_result", -1) == 0;
            if (i == 58) {
                if (z) {
                    return;
                }
                finish();
            } else if (i != 107) {
            } else {
                if (z) {
                    this.mUserPassword = bundle.getString("password", "");
                    return;
                }
                setFragmentResult(-1);
                finish();
            }
        }

        protected void onPasswordSaved(byte[] bArr, boolean z) {
            ProgressDialog progressDialog = this.mLoadingDialog;
            if (progressDialog != null) {
                progressDialog.dismiss();
                this.mLoadingDialog = null;
            }
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            MiuiKeyguardSettingsUtils.saveUpdatepatternTime(getActivity().getApplicationContext());
            if (z) {
                showDialogToAddFingerprint(bArr);
            } else if (this.mAddKeyguardpasswordThenAddFaceRecoginition) {
                Intent intent = new Intent();
                if (bArr != null) {
                    intent.putExtra("hw_auth_token", bArr);
                }
                getActivity().setResult(-1, intent);
                getActivity().finish();
            } else if (!MiuiKeyguardSettingsUtils.isShowDialogToAddFace(getActivity()) || this.mFingerprintHelper.isHardwareDetected()) {
                returnToKeyguardPasswordSettings(bArr);
            } else {
                MiuiKeyguardSettingsUtils.showDialogToAddFace(getActivity(), bArr, R.style.AlertDialog_Theme_DayNight, false);
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onPause() {
            this.mHandler.removeMessages(1);
            super.onPause();
            showSoftInput(false);
            if (this.mClipData != null) {
                ((ClipboardManager) getActivity().getSystemService("clipboard")).setPrimaryClip(this.mClipData);
            }
            AsyncTask<?, ?, ?> asyncTask = this.mPendingLockCheck;
            if (asyncTask != null) {
                asyncTask.cancel(false);
                this.mPendingLockCheck = null;
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            updateStage(this.mUiStage);
            this.mPasswordEntry.requestFocus();
            showSoftInput(true);
            ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService("clipboard");
            if (clipboardManager.hasPrimaryClip()) {
                this.mClipData = clipboardManager.getPrimaryClip();
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, ""));
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putString("ui_stage", this.mUiStage.name());
            bundle.putString("first_pin", this.mFirstPin);
            if (TextUtils.isEmpty(this.mUserPassword)) {
                return;
            }
            bundle.putString("user_password", this.mUserPassword);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onStart() {
            ActionBar appCompatActionBar;
            super.onStart();
            MiuiUtils.onStartEdit(this);
            if (isSetUp() || !(getActivity() instanceof AppCompatActivity) || (appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar()) == null) {
                return;
            }
            appCompatActionBar.setExpandState(0);
            appCompatActionBar.setResizable(false);
        }

        @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onStop() {
            MiuiUtils.onFinishEdit(this);
            super.onStop();
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void returnToKeyguardPasswordSettings(byte[] bArr) {
            if (getActivity() != null) {
                if (getActivity() instanceof MiuiSettings) {
                    finishFragment(0, bArr);
                    return;
                }
                Intent intent = getActivity().getIntent();
                if (intent == null || !intent.getBooleanExtra("has_challenge", false)) {
                    onPasswordChecked(null);
                } else {
                    onPasswordChecked(bArr);
                }
            }
        }

        protected void setCancelEnable(boolean z) {
            this.mCancelButton.setEnabled(z);
        }

        protected void setNextEnable(boolean z) {
            this.mNextButton.setEnabled(z);
        }

        protected void setupViews(View view) {
            this.mHeaderText = (TextView) view.findViewById(R.id.headerText);
            this.mPasswordEntry = (EditText) view.findViewById(R.id.password_entry);
            this.mCancelButton = (TextView) view.findViewById(R.id.footerLeftButton);
            this.mNextButton = (TextView) view.findViewById(R.id.footerRightButton);
        }

        public void showDialogToAddFingerprint(final byte[] bArr) {
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.ChooseLockPassword.ChooseLockPasswordFragment.8
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i != -1) {
                        dialogInterface.dismiss();
                        ChooseLockPasswordFragment.this.returnToKeyguardPasswordSettings(bArr);
                        return;
                    }
                    Intent intent = new Intent(ChooseLockPasswordFragment.this.getActivity(), NewFingerprintInternalActivity.class);
                    intent.putExtra("add_keyguard_password_then_add_fingerprint", true);
                    intent.putExtra("hw_auth_token", bArr);
                    ChooseLockPasswordFragment.this.startActivity(intent);
                    ChooseLockPasswordFragment.this.getActivity().setResult(-1);
                    ChooseLockPasswordFragment.this.getActivity().finish();
                }
            };
            new AlertDialog.Builder(getActivity()).setCancelable(true).setTitle(R.string.new_password_to_new_fingerprint_dialog_tittle).setMessage(R.string.new_password_to_new_fingerprint_dialog_msg).setPositiveButton(R.string.new_password_to_new_fingerprint_dialog_positive_msg, onClickListener).setNegativeButton(R.string.new_password_to_new_fingerprint_dialog_negative_msg, onClickListener).setCancelable(false).create().show();
        }

        protected void updateStage(Stage stage) {
            Stage stage2 = this.mUiStage;
            this.mUiStage = stage;
            updateUi();
            if (stage2 != stage) {
                TextView textView = this.mHeaderText;
                textView.announceForAccessibility(textView.getText());
            }
        }
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", ChooseLockPasswordFragment.class.getName());
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return ChooseLockPasswordFragment.class.getName().equals(str);
    }

    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(getText(R.string.empty_title));
    }
}
