package com.android.settings.development;

import android.app.backup.IBackupManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.BaseEditFragment;
import com.android.settings.R;
import com.android.settingslib.util.ToastUtil;
import miui.provider.ExtraContacts;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiSetFullBackupPasswordFragment extends BaseEditFragment {
    private String TAG = "MiuiSetFullBackupPasswordFragment";
    private TextView cancelText;
    private TextView confirBtn;
    IBackupManager mBackupManager;
    private EditText mConfirmNewPw;
    private LinearLayout mCurrentLayout;
    private EditText mCurrentPw;
    int mCurrestStatus;
    private EditText mNewPw;
    private LinearLayout mResetLayout;
    private TextView modifyText;

    private void saveClearPwStatus() {
        if (!setBackupPassword(this.mCurrentPw.getText().toString(), "")) {
            Log.i(this.TAG, "failure; password mismatch?");
            ToastUtil.show(getContext(), R.string.backup_set_error_pw, 1);
            return;
        }
        Log.i(this.TAG, "password set successfully");
        ToastUtil.show(getContext(), R.string.backup_set_clear_success, 1);
        finish();
    }

    private void saveModePwStatus() {
        String obj = this.mCurrentPw.getText().toString();
        String obj2 = this.mNewPw.getText().toString();
        if (!obj2.equals(this.mConfirmNewPw.getText().toString())) {
            ToastUtil.show(getContext(), R.string.local_backup_password_toast_confirmation_mismatch, 1);
        } else if (TextUtils.isEmpty(obj2)) {
            ToastUtil.show(getContext(), R.string.backup_set_null_pw, 1);
        } else if (!setBackupPassword(obj, obj2)) {
            ToastUtil.show(getContext(), R.string.backup_set_error_pw, 1);
        } else {
            ToastUtil.show(getContext(), R.string.local_backup_password_toast_success, 1);
            finish();
        }
    }

    private void saveNoPwStatus() {
        final String obj = this.mNewPw.getText().toString();
        if (!obj.equals(this.mConfirmNewPw.getText().toString())) {
            ToastUtil.show(getContext(), R.string.local_backup_password_toast_confirmation_mismatch, 1);
        } else if (TextUtils.isEmpty(obj)) {
            ToastUtil.show(getContext(), R.string.backup_set_null_pw, 1);
        } else {
            if (Settings.System.getInt(getContext().getContentResolver(), "local_auto_backup", 0) == 1) {
                new AlertDialog.Builder(getContext()).setTitle(getString(R.string.backup_set_new_pw_confirm_hint)).setMessage(getString(R.string.backup_set_new_pw_confirm_summary)).setCancelable(true).setPositiveButton(R.string.backup_set_new_exit, new DialogInterface.OnClickListener() { // from class: com.android.settings.development.MiuiSetFullBackupPasswordFragment.4
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MiuiSetFullBackupPasswordFragment.this.finish();
                    }
                }).setNegativeButton(R.string.backup_set_new_continue, new DialogInterface.OnClickListener() { // from class: com.android.settings.development.MiuiSetFullBackupPasswordFragment.3
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!MiuiSetFullBackupPasswordFragment.this.setBackupPassword("", obj)) {
                            ToastUtil.show(MiuiSetFullBackupPasswordFragment.this.getContext(), R.string.local_backup_password_toast_validation_failure, 1);
                            return;
                        }
                        ToastUtil.show(MiuiSetFullBackupPasswordFragment.this.getContext(), R.string.local_backup_password_toast_success, 1);
                        MiuiSetFullBackupPasswordFragment.this.finish();
                    }
                }).create().show();
            } else if (!setBackupPassword("", obj)) {
                ToastUtil.show(getContext(), R.string.local_backup_password_toast_validation_failure, 1);
            } else {
                ToastUtil.show(getContext(), R.string.local_backup_password_toast_success, 1);
                finish();
            }
        }
    }

    private void setActionBarTitle(int i) {
        if (getAppCompatActivity().getAppCompatActionBar() != null) {
            getAppCompatActivity().getAppCompatActionBar().setTitle(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean setBackupPassword(String str, String str2) {
        try {
            return this.mBackupManager.setBackupPassword(str, str2);
        } catch (RemoteException unused) {
            Log.e(this.TAG, "Unable to communicate with backup manager");
            return false;
        }
    }

    private void setText(EditText editText, String str) {
        if (str != null) {
            editText.setHint(str);
        }
    }

    private void setVisibilty(int i, int i2, int i3, int i4) {
        this.mResetLayout.setVisibility(i);
        this.mCurrentLayout.setVisibility(i2);
        if (i2 == 0) {
            this.mCurrentPw.setVisibility(i3);
            this.mNewPw.setVisibility(i4);
            this.mConfirmNewPw.setVisibility(i4);
        }
    }

    private void sethintText(String str, String str2, String str3) {
        setText(this.mCurrentPw, str);
        setText(this.mNewPw, str2);
        setText(this.mConfirmNewPw, str3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateViews() {
        int i = this.mCurrestStatus;
        if (i == 1) {
            setActionBarTitle(R.string.local_backup_password_title);
            setVisibilty(8, 0, 8, 0);
            sethintText(null, getString(R.string.backup_set_new_pw_title), getString(R.string.backup_set_pw_confirm_title));
        } else if (i == 2) {
            setActionBarTitle(R.string.local_backup_password_title);
            setVisibilty(0, 8, 8, 8);
            this.confirBtn.setVisibility(8);
            this.cancelText.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.development.MiuiSetFullBackupPasswordFragment.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    MiuiSetFullBackupPasswordFragment miuiSetFullBackupPasswordFragment = MiuiSetFullBackupPasswordFragment.this;
                    miuiSetFullBackupPasswordFragment.mCurrestStatus = 4;
                    miuiSetFullBackupPasswordFragment.updateViews();
                }
            });
            this.modifyText.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.development.MiuiSetFullBackupPasswordFragment.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    MiuiSetFullBackupPasswordFragment miuiSetFullBackupPasswordFragment = MiuiSetFullBackupPasswordFragment.this;
                    miuiSetFullBackupPasswordFragment.mCurrestStatus = 3;
                    miuiSetFullBackupPasswordFragment.updateViews();
                }
            });
        } else if (i == 3) {
            setActionBarTitle(R.string.backup_set_modify_title);
            this.confirBtn.setVisibility(0);
            setVisibilty(8, 0, 0, 0);
            sethintText(getString(R.string.backup_set_enter_old_pw), getString(R.string.backup_set_enter_new_pw), getString(R.string.backup_set_enter_new_pw_again));
        } else if (i != 4) {
        } else {
            setActionBarTitle(R.string.backup_set_clear_title);
            this.confirBtn.setVisibility(0);
            setVisibilty(8, 0, 0, 8);
            sethintText(getString(R.string.current_backup_pw_prompt), null, null);
        }
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onInflateView(layoutInflater, viewGroup, bundle);
        return layoutInflater.inflate(R.layout.set_backup_pw, viewGroup, false);
    }

    @Override // com.android.settings.BaseEditFragment
    public void onSave() {
        int i = this.mCurrestStatus;
        if (i == 1) {
            saveNoPwStatus();
        } else if (i == 3) {
            saveModePwStatus();
        } else if (i != 4) {
        } else {
            saveClearPwStatus();
        }
    }

    @Override // com.android.settings.BaseEditFragment, com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        ActionBar appCompatActionBar = getAppCompatActivity().getAppCompatActionBar();
        if (appCompatActionBar != null) {
            this.confirBtn = (TextView) appCompatActionBar.getCustomView().findViewById(16908314);
        }
        updateViews();
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        IBackupManager asInterface = IBackupManager.Stub.asInterface(ServiceManager.getService(ExtraContacts.Calls.BACKUP_PARAM));
        this.mBackupManager = asInterface;
        try {
            this.mCurrestStatus = asInterface.hasBackupPassword() ? 2 : 1;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.mCurrentLayout = (LinearLayout) view.findViewById(R.id.current_backup_pw_layout);
        this.mResetLayout = (LinearLayout) view.findViewById(R.id.reset_backup_pw_layout);
        this.mCurrentPw = (EditText) view.findViewById(R.id.current_backup_pw);
        this.mNewPw = (EditText) view.findViewById(R.id.new_backup_pw);
        this.mConfirmNewPw = (EditText) view.findViewById(R.id.confirm_backup_pw);
        this.cancelText = (TextView) view.findViewById(R.id.cancel_backup_pw);
        this.modifyText = (TextView) view.findViewById(R.id.modify_backup_pw);
    }
}
