package com.android.settings.bluetooth;

import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import com.android.settings.R;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class RequestPermissionHelperActivity extends AppCompatActivity implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private CharSequence mAppLabel;
    private String mApplicationName;
    private BluetoothAdapter mBluetoothAdapter;
    private AlertDialog mDialog;
    private int mRequest;
    private int mTimeout = -1;

    private boolean parseIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return false;
        }
        this.mApplicationName = intent.getStringExtra("ApplicationName");
        String action = intent.getAction();
        if ("com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_ON".equals(action)) {
            this.mRequest = 1;
            if (intent.hasExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION")) {
                this.mTimeout = intent.getIntExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", 120);
            }
        } else if (!"com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_OFF".equals(action)) {
            return false;
        } else {
            this.mRequest = 3;
        }
        this.mAppLabel = getIntent().getCharSequenceExtra("com.android.settings.bluetooth.extra.APP_LABEL");
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        if (defaultAdapter == null) {
            Log.e("RequestPermissionHelperActivity", "Error: there's a problem starting Bluetooth");
            return false;
        }
        return true;
    }

    void createDialog() {
        String string;
        String string2;
        String string3;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int i = this.mRequest;
        if (i == 1) {
            int i2 = this.mTimeout;
            if (i2 < 0) {
                if (this.mApplicationName != null) {
                    if (this.mAppLabel != null) {
                        string3 = this.mApplicationName + getString(R.string.bluetooth_ask_enablement_detected);
                    } else {
                        string3 = getString(R.string.bluetooth_ask_enablement_no_name);
                    }
                    builder.setMessage(string3);
                } else {
                    CharSequence charSequence = this.mAppLabel;
                    builder.setMessage(charSequence != null ? getString(R.string.bluetooth_ask_enablement, new Object[]{charSequence}) : getString(R.string.bluetooth_ask_enablement_no_name));
                }
            } else if (i2 == 0) {
                if (this.mApplicationName != null) {
                    if (this.mAppLabel != null) {
                        string2 = this.mApplicationName + getString(R.string.bluetooth_ask_enablement_and_lasting_discovery_detected);
                    } else {
                        string2 = getString(R.string.bluetooth_ask_enablement_and_lasting_discovery_no_name);
                    }
                    builder.setMessage(string2);
                } else {
                    CharSequence charSequence2 = this.mAppLabel;
                    builder.setMessage(charSequence2 != null ? getString(R.string.bluetooth_ask_enablement_and_lasting_discovery, new Object[]{charSequence2}) : getString(R.string.bluetooth_ask_enablement_and_lasting_discovery_no_name));
                }
            } else if (this.mApplicationName != null) {
                if (this.mAppLabel != null) {
                    string = this.mApplicationName + getString(R.string.bluetooth_ask_enablement_and_discovery_detected, new Object[]{Integer.valueOf(this.mTimeout)});
                } else {
                    string = getString(R.string.bluetooth_ask_enablement_and_discovery_no_name, new Object[]{Integer.valueOf(i2)});
                }
                builder.setMessage(string);
            } else {
                CharSequence charSequence3 = this.mAppLabel;
                builder.setMessage(charSequence3 != null ? getString(R.string.bluetooth_ask_enablement_and_discovery, new Object[]{charSequence3, Integer.valueOf(i2)}) : getString(R.string.bluetooth_ask_enablement_and_discovery_no_name, new Object[]{Integer.valueOf(i2)}));
            }
        } else if (i == 3) {
            CharSequence charSequence4 = this.mAppLabel;
            builder.setMessage(charSequence4 != null ? getString(R.string.bluetooth_ask_disablement, new Object[]{charSequence4}) : getString(R.string.bluetooth_ask_disablement_no_name));
        }
        builder.setPositiveButton(getString(R.string.allow), this);
        builder.setNegativeButton(getString(R.string.deny), (DialogInterface.OnClickListener) null);
        builder.setOnDismissListener(this);
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        int i2 = this.mRequest;
        if (i2 != 1 && i2 != 2) {
            if (i2 != 3) {
                return;
            }
            this.mBluetoothAdapter.disable();
            setResult(-1);
        } else if (!((UserManager) getSystemService(UserManager.class)).hasUserRestriction("no_bluetooth")) {
            this.mBluetoothAdapter.enable();
            setResult(-1);
        } else {
            Intent createAdminSupportIntent = ((DevicePolicyManager) getSystemService(DevicePolicyManager.class)).createAdminSupportIntent("no_bluetooth");
            if (createAdminSupportIntent != null) {
                startActivity(createAdminSupportIntent);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setResult(0);
        if (!parseIntent()) {
            finish();
        } else if (!getResources().getBoolean(R.bool.auto_confirm_bluetooth_activation_dialog)) {
            createDialog();
        } else {
            onClick(null, -1);
            finish();
        }
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        finish();
    }
}
