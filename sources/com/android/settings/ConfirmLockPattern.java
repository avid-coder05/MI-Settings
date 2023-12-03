package com.android.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.security.MiuiLockPatternUtils;
import android.util.Log;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.android.internal.widget.LockPatternView;
import com.android.settings.ConfirmLockPattern;
import com.android.settings.LockPatternChecker;
import com.android.settings.LockPatternView;
import com.android.settings.compat.LockPatternUtilsCompat;
import com.android.settings.password.ConfirmDeviceCredentialBaseFragment;
import com.android.settings.utils.TabletUtils;
import java.lang.reflect.Field;
import java.util.List;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.ProgressDialog;

/* loaded from: classes.dex */
public class ConfirmLockPattern extends BaseConfirmLockActivity {

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.ConfirmLockPattern$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$ConfirmLockPattern$Stage;

        static {
            int[] iArr = new int[Stage.values().length];
            $SwitchMap$com$android$settings$ConfirmLockPattern$Stage = iArr;
            try {
                iArr[Stage.NeedToUnlock.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$ConfirmLockPattern$Stage[Stage.NeedToUnlockWrong.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$ConfirmLockPattern$Stage[Stage.LockedOut.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* loaded from: classes.dex */
    public static class ConfirmLockPatternFragment extends BaseConfirmLockFragment implements OnBackPressedListener {
        private Bundle bundle;
        private int mBackGroundColor;
        private Context mContext;
        private CountDownTimer mCountdownTimer;
        private int mDefaultUserId;
        private ContentObserver mDisableObserver;
        protected FingerprintHelper mFingerprintHelper;
        protected CharSequence mFooterText;
        private TextView mFooterTextView;
        private int mFooterViewColor;
        private CharSequence mFooterWrongText;
        private View.OnClickListener mForgetClickListener;
        protected Button mForgetPattern;
        private int mForgetPatternColor;
        private TextView mFrpHeaderMessage;
        private TextView mFrpSkipPassword;
        protected CharSequence mHeaderText;
        private TextView mHeaderTextView;
        private CharSequence mHeaderWrongText;
        private ImageView mIconView;
        private boolean mIsFromFrp;
        private boolean mIsLockPassword;
        private boolean mIsLockTouchBtnWhite;
        private boolean mIsManagedProfile;
        private boolean mIsReOnCreate;
        private boolean mIsShowForgetPwd;
        protected MiuiLockPatternUtils mLockPatternUtils;
        private LockPatternView mLockPatternView;
        private int mNumWrongConfirmAttempts;
        private AsyncTask<?, ?, ?> mPendingLockCheck;
        private TextView mSubHeaderTextView;
        private int mTitleTextColor;
        private View mTopView;
        private boolean mVerifyChallenge;
        private Bundle outbundle;
        private int mFragmentResult = -1;
        private String mCurrentPassword = null;
        protected int mUserIdToConfirmPattern = UserHandle.myUserId();
        private boolean mReturnCredentials = false;
        private ForgetPasswordDialog mForgetPasswordDialog = null;
        private long mFingerEnrollChallenge = 0;
        private Runnable mClearPatternRunnable = new Runnable() { // from class: com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment.6
            @Override // java.lang.Runnable
            public void run() {
                ConfirmLockPatternFragment.this.mLockPatternView.clearPattern();
            }
        };
        private LockPatternView.OnPatternListener mConfirmExistingLockPatternListener = new LockPatternView.OnPatternListener() { // from class: com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment.7
            @Override // com.android.settings.LockPatternView.OnPatternListener
            public void onPatternCellAdded(List<LockPatternView.Cell> list) {
            }

            @Override // com.android.settings.LockPatternView.OnPatternListener
            public void onPatternCleared() {
                ConfirmLockPatternFragment.this.mLockPatternView.removeCallbacks(ConfirmLockPatternFragment.this.mClearPatternRunnable);
            }

            @Override // com.android.settings.LockPatternView.OnPatternListener
            public void onPatternDetected(List<LockPatternView.Cell> list) {
                ConfirmLockPatternFragment.this.mLockPatternView.setEnabled(false);
                if (ConfirmLockPatternFragment.this.mPendingLockCheck != null) {
                    ConfirmLockPatternFragment.this.mPendingLockCheck.cancel(false);
                }
                if (ConfirmLockPatternFragment.this.bundle.getBoolean("has_challenge", false)) {
                    ConfirmLockPatternFragment.this.startVerifyPattern(list);
                } else {
                    ConfirmLockPatternFragment.this.startCheckPattern(list);
                }
            }

            @Override // com.android.settings.LockPatternView.OnPatternListener
            public void onPatternStart() {
                ConfirmLockPatternFragment.this.mLockPatternView.removeCallbacks(ConfirmLockPatternFragment.this.mClearPatternRunnable);
            }
        };

        private int getLastTryErrorMessage(boolean z) {
            return z ? R.string.lock_profile_wipe_warning_content_password : R.string.lock_profile_wipe_warning_content_pin;
        }

        private void handleWrongPattern(List<LockPatternView.Cell> list) {
            handleWrongPattern(list, 0);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleWrongPattern(List<LockPatternView.Cell> list, int i) {
            if (list.size() < 4 || i <= 0) {
                updateStage(Stage.NeedToUnlockWrong);
                postClearPatternRunnable();
                return;
            }
            long lockoutAttemptDeadline = this.mLockPatternUtils.setLockoutAttemptDeadline(this.mUserIdToConfirmPattern, i);
            setLockoutAttepmpDeadline(lockoutAttemptDeadline);
            handleAttemptLockout(lockoutAttemptDeadline);
        }

        private boolean isConfirmLockPatternActivity() {
            return getActivity() instanceof ConfirmLockPattern;
        }

        private boolean isInternalActivity() {
            return getActivity() instanceof InternalActivity;
        }

        private boolean isValidPattern(List<LockPatternView.Cell> list) {
            return list.size() >= 4;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onStart$0(int i, int i2, long j) {
            Slog.d(ConfirmDeviceCredentialBaseFragment.TAG, "generateChallenge challenge=" + j);
            this.mFingerEnrollChallenge = j;
        }

        private void postClearPatternRunnable() {
            this.mLockPatternView.removeCallbacks(this.mClearPatternRunnable);
            this.mLockPatternView.postDelayed(this.mClearPatternRunnable, 2000L);
        }

        private void showProgressAnim() {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getActivity().getString(R.string.security_check_progress_dialog_message));
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(1);
            progressDialog.setCancelable(false);
            ValueAnimator ofInt = ValueAnimator.ofInt(0, 100);
            ofInt.setDuration(2000L);
            ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment.4
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    progressDialog.setProgress(((Integer) valueAnimator.getAnimatedValue()).intValue());
                }
            });
            ofInt.addListener(new Animator.AnimatorListener() { // from class: com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment.5
                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    progressDialog.setProgress(100);
                    progressDialog.dismiss();
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    Slog.d(ConfirmDeviceCredentialBaseFragment.TAG, "generateChallenge onAnimationEnd challenge=" + ConfirmLockPatternFragment.this.mFingerEnrollChallenge);
                    if (ConfirmLockPatternFragment.this.mFingerEnrollChallenge == 0) {
                        ConfirmLockPatternFragment.this.getActivity().finish();
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

        /* JADX INFO: Access modifiers changed from: private */
        public void startCheckPattern(final List<LockPatternView.Cell> list) {
            if (!isValidPattern(list)) {
                handleWrongPattern(list);
            } else if (!(getActivity() instanceof ConfirmAccessControl) && !(getActivity() instanceof ConfirmGalleryLockPattern) && !(getActivity() instanceof ConfirmSmsLockPattern)) {
                this.mPendingLockCheck = LockPatternChecker.checkPattern(this.mLockPatternUtils, list, this.mUserIdToConfirmPattern, this.mContext, new LockPatternChecker.OnCheckCallback() { // from class: com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment.9
                    @Override // com.android.settings.LockPatternChecker.OnCheckCallback
                    public void onChecked(boolean z, int i) {
                        ConfirmLockPatternFragment.this.mPendingLockCheck = null;
                        if (z) {
                            ConfirmLockPatternFragment.this.accessLockPattern(list);
                        } else {
                            ConfirmLockPatternFragment.this.handleWrongPattern(list, i);
                        }
                    }
                });
            } else if (checkPattern(list)) {
                accessLockPattern(list);
            } else {
                handleWrongPattern(list);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startVerifyPattern(final List<LockPatternView.Cell> list) {
            AsyncTask<?, ?, ?> verifyTiedProfileChallenge;
            if (!isValidPattern(list)) {
                handleWrongPattern(list);
                return;
            }
            int i = this.mEffectiveUserId;
            int i2 = this.mUserId;
            LockPatternChecker.OnVerifyCallback onVerifyCallback = new LockPatternChecker.OnVerifyCallback() { // from class: com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment.8
                @Override // com.android.settings.LockPatternChecker.OnVerifyCallback
                public void onVerified(byte[] bArr, int i3) {
                    ConfirmLockPatternFragment.this.mPendingLockCheck = null;
                    if (bArr != null) {
                        ConfirmLockPatternFragment.this.accessLockPattern(list, bArr);
                    } else {
                        ConfirmLockPatternFragment.this.handleWrongPattern(list, i3);
                    }
                }
            };
            if (i == i2) {
                verifyTiedProfileChallenge = LockPatternChecker.verifyPattern(this.mLockPatternUtils, list, this.mFingerEnrollChallenge, i2, this.mContext, onVerifyCallback);
            } else {
                MiuiLockPatternUtils miuiLockPatternUtils = this.mLockPatternUtils;
                verifyTiedProfileChallenge = LockPatternChecker.verifyTiedProfileChallenge(miuiLockPatternUtils, LockPatternUtilsCompat.patternToString(miuiLockPatternUtils, list), true, i2, onVerifyCallback);
            }
            this.mPendingLockCheck = verifyTiedProfileChallenge;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void accessLockPattern(List<LockPatternView.Cell> list) {
            accessLockPattern(list, null);
        }

        protected void accessLockPattern(List<LockPatternView.Cell> list, byte[] bArr) {
            this.mLockPatternView.setEnabled(true);
            int i = this.mUserIdToConfirmPattern;
            if (i != 0 && i != -9999 && this.mLockPatternUtils.getStrongAuthForUser(i) != 0) {
                try {
                    this.mLockPatternUtils.userPresent(this.mUserIdToConfirmPattern);
                } catch (Exception e) {
                    Log.e(ConfirmDeviceCredentialBaseFragment.TAG, "sth wrong when user present", e);
                }
            }
            Fragment targetFragment = getTargetFragment();
            Bundle bundle = new Bundle();
            this.outbundle = bundle;
            bundle.putInt("type", 2);
            if (isInternalActivity() || this.mReturnCredentials) {
                this.outbundle.putString("password", LockPatternUtilsCompat.patternToString(this.mLockPatternUtils, list));
            }
            if (bArr != null) {
                this.outbundle.putByteArray("hw_auth_token", bArr);
            }
            if (targetFragment == null && getActivity() != null && !getActivity().isFinishing()) {
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
            } else if (MiuiKeyguardSettingsUtils.instanceofSettingsPreFragment(targetFragment)) {
                this.mFragmentResult = 0;
                if (this.mReturnCredentials) {
                    this.mCurrentPassword = LockPatternUtilsCompat.patternToString(this.mLockPatternUtils, list);
                }
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    fragmentManager.popBackStackImmediate();
                }
            }
        }

        protected boolean checkPattern(List<LockPatternView.Cell> list) {
            return false;
        }

        @Override // com.android.settings.BaseConfirmLockFragment
        public View createView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            View inflate = layoutInflater.inflate(R.layout.miui_confirm_lock_pattern, (ViewGroup) null);
            this.mTopView = inflate;
            this.mHeaderTextView = (TextView) inflate.findViewById(R.id.headerText);
            this.mSubHeaderTextView = (TextView) this.mTopView.findViewById(R.id.subHeaderText);
            this.mLockPatternView = (LockPatternView) this.mTopView.findViewById(R.id.lockPattern);
            this.mFooterTextView = (TextView) this.mTopView.findViewById(R.id.footerText);
            this.mIsLockPassword = (this.mIsFromFrp ? this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mUserIdToConfirmPattern) : this.mLockPatternUtils.getActivePasswordQuality(this.mUserIdToConfirmPattern)) != 0;
            this.mForgetPattern = (Button) this.mTopView.findViewById(R.id.forgetPattern);
            this.mForgetClickListener = new View.OnClickListener() { // from class: com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (ConfirmLockPatternFragment.this.mUserIdToConfirmPattern == 0 || UserHandle.myUserId() != 0) {
                        ConfirmLockPatternFragment.this.mForgetPasswordDialog = new ForgetPasswordDialog(ConfirmLockPatternFragment.this.getActivity());
                        ConfirmLockPatternFragment.this.mForgetPasswordDialog.show();
                        return;
                    }
                    FragmentActivity activity = ConfirmLockPatternFragment.this.getActivity();
                    String disableKey = ConfirmLockPatternFragment.this.getDisableKey();
                    ConfirmLockPatternFragment confirmLockPatternFragment = ConfirmLockPatternFragment.this;
                    MiuiUtils.handleForgetPasswordRequestForSSpace(activity, disableKey, null, confirmLockPatternFragment.mLockPatternUtils, confirmLockPatternFragment.mUserIdToConfirmPattern);
                }
            };
            setForgetPatternVisibility(getActivity());
            this.mForgetPattern.setOnClickListener(this.mForgetClickListener);
            TextView textView = (TextView) this.mTopView.findViewById(R.id.frpSkipPassword);
            this.mFrpSkipPassword = textView;
            textView.setVisibility(this.mIsFromFrp ? 0 : 8);
            this.mFrpSkipPassword.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment.3
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ConfirmLockPatternFragment.this.getActivity().setResult(1);
                    ConfirmLockPatternFragment.this.getActivity().finish();
                }
            });
            TextView textView2 = (TextView) this.mTopView.findViewById(R.id.headerMessage);
            this.mFrpHeaderMessage = textView2;
            textView2.setVisibility(this.mIsFromFrp ? 0 : 8);
            ImageView imageView = (ImageView) this.mTopView.findViewById(R.id.cts_icon);
            this.mIconView = imageView;
            if (imageView != null && this.mIsManagedProfile) {
                this.mIconView.setImageDrawable(getResources().getDrawable(R.drawable.auth_dialog_enterprise, this.mContext.getTheme()));
                this.mIconView.setVisibility(0);
            }
            this.mTopView.findViewById(R.id.topLayout).setDefaultTouchRecepient(this.mLockPatternView);
            this.mHeaderTextView.setTextColor(this.mTitleTextColor);
            this.mSubHeaderTextView.setTextColor(this.mTitleTextColor);
            this.mFooterTextView.setTextColor(this.mFooterViewColor);
            this.mForgetPattern.setTextColor(this.mForgetPatternColor);
            this.mTopView.setBackgroundColor(this.mBackGroundColor);
            if (this.mIsLockTouchBtnWhite) {
                this.mLockPatternView.setBitmapBtnTouched(R.drawable.lock_pattern_code_lock_white);
            }
            this.mLockPatternView.setTactileFeedbackEnabled(getTactileFeedbackEnabled());
            this.mLockPatternView.setOnPatternListener(this.mConfirmExistingLockPatternListener);
            updateStage(Stage.NeedToUnlock);
            if (bundle != null) {
                this.mNumWrongConfirmAttempts = bundle.getInt("num_wrong_attempts");
                if (bundle.getInt("save_ui_mode_night") != (getResources().getConfiguration().uiMode & 48)) {
                    getActivity().setResult(0);
                    getActivity().finish();
                }
            } else {
                onCreateNoSaveState();
            }
            return this.mTopView;
        }

        protected String getDisableKey() {
            return null;
        }

        protected boolean getInStealthMode() {
            return !this.mLockPatternUtils.isVisiblePatternEnabled(this.mUserIdToConfirmPattern);
        }

        public Bundle getIntentArguments() {
            return isConfirmLockPatternActivity() ? getActivity().getIntent().getExtras() : getArguments();
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment
        protected int getLastTryErrorMessage(int i) {
            return getLastTryErrorMessage(false);
        }

        protected long getLockoutAttepmpDeadline(long j) {
            return j;
        }

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return getMetricsPatternCategory();
        }

        protected boolean getTactileFeedbackEnabled() {
            return this.mLockPatternUtils.isTactileFeedbackEnabled();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void handleAttemptLockout(long j) {
            updateStage(Stage.LockedOut);
            refreshLockScreen();
            this.mCountdownTimer = new CountDownTimer(j - SystemClock.elapsedRealtime(), 1000L) { // from class: com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment.10
                @Override // android.os.CountDownTimer
                public void onFinish() {
                    ConfirmLockPatternFragment.this.mNumWrongConfirmAttempts = 0;
                    ConfirmLockPatternFragment.this.updateStage(Stage.NeedToUnlock);
                }

                @Override // android.os.CountDownTimer
                public void onTick(long j2) {
                    int i = (int) (j2 / 1000);
                    ConfirmLockPatternFragment.this.mFooterTextView.setText(ConfirmLockPatternFragment.this.getResources().getQuantityString(R.plurals.lockpattern_too_many_failed_confirmation_attempts, i, Integer.valueOf(i)));
                }
            }.start();
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);
            if (bundle == null || bundle.getParcelable("account_dialog_state") == null) {
                return;
            }
            this.mForgetPattern.setVisibility(0);
            this.mForgetPattern.setTag(bundle.getParcelable("account_dialog_state"));
            this.mForgetClickListener.onClick(this.mForgetPattern);
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

        @Override // com.android.settings.BaseConfirmLockFragment, com.android.settings.password.ConfirmDeviceCredentialBaseFragment, com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mFingerprintHelper = new FingerprintHelper(getActivity());
            this.mContext = getContext();
            this.mIsReOnCreate = bundle != null;
            Bundle intentArguments = getIntentArguments();
            this.bundle = intentArguments;
            if (intentArguments != null) {
                this.mReturnCredentials = intentArguments.getBoolean("return_credentials", false);
            }
            this.mReturnCredentials = this.mReturnCredentials || this.bundle.getBoolean("return_credentials", false);
            this.mLockPatternUtils = new MiuiLockPatternUtils(getActivity());
            int effectiveUserId = MiuiKeyguardSettingsUtils.getEffectiveUserId(getActivity(), this.bundle);
            boolean z = this.bundle.getBoolean("from_confirm_frp_credential", false) || "android.app.action.CONFIRM_FRP_CREDENTIAL".equalsIgnoreCase(getActivity().getIntent().getAction()) || effectiveUserId == -9999;
            this.mIsFromFrp = z;
            if (z) {
                effectiveUserId = -9999;
            }
            this.mDefaultUserId = effectiveUserId;
            this.mUserIdToConfirmPattern = z ? -9999 : UserHandle.myUserId();
            if (getDisableKey() != null) {
                this.mDisableObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment.1
                    @Override // android.database.ContentObserver
                    public void onChange(boolean z2) {
                        ConfirmLockPatternFragment.this.setLockoutAttepmpDeadline(0L);
                    }
                };
                getActivity().getContentResolver().registerContentObserver(Settings.Secure.getUriFor(getDisableKey()), true, this.mDisableObserver);
            }
            getActivity().getWindow().addExtraFlags(16);
            getActivity().getWindow().addFlags(8192);
        }

        protected void onCreateNoSaveState() {
            int keyguardStoredPasswordQuality = this.mIsFromFrp ? this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mUserIdToConfirmPattern) : this.mLockPatternUtils.getActivePasswordQuality(this.mUserIdToConfirmPattern);
            if (keyguardStoredPasswordQuality != 65536) {
                getActivity().setResult(keyguardStoredPasswordQuality == 0 ? -1 : 0);
                getActivity().finish();
            }
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
        public void onDestroy() {
            if (this.mDisableObserver != null) {
                getActivity().getContentResolver().unregisterContentObserver(this.mDisableObserver);
            }
            super.onDestroy();
        }

        @Override // androidx.fragment.app.Fragment
        public void onDetach() {
            MiuiSecurityCommonSettings.setFragmentResultOnDetach(this, this.mFragmentResult, this.mCurrentPassword, this.outbundle);
            super.onDetach();
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
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment, com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            long lockoutAttepmpDeadline = getLockoutAttepmpDeadline(this.mLockPatternUtils.getLockoutAttemptDeadline(this.mUserIdToConfirmPattern));
            if (lockoutAttepmpDeadline != 0) {
                handleAttemptLockout(lockoutAttepmpDeadline);
            } else if (!this.mLockPatternView.isEnabled()) {
                this.mNumWrongConfirmAttempts = 0;
                updateStage(Stage.NeedToUnlock);
            }
            setForgetPatternVisibility(getActivity());
        }

        @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            bundle.putInt("num_wrong_attempts", this.mNumWrongConfirmAttempts);
            bundle.putInt("save_ui_mode_night", getResources().getConfiguration().uiMode & 48);
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment
        protected void onShowError() {
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onStart() {
            ActionBar appCompatActionBar;
            super.onStart();
            if (this.mIsReOnCreate && (getActivity() instanceof AppCompatActivity) && !isInternalActivity() && (appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar()) != null) {
                appCompatActionBar.hide();
            }
            boolean z = this.bundle.getBoolean("has_challenge", false);
            this.mVerifyChallenge = z;
            if (z) {
                this.mFingerprintHelper.generateChallenge(UserHandle.myUserId(), new FingerprintManager.GenerateChallengeCallback() { // from class: com.android.settings.ConfirmLockPattern$ConfirmLockPatternFragment$$ExternalSyntheticLambda0
                    public final void onChallengeGenerated(int i, int i2, long j) {
                        ConfirmLockPattern.ConfirmLockPatternFragment.this.lambda$onStart$0(i, i2, j);
                    }
                });
                showProgressAnim();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void parseIntent(Intent intent) {
            if (intent != null) {
                this.mHeaderText = MiuiKeyguardSettingsUtils.getHeader(intent);
                this.mFooterText = intent.getCharSequenceExtra("com.android.settings.ConfirmLockPattern.footer");
                this.mHeaderWrongText = intent.getCharSequenceExtra("com.android.settings.ConfirmLockPattern.header_wrong");
                this.mFooterWrongText = intent.getCharSequenceExtra("com.android.settings.ConfirmLockPattern.footer_wrong");
                int intExtra = intent.getIntExtra("com.android.settings.userIdToConfirm", this.mDefaultUserId);
                this.mUserIdToConfirmPattern = intExtra;
                this.mIsLockPassword = (this.mIsFromFrp ? this.mLockPatternUtils.getKeyguardStoredPasswordQuality(intExtra) : this.mLockPatternUtils.getActivePasswordQuality(intExtra)) != 0;
                this.mTitleTextColor = intent.getIntExtra("com.android.settings.titleColor", this.mSubHeaderTextView.getCurrentTextColor());
                this.mBackGroundColor = intent.getIntExtra("com.android.settings.bgColor", this.mTopView.getDrawingCacheBackgroundColor());
                this.mIsLockTouchBtnWhite = intent.getBooleanExtra("com.android.settings.lockBtnWhite", false);
                this.mFooterViewColor = intent.getIntExtra("com.android.settings.footerTextColor", this.mFooterTextView.getCurrentTextColor());
                this.mForgetPatternColor = intent.getIntExtra("com.android.settings.forgetPatternColor", this.mForgetPattern.getCurrentTextColor());
                this.mIsShowForgetPwd = intent.getBooleanExtra("com.android.settings.forgetPassword", true);
            }
            this.mIsManagedProfile = MiuiKeyguardSettingsUtils.isManagedProfile(UserManager.get(getActivity()), this.mUserIdToConfirmPattern);
        }

        public void setForgetPatternVisibility(Activity activity) {
            Account[] accountsByType = AccountManager.get(activity).getAccountsByType("com.xiaomi");
            parseIntent(new Intent().putExtras(this.bundle));
            if (accountsByType == null || accountsByType.length <= 0 || !this.mIsShowForgetPwd || this.mUserIdToConfirmPattern == -9999 || this.mIsManagedProfile) {
                this.mForgetPattern.setVisibility(4);
            } else {
                this.mForgetPattern.setVisibility(0);
            }
        }

        protected void setLockoutAttepmpDeadline(long j) {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void updateStage(Stage stage) {
            int i = AnonymousClass1.$SwitchMap$com$android$settings$ConfirmLockPattern$Stage[stage.ordinal()];
            if (i == 1) {
                CharSequence charSequence = this.mFooterText;
                if (charSequence != null) {
                    this.mFooterTextView.setText(charSequence);
                } else {
                    this.mFooterTextView.setText("");
                }
                this.mLockPatternView.setInStealthMode(getInStealthMode());
                this.mLockPatternView.setEnabled(true);
                this.mLockPatternView.enableInput();
            } else if (i == 2) {
                CharSequence charSequence2 = this.mHeaderWrongText;
                if (charSequence2 != null) {
                    this.mSubHeaderTextView.setText(charSequence2);
                } else {
                    this.mSubHeaderTextView.setText(R.string.lockpattern_need_to_unlock_wrong);
                }
                CharSequence charSequence3 = this.mFooterWrongText;
                if (charSequence3 != null) {
                    this.mFooterTextView.setText(charSequence3);
                }
                this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                this.mLockPatternView.setEnabled(true);
                this.mLockPatternView.enableInput();
            } else if (i == 3) {
                this.mLockPatternView.clearPattern();
                this.mLockPatternView.setEnabled(false);
            }
            TextView textView = this.mSubHeaderTextView;
            textView.announceForAccessibility(textView.getText());
        }
    }

    /* loaded from: classes.dex */
    public static class InternalActivity extends ConfirmLockPattern {
        public static String getExtraFragmentName() {
            return ConfirmLockPatternFragment.class.getName();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public enum Stage {
        NeedToUnlock,
        NeedToUnlockWrong,
        LockedOut
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", ConfirmLockPatternFragment.class.getName());
        intent.putExtra(":android:no_headers", true);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return ConfirmLockPatternFragment.class.getName().equals(str);
    }
}
