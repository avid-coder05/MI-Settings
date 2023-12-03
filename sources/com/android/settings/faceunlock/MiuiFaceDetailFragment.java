package com.android.settings.faceunlock;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.settings.BaseEditFragment;
import com.android.settings.MiuiKeyguardSettingsUtils;
import com.android.settings.R;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiFaceDetailFragment extends BaseEditFragment {
    private Button mDeleteBtn;
    private EditText mEditText;
    private boolean mFaceDeleted = false;
    private String mFaceKey;
    private String mFaceTitle;
    private View mView;

    private void initView() {
        EditText editText = (EditText) this.mView.findViewById(R.id.face_title_edit_text);
        this.mEditText = editText;
        editText.setText(this.mFaceTitle);
        EditText editText2 = this.mEditText;
        editText2.setSelection(editText2.getText().length(), this.mEditText.getText().length());
        Button button = (Button) this.mView.findViewById(R.id.face_delete);
        this.mDeleteBtn = button;
        button.setTextColor(-65536);
        final FaceRemoveCallback faceRemoveCallback = new FaceRemoveCallback() { // from class: com.android.settings.faceunlock.MiuiFaceDetailFragment.1
            @Override // com.android.settings.faceunlock.FaceRemoveCallback
            public void onFailed() {
                Toast.makeText(MiuiFaceDetailFragment.this.getAppCompatActivity(), R.string.structure_face_data_delete_fail, 0);
                MiuiFaceDetailFragment.this.finish();
            }

            @Override // com.android.settings.faceunlock.FaceRemoveCallback
            public void onRemoved() {
                MiuiFaceDetailFragment.this.mFaceDeleted = true;
                KeyguardSettingsFaceUnlockUtils.removeFaceData(MiuiFaceDetailFragment.this.getAppCompatActivity(), MiuiFaceDetailFragment.this.mFaceKey);
                MiuiFaceDetailFragment.this.finish();
            }
        };
        final DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.faceunlock.MiuiFaceDetailFragment.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    KeyguardSettingsFaceUnlockManager.getInstance(MiuiFaceDetailFragment.this.getAppCompatActivity()).deleteFeature(MiuiFaceDetailFragment.this.mFaceKey, faceRemoveCallback);
                }
                dialogInterface.dismiss();
            }
        };
        this.mDeleteBtn.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.faceunlock.MiuiFaceDetailFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AlertDialog create = new AlertDialog.Builder(MiuiFaceDetailFragment.this.getAppCompatActivity()).create();
                create.setCancelable(false);
                create.setCanceledOnTouchOutside(false);
                create.setTitle(MiuiFaceDetailFragment.this.getString(R.string.multi_face_delete_message));
                create.setMessage(MiuiFaceDetailFragment.this.getString(R.string.multi_face_delete_show_message));
                create.setButton(-2, MiuiFaceDetailFragment.this.getString(17039360), onClickListener);
                create.setButton(-1, MiuiFaceDetailFragment.this.getString(17039370), onClickListener);
                create.show();
            }
        });
        this.mEditText.addTextChangedListener(new TextWatcher() { // from class: com.android.settings.faceunlock.MiuiFaceDetailFragment.4
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(MiuiFaceDetailFragment.this.mEditText.getText().toString())) {
                    MiuiFaceDetailFragment.this.mEditText.setHint(MiuiFaceDetailFragment.this.mFaceTitle);
                }
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
    }

    private void saveFaceName() {
        String obj = this.mEditText.getText().toString();
        if (TextUtils.isEmpty(obj) || this.mFaceDeleted || getAppCompatActivity() == null) {
            return;
        }
        KeyguardSettingsFaceUnlockUtils.setFaceDataName(getAppCompatActivity(), this.mFaceKey, obj);
    }

    @Override // com.android.settings.BaseEditFragment
    public String getTitle() {
        return getString(R.string.multi_face_list);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mFaceKey = arguments.getString("extra_face_key");
            this.mFaceTitle = arguments.getString("extra_face_title");
        }
        if (MiuiKeyguardSettingsUtils.isInFullWindowGestureMode(getAppCompatActivity().getApplicationContext())) {
            getAppCompatActivity().getWindow().clearFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
        }
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onInflateView(layoutInflater, viewGroup, bundle);
        this.mView = layoutInflater.inflate(R.layout.miui_face_detail_info, viewGroup, false);
        initView();
        return this.mView;
    }

    @Override // com.android.settings.BaseEditFragment
    public void onSave() {
        saveFaceName();
        super.onSave();
    }
}
