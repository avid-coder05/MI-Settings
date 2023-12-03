package com.android.settings.vpn2;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.security.KeyStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import com.android.internal.net.VpnProfile;
import com.android.settings.BaseEditFragment;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.utils.AndroidKeystoreAliasLoader;
import com.android.settings.utils.KeyboardHelper;
import com.android.settings.vpn2.VpnCheckBox;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import miui.yellowpage.YellowPageContract;
import miuix.androidbasewidget.widget.StateEditText;
import miuix.animation.Folme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class MiuiVpnEditFragment extends BaseEditFragment implements TextWatcher, AdapterView.OnItemSelectedListener {
    private List<String> mAllowedTypes;
    private Bundle mArgs;
    private boolean mChoice;
    private Context mContext;
    private StateEditText mDnsServers;
    private boolean mEditing;
    private VpnSpinner mIpsecCaCert;
    private StateEditText mIpsecIdentifier;
    private StateEditText mIpsecSecret;
    private VpnSpinner mIpsecServerCert;
    private VpnSpinner mIpsecUserCert;
    private KeyboardHelper mKeyboardHelper;
    private StateEditText mL2tpSecret;
    private VpnCheckBox mMppe;
    private StateEditText mName;
    private VpnCheckBox mOptions;
    private StateEditText mPassword;
    private VpnProfile mProfile;
    private StateEditText mRoutes;
    private StateEditText mSearchDomains;
    private StateEditText mServer;
    private List<String> mTotalTypes;
    private VpnSpinner mType;
    private StateEditText mUsername;
    private final KeyStore mKeyStore = KeyStore.getInstance();
    private boolean mAddVpn = true;

    private void addAnim(View view) {
        Folme.useAt(view).touch().setBackgroundColor(0.08f, 0.0f, 0.0f, 0.0f).setScale(1.0f, new ITouchStyle.TouchType[0]).handleTouchOf(view, new AnimConfig[0]);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private void changeType(int i) {
        View view = getView();
        this.mMppe.setVisibility(8);
        int i2 = R.id.l2tp;
        view.findViewById(i2).setVisibility(8);
        int i3 = R.id.ipsec_psk;
        view.findViewById(i3).setVisibility(8);
        int i4 = R.id.ipsec_user_cert;
        view.findViewById(i4).setVisibility(8);
        int i5 = R.id.ipsec_identifier_bg;
        view.findViewById(i5).setVisibility(8);
        int i6 = R.id.ipsec_secret_bg;
        view.findViewById(i6).setVisibility(8);
        int i7 = R.id.ipsec_ca_cert;
        view.findViewById(i7).setVisibility(8);
        int i8 = R.id.ipsec_server_cert;
        view.findViewById(i8).setVisibility(8);
        setUsernamePasswordVisibility(i);
        if (!VpnProfile.isLegacyType(i)) {
            view.findViewById(i3).setVisibility(0);
            view.findViewById(i5).setVisibility(0);
        }
        switch (i) {
            case 0:
                this.mMppe.setVisibility(0);
                return;
            case 1:
                view.findViewById(i2).setVisibility(0);
                view.findViewById(i3).setVisibility(0);
                view.findViewById(i5).setVisibility(0);
                view.findViewById(i6).setVisibility(0);
                return;
            case 2:
                view.findViewById(i3).setVisibility(0);
                view.findViewById(i2).setVisibility(0);
                view.findViewById(i4).setVisibility(0);
                break;
            case 3:
            case 7:
                view.findViewById(i3).setVisibility(0);
                view.findViewById(i5).setVisibility(0);
                view.findViewById(i6).setVisibility(0);
                return;
            case 4:
            case 8:
                view.findViewById(i4).setVisibility(0);
                break;
            case 5:
            case 6:
                break;
            default:
                return;
        }
        view.findViewById(i7).setVisibility(0);
        view.findViewById(i8).setVisibility(0);
    }

    private int convertAllowedIndexToProfileType(int i) {
        List<String> list = this.mAllowedTypes;
        if (list == null || this.mTotalTypes == null) {
            Log.w("MiuiVpnEditFragment", "Allowed or Total vpn types not initialized when converting protileType");
            return i;
        }
        return this.mTotalTypes.indexOf(list.get(i));
    }

    private int getSelectedVpnType() {
        return convertAllowedIndexToProfileType(this.mType.getSelectedItemPosition());
    }

    private void loadCertificates(VpnSpinner vpnSpinner, Collection<String> collection, int i, String str) {
        String[] strArr;
        String string = i == 0 ? "" : getContext().getString(i);
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
        ArrayAdapter arrayAdapter = new ArrayAdapter(this.mContext, R.layout.miuix_appcompat_simple_spinner_layout_integrated, 16908308, strArr);
        arrayAdapter.setDropDownViewResource(R.layout.miuix_appcompat_simple_spinner_dropdown_item);
        vpnSpinner.getSpinner().setAdapter((SpinnerAdapter) arrayAdapter);
        vpnSpinner.setPrompt(vpnSpinner.getPrompt());
        for (int i3 = 1; i3 < strArr.length; i3++) {
            if (strArr[i3].equals(str)) {
                vpnSpinner.setSelection(i3);
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
            if (this.mAddVpn) {
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
        ArrayAdapter arrayAdapter = new ArrayAdapter(this.mContext, R.layout.miuix_appcompat_simple_spinner_layout_integrated, 16908308, stringArray);
        arrayAdapter.setDropDownViewResource(R.layout.miuix_appcompat_simple_spinner_dropdown_item);
        spinner.setAdapter((SpinnerAdapter) arrayAdapter);
    }

    private void setUsernamePasswordVisibility(int i) {
        getView().findViewById(R.id.login).setVisibility(requiresUsernamePassword(i) ? 0 : 8);
    }

    private boolean validate(boolean z) {
        int selectedVpnType = getSelectedVpnType();
        if (!z && requiresUsernamePassword(selectedVpnType)) {
            return (this.mUsername.getText().length() == 0 || this.mPassword.getText().length() == 0) ? false : true;
        } else if (this.mName.getText().length() == 0 || this.mServer.getText().length() == 0 || !validateAddresses(this.mDnsServers.getText().toString(), false) || !validateAddresses(this.mRoutes.getText().toString(), true)) {
            return false;
        } else {
            if (VpnProfile.isLegacyType(this.mProfile.type) || this.mIpsecIdentifier.getText().length() != 0) {
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

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable editable) {
        onEditStateChange(validate(this.mEditing));
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // com.android.settings.BaseFragment
    public View doInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (getActivity() != null) {
            this.mContext = getActivity();
        }
        return layoutInflater.inflate(R.layout.vpn_edit_layout, viewGroup, false);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:12:0x00a2  */
    /* JADX WARN: Removed duplicated region for block: B:15:0x00b4  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x00c6  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    com.android.internal.net.VpnProfile getProfile() {
        /*
            r2 = this;
            com.android.internal.net.VpnProfile r0 = new com.android.internal.net.VpnProfile
            com.android.internal.net.VpnProfile r1 = r2.mProfile
            java.lang.String r1 = r1.key
            r0.<init>(r1)
            miuix.androidbasewidget.widget.StateEditText r1 = r2.mName
            android.text.Editable r1 = r1.getText()
            java.lang.String r1 = r1.toString()
            r0.name = r1
            int r1 = r2.getSelectedVpnType()
            r0.type = r1
            miuix.androidbasewidget.widget.StateEditText r1 = r2.mServer
            android.text.Editable r1 = r1.getText()
            java.lang.String r1 = r1.toString()
            java.lang.String r1 = r1.trim()
            r0.server = r1
            miuix.androidbasewidget.widget.StateEditText r1 = r2.mUsername
            android.text.Editable r1 = r1.getText()
            java.lang.String r1 = r1.toString()
            r0.username = r1
            miuix.androidbasewidget.widget.StateEditText r1 = r2.mPassword
            android.text.Editable r1 = r1.getText()
            java.lang.String r1 = r1.toString()
            r0.password = r1
            int r1 = r0.type
            boolean r1 = com.android.internal.net.VpnProfile.isLegacyType(r1)
            if (r1 == 0) goto L7c
            miuix.androidbasewidget.widget.StateEditText r1 = r2.mSearchDomains
            android.text.Editable r1 = r1.getText()
            java.lang.String r1 = r1.toString()
            java.lang.String r1 = r1.trim()
            r0.searchDomains = r1
            miuix.androidbasewidget.widget.StateEditText r1 = r2.mDnsServers
            android.text.Editable r1 = r1.getText()
            java.lang.String r1 = r1.toString()
            java.lang.String r1 = r1.trim()
            r0.dnsServers = r1
            miuix.androidbasewidget.widget.StateEditText r1 = r2.mRoutes
            android.text.Editable r1 = r1.getText()
            java.lang.String r1 = r1.toString()
            java.lang.String r1 = r1.trim()
            r0.routes = r1
            goto L88
        L7c:
            miuix.androidbasewidget.widget.StateEditText r1 = r2.mIpsecIdentifier
            android.text.Editable r1 = r1.getText()
            java.lang.String r1 = r1.toString()
            r0.ipsecIdentifier = r1
        L88:
            int r1 = r0.type
            switch(r1) {
                case 0: goto Lf6;
                case 1: goto Ld1;
                case 2: goto L8e;
                case 3: goto Ldd;
                case 4: goto L9a;
                case 5: goto Lac;
                case 6: goto Lac;
                case 7: goto Ldd;
                case 8: goto L9a;
                default: goto L8d;
            }
        L8d:
            goto Lfe
        L8e:
            miuix.androidbasewidget.widget.StateEditText r1 = r2.mL2tpSecret
            android.text.Editable r1 = r1.getText()
            java.lang.String r1 = r1.toString()
            r0.l2tpSecret = r1
        L9a:
            com.android.settings.vpn2.VpnSpinner r1 = r2.mIpsecUserCert
            int r1 = r1.getSelectedItemPosition()
            if (r1 == 0) goto Lac
            com.android.settings.vpn2.VpnSpinner r1 = r2.mIpsecUserCert
            java.lang.Object r1 = r1.getSelectedItem()
            java.lang.String r1 = (java.lang.String) r1
            r0.ipsecUserCert = r1
        Lac:
            com.android.settings.vpn2.VpnSpinner r1 = r2.mIpsecCaCert
            int r1 = r1.getSelectedItemPosition()
            if (r1 == 0) goto Lbe
            com.android.settings.vpn2.VpnSpinner r1 = r2.mIpsecCaCert
            java.lang.Object r1 = r1.getSelectedItem()
            java.lang.String r1 = (java.lang.String) r1
            r0.ipsecCaCert = r1
        Lbe:
            com.android.settings.vpn2.VpnSpinner r1 = r2.mIpsecServerCert
            int r1 = r1.getSelectedItemPosition()
            if (r1 == 0) goto Lfe
            com.android.settings.vpn2.VpnSpinner r2 = r2.mIpsecServerCert
            java.lang.Object r2 = r2.getSelectedItem()
            java.lang.String r2 = (java.lang.String) r2
            r0.ipsecServerCert = r2
            goto Lfe
        Ld1:
            miuix.androidbasewidget.widget.StateEditText r1 = r2.mL2tpSecret
            android.text.Editable r1 = r1.getText()
            java.lang.String r1 = r1.toString()
            r0.l2tpSecret = r1
        Ldd:
            miuix.androidbasewidget.widget.StateEditText r1 = r2.mIpsecIdentifier
            android.text.Editable r1 = r1.getText()
            java.lang.String r1 = r1.toString()
            r0.ipsecIdentifier = r1
            miuix.androidbasewidget.widget.StateEditText r2 = r2.mIpsecSecret
            android.text.Editable r2 = r2.getText()
            java.lang.String r2 = r2.toString()
            r0.ipsecSecret = r2
            goto Lfe
        Lf6:
            com.android.settings.vpn2.VpnCheckBox r2 = r2.mMppe
            boolean r2 = r2.isChecked()
            r0.mppe = r2
        Lfe:
            r2 = 1
            r0.saveLogin = r2
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.vpn2.MiuiVpnEditFragment.getProfile():com.android.internal.net.VpnProfile");
    }

    @Override // com.android.settings.BaseEditFragment
    public String getTitle() {
        return getString(this.mArgs.getBoolean("profile_add", true) ? R.string.vpn_create : R.string.vpn_edit);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mChoice = bundle.getBoolean("show_options_flag");
        }
        Bundle arguments = getArguments();
        this.mArgs = arguments;
        this.mAddVpn = arguments.getBoolean("profile_add", true);
        byte[] byteArray = this.mArgs.getByteArray(YellowPageContract.Profile.DIRECTORY);
        String string = this.mArgs.getString("profile_key");
        if (TextUtils.isEmpty(string)) {
            return;
        }
        if (byteArray == null || byteArray.length == 0) {
            this.mProfile = new VpnProfile(string);
        } else {
            this.mProfile = VpnProfile.decode(string, byteArray);
        }
        this.mEditing = true;
    }

    @Override // com.android.settings.BaseEditFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        KeyboardHelper keyboardHelper = this.mKeyboardHelper;
        if (keyboardHelper != null) {
            keyboardHelper.destroy();
        }
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        if (adapterView == this.mType.getSpinner()) {
            changeType(convertAllowedIndexToProfileType(i));
        }
        onEditStateChange(validate(this.mEditing));
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        onEditStateChange(validate(this.mEditing));
    }

    @Override // com.android.settings.BaseEditFragment
    public void onSave(boolean z) {
        VpnProfile profile = getProfile();
        Bundle bundle = new Bundle();
        bundle.putByteArray(YellowPageContract.Profile.DIRECTORY, profile.encode());
        bundle.putString("profile_key", profile.key);
        bundle.putBoolean("profile_delete", z);
        onSave(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("show_options_flag", this.mChoice);
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        List<String> list;
        super.onViewCreated(view, bundle);
        if (TextUtils.isEmpty(this.mArgs.getString("profile_key"))) {
            finish();
            return;
        }
        this.mName = (StateEditText) view.findViewById(R.id.name);
        this.mServer = (StateEditText) view.findViewById(R.id.server);
        this.mUsername = (StateEditText) view.findViewById(R.id.username);
        this.mPassword = (StateEditText) view.findViewById(R.id.password);
        this.mSearchDomains = (StateEditText) view.findViewById(R.id.search_domains);
        this.mDnsServers = (StateEditText) view.findViewById(R.id.dns_servers);
        this.mRoutes = (StateEditText) view.findViewById(R.id.routes);
        this.mContext = view.getContext();
        this.mOptions = (VpnCheckBox) view.findViewById(R.id.show_options);
        this.mMppe = (VpnCheckBox) view.findViewById(R.id.mppe);
        this.mType = (VpnSpinner) view.findViewById(R.id.type);
        this.mL2tpSecret = (StateEditText) view.findViewById(R.id.l2tp_secret);
        this.mIpsecIdentifier = (StateEditText) view.findViewById(R.id.ipsec_identifier);
        this.mIpsecSecret = (StateEditText) view.findViewById(R.id.ipsec_secret);
        this.mIpsecUserCert = (VpnSpinner) view.findViewById(R.id.ipsec_user_cert);
        this.mIpsecCaCert = (VpnSpinner) view.findViewById(R.id.ipsec_ca_cert);
        this.mIpsecServerCert = (VpnSpinner) view.findViewById(R.id.ipsec_server_cert);
        this.mServer.setHint(this.mContext.getResources().getString(R.string.proxy_hostname_hint));
        AndroidKeystoreAliasLoader androidKeystoreAliasLoader = new AndroidKeystoreAliasLoader(null);
        loadCertificates(this.mIpsecUserCert, androidKeystoreAliasLoader.getKeyCertAliases(), 0, this.mProfile.ipsecUserCert);
        loadCertificates(this.mIpsecCaCert, androidKeystoreAliasLoader.getCaCertAliases(), R.string.vpn_no_ca_cert, this.mProfile.ipsecCaCert);
        loadCertificates(this.mIpsecServerCert, androidKeystoreAliasLoader.getKeyCertAliases(), R.string.vpn_no_server_cert, this.mProfile.ipsecServerCert);
        this.mName.setText(this.mProfile.name);
        setTypesByFeature(this.mType.getSpinner());
        List<String> list2 = this.mAllowedTypes;
        if (list2 == null || (list = this.mTotalTypes) == null) {
            Log.w("MiuiVpnEditFragment", "Allowed or Total vpn types not initialized when setting initial selection");
        } else {
            this.mType.setSelection(list2.indexOf(list.get(this.mProfile.type)));
        }
        this.mServer.setText(this.mProfile.server);
        this.mUsername.setText(this.mProfile.username);
        this.mPassword.setText(this.mProfile.password);
        this.mSearchDomains.setText(this.mProfile.searchDomains);
        this.mDnsServers.setText(this.mProfile.dnsServers);
        this.mRoutes.setText(this.mProfile.routes);
        this.mMppe.setChecked(this.mProfile.mppe);
        this.mL2tpSecret.setText(this.mProfile.l2tpSecret);
        this.mIpsecIdentifier.setText(this.mProfile.ipsecIdentifier);
        this.mIpsecSecret.setText(this.mProfile.ipsecSecret);
        VpnSpinner vpnSpinner = this.mType;
        vpnSpinner.setPrompt(vpnSpinner.getPrompt());
        VpnSpinner vpnSpinner2 = this.mIpsecUserCert;
        vpnSpinner2.setPrompt(vpnSpinner2.getPrompt());
        VpnSpinner vpnSpinner3 = this.mIpsecCaCert;
        vpnSpinner3.setPrompt(vpnSpinner3.getPrompt());
        VpnSpinner vpnSpinner4 = this.mIpsecServerCert;
        vpnSpinner4.setPrompt(vpnSpinner4.getPrompt());
        this.mName.addTextChangedListener(this);
        this.mType.getSpinner().setOnItemSelectedListener(this);
        this.mServer.addTextChangedListener(this);
        this.mUsername.addTextChangedListener(this);
        this.mPassword.addTextChangedListener(this);
        this.mDnsServers.addTextChangedListener(this);
        this.mRoutes.addTextChangedListener(this);
        this.mIpsecIdentifier.addTextChangedListener(this);
        this.mIpsecSecret.addTextChangedListener(this);
        this.mIpsecUserCert.getSpinner().setOnItemSelectedListener(this);
        boolean z = this.mEditing || !validate(true);
        this.mEditing = z;
        if (z) {
            view.findViewById(R.id.editor).setVisibility(0);
            int i = R.id.options;
            view.findViewById(i).setVisibility(this.mChoice ? 0 : 8);
            changeType(this.mProfile.type);
            this.mOptions.setCheckListener(new VpnCheckBox.CheckListener() { // from class: com.android.settings.vpn2.MiuiVpnEditFragment.1
                @Override // com.android.settings.vpn2.VpnCheckBox.CheckListener
                public void check() {
                    MiuiVpnEditFragment.this.getView().findViewById(R.id.options).setVisibility(MiuiVpnEditFragment.this.mOptions.isChecked() ? 0 : 8);
                    MiuiVpnEditFragment miuiVpnEditFragment = MiuiVpnEditFragment.this;
                    miuiVpnEditFragment.mChoice = miuiVpnEditFragment.mOptions.isChecked();
                }
            });
            if (!this.mProfile.searchDomains.isEmpty() || !this.mProfile.dnsServers.isEmpty() || !this.mProfile.routes.isEmpty()) {
                this.mOptions.performClick();
                getView().findViewById(i).setVisibility(0);
                this.mChoice = this.mOptions.isChecked();
            }
        }
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.button_delete);
        addAnim(viewGroup);
        if (!this.mArgs.getBoolean("profile_add", true)) {
            viewGroup.setVisibility(0);
            viewGroup.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.vpn2.MiuiVpnEditFragment.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    new AlertDialog.Builder(MiuiVpnEditFragment.this.mContext, R.style.AlertDialog_Theme_DayNight).setTitle(R.string.vpn_menu_delete).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.vpn2.MiuiVpnEditFragment.2.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i2) {
                            MiuiVpnEditFragment.this.onSave(true);
                        }
                    }).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show().setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.vpn2.MiuiVpnEditFragment.2.2
                        @Override // android.content.DialogInterface.OnDismissListener
                        public void onDismiss(DialogInterface dialogInterface) {
                            MiuiVpnEditFragment.this.finish();
                        }
                    });
                }
            });
        }
        this.mKeyboardHelper = KeyboardHelper.assistActivity(getActivity());
    }
}
