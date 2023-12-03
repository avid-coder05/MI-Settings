package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.ScoredNetwork;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceFrameLayout;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.app.ResolverListAdapter;
import com.android.internal.app.chooser.DisplayResolveInfo;
import com.android.internal.app.chooser.TargetInfo;
import com.android.settings.MiuiSettings;
import com.android.settings.MiuiUtils;
import com.android.settings.OnBackPressedListener;
import com.android.settings.OneTrackManager;
import com.android.settings.R;
import com.android.settings.RegionUtils;
import com.android.settings.SettingsApplication;
import com.android.settings.bluetooth.FitSplitUtils;
import com.android.settings.connectivity.MiuiBluetoothDataBaseOperaterUtil;
import com.android.settings.core.OnActivityResultListener;
import com.android.settings.network.NetworkProviderSettings;
import com.android.settings.operator.kddi.KDDIDataConnectionDialog;
import com.android.settings.operator.softbank.SoftBankEsimActivationDialog;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.wifi.MiuiWifiEntryPreference;
import com.android.settings.wifi.dpp.MiuiWifiDppUtils;
import com.android.settings.wifi.dpp.MiuishowDppQrCodeFragment;
import com.android.settings.wifi.dpp.WifiNetworkConfig;
import com.android.settings.wifi.dpp.WifiQrCode;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import com.android.settingslib.util.ToastUtil;
import com.android.settingslib.wifi.AccessPoint;
import com.android.wifitrackerlib.StandardWifiEntry;
import com.android.wifitrackerlib.Utils;
import com.android.wifitrackerlib.WifiEntry;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManagerService;
import com.xiaomi.mirror.synergy.MiuiSynergySdk;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import miui.content.ExtraIntent;
import miui.os.Build;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class MiuiWifiSettings extends NetworkProviderSettings implements Preference.OnPreferenceChangeListener, OnBackPressedListener, OnActivityResultListener, AccessPoint.AccessPointListener, WifiEntry.WifiEntryCallback {
    public static final HashSet<String> sCmccSSidSet;
    private ImageButton mBackArrow;
    private TextView mBackButton;
    private CheckBoxPreference mCmccNetworkNotification;
    private ConnectivityManager mConnManager;
    private Handler mDppHandler;
    private String mExtraSsid;
    private IntentFilter mFilter;
    private boolean mIsConnecting;
    private boolean mIsDeviceLockNeed;
    private boolean mIsFromKeyguard;
    private boolean mIsFromPhoneActivation;
    private boolean mIsInProvision;
    private boolean mIsRestricted;
    private boolean mIsWifiShareTurnOn;
    private String mLanguage;
    private LocalSameAccountApCallback mLocalSameAccountApCallback;
    private MainThreadHandler mMainHandler;
    private ImageButton mNextArrow;
    private TextView mNextButton;
    private String mOcrWifiPwd;
    private IntentFilter mOpenWifiFilter;
    private Intent mOpenWifiIntent;
    private BroadcastReceiver mReceiver;
    private MiuiWifiEntryPreference mSameAccountAPPreference;
    private MiuiSynergySdk.SameAccountAccessPoint mSameAccountAccessPoint;
    private AlertDialog mSharedDialog;
    private TextView mSkipButton;
    private boolean mUserSelect;
    private MiuiVirtualWifiEntryPreference mVirtualAPPreference;
    private WifiConfiguration mWifiConfig;
    private WifiConfigurationManager mWifiConfigurationManager;
    protected CheckBoxPreference mWifiEnablePreference;
    private HandlerThread mWifiHelpThread;
    protected WifiManager mWifiManager;
    private WifiNetworkConfig mWifiNetworkConfig;
    private WifiNetworkScoreCache mWifiNetworkScoreCache;
    private WifiQrCode mWifiQrcode;
    private Handler mWorkHandler;
    private HandlerThread mWorkThread;
    private MiuiNearbyApPreference nearbyAccessPointPre;
    private RecyclerView recyclerview;
    private View rootView;
    private static final String TAG = MiuiWifiSettings.class.getSimpleName();
    private static String BT_PLUGIN_INITED_NOTIFY = "BLUETOOTHHEADSETPLUGIN_INITED";
    private static int HEADSETPLUGIN_NOTSET = -1;
    private static int HEADSETPLUGIN_INITED = 1;
    public static boolean mIsDisableBack = false;
    private NetworkInfo.State mNetworkState = NetworkInfo.State.DISCONNECTED;
    private boolean mIsShowDataDialog = false;
    private boolean mIsWorkHandlerQuit = false;
    private int mESimCode = -1;
    private MessageHandler mWifiHelpWorkHandler = null;
    private final Runnable mUpdateWifiEntryPreferencesRunnable = new Runnable() { // from class: com.android.settings.wifi.MiuiWifiSettings$$ExternalSyntheticLambda1
        @Override // java.lang.Runnable
        public final void run() {
            MiuiWifiSettings.this.lambda$new$0();
        }
    };
    private final Runnable mUpdateRefreshRunnable = new Runnable() { // from class: com.android.settings.wifi.MiuiWifiSettings$$ExternalSyntheticLambda2
        @Override // java.lang.Runnable
        public final void run() {
            MiuiWifiSettings.this.lambda$new$1();
        }
    };
    private final View.OnClickListener mRefreshListener = new View.OnClickListener() { // from class: com.android.settings.wifi.MiuiWifiSettings.4
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            MiuiWifiSettings.this.updateScanState(true);
        }
    };
    private Preference.OnPreferenceClickListener mWifiHelpClickListener = new Preference.OnPreferenceClickListener() { // from class: com.android.settings.wifi.MiuiWifiSettings.7
        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent();
            intent.putExtra("COME_FROM", "MIUI_WIFI_CONNECT_HELP");
            intent.setClassName("com.android.settings", "com.android.settings.bluetooth.MiuiHeadsetActivityPlugin");
            if (FitSplitUtils.isFitSplit()) {
                intent.addMiuiFlags(16);
            }
            MiuiWifiSettings.this.startActivity(intent);
            OneTrackManager.trackHelpClick(MiuiWifiSettings.this.getContext(), "WIFI");
            return true;
        }
    };
    private boolean mIsShown = false;
    private boolean mIsFirstWifiStateChange = true;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class LocalSameAccountApCallback implements MiuiSynergySdk.SameAccountApCallback {
        private WeakReference<MiuiWifiSettings> mWifiSettingsRef;

        public LocalSameAccountApCallback(MiuiWifiSettings miuiWifiSettings) {
            this.mWifiSettingsRef = new WeakReference<>(miuiWifiSettings);
        }

        @Override // com.xiaomi.mirror.synergy.MiuiSynergySdk.SameAccountApCallback
        public void onApConnectedStatusUpdate(int i, MiuiSynergySdk.SameAccountAccessPoint sameAccountAccessPoint) {
            MiuiWifiSettings miuiWifiSettings = this.mWifiSettingsRef.get();
            if (miuiWifiSettings != null) {
                miuiWifiSettings.apConnectedStatusUpdated(i, sameAccountAccessPoint);
            } else {
                Log.e(MiuiWifiSettings.TAG, "onApConnectedStatusUpdate: MiuiWifiSettings is null!");
            }
        }

        @Override // com.xiaomi.mirror.synergy.MiuiSynergySdk.SameAccountApCallback
        public void onApInfoUpdate(MiuiSynergySdk.SameAccountAccessPoint sameAccountAccessPoint) {
            MiuiWifiSettings miuiWifiSettings = this.mWifiSettingsRef.get();
            if (miuiWifiSettings != null) {
                miuiWifiSettings.refreshVirtualApInfo(sameAccountAccessPoint);
            } else {
                Log.e(MiuiWifiSettings.TAG, "onApInfoUpdate: MiuiWifiSettings is null!");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public final class MainThreadHandler extends Handler {
        public MainThreadHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            View findViewById;
            if (MiuiWifiSettings.this.mIsWorkHandlerQuit) {
                Log.e(MiuiWifiSettings.TAG, "the handler is quit, do not handle " + message);
                return;
            }
            int i = message.what;
            if (i == 1) {
                MiuiWifiSettings.this.internalSmoothScrollToPosition();
            } else if (i != 2) {
                if (i != 3) {
                    return;
                }
                MiuiWifiSettings.this.updateScanState(false);
            } else if (MiuiWifiSettings.this.getActivity() == null || (findViewById = MiuiWifiSettings.this.getActivity().findViewById(R.id.action_bar)) == null) {
            } else {
                findViewById.sendAccessibilityEvent(8);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class MessageHandler extends Handler {
        private WeakReference<MiuiWifiSettings> miuiWifiSettingsRef;

        private MessageHandler(Looper looper, MiuiWifiSettings miuiWifiSettings) {
            super(looper);
            this.miuiWifiSettingsRef = new WeakReference<>(miuiWifiSettings);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            try {
                int i = message.what;
                Log.d(MiuiWifiSettings.TAG, "handleMessage: what: " + i);
                if (i != 1) {
                    return;
                }
                WeakReference<MiuiWifiSettings> weakReference = this.miuiWifiSettingsRef;
                MiuiWifiSettings miuiWifiSettings = weakReference != null ? weakReference.get() : null;
                if (miuiWifiSettings != null) {
                    miuiWifiSettings.checkEnableHelpPreference();
                }
            } catch (Exception unused) {
                Log.d(MiuiWifiSettings.TAG, "error ");
            }
        }
    }

    static {
        HashSet<String> hashSet = new HashSet<>();
        sCmccSSidSet = hashSet;
        hashSet.add("CMCC");
        hashSet.add("CMCC-AUTO");
        hashSet.add("CMCC-EDU");
        hashSet.add("CMCC-WEB");
    }

    private void addCmccNetworkNotificationPref() {
        if (Build.IS_CM_CUSTOMIZATION_TEST) {
            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getPrefContext());
            this.mCmccNetworkNotification = checkBoxPreference;
            checkBoxPreference.setKey("cmcc_network_notification");
            this.mCmccNetworkNotification.setTitle(R.string.wifi_notify_cmcc_connected_title);
            this.mCmccNetworkNotification.setSummary(R.string.wifi_notify_cmcc_connected_summary);
            this.mCmccNetworkNotification.setOnPreferenceChangeListener(this);
            ((PreferenceCategory) findPreference("wifi_settings")).addPreference(this.mCmccNetworkNotification);
            updateCmccNetworkNotificationState();
        }
    }

    private void addVirtualAPPreference(boolean z, int i) {
        final MiuiSynergySdk.SameAccountAccessPoint sameAccountAccessPoint = this.mSameAccountAccessPoint;
        if (sameAccountAccessPoint == null) {
            Log.e(TAG, "mSameAccountAccessPoint is null!");
            return;
        }
        if (this.mVirtualAPPreference == null) {
            this.mVirtualAPPreference = new MiuiVirtualWifiEntryPreference(getPrefContext(), null, sameAccountAccessPoint.getSsid(), sameAccountAccessPoint.getBatteryPercent(), sameAccountAccessPoint.isIs5G());
        }
        this.mVirtualAPPreference.setTitle(sameAccountAccessPoint.getSsid());
        this.mVirtualAPPreference.updateIcon();
        this.mVirtualAPPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.wifi.MiuiWifiSettings.12
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                Log.d(MiuiWifiSettings.TAG, "click virtual AP Preference!");
                if (MiuiWifiSettings.this.getActivity() == null) {
                    return true;
                }
                int connectSameAccountAp = MiuiSynergySdk.getInstance().connectSameAccountAp(MiuiWifiSettings.this.getActivity(), sameAccountAccessPoint.getSsid());
                if (connectSameAccountAp == 0 || connectSameAccountAp == 1) {
                    MiuiWifiSettings.this.mIsConnecting = true;
                    MiuiWifiSettings.this.mVirtualAPPreference.updateState(2);
                    MiuiWifiSettings.this.mVirtualAPPreference.updateSummary();
                } else if (connectSameAccountAp == -1) {
                    Log.e(MiuiWifiSettings.TAG, "connectSameAccountAp fail");
                    ToastUtil.show(MiuiWifiSettings.this.getActivity(), MiuiWifiSettings.this.getActivity().getResources().getString(R.string.ap_connect_failed), 1);
                } else {
                    Log.w(MiuiWifiSettings.TAG, "connectSameAccountAp state = " + connectSameAccountAp);
                }
                return true;
            }
        });
        if (AccessPoint.removeDoubleQuotes(this.mWifiManager.getConnectionInfo().getSSID()).equals(sameAccountAccessPoint.getSsid()) || this.mConnectedWifiEntryPreferenceCategory == null) {
            return;
        }
        this.mVirtualAPPreference.setOrder(i);
        this.mConnectedWifiEntryPreferenceCategory.addPreference(this.mVirtualAPPreference);
        if (this.mIsConnecting) {
            this.mVirtualAPPreference.updateState(2);
            this.mVirtualAPPreference.updateSummary();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void apConnectedStatusUpdated(final int i, MiuiSynergySdk.SameAccountAccessPoint sameAccountAccessPoint) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.wifi.MiuiWifiSettings.15
            @Override // java.lang.Runnable
            public void run() {
                MiuiWifiSettings.this.mIsConnecting = false;
                if (i > 0) {
                    Log.d(MiuiWifiSettings.TAG, "onApConnectedStatusUpdate: connect AP success!");
                    MiuiWifiSettings.this.removeVirtualAPPreference();
                    return;
                }
                Log.d(MiuiWifiSettings.TAG, "onApConnectedStatusUpdate: connect AP fail!");
                if (MiuiWifiSettings.this.mVirtualAPPreference != null) {
                    MiuiWifiSettings.this.mVirtualAPPreference.updateState(0);
                    MiuiWifiSettings.this.mVirtualAPPreference.updateSummary();
                    MiuiWifiSettings.this.updateWifiEntryPreferences();
                }
                if (MiuiWifiSettings.this.getActivity() != null) {
                    ToastUtil.show(MiuiWifiSettings.this.getActivity().getApplicationContext(), MiuiWifiSettings.this.getActivity().getResources().getString(R.string.ap_connect_failed), 1);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkEnableHelpPreference() {
        SplitInfoManager splitInfoManagerService;
        try {
            boolean z = true;
            boolean z2 = getResources().getConfiguration().locale.getCountry().equals("CN") && getResources().getConfiguration().locale.getLanguage().equals("zh");
            int i = ((SettingsApplication) getActivity().getApplication()).mQigsawStarted;
            Log.d(TAG, "status " + i);
            if (i == HEADSETPLUGIN_INITED && (splitInfoManagerService = SplitInfoManagerService.getInstance()) != null) {
                String currentSplitInfoVersion = splitInfoManagerService.getCurrentSplitInfoVersion();
                if (z2 && !TextUtils.isEmpty(currentSplitInfoVersion) && MiuiBluetoothDataBaseOperaterUtil.queryPluginSupport(getActivity(), currentSplitInfoVersion, "wifi_help")) {
                    setConnectHelpPreferenceVisible(z);
                }
            }
            z = false;
            setConnectHelpPreferenceVisible(z);
        } catch (Exception e) {
            Log.e(TAG, "error " + e);
        }
    }

    private MiuiWifiEntryPreference createEntryPreference(WifiEntry wifiEntry) {
        LongPressWifiEntryPreference longPressWifiEntryPreference = new LongPressWifiEntryPreference(getPrefContext(), wifiEntry, this);
        longPressWifiEntryPreference.setTitle(wifiEntry.getTitle());
        longPressWifiEntryPreference.setArrowClickListener(new MiuiWifiEntryPreference.ArrowClickListener(wifiEntry, this));
        longPressWifiEntryPreference.setOnPreferenceChangeListener(this);
        return longPressWifiEntryPreference;
    }

    private void displayNearbyButtonIfNeeded(View view) {
        final Intent component = new Intent().setComponent(getNearbySharingComponent());
        TargetInfo nearbySharingTarget = getNearbySharingTarget(component);
        if (nearbySharingTarget == null) {
            Log.i(TAG, "Do not support Nearby Sharing!");
            return;
        }
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.layout_wifi_share_qrcode);
        Button button = (Button) LayoutInflater.from(getContext()).inflate(17367124, (ViewGroup) null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 1;
        int dimensionPixelSize = getResources().getDimensionPixelSize(17105511) / 2;
        layoutParams.setMargins(dimensionPixelSize, 0, dimensionPixelSize, 0);
        viewGroup.addView(button, layoutParams);
        Drawable displayIcon = nearbySharingTarget.getDisplayIcon(getContext());
        CharSequence displayLabel = nearbySharingTarget.getDisplayLabel();
        if (displayIcon != null) {
            int dimensionPixelSize2 = getResources().getDimensionPixelSize(17105043);
            displayIcon.setBounds(0, 0, dimensionPixelSize2, dimensionPixelSize2);
            button.setCompoundDrawablesRelative(displayIcon, null, null, null);
        }
        button.setText(displayLabel);
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.MiuiWifiSettings$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                MiuiWifiSettings.this.lambda$displayNearbyButtonIfNeeded$2(component, view2);
            }
        });
        button.setVisibility(0);
        button.setAllCaps(false);
    }

    private void focusOnBackIcon() {
        View findViewById;
        View findViewById2;
        if (this.mIsFirstWifiStateChange) {
            this.mIsFirstWifiStateChange = false;
        } else if (getActivity() == null || (findViewById = getActivity().findViewById(R.id.action_bar)) == null || (findViewById2 = findViewById.findViewById(16908313)) == null) {
        } else {
            findViewById2.sendAccessibilityEvent(8);
        }
    }

    private Bundle getExtraBundle() {
        Intent intent;
        FragmentActivity activity = getActivity();
        if (activity == null || (intent = activity.getIntent()) == null || !intent.getBooleanExtra(ExtraIntent.EXTRA_SHOW_ON_FINDDEVICE_KEYGUARD, false)) {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(ExtraIntent.EXTRA_SHOW_ON_FINDDEVICE_KEYGUARD, true);
        return bundle;
    }

    private TargetInfo getNearbySharingTarget(Intent intent) {
        ActivityInfo activityInfo;
        String str;
        Drawable drawable;
        ComponentName nearbySharingComponent = getNearbySharingComponent();
        Drawable drawable2 = null;
        drawable2 = null;
        CharSequence charSequence = null;
        if (nearbySharingComponent == null) {
            return null;
        }
        Intent intent2 = new Intent(intent);
        intent2.setComponent(nearbySharingComponent);
        PackageManager packageManager = getContext().getPackageManager();
        ResolveInfo resolveActivity = packageManager.resolveActivity(intent2, 128);
        if (resolveActivity == null || (activityInfo = resolveActivity.activityInfo) == null) {
            Log.e(TAG, "Device-specified nearby sharing component (" + nearbySharingComponent + ") not available");
            return null;
        }
        Bundle bundle = activityInfo.metaData;
        if (bundle != null) {
            try {
                Resources resourcesForActivity = packageManager.getResourcesForActivity(nearbySharingComponent);
                str = resourcesForActivity.getString(bundle.getInt("android.service.chooser.chip_label"));
                try {
                    drawable2 = resourcesForActivity.getDrawable(bundle.getInt("android.service.chooser.chip_icon"));
                } catch (PackageManager.NameNotFoundException | Resources.NotFoundException unused) {
                }
            } catch (PackageManager.NameNotFoundException | Resources.NotFoundException unused2) {
                str = null;
            }
            drawable = drawable2;
            charSequence = str;
        } else {
            drawable = null;
        }
        if (TextUtils.isEmpty(charSequence)) {
            charSequence = resolveActivity.loadLabel(packageManager);
        }
        CharSequence charSequence2 = charSequence;
        if (drawable == null) {
            drawable = resolveActivity.loadIcon(packageManager);
        }
        DisplayResolveInfo displayResolveInfo = new DisplayResolveInfo(intent, resolveActivity, charSequence2, "", intent2, (ResolverListAdapter.ResolveInfoPresentationGetter) null);
        displayResolveInfo.setDisplayIcon(drawable);
        return displayResolveInfo;
    }

    private static String getSecurityString(WifiConfiguration wifiConfiguration) {
        return wifiConfiguration.allowedKeyManagement.get(8) ? "SAE" : wifiConfiguration.allowedKeyManagement.get(9) ? "nopass" : (wifiConfiguration.allowedKeyManagement.get(1) || wifiConfiguration.allowedKeyManagement.get(4)) ? "WPA" : wifiConfiguration.wepKeys[0] == null ? "nopass" : "WEP";
    }

    private void initBroadcastReceiver(Handler handler) {
        IntentFilter intentFilter = new IntentFilter();
        this.mFilter = intentFilter;
        intentFilter.addAction("android.net.wifi.SCAN_RESULTS");
        this.mFilter.addAction("android.net.wifi.STATE_CHANGE");
        this.mFilter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
        this.mFilter.addAction("show_dpp_qr_code");
        this.mFilter.addAction("scan_dpp_success");
        if (this.mIsDeviceLockNeed && this.mIsInProvision) {
            this.mFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        }
        IntentFilter intentFilter2 = new IntentFilter();
        this.mOpenWifiFilter = intentFilter2;
        intentFilter2.addAction("miui.intent.CACHE_OPENWIFI");
        this.mOpenWifiFilter.addDataScheme("http");
        this.mOpenWifiFilter.addDataScheme("https");
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.MiuiWifiSettings.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("android.net.wifi.SCAN_RESULTS".equals(action)) {
                    MiuiWifiSettings.this.mMainHandler.sendEmptyMessage(3);
                } else if (!"android.net.wifi.STATE_CHANGE".equals(action)) {
                    if ("android.net.wifi.CONFIGURED_NETWORKS_CHANGE".equals(action)) {
                        if (intent.getIntExtra("changeReason", 3) == 1 && intent.getIntExtra("changeReason", 2) == 1) {
                            WifiConfiguration wifiConfiguration = (WifiConfiguration) intent.getParcelableExtra("wifiConfiguration");
                            if (wifiConfiguration != null) {
                                MiuiUtils.getInstance().resetAutoConnectAp(context, wifiConfiguration);
                            }
                            MiuiWifiSettings.this.mWifiConfigurationManager.deleteWifiConfiguration(wifiConfiguration);
                        }
                    } else if ("miui.intent.CACHE_OPENWIFI".equals(action)) {
                        MiuiWifiSettings.this.mOpenWifiIntent = intent;
                    } else if ("show_dpp_qr_code".equals(action)) {
                        MiuiWifiSettings.this.mDppHandler.sendEmptyMessage(20481);
                    } else if (!"scan_dpp_success".equals(action)) {
                        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action) && MiuiWifiSettings.this.mIsDeviceLockNeed) {
                            MiuiWifiSettings.this.isCustShowSkipButton();
                        }
                    } else {
                        MiuiWifiSettings.this.mWifiQrcode = (WifiQrCode) intent.getExtras().getSerializable("wifi_qr_code");
                        MiuiWifiSettings.this.mWifiNetworkConfig = (WifiNetworkConfig) intent.getExtras().getSerializable("wifi_net_work_config");
                        MiuiWifiSettings.this.mDppHandler.sendEmptyMessage(20482);
                    }
                } else {
                    NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                    if (networkInfo != null) {
                        if (MiuiWifiSettings.this.mUserSelect && MiuiWifiSettings.this.mNetworkState == NetworkInfo.State.DISCONNECTED && networkInfo.getState() == NetworkInfo.State.CONNECTING) {
                            MiuiWifiSettings.this.mMainHandler.sendEmptyMessage(1);
                        } else if (MiuiWifiSettings.this.mUserSelect && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            MiuiWifiSettings.this.mMainHandler.sendEmptyMessageDelayed(2, 500L);
                            MiuiWifiSettings.this.mUserSelect = false;
                        }
                        MiuiWifiSettings.this.mNetworkState = networkInfo.getState();
                        if (!MiuiWifiSettings.this.isPad() || networkInfo.getState() != NetworkInfo.State.CONNECTED || MiuiWifiSettings.this.mSameAccountAccessPoint == null || MiuiWifiSettings.this.mVirtualAPPreference == null) {
                            return;
                        }
                        String removeDoubleQuotes = AccessPoint.removeDoubleQuotes(MiuiWifiSettings.this.mWifiManager.getConnectionInfo().getSSID());
                        if (removeDoubleQuotes.equals(MiuiWifiSettings.this.mSameAccountAccessPoint.getSsid())) {
                            Log.d(MiuiWifiSettings.TAG, "onReceive connected: ssid=" + removeDoubleQuotes);
                            MiuiWifiSettings.this.mIsConnecting = false;
                            MiuiWifiSettings.this.removeVirtualAPPreference();
                        }
                    }
                }
            }
        };
        getActivity().registerReceiver(this.mReceiver, this.mFilter, null, handler);
        getActivity().registerReceiver(this.mReceiver, this.mOpenWifiFilter, null, handler);
    }

    private void initDppHandler() {
        this.mDppHandler = new Handler() { // from class: com.android.settings.wifi.MiuiWifiSettings.9
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == 20481 && !((NetworkProviderSettings) MiuiWifiSettings.this).mIsDppQrCodeFgShow) {
                    ((NetworkProviderSettings) MiuiWifiSettings.this).mIsDppQrCodeFgShow = true;
                    MiuiWifiSettings miuiWifiSettings = MiuiWifiSettings.this;
                    miuiWifiSettings.startFragment(miuiWifiSettings, MiuishowDppQrCodeFragment.class.getName(), 101, (Bundle) null, R.string.dpp_theme_title);
                } else if (message.what != 20482 || ((NetworkProviderSettings) MiuiWifiSettings.this).mIsShareDialogShow) {
                } else {
                    ((NetworkProviderSettings) MiuiWifiSettings.this).mIsShareDialogShow = true;
                    if (MiuiWifiSettings.this.getActivity() == null || MiuiWifiSettings.this.mWifiQrcode == null || MiuiWifiSettings.this.mWifiNetworkConfig == null) {
                        return;
                    }
                    MiuiWifiDppUtils miuiWifiDppUtils = new MiuiWifiDppUtils(MiuiWifiSettings.this.getActivity());
                    miuiWifiDppUtils.setWifiQrCode(MiuiWifiSettings.this.mWifiQrcode);
                    miuiWifiDppUtils.setWifiNetworkConfig(MiuiWifiSettings.this.mWifiNetworkConfig);
                    miuiWifiDppUtils.showWifiShareDialog();
                }
            }
        };
    }

    private void initHandler() {
        HandlerThread handlerThread = new HandlerThread("MiuiWifiSetting");
        this.mWifiHelpThread = handlerThread;
        handlerThread.start();
        this.mWifiHelpWorkHandler = new MessageHandler(this.mWifiHelpThread.getLooper(), this);
    }

    private void initMiuiSynergySdk() {
        new Thread(new Runnable() { // from class: com.android.settings.wifi.MiuiWifiSettings.11
            @Override // java.lang.Runnable
            public void run() {
                if (MiuiWifiSettings.this.getActivity() != null) {
                    MiuiWifiSettings.this.mSameAccountAccessPoint = MiuiSynergySdk.getInstance().querySameAccountApInfo(MiuiWifiSettings.this.getActivity().getApplicationContext());
                    MiuiWifiSettings miuiWifiSettings = MiuiWifiSettings.this;
                    miuiWifiSettings.refreshVirtualApInfo(miuiWifiSettings.mSameAccountAccessPoint);
                }
            }
        }).start();
    }

    private void initOperatorDialog() {
        View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.android.settings.wifi.MiuiWifiSettings.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                int id = view.getId();
                if (id == R.id.provision_skip_btn) {
                    if (MiuiWifiSettings.this.mIsShowDataDialog && RegionUtils.IS_JP_KDDI && TelephonyManager.getDefault().getSimState() == 5) {
                        KDDIDataConnectionDialog.showDataConnectionDialog(MiuiWifiSettings.this.getActivity());
                        return;
                    } else if (SettingsFeatures.isNeedESIMCustmized() && 1 == MiuiWifiSettings.this.mESimCode && TelephonyManager.getDefault().getDataState() != 2) {
                        SoftBankEsimActivationDialog.show(MiuiWifiSettings.this.getActivity());
                        return;
                    } else {
                        OneTrackInterfaceUtils.track("provision_wifi_skip", null);
                    }
                } else if (id != R.id.provision_global_next_btn && id != R.id.provision_next_btn) {
                    OneTrackInterfaceUtils.track("provision_wifi_next", null);
                } else if (RegionUtils.IS_JP_KDDI) {
                    KDDIDataConnectionDialog.setDataEnabled(MiuiWifiSettings.this.getActivity());
                }
                MiStatInterfaceUtils.trackPageEnd("provision_wifi_page");
                MiuiWifiSettings.this.getActivity().setResult(-1);
                MiuiWifiSettings.this.finish();
            }
        };
        this.mSkipButton.setOnClickListener(onClickListener);
        this.mNextArrow.setOnClickListener(onClickListener);
        this.mNextButton.setOnClickListener(onClickListener);
    }

    private void initUI() {
        Preference findPreference;
        if (this.mIsInProvision || this.mIsFromKeyguard) {
            Preference findPreference2 = findPreference("wifi_settings");
            if (findPreference2 != null) {
                getPreferenceScreen().removePreference(findPreference2);
            }
            Preference findPreference3 = findPreference("wifi_assist");
            if (findPreference3 != null) {
                getPreferenceScreen().removePreference(findPreference3);
                return;
            }
            return;
        }
        if (!MiuiWifiAssistFeatureSupport.isWifiAssistAvailable(getContext()) && (findPreference = findPreference("wifi_assist")) != null) {
            getPreferenceScreen().removePreference(findPreference);
        }
        PreferenceGroup preferenceGroup = this.mWifiEntryPreferenceCategory;
        int i = R.layout.preference_wifi_category;
        preferenceGroup.setLayoutResource(i);
        this.mConnectedWifiEntryPreferenceCategory.setLayoutResource(i);
        addCmccNetworkNotificationPref();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void internalSmoothScrollToPosition() {
        RecyclerView listView = getListView();
        if (listView == null || listView.getChildCount() <= 0) {
            return;
        }
        listView.smoothScrollToPosition(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void isCustShowSkipButton() {
        getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.wifi.MiuiWifiSettings.10
            @Override // java.lang.Runnable
            public void run() {
                if (MiuiWifiSettings.this.mSkipButton == null) {
                    return;
                }
                NetworkInfo networkInfo = ((ConnectivityManager) MiuiWifiSettings.this.getActivity().getSystemService("connectivity")).getNetworkInfo(0);
                if (TelephonyManager.getDefault().getSimState() == 5 && networkInfo.isConnected()) {
                    MiuiWifiSettings.this.mSkipButton.setVisibility(0);
                } else {
                    MiuiWifiSettings.this.mSkipButton.setVisibility(8);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPad() {
        return Build.IS_TABLET || Log.isLoggable("MiuiQuickHotspotTest", 2);
    }

    private boolean isWifiConnected() {
        NetworkInfo networkInfo;
        return getActivity() != null && isAdded() && (networkInfo = ((ConnectivityManager) getActivity().getSystemService("connectivity")).getNetworkInfo(1)) != null && networkInfo.isConnected();
    }

    private boolean isWifiEntryConnected(WifiEntry wifiEntry) {
        return wifiEntry.getConnectedState() == 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayNearbyButtonIfNeeded$2(Intent intent, View view) {
        intent.setAction("android.intent.action.SEND");
        intent.addFlags(268435456);
        intent.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON);
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.SSID", this.mWifiConfig.getPrintableSsid());
        bundle.putString("android.intent.extra.PASSWORD", this.mWifiConfigurationManager.removeDoubleQuotes(this.mWifiConfig.preSharedKey));
        bundle.putString("android.intent.extra.SECURITY_TYPE", getSecurityString(this.mWifiConfig));
        bundle.putBoolean("android.intent.extra.HIDDEN_SSID", this.mWifiConfig.hiddenSSID);
        intent.putExtra("android.intent.extra.WIFI_CREDENTIALS_BUNDLE", bundle);
        getActivity().getApplicationContext().startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        removeAccessPointPre();
        updateWifiEntryPreferences();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        updateScanState(true);
    }

    private void manuallyAddNetwork() {
        WifiManager wifiManager = this.mWifiManager;
        if (wifiManager == null || !wifiManager.isWifiEnabled()) {
            return;
        }
        startFragment(this, MiuiAddNetworkFragment.class.getName(), 100, getExtraBundle(), 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshVirtualApInfo(final MiuiSynergySdk.SameAccountAccessPoint sameAccountAccessPoint) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.wifi.MiuiWifiSettings.14
            @Override // java.lang.Runnable
            public void run() {
                if (sameAccountAccessPoint == null) {
                    MiuiWifiSettings.this.removeVirtualAPPreference();
                    MiuiWifiSettings.this.mSameAccountAccessPoint = null;
                    return;
                }
                String str = "ssid: " + sameAccountAccessPoint.getSsid() + "battery: " + sameAccountAccessPoint.getBatteryPercent() + "is5G: " + sameAccountAccessPoint.isIs5G();
                Log.d(MiuiWifiSettings.TAG, "refreshVirtualApInfo: apInfo=" + str);
                MiuiSynergySdk.SameAccountAccessPoint sameAccountAccessPoint2 = MiuiWifiSettings.this.mSameAccountAccessPoint;
                MiuiWifiSettings.this.mSameAccountAccessPoint = sameAccountAccessPoint;
                if (sameAccountAccessPoint2 == null) {
                    MiuiWifiSettings.this.updateWifiEntryPreferences();
                } else if (MiuiWifiSettings.this.mVirtualAPPreference != null) {
                    MiuiWifiSettings.this.mVirtualAPPreference.updateBatteryLevel(sameAccountAccessPoint.getBatteryPercent());
                }
                if (MiuiWifiSettings.this.mSameAccountAPPreference != null) {
                    MiuiWifiSettings.this.mSameAccountAPPreference.updateBatteryLevel(sameAccountAccessPoint.getBatteryPercent());
                }
            }
        });
    }

    private void registerSameAccountApCallback() {
        initMiuiSynergySdk();
        this.mLocalSameAccountApCallback = new LocalSameAccountApCallback(this);
        if (getActivity() == null) {
            return;
        }
        MiuiSynergySdk.getInstance().registerSameAccountApCallback(getActivity().getApplicationContext(), this.mLocalSameAccountApCallback);
    }

    private void removeAccessPointPre() {
        MiuiNearbyApPreference miuiNearbyApPreference = this.nearbyAccessPointPre;
        if (miuiNearbyApPreference == null || miuiNearbyApPreference.getParent() == null) {
            return;
        }
        this.nearbyAccessPointPre.getParent().removePreference(this.nearbyAccessPointPre);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeVirtualAPPreference() {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.wifi.MiuiWifiSettings.13
            @Override // java.lang.Runnable
            public void run() {
                if (MiuiWifiSettings.this.mSameAccountAccessPoint == null || MiuiWifiSettings.this.mVirtualAPPreference == null || ((NetworkProviderSettings) MiuiWifiSettings.this).mConnectedWifiEntryPreferenceCategory == null) {
                    return;
                }
                MiuiWifiSettings.this.mVirtualAPPreference.updateState(0);
                MiuiWifiSettings.this.mVirtualAPPreference.updateSummary();
                ((NetworkProviderSettings) MiuiWifiSettings.this).mConnectedWifiEntryPreferenceCategory.removePreference(MiuiWifiSettings.this.mVirtualAPPreference);
            }
        });
    }

    private ArrayList<MiuiWifiEntryPreference> resortAccessPoint(List<WifiEntry> list) {
        ArrayList<MiuiWifiEntryPreference> arrayList = new ArrayList<>();
        HashSet hashSet = new HashSet();
        cacheRemoveAllPrefs(this.mWifiEntryPreferenceCategory);
        for (WifiEntry wifiEntry : list) {
            if (!hashSet.contains(wifiEntry.getTitle() + "-" + wifiEntry.getSecurity())) {
                String key = wifiEntry.getKey();
                MiuiWifiEntryPreference miuiWifiEntryPreference = null;
                MiuiWifiEntryPreference miuiWifiEntryPreference2 = (MiuiWifiEntryPreference) getCachedPreference(key);
                if (miuiWifiEntryPreference2 != null) {
                    if (miuiWifiEntryPreference2.getWifiEntry() == wifiEntry) {
                        miuiWifiEntryPreference = miuiWifiEntryPreference2;
                    } else {
                        removePreference(key);
                    }
                }
                if (miuiWifiEntryPreference == null) {
                    miuiWifiEntryPreference = new LongPressWifiEntryPreference(getPrefContext(), wifiEntry, this);
                    miuiWifiEntryPreference.update(wifiEntry);
                    miuiWifiEntryPreference.setTitle(wifiEntry.getTitle());
                    miuiWifiEntryPreference.setArrowClickListener(new MiuiWifiEntryPreference.ArrowClickListener(wifiEntry, this));
                    miuiWifiEntryPreference.setOnPreferenceChangeListener(this);
                }
                arrayList.add(miuiWifiEntryPreference);
                hashSet.add(wifiEntry.getTitle() + "-" + wifiEntry.getSecurity());
            }
        }
        removeCachedPrefs(this.mWifiEntryPreferenceCategory);
        Collections.sort(arrayList, MiuiWifiEntryPreference.getSuperComparator());
        return arrayList;
    }

    private void setConnectHelpPreferenceVisible(final boolean z) {
        if (this.mMainHandler == null) {
            this.mMainHandler = new MainThreadHandler(Looper.getMainLooper());
        }
        this.mMainHandler.post(new Runnable() { // from class: com.android.settings.wifi.MiuiWifiSettings.3
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Log.d(MiuiWifiSettings.TAG, "connect help prefernce visible: " + z);
                    Preference findPreference = MiuiWifiSettings.this.findPreference("wifi_connect_help");
                    if (findPreference != null) {
                        findPreference.setVisible(z);
                        if (z) {
                            findPreference.setOnPreferenceClickListener(MiuiWifiSettings.this.mWifiHelpClickListener);
                        }
                    }
                } catch (Exception e) {
                    Log.e(MiuiWifiSettings.TAG, "set connectHelpPreference visible error: " + e);
                }
            }
        });
    }

    private void showDialogForWifiTile() {
        List<ScanResult> scanResults = this.mWifiManager.getScanResults();
        this.mWifiNetworkScoreCache = new WifiNetworkScoreCache(getActivity().getApplicationContext(), new WifiNetworkScoreCache.CacheListener(this.mWorkHandler) { // from class: com.android.settings.wifi.MiuiWifiSettings.5
            public void networkCacheUpdated(List<ScoredNetwork> list) {
            }
        });
        for (ScanResult scanResult : scanResults) {
            String str = this.mExtraSsid;
            if (str != null && str.equals(scanResult.SSID)) {
                StandardWifiEntry standardWifiEntry = new StandardWifiEntry(getActivity().getApplicationContext(), this.mMainHandler, this.mWifiNetworkScoreCache, this.mWifiManager, false, scanResult.SSID, Utils.getSecurityTypesFromScanResult(scanResult));
                if (WifiUtils.getWifiEntrySecurity(scanResult) != 0) {
                    if (TextUtils.isEmpty(this.mOcrWifiPwd)) {
                        showDialog(standardWifiEntry, 1);
                    } else {
                        showDialog(standardWifiEntry, 1, this.mOcrWifiPwd);
                    }
                }
                this.mExtraSsid = null;
                this.mOcrWifiPwd = null;
                return;
            }
        }
    }

    private void showSharePasswordDialog(WifiConfiguration wifiConfiguration) {
        MiStatInterfaceUtils.trackEvent("wifi_share_password");
        OneTrackInterfaceUtils.track("wifi_share_password", null);
        this.mWifiConfig = wifiConfiguration;
        Bitmap wifiQrcode = QRCodeUtils.getWifiQrcode(getActivity(), wifiConfiguration);
        if (wifiQrcode == null) {
            return;
        }
        this.mIsShown = true;
        final Window window = getActivity().getWindow();
        final WindowManager.LayoutParams attributes = window.getAttributes();
        final float f = attributes.screenBrightness;
        attributes.screenBrightness = 0.8f;
        window.setAttributes(attributes);
        window.addFlags(128);
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.wifi_share_qrcode, (ViewGroup) null);
        ((ImageView) inflate.findViewById(R.id.qrcode)).setImageBitmap(wifiQrcode);
        displayNearbyButtonIfNeeded(inflate);
        AlertDialog create = new AlertDialog.Builder(getActivity(), R.style.AlertDialog_Theme_DayNight).setTitle(R.string.wifi_share_qrcode_title).setView(inflate).setPositiveButton(R.string.wifi_share_qrcode_finish, (DialogInterface.OnClickListener) null).create();
        this.mSharedDialog = create;
        create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.wifi.MiuiWifiSettings.8
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                MiuiWifiSettings.this.mIsShown = false;
                MiuiWifiSettings.this.mSharedDialog = null;
                MiuiWifiSettings.this.mWifiConfig = null;
                WindowManager.LayoutParams layoutParams = attributes;
                layoutParams.screenBrightness = f;
                window.setAttributes(layoutParams);
                window.clearFlags(128);
            }
        });
        AlertDialog alertDialog = this.mSharedDialog;
        if (alertDialog != null) {
            alertDialog.show();
        }
    }

    private void unRegisterSameAccountApCallback() {
        if (getActivity() != null && MiuiSynergySdk.getInstance().unRegisterSameAccountApCallback(getActivity()) == -1) {
            Log.e(TAG, "unRegisterSameAccountApCallback failed");
        }
    }

    private void updateCmccNetworkNotificationState() {
        if (!Build.IS_CM_CUSTOMIZATION_TEST || this.mCmccNetworkNotification == null || this.mWifiManager == null) {
            return;
        }
        this.mCmccNetworkNotification.setChecked(!WifiTipActivity.getCmccConnectedTipValue(getActivity()));
        this.mCmccNetworkNotification.setEnabled(this.mWifiManager.isWifiEnabled());
    }

    protected void addMessagePreference(int i) {
    }

    @Override // com.android.settings.network.NetworkProviderSettings
    protected void changeNextButtonState(boolean z) {
        super.changeNextButtonState(z);
        if (this.mIsInProvision && isAdded()) {
            if (z) {
                this.mNextButton.setTextColor(getResources().getColorStateList(R.drawable.provision_btn_next_color));
                this.mNextButton.setEnabled(true);
                this.mSkipButton.setEnabled(false);
                this.mNextArrow.setEnabled(true);
                return;
            }
            this.mNextButton.setTextColor(getResources().getColorStateList(R.drawable.provision_btn_next_color));
            this.mNextButton.setEnabled(false);
            this.mSkipButton.setEnabled(true);
            this.mNextArrow.setEnabled(false);
        }
    }

    public void closeFragment() {
        OneTrackInterfaceUtils.track("provision_wifi_skip", null);
        getActivity().setResult(-1);
        getActivity().finish();
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiWifiSettings.class.getName();
    }

    ComponentName getNearbySharingComponent() {
        String string = Settings.Secure.getString(getContext().getContentResolver(), "nearby_sharing_component");
        if (TextUtils.isEmpty(string)) {
            string = getString(17039923);
        }
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        return ComponentName.unflattenFromString(string);
    }

    @Override // com.android.settingslib.wifi.AccessPoint.AccessPointListener
    public void onAccessPointChanged(AccessPoint accessPoint) {
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("wifi_enable");
        this.mWifiEnablePreference = checkBoxPreference;
        this.mWifiEnabler = new MiuiWifiEnabler(this, checkBoxPreference);
        if (this.mIsInProvision) {
            getPreferenceScreen().removePreference(this.mWifiEnablePreference);
            getListView().setPadding(getListView().getPaddingLeft(), getListView().getPaddingTop(), getListView().getPaddingRight(), 0);
        }
        String stringExtra = getActivity().getIntent().getStringExtra("miui.intent.extra.OPEN_WIFI_SSID");
        if (stringExtra != null) {
            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.SSID = AccessPoint.convertToQuotedString(stringExtra);
            wifiConfiguration.allowedKeyManagement.set(0);
            this.mWifiManager.connect(wifiConfiguration, null);
        }
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.RestrictedSettingsFragment, androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (RegionUtils.IS_JP_KDDI) {
            this.mIsShowDataDialog = Settings.System.getInt(getContentResolver(), "pref_set_mobile_data_show", 1) != 0;
            if (intent != null && i == 1 && intent.getBooleanExtra("next", false)) {
                closeFragment();
            }
        }
        if (intent != null) {
            onFragmentResult(i, intent.getExtras());
        }
    }

    @Override // com.android.settings.OnBackPressedListener
    public boolean onBackPressed() {
        return this.mIsInProvision && mIsDisableBack;
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        NetworkInfo networkInfo;
        ActionBar appCompatActionBar;
        super.onCreate(bundle);
        if (RegionUtils.IS_JP_KDDI) {
            this.mIsShowDataDialog = Settings.System.getInt(getContentResolver(), "pref_set_mobile_data_show", 1) != 0;
        }
        if (SettingsFeatures.isNeedESIMCustmized()) {
            this.mESimCode = getActivity().getIntent().getIntExtra("eSim", -1);
        }
        if (bundle != null) {
            boolean z = bundle.getBoolean("is_dialog_shown");
            this.mIsShown = z;
            if (z) {
                showSharePasswordDialog((WifiConfiguration) bundle.getParcelable("wifi_configuration_info"));
            }
        }
        this.mWifiManager = (WifiManager) getSystemService("wifi");
        if (!Build.IS_TABLET) {
            getActivity().setRequestedOrientation(1);
        }
        this.mIsInProvision = getActivity().getIntent().getBooleanExtra("wifi_setup_wizard", false);
        this.mIsFromKeyguard = getActivity().getIntent().getBooleanExtra("wifi_settings_keyguard", false);
        this.mIsFromPhoneActivation = getActivity().getIntent().getBooleanExtra("from_phone_activation", false);
        this.mIsDeviceLockNeed = SettingsFeatures.isDeviceLockNeed(getContext());
        if (this.mIsInProvision) {
            if (!this.mWifiManager.isWifiEnabled()) {
                this.mWifiManager.setWifiEnabled(true);
            }
            if ((getActivity() instanceof AppCompatActivity) && (appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar()) != null) {
                appCompatActionBar.hide();
            }
            setThemeRes(R.style.Theme_Provision_Notitle_WifiSettings);
            getActivity().getWindow().setSoftInputMode(48);
            mIsDisableBack = getActivity().getIntent().getBooleanExtra("extra_disable_back", false);
        } else if (getActivity() instanceof MiuiSettings) {
            setThemeRes(R.style.Theme_WifiSettings_showTitle);
        } else {
            setThemeRes(R.style.Theme_WifiSettings);
        }
        if (this.mIsFromKeyguard && this.mIsFromPhoneActivation) {
            getActivity().getWindow().addFlags(524288);
        }
        initUI();
        this.mMainHandler = new MainThreadHandler(Looper.getMainLooper());
        HandlerThread handlerThread = new HandlerThread(TAG + "{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
        this.mWorkThread = handlerThread;
        handlerThread.start();
        this.mWorkHandler = new Handler(this.mWorkThread.getLooper());
        this.mWifiConfigurationManager = WifiConfigurationManager.getInstance(getActivity());
        Network currentNetwork = this.mWifiManager.getCurrentNetwork();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService("connectivity");
        this.mConnManager = connectivityManager;
        if (currentNetwork != null && (networkInfo = connectivityManager.getNetworkInfo(currentNetwork)) != null) {
            this.mNetworkState = networkInfo.getState();
        }
        initBroadcastReceiver(this.mWorkHandler);
        this.mIsWorkHandlerQuit = false;
        this.mLanguage = Locale.getDefault().getLanguage();
        initDppHandler();
        this.mIsRestricted = isUiRestricted();
        if (isPad()) {
            registerSameAccountApCallback();
        }
        initHandler();
        Preference findPreference = findPreference("wifi_connect_help");
        if (findPreference != null) {
            findPreference.setVisible(false);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (viewGroup != null && !this.mIsInProvision) {
            MiuiUtils.updateFragmentView(getActivity(), viewGroup);
        }
        this.rootView = layoutInflater.inflate(this.mIsInProvision ? R.layout.provision_base_layout : R.layout.wifi_settings, viewGroup, false);
        if (this.mIsInProvision) {
            ViewGroup.LayoutParams layoutParams = new PreferenceFrameLayout.LayoutParams(-1, -1);
            ((PreferenceFrameLayout.LayoutParams) layoutParams).removeBorders = true;
            this.rootView.setLayoutParams(layoutParams);
        }
        ViewGroup viewGroup2 = (ViewGroup) this.rootView.findViewById(R.id.prefs_container);
        View onCreateView = super.onCreateView(layoutInflater, viewGroup2, bundle);
        RecyclerView recyclerView = (RecyclerView) onCreateView.findViewById(R.id.recycler_view);
        this.recyclerview = recyclerView;
        recyclerView.setItemAnimator(null);
        viewGroup2.addView(onCreateView);
        return this.rootView;
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.RestrictedSettingsFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        if (Build.IS_TABLET) {
            getActivity().setRequestedOrientation(2);
            this.mLocalSameAccountApCallback = null;
        }
        super.onDestroy();
        getActivity().unregisterReceiver(this.mReceiver);
        this.mWorkThread.quit();
        this.mIsWorkHandlerQuit = true;
        MessageHandler messageHandler = this.mWifiHelpWorkHandler;
        if (messageHandler != null) {
            messageHandler.removeCallbacksAndMessages(null);
            HandlerThread handlerThread = this.mWifiHelpThread;
            if (handlerThread != null) {
                handlerThread.quit();
            }
            this.mWifiHelpWorkHandler = null;
        }
        if (isPad()) {
            unRegisterSameAccountApCallback();
        }
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.rootView = null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public void onFragmentResult(int i, Bundle bundle) {
        WifiConfiguration wifiConfiguration;
        if (i == 100) {
            WifiConfiguration wifiConfiguration2 = (WifiConfiguration) bundle.getParcelable("config");
            if (wifiConfiguration2 != null) {
                this.mUserSelect = true;
                if (!isSlaveWifiConnectedWhenAddNetwork()) {
                    this.mWifiManager.save(wifiConfiguration2, this.mSaveListener);
                    this.mWifiManager.connect(wifiConfiguration2, this.mConnectListener);
                } else if (!isWifiSwitchPromptNotRemind()) {
                    showWifiSwitchPrompt(wifiConfiguration2, null);
                } else {
                    this.mSlaveWifiUtils.disconnectSlaveWifi();
                    this.mWifiManager.save(wifiConfiguration2, this.mSaveListener);
                    this.mWifiManager.connect(wifiConfiguration2, this.mConnectListener);
                }
            }
        } else if (i == 200 && (wifiConfiguration = (WifiConfiguration) bundle.getParcelable("config")) != null) {
            this.mWifiManager.save(wifiConfiguration, this.mSaveListener);
            int networkId = this.mWifiManager.getConnectionInfo().getNetworkId();
            int i2 = wifiConfiguration.networkId;
            if (networkId != i2 || i2 == -1) {
                return;
            }
            if (bundle.getShort("mac_random_changed") == 1) {
                this.mWifiManager.disconnect();
            }
            WifiEntry connectedWifiEntry = this.mWifiPickerTracker.getConnectedWifiEntry();
            if (connectedWifiEntry != null) {
                connectedWifiEntry.connect(null);
            } else {
                this.mWifiManager.connect(wifiConfiguration, this.mSaveListener);
            }
        }
    }

    @Override // com.android.settingslib.wifi.AccessPoint.AccessPointListener
    public void onLevelChanged(AccessPoint accessPoint) {
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        if (this.mIsInProvision && mIsDisableBack) {
            return false;
        }
        finish();
        return true;
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        CheckBoxPreference checkBoxPreference = this.mWifiEnablePreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(null);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if ("wifi_enable".equals(key)) {
            if (this.recyclerview.isComputingLayout()) {
                return false;
            }
            this.mWifiEnabler.checkedChanged(booleanValue);
            return true;
        }
        if ("manually_add_network".equals(key)) {
            manuallyAddNetwork();
        } else if ("cmcc_network_notification".equals(key)) {
            WifiTipActivity.setCmccConnectedTipValue(getActivity(), !booleanValue);
        }
        if (preference instanceof LongPressWifiEntryPreference) {
            this.mUserSelect = true;
            if (this.mIsInProvision) {
                MiStatInterfaceUtils.trackEvent("provision_wifi_connect_count");
                OneTrackInterfaceUtils.track("provision_wifi_connect_count", null);
            }
            LongPressWifiEntryPreference longPressWifiEntryPreference = (LongPressWifiEntryPreference) preference;
            WifiEntry wifiEntry = longPressWifiEntryPreference.getWifiEntry();
            if (key != null && key.startsWith("slave-") && wifiEntry.getSlaveConnectedState() == 2) {
                return true;
            }
            if (longPressWifiEntryPreference.isConnected() && isWifiEntryConnected(wifiEntry) && !this.mIsInProvision) {
                NetworkCapabilities networkCapabilities = ((ConnectivityManager) getSystemService("connectivity")).getNetworkCapabilities(this.mWifiManager.getCurrentNetwork());
                if ((wifiEntry.getSecurity() == 2 || wifiEntry.getSecurity() == 5) && ((networkCapabilities == null || !networkCapabilities.hasCapability(17)) && !com.android.settingslib.wifi.WifiUtils.isInMishow(getActivity()) && this.mSharedDialog == null)) {
                    showSharePasswordDialog(wifiEntry.getWifiConfiguration());
                    return true;
                } else if (wifiEntry.canSignIn()) {
                    wifiEntry.signIn(null);
                }
            } else if (wifiEntry.isSaved()) {
                longPressWifiEntryPreference.setConnected(false);
                android.net.wifi.WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
                WifiConfiguration wifiConfiguration = wifiEntry.getWifiConfiguration();
                if (wifiConfiguration == null || wifiConfiguration.networkId < 0 || ((connectionInfo == null || connectionInfo.getNetworkId() == wifiConfiguration.networkId) && wifiConfiguration.status != 1)) {
                    if (wifiEntry.getWifiConfiguration() == null || !wifiEntry.getWifiConfiguration().isPasspoint()) {
                        longPressWifiEntryPreference.setConnected(true);
                    } else {
                        this.mWifiManager.connect(wifiEntry.getWifiConfiguration(), this.mConnectListener);
                    }
                } else if (WifiUtils.getConnectingType(wifiEntry) != 1) {
                    if (!maybeSameBandAsSlaveWifi(wifiEntry)) {
                        this.mWifiManager.connect(wifiConfiguration.networkId, this.mConnectListener);
                    } else if (isWifiSwitchPromptNotRemind()) {
                        this.mSlaveWifiUtils.disconnectSlaveWifi();
                        this.mWifiManager.connect(wifiConfiguration.networkId, this.mConnectListener);
                    } else {
                        showWifiSwitchPrompt(null, wifiEntry);
                    }
                }
            }
        }
        return true;
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if ("manually_add_network".equals(preference.getKey())) {
            manuallyAddNetwork();
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mIsWifiShareTurnOn = WifiShareUtils.isWifiShareTurnOn(getActivity());
        updateCmccNetworkNotificationState();
        if (this.mIsInProvision) {
            MiStatInterfaceUtils.trackPageStart("provision_wifi_page");
            MiStatInterfaceUtils.trackEvent("provision_wifi_page_count");
        }
        this.mExtraSsid = getActivity().getIntent().getStringExtra("ssid");
        this.mOcrWifiPwd = getActivity().getIntent().getStringExtra("key_ocr_wifi_token");
        if (this.mExtraSsid != null) {
            showDialogForWifiTile();
            Intent intent = new Intent(getActivity().getIntent());
            intent.putExtra("ssid", (String) null);
            getActivity().setIntent(intent);
        }
        if (this.nearbyAccessPointPre == null) {
            this.nearbyAccessPointPre = new MiuiNearbyApPreference(getPrefContext());
        }
        this.nearbyAccessPointPre.setOnSettingsClickListener(this.mRefreshListener);
        getView().postDelayed(this.mUpdateRefreshRunnable, 1000L);
        this.mWifiEnablePreference.setOnPreferenceChangeListener(this);
        this.mIsRestricted = isUiRestricted();
        MessageHandler messageHandler = this.mWifiHelpWorkHandler;
        if (messageHandler != null) {
            messageHandler.sendEmptyMessage(1);
        }
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        WifiConfiguration wifiConfiguration;
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("is_dialog_shown", this.mIsShown);
        if (!this.mIsShown || (wifiConfiguration = this.mWifiConfig) == null) {
            return;
        }
        bundle.putParcelable("wifi_configuration_info", wifiConfiguration);
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        CheckBoxPreference checkBoxPreference = this.mWifiEnablePreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(null);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry.WifiEntryCallback
    public void onUpdated() {
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (this.mIsInProvision) {
            this.mNextButton = (TextView) getActivity().findViewById(R.id.provision_next_btn);
            this.mSkipButton = (TextView) getActivity().findViewById(R.id.provision_skip_btn);
            this.mBackButton = (TextView) getActivity().findViewById(R.id.provision_back_btn);
            this.mBackArrow = (ImageButton) getActivity().findViewById(R.id.provision_global_back_btn);
            this.mNextArrow = (ImageButton) getActivity().findViewById(R.id.provision_global_next_btn);
            TextView textView = this.mNextButton;
            if (textView != null) {
                textView.setEnabled(false);
            }
            this.mSkipButton.setVisibility(0);
            if (Locale.CHINESE.getLanguage().equalsIgnoreCase(this.mLanguage)) {
                if (this.mIsFromPhoneActivation) {
                    this.mNextButton.setVisibility(8);
                    this.mSkipButton.setVisibility(8);
                } else {
                    this.mNextButton.setVisibility(0);
                }
                this.mBackButton.setVisibility(0);
                this.mBackArrow.setVisibility(8);
                this.mNextArrow.setVisibility(8);
            } else {
                this.mNextButton.setVisibility(8);
                this.mBackButton.setVisibility(8);
                this.mBackArrow.setVisibility(0);
                this.mNextArrow.setVisibility(0);
            }
            if (Build.IS_MIPAD) {
                this.mNextButton.setText("");
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mNextButton.getLayoutParams();
                layoutParams.width = -2;
                layoutParams.gravity = 1;
                this.mNextButton.setLayoutParams(layoutParams);
            }
            if (Build.IS_PRIVATE_BUILD) {
                this.mSkipButton.setVisibility(8);
            }
            if (this.mIsDeviceLockNeed) {
                isCustShowSkipButton();
            }
            changeNextButtonState(isWifiConnected());
            if (RegionUtils.IS_JP_KDDI || SettingsFeatures.isNeedESIMCustmized()) {
                initOperatorDialog();
            }
        }
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onWifiEntriesChanged() {
        changeNextButtonState(isWifiConnected() || this.mNetworkState == NetworkInfo.State.CONNECTED);
        if (getActivity() == null || getView() == null || this.mIsRestricted || this.mWifiPickerTracker.getWifiState() != 3) {
            return;
        }
        View view = getView();
        Handler handler = view.getHandler();
        if (handler == null || !handler.hasCallbacks(this.mUpdateWifiEntryPreferencesRunnable)) {
            view.postDelayed(this.mUpdateWifiEntryPreferencesRunnable, 300L);
        }
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.wifitrackerlib.BaseWifiTracker.BaseWifiTrackerCallback
    /* renamed from: onWifiStateChanged */
    public void lambda$onInternetTypeChanged$4() {
        if (this.mIsRestricted) {
            return;
        }
        int wifiState = this.mWifiPickerTracker.getWifiState();
        if (wifiState == 1) {
            removeConnectedWifiEntryPreference();
            removeWifiEntryPreference();
            addMessagePreference(R.string.wifi_empty_list_wifi_off);
            focusOnBackIcon();
        } else if (wifiState == 2) {
            removeConnectedWifiEntryPreference();
            removeWifiEntryPreference();
        } else if (wifiState != 3) {
            Log.e(TAG, "Invalid state");
        } else {
            focusOnBackIcon();
            this.mConnectedWifiEntryPreferenceCategory.setVisible(true);
            this.mWifiEntryPreferenceCategory.setVisible(true);
            if (isPad()) {
                initMiuiSynergySdk();
            }
        }
        super.lambda$onInternetTypeChanged$4();
    }

    @Override // com.android.settings.network.NetworkProviderSettings
    protected void reloadDialog(int i, String str) {
        List<ScanResult> scanResults = this.mWifiManager.getScanResults();
        this.mWifiNetworkScoreCache = new WifiNetworkScoreCache(getActivity().getApplicationContext(), new WifiNetworkScoreCache.CacheListener(this.mWorkHandler) { // from class: com.android.settings.wifi.MiuiWifiSettings.6
            public void networkCacheUpdated(List<ScoredNetwork> list) {
            }
        });
        for (ScanResult scanResult : scanResults) {
            if (str != null && str.equals(scanResult.SSID)) {
                StandardWifiEntry standardWifiEntry = new StandardWifiEntry(getActivity().getApplicationContext(), this.mMainHandler, this.mWifiNetworkScoreCache, this.mWifiManager, false, scanResult.SSID, Utils.getSecurityTypesFromScanResult(scanResult));
                if (WifiUtils.getWifiEntrySecurity(scanResult) != 0) {
                    showDialog(standardWifiEntry, i);
                    return;
                }
                return;
            }
        }
        Log.e(TAG, "No scanResult for reload dialog.");
    }

    protected void updateScanState(boolean z) {
        MiuiNearbyApPreference miuiNearbyApPreference;
        if (this.mIsInProvision || (miuiNearbyApPreference = this.nearbyAccessPointPre) == null) {
            return;
        }
        if (z) {
            miuiNearbyApPreference.startScanAnimation();
        } else {
            miuiNearbyApPreference.stopScanAnimation();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Removed duplicated region for block: B:43:0x0104  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0164  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x017c  */
    @Override // com.android.settings.network.NetworkProviderSettings
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateWifiEntryPreferences() {
        /*
            Method dump skipped, instructions count: 638
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.MiuiWifiSettings.updateWifiEntryPreferences():void");
    }
}
