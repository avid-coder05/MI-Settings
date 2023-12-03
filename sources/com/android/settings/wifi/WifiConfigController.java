package com.android.settings.wifi;

import android.content.Context;
import android.content.res.Resources;
import android.net.InetAddresses;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.ProxyInfo;
import android.net.StaticIpConfiguration;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.android.net.module.util.NetUtils;
import com.android.net.module.util.ProxyUtils;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.utils.AndroidKeystoreAliasLoader;
import com.android.settings.wifi.details.WifiPrivacyPreferenceController;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settings.wifi.operatorutils.Operator;
import com.android.settings.wifi.operatorutils.OperatorFactory;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.wifi.AccessPoint;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import miui.os.Build;
import miuix.appcompat.widget.Spinner;

/* loaded from: classes2.dex */
public class WifiConfigController implements TextWatcher, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, TextView.OnEditorActionListener, View.OnKeyListener {
    static final int PRIVACY_SPINNER_INDEX_DEVICE_MAC = 1;
    static final int PRIVACY_SPINNER_INDEX_RANDOMIZED_MAC = 0;
    static final String[] UNDESIRED_CERTIFICATES = {"MacRandSecret", "MacRandSapSecret"};
    private static final int[] WAPI_PSK_TYPE = {0, 1};
    private final AccessPoint mAccessPoint;
    int mAccessPointSecurity;
    private final List<SubscriptionInfo> mActiveSubscriptionInfos;
    private final WifiConfigUiBase mConfigUi;
    private Context mContext;
    private int mDefaultEapMethod;
    private TextView mDns1View;
    private TextView mDns2View;
    private String mDoNotProvideEapUserCertString;
    private TextView mEapAnonymousView;
    private Spinner mEapCaCertSpinner;
    private TextView mEapDomainView;
    private TextView mEapIdentityView;
    Spinner mEapMethodSpinner;
    private Spinner mEapOcspSpinner;
    Spinner mEapSimSpinner;
    private Spinner mEapUserCertSpinner;
    private boolean mForceUpdateEapOptionFields;
    private TextView mGatewayView;
    private boolean mHaveWapiCert;
    private Spinner mHiddenSettingsSpinner;
    private TextView mHiddenWarningView;
    private boolean mHideEapOptionFields;
    private ProxyInfo mHttpProxy;
    private TextView mIpAddressView;
    private IpConfiguration.IpAssignment mIpAssignment;
    private Spinner mIpSettingsSpinner;
    private Spinner mLPrivacySettingsSpinner;
    private String[] mLevels;
    private Spinner mMeteredSettingsSpinner;
    private int mMode;
    private String mMultipleCertSetString;
    private TextView mNetworkPrefixLengthView;
    private TextView mPasswordView;
    private ArrayAdapter<CharSequence> mPhase2Adapter;
    private ArrayAdapter<CharSequence> mPhase2PeapAdapter;
    private Spinner mPhase2Spinner;
    private ArrayAdapter<CharSequence> mPhase2TtlsAdapter;
    private Spinner mPrivacySettingsSpinner;
    private TextView mProxyExclusionListView;
    private TextView mProxyHostView;
    private TextView mProxyPacView;
    private TextView mProxyPortView;
    private IpConfiguration.ProxySettings mProxySettings;
    private Spinner mProxySettingsSpinner;
    private boolean mRequestFocus;
    Integer[] mSecurityInPosition;
    private Spinner mSecuritySpinner;
    private CheckBox mSharedCheckBox;
    private int mSlotid;
    private ImageButton mSsidScanButton;
    private TextView mSsidView;
    private StaticIpConfiguration mStaticIpConfiguration;
    private String mUnspecifiedCertString;
    private String mUseSystemCertsString;
    private final View mView;
    private Spinner mWapiCertSpinner;
    private Spinner mWapiPskTypeSpinner;
    private final WifiManager mWifiManager;

    public WifiConfigController(WifiConfigUiBase wifiConfigUiBase, View view, AccessPoint accessPoint, int i) {
        this(wifiConfigUiBase, view, accessPoint, i, true);
    }

    public WifiConfigController(WifiConfigUiBase wifiConfigUiBase, View view, AccessPoint accessPoint, int i, WifiManager wifiManager) {
        this.mHaveWapiCert = false;
        this.mIpAssignment = IpConfiguration.IpAssignment.UNASSIGNED;
        this.mProxySettings = IpConfiguration.ProxySettings.UNASSIGNED;
        this.mHttpProxy = null;
        this.mStaticIpConfiguration = null;
        this.mRequestFocus = true;
        this.mSlotid = 0;
        this.mDefaultEapMethod = -1;
        this.mActiveSubscriptionInfos = new ArrayList();
        this.mConfigUi = wifiConfigUiBase;
        this.mView = view;
        this.mAccessPoint = accessPoint;
        this.mContext = wifiConfigUiBase.getContext();
        this.mWifiManager = wifiManager;
        initWifiConfigController(accessPoint, i);
    }

    public WifiConfigController(WifiConfigUiBase wifiConfigUiBase, View view, AccessPoint accessPoint, int i, boolean z) {
        this.mHaveWapiCert = false;
        this.mIpAssignment = IpConfiguration.IpAssignment.UNASSIGNED;
        this.mProxySettings = IpConfiguration.ProxySettings.UNASSIGNED;
        this.mHttpProxy = null;
        this.mStaticIpConfiguration = null;
        this.mRequestFocus = true;
        this.mSlotid = 0;
        this.mDefaultEapMethod = -1;
        this.mActiveSubscriptionInfos = new ArrayList();
        this.mConfigUi = wifiConfigUiBase;
        this.mView = view;
        this.mAccessPoint = accessPoint;
        Context context = wifiConfigUiBase.getContext();
        this.mContext = context;
        this.mRequestFocus = z;
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        initWifiConfigController(accessPoint, i);
    }

    private void addRow(ViewGroup viewGroup, int i, String str) {
        View inflate = this.mConfigUi.getLayoutInflater().inflate(R.layout.wifi_dialog_row, viewGroup, false);
        ((TextView) inflate.findViewById(R.id.name)).setText(i);
        ((TextView) inflate.findViewById(R.id.value)).setText(str);
        viewGroup.addView(inflate);
    }

