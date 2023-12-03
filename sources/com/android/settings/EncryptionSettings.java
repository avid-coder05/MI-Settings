package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.ConfirmLockPassword;
import com.android.settings.ConfirmLockPattern;
import com.android.settings.core.SubSettingLauncher;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.ProgressDialog;

/* loaded from: classes.dex */
public class EncryptionSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final int MY_USER_ID = UserHandle.myUserId();
    private ConfigureEncryptionDialog mDialog;
    private boolean mDialogShow;
    private CheckBoxPreference mEncryptionEnabled;
    private IntentFilter mFilter;
    private LockPatternUtils mLockPatternUtils;
    private WaitForEncryptionProgressDialog mProgressDialog;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.EncryptionSettings.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (-1 == intent.getIntExtra("vold_status", -1)) {
                return;
            }
            EncryptionSettings.this.mProgressDialog.tryToDismiss();
        }
    };

    /* loaded from: classes.dex */
    private class ConfigureEncryptionDialog implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
        private boolean mCheckBoxStatus;
        private boolean mConfigureConfirmed;
        private AlertDialog mDialog;

        private ConfigureEncryptionDialog() {
            FragmentActivity activity = EncryptionSettings.this.getActivity();
            boolean isChecked = EncryptionSettings.this.mEncryptionEnabled.isChecked();
            this.mCheckBoxStatus = isChecked;
            if (isChecked) {
                Log.v("EncryptionSettings", "checkbox: enabled, prepare the close dialog");
                this.mDialog = new AlertDialog.Builder(activity).setTitle(activity.getResources().getString(R.string.security_encryption_close_dialog_title)).setMessage(activity.getResources().getString(R.string.security_encryption_close_dialog_info)).setIconAttribute(16843605).setPositiveButton(17039370, this).setNegativeButton(17039360, this).create();
            } else {
                Log.v("EncryptionSettings", "checkbox: not enabled, prepare set up dialog");
                this.mDialog = new AlertDialog.Builder(activity).setTitle(activity.getResources().getString(R.string.security_encryption_alert_dialog_title_first)).setMessage(activity.getResources().getString(R.string.security_encryption_alert_dialog_info_first)).setIconAttribute(16843605).setPositiveButton(R.string.security_encryption_diaglog_continue, this).setNegativeButton(17039360, this).create();
            }
            this.mDialog.setOnDismissListener(this);
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            this.mConfigureConfirmed = i == -1;
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialogInterface) {
            EncryptionSettings.this.mDialogShow = false;
            if (this.mConfigureConfirmed) {
                this.mConfigureConfirmed = false;
                if (this.mCheckBoxStatus) {
                    Log.d("EncryptionSettings", "close the device encryption");
                    EncryptionSettings.this.launchConfirmationFragment(101);
                } else {
                    Log.d("EncryptionSettings", "set up new lock password");
                    Intent intent = new Intent();
                    intent.setClassName("com.android.settings", "com.android.settings.MiuiSecurityChooseUnlock");
                    intent.putExtra("use_lock_password_to_encrypt_device", true);
                    EncryptionSettings.this.startActivityForResult(intent, 1);
                }
            }
            EncryptionSettings.this.updateEncryptionEnabled();
        }

        public void show() {
            EncryptionSettings.this.mDialogShow = true;
            this.mDialog.show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class WaitForEncryptionProgressDialog extends ProgressDialog {
        private boolean mShow;

        private WaitForEncryptionProgressDialog(int i) {
            super(EncryptionSettings.this.getContext());
            this.mShow = false;
            setProgressStyle(0);
            setCancelable(false);
            setCanceledOnTouchOutside(false);
            setMessage(EncryptionSettings.this.getString(i));
            setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.EncryptionSettings.WaitForEncryptionProgressDialog.1
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    synchronized (this) {
                        WaitForEncryptionProgressDialog.this.mShow = false;
                    }
                }
            });
        }

        private void waitForDismiss() {
            new Handler().postDelayed(new Runnable() { // from class: com.android.settings.EncryptionSettings.WaitForEncryptionProgressDialog.2
                @Override // java.lang.Runnable
                public void run() {
                    WaitForEncryptionProgressDialog.this.tryToDismiss();
                }
            }, 5000L);
        }

        @Override // android.app.Dialog
        public void show() {
            synchronized (this) {
                if (!this.mShow) {
                    this.mShow = true;
                    super.show();
                }
            }
            waitForDismiss();
        }

        public void tryToDismiss() {
            if (this.mShow) {
                dismiss();
            }
        }
    }

    private void closeSecurityEncryption() {
        this.mLockPatternUtils.clearEncryptionPassword();
        this.mLockPatternUtils.setCredentialRequiredToDecrypt(false);
        this.mEncryptionEnabled.setChecked(false);
        WaitForEncryptionProgressDialog waitForEncryptionProgressDialog = new WaitForEncryptionProgressDialog(R.string.security_encryption_progress_dialog_close);
        this.mProgressDialog = waitForEncryptionProgressDialog;
        waitForEncryptionProgressDialog.show();
        setSecurityEncryptionEnabled(false);
    }

    private boolean isSecurityEncryptionEnabled() {
        return Settings.System.getInt(getContentResolver(), "is_security_encryption_enabled", 0) > 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchConfirmationFragment(int i) {
        int activePasswordQuality = this.mLockPatternUtils.getActivePasswordQuality(UserHandle.myUserId());
        if (activePasswordQuality == 0) {
            closeSecurityEncryption();
        } else if (activePasswordQuality == 65536) {
            new SubSettingLauncher(getActivity()).setDestination(ConfirmLockPattern.ConfirmLockPatternFragment.class.getName()).setResultListener(this, i).setTitleRes(R.string.lockpassword_confirm_your_pattern_header).setSourceMetricsCategory(0).launch();
        } else if (activePasswordQuality == 131072 || activePasswordQuality == 196608 || activePasswordQuality == 262144 || activePasswordQuality == 327680 || activePasswordQuality == 393216) {
            new SubSettingLauncher(getActivity()).setDestination(ConfirmLockPassword.ConfirmLockPasswordFragment.class.getName()).setResultListener(this, i).setTitleRes(R.string.lockpassword_confirm_your_lock_password_header).setSourceMetricsCategory(0).launch();
        }
    }

    private void openSecurityEncryption() {
        this.mEncryptionEnabled.setChecked(true);
        WaitForEncryptionProgressDialog waitForEncryptionProgressDialog = new WaitForEncryptionProgressDialog(R.string.security_encryption_progress_dialog_open);
        this.mProgressDialog = waitForEncryptionProgressDialog;
        waitForEncryptionProgressDialog.show();
        setSecurityEncryptionEnabled(true);
    }

    private void setSecurityEncryptionEnabled(boolean z) {
        Settings.System.putInt(getContentResolver(), "is_security_encryption_enabled", z ? 1 : 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEncryptionEnabled() {
        CheckBoxPreference checkBoxPreference = this.mEncryptionEnabled;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(isSecurityEncryptionEnabled());
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return EncryptionSettings.class.getName();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i != 1) {
            if (i == 101 && i2 == -1) {
                closeSecurityEncryption();
            }
        } else if (i2 == -1) {
            openSecurityEncryption();
        }
        updateEncryptionEnabled();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getPackageManager().isPackageAvailable("com.xiaomi.mihomemanager")) {
            Toast.makeText(getContext(), R.string.security_encryption_rejected_via_home_manager, 0).show();
            finish();
        }
        this.mLockPatternUtils = new LockPatternUtils(getActivity());
        addPreferencesFromResource(R.xml.encryption_settings);
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("security_encryption_enable");
        this.mEncryptionEnabled = checkBoxPreference;
        checkBoxPreference.setChecked(isSecurityEncryptionEnabled());
        this.mEncryptionEnabled.setOnPreferenceChangeListener(this);
        IntentFilter intentFilter = new IntentFilter();
        this.mFilter = intentFilter;
        intentFilter.addAction("com.miui.EncryptionPassword");
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        WaitForEncryptionProgressDialog waitForEncryptionProgressDialog = this.mProgressDialog;
        if (waitForEncryptionProgressDialog != null) {
            waitForEncryptionProgressDialog.tryToDismiss();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.mReceiver);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("security_encryption_enable".equals(preference.getKey())) {
            this.mEncryptionEnabled.setChecked(!((Boolean) obj).booleanValue());
            ConfigureEncryptionDialog configureEncryptionDialog = new ConfigureEncryptionDialog();
            this.mDialog = configureEncryptionDialog;
            configureEncryptionDialog.show();
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        Bundle bundle = getArguments() != null ? getArguments().getBundle("saved_bundle") : null;
        if (bundle != null) {
            boolean z = bundle.getBoolean("show_dialog");
            this.mDialogShow = z;
            if (z) {
                ConfigureEncryptionDialog configureEncryptionDialog = new ConfigureEncryptionDialog();
                this.mDialog = configureEncryptionDialog;
                configureEncryptionDialog.show();
            }
        }
        getActivity().registerReceiver(this.mReceiver, this.mFilter);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("show_dialog", this.mDialogShow);
        getArguments().putBundle("saved_bundle", bundle);
    }
}
