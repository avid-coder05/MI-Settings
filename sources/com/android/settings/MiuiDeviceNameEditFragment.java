package com.android.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MiuiSettings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.bluetooth.MiuiBTUtils;
import com.android.settingslib.util.ToastUtil;

/* loaded from: classes.dex */
public class MiuiDeviceNameEditFragment extends BaseEditFragment {
    private String fragmentLabel = "";
    private String mDeviceName;
    private EditText mDeviceNameEdit;
    private Handler mHandler;
    private Runnable mRunnable;

    /* loaded from: classes.dex */
    public static class LengthFilter implements InputFilter {
        private NullContentCallBack mCallBack;
        private int mMax;

        /* loaded from: classes.dex */
        public interface NullContentCallBack {
            void beyondLimit();

            void isNullContent(boolean z);
        }

        public LengthFilter(NullContentCallBack nullContentCallBack, int i) {
            this.mCallBack = nullContentCallBack;
            this.mMax = i;
        }

        @Override // android.text.InputFilter
        public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
            int i5;
            this.mCallBack.isNullContent(TextUtils.isEmpty(charSequence) && i3 == 0 && spanned.length() == 1);
            int length = this.mMax - spanned.toString().getBytes().length;
            SpannableString spannableString = new SpannableString(charSequence);
            Object[] spans = spannableString.getSpans(0, spannableString.length(), Object.class);
            if (spans != null) {
                for (Object obj : spans) {
                    if (obj instanceof UnderlineSpan) {
                        if (length > 0 || (i5 = i4 - i3) > i2 - i) {
                            return null;
                        }
                        this.mCallBack.beyondLimit();
                        return charSequence.subSequence(0, i5);
                    }
                }
            }
            if (length >= charSequence.toString().getBytes().length) {
                return null;
            }
            this.mCallBack.beyondLimit();
            return "";
        }
    }

    /* loaded from: classes.dex */
    public static class LengthTextWatcher implements TextWatcher {
        public Inputcallback mInputcallback;
        private String mLastString;
        private int mMaxLength;

        /* loaded from: classes.dex */
        public interface Inputcallback {
            void beyondLimit(String str);

            void nullContent(boolean z);
        }

        public LengthTextWatcher(int i, Inputcallback inputcallback) {
            this.mMaxLength = i;
            this.mInputcallback = inputcallback;
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            if (editable != null) {
                String obj = editable.toString();
                this.mInputcallback.nullContent(obj.length() <= 0);
                if (obj.getBytes().length <= this.mMaxLength || TextUtils.equals(obj, this.mLastString)) {
                    return;
                }
                this.mInputcallback.beyondLimit(this.mLastString);
            }
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            this.mLastString = charSequence.toString();
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    private boolean isCustomized() {
        return MiuiBTUtils.isCustomizedOperator() && "bluetooth_label".equals(this.fragmentLabel);
    }

    @Override // com.android.settings.BaseEditFragment
    public String getTitle() {
        return getResources().getString(R.string.device_edit_title);
    }

    @Override // com.android.settings.BaseEditFragment
    public boolean isChanged() {
        String deviceName = MiuiSettings.System.getDeviceName(getActivity());
        if (isCustomized()) {
            deviceName = MiuiBTUtils.getBluetoothName();
        } else if ("p2p_label".equals(this.fragmentLabel)) {
            deviceName = MiuiUtils.getP2pDeviceName(getActivity());
        }
        return !this.mDeviceNameEdit.getText().toString().equals(deviceName);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        Bundle bundleExtra;
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent != null && (bundleExtra = intent.getBundleExtra(":settings:show_fragment_args")) != null) {
            this.fragmentLabel = bundleExtra.getString(":miui:starting_window_label", "");
        }
        Log.d("MiuiDeviceNameEditFragment", "fragmentLabel: " + this.fragmentLabel);
    }

    @Override // com.android.settings.BaseEditFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        Runnable runnable;
        super.onDestroyView();
        Handler handler = this.mHandler;
        if (handler == null || (runnable = this.mRunnable) == null) {
            return;
        }
        handler.removeCallbacks(runnable);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onInflateView(layoutInflater, viewGroup, bundle);
        return layoutInflater.inflate(R.layout.device_name_edit_layout, viewGroup, false);
    }

    @Override // com.android.settings.BaseEditFragment
    public void onSave() {
        String trim = this.mDeviceNameEdit.getText().toString().trim();
        try {
            if (isCustomized()) {
                MiuiBTUtils.setBluetoothName(trim);
            } else if ("p2p_label".equals(this.fragmentLabel)) {
                MiuiUtils.setP2pDeviceName(trim);
            } else {
                MiuiUtils.setDeviceName(getActivity(), trim);
            }
            if (!TextUtils.equals(this.mDeviceName, trim)) {
                getActivity().sendBroadcast(new Intent("com.miui.action.edit_device_name"));
            }
        } catch (Exception unused) {
            Toast.makeText(getContext().getApplicationContext(), R.string.device_name_input_error, 0).show();
        }
        super.onSave();
    }

    @Override // com.android.settings.BaseEditFragment, com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mDeviceNameEdit != null) {
            onEditStateChange(!TextUtils.isEmpty(r0.getText()));
        }
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        this.mDeviceNameEdit = (EditText) view.findViewById(R.id.device_name);
        String bluetoothName = isCustomized() ? MiuiBTUtils.getBluetoothName() : "p2p_label".equals(this.fragmentLabel) ? MiuiUtils.getP2pDeviceName(getActivity()) : MiuiSettings.System.getDeviceName(getActivity());
        this.mDeviceNameEdit.setText(bluetoothName);
        if (!TextUtils.isEmpty(bluetoothName)) {
            this.mDeviceNameEdit.setSelection(bluetoothName.length());
        }
        this.mDeviceNameEdit.addTextChangedListener(new LengthTextWatcher(31, new LengthTextWatcher.Inputcallback() { // from class: com.android.settings.MiuiDeviceNameEditFragment.1
            @Override // com.android.settings.MiuiDeviceNameEditFragment.LengthTextWatcher.Inputcallback
            public void beyondLimit(String str) {
                FragmentActivity activity = MiuiDeviceNameEditFragment.this.getActivity();
                if (activity == null) {
                    return;
                }
                MiuiDeviceNameEditFragment.this.mDeviceNameEdit.setText(str);
                MiuiDeviceNameEditFragment.this.mDeviceNameEdit.setSelection(MiuiDeviceNameEditFragment.this.mDeviceNameEdit.length());
                ToastUtil.show(activity.getApplicationContext(), R.string.device_name_input_overlength, 0);
            }

            @Override // com.android.settings.MiuiDeviceNameEditFragment.LengthTextWatcher.Inputcallback
            public void nullContent(boolean z) {
                if (z == MiuiDeviceNameEditFragment.this.isEditEabled()) {
                    MiuiDeviceNameEditFragment.this.onEditStateChange(!z);
                }
            }
        }));
        this.mDeviceNameEdit.requestFocus();
        this.mHandler = new Handler();
        Runnable runnable = new Runnable() { // from class: com.android.settings.MiuiDeviceNameEditFragment.2
            @Override // java.lang.Runnable
            public void run() {
                ((InputMethodManager) MiuiDeviceNameEditFragment.this.mDeviceNameEdit.getContext().getSystemService("input_method")).showSoftInput(MiuiDeviceNameEditFragment.this.mDeviceNameEdit, 0);
            }
        };
        this.mRunnable = runnable;
        this.mHandler.postDelayed(runnable, 200L);
    }
}
