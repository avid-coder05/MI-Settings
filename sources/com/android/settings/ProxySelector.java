package com.android.settings;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.ProxyInfo;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.android.net.module.util.ProxyUtils;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.InstrumentedFragment;
import java.util.Arrays;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class ProxySelector extends InstrumentedFragment implements DialogCreatable {
    Button mClearButton;
    Button mDefaultButton;
    private SettingsPreferenceFragment.SettingsDialogFragment mDialogFragment;
    EditText mExclusionListField;
    EditText mHostnameField;
    Button mOKButton;
    EditText mPortField;
    private View mView;
    View.OnClickListener mOKHandler = new View.OnClickListener() { // from class: com.android.settings.ProxySelector.1
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (ProxySelector.this.saveToDb()) {
                ProxySelector.this.getActivity().onBackPressed();
            }
        }
    };
    View.OnClickListener mClearHandler = new View.OnClickListener() { // from class: com.android.settings.ProxySelector.2
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ProxySelector.this.mHostnameField.setText("");
            ProxySelector.this.mPortField.setText("");
            ProxySelector.this.mExclusionListField.setText("");
        }
    };
    View.OnClickListener mDefaultHandler = new View.OnClickListener() { // from class: com.android.settings.ProxySelector.3
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ProxySelector.this.populateFields();
        }
    };
    View.OnFocusChangeListener mOnFocusChangeHandler = new View.OnFocusChangeListener() { // from class: com.android.settings.ProxySelector.4
        @Override // android.view.View.OnFocusChangeListener
        public void onFocusChange(View view, boolean z) {
            if (z) {
                Selection.selectAll((Spannable) ((TextView) view).getText());
            }
        }
    };

    private void initView(View view) {
        EditText editText = (EditText) view.findViewById(R.id.hostname);
        this.mHostnameField = editText;
        editText.setOnFocusChangeListener(this.mOnFocusChangeHandler);
        EditText editText2 = (EditText) view.findViewById(R.id.port);
        this.mPortField = editText2;
        editText2.setOnClickListener(this.mOKHandler);
        this.mPortField.setOnFocusChangeListener(this.mOnFocusChangeHandler);
        EditText editText3 = (EditText) view.findViewById(R.id.exclusionlist);
        this.mExclusionListField = editText3;
        editText3.setOnFocusChangeListener(this.mOnFocusChangeHandler);
        Button button = (Button) view.findViewById(R.id.action);
        this.mOKButton = button;
        button.setOnClickListener(this.mOKHandler);
        Button button2 = (Button) view.findViewById(R.id.clear);
        this.mClearButton = button2;
        button2.setOnClickListener(this.mClearHandler);
        Button button3 = (Button) view.findViewById(R.id.defaultView);
        this.mDefaultButton = button3;
        button3.setOnClickListener(this.mDefaultHandler);
    }

    private void showDialog(int i) {
        if (this.mDialogFragment != null) {
            Log.e("ProxySelector", "Old dialog fragment not null!");
        }
        SettingsPreferenceFragment.SettingsDialogFragment newInstance = SettingsPreferenceFragment.SettingsDialogFragment.newInstance(this, i);
        this.mDialogFragment = newInstance;
        newInstance.show(getActivity().getSupportFragmentManager(), Integer.toString(i));
    }

    public static int validate(String str, String str2, String str3) {
        int validate = ProxyUtils.validate(str, str2, str3);
        if (validate != 0) {
            if (validate != 1) {
                if (validate != 2) {
                    if (validate != 3) {
                        if (validate != 4) {
                            if (validate != 5) {
                                Log.e("ProxySelector", "Unknown proxy settings error");
                                return -1;
                            }
                            return R.string.proxy_error_invalid_exclusion_list;
                        }
                        return R.string.proxy_error_invalid_port;
                    }
                    return R.string.proxy_error_empty_port;
                }
                return R.string.proxy_error_invalid_host;
            }
            return R.string.proxy_error_empty_host_set_port;
        }
        return 0;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 82;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        boolean z = ((DevicePolicyManager) getActivity().getSystemService("device_policy")).getGlobalProxyAdmin() == null;
        this.mHostnameField.setEnabled(z);
        this.mPortField.setEnabled(z);
        this.mExclusionListField.setEnabled(z);
        this.mOKButton.setEnabled(z);
        this.mClearButton.setEnabled(z);
        this.mDefaultButton.setEnabled(z);
    }

    @Override // com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i == 0) {
            return new AlertDialog.Builder(getActivity()).setTitle(R.string.proxy_error).setPositiveButton(R.string.proxy_error_dismiss, (DialogInterface.OnClickListener) null).setMessage(getActivity().getString(validate(this.mHostnameField.getText().toString().trim(), this.mPortField.getText().toString().trim(), this.mExclusionListField.getText().toString().trim()))).create();
        }
        return null;
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.proxy, viewGroup, false);
        this.mView = inflate;
        initView(inflate);
        populateFields();
        return this.mView;
    }

    void populateFields() {
        int i;
        String str;
        String str2;
        FragmentActivity activity = getActivity();
        ProxyInfo globalProxy = ((ConnectivityManager) getActivity().getSystemService("connectivity")).getGlobalProxy();
        if (globalProxy != null) {
            str2 = globalProxy.getHost();
            i = globalProxy.getPort();
            str = ProxyUtils.exclusionListAsString(globalProxy.getExclusionList());
        } else {
            i = -1;
            str = "";
            str2 = str;
        }
        if (str2 == null) {
            str2 = "";
        }
        this.mHostnameField.setText(str2);
        this.mPortField.setText(i != -1 ? Integer.toString(i) : "");
        this.mExclusionListField.setText(str);
        Intent intent = activity.getIntent();
        String stringExtra = intent.getStringExtra("button-label");
        if (!TextUtils.isEmpty(stringExtra)) {
            this.mOKButton.setText(stringExtra);
        }
        String stringExtra2 = intent.getStringExtra("title");
        if (TextUtils.isEmpty(stringExtra2)) {
            activity.setTitle(R.string.proxy_settings_title);
        } else {
            activity.setTitle(stringExtra2);
        }
    }

    boolean saveToDb() {
        String trim = this.mHostnameField.getText().toString().trim();
        String trim2 = this.mPortField.getText().toString().trim();
        String trim3 = this.mExclusionListField.getText().toString().trim();
        int i = 0;
        if (validate(trim, trim2, trim3) != 0) {
            showDialog(0);
            return false;
        }
        if (trim2.length() > 0) {
            try {
                i = Integer.parseInt(trim2);
            } catch (NumberFormatException unused) {
                return false;
            }
        }
        ((ConnectivityManager) getActivity().getSystemService("connectivity")).setGlobalProxy(ProxyInfo.buildDirectProxy(trim, i, Arrays.asList(trim3.split(","))));
        return true;
    }
}
