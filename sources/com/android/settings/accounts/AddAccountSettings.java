package com.android.settings.accounts;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.os.BadParcelableException;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import android.widget.Toast;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.password.ChooseLockSettingsHelper;
import java.io.IOException;
import java.lang.ref.WeakReference;
import miui.payment.PaymentManager;
import miui.provider.ExtraTelephony;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class AddAccountSettings extends AppCompatActivity {
    private boolean mAddAccountCalled = false;
    private PendingIntent mPendingIntent;
    private UserHandle mUserHandle;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AddAccountSettingsCallback implements AccountManagerCallback<Bundle> {
        private WeakReference<AddAccountSettings> mOuter;

        AddAccountSettingsCallback(AddAccountSettings addAccountSettings) {
            this.mOuter = new WeakReference<>(addAccountSettings);
        }

        @Override // android.accounts.AccountManagerCallback
        public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
            AddAccountSettings addAccountSettings = this.mOuter.get();
            if (addAccountSettings == null) {
                return;
            }
            boolean z = true;
            try {
                try {
                    Bundle result = accountManagerFuture.getResult();
                    Intent intent = (Intent) result.get(PaymentManager.KEY_INTENT);
                    if (intent != null) {
                        try {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("pendingIntent", addAccountSettings.mPendingIntent);
                            bundle.putBoolean("hasMultipleUsers", Utils.hasMultipleUsers(addAccountSettings.getApplicationContext()));
                            bundle.putParcelable("android.intent.extra.USER", addAccountSettings.mUserHandle);
                            intent.putExtras(bundle).addFlags(268435456).addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON);
                            addAccountSettings.startActivityForResultAsUser(intent, 2, addAccountSettings.mUserHandle);
                            z = false;
                        } catch (AuthenticatorException e) {
                            e = e;
                            z = false;
                            if (Log.isLoggable("AddAccountSettings", 2)) {
                                Log.v("AddAccountSettings", "addAccount failed: " + e);
                            }
                            if (addAccountSettings.getIntent().getBooleanExtra("account_setup_wizard", false)) {
                                addAccountSettings.setResult(-1);
                            }
                            if (!z) {
                                return;
                            }
                            addAccountSettings.finish();
                        } catch (OperationCanceledException unused) {
                            z = false;
                            if (Log.isLoggable("AddAccountSettings", 2)) {
                                Log.v("AddAccountSettings", "addAccount was canceled");
                            }
                            if (addAccountSettings.getIntent().getBooleanExtra("account_setup_wizard", false)) {
                                addAccountSettings.setResult(-1);
                            }
                            if (!z) {
                                return;
                            }
                            addAccountSettings.finish();
                        } catch (BadParcelableException e2) {
                            e = e2;
                            z = false;
                            Log.e("AddAccountSettings", "addAccount failed (MIUI): did you install a broken GMS?", e);
                            if (addAccountSettings.getIntent().getBooleanExtra("account_setup_wizard", false)) {
                                addAccountSettings.setResult(-1);
                            }
                            if (!z) {
                                return;
                            }
                            addAccountSettings.finish();
                        } catch (IOException e3) {
                            e = e3;
                            z = false;
                            if (Log.isLoggable("AddAccountSettings", 2)) {
                                Log.v("AddAccountSettings", "addAccount failed: " + e);
                            }
                            if (addAccountSettings.getIntent().getBooleanExtra("account_setup_wizard", false)) {
                                addAccountSettings.setResult(-1);
                            }
                            if (!z) {
                                return;
                            }
                            addAccountSettings.finish();
                        } catch (Throwable th) {
                            th = th;
                            z = false;
                            if (addAccountSettings.getIntent().getBooleanExtra("account_setup_wizard", false)) {
                                addAccountSettings.setResult(-1);
                            }
                            if (z) {
                                addAccountSettings.finish();
                            }
                            throw th;
                        }
                    } else {
                        addAccountSettings.setResult(-1);
                        if (addAccountSettings.mPendingIntent != null) {
                            addAccountSettings.mPendingIntent.cancel();
                            addAccountSettings.mPendingIntent = null;
                        }
                    }
                    if (Log.isLoggable("AddAccountSettings", 2)) {
                        Log.v("AddAccountSettings", "account added: " + result);
                    }
                    if (addAccountSettings.getIntent().getBooleanExtra("account_setup_wizard", false)) {
                        addAccountSettings.setResult(-1);
                    }
                    if (!z) {
                        return;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (AuthenticatorException e4) {
                e = e4;
            } catch (OperationCanceledException unused2) {
            } catch (BadParcelableException e5) {
                e = e5;
            } catch (IOException e6) {
                e = e6;
            }
            addAccountSettings.finish();
        }
    }

    private void addAccount(String str) {
        Bundle bundle = new Bundle();
        if ("com.xiaomi".equals(str)) {
            bundle.putBoolean("show_detail", true);
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("SHOULDN'T RESOLVE!", "SHOULDN'T RESOLVE!"));
        intent.setAction("SHOULDN'T RESOLVE!");
        intent.addCategory("SHOULDN'T RESOLVE!");
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, intent, MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
        this.mPendingIntent = broadcast;
        bundle.putParcelable("pendingIntent", broadcast);
        bundle.putBoolean("hasMultipleUsers", Utils.hasMultipleUsers(this));
        AccountManager.get(getApplicationContext()).addAccountAsUser(str, null, null, bundle, null, new AddAccountSettingsCallback(this), null, this.mUserHandle);
        this.mAddAccountCalled = true;
    }

    private void requestChooseAccount() {
        String[] stringArrayExtra = getIntent().getStringArrayExtra("authorities");
        String[] stringArrayExtra2 = getIntent().getStringArrayExtra("account_types");
        String[] stringArrayExtra3 = getIntent().getStringArrayExtra("miui.intent.extra.account_support_contacts");
        Intent intent = new Intent(this, MiuiChooseAccountActivity.class);
        if (stringArrayExtra != null) {
            intent.putExtra("authorities", stringArrayExtra);
        }
        if (stringArrayExtra3 != null) {
            intent.putExtra("account_types", stringArrayExtra3);
        } else if (stringArrayExtra2 != null) {
            intent.putExtra("account_types", stringArrayExtra2);
        }
        intent.putExtra("android.intent.extra.USER", this.mUserHandle);
        intent.putExtra("account_setup_wizard", getIntent().getBooleanExtra("account_setup_wizard", false));
        String stringExtra = getIntent().getStringExtra("selected_account");
        if (TextUtils.isEmpty(stringExtra)) {
            startActivityForResult(intent, 1);
        } else {
            addAccount(stringExtra);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1) {
            if (i2 != 0) {
                addAccount(intent.getStringExtra("selected_account"));
                return;
            }
            if (intent != null) {
                startActivityAsUser(intent, this.mUserHandle);
            }
            setResult(i2);
            finish();
        } else if (i != 2) {
            if (i != 3) {
                return;
            }
            if (i2 == -1) {
                requestChooseAccount();
            } else {
                finish();
            }
        } else {
            if (getIntent().getBooleanExtra("account_setup_wizard", false)) {
                i2 = -1;
            }
            setResult(i2);
            PendingIntent pendingIntent = this.mPendingIntent;
            if (pendingIntent != null) {
                pendingIntent.cancel();
                this.mPendingIntent = null;
            }
            finish();
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mAddAccountCalled = bundle.getBoolean("AddAccountCalled");
            if (Log.isLoggable("AddAccountSettings", 2)) {
                Log.v("AddAccountSettings", ExtraTelephony.MmsSms.INSERT_PATH_RESTORED);
            }
        }
        UserManager userManager = (UserManager) getSystemService("user");
        UserHandle secureTargetUser = Utils.getSecureTargetUser(getActivityToken(), userManager, null, getIntent().getExtras());
        this.mUserHandle = secureTargetUser;
        if (userManager.hasUserRestriction("no_modify_accounts", secureTargetUser)) {
            Toast.makeText(this, R.string.user_cannot_add_accounts_message, 1).show();
            finish();
        } else if (this.mAddAccountCalled) {
            finish();
        } else if (Utils.startQuietModeDialogIfNecessary(this, userManager, this.mUserHandle.getIdentifier())) {
            finish();
        } else if (userManager.isUserUnlocked(this.mUserHandle)) {
            requestChooseAccount();
        } else if (new ChooseLockSettingsHelper.Builder(this).setRequestCode(3).setTitle(getString(R.string.unlock_set_unlock_launch_picker_title)).setUserId(this.mUserHandle.getIdentifier()).show()) {
        } else {
            requestChooseAccount();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("AddAccountCalled", this.mAddAccountCalled);
        if (Log.isLoggable("AddAccountSettings", 2)) {
            Log.v("AddAccountSettings", "saved");
        }
    }
}
