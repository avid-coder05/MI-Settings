package com.android.settings.development;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothCodecStatus;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.VendorUtils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settings.development.BluetoothA2dpHwOffloadRebootDialog;
import com.android.settings.development.DevelopmentSettingsDashboardFragment;
import com.android.settings.development.autofill.AutofillLoggingLevelPreferenceController;
import com.android.settings.development.autofill.AutofillResetOptionsPreferenceController;
import com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController;
import com.android.settings.development.bluetooth.AbstractBluetoothPreferenceController;
import com.android.settings.development.bluetooth.BluetoothCodecDialogPreferenceController;
import com.android.settings.development.bluetooth.BluetoothHDAudioPreferenceController;
import com.android.settings.development.qstile.DevelopmentTiles;
import com.android.settings.development.storage.SharedDataPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.tree.DevelopmentSettingsTree;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.development.SystemPropPoker;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;

/* loaded from: classes.dex */
public class DevelopmentSettingsDashboardFragment extends RestrictedDashboardFragment implements OnMainSwitchChangeListener, OemUnlockDialogHost, AdbDialogHost, AdbClearKeysDialogHost, LogPersistDialogHost, DemoModeDialogHost, SystemVarFontDialogHost, BluetoothA2dpHwOffloadRebootDialog.OnA2dpHwDialogConfirmedListener, AbstractBluetoothPreferenceController.Callback {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.development_settings) { // from class: com.android.settings.development.DevelopmentSettingsDashboardFragment.6
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return DevelopmentSettingsDashboardFragment.buildPreferenceControllers(context, null, null, null, null);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(context);
        }
    };
    private int clickCount;
    private long firstTime;
    private BluetoothA2dp mBluetoothA2dp;
    private final BluetoothA2dpConfigStore mBluetoothA2dpConfigStore;
    private final BroadcastReceiver mBluetoothA2dpReceiver;
    private final BluetoothProfile.ServiceListener mBluetoothA2dpServiceListener;
    private final BroadcastReceiver mEnableAdbReceiver;
    private boolean mIsAvailable;
    private List<AbstractPreferenceController> mPreferenceControllers;
    private final Runnable mSystemPropertiesChanged;
    private CheckBoxPreference miuiExperienceOptimization;
    private PreferenceGroup miuiExperienceOptimizationParent;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.development.DevelopmentSettingsDashboardFragment$4  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass4 implements Runnable {
        AnonymousClass4() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$run$0() {
            DevelopmentSettingsDashboardFragment.this.lambda$onStart$0();
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (this) {
                FragmentActivity activity = DevelopmentSettingsDashboardFragment.this.getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() { // from class: com.android.settings.development.DevelopmentSettingsDashboardFragment$4$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            DevelopmentSettingsDashboardFragment.AnonymousClass4.this.lambda$run$0();
                        }
                    });
                }
            }
        }
    }

    public DevelopmentSettingsDashboardFragment() {
        super("no_debugging_features");
        this.mBluetoothA2dpConfigStore = new BluetoothA2dpConfigStore();
        this.mIsAvailable = true;
        this.mPreferenceControllers = new ArrayList();
        this.mEnableAdbReceiver = new BroadcastReceiver() { // from class: com.android.settings.development.DevelopmentSettingsDashboardFragment.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                for (AbstractPreferenceController abstractPreferenceController : DevelopmentSettingsDashboardFragment.this.mPreferenceControllers) {
                    if (abstractPreferenceController instanceof AdbOnChangeListener) {
                        ((AdbOnChangeListener) abstractPreferenceController).onAdbSettingChanged();
                    }
                }
            }
        };
        this.mBluetoothA2dpReceiver = new BroadcastReceiver() { // from class: com.android.settings.development.DevelopmentSettingsDashboardFragment.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                Log.d("DevSettingsDashboard", "mBluetoothA2dpReceiver.onReceive intent=" + intent);
                if ("android.bluetooth.a2dp.profile.action.CODEC_CONFIG_CHANGED".equals(intent.getAction())) {
                    BluetoothCodecStatus bluetoothCodecStatus = (BluetoothCodecStatus) intent.getParcelableExtra("android.bluetooth.extra.CODEC_STATUS");
                    Log.d("DevSettingsDashboard", "Received BluetoothCodecStatus=" + bluetoothCodecStatus);
                    DevelopmentSettingsDashboardFragment.this.setSpecificCodecStatus(bluetoothCodecStatus);
                    for (AbstractPreferenceController abstractPreferenceController : DevelopmentSettingsDashboardFragment.this.mPreferenceControllers) {
                        if (abstractPreferenceController instanceof BluetoothServiceConnectionListener) {
                            ((BluetoothServiceConnectionListener) abstractPreferenceController).onBluetoothCodecUpdated();
                        }
                    }
                }
            }
        };
        this.mBluetoothA2dpServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settings.development.DevelopmentSettingsDashboardFragment.3
            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                synchronized (DevelopmentSettingsDashboardFragment.this.mBluetoothA2dpConfigStore) {
                    DevelopmentSettingsDashboardFragment.this.mBluetoothA2dp = (BluetoothA2dp) bluetoothProfile;
                }
                for (AbstractPreferenceController abstractPreferenceController : DevelopmentSettingsDashboardFragment.this.mPreferenceControllers) {
                    if (abstractPreferenceController instanceof BluetoothServiceConnectionListener) {
                        ((BluetoothServiceConnectionListener) abstractPreferenceController).onBluetoothServiceConnected(DevelopmentSettingsDashboardFragment.this.mBluetoothA2dp);
                    }
                }
            }

            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceDisconnected(int i) {
                synchronized (DevelopmentSettingsDashboardFragment.this.mBluetoothA2dpConfigStore) {
                    DevelopmentSettingsDashboardFragment.this.mBluetoothA2dp = null;
                }
                for (AbstractPreferenceController abstractPreferenceController : DevelopmentSettingsDashboardFragment.this.mPreferenceControllers) {
                    if (abstractPreferenceController instanceof BluetoothServiceConnectionListener) {
                        ((BluetoothServiceConnectionListener) abstractPreferenceController).onBluetoothServiceDisconnected();
                    }
                }
            }
        };
        this.mSystemPropertiesChanged = new AnonymousClass4();
        this.clickCount = 0;
    }

    static /* synthetic */ int access$708(DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment) {
        int i = developmentSettingsDashboardFragment.clickCount;
        developmentSettingsDashboardFragment.clickCount = i + 1;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Activity activity, Lifecycle lifecycle, DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new SystemServerHeapDumpPreferenceController(context));
        arrayList.add(new LocalBackupPasswordPreferenceController(context));
        arrayList.add(new StayAwakePreferenceController(context, lifecycle));
        arrayList.add(new HdcpCheckingPreferenceController(context));
        arrayList.add(new DarkUIPreferenceController(context));
        arrayList.add(new BluetoothSnoopLogPreferenceController(context));
        arrayList.add(new OemUnlockPreferenceController(context, activity, developmentSettingsDashboardFragment));
        arrayList.add(new WebViewAppPreferenceController(context));
        arrayList.add(new CoolColorTemperaturePreferenceController(context));
        arrayList.add(new DisableAutomaticUpdatesPreferenceController(context));
        arrayList.add(new SelectDSUPreferenceController(context));
        arrayList.add(new AdbPreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new ClearAdbKeysPreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new AdbAuthorizationTimeoutPreferenceController(context));
        arrayList.add(new LocalTerminalPreferenceController(context));
        arrayList.add(new AutomaticSystemServerHeapDumpPreferenceController(context));
        arrayList.add(new MockLocationAppPreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new DebugViewAttributesPreferenceController(context));
        arrayList.add(new SelectDebugAppPreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new WaitForDebuggerPreferenceController(context));
        arrayList.add(new EnableGpuDebugLayersPreferenceController(context));
        arrayList.add(new ForcePeakRefreshRatePreferenceController(context));
        arrayList.add(new EnableVerboseVendorLoggingPreferenceController(context));
        arrayList.add(new VerifyAppsOverUsbPreferenceController(context));
        arrayList.add(new ArtVerifierPreferenceController(context));
        arrayList.add(new LogdSizePreferenceController(context));
        arrayList.add(new LogPersistPreferenceController(context, developmentSettingsDashboardFragment, lifecycle));
        arrayList.add(new CameraLaserSensorPreferenceController(context));
        arrayList.add(new WifiDisplayCertificationPreferenceController(context));
        arrayList.add(new FiveGNrcaConfigController(context, lifecycle));
        arrayList.add(new FiveGViceSAPreferenceController(context, lifecycle));
        VendorUtils.addWifiCoverageExtendPreferenceController(arrayList, context);
        arrayList.add(new WifiVerboseLoggingPreferenceController(context));
        arrayList.add(new WifiScanThrottlingPreferenceController(context));
        arrayList.add(new WifiEnhancedMacRandomizationPreferenceController(context));
        arrayList.add(new MobileDataAlwaysOnPreferenceController(context));
        arrayList.add(new TetheringHardwareAccelPreferenceController(context));
        arrayList.add(new BluetoothAbsoluteVolumePreferenceController(context));
        arrayList.add(new BluetoothPageScanPreferenceController(context));
        arrayList.add(new PtsTestController(context));
        arrayList.add(new BluetoothGabeldorschePreferenceController(context));
        arrayList.add(new BluetoothAvrcpVersionPreferenceController(context));
        arrayList.add(new BluetoothMapVersionPreferenceController(context));
        arrayList.add(new BluetoothA2dpHwOffloadPreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new BluetoothLHDCAudioQualityPreferenceController(context, lifecycle, bluetoothA2dpConfigStore));
        arrayList.add(new BluetoothLHDCAudioLatencyPreferenceController(context, lifecycle, bluetoothA2dpConfigStore));
        arrayList.add(new BluetoothMaxConnectedAudioDevicesPreferenceController(context));
        arrayList.add(new ShowTapsPreferenceController(context));
        arrayList.add(new PointerLocationPreferenceController(context));
        arrayList.add(new ShowSurfaceUpdatesPreferenceController(context));
        arrayList.add(new ShowLayoutBoundsPreferenceController(context));
        arrayList.add(new ShowRefreshRatePreferenceController(context));
        arrayList.add(new RtlLayoutPreferenceController(context));
        arrayList.add(new WindowAnimationScalePreferenceController(context));
        arrayList.add(new EmulateDisplayCutoutPreferenceController(context));
        arrayList.add(new TransitionAnimationScalePreferenceController(context));
        arrayList.add(new AnimatorDurationScalePreferenceController(context));
        arrayList.add(new SecondaryDisplayPreferenceController(context));
        arrayList.add(new GpuViewUpdatesPreferenceController(context));
        arrayList.add(new HardwareLayersUpdatesPreferenceController(context));
        arrayList.add(new DebugGpuOverdrawPreferenceController(context));
        arrayList.add(new DebugNonRectClipOperationsPreferenceController(context));
        arrayList.add(new ForceDarkPreferenceController(context));
        arrayList.add(new EnableBlursPreferenceController(context));
        arrayList.add(new ForceMSAAPreferenceController(context));
        arrayList.add(new HardwareOverlaysPreferenceController(context));
        arrayList.add(new SimulateColorSpacePreferenceController(context));
        arrayList.add(new UsbAudioRoutingPreferenceController(context));
        arrayList.add(new StrictModePreferenceController(context));
        arrayList.add(new ProfileGpuRenderingPreferenceController(context));
        arrayList.add(new KeepActivitiesPreferenceController(context));
        arrayList.add(new BackgroundProcessLimitPreferenceController(context));
        arrayList.add(new CachedAppsFreezerPreferenceController(context));
        arrayList.add(new ShowFirstCrashDialogPreferenceController(context));
        arrayList.add(new AppsNotRespondingPreferenceController(context));
        arrayList.add(new NotificationChannelWarningsPreferenceController(context));
        arrayList.add(new AllowAppsOnExternalPreferenceController(context));
        arrayList.add(new ResizableActivityPreferenceController(context));
        arrayList.add(new FreeformWindowsPreferenceController(context));
        arrayList.add(new DesktopModePreferenceController(context));
        arrayList.add(new NonResizableMultiWindowPreferenceController(context));
        arrayList.add(new ShortcutManagerThrottlingPreferenceController(context));
        arrayList.add(new EnableGnssRawMeasFullTrackingPreferenceController(context));
        arrayList.add(new DemoModePreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new DefaultLaunchPreferenceController(context, "quick_settings_tiles"));
        arrayList.add(new DefaultLaunchPreferenceController(context, "toggle_adb_wireless"));
        arrayList.add(new LowFlickerBacklightController(context));
        arrayList.add(new DefaultLaunchPreferenceController(context, "feature_flags_dashboard"));
        arrayList.add(new DefaultUsbConfigurationPreferenceController(context));
        arrayList.add(new DefaultLaunchPreferenceController(context, "density"));
        arrayList.add(new AutofillLoggingLevelPreferenceController(context, lifecycle));
        arrayList.add(new AutofillResetOptionsPreferenceController(context));
        arrayList.add(new DevelopmentEnableController(context, developmentSettingsDashboardFragment));
        arrayList.add(new DeviceLockStateController(activity));
        arrayList.add(new AdbInstallPreferenceController(activity));
        arrayList.add(new AdbInputPreferenceController(activity));
        arrayList.add(new SelectLogLevelPreferenceController(activity));
        arrayList.add(new MiuiOptimizationController(activity));
        arrayList.add(new MiuiDirectEnterSystemController(activity));
        arrayList.add(new DangerousOptionsController(activity));
        arrayList.add(new SecondSpaceDeleteController(activity, lifecycle));
        arrayList.add(new BluetoothAptxAdaptiveModePreferenceController(context, lifecycle, bluetoothA2dpConfigStore));
        arrayList.add(new BluetoothAudioCodecPreferenceController(context, lifecycle, bluetoothA2dpConfigStore));
        arrayList.add(new BluetoothAudioSampleRatePreferenceController(context, lifecycle, bluetoothA2dpConfigStore));
        arrayList.add(new BluetoothAudioBitsPerSamplePreferenceController(context, lifecycle, bluetoothA2dpConfigStore));
        arrayList.add(new BluetoothAudioChannelModePreferenceController(context, lifecycle, bluetoothA2dpConfigStore));
        arrayList.add(new BluetoothAudioQualityPreferenceController(context, lifecycle, bluetoothA2dpConfigStore));
        arrayList.add(new BluetoothHDAudioPreferenceController(context, lifecycle, bluetoothA2dpConfigStore, developmentSettingsDashboardFragment));
        arrayList.add(new SharedDataPreferenceController(context));
        arrayList.add(new OverlaySettingsPreferenceController(context));
        arrayList.add(new MiuiTimeFloatingWindowController(context));
        arrayList.add(new ExtendedPowerMenuPreferenceController(context));
        arrayList.add(new SystemVarFontPreferenceController(context, developmentSettingsDashboardFragment));
        int i = Settings.Secure.getInt(context.getContentResolver(), SpeedModeToolsPreferenceController.SPEED_MODE_ENABLE, 0);
        Log.d("DevSettingsDashboard", "speedMode=" + i);
        if (i == 1) {
            arrayList.add(new SpeedModeToolsPreferenceController(context, SpeedModeToolsPreferenceController.SPEED_MODE_KEY));
        }
        return arrayList;
    }

    private void disableDeveloperOptions() {
        if (Utils.isMonkeyRunning()) {
            return;
        }
        DevelopmentSettingsEnabler.setDevelopmentSettingsEnabled(getContext(), false);
        SystemPropPoker systemPropPoker = SystemPropPoker.getInstance();
        systemPropPoker.blockPokes();
        for (AbstractPreferenceController abstractPreferenceController : this.mPreferenceControllers) {
            if (abstractPreferenceController instanceof DeveloperOptionsPreferenceController) {
                ((DeveloperOptionsPreferenceController) abstractPreferenceController).onDeveloperOptionsDisabled();
            } else if (abstractPreferenceController instanceof SpeedModeToolsPreferenceController) {
                ((SpeedModeToolsPreferenceController) abstractPreferenceController).onDeveloperOptionsSwitchDisabled();
            }
        }
        systemPropPoker.unblockPokes();
        systemPropPoker.poke();
    }

    private void enableDeveloperOptions() {
        if (Utils.isMonkeyRunning()) {
            return;
        }
        DevelopmentSettingsEnabler.setDevelopmentSettingsEnabled(getContext(), true);
        for (AbstractPreferenceController abstractPreferenceController : this.mPreferenceControllers) {
            if (abstractPreferenceController instanceof DeveloperOptionsPreferenceController) {
                ((DeveloperOptionsPreferenceController) abstractPreferenceController).onDeveloperOptionsEnabled();
            } else if (abstractPreferenceController instanceof SpeedModeToolsPreferenceController) {
                ((SpeedModeToolsPreferenceController) abstractPreferenceController).onDeveloperOptionsSwitchEnabled();
            }
        }
    }

    private void handleQsTileLongPressActionIfAny() {
        Intent intent = getActivity().getIntent();
        if (intent == null || !TextUtils.equals("android.service.quicksettings.action.QS_TILE_PREFERENCES", intent.getAction())) {
            return;
        }
        Log.d("DevSettingsDashboard", "Developer options started from qstile long-press");
        ComponentName componentName = (ComponentName) intent.getParcelableExtra("android.intent.extra.COMPONENT_NAME");
        if (componentName != null && DevelopmentTiles.WirelessDebugging.class.getName().equals(componentName.getClassName()) && ((WirelessDebuggingPreferenceController) getDevelopmentOptionsController(WirelessDebuggingPreferenceController.class)).isAvailable()) {
            Log.d("DevSettingsDashboard", "Long press from wireless debugging qstile");
            new SubSettingLauncher(getContext()).setDestination(WirelessDebuggingFragment.class.getName()).setSourceMetricsCategory(1831).launch();
        }
    }

    private void hideMiuiExperienceOtimization() {
        if (!Build.IS_INTERNATIONAL_BUILD || getPreferenceScreen() == null) {
            return;
        }
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("miui_experience_optimization");
        this.miuiExperienceOptimization = checkBoxPreference;
        PreferenceGroup parent = checkBoxPreference.getParent();
        this.miuiExperienceOptimizationParent = parent;
        parent.removePreference(this.miuiExperienceOptimization);
        getPreferenceScreen().findPreference("autofill_reset_developer_options").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.development.DevelopmentSettingsDashboardFragment.5
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                if (DevelopmentSettingsDashboardFragment.this.miuiExperienceOptimization != null && DevelopmentSettingsDashboardFragment.this.miuiExperienceOptimizationParent != null) {
                    if (DevelopmentSettingsDashboardFragment.this.clickCount == 2) {
                        DevelopmentSettingsDashboardFragment.this.miuiExperienceOptimization.setChecked(SystemProperties.getBoolean("persist.sys.miui_optimization", !Build.IS_CTS_BUILD));
                        DevelopmentSettingsDashboardFragment.this.miuiExperienceOptimizationParent.addPreference(DevelopmentSettingsDashboardFragment.this.miuiExperienceOptimization);
                    }
                    long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis - DevelopmentSettingsDashboardFragment.this.firstTime <= 1000) {
                        DevelopmentSettingsDashboardFragment.access$708(DevelopmentSettingsDashboardFragment.this);
                    } else {
                        DevelopmentSettingsDashboardFragment.this.clickCount = 0;
                    }
                    DevelopmentSettingsDashboardFragment.this.firstTime = currentTimeMillis;
                }
                return false;
            }
        });
    }

    private void registerReceivers() {
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(this.mEnableAdbReceiver, new IntentFilter("com.android.settingslib.development.AbstractEnableAdbController.ENABLE_ADB_STATE_CHANGED"));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.a2dp.profile.action.CODEC_CONFIG_CHANGED");
        getActivity().registerReceiver(this.mBluetoothA2dpReceiver, intentFilter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSpecificCodecStatus(BluetoothCodecStatus bluetoothCodecStatus) {
        LocalBluetoothManager localBluetoothManager;
        BluetoothCodecConfig codecConfig;
        Log.d("DevSettingsDashboard", "setSpecificCodecStatus()");
        if (bluetoothCodecStatus == null || (localBluetoothManager = LocalBluetoothManager.getInstance(getActivity(), null)) == null) {
            return;
        }
        CachedBluetoothDeviceManager cachedDeviceManager = localBluetoothManager.getCachedDeviceManager();
        synchronized (this.mBluetoothA2dpConfigStore) {
            if (this.mBluetoothA2dp != null && (codecConfig = bluetoothCodecStatus.getCodecConfig()) != null && codecConfig.getCodecType() == 4) {
                Iterator<BluetoothDevice> it = this.mBluetoothA2dp.getConnectedDevices().iterator();
                while (it.hasNext()) {
                    CachedBluetoothDevice findDevice = cachedDeviceManager.findDevice(it.next());
                    if (findDevice != null && findDevice.getSpecificCodecStatus("LDAC") != 1) {
                        findDevice.setSpecificCodecStatus("LDAC", 1);
                        Log.d("DevSettingsDashboard", "setSpecificCodecStatus() end");
                    }
                }
            }
        }
    }

    private void unregisterReceivers() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(this.mEnableAdbReceiver);
        getActivity().unregisterReceiver(this.mBluetoothA2dpReceiver);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        if (Utils.isMonkeyRunning()) {
            this.mPreferenceControllers = new ArrayList();
            return null;
        }
        List<AbstractPreferenceController> buildPreferenceControllers = buildPreferenceControllers(context, getActivity(), getSettingsLifecycle(), this, new BluetoothA2dpConfigStore());
        this.mPreferenceControllers = buildPreferenceControllers;
        return buildPreferenceControllers;
    }

    <T extends AbstractPreferenceController> T getDevelopmentOptionsController(Class<T> cls) {
        return (T) use(cls);
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "DevSettingsDashboard";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 39;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return DevelopmentSettingsDashboardFragment.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return Utils.isMonkeyRunning() ? R.xml.placeholder_prefs : R.xml.development_settings;
    }

    @Override // com.android.settings.development.BluetoothA2dpHwOffloadRebootDialog.OnA2dpHwDialogConfirmedListener
    public void onA2dpHwDialogConfirmed() {
        ((BluetoothA2dpHwOffloadPreferenceController) getDevelopmentOptionsController(BluetoothA2dpHwOffloadPreferenceController.class)).onA2dpHwDialogConfirmed();
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        PreferenceCategory preferenceCategory;
        Preference findPreference;
        super.onActivityCreated(bundle);
        setIfOnlyAvailableForAdmins(true);
        if (isUiRestricted() || !WizardManagerHelper.isDeviceProvisioned(getActivity())) {
            this.mIsAvailable = false;
            if (!isUiRestrictedByOnlyAdmin()) {
                getEmptyTextView().setText(R.string.development_settings_not_available);
            }
            getPreferenceScreen().removeAll();
            return;
        }
        if (DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(getContext())) {
            enableDeveloperOptions();
            handleQsTileLongPressActionIfAny();
        } else {
            disableDeveloperOptions();
        }
        if (MiuiUtils.isSecondSpace(getContext())) {
            getPreferenceScreen().removePreference(findPreference("storage_category"));
        }
        String string = getResources().getString(R.string.string_off);
        Resources resources = getResources();
        int i = R.string.string_KB;
        String format = String.format(resources.getString(i), 64);
        String format2 = String.format(getResources().getString(i), 256);
        Resources resources2 = getResources();
        int i2 = R.string.string_MB;
        ((DropDownPreference) findPreference("select_logd_size")).setEntries(new String[]{string, format, format2, String.format(resources2.getString(i2), 1), String.format(getResources().getString(i2), 4), String.format(getResources().getString(i2), 8)});
        try {
            if (!SystemProperties.get("ro.soc.model").equals("SM8450") && (findPreference = (preferenceCategory = (PreferenceCategory) getPreferenceScreen().findPreference("debug_networking_category")).findPreference(DevelopmentSettingsTree.BLUETOOTH_ENABLE_PTS_TEST_KEY)) != null) {
                preferenceCategory.removePreference(findPreference);
            }
        } catch (Exception e) {
            Log.e("DevSettingsDashboard", e.toString());
        }
        try {
            hideMiuiExperienceOtimization();
        } catch (Exception e2) {
            Log.e("DevSettingsDashboard", e2.toString());
        }
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        boolean z = false;
        for (AbstractPreferenceController abstractPreferenceController : this.mPreferenceControllers) {
            if (abstractPreferenceController instanceof OnActivityResultListener) {
                z |= ((OnActivityResultListener) abstractPreferenceController).onActivityResult(i, i2, intent);
            }
        }
        if (z) {
            return;
        }
        super.onActivityResult(i, i2, intent);
    }

    @Override // com.android.settings.development.AdbClearKeysDialogHost
    public void onAdbClearKeysDialogConfirmed() {
        ((ClearAdbKeysPreferenceController) getDevelopmentOptionsController(ClearAdbKeysPreferenceController.class)).onClearAdbKeysConfirmed();
    }

    @Override // com.android.settings.development.bluetooth.AbstractBluetoothPreferenceController.Callback
    public void onBluetoothCodecChanged() {
        for (AbstractPreferenceController abstractPreferenceController : this.mPreferenceControllers) {
            if ((abstractPreferenceController instanceof AbstractBluetoothDialogPreferenceController) && !(abstractPreferenceController instanceof BluetoothCodecDialogPreferenceController)) {
                ((AbstractBluetoothDialogPreferenceController) abstractPreferenceController).onBluetoothCodecUpdated();
            }
        }
    }

    @Override // com.android.settings.development.bluetooth.AbstractBluetoothPreferenceController.Callback
    public void onBluetoothHDAudioEnabled(boolean z) {
        for (AbstractPreferenceController abstractPreferenceController : this.mPreferenceControllers) {
            if (abstractPreferenceController instanceof AbstractBluetoothDialogPreferenceController) {
                ((AbstractBluetoothDialogPreferenceController) abstractPreferenceController).onHDAudioEnabled(z);
            }
        }
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Utils.isMonkeyRunning()) {
            getActivity().finish();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        registerReceivers();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && defaultAdapter.isEnabled()) {
            defaultAdapter.getProfileProxy(getActivity().getApplicationContext(), this.mBluetoothA2dpServiceListener, 2);
        }
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        unregisterReceivers();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            defaultAdapter.closeProfileProxy(2, this.mBluetoothA2dp);
            this.mBluetoothA2dp = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDisableDevelopmentOptionsConfirmed() {
        disableDeveloperOptions();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDisableDevelopmentOptionsRejected() {
        ((DevelopmentEnableController) getDevelopmentOptionsController(DevelopmentEnableController.class)).setChecked(true);
    }

    @Override // com.android.settings.development.LogPersistDialogHost
    public void onDisableLogPersistDialogConfirmed() {
        ((LogPersistPreferenceController) getDevelopmentOptionsController(LogPersistPreferenceController.class)).onDisableLogPersistDialogConfirmed();
    }

    @Override // com.android.settings.development.LogPersistDialogHost
    public void onDisableLogPersistDialogRejected() {
        ((LogPersistPreferenceController) getDevelopmentOptionsController(LogPersistPreferenceController.class)).onDisableLogPersistDialogRejected();
    }

    @Override // com.android.settings.development.AdbDialogHost
    public void onEnableAdbDialogConfirmed() {
        ((AdbPreferenceController) getDevelopmentOptionsController(AdbPreferenceController.class)).onAdbDialogConfirmed();
    }

    @Override // com.android.settings.development.AdbDialogHost
    public void onEnableAdbDialogDismissed() {
        ((AdbPreferenceController) getDevelopmentOptionsController(AdbPreferenceController.class)).onAdbDialogDismissed();
    }

    @Override // com.android.settings.development.DemoModeDialogHost
    public void onEnableDemoModeConfirmed() {
        ((DemoModePreferenceController) getDevelopmentOptionsController(DemoModePreferenceController.class)).onEnableDemoModeConfirmed();
    }

    @Override // com.android.settings.development.DemoModeDialogHost
    public void onEnableDemoModeDismissed() {
        ((DemoModePreferenceController) getDevelopmentOptionsController(DemoModePreferenceController.class)).onEnableDemoModeDismissed();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onEnableDevelopmentOptionsConfirmed() {
        enableDeveloperOptions();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onEnableDevelopmentOptionsRejected() {
        ((DevelopmentEnableController) getDevelopmentOptionsController(DevelopmentEnableController.class)).setChecked(false);
    }

    @Override // com.android.settings.development.OemUnlockDialogHost
    public void onOemUnlockDialogConfirmed() {
        ((OemUnlockPreferenceController) getDevelopmentOptionsController(OemUnlockPreferenceController.class)).onOemUnlockConfirmed();
    }

    @Override // com.android.settings.development.OemUnlockDialogHost
    public void onOemUnlockDialogDismissed() {
        ((OemUnlockPreferenceController) getDevelopmentOptionsController(OemUnlockPreferenceController.class)).onOemUnlockDismissed();
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        if (z != DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(getContext())) {
            if (z) {
                EnableDevelopmentSettingWarningDialog.show(this);
                return;
            }
            BluetoothA2dpHwOffloadPreferenceController bluetoothA2dpHwOffloadPreferenceController = (BluetoothA2dpHwOffloadPreferenceController) getDevelopmentOptionsController(BluetoothA2dpHwOffloadPreferenceController.class);
            if (bluetoothA2dpHwOffloadPreferenceController == null || bluetoothA2dpHwOffloadPreferenceController.isDefaultValue()) {
                disableDeveloperOptions();
            } else {
                DisableDevSettingsDialogFragment.show(this);
            }
        }
    }

    @Override // com.android.settings.development.SystemVarFontDialogHost
    public void onSystemVarFontDialogConfirmed() {
        ((SystemVarFontPreferenceController) getDevelopmentOptionsController(SystemVarFontPreferenceController.class)).onSystemVarFontDialogConfirmed();
    }

    @Override // com.android.settings.development.SystemVarFontDialogHost
    public void onSystemVarFontDialogDismissed() {
        ((SystemVarFontPreferenceController) getDevelopmentOptionsController(SystemVarFontPreferenceController.class)).onSystemVarFontDialogDismissed();
    }
}
