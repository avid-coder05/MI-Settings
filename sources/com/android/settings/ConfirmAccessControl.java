package com.android.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.security.ChooseLockSettingsHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.android.internal.widget.LockPatternView;
import com.android.settings.ConfirmLockPattern;
import com.android.settings.password.ConfirmDeviceCredentialBaseFragment;
import java.util.List;
import miui.cloud.sync.providers.ContactsSyncInfoProvider;
import miui.security.SecurityManager;

/* loaded from: classes.dex */
public class ConfirmAccessControl extends ConfirmLockPattern {
    private ConfirmAccessControlFragment mFragment;
    private boolean mNoBack;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.ConfirmAccessControl$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$ConfirmLockPattern$Stage;

        static {
            int[] iArr = new int[ConfirmLockPattern.Stage.values().length];
            $SwitchMap$com$android$settings$ConfirmLockPattern$Stage = iArr;
            try {
                iArr[ConfirmLockPattern.Stage.NeedToUnlock.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$ConfirmLockPattern$Stage[ConfirmLockPattern.Stage.NeedToUnlockWrong.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$ConfirmLockPattern$Stage[ConfirmLockPattern.Stage.LockedOut.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* loaded from: classes.dex */
    public static class ConfirmAccessControlFragment extends ConfirmLockPattern.ConfirmLockPatternFragment {
        private static long sDeadline;
        private IBinder mCallerToken;
        private boolean mCheckAccess;
        private ChooseLockSettingsHelper mChooseLockSettingsHelper;
        private int mConfirmPurpose;
        private Button mEmergencyCall = null;
        private Intent mIntent;
        private String mPackageName;
        private SecurityManager mSecurityManager;

        private void handleConfirmPurpose() {
            int i = this.mConfirmPurpose;
            if (i == 1) {
                this.mChooseLockSettingsHelper.setACLockEnabled(false);
            } else if (i == 2) {
                this.mChooseLockSettingsHelper.setPasswordForPrivacyModeEnabled(true);
            } else if (i == 3) {
                this.mChooseLockSettingsHelper.setPasswordForPrivacyModeEnabled(false);
            } else if (i == 4) {
                this.mChooseLockSettingsHelper.setPrivacyModeEnabled(false);
            }
        }

        @Override // com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment
        protected void accessLockPattern(List<LockPatternView.Cell> list) {
            handleConfirmPurpose();
            if (!TextUtils.isEmpty(this.mPackageName)) {
                this.mSecurityManager.addAccessControlPass(this.mPackageName);
            }
            Intent intent = this.mIntent;
            if (intent != null) {
                startActivity(intent);
            }
            super.accessLockPattern(list);
        }

        @Override // com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment
        protected boolean checkPattern(List<LockPatternView.Cell> list) {
            return ((ConfirmLockPattern.ConfirmLockPatternFragment) this).mLockPatternUtils.checkMiuiLockPattern(list);
        }

        @Override // com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment, com.android.settings.BaseConfirmLockFragment
        public View createView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            View createView = super.createView(layoutInflater, viewGroup, bundle);
            Button button = (Button) createView.findViewById(R.id.emergencyCall);
            this.mEmergencyCall = button;
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.ConfirmAccessControl.ConfirmAccessControlFragment.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    Intent intent = new Intent("com.android.phone.EmergencyDialer.DIAL");
                    intent.setFlags(276824064);
                    ConfirmAccessControlFragment.this.startActivity(intent);
                }
            });
            if (ContactsSyncInfoProvider.AUTHORITY.equals(this.mPackageName) && Utils.isVoiceCapable(this.mEmergencyCall.getContext())) {
                this.mEmergencyCall.setVisibility(0);
            } else {
                this.mEmergencyCall.setVisibility(8);
            }
            return createView;
        }

        @Override // com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment
        protected String getDisableKey() {
            return "access_control_lock_enabled";
        }

        @Override // com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment
        protected boolean getInStealthMode() {
            return !PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getBoolean("ac_visiblepattern", false);
        }

        @Override // com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment
        protected long getLockoutAttepmpDeadline(long j) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long j2 = sDeadline;
            if (j2 < elapsedRealtime || j2 > elapsedRealtime + 30000) {
                sDeadline = 0L;
            }
            return sDeadline;
        }

        @Override // com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment
        protected void handleAttemptLockout(long j) {
            super.handleAttemptLockout(j);
            Account[] accountsByType = AccountManager.get(getActivity()).getAccountsByType("com.xiaomi");
            if (accountsByType == null || accountsByType.length <= 0) {
                this.mForgetPattern.setVisibility(8);
            } else {
                this.mForgetPattern.setVisibility(0);
            }
        }

        @Override // com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment, com.android.settings.BaseConfirmLockFragment, com.android.settings.password.ConfirmDeviceCredentialBaseFragment, com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mChooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity());
            this.mSecurityManager = (SecurityManager) getActivity().getSystemService("security");
        }

