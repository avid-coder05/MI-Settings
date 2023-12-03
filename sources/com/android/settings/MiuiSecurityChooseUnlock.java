package com.android.settings;

import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.PreferenceFrameLayout;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.security.KeyStore;
import android.security.MiuiLockPatternUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.KeyguardSettingsPreferenceFragment;
import com.android.settings.compat.LockPatternUtilsCompat;
import com.android.settings.faceunlock.KeyguardSettingsFaceUnlockManager;
import com.android.settings.faceunlock.KeyguardSettingsFaceUnlockUtils;
import com.android.settings.search.SearchUpdater;
import com.android.settings.utils.TabletUtils;
import com.android.settingslib.util.ToastUtil;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.app.AlertDialog;
import miuix.slidingwidget.widget.SlidingButton;
import miuix.springback.view.SpringBackLayout;

/* loaded from: classes.dex */
public class MiuiSecurityChooseUnlock extends Settings {
    public static final String TAG = "MiuiSecurityChooseUnlock";

    /* loaded from: classes.dex */
    public static class InternalActivity extends MiuiSecurityChooseUnlock {
        public static String getExtraFragmentName() {
            return MiuiSecurityChooseUnlockFragment.class.getName();
        }
    }

    /* loaded from: classes.dex */
    public static class MiuiSecurityChooseUnlockFragment extends KeyguardSettingsPreferenceFragment implements OnBackPressedListener {
        private MiuiChooseLockSettingsHelper mChooseLockSettingsHelper;
        private AlertDialog mConformDialog;
        private CountDownTimer mCountdownTimer;
        private DevicePolicyManager mDpm;
        private TextView mHintText;
        private boolean mIsReOnCreate;
        private KeyStore mKeyStore;
        private ProgressDialog mLoadingDialog;
        private LockPatternUtils mLockPatternUtils;
        private int mRequestedMinComplexity;
        private int mSecuritySpaceUserId;
        private TextView mTurnOffPassword;
        private Preference mUnlockSetPassword;
        private Preference mUnlockSetPattern;
        private Preference mUnlockSetPin;
        private String mUnlockSetPinSummary;
        private SlidingButton mVisiblePatternBtn;
        private FrameLayout mVisiblePatternLayout;
        private TextView mVisiblePatternTitle;
        private boolean mEnableKeyguardPassword = true;
        private String mTheBusinessKey = null;
        private String mUserPassword = null;
        private AlertDialog mForgetPasswordAlertDialog = null;
        private boolean mPasswordConfirmed = false;
        private boolean mSetPasswordStarted = false;
        private int mUserId = UserHandle.myUserId();
        protected boolean mAddKeyguardpasswordThenAddFingerprint = false;
        private FingerprintRemoveCallback callbak = new FingerprintRemoveCallback() { // from class: com.android.settings.MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment.8
            @Override // com.android.settings.FingerprintRemoveCallback
            public void onFailed() {
                Log.e("removeFingerprint", "remove finger failed");
            }

            @Override // com.android.settings.FingerprintRemoveCallback
            public void onRemoved() {
                Log.w("removeFingerprint", "remove finger succeed");
                MiuiSecurityChooseUnlockFragment.this.finish();
            }
        };

