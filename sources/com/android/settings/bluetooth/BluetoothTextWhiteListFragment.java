package com.android.settings.bluetooth;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.android.settings.BaseEditFragment;
import com.android.settings.R;

/* loaded from: classes.dex */
public class BluetoothTextWhiteListFragment extends BaseEditFragment {
    private Context mContext;
    private EditText mTextWhiteList;

    /* loaded from: classes.dex */
    public static class ListTextWatcher implements TextWatcher {
        public Inputcallback mInputcallback;

        /* loaded from: classes.dex */
        public interface Inputcallback {
            void nullContent(boolean z);
        }

        public ListTextWatcher(Inputcallback inputcallback) {
            this.mInputcallback = inputcallback;
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            if (editable != null) {
                this.mInputcallback.nullContent(editable.toString().length() <= 0);
            }
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    @Override // com.android.settings.BaseFragment
    public View doInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.bluetooth_text_white_list_layout, viewGroup, false);
    }

    @Override // com.android.settings.BaseEditFragment
    public String getTitle() {
        return getResources().getString(R.string.add_text_app_package_name);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getContext();
    }

    @Override // com.android.settings.BaseEditFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override // com.android.settings.BaseEditFragment
    public void onSave() {
        String obj = this.mTextWhiteList.getText().toString();
        try {
            Settings.System.putString(this.mContext.getContentResolver(), "com.xiaomi.bluetooth.thirdapp", obj + "," + System.currentTimeMillis());
        } catch (Exception unused) {
            Log.e("BluetoothTextWhiteListFragment", "On save failed");
        }
        super.onSave();
    }

    @Override // com.android.settings.BaseEditFragment, com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mTextWhiteList != null) {
            onEditStateChange(!TextUtils.isEmpty(r0.getText()));
        }
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        EditText editText = (EditText) view.findViewById(R.id.bluetooth_text_white_list);
        this.mTextWhiteList = editText;
        editText.setText("");
        this.mTextWhiteList.addTextChangedListener(new ListTextWatcher(new ListTextWatcher.Inputcallback() { // from class: com.android.settings.bluetooth.BluetoothTextWhiteListFragment.1
            @Override // com.android.settings.bluetooth.BluetoothTextWhiteListFragment.ListTextWatcher.Inputcallback
            public void nullContent(boolean z) {
                if (z == BluetoothTextWhiteListFragment.this.isEditEabled()) {
                    BluetoothTextWhiteListFragment.this.onEditStateChange(!z);
                }
            }
        }));
        this.mTextWhiteList.requestFocus();
    }
}
