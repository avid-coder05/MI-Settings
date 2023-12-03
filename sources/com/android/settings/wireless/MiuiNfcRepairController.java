package com.android.settings.wireless;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.UserHandle;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.core.lifecycle.events.OnSaveInstanceState;
import com.miui.enterprise.RestrictionsHelper;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.ProgressDialog;

/* loaded from: classes2.dex */
public class MiuiNfcRepairController extends AbstractPreferenceController implements DialogInterface.OnDismissListener, Preference.OnPreferenceClickListener, LifecycleObserver, OnCreate, OnSaveInstanceState, OnResume, OnPause, OnDestroy {
    DialogInterface.OnClickListener mActionListener;
    private Dialog mDialog;
    Handler mHandler;
    DialogInterface.OnClickListener mInquiryListener;
    DialogInterface.OnClickListener mRebootListener;
    private final BroadcastReceiver mReceiver;
    private boolean mRecovery;
    private Preference mRepairPref;
    private long mStartTime;
    private int mState;
    private Context mUiContext;
    private int mWhichBtn;

    public MiuiNfcRepairController(Context context, Lifecycle lifecycle) {
        super(context);
        this.mStartTime = 0L;
        this.mState = 0;
        this.mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.wireless.MiuiNfcRepairController.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                Log.i("MiuiNfcRepairController", "handleMessage: " + message.what);
                MiuiNfcRepairController.this.mHandler.removeMessages(1);
                if (message.what != 0) {
                    MiuiNfcRepairController.this.updateState(5);
                } else {
                    MiuiNfcRepairController.this.updateState(4);
                }
                if (MiuiNfcRepairController.this.mDialog != null) {
                    MiuiNfcRepairController.this.mWhichBtn = -1;
                    MiuiNfcRepairController.this.mDialog.dismiss();
                    MiuiNfcRepairController.this.mDialog = null;
                }
            }
        };
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wireless.MiuiNfcRepairController.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("com.android.nfc.action.repair.rsp".equals(intent.getAction())) {
                    ((AbstractPreferenceController) MiuiNfcRepairController.this).mContext.unregisterReceiver(MiuiNfcRepairController.this.mReceiver);
                    MiuiNfcRepairController.this.mHandler.sendEmptyMessage(!intent.getBooleanExtra("success", false));
                }
            }
        };
        this.mWhichBtn = -2;
        this.mRebootListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.wireless.MiuiNfcRepairController$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                MiuiNfcRepairController.this.lambda$new$0(dialogInterface, i);
            }
        };
        this.mActionListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.wireless.MiuiNfcRepairController$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                MiuiNfcRepairController.this.lambda$new$1(dialogInterface, i);
            }
        };
        this.mInquiryListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.wireless.MiuiNfcRepairController$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                MiuiNfcRepairController.this.lambda$new$2(dialogInterface, i);
            }
        };
        lifecycle.addObserver(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(DialogInterface dialogInterface, int i) {
        updateState(0);
        if (i == -1) {
            this.mWhichBtn = i;
            rebootPhone();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(DialogInterface dialogInterface, int i) {
        if (i != -1) {
            updateState(0);
            return;
        }
        this.mWhichBtn = i;
        updateState(3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(DialogInterface dialogInterface, int i) {
        if (i != -1) {
            updateState(0);
            return;
        }
        this.mWhichBtn = i;
        updateState(2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateState(int i) {
        Log.d("MiuiNfcRepairController", "updateState: old state=" + this.mState + ", new state=" + i);
        this.mState = i;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Log.d("MiuiNfcRepairController", "displayPreference: " + this);
        Preference findPreference = preferenceScreen.findPreference("nfc_repair");
        this.mRepairPref = findPreference;
        if (findPreference != null) {
            if (!SettingsFeatures.hasNfcRepairFeature(this.mContext)) {
                preferenceScreen.removePreference(this.mRepairPref);
                return;
            }
            this.mUiContext = this.mRepairPref.getContext();
            this.mRepairPref.setOnPreferenceClickListener(this);
            if (RestrictionsHelper.hasNFCRestriction(this.mContext)) {
                this.mRepairPref.setEnabled(false);
            }
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "nfc_repair";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !SettingsFeatures.isNeedShowMiuiNFC() && SettingsFeatures.hasNfcRepairFeature(this.mContext);
    }

    void nextDialogShow() {
        int i;
        String format;
        DialogInterface.OnClickListener onClickListener;
        int i2;
        int i3;
        int i4;
        int i5;
        DialogInterface.OnClickListener onClickListener2;
        this.mWhichBtn = -2;
        Log.d("MiuiNfcRepairController", "nextDialogShow: " + this.mState);
        int i6 = this.mState;
        int i7 = 17039360;
        if (i6 != 1) {
            if (i6 == 2) {
                i = R.string.nfc_repair_title;
                i4 = R.string.nfc_repair_action_desc;
                i5 = R.string.nfc_repair_action_btn;
                onClickListener2 = this.mActionListener;
            } else if (i6 == 3) {
                repairNFC();
                ProgressDialog progressDialog = new ProgressDialog(this.mUiContext);
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage(this.mContext.getResources().getText(R.string.nfc_repair_ongoing));
                progressDialog.setOnDismissListener(this);
                progressDialog.show();
                this.mDialog = progressDialog;
                return;
            } else if (i6 == 4) {
                i = R.string.nfc_repair_pass_title;
                i4 = R.string.nfc_repair_pass_desc;
                i5 = R.string.nfc_repair_btn_reboot;
                i7 = R.string.nfc_repair_btn_reboot_later;
                onClickListener2 = this.mRebootListener;
            } else if (i6 != 5) {
                return;
            } else {
                i = R.string.nfc_repair_fail_title;
                i4 = R.string.nfc_repair_fail_desc;
                i5 = R.string.nfc_repair_btn_retry;
                onClickListener2 = this.mActionListener;
            }
            onClickListener = onClickListener2;
            i3 = i5;
            i2 = i4;
            format = null;
        } else {
            i = R.string.nfc_repair_title;
            int i8 = R.string.nfc_repair_confirm_desc;
            format = String.format(this.mContext.getString(i8, 1, 2, 3), new Object[0]);
            int i9 = R.string.nfc_repair_btn_yes;
            int i10 = R.string.nfc_repair_btn_no;
            onClickListener = this.mInquiryListener;
            i2 = i8;
            i7 = i10;
            i3 = i9;
        }
        AlertDialog.Builder onDismissListener = new AlertDialog.Builder(this.mUiContext).setTitle(i).setPositiveButton(i3, onClickListener).setNegativeButton(i7, (DialogInterface.OnClickListener) null).setOnDismissListener(this);
        if (format == null) {
            onDismissListener.setMessage(i2);
        } else {
            onDismissListener.setMessage(format);
        }
        this.mDialog = onDismissListener.show();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnCreate
    public void onCreate(Bundle bundle) {
        if (bundle != null) {
            this.mRecovery = true;
            updateState(bundle.getInt("RepairState", 0));
            this.mStartTime = bundle.getLong("RepairStart", 0L);
        }
        Log.d("MiuiNfcRepairController", "onCreate: " + this.mState);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        Log.d("MiuiNfcRepairController", "onDestroy: " + this);
        if (this.mState == 3) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.mHandler.removeMessages(1);
        }
        Dialog dialog = this.mDialog;
        if (dialog != null) {
            dialog.setOnDismissListener(null);
            this.mDialog.dismiss();
            this.mDialog = null;
        }
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        this.mDialog = null;
        if (this.mWhichBtn != -1) {
            updateState(0);
        }
        nextDialogShow();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        Log.d("MiuiNfcRepairController", "trigger inquiry");
        if (Utils.isMonkeyRunning()) {
            Log.i("MiuiNfcRepairController", "Ingore, Monkey running...");
            return true;
        }
        updateState(1);
        nextDialogShow();
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (this.mRecovery) {
            nextDialogShow();
            this.mRecovery = false;
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnSaveInstanceState
    public void onSaveInstanceState(Bundle bundle) {
        Log.i("MiuiNfcRepairController", "onSaveInstanceState: " + this.mState + ", StartTime=" + this.mStartTime);
        bundle.putInt("RepairState", this.mState);
        bundle.putLong("RepairStart", this.mStartTime);
    }

    void rebootPhone() {
        try {
            Log.d("MiuiNfcRepairController", "rebootPhone: " + this.mState);
            ((PowerManager) this.mContext.getSystemService("power")).reboot("OneTouchRepair...");
        } catch (Exception e) {
            Log.e("MiuiNfcRepairController", "RebootPhone fail: " + e);
        }
    }

    void repairNFC() {
        Log.d("MiuiNfcRepairController", "repairNFC: " + this.mState);
        long j = 0;
        try {
            this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, new IntentFilter("com.android.nfc.action.repair.rsp"), "android.permission.WRITE_SECURE_SETTINGS", null);
            if (this.mRecovery) {
                j = System.currentTimeMillis() - this.mStartTime;
            } else {
                Intent intent = new Intent("com.android.nfc.action.repair.req");
                intent.setPackage("com.android.nfc");
                this.mContext.sendBroadcast(intent);
                this.mStartTime = System.currentTimeMillis();
            }
            this.mHandler.sendEmptyMessageDelayed(1, 30000 - j);
        } catch (Exception e) {
            Log.e("MiuiNfcRepairController", "RepairNFC fail: " + e);
            this.mHandler.sendEmptyMessage(1);
        }
    }
}
