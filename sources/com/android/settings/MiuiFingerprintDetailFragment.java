package com.android.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.security.FingerprintIdUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.settings.utils.FingerprintUtils;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiFingerprintDetailFragment extends BaseEditFragment {
    private Button mDeleteBtn;
    private EditText mEditText;
    private boolean mFingerprintDeleted = false;
    private String mFingerprintKey;
    private String mFingerprintTitle;
    private View mView;

    private void initView() {
        this.mEditText = (EditText) this.mView.findViewById(R.id.fingerprint_title_edit_text);
        this.mDeleteBtn = (Button) this.mView.findViewById(R.id.fingerprint_delete);
        this.mEditText.setText(this.mFingerprintTitle);
        EditText editText = this.mEditText;
        editText.setSelection(editText.getText().length(), this.mEditText.getText().length());
        this.mDeleteBtn.setTextColor(-65536);
        final FingerprintRemoveCallback fingerprintRemoveCallback = new FingerprintRemoveCallback() { // from class: com.android.settings.MiuiFingerprintDetailFragment.1
            @Override // com.android.settings.FingerprintRemoveCallback
            public void onFailed() {
                if (MiuiFingerprintDetailFragment.this.getActivity() != null) {
                    Toast.makeText(MiuiFingerprintDetailFragment.this.getActivity(), R.string.fingerprint_removal_failed, 0).show();
                    MiuiFingerprintDetailFragment.this.getActivity().setResult(0);
                    MiuiFingerprintDetailFragment.this.mFingerprintDeleted = true;
                    MiuiFingerprintDetailFragment.this.finish();
                }
            }

            @Override // com.android.settings.FingerprintRemoveCallback
            public void onRemoved() {
                if (MiuiFingerprintDetailFragment.this.getActivity() != null) {
                    FingerprintUtils.removeFingerprintData(MiuiFingerprintDetailFragment.this.getActivity(), MiuiFingerprintDetailFragment.this.mFingerprintKey);
                    MiuiFingerprintDetailFragment.this.mFingerprintDeleted = true;
                    FingerprintIdUtils.deleteFingerprintById(MiuiFingerprintDetailFragment.this.getActivity(), MiuiFingerprintDetailFragment.this.mFingerprintKey);
                    MiuiFingerprintDetailFragment.this.getActivity().setResult(-1);
                    MiuiFingerprintDetailFragment.this.finish();
                }
            }
        };
        final DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiFingerprintDetailFragment.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    new FingerprintHelper(MiuiFingerprintDetailFragment.this.getActivity()).removeFingerprint(MiuiFingerprintDetailFragment.this.mFingerprintKey, fingerprintRemoveCallback);
                }
                dialogInterface.dismiss();
            }
        };
        this.mDeleteBtn.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.MiuiFingerprintDetailFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AlertDialog create = new AlertDialog.Builder(MiuiFingerprintDetailFragment.this.getActivity()).create();
                create.setCancelable(false);
                create.setCanceledOnTouchOutside(false);
                create.setTitle(MiuiFingerprintDetailFragment.this.getString(R.string.delete_fingerprint_confirm_title));
                create.setMessage(MiuiFingerprintDetailFragment.this.getString(R.string.delete_fingerprint_confirm_msg));
                create.setButton(-2, MiuiFingerprintDetailFragment.this.getString(17039360), onClickListener);
                create.setButton(-1, MiuiFingerprintDetailFragment.this.getString(17039370), onClickListener);
                create.show();
            }
        });
    }

    private void saveFingerprintTitle() {
        String obj = this.mEditText.getText().toString();
        if (TextUtils.isEmpty(obj) || this.mFingerprintDeleted || getActivity() == null) {
            return;
        }
        FingerprintUtils.setFingerprintName(getActivity(), this.mFingerprintKey, obj);
    }

    @Override // com.android.settings.BaseEditFragment
    public String getTitle() {
        return getString(R.string.fingerprint_list_title);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mFingerprintKey = arguments.getString("extra_fingerprint_key");
            this.mFingerprintTitle = arguments.getString("extra_fingerprint_title");
        }
        if (MiuiKeyguardSettingsUtils.isInFullWindowGestureMode(getActivity().getApplicationContext())) {
            getActivity().getWindow().clearFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
        }
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onInflateView(layoutInflater, viewGroup, bundle);
        this.mView = layoutInflater.inflate(R.layout.fingerprint_detail, viewGroup, false);
        initView();
        return this.mView;
    }

    @Override // com.android.settings.BaseEditFragment
    public void onSave() {
        saveFingerprintTitle();
        super.onSave();
    }
}
