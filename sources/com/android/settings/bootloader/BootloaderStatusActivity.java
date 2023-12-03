package com.android.settings.bootloader;

import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.R;
import com.android.settings.bootloader.Utils;
import com.android.settings.search.provider.SettingsProvider;
import java.util.Locale;
import miui.os.Build;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class BootloaderStatusActivity extends AppCompatActivity implements AccountManagerCallback<Bundle> {
    private static final String TAG = BootloaderStatusActivity.class.getSimpleName();
    private Button mBtn;
    private boolean mIsFirst = true;
    private boolean mHasRefreshToken = false;
    private Toast mToast = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class BindTask extends AsyncTask<Context, Void, Utils.RetType> {
        BindTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Utils.RetType doInBackground(Context... contextArr) {
            Context context = contextArr[0];
            Utils.RetType retType = new Utils.RetType();
            try {
                return CloudDeviceStatus.bindAccountWithDevice(context);
            } catch (Utils.AccountExcepiton unused) {
                retType.retCode = 1;
                return retType;
            } catch (Exception unused2) {
                retType.retCode = 3;
                return retType;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Utils.RetType retType) {
            BootloaderStatusActivity.this.mBtn.setEnabled(true);
            BootloaderStatusActivity.this.mBtn.setTextColor(-872415232);
            BootloaderStatusActivity.this.notifyUser(retType.retCode, retType.retMsg);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bindAccountWithDevice() {
        if (Utils.needSimCard() && 1 == Utils.getSimState(this)) {
            notifyUser(7, null);
        } else if (!Utils.isAccountLogined(this)) {
            if (!this.mIsFirst) {
                notifyUser(1, null);
                return;
            }
            this.mIsFirst = false;
            Utils.addAccount(this, this);
        } else if (!Utils.isNetworkConnected(this)) {
            notifyUser(2, null);
        } else if (Utils.needSimCard() && !Utils.isMobileConnected(this)) {
            notifyUser(11, null);
        } else {
            this.mBtn.setEnabled(false);
            this.mBtn.setTextColor(863010960);
            notifyUser(6, null);
            new BindTask().execute(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyUser(int i, String str) {
        if (!this.mIsFirst) {
            this.mIsFirst = true;
        }
        Toast toast = this.mToast;
        if (toast != null) {
            toast.cancel();
        }
        int i2 = R.string.bootloader_device_bind_fail;
        if (i == 1) {
            i2 = R.string.bootloader_device_bind_no_account;
        } else if (i == 2) {
            i2 = R.string.bootloader_device_bind_no_network;
        } else if (i != 3 && i != 5) {
            if (i == 6) {
                i2 = R.string.bootloader_device_bind_msg;
            } else if (i == 4) {
                Toast makeText = Toast.makeText(getApplicationContext(), str, 1);
                this.mToast = makeText;
                makeText.show();
                return;
            } else if (i == 7) {
                i2 = R.string.bootloader_device_bind_no_sim;
            } else if (i == 8) {
                i2 = R.string.bootloader_device_bind_sim_invalid;
            } else if (i == 9) {
                i2 = R.string.bootloader_device_bind_exceed_sim_limit;
            } else if (i == 11) {
                i2 = R.string.bootloader_device_bind_no_data_network;
            } else if (i == 401) {
                if (this.mHasRefreshToken) {
                    i2 = R.string.bootloader_device_bind_server_token_expired_2;
                } else {
                    i2 = R.string.bootloader_device_bind_server_token_expired_1;
                    this.mHasRefreshToken = true;
                }
            } else if (i == 10) {
                Toast makeText2 = Toast.makeText(getApplicationContext(), getString(R.string.bootloader_device_bind_server_code) + str, 1);
                this.mToast = makeText2;
                makeText2.show();
                return;
            } else if (i == 0) {
                i2 = R.string.bootloader_device_bind_already;
            }
        }
        Toast makeText3 = Toast.makeText(getApplicationContext(), i2, 1);
        this.mToast = makeText3;
        makeText3.show();
    }

    private boolean privacyEnabled(Context context) {
        if (Build.IS_INTERNATIONAL_BUILD) {
            try {
                return MiuiSettings.Privacy.isEnabled(context, context.getPackageName());
            } catch (Exception e) {
                Log.e(TAG, "get privacy status error: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    private void showPrivacyDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.bootloader_status_privacy_dialog_title).setMessage(Html.fromHtml(getString(R.string.bootloader_status_privacy_dialog_message, new Object[]{String.format("https://privacy.mi.com/all/%s_%s", Locale.getDefault().getLanguage(), Build.getRegion())}))).setCancelable(false).setPositiveButton(R.string.bootloader_status_privacy_dialog_yes, new DialogInterface.OnClickListener() { // from class: com.android.settings.bootloader.BootloaderStatusActivity.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).setNegativeButton(R.string.bootloader_status_privacy_dialog_no, new DialogInterface.OnClickListener() { // from class: com.android.settings.bootloader.BootloaderStatusActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                BootloaderStatusActivity.this.finish();
            }
        }).show().getMessageView().setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1 && i2 != -1) {
            finish();
        }
        super.onActivityResult(i, i2, intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!"locked".equals(SystemProperties.get("ro.secureboot.lockstate", (String) null))) {
            setContentView(R.layout.bootloader_status_unlocked);
            return;
        }
        setContentView(R.layout.bootloader_status_locked);
        ((TextView) findViewById(R.id.answer)).setText(Utils.needSimCard() ? R.string.bootloader_locked_answer_2 : R.string.bootloader_locked_answer_2_no_sim);
        Button button = (Button) findViewById(R.id.button);
        this.mBtn = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bootloader.BootloaderStatusActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BootloaderStatusActivity.this.bindAccountWithDevice();
            }
        });
        if (!privacyEnabled(this)) {
            Intent intent = new Intent("miui.intent.action.PRIVACY_AUTHORIZATION_DIALOG");
            intent.putExtra(SettingsProvider.ARGS_KEY, getPackageName());
            startActivityForResult(intent, 1);
        }
        if (Build.IS_INTERNATIONAL_BUILD) {
            showPrivacyDialog();
        }
    }

    @Override // android.accounts.AccountManagerCallback
    public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
        boolean z = false;
        try {
            Bundle result = accountManagerFuture.getResult();
            if (result != null) {
                if (result.getBoolean("booleanResult")) {
                    z = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (z) {
            bindAccountWithDevice();
        } else {
            notifyUser(1, null);
        }
    }
}