    private void configureSecuritySpinner() {
        int i;
        int i2;
        this.mConfigUi.setTitle(R.string.wifi_add_network);
        TextView textView = (TextView) this.mView.findViewById(R.id.ssid);
        this.mSsidView = textView;
        textView.addTextChangedListener(this);
        Spinner spinner = (Spinner) this.mView.findViewById(R.id.security);
        this.mSecuritySpinner = spinner;
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this.mContext, R.layout.miuix_appcompat_simple_spinner_layout_integrated, 16908308);
        arrayAdapter.setDropDownViewResource(R.layout.miuix_appcompat_simple_spinner_dropdown_item);
        arrayAdapter.add(this.mContext.getString(R.string.wifi_security_none));
        this.mSecurityInPosition[0] = 0;
        if (this.mWifiManager.isEnhancedOpenSupported()) {
            arrayAdapter.add(this.mContext.getString(R.string.wifi_security_owe));
            this.mSecurityInPosition[1] = 4;
            i = 2;
        } else {
            i = 1;
        }
        arrayAdapter.add(this.mContext.getString(R.string.wifi_security_wep));
        int i3 = i + 1;
        this.mSecurityInPosition[i] = 1;
        arrayAdapter.add(this.mContext.getString(R.string.wifi_security_wpa_wpa2));
        int i4 = i3 + 1;
        this.mSecurityInPosition[i3] = 2;
        if (this.mWifiManager.isWpa3SaeSupported()) {
            arrayAdapter.add(this.mContext.getString(R.string.wifi_security_sae));
            int i5 = i4 + 1;
            this.mSecurityInPosition[i4] = 5;
            arrayAdapter.add(this.mContext.getString(R.string.wifi_security_eap_wpa_wpa2));
            int i6 = i5 + 1;
            this.mSecurityInPosition[i5] = 3;
            arrayAdapter.add(this.mContext.getString(R.string.wifi_security_eap_wpa3));
            i2 = i6 + 1;
            this.mSecurityInPosition[i6] = 7;
        } else {
            arrayAdapter.add(this.mContext.getString(R.string.wifi_security_eap));
            this.mSecurityInPosition[i4] = 3;
            i2 = i4 + 1;
        }
        if (this.mWifiManager.isWpa3SuiteBSupported()) {
            arrayAdapter.add(this.mContext.getString(R.string.wifi_security_eap_suiteb));
            this.mSecurityInPosition[i2] = 6;
            i2++;
        }
        if (this.mWifiManager.isWapiSupported()) {
            arrayAdapter.add(this.mContext.getString(R.string.wifi_security_wapi_psk));
            this.mSecurityInPosition[i2] = 11;
            arrayAdapter.add(this.mContext.getString(R.string.wifi_security_wapi_cert));
            this.mSecurityInPosition[i2 + 1] = 12;
        }
        arrayAdapter.notifyDataSetChanged();
        this.mView.findViewById(R.id.type).setVisibility(0);
        showIpConfigFields();
        showProxyFields();
        this.mView.findViewById(R.id.hidden_settings_field).setVisibility(0);
        ((CheckBox) this.mView.findViewById(R.id.wifi_advanced_togglebox)).setOnCheckedChangeListener(this);
        setAdvancedOptionAccessibilityString();
    }

    private SpannableString[] createAccessibleEntries(CharSequence[] charSequenceArr, CharSequence[] charSequenceArr2) {
        SpannableString[] spannableStringArr = new SpannableString[charSequenceArr.length];
        for (int i = 0; i < charSequenceArr.length; i++) {
            spannableStringArr[i] = Utils.createAccessibleSequence(charSequenceArr[i], charSequenceArr2[i].toString());
        }
        return spannableStringArr;
    }

    private Inet4Address getIPv4Address(String str) {
        try {
            return (Inet4Address) InetAddresses.parseNumericAddress(str);
        } catch (ClassCastException | IllegalArgumentException unused) {
            return null;
        }
    }

    private ArrayAdapter<CharSequence> getSpinnerAdapter(int i) {
        return getSpinnerAdapter(this.mContext.getResources().getStringArray(i));
    }

    private ArrayAdapter<CharSequence> getSpinnerAdapterWithEapMethodsTts(int i) {
        Resources resources = this.mContext.getResources();
        String[] stringArray = resources.getStringArray(i);
        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<>(this.mContext, R.layout.miuix_appcompat_simple_spinner_layout_integrated, 16908308, createAccessibleEntries(stringArray, findAndReplaceTargetStrings(stringArray, resources.getStringArray(R.array.wifi_eap_method_target_strings), resources.getStringArray(R.array.wifi_eap_method_tts_strings))));
        arrayAdapter.setDropDownViewResource(R.layout.miuix_appcompat_simple_spinner_dropdown_item);
        return arrayAdapter;
    }

    private void hideSoftKeyboard(IBinder iBinder) {
        ((InputMethodManager) this.mContext.getSystemService(InputMethodManager.class)).hideSoftInputFromWindow(iBinder, 0);
    }

    /* JADX WARN: Removed duplicated region for block: B:52:0x01f4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void initWifiConfigController(com.android.settingslib.wifi.AccessPoint r11, int r12) {
        /*
            Method dump skipped, instructions count: 1006
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiConfigController.initWifiConfigController(com.android.settingslib.wifi.AccessPoint, int):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x0066  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0077 A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean ipAndProxyFieldsAreValid() {
        /*
            r6 = this;
            miuix.appcompat.widget.Spinner r0 = r6.mIpSettingsSpinner
            r1 = 1
            if (r0 == 0) goto Le
            int r0 = r0.getSelectedItemPosition()
            if (r0 != r1) goto Le
            android.net.IpConfiguration$IpAssignment r0 = android.net.IpConfiguration.IpAssignment.STATIC
            goto L10
        Le:
            android.net.IpConfiguration$IpAssignment r0 = android.net.IpConfiguration.IpAssignment.DHCP
        L10:
            r6.mIpAssignment = r0
            android.net.IpConfiguration$IpAssignment r2 = android.net.IpConfiguration.IpAssignment.STATIC
            r3 = 0
            if (r0 != r2) goto L25
            android.net.StaticIpConfiguration r0 = new android.net.StaticIpConfiguration
            r0.<init>()
            r6.mStaticIpConfiguration = r0
            int r0 = r6.validateIpConfigFields(r0)
            if (r0 == 0) goto L25
            return r3
        L25:
            miuix.appcompat.widget.Spinner r0 = r6.mProxySettingsSpinner
            int r0 = r0.getSelectedItemPosition()
            android.net.IpConfiguration$ProxySettings r2 = android.net.IpConfiguration.ProxySettings.NONE
            r6.mProxySettings = r2
            r2 = 0
            r6.mHttpProxy = r2
            if (r0 != r1) goto L78
            android.widget.TextView r2 = r6.mProxyHostView
            if (r2 == 0) goto L78
            android.net.IpConfiguration$ProxySettings r0 = android.net.IpConfiguration.ProxySettings.STATIC
            r6.mProxySettings = r0
            java.lang.CharSequence r0 = r2.getText()
            java.lang.String r0 = r0.toString()
            android.widget.TextView r2 = r6.mProxyPortView
            java.lang.CharSequence r2 = r2.getText()
            java.lang.String r2 = r2.toString()
            android.widget.TextView r4 = r6.mProxyExclusionListView
            java.lang.CharSequence r4 = r4.getText()
            java.lang.String r4 = r4.toString()
            int r5 = java.lang.Integer.parseInt(r2)     // Catch: java.lang.NumberFormatException -> L61
            int r2 = com.android.settings.ProxySelector.validate(r0, r2, r4)     // Catch: java.lang.NumberFormatException -> L62
            goto L64
        L61:
            r5 = r3
        L62:
            int r2 = com.android.settings.R.string.proxy_error_invalid_port
        L64:
            if (r2 != 0) goto L77
            java.lang.String r2 = ","
            java.lang.String[] r2 = r4.split(r2)
            java.util.List r2 = java.util.Arrays.asList(r2)
            android.net.ProxyInfo r0 = android.net.ProxyInfo.buildDirectProxy(r0, r5, r2)
            r6.mHttpProxy = r0
            goto L9f
        L77:
            return r3
        L78:
            r2 = 2
            if (r0 != r2) goto L9f
            android.widget.TextView r0 = r6.mProxyPacView
            if (r0 == 0) goto L9f
            android.net.IpConfiguration$ProxySettings r2 = android.net.IpConfiguration.ProxySettings.PAC
            r6.mProxySettings = r2
            java.lang.CharSequence r0 = r0.getText()
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 == 0) goto L8e
            return r3
        L8e:
            java.lang.String r0 = r0.toString()
            android.net.Uri r0 = android.net.Uri.parse(r0)
            if (r0 != 0) goto L99
            return r3
        L99:
            android.net.ProxyInfo r0 = android.net.ProxyInfo.buildPacProxy(r0)
            r6.mHttpProxy = r0
        L9f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiConfigController.ipAndProxyFieldsAreValid():boolean");
    }

    private boolean isWapiPskValid() {
        if (this.mPasswordView.length() < 8 || this.mPasswordView.length() > 64) {
            return false;
        }
        return WAPI_PSK_TYPE[this.mWapiPskTypeSpinner.getSelectedItemPosition()] != 1 || (this.mPasswordView.length() % 2 == 0 && this.mPasswordView.getText().toString().matches("[0-9A-Fa-f]*"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$afterTextChanged$2() {
        showWarningMessagesIfAppropriate();
        enableSubmitIfAppropriate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$loadCertificates$1(String str) {
        for (String str2 : UNDESIRED_CERTIFICATES) {
            if (str.startsWith(str2)) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String[] lambda$loadSims$0(int i) {
        return new String[i];
    }

    private void loadWapiCertificates(Spinner spinner) {
        Context context = this.mConfigUi.getContext();
        context.getString(R.string.wifi_unspecified);
        context.getString(R.string.wapi_auto_sel_cert);
        ArrayList arrayList = new ArrayList();
        if (arrayList.size() > 1) {
            this.mHaveWapiCert = true;
        } else {
            this.mHaveWapiCert = false;
        }
        MiuiUtils.getInstance().setSpinnerAdapter(this.mContext, (String[]) arrayList.toArray(new String[0]), spinner);
    }

    private void setAccessibilityDelegateForSecuritySpinners() {
        View.AccessibilityDelegate accessibilityDelegate = new View.AccessibilityDelegate() { // from class: com.android.settings.wifi.WifiConfigController.1
            @Override // android.view.View.AccessibilityDelegate
            public void sendAccessibilityEvent(View view, int i) {
                if (i == 4) {
                    return;
                }
                super.sendAccessibilityEvent(view, i);
            }
        };
        this.mEapMethodSpinner.setAccessibilityDelegate(accessibilityDelegate);
        this.mPhase2Spinner.setAccessibilityDelegate(accessibilityDelegate);
        this.mEapCaCertSpinner.setAccessibilityDelegate(accessibilityDelegate);
        this.mEapOcspSpinner.setAccessibilityDelegate(accessibilityDelegate);
        this.mEapUserCertSpinner.setAccessibilityDelegate(accessibilityDelegate);
    }

    private void setAdvancedOptionAccessibilityString() {
        ((CheckBox) this.mView.findViewById(R.id.wifi_advanced_togglebox)).setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: com.android.settings.wifi.WifiConfigController.2
            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                accessibilityNodeInfo.setCheckable(false);
                accessibilityNodeInfo.setClassName(null);
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, WifiConfigController.this.mContext.getString(R.string.wifi_advanced_toggle_description_collapsed)));
            }
        });
    }

    private void setAnonymousIdentInvisible() {
        this.mView.findViewById(R.id.l_anonymous).setVisibility(8);
        this.mEapAnonymousView.setText("");
    }

    private void setCaCertInvisible() {
        this.mView.findViewById(R.id.l_ca_cert).setVisibility(8);
        setSelection(this.mEapCaCertSpinner, this.mUnspecifiedCertString);
    }

    private void setDomainInvisible() {
        this.mView.findViewById(R.id.l_domain).setVisibility(8);
        this.mEapDomainView.setText("");
    }

    private void setIdentityInvisible() {
        this.mView.findViewById(R.id.l_identity).setVisibility(8);
    }

    private void setOcspInvisible() {
        this.mView.findViewById(R.id.l_ocsp).setVisibility(8);
        this.mEapOcspSpinner.setSelection(0);
    }

    private void setPasswordInvisible() {
        this.mPasswordView.setText("");
        this.mView.findViewById(R.id.password_layout).setVisibility(8);
        this.mView.findViewById(R.id.show_password_layout).setVisibility(8);
    }

    private void setPhase2Invisible() {
        this.mView.findViewById(R.id.l_phase2).setVisibility(8);
    }

    private void setSelection(Spinner spinner, String str) {
        if (str != null) {
            ArrayAdapter arrayAdapter = (ArrayAdapter) spinner.getAdapter();
            for (int count = arrayAdapter.getCount() - 1; count >= 0; count--) {
                if (str.equals(arrayAdapter.getItem(count))) {
                    spinner.setSelection(count);
                    return;
                }
            }
        }
    }

    private void setUserCertInvisible() {
        this.mView.findViewById(R.id.l_user_cert).setVisibility(8);
        setSelection(this.mEapUserCertSpinner, this.mUnspecifiedCertString);
    }

    private void setVisibility(int i, int i2) {
        View findViewById = this.mView.findViewById(i);
        if (findViewById != null) {
            findViewById.setVisibility(i2);
        }
    }

    private void showEapFieldsByMethod(int i) {
        this.mView.findViewById(R.id.l_method).setVisibility(0);
        this.mView.findViewById(R.id.l_identity).setVisibility(0);
        this.mView.findViewById(R.id.l_domain).setVisibility(0);
        View view = this.mView;
        int i2 = R.id.l_ca_cert;
        view.findViewById(i2).setVisibility(0);
        this.mView.findViewById(R.id.l_ocsp).setVisibility(0);
        this.mView.findViewById(R.id.password_layout).setVisibility(0);
        this.mView.findViewById(R.id.show_password_layout).setVisibility(0);
        View view2 = this.mView;
        int i3 = R.id.l_sim;
        view2.findViewById(i3).setVisibility(0);
        this.mConfigUi.getContext();
        switch (i) {
            case 0:
                ArrayAdapter<CharSequence> arrayAdapter = this.mPhase2Adapter;
                ArrayAdapter<CharSequence> arrayAdapter2 = this.mPhase2PeapAdapter;
                if (arrayAdapter != arrayAdapter2) {
                    this.mPhase2Adapter = arrayAdapter2;
                    this.mPhase2Spinner.setAdapter((SpinnerAdapter) arrayAdapter2);
                }
                this.mView.findViewById(R.id.l_phase2).setVisibility(0);
                this.mView.findViewById(R.id.l_anonymous).setVisibility(0);
                showPeapFields();
                setUserCertInvisible();
                break;
            case 1:
                this.mView.findViewById(R.id.l_user_cert).setVisibility(0);
                setPhase2Invisible();
                setAnonymousIdentInvisible();
                setPasswordInvisible();
                this.mView.findViewById(i3).setVisibility(8);
                break;
            case 2:
                ArrayAdapter<CharSequence> arrayAdapter3 = this.mPhase2Adapter;
                ArrayAdapter<CharSequence> arrayAdapter4 = this.mPhase2TtlsAdapter;
                if (arrayAdapter3 != arrayAdapter4) {
                    this.mPhase2Adapter = arrayAdapter4;
                    this.mPhase2Spinner.setAdapter((SpinnerAdapter) arrayAdapter4);
                }
                this.mView.findViewById(R.id.l_phase2).setVisibility(0);
                this.mView.findViewById(R.id.l_anonymous).setVisibility(0);
                setUserCertInvisible();
                this.mView.findViewById(i3).setVisibility(8);
                break;
            case 3:
                setPhase2Invisible();
                setCaCertInvisible();
                setOcspInvisible();
                setDomainInvisible();
                setAnonymousIdentInvisible();
                setUserCertInvisible();
                this.mView.findViewById(i3).setVisibility(8);
                break;
            case 4:
            case 5:
            case 6:
                setPhase2Invisible();
                setAnonymousIdentInvisible();
                setCaCertInvisible();
                setOcspInvisible();
                setDomainInvisible();
                setUserCertInvisible();
                setPasswordInvisible();
                setIdentityInvisible();
                break;
        }
        if (this.mView.findViewById(i2).getVisibility() == 8 || !((String) this.mEapCaCertSpinner.getSelectedItem()).equals(this.mUnspecifiedCertString)) {
            return;
        }
        setDomainInvisible();
        setOcspInvisible();
    }

    private void showIpConfigFields() {
        StaticIpConfiguration staticIpConfiguration;
        this.mView.findViewById(R.id.ip_fields).setVisibility(0);
        AccessPoint accessPoint = this.mAccessPoint;
        WifiConfiguration config = (accessPoint == null || !accessPoint.isSaved()) ? null : this.mAccessPoint.getConfig();
        if (this.mIpSettingsSpinner.getSelectedItemPosition() != 1) {
            this.mView.findViewById(R.id.staticip).setVisibility(8);
            return;
        }
        this.mView.findViewById(R.id.staticip).setVisibility(0);
        if (this.mIpAddressView == null) {
            TextView textView = (TextView) this.mView.findViewById(R.id.ipaddress);
            this.mIpAddressView = textView;
            textView.addTextChangedListener(this);
            TextView textView2 = (TextView) this.mView.findViewById(R.id.gateway);
            this.mGatewayView = textView2;
            textView2.addTextChangedListener(this);
            TextView textView3 = (TextView) this.mView.findViewById(R.id.network_prefix_length);
            this.mNetworkPrefixLengthView = textView3;
            textView3.addTextChangedListener(this);
            TextView textView4 = (TextView) this.mView.findViewById(R.id.dns1);
            this.mDns1View = textView4;
            textView4.addTextChangedListener(this);
            TextView textView5 = (TextView) this.mView.findViewById(R.id.dns2);
            this.mDns2View = textView5;
            textView5.addTextChangedListener(this);
        }
        if (config == null || (staticIpConfiguration = config.getIpConfiguration().getStaticIpConfiguration()) == null) {
            return;
        }
        if (staticIpConfiguration.getIpAddress() != null) {
            this.mIpAddressView.setText(staticIpConfiguration.getIpAddress().getAddress().getHostAddress());
            this.mNetworkPrefixLengthView.setText(Integer.toString(staticIpConfiguration.getIpAddress().getPrefixLength()));
        }
        if (staticIpConfiguration.getGateway() != null) {
            this.mGatewayView.setText(staticIpConfiguration.getGateway().getHostAddress());
        }
        Iterator<InetAddress> it = staticIpConfiguration.getDnsServers().iterator();
        if (it.hasNext()) {
            this.mDns1View.setText(it.next().getHostAddress());
        }
        if (it.hasNext()) {
            this.mDns2View.setText(it.next().getHostAddress());
        }
    }

    private void showPeapFields() {
        int selectedItemPosition = this.mPhase2Spinner.getSelectedItemPosition();
        if (selectedItemPosition == 2 || selectedItemPosition == 3 || selectedItemPosition == 4) {
            this.mEapIdentityView.setText("");
            this.mView.findViewById(R.id.l_identity).setVisibility(8);
            setPasswordInvisible();
            this.mView.findViewById(R.id.l_sim).setVisibility(0);
            return;
        }
        this.mView.findViewById(R.id.l_identity).setVisibility(0);
        this.mView.findViewById(R.id.l_anonymous).setVisibility(0);
        this.mView.findViewById(R.id.password_layout).setVisibility(0);
        this.mView.findViewById(R.id.show_password_layout).setVisibility(0);
        this.mView.findViewById(R.id.l_sim).setVisibility(8);
    }

    private void showProxyFields() {
        ProxyInfo httpProxy;
        ProxyInfo httpProxy2;
        this.mView.findViewById(R.id.proxy_settings_fields).setVisibility(0);
        AccessPoint accessPoint = this.mAccessPoint;
        WifiConfiguration config = (accessPoint == null || !accessPoint.isSaved()) ? null : this.mAccessPoint.getConfig();
        if (this.mProxySettingsSpinner.getSelectedItemPosition() != 1) {
            if (this.mProxySettingsSpinner.getSelectedItemPosition() != 2) {
                setVisibility(R.id.proxy_warning_limited_support, 8);
                setVisibility(R.id.proxy_fields, 8);
                setVisibility(R.id.proxy_pac_field, 8);
                return;
            }
            setVisibility(R.id.proxy_warning_limited_support, 8);
            setVisibility(R.id.proxy_fields, 8);
            setVisibility(R.id.proxy_pac_field, 0);
            if (this.mProxyPacView == null) {
                TextView textView = (TextView) this.mView.findViewById(R.id.proxy_pac);
                this.mProxyPacView = textView;
                textView.addTextChangedListener(this);
            }
            if (config == null || (httpProxy = config.getHttpProxy()) == null) {
                return;
            }
            this.mProxyPacView.setText(httpProxy.getPacFileUrl().toString());
            return;
        }
        setVisibility(R.id.proxy_warning_limited_support, 0);
        setVisibility(R.id.proxy_fields, 0);
        setVisibility(R.id.proxy_pac_field, 8);
        if (this.mProxyHostView == null) {
            TextView textView2 = (TextView) this.mView.findViewById(R.id.proxy_hostname);
            this.mProxyHostView = textView2;
            textView2.addTextChangedListener(this);
            TextView textView3 = (TextView) this.mView.findViewById(R.id.proxy_port);
            this.mProxyPortView = textView3;
            textView3.addTextChangedListener(this);
            TextView textView4 = (TextView) this.mView.findViewById(R.id.proxy_exclusionlist);
            this.mProxyExclusionListView = textView4;
            textView4.addTextChangedListener(this);
        }
        if (config == null || (httpProxy2 = config.getHttpProxy()) == null) {
            return;
        }
        this.mProxyHostView.setText(httpProxy2.getHost());
        this.mProxyPortView.setText(Integer.toString(httpProxy2.getPort()));
        this.mProxyExclusionListView.setText(ProxyUtils.exclusionListAsString(httpProxy2.getExclusionList()));
    }

    private void showSecurityFields(boolean z, boolean z2) {
        boolean z3;
        int i;
        AccessPoint accessPoint;
        int i2 = this.mAccessPointSecurity;
        if (i2 == 0 || i2 == 4) {
            this.mView.findViewById(R.id.security_fields).setVisibility(8);
            return;
        }
        this.mView.findViewById(R.id.security_fields).setVisibility(0);
        if (this.mPasswordView == null) {
            TextView textView = (TextView) this.mView.findViewById(R.id.password);
            this.mPasswordView = textView;
            textView.addTextChangedListener(this);
            this.mPasswordView.setOnEditorActionListener(this);
            this.mPasswordView.setOnKeyListener(this);
            ((CheckBox) this.mView.findViewById(R.id.show_password)).setOnCheckedChangeListener(this);
            AccessPoint accessPoint2 = this.mAccessPoint;
            if (accessPoint2 != null && accessPoint2.isSaved()) {
                this.mPasswordView.setHint(R.string.wifi_unchanged);
            }
        }
        if (this.mAccessPointSecurity != 11) {
            this.mView.findViewById(R.id.wapi_psk).setVisibility(8);
        } else {
            this.mView.findViewById(R.id.wapi_psk).setVisibility(0);
            this.mWapiPskTypeSpinner = (Spinner) this.mView.findViewById(R.id.wapi_psk_type);
            AccessPoint accessPoint3 = this.mAccessPoint;
            if (accessPoint3 != null && accessPoint3.isSaved()) {
                this.mAccessPoint.getConfig();
            }
            this.mWapiPskTypeSpinner.setOnItemSelectedListener(this);
        }
        if (this.mAccessPointSecurity == 12) {
            this.mView.findViewById(R.id.password_layout).setVisibility(8);
            this.mView.findViewById(R.id.show_password_layout).setVisibility(8);
            this.mView.findViewById(R.id.eap).setVisibility(8);
            this.mView.findViewById(R.id.wapi_cert).setVisibility(0);
            Spinner spinner = (Spinner) this.mView.findViewById(R.id.wapi_cert_select);
            this.mWapiCertSpinner = spinner;
            loadWapiCertificates(spinner);
            AccessPoint accessPoint4 = this.mAccessPoint;
            if (accessPoint4 == null || !accessPoint4.isSaved()) {
                return;
            }
            WifiConfiguration config = this.mAccessPoint.getConfig();
            if (config.enterpriseConfig.getWapiCertSuite().equals("auto")) {
                this.mWapiCertSpinner.setSelection(0);
                return;
            } else {
                setSelection(this.mWapiCertSpinner, config.enterpriseConfig.getWapiCertSuite());
                return;
            }
        }
        this.mView.findViewById(R.id.wapi_cert).setVisibility(8);
        this.mView.findViewById(R.id.password_layout).setVisibility(0);
        this.mView.findViewById(R.id.show_password_layout).setVisibility(0);
        int i3 = this.mAccessPointSecurity;
        if (i3 != 3 && i3 != 6) {
            this.mView.findViewById(R.id.eap).setVisibility(8);
            return;
        }
        this.mView.findViewById(R.id.eap).setVisibility(0);
        if (this.mEapMethodSpinner == null) {
            Spinner spinner2 = (Spinner) this.mView.findViewById(R.id.method);
            this.mEapMethodSpinner = spinner2;
            spinner2.setOnItemSelectedListener(this);
            this.mEapSimSpinner = (Spinner) this.mView.findViewById(R.id.sim);
            Spinner spinner3 = (Spinner) this.mView.findViewById(R.id.phase2);
            this.mPhase2Spinner = spinner3;
            spinner3.setOnItemSelectedListener(this);
            Spinner spinner4 = (Spinner) this.mView.findViewById(R.id.ca_cert);
            this.mEapCaCertSpinner = spinner4;
            spinner4.setOnItemSelectedListener(this);
            this.mEapOcspSpinner = (Spinner) this.mView.findViewById(R.id.ocsp);
            MiuiUtils miuiUtils = MiuiUtils.getInstance();
            Context context = this.mContext;
            miuiUtils.setSpinnerAdapter(context, context.getResources().getStringArray(R.array.eap_ocsp_type), this.mEapOcspSpinner);
            TextView textView2 = (TextView) this.mView.findViewById(R.id.domain);
            this.mEapDomainView = textView2;
            textView2.addTextChangedListener(this);
            Spinner spinner5 = (Spinner) this.mView.findViewById(R.id.user_cert);
            this.mEapUserCertSpinner = spinner5;
            spinner5.setOnItemSelectedListener(this);
            this.mEapIdentityView = (TextView) this.mView.findViewById(R.id.identity);
            this.mEapAnonymousView = (TextView) this.mView.findViewById(R.id.anonymous);
            setAccessibilityDelegateForSecuritySpinners();
            z3 = true;
        } else {
            z3 = false;
        }
        if (z) {
            if (this.mAccessPointSecurity == 6) {
                this.mEapMethodSpinner.setAdapter((SpinnerAdapter) getSpinnerAdapter(R.array.wifi_eap_method));
                this.mEapMethodSpinner.setSelection(1);
                this.mEapMethodSpinner.setEnabled(false);
            } else if (com.android.settingslib.Utils.isWifiOnly(this.mContext) || !this.mContext.getResources().getBoolean(17891525)) {
                this.mEapMethodSpinner.setAdapter((SpinnerAdapter) getSpinnerAdapter(R.array.eap_method_without_sim_auth));
                this.mEapMethodSpinner.setEnabled(true);
            } else {
                this.mEapMethodSpinner.setAdapter((SpinnerAdapter) getSpinnerAdapterWithEapMethodsTts(R.array.wifi_eap_method));
                this.mEapMethodSpinner.setEnabled(true);
            }
        }
        if (z2) {
            loadSims();
            AndroidKeystoreAliasLoader androidKeystoreAliasLoader = getAndroidKeystoreAliasLoader();
            loadCertificates(this.mEapCaCertSpinner, androidKeystoreAliasLoader.getCaCertAliases(), null, false, true);
            loadCertificates(this.mEapUserCertSpinner, androidKeystoreAliasLoader.getKeyCertAliases(), this.mDoNotProvideEapUserCertString, false, false);
            setSelection(this.mEapCaCertSpinner, this.mUseSystemCertsString);
        }
        if (z3 && (accessPoint = this.mAccessPoint) != null && accessPoint.isSaved()) {
            WifiConfiguration config2 = this.mAccessPoint.getConfig();
            WifiEnterpriseConfig wifiEnterpriseConfig = config2.enterpriseConfig;
            int eapMethod = wifiEnterpriseConfig.getEapMethod();
            int phase2Method = wifiEnterpriseConfig.getPhase2Method();
            this.mEapMethodSpinner.setSelection(eapMethod);
            showEapFieldsByMethod(eapMethod);
            if (eapMethod != 0) {
                if (eapMethod == 2) {
                    if (phase2Method == 1) {
                        this.mPhase2Spinner.setSelection(0);
                    } else if (phase2Method == 2) {
                        this.mPhase2Spinner.setSelection(1);
                    } else if (phase2Method == 3) {
                        this.mPhase2Spinner.setSelection(2);
                    } else if (phase2Method != 4) {
                        Log.e("WifiConfigController", "Invalid phase 2 method " + phase2Method);
                    } else {
                        this.mPhase2Spinner.setSelection(3);
                    }
                }
            } else if (phase2Method == 3) {
                this.mPhase2Spinner.setSelection(0);
            } else if (phase2Method == 4) {
                this.mPhase2Spinner.setSelection(1);
            } else if (phase2Method == 5) {
                this.mPhase2Spinner.setSelection(2);
            } else if (phase2Method == 6) {
                this.mPhase2Spinner.setSelection(3);
            } else if (phase2Method != 7) {
                Log.e("WifiConfigController", "Invalid phase 2 method " + phase2Method);
            } else {
                this.mPhase2Spinner.setSelection(4);
            }
            if (wifiEnterpriseConfig.isAuthenticationSimBased()) {
                int i4 = 0;
                while (true) {
                    if (i4 >= this.mActiveSubscriptionInfos.size()) {
                        break;
                    } else if (config2.carrierId == this.mActiveSubscriptionInfos.get(i4).getCarrierId()) {
                        this.mEapSimSpinner.setSelection(i4);
                        break;
                    } else {
                        i4++;
                    }
                }
            }
            if (TextUtils.isEmpty(wifiEnterpriseConfig.getCaPath())) {
                String[] caCertificateAliases = wifiEnterpriseConfig.getCaCertificateAliases();
                if (caCertificateAliases == null) {
                    setSelection(this.mEapCaCertSpinner, this.mUnspecifiedCertString);
                } else if (caCertificateAliases.length == 1) {
                    setSelection(this.mEapCaCertSpinner, caCertificateAliases[0]);
                } else {
                    loadCertificates(this.mEapCaCertSpinner, getAndroidKeystoreAliasLoader().getCaCertAliases(), null, true, true);
                    setSelection(this.mEapCaCertSpinner, this.mMultipleCertSetString);
                }
            } else {
                setSelection(this.mEapCaCertSpinner, this.mUseSystemCertsString);
            }
            this.mEapOcspSpinner.setSelection(wifiEnterpriseConfig.getOcsp());
            this.mEapDomainView.setText(wifiEnterpriseConfig.getDomainSuffixMatch());
            String clientCertificateAlias = wifiEnterpriseConfig.getClientCertificateAlias();
            if (TextUtils.isEmpty(clientCertificateAlias)) {
                setSelection(this.mEapUserCertSpinner, this.mDoNotProvideEapUserCertString);
            } else {
                setSelection(this.mEapUserCertSpinner, clientCertificateAlias);
            }
            this.mEapIdentityView.setText(wifiEnterpriseConfig.getIdentity());
            this.mEapAnonymousView.setText(wifiEnterpriseConfig.getAnonymousIdentity());
        } else {
            showEapFieldsByMethod(this.mEapMethodSpinner.getSelectedItemPosition());
        }
        if (!isCarrierCustomization() || (i = this.mDefaultEapMethod) == -1) {
            mayUpdateEapOptionFields(false);
        } else {
            showEapMethodFieldOnly(i);
        }
    }

    private int validateIpConfigFields(StaticIpConfiguration staticIpConfiguration) {
        TextView textView = this.mIpAddressView;
        if (textView == null) {
            return 0;
        }
        String charSequence = textView.getText().toString();
        if (TextUtils.isEmpty(charSequence)) {
            return R.string.wifi_ip_settings_invalid_ip_address;
        }
        Inet4Address iPv4Address = getIPv4Address(charSequence);
        if (iPv4Address == null || iPv4Address.equals(Inet4Address.ANY)) {
            return R.string.wifi_ip_settings_invalid_ip_address;
        }
        StaticIpConfiguration.Builder ipAddress = new StaticIpConfiguration.Builder().setDnsServers(staticIpConfiguration.getDnsServers()).setDomains(staticIpConfiguration.getDomains()).setGateway(staticIpConfiguration.getGateway()).setIpAddress(staticIpConfiguration.getIpAddress());
        int i = -1;
        try {
            try {
                try {
                    i = Integer.parseInt(this.mNetworkPrefixLengthView.getText().toString());
                } catch (IllegalArgumentException unused) {
                    int i2 = R.string.wifi_ip_settings_invalid_ip_address;
                    ipAddress.build();
                    return i2;
                }
            } catch (NumberFormatException unused2) {
                this.mNetworkPrefixLengthView.setText(this.mConfigUi.getContext().getString(R.string.wifi_network_prefix_length_hint));
            }
            if (i >= 0 && i <= 32) {
                ipAddress.setIpAddress(new LinkAddress(iPv4Address, i));
                String charSequence2 = this.mGatewayView.getText().toString();
                if (TextUtils.isEmpty(charSequence2)) {
                    try {
                        byte[] address = NetUtils.getNetworkPart(iPv4Address, i).getAddress();
                        address[address.length - 1] = 1;
                        this.mGatewayView.setText(InetAddress.getByAddress(address).getHostAddress());
                    } catch (RuntimeException | UnknownHostException unused3) {
                    }
                } else {
                    Inet4Address iPv4Address2 = getIPv4Address(charSequence2);
                    if (iPv4Address2 == null) {
                        int i3 = R.string.wifi_ip_settings_invalid_gateway;
                        ipAddress.build();
                        return i3;
                    } else if (iPv4Address2.isMulticastAddress()) {
                        int i4 = R.string.wifi_ip_settings_invalid_gateway;
                        ipAddress.build();
                        return i4;
                    } else {
                        ipAddress.setGateway(iPv4Address2);
                    }
                }
                String charSequence3 = this.mDns1View.getText().toString();
                ArrayList arrayList = new ArrayList();
                if (TextUtils.isEmpty(charSequence3)) {
                    this.mDns1View.setText(this.mConfigUi.getContext().getString(R.string.wifi_dns1_hint));
                } else {
                    Inet4Address iPv4Address3 = getIPv4Address(charSequence3);
                    if (iPv4Address3 == null) {
                        int i5 = R.string.wifi_ip_settings_invalid_dns;
                        ipAddress.build();
                        return i5;
                    }
                    arrayList.add(iPv4Address3);
                }
                if (this.mDns2View.length() > 0) {
                    Inet4Address iPv4Address4 = getIPv4Address(this.mDns2View.getText().toString());
                    if (iPv4Address4 == null) {
                        int i6 = R.string.wifi_ip_settings_invalid_dns;
                        ipAddress.build();
                        return i6;
                    }
                    arrayList.add(iPv4Address4);
                }
                ipAddress.setDnsServers(arrayList);
                ipAddress.build();
                return 0;
            }
            int i7 = R.string.wifi_ip_settings_invalid_network_prefix_length;
            ipAddress.build();
            return i7;
        } catch (Throwable th) {
            ipAddress.build();
            throw th;
        }
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable editable) {
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.wifi.WifiConfigController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                WifiConfigController.this.lambda$afterTextChanged$2();
            }
        });
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void displayOcrPwd(String str) {
        this.mPasswordView.setText(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void enableSubmitIfAppropriate() {
        Button submitButton = this.mConfigUi.getSubmitButton();
        if (submitButton == null) {
            return;
        }
        submitButton.setEnabled(isSubmittable());
    }

    CharSequence[] findAndReplaceTargetStrings(CharSequence[] charSequenceArr, CharSequence[] charSequenceArr2, CharSequence[] charSequenceArr3) {
        if (charSequenceArr2.length != charSequenceArr3.length) {
            return charSequenceArr;
        }
        CharSequence[] charSequenceArr4 = new CharSequence[charSequenceArr.length];
        for (int i = 0; i < charSequenceArr.length; i++) {
            charSequenceArr4[i] = charSequenceArr[i];
            for (int i2 = 0; i2 < charSequenceArr2.length; i2++) {
                if (TextUtils.equals(charSequenceArr[i], charSequenceArr2[i2])) {
                    charSequenceArr4[i] = charSequenceArr3[i2];
                }
            }
        }
        return charSequenceArr4;
    }

    protected void forceUpdateEapOptionFields(boolean z) {
        this.mForceUpdateEapOptionFields = true;
        this.mHideEapOptionFields = z;
        mayUpdateEapOptionFields(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void forceUpdateOptionFields(boolean z) {
        if (this.mWifiManager.isConnectedMacRandomizationSupported()) {
            this.mView.findViewById(R.id.privacy_settings_fields).setVisibility(z ? 8 : 0);
            String string = z ? this.mContext.getString(R.string.wifi_eap_options_advanced) : this.mContext.getString(R.string.wifi_eap_options_simple);
            if (this.mConfigUi.getCancelButton() == null) {
                this.mConfigUi.setCancelButton(string);
            } else {
                this.mConfigUi.getCancelButton().setText(string);
            }
        }
        forceUpdateEapOptionFields(z);
    }

    public AccessPoint getAccessPoint() {
        return this.mAccessPoint;
    }

    AndroidKeystoreAliasLoader getAndroidKeystoreAliasLoader() {
        return new AndroidKeystoreAliasLoader(102);
    }

    public WifiConfiguration getConfig() {
        if (this.mMode == 0) {
            return null;
        }
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        AccessPoint accessPoint = this.mAccessPoint;
        if (accessPoint == null) {
            wifiConfiguration.SSID = AccessPoint.convertToQuotedString(this.mSsidView.getText().toString());
            wifiConfiguration.hiddenSSID = this.mHiddenSettingsSpinner.getSelectedItemPosition() == 1;
        } else if (accessPoint.isSaved()) {
            wifiConfiguration.networkId = this.mAccessPoint.getConfig().networkId;
            wifiConfiguration.hiddenSSID = this.mAccessPoint.getConfig().hiddenSSID;
        } else {
            wifiConfiguration.SSID = AccessPoint.convertToQuotedString(this.mAccessPoint.getSsidStr());
        }
        wifiConfiguration.shared = this.mSharedCheckBox.isChecked();
        int i = this.mAccessPointSecurity;
        if (i == 11) {
            wifiConfiguration.allowedKeyManagement.set(13);
            if (this.mPasswordView.length() != 0) {
                if (this.mWapiPskTypeSpinner.getSelectedItemPosition() == 0) {
                    wifiConfiguration.preSharedKey = '\"' + this.mPasswordView.getText().toString() + '\"';
                } else {
                    wifiConfiguration.preSharedKey = this.mPasswordView.getText().toString();
                }
            }
        } else if (i != 12) {
            switch (i) {
                case 0:
                    wifiConfiguration.setSecurityParams(0);
                    break;
                case 1:
                    wifiConfiguration.setSecurityParams(1);
                    if (this.mPasswordView.length() != 0) {
                        int length = this.mPasswordView.length();
                        String charSequence = this.mPasswordView.getText().toString();
                        if ((length != 10 && length != 26 && length != 58) || !charSequence.matches("[0-9A-Fa-f]*")) {
                            wifiConfiguration.wepKeys[0] = '\"' + charSequence + '\"';
                            break;
                        } else {
                            wifiConfiguration.wepKeys[0] = charSequence;
                            break;
                        }
                    }
                    break;
                case 2:
                    wifiConfiguration.setSecurityParams(2);
                    if (this.mPasswordView.length() != 0) {
                        String charSequence2 = this.mPasswordView.getText().toString();
                        if (!charSequence2.matches("[0-9A-Fa-f]{64}")) {
                            wifiConfiguration.preSharedKey = '\"' + charSequence2 + '\"';
                            break;
                        } else {
                            wifiConfiguration.preSharedKey = charSequence2;
                            break;
                        }
                    }
                    break;
                case 3:
                case 6:
                case 7:
                    if (i == 6) {
                        wifiConfiguration.setSecurityParams(5);
                    } else if (i == 7) {
                        wifiConfiguration.setSecurityParams(9);
                    } else {
                        wifiConfiguration.setSecurityParams(3);
                    }
                    wifiConfiguration.enterpriseConfig = new WifiEnterpriseConfig();
                    if (this.mHideEapOptionFields) {
                        this.mEapMethodSpinner.setSelection(0);
                        this.mEapCaCertSpinner.setSelection(0);
                        this.mEapUserCertSpinner.setSelection(0);
                        this.mEapDomainView.setText("");
                    }
                    int selectedItemPosition = this.mEapMethodSpinner.getSelectedItemPosition();
                    int selectedItemPosition2 = this.mPhase2Spinner.getSelectedItemPosition();
                    wifiConfiguration.enterpriseConfig.setEapMethod(selectedItemPosition);
                    if (selectedItemPosition == 0) {
                        if (selectedItemPosition2 == 0) {
                            wifiConfiguration.enterpriseConfig.setPhase2Method(3);
                        } else if (selectedItemPosition2 == 1) {
                            wifiConfiguration.enterpriseConfig.setPhase2Method(4);
                        } else if (selectedItemPosition2 == 2) {
                            wifiConfiguration.enterpriseConfig.setPhase2Method(5);
                        } else if (selectedItemPosition2 == 3) {
                            wifiConfiguration.enterpriseConfig.setPhase2Method(6);
                        } else if (selectedItemPosition2 != 4) {
                            Log.e("WifiConfigController", "Unknown phase2 method" + selectedItemPosition2);
                        } else {
                            wifiConfiguration.enterpriseConfig.setPhase2Method(7);
                        }
                        if (this.mHideEapOptionFields) {
                            wifiConfiguration.enterpriseConfig.setPhase2Method(0);
                        }
                    } else if (selectedItemPosition == 2) {
                        if (selectedItemPosition2 == 0) {
                            wifiConfiguration.enterpriseConfig.setPhase2Method(1);
                        } else if (selectedItemPosition2 == 1) {
                            wifiConfiguration.enterpriseConfig.setPhase2Method(2);
                        } else if (selectedItemPosition2 == 2) {
                            wifiConfiguration.enterpriseConfig.setPhase2Method(3);
                        } else if (selectedItemPosition2 != 3) {
                            Log.e("WifiConfigController", "Unknown phase2 method" + selectedItemPosition2);
                        } else {
                            wifiConfiguration.enterpriseConfig.setPhase2Method(4);
                        }
                    }
                    String str = (String) this.mEapCaCertSpinner.getSelectedItem();
                    wifiConfiguration.enterpriseConfig.setCaCertificateAliases(null);
                    wifiConfiguration.enterpriseConfig.setCaPath(null);
                    wifiConfiguration.enterpriseConfig.setDomainSuffixMatch(this.mEapDomainView.getText().toString());
                    if (!str.equals(this.mUnspecifiedCertString)) {
                        if (str.equals(this.mUseSystemCertsString)) {
                            wifiConfiguration.enterpriseConfig.setCaPath("/system/etc/security/cacerts");
                        } else if (str.equals(this.mMultipleCertSetString)) {
                            AccessPoint accessPoint2 = this.mAccessPoint;
                            if (accessPoint2 != null) {
                                if (!accessPoint2.isSaved()) {
                                    Log.e("WifiConfigController", "Multiple certs can only be set when editing saved network");
                                }
                                wifiConfiguration.enterpriseConfig.setCaCertificateAliases(this.mAccessPoint.getConfig().enterpriseConfig.getCaCertificateAliases());
                            }
                        } else {
                            wifiConfiguration.enterpriseConfig.setCaCertificateAliases(new String[]{str});
                        }
                    }
                    if (wifiConfiguration.enterpriseConfig.getCaCertificateAliases() != null && wifiConfiguration.enterpriseConfig.getCaPath() != null) {
                        Log.e("WifiConfigController", "ca_cert (" + wifiConfiguration.enterpriseConfig.getCaCertificateAliases() + ") and ca_path (" + wifiConfiguration.enterpriseConfig.getCaPath() + ") should not both be non-null");
                    }
                    if (str.equals(this.mUnspecifiedCertString)) {
                        wifiConfiguration.enterpriseConfig.setOcsp(0);
                    } else {
                        wifiConfiguration.enterpriseConfig.setOcsp(this.mEapOcspSpinner.getSelectedItemPosition());
                    }
                    String str2 = (String) this.mEapUserCertSpinner.getSelectedItem();
                    if (str2.equals(this.mUnspecifiedCertString) || str2.equals(this.mDoNotProvideEapUserCertString)) {
                        str2 = "";
                    }
                    wifiConfiguration.enterpriseConfig.setClientCertificateAlias(str2);
                    if (selectedItemPosition == 4 || selectedItemPosition == 5 || selectedItemPosition == 6) {
                        wifiConfiguration.enterpriseConfig.setIdentity("");
                        wifiConfiguration.enterpriseConfig.setAnonymousIdentity("");
                    } else if (selectedItemPosition == 3) {
                        wifiConfiguration.enterpriseConfig.setIdentity(this.mEapIdentityView.getText().toString());
                        wifiConfiguration.enterpriseConfig.setAnonymousIdentity("");
                    } else {
                        wifiConfiguration.enterpriseConfig.setIdentity(this.mEapIdentityView.getText().toString());
                        wifiConfiguration.enterpriseConfig.setAnonymousIdentity(this.mEapAnonymousView.getText().toString());
                    }
                    if (!this.mPasswordView.isShown()) {
                        wifiConfiguration.enterpriseConfig.setPassword(this.mPasswordView.getText().toString());
                        break;
                    } else if (this.mPasswordView.length() > 0) {
                        wifiConfiguration.enterpriseConfig.setPassword(this.mPasswordView.getText().toString());
                        break;
                    }
                    break;
                case 4:
                    wifiConfiguration.setSecurityParams(6);
                    break;
                case 5:
                    wifiConfiguration.setSecurityParams(4);
                    if (this.mPasswordView.length() != 0) {
                        wifiConfiguration.preSharedKey = '\"' + this.mPasswordView.getText().toString() + '\"';
                        break;
                    }
                    break;
                default:
                    return null;
            }
        } else {
            wifiConfiguration.allowedKeyManagement.set(14);
            wifiConfiguration.enterpriseConfig.setEapMethod(8);
            if (this.mWapiCertSpinner.getSelectedItemPosition() == 0) {
                wifiConfiguration.enterpriseConfig.setWapiCertSuite("auto");
            } else {
                wifiConfiguration.enterpriseConfig.setWapiCertSuite((String) this.mWapiCertSpinner.getSelectedItem());
            }
        }
        if (wifiConfiguration.enterpriseConfig.isAuthenticationSimBased() && this.mActiveSubscriptionInfos.size() > 0) {
            wifiConfiguration.carrierId = this.mActiveSubscriptionInfos.get(this.mEapSimSpinner.getSelectedItemPosition()).getCarrierId();
        }
        IpConfiguration ipConfiguration = new IpConfiguration();
        ipConfiguration.setIpAssignment(this.mIpAssignment);
        ipConfiguration.setProxySettings(this.mProxySettings);
        ipConfiguration.setStaticIpConfiguration(this.mStaticIpConfiguration);
        ipConfiguration.setHttpProxy(this.mHttpProxy);
        wifiConfiguration.setIpConfiguration(ipConfiguration);
        Spinner spinner = this.mMeteredSettingsSpinner;
        if (spinner != null) {
            wifiConfiguration.meteredOverride = spinner.getSelectedItemPosition();
        }
        Spinner spinner2 = this.mPrivacySettingsSpinner;
        if (spinner2 != null) {
            wifiConfiguration.macRandomizationSetting = spinner2.getSelectedItemPosition() != 0 ? 0 : 1;
        }
        Spinner spinner3 = this.mLPrivacySettingsSpinner;
        if (spinner3 != null && this.mAccessPointSecurity == 3) {
            wifiConfiguration.macRandomizationSetting = WifiPrivacyPreferenceController.translatePrefValueToMacRandomizedValue(spinner3.getSelectedItemPosition());
        }
        return wifiConfiguration;
    }

    String getSignalString() {
        int level;
        if (this.mAccessPoint.isReachable() && (level = this.mAccessPoint.getLevel()) > -1) {
            String[] strArr = this.mLevels;
            if (level < strArr.length) {
                return strArr[level];
            }
            return null;
        }
        return null;
    }

    ArrayAdapter<CharSequence> getSpinnerAdapter(String[] strArr) {
        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<>(this.mContext, R.layout.miuix_appcompat_simple_spinner_layout_integrated, 16908308, strArr);
        arrayAdapter.setDropDownViewResource(R.layout.miuix_appcompat_simple_spinner_dropdown_item);
        return arrayAdapter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void hideForgetButton() {
        Button forgetButton = this.mConfigUi.getForgetButton();
        if (forgetButton == null) {
            return;
        }
        forgetButton.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void hideSubmitButton() {
        Button submitButton = this.mConfigUi.getSubmitButton();
        if (submitButton == null) {
            return;
        }
        submitButton.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isCarrierCustomization() {
        Operator operatorFactory;
        if (this.mAccessPoint == null || (operatorFactory = OperatorFactory.getInstance(this.mContext)) == null || !operatorFactory.isOpCustomization(this.mAccessPoint.getSsidStr())) {
            return false;
        }
        this.mSlotid = operatorFactory.getSlotId();
        this.mDefaultEapMethod = operatorFactory.getDefaultEapMethod();
        return true;
    }

    boolean isSubmittable() {
        AccessPoint accessPoint;
        TextView textView = this.mPasswordView;
        boolean z = true;
        if (textView == null || ((this.mAccessPointSecurity != 1 || isValidWepPassword(textView.getText().toString())) && ((this.mAccessPointSecurity != 2 || isValidPsk(this.mPasswordView.getText().toString())) && ((this.mAccessPointSecurity != 5 || isValidSaePassword(this.mPasswordView.getText().toString())) && (this.mAccessPointSecurity != 11 || isWapiPskValid()))))) {
            z = false;
        }
        TextView textView2 = this.mPasswordView;
        if (textView2 != null && textView2.length() == 0 && (accessPoint = this.mAccessPoint) != null && accessPoint.isSaved()) {
            z = false;
        }
        TextView textView3 = this.mSsidView;
        if ((textView3 == null || textView3.length() != 0) && !z) {
            return ipAndProxyFieldsAreValid();
        }
        return false;
    }

    boolean isValidPsk(String str) {
        if (str.length() == 64 && str.matches("[0-9A-Fa-f]{64}")) {
            return true;
        }
        return str.length() >= 8 && str.length() <= 63;
    }

    boolean isValidSaePassword(String str) {
        return str.length() >= 1 && str.length() <= 63;
    }

    boolean isValidWepPassword(String str) {
        if (str != null) {
            int length = str.length();
            return ((length == 10 || length == 26) && str.matches("[0-9A-Fa-f]*")) || length == 5 || length == 13;
        }
        return false;
    }

    void loadCertificates(Spinner spinner, Collection<String> collection, String str, boolean z, boolean z2) {
        this.mConfigUi.getContext();
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.mUnspecifiedCertString);
        if (z) {
            arrayList.add(this.mMultipleCertSetString);
        }
        if (z2) {
            arrayList.add(this.mUseSystemCertsString);
        }
        if (collection != null && collection.size() != 0) {
            arrayList.addAll((Collection) collection.stream().filter(new Predicate() { // from class: com.android.settings.wifi.WifiConfigController$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$loadCertificates$1;
                    lambda$loadCertificates$1 = WifiConfigController.lambda$loadCertificates$1((String) obj);
                    return lambda$loadCertificates$1;
                }
            }).collect(Collectors.toList()));
        }
        if (!TextUtils.isEmpty(str) && this.mAccessPointSecurity != 6) {
            arrayList.add(str);
        }
        if (arrayList.size() == 2) {
            arrayList.remove(this.mUnspecifiedCertString);
            spinner.setEnabled(false);
        } else {
            spinner.setEnabled(true);
        }
        spinner.setAdapter((SpinnerAdapter) getSpinnerAdapter((String[]) arrayList.toArray(new String[arrayList.size()])));
    }

    void loadSims() {
        List<SubscriptionInfo> activeSubscriptionInfoList = ((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class)).getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList == null) {
            activeSubscriptionInfoList = Collections.EMPTY_LIST;
        }
        this.mActiveSubscriptionInfos.clear();
        for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
            for (SubscriptionInfo subscriptionInfo2 : this.mActiveSubscriptionInfos) {
                subscriptionInfo.getCarrierId();
                subscriptionInfo2.getCarrierId();
            }
            this.mActiveSubscriptionInfos.add(subscriptionInfo);
        }
        if (this.mActiveSubscriptionInfos.size() == 0) {
            this.mEapSimSpinner.setAdapter((SpinnerAdapter) getSpinnerAdapter(new String[]{this.mContext.getString(R.string.wifi_no_sim_card)}));
            this.mEapSimSpinner.setSelection(0);
            this.mEapSimSpinner.setEnabled(false);
            return;
        }
        String[] strArr = (String[]) SubscriptionUtil.getUniqueSubscriptionDisplayNames(this.mContext).values().stream().toArray(new IntFunction() { // from class: com.android.settings.wifi.WifiConfigController$$ExternalSyntheticLambda1
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                String[] lambda$loadSims$0;
                lambda$loadSims$0 = WifiConfigController.lambda$loadSims$0(i);
                return lambda$loadSims$0;
            }
        });
        this.mEapSimSpinner.setAdapter((SpinnerAdapter) getSpinnerAdapter(strArr));
        this.mEapSimSpinner.setSelection(0);
        if (strArr.length == 1) {
            this.mEapSimSpinner.setEnabled(false);
        }
    }

    protected void mayUpdateEapOptionFields(boolean z) {
        AccessPoint accessPoint = this.mAccessPoint;
        if (accessPoint == null || TextUtils.isEmpty(accessPoint.getSsidStr()) || this.mAccessPointSecurity != 3) {
            return;
        }
        WifiConfiguration config = this.mAccessPoint.getConfig();
        AccessPoint accessPoint2 = this.mAccessPoint;
        boolean z2 = true;
        boolean z3 = accessPoint2 != null && "CMCC".equals(accessPoint2.getSsidStr());
        if (!this.mForceUpdateEapOptionFields) {
            if (config != null && config.enterpriseConfig.getEapMethod() != 0 && config.enterpriseConfig.getEapMethod() != 2 && config.enterpriseConfig.getEapMethod() != 3 && (!Build.IS_CM_CUSTOMIZATION || !z3)) {
                z2 = false;
            }
            this.mHideEapOptionFields = z2;
        }
        if (this.mHideEapOptionFields) {
            showEapFieldsByMethod(3);
            this.mView.findViewById(R.id.l_sim).setVisibility(8);
            this.mView.findViewById(R.id.l_identity).setVisibility(0);
            this.mView.findViewById(R.id.password_layout).setVisibility(0);
        } else {
            showEapFieldsByMethod(this.mEapMethodSpinner.getSelectedItemPosition());
        }
        this.mView.findViewById(R.id.show_password_layout).setVisibility(8);
        this.mView.findViewById(R.id.l_method).setVisibility((!this.mHideEapOptionFields || (Build.IS_CM_CUSTOMIZATION && z3)) ? 0 : 8);
        if (this.mHideEapOptionFields) {
            View view = this.mView;
            int i = R.id.no_ca_cert_warning;
            if (view.findViewById(i).getVisibility() == 0) {
                this.mView.findViewById(i).setVisibility(8);
            }
        }
        this.mView.findViewById(R.id.privacy_settings_fields).setVisibility(8);
        if (this.mWifiManager.isConnectedMacRandomizationSupported()) {
            this.mView.findViewById(R.id.l_privacy_settings_fields).setVisibility(this.mHideEapOptionFields ? 8 : 0);
        }
        String string = this.mHideEapOptionFields ? this.mContext.getString(R.string.wifi_eap_options_advanced) : this.mContext.getString(R.string.wifi_eap_options_simple);
        if (this.mConfigUi.getCancelButton() == null) {
            this.mConfigUi.setCancelButton(string);
        } else {
            this.mConfigUi.getCancelButton().setText(string);
        }
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        if (compoundButton.getId() == R.id.show_password) {
            int selectionEnd = this.mPasswordView.getSelectionEnd();
            this.mPasswordView.setInputType((z ? 144 : 128) | 1);
            if (selectionEnd >= 0) {
                ((EditText) this.mPasswordView).setSelection(selectionEnd);
            }
        } else if (compoundButton.getId() == R.id.wifi_advanced_togglebox) {
            hideSoftKeyboard(this.mView.getWindowToken());
            compoundButton.setVisibility(8);
            this.mView.findViewById(R.id.wifi_advanced_fields).setVisibility(0);
        }
    }

    @Override // android.widget.TextView.OnEditorActionListener
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (textView == this.mPasswordView && i == 6 && isSubmittable()) {
            this.mConfigUi.dispatchSubmit();
            return true;
        }
        return false;
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        if (adapterView == this.mSecuritySpinner) {
            this.mAccessPointSecurity = this.mSecurityInPosition[i].intValue();
            showSecurityFields(true, true);
            if (WifiDppUtils.isSupportEnrolleeQrCodeScanner(this.mContext, this.mAccessPointSecurity)) {
                this.mSsidScanButton.setVisibility(0);
            } else {
                this.mSsidScanButton.setVisibility(8);
            }
        } else {
            Spinner spinner = this.mEapMethodSpinner;
            if (adapterView == spinner) {
                showSecurityFields(false, true);
            } else if (adapterView == this.mEapCaCertSpinner) {
                showSecurityFields(false, false);
            } else if (adapterView == this.mPhase2Spinner && spinner.getSelectedItemPosition() == 0) {
                showPeapFields();
            } else if (adapterView == this.mProxySettingsSpinner) {
                showProxyFields();
            } else if (adapterView == this.mHiddenSettingsSpinner) {
                this.mHiddenWarningView.setVisibility(i != 0 ? 0 : 8);
            } else {
                showIpConfigFields();
            }
        }
        showWarningMessagesIfAppropriate();
        enableSubmitIfAppropriate();
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (view == this.mPasswordView && i == 66 && isSubmittable()) {
            this.mConfigUi.dispatchSubmit();
            return true;
        }
        return false;
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    protected void showEapMethodFieldOnly(int i) {
        AccessPoint accessPoint = this.mAccessPoint;
        if (accessPoint == null || TextUtils.isEmpty(accessPoint.getSsidStr()) || this.mAccessPointSecurity != 3 || com.android.settingslib.Utils.isWifiOnly(this.mContext) || !this.mContext.getResources().getBoolean(17891525)) {
            return;
        }
        showEapFieldsByMethod(i);
        this.mView.findViewById(R.id.show_password_layout).setVisibility(8);
        Spinner spinner = this.mEapMethodSpinner;
        if (spinner != null) {
            spinner.setSelection(i);
            this.mEapMethodSpinner.setEnabled(false);
        }
    }

    void showWarningMessagesIfAppropriate() {
        View view = this.mView;
        int i = R.id.no_user_cert_warning;
        view.findViewById(i).setVisibility(8);
        View view2 = this.mView;
        int i2 = R.id.no_domain_warning;
        view2.findViewById(i2).setVisibility(8);
        View view3 = this.mView;
        int i3 = R.id.ssid_too_long_warning;
        view3.findViewById(i3).setVisibility(8);
        TextView textView = this.mSsidView;
        if (textView != null && WifiUtils.isSSIDTooLong(textView.getText().toString())) {
            this.mView.findViewById(i3).setVisibility(0);
        }
        if (this.mEapCaCertSpinner != null && this.mView.findViewById(R.id.l_ca_cert).getVisibility() != 8 && this.mEapDomainView != null && this.mView.findViewById(R.id.l_domain).getVisibility() != 8 && TextUtils.isEmpty(this.mEapDomainView.getText().toString())) {
            this.mView.findViewById(i2).setVisibility(0);
        }
        if (this.mAccessPointSecurity == 6 && this.mEapMethodSpinner.getSelectedItemPosition() == 1 && ((String) this.mEapUserCertSpinner.getSelectedItem()).equals(this.mUnspecifiedCertString)) {
            this.mView.findViewById(i).setVisibility(0);
        }
    }

    public void updatePassword() {
        ((TextView) this.mView.findViewById(R.id.password)).setInputType((((CheckBox) this.mView.findViewById(R.id.show_password)).isChecked() ? 144 : 128) | 1);
    }
}
