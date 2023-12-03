package com.android.settings.network;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserManager;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.EventLog;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;
import com.android.settings.AirplaneModeEnabler;
import com.android.settings.R;
import com.android.settings.RestrictedSettingsFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.datausage.DataUsagePreference;
import com.android.settings.datausage.DataUsageUtils;
import com.android.settings.network.InternetUpdater;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.wifi.AddNetworkFragment;
import com.android.settings.wifi.AddWifiNetworkPreference;
import com.android.settings.wifi.ConfigureWifiEntryFragment;
import com.android.settings.wifi.ConnectedWifiEntryPreference;
import com.android.settings.wifi.LinkablePreference;
import com.android.settings.wifi.LongPressWifiEntryPreference;
import com.android.settings.wifi.MiuiWifiEnabler;
import com.android.settings.wifi.WifiConnectListener;
import com.android.settings.wifi.WifiDialog2;
import com.android.settings.wifi.WifiPickerTrackerHelper;
import com.android.settings.wifi.WifiUtils;
import com.android.settings.wifi.details2.WifiNetworkDetailsFragment2;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settings.wifi.passpoint.MiuiPasspointR1Utils;
import com.android.settings.wifi.passpoint.PasspointConfigureReceiver;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.LayoutPreference;
import com.android.settingslib.wifi.SlaveWifiUtils;
import com.android.settingslib.wifi.WifiEntryPreference;
import com.android.settingslib.wifi.WifiSavedConfigUtils;
import com.android.settingslib.wifi.WifiTracker;
import com.android.wifitrackerlib.BaseWifiTracker;
import com.android.wifitrackerlib.PasspointR1WifiEntry;
import com.android.wifitrackerlib.PasspointWifiEntry;
import com.android.wifitrackerlib.WifiEntry;
import com.android.wifitrackerlib.WifiPickerTracker;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import miui.util.FeatureParser;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class NetworkProviderSettings extends RestrictedSettingsFragment implements WifiPickerTracker.WifiPickerTrackerCallback, WifiDialog2.WifiDialog2Listener, DialogInterface.OnDismissListener, AirplaneModeEnabler.OnAirplaneModeChangedListener, InternetUpdater.InternetChangeListener {
    static final int ADD_NETWORK_REQUEST = 2;
    static final int MENU_ID_DISCONNECT = 3;
    static final int MENU_ID_FORGET = 4;
    static final String PREF_KEY_CONNECTED_ACCESS_POINTS = "connected_access_point";
    static final String PREF_KEY_DATA_USAGE = "wifi_data_usage";
    static final String PREF_KEY_FIRST_ACCESS_POINTS = "first_access_points";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.wifi_settings) { // from class: com.android.settings.network.NetworkProviderSettings.4
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (WifiSavedConfigUtils.getAllConfigsCount(context, (WifiManager) context.getSystemService(WifiManager.class)) == 0) {
                nonIndexableKeys.add("saved_networks");
            }
            if (!DataUsageUtils.hasWifiRadio(context)) {
                nonIndexableKeys.add(NetworkProviderSettings.PREF_KEY_DATA_USAGE);
            }
            return nonIndexableKeys;
        }
    };
    private ActionBar mActionBar;
    AddWifiNetworkPreference mAddWifiNetworkPreference;
    AirplaneModeEnabler mAirplaneModeEnabler;
    Preference mAirplaneModeMsgPreference;
    private boolean mClickedConnect;
    Preference mConfigureWifiSettingsPreference;
    protected WifiManager.ActionListener mConnectListener;
    ConnectedEthernetNetworkController mConnectedEthernetNetworkController;
    protected PreferenceCategory mConnectedWifiEntryPreferenceCategory;
    ConnectivityManager mConnectivityManager;
    DataUsagePreference mDataUsagePreference;
    private WifiDialog2 mDialog;
    private int mDialogMode;
    private WifiEntry mDialogWifiEntry;
    private String mDialogWifiEntryKey;
    private String mDialogWifiSSID;
    private boolean mEnableNextOnConnection;
    private IntentFilter mFilter;
    PreferenceCategory mFirstWifiEntryPreferenceCategory;
    protected WifiManager.ActionListener mForgetListener;
    final Runnable mHideProgressBarRunnable;
    protected InternetResetHelper mInternetResetHelper;
    InternetUpdater mInternetUpdater;
    boolean mIsAdmin;
    protected boolean mIsDppQrCodeFgShow;
    protected boolean mIsRestricted;
    protected boolean mIsShareDialogShow;
    private boolean mIsViewLoading;
    private boolean mIsWifiEntryListStale;
    private NetworkMobileProviderController mNetworkMobileProviderController;
    private String mOcrWifiPwd;
    private String mOpenSsid;
    private BroadcastReceiver mReceiver;
    final Runnable mRemoveLoadingRunnable;
    LayoutPreference mResetInternetPreference;
    protected WifiManager.ActionListener mSaveListener;
    Preference mSavedNetworksPreference;
    private WifiEntry mSelectedWifiEntry;
    protected SlaveWifiUtils mSlaveWifiUtils;
    private LinkablePreference mStatusMessagePreference;
    final Runnable mUpdateWifiEntryPreferencesRunnable;
    protected WifiEntryPreference.UserBadgeCache mUserBadgeCache;
    protected MiuiWifiEnabler mWifiEnabler;
    protected PreferenceGroup mWifiEntryPreferenceCategory;
    protected WifiManager mWifiManager;
    protected WifiPickerTracker mWifiPickerTracker;
    private WifiPickerTrackerHelper mWifiPickerTrackerHelper;
    protected WifiTracker mWifiTracker;

    /* loaded from: classes.dex */
    public class FirstWifiEntryPreference extends ConnectedWifiEntryPreference {
        public FirstWifiEntryPreference(Context context, WifiEntry wifiEntry, Fragment fragment) {
            super(context, wifiEntry, fragment);
        }

        @Override // com.android.settingslib.wifi.WifiEntryPreference
        protected int getIconColorAttr() {
            return 16843817;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class WifiConnectActionListener implements WifiManager.ActionListener {
        private WifiConnectActionListener() {
        }

        public void onFailure(int i) {
            if (NetworkProviderSettings.this.isFinishingOrDestroyed()) {
                return;
            }
            Toast.makeText(NetworkProviderSettings.this.getContext(), R.string.wifi_failed_connect_message, 0).show();
        }

        public void onSuccess() {
            NetworkProviderSettings.this.mClickedConnect = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class WifiEntryConnectCallback implements WifiEntry.ConnectCallback {
        final WifiEntry mConnectWifiEntry;
        final boolean mEditIfNoConfig;
        final boolean mFullScreenEdit;

        WifiEntryConnectCallback(WifiEntry wifiEntry, boolean z, boolean z2) {
            this.mConnectWifiEntry = wifiEntry;
            this.mEditIfNoConfig = z;
            this.mFullScreenEdit = z2;
        }

        @Override // com.android.wifitrackerlib.WifiEntry.ConnectCallback
        public void onConnectResult(int i) {
            if (NetworkProviderSettings.this.isFinishingOrDestroyed()) {
                return;
            }
            if (i == 0) {
                NetworkProviderSettings.this.mClickedConnect = true;
            } else if (i != 1) {
                if (i == 2) {
                    Toast.makeText(NetworkProviderSettings.this.getContext(), R.string.wifi_failed_connect_message, 0).show();
                }
            } else if (this.mEditIfNoConfig) {
                if (this.mFullScreenEdit) {
                    NetworkProviderSettings.this.launchConfigNewNetworkFragment(this.mConnectWifiEntry);
                } else {
                    NetworkProviderSettings.this.showDialog(this.mConnectWifiEntry, 1);
                }
            }
        }
    }

    public NetworkProviderSettings() {
        super("no_config_wifi");
        this.mRemoveLoadingRunnable = new Runnable() { // from class: com.android.settings.network.NetworkProviderSettings$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                NetworkProviderSettings.this.lambda$new$0();
            }
        };
        this.mIsWifiEntryListStale = true;
        this.mUpdateWifiEntryPreferencesRunnable = new Runnable() { // from class: com.android.settings.network.NetworkProviderSettings$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                NetworkProviderSettings.this.lambda$new$1();
            }
        };
        this.mHideProgressBarRunnable = new Runnable() { // from class: com.android.settings.network.NetworkProviderSettings$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                NetworkProviderSettings.this.lambda$new$2();
            }
        };
        this.mIsAdmin = true;
    }

    private void addPreferences() {
        if (isFromMiuiSlaveWifiSettings()) {
            addPreferencesFromResource(R.xml.slave_wifi_settings);
        } else {
            addPreferencesFromResource(R.xml.wifi_settings);
        }
        this.mAirplaneModeMsgPreference = findPreference("airplane_mode_message");
        updateAirplaneModeMsgPreference(this.mAirplaneModeEnabler.isAirplaneModeOn());
        this.mConnectedWifiEntryPreferenceCategory = (PreferenceCategory) findPreference(PREF_KEY_CONNECTED_ACCESS_POINTS);
        this.mFirstWifiEntryPreferenceCategory = (PreferenceCategory) findPreference(PREF_KEY_FIRST_ACCESS_POINTS);
        this.mWifiEntryPreferenceCategory = (PreferenceGroup) findPreference("access_points");
        this.mConfigureWifiSettingsPreference = findPreference("configure_wifi_settings");
        this.mSavedNetworksPreference = findPreference("saved_networks");
        this.mAddWifiNetworkPreference = new AddWifiNetworkPreference(getPrefContext());
        DataUsagePreference dataUsagePreference = (DataUsagePreference) findPreference(PREF_KEY_DATA_USAGE);
        this.mDataUsagePreference = dataUsagePreference;
        dataUsagePreference.setVisible(DataUsageUtils.hasWifiRadio(getContext()));
        LayoutPreference layoutPreference = (LayoutPreference) findPreference("resetting_your_internet");
        this.mResetInternetPreference = layoutPreference;
        if (layoutPreference != null) {
            layoutPreference.setVisible(false);
        }
        this.mUserBadgeCache = new WifiEntryPreference.UserBadgeCache(getPackageManager());
        this.mStatusMessagePreference = (LinkablePreference) findPreference("wifi_status_message");
        PreferenceCategory preferenceCategory = this.mFirstWifiEntryPreferenceCategory;
        if (preferenceCategory != null) {
            preferenceCategory.setVisible(false);
        }
        DataUsagePreference dataUsagePreference2 = this.mDataUsagePreference;
        if (dataUsagePreference2 != null) {
            dataUsagePreference2.setVisible(false);
        }
        LinkablePreference linkablePreference = this.mStatusMessagePreference;
        if (linkablePreference != null) {
            linkablePreference.setVisible(false);
        }
        Preference preference = this.mSavedNetworksPreference;
        if (preference != null) {
            preference.setVisible(false);
        }
        Preference preference2 = this.mConfigureWifiSettingsPreference;
        if (preference2 != null) {
            preference2.setVisible(false);
        }
    }

    private boolean canForgetNetwork() {
        return this.mSelectedWifiEntry.canForget() && !WifiUtils.isNetworkLockedDown(getActivity(), this.mSelectedWifiEntry.getWifiConfiguration());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fixConnectivity() {
        if (this.mInternetResetHelper == null) {
            InternetResetHelper internetResetHelper = new InternetResetHelper(getContext(), getLifecycle());
            this.mInternetResetHelper = internetResetHelper;
            internetResetHelper.setResettingPreference(this.mResetInternetPreference);
            this.mInternetResetHelper.setMobileNetworkController(this.mNetworkMobileProviderController);
            this.mInternetResetHelper.setWifiTogglePreference(findPreference("main_toggle_wifi"));
            this.mInternetResetHelper.addWifiNetworkPreference(this.mConnectedWifiEntryPreferenceCategory);
            this.mInternetResetHelper.addWifiNetworkPreference(this.mFirstWifiEntryPreferenceCategory);
            this.mInternetResetHelper.addWifiNetworkPreference(this.mWifiEntryPreferenceCategory);
        }
        this.mInternetResetHelper.restart();
    }

    private void forget(WifiEntry wifiEntry) {
        this.mMetricsFeatureProvider.action(getActivity(), 137, new Pair[0]);
        wifiEntry.forget(null);
        if ((wifiEntry instanceof PasspointWifiEntry) && wifiEntry.isPasspointR1()) {
            MiuiPasspointR1Utils.saveRegisterState(getContext(), null, false);
        }
    }

    private void handleAddNetworkSubmitEvent(Intent intent) {
        WifiConfiguration wifiConfiguration = (WifiConfiguration) intent.getParcelableExtra("wifi_config_key");
        if (wifiConfiguration != null) {
            this.mWifiManager.save(wifiConfiguration, this.mSaveListener);
        }
    }

    private void initActionBar() {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (getActivity() instanceof AppCompatActivity) {
            this.mActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar();
        }
        boolean booleanExtra = activity.getIntent().getBooleanExtra("wifi_setup_wizard", false);
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null && booleanExtra) {
            actionBar.hide();
            return;
        }
        ImageView imageView = new ImageView(activity);
        if (!isFromMiuiSlaveWifiSettings() ? this.mWifiManager.isWifiEnabled() : this.mSlaveWifiUtils.isSlaveWifiEnabled()) {
            imageView.setBackgroundResource(R.drawable.ic_union_disable);
            imageView.setImportantForAccessibility(2);
            imageView.setEnabled(false);
        } else {
            imageView.setBackgroundResource(R.drawable.ic_union);
            imageView.setContentDescription(getResources().getString(R.string.wifi_dpp_scan_qr_code));
            imageView.setEnabled(true);
        }
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.network.NetworkProviderSettings.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                NetworkProviderSettings networkProviderSettings = NetworkProviderSettings.this;
                networkProviderSettings.mIsShareDialogShow = false;
                networkProviderSettings.mIsDppQrCodeFgShow = false;
                MiStatInterfaceUtils.trackEvent("wifi_qrCode_scanner");
                OneTrackInterfaceUtils.track("wifi_qrCode_scanner", null);
                WifiEntry connectedWifiEntry = NetworkProviderSettings.this.mWifiPickerTracker.getConnectedWifiEntry();
                int security = connectedWifiEntry != null ? connectedWifiEntry.getSecurity() : 0;
                if ((security != 2 && security != 5) || TextUtils.equals("mediatek", FeatureParser.getString("vendor")) || !NetworkProviderSettings.this.mWifiManager.isEasyConnectSupported() || NetworkProviderSettings.this.isFromMiuiSlaveWifiSettings()) {
                    activity.startActivityForResult(WifiDppUtils.getEnrolleeQrCodeScannerIntent(NetworkProviderSettings.this.getActivity(), null, NetworkProviderSettings.this.isFromMiuiSlaveWifiSettings()), 0);
                    activity.overridePendingTransition(0, 0);
                    return;
                }
                Intent miuiConfiguratorQrCodeScannerIntentOrNull = WifiDppUtils.getMiuiConfiguratorQrCodeScannerIntentOrNull(NetworkProviderSettings.this.getActivity(), NetworkProviderSettings.this.mWifiManager, connectedWifiEntry);
                if (miuiConfiguratorQrCodeScannerIntentOrNull != null) {
                    activity.startActivityForResult(miuiConfiguratorQrCodeScannerIntentOrNull, 1);
                    activity.overridePendingTransition(0, 0);
                }
            }
        });
        ActionBar actionBar2 = this.mActionBar;
        if (actionBar2 != null) {
            actionBar2.setEndView(imageView);
        }
    }

    private boolean isAdminUser() {
        UserManager userManager = (UserManager) getSystemService(UserManager.class);
        if (userManager == null) {
            return true;
        }
        return userManager.isAdminUser();
    }

    private static boolean isDisabledByWrongPassword(WifiEntry wifiEntry) {
        WifiConfiguration.NetworkSelectionStatus networkSelectionStatus;
        WifiConfiguration wifiConfiguration = wifiEntry.getWifiConfiguration();
        return (wifiConfiguration == null || (networkSelectionStatus = wifiConfiguration.getNetworkSelectionStatus()) == null || networkSelectionStatus.getNetworkSelectionStatus() == 0 || 8 != networkSelectionStatus.getNetworkSelectionDisableReason()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isFromMiuiSlaveWifiSettings() {
        return "MiuiSlaveWifiSettings".equals(getClass().getSimpleName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean isVerboseLoggingEnabled() {
        return BaseWifiTracker.isVerboseLoggingEnabled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (this.mIsViewLoading) {
            this.mIsViewLoading = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        updateWifiEntryPreferences();
        getView().postDelayed(this.mRemoveLoadingRunnable, 10L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2() {
        setProgressBarVisible(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onContextItemSelected$3() {
        launchWifiDppConfiguratorActivity(this.mSelectedWifiEntry);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onWifiEntriesChanged$5(WifiEntry wifiEntry) {
        return TextUtils.equals(this.mOpenSsid, wifiEntry.getSsid());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onWifiEntriesChanged$6(WifiEntry wifiEntry) {
        return (wifiEntry.getSecurity() == 0 || wifiEntry.getSecurity() == 4) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onWifiEntriesChanged$7(WifiEntry wifiEntry) {
        return !wifiEntry.isSaved() || isDisabledByWrongPassword(wifiEntry);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateWifiEntryPreferences$10(WifiEntry wifiEntry, WifiEntryPreference wifiEntryPreference) {
        openSubscriptionHelpPage(wifiEntry);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateWifiEntryPreferences$8(WifiEntry wifiEntry, ConnectedWifiEntryPreference connectedWifiEntryPreference, Preference preference) {
        if (wifiEntry.canSignIn()) {
            wifiEntry.signIn(null);
            return true;
        }
        launchNetworkDetailsFragment(connectedWifiEntryPreference);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateWifiEntryPreferences$9(ConnectedWifiEntryPreference connectedWifiEntryPreference, ConnectedWifiEntryPreference connectedWifiEntryPreference2) {
        launchNetworkDetailsFragment(connectedWifiEntryPreference);
    }

    private void launchAddNetworkFragment() {
        new SubSettingLauncher(getContext()).setTitleRes(R.string.wifi_add_network).setDestination(AddNetworkFragment.class.getName()).setSourceMetricsCategory(getMetricsCategory()).setResultListener(this, 2).launch();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchConfigNewNetworkFragment(WifiEntry wifiEntry) {
        Bundle bundle = new Bundle();
        bundle.putString("key_chosen_wifientry_key", wifiEntry.getKey());
        new SubSettingLauncher(getContext()).setTitleText(wifiEntry.getTitle()).setDestination(ConfigureWifiEntryFragment.class.getName()).setArguments(bundle).setSourceMetricsCategory(getMetricsCategory()).setResultListener(this, 3).launch();
    }

    private void launchNetworkDetailsFragment(LongPressWifiEntryPreference longPressWifiEntryPreference) {
        WifiEntry wifiEntry = longPressWifiEntryPreference.getWifiEntry();
        Context context = getContext();
        CharSequence title = FeatureFlagUtils.isEnabled(context, "settings_wifi_details_datausage_header") ? wifiEntry.getTitle() : context.getText(R.string.pref_title_network_details);
        Bundle bundle = new Bundle();
        bundle.putString("key_chosen_wifientry_key", wifiEntry.getKey());
        new SubSettingLauncher(context).setTitleText(title).setDestination(WifiNetworkDetailsFragment2.class.getName()).setArguments(bundle).setSourceMetricsCategory(getMetricsCategory()).launch();
    }

    private void launchWifiDppConfiguratorActivity(WifiEntry wifiEntry) {
        Intent configuratorQrCodeGeneratorIntentOrNull = WifiDppUtils.getConfiguratorQrCodeGeneratorIntentOrNull(getContext(), this.mWifiManager, wifiEntry);
        if (configuratorQrCodeGeneratorIntentOrNull == null) {
            Log.e("NetworkProviderSettings", "Launch Wi-Fi DPP QR code generator with a wrong Wi-Fi network!");
            return;
        }
        this.mMetricsFeatureProvider.action(0, 1710, 1595, null, Integer.MIN_VALUE);
        startActivity(configuratorQrCodeGeneratorIntentOrNull);
    }

    private void onAddNetworkPressed() {
        launchAddNetworkFragment();
    }

    private void restrictUi() {
        if (!isUiRestrictedByOnlyAdmin()) {
            getEmptyTextView().setText(R.string.wifi_empty_list_user_restricted);
        }
        getPreferenceScreen().removeAll();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showPrivacyDialog(final WifiEntry wifiEntry) {
        String string = getString(R.string.passpoint_r1_privacy_dialog_url_text);
        String string2 = getString(R.string.passpoint_r1_privacy_dialog_message, string);
        SpannableString spannableString = new SpannableString(string2);
        int indexOf = spannableString.toString().indexOf(string);
        int length = string.length() + indexOf;
        spannableString.setSpan(new URLSpan("https://hs2.exands.com:10443/xiaomi/tnc.html"), indexOf, length, 33);
        spannableString.setSpan(new AbsoluteSizeSpan(36), string2.indexOf("\n\n"), string2.length(), 33);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.bootloader_url)), indexOf, length, 33);
        new AlertDialog.Builder(getActivity()).setTitle(R.string.passpoint_r1_privacy_dialog_title).setMessage(spannableString).setCancelable(false).setPositiveButton(R.string.passpoint_r1_privacy_dialog_yes, new DialogInterface.OnClickListener() { // from class: com.android.settings.network.NetworkProviderSettings.10
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                PasspointConfigureReceiver.enablePasspointWifiReceiver(NetworkProviderSettings.this.getPrefContext());
                NetworkProviderSettings.this.connect(wifiEntry, false, false);
                NetworkProviderSettings.this.mClickedConnect = true;
            }
        }).setNegativeButton(R.string.passpoint_r1_privacy_dialog_no, new DialogInterface.OnClickListener() { // from class: com.android.settings.network.NetworkProviderSettings.9
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show().getMessageView().setMovementMethod(LinkMovementMethod.getInstance());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateActionBar(boolean z) {
        View endView;
        ActionBar actionBar = this.mActionBar;
        if (actionBar == null || (endView = actionBar.getEndView()) == null || !(endView instanceof ImageView)) {
            return;
        }
        ImageView imageView = (ImageView) endView;
        if (z) {
            imageView.setBackgroundResource(R.drawable.ic_union);
            imageView.setContentDescription(getResources().getString(R.string.wifi_dpp_scan_qr_code));
            imageView.setImportantForAccessibility(1);
        } else {
            imageView.setBackgroundResource(R.drawable.ic_union_disable);
            imageView.setImportantForAccessibility(2);
        }
        imageView.setEnabled(z);
    }

    private void updateAirplaneModeMsgPreference(boolean z) {
        Preference preference = this.mAirplaneModeMsgPreference;
        if (preference != null) {
            preference.setVisible(false);
        }
    }

    private void updateWifiEntryPreferencesDelayed() {
        if (getActivity() == null || this.mIsRestricted || this.mWifiPickerTracker.getWifiState() != 3) {
            return;
        }
        View view = getView();
        Handler handler = view.getHandler();
        if (handler == null || !handler.hasCallbacks(this.mUpdateWifiEntryPreferencesRunnable)) {
            setProgressBarVisible(true);
            view.postDelayed(this.mUpdateWifiEntryPreferencesRunnable, 300L);
        }
    }

    void addForgetMenuIfSuitable(ContextMenu contextMenu) {
        if (this.mIsAdmin) {
            contextMenu.add(0, 4, 0, R.string.forget);
        }
    }

    void addShareMenuIfSuitable(ContextMenu contextMenu) {
        if (this.mIsAdmin) {
            contextMenu.add(0, 7, 0, R.string.share);
            return;
        }
        Log.w("NetworkProviderSettings", "Don't add the Wi-Fi share menu because the user is not an admin.");
        EventLog.writeEvent(1397638484, "206986392", -1, "User is not an admin");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void changeNextButtonState(boolean z) {
        if (this.mEnableNextOnConnection && hasNextButton()) {
            getNextButton().setEnabled(z);
        }
    }

    void connect(WifiEntry wifiEntry, boolean z, boolean z2) {
        this.mMetricsFeatureProvider.action(getActivity(), 135, wifiEntry.isSaved());
        wifiEntry.connect(new WifiEntryConnectCallback(wifiEntry, z, z2));
    }

    ConnectedWifiEntryPreference createConnectedWifiEntryPreference(WifiEntry wifiEntry) {
        return this.mInternetUpdater.getInternetType() == 2 ? new ConnectedWifiEntryPreference(getPrefContext(), wifiEntry, this) : new FirstWifiEntryPreference(getPrefContext(), wifiEntry, this);
    }

    LongPressWifiEntryPreference createLongPressWifiEntryPreference(WifiEntry wifiEntry) {
        return new LongPressWifiEntryPreference(getPrefContext(), wifiEntry, this);
    }

    PreferenceCategory getConnectedWifiPreferenceCategory() {
        if (this.mInternetUpdater.getInternetType() == 2) {
            this.mFirstWifiEntryPreferenceCategory.setVisible(false);
            this.mFirstWifiEntryPreferenceCategory.removeAll();
            return this.mConnectedWifiEntryPreferenceCategory;
        }
        this.mConnectedWifiEntryPreferenceCategory.setVisible(false);
        this.mConnectedWifiEntryPreferenceCategory.removeAll();
        return this.mFirstWifiEntryPreferenceCategory;
    }

    Intent getHelpIntent(Context context, String str) {
        return HelpUtils.getHelpIntent(context, str, context.getClass().getName());
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_wifi;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 103;
    }

    void handleAddNetworkRequest(int i, Intent intent) {
        if (i == -1) {
            handleAddNetworkSubmitEvent(intent);
        }
    }

    boolean isPhoneOnCall() {
        return ((TelephonyManager) getActivity().getSystemService(TelephonyManager.class)).getCallState() != 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isSlaveWifiConnectedWhenAddNetwork() {
        NetworkInfo networkInfo;
        return (isFromMiuiSlaveWifiSettings() || (networkInfo = this.mConnectivityManager.getNetworkInfo(this.mSlaveWifiUtils.getSlaveWifiCurrentNetwork())) == null || !networkInfo.isConnected()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isWifiSwitchPromptNotRemind() {
        return PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getBoolean("dual_wifi_switching_not_remind", false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean maybeSameBandAsSlaveWifi(WifiEntry wifiEntry) {
        if (isFromMiuiSlaveWifiSettings()) {
            return false;
        }
        WifiInfo wifiSlaveConnectionInfo = this.mSlaveWifiUtils.getWifiSlaveConnectionInfo();
        NetworkInfo networkInfo = this.mConnectivityManager.getNetworkInfo(this.mSlaveWifiUtils.getSlaveWifiCurrentNetwork());
        if (wifiSlaveConnectionInfo == null || networkInfo == null || !networkInfo.isConnected()) {
            return false;
        }
        if (com.android.settingslib.wifi.WifiUtils.is24GHz(wifiSlaveConnectionInfo.getFrequency())) {
            if (wifiEntry.isOnly5Ghz()) {
                return false;
            }
        } else if (wifiEntry.isOnly24Ghz()) {
            return false;
        }
        return true;
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        WifiPickerTrackerHelper wifiPickerTrackerHelper = new WifiPickerTrackerHelper(getSettingsLifecycle(), getContext(), this);
        this.mWifiPickerTrackerHelper = wifiPickerTrackerHelper;
        this.mWifiPickerTracker = wifiPickerTrackerHelper.getWifiPickerTracker();
        this.mInternetUpdater = new InternetUpdater(getContext(), getSettingsLifecycle(), this);
        if (getActivity() != null) {
            this.mWifiManager = (WifiManager) getActivity().getSystemService(WifiManager.class);
            this.mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(ConnectivityManager.class);
        }
        this.mConnectListener = new WifiConnectListener(getActivity());
        this.mSaveListener = new WifiManager.ActionListener() { // from class: com.android.settings.network.NetworkProviderSettings.2
            public void onFailure(int i) {
                FragmentActivity activity = NetworkProviderSettings.this.getActivity();
                if (activity != null) {
                    Toast.makeText(activity, R.string.wifi_failed_save_message, 0).show();
                }
            }

            public void onSuccess() {
            }
        };
        this.mForgetListener = new WifiManager.ActionListener() { // from class: com.android.settings.network.NetworkProviderSettings.3
            public void onFailure(int i) {
                FragmentActivity activity = NetworkProviderSettings.this.getActivity();
                if (activity != null) {
                    Toast.makeText(activity, R.string.wifi_failed_forget_message, 0).show();
                }
            }

            public void onSuccess() {
            }
        };
        setHasOptionsMenu(true);
        if (bundle != null) {
            this.mDialogMode = bundle.getInt("dialog_mode", -1);
            this.mDialogWifiSSID = bundle.getString("dialog_ssid");
            this.mDialogWifiEntryKey = bundle.getString("wifi_ap_key");
            if (this.mDialog != null) {
                removeDialog(1);
                this.mDialog = null;
            }
            int i = this.mDialogMode;
            if (i != -1 && this.mDialogWifiEntryKey != null) {
                reloadDialog(i, this.mDialogWifiSSID);
            }
        }
        Intent intent = getActivity().getIntent();
        this.mEnableNextOnConnection = intent.getBooleanExtra("wifi_enable_next_on_connect", false);
        if (intent.hasExtra("wifi_start_connect_ssid")) {
            this.mOpenSsid = intent.getStringExtra("wifi_start_connect_ssid");
        }
        NetworkMobileProviderController networkMobileProviderController = this.mNetworkMobileProviderController;
        if (networkMobileProviderController != null) {
            networkMobileProviderController.setWifiPickerTrackerHelper(this.mWifiPickerTrackerHelper);
        }
    }

    @Override // com.android.settings.RestrictedSettingsFragment, androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        WifiConfiguration wifiConfiguration;
        WifiDialog2 wifiDialog2;
        super.onActivityResult(i, i2, intent);
        if (i == 2) {
            handleAddNetworkRequest(i2, intent);
        } else if (i == 0) {
            if (i2 != -1 || (wifiDialog2 = this.mDialog) == null) {
                return;
            }
            wifiDialog2.dismiss();
        } else if (i == 3) {
            if (i2 != -1 || (wifiConfiguration = (WifiConfiguration) intent.getParcelableExtra("network_config_key")) == null) {
                return;
            }
            this.mWifiManager.connect(wifiConfiguration, new WifiConnectActionListener());
        } else if (i == 4) {
        } else {
            boolean z = this.mIsRestricted;
            boolean isUiRestricted = isUiRestricted();
            this.mIsRestricted = isUiRestricted;
            if (z && !isUiRestricted && getPreferenceScreen().getPreferenceCount() == 0) {
                addPreferences();
            }
        }
    }

    @Override // com.android.settings.AirplaneModeEnabler.OnAirplaneModeChangedListener
    public void onAirplaneModeChanged(boolean z) {
        updateAirplaneModeMsgPreference(z);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onContextItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 2) {
            connect(this.mSelectedWifiEntry, true, false);
            return true;
        } else if (itemId == 3) {
            this.mSelectedWifiEntry.disconnect(null);
            return true;
        } else if (itemId == 4) {
            forget(this.mSelectedWifiEntry);
            return true;
        } else if (itemId == 5) {
            showDialog(this.mSelectedWifiEntry, 2);
            return true;
        } else if (itemId != 7) {
            return super.onContextItemSelected(menuItem);
        } else {
            WifiDppUtils.showLockScreen(getContext(), new Runnable() { // from class: com.android.settings.network.NetworkProviderSettings$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    NetworkProviderSettings.this.lambda$onContextItemSelected$3();
                }
            });
            return true;
        }
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mAirplaneModeEnabler = new AirplaneModeEnabler(getContext(), this);
        setAnimationAllowed(false);
        addPreferences();
        this.mIsRestricted = isUiRestricted();
        this.mIsAdmin = isAdminUser();
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.NetworkProviderSettings.1
            /* JADX WARN: Code restructure failed: missing block: B:12:0x002d, code lost:
            
                if (r6.getIntExtra("wifi_state", 18) == 17) goto L8;
             */
            /* JADX WARN: Code restructure failed: missing block: B:5:0x0017, code lost:
            
                if (r6.getIntExtra("wifi_state", 4) == 3) goto L8;
             */
            /* JADX WARN: Code restructure failed: missing block: B:7:0x001a, code lost:
            
                r1 = false;
             */
            /* JADX WARN: Code restructure failed: missing block: B:8:0x001b, code lost:
            
                r3 = r1;
             */
            @Override // android.content.BroadcastReceiver
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public void onReceive(android.content.Context r5, android.content.Intent r6) {
                /*
                    r4 = this;
                    java.lang.String r5 = r6.getAction()
                    java.lang.String r0 = "android.net.wifi.WIFI_STATE_CHANGED"
                    boolean r0 = r0.equals(r5)
                    r1 = 1
                    java.lang.String r2 = "wifi_state"
                    r3 = 0
                    if (r0 == 0) goto L1d
                    r5 = 4
                    int r5 = r6.getIntExtra(r2, r5)
                    r6 = 3
                    if (r5 != r6) goto L1a
                    goto L1b
                L1a:
                    r1 = r3
                L1b:
                    r3 = r1
                    goto L30
                L1d:
                    java.lang.String r0 = "android.net.wifi.WIFI_SLAVE_STATE_CHANGED"
                    boolean r5 = r0.equals(r5)
                    if (r5 == 0) goto L30
                    r5 = 18
                    int r5 = r6.getIntExtra(r2, r5)
                    r6 = 17
                    if (r5 != r6) goto L1a
                    goto L1b
                L30:
                    com.android.settings.network.NetworkProviderSettings r4 = com.android.settings.network.NetworkProviderSettings.this
                    com.android.settings.network.NetworkProviderSettings.access$000(r4, r3)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.NetworkProviderSettings.AnonymousClass1.onReceive(android.content.Context, android.content.Intent):void");
            }
        };
        this.mFilter = new IntentFilter();
        if (isFromMiuiSlaveWifiSettings()) {
            this.mFilter.addAction("android.net.wifi.WIFI_SLAVE_STATE_CHANGED");
        } else {
            this.mFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        }
        this.mSlaveWifiUtils = SlaveWifiUtils.getInstance(getActivity());
    }

    @Override // androidx.fragment.app.Fragment, android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        Preference preference = (Preference) view.getTag();
        if (preference instanceof LongPressWifiEntryPreference) {
            WifiEntry wifiEntry = ((LongPressWifiEntryPreference) preference).getWifiEntry();
            this.mSelectedWifiEntry = wifiEntry;
            contextMenu.setHeaderTitle(wifiEntry.getTitle());
            if (this.mSelectedWifiEntry.canConnect()) {
                contextMenu.add(0, 2, 0, R.string.wifi_connect);
            }
            if (this.mSelectedWifiEntry.canDisconnect()) {
                if (this.mSelectedWifiEntry.canShare()) {
                    addShareMenuIfSuitable(contextMenu);
                }
                contextMenu.add(0, 3, 1, R.string.wifi_disconnect_button_text);
            }
            if (canForgetNetwork()) {
                addForgetMenuIfSuitable(contextMenu);
            }
            if (WifiUtils.isNetworkLockedDown(getActivity(), this.mSelectedWifiEntry.getWifiConfiguration()) || !this.mSelectedWifiEntry.isSaved() || this.mSelectedWifiEntry.getConnectedState() == 2) {
                return;
            }
            contextMenu.add(0, 5, 0, R.string.wifi_modify);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i != 1) {
            return super.onCreateDialog(i);
        }
        WifiDialog2 createModal = WifiDialog2.createModal(getActivity(), this, this.mDialogWifiEntry, this.mDialogMode);
        this.mDialog = createModal;
        return createModal;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mAirplaneModeEnabler.close();
        super.onDestroy();
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null) {
            actionBar.setEndView(null);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public void onDialogShowing() {
        super.onDialogShowing();
        setOnDismissListener(this);
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        this.mDialog = null;
        this.mDialogWifiEntry = null;
        this.mDialogWifiEntryKey = null;
        this.mDialogWifiSSID = null;
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onForget(WifiDialog2 wifiDialog2) {
        forget(wifiDialog2.getWifiEntry());
    }

    @Override // com.android.settings.network.InternetUpdater.InternetChangeListener
    public void onInternetTypeChanged(int i) {
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.network.NetworkProviderSettings$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                NetworkProviderSettings.this.lambda$onInternetTypeChanged$4();
            }
        });
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedNetworksChanged() {
        if (isFinishingOrDestroyed()) {
            return;
        }
        setAdditionalSettingsSummaries();
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedSubscriptionsChanged() {
        if (isFinishingOrDestroyed()) {
            return;
        }
        setAdditionalSettingsSummaries();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 6) {
            if (isPhoneOnCall()) {
                showResetInternetDialog();
                return true;
            }
            fixConnectivity();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        MiuiWifiEnabler miuiWifiEnabler = this.mWifiEnabler;
        if (miuiWifiEnabler != null) {
            miuiWifiEnabler.pause();
        }
        if (getActivity() != null) {
            getActivity().unregisterReceiver(this.mReceiver);
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getFragment() != null) {
            preference.setOnPreferenceClickListener(null);
            return super.onPreferenceTreeClick(preference);
        }
        if (preference instanceof LongPressWifiEntryPreference) {
            WifiEntry wifiEntry = ((LongPressWifiEntryPreference) preference).getWifiEntry();
            if (wifiEntry instanceof PasspointR1WifiEntry) {
                if (!maybeSameBandAsSlaveWifi(wifiEntry)) {
                    showPrivacyDialog(wifiEntry);
                } else if (isWifiSwitchPromptNotRemind()) {
                    this.mSlaveWifiUtils.disconnectSlaveWifi();
                    showPrivacyDialog(wifiEntry);
                } else {
                    showWifiSwitchPrompt(null, wifiEntry);
                }
                return true;
            }
            int connectingType = WifiUtils.getConnectingType(wifiEntry);
            if (connectingType != 1) {
                if (connectingType != 2 && connectingType != 3 && connectingType != 4 && !wifiEntry.isSaved()) {
                    showDialog(wifiEntry, 1);
                }
            } else if (2 != wifiEntry.getConnectedState()) {
                if (!maybeSameBandAsSlaveWifi(wifiEntry)) {
                    connect(wifiEntry, false, false);
                } else if (isWifiSwitchPromptNotRemind()) {
                    this.mSlaveWifiUtils.disconnectSlaveWifi();
                    connect(wifiEntry, false, false);
                } else {
                    showWifiSwitchPrompt(null, wifiEntry);
                }
            }
        } else if (preference != this.mAddWifiNetworkPreference) {
            return super.onPreferenceTreeClick(preference);
        } else {
            onAddNetworkPressed();
        }
        return true;
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        FragmentActivity activity = getActivity();
        super.onResume();
        initActionBar();
        if (activity != null) {
            activity.registerReceiver(this.mReceiver, this.mFilter);
        }
        MiuiWifiEnabler miuiWifiEnabler = this.mWifiEnabler;
        if (miuiWifiEnabler != null) {
            miuiWifiEnabler.resume(activity);
        }
        boolean z = this.mIsRestricted;
        boolean isUiRestricted = isUiRestricted();
        this.mIsRestricted = isUiRestricted;
        if (!z && isUiRestricted) {
            restrictUi();
        }
        changeNextButtonState(this.mWifiPickerTracker.getConnectedWifiEntry() != null);
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.mDialog != null) {
            bundle.putInt("dialog_mode", this.mDialogMode);
            bundle.putString("wifi_ap_key", this.mDialogWifiEntryKey);
            bundle.putString("dialog_ssid", this.mDialogWifiSSID);
        }
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onScan(WifiDialog2 wifiDialog2, String str) {
        startActivityForResult(WifiDppUtils.getEnrolleeQrCodeScannerIntent(str), 0);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mIsRestricted) {
            restrictUi();
        } else {
            this.mAirplaneModeEnabler.start();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        this.mIsWifiEntryListStale = true;
        getView().removeCallbacks(this.mRemoveLoadingRunnable);
        getView().removeCallbacks(this.mUpdateWifiEntryPreferencesRunnable);
        getView().removeCallbacks(this.mHideProgressBarRunnable);
        this.mAirplaneModeEnabler.stop();
        super.onStop();
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onSubmit(WifiDialog2 wifiDialog2) {
        int mode = wifiDialog2.getMode();
        WifiConfiguration config = wifiDialog2.getController().getConfig();
        WifiEntry wifiEntry = wifiDialog2.getWifiEntry();
        if (mode == 2) {
            if (config == null) {
                Toast.makeText(getContext(), R.string.wifi_failed_save_message, 0).show();
            } else {
                this.mWifiManager.save(config, this.mSaveListener);
            }
        } else if (mode == 1 || (mode == 0 && wifiEntry.canConnect())) {
            if (maybeSameBandAsSlaveWifi(wifiEntry)) {
                if (!isWifiSwitchPromptNotRemind()) {
                    showWifiSwitchPrompt(config, wifiEntry);
                    return;
                }
                this.mSlaveWifiUtils.disconnectSlaveWifi();
            }
            if (config == null) {
                connect(wifiEntry, false, false);
            } else {
                this.mWifiManager.connect(config, new WifiConnectActionListener());
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        setPinnedHeaderView(R.layout.progress_header);
        setProgressBarVisible(false);
        WifiManager wifiManager = (WifiManager) activity.getSystemService(WifiManager.class);
        this.mWifiManager = wifiManager;
        if (wifiManager != null) {
            this.mIsViewLoading = true;
            getView().postDelayed(this.mRemoveLoadingRunnable, this.mWifiManager.isWifiEnabled() ? 1000L : 100L);
        }
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onWifiEntriesChanged() {
        if (this.mIsWifiEntryListStale) {
            this.mIsWifiEntryListStale = false;
            updateWifiEntryPreferences();
        } else {
            updateWifiEntryPreferencesDelayed();
        }
        changeNextButtonState(this.mWifiPickerTracker.getConnectedWifiEntry() != null);
        if (this.mOpenSsid != null) {
            Optional<WifiEntry> findFirst = this.mWifiPickerTracker.getWifiEntries().stream().filter(new Predicate() { // from class: com.android.settings.network.NetworkProviderSettings$$ExternalSyntheticLambda8
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onWifiEntriesChanged$5;
                    lambda$onWifiEntriesChanged$5 = NetworkProviderSettings.this.lambda$onWifiEntriesChanged$5((WifiEntry) obj);
                    return lambda$onWifiEntriesChanged$5;
                }
            }).filter(new Predicate() { // from class: com.android.settings.network.NetworkProviderSettings$$ExternalSyntheticLambda10
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onWifiEntriesChanged$6;
                    lambda$onWifiEntriesChanged$6 = NetworkProviderSettings.lambda$onWifiEntriesChanged$6((WifiEntry) obj);
                    return lambda$onWifiEntriesChanged$6;
                }
            }).filter(new Predicate() { // from class: com.android.settings.network.NetworkProviderSettings$$ExternalSyntheticLambda9
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onWifiEntriesChanged$7;
                    lambda$onWifiEntriesChanged$7 = NetworkProviderSettings.lambda$onWifiEntriesChanged$7((WifiEntry) obj);
                    return lambda$onWifiEntriesChanged$7;
                }
            }).findFirst();
            if (findFirst.isPresent()) {
                this.mOpenSsid = null;
                launchConfigNewNetworkFragment(findFirst.get());
            }
        }
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker.BaseWifiTrackerCallback
    /* renamed from: onWifiStateChanged  reason: merged with bridge method [inline-methods] */
    public void lambda$onInternetTypeChanged$4() {
        if (this.mIsRestricted) {
            return;
        }
        int wifiState = this.mWifiPickerTracker.getWifiState();
        if (isVerboseLoggingEnabled()) {
            Log.i("NetworkProviderSettings", "onWifiStateChanged called with wifi state: " + wifiState);
        }
        if (wifiState == 0) {
            removeConnectedWifiEntryPreference();
            removeWifiEntryPreference();
        } else if (wifiState == 1) {
            removeConnectedWifiEntryPreference();
            removeWifiEntryPreference();
            setAdditionalSettingsSummaries();
            setProgressBarVisible(false);
            this.mClickedConnect = false;
        } else if (wifiState != 2) {
            if (wifiState != 3) {
                return;
            }
            updateWifiEntryPreferences();
        } else {
            removeConnectedWifiEntryPreference();
            removeWifiEntryPreference();
            setProgressBarVisible(true);
        }
    }

    void openSubscriptionHelpPage(WifiEntry wifiEntry) {
        Intent helpIntent = getHelpIntent(getContext(), wifiEntry.getHelpUriString());
        if (helpIntent != null) {
            try {
                startActivityForResult(helpIntent, 4);
            } catch (ActivityNotFoundException unused) {
                Log.e("NetworkProviderSettings", "Activity was not found for intent, " + helpIntent.toString());
            }
        }
    }

    protected void reloadDialog(int i, String str) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void removeConnectedWifiEntryPreference() {
        this.mConnectedWifiEntryPreferenceCategory.removeAll();
        this.mConnectedWifiEntryPreferenceCategory.setVisible(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void removeWifiEntryPreference() {
        this.mWifiEntryPreferenceCategory.removeAll();
        this.mWifiEntryPreferenceCategory.setVisible(false);
    }

    void setAdditionalSettingsSummaries() {
        if (this.mConfigureWifiSettingsPreference == null) {
            return;
        }
        this.mSavedNetworksPreference.setVisible(false);
        this.mConfigureWifiSettingsPreference.setVisible(false);
    }

    protected void setProgressBarVisible(boolean z) {
        showPinnedHeader(z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showDialog(WifiEntry wifiEntry, int i) {
        if (WifiUtils.isNetworkLockedDown(getActivity(), wifiEntry.getWifiConfiguration()) && wifiEntry.getConnectedState() == 2) {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(), RestrictedLockUtilsInternal.getDeviceOwner(getActivity()));
            return;
        }
        if (this.mDialog != null) {
            removeDialog(1);
            this.mDialog = null;
        }
        this.mDialogWifiEntry = wifiEntry;
        this.mDialogWifiEntryKey = wifiEntry.getKey();
        this.mDialogMode = i;
        this.mDialogWifiSSID = wifiEntry.getSsid();
        showDialog(1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showDialog(WifiEntry wifiEntry, int i, String str) {
        if (WifiUtils.isNetworkLockedDown(getActivity(), wifiEntry.getWifiConfiguration()) && wifiEntry.getConnectedState() == 2) {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(), RestrictedLockUtilsInternal.getDeviceOwner(getActivity()));
            return;
        }
        if (this.mDialog != null) {
            removeDialog(1);
            this.mDialog = null;
        }
        this.mDialogWifiEntry = wifiEntry;
        this.mDialogWifiEntryKey = wifiEntry.getKey();
        this.mDialogWifiSSID = wifiEntry.getSsid();
        this.mDialogMode = i;
        this.mOcrWifiPwd = str;
        showDialog(1);
    }

    void showResetInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.reset_your_internet_title).setMessage(R.string.reset_internet_text).setPositiveButton(R.string.tts_reset, new DialogInterface.OnClickListener() { // from class: com.android.settings.network.NetworkProviderSettings.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                NetworkProviderSettings.this.fixConnectivity();
            }
        }).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create().show();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showWifiSwitchPrompt(final WifiConfiguration wifiConfiguration, final WifiEntry wifiEntry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.dual_wifi_switching_prompt));
        builder.setMessage(getString(R.string.dual_wifi_switching_summary));
        builder.setCheckBox(false, getString(R.string.dual_wifi_switching_not_remind));
        builder.setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() { // from class: com.android.settings.network.NetworkProviderSettings.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(getString(R.string.screen_confirm), new DialogInterface.OnClickListener() { // from class: com.android.settings.network.NetworkProviderSettings.8
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (((AlertDialog) dialogInterface).isChecked()) {
                    PreferenceManager.getDefaultSharedPreferences(NetworkProviderSettings.this.getActivity().getApplicationContext()).edit().putBoolean("dual_wifi_switching_not_remind", true).commit();
                }
                NetworkProviderSettings.this.mSlaveWifiUtils.disconnectSlaveWifi();
                WifiEntry wifiEntry2 = wifiEntry;
                if (wifiEntry2 instanceof PasspointR1WifiEntry) {
                    NetworkProviderSettings.this.showPrivacyDialog(wifiEntry2);
                } else {
                    WifiConfiguration wifiConfiguration2 = wifiConfiguration;
                    if (wifiConfiguration2 == null) {
                        NetworkProviderSettings.this.connect(wifiEntry2, false, false);
                    } else {
                        NetworkProviderSettings networkProviderSettings = NetworkProviderSettings.this;
                        networkProviderSettings.mWifiManager.connect(wifiConfiguration2, new WifiConnectActionListener());
                    }
                }
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    protected void updateWifiEntryPreferences() {
        if (getActivity() == null || getView() == null || this.mIsRestricted || this.mWifiPickerTracker.getWifiState() != 3) {
            return;
        }
        this.mWifiEntryPreferenceCategory.setVisible(true);
        final WifiEntry connectedWifiEntry = this.mWifiPickerTracker.getConnectedWifiEntry();
        PreferenceCategory connectedWifiPreferenceCategory = getConnectedWifiPreferenceCategory();
        connectedWifiPreferenceCategory.setVisible(connectedWifiEntry != null);
        if (connectedWifiEntry != null) {
            LongPressWifiEntryPreference longPressWifiEntryPreference = (LongPressWifiEntryPreference) connectedWifiPreferenceCategory.findPreference(connectedWifiEntry.getKey());
            if (longPressWifiEntryPreference == null || longPressWifiEntryPreference.getWifiEntry() != connectedWifiEntry) {
                connectedWifiPreferenceCategory.removeAll();
                final ConnectedWifiEntryPreference createConnectedWifiEntryPreference = createConnectedWifiEntryPreference(connectedWifiEntry);
                createConnectedWifiEntryPreference.setKey(connectedWifiEntry.getKey());
                createConnectedWifiEntryPreference.refresh();
                connectedWifiPreferenceCategory.addPreference(createConnectedWifiEntryPreference);
                createConnectedWifiEntryPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.network.NetworkProviderSettings$$ExternalSyntheticLambda0
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        boolean lambda$updateWifiEntryPreferences$8;
                        lambda$updateWifiEntryPreferences$8 = NetworkProviderSettings.this.lambda$updateWifiEntryPreferences$8(connectedWifiEntry, createConnectedWifiEntryPreference, preference);
                        return lambda$updateWifiEntryPreferences$8;
                    }
                });
                createConnectedWifiEntryPreference.setOnGearClickListener(new ConnectedWifiEntryPreference.OnGearClickListener() { // from class: com.android.settings.network.NetworkProviderSettings$$ExternalSyntheticLambda1
                    @Override // com.android.settings.wifi.ConnectedWifiEntryPreference.OnGearClickListener
                    public final void onGearClick(ConnectedWifiEntryPreference connectedWifiEntryPreference) {
                        NetworkProviderSettings.this.lambda$updateWifiEntryPreferences$9(createConnectedWifiEntryPreference, connectedWifiEntryPreference);
                    }
                });
                if (this.mClickedConnect) {
                    this.mClickedConnect = false;
                    scrollToPreference(connectedWifiPreferenceCategory);
                }
            }
        } else {
            connectedWifiPreferenceCategory.removeAll();
        }
        cacheRemoveAllPrefs(this.mWifiEntryPreferenceCategory);
        boolean z = false;
        int i = 0;
        for (final WifiEntry wifiEntry : this.mWifiPickerTracker.getWifiEntries()) {
            String key = wifiEntry.getKey();
            LongPressWifiEntryPreference longPressWifiEntryPreference2 = (LongPressWifiEntryPreference) getCachedPreference(key);
            if (longPressWifiEntryPreference2 != null) {
                if (longPressWifiEntryPreference2.getWifiEntry() == wifiEntry) {
                    longPressWifiEntryPreference2.setOrder(i);
                    i++;
                    z = true;
                } else {
                    removePreference(key);
                }
            }
            LongPressWifiEntryPreference createLongPressWifiEntryPreference = createLongPressWifiEntryPreference(wifiEntry);
            createLongPressWifiEntryPreference.setKey(wifiEntry.getKey());
            int i2 = i + 1;
            createLongPressWifiEntryPreference.setOrder(i);
            createLongPressWifiEntryPreference.refresh();
            if (wifiEntry.getHelpUriString() != null) {
                createLongPressWifiEntryPreference.setOnButtonClickListener(new WifiEntryPreference.OnButtonClickListener() { // from class: com.android.settings.network.NetworkProviderSettings$$ExternalSyntheticLambda2
                    @Override // com.android.settingslib.wifi.WifiEntryPreference.OnButtonClickListener
                    public final void onButtonClick(WifiEntryPreference wifiEntryPreference) {
                        NetworkProviderSettings.this.lambda$updateWifiEntryPreferences$10(wifiEntry, wifiEntryPreference);
                    }
                });
            }
            this.mWifiEntryPreferenceCategory.addPreference(createLongPressWifiEntryPreference);
            z = true;
            i = i2;
        }
        removeCachedPrefs(this.mWifiEntryPreferenceCategory);
        if (z) {
            getView().postDelayed(this.mHideProgressBarRunnable, 1700L);
        } else {
            setProgressBarVisible(true);
            Preference preference = new Preference(getPrefContext());
            preference.setSelectable(false);
            preference.setSummary(R.string.wifi_empty_list_wifi_on);
            preference.setOrder(i);
            preference.setKey("wifi_empty_list");
            this.mWifiEntryPreferenceCategory.addPreference(preference);
            i++;
        }
        this.mAddWifiNetworkPreference.setOrder(i);
        this.mWifiEntryPreferenceCategory.addPreference(this.mAddWifiNetworkPreference);
        setAdditionalSettingsSummaries();
    }
}
