package com.android.settings.wifi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.BaseEditFragment;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.utils.AndroidKeystoreAliasLoader;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.wifi.details2.WifiPrivacyPreferenceController2;
import com.android.settingslib.Utils;
import com.android.settingslib.wifi.AccessPoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import miuix.appcompat.widget.Spinner;

/* loaded from: classes2.dex */
public class MiuiAddNetworkFragment extends BaseEditFragment implements TextWatcher, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    static final String[] UNDESIRED_CERTIFICATES = {"MacRandSecret", "MacRandSapSecret"};
    private int mAccessPointSecurity;
    private Context mContext;
    private String mDoNotProvideEapUserCertString;
    private String mDoNotValidateEapServerString;
    private TextView mEapAnonymousView;
    private Spinner mEapCaCertSpinner;
    private TextView mEapDomainView;
    private TextView mEapIdentityView;
    private Spinner mEapMethodSpinner;
    private Spinner mEapUserCertSpinner;
    private Spinner mHiddenSettingsSpinner;
    private String mInstallCertsString;
    private Spinner mIpFieldsSpinner;
    private LinearLayout mIpFieldsSpinnerParent;
    private Spinner mLPrivacySettingsSpinner;
    private int mMaxSsidLength = 0;
    private String mMultipleCertSetString;
    private TextView mPasswordView;
    private ArrayAdapter mPhase2Adapter;
    private ArrayAdapter mPhase2PeapAdapter;
    private Spinner mPhase2Spinner;
    private ArrayAdapter mPhase2TtlsAdapter;
    private Spinner mPrivacySettingsSpinner;
    private Spinner mProxySettingsSpinner;
    private LinearLayout mProxySettingsSpinnerParent;
    private Spinner mSecuritySpinner;
    private Spinner mSimCardSpinner;
    private ArrayList<String> mSimDisplayNames;
    private TextView mSsidView;
    private TelephonyManager mTelephonyManager;
    private Handler mTextViewChangedHandler;
    private String mUnspecifiedCertString;
    private String mUseSystemCertsString;
    private View mView;
    private WifiManager mWifiManager;

    private void adaptSoftInput() {
        FragmentActivity activity = getActivity();
        boolean z = false;
        if (activity != null && Settings.Secure.getInt(activity.getContentResolver(), "navigation_mode", 0) == 2) {
            z = true;
        }
        if (z) {
            try {
                final View decorView = activity.getWindow().getDecorView();
                decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.android.settings.wifi.MiuiAddNetworkFragment$$ExternalSyntheticLambda0
                    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                    public final void onGlobalLayout() {
                        MiuiAddNetworkFragment.lambda$adaptSoftInput$0(decorView);
                    }
                });
            } catch (Exception unused) {
                Log.e("MiuiAddNetworkFragment", "Resize decorView layout error!");
            }
        }
    }

    private void checkSsidLength() {
        String charSequence = this.mSsidView.getText().toString();
        if (charSequence.getBytes().length <= this.mMaxSsidLength) {
            return;
        }
        do {
            charSequence = charSequence.substring(0, charSequence.length() - 1);
        } while (charSequence.getBytes().length > this.mMaxSsidLength);
        this.mSsidView.setText(charSequence);
        Selection.setSelection((Spannable) this.mSsidView.getText(), charSequence.length());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x003c, code lost:
    
        if (r1 != 26) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0044, code lost:
    
        if (r4.matches("[0-9A-Fa-f]*") == false) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0046, code lost:
    
        if (r1 == 5) goto L28;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x004a, code lost:
    
        if (r1 == 13) goto L28;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0052, code lost:
    
        if (r1 != 5) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x005a, code lost:
    
        if (r7.mPasswordView.length() >= 8) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0064, code lost:
    
        if (r7.mPasswordView.length() > 63) goto L37;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void enableSubmitIfAppropriate() {
        /*
            r7 = this;
            android.widget.TextView r0 = r7.mSsidView
            r1 = 1
            r2 = 8
            r3 = 0
            if (r0 == 0) goto L18
            int r0 = r0.length()
            if (r0 <= 0) goto L18
            android.widget.TextView r0 = r7.mSsidView
            int r0 = r0.getVisibility()
            if (r0 == r2) goto L18
            r0 = r1
            goto L19
        L18:
            r0 = r3
        L19:
            if (r0 == 0) goto L67
            android.widget.TextView r4 = r7.mPasswordView
            if (r4 == 0) goto L67
            int r5 = r7.mAccessPointSecurity
            r6 = 5
            if (r5 != r1) goto L4d
            int r1 = r4.length()
            if (r1 != 0) goto L2c
        L2a:
            r0 = r3
            goto L4d
        L2c:
            android.widget.TextView r4 = r7.mPasswordView
            java.lang.CharSequence r4 = r4.getText()
            java.lang.String r4 = r4.toString()
            r5 = 10
            if (r1 == r5) goto L3e
            r5 = 26
            if (r1 != r5) goto L46
        L3e:
            java.lang.String r5 = "[0-9A-Fa-f]*"
            boolean r4 = r4.matches(r5)
            if (r4 != 0) goto L4d
        L46:
            if (r1 == r6) goto L4d
            r4 = 13
            if (r1 == r4) goto L4d
            goto L2a
        L4d:
            int r1 = r7.mAccessPointSecurity
            r4 = 2
            if (r1 == r4) goto L54
            if (r1 != r6) goto L5c
        L54:
            android.widget.TextView r1 = r7.mPasswordView
            int r1 = r1.length()
            if (r1 < r2) goto L68
        L5c:
            android.widget.TextView r1 = r7.mPasswordView
            int r1 = r1.length()
            r2 = 63
            if (r1 <= r2) goto L67
            goto L68
        L67:
            r3 = r0
        L68:
            r7.onEditStateChange(r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.MiuiAddNetworkFragment.enableSubmitIfAppropriate():void");
    }

    private AndroidKeystoreAliasLoader getAndroidKeystoreAliasLoader() {
        return new AndroidKeystoreAliasLoader(102);
    }

    private WifiConfiguration getConfig() {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = AccessPoint.convertToQuotedString(this.mSsidView.getText().toString());
        Spinner spinner = this.mHiddenSettingsSpinner;
        wifiConfiguration.hiddenSSID = spinner == null || spinner.getSelectedItemPosition() == 1;
        int i = this.mAccessPointSecurity;
        if (i == 0) {
            wifiConfiguration.allowedKeyManagement.set(0);
        } else if (i != 1) {
            if (i != 2) {
                if (i == 3) {
                    wifiConfiguration.allowedKeyManagement.set(2);
                    wifiConfiguration.allowedKeyManagement.set(3);
                    wifiConfiguration.enterpriseConfig = new WifiEnterpriseConfig();
                    int selectedItemPosition = this.mEapMethodSpinner.getSelectedItemPosition();
                    int selectedItemPosition2 = this.mPhase2Spinner.getSelectedItemPosition();
                    wifiConfiguration.enterpriseConfig.setEapMethod(selectedItemPosition);
                    if (selectedItemPosition != 0) {
                        if (selectedItemPosition == 4 || selectedItemPosition == 5 || selectedItemPosition == 6) {
                            SubscriptionInfo activeSubscriptionInfoForSimSlotIndex = SubscriptionManager.from(this.mContext).getActiveSubscriptionInfoForSimSlotIndex(this.mSimCardSpinner.getSelectedItemPosition());
                            if (activeSubscriptionInfoForSimSlotIndex != null) {
                                wifiConfiguration.carrierId = activeSubscriptionInfoForSimSlotIndex.getCarrierId();
                                wifiConfiguration.subscriptionId = activeSubscriptionInfoForSimSlotIndex.getSubscriptionId();
                            }
                        } else {
                            wifiConfiguration.enterpriseConfig.setPhase2Method(selectedItemPosition2);
                        }
                    } else if (selectedItemPosition2 == 0) {
                        wifiConfiguration.enterpriseConfig.setPhase2Method(0);
                    } else if (selectedItemPosition2 == 1) {
                        wifiConfiguration.enterpriseConfig.setPhase2Method(3);
                    } else if (selectedItemPosition2 == 2) {
                        wifiConfiguration.enterpriseConfig.setPhase2Method(4);
                    } else if (selectedItemPosition2 == 3) {
                        wifiConfiguration.enterpriseConfig.setPhase2Method(5);
                    } else if (selectedItemPosition2 == 4) {
                        wifiConfiguration.enterpriseConfig.setPhase2Method(6);
                    } else if (selectedItemPosition2 != 5) {
                        Log.e("MiuiAddNetworkFragment", "Unknown phase2 method" + selectedItemPosition2);
                    } else {
                        wifiConfiguration.enterpriseConfig.setPhase2Method(7);
                    }
                    String str = (String) this.mEapCaCertSpinner.getSelectedItem();
                    wifiConfiguration.enterpriseConfig.setCaCertificateAliases(null);
                    wifiConfiguration.enterpriseConfig.setCaPath(null);
                    wifiConfiguration.enterpriseConfig.setDomainSuffixMatch(this.mEapDomainView.getText().toString());
                    if (!str.equals(this.mUnspecifiedCertString) && !str.equals(this.mDoNotValidateEapServerString)) {
                        if (str.equals(this.mUseSystemCertsString)) {
                            wifiConfiguration.enterpriseConfig.setCaPath("/system/etc/security/cacerts");
                        } else if (!str.equals(this.mMultipleCertSetString)) {
                            wifiConfiguration.enterpriseConfig.setCaCertificateAliases(new String[]{str});
                        }
                    }
                    if (wifiConfiguration.enterpriseConfig.getCaCertificateAliases() != null && wifiConfiguration.enterpriseConfig.getCaPath() != null) {
                        Log.e("MiuiAddNetworkFragment", "ca_cert (" + wifiConfiguration.enterpriseConfig.getCaCertificateAliases() + ") and ca_path (" + wifiConfiguration.enterpriseConfig.getCaPath() + ") should not both be non-null");
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
                    } else if (this.mPasswordView.length() > 0) {
                        wifiConfiguration.enterpriseConfig.setPassword(this.mPasswordView.getText().toString());
                    }
                } else if (i != 5) {
                    return null;
                }
            }
            if (i == 2) {
                wifiConfiguration.allowedKeyManagement.set(1);
            } else {
                wifiConfiguration.allowedKeyManagement.set(8);
                wifiConfiguration.requirePmf = true;
            }
            if (this.mPasswordView.length() != 0) {
                String charSequence = this.mPasswordView.getText().toString();
                if (charSequence.matches("[0-9A-Fa-f]{64}")) {
                    wifiConfiguration.preSharedKey = charSequence;
                } else {
                    wifiConfiguration.preSharedKey = '\"' + charSequence + '\"';
                }
            }
        } else {
            wifiConfiguration.allowedKeyManagement.set(0);
            wifiConfiguration.allowedAuthAlgorithms.set(0);
            wifiConfiguration.allowedAuthAlgorithms.set(1);
            if (this.mPasswordView.length() != 0) {
                int length = this.mPasswordView.length();
                String charSequence2 = this.mPasswordView.getText().toString();
                if ((length == 10 || length == 26 || length == 32) && charSequence2.matches("[0-9A-Fa-f]*")) {
                    wifiConfiguration.wepKeys[0] = charSequence2;
                } else {
                    wifiConfiguration.wepKeys[0] = '\"' + charSequence2 + '\"';
                }
            }
        }
        Spinner spinner2 = this.mLPrivacySettingsSpinner;
        if (spinner2 == null || this.mAccessPointSecurity != 3) {
            Spinner spinner3 = this.mPrivacySettingsSpinner;
            if (spinner3 != null) {
                wifiConfiguration.macRandomizationSetting = WifiPrivacyPreferenceController2.translatePrefValueToMacRandomizedValue(spinner3.getSelectedItemPosition());
            }
        } else {
            wifiConfiguration.macRandomizationSetting = WifiPrivacyPreferenceController2.translatePrefValueToMacRandomizedValue(spinner2.getSelectedItemPosition());
        }
        return wifiConfiguration;
    }

    private void getSIMInfo() {
        SubscriptionManager from = SubscriptionManager.from(this.mContext);
        for (int i = 0; i < this.mTelephonyManager.getSimCount(); i++) {
            SubscriptionInfo activeSubscriptionInfoForSimSlotIndex = from.getActiveSubscriptionInfoForSimSlotIndex(i);
            this.mSimDisplayNames.add(activeSubscriptionInfoForSimSlotIndex != null ? String.valueOf(activeSubscriptionInfoForSimSlotIndex.getDisplayName()) : this.mContext.getString(R.string.sim_editor_title, Integer.valueOf(i + 1)));
        }
    }

    private void init() {
        this.mAccessPointSecurity = 0;
        TextView textView = (TextView) this.mView.findViewById(R.id.ssid);
        this.mSsidView = textView;
        textView.addTextChangedListener(this);
        int integer = getResources().getInteger(R.integer.wifi_max_ssid_length);
        this.mMaxSsidLength = integer;
        if (integer > 0) {
            this.mSsidView.setMaxEms(integer);
        }
        this.mView.findViewById(R.id.type).setVisibility(0);
        this.mTextViewChangedHandler = new Handler();
        this.mContext = this.mView.getContext();
        TextView textView2 = (TextView) this.mView.findViewById(R.id.password);
        this.mPasswordView = textView2;
        textView2.addTextChangedListener(this);
        this.mTelephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        this.mSimDisplayNames = new ArrayList<>();
        this.mUnspecifiedCertString = this.mContext.getString(R.string.wifi_unspecified);
        this.mMultipleCertSetString = this.mContext.getString(R.string.wifi_multiple_cert_added);
        this.mUseSystemCertsString = this.mContext.getString(R.string.wifi_use_system_certs);
        this.mDoNotProvideEapUserCertString = this.mContext.getString(R.string.wifi_do_not_provide_eap_user_cert);
        this.mDoNotValidateEapServerString = this.mContext.getString(R.string.wifi_do_not_validate_eap_server);
        this.mInstallCertsString = this.mContext.getString(R.string.wifi_install_credentials);
        this.mSecuritySpinner = (Spinner) this.mView.findViewById(R.id.security);
        WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        this.mWifiManager = wifiManager;
        MiuiUtils.getInstance().setSpinnerAdapter(this.mContext, wifiManager.isWpa3SaeSupported() ? this.mContext.getResources().getStringArray(R.array.add_wifi_security_with_sae) : this.mContext.getResources().getStringArray(R.array.add_wifi_security), this.mSecuritySpinner);
        this.mSecuritySpinner.setVisibility(0);
        this.mSecuritySpinner.setOnItemSelectedListener(this);
        Spinner spinner = this.mSecuritySpinner;
        spinner.setPrompt(spinner.getPrompt());
        Spinner spinner2 = (Spinner) this.mView.findViewById(R.id.sim_card);
        this.mSimCardSpinner = spinner2;
        spinner2.setOnItemSelectedListener(this);
        Spinner spinner3 = (Spinner) this.mView.findViewById(R.id.phase2);
        this.mPhase2Spinner = spinner3;
        spinner3.setPrompt(spinner3.getPrompt());
        Spinner spinner4 = (Spinner) this.mView.findViewById(R.id.ca_cert);
        this.mEapCaCertSpinner = spinner4;
        spinner4.setPrompt(spinner4.getPrompt());
        Spinner spinner5 = (Spinner) this.mView.findViewById(R.id.user_cert);
        this.mEapUserCertSpinner = spinner5;
        spinner5.setPrompt(spinner5.getPrompt());
        this.mEapIdentityView = (TextView) this.mView.findViewById(R.id.identity);
        this.mEapAnonymousView = (TextView) this.mView.findViewById(R.id.anonymous);
        Context context = this.mContext;
        int i = R.layout.miuix_appcompat_simple_spinner_layout_integrated;
        ArrayAdapter arrayAdapter = new ArrayAdapter(context, i, 16908308, context.getResources().getStringArray(R.array.wifi_peap_phase2_entries));
        this.mPhase2PeapAdapter = arrayAdapter;
        int i2 = R.layout.miuix_appcompat_simple_spinner_dropdown_item;
        arrayAdapter.setDropDownViewResource(i2);
        Context context2 = this.mContext;
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(context2, i, 16908308, context2.getResources().getStringArray(R.array.wifi_ttls_phase2_entries));
        this.mPhase2TtlsAdapter = arrayAdapter2;
        arrayAdapter2.setDropDownViewResource(i2);
        this.mPrivacySettingsSpinner = (Spinner) this.mView.findViewById(R.id.privacy_settings);
        this.mLPrivacySettingsSpinner = (Spinner) this.mView.findViewById(R.id.l_privacy_settings);
        String[] stringArray = this.mContext.getResources().getStringArray(R.array.wifi_privacy_entries);
        MiuiUtils.getInstance().setSpinnerAdapter(this.mContext, stringArray, this.mLPrivacySettingsSpinner);
        MiuiUtils.getInstance().setSpinnerAdapter(this.mContext, stringArray, this.mPrivacySettingsSpinner);
        this.mProxySettingsSpinnerParent = (LinearLayout) this.mView.findViewById(R.id.proxy_settings_fields);
        this.mProxySettingsSpinner = (Spinner) this.mView.findViewById(R.id.proxy_settings);
        this.mHiddenSettingsSpinner = (Spinner) this.mView.findViewById(R.id.hidden_settings);
        MiuiUtils.getInstance().setSpinnerAdapter(this.mContext, this.mContext.getResources().getStringArray(R.array.wifi_hidden_entries), this.mHiddenSettingsSpinner);
        this.mHiddenSettingsSpinner.setSelection(1);
        this.mIpFieldsSpinnerParent = (LinearLayout) this.mView.findViewById(R.id.ip_fields);
        this.mIpFieldsSpinner = (Spinner) this.mView.findViewById(R.id.ip_settings);
        int translateMacRandomizedValueToPrefValue = WifiPrivacyPreferenceController2.translateMacRandomizedValueToPrefValue(com.android.settingslib.wifi.WifiUtils.getDefaultWifiPrivacy(this.mContext));
        this.mPrivacySettingsSpinner.setSelection(translateMacRandomizedValueToPrefValue);
        this.mLPrivacySettingsSpinner.setSelection(translateMacRandomizedValueToPrefValue);
        enableSubmitIfAppropriate();
        int color = getActivity().getColor(R.color.bg_spinner_parent);
        MiuiUtils miuiUtils = MiuiUtils.getInstance();
        miuiUtils.setSpinnerDisplayLocation((Spinner) this.mView.findViewById(R.id.method), color);
        miuiUtils.setSpinnerDisplayLocation(this.mSecuritySpinner, color);
        miuiUtils.setSpinnerDisplayLocation(this.mSimCardSpinner, color);
        miuiUtils.setSpinnerDisplayLocation(this.mPhase2Spinner, color);
        miuiUtils.setSpinnerDisplayLocation(this.mEapCaCertSpinner, color);
        miuiUtils.setSpinnerDisplayLocation(this.mEapUserCertSpinner, color);
        miuiUtils.setSpinnerDisplayLocation(this.mPrivacySettingsSpinner, color);
        miuiUtils.setSpinnerDisplayLocation(this.mLPrivacySettingsSpinner, color);
        miuiUtils.setSpinnerDisplayLocation(this.mProxySettingsSpinner, color);
        miuiUtils.setSpinnerDisplayLocation(this.mIpFieldsSpinner, color);
        miuiUtils.setSpinnerDisplayLocation(this.mHiddenSettingsSpinner, color);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$adaptSoftInput$0(View view) {
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int i = (view.getContext().getResources().getDisplayMetrics().heightPixels - rect.bottom) + rect.top + 80;
        if (i != 0) {
            if (view.getPaddingBottom() != i) {
                view.setPadding(0, 0, 0, i);
            }
        } else if (view.getPaddingBottom() != 0) {
            view.setPadding(0, 0, 0, 0);
        }
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

    private void loadCertificates(Spinner spinner, Collection<String> collection, String str, boolean z, boolean z2) {
        spinner.getContext();
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.mUnspecifiedCertString);
        if (z) {
            arrayList.add(this.mMultipleCertSetString);
        }
        if (z2) {
            arrayList.add(this.mUseSystemCertsString);
            arrayList.add(this.mInstallCertsString);
        }
        if (collection != null && collection.size() != 0) {
            arrayList.addAll((Collection) collection.stream().filter(new Predicate() { // from class: com.android.settings.wifi.MiuiAddNetworkFragment$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$loadCertificates$1;
                    lambda$loadCertificates$1 = MiuiAddNetworkFragment.lambda$loadCertificates$1((String) obj);
                    return lambda$loadCertificates$1;
                }
            }).collect(Collectors.toList()));
        }
        if (!TextUtils.isEmpty(str)) {
            arrayList.add(str);
        }
        MiuiUtils.getInstance().setSpinnerAdapter(this.mContext, (String[]) arrayList.toArray(new String[arrayList.size()]), spinner);
    }

    private void mapPositionToSecurityType(int i) {
        if (i == 0 || i == 1 || i == 2 || i == 3) {
            this.mAccessPointSecurity = i;
        } else if (i == 4) {
            this.mAccessPointSecurity = 5;
        } else {
            Log.e("MiuiAddNetworkFragment", "Position out of range, unsupport security type.");
            this.mAccessPointSecurity = 0;
        }
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
        this.mPhase2Spinner.setSelection(0);
    }

    private void setPasswordEapInvisible() {
        this.mPasswordView.setText("");
        this.mView.findViewById(R.id.l_password_layout).setVisibility(8);
    }

    private void setPasswordEapVisible() {
        this.mPasswordView.setText("");
        this.mView.findViewById(R.id.l_password_layout).setVisibility(0);
    }

    private void setPasswordInVisible() {
        this.mPasswordView.setText("");
        this.mView.findViewById(R.id.password_layout).setVisibility(8);
    }

    private void setPasswordVisible() {
        this.mPasswordView.setText("");
        this.mView.findViewById(R.id.password_layout).setVisibility(0);
    }

    private void setPhase2Invisible() {
        this.mView.findViewById(R.id.l_phase2).setVisibility(8);
        this.mPhase2Spinner.setSelection(0);
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

    private void setSimCardInvisible() {
        this.mView.findViewById(R.id.l_sim_card).setVisibility(8);
    }

    private void setUserCertInvisible() {
        this.mView.findViewById(R.id.l_user_cert).setVisibility(8);
        setSelection(this.mEapUserCertSpinner, this.mUnspecifiedCertString);
    }

    private void showEapFieldsByMethod(int i) {
        this.mView.findViewById(R.id.l_method).setVisibility(0);
        this.mView.findViewById(R.id.l_identity).setVisibility(0);
        this.mView.findViewById(R.id.l_domain).setVisibility(0);
        View view = this.mView;
        int i2 = R.id.l_ca_cert;
        view.findViewById(i2).setVisibility(0);
        setPasswordInVisible();
        switch (i) {
            case 0:
                ArrayAdapter arrayAdapter = this.mPhase2Adapter;
                ArrayAdapter arrayAdapter2 = this.mPhase2PeapAdapter;
                if (arrayAdapter != arrayAdapter2) {
                    this.mPhase2Adapter = arrayAdapter2;
                    this.mPhase2Spinner.setAdapter((SpinnerAdapter) arrayAdapter2);
                }
                this.mView.findViewById(R.id.l_phase2).setVisibility(0);
                this.mView.findViewById(R.id.l_anonymous).setVisibility(0);
                showPeapFields();
                setUserCertInvisible();
                setSimCardInvisible();
                setPasswordEapVisible();
                break;
            case 1:
                this.mView.findViewById(R.id.l_user_cert).setVisibility(0);
                setPhase2Invisible();
                setAnonymousIdentInvisible();
                setPasswordEapInvisible();
                setSimCardInvisible();
                break;
            case 2:
                ArrayAdapter arrayAdapter3 = this.mPhase2Adapter;
                ArrayAdapter arrayAdapter4 = this.mPhase2TtlsAdapter;
                if (arrayAdapter3 != arrayAdapter4) {
                    this.mPhase2Adapter = arrayAdapter4;
                    this.mPhase2Spinner.setAdapter((SpinnerAdapter) arrayAdapter4);
                }
                this.mView.findViewById(R.id.l_phase2).setVisibility(0);
                this.mView.findViewById(R.id.l_anonymous).setVisibility(0);
                setUserCertInvisible();
                setSimCardInvisible();
                setPasswordEapVisible();
                break;
            case 3:
                setPhase2Invisible();
                setCaCertInvisible();
                setDomainInvisible();
                setAnonymousIdentInvisible();
                setUserCertInvisible();
                setSimCardInvisible();
                setPasswordEapVisible();
                break;
            case 4:
            case 5:
            case 6:
                MiuiUtils miuiUtils = MiuiUtils.getInstance();
                Context context = this.mContext;
                ArrayList<String> arrayList = this.mSimDisplayNames;
                miuiUtils.setSpinnerAdapter(context, (String[]) arrayList.toArray(new String[arrayList.size()]), this.mSimCardSpinner);
                this.mView.findViewById(R.id.l_sim_card).setVisibility(0);
                setPhase2Invisible();
                setAnonymousIdentInvisible();
                setCaCertInvisible();
                setDomainInvisible();
                setUserCertInvisible();
                setIdentityInvisible();
                setPasswordEapInvisible();
                break;
        }
        if (this.mView.findViewById(i2).getVisibility() != 8) {
            String str = (String) this.mEapCaCertSpinner.getSelectedItem();
            if (str.equals(this.mDoNotValidateEapServerString) || str.equals(this.mUnspecifiedCertString)) {
                setDomainInvisible();
            }
        }
    }

    private void showLPrivacySettingsFields(boolean z) {
        if (this.mWifiManager.isConnectedMacRandomizationSupported()) {
            this.mView.findViewById(R.id.l_privacy_settings_fields).setVisibility(z ? 0 : 8);
        }
    }

    private void showPeapFields() {
        int selectedItemPosition = this.mPhase2Spinner.getSelectedItemPosition();
        if (selectedItemPosition == 3 || selectedItemPosition == 4 || selectedItemPosition == 5) {
            this.mEapIdentityView.setText("");
            this.mView.findViewById(R.id.l_identity).setVisibility(8);
            return;
        }
        this.mView.findViewById(R.id.l_identity).setVisibility(0);
        this.mView.findViewById(R.id.l_anonymous).setVisibility(0);
    }

    private void showPrivacySettingsFields(boolean z) {
        if (this.mWifiManager.isConnectedMacRandomizationSupported()) {
            this.mView.findViewById(R.id.privacy_settings_fields).setVisibility(z ? 0 : 8);
        }
    }

    private void showSecurityFields() {
        if (this.mAccessPointSecurity == 0) {
            this.mView.findViewById(R.id.security_fields).setVisibility(8);
            showPrivacySettingsFields(true);
            showLPrivacySettingsFields(false);
            return;
        }
        this.mView.findViewById(R.id.security_fields).setVisibility(0);
        if (this.mAccessPointSecurity != 3) {
            this.mPasswordView = (TextView) this.mView.findViewById(R.id.password);
            setPasswordVisible();
            this.mView.findViewById(R.id.eap).setVisibility(8);
            showPrivacySettingsFields(true);
            showLPrivacySettingsFields(false);
            return;
        }
        this.mView.findViewById(R.id.eap).setVisibility(0);
        showPrivacySettingsFields(false);
        showLPrivacySettingsFields(true);
        TextView textView = (TextView) this.mView.findViewById(R.id.l_password);
        this.mPasswordView = textView;
        textView.addTextChangedListener(this);
        Spinner spinner = this.mEapMethodSpinner;
        if (spinner != null) {
            showEapFieldsByMethod(spinner.getSelectedItemPosition());
            return;
        }
        getSIMInfo();
        Spinner spinner2 = (Spinner) this.mView.findViewById(R.id.method);
        this.mEapMethodSpinner = spinner2;
        spinner2.setOnItemSelectedListener(this);
        MiuiUtils.getInstance().setSpinnerAdapter(this.mContext, this.mContext.getResources().getStringArray(R.array.wifi_eap_method), this.mEapMethodSpinner);
        if (Utils.isWifiOnly(this.mContext) || !this.mContext.getResources().getBoolean(17891525)) {
            MiuiUtils.getInstance().setSpinnerAdapter(this.mContext, this.mContext.getResources().getStringArray(R.array.eap_method_without_sim_auth), this.mEapMethodSpinner);
        }
        Spinner spinner3 = this.mEapMethodSpinner;
        spinner3.setPrompt(spinner3.getPrompt());
        View view = this.mView;
        int i = R.id.phase2;
        Spinner spinner4 = (Spinner) view.findViewById(i);
        this.mPhase2Spinner = spinner4;
        spinner4.setPrompt(spinner4.getPrompt());
        Spinner spinner5 = (Spinner) this.mView.findViewById(R.id.ca_cert);
        this.mEapCaCertSpinner = spinner5;
        spinner5.setOnItemSelectedListener(this);
        TextView textView2 = (TextView) this.mView.findViewById(R.id.domain);
        this.mEapDomainView = textView2;
        textView2.addTextChangedListener(this);
        Spinner spinner6 = (Spinner) this.mView.findViewById(R.id.user_cert);
        this.mEapUserCertSpinner = spinner6;
        spinner6.setOnItemSelectedListener(this);
        this.mSimCardSpinner = (Spinner) this.mView.findViewById(R.id.sim_card);
        this.mEapIdentityView = (TextView) this.mView.findViewById(R.id.identity);
        this.mEapAnonymousView = (TextView) this.mView.findViewById(R.id.anonymous);
        this.mSimCardSpinner.setOnItemSelectedListener(this);
        AndroidKeystoreAliasLoader androidKeystoreAliasLoader = getAndroidKeystoreAliasLoader();
        loadCertificates(this.mEapCaCertSpinner, androidKeystoreAliasLoader.getCaCertAliases(), this.mDoNotValidateEapServerString, false, true);
        loadCertificates(this.mEapUserCertSpinner, androidKeystoreAliasLoader.getKeyCertAliases(), this.mDoNotProvideEapUserCertString, false, false);
        this.mPhase2Spinner = (Spinner) this.mView.findViewById(i);
        showEapFieldsByMethod(this.mEapMethodSpinner.getSelectedItemPosition());
    }

    private void startActivityForInstallCerts() {
        Intent intent = new Intent("android.credentials.INSTALL");
        intent.setFlags(268435456);
        intent.setComponent(new ComponentName("com.android.certinstaller", "com.android.certinstaller.CertInstallerMain"));
        intent.putExtra("certificate_install_usage", "wifi");
        this.mContext.startActivity(intent);
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable editable) {
        this.mTextViewChangedHandler.post(new Runnable() { // from class: com.android.settings.wifi.MiuiAddNetworkFragment.1
            @Override // java.lang.Runnable
            public void run() {
                MiuiAddNetworkFragment.this.enableSubmitIfAppropriate();
            }
        });
        if (this.mMaxSsidLength <= 0 || this.mSsidView == null) {
            return;
        }
        checkSsidLength();
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // com.android.settings.BaseEditFragment
    public String getTitle() {
        return getString(R.string.manually_add_network);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        init();
        if (getActivity() != null && !SettingsFeatures.isSplitTablet(getContext())) {
            getActivity().setRequestedOrientation(1);
        }
        getActivity().getWindow().setSoftInputMode(16);
        adaptSoftInput();
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        if (compoundButton.getId() == R.id.show_password) {
            int selectionEnd = this.mPasswordView.getSelectionEnd();
            this.mPasswordView.setInputType((z ? 144 : 128) | 1);
            if (selectionEnd >= 0) {
                ((EditText) this.mPasswordView).setSelection(selectionEnd);
            }
        }
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onInflateView(layoutInflater, viewGroup, bundle);
        return layoutInflater.inflate(R.layout.wifi_add_network_layout, viewGroup, false);
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        if (adapterView == this.mSecuritySpinner) {
            mapPositionToSecurityType(i);
            showSecurityFields();
        } else if (adapterView == this.mEapMethodSpinner || adapterView == this.mEapCaCertSpinner) {
            if (adapterView.getItemAtPosition(i).toString().equals(this.mInstallCertsString)) {
                startActivityForInstallCerts();
            }
            showSecurityFields();
        }
        enableSubmitIfAppropriate();
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        enableSubmitIfAppropriate();
    }

    @Override // com.android.settings.BaseEditFragment
    public void onSave() {
        Bundle bundle;
        WifiConfiguration config = getConfig();
        if (config != null) {
            bundle = new Bundle();
            bundle.putParcelable("config", config);
        } else {
            bundle = null;
        }
        onSave(bundle);
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mView = view;
    }
}