        @Override // com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment
        protected void onCreateNoSaveState() {
            if (((ConfirmLockPattern.ConfirmLockPatternFragment) this).mLockPatternUtils.savedMiuiLockPatternExists()) {
                return;
            }
            getActivity().setResult(-1);
            if (!TextUtils.isEmpty(this.mPackageName)) {
                this.mSecurityManager.addAccessControlPass(this.mPackageName);
            }
            handleConfirmPurpose();
            getActivity().finish();
        }

        @Override // com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment
        protected void parseIntent(Intent intent) {
            boolean z;
            CharSequence loadLabel;
            ApplicationInfo applicationInfo = null;
            this.mIntent = null;
            this.mCallerToken = null;
            boolean z2 = false;
            this.mCheckAccess = false;
            if (intent != null) {
                super.parseIntent(intent);
                String stringExtra = intent.getStringExtra("android.intent.action.CREATE_SHORTCUT");
                this.mPackageName = stringExtra;
                if (TextUtils.isEmpty(stringExtra)) {
                    this.mPackageName = intent.getStringExtra("android.intent.extra.shortcut.NAME");
                }
                this.mConfirmPurpose = intent.getIntExtra("confirm_purpose", 0);
                this.mIntent = (Intent) intent.getParcelableExtra("android.intent.extra.INTENT");
                this.mCallerToken = intent.getIBinderExtra("android.app.extra.PROTECTED_APP_TOKEN");
                this.mCheckAccess = "miui.intent.action.CHECK_ACCESS_CONTROL".equals(intent.getAction()) || "android.app.action.CHECK_ACCESS_CONTROL_PAD".equals(intent.getAction());
                if (TextUtils.isEmpty(this.mPackageName)) {
                    if (this.mConfirmPurpose == 4) {
                        this.mHeaderText = getString(R.string.access_control_need_password, getString(R.string.privacy_mode_dialog_title)) + getString(R.string.access_control_need_to_unlock);
                    }
                    z = false;
                } else {
                    PackageManager packageManager = getActivity().getPackageManager();
                    try {
                        applicationInfo = packageManager.getApplicationInfo(this.mPackageName, 0);
                    } catch (PackageManager.NameNotFoundException unused) {
                    }
                    if (applicationInfo != null && (loadLabel = applicationInfo.loadLabel(packageManager)) != null) {
                        this.mHeaderText = getString(R.string.access_control_need_password, loadLabel) + getString(R.string.access_control_need_to_unlock);
                    }
                    z = true;
                }
                ((ConfirmAccessControl) getActivity()).mNoBack = z;
                try {
                    Intent intent2 = this.mIntent;
                    if (intent2 != null) {
                        if (intent2.getBooleanExtra("StartActivityWhenLocked", false)) {
                            z2 = true;
                        }
                    }
                } catch (Exception e) {
                    Log.w(ConfirmDeviceCredentialBaseFragment.TAG, "Fail to read StartActivityWhenLocked from intent", e);
                }
                Window window = getActivity().getWindow();
                if (z2) {
                    window.addFlags(524288);
                } else {
                    window.clearFlags(524288);
                }
            }
        }

        @Override // com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment
        protected void setLockoutAttepmpDeadline(long j) {
            sDeadline = j;
            ((ConfirmLockPattern.ConfirmLockPatternFragment) this).mLockPatternUtils.clearLockoutAttemptDeadline();
        }

        @Override // com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment
        protected void updateStage(ConfirmLockPattern.Stage stage) {
            super.updateStage(stage);
            int i = AnonymousClass1.$SwitchMap$com$android$settings$ConfirmLockPattern$Stage[stage.ordinal()];
            if (i == 1) {
                this.mForgetPattern.setVisibility(8);
            } else if (i != 3) {
            } else {
                this.mForgetPattern.setVisibility(0);
            }
        }
    }

    @Override // com.android.settings.ConfirmLockPattern, com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", ConfirmAccessControlFragment.class.getName());
        return intent;
    }

    @Override // com.android.settings.ConfirmLockPattern, com.android.settings.SettingsActivity
    protected boolean isValidFragment(String str) {
        return true;
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onAttachFragment(Fragment fragment) {
        this.mFragment = (ConfirmAccessControlFragment) fragment;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.mFragment.parseIntent(intent);
        this.mFragment.updateStage(ConfirmLockPattern.Stage.NeedToUnlock);
    }

    @Override // com.android.settings.password.ConfirmDeviceCredentialBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (this.mNoBack && menuItem.getItemId() == 16908332) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
