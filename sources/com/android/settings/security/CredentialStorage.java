package com.android.settings.security;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.security.IKeyChainService;
import android.security.KeyChain;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settings.vpn2.VpnUtils;
import com.android.settingslib.core.lifecycle.HideNonSystemOverlayMixin;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public final class CredentialStorage extends FragmentActivity {
    private Bundle mInstallBundle;
    private LockPatternUtils mUtils;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class InstallKeyInKeyChain extends AsyncTask<Void, Void, Boolean> {
        final String mAlias;
        private final byte[] mCaListData;
        private final byte[] mCertData;
        private final byte[] mKeyData;
        private final int mUid;

        InstallKeyInKeyChain(String str, byte[] bArr, byte[] bArr2, byte[] bArr3, int i) {
            this.mAlias = str;
            this.mKeyData = bArr;
            this.mCertData = bArr2;
            this.mCaListData = bArr3;
            this.mUid = i;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... voidArr) {
            try {
                KeyChain.KeyChainConnection bind = KeyChain.bind(CredentialStorage.this);
                try {
                    IKeyChainService service = bind.getService();
                    if (!service.installKeyPair(this.mKeyData, this.mCertData, this.mCaListData, this.mAlias, this.mUid)) {
                        Log.w("CredentialStorage", String.format("Failed installing key %s", this.mAlias));
                        Boolean bool = Boolean.FALSE;
                        bind.close();
                        return bool;
                    }
                    int i = this.mUid;
                    if (i == 1000 || i == -1) {
                        service.setUserSelectable(this.mAlias, true);
                    }
                    Boolean bool2 = Boolean.TRUE;
                    bind.close();
                    return bool2;
                } finally {
                }
            } catch (RemoteException e) {
                Log.w("CredentialStorage", String.format("Failed to install key %s to uid %d", this.mAlias, Integer.valueOf(this.mUid)), e);
                return Boolean.FALSE;
            } catch (InterruptedException e2) {
                Log.w("CredentialStorage", String.format("Interrupted while installing key %s", this.mAlias), e2);
                Thread.currentThread().interrupt();
                return Boolean.FALSE;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean bool) {
            CredentialStorage.this.onKeyInstalled(this.mAlias, this.mUid, bool.booleanValue());
        }
    }

    /* loaded from: classes2.dex */
    private class ResetDialog implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
        private boolean mResetConfirmed;

        private ResetDialog() {
            AlertDialog create = new AlertDialog.Builder(CredentialStorage.this, R.style.AlertDialog_Theme_DayNight).setTitle(17039380).setMessage(R.string.credentials_reset_hint).setPositiveButton(17039370, this).setNegativeButton(17039360, this).create();
            create.setOnDismissListener(this);
            create.show();
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            this.mResetConfirmed = i == -1;
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialogInterface) {
            if (!this.mResetConfirmed) {
                CredentialStorage.this.finish();
                return;
            }
            this.mResetConfirmed = false;
            if (!CredentialStorage.this.mUtils.isSecure(UserHandle.myUserId())) {
                new ResetKeyStoreAndKeyChain().execute(new Void[0]);
            } else if (CredentialStorage.this.confirmKeyGuard(1)) {
            } else {
                Log.w("CredentialStorage", "Failed to launch credential confirmation for a secure user.");
                CredentialStorage.this.finish();
            }
        }
    }

    /* loaded from: classes2.dex */
    private class ResetKeyStoreAndKeyChain extends AsyncTask<Void, Void, Boolean> {
        private ResetKeyStoreAndKeyChain() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... voidArr) {
            CredentialStorage.this.mUtils.resetKeyStore(UserHandle.myUserId());
            try {
                KeyChain.KeyChainConnection bind = KeyChain.bind(CredentialStorage.this);
                try {
                    try {
                        Boolean valueOf = Boolean.valueOf(bind.getService().reset());
                        try {
                            bind.close();
                        } catch (Exception e) {
                            Log.e("CredentialStorage", "doInBackground: keyChainConnection.close()", e);
                        }
                        return valueOf;
                    } catch (RemoteException unused) {
                        Boolean bool = Boolean.FALSE;
                        try {
                            bind.close();
                        } catch (Exception e2) {
                            Log.e("CredentialStorage", "doInBackground: keyChainConnection.close()", e2);
                        }
                        return bool;
                    }
                } catch (Throwable th) {
                    try {
                        bind.close();
                    } catch (Exception e3) {
                        Log.e("CredentialStorage", "doInBackground: keyChainConnection.close()", e3);
                    }
                    throw th;
                }
            } catch (InterruptedException unused2) {
                Thread.currentThread().interrupt();
                return Boolean.FALSE;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean bool) {
            if (bool.booleanValue()) {
                Toast.makeText(CredentialStorage.this, R.string.credentials_erased, 0).show();
                CredentialStorage.this.clearLegacyVpnIfEstablished();
            } else {
                Toast.makeText(CredentialStorage.this, R.string.credentials_not_erased, 0).show();
            }
            CredentialStorage.this.finish();
        }
    }

    private boolean checkCallerIsCertInstallerOrSelfInProfile() {
        if (TextUtils.equals("com.android.certinstaller", getCallingPackage())) {
            return getPackageManager().checkSignatures(getCallingPackage(), getPackageName()) == 0;
        }
        try {
            int launchedFromUid = ActivityManager.getService().getLaunchedFromUid(getActivityToken());
            if (launchedFromUid == -1) {
                Log.e("CredentialStorage", "com.android.credentials.INSTALL must be started with startActivityForResult");
                return false;
            } else if (UserHandle.isSameApp(launchedFromUid, Process.myUid())) {
                UserInfo profileParent = ((UserManager) getSystemService("user")).getProfileParent(UserHandle.getUserId(launchedFromUid));
                return profileParent != null && profileParent.id == UserHandle.myUserId();
            } else {
                return false;
            }
        } catch (RemoteException unused) {
            return false;
        }
    }

    private boolean checkCallerIsSelf() {
        try {
            return Process.myUid() == ActivityManager.getService().getLaunchedFromUid(getActivityToken());
        } catch (RemoteException unused) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearLegacyVpnIfEstablished() {
        if (VpnUtils.disconnectLegacyVpn(getApplicationContext())) {
            Toast.makeText(this, R.string.vpn_disconnected, 0).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean confirmKeyGuard(int i) {
        return new ChooseLockSettingsHelper.Builder(this).setRequestCode(i).setTitle(getResources().getText(R.string.credentials_title)).show();
    }

    private void handleInstall() {
        if (!isFinishing() && installIfAvailable()) {
            finish();
        }
    }

    private boolean installIfAvailable() {
        Bundle bundle = this.mInstallBundle;
        if (bundle == null || bundle.isEmpty()) {
            return true;
        }
        Bundle bundle2 = this.mInstallBundle;
        this.mInstallBundle = null;
        int i = bundle2.getInt("install_as_uid", -1);
        if (i == -1 || UserHandle.isSameUser(i, Process.myUid())) {
            String string = bundle2.getString("user_key_pair_name", null);
            if (TextUtils.isEmpty(string)) {
                Log.e("CredentialStorage", "Cannot install key without an alias");
                return true;
            }
            new InstallKeyInKeyChain(string, bundle2.getByteArray("user_private_key_data"), bundle2.getByteArray("user_certificate_data"), bundle2.getByteArray("ca_certificates_data"), i).execute(new Void[0]);
            return false;
        }
        int userId = UserHandle.getUserId(i);
        if (i == 1010) {
            startActivityAsUser(new Intent("com.android.credentials.INSTALL").setFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_PIP_SCREEN_PROJECTION).putExtras(bundle2), new UserHandle(userId));
            return true;
        }
        Log.e("CredentialStorage", "Failed to install credentials as uid " + i + ": cross-user installs may only target wifi uids");
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onKeyInstalled(String str, int i, boolean z) {
        if (!z) {
            Log.w("CredentialStorage", String.format("Error installing alias %s for uid %d", str, Integer.valueOf(i)));
            finish();
            return;
        }
        Log.i("CredentialStorage", String.format("Successfully installed alias %s to uid %d.", str, Integer.valueOf(i)));
        sendBroadcast(new Intent("android.security.action.KEYCHAIN_CHANGED"));
        setResult(-1);
        finish();
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1) {
            if (i2 == -1) {
                new ResetKeyStoreAndKeyChain().execute(new Void[0]);
            } else {
                finish();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mUtils = new LockPatternUtils(this);
        getLifecycle().addObserver(new HideNonSystemOverlayMixin(this));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String action = intent.getAction();
        if (((UserManager) getSystemService("user")).hasUserRestriction("no_config_credentials")) {
            finish();
        } else if ("com.android.credentials.RESET".equals(action) && checkCallerIsSelf()) {
            new ResetDialog();
        } else {
            if ("com.android.credentials.INSTALL".equals(action) && checkCallerIsCertInstallerOrSelfInProfile()) {
                this.mInstallBundle = intent.getExtras();
            }
            handleInstall();
        }
    }
}
