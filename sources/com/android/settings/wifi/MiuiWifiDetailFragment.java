package com.android.settings.wifi;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.ProxyInfo;
import android.net.StaticIpConfiguration;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SimpleClock;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.Preference;
import com.android.net.module.util.Inet4AddressUtils;
import com.android.net.module.util.ProxyUtils;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.wifi.MiuiWifiDetailFragment;
import com.android.settings.wifi.WifiDialog2;
import com.android.settings.wifi.details.WifiPrivacyPreferenceController;
import com.android.settings.wifi.operatorutils.Operator;
import com.android.settings.wifi.operatorutils.OperatorFactory;
import com.android.settings.wifi.passpoint.MiuiPasspointR1Utils;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.SlaveWifiUtils;
import com.android.wifitrackerlib.NetworkDetailsTracker;
import com.android.wifitrackerlib.PasspointWifiEntry;
import com.android.wifitrackerlib.WifiEntry;
import com.android.wifitrackerlib.WifiPickerTracker;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Predicate;
import miui.os.Build;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.widget.Spinner;
import miuix.slidingwidget.widget.SlidingButton;
import miuix.util.HapticFeedbackCompat;
import miuix.view.HapticFeedbackConstants;

/* loaded from: classes2.dex */
public class MiuiWifiDetailFragment extends EditPreferenceFragment implements TextWatcher, AdapterView.OnItemSelectedListener, WifiPickerTracker.WifiPickerTrackerCallback, WifiEntry.WifiEntryCallback {
    private ConnectivityManager mCm;
    private TextView mDns1View;
    private TextView mDns2View;
    private TextView mGatewayView;
    private HapticFeedbackCompat mHhapticFeedbackCompat;
    private TextView mIpAddressView;
    private Spinner mIpSettingsSpinner;
    private boolean mIsAutoConnect;
    private boolean mIsSavedInstance;
    private boolean mIsSlaveWifi;
    private LinkProperties mLinkProperties;
    private MiuiWifiPrivacyUtils mMiuiWifiPrivacyUtils;
    private WifiConfiguration mModifyConfig;
    private Network mNetwork;
    private NetworkCapabilities mNetworkCapabilities;
    private NetworkDetailsTracker mNetworkDetailsTracker;
    private int mNetworkId;
    private NetworkInfo mNetworkInfo;
    private TextView mNetworkPrefixLengthView;
    private int mOriginalRandomizationValue;
    private Spinner mPrivacySettingsSpinner;
    private TextView mProxyExclusionListView;
    private TextView mProxyHostView;
    private TextView mProxyPacView;
    private TextView mProxyPortView;
    private Spinner mProxySettingsSpinner;
    private View mRootView;
    private WifiConfiguration mSelectWifiConfig;
    private SlaveWifiUtils mSlaveWifiUtils;
    private View mView;
    private WifiDialog2 mWifiDialog;
    private WifiEntry mWifiEntry;
    private android.net.wifi.WifiInfo mWifiInfo;
    private WifiManager mWifiManager;
    private HandlerThread mWorkerThread;
    private boolean mIsDismiss = true;
    private IpConfiguration.IpAssignment mIpAssignment = IpConfiguration.IpAssignment.UNASSIGNED;
    private IpConfiguration.ProxySettings mProxySettings = IpConfiguration.ProxySettings.UNASSIGNED;
    private ProxyInfo mHttpProxy = null;
    private StaticIpConfiguration mStaticIpConfiguration = null;
    private HashMap<String, WifiDetailInfoBean> mWifiDetailGridMap = new LinkedHashMap();
    private IpConfiguration mIpConfiguration = new IpConfiguration();
    private String mSubnet = null;
    private int mPrefixLength = 0;
    private boolean mRefrshPage = false;
    private boolean mIsActivityCreated = false;
    private WifiDialog2.WifiDialog2Listener mModifyPasswordListener = new WifiDialog2.WifiDialog2Listener() { // from class: com.android.settings.wifi.MiuiWifiDetailFragment.5
        @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
        public void onForget(WifiDialog2 wifiDialog2) {
        }

        @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
        public void onSubmit(WifiDialog2 wifiDialog2) {
            WifiConfigController2 controller = wifiDialog2.getController();
            MiuiWifiDetailFragment.this.mModifyConfig = controller.getConfig();
            MiuiWifiDetailFragment.this.mMiuiWifiPrivacyUtils.setWifiConfiguration(MiuiWifiDetailFragment.this.mModifyConfig);
            MiuiWifiDetailFragment.this.mMiuiWifiPrivacyUtils.update(MiuiWifiDetailFragment.this.mPrivacySettingsSpinner);
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.wifi.MiuiWifiDetailFragment$3  reason: invalid class name */
    /* loaded from: classes2.dex */
    public class AnonymousClass3 implements Preference.OnPreferenceClickListener {
        AnonymousClass3() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPreferenceClick$0(DialogInterface dialogInterface) {
            MiuiWifiDetailFragment.this.mIsDismiss = true;
        }

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            if (MiuiWifiDetailFragment.this.mHhapticFeedbackCompat != null) {
                MiuiWifiDetailFragment.this.mHhapticFeedbackCompat.performHapticFeedback(HapticFeedbackConstants.MIUI_TAP_LIGHT);
            }
            AlertDialog.Builder onDismissListener = new AlertDialog.Builder(MiuiWifiDetailFragment.this.getActivity(), R.style.AlertDialog_Theme_DayNight).setTitle(R.string.wifi_menu_forget).setMessage(MiuiWifiDetailFragment.this.isPasspoint() ? R.string.forget_passpoint_wifi_message : R.string.forget_wifi_message).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.MiuiWifiDetailFragment.3.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    MiuiUtils.getInstance().resetAutoConnectAp(MiuiWifiDetailFragment.this.getActivity(), MiuiWifiDetailFragment.this.mSelectWifiConfig);
                    WifiConfigurationManager.getInstance(MiuiWifiDetailFragment.this.getActivity()).deleteWifiConfiguration(MiuiWifiDetailFragment.this.mSelectWifiConfig);
                    if (MiuiWifiDetailFragment.this.mSelectWifiConfig != null && MiuiWifiDetailFragment.this.isPasspoint()) {
                        MiuiPasspointR1Utils.removePasspointConfig(MiuiWifiDetailFragment.this.getActivity(), MiuiWifiDetailFragment.this.mSelectWifiConfig.FQDN);
                    } else if (MiuiWifiDetailFragment.this.mWifiEntry.isSaved()) {
                        MiuiWifiDetailFragment.this.mWifiManager.forget(MiuiWifiDetailFragment.this.mNetworkId, new WifiManager.ActionListener() { // from class: com.android.settings.wifi.MiuiWifiDetailFragment.3.1.1
                            public void onFailure(int i2) {
                            }

                            public void onSuccess() {
                            }
                        });
                    } else if (MiuiWifiDetailFragment.this.mWifiEntry.getNetworkInfo() != null && MiuiWifiDetailFragment.this.mWifiEntry.getNetworkInfo().getState() != NetworkInfo.State.DISCONNECTED) {
                        MiuiWifiDetailFragment.this.mWifiManager.disableEphemeralNetwork(AccessPoint.convertToQuotedString(MiuiWifiDetailFragment.this.mWifiEntry.getSsid()));
                        MiuiWifiDetailFragment.this.mWifiManager.disconnect();
                    }
                    MiuiWifiDetailFragment.this.finish();
                }
            }).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.wifi.MiuiWifiDetailFragment$3$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    MiuiWifiDetailFragment.AnonymousClass3.this.lambda$onPreferenceClick$0(dialogInterface);
                }
            });
            if (MiuiWifiDetailFragment.this.mIsDismiss) {
                MiuiWifiDetailFragment.this.mIsDismiss = false;
                onDismissListener.show();
                return true;
            }
            return true;
        }
    }

    private void adaptSoftInput(Activity activity) {
        boolean z = false;
        if (activity != null && Settings.Secure.getInt(activity.getContentResolver(), "navigation_mode", 0) == 2) {
            z = true;
        }
        if (z) {
            try {
                final View decorView = activity.getWindow().getDecorView();
                decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.android.settings.wifi.MiuiWifiDetailFragment$$ExternalSyntheticLambda0
                    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                    public final void onGlobalLayout() {
                        MiuiWifiDetailFragment.lambda$adaptSoftInput$0(decorView);
                    }
                });
            } catch (Exception unused) {
                Log.e("MiuiWifiDetailFragment", "Resize decorView layout error!");
            }
        }
    }

    private void dimissWifiDialog() {
        WifiDialog2 wifiDialog2 = this.mWifiDialog;
        if (wifiDialog2 != null && wifiDialog2.isShowing()) {
            this.mWifiDialog.dismiss();
        }
        this.mWifiDialog = null;
    }

    private String formatIpAddresses(LinkProperties linkProperties) {
        if (linkProperties == null) {
            return null;
        }
        StringJoiner stringJoiner = new StringJoiner("\n");
        String str = "";
        for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
            if (linkAddress.getAddress() instanceof Inet4Address) {
                str = linkAddress.getAddress().getHostAddress();
                int prefixLength = linkAddress.getPrefixLength();
                this.mPrefixLength = prefixLength;
                this.mSubnet = ipv4PrefixLengthToSubnetMask(prefixLength);
            } else if (linkAddress.getAddress() instanceof Inet6Address) {
                stringJoiner.add(linkAddress.getAddress().getHostAddress());
            }
        }
        if (stringJoiner.length() > 0) {
            return stringJoiner.toString() + "\n" + str;
        }
        return str;
    }

    private Inet4Address getIPv4Address(String str) {
        try {
            return (Inet4Address) NetworkUtils.numericToInetAddress(str);
        } catch (ClassCastException | IllegalArgumentException unused) {
            return null;
        }
    }

    private String getSubnetMask(int i) {
        StringBuilder sb = new StringBuilder();
        int i2 = i > 32 ? 16 : 4;
        for (int i3 = 0; i3 < i2; i3++) {
            int i4 = i < 8 ? i : 8;
            int i5 = 8 - i4;
            int i6 = 0;
            for (int i7 = i4 - 1; i7 >= 0; i7--) {
                i6 += 1 << i7;
            }
            if (i5 > 0) {
                i6 <<= i5;
            }
            sb.append(i6);
            sb.append(".");
            i = i >= 8 ? i - 8 : 0;
        }
        return sb.substring(0, sb.length() - 1);
    }

    private void initManageXiaomRouter() {
        LinearLayout linearLayout;
        HashSet hashSet = new HashSet();
        hashSet.addAll(this.mWifiEntry.getTargetScanResults());
        if (this.mNetworkId == -1 || !isApConnected() || !XiaomiRouterUtils.isXiaomiRouter(hashSet) || (linearLayout = (LinearLayout) getView().findViewById(R.id.manage_xiaomi_router)) == null) {
            return;
        }
        linearLayout.setVisibility(0);
        linearLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.MiuiWifiDetailFragment.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (MiuiWifiDetailFragment.this.mIsSlaveWifi) {
                    XiaomiRouterUtils.showSlaveManageRouter(MiuiWifiDetailFragment.this.getActivity());
                } else {
                    XiaomiRouterUtils.showManageRouter(MiuiWifiDetailFragment.this.getActivity());
                }
            }
        });
    }

    private void initPreference() {
        WifiConfiguration wifiConfiguration;
        getPreferenceScreen().removeAll();
        if (this.mSelectWifiConfig == null) {
            Log.e("MiuiWifiDetailFragment", "initPreference but config is null, return!");
            return;
        }
        if (this.mNetworkId != -1 || isPasspoint() || this.mSelectWifiConfig.isEphemeral()) {
            boolean isNetworkLockedDown = WifiUtils.isNetworkLockedDown(getActivity(), this.mSelectWifiConfig);
            if (this.mWifiEntry.getSecurity() != 0 && !isPasspoint()) {
                setVisibility(R.id.prefs_container, 0);
                MiuiWifiDetailPreference miuiWifiDetailPreference = new MiuiWifiDetailPreference(getActivity());
                miuiWifiDetailPreference.setKey("wifi_detail_modify");
                miuiWifiDetailPreference.setTitle(R.string.wifi_menu_modify);
                miuiWifiDetailPreference.setTitleColorRes(getActivity().getColor(R.color.wifi_detail_modify_color));
                getPreferenceScreen().addPreference(miuiWifiDetailPreference);
                miuiWifiDetailPreference.setEnabled(!isNetworkLockedDown);
                if (!isNetworkLockedDown) {
                    miuiWifiDetailPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.wifi.MiuiWifiDetailFragment.2
                        @Override // androidx.preference.Preference.OnPreferenceClickListener
                        public boolean onPreferenceClick(Preference preference) {
                            MiuiWifiDetailFragment.this.showWifiDialog();
                            return true;
                        }
                    });
                }
            }
            if (!(Build.IS_CM_CUSTOMIZATION && (wifiConfiguration = this.mSelectWifiConfig) != null && "CMCC".equals(wifiConfiguration.SSID)) && UserHandle.myUserId() == 0) {
                setVisibility(R.id.prefs_container, 0);
                MiuiWifiDetailPreference miuiWifiDetailPreference2 = new MiuiWifiDetailPreference(getActivity());
                miuiWifiDetailPreference2.setKey("wifi_detail_delete");
                miuiWifiDetailPreference2.setTitle(R.string.wifi_menu_forget);
                miuiWifiDetailPreference2.setTitleColorRes(getActivity().getColor(R.color.wifi_detail_delete_color));
                getPreferenceScreen().addPreference(miuiWifiDetailPreference2);
                miuiWifiDetailPreference2.setEnabled(!isNetworkLockedDown);
                miuiWifiDetailPreference2.setOnPreferenceClickListener(new AnonymousClass3());
            }
        }
        if (this.mNetworkId != -1 && Build.IS_CM_CUSTOMIZATION && isConnectedOrConnecting(this.mWifiEntry)) {
            setVisibility(R.id.prefs_container, 0);
            MiuiWifiDetailPreference miuiWifiDetailPreference3 = new MiuiWifiDetailPreference(getActivity());
            miuiWifiDetailPreference3.setKey("wifi_detail_disconnect");
            miuiWifiDetailPreference3.setTitle(R.string.button_disconnect_network);
            miuiWifiDetailPreference3.setTitleColorRes(getActivity().getColor(R.color.wifi_detail_disconnect_color));
            getPreferenceScreen().addPreference(miuiWifiDetailPreference3);
            miuiWifiDetailPreference3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.wifi.MiuiWifiDetailFragment.4
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    MiuiUtils.getInstance().connectToOtherWifi(MiuiWifiDetailFragment.this.getActivity(), MiuiWifiDetailFragment.this.mNetworkId);
                    MiuiWifiDetailFragment.this.finish();
                    return true;
                }
            });
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:90:0x02d7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void initUI() {
        /*
            Method dump skipped, instructions count: 831
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.MiuiWifiDetailFragment.initUI():void");
    }

    /* JADX WARN: Removed duplicated region for block: B:29:0x0070  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean ipAndProxyFieldsAreValid() {
        /*
            r6 = this;
            android.net.wifi.WifiConfiguration r0 = r6.mSelectWifiConfig
            r1 = 0
            if (r0 != 0) goto L6
            return r1
        L6:
            miuix.appcompat.widget.Spinner r0 = r6.mIpSettingsSpinner
            r2 = 1
            if (r0 == 0) goto L14
            int r0 = r0.getSelectedItemPosition()
            if (r0 != r2) goto L14
            android.net.IpConfiguration$IpAssignment r0 = android.net.IpConfiguration.IpAssignment.STATIC
            goto L16
        L14:
            android.net.IpConfiguration$IpAssignment r0 = android.net.IpConfiguration.IpAssignment.DHCP
        L16:
            r6.mIpAssignment = r0
            r3 = 0
            r6.mStaticIpConfiguration = r3
            android.net.IpConfiguration$IpAssignment r4 = android.net.IpConfiguration.IpAssignment.STATIC
            if (r0 != r4) goto L2f
            android.net.StaticIpConfiguration r0 = new android.net.StaticIpConfiguration
            r0.<init>()
            r6.mStaticIpConfiguration = r0
            int r0 = r6.validateIpConfigFields(r0)
            if (r0 == 0) goto L2d
            return r1
        L2d:
            r0 = r2
            goto L30
        L2f:
            r0 = r1
        L30:
            miuix.appcompat.widget.Spinner r4 = r6.mProxySettingsSpinner
            int r4 = r4.getSelectedItemPosition()
            android.net.IpConfiguration$ProxySettings r5 = android.net.IpConfiguration.ProxySettings.NONE
            r6.mProxySettings = r5
            r6.mHttpProxy = r3
            if (r4 != r2) goto L83
            android.widget.TextView r3 = r6.mProxyHostView
            if (r3 == 0) goto L83
            android.net.IpConfiguration$ProxySettings r0 = android.net.IpConfiguration.ProxySettings.STATIC
            r6.mProxySettings = r0
            java.lang.CharSequence r0 = r3.getText()
            java.lang.String r0 = r0.toString()
            android.widget.TextView r3 = r6.mProxyPortView
            java.lang.CharSequence r3 = r3.getText()
            java.lang.String r3 = r3.toString()
            android.widget.TextView r4 = r6.mProxyExclusionListView
            java.lang.CharSequence r4 = r4.getText()
            java.lang.String r4 = r4.toString()
            int r5 = java.lang.Integer.parseInt(r3)     // Catch: java.lang.NumberFormatException -> L6b
            int r3 = com.android.settings.ProxySelector.validate(r0, r3, r4)     // Catch: java.lang.NumberFormatException -> L6c
            goto L6e
        L6b:
            r5 = r1
        L6c:
            int r3 = com.android.settings.R.string.proxy_error_invalid_port
        L6e:
            if (r3 != 0) goto L81
            java.lang.String r1 = ","
            java.lang.String[] r1 = r4.split(r1)
            java.util.List r1 = java.util.Arrays.asList(r1)
            android.net.ProxyInfo r0 = android.net.ProxyInfo.buildDirectProxy(r0, r5, r1)
            r6.mHttpProxy = r0
            r1 = r2
        L81:
            r0 = r1
            goto Lab
        L83:
            r3 = 2
            if (r4 != r3) goto Lab
            android.widget.TextView r3 = r6.mProxyPacView
            if (r3 == 0) goto Lab
            android.net.IpConfiguration$ProxySettings r0 = android.net.IpConfiguration.ProxySettings.PAC
            r6.mProxySettings = r0
            java.lang.CharSequence r0 = r3.getText()
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            if (r3 == 0) goto L99
            return r1
        L99:
            java.lang.String r0 = r0.toString()
            android.net.Uri r0 = android.net.Uri.parse(r0)
            if (r0 != 0) goto La4
            return r1
        La4:
            android.net.ProxyInfo r0 = android.net.ProxyInfo.buildPacProxy(r0)
            r6.mHttpProxy = r0
            r0 = r2
        Lab:
            android.net.IpConfiguration$IpAssignment r1 = r6.mIpAssignment
            android.net.IpConfiguration$IpAssignment r3 = android.net.IpConfiguration.IpAssignment.DHCP
            if (r1 != r3) goto Lb8
            android.net.IpConfiguration$ProxySettings r6 = r6.mProxySettings
            android.net.IpConfiguration$ProxySettings r1 = android.net.IpConfiguration.ProxySettings.NONE
            if (r6 != r1) goto Lb8
            goto Lb9
        Lb8:
            r2 = r0
        Lb9:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.MiuiWifiDetailFragment.ipAndProxyFieldsAreValid():boolean");
    }

    private static String ipv4PrefixLengthToSubnetMask(int i) {
        try {
            return Inet4AddressUtils.getPrefixMaskAsInet4Address(i).getHostAddress();
        } catch (IllegalArgumentException unused) {
            return null;
        }
    }

    private boolean isApConnected() {
        return (!this.mIsSlaveWifi ? this.mWifiEntry.getNetworkInfo() != null ? this.mWifiEntry.getNetworkInfo().getState() : NetworkInfo.State.DISCONNECTED : this.mWifiEntry.getSlaveNetworkInfo() != null ? this.mWifiEntry.getSlaveNetworkInfo().getState() : NetworkInfo.State.DISCONNECTED) == NetworkInfo.State.CONNECTED;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPasspoint() {
        WifiConfiguration wifiConfiguration = this.mSelectWifiConfig;
        if (wifiConfiguration == null) {
            return false;
        }
        return wifiConfiguration.isPasspoint();
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
    public /* synthetic */ boolean lambda$updatePasspointWificonfig$2(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration.isPasspoint()) {
            if (TextUtils.equals(this.mWifiEntry.getKey(), "PasspointWifiEntry:" + wifiConfiguration.getKey())) {
                return true;
            }
        }
        return false;
    }

    private void operatorCustomUi() {
        Operator operatorFactory;
        if (this.mSelectWifiConfig == null || this.mView == null || getPreferenceScreen() == null || (operatorFactory = OperatorFactory.getInstance(getActivity())) == null || !operatorFactory.isOpCustomization(this.mWifiEntry.getSsid())) {
            return;
        }
        operatorFactory.opCustomizationView(this.mView, getPreferenceScreen());
    }

    private void refreshPage() {
        try {
            addPreferencesFromResource(getPreferenceScreenResId());
        } catch (NullPointerException unused) {
            Log.e("MiuiWifiDetailFragment", "Add preference screen res failed!");
        }
        this.mSelectWifiConfig = this.mWifiEntry.getWifiConfiguration();
        updatePasspointWificonfig();
        updateAutoConnect();
        WifiConfiguration wifiConfiguration = this.mSelectWifiConfig;
        if (wifiConfiguration != null) {
            this.mRefrshPage = false;
            this.mMiuiWifiPrivacyUtils.setWifiConfiguration(wifiConfiguration);
            this.mMiuiWifiPrivacyUtils.setIsEphemeral(this.mSelectWifiConfig.isEphemeral());
            this.mMiuiWifiPrivacyUtils.setIsPasspoint(isPasspoint());
            this.mNetworkId = this.mSelectWifiConfig.networkId;
        } else if (this.mWifiEntry.getLevel() != -1 && this.mWifiEntry.getWifiStandard() != 0) {
            this.mRefrshPage = false;
        }
        this.mOriginalRandomizationValue = this.mMiuiWifiPrivacyUtils.getRandomizationValue();
        initUI();
        operatorCustomUi();
        lambda$afterTextChanged$1();
    }

    private void setVisibility(int i, int i2) {
        View findViewById = this.mView.findViewById(i);
        if (findViewById != null) {
            findViewById.setVisibility(i2);
        }
    }

    private void showIpConfigFields() {
        DhcpInfo dhcpInfo;
        this.mView.findViewById(R.id.ip_fields).setVisibility(0);
        WifiEntry wifiEntry = this.mWifiEntry;
        WifiConfiguration wifiConfiguration = (wifiEntry == null || !wifiEntry.isSaved()) ? null : this.mSelectWifiConfig;
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
        if (wifiConfiguration != null) {
            StaticIpConfiguration staticIpConfiguration = wifiConfiguration.getIpConfiguration().getStaticIpConfiguration();
            if (staticIpConfiguration != null) {
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
            } else if (this.mWifiEntry == null || !isApConnected() || (dhcpInfo = this.mWifiManager.getDhcpInfo()) == null) {
            } else {
                String hostAddress = NetworkUtils.intToInetAddress(dhcpInfo.ipAddress).getHostAddress();
                String hostAddress2 = NetworkUtils.intToInetAddress(dhcpInfo.gateway).getHostAddress();
                String hostAddress3 = NetworkUtils.intToInetAddress(dhcpInfo.dns1).getHostAddress();
                this.mIpAddressView.setText(hostAddress);
                this.mGatewayView.setText(hostAddress2);
                this.mDns1View.setText(hostAddress3);
                this.mNetworkPrefixLengthView.setText(String.valueOf(this.mPrefixLength));
            }
        }
    }

    private void showProxyFields() {
        ProxyInfo httpProxy;
        ProxyInfo httpProxy2;
        this.mView.findViewById(R.id.proxy_settings_fields).setVisibility(0);
        WifiEntry wifiEntry = this.mWifiEntry;
        WifiConfiguration wifiConfiguration = (wifiEntry == null || !wifiEntry.isSaved()) ? null : this.mSelectWifiConfig;
        if (this.mProxySettingsSpinner.getSelectedItemPosition() != 1) {
            if (this.mProxySettingsSpinner.getSelectedItemPosition() != 2) {
                setVisibility(R.id.proxy_warning_limited_support, 8);
                setVisibility(R.id.proxy_fields, 8);
                setVisibility(R.id.proxy_pac, 8);
                return;
            }
            setVisibility(R.id.proxy_warning_limited_support, 8);
            setVisibility(R.id.proxy_fields, 8);
            int i = R.id.proxy_pac;
            setVisibility(i, 0);
            if (this.mProxyPacView == null) {
                TextView textView = (TextView) this.mView.findViewById(i);
                this.mProxyPacView = textView;
                textView.addTextChangedListener(this);
            }
            if (wifiConfiguration == null || (httpProxy = wifiConfiguration.getHttpProxy()) == null) {
                return;
            }
            this.mProxyPacView.setText(httpProxy.getPacFileUrl().toString());
            return;
        }
        setVisibility(R.id.proxy_warning_limited_support, 0);
        setVisibility(R.id.proxy_fields, 0);
        setVisibility(R.id.proxy_pac, 8);
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
        if (wifiConfiguration == null || (httpProxy2 = wifiConfiguration.getHttpProxy()) == null) {
            return;
        }
        this.mProxyHostView.setText(httpProxy2.getHost());
        this.mProxyPortView.setText(Integer.toString(httpProxy2.getPort()));
        this.mProxyExclusionListView.setText(ProxyUtils.exclusionListAsString(httpProxy2.getExclusionList()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showWifiDialog() {
        dimissWifiDialog();
        WifiDialog2 wifiDialog2 = new WifiDialog2(getActivity(), this.mModifyPasswordListener, this.mWifiEntry, 2, 0, false);
        this.mWifiDialog = wifiDialog2;
        wifiDialog2.setSubmitButton(getString(17039379));
        this.mWifiDialog.show();
    }

    private void updateAutoConnect() {
        View view = getView();
        if (view == null) {
            return;
        }
        View findViewById = view.findViewById(R.id.auto_connect);
        if (isPasspoint() || this.mSelectWifiConfig == null) {
            findViewById.setVisibility(8);
            return;
        }
        findViewById.setVisibility(0);
        SlidingButton slidingButton = (SlidingButton) view.findViewById(R.id.auto_connect_slidingButton);
        boolean isAutoConnect = this.mIsSavedInstance ? this.mIsAutoConnect : AutoConnectUtils.getInstance(getActivity()).isAutoConnect(this.mSelectWifiConfig.SSID);
        this.mIsAutoConnect = isAutoConnect;
        slidingButton.setChecked(isAutoConnect);
        slidingButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.wifi.MiuiWifiDetailFragment.7
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                MiuiWifiDetailFragment.this.mIsAutoConnect = z;
            }
        });
    }

    private void updatePasspointWificonfig() {
        if (this.mSelectWifiConfig == null && (this.mWifiEntry instanceof PasspointWifiEntry) && isApConnected()) {
            Optional findAny = this.mWifiManager.getPrivilegedConfiguredNetworks().stream().filter(new Predicate() { // from class: com.android.settings.wifi.MiuiWifiDetailFragment$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updatePasspointWificonfig$2;
                    lambda$updatePasspointWificonfig$2 = MiuiWifiDetailFragment.this.lambda$updatePasspointWificonfig$2((WifiConfiguration) obj);
                    return lambda$updatePasspointWificonfig$2;
                }
            }).findAny();
            if (findAny.isPresent()) {
                this.mSelectWifiConfig = new WifiConfiguration((WifiConfiguration) findAny.get());
                ActionBar appCompatActionBar = getAppCompatActionBar();
                if (appCompatActionBar != null) {
                    appCompatActionBar.setTitle(this.mSelectWifiConfig.providerFriendlyName);
                }
            }
        }
    }

    private int validateIpConfigFields(StaticIpConfiguration staticIpConfiguration) {
        int i;
        int parseInt;
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
        try {
            try {
                try {
                    parseInt = Integer.parseInt(this.mNetworkPrefixLengthView.getText().toString());
                } catch (NumberFormatException unused) {
                    i = R.string.wifi_ip_settings_invalid_ip_address;
                }
            } catch (IllegalArgumentException unused2) {
                i = R.string.wifi_ip_settings_invalid_ip_address;
            }
            if (parseInt >= 0 && parseInt <= 32) {
                ipAddress.setIpAddress(new LinkAddress(iPv4Address, parseInt));
                String charSequence2 = this.mGatewayView.getText().toString();
                if (TextUtils.isEmpty(charSequence2)) {
                    i = R.string.wifi_ip_settings_invalid_gateway;
                } else {
                    Inet4Address iPv4Address2 = getIPv4Address(charSequence2);
                    if (iPv4Address2 == null) {
                        i = R.string.wifi_ip_settings_invalid_gateway;
                    } else if (iPv4Address2.isMulticastAddress()) {
                        i = R.string.wifi_ip_settings_invalid_gateway;
                    } else {
                        ipAddress.setGateway(iPv4Address2);
                        String charSequence3 = this.mDns1View.getText().toString();
                        ArrayList arrayList = new ArrayList();
                        if (TextUtils.isEmpty(charSequence3)) {
                            i = R.string.wifi_ip_settings_invalid_dns;
                        } else {
                            Inet4Address iPv4Address3 = getIPv4Address(charSequence3);
                            if (iPv4Address3 != null) {
                                arrayList.add(iPv4Address3);
                                staticIpConfiguration.getDnsServers().add(iPv4Address3);
                                if (this.mDns2View.length() > 0) {
                                    Inet4Address iPv4Address4 = getIPv4Address(this.mDns2View.getText().toString());
                                    if (iPv4Address4 == null) {
                                        i = R.string.wifi_ip_settings_invalid_dns;
                                    } else {
                                        arrayList.add(iPv4Address4);
                                        staticIpConfiguration.getDnsServers().add(iPv4Address4);
                                    }
                                }
                                ipAddress.setDnsServers(arrayList);
                                return 0;
                            }
                            i = R.string.wifi_ip_settings_invalid_dns;
                        }
                    }
                }
                return i;
            }
            i = R.string.wifi_ip_settings_invalid_network_prefix_length;
            return i;
        } finally {
            this.mStaticIpConfiguration = ipAddress.build();
        }
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable editable) {
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.wifi.MiuiWifiDetailFragment$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                MiuiWifiDetailFragment.this.lambda$afterTextChanged$1();
            }
        });
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: enableSubmitIfAppropriate  reason: merged with bridge method [inline-methods] */
    public void lambda$afterTextChanged$1() {
        onEditStateChange(ipAndProxyFieldsAreValid() && !WifiUtils.isNetworkLockedDown(getActivity(), this.mSelectWifiConfig));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.wifi_detail_bottom;
    }

    @Override // com.android.settings.wifi.EditPreferenceFragment
    public String getTitle() {
        String string = getArguments().getString("ssid");
        String string2 = getArguments().getString("title_name");
        int i = R.string.network_detail;
        Object[] objArr = new Object[1];
        if (isPasspoint()) {
            string = string2;
        }
        objArr[0] = string;
        return getString(i, objArr);
    }

    public boolean isConnectedOrConnecting(WifiEntry wifiEntry) {
        NetworkInfo.DetailedState detailedState = wifiEntry.getNetworkInfo() == null ? null : wifiEntry.getNetworkInfo().getDetailedState();
        return detailedState != null && detailedState.compareTo(NetworkInfo.DetailedState.CONNECTING) >= 0 && detailedState.compareTo(NetworkInfo.DetailedState.CONNECTED) <= 0;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mIsActivityCreated = true;
        if (bundle != null) {
            this.mIsAutoConnect = bundle.getBoolean("is_autoConnect");
            this.mIsSavedInstance = true;
        }
        this.mIsSlaveWifi = getArguments().getBoolean("is_salve_wifi", false);
        setHasOptionsMenu(true);
        this.mView = getView();
        this.mCm = (ConnectivityManager) getActivity().getSystemService("connectivity");
        if (this.mNetworkDetailsTracker == null) {
            Context context = getContext();
            HandlerThread handlerThread = new HandlerThread("MiuiWifiDetailFragment{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
            this.mWorkerThread = handlerThread;
            handlerThread.start();
            this.mNetworkDetailsTracker = FeatureFactory.getFactory(context).getWifiTrackerLibProvider().createNetworkDetailsTracker(getSettingsLifecycle(), context, new Handler(Looper.getMainLooper()), this.mWorkerThread.getThreadHandler(), new SimpleClock(ZoneOffset.UTC) { // from class: com.android.settings.wifi.MiuiWifiDetailFragment.1
                public long millis() {
                    return SystemClock.elapsedRealtime();
                }
            }, 15000L, 10000L, getArguments().getString("key_chosen_wifientry_key"));
        }
        NetworkDetailsTracker networkDetailsTracker = this.mNetworkDetailsTracker;
        if (networkDetailsTracker != null) {
            this.mWifiEntry = networkDetailsTracker.getWifiEntry();
        }
        this.mRefrshPage = true;
        this.mWifiEntry.setListener(this);
        this.mSelectWifiConfig = this.mWifiEntry.getWifiConfiguration();
        this.mWifiManager = (WifiManager) getActivity().getSystemService("wifi");
        this.mSlaveWifiUtils = SlaveWifiUtils.getInstance(getActivity());
        if (getActivity() != null && !SettingsFeatures.isSplitTablet(getContext())) {
            getActivity().setRequestedOrientation(1);
        }
        MiuiWifiPrivacyUtils miuiWifiPrivacyUtils = new MiuiWifiPrivacyUtils(getActivity());
        this.mMiuiWifiPrivacyUtils = miuiWifiPrivacyUtils;
        WifiConfiguration wifiConfiguration = this.mSelectWifiConfig;
        if (wifiConfiguration != null) {
            miuiWifiPrivacyUtils.setWifiConfiguration(wifiConfiguration);
            this.mMiuiWifiPrivacyUtils.setIsEphemeral(this.mSelectWifiConfig.isEphemeral());
            this.mMiuiWifiPrivacyUtils.setIsPasspoint(isPasspoint());
        }
        this.mOriginalRandomizationValue = this.mMiuiWifiPrivacyUtils.getRandomizationValue();
        this.mHhapticFeedbackCompat = new HapticFeedbackCompat(getActivity());
        initUI();
        operatorCustomUi();
        adaptSoftInput(getActivity());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (this.mRootView == null) {
            View inflate = layoutInflater.inflate(R.layout.wifi_edit_layout, viewGroup, false);
            this.mRootView = inflate;
            ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(R.id.prefs_container);
            viewGroup2.addView(super.onCreateView(layoutInflater, viewGroup, bundle));
            viewGroup2.setVisibility(8);
        }
        return this.mRootView;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        dimissWifiDialog();
        super.onDestroy();
    }

    @Override // com.android.settings.wifi.EditPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mRootView = null;
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        if (adapterView == this.mProxySettingsSpinner) {
            showProxyFields();
        } else if (adapterView == this.mIpSettingsSpinner) {
            showIpConfigFields();
        }
        lambda$afterTextChanged$1();
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedNetworksChanged() {
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedSubscriptionsChanged() {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateAutoConnect();
        lambda$afterTextChanged$1();
    }

    @Override // com.android.settings.wifi.EditPreferenceFragment
    public void onSave(boolean z) {
        Bundle bundle = new Bundle();
        WifiConfiguration wifiConfiguration = this.mModifyConfig;
        if (wifiConfiguration == null) {
            wifiConfiguration = this.mSelectWifiConfig;
        }
        if (wifiConfiguration != null) {
            this.mIpConfiguration.setHttpProxy(this.mHttpProxy);
            this.mIpConfiguration.setIpAssignment(this.mIpAssignment);
            this.mIpConfiguration.setProxySettings(this.mProxySettings);
            this.mIpConfiguration.setStaticIpConfiguration(this.mStaticIpConfiguration);
            wifiConfiguration.setIpConfiguration(new IpConfiguration(this.mIpConfiguration));
            WifiConfiguration wifiConfiguration2 = this.mSelectWifiConfig;
            if (wifiConfiguration2 != null) {
                wifiConfiguration.hiddenSSID = wifiConfiguration2.hiddenSSID;
                wifiConfiguration.creatorUid = wifiConfiguration2.creatorUid;
            }
            wifiConfiguration.macRandomizationSetting = WifiPrivacyPreferenceController.translatePrefValueToMacRandomizedValue(this.mPrivacySettingsSpinner.getSelectedItemPosition());
            bundle.putParcelable("config", wifiConfiguration);
            bundle.putShort("mac_random_changed", (short) (!MiuiWifiPrivacyUtils.isSamePrefValue(this.mOriginalRandomizationValue, wifiConfiguration.macRandomizationSetting) ? 1 : 0));
        }
        if (this.mSelectWifiConfig != null && !isPasspoint()) {
            AutoConnectUtils.getInstance(getActivity()).enableAutoConnect(getActivity(), this.mSelectWifiConfig.SSID, this.mIsAutoConnect);
            this.mWifiManager.allowAutojoin(this.mSelectWifiConfig.networkId, this.mIsAutoConnect);
        }
        onSave(bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("is_autoConnect", this.mIsAutoConnect);
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // com.android.wifitrackerlib.WifiEntry.WifiEntryCallback
    public void onUpdated() {
        if (this.mRefrshPage && this.mIsActivityCreated && getActivity() != null) {
            updateNetworkInfo();
            refreshPage();
        }
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onWifiEntriesChanged() {
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker.BaseWifiTrackerCallback
    /* renamed from: onWifiStateChanged */
    public void lambda$onInternetTypeChanged$4() {
    }

    void updateNetworkInfo() {
        if (this.mWifiEntry.getConnectedState() == 2) {
            Network currentNetwork = this.mWifiManager.getCurrentNetwork();
            this.mNetwork = currentNetwork;
            this.mLinkProperties = this.mCm.getLinkProperties(currentNetwork);
            this.mNetworkCapabilities = this.mCm.getNetworkCapabilities(this.mNetwork);
            this.mNetworkInfo = this.mCm.getNetworkInfo(this.mNetwork);
            this.mWifiInfo = this.mWifiManager.getConnectionInfo();
        } else if (this.mWifiEntry.getSlaveConnectedState() != 2) {
            this.mNetwork = null;
            this.mLinkProperties = null;
            this.mNetworkCapabilities = null;
            this.mNetworkInfo = null;
            this.mWifiInfo = null;
        } else {
            Network slaveWifiCurrentNetwork = this.mSlaveWifiUtils.getSlaveWifiCurrentNetwork();
            this.mNetwork = slaveWifiCurrentNetwork;
            this.mLinkProperties = this.mCm.getLinkProperties(slaveWifiCurrentNetwork);
            this.mNetworkCapabilities = this.mCm.getNetworkCapabilities(this.mNetwork);
            this.mNetworkInfo = this.mCm.getNetworkInfo(this.mNetwork);
            this.mWifiInfo = this.mSlaveWifiUtils.getWifiSlaveConnectionInfo();
        }
    }
}
