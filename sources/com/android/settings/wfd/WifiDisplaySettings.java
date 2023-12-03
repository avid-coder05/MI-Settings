package com.android.settings.wfd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.hardware.display.WifiDisplay;
import android.hardware.display.WifiDisplayStatus;
import android.media.MediaRouter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Slog;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import com.android.internal.app.MediaRouteDialogPresenter;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import miui.provider.ExtraTelephony;

/* loaded from: classes2.dex */
public class WifiDisplaySettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.wifi_display_settings);
    private boolean mAutoGO;
    private PreferenceGroup mCertCategory;
    private DisplayManager mDisplayManager;
    private TextView mEmptyView;
    private boolean mListen;
    private int mListenChannel;
    private int mOperatingChannel;
    private int mPendingChanges;
    private MediaRouter mRouter;
    private boolean mStarted;
    private boolean mWifiDisplayCertificationOn;
    protected boolean mWifiDisplayOnSetting;
    private WifiDisplayStatus mWifiDisplayStatus;
    protected WifiManager mWifiManager;
    private WifiP2pManager.Channel mWifiP2pChannel;
    private WifiP2pManager mWifiP2pManager;
    private int mWpsConfig = 4;
    private final Runnable mUpdateRunnable = new Runnable() { // from class: com.android.settings.wfd.WifiDisplaySettings.13
        @Override // java.lang.Runnable
        public void run() {
            int i = WifiDisplaySettings.this.mPendingChanges;
            WifiDisplaySettings.this.mPendingChanges = 0;
            WifiDisplaySettings.this.update(i);
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wfd.WifiDisplaySettings.14
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED")) {
                WifiDisplaySettings.this.scheduleUpdate(4);
            }
        }
    };
    private final ContentObserver mSettingsObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.wfd.WifiDisplaySettings.15
        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            WifiDisplaySettings.this.scheduleUpdate(1);
        }
    };
    private final MediaRouter.Callback mRouterCallback = new MediaRouter.SimpleCallback() { // from class: com.android.settings.wfd.WifiDisplaySettings.16
        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteAdded(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
            WifiDisplaySettings.this.scheduleUpdate(2);
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteChanged(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
            WifiDisplaySettings.this.scheduleUpdate(2);
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteRemoved(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
            WifiDisplaySettings.this.scheduleUpdate(2);
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteSelected(MediaRouter mediaRouter, int i, MediaRouter.RouteInfo routeInfo) {
            WifiDisplaySettings.this.scheduleUpdate(2);
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteUnselected(MediaRouter mediaRouter, int i, MediaRouter.RouteInfo routeInfo) {
            WifiDisplaySettings.this.scheduleUpdate(2);
        }
    };
    private final Handler mHandler = new Handler();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class RoutePreference extends com.android.settingslib.miuisettings.preference.Preference implements Preference.OnPreferenceClickListener {
        private final MediaRouter.RouteInfo mRoute;

        public RoutePreference(Context context, MediaRouter.RouteInfo routeInfo) {
            super(context);
            this.mRoute = routeInfo;
            setTitle(routeInfo.getName());
            setSummary(routeInfo.getDescription());
            setEnabled(routeInfo.isEnabled());
            if (routeInfo.isSelected()) {
                setOrder(2);
                if (routeInfo.isConnecting()) {
                    setSummary(R.string.wifi_display_status_connecting);
                } else {
                    setSummary(R.string.wifi_display_status_connected);
                }
            } else if (isEnabled()) {
                setOrder(3);
            } else {
                setOrder(4);
                if (routeInfo.getStatusCode() == 5) {
                    setSummary(R.string.wifi_display_status_in_use);
                } else {
                    setSummary(R.string.wifi_display_status_not_available);
                }
            }
            setOnPreferenceClickListener(this);
        }

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            WifiDisplaySettings.this.toggleRoute(this.mRoute);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class UnpairedWifiDisplayPreference extends Preference implements Preference.OnPreferenceClickListener {
        private final WifiDisplay mDisplay;

        public UnpairedWifiDisplayPreference(Context context, WifiDisplay wifiDisplay) {
            super(context);
            this.mDisplay = wifiDisplay;
            setTitle(wifiDisplay.getFriendlyDisplayName());
            setSummary(286195783);
            setEnabled(wifiDisplay.canConnect());
            if (isEnabled()) {
                setOrder(3);
            } else {
                setOrder(4);
                setSummary(R.string.wifi_display_status_in_use);
            }
            setOnPreferenceClickListener(this);
        }

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            WifiDisplaySettings.this.pairWifiDisplay(this.mDisplay);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class WifiDisplayRoutePreference extends RoutePreference implements View.OnClickListener {
        private final WifiDisplay mDisplay;

        public WifiDisplayRoutePreference(Context context, MediaRouter.RouteInfo routeInfo, WifiDisplay wifiDisplay) {
            super(context, routeInfo);
            this.mDisplay = wifiDisplay;
        }

        @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            super.onBindViewHolder(preferenceViewHolder);
            ImageView imageView = (ImageView) preferenceViewHolder.findViewById(R.id.settings_button);
            if (imageView != null) {
                imageView.setOnClickListener(this);
                if (isEnabled()) {
                    return;
                }
                TypedValue typedValue = new TypedValue();
                getContext().getTheme().resolveAttribute(16842803, typedValue, true);
                imageView.setImageAlpha((int) (typedValue.getFloat() * 255.0f));
                imageView.setEnabled(true);
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            WifiDisplaySettings.this.showWifiDisplayOptionsDialog(this.mDisplay);
        }
    }

    private void buildCertificationMenu(PreferenceScreen preferenceScreen) {
        PreferenceGroup preferenceGroup = this.mCertCategory;
        if (preferenceGroup == null) {
            PreferenceCategory preferenceCategory = new PreferenceCategory(getPrefContext());
            this.mCertCategory = preferenceCategory;
            preferenceCategory.setTitle(R.string.wifi_display_certification_heading);
            this.mCertCategory.setOrder(1);
        } else {
            preferenceGroup.removeAll();
        }
        preferenceScreen.addPreference(this.mCertCategory);
        if (!this.mWifiDisplayStatus.getSessionInfo().getGroupId().isEmpty()) {
            Preference preference = new Preference(getPrefContext());
            preference.setTitle(R.string.wifi_display_session_info);
            preference.setSummary(this.mWifiDisplayStatus.getSessionInfo().toString());
            this.mCertCategory.addPreference(preference);
            if (this.mWifiDisplayStatus.getSessionInfo().getSessionId() != 0) {
                this.mCertCategory.addPreference(new com.android.settingslib.miuisettings.preference.Preference(getPrefContext()) { // from class: com.android.settings.wfd.WifiDisplaySettings.1
                    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
                    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
                        super.onBindViewHolder(preferenceViewHolder);
                        Button button = (Button) preferenceViewHolder.findViewById(R.id.left_button);
                        button.setText(R.string.wifi_display_pause);
                        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wfd.WifiDisplaySettings.1.1
                            @Override // android.view.View.OnClickListener
                            public void onClick(View view) {
                                WifiDisplaySettings.this.mDisplayManager.pauseWifiDisplay();
                            }
                        });
                        Button button2 = (Button) preferenceViewHolder.findViewById(R.id.right_button);
                        button2.setText(R.string.wifi_display_resume);
                        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wfd.WifiDisplaySettings.1.2
                            @Override // android.view.View.OnClickListener
                            public void onClick(View view) {
                                WifiDisplaySettings.this.mDisplayManager.resumeWifiDisplay();
                            }
                        });
                    }
                });
                this.mCertCategory.setLayoutResource(R.layout.two_buttons_panel);
            }
        }
        CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getPrefContext()) { // from class: com.android.settings.wfd.WifiDisplaySettings.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // androidx.preference.TwoStatePreference, androidx.preference.Preference
            public void onClick() {
                WifiDisplaySettings.this.mListen = !r0.mListen;
                WifiDisplaySettings wifiDisplaySettings = WifiDisplaySettings.this;
                wifiDisplaySettings.setListenMode(wifiDisplaySettings.mListen);
                setChecked(WifiDisplaySettings.this.mListen);
            }
        };
        checkBoxPreference.setTitle(R.string.wifi_display_listen_mode);
        checkBoxPreference.setChecked(this.mListen);
        this.mCertCategory.addPreference(checkBoxPreference);
        CheckBoxPreference checkBoxPreference2 = new CheckBoxPreference(getPrefContext()) { // from class: com.android.settings.wfd.WifiDisplaySettings.3
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // androidx.preference.TwoStatePreference, androidx.preference.Preference
            public void onClick() {
                WifiDisplaySettings.this.mAutoGO = !r0.mAutoGO;
                if (WifiDisplaySettings.this.mAutoGO) {
                    WifiDisplaySettings.this.startAutoGO();
                } else {
                    WifiDisplaySettings.this.stopAutoGO();
                }
                setChecked(WifiDisplaySettings.this.mAutoGO);
            }
        };
        checkBoxPreference2.setTitle(R.string.wifi_display_autonomous_go);
        checkBoxPreference2.setChecked(this.mAutoGO);
        this.mCertCategory.addPreference(checkBoxPreference2);
        DropDownPreference dropDownPreference = new DropDownPreference(getPrefContext());
        dropDownPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.wfd.WifiDisplaySettings.4
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference2, Object obj) {
                int parseInt = Integer.parseInt((String) obj);
                if (parseInt != WifiDisplaySettings.this.mWpsConfig) {
                    WifiDisplaySettings.this.mWpsConfig = parseInt;
                    WifiDisplaySettings.this.getActivity().invalidateOptionsMenu();
                    Settings.Global.putInt(WifiDisplaySettings.this.getActivity().getContentResolver(), "wifi_display_wps_config", WifiDisplaySettings.this.mWpsConfig);
                    return true;
                }
                return true;
            }
        });
        this.mWpsConfig = Settings.Global.getInt(getActivity().getContentResolver(), "wifi_display_wps_config", 4);
        String[] strArr = {ExtraTelephony.Phonelist.TYPE_CLOUDS_BLACK, "0", "2", "1"};
        dropDownPreference.setKey("wps");
        dropDownPreference.setTitle(R.string.wifi_display_wps_config);
        dropDownPreference.setEntries(new String[]{"Default", "PBC", "KEYPAD", "DISPLAY"});
        dropDownPreference.setEntryValues(strArr);
        dropDownPreference.setValue("" + this.mWpsConfig);
        dropDownPreference.setSummary("%1$s");
        this.mCertCategory.addPreference(dropDownPreference);
        DropDownPreference dropDownPreference2 = new DropDownPreference(getPrefContext());
        dropDownPreference2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.wfd.WifiDisplaySettings.5
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference2, Object obj) {
                int parseInt = Integer.parseInt((String) obj);
                if (parseInt != WifiDisplaySettings.this.mListenChannel) {
                    WifiDisplaySettings.this.mListenChannel = parseInt;
                    WifiDisplaySettings.this.getActivity().invalidateOptionsMenu();
                    WifiDisplaySettings wifiDisplaySettings = WifiDisplaySettings.this;
                    wifiDisplaySettings.setWifiP2pChannels(wifiDisplaySettings.mListenChannel, WifiDisplaySettings.this.mOperatingChannel);
                    return true;
                }
                return true;
            }
        });
        String[] strArr2 = {"Auto", "1", ExtraTelephony.Phonelist.TYPE_STRONG_CLOUDS_BLACK, "11"};
        String[] strArr3 = {"0", "1", ExtraTelephony.Phonelist.TYPE_STRONG_CLOUDS_BLACK, "11"};
        dropDownPreference2.setKey("listening_channel");
        dropDownPreference2.setTitle(R.string.wifi_display_listen_channel);
        dropDownPreference2.setEntries(strArr2);
        dropDownPreference2.setEntryValues(strArr3);
        dropDownPreference2.setValue("" + this.mListenChannel);
        dropDownPreference2.setSummary("%1$s");
        this.mCertCategory.addPreference(dropDownPreference2);
        DropDownPreference dropDownPreference3 = new DropDownPreference(getPrefContext());
        dropDownPreference3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.wfd.WifiDisplaySettings.6
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference2, Object obj) {
                int parseInt = Integer.parseInt((String) obj);
                if (parseInt != WifiDisplaySettings.this.mOperatingChannel) {
                    WifiDisplaySettings.this.mOperatingChannel = parseInt;
                    WifiDisplaySettings.this.getActivity().invalidateOptionsMenu();
                    WifiDisplaySettings wifiDisplaySettings = WifiDisplaySettings.this;
                    wifiDisplaySettings.setWifiP2pChannels(wifiDisplaySettings.mListenChannel, WifiDisplaySettings.this.mOperatingChannel);
                    return true;
                }
                return true;
            }
        });
        String[] strArr4 = {"Auto", "1", ExtraTelephony.Phonelist.TYPE_STRONG_CLOUDS_BLACK, "11", "36"};
        String[] strArr5 = {"0", "1", ExtraTelephony.Phonelist.TYPE_STRONG_CLOUDS_BLACK, "11", "36"};
        dropDownPreference3.setKey("operating_channel");
        dropDownPreference3.setTitle(R.string.wifi_display_operating_channel);
        dropDownPreference3.setEntries(strArr4);
        dropDownPreference3.setEntryValues(strArr5);
        dropDownPreference3.setValue("" + this.mOperatingChannel);
        dropDownPreference3.setSummary("%1$s");
        this.mCertCategory.addPreference(dropDownPreference3);
    }

    private RoutePreference createRoutePreference(MediaRouter.RouteInfo routeInfo) {
        WifiDisplay findWifiDisplay = findWifiDisplay(routeInfo.getDeviceAddress());
        return findWifiDisplay != null ? new WifiDisplayRoutePreference(getPrefContext(), routeInfo, findWifiDisplay) : new RoutePreference(getPrefContext(), routeInfo);
    }

    private WifiDisplay findWifiDisplay(String str) {
        WifiDisplayStatus wifiDisplayStatus = this.mWifiDisplayStatus;
        if (wifiDisplayStatus == null || str == null) {
            return null;
        }
        for (WifiDisplay wifiDisplay : wifiDisplayStatus.getDisplays()) {
            if (wifiDisplay.getDeviceAddress().equals(str)) {
                return wifiDisplay;
            }
        }
        return null;
    }

    public static boolean isAvailable(Context context) {
        return (context.getSystemService("display") == null || !context.getPackageManager().hasSystemFeature("android.hardware.wifi.direct") || context.getSystemService("wifip2p") == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pairWifiDisplay(WifiDisplay wifiDisplay) {
        if (wifiDisplay.canConnect()) {
            this.mDisplayManager.connectWifiDisplay(wifiDisplay.getDeviceAddress());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleUpdate(int i) {
        if (this.mStarted) {
            if (this.mPendingChanges == 0) {
                this.mHandler.post(this.mUpdateRunnable);
            }
            this.mPendingChanges = i | this.mPendingChanges;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setListenMode(final boolean z) {
        WifiP2pManager.ActionListener actionListener = new WifiP2pManager.ActionListener() { // from class: com.android.settings.wfd.WifiDisplaySettings.9
            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
            public void onFailure(int i) {
                StringBuilder sb = new StringBuilder();
                sb.append("Failed to ");
                sb.append(z ? "entered" : "exited");
                sb.append(" listen mode with reason ");
                sb.append(i);
                sb.append(".");
                Slog.e("WifiDisplaySettings", sb.toString());
            }

            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
            public void onSuccess() {
            }
        };
        if (z) {
            this.mWifiP2pManager.startListening(this.mWifiP2pChannel, actionListener);
        } else {
            this.mWifiP2pManager.stopListening(this.mWifiP2pChannel, actionListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWifiP2pChannels(int i, int i2) {
        this.mWifiP2pManager.setWifiP2pChannels(this.mWifiP2pChannel, i, i2, new WifiP2pManager.ActionListener() { // from class: com.android.settings.wfd.WifiDisplaySettings.10
            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
            public void onFailure(int i3) {
                Slog.e("WifiDisplaySettings", "Failed to set wifi p2p channels with reason " + i3 + ".");
            }

            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
            public void onSuccess() {
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showWifiDisplayOptionsDialog(final WifiDisplay wifiDisplay) {
        View inflate = getActivity().getLayoutInflater().inflate(R.layout.wifi_display_options, (ViewGroup) null);
        final EditText editText = (EditText) inflate.findViewById(R.id.name);
        editText.setText(wifiDisplay.getFriendlyDisplayName());
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.wfd.WifiDisplaySettings.11
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                String trim = editText.getText().toString().trim();
                if (trim.isEmpty() || trim.equals(wifiDisplay.getDeviceName())) {
                    trim = null;
                }
                WifiDisplaySettings.this.mDisplayManager.renameWifiDisplay(wifiDisplay.getDeviceAddress(), trim);
            }
        };
        new AlertDialog.Builder(getActivity()).setCancelable(true).setTitle(R.string.wifi_display_options_title).setView(inflate).setPositiveButton(R.string.wifi_display_options_done, onClickListener).setNegativeButton(R.string.wifi_display_options_forget, new DialogInterface.OnClickListener() { // from class: com.android.settings.wfd.WifiDisplaySettings.12
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                WifiDisplaySettings.this.mDisplayManager.forgetWifiDisplay(wifiDisplay.getDeviceAddress());
            }
        }).create().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startAutoGO() {
        this.mWifiP2pManager.createGroup(this.mWifiP2pChannel, new WifiP2pManager.ActionListener() { // from class: com.android.settings.wfd.WifiDisplaySettings.7
            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
            public void onFailure(int i) {
                Slog.e("WifiDisplaySettings", "Failed to start AutoGO with reason " + i + ".");
            }

            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
            public void onSuccess() {
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopAutoGO() {
        this.mWifiP2pManager.removeGroup(this.mWifiP2pChannel, new WifiP2pManager.ActionListener() { // from class: com.android.settings.wfd.WifiDisplaySettings.8
            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
            public void onFailure(int i) {
                Slog.e("WifiDisplaySettings", "Failed to stop AutoGO with reason " + i + ".");
            }

            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
            public void onSuccess() {
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleRoute(MediaRouter.RouteInfo routeInfo) {
        if (routeInfo.isSelected()) {
            MediaRouteDialogPresenter.showDialogFragment(getActivity(), 4, (View.OnClickListener) null);
        } else {
            routeInfo.select();
        }
    }

    private void unscheduleUpdate() {
        if (this.mPendingChanges != 0) {
            this.mPendingChanges = 0;
            this.mHandler.removeCallbacks(this.mUpdateRunnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update(int i) {
        boolean z;
        if ((i & 1) != 0) {
            this.mWifiDisplayOnSetting = Settings.Global.getInt(getContentResolver(), "wifi_display_on", 0) != 0;
            this.mWifiDisplayCertificationOn = Settings.Global.getInt(getContentResolver(), "wifi_display_certification_on", 0) != 0;
            this.mWpsConfig = Settings.Global.getInt(getContentResolver(), "wifi_display_wps_config", 4);
            z = true;
        } else {
            z = false;
        }
        boolean z2 = z;
        if ((i & 4) != 0) {
            this.mWifiDisplayStatus = this.mDisplayManager.getWifiDisplayStatus();
            if (this.mWifiDisplayOnSetting && !this.mWifiManager.isWifiEnabled()) {
                this.mWifiDisplayOnSetting = false;
                Settings.Global.putInt(getContentResolver(), "wifi_display_on", this.mWifiDisplayOnSetting ? 1 : 0);
            }
            this.mWifiDisplayStatus = this.mDisplayManager.getWifiDisplayStatus();
            z2 = true;
        }
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        addPreferencesFromResource(R.xml.wifi_display_settings);
        int routeCount = this.mRouter.getRouteCount();
        for (int i2 = 0; i2 < routeCount; i2++) {
            MediaRouter.RouteInfo routeAt = this.mRouter.getRouteAt(i2);
            if (routeAt.matchesTypes(4)) {
                preferenceScreen.addPreference(createRoutePreference(routeAt));
            }
        }
        WifiDisplayStatus wifiDisplayStatus = this.mWifiDisplayStatus;
        if (wifiDisplayStatus != null && wifiDisplayStatus.getFeatureState() == 3) {
            for (WifiDisplay wifiDisplay : this.mWifiDisplayStatus.getDisplays()) {
                if (!wifiDisplay.isRemembered() && wifiDisplay.isAvailable() && !wifiDisplay.equals(this.mWifiDisplayStatus.getActiveDisplay())) {
                    preferenceScreen.addPreference(new UnpairedWifiDisplayPreference(getPrefContext(), wifiDisplay));
                }
            }
            if (this.mWifiDisplayCertificationOn) {
                buildCertificationMenu(preferenceScreen);
            }
        }
        if (z2) {
            getActivity().invalidateOptionsMenu();
        }
        if (preferenceScreen.getPreferenceCount() > 1) {
            PreferenceCategory preferenceCategory = new PreferenceCategory(getPrefContext());
            preferenceCategory.setOrder(-99);
            preferenceScreen.addPreference(preferenceCategory);
        }
        ((CheckBoxPreference) findPreference("enable_wifi_display")).setChecked(this.mWifiDisplayOnSetting);
        ((CheckBoxPreference) findPreference("enable_wifi_display")).setOnPreferenceChangeListener(this);
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_remote_display;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 102;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        TextView textView = (TextView) getView().findViewById(16908292);
        this.mEmptyView = textView;
        textView.setText(R.string.wifi_display_no_devices_found);
        setEmptyView(this.mEmptyView);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        MediaRouter mediaRouter = (MediaRouter) activity.getSystemService("media_router");
        this.mRouter = mediaRouter;
        mediaRouter.setRouterGroupId("android.media.mirroring_group");
        this.mDisplayManager = (DisplayManager) activity.getSystemService("display");
        WifiP2pManager wifiP2pManager = (WifiP2pManager) activity.getSystemService("wifip2p");
        this.mWifiP2pManager = wifiP2pManager;
        this.mWifiP2pChannel = wifiP2pManager.initialize(activity, Looper.getMainLooper(), null);
        this.mWifiManager = (WifiManager) activity.getSystemService("wifi");
        addPreferencesFromResource(R.xml.wifi_display_settings);
        setHasOptionsMenu(true);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 1) {
            return super.onOptionsItemSelected(menuItem);
        }
        boolean z = !menuItem.isChecked();
        this.mWifiDisplayOnSetting = z;
        menuItem.setChecked(z);
        Settings.Global.putInt(getContentResolver(), "wifi_display_on", this.mWifiDisplayOnSetting ? 1 : 0);
        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mStarted = true;
        FragmentActivity activity = getActivity();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED");
        activity.registerReceiver(this.mReceiver, intentFilter);
        getContentResolver().registerContentObserver(Settings.Global.getUriFor("wifi_display_on"), false, this.mSettingsObserver);
        getContentResolver().registerContentObserver(Settings.Global.getUriFor("wifi_display_certification_on"), false, this.mSettingsObserver);
        getContentResolver().registerContentObserver(Settings.Global.getUriFor("wifi_display_wps_config"), false, this.mSettingsObserver);
        this.mRouter.addCallback(4, this.mRouterCallback, 1);
        update(-1);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mStarted = false;
        getActivity().unregisterReceiver(this.mReceiver);
        getContentResolver().unregisterContentObserver(this.mSettingsObserver);
        this.mRouter.removeCallback(this.mRouterCallback);
        unscheduleUpdate();
    }
}
