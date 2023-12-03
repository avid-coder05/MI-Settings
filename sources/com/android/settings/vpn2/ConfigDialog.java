package com.android.settings.vpn2;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ProxyInfo;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.android.internal.net.VpnProfile;
import com.android.net.module.util.ProxyUtils;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.utils.AndroidKeystoreAliasLoader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
class ConfigDialog extends AlertDialog implements TextWatcher, View.OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    private List<String> mAllowedTypes;
    private TextView mAlwaysOnInvalidReason;
    private CheckBox mAlwaysOnVpn;
    private TextView mDnsServers;
    private boolean mEditing;
    private boolean mExists;
    private Spinner mIpsecCaCert;
    private TextView mIpsecIdentifier;
    private TextView mIpsecSecret;
    private Spinner mIpsecServerCert;
    private Spinner mIpsecUserCert;
    private TextView mL2tpSecret;
    private final DialogInterface.OnClickListener mListener;
    private CheckBox mMppe;
    private TextView mName;
    private TextView mPassword;
    private final VpnProfile mProfile;
    private TextView mProxyHost;
    private TextView mProxyPort;
    private Spinner mProxySettings;
    private TextView mRoutes;
    private CheckBox mSaveLogin;
    private TextView mSearchDomains;
    private TextView mServer;
    private CheckBox mShowOptions;
    private List<String> mTotalTypes;
    private Spinner mType;
    private TextView mUsername;
    private View mView;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigDialog(Context context, DialogInterface.OnClickListener onClickListener, VpnProfile vpnProfile, boolean z, boolean z2) {
        super(context);
        this.mListener = onClickListener;
        this.mProfile = vpnProfile;
        this.mEditing = z;
        this.mExists = z2;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private void changeType(int i) {
        this.mMppe.setVisibility(8);
        View view = this.mView;
        int i2 = R.id.l2tp;
        view.findViewById(i2).setVisibility(8);
        View view2 = this.mView;
        int i3 = R.id.ipsec_psk;
        view2.findViewById(i3).setVisibility(8);
        View view3 = this.mView;
        int i4 = R.id.ipsec_user;
        view3.findViewById(i4).setVisibility(8);
        View view4 = this.mView;
        int i5 = R.id.ipsec_peer;
        view4.findViewById(i5).setVisibility(8);
        View view5 = this.mView;
        int i6 = R.id.options_ipsec_identity;
        view5.findViewById(i6).setVisibility(8);
        setUsernamePasswordVisibility(i);
        if (!VpnProfile.isLegacyType(i)) {
            this.mView.findViewById(i6).setVisibility(0);
        }
        switch (i) {
            case 0:
                this.mMppe.setVisibility(0);
                break;
            case 1:
                this.mView.findViewById(i2).setVisibility(0);
                this.mView.findViewById(i3).setVisibility(0);
                this.mView.findViewById(i6).setVisibility(0);
                break;
            case 2:
                this.mView.findViewById(i2).setVisibility(0);
                this.mView.findViewById(i4).setVisibility(0);
                this.mView.findViewById(i5).setVisibility(0);
                break;
            case 3:
            case 7:
                this.mView.findViewById(i3).setVisibility(0);
                this.mView.findViewById(i6).setVisibility(0);
                break;
            case 4:
            case 8:
                this.mView.findViewById(i4).setVisibility(0);
                this.mView.findViewById(i5).setVisibility(0);
                break;
            case 5:
            case 6:
                this.mView.findViewById(i5).setVisibility(0);
                break;
        }
        configureAdvancedOptionsVisibility();
    }

    private void configureAdvancedOptionsVisibility() {
        if (!this.mShowOptions.isChecked() && !isAdvancedOptionsEnabled()) {
            this.mView.findViewById(R.id.options).setVisibility(8);
            this.mShowOptions.setVisibility(0);
            return;
        }
        this.mView.findViewById(R.id.options).setVisibility(0);
        this.mShowOptions.setVisibility(8);
        this.mView.findViewById(R.id.network_options).setVisibility(VpnProfile.isLegacyType(getSelectedVpnType()) ? 0 : 8);
    }

    private int convertAllowedIndexToProfileType(int i) {
        List<String> list = this.mAllowedTypes;
        if (list == null || this.mTotalTypes == null) {
            Log.w("ConfigDialog", "Allowed or Total vpn types not initialized when converting protileType");
            return i;
        }
        return this.mTotalTypes.indexOf(list.get(i));
    }

    private int getSelectedVpnType() {
        return convertAllowedIndexToProfileType(this.mType.getSelectedItemPosition());
    }

    private boolean isAdvancedOptionsEnabled() {
        return this.mSearchDomains.getText().length() > 0 || this.mDnsServers.getText().length() > 0 || this.mRoutes.getText().length() > 0 || this.mProxyHost.getText().length() > 0 || this.mProxyPort.getText().length() > 0;
    }

    private void loadCertificates(Spinner spinner, Collection<String> collection, int i, String str) {
        String[] strArr;
        Context context = getContext();
        String string = i == 0 ? "" : context.getString(i);
        if (collection == null || collection.size() == 0) {
            strArr = new String[]{string};
        } else {
            strArr = new String[collection.size() + 1];
            strArr[0] = string;
            Iterator<String> it = collection.iterator();
            int i2 = 1;
            while (it.hasNext()) {
                strArr[i2] = it.next();
                i2++;
            }
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(context, 17367048, strArr);
        arrayAdapter.setDropDownViewResource(17367049);
        spinner.setAdapter((SpinnerAdapter) arrayAdapter);
        for (int i3 = 1; i3 < strArr.length; i3++) {
            if (strArr[i3].equals(str)) {
                spinner.setSelection(i3);
                return;
            }
        }
    }

    private boolean requiresUsernamePassword(int i) {
        return (i == 7 || i == 8) ? false : true;
    }

    private void setTypesByFeature(Spinner spinner) {
        String[] stringArray = getContext().getResources().getStringArray(R.array.vpn_types);
        this.mTotalTypes = new ArrayList(Arrays.asList(stringArray));
        this.mAllowedTypes = new ArrayList(Arrays.asList(stringArray));
        if (!getContext().getPackageManager().hasSystemFeature("android.software.ipsec_tunnels")) {
            ArrayList arrayList = new ArrayList(Arrays.asList(stringArray));
            arrayList.remove(8);
            arrayList.remove(7);
            arrayList.remove(6);
            stringArray = (String[]) arrayList.toArray(new String[0]);
        } else if (Utils.isProviderModelEnabled(getContext())) {
            if (!this.mExists) {
                this.mProfile.type = 6;
            }
            if (!VpnProfile.isLegacyType(this.mProfile.type)) {
                for (int size = this.mAllowedTypes.size() - 1; size >= 0; size--) {
                    if (VpnProfile.isLegacyType(size)) {
                        this.mAllowedTypes.remove(size);
                    }
                }
                stringArray = (String[]) this.mAllowedTypes.toArray(new String[0]);
            }
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), 17367048, stringArray);
        arrayAdapter.setDropDownViewResource(17367049);
        spinner.setAdapter((SpinnerAdapter) arrayAdapter);
    }

    private void setUsernamePasswordVisibility(int i) {
        this.mView.findViewById(R.id.userpass).setVisibility(requiresUsernamePassword(i) ? 0 : 8);
    }

    private void updateProxyFieldsVisibility(int i) {
        this.mView.findViewById(R.id.vpn_proxy_fields).setVisibility(i == 1 ? 0 : 8);
    }

    private void updateUiControls() {
        VpnProfile profile = getProfile();
        if (profile.isValidLockdownProfile()) {
            this.mAlwaysOnVpn.setEnabled(true);
            this.mAlwaysOnInvalidReason.setVisibility(8);
        } else {
            this.mAlwaysOnVpn.setChecked(false);
            this.mAlwaysOnVpn.setEnabled(false);
            if (!profile.isTypeValidForLockdown()) {
                this.mAlwaysOnInvalidReason.setText(R.string.vpn_always_on_invalid_reason_type);
            } else if (VpnProfile.isLegacyType(profile.type) && !profile.isServerAddressNumeric()) {
                this.mAlwaysOnInvalidReason.setText(R.string.vpn_always_on_invalid_reason_server);
            } else if (VpnProfile.isLegacyType(profile.type) && !profile.hasDns()) {
                this.mAlwaysOnInvalidReason.setText(R.string.vpn_always_on_invalid_reason_no_dns);
            } else if (!VpnProfile.isLegacyType(profile.type) || profile.areDnsAddressesNumeric()) {
                this.mAlwaysOnInvalidReason.setText(R.string.vpn_always_on_invalid_reason_other);
            } else {
                this.mAlwaysOnInvalidReason.setText(R.string.vpn_always_on_invalid_reason_dns);
            }
            this.mAlwaysOnInvalidReason.setVisibility(0);
        }
        ProxyInfo proxyInfo = this.mProfile.proxy;
        if (proxyInfo != null && (!proxyInfo.getHost().isEmpty() || this.mProfile.proxy.getPort() != 0)) {
            this.mProxySettings.setSelection(1);
            updateProxyFieldsVisibility(1);
        }
        if (this.mAlwaysOnVpn.isChecked()) {
            this.mSaveLogin.setChecked(true);
            this.mSaveLogin.setEnabled(false);
        } else {
            this.mSaveLogin.setChecked(this.mProfile.saveLogin);
            this.mSaveLogin.setEnabled(true);
        }
        getButton(-1).setEnabled(validate(this.mEditing));
    }

    private boolean validate(boolean z) {
        if (!this.mAlwaysOnVpn.isChecked() || getProfile().isValidLockdownProfile()) {
            int selectedVpnType = getSelectedVpnType();
            if (!z && requiresUsernamePassword(selectedVpnType)) {
                return (this.mUsername.getText().length() == 0 || this.mPassword.getText().length() == 0) ? false : true;
            } else if (this.mName.getText().length() == 0 || this.mServer.getText().length() == 0) {
                return false;
            } else {
                if (!VpnProfile.isLegacyType(this.mProfile.type) || (validateAddresses(this.mDnsServers.getText().toString(), false) && validateAddresses(this.mRoutes.getText().toString(), true))) {
                    if ((VpnProfile.isLegacyType(this.mProfile.type) || this.mIpsecIdentifier.getText().length() != 0) && validateProxy()) {
                        switch (selectedVpnType) {
                            case 0:
                            case 5:
                            case 6:
                                return true;
                            case 1:
                            case 3:
                            case 7:
                                return this.mIpsecSecret.getText().length() != 0;
                            case 2:
                            case 4:
                            case 8:
                                return this.mIpsecUserCert.getSelectedItemPosition() != 0;
                            default:
                                return false;
                        }
                    }
                    return false;
                }
                return false;
            }
        }
        return false;
    }

    private boolean validateAddresses(String str, boolean z) {
        int i;
        try {
            for (String str2 : str.split(" ")) {
                if (!str2.isEmpty()) {
                    if (z) {
                        String[] split = str2.split("/", 2);
                        String str3 = split[0];
                        i = Integer.parseInt(split[1]);
                        str2 = str3;
                    } else {
                        i = 32;
                    }
                    byte[] address = InetAddress.parseNumericAddress(str2).getAddress();
                    int i2 = ((address[1] & 255) << 16) | ((address[2] & 255) << 8) | (address[3] & 255) | ((address[0] & 255) << 24);
                    if (address.length != 4 || i < 0 || i > 32 || (i < 32 && (i2 << i) != 0)) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    private boolean validateProxy() {
        return !hasProxy() || ProxyUtils.validate(this.mProxyHost.getText().toString().trim(), this.mProxyPort.getText().toString().trim(), "") == 0;
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable editable) {
        updateUiControls();
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:20:0x00db  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x00ed  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x00ff  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public com.android.internal.net.VpnProfile getProfile() {
        /*
            Method dump skipped, instructions count: 372
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.vpn2.ConfigDialog.getProfile():com.android.internal.net.VpnProfile");
    }

    boolean hasProxy() {
        return this.mProxySettings.getSelectedItemPosition() == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEditing() {
        return this.mEditing;
    }

    public boolean isVpnAlwaysOn() {
        return this.mAlwaysOnVpn.isChecked();
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        if (compoundButton == this.mAlwaysOnVpn) {
            updateUiControls();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view == this.mShowOptions) {
            configureAdvancedOptionsVisibility();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog, android.app.Dialog
    public void onCreate(Bundle bundle) {
        List<String> list;
        View inflate = getLayoutInflater().inflate(R.layout.vpn_dialog, (ViewGroup) null);
        this.mView = inflate;
        setView(inflate);
        Context context = getContext();
        this.mName = (TextView) this.mView.findViewById(R.id.name);
        this.mType = (Spinner) this.mView.findViewById(R.id.type);
        this.mServer = (TextView) this.mView.findViewById(R.id.server);
        this.mUsername = (TextView) this.mView.findViewById(R.id.username);
        this.mPassword = (TextView) this.mView.findViewById(R.id.password);
        this.mSearchDomains = (TextView) this.mView.findViewById(R.id.search_domains);
        this.mDnsServers = (TextView) this.mView.findViewById(R.id.dns_servers);
        this.mRoutes = (TextView) this.mView.findViewById(R.id.routes);
        this.mProxySettings = (Spinner) this.mView.findViewById(R.id.vpn_proxy_settings);
        this.mProxyHost = (TextView) this.mView.findViewById(R.id.vpn_proxy_host);
        this.mProxyPort = (TextView) this.mView.findViewById(R.id.vpn_proxy_port);
        this.mMppe = (CheckBox) this.mView.findViewById(R.id.mppe);
        this.mL2tpSecret = (TextView) this.mView.findViewById(R.id.l2tp_secret);
        this.mIpsecIdentifier = (TextView) this.mView.findViewById(R.id.ipsec_identifier);
        this.mIpsecSecret = (TextView) this.mView.findViewById(R.id.ipsec_secret);
        this.mIpsecUserCert = (Spinner) this.mView.findViewById(R.id.ipsec_user_cert);
        this.mIpsecCaCert = (Spinner) this.mView.findViewById(R.id.ipsec_ca_cert);
        this.mIpsecServerCert = (Spinner) this.mView.findViewById(R.id.ipsec_server_cert);
        this.mSaveLogin = (CheckBox) this.mView.findViewById(R.id.save_login);
        this.mShowOptions = (CheckBox) this.mView.findViewById(R.id.show_options);
        this.mAlwaysOnVpn = (CheckBox) this.mView.findViewById(R.id.always_on_vpn);
        this.mAlwaysOnInvalidReason = (TextView) this.mView.findViewById(R.id.always_on_invalid_reason);
        this.mName.setText(this.mProfile.name);
        setTypesByFeature(this.mType);
        List<String> list2 = this.mAllowedTypes;
        if (list2 == null || (list = this.mTotalTypes) == null) {
            Log.w("ConfigDialog", "Allowed or Total vpn types not initialized when setting initial selection");
        } else {
            this.mType.setSelection(list2.indexOf(list.get(this.mProfile.type)));
        }
        this.mServer.setText(this.mProfile.server);
        VpnProfile vpnProfile = this.mProfile;
        if (vpnProfile.saveLogin) {
            this.mUsername.setText(vpnProfile.username);
            this.mPassword.setText(this.mProfile.password);
        }
        this.mSearchDomains.setText(this.mProfile.searchDomains);
        this.mDnsServers.setText(this.mProfile.dnsServers);
        this.mRoutes.setText(this.mProfile.routes);
        ProxyInfo proxyInfo = this.mProfile.proxy;
        if (proxyInfo != null) {
            this.mProxyHost.setText(proxyInfo.getHost());
            int port = this.mProfile.proxy.getPort();
            this.mProxyPort.setText(port == 0 ? "" : Integer.toString(port));
        }
        this.mMppe.setChecked(this.mProfile.mppe);
        this.mL2tpSecret.setText(this.mProfile.l2tpSecret);
        this.mL2tpSecret.setTextAppearance(16974257);
        this.mIpsecIdentifier.setText(this.mProfile.ipsecIdentifier);
        this.mIpsecSecret.setText(this.mProfile.ipsecSecret);
        AndroidKeystoreAliasLoader androidKeystoreAliasLoader = new AndroidKeystoreAliasLoader(null);
        loadCertificates(this.mIpsecUserCert, androidKeystoreAliasLoader.getKeyCertAliases(), 0, this.mProfile.ipsecUserCert);
        loadCertificates(this.mIpsecCaCert, androidKeystoreAliasLoader.getCaCertAliases(), R.string.vpn_no_ca_cert, this.mProfile.ipsecCaCert);
        loadCertificates(this.mIpsecServerCert, androidKeystoreAliasLoader.getKeyCertAliases(), R.string.vpn_no_server_cert, this.mProfile.ipsecServerCert);
        this.mSaveLogin.setChecked(this.mProfile.saveLogin);
        this.mAlwaysOnVpn.setChecked(this.mProfile.key.equals(VpnUtils.getLockdownVpn()));
        this.mPassword.setTextAppearance(16974257);
        if (SystemProperties.getBoolean("persist.radio.imsregrequired", false)) {
            this.mAlwaysOnVpn.setVisibility(8);
        }
        this.mName.addTextChangedListener(this);
        this.mType.setOnItemSelectedListener(this);
        this.mServer.addTextChangedListener(this);
        this.mUsername.addTextChangedListener(this);
        this.mPassword.addTextChangedListener(this);
        this.mDnsServers.addTextChangedListener(this);
        this.mRoutes.addTextChangedListener(this);
        this.mProxySettings.setOnItemSelectedListener(this);
        this.mProxyHost.addTextChangedListener(this);
        this.mProxyPort.addTextChangedListener(this);
        this.mIpsecIdentifier.addTextChangedListener(this);
        this.mIpsecSecret.addTextChangedListener(this);
        this.mIpsecUserCert.setOnItemSelectedListener(this);
        this.mShowOptions.setOnClickListener(this);
        this.mAlwaysOnVpn.setOnCheckedChangeListener(this);
        boolean z = this.mEditing || !validate(true);
        this.mEditing = z;
        if (z) {
            setTitle(R.string.vpn_edit);
            this.mView.findViewById(R.id.editor).setVisibility(0);
            changeType(this.mProfile.type);
            this.mSaveLogin.setVisibility(8);
            configureAdvancedOptionsVisibility();
            if (this.mExists) {
                setButton(-3, context.getString(R.string.vpn_forget), this.mListener);
                if (VpnProfile.isLegacyType(this.mProfile.type)) {
                    ((TextView) this.mView.findViewById(R.id.dialog_alert_subtitle)).setVisibility(0);
                }
            }
            setButton(-1, context.getString(R.string.vpn_save), this.mListener);
        } else {
            setTitle(context.getString(R.string.vpn_connect_to, this.mProfile.name));
            setUsernamePasswordVisibility(this.mProfile.type);
            setButton(-1, context.getString(R.string.vpn_connect), this.mListener);
        }
        setButton(-2, context.getString(R.string.vpn_cancel), this.mListener);
        super.onCreate(bundle);
        updateUiControls();
        getWindow().setSoftInputMode(20);
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        if (adapterView == this.mType) {
            changeType(convertAllowedIndexToProfileType(i));
        } else if (adapterView == this.mProxySettings) {
            updateProxyFieldsVisibility(i);
        }
        updateUiControls();
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override // android.app.Dialog
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        configureAdvancedOptionsVisibility();
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }
}
