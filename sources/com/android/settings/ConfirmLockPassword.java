package com.android.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.security.MiuiLockPatternUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.ConfirmLockPassword;
import com.android.settings.LockPatternChecker;
import com.android.settings.password.ConfirmDeviceCredentialBaseFragment;
import com.android.settings.utils.TabletUtils;
import java.lang.reflect.Field;
import miuix.appcompat.app.ProgressDialog;

/* loaded from: classes.dex */
public class ConfirmLockPassword extends BaseConfirmLockActivity {

    /* loaded from: classes.dex */
    public static class ConfirmLockPasswordFragment extends BaseConfirmLockFragment implements View.OnClickListener, TextView.OnEditorActionListener, TextWatcher, OnBackPressedListener {
        private Bundle bundle;
        private Context mContext;
        private Button mContinueButton;
        private CountDownTimer mCountdownTimer;
        private int mDefaultUserId;
        protected FingerprintHelper mFingerprintHelper;
        private LinearLayout mFooterLayout;
        protected Button mForgetPassword;
        private TextView mFrpHeaderMessage;
        private TextView mFrpSkipPassword;
        private String mHeaderConfirmFRP;
        private String mHeaderConfirmLockpassword;
        private String mHeaderConfirmPrivatepassword;
        private CharSequence mHeaderMessage;
        private TextView mHeaderText;
        private ImageView mIconView;
        private InputMethodManager mImm;
        private boolean mIsAlpha;
        private boolean mIsFromFrp;
        private boolean mIsLockPassword;
        private boolean mIsManagedProfile;
        private boolean mIsShowForgetPwd;
        private MiuiLockPatternUtils mLockPatternUtils;
        private int mNumWrongConfirmAttempts;
        private TextView mPasswordEntry;
        private AsyncTask<?, ?, ?> mPendingLockCheck;
        private Runnable mTopBlankHeightRunnable;
        private boolean mVerifyChallenge;
        private Bundle outbundle;
        private Handler mHandler = new Handler();
        private int mFragmentResult = -1;
        private String mCurrentPassword = null;
        protected int mUserIdToConfirmPassword = UserHandle.myUserId();
        private boolean mReturnCredentials = false;
        private ForgetPasswordDialog mForgetPasswordDialog = null;
        private long mFingerEnrollChallenge = 0;
        private final Runnable mResetErrorRunnable = new Runnable() { // from class: com.android.settings.ConfirmLockPassword.ConfirmLockPasswordFragment.8
            @Override // java.lang.Runnable
            public void run() {
                ConfirmLockPasswordFragment.this.mHeaderText.setText(ConfirmLockPasswordFragment.this.getDefaultHeader());
            }
        };

        private void handleAttemptLockout(long j) {
            this.mHandler.removeCallbacks(this.mResetErrorRunnable);
            refreshLockScreen();
            long elapsedRealtime = SystemClock.elapsedRealtime();
            this.mPasswordEntry.setText((CharSequence) null);
            this.mPasswordEntry.setEnabled(false);
            this.mCountdownTimer = new CountDownTimer(j - elapsedRealtime, 1000L) { // from class: com.android.settings.ConfirmLockPassword.ConfirmLockPasswordFragment.7
                @Override // android.os.CountDownTimer
                public void onFinish() {
                    ConfirmLockPasswordFragment.this.mPasswordEntry.setEnabled(true);
                    ConfirmLockPasswordFragment.this.mPasswordEntry.requestFocus();
                    ConfirmLockPasswordFragment.this.mImm.showSoftInput(ConfirmLockPasswordFragment.this.mPasswordEntry, 1);
                    ConfirmLockPasswordFragment.this.mHeaderText.setText(ConfirmLockPasswordFragment.this.getDefaultHeader());
                    ConfirmLockPasswordFragment.this.mNumWrongConfirmAttempts = 0;
                }

                @Override // android.os.CountDownTimer
                public void onTick(long j2) {
                    if (ConfirmLockPasswordFragment.this.isAdded()) {
                        int i = (int) (j2 / 1000);
                        ConfirmLockPasswordFragment.this.mHeaderText.setText(ConfirmLockPasswordFragment.this.getResources().getQuantityString(R.plurals.lockpattern_too_many_failed_confirmation_attempts, i, Integer.valueOf(i)));
                    }
                }
            }.start();
        }

