package com.android.settings.wifi;

import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import java.nio.charset.Charset;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.widget.Spinner;

/* loaded from: classes2.dex */
public class WifiApDialog extends AlertDialog implements View.OnClickListener, TextWatcher, AdapterView.OnItemSelectedListener {
    private int mBandIndex;
    private Context mContext;
    private final DialogInterface.OnClickListener mListener;
    private EditText mPassword;
    SoftApConfiguration mSapConfig;
    private int mSecurityTypeIndex;
    private TextView mSsid;
    private View mView;
    WifiManager mWifiManager;

    public WifiApDialog(Context context, DialogInterface.OnClickListener onClickListener, SoftApConfiguration softApConfiguration) {
        super(context, R.style.Theme_WifiDialog);
        this.mSecurityTypeIndex = 0;
        this.mBandIndex = 1;
        this.mListener = onClickListener;
        this.mSapConfig = softApConfiguration;
        if (softApConfiguration != null) {
            this.mSecurityTypeIndex = getSecurityTypeIndex(softApConfiguration);
        }
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mContext = context;
    }

    public static int getSecurityTypeIndex(SoftApConfiguration softApConfiguration) {
        if (softApConfiguration.getSecurityType() == 1) {
            return 1;
        }
        return (softApConfiguration.getSecurityType() == 3 || softApConfiguration.getSecurityType() == 2) ? 2 : 0;
    }

    private void showSecurityFields() {
        if (this.mSecurityTypeIndex == 0) {
            this.mView.findViewById(R.id.fields).setVisibility(8);
        } else {
            this.mView.findViewById(R.id.fields).setVisibility(0);
        }
    }

    private void validate() {
        String charSequence = this.mSsid.getText().toString();
        TextView textView = this.mSsid;
        if ((textView == null || textView.length() != 0) && ((this.mSecurityTypeIndex != 1 || this.mPassword.length() >= 8) && ((this.mSecurityTypeIndex != 2 || this.mPassword.length() >= 8) && (this.mSsid == null || Charset.forName("UTF-8").encode(charSequence).limit() <= 32)))) {
            getButton(-1).setEnabled(true);
        } else {
            getButton(-1).setEnabled(false);
        }
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable editable) {
        validate();
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public SoftApConfiguration getSoftApConfig() {
        SoftApConfiguration.Builder builder = new SoftApConfiguration.Builder();
        builder.setSsid(this.mSsid.getText().toString());
        builder.setBand(this.mBandIndex);
        int i = this.mSecurityTypeIndex;
        if (i == 0) {
            builder.setPassphrase((String) null, 0);
            return builder.build();
        } else if (i == 1) {
            builder.setPassphrase(this.mPassword.getText().toString(), 1);
            return builder.build();
        } else if (i != 2) {
            return null;
        } else {
            builder.setPassphrase(this.mPassword.getText().toString(), 3);
            return builder.build();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        this.mPassword.setInputType((((CheckBox) view).isChecked() ? 144 : 128) | 1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog, android.app.Dialog
    public void onCreate(Bundle bundle) {
        View inflate = getLayoutInflater().inflate(R.layout.wifi_ap_dialog, (ViewGroup) null);
        this.mView = inflate;
        Spinner spinner = (Spinner) inflate.findViewById(R.id.security);
        if (MiuiUtils.getInstance().isWpa3SoftApSupport(getContext())) {
            spinner.setAdapter((SpinnerAdapter) ArrayAdapter.createFromResource(getContext(), R.array.wifi_ap_security_with_sae, 17367048));
        }
        setView(this.mView);
        Context context = getContext();
        setTitle(R.string.wifi_tether_configure_ap_text);
        this.mView.findViewById(R.id.type).setVisibility(0);
        this.mView.findViewById(R.id.l_security).setVisibility(0);
        this.mSsid = (TextView) this.mView.findViewById(R.id.ssid);
        this.mPassword = (EditText) this.mView.findViewById(R.id.password);
        (this.mWifiManager.getCountryCode() == null ? ArrayAdapter.createFromResource(this.mContext, R.array.wifi_ap_band_config_2G_only, 17367048) : ArrayAdapter.createFromResource(this.mContext, R.array.wifi_ap_band_config_full, 17367048)).setDropDownViewResource(17367049);
        setButton(-1, context.getString(R.string.wifi_save), this.mListener);
        setButton(-2, context.getString(R.string.wifi_cancel), this.mListener);
        SoftApConfiguration softApConfiguration = this.mSapConfig;
        if (softApConfiguration != null) {
            this.mSsid.setText(softApConfiguration.getSsid());
            this.mBandIndex = this.mSapConfig.getBand();
            spinner.setSelection(this.mSecurityTypeIndex);
            int i = this.mSecurityTypeIndex;
            if (i == 1 || i == 2) {
                this.mPassword.setText(this.mSapConfig.getPassphrase());
            }
        }
        this.mSsid.addTextChangedListener(this);
        this.mPassword.addTextChangedListener(this);
        ((CheckBox) this.mView.findViewById(R.id.show_password)).setOnClickListener(this);
        spinner.setOnItemSelectedListener(this);
        super.onCreate(bundle);
        showSecurityFields();
        validate();
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        this.mSecurityTypeIndex = i;
        showSecurityFields();
        validate();
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override // android.app.Dialog
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.mPassword.setInputType((((CheckBox) this.mView.findViewById(R.id.show_password)).isChecked() ? 144 : 128) | 1);
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }
}
