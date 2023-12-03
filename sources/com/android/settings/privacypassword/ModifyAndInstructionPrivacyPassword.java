package com.android.settings.privacypassword;

import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.FingerprintHelper;
import com.android.settings.FingerprintIdentifyCallback;
import com.android.settings.R;
import java.util.List;
import miui.os.Build;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;
import miuix.preference.PreferenceFragment;

/* loaded from: classes2.dex */
public class ModifyAndInstructionPrivacyPassword extends AppCompatActivity {
    private ModifyAndInstructionPrivacyPasswordFragment mFragment;

    /* loaded from: classes2.dex */
    public static class ModifyAndInstructionPrivacyPasswordFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
        private AlertDialog mAccountDialog;
        private AppCompatActivity mActivity;
        private android.app.AlertDialog mAlertDialog;
        private CheckBoxPreference mBindAccount;
        private CheckBoxPreference mConfirmUseFingerprint;
        private TextView mFingerprintDialogTitle;
        private int mFingerprintErrorCount;
        private FingerprintHelper mFingerprintHelper;
        private boolean mIsVerifyFingerprintFailed;
        private PreferenceCategory mPasswordSettingCategory;
        private CheckBoxPreference mPasswordToggle;
        private PreferenceCategory mPreferenceCategory;
        private PrivacyPasswordManager mPrivacyPasswordManager;
        private CheckBoxPreference mVisiblePattern;
        private Preference modifyPrivacyPassword;
        private AccountManagerCallback<Bundle> mAccountCallback = new AccountManagerCallback<Bundle>() { // from class: com.android.settings.privacypassword.ModifyAndInstructionPrivacyPassword.ModifyAndInstructionPrivacyPasswordFragment.4
            @Override // android.accounts.AccountManagerCallback
            public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                AppCompatActivity appCompatActivity = ModifyAndInstructionPrivacyPasswordFragment.this.mActivity;
                try {
                    if (accountManagerFuture.getResult().getBoolean("booleanResult")) {
                        ModifyAndInstructionPrivacyPasswordFragment.this.mPrivacyPasswordManager.bindXiaoMiAccount(XiaomiAccountUtils.loginedXiaomiAccount(appCompatActivity).name);
                        Toast.makeText(appCompatActivity, appCompatActivity.getResources().getString(R.string.bind_xiaomi_account_success), 1).show();
                    } else {
                        ModifyAndInstructionPrivacyPasswordFragment.this.mPrivacyPasswordManager.bindXiaoMiAccount(null);
                    }
                } catch (Exception e) {
                    Log.e("ModifyAndInstructionPrivacyPassword", "forgetPrivacyPassword error", e);
                }
            }
        };
        private DialogInterface.OnClickListener mFingerDialogListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.privacypassword.ModifyAndInstructionPrivacyPassword.ModifyAndInstructionPrivacyPasswordFragment.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                ModifyAndInstructionPrivacyPasswordFragment.this.mPrivacyPasswordManager.setFingerprintEnable(false);
                ModifyAndInstructionPrivacyPasswordFragment.this.mConfirmUseFingerprint.setChecked(false);
                ModifyAndInstructionPrivacyPasswordFragment.this.mFingerprintHelper.cancelIdentify();
            }
        };
        private DialogInterface.OnDismissListener mFingerprintDialogDismissListener = new DialogInterface.OnDismissListener() { // from class: com.android.settings.privacypassword.ModifyAndInstructionPrivacyPassword.ModifyAndInstructionPrivacyPasswordFragment.8
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                Log.i("ModifyAndInstructionPrivacyPassword", "mFingerprintDialog dismiss");
                ModifyAndInstructionPrivacyPasswordFragment.this.mIsVerifyFingerprintFailed = false;
                ModifyAndInstructionPrivacyPasswordFragment.this.mFingerprintErrorCount = 0;
                ModifyAndInstructionPrivacyPasswordFragment.this.setFingerprintRevive();
                ModifyAndInstructionPrivacyPasswordFragment.this.mFingerprintHelper.cancelIdentify();
            }
        };
        private FingerprintIdentifyCallback mFingerprintIdentifyCallback = new FingerprintIdentifyCallback() { // from class: com.android.settings.privacypassword.ModifyAndInstructionPrivacyPassword.ModifyAndInstructionPrivacyPasswordFragment.9
            @Override // com.android.settings.FingerprintIdentifyCallback
            public void onFailed() {
                int wrongFingerAttempts = PrivacyPasswordUtils.getWrongFingerAttempts(ModifyAndInstructionPrivacyPasswordFragment.this.mActivity) + 1;
                PrivacyPasswordUtils.setWrongFingerAttempts(ModifyAndInstructionPrivacyPasswordFragment.this.mActivity, wrongFingerAttempts);
                if (ModifyAndInstructionPrivacyPasswordFragment.access$704(ModifyAndInstructionPrivacyPasswordFragment.this) < 5 && wrongFingerAttempts < 5) {
                    ModifyAndInstructionPrivacyPasswordFragment.this.mIsVerifyFingerprintFailed = true;
                    ModifyAndInstructionPrivacyPasswordFragment.this.mFingerprintDialogTitle.setText(ModifyAndInstructionPrivacyPasswordFragment.this.getResources().getString(R.string.privacy_password_fingerprint_dialog_error_title));
                    ModifyAndInstructionPrivacyPasswordFragment.this.mAlertDialog.show();
                    ModifyAndInstructionPrivacyPasswordFragment.this.setVibrator();
                    return;
                }
                ModifyAndInstructionPrivacyPasswordFragment.this.mIsVerifyFingerprintFailed = false;
                ModifyAndInstructionPrivacyPasswordFragment.this.mAlertDialog.dismiss();
                ModifyAndInstructionPrivacyPasswordFragment.this.mFingerprintErrorCount = 0;
                Toast.makeText(ModifyAndInstructionPrivacyPasswordFragment.this.mActivity, ModifyAndInstructionPrivacyPasswordFragment.this.getResources().getString(R.string.privacy_password_fingerprint_confirm_failed), 1).show();
                ModifyAndInstructionPrivacyPasswordFragment.this.setFingerprintRevive();
                ModifyAndInstructionPrivacyPasswordFragment.this.mFingerprintHelper.cancelIdentify();
            }

            @Override // com.android.settings.FingerprintIdentifyCallback
            public void onIdentified(int i) {
                ModifyAndInstructionPrivacyPasswordFragment.this.mPrivacyPasswordManager.setFingerprintEnable(true);
                ModifyAndInstructionPrivacyPasswordFragment.this.mConfirmUseFingerprint.setChecked(true);
                Toast.makeText(ModifyAndInstructionPrivacyPasswordFragment.this.mActivity, ModifyAndInstructionPrivacyPasswordFragment.this.getResources().getString(R.string.privacy_password_fingerprint_confirm_succes), 1).show();
                ModifyAndInstructionPrivacyPasswordFragment.this.mAlertDialog.setOnDismissListener(null);
                ModifyAndInstructionPrivacyPasswordFragment.this.mAlertDialog.dismiss();
                ModifyAndInstructionPrivacyPasswordFragment.this.mIsVerifyFingerprintFailed = true;
                ModifyAndInstructionPrivacyPasswordFragment.this.mFingerprintErrorCount = 0;
                ModifyAndInstructionPrivacyPasswordFragment.this.setFingerprintRevive();
                ModifyAndInstructionPrivacyPasswordFragment.this.mFingerprintHelper.cancelIdentify();
            }

            @Override // com.android.settings.FingerprintIdentifyCallback
            public void onLockout() {
                ModifyAndInstructionPrivacyPasswordFragment.this.mIsVerifyFingerprintFailed = false;
                ModifyAndInstructionPrivacyPasswordFragment.this.mAlertDialog.dismiss();
                ModifyAndInstructionPrivacyPasswordFragment.this.mFingerprintErrorCount = 0;
                Toast.makeText(ModifyAndInstructionPrivacyPasswordFragment.this.mActivity, ModifyAndInstructionPrivacyPasswordFragment.this.getResources().getString(R.string.privacy_password_fingerprint_confirm_failed), 1).show();
                ModifyAndInstructionPrivacyPasswordFragment.this.setFingerprintRevive();
                ModifyAndInstructionPrivacyPasswordFragment.this.mFingerprintHelper.cancelIdentify();
            }
        };

        static /* synthetic */ int access$704(ModifyAndInstructionPrivacyPasswordFragment modifyAndInstructionPrivacyPasswordFragment) {
            int i = modifyAndInstructionPrivacyPasswordFragment.mFingerprintErrorCount + 1;
            modifyAndInstructionPrivacyPasswordFragment.mFingerprintErrorCount = i;
            return i;
        }

        private void addPreferenceItem(String str, String str2) {
            if (PrivacyPasswordUtils.excludPreferenceItem(this.mActivity, str)) {
                return;
            }
            Preference preference = new Preference(getPreferenceManager().getContext());
            preference.setKey(str);
            preference.setTitle(str2);
            preference.setOnPreferenceClickListener(this);
            this.mPreferenceCategory.addPreference(preference);
        }

        private void identifyFingerprint() {
            try {
                FingerprintHelper fingerprintHelper = this.mFingerprintHelper;
                fingerprintHelper.identify(this.mFingerprintIdentifyCallback, fingerprintHelper.getFingerprintIds());
            } catch (Exception e) {
                Log.e("ModifyAndInstructionPrivacyPassword", "finger identify error", e);
            }
        }

        private boolean isToggled(Preference preference) {
            return ((CheckBoxPreference) preference).isChecked();
        }

        private void setBindAccountStat() {
            boolean isLoginXiaomiAccount = XiaomiAccountUtils.isLoginXiaomiAccount(this.mActivity);
            String bindXiaoMiAccount = this.mPrivacyPasswordManager.getBindXiaoMiAccount();
            boolean z = isLoginXiaomiAccount && bindXiaoMiAccount != null && TextUtils.equals(bindXiaoMiAccount, XiaomiAccountUtils.getLoginedAccountMd5(this.mActivity));
            if (!z) {
                this.mPrivacyPasswordManager.bindXiaoMiAccount(null);
            }
            this.mBindAccount.setChecked(z);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setFingerprintRevive() {
            if (PrivacyPasswordUtils.getWrongFingerAttempts(this.mActivity) != 0) {
                PrivacyPasswordUtils.setWrongFingerAttempts(this.mActivity, 0);
                if (PrivacyPasswordUtils.isFingerprintHardWareDetected()) {
                    PrivacyPasswordUtils.invokeResetTimeout(this.mActivity);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setVibrator() {
            Vibrator vibrator = (Vibrator) this.mActivity.getSystemService("vibrator");
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(200L);
            }
        }

        private void showClosePasswordDialog() {
            new AlertDialog.Builder(this.mActivity).setTitle(R.string.privacy_close_dlg_title).setMessage(R.string.privacy_close_dlg_msg).setNegativeButton(R.string.privacy_dlg_button_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.privacypassword.ModifyAndInstructionPrivacyPassword.ModifyAndInstructionPrivacyPasswordFragment.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    ModifyAndInstructionPrivacyPasswordFragment.this.mPasswordToggle.setChecked(true);
                }
            }).setPositiveButton(R.string.privacy_close_dlg_button_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.privacypassword.ModifyAndInstructionPrivacyPassword.ModifyAndInstructionPrivacyPasswordFragment.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    ModifyAndInstructionPrivacyPasswordFragment.this.startConfirmPassword(29028);
                }
            }).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.privacypassword.ModifyAndInstructionPrivacyPassword.ModifyAndInstructionPrivacyPasswordFragment.1
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    ModifyAndInstructionPrivacyPasswordFragment.this.mPasswordToggle.setChecked(ModifyAndInstructionPrivacyPasswordFragment.this.mPrivacyPasswordManager.havePattern());
                }
            }).show();
        }

        private void updatePreferenceCategories() {
            String[] stringArray = getResources().getStringArray(R.array.privacy_password_function_specfication_keys);
            String[] stringArray2 = getResources().getStringArray(R.array.privacy_password_function_specfication_names);
            PackageManager packageManager = this.mActivity.getPackageManager();
            for (int i = 0; i < stringArray.length; i++) {
                if ((!"privacy_mms".equals(stringArray[i]) || Build.getUserMode() != 1) && (!"privacy_file".equals(stringArray[i]) || UserHandle.myUserId() == 0 || !Build.IS_INTERNATIONAL_BUILD)) {
                    BussinessSpecificationInfo bussinessSpecificationInfo = BussinessPackageInfoCache.getSpcificationInfos().get(BussinessPackageInfoCache.getModifyandInstructionsInfo().get(stringArray[i]));
                    Intent intent = new Intent(bussinessSpecificationInfo.intentAction);
                    intent.setPackage(bussinessSpecificationInfo.startPackage);
                    List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
                    if (queryIntentActivities != null && queryIntentActivities.size() != 0) {
                        addPreferenceItem(stringArray[i], stringArray2[i]);
                    }
                }
            }
        }

        protected void createBindXiaomiAccountDialog() {
            AlertDialog create = new AlertDialog.Builder(this.mActivity).setTitle(getResources().getString(R.string.confirm_bind_xiaomi_account_dialog_title)).setMessage(getResources().getString(R.string.bind_xiaomi_account_dialog_summery, XiaomiAccountUtils.loginedXiaomiAccount(this.mActivity).name)).setNegativeButton(getResources().getString(R.string.privacy_dlg_button_cancel), new DialogInterface.OnClickListener() { // from class: com.android.settings.privacypassword.ModifyAndInstructionPrivacyPassword.ModifyAndInstructionPrivacyPasswordFragment.6
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    ModifyAndInstructionPrivacyPasswordFragment.this.mPrivacyPasswordManager.bindXiaoMiAccount(null);
                    ModifyAndInstructionPrivacyPasswordFragment.this.mBindAccount.setChecked(false);
                }
            }).setPositiveButton(getResources().getString(R.string.bind_xiaomi_account_confirm), new DialogInterface.OnClickListener() { // from class: com.android.settings.privacypassword.ModifyAndInstructionPrivacyPassword.ModifyAndInstructionPrivacyPasswordFragment.5
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    ModifyAndInstructionPrivacyPasswordFragment.this.mPrivacyPasswordManager.bindXiaoMiAccount(XiaomiAccountUtils.loginedXiaomiAccount(ModifyAndInstructionPrivacyPasswordFragment.this.mActivity).name);
                    Toast.makeText(ModifyAndInstructionPrivacyPasswordFragment.this.mActivity, ModifyAndInstructionPrivacyPasswordFragment.this.getResources().getString(R.string.bind_xiaomi_account_success), 1).show();
                    ModifyAndInstructionPrivacyPasswordFragment.this.mBindAccount.setChecked(true);
                }
            }).create();
            this.mAccountDialog = create;
            create.show();
        }

        protected void createConfirmFingerprintDialog() {
            Animation loadAnimation;
            View inflate;
            if (PrivacyPasswordUtils.isFodFingerprint()) {
                this.mAlertDialog = new FullScreenDialog(this.mActivity, R.style.Fod_Dialog_Fullscreen, this.mFingerprintHelper);
                loadAnimation = AnimationUtils.loadAnimation(this.mActivity, R.anim.fod_finger_appear);
                inflate = getLayoutInflater().inflate(R.layout.applock_fod_fingerprint_window, (ViewGroup) null);
                this.mFingerprintDialogTitle = (TextView) inflate.findViewById(R.id.confirm_fingerprint_view_msg);
            } else {
                this.mAlertDialog = new FullScreenDialog(this.mActivity, R.style.Fod_Dialog_Fullscreen, null);
                loadAnimation = AnimationUtils.loadAnimation(this.mActivity, R.anim.fod_finger_appear);
                inflate = getLayoutInflater().inflate(R.layout.finger_alertdialog_layout, (ViewGroup) null);
                ImageView imageView = (ImageView) inflate.findViewById(R.id.back_finger_print);
                this.mFingerprintDialogTitle = (TextView) inflate.findViewById(R.id.confirm_fingerprint_view_msg);
                if (PrivacyPasswordUtils.isSideFingerprint()) {
                    imageView.setImageResource(R.drawable.core_scan_gesture_broadside);
                } else {
                    imageView.setImageResource(R.drawable.back_finger_print);
                }
                this.mAlertDialog.setOnDismissListener(this.mFingerprintDialogDismissListener);
            }
            inflate.setAnimation(loadAnimation);
            this.mAlertDialog.show();
            this.mAlertDialog.setContentView(inflate);
            TextView textView = (TextView) inflate.findViewById(R.id.cancel_finger_authenticate);
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.privacypassword.ModifyAndInstructionPrivacyPassword.ModifyAndInstructionPrivacyPasswordFragment.10
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ModifyAndInstructionPrivacyPasswordFragment.this.mAlertDialog.dismiss();
                }
            });
            textView.setContentDescription(getResources().getString(R.string.setup_password_back));
            identifyFingerprint();
        }

        public void finishFromActivity() {
            if (XiaomiAccountUtils.isLoginXiaomiAccount(this.mActivity)) {
                return;
            }
            this.mPrivacyPasswordManager.bindXiaoMiAccount(null);
        }

        protected void forgetPrivacyPasswordSettings() {
            if (!XiaomiAccountUtils.isLoginXiaomiAccount(this.mActivity)) {
                XiaomiAccountUtils.loginAccount(this.mActivity, new Bundle(), this.mAccountCallback);
            } else if (this.mPrivacyPasswordManager.getBindXiaoMiAccount() != null) {
                this.mPrivacyPasswordManager.bindXiaoMiAccount(null);
                this.mBindAccount.setChecked(false);
            } else {
                createBindXiaomiAccountDialog();
                this.mBindAccount.setSummary(getResources().getString(R.string.forget_privacy_password_summary));
            }
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i == 290242) {
                if (i2 == -1) {
                    Intent intent2 = new Intent(this.mActivity, SetPrivacyPasswordChooseAccessControl.class);
                    intent2.putExtra("privacy_password_extra_data", "ModifyPassword");
                    startActivityForResult(intent2, 29029);
                    return;
                }
                return;
            }
            if (i == 290251) {
                if (i2 == -1) {
                    forgetPrivacyPasswordSettings();
                    return;
                } else {
                    this.mBindAccount.setChecked(false);
                    return;
                }
            }
            switch (i) {
                case 29026:
                    boolean z = i2 == -1;
                    this.mConfirmUseFingerprint.setChecked(z);
                    this.mPrivacyPasswordManager.setFingerprintEnable(z);
                    return;
                case 29027:
                    if (i2 == -1) {
                        setUseFingerprintConfirmPassword();
                        return;
                    }
                    this.mConfirmUseFingerprint.setChecked(this.mPrivacyPasswordManager.isFingerprintEnable());
                    PrivacyPasswordManager privacyPasswordManager = this.mPrivacyPasswordManager;
                    privacyPasswordManager.setFingerprintEnable(privacyPasswordManager.isFingerprintEnable());
                    return;
                case 29028:
                    if (i2 != -1) {
                        this.mPasswordToggle.setChecked(true);
                        return;
                    }
                    this.mPrivacyPasswordManager.bindXiaoMiAccount(null);
                    if (this.mFingerprintHelper.isHardwareDetected()) {
                        this.mPrivacyPasswordManager.setFingerprintEnable(false);
                    }
                    this.mPrivacyPasswordManager.setVisibilePattern(true);
                    this.mPrivacyPasswordManager.setPasswordEnable(this.mActivity, false);
                    this.mActivity.finish();
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.preference.PreferenceFragmentCompat
        public void onCreatePreferences(Bundle bundle, String str) {
            addPreferencesFromResource(R.xml.modify_instruction_privacy_password);
            AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
            this.mActivity = appCompatActivity;
            this.mPrivacyPasswordManager = PrivacyPasswordManager.getInstance(appCompatActivity);
            Preference findPreference = findPreference("modify_privacy_password");
            this.modifyPrivacyPassword = findPreference;
            findPreference.setOnPreferenceClickListener(this);
            this.mBindAccount = (CheckBoxPreference) findPreference("forget_privacy_password_setting");
            this.mPreferenceCategory = (PreferenceCategory) findPreference("privacy_password_spcific");
            this.mBindAccount.setOnPreferenceChangeListener(this);
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("privacy_password_visible_pattern");
            this.mVisiblePattern = checkBoxPreference;
            checkBoxPreference.setChecked(this.mPrivacyPasswordManager.isVisibilePattern());
            this.mVisiblePattern.setOnPreferenceChangeListener(this);
            this.mFingerprintHelper = new FingerprintHelper(this.mActivity);
            this.mPasswordSettingCategory = (PreferenceCategory) findPreference("password_settings_category");
            this.mConfirmUseFingerprint = (CheckBoxPreference) findPreference("use_finger_cofirm_password");
            if (this.mFingerprintHelper.isHardwareDetected()) {
                boolean z = !this.mFingerprintHelper.getFingerprintIds().isEmpty() && TransparentHelper.isScreenLockOpen(this.mActivity) && this.mPrivacyPasswordManager.isFingerprintEnable();
                this.mConfirmUseFingerprint.setChecked(z);
                this.mConfirmUseFingerprint.setOnPreferenceChangeListener(this);
                this.mPrivacyPasswordManager.setFingerprintEnable(z);
            } else {
                this.mPasswordSettingCategory.removePreference(this.mConfirmUseFingerprint);
            }
            CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) findPreference("privacy_password_toggle");
            this.mPasswordToggle = checkBoxPreference2;
            checkBoxPreference2.setChecked(true);
            this.mPasswordToggle.setOnPreferenceChangeListener(this);
            if (getActivity().getIntent().getBooleanExtra("enter_from_settings", true)) {
                updatePreferenceCategories();
            } else {
                getPreferenceScreen().removePreference(findPreference("privacy_password_spcific"));
            }
            setFingerprintRevive();
        }

        @Override // androidx.fragment.app.Fragment
        public void onPause() {
            android.app.AlertDialog alertDialog = this.mAlertDialog;
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
            AlertDialog alertDialog2 = this.mAccountDialog;
            if (alertDialog2 != null) {
                alertDialog2.dismiss();
            }
            super.onPause();
        }

        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            String key = preference.getKey();
            if ("privacy_password_visible_pattern".equals(key)) {
                this.mPrivacyPasswordManager.setVisibilePattern(booleanValue);
                return true;
            } else if ("forget_privacy_password_setting".equals(key)) {
                startConfirmPassword(290251);
                return true;
            } else if ("use_finger_cofirm_password".equals(key)) {
                startConfirmPassword(29027);
                return true;
            } else if ("privacy_password_toggle".equals(key)) {
                showClosePasswordDialog();
                return true;
            } else {
                return true;
            }
        }

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            String str = BussinessPackageInfoCache.getModifyandInstructionsInfo().get(key);
            if (str != null) {
                Intent intent = new Intent(this.mActivity, FunctionSpecification.class);
                intent.putExtra("privacy_password_function_specification", str);
                startActivity(intent);
                return true;
            } else if ("modify_privacy_password".equals(key)) {
                startConfirmPassword(290242);
                return true;
            } else {
                return true;
            }
        }

        public void onRestartFromActivity() {
            this.mVisiblePattern.setChecked(this.mPrivacyPasswordManager.isVisibilePattern());
        }

        @Override // androidx.fragment.app.Fragment
        public void onResume() {
            if (!this.mPrivacyPasswordManager.havePattern()) {
                this.mActivity.finish();
            }
            setBindAccountStat();
            this.mConfirmUseFingerprint.setChecked(this.mFingerprintHelper.isHardwareDetected() && this.mPrivacyPasswordManager.isFingerprintEnable());
            super.onResume();
        }

        @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onStart() {
            setBindAccountStat();
            super.onStart();
        }

        protected void setUseFingerprintConfirmPassword() {
            if (!isToggled(this.mConfirmUseFingerprint)) {
                this.mPrivacyPasswordManager.setFingerprintEnable(false);
            } else if (TransparentHelper.isScreenLockOpen(this.mActivity) && !this.mFingerprintHelper.getFingerprintIds().isEmpty()) {
                createConfirmFingerprintDialog();
            } else if (!TransparentHelper.isScreenLockOpen(this.mActivity) && !this.mFingerprintHelper.getFingerprintIds().isEmpty()) {
                Intent intent = new Intent("android.app.action.SET_NEW_PASSWORD");
                if (UserHandle.myUserId() != 0) {
                    PrivacyPasswordUtils.putIntentExtra(this.mActivity, intent);
                }
                startActivityForResult(intent, 29026);
            } else {
                Intent intent2 = new Intent();
                intent2.setComponent(new ComponentName("com.android.settings", "com.android.settings.NewFingerprintInternalActivity"));
                if (UserHandle.myUserId() != 0) {
                    PrivacyPasswordUtils.putIntentExtra(this.mActivity, intent2);
                }
                startActivityForResult(intent2, 29026);
            }
        }

        protected void startConfirmPassword(int i) {
            Intent intent = new Intent(this.mActivity, PrivacyPasswordConfirmAccessControl.class);
            intent.putExtra("enter_from_settings", true);
            startActivityForResult(intent, i);
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, android.app.Activity
    public void finish() {
        ModifyAndInstructionPrivacyPasswordFragment modifyAndInstructionPrivacyPasswordFragment = this.mFragment;
        if (modifyAndInstructionPrivacyPasswordFragment != null) {
            modifyAndInstructionPrivacyPasswordFragment.finishFromActivity();
        }
        super.finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mFragment = new ModifyAndInstructionPrivacyPasswordFragment();
        getSupportFragmentManager().beginTransaction().replace(16908290, this.mFragment).commit();
    }

    @Override // android.app.Activity
    protected void onRestart() {
        super.onRestart();
        ModifyAndInstructionPrivacyPasswordFragment modifyAndInstructionPrivacyPasswordFragment = this.mFragment;
        if (modifyAndInstructionPrivacyPasswordFragment != null) {
            modifyAndInstructionPrivacyPasswordFragment.onRestartFromActivity();
        }
    }
}