        private void handleNext() {
            AsyncTask<?, ?, ?> asyncTask = this.mPendingLockCheck;
            if (asyncTask != null) {
                asyncTask.cancel(false);
            }
            String charSequence = this.mPasswordEntry.getText() == null ? null : this.mPasswordEntry.getText().toString();
            if (this.bundle.getBoolean("has_challenge", false)) {
                startVerifyPassword(charSequence);
            } else {
                startCheckPassword(charSequence);
            }
        }

        private boolean isConfirmLockPasswordActivity() {
            return getActivity() instanceof ConfirmLockPassword;
        }

        private boolean isInternalActivity() {
            return getActivity() instanceof InternalActivity;
        }

        private boolean isValidPassword(String str) {
            return str != null && str.length() >= 4;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onStart$0(int i, int i2, long j) {
            this.mFingerEnrollChallenge = j;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onPasswordChecked(String str, byte[] bArr) {
            int i = this.mUserIdToConfirmPassword;
            if (i != 0 && i != -9999 && this.mLockPatternUtils.getStrongAuthForUser(i) != 0) {
                try {
                    this.mLockPatternUtils.userPresent(this.mUserIdToConfirmPassword);
                } catch (Exception e) {
                    Log.e(ConfirmDeviceCredentialBaseFragment.TAG, "sth wrong when user present", e);
                }
            }
            Fragment targetFragment = getTargetFragment();
            Bundle bundle = new Bundle();
            this.outbundle = bundle;
            bundle.putInt("type", 0);
            if (isInternalActivity() || this.mReturnCredentials) {
                this.outbundle.putString("password", str);
            }
            if (bArr != null) {
                this.outbundle.putByteArray("hw_auth_token", bArr);
            }
            if (targetFragment != null || getActivity() == null || getActivity().isFinishing()) {
                if (MiuiKeyguardSettingsUtils.instanceofSettingsPreFragment(targetFragment)) {
                    this.mFragmentResult = 0;
                    if (this.mReturnCredentials) {
                        this.mCurrentPassword = str;
                    }
                    popupFragment();
                    return;
                }
                return;
            }
            Intent intent = new Intent();
            intent.putExtras(this.outbundle);
            onConfirmDeviceCredentialSuccess();
            getActivity().setResult(-1, intent);
            getActivity().finish();
            try {
                checkForPendingIntentForCts();
            } catch (Exception unused) {
                Log.d(ConfirmDeviceCredentialBaseFragment.TAG, "check for pending intent error");
            }
        }

        private void popupFragment() {
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null) {
                fragmentManager.popBackStackImmediate();
            }
        }

        private void showError(int i) {
            showError(i, 3000L);
        }

        private void showProgressAnim() {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getActivity().getString(R.string.security_check_progress_dialog_message));
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(1);
            progressDialog.setCancelable(false);
            ValueAnimator ofInt = ValueAnimator.ofInt(0, 100);
            ofInt.setDuration(2000L);
            ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.ConfirmLockPassword.ConfirmLockPasswordFragment.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    progressDialog.setProgress(((Integer) valueAnimator.getAnimatedValue()).intValue());
                }
            });
            ofInt.addListener(new Animator.AnimatorListener() { // from class: com.android.settings.ConfirmLockPassword.ConfirmLockPasswordFragment.2
                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    progressDialog.setProgress(100);
                    progressDialog.dismiss();
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (ConfirmLockPasswordFragment.this.mFingerEnrollChallenge == 0) {
                        ConfirmLockPasswordFragment.this.getActivity().finish();
                    }
                    progressDialog.setProgress(100);
                    progressDialog.dismiss();
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationRepeat(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    progressDialog.show();
                }
            });
            try {
                Field declaredField = ValueAnimator.class.getDeclaredField("sDurationScale");
                declaredField.setAccessible(true);
                if (declaredField.getFloat(null) != 1.0f) {
                    declaredField.setFloat(null, 1.0f);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ofInt.start();
        }

        private void startCheckPassword(final String str) {
            if (isValidPassword(str)) {
                this.mPendingLockCheck = LockPatternChecker.checkPassword(this.mLockPatternUtils, str, this.mUserIdToConfirmPassword, this.mContext, new LockPatternChecker.OnCheckCallback() { // from class: com.android.settings.ConfirmLockPassword.ConfirmLockPasswordFragment.6
                    @Override // com.android.settings.LockPatternChecker.OnCheckCallback
                    public void onChecked(boolean z, int i) {
                        ConfirmLockPasswordFragment.this.mPendingLockCheck = null;
                        if (z) {
                            ConfirmLockPasswordFragment.this.onPasswordChecked(str, null);
                        } else {
                            ConfirmLockPasswordFragment.this.handleWrongPassword(str, i);
                        }
                    }
                });
            } else {
                handleWrongPassword(str);
            }
        }

        private void startVerifyPassword(final String str) {
            if (!isValidPassword(str)) {
                handleWrongPassword(str);
                return;
            }
            int i = this.mEffectiveUserId;
            int i2 = this.mUserId;
            LockPatternChecker.OnVerifyCallback onVerifyCallback = new LockPatternChecker.OnVerifyCallback() { // from class: com.android.settings.ConfirmLockPassword.ConfirmLockPasswordFragment.5
                @Override // com.android.settings.LockPatternChecker.OnVerifyCallback
                public void onVerified(byte[] bArr, int i3) {
                    ConfirmLockPasswordFragment.this.mPendingLockCheck = null;
                    if (bArr != null) {
                        ConfirmLockPasswordFragment.this.onPasswordChecked(str, bArr);
                    } else {
                        ConfirmLockPasswordFragment.this.handleWrongPassword(str, i3);
                    }
                }
            };
            this.mPendingLockCheck = i == i2 ? LockPatternChecker.verifyPassword(this.mLockPatternUtils, str, this.mFingerEnrollChallenge, i2, this.mContext, onVerifyCallback) : LockPatternChecker.verifyTiedProfileChallenge(this.mLockPatternUtils, str, false, i2, onVerifyCallback);
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            this.mContinueButton.setEnabled(this.mPasswordEntry.getText().length() > 0);
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // com.android.settings.BaseConfirmLockFragment
        public View createView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            View inflate = layoutInflater.inflate(R.layout.miui_confirm_lock_password, (ViewGroup) null);
            inflate.findViewById(R.id.footerLeftButton).setOnClickListener(this);
            Button button = (Button) inflate.findViewById(R.id.footerRightButton);
            this.mContinueButton = button;
            button.setOnClickListener(this);
            this.mContinueButton.setEnabled(false);
            TextView textView = (TextView) inflate.findViewById(R.id.password_entry);
            this.mPasswordEntry = textView;
            textView.setOnEditorActionListener(this);
            this.mPasswordEntry.addTextChangedListener(this);
            this.mHeaderText = (TextView) inflate.findViewById(R.id.headerText);
            this.mForgetPassword = (Button) inflate.findViewById(R.id.forgetPassword);
            this.mFooterLayout = (LinearLayout) inflate.findViewById(R.id.footerLayout);
            this.mHeaderConfirmPrivatepassword = getString(R.string.lockpassword_confirm_your_private_password_header);
            this.mHeaderConfirmFRP = getString(R.string.confirm_frp_credential_title);
            Intent putExtras = new Intent().putExtras(this.bundle);
            if (putExtras != null) {
                this.mHeaderMessage = MiuiKeyguardSettingsUtils.getHeader(putExtras);
                this.mUserIdToConfirmPassword = putExtras.getIntExtra("com.android.settings.userIdToConfirm", this.mDefaultUserId);
                TextView textView2 = this.mHeaderText;
                textView2.setTextColor(putExtras.getIntExtra("com.android.settings.titleColor", textView2.getCurrentTextColor()));
                inflate.setBackgroundColor(putExtras.getIntExtra("com.android.settings.bgColor", inflate.getDrawingCacheBackgroundColor()));
                Button button2 = this.mForgetPassword;
                button2.setTextColor(putExtras.getIntExtra("com.android.settings.forgetPatternColor", button2.getCurrentTextColor()));
                this.mIsShowForgetPwd = putExtras.getBooleanExtra("com.android.settings.forgetPassword", true);
            }
            this.mIsManagedProfile = MiuiKeyguardSettingsUtils.isManagedProfile(UserManager.get(getActivity()), this.mUserIdToConfirmPassword);
            ImageView imageView = (ImageView) inflate.findViewById(R.id.cts_icon);
            this.mIconView = imageView;
            if (imageView != null && this.mIsManagedProfile) {
                this.mIconView.setImageDrawable(getResources().getDrawable(R.drawable.auth_dialog_enterprise, this.mContext.getTheme()));
                this.mIconView.setVisibility(0);
            }
            int keyguardStoredPasswordQuality = this.mIsFromFrp ? this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mUserIdToConfirmPassword) : this.mLockPatternUtils.getActivePasswordQuality(this.mUserIdToConfirmPassword);
            this.mIsLockPassword = keyguardStoredPasswordQuality != 0;
            this.mHeaderText.setText(getDefaultHeader());
            TextView textView3 = (TextView) inflate.findViewById(R.id.frpSkipPassword);
            this.mFrpSkipPassword = textView3;
            textView3.setVisibility(this.mIsFromFrp ? 0 : 8);
            this.mFrpSkipPassword.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.ConfirmLockPassword.ConfirmLockPasswordFragment.3
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ConfirmLockPasswordFragment.this.getActivity().setResult(1);
                    ConfirmLockPasswordFragment.this.getActivity().finish();
                }
            });
            TextView textView4 = (TextView) inflate.findViewById(R.id.headerMessage);
            this.mFrpHeaderMessage = textView4;
            textView4.setVisibility(this.mIsFromFrp ? 0 : 8);
            boolean z = 262144 == keyguardStoredPasswordQuality || 327680 == keyguardStoredPasswordQuality || 393216 == keyguardStoredPasswordQuality;
            this.mIsAlpha = z;
            this.mHeaderConfirmLockpassword = getString(z ? R.string.lockpassword_confirm_your_lock_password_header : R.string.lockpassword_confirm_your_lock_pin_header);
            setForgetPasswordVisibility(getActivity());
            this.mForgetPassword.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.ConfirmLockPassword.ConfirmLockPasswordFragment.4
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (ConfirmLockPasswordFragment.this.mUserIdToConfirmPassword != 0 && UserHandle.myUserId() == 0) {
                        MiuiUtils.handleForgetPasswordRequestForSSpace(ConfirmLockPasswordFragment.this.getActivity(), null, null, ConfirmLockPasswordFragment.this.mLockPatternUtils, ConfirmLockPasswordFragment.this.mUserIdToConfirmPassword);
                        return;
                    }
                    ConfirmLockPasswordFragment.this.mForgetPasswordDialog = new ForgetPasswordDialog(ConfirmLockPasswordFragment.this.getActivity());
                    ConfirmLockPasswordFragment.this.mForgetPasswordDialog.show();
                }
            });
            int inputType = this.mPasswordEntry.getInputType();
            TextView textView5 = this.mPasswordEntry;
            if (!this.mIsAlpha) {
                inputType = 18;
            }
            textView5.setInputType(inputType);
            getActivity().getWindow().clearFlags(131072);
            return inflate;
        }

        protected String getDefaultHeader() {
            return this.mIsFromFrp ? this.mHeaderConfirmFRP : this.mIsManagedProfile ? getString(R.string.lockpassword_confirm_your_password_generic_profile) : !TextUtils.isEmpty(this.mHeaderMessage) ? this.mHeaderMessage.toString() : this.mIsLockPassword ? this.mHeaderConfirmLockpassword : this.mHeaderConfirmPrivatepassword;
        }

        public Bundle getIntentArguments() {
            return isConfirmLockPasswordActivity() ? getActivity().getIntent().getExtras() : getArguments();
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment
        protected int getLastTryErrorMessage(int i) {
            return R.string.lock_profile_wipe_warning_content_pattern;
        }

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return getMetricsPasswordCategory();
        }

        protected void handleWrongPassword(String str) {
            handleWrongPassword(str, 0);
        }

        protected void handleWrongPassword(String str, int i) {
            if (str == null || str.length() < 4 || i <= 0) {
                showError(R.string.lockpattern_need_to_unlock_wrong);
            } else {
                handleAttemptLockout(this.mLockPatternUtils.setLockoutAttemptDeadline(this.mUserIdToConfirmPassword, i));
            }
        }

        @Override // com.android.settings.OnBackPressedListener
        public boolean onBackPressed() {
            if (TabletUtils.IS_TABLET) {
                FragmentManager fragmentManager = getFragmentManager();
                while (fragmentManager.getBackStackEntryCount() > 1) {
                    fragmentManager.popBackStackImmediate();
                }
                return true;
            }
            return false;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.footerRightButton) {
                handleNext();
            } else if (id == R.id.footerLeftButton) {
                if (getTargetFragment() != null) {
                    onBackPressed();
                    return;
                }
                getActivity().setResult(0);
                getActivity().finish();
            }
        }

        @Override // com.android.settings.BaseConfirmLockFragment, com.android.settings.password.ConfirmDeviceCredentialBaseFragment, com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mFingerprintHelper = new FingerprintHelper(getActivity());
            this.mLockPatternUtils = new MiuiLockPatternUtils(getActivity());
            this.mContext = getContext();
            Bundle intentArguments = getIntentArguments();
            this.bundle = intentArguments;
            if (intentArguments != null) {
                this.mReturnCredentials = intentArguments.getBoolean("return_credentials", false);
            }
            boolean z = true;
            this.mReturnCredentials = this.mReturnCredentials || this.bundle.getBoolean("return_credentials", false);
            int effectiveUserId = MiuiKeyguardSettingsUtils.getEffectiveUserId(getActivity(), this.bundle);
            if (!this.bundle.getBoolean("from_confirm_frp_credential", false) && !"android.app.action.CONFIRM_FRP_CREDENTIAL".equalsIgnoreCase(getActivity().getIntent().getAction()) && effectiveUserId != -9999) {
                z = false;
            }
            this.mIsFromFrp = z;
            if (z) {
                effectiveUserId = -9999;
            }
            this.mDefaultUserId = effectiveUserId;
            this.mUserIdToConfirmPassword = z ? -9999 : UserHandle.myUserId();
            if (bundle != null) {
                this.mNumWrongConfirmAttempts = bundle.getInt("confirm_lock_password_fragment.key_num_wrong_confirm_attempts", 0);
                if (bundle.getInt("save_ui_mode_night") != (getResources().getConfiguration().uiMode & 48)) {
                    getActivity().setResult(0);
                    getActivity().finish();
                    return;
                }
            }
            getActivity().getWindow().setSoftInputMode(16);
            getActivity().getWindow().addExtraFlags(16);
            if (MiuiKeyguardSettingsUtils.isInFullWindowGestureMode(getActivity().getApplicationContext())) {
                getActivity().getWindow().clearFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
            }
            getActivity().getWindow().addFlags(8192);
            this.mImm = (InputMethodManager) getActivity().getSystemService("input_method");
        }

        @Override // androidx.fragment.app.Fragment
        public void onDetach() {
            MiuiSecurityCommonSettings.setFragmentResultOnDetach(this, this.mFragmentResult, this.mCurrentPassword, this.outbundle);
            this.mHandler.removeCallbacks(this.mResetErrorRunnable);
            Runnable runnable = this.mTopBlankHeightRunnable;
            if (runnable != null) {
                this.mHandler.removeCallbacks(runnable);
            }
            super.onDetach();
        }

        @Override // android.widget.TextView.OnEditorActionListener
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 0 || i == 6 || i == 5) {
                handleNext();
                return true;
            }
            return false;
        }

        @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public boolean onOptionsItemSelected(MenuItem menuItem) {
            return !onBackPressed() && super.onOptionsItemSelected(menuItem);
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onPause() {
            super.onPause();
            CountDownTimer countDownTimer = this.mCountdownTimer;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                this.mCountdownTimer = null;
            }
            AsyncTask<?, ?, ?> asyncTask = this.mPendingLockCheck;
            if (asyncTask != null) {
                asyncTask.cancel(false);
                this.mPendingLockCheck = null;
            }
            ForgetPasswordDialog forgetPasswordDialog = this.mForgetPasswordDialog;
            if (forgetPasswordDialog != null) {
                forgetPasswordDialog.dismiss();
            }
            this.mImm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment, com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            long lockoutAttemptDeadline = this.mLockPatternUtils.getLockoutAttemptDeadline(this.mUserIdToConfirmPassword);
            if (lockoutAttemptDeadline != 0) {
                handleAttemptLockout(lockoutAttemptDeadline);
            } else {
                this.mPasswordEntry.setEnabled(true);
                this.mHeaderText.setText(getDefaultHeader());
                this.mNumWrongConfirmAttempts = 0;
            }
            this.mPasswordEntry.requestFocus();
            this.mImm.showSoftInput(this.mPasswordEntry, 1);
            setForgetPasswordVisibility(getActivity());
        }

        @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putInt("confirm_lock_password_fragment.key_num_wrong_confirm_attempts", this.mNumWrongConfirmAttempts);
            bundle.putInt("save_ui_mode_night", getResources().getConfiguration().uiMode & 48);
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment
        protected void onShowError() {
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onStart() {
            super.onStart();
            MiuiUtils.onStartEdit(this);
            boolean z = this.bundle.getBoolean("has_challenge", false);
            this.mVerifyChallenge = z;
            if (z) {
                this.mFingerprintHelper.generateChallenge(UserHandle.myUserId(), new FingerprintManager.GenerateChallengeCallback() { // from class: com.android.settings.ConfirmLockPassword$ConfirmLockPasswordFragment$$ExternalSyntheticLambda0
                    public final void onChallengeGenerated(int i, int i2, long j) {
                        ConfirmLockPassword.ConfirmLockPasswordFragment.this.lambda$onStart$0(i, i2, j);
                    }
                });
                showProgressAnim();
            }
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
        public void onStop() {
            MiuiUtils.onFinishEdit(this);
            super.onStop();
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void setForgetPasswordVisibility(Activity activity) {
            Account[] accountsByType = AccountManager.get(activity).getAccountsByType("com.xiaomi");
            if (accountsByType == null || accountsByType.length <= 0 || !this.mIsShowForgetPwd || this.mUserIdToConfirmPassword == -9999 || this.mIsManagedProfile) {
                this.mForgetPassword.setVisibility(4);
            } else {
                this.mForgetPassword.setVisibility(0);
            }
        }

        public void showError(int i, long j) {
            this.mHeaderText.setText(i);
            TextView textView = this.mHeaderText;
            textView.announceForAccessibility(textView.getText());
            this.mPasswordEntry.setText((CharSequence) null);
            this.mHandler.removeCallbacks(this.mResetErrorRunnable);
            if (j != 0) {
                this.mHandler.postDelayed(this.mResetErrorRunnable, j);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class InternalActivity extends ConfirmLockPassword {
        public static String getExtraFragmentName() {
            return ConfirmLockPasswordFragment.class.getName();
        }
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", ConfirmLockPasswordFragment.class.getName());
        intent.putExtra(":android:no_headers", true);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return ConfirmLockPasswordFragment.class.getName().equals(str);
    }
}