        private void disableUnusablePreferences() {
            Intent intent = getActivity().getIntent();
            int intExtra = intent.getIntExtra("lockscreen.password_type", -1);
            if (intExtra == -1) {
                intExtra = intent.getIntExtra("minimum_quality", -1);
            }
            disableUnusablePreferences(this.mChooseLockSettingsHelper.upgradeQuality(intExtra, this, this.mUserId, this.mRequestedMinComplexity), intent.getBooleanExtra("hide_disabled_prefs", false));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void goSetPassword(String str) {
            if ("unlock_set_pattern".equals(str)) {
                if (getTargetFragment() == null) {
                    this.mChooseLockSettingsHelper.startActivityToSetPassword(SearchUpdater.GOOGLE, this, this.mPasswordConfirmed, this.mUserPassword, this.mUserId, isSetUp());
                } else {
                    this.mChooseLockSettingsHelper.startFragmentToSetPattern(this, 202, this.mPasswordConfirmed, this.mUserPassword, this.mUserId, isSetUp());
                }
            } else if ("unlock_set_pin".equals(str)) {
                if (getTargetFragment() == null) {
                    this.mChooseLockSettingsHelper.startActivityToSetPassword(131072, this, this.mPasswordConfirmed, this.mUserPassword, this.mUserId, isSetUp());
                } else {
                    this.mChooseLockSettingsHelper.startFragmentToSetNumericPassword(this, 202, this.mPasswordConfirmed, this.mUserPassword, this.mUserId, isSetUp());
                }
            } else if ("unlock_set_password".equals(str)) {
                if (getTargetFragment() == null) {
                    this.mChooseLockSettingsHelper.startActivityToSetPassword(262144, this, this.mPasswordConfirmed, this.mUserPassword, this.mUserId, isSetUp());
                } else {
                    this.mChooseLockSettingsHelper.startFragmentToSetMixedPassword(this, 202, this.mPasswordConfirmed, this.mUserPassword, this.mUserId, isSetUp());
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleRemoveAllFingerprints() {
            FingerprintHelper fingerprintHelper = new FingerprintHelper(getActivity());
            List<String> fingerprintIds = fingerprintHelper.getFingerprintIds();
            boolean isHardwareDetected = fingerprintHelper.isHardwareDetected();
            if (fingerprintIds == null || fingerprintIds.size() == 0) {
                finish();
            } else if (!isHardwareDetected || fingerprintIds.size() <= 0) {
            } else {
                fingerprintHelper.removeAllFingerprint(this.callbak);
            }
        }

        private boolean isDeviceProvisioned(Context context) {
            return Settings.Secure.getInt(context.getContentResolver(), "device_provisioned", 0) == 1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onSlidingButtonCheckedChanged(boolean z) {
            this.mVisiblePatternBtn.setChecked(z);
            this.mChooseLockSettingsHelper.utils().setVisiblePatternEnabled(z, UserHandle.myUserId());
        }

        private void showConfirmDialog(DialogInterface.OnClickListener onClickListener, String str) {
            AlertDialog create = new AlertDialog.Builder(getActivity()).setCancelable(false).setIconAttribute(16843605).setTitle(R.string.turn_off_keyguard_password_alert_title).setMessage(str).setPositiveButton(17039370, onClickListener).setNegativeButton(17039360, onClickListener).create();
            this.mConformDialog = create;
            create.show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void showDialogToWaitTurnOffPassword() {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            this.mLoadingDialog = progressDialog;
            progressDialog.setCancelable(false);
            this.mLoadingDialog.setMessage(getResources().getString(R.string.turn_off_keyguard_password_wait_dialog));
            this.mLoadingDialog.show();
            new Handler().postDelayed(new Runnable() { // from class: com.android.settings.MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment.9
                @Override // java.lang.Runnable
                public void run() {
                    MiuiSecurityChooseUnlockFragment.this.mLoadingDialog.dismiss();
                    MiuiSecurityChooseUnlockFragment.this.mLoadingDialog = null;
                    MiuiSecurityChooseUnlockFragment.this.turnoffPassword();
                    MiuiKeyguardSettingsUtils.saveUpdatepatternTime(MiuiSecurityChooseUnlockFragment.this.getActivity().getApplicationContext());
                    MiuiSecurityChooseUnlockFragment.this.handleRemoveAllFingerprints();
                }
            }, 5000L);
        }

        private AlertDialog showForgetPasswordAlertDialog(final String str) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog_Theme_DayNight);
            builder.setCancelable(false);
            builder.setIconAttribute(16843605);
            builder.setTitle(R.string.turn_on_keyguard_password_alert_forget_title);
            builder.setMessage(R.string.turn_on_keyguard_password_alert_forget_content);
            builder.setPositiveButton(R.string.turn_on_keyguard_password_alert_foget_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    MiuiSecurityChooseUnlockFragment.this.goSetPassword(str);
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton(R.string.turn_on_keyguard_password_alert_foget_cacel, new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment.5
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog create = builder.create();
            create.show();
            final Button button = create.getButton(-1);
            button.setClickable(false);
            this.mCountdownTimer = new CountDownTimer(5000L, 1000L) { // from class: com.android.settings.MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment.6
                @Override // android.os.CountDownTimer
                public void onFinish() {
                    if (MiuiSecurityChooseUnlockFragment.this.getActivity() != null) {
                        button.setEnabled(true);
                        button.setClickable(true);
                        button.setText(MiuiSecurityChooseUnlockFragment.this.getResources().getString(R.string.turn_on_keyguard_password_alert_foget_ok));
                    }
                }

                @Override // android.os.CountDownTimer
                public void onTick(long j) {
                    if (MiuiSecurityChooseUnlockFragment.this.getActivity() != null) {
                        button.setEnabled(false);
                        button.setText(MiuiSecurityChooseUnlockFragment.this.getResources().getString(R.string.turn_on_keyguard_password_alert_foget_ok_time, Long.valueOf(j / 1000)));
                    }
                }
            }.start();
            return create;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void showTurnoffPasswordConfirmDialogAndFinish() {
            FingerprintHelper fingerprintHelper = new FingerprintHelper(getActivity());
            fingerprintHelper.getFingerprintIds();
            boolean isHardwareDetected = fingerprintHelper.isHardwareDetected();
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment.7
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == -1) {
                        if (MiuiKeyguardSettingsUtils.showWaitTurnOffPassword(MiuiSecurityChooseUnlockFragment.this.getActivity().getApplicationContext())) {
                            MiuiSecurityChooseUnlockFragment.this.showDialogToWaitTurnOffPassword();
                            return;
                        }
                        MiuiSecurityChooseUnlockFragment.this.turnoffPassword();
                        MiuiKeyguardSettingsUtils.saveUpdatepatternTime(MiuiSecurityChooseUnlockFragment.this.getActivity().getApplicationContext());
                        MiuiSecurityChooseUnlockFragment.this.handleRemoveAllFingerprints();
                    }
                }
            };
            int i = R.string.turn_off_keyguard_password_confirm_msg;
            if (KeyguardSettingsFaceUnlockUtils.isSupportFaceUnlock(getActivity().getApplicationContext()) && isHardwareDetected) {
                i = R.string.turn_off_keyguard_password_with_fingerprint_face_confirm_msg;
            } else if (isHardwareDetected) {
                i = R.string.turn_off_keyguard_password_with_fingerprint_confirm_msg;
            } else if (KeyguardSettingsFaceUnlockUtils.isSupportFaceUnlock(getActivity().getApplicationContext())) {
                i = R.string.turn_off_keyguard_password_with_face_confirm_msg;
            }
            showConfirmDialog(onClickListener, getString(i));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void turnoffPassword() {
            KeyguardSettingsFaceUnlockManager.getInstance(getActivity()).deleteFeature("0", null);
            LockPatternUtilsCompat.clearLock(this.mChooseLockSettingsHelper.utils(), UserHandle.myUserId(), false, this.mUserPassword);
            this.mChooseLockSettingsHelper.utils().setLockScreenDisabled(false, UserHandle.myUserId());
            LockPatternUtilsCompat.setSeparateProfileChallengeEnabled(this.mChooseLockSettingsHelper.utils(), this.mUserId, false, this.mUserPassword);
            MiuiSettings.System.putBooleanForUser(getActivity().getContentResolver(), "new_numeric_password_type", false, UserHandle.myUserId());
            if (new MiuiLockPatternUtils(getActivity()).getBluetoothUnlockEnabled()) {
                getActivity().sendBroadcast(new Intent("com.miui.keyguard.bluetoothdeviceunlock.disable"));
            }
            Settings.Secure.getInt(getActivity().getContentResolver(), "vpn_password_enable", 0);
        }

        @Override // com.android.settings.KeyguardSettingsPreferenceFragment
        protected void disableSpringBack() {
            View view = (View) getListView().getParent();
            if (view instanceof SpringBackLayout) {
                view.setEnabled(false);
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:12:0x0040, code lost:
        
            if (r0 > 65536) goto L13;
         */
        /* JADX WARN: Code restructure failed: missing block: B:13:0x0042, code lost:
        
            r6 = true;
         */
        /* JADX WARN: Code restructure failed: missing block: B:32:0x0078, code lost:
        
            if (r0 > 393216) goto L13;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected void disableUnusablePreferences(int r12, boolean r13) {
            /*
                r11 = this;
                android.app.admin.DevicePolicyManager r0 = r11.mDpm
                int r1 = r11.mUserId
                r2 = 0
                int r0 = r0.getPasswordQuality(r2, r1)
                androidx.fragment.app.FragmentActivity r1 = r11.getActivity()
                int r3 = r11.mUserId
                com.android.settingslib.RestrictedLockUtils$EnforcedAdmin r1 = com.android.settings.compat.RestrictedLockUtilsCompat.checkIfPasswordQualityIsSet(r1, r3)
                androidx.preference.PreferenceScreen r3 = r11.getPreferenceScreen()
                int r3 = r3.getPreferenceCount()
                r4 = 1
                int r3 = r3 - r4
            L1d:
                if (r3 < 0) goto Lb0
                androidx.preference.PreferenceScreen r5 = r11.getPreferenceScreen()
                androidx.preference.Preference r5 = r5.getPreference(r3)
                boolean r6 = r5 instanceof com.android.settings.KeyguardRestrictedPreference
                if (r6 == 0) goto Lac
                java.lang.String r6 = r5.getKey()
                java.lang.String r7 = "unlock_set_pattern"
                boolean r7 = r7.equals(r6)
                r8 = 0
                if (r7 == 0) goto L44
                r6 = 65536(0x10000, float:9.1835E-41)
                if (r12 > r6) goto L3f
                r7 = r4
                goto L40
            L3f:
                r7 = r8
            L40:
                if (r0 <= r6) goto L7c
            L42:
                r6 = r4
                goto L7d
            L44:
                java.lang.String r7 = "unlock_set_pin"
                boolean r7 = r7.equals(r6)
                if (r7 == 0) goto L68
                android.app.admin.DevicePolicyManager r6 = r11.mDpm
                int r7 = r11.mUserId
                int r6 = r6.getPasswordMinimumLength(r2, r7)
                r7 = 196608(0x30000, float:2.75506E-40)
                if (r12 > r7) goto L5e
                r9 = 4
                if (r6 > r9) goto L5e
                r6 = r4
                goto L5f
            L5e:
                r6 = r8
            L5f:
                if (r0 <= r7) goto L63
                r7 = r4
                goto L64
            L63:
                r7 = r8
            L64:
                r10 = r7
                r7 = r6
                r6 = r10
                goto L7d
            L68:
                java.lang.String r7 = "unlock_set_password"
                boolean r6 = r7.equals(r6)
                if (r6 == 0) goto L7b
                r6 = 393216(0x60000, float:5.51013E-40)
                if (r12 > r6) goto L77
                r7 = r4
                goto L78
            L77:
                r7 = r8
            L78:
                if (r0 <= r6) goto L7c
                goto L42
            L7b:
                r7 = r4
            L7c:
                r6 = r8
            L7d:
                if (r13 == 0) goto L81
                r9 = r7
                goto L82
            L81:
                r9 = r4
            L82:
                if (r9 != 0) goto L8c
                androidx.preference.PreferenceScreen r6 = r11.getPreferenceScreen()
                r6.removePreference(r5)
                goto Lac
            L8c:
                if (r6 == 0) goto L96
                if (r1 == 0) goto L96
                com.android.settings.KeyguardRestrictedPreference r5 = (com.android.settings.KeyguardRestrictedPreference) r5
                r5.setDisabledByAdmin(r1)
                goto Lac
            L96:
                if (r7 != 0) goto La7
                r6 = r5
                com.android.settings.KeyguardRestrictedPreference r6 = (com.android.settings.KeyguardRestrictedPreference) r6
                r6.setDisabledByAdmin(r2)
                int r6 = com.android.settings.R.string.unlock_set_unlock_disabled_summary
                r5.setSummary(r6)
                r5.setEnabled(r8)
                goto Lac
            La7:
                com.android.settings.KeyguardRestrictedPreference r5 = (com.android.settings.KeyguardRestrictedPreference) r5
                r5.setDisabledByAdmin(r2)
            Lac:
                int r3 = r3 + (-1)
                goto L1d
            Lb0:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment.disableUnusablePreferences(int, boolean):void");
        }

        @Override // com.android.settings.SettingsPreferenceFragment
        public String getName() {
            return MiuiSecurityChooseUnlockFragment.class.getName();
        }

        @Override // com.android.settings.KeyguardSettingsPreferenceFragment
        protected View inflateCustomizeView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            View inflate = layoutInflater.inflate(R.layout.choose_lock, viewGroup, false);
            View findViewById = inflate.findViewById(R.id.lock_pin);
            View findViewById2 = inflate.findViewById(R.id.lock_none);
            findViewById.setVisibility(8);
            findViewById2.setVisibility(8);
            if (viewGroup != null) {
                PreferenceFrameLayout.LayoutParams layoutParams = ((ViewGroup) viewGroup.getParent()).getLayoutParams();
                if (layoutParams instanceof PreferenceFrameLayout.LayoutParams) {
                    layoutParams.removeBorders = true;
                }
            }
            this.mHintText = (TextView) inflate.findViewById(R.id.hint_text);
            TextView textView = (TextView) inflate.findViewById(R.id.turn_off_password);
            this.mTurnOffPassword = textView;
            Folme.useAt(textView).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).clearTintColor().setBackgroundColor(0.08f, 0.0f, 0.0f, 0.0f).handleTouchOf(this.mTurnOffPassword, new AnimConfig[0]);
            this.mTurnOffPassword.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    MiuiSecurityChooseUnlockFragment.this.showTurnoffPasswordConfirmDialogAndFinish();
                }
            });
            this.mVisiblePatternLayout = (FrameLayout) inflate.findViewById(R.id.visible_pattern_layout);
            TextView textView2 = (TextView) inflate.findViewById(R.id.visible_pattern_title);
            this.mVisiblePatternTitle = textView2;
            Folme.useAt(textView2).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).clearTintColor().setBackgroundColor(0.08f, 0.0f, 0.0f, 0.0f).handleTouchOf(this.mVisiblePatternTitle, new AnimConfig[0]);
            SlidingButton slidingButton = (SlidingButton) inflate.findViewById(R.id.visible_pattern_button);
            this.mVisiblePatternBtn = slidingButton;
            slidingButton.setChecked(this.mChooseLockSettingsHelper.utils().isVisiblePatternEnabled(UserHandle.myUserId()));
            this.mVisiblePatternTitle.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    MiuiSecurityChooseUnlockFragment.this.onSlidingButtonCheckedChanged(!MiuiSecurityChooseUnlockFragment.this.mVisiblePatternBtn.isChecked());
                }
            });
            this.mVisiblePatternBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment.3
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    MiuiSecurityChooseUnlockFragment.this.onSlidingButtonCheckedChanged(z);
                }
            });
            return inflate;
        }

        protected boolean isInternalActivity() {
            return getActivity() instanceof InternalActivity;
        }

        protected boolean isSetUp() {
            return false;
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i == 201) {
                FragmentActivity activity = getActivity();
                if (!isInternalActivity()) {
                    intent = null;
                }
                activity.setResult(i2, intent);
                this.mSetPasswordStarted = false;
                finish();
            } else if (i != 202) {
                if (i == 107) {
                    if (i2 != -1) {
                        finish();
                        return;
                    }
                    this.mPasswordConfirmed = true;
                    if (intent != null) {
                        this.mUserPassword = intent.getStringExtra("password");
                    }
                }
            } else {
                if (i2 == -1) {
                    if (!TextUtils.isEmpty(this.mTheBusinessKey)) {
                        MiuiSettings.Secure.putBoolean(getContentResolver(), this.mTheBusinessKey, true);
                    }
                    FragmentActivity activity2 = getActivity();
                    if (!isInternalActivity()) {
                        intent = null;
                    }
                    activity2.setResult(i2, intent);
                    finish();
                } else if (this.mSecuritySpaceUserId != -10000 && UserHandle.myUserId() == this.mSecuritySpaceUserId) {
                    finish();
                }
                this.mSetPasswordStarted = false;
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

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            int keyguardStoredPasswordQuality;
            super.onCreate(bundle);
            this.mLockPatternUtils = new LockPatternUtils(getActivity());
            this.mIsReOnCreate = bundle != null;
            this.mKeyStore = KeyStore.getInstance();
            Bundle arguments = getArguments();
            this.mAddKeyguardpasswordThenAddFingerprint = MiuiKeyguardSettingsUtils.getBooolExtra(arguments, getActivity().getIntent(), "add_keyguard_password_then_add_fingerprint");
            getActivity().getIntent().putExtra("add_keyguard_password_then_add_fingerprint", this.mAddKeyguardpasswordThenAddFingerprint);
            this.mDpm = (DevicePolicyManager) getSystemService("device_policy");
            this.mChooseLockSettingsHelper = new MiuiChooseLockSettingsHelper(getActivity());
            this.mSecuritySpaceUserId = Settings.Secure.getIntForUser(getContentResolver(), "second_user_id", -10000, 0);
            this.mUserId = MiuiKeyguardSettingsUtils.getUserId(arguments, this.mChooseLockSettingsHelper.utils(), getActivity());
            int intExtra = getActivity().getIntent().getIntExtra("user_id_to_set_password", -10000);
            if (intExtra != -10000) {
                this.mUserId = intExtra;
            }
            if (bundle != null) {
                this.mPasswordConfirmed = bundle.getBoolean("password_confirmed");
                this.mSetPasswordStarted = bundle.getBoolean("set_password_started");
            }
            if (this.mSecuritySpaceUserId != -10000 && UserHandle.myUserId() == this.mSecuritySpaceUserId && this.mChooseLockSettingsHelper.utils().isSecure(0)) {
                keyguardStoredPasswordQuality = this.mChooseLockSettingsHelper.utils().getActivePasswordQuality(0);
                if (this.mSetPasswordStarted) {
                    Log.e(MiuiSecurityChooseUnlock.TAG, "skip set password for security space");
                    finish();
                } else {
                    if (keyguardStoredPasswordQuality == 65536) {
                        if (getTargetFragment() == null) {
                            this.mChooseLockSettingsHelper.startActivityToSetPassword(SearchUpdater.GOOGLE, this, this.mPasswordConfirmed, this.mUserPassword, this.mUserId);
                        } else {
                            this.mChooseLockSettingsHelper.startFragmentToSetPattern(this, 202, this.mPasswordConfirmed, this.mUserPassword, this.mUserId);
                        }
                    } else if (keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608) {
                        if (getTargetFragment() == null) {
                            this.mChooseLockSettingsHelper.startActivityToSetPassword(131072, this, this.mPasswordConfirmed, this.mUserPassword, this.mUserId);
                        } else {
                            this.mChooseLockSettingsHelper.startFragmentToSetNumericPassword(this, 202, this.mPasswordConfirmed, this.mUserPassword, this.mUserId);
                        }
                    } else if (keyguardStoredPasswordQuality == 262144 || keyguardStoredPasswordQuality == 327680 || keyguardStoredPasswordQuality == 393216) {
                        if (getTargetFragment() == null) {
                            this.mChooseLockSettingsHelper.startActivityToSetPassword(262144, this, this.mPasswordConfirmed, this.mUserPassword, this.mUserId);
                        } else {
                            this.mChooseLockSettingsHelper.startFragmentToSetMixedPassword(this, 202, this.mPasswordConfirmed, this.mUserPassword, this.mUserId);
                        }
                    }
                    this.mSetPasswordStarted = true;
                }
            } else {
                keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mUserId);
                String action = getActivity().getIntent().getAction();
                if ((MiuiKeyguardSettingsUtils.isManagedProfile(UserManager.get(getActivity()), this.mUserId) && !this.mLockPatternUtils.isSeparateProfileChallengeEnabled(this.mUserId)) && !("android.app.action.SET_NEW_PARENT_PROFILE_PASSWORD".equals(action) || "android.app.action.SET_NEW_PASSWORD".equals(action))) {
                    this.mPasswordConfirmed = true;
                } else if (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(this.mUserId)) {
                    if (keyguardStoredPasswordQuality != 0) {
                        this.mChooseLockSettingsHelper.launchConfirmationActivity(this, this.mUserId, keyguardStoredPasswordQuality, 107, getString(R.string.lockpassword_confirm_your_password_generic_profile));
                    }
                } else if (!this.mPasswordConfirmed) {
                    keyguardStoredPasswordQuality = this.mChooseLockSettingsHelper.utils().getActivePasswordQuality(UserManager.get(getActivity()).getCredentialOwnerProfile(this.mUserId));
                    if (keyguardStoredPasswordQuality == 0 || this.mIsReOnCreate) {
                        this.mPasswordConfirmed = true;
                    } else {
                        this.mChooseLockSettingsHelper.launchConfirmFragment(this, keyguardStoredPasswordQuality, 107);
                    }
                }
            }
            Intent intent = getActivity().getIntent();
            this.mTheBusinessKey = intent.getStringExtra("common_password_business_key");
            this.mEnableKeyguardPassword = intent.getBooleanExtra("set_keyguard_password", true);
            this.mRequestedMinComplexity = Math.max(getActivity().getIntent().getIntExtra("requested_min_complexity", 0), this.mLockPatternUtils.getRequestedPasswordComplexity(this.mUserId));
            if (!isSetUp()) {
                addPreferencesFromResource(R.xml.security_settings_picker);
            } else if (isDeviceProvisioned(getActivity())) {
                addPreferencesFromResource(R.xml.miui_setup_security_settings_picker);
            } else {
                addPreferencesFromResource(R.xml.miui_provision_setup_security_settings_picker);
            }
            this.mUnlockSetPattern = findPreference("unlock_set_pattern");
            this.mUnlockSetPin = findPreference("unlock_set_pin");
            this.mUnlockSetPassword = findPreference("unlock_set_password");
            int max = Math.max(4, this.mDpm.getPasswordMinimumLength(null));
            boolean z = 262144 == keyguardStoredPasswordQuality || 327680 == keyguardStoredPasswordQuality || 393216 == keyguardStoredPasswordQuality;
            PasswordMetricsWrapper passwordMetricsWrapper = new PasswordMetricsWrapper();
            passwordMetricsWrapper.updatePasswordMetrics("", this.mRequestedMinComplexity, 131072, false, false, new MiuiLockPatternUtils(getActivity()), this.mUserId, z);
            String string = getResources().getString(R.string.unlock_set_unlock_pin_summary, Integer.valueOf(Math.max(max, passwordMetricsWrapper.getMinLength())), Integer.valueOf(this.mDpm.getPasswordMaximumLength(131072)));
            this.mUnlockSetPinSummary = string;
            this.mUnlockSetPin.setSummary(string);
            if (getActivity().getIntent().getBooleanExtra("lockscreen.biometric_weak_fallback", false)) {
                getPreferenceScreen().removePreference(findPreference("unlock_set_password"));
                getPreferenceScreen().removePreference(findPreference("unlock_set_biometric_weak"));
            } else {
                if (getArguments() != null && getArguments().getBoolean("skip_pattern_unlock")) {
                    getPreferenceScreen().removePreference(findPreference("unlock_set_pattern"));
                }
                getPreferenceScreen().removePreference(findPreference("unlock_set_biometric_weak"));
            }
            if (!MiuiUtils.isMaintenanceMode(getActivity()) || getPreferenceScreen() == null) {
                return;
            }
            if (findPreference("unlock_set_biometric_weak") != null) {
                getPreferenceScreen().removePreference(findPreference("unlock_set_biometric_weak"));
            }
            if (this.mUnlockSetPattern != null) {
                getPreferenceScreen().removePreference(this.mUnlockSetPattern);
            }
            if (this.mUnlockSetPin != null) {
                getPreferenceScreen().removePreference(this.mUnlockSetPin);
            }
            if (this.mUnlockSetPassword != null) {
                getPreferenceScreen().removePreference(this.mUnlockSetPassword);
            }
        }

        @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onDestroy() {
            if (!TextUtils.isEmpty(this.mTheBusinessKey)) {
                MiuiSettings.Secure.putBoolean(getContentResolver(), this.mTheBusinessKey, true);
            }
            if (!this.mEnableKeyguardPassword) {
                new MiuiLockPatternUtils(getActivity()).setKeyguardPasswordQuality(0);
            }
            Folme.clean(this.mTurnOffPassword);
            Folme.clean(this.mVisiblePatternTitle);
            super.onDestroy();
        }

        @Override // com.android.settings.SettingsPreferenceFragment
        public void onFragmentResult(int i, Bundle bundle) {
            boolean z = bundle != null && bundle.getInt("miui_security_fragment_result", -1) == 0;
            if (i == 202) {
                finish();
                Bundle bundle2 = new Bundle();
                if (bundle != null) {
                    bundle2.putAll(bundle);
                }
                bundle2.putInt("miui_security_fragment_result", z ? 0 : -1);
                MiuiKeyguardSettingsUtils.onFragmentResult(getTargetFragment(), getTargetRequestCode(), bundle2);
            } else if (i == 107) {
                if (!z) {
                    finish();
                    return;
                }
                this.mPasswordConfirmed = true;
                this.mUserPassword = bundle.getString("password", "");
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onPause() {
            super.onPause();
            AlertDialog alertDialog = this.mForgetPasswordAlertDialog;
            if (alertDialog != null) {
                alertDialog.dismiss();
                this.mForgetPasswordAlertDialog = null;
            }
            CountDownTimer countDownTimer = this.mCountdownTimer;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                this.mCountdownTimer = null;
            }
            ProgressDialog progressDialog = this.mLoadingDialog;
            if (progressDialog != null) {
                progressDialog.dismiss();
                this.mLoadingDialog = null;
            }
            AlertDialog alertDialog2 = this.mConformDialog;
            if (alertDialog2 != null) {
                alertDialog2.dismiss();
                this.mConformDialog = null;
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            String key = preference.getKey();
            key.hashCode();
            char c = 65535;
            switch (key.hashCode()) {
                case -122970563:
                    if (key.equals("unlock_set_pin")) {
                        c = 0;
                        break;
                    }
                    break;
                case 669087475:
                    if (key.equals("unlock_set_password")) {
                        c = 1;
                        break;
                    }
                    break;
                case 1037900023:
                    if (key.equals("unlock_set_biometric_weak")) {
                        c = 2;
                        break;
                    }
                    break;
                case 1407992888:
                    if (key.equals("unlock_set_pattern")) {
                        c = 3;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                case 1:
                case 3:
                    if (MiuiSecuritySettings.isMiShowMode(getActivity())) {
                        ToastUtil.show(getActivity().getApplicationContext(), R.string.mishow_disable_password_setting, 0);
                        return true;
                    }
                    this.mForgetPasswordAlertDialog = showForgetPasswordAlertDialog(key);
                    return true;
                case 2:
                    this.mChooseLockSettingsHelper.startActivityToSetPassword(MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON, this, this.mPasswordConfirmed, this.mUserPassword, this.mUserId);
                    return true;
                default:
                    return false;
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            if (isSetUp()) {
                FrameLayout frameLayout = this.mVisiblePatternLayout;
                if (frameLayout != null) {
                    frameLayout.setVisibility(8);
                    return;
                }
                return;
            }
            disableUnusablePreferences();
            Intent intent = getActivity().getIntent();
            int activePasswordQuality = this.mChooseLockSettingsHelper.utils().getActivePasswordQuality(this.mUserId);
            if (activePasswordQuality == 0) {
                this.mTurnOffPassword.setVisibility(8);
                if (intent.getBooleanExtra("show_add_fingerprint_hint", false)) {
                    if (MiuiUtils.isMaintenanceMode(getActivity())) {
                        this.mHintText.setText(R.string.forbidden_set_unlock_password_msg);
                    } else {
                        this.mHintText.setText(R.string.choose_unlock_fingerprint_msg);
                    }
                } else if (intent.getBooleanExtra("add_keyguard_password_then_add_face_recoginition", false)) {
                    this.mHintText.setText(R.string.choose_unlock_face_msg);
                } else {
                    this.mHintText.setVisibility(8);
                }
            } else {
                this.mTurnOffPassword.setVisibility(0);
                this.mHintText.setVisibility(8);
            }
            if (activePasswordQuality == 65536) {
                LockPatternUtils utils = this.mChooseLockSettingsHelper.utils();
                this.mVisiblePatternLayout.setVisibility(0);
                this.mVisiblePatternBtn.setChecked(utils.isVisiblePatternEnabled(UserHandle.myUserId()));
            } else {
                this.mVisiblePatternLayout.setVisibility(8);
            }
            try {
                ((KeyguardRestrictedPreference) this.mUnlockSetPattern).setSelected(false);
                ((KeyguardRestrictedPreference) this.mUnlockSetPin).setSelected(false);
                ((KeyguardRestrictedPreference) this.mUnlockSetPin).setSummary(this.mUnlockSetPinSummary);
                ((KeyguardRestrictedPreference) this.mUnlockSetPassword).setSelected(false);
                if (activePasswordQuality == 65536) {
                    ((KeyguardRestrictedPreference) this.mUnlockSetPattern).setSelected(true);
                } else if (activePasswordQuality == 131072 || activePasswordQuality == 196608) {
                    ((KeyguardRestrictedPreference) this.mUnlockSetPin).setSelected(true);
                } else if (activePasswordQuality == 262144 || activePasswordQuality == 327680 || activePasswordQuality == 393216 || activePasswordQuality == 524288) {
                    ((KeyguardRestrictedPreference) this.mUnlockSetPassword).setSelected(true);
                }
            } catch (Exception unused) {
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putBoolean("password_confirmed", this.mPasswordConfirmed);
            bundle.putBoolean("set_password_started", this.mSetPasswordStarted);
        }

        @Override // com.android.settings.KeyguardSettingsPreferenceFragment
        protected void setItemSpace() {
            if (!(getListView() instanceof RecyclerView) || isSetUp()) {
                return;
            }
            getListView().addItemDecoration(new KeyguardSettingsPreferenceFragment.SpacesItemDecoration(36));
        }
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", MiuiSecurityChooseUnlockFragment.class.getName());
        return intent;
    }

    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(R.string.password_entrance_title);
    }
}
