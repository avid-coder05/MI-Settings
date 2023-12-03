package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
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
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.MiuiSearchDrawable;
import com.android.settings.MiuiSettings;
import com.android.settings.MiuiUtils;
import com.android.settings.OnBackPressedListener;
import com.android.settings.R;
import com.android.settings.core.OnActivityResultListener;
import com.android.settings.network.NetworkProviderSettings;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.wifi.MiuiWifiEntryPreference;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import com.android.settingslib.wifi.SlaveWifiUtils;
import com.android.wifitrackerlib.PasspointR1WifiEntry;
import com.android.wifitrackerlib.PasspointWifiEntry;
import com.android.wifitrackerlib.StandardWifiEntry;
import com.android.wifitrackerlib.Utils;
import com.android.wifitrackerlib.WifiEntry;
import com.android.wifitrackerlib.WifiPickerTracker;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class MiuiSlaveWifiSettings extends NetworkProviderSettings implements Preference.OnPreferenceChangeListener, OnBackPressedListener, OnActivityResultListener, WifiEntry.WifiEntryCallback {
    private static final String TAG = MiuiSlaveWifiSettings.class.getSimpleName();
    private static volatile Method methodGetWifiEntries;
    private static volatile Method methodIsSupportMiWill;
    private Context mApplicationContext;
    private ConnectivityManager mConnManager;
    private IntentFilter mFilter;
    private boolean mIsRestricted;
    private MainThreadHandler mMainHandler;
    private MiuiSlaveWifiEnabler mMiuiSlaveWifiEnabler;
    private IntentFilter mOpenWifiFilter;
    private Intent mOpenWifiIntent;
    protected PreferenceCategory mPrimaryConnectedAccessPointPreferenceCategory;
    private BroadcastReceiver mReceiver;
    private MiuiSearchDrawable mSearchIcon;
    private boolean mUserSelect;
    private CheckBoxPreference mWifiAutoDisablePreference;
    private WifiConfiguration mWifiConfig;
    private CheckBoxPreference mWifiEnablePreference;
    private WifiManager mWifiManager;
    private Handler mWorkHandler;
    private HandlerThread mWorkThread;
    private RecyclerView recyclerview;
    private NetworkInfo.State mNetworkState = NetworkInfo.State.DISCONNECTED;
    private final Runnable mUpdateWifiEntryPreferencesRunnable = new Runnable() { // from class: com.android.settings.wifi.MiuiSlaveWifiSettings$$ExternalSyntheticLambda1
        @Override // java.lang.Runnable
        public final void run() {
            MiuiSlaveWifiSettings.this.lambda$new$0();
        }
    };
    private final Runnable mUpdateRefreshRunnable = new Runnable() { // from class: com.android.settings.wifi.MiuiSlaveWifiSettings$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            MiuiSlaveWifiSettings.this.lambda$new$1();
        }
    };
    private final View.OnClickListener mRefreshListener = new View.OnClickListener() { // from class: com.android.settings.wifi.MiuiSlaveWifiSettings.2
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            MiuiSlaveWifiSettings.this.updateScanState(true);
        }
    };
    private boolean mIsShown = false;

    /* loaded from: classes2.dex */
    private final class MainThreadHandler extends Handler {
        public MainThreadHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            View findViewById;
            int i = message.what;
            if (i == 1) {
                MiuiSlaveWifiSettings.this.internalSmoothScrollToPosition();
            } else if (i != 2) {
                if (i != 5) {
                    return;
                }
                MiuiSlaveWifiSettings.this.updateSlaveWifiEnabler();
            } else if (MiuiSlaveWifiSettings.this.getActivity() == null || (findViewById = MiuiSlaveWifiSettings.this.getActivity().findViewById(R.id.action_bar)) == null) {
            } else {
                findViewById.sendAccessibilityEvent(8);
            }
        }
    }

    static {
        methodIsSupportMiWill = null;
        methodGetWifiEntries = null;
        try {
            methodIsSupportMiWill = Class.forName("com.android.wifitrackerlib.StandardWifiEntry").getMethod("isSupportMiWill", null);
            methodGetWifiEntries = WifiPickerTracker.class.getMethod("getWifiEntries", Boolean.TYPE);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
        }
    }

    private void addConnectedWifiEntryPreferenceCategory() {
        this.mConnectedWifiEntryPreferenceCategory.setKey("connected_access_point");
        this.mConnectedWifiEntryPreferenceCategory.setTitle(R.string.dual_wifi_slave_wifi_connected);
        getPreferenceScreen().addPreference(this.mConnectedWifiEntryPreferenceCategory);
        this.mConnectedWifiEntryPreferenceCategory.setVisible(true);
        this.mConnectedWifiEntryPreferenceCategory.removeAll();
    }

    private MiuiWifiEntryPreference createEntryPreference(WifiEntry wifiEntry) {
        LongPressWifiEntryPreference longPressWifiEntryPreference = new LongPressWifiEntryPreference(getPrefContext(), wifiEntry, this, true);
        longPressWifiEntryPreference.setArrowClickListener(new MiuiWifiEntryPreference.ArrowClickListener(wifiEntry, this));
        longPressWifiEntryPreference.setOnPreferenceChangeListener(this);
        return longPressWifiEntryPreference;
    }

    private void disableAutoDisablePreference() {
        this.mWifiAutoDisablePreference.setEnabled(false);
    }

    private void enableAutoDisablePreference() {
        this.mWifiAutoDisablePreference.setEnabled(true);
    }

    private void focusOnBackIcon() {
        View findViewById;
        if (getActivity() == null || (findViewById = getActivity().findViewById(R.id.action_bar)) == null) {
            return;
        }
        findViewById.sendAccessibilityEvent(8);
    }

    private void initBroadcastReceiver(Handler handler) {
        IntentFilter intentFilter = new IntentFilter();
        this.mFilter = intentFilter;
        intentFilter.addAction("android.net.wifi.SCAN_RESULTS");
        this.mFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        this.mFilter.addAction("android.net.wifi.SLAVE_STATE_CHANGE");
        IntentFilter intentFilter2 = new IntentFilter();
        this.mOpenWifiFilter = intentFilter2;
        intentFilter2.addAction("miui.intent.DUAL_WIFI.CACHE_OPENWIFI");
        this.mOpenWifiFilter.addDataScheme("http");
        this.mOpenWifiFilter.addDataScheme("https");
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.MiuiSlaveWifiSettings.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("android.net.wifi.SCAN_RESULTS".equals(action)) {
                    MiuiSlaveWifiSettings.this.updateScanState(false);
                } else if (!"android.net.wifi.SLAVE_STATE_CHANGE".equals(action)) {
                    if ("miui.intent.DUAL_WIFI.CACHE_OPENWIFI".equals(action)) {
                        MiuiSlaveWifiSettings.this.mOpenWifiIntent = intent;
                    } else if ("android.net.wifi.WIFI_STATE_CHANGED".equals(action)) {
                        MiuiSlaveWifiSettings.this.mMainHandler.sendEmptyMessage(5);
                    }
                } else {
                    NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                    if (networkInfo != null) {
                        if (MiuiSlaveWifiSettings.this.mUserSelect && MiuiSlaveWifiSettings.this.mNetworkState == NetworkInfo.State.DISCONNECTED && networkInfo.getState() == NetworkInfo.State.CONNECTING) {
                            MiuiSlaveWifiSettings.this.mMainHandler.sendEmptyMessage(1);
                        } else if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            MiuiSlaveWifiSettings.this.mMainHandler.sendEmptyMessage(2);
                        }
                        MiuiSlaveWifiSettings.this.mNetworkState = networkInfo.getState();
                    }
                }
            }
        };
        getActivity().registerReceiver(this.mReceiver, this.mFilter, null, handler);
        getActivity().registerReceiver(this.mReceiver, this.mOpenWifiFilter, null, handler);
    }

    private void initUI() {
        this.mPrimaryConnectedAccessPointPreferenceCategory = (PreferenceCategory) findPreference("connected_primary_wifi");
        getPreferenceScreen().removePreference(this.mPrimaryConnectedAccessPointPreferenceCategory);
        getPreferenceScreen().removePreference(this.mConnectedWifiEntryPreferenceCategory);
        getPreferenceScreen().removePreference(this.mWifiEntryPreferenceCategory);
        this.mWifiManager = (WifiManager) getSystemService("wifi");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void internalSmoothScrollToPosition() {
        RecyclerView listView = getListView();
        if (listView == null || listView.getChildCount() <= 0) {
            return;
        }
        listView.smoothScrollToPosition(0);
        this.mUserSelect = false;
    }

    private boolean isMiWillWifiEntry(WifiEntry wifiEntry) {
        if (methodIsSupportMiWill != null && (wifiEntry instanceof StandardWifiEntry)) {
            try {
                if (((Boolean) methodIsSupportMiWill.invoke((StandardWifiEntry) wifiEntry, null)).booleanValue()) {
                    return true;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e2) {
                e2.printStackTrace();
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        updateScanState(true);
    }

    private void manuallyAddNetwork() {
        SlaveWifiUtils slaveWifiUtils;
        WifiManager wifiManager = this.mWifiManager;
        if (wifiManager == null || !wifiManager.isWifiEnabled() || (slaveWifiUtils = this.mSlaveWifiUtils) == null || !slaveWifiUtils.isSlaveWifiEnabled()) {
            return;
        }
        startFragment(this, MiuiAddNetworkFragment.class.getName(), 100, (Bundle) null, 0);
    }

    private void removeAccessPointsPreference() {
        this.mWifiEntryPreferenceCategory.removeAll();
        getPreferenceScreen().removePreference(this.mWifiEntryPreferenceCategory);
    }

    private void removeConnectedAccessPointPreferenceCategory() {
        this.mConnectedWifiEntryPreferenceCategory.removeAll();
        getPreferenceScreen().removePreference(this.mConnectedWifiEntryPreferenceCategory);
    }

    private void removePrimaryConnectedAccessPointPreferenceCategory() {
        this.mPrimaryConnectedAccessPointPreferenceCategory.removeAll();
        getPreferenceScreen().removePreference(this.mPrimaryConnectedAccessPointPreferenceCategory);
    }

    private ArrayList<MiuiWifiEntryPreference> resortAccessPoint(List<WifiEntry> list) {
        ArrayList<MiuiWifiEntryPreference> arrayList = new ArrayList<>();
        HashSet hashSet = new HashSet();
        for (WifiEntry wifiEntry : list) {
            if (!hashSet.contains(wifiEntry.getTitle() + "-" + wifiEntry.getSecurity())) {
                wifiEntry.setListener(this);
                LongPressWifiEntryPreference longPressWifiEntryPreference = new LongPressWifiEntryPreference(getPrefContext(), wifiEntry, this, true);
                longPressWifiEntryPreference.update(wifiEntry);
                longPressWifiEntryPreference.setTitle(wifiEntry.getTitle());
                longPressWifiEntryPreference.setArrowClickListener(new MiuiWifiEntryPreference.ArrowClickListener(wifiEntry, this));
                longPressWifiEntryPreference.setOnPreferenceChangeListener(this);
                arrayList.add(longPressWifiEntryPreference);
                hashSet.add(wifiEntry.getTitle() + "-" + wifiEntry.getSecurity());
            }
        }
        Collections.sort(arrayList, MiuiWifiEntryPreference.getSuperComparator());
        return arrayList;
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
        AlertDialog create = new AlertDialog.Builder(getActivity(), R.style.AlertDialog_Theme_DayNight).setTitle(R.string.wifi_share_qrcode_title).setView(inflate).setPositiveButton(R.string.wifi_share_qrcode_finish, (DialogInterface.OnClickListener) null).create();
        create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.wifi.MiuiSlaveWifiSettings.4
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                MiuiSlaveWifiSettings.this.mIsShown = false;
                MiuiSlaveWifiSettings.this.mWifiConfig = null;
                WindowManager.LayoutParams layoutParams = attributes;
                layoutParams.screenBrightness = f;
                window.setAttributes(layoutParams);
                window.clearFlags(128);
            }
        });
        create.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSlaveWifiEnabler() {
        CheckBoxPreference checkBoxPreference;
        SlaveWifiUtils slaveWifiUtils;
        if (getActivity() == null || (checkBoxPreference = this.mWifiEnablePreference) == null || (slaveWifiUtils = this.mSlaveWifiUtils) == null || this.mWifiManager == null) {
            return;
        }
        checkBoxPreference.setEnabled(slaveWifiUtils.supportDualWifi() && this.mWifiManager.isWifiEnabled());
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiSlaveWifiSettings.class.getName();
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mWifiPickerTracker.setIsSlave(true);
        this.mApplicationContext = getActivity();
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("wifi_enable");
        this.mWifiEnablePreference = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this);
        this.mMiuiSlaveWifiEnabler = new MiuiSlaveWifiEnabler(this, this.mWifiEnablePreference, this.mSlaveWifiUtils);
        updateSlaveWifiEnabler();
        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) getPreferenceScreen().findPreference("wifi_slave_auto_disable");
        this.mWifiAutoDisablePreference = checkBoxPreference2;
        checkBoxPreference2.setOnPreferenceChangeListener(this);
        this.mWifiAutoDisablePreference.setChecked(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(), "wifi_slave_auto_disable", this.mSlaveWifiUtils.getAutoDisableDefault(this.mApplicationContext)) == 1);
        this.mWifiAutoDisablePreference.setEnabled(this.mWifiEnablePreference.isChecked());
        getListView().setPadding(getListView().getPaddingLeft(), getListView().getPaddingTop(), getListView().getPaddingRight(), 0);
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.RestrictedSettingsFragment, androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (intent != null) {
            onFragmentResult(i, intent.getExtras());
        }
    }

    @Override // com.android.settings.OnBackPressedListener
    public boolean onBackPressed() {
        return false;
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        NetworkInfo networkInfo;
        super.onCreate(bundle);
        if (bundle != null) {
            boolean z = bundle.getBoolean("is_dialog_shown");
            this.mIsShown = z;
            if (z) {
                showSharePasswordDialog((WifiConfiguration) bundle.getParcelable("wifi_configuration_info"));
            }
        }
        this.mWifiManager = (WifiManager) getSystemService("wifi");
        if (!SettingsFeatures.isSplitTablet(getContext())) {
            getActivity().setRequestedOrientation(1);
        }
        if (getActivity() instanceof MiuiSettings) {
            setThemeRes(R.style.Theme_WifiSettings_showTitle);
        } else {
            setThemeRes(R.style.Theme_WifiSettings);
        }
        this.mSearchIcon = new MiuiSearchDrawable(getActivity());
        initUI();
        this.mMainHandler = new MainThreadHandler(Looper.getMainLooper());
        HandlerThread handlerThread = new HandlerThread(TAG + "{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
        this.mWorkThread = handlerThread;
        handlerThread.start();
        this.mWorkHandler = new Handler(this.mWorkThread.getLooper());
        Network slaveWifiCurrentNetwork = this.mSlaveWifiUtils.getSlaveWifiCurrentNetwork();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService("connectivity");
        this.mConnManager = connectivityManager;
        if (slaveWifiCurrentNetwork != null && (networkInfo = connectivityManager.getNetworkInfo(slaveWifiCurrentNetwork)) != null) {
            this.mNetworkState = networkInfo.getState();
        }
        initBroadcastReceiver(this.mWorkHandler);
        this.mIsRestricted = isUiRestricted();
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (isUiRestricted()) {
            return;
        }
        menu.add(0, 11, 0, R.string.menu_stats_refresh).setEnabled(this.mSlaveWifiUtils.isSlaveWifiEnabled()).setShowAsAction(1);
        MenuItem findItem = menu.findItem(11);
        if (findItem != null) {
            findItem.setIcon(this.mSearchIcon.getSearchIcon());
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (viewGroup != null) {
            MiuiUtils.updateFragmentView(getActivity(), viewGroup);
        }
        View inflate = layoutInflater.inflate(R.layout.wifi_settings, viewGroup, false);
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(R.id.prefs_container);
        View onCreateView = super.onCreateView(layoutInflater, viewGroup2, bundle);
        RecyclerView recyclerView = (RecyclerView) onCreateView.findViewById(R.id.recycler_view);
        this.recyclerview = recyclerView;
        recyclerView.setItemAnimator(null);
        viewGroup2.addView(onCreateView);
        return inflate;
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.RestrictedSettingsFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        if (Build.IS_TABLET) {
            getActivity().setRequestedOrientation(2);
        }
        super.onDestroy();
        getActivity().unregisterReceiver(this.mReceiver);
        this.mWorkThread.quit();
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public void onFragmentResult(int i, Bundle bundle) {
        WifiConfiguration wifiConfiguration;
        if (i == 100) {
            WifiConfiguration wifiConfiguration2 = (WifiConfiguration) bundle.getParcelable("config");
            if (wifiConfiguration2 != null) {
                this.mUserSelect = true;
                this.mWifiManager.save(wifiConfiguration2, this.mSaveListener);
            }
        } else if (i == 200 && (wifiConfiguration = (WifiConfiguration) bundle.getParcelable("config")) != null) {
            this.mWifiManager.save(wifiConfiguration, this.mSaveListener);
            int networkId = this.mSlaveWifiUtils.getWifiSlaveConnectionInfo().getNetworkId();
            int i2 = wifiConfiguration.networkId;
            if (networkId != i2 || i2 == -1) {
                return;
            }
            if (bundle.getShort("mac_random_changed") == 1) {
                this.mSlaveWifiUtils.disconnectSlaveWifi();
            }
            this.mSlaveWifiUtils.connectToSlaveAp(wifiConfiguration);
        }
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 11) {
            updateScanState(true);
        } else if (itemId == 16908332) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        if ("wifi_enable".equals(key)) {
            if (this.recyclerview.isComputingLayout()) {
                return false;
            }
            this.mMiuiSlaveWifiEnabler.checkedChanged(((Boolean) obj).booleanValue());
            return true;
        } else if ("wifi_slave_auto_disable".equals(key)) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), "wifi_slave_auto_disable", ((Boolean) obj).booleanValue() ? 1 : 0);
            return true;
        } else {
            if (preference instanceof LongPressWifiEntryPreference) {
                this.mUserSelect = true;
                LongPressWifiEntryPreference longPressWifiEntryPreference = (LongPressWifiEntryPreference) preference;
                WifiEntry wifiEntry = longPressWifiEntryPreference.getWifiEntry();
                if (key != null && key.startsWith("master-") && wifiEntry.getConnectedState() == 2) {
                    return true;
                }
                if (wifiEntry.getSlaveConnectedState() == 2) {
                    NetworkCapabilities networkCapabilities = ((ConnectivityManager) getSystemService("connectivity")).getNetworkCapabilities(this.mSlaveWifiUtils.getSlaveWifiCurrentNetwork());
                    if ((wifiEntry.getSecurity() == 2 || wifiEntry.getSecurity() == 5) && ((networkCapabilities == null || !networkCapabilities.hasCapability(17)) && !com.android.settingslib.wifi.WifiUtils.isInMishow(getActivity()))) {
                        showSharePasswordDialog(wifiEntry.getWifiConfiguration());
                        return true;
                    } else if (wifiEntry.canSlaveSignIn()) {
                        wifiEntry.slaveSignIn(null);
                    }
                } else if (wifiEntry.isSaved() && WifiUtils.getConnectingType(wifiEntry) != 1) {
                    longPressWifiEntryPreference.setConnected(false);
                    android.net.wifi.WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
                    WifiConfiguration wifiConfiguration = wifiEntry.getWifiConfiguration();
                    boolean isMiWillWifiEntry = isMiWillWifiEntry(wifiEntry);
                    if ((connectionInfo != null && wifiConfiguration != null && connectionInfo.getNetworkId() != wifiConfiguration.networkId) || (wifiConfiguration != null && wifiConfiguration.status == 1)) {
                        this.mSlaveWifiUtils.connectToSlaveAp(wifiConfiguration);
                    } else if (connectionInfo == null || wifiConfiguration == null || connectionInfo.getNetworkId() != wifiConfiguration.networkId || !isMiWillWifiEntry) {
                        longPressWifiEntryPreference.setConnected(true);
                    } else {
                        WifiEntry connectedWifiEntry = this.mWifiPickerTracker.getConnectedWifiEntry();
                        WifiEntry slaveConnectedWifiEntry = this.mWifiPickerTracker.getSlaveConnectedWifiEntry();
                        if (slaveConnectedWifiEntry == null || connectedWifiEntry != slaveConnectedWifiEntry) {
                            this.mSlaveWifiUtils.connectToSlaveAp(wifiConfiguration);
                        } else {
                            longPressWifiEntryPreference.setConnected(true);
                        }
                    }
                }
            }
            return true;
        }
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if ("manually_add_network".equals(key)) {
            manuallyAddNetwork();
            return true;
        }
        if (preference instanceof LongPressWifiEntryPreference) {
            WifiEntry wifiEntry = ((LongPressWifiEntryPreference) preference).getWifiEntry();
            boolean isMiWillWifiEntry = isMiWillWifiEntry(wifiEntry);
            if (WifiUtils.getConnectingType(wifiEntry) == 1 && (!wifiEntry.equals(this.mWifiPickerTracker.getConnectedWifiEntry()) || (isMiWillWifiEntry && key != null && key.startsWith("slave-")))) {
                wifiEntry.connect(null, true);
                return true;
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        if (isUiRestricted()) {
            return;
        }
        MenuItem findItem = menu.findItem(11);
        if (findItem != null) {
            findItem.setVisible(this.mSlaveWifiUtils.isSlaveWifiEnabled());
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        getView().postDelayed(this.mUpdateRefreshRunnable, 1000L);
        this.mIsRestricted = isUiRestricted();
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

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mMiuiSlaveWifiEnabler.start();
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mMiuiSlaveWifiEnabler.stop();
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
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
            if (config == null) {
                wifiEntry.connect(null, true);
            } else {
                this.mSlaveWifiUtils.connectToSlaveAp(config);
            }
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry.WifiEntryCallback
    public void onUpdated() {
    }

    @Override // com.android.settings.network.NetworkProviderSettings, com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onWifiEntriesChanged() {
        if (getActivity() == null || this.mIsRestricted || this.mWifiPickerTracker.getWifiState() != 3 || !this.mWifiPickerTracker.isSlaveWifiEnabled()) {
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
        int slaveWifiState = this.mSlaveWifiUtils.getSlaveWifiState();
        if (NetworkProviderSettings.isVerboseLoggingEnabled()) {
            String str = TAG;
            Log.i(str, "onWifiStateChanged called with wifi state: " + wifiState);
            Log.i(str, "onWifiStateChanged called with slave wifi state: " + slaveWifiState);
        }
        switch (slaveWifiState) {
            case 14:
            case 16:
                removeAccessPointsPreference();
                removePrimaryConnectedAccessPointPreferenceCategory();
                removeConnectedAccessPointPreferenceCategory();
                if (getActivity() != null) {
                    getActivity().invalidateOptionsMenu();
                    break;
                }
                break;
            case 15:
                removeAccessPointsPreference();
                removePrimaryConnectedAccessPointPreferenceCategory();
                removeConnectedAccessPointPreferenceCategory();
                disableAutoDisablePreference();
                focusOnBackIcon();
                if (getActivity() != null) {
                    getActivity().invalidateOptionsMenu();
                    break;
                }
                break;
            case 17:
                lambda$new$0();
                focusOnBackIcon();
                enableAutoDisablePreference();
                if (getActivity() != null) {
                    getActivity().invalidateOptionsMenu();
                    break;
                }
                break;
        }
        super.lambda$onInternetTypeChanged$4();
    }

    @Override // com.android.settings.network.NetworkProviderSettings
    protected void reloadDialog(int i, String str) {
        WifiManager wifiManager;
        if (getActivity() == null || (wifiManager = this.mWifiManager) == null || this.mWorkHandler == null) {
            return;
        }
        List<ScanResult> scanResults = wifiManager.getScanResults();
        WifiNetworkScoreCache wifiNetworkScoreCache = new WifiNetworkScoreCache(getActivity().getApplicationContext(), new WifiNetworkScoreCache.CacheListener(this.mWorkHandler) { // from class: com.android.settings.wifi.MiuiSlaveWifiSettings.3
            public void networkCacheUpdated(List<ScoredNetwork> list) {
            }
        });
        for (ScanResult scanResult : scanResults) {
            if (str != null && str.equals(scanResult.SSID)) {
                StandardWifiEntry standardWifiEntry = new StandardWifiEntry(getActivity().getApplicationContext(), this.mMainHandler, wifiNetworkScoreCache, this.mWifiManager, false, scanResult.SSID, Utils.getSecurityTypesFromScanResult(scanResult));
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
        if (z) {
            this.mSearchIcon.playAnimation();
        } else {
            this.mSearchIcon.stopAnimation();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.network.NetworkProviderSettings
    /* renamed from: updateWifiEntryPreferences  reason: merged with bridge method [inline-methods] */
    public void lambda$new$0() {
        List<WifiEntry> list;
        Iterator<MiuiWifiEntryPreference> it;
        if (isUiRestricted()) {
            getPreferenceScreen().removeAll();
        } else if (getActivity() != null && this.mWifiManager.isWifiEnabled() && this.mSlaveWifiUtils.isSlaveWifiEnabled()) {
            android.net.wifi.WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
            boolean z = false;
            boolean z2 = connectionInfo != null && com.android.settingslib.wifi.WifiUtils.is24GHz(connectionInfo.getFrequency());
            boolean z3 = connectionInfo != null && com.android.settingslib.wifi.WifiUtils.is5GHz(connectionInfo.getFrequency());
            this.mWifiEntryPreferenceCategory.setKey("access_points");
            this.mWifiEntryPreferenceCategory.setVisible(true);
            getPreferenceScreen().addPreference(this.mWifiEntryPreferenceCategory);
            this.mWifiEntryPreferenceCategory.removeAll();
            getPreferenceScreen().removePreference(this.mPrimaryConnectedAccessPointPreferenceCategory);
            getPreferenceScreen().removePreference(this.mConnectedWifiEntryPreferenceCategory);
            if (methodGetWifiEntries != null) {
                try {
                    list = (List) methodGetWifiEntries.invoke(this.mWifiPickerTracker, Boolean.TRUE);
                } catch (Exception e) {
                    List<WifiEntry> wifiEntries = this.mWifiPickerTracker.getWifiEntries();
                    Log.e(TAG, "methodGetWifiEntries catch:" + e);
                    list = wifiEntries;
                }
            } else {
                list = this.mWifiPickerTracker.getWifiEntries();
            }
            ArrayList<MiuiWifiEntryPreference> resortAccessPoint = resortAccessPoint(list);
            WifiEntry connectedWifiEntry = this.mWifiPickerTracker.getConnectedWifiEntry();
            WifiEntry slaveConnectedWifiEntry = this.mWifiPickerTracker.getSlaveConnectedWifiEntry();
            if (connectedWifiEntry != null) {
                MiuiWifiEntryPreference miuiWifiEntryPreference = (MiuiWifiEntryPreference) this.mPrimaryConnectedAccessPointPreferenceCategory.findPreference("master-" + connectedWifiEntry.getKey());
                this.mPrimaryConnectedAccessPointPreferenceCategory.setTitle(R.string.dual_wifi_primary_wifi_connected);
                getPreferenceScreen().addPreference(this.mPrimaryConnectedAccessPointPreferenceCategory);
                this.mPrimaryConnectedAccessPointPreferenceCategory.removeAll();
                if (miuiWifiEntryPreference == null || miuiWifiEntryPreference.getWifiEntry() != connectedWifiEntry) {
                    miuiWifiEntryPreference = createEntryPreference(connectedWifiEntry);
                }
                miuiWifiEntryPreference.setOrder(0);
                miuiWifiEntryPreference.setKey("master-" + connectedWifiEntry.getKey());
                this.mPrimaryConnectedAccessPointPreferenceCategory.addPreference(miuiWifiEntryPreference);
            }
            if (slaveConnectedWifiEntry != null) {
                MiuiWifiEntryPreference miuiWifiEntryPreference2 = (MiuiWifiEntryPreference) this.mConnectedWifiEntryPreferenceCategory.findPreference("slave-" + slaveConnectedWifiEntry.getKey());
                addConnectedWifiEntryPreferenceCategory();
                if (miuiWifiEntryPreference2 == null || miuiWifiEntryPreference2.getWifiEntry() != slaveConnectedWifiEntry) {
                    miuiWifiEntryPreference2 = createEntryPreference(slaveConnectedWifiEntry);
                }
                miuiWifiEntryPreference2.setOrder(0);
                miuiWifiEntryPreference2.setKey("slave-" + slaveConnectedWifiEntry.getKey());
                this.mConnectedWifiEntryPreferenceCategory.addPreference(miuiWifiEntryPreference2);
                z = true;
            }
            if (NetworkProviderSettings.isVerboseLoggingEnabled()) {
                String str = TAG;
                Log.d(str, "masterConnectedEntry is " + connectedWifiEntry);
                Log.d(str, "slaveConnectedEntry is " + slaveConnectedWifiEntry);
            }
            this.mWifiEntryPreferenceCategory.setTitle((z2 || z3) ? z2 ? R.string.dual_wifi_avaliable_slave_wifi_5G : R.string.dual_wifi_avaliable_slave_wifi_24G : R.string.dual_wifi_avaliable_slave_wifi);
            boolean isMiWillWifiEntry = isMiWillWifiEntry(connectedWifiEntry);
            boolean z4 = SystemProperties.getBoolean("persist.log.tag.miwill_dual_wifi_enable", true);
            Iterator<MiuiWifiEntryPreference> it2 = resortAccessPoint.iterator();
            int i = 1;
            int i2 = 1;
            while (it2.hasNext()) {
                MiuiWifiEntryPreference next = it2.next();
                WifiEntry wifiEntry = next.getWifiEntry();
                if ((wifiEntry instanceof PasspointWifiEntry) || (wifiEntry instanceof PasspointR1WifiEntry) || (!(!z2 || z3 || wifiEntry.isOnly5Ghz() || (z4 && isMiWillWifiEntry && connectedWifiEntry == wifiEntry)) || (!(z2 || !z3 || wifiEntry.isOnly24Ghz() || (z4 && isMiWillWifiEntry && connectedWifiEntry == wifiEntry)) || (slaveConnectedWifiEntry != null && slaveConnectedWifiEntry == wifiEntry)))) {
                    it = it2;
                } else if (wifiEntry.isSaved()) {
                    if (!z) {
                        addConnectedWifiEntryPreferenceCategory();
                        z = true;
                    }
                    next.setOrder(i2);
                    next.setKey("slave-" + wifiEntry.getKey());
                    this.mConnectedWifiEntryPreferenceCategory.addPreference(next);
                    it = it2;
                    i2++;
                } else {
                    int i3 = i + 1;
                    next.setOrder(i);
                    this.mConnectedWifiEntryPreferenceCategory.removePreference(next);
                    StringBuilder sb = new StringBuilder();
                    sb.append("master-");
                    it = it2;
                    sb.append(wifiEntry.getKey());
                    next.setKey(sb.toString());
                    this.mPrimaryConnectedAccessPointPreferenceCategory.removePreference(next);
                    next.setKey("slave-" + wifiEntry.getKey());
                    this.mConnectedWifiEntryPreferenceCategory.removePreference(next);
                    this.mWifiEntryPreferenceCategory.addPreference(next);
                    i = i3;
                }
                it2 = it;
            }
            OtherAccessPoint otherAccessPoint = new OtherAccessPoint(getThemedContext());
            otherAccessPoint.setKey("manually_add_network");
            otherAccessPoint.setTitle(R.string.wifi_add_network);
            otherAccessPoint.setOrder(i);
            this.mWifiEntryPreferenceCategory.addPreference(otherAccessPoint);
            this.mWifiEnablePreference.setSummary((CharSequence) null);
        }
    }
}
