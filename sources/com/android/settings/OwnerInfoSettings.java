package com.android.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.android.internal.widget.LockPatternUtils;

/* loaded from: classes.dex */
public class OwnerInfoSettings extends BaseEditFragment {
    private CheckBox mCheckbox;
    private boolean mIsChanged = false;
    private LockPatternUtils mLockPatternUtils;
    private EditText mNickname;
    private EditText mOwnerInfo;
    private boolean mShowNickname;
    private int mUserId;
    private View mView;

    private void initView() {
        EditText editText = (EditText) this.mView.findViewById(R.id.owner_info_nickname);
        this.mNickname = editText;
        if (this.mShowNickname) {
            editText.setText(UserManager.get(getAppCompatActivity()).getUserName());
            this.mNickname.setSelected(true);
        } else {
            editText.setVisibility(8);
        }
        boolean isOwnerInfoEnabled = this.mLockPatternUtils.isOwnerInfoEnabled(this.mUserId);
        CheckBox checkBox = (CheckBox) this.mView.findViewById(R.id.show_owner_info_on_lockscreen_checkbox);
        this.mCheckbox = checkBox;
        checkBox.setChecked(isOwnerInfoEnabled);
        if (UserHandle.myUserId() != 0) {
            if (UserManager.get(getAppCompatActivity()).isLinkedUser()) {
                this.mCheckbox.setText(R.string.show_profile_info_on_lockscreen_label);
            } else {
                this.mCheckbox.setText(R.string.show_user_info_on_lockscreen_label);
            }
        }
        this.mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.OwnerInfoSettings.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                OwnerInfoSettings.this.mOwnerInfo.setEnabled(z);
                OwnerInfoSettings.this.mIsChanged = true;
                if (z) {
                    ((InputMethodManager) OwnerInfoSettings.this.getAppCompatActivity().getSystemService("input_method")).showSoftInput(OwnerInfoSettings.this.mOwnerInfo, 0);
                }
            }
        });
        String ownerInfo = this.mLockPatternUtils.getOwnerInfo(this.mUserId);
        EditText editText2 = (EditText) this.mView.findViewById(R.id.owner_info_edit_text);
        this.mOwnerInfo = editText2;
        editText2.setEnabled(isOwnerInfoEnabled);
        if (!TextUtils.isEmpty(ownerInfo)) {
            this.mOwnerInfo.setText(ownerInfo);
        }
        this.mOwnerInfo.addTextChangedListener(new TextWatcher() { // from class: com.android.settings.OwnerInfoSettings.2
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                OwnerInfoSettings.this.mIsChanged = true;
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
        if (!isOwnerInfoEnabled) {
            getAppCompatActivity().getWindow().setSoftInputMode(18);
        }
        ((LinearLayout) this.mView.findViewById(R.id.owner_info_line_layout)).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.OwnerInfoSettings.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                OwnerInfoSettings.this.mCheckbox.setChecked(!OwnerInfoSettings.this.mCheckbox.isChecked());
            }
        });
    }

    @Override // com.android.settings.BaseEditFragment
    public String getTitle() {
        return getResources().getString(R.string.owner_info_settings_title);
    }

    @Override // com.android.settings.BaseEditFragment
    public boolean isChanged() {
        return this.mIsChanged;
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey("show_nickname")) {
            return;
        }
        this.mShowNickname = arguments.getBoolean("show_nickname");
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        ((InputMethodManager) getAppCompatActivity().getSystemService("input_method")).hideSoftInputFromWindow(getView().getWindowToken(), 0);
        super.onDestroy();
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mView = layoutInflater.inflate(R.layout.ownerinfo, viewGroup, false);
        this.mUserId = UserHandle.myUserId();
        this.mLockPatternUtils = new LockPatternUtils(getAppCompatActivity());
        initView();
        return this.mView;
    }

    @Override // com.android.settings.BaseEditFragment
    public void onSave(boolean z) {
        saveChanges();
        super.onSave(z);
    }

    void saveChanges() {
        this.mLockPatternUtils.setOwnerInfo(this.mOwnerInfo.getText().toString(), this.mUserId);
        this.mLockPatternUtils.setOwnerInfoEnabled(this.mCheckbox.isChecked(), this.mUserId);
        getAppCompatActivity().sendBroadcast(new Intent("owner_info_changed"));
        if (this.mShowNickname) {
            String userName = UserManager.get(getAppCompatActivity()).getUserName();
            Editable text = this.mNickname.getText();
            if (TextUtils.isEmpty(text) || text.equals(userName)) {
                return;
            }
            UserManager.get(getAppCompatActivity()).setUserName(UserHandle.myUserId(), text.toString());
        }
    }
}
