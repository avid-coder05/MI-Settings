package com.android.settings;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.os.UserManager;
import android.provider.Settings;
import android.security.ChooseLockSettingsHelper;
import android.security.MiuiLockPatternUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternView;
import com.android.internal.widget.VerifyCredentialResponse;
import com.android.settings.ChooseLockPattern;
import com.android.settings.LockPatternChecker;
import com.android.settings.LockPatternView;
import com.android.settings.compat.LockPatternUtilsCompat;
import com.android.settings.faceunlock.KeyguardSettingsFaceUnlockManager;
import com.android.settings.faceunlock.KeyguardSettingsFaceUnlockUtils;
import com.google.android.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import miui.securityspace.CrossUserUtils;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class ChooseLockPattern extends Settings {

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.ChooseLockPattern$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$ChooseLockPattern$ChooseLockPatternFragment$Stage;

        static {
            int[] iArr = new int[ChooseLockPatternFragment.Stage.values().length];
            $SwitchMap$com$android$settings$ChooseLockPattern$ChooseLockPatternFragment$Stage = iArr;
            try {
                iArr[ChooseLockPatternFragment.Stage.Introduction.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$ChooseLockPattern$ChooseLockPatternFragment$Stage[ChooseLockPatternFragment.Stage.HelpScreen.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$ChooseLockPattern$ChooseLockPatternFragment$Stage[ChooseLockPatternFragment.Stage.ChoiceTooShort.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$settings$ChooseLockPattern$ChooseLockPatternFragment$Stage[ChooseLockPatternFragment.Stage.SameWithOwnerUser.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$settings$ChooseLockPattern$ChooseLockPatternFragment$Stage[ChooseLockPatternFragment.Stage.SameWithSecuritySpaceUser.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$settings$ChooseLockPattern$ChooseLockPatternFragment$Stage[ChooseLockPatternFragment.Stage.SameWithOtherSpaceUser.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$settings$ChooseLockPattern$ChooseLockPatternFragment$Stage[ChooseLockPatternFragment.Stage.FirstChoiceValid.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$settings$ChooseLockPattern$ChooseLockPatternFragment$Stage[ChooseLockPatternFragment.Stage.NeedToConfirm.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$android$settings$ChooseLockPattern$ChooseLockPatternFragment$Stage[ChooseLockPatternFragment.Stage.ConfirmWrong.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$android$settings$ChooseLockPattern$ChooseLockPatternFragment$Stage[ChooseLockPatternFragment.Stage.ChoiceConfirmed.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
        }
    }

    /* loaded from: classes.dex */
    public static class ChooseLockPatternFragment extends KeyguardSettingsPreferenceFragment implements View.OnClickListener {
        protected ChooseLockSettingsHelper mChooseLockSettingsHelper;
        private boolean mEnableKeyguardPassword;
        protected TextView mHeaderText;
        private boolean mIsSetPasswordForAirSpace;
        private boolean mIsSetPasswordForOwnerUser;
        private boolean mIsSetPasswordForSSUser;
        private ProgressDialog mLoadingDialog;
        protected LockPatternView mLockPatternView;
        protected TextView mNextButton;
        protected ImageView mNextImage;
        private AsyncTask<?, ?, ?> mPendingLockCheck;
        protected TextView mResetButton;
        private int mSecuritySpaceId;
        protected TextView mSubHeaderText;
        private int mUserIdToSetPassword;
        private String mUserPassword;
        private boolean mAddKeyguardpasswordThenAddFingerprint = false;
        private boolean mAddKeyguardpasswordThenAddFaceRecoginition = false;
        protected List<LockPatternView.Cell> mChosenPattern = null;
        private Handler mHandler = new Handler();
        private FingerprintHelper mFingerprintHelper = null;
        private final List<LockPatternView.Cell> mAnimatePattern = Collections.unmodifiableList(Lists.newArrayList(new LockPatternView.Cell[]{LockPatternView.Cell.of(0, 0), LockPatternView.Cell.of(0, 1), LockPatternView.Cell.of(1, 1), LockPatternView.Cell.of(2, 1)}));
        protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() { // from class: com.android.settings.ChooseLockPattern.ChooseLockPatternFragment.1
            private void checkPassword(final List<LockPatternView.Cell> list) {
                MiuiLockPatternUtils utils = ChooseLockPatternFragment.this.mChooseLockSettingsHelper.utils();
                if (ChooseLockPatternFragment.this.mSecuritySpaceId == -10000 && !CrossUserUtils.hasAirSpace(ChooseLockPatternFragment.this.getActivity())) {
                    handleFirstChoiceValid(list);
                    return;
                }
                ChooseLockPatternFragment.this.mLockPatternView.setEnabled(false);
                if (ChooseLockPatternFragment.this.mPendingLockCheck != null) {
                    ChooseLockPatternFragment.this.mPendingLockCheck.cancel(false);
                }
                ChooseLockPatternFragment chooseLockPatternFragment = ChooseLockPatternFragment.this;
                chooseLockPatternFragment.mPendingLockCheck = LockPatternChecker.checkPatternForUsers(utils, list, chooseLockPatternFragment.getUserList(chooseLockPatternFragment.getActivity()), ChooseLockPatternFragment.this.getActivity(), new LockPatternChecker.OnCheckForUsersCallback() { // from class: com.android.settings.ChooseLockPattern.ChooseLockPatternFragment.1.1
                    @Override // com.android.settings.LockPatternChecker.OnCheckForUsersCallback
                    public void onChecked(boolean z, int i, int i2) {
                        ChooseLockPatternFragment.this.mPendingLockCheck = null;
                        ChooseLockPatternFragment.this.mLockPatternView.setEnabled(true);
                        if (!z || i == ChooseLockPatternFragment.this.mUserIdToSetPassword) {
                            handleFirstChoiceValid(list);
                        } else if (i == 0) {
                            ChooseLockPatternFragment.this.updateStage(Stage.SameWithOwnerUser);
                        } else if (i == ChooseLockPatternFragment.this.mSecuritySpaceId) {
                            ChooseLockPatternFragment.this.updateStage(Stage.SameWithSecuritySpaceUser);
                        } else {
                            ChooseLockPatternFragment.this.updateStage(Stage.SameWithOtherSpaceUser);
                        }
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void handleFirstChoiceValid(List<LockPatternView.Cell> list) {
                ChooseLockPatternFragment.this.mChosenPattern = new ArrayList(list);
                ChooseLockPatternFragment.this.updateStage(Stage.FirstChoiceValid);
            }

            private void patternInProgress() {
                ChooseLockPatternFragment.this.mSubHeaderText.setText(R.string.lockpattern_recording_inprogress);
                ChooseLockPatternFragment.this.mResetButton.setEnabled(false);
                ChooseLockPatternFragment.this.setNextEnable(false);
            }

            @Override // com.android.settings.LockPatternView.OnPatternListener
            public void onPatternCellAdded(List<LockPatternView.Cell> list) {
            }

            @Override // com.android.settings.LockPatternView.OnPatternListener
            public void onPatternCleared() {
                ChooseLockPatternFragment chooseLockPatternFragment = ChooseLockPatternFragment.this;
                chooseLockPatternFragment.mLockPatternView.removeCallbacks(chooseLockPatternFragment.mClearPatternRunnable);
            }

            @Override // com.android.settings.LockPatternView.OnPatternListener
            public void onPatternDetected(List<LockPatternView.Cell> list) {
                ChooseLockPatternFragment chooseLockPatternFragment = ChooseLockPatternFragment.this;
                Stage stage = chooseLockPatternFragment.mUiStage;
                if (stage == Stage.NeedToConfirm || stage == Stage.ConfirmWrong) {
                    List<LockPatternView.Cell> list2 = chooseLockPatternFragment.mChosenPattern;
                    if (list2 == null) {
                        throw new IllegalStateException("null chosen pattern in stage 'need to confirm");
                    }
                    if (list2.equals(list)) {
                        ChooseLockPatternFragment.this.updateStage(Stage.ChoiceConfirmed);
                    } else {
                        ChooseLockPatternFragment.this.updateStage(Stage.ConfirmWrong);
                    }
                } else if (stage == Stage.Introduction || stage == Stage.ChoiceTooShort || stage == Stage.SameWithOwnerUser || stage == Stage.SameWithSecuritySpaceUser || stage == Stage.SameWithOtherSpaceUser) {
                    if (list.size() < 4) {
                        ChooseLockPatternFragment.this.updateStage(Stage.ChoiceTooShort);
                    } else {
                        checkPassword(list);
                    }
                } else if (stage != Stage.ChoiceConfirmed || chooseLockPatternFragment.isResumed()) {
                    throw new IllegalStateException("Unexpected stage " + ChooseLockPatternFragment.this.mUiStage + " when entering the pattern.");
                }
            }

            @Override // com.android.settings.LockPatternView.OnPatternListener
            public void onPatternStart() {
                ChooseLockPatternFragment chooseLockPatternFragment = ChooseLockPatternFragment.this;
                chooseLockPatternFragment.mLockPatternView.removeCallbacks(chooseLockPatternFragment.mClearPatternRunnable);
                patternInProgress();
            }
        };
        protected Stage mUiStage = Stage.Introduction;
        private Runnable mClearPatternRunnable = new Runnable() { // from class: com.android.settings.ChooseLockPattern.ChooseLockPatternFragment.2
            @Override // java.lang.Runnable
            public void run() {
                ChooseLockPatternFragment.this.mLockPatternView.clearPattern();
            }
        };

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.settings.ChooseLockPattern$ChooseLockPatternFragment$5  reason: invalid class name */
        /* loaded from: classes.dex */
        public class AnonymousClass5 extends AsyncTask<Void, Void, byte[]> {
            final /* synthetic */ boolean val$isFallback;
            final /* synthetic */ boolean val$isNeedAddFace;
            final /* synthetic */ boolean val$isShowDialogToAddFingerprint;
            final /* synthetic */ boolean val$mChallenge;
            final /* synthetic */ LockPatternUtils val$utils;

            AnonymousClass5(boolean z, boolean z2, boolean z3, boolean z4, LockPatternUtils lockPatternUtils) {
                this.val$isFallback = z;
                this.val$isShowDialogToAddFingerprint = z2;
                this.val$isNeedAddFace = z3;
                this.val$mChallenge = z4;
                this.val$utils = lockPatternUtils;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$doInBackground$0(boolean z) {
                ChooseLockPatternFragment.this.onPasswordSaved(null, z);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$doInBackground$1(byte[] bArr, boolean z, LockPatternUtils lockPatternUtils, long j) {
                ChooseLockPatternFragment.this.onPasswordSaved(bArr, z);
                LockPatternUtilsCompat.removeGatekeeperPasswordHandle(lockPatternUtils, j);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$doInBackground$2(VerifyCredentialResponse verifyCredentialResponse, final LockPatternUtils lockPatternUtils, Activity activity, final boolean z, int i, int i2, long j) {
                final long gatekeeperPasswordHandle = verifyCredentialResponse.getGatekeeperPasswordHandle();
                final byte[] verifyGatekeeperPasswordHandle = LockPatternUtilsCompat.verifyGatekeeperPasswordHandle(lockPatternUtils, gatekeeperPasswordHandle, j, ChooseLockPatternFragment.this.mUserIdToSetPassword);
                activity.runOnUiThread(new Runnable() { // from class: com.android.settings.ChooseLockPattern$ChooseLockPatternFragment$5$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChooseLockPattern.ChooseLockPatternFragment.AnonymousClass5.this.lambda$doInBackground$1(verifyGatekeeperPasswordHandle, z, lockPatternUtils, gatekeeperPasswordHandle);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$doInBackground$3(byte[] bArr, boolean z, LockPatternUtils lockPatternUtils, long j) {
                ChooseLockPatternFragment.this.onPasswordSaved(bArr, z);
                LockPatternUtilsCompat.removeGatekeeperPasswordHandle(lockPatternUtils, j);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$doInBackground$4(VerifyCredentialResponse verifyCredentialResponse, final LockPatternUtils lockPatternUtils, Activity activity, final boolean z, int i, int i2, long j) {
                final long gatekeeperPasswordHandle = verifyCredentialResponse.getGatekeeperPasswordHandle();
                final byte[] verifyGatekeeperPasswordHandle = LockPatternUtilsCompat.verifyGatekeeperPasswordHandle(lockPatternUtils, gatekeeperPasswordHandle, j, ChooseLockPatternFragment.this.mUserIdToSetPassword);
                activity.runOnUiThread(new Runnable() { // from class: com.android.settings.ChooseLockPattern$ChooseLockPatternFragment$5$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChooseLockPattern.ChooseLockPatternFragment.AnonymousClass5.this.lambda$doInBackground$3(verifyGatekeeperPasswordHandle, z, lockPatternUtils, gatekeeperPasswordHandle);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public byte[] doInBackground(Void... voidArr) {
                final boolean z;
                final FragmentActivity activity = ChooseLockPatternFragment.this.getActivity();
                if (activity == null) {
                    return null;
                }
                try {
                    MiuiLockPatternUtils miuiLockPatternUtils = new MiuiLockPatternUtils(activity);
                    ChooseLockPatternFragment chooseLockPatternFragment = ChooseLockPatternFragment.this;
                    LockPatternUtilsCompat.saveLockPattern(miuiLockPatternUtils, chooseLockPatternFragment.mChosenPattern, chooseLockPatternFragment.mUserPassword, ChooseLockPatternFragment.this.mUserIdToSetPassword, this.val$isFallback);
                    z = this.val$isShowDialogToAddFingerprint;
                } catch (Exception e) {
                    Log.d("ChooseLockPattern", "critical: no token returned for known good pattern", e);
                }
                if (!z && !this.val$isNeedAddFace && !this.val$mChallenge) {
                    activity.runOnUiThread(new Runnable() { // from class: com.android.settings.ChooseLockPattern$ChooseLockPatternFragment$5$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChooseLockPattern.ChooseLockPatternFragment.AnonymousClass5.this.lambda$doInBackground$0(z);
                        }
                    });
                    return null;
                }
                LockPatternUtils lockPatternUtils = this.val$utils;
                ChooseLockPatternFragment chooseLockPatternFragment2 = ChooseLockPatternFragment.this;
                final VerifyCredentialResponse verifyPattern = LockPatternUtilsCompat.verifyPattern(lockPatternUtils, chooseLockPatternFragment2.mChosenPattern, chooseLockPatternFragment2.mUserIdToSetPassword);
                if (this.val$isNeedAddFace) {
                    KeyguardSettingsFaceUnlockManager keyguardSettingsFaceUnlockManager = KeyguardSettingsFaceUnlockManager.getInstance(activity);
                    final LockPatternUtils lockPatternUtils2 = this.val$utils;
                    final boolean z2 = this.val$isShowDialogToAddFingerprint;
                    keyguardSettingsFaceUnlockManager.generateFaceEnrollChallenge(new FaceManager.GenerateChallengeCallback() { // from class: com.android.settings.ChooseLockPattern$ChooseLockPatternFragment$5$$ExternalSyntheticLambda0
                        public final void onGenerateChallengeResult(int i, int i2, long j) {
                            ChooseLockPattern.ChooseLockPatternFragment.AnonymousClass5.this.lambda$doInBackground$4(verifyPattern, lockPatternUtils2, activity, z2, i, i2, j);
                        }
                    });
                } else {
                    FingerprintHelper fingerprintHelper = ChooseLockPatternFragment.this.mFingerprintHelper;
                    int i = ChooseLockPatternFragment.this.mUserIdToSetPassword;
                    final LockPatternUtils lockPatternUtils3 = this.val$utils;
                    final boolean z3 = this.val$isShowDialogToAddFingerprint;
                    fingerprintHelper.generateChallenge(i, new FingerprintManager.GenerateChallengeCallback() { // from class: com.android.settings.ChooseLockPattern$ChooseLockPatternFragment$5$$ExternalSyntheticLambda1
                        public final void onChallengeGenerated(int i2, int i3, long j) {
                            ChooseLockPattern.ChooseLockPatternFragment.AnonymousClass5.this.lambda$doInBackground$2(verifyPattern, lockPatternUtils3, activity, z3, i2, i3, j);
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

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Enum visitor error
        jadx.core.utils.exceptions.JadxRuntimeException: Init of enum field 'Cancel' uses external variables
        	at jadx.core.dex.visitors.EnumVisitor.createEnumFieldByConstructor(EnumVisitor.java:451)
        	at jadx.core.dex.visitors.EnumVisitor.processEnumFieldByRegister(EnumVisitor.java:395)
        	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromFilledArray(EnumVisitor.java:324)
        	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromInsn(EnumVisitor.java:262)
        	at jadx.core.dex.visitors.EnumVisitor.convertToEnum(EnumVisitor.java:151)
        	at jadx.core.dex.visitors.EnumVisitor.visit(EnumVisitor.java:100)
         */
        /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
        /* loaded from: classes.dex */
        public static final class LeftButtonMode {
            private static final /* synthetic */ LeftButtonMode[] $VALUES;
            public static final LeftButtonMode Cancel;
            public static final LeftButtonMode CancelDisabled;
            public static final LeftButtonMode Gone;
            public static final LeftButtonMode Retry;
            public static final LeftButtonMode RetryDisabled;
            final boolean enabled;
            final int text;

            static {
                int i = R.string.cancel;
                LeftButtonMode leftButtonMode = new LeftButtonMode("Cancel", 0, i, true);
                Cancel = leftButtonMode;
                LeftButtonMode leftButtonMode2 = new LeftButtonMode("CancelDisabled", 1, i, false);
                CancelDisabled = leftButtonMode2;
                int i2 = R.string.lockpattern_retry_button_text;
                LeftButtonMode leftButtonMode3 = new LeftButtonMode("Retry", 2, i2, true);
                Retry = leftButtonMode3;
                LeftButtonMode leftButtonMode4 = new LeftButtonMode("RetryDisabled", 3, i2, false);
                RetryDisabled = leftButtonMode4;
                LeftButtonMode leftButtonMode5 = new LeftButtonMode("Gone", 4, -1, false);
                Gone = leftButtonMode5;
                $VALUES = new LeftButtonMode[]{leftButtonMode, leftButtonMode2, leftButtonMode3, leftButtonMode4, leftButtonMode5};
            }

            private LeftButtonMode(String str, int i, int i2, boolean z) {
                this.text = i2;
                this.enabled = z;
            }

            public static LeftButtonMode valueOf(String str) {
                return (LeftButtonMode) Enum.valueOf(LeftButtonMode.class, str);
            }

            public static LeftButtonMode[] values() {
                return (LeftButtonMode[]) $VALUES.clone();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Enum visitor error
        jadx.core.utils.exceptions.JadxRuntimeException: Init of enum field 'Continue' uses external variables
        	at jadx.core.dex.visitors.EnumVisitor.createEnumFieldByConstructor(EnumVisitor.java:451)
        	at jadx.core.dex.visitors.EnumVisitor.processEnumFieldByRegister(EnumVisitor.java:395)
        	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromFilledArray(EnumVisitor.java:324)
        	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromInsn(EnumVisitor.java:262)
        	at jadx.core.dex.visitors.EnumVisitor.convertToEnum(EnumVisitor.java:151)
        	at jadx.core.dex.visitors.EnumVisitor.visit(EnumVisitor.java:100)
         */
        /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
        /* loaded from: classes.dex */
        public static final class RightButtonMode {
            private static final /* synthetic */ RightButtonMode[] $VALUES;
            public static final RightButtonMode Confirm;
            public static final RightButtonMode ConfirmDisabled;
            public static final RightButtonMode Continue;
            public static final RightButtonMode ContinueDisabled;
            public static final RightButtonMode Gone;
            public static final RightButtonMode Ok;
            final boolean enabled;
            final int text;

            static {
                int i = R.string.lockpattern_continue_button_text;
                RightButtonMode rightButtonMode = new RightButtonMode("Continue", 0, i, true);
                Continue = rightButtonMode;
                RightButtonMode rightButtonMode2 = new RightButtonMode("ContinueDisabled", 1, i, false);
                ContinueDisabled = rightButtonMode2;
                int i2 = R.string.lockpattern_confirm_button_text;
                RightButtonMode rightButtonMode3 = new RightButtonMode("Confirm", 2, i2, true);
                Confirm = rightButtonMode3;
                RightButtonMode rightButtonMode4 = new RightButtonMode("ConfirmDisabled", 3, i2, false);
                ConfirmDisabled = rightButtonMode4;
                RightButtonMode rightButtonMode5 = new RightButtonMode("Ok", 4, 17039370, true);
                Ok = rightButtonMode5;
                RightButtonMode rightButtonMode6 = new RightButtonMode("Gone", 5, -1, false);
                Gone = rightButtonMode6;
                $VALUES = new RightButtonMode[]{rightButtonMode, rightButtonMode2, rightButtonMode3, rightButtonMode4, rightButtonMode5, rightButtonMode6};
            }

            private RightButtonMode(String str, int i, int i2, boolean z) {
                this.text = i2;
                this.enabled = z;
            }

            public static RightButtonMode valueOf(String str) {
                return (RightButtonMode) Enum.valueOf(RightButtonMode.class, str);
            }

            public static RightButtonMode[] values() {
                return (RightButtonMode[]) $VALUES.clone();
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
            public static final Stage ChoiceConfirmed;
            public static final Stage ChoiceTooShort;
            public static final Stage ConfirmWrong;
            public static final Stage FirstChoiceValid;
            public static final Stage HelpScreen;
            public static final Stage Introduction;
            public static final Stage NeedToConfirm;
            public static final Stage SameWithOtherSpaceUser;
            public static final Stage SameWithOwnerUser;
            public static final Stage SameWithSecuritySpaceUser;
            final int footerMessage;
            int headerMessage;
            final LeftButtonMode leftMode;
            final boolean patternEnabled;
            final RightButtonMode rightMode;

            static {
                int i = R.string.lockpattern_recording_intro_header;
                LeftButtonMode leftButtonMode = LeftButtonMode.Gone;
                RightButtonMode rightButtonMode = RightButtonMode.Gone;
                Stage stage = new Stage("Introduction", 0, i, leftButtonMode, rightButtonMode, -1, true);
                Introduction = stage;
                Stage stage2 = new Stage("HelpScreen", 1, R.string.lockpattern_settings_help_how_to_record, leftButtonMode, RightButtonMode.Ok, -1, false);
                HelpScreen = stage2;
                Stage stage3 = new Stage("ChoiceTooShort", 2, R.string.lockpattern_recording_incorrect_too_short, leftButtonMode, rightButtonMode, -1, true);
                ChoiceTooShort = stage3;
                Stage stage4 = new Stage("FirstChoiceValid", 3, R.string.lockpattern_pattern_entered_header, leftButtonMode, rightButtonMode, -1, false);
                FirstChoiceValid = stage4;
                int i2 = R.string.lockpattern_need_to_confirm;
                LeftButtonMode leftButtonMode2 = LeftButtonMode.Retry;
                RightButtonMode rightButtonMode2 = RightButtonMode.ConfirmDisabled;
                Stage stage5 = new Stage("NeedToConfirm", 4, i2, leftButtonMode2, rightButtonMode2, -1, true);
                NeedToConfirm = stage5;
                Stage stage6 = new Stage("ConfirmWrong", 5, R.string.lockpattern_need_to_unlock_wrong, leftButtonMode2, rightButtonMode2, -1, true);
                ConfirmWrong = stage6;
                Stage stage7 = new Stage("ChoiceConfirmed", 6, R.string.lockpattern_pattern_confirmed_header, leftButtonMode2, RightButtonMode.Confirm, -1, false);
                ChoiceConfirmed = stage7;
                Stage stage8 = new Stage("SameWithOwnerUser", 7, R.string.lockpattern_pattern_same_with_owner, leftButtonMode, rightButtonMode, -1, true);
                SameWithOwnerUser = stage8;
                Stage stage9 = new Stage("SameWithSecuritySpaceUser", 8, R.string.lockpattern_pattern_same_with_security_space, leftButtonMode, rightButtonMode, -1, true);
                SameWithSecuritySpaceUser = stage9;
                Stage stage10 = new Stage("SameWithOtherSpaceUser", 9, R.string.lockpattern_pattern_same_with_others, leftButtonMode, rightButtonMode, -1, true);
                SameWithOtherSpaceUser = stage10;
                $VALUES = new Stage[]{stage, stage2, stage3, stage4, stage5, stage6, stage7, stage8, stage9, stage10};
            }

            private Stage(String str, int i, int i2, LeftButtonMode leftButtonMode, RightButtonMode rightButtonMode, int i3, boolean z) {
                this.headerMessage = i2;
                this.leftMode = leftButtonMode;
                this.rightMode = rightButtonMode;
                this.footerMessage = i3;
                this.patternEnabled = z;
            }

            public static Stage valueOf(String str) {
                return (Stage) Enum.valueOf(Stage.class, str);
            }

            public static Stage[] values() {
                return (Stage[]) $VALUES.clone();
            }
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
            finish();
            setFragmentResult(0, bArr);
        }

        private void postClearPatternRunnable() {
            this.mLockPatternView.removeCallbacks(this.mClearPatternRunnable);
            this.mLockPatternView.postDelayed(this.mClearPatternRunnable, 2000L);
        }

        private void setCredentialRequiredToDecrypt(LockPatternUtils lockPatternUtils, boolean z) {
            if (getActivity() == null || UserManager.get(getActivity()).getUserInfo(this.mUserIdToSetPassword).isPrimary()) {
                lockPatternUtils.setCredentialRequiredToDecrypt(z);
            }
        }

        private void setFragmentResult(int i) {
            setFragmentResult(i, null);
        }

        private void setFragmentResult(int i, byte[] bArr) {
            if (getTargetFragment() != null) {
                Bundle bundle = new Bundle();
                if (bArr != null) {
                    bundle.putByteArray("hw_auth_token", bArr);
                }
                bundle.putInt("miui_security_fragment_result", i);
                MiuiKeyguardSettingsUtils.onFragmentResult(getTargetFragment(), getTargetRequestCode(), bundle);
            }
        }

        private void showDialogToWaitUpdatePassword() {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            this.mLoadingDialog = progressDialog;
            progressDialog.setCancelable(false);
            this.mLoadingDialog.setMessage(getResources().getString(R.string.turn_update_keyguard_password_wait_dialog));
            this.mLoadingDialog.show();
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.ChooseLockPattern.ChooseLockPatternFragment.4
                @Override // java.lang.Runnable
                public void run() {
                    ChooseLockPatternFragment.this.saveChosenPatternAndFinish();
                }
            }, 5000L);
        }

        @Override // com.android.settings.SettingsPreferenceFragment
        public String getName() {
            return ChooseLockPatternFragment.class.getName();
        }

        protected List<UserInfo> getUserList(Context context) {
            return ((UserManager) context.getSystemService("user")).getUsers();
        }

        protected boolean isSetUp() {
            return false;
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i == 55) {
                if (i2 != -1) {
                    getActivity().setResult(0);
                    getActivity().finish();
                }
                updateStage(Stage.Introduction);
            } else if (i == 56) {
                if (i2 == -1) {
                    getActivity().setResult(-1);
                    getActivity().finish();
                }
            } else if (i != 107) {
            } else {
                if (i2 != -1) {
                    getActivity().setResult(0);
                    getActivity().finish();
                } else if (intent != null) {
                    this.mUserPassword = intent.getStringExtra("password");
                }
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view == this.mResetButton) {
                LeftButtonMode leftButtonMode = this.mUiStage.leftMode;
                if (leftButtonMode == LeftButtonMode.Retry) {
                    this.mChosenPattern = null;
                    this.mLockPatternView.clearPattern();
                    updateStage(Stage.Introduction);
                } else if (leftButtonMode == LeftButtonMode.Cancel) {
                    getActivity().setResult(0);
                    getActivity().finish();
                } else {
                    throw new IllegalStateException("left footer button pressed, but stage of " + this.mUiStage + " doesn't make sense");
                }
            } else if (view == this.mNextButton || view == this.mNextImage) {
                Stage stage = this.mUiStage;
                RightButtonMode rightButtonMode = stage.rightMode;
                RightButtonMode rightButtonMode2 = RightButtonMode.Continue;
                if (rightButtonMode == rightButtonMode2) {
                    Stage stage2 = Stage.FirstChoiceValid;
                    if (stage == stage2) {
                        updateStage(Stage.NeedToConfirm);
                        return;
                    }
                    throw new IllegalStateException("expected ui stage " + stage2 + " when button is " + rightButtonMode2);
                }
                RightButtonMode rightButtonMode3 = RightButtonMode.Confirm;
                if (rightButtonMode != rightButtonMode3) {
                    if (rightButtonMode == RightButtonMode.Ok) {
                        if (stage == Stage.HelpScreen) {
                            this.mLockPatternView.clearPattern();
                            this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
                            updateStage(Stage.Introduction);
                            return;
                        }
                        throw new IllegalStateException("Help screen is only mode with ok button, but stage is " + this.mUiStage);
                    }
                    return;
                }
                Stage stage3 = Stage.ChoiceConfirmed;
                if (stage == stage3) {
                    if (MiuiKeyguardSettingsUtils.showWaitTurnOffPassword(getActivity().getApplicationContext())) {
                        showDialogToWaitUpdatePassword();
                        return;
                    } else {
                        saveChosenPatternAndFinish();
                        return;
                    }
                }
                throw new IllegalStateException("expected ui stage " + stage3 + " when button is " + rightButtonMode3);
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mEnableKeyguardPassword = getActivity().getIntent().getBooleanExtra("set_keyguard_password", true);
            this.mSecuritySpaceId = Settings.Secure.getIntForUser(getContentResolver(), "second_user_id", -10000, 0);
            this.mAddKeyguardpasswordThenAddFingerprint = getActivity().getIntent().getBooleanExtra("add_keyguard_password_then_add_fingerprint", false);
            this.mAddKeyguardpasswordThenAddFaceRecoginition = getActivity().getIntent().getBooleanExtra("add_keyguard_password_then_add_face_recoginition", false);
            this.mUserPassword = getActivity().getIntent().getStringExtra("password");
            int intExtra = getActivity().getIntent().getIntExtra("user_id_to_set_password", -10000);
            this.mUserIdToSetPassword = intExtra;
            if (intExtra == -10000) {
                intExtra = MiuiKeyguardSettingsUtils.getUserId(getActivity(), getActivity().getIntent().getExtras());
            }
            this.mUserIdToSetPassword = intExtra;
            int i = this.mSecuritySpaceId;
            if (i != -10000 && intExtra == i) {
                this.mIsSetPasswordForSSUser = true;
            }
            if (intExtra == 0) {
                this.mIsSetPasswordForOwnerUser = true;
            }
            if (!this.mIsSetPasswordForSSUser && !this.mIsSetPasswordForOwnerUser) {
                this.mIsSetPasswordForAirSpace = CrossUserUtils.isAirSpace(getActivity(), this.mUserIdToSetPassword);
            }
            this.mChooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity());
            if (MiuiKeyguardSettingsUtils.instanceofSettingsPreFragment(getTargetFragment())) {
                new MiuiChooseLockSettingsHelper(getActivity()).launchConfirmWhenNecessary(this, 107, this.mUserIdToSetPassword);
            }
            getActivity().getWindow().addFlags(8192);
        }

        protected void onCreateNoSavedState() {
            updateStage(Stage.Introduction);
        }

        @Override // com.android.settings.KeyguardSettingsPreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            preSetupViews();
            View inflate = isSetUp() ? isDeviceProvisioned(getActivity()) ? layoutInflater.inflate(R.layout.setup_choose_lock_pattern_view, viewGroup, false) : layoutInflater.inflate(R.layout.provision_setup_choose_lock_pattern_view, viewGroup, false) : layoutInflater.inflate(R.layout.choose_lock_pattern, (ViewGroup) null);
            this.mHeaderText = (TextView) inflate.findViewById(R.id.headerText);
            setupViews(inflate);
            this.mLockPatternView.setOnPatternListener(this.mChooseNewLockPatternListener);
            this.mLockPatternView.setTactileFeedbackEnabled(this.mChooseLockSettingsHelper.utils().isTactileFeedbackEnabled());
            this.mResetButton.setOnClickListener(this);
            this.mNextButton.setOnClickListener(this);
            if (bundle == null) {
                if (getTargetFragment() == null) {
                    new MiuiChooseLockSettingsHelper(getActivity()).launchConfirmWhenNecessary(this, 107, this.mUserIdToSetPassword);
                }
                onCreateNoSavedState();
            } else {
                String string = bundle.getString("chosenPattern");
                if (string != null) {
                    this.mChosenPattern = LockPatternUtilsCompat.stringToPattern(this.mChooseLockSettingsHelper.utils(), string);
                }
                updateStage(Stage.values()[bundle.getInt("uiStage")]);
                if (TextUtils.isEmpty(this.mUserPassword)) {
                    this.mUserPassword = bundle.getString("userPassword");
                }
            }
            if (this.mIsSetPasswordForSSUser) {
                Stage stage = Stage.Introduction;
                stage.headerMessage = R.string.lockpattern_recording_intro_header_second_space;
                TextView textView = this.mHeaderText;
                Resources resources = getResources();
                int i = R.color.security_lock_pattern_head_text;
                textView.setTextColor(resources.getColor(i));
                this.mSubHeaderText.setTextColor(getResources().getColor(i));
                this.mSubHeaderText.setText(stage.headerMessage);
                this.mSubHeaderText.setTextSize(13.0f);
                this.mSubHeaderText.setMinLines(2);
                inflate.setBackgroundColor(getResources().getColor(R.color.set_second_space_background));
                ActionBar appCompatActionBar = getActivity() instanceof AppCompatActivity ? ((AppCompatActivity) getActivity()).getAppCompatActionBar() : null;
                if (appCompatActionBar != null) {
                    appCompatActionBar.setTitle(R.string.lockpassword_choose_for_second_user);
                    appCompatActionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_second_space));
                }
                this.mLockPatternView.setBitmapBtnTouched(R.drawable.lock_pattern_code_lock_white);
            }
            return inflate;
        }

        @Override // com.android.settings.SettingsPreferenceFragment
        public void onFragmentResult(int i, Bundle bundle) {
            boolean z = bundle != null && bundle.getInt("miui_security_fragment_result", -1) == 0;
            if (i == 55) {
                if (z) {
                    updateStage(Stage.Introduction);
                    return;
                }
                finish();
                setFragmentResult(0);
            } else if (i == 56) {
                if (z) {
                    finish();
                    setFragmentResult(0);
                }
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
            super.onPause();
            AsyncTask<?, ?, ?> asyncTask = this.mPendingLockCheck;
            if (asyncTask != null) {
                asyncTask.cancel(false);
                this.mPendingLockCheck = null;
            }
            if (this.mChosenPattern != null) {
                this.mChosenPattern = null;
            }
            LockPatternView lockPatternView = this.mLockPatternView;
            if (lockPatternView != null) {
                lockPatternView.clearPattern();
            }
            updateStage(Stage.Introduction);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putInt("uiStage", this.mUiStage.ordinal());
            if (this.mChosenPattern != null) {
                bundle.putString("chosenPattern", LockPatternUtilsCompat.patternToString(this.mChooseLockSettingsHelper.utils(), this.mChosenPattern));
            }
            if (TextUtils.isEmpty(this.mUserPassword)) {
                return;
            }
            bundle.putString("userPassword", this.mUserPassword);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onStart() {
            super.onStart();
            MiuiUtils.onStartEdit(this);
            if (isSetUp() || !(getActivity() instanceof AppCompatActivity)) {
                return;
            }
            ActionBar appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar();
            if (appCompatActionBar instanceof ActionBar) {
                appCompatActionBar.setExpandState(0);
                appCompatActionBar.setResizable(false);
            }
        }

        @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onStop() {
            MiuiUtils.onFinishEdit(this);
            super.onStop();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void preSetupViews() {
            Stage.Introduction.headerMessage = R.string.lockpattern_recording_intro_header;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void returnToKeyguardPasswordSettings(byte[] bArr) {
            if (getActivity() != null) {
                if (MiuiKeyguardSettingsUtils.getBooolExtra(getArguments(), getActivity().getIntent(), "has_challenge")) {
                    onPasswordChecked(bArr);
                } else {
                    onPasswordChecked(null);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void saveChosenPatternAndFinish() {
            MiuiLockPatternUtils utils = this.mChooseLockSettingsHelper.utils();
            boolean z = !utils.isPatternEverChosen(this.mUserIdToSetPassword);
            boolean z2 = Settings.System.getInt(getContentResolver(), "is_security_encryption_enabled", 0) == 1;
            setCredentialRequiredToDecrypt(utils, false);
            if (z2 || getActivity().getIntent().getBooleanExtra("use_lock_password_to_encrypt_device", false)) {
                setCredentialRequiredToDecrypt(utils, true);
            }
            this.mResetButton.setEnabled(false);
            setNextEnable(false);
            boolean booleanExtra = getActivity().getIntent().getBooleanExtra("lockscreen.biometric_weak_fallback", false);
            if (z) {
                utils.setVisiblePatternEnabled(true, this.mUserIdToSetPassword);
            }
            FingerprintHelper fingerprintHelper = new FingerprintHelper(getActivity());
            this.mFingerprintHelper = fingerprintHelper;
            new AnonymousClass5(booleanExtra, fingerprintHelper.isHardwareDetected() && this.mAddKeyguardpasswordThenAddFingerprint && this.mFingerprintHelper.getFingerprintIds().size() == 0, KeyguardSettingsFaceUnlockUtils.isSupportFaceUnlock(getActivity()) && this.mAddKeyguardpasswordThenAddFaceRecoginition && KeyguardSettingsFaceUnlockUtils.getEnrolledFacesNumber(getActivity()) == 0, MiuiKeyguardSettingsUtils.getBooolExtra(getArguments(), getActivity().getIntent(), "has_challenge"), utils).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }

        protected void setNextEnable(boolean z) {
            this.mNextButton.setEnabled(z);
        }

        protected void setupViews(View view) {
            this.mSubHeaderText = (TextView) view.findViewById(R.id.subHeaderText);
            this.mLockPatternView = (LockPatternView) view.findViewById(R.id.lockPattern);
            this.mResetButton = (TextView) view.findViewById(R.id.footerLeftButton);
            this.mNextButton = (TextView) view.findViewById(R.id.footerRightButton);
            view.findViewById(R.id.topLayout).setDefaultTouchRecepient(this.mLockPatternView);
        }

        public void showDialogToAddFingerprint(final byte[] bArr) {
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.ChooseLockPattern.ChooseLockPatternFragment.6
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i != -1) {
                        dialogInterface.dismiss();
                        ChooseLockPatternFragment.this.returnToKeyguardPasswordSettings(bArr);
                        return;
                    }
                    Intent intent = new Intent(ChooseLockPatternFragment.this.getActivity(), NewFingerprintInternalActivity.class);
                    intent.putExtra("add_keyguard_password_then_add_fingerprint", true);
                    intent.putExtra("hw_auth_token", bArr);
                    ChooseLockPatternFragment.this.startActivity(intent);
                    ChooseLockPatternFragment.this.getActivity().setResult(-1);
                    ChooseLockPatternFragment.this.getActivity().finish();
                }
            };
            new AlertDialog.Builder(getActivity()).setCancelable(true).setTitle(R.string.new_password_to_new_fingerprint_dialog_tittle).setMessage(R.string.new_password_to_new_fingerprint_dialog_msg).setPositiveButton(R.string.new_password_to_new_fingerprint_dialog_positive_msg, onClickListener).setNegativeButton(R.string.new_password_to_new_fingerprint_dialog_negative_msg, onClickListener).setCancelable(false).create().show();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void updateStage(Stage stage) {
            this.mUiStage = stage;
            if (stage == Stage.ChoiceTooShort) {
                this.mSubHeaderText.setText(getResources().getString(stage.headerMessage, 4));
            } else if (stage == Stage.SameWithOwnerUser) {
                this.mSubHeaderText.setText(getString(R.string.lockpattern_pattern_same_with_owner));
            } else if (stage == Stage.SameWithSecuritySpaceUser) {
                this.mSubHeaderText.setText(getString(R.string.lockpattern_pattern_same_with_security_space));
            } else if (stage == Stage.SameWithOtherSpaceUser) {
                this.mSubHeaderText.setText(getString(R.string.lockpattern_pattern_same_with_others));
            } else {
                this.mSubHeaderText.setText(stage.headerMessage);
            }
            if (stage.leftMode != LeftButtonMode.Gone) {
                this.mResetButton.setVisibility(0);
                this.mResetButton.setText(stage.leftMode.text);
                this.mResetButton.setEnabled(stage.leftMode.enabled);
            } else if (isSetUp()) {
                this.mResetButton.setEnabled(false);
            } else {
                this.mResetButton.setVisibility(4);
            }
            if (stage.rightMode != RightButtonMode.Gone) {
                if (!isSetUp()) {
                    this.mNextButton.setVisibility(0);
                }
                this.mNextButton.setText(stage.rightMode.text);
                setNextEnable(stage.rightMode.enabled);
            } else if (isSetUp()) {
                setNextEnable(false);
            } else {
                this.mNextButton.setVisibility(4);
            }
            if (stage.patternEnabled) {
                this.mLockPatternView.enableInput();
            } else {
                this.mLockPatternView.disableInput();
            }
            this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
            switch (AnonymousClass1.$SwitchMap$com$android$settings$ChooseLockPattern$ChooseLockPatternFragment$Stage[this.mUiStage.ordinal()]) {
                case 1:
                    this.mLockPatternView.clearPattern();
                    return;
                case 2:
                    this.mLockPatternView.setPattern(LockPatternView.DisplayMode.Animate, this.mAnimatePattern);
                    return;
                case 3:
                case 4:
                case 5:
                case 6:
                    this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    postClearPatternRunnable();
                    return;
                case 7:
                    this.mLockPatternView.postDelayed(new Runnable() { // from class: com.android.settings.ChooseLockPattern.ChooseLockPatternFragment.3
                        @Override // java.lang.Runnable
                        public void run() {
                            ChooseLockPatternFragment.this.updateStage(Stage.NeedToConfirm);
                        }
                    }, 500L);
                    return;
                case 8:
                    this.mLockPatternView.clearPattern();
                    return;
                case 9:
                    this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    postClearPatternRunnable();
                    return;
                default:
                    return;
            }
        }
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", ChooseLockPatternFragment.class.getName());
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        ChooseLockPatternFragment.class.getName().equals(str);
        return true;
    }

    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(getText(R.string.empty_title));
    }
}
