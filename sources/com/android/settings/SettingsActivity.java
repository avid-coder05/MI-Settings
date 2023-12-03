package com.android.settings;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.android.internal.util.ArrayUtils;
import com.android.settings.Settings;
import com.android.settings.accounts.XiaomiAccountUtils;
import com.android.settings.applications.appinfo.AppButtonsPreferenceController;
import com.android.settings.applications.manageapplications.ManageApplications;
import com.android.settings.core.OnActivityResultListener;
import com.android.settings.core.SettingsBaseActivity;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.gateway.SettingsGateway;
import com.android.settings.dashboard.DashboardFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.FunctionColumns;
import com.android.settings.search.tree.SecuritySettingsTree;
import com.android.settings.security.SecurityGateway;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.utils.TabletUtils;
import com.android.settings.wfd.WifiDisplaySettings;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settings.wifi.MiuiWifiSettings;
import com.android.settingslib.core.instrumentation.Instrumentable;
import com.android.settingslib.core.instrumentation.SharedPreferencesLogger;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.utils.ThreadUtils;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import miui.cloud.finddevice.FindDeviceStatusManager;
import miui.content.ExtraIntent;
import miui.os.Build;
import miui.payment.PaymentManager;
import miui.settings.commonlib.MemoryOptimizationUtil;
import miui.settings.splitlib.SplitUtils;
import miuix.appcompat.app.ActionBar;

/* loaded from: classes.dex */
public class SettingsActivity extends SettingsBaseActivity implements PreferenceManager.OnPreferenceTreeClickListener, PreferenceFragmentCompat.OnPreferenceStartFragmentCallback, ButtonBarHandler, FragmentManager.OnBackStackChangedListener {
    private String initialFragmentName;
    private DashboardFeatureProvider mDashboardFeatureProvider;
    private BroadcastReceiver mDevelopmentSettingsListener;
    private BroadcastReceiver mFindDeviceStatusChangeReceiver;
    private String mFragmentClass;
    private CharSequence mInitialTitle;
    private int mInitialTitleResId;
    private SettingsMainSwitchBar mMainSwitch;
    private MemoryOptimizationUtil mMemoryOptimizationUtil;
    private MiuiCustSplitUtils mMiuiCustSplitUtilsImpl;
    private Button mNextButton;
    private TrimMemoryUtils mTrimMemoryUtils;
    private UpdateTask mUpdateTilesTask;
    private XiaomiAccountUtils mXiaomiAccountUtils;
    private boolean mBatteryPresent = true;
    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() { // from class: com.android.settings.SettingsActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            boolean isBatteryPresent;
            if (!"android.intent.action.BATTERY_CHANGED".equals(intent.getAction()) || SettingsActivity.this.mBatteryPresent == (isBatteryPresent = Utils.isBatteryPresent(intent))) {
                return;
            }
            SettingsActivity.this.mBatteryPresent = isBatteryPresent;
            SettingsActivity.this.updateTilesList();
        }
    };
    private ArrayList<DashboardCategory> mCategories = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class UpdateTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<SettingsActivity> mActivityRef;

        UpdateTask(SettingsActivity settingsActivity) {
            this.mActivityRef = new WeakReference<>(settingsActivity);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            WeakReference<SettingsActivity> weakReference = this.mActivityRef;
            if (weakReference == null || weakReference.get() == null) {
                return null;
            }
            this.mActivityRef.get().doUpdateTilesList();
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: callXiaomiAccountUtilsRenameDevice  reason: merged with bridge method [inline-methods] */
    public void lambda$onCreate$0() {
        if (Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        XiaomiAccountUtils xiaomiAccountUtils = XiaomiAccountUtils.getInstance(this);
        this.mXiaomiAccountUtils = xiaomiAccountUtils;
        xiaomiAccountUtils.renameDevice();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doUpdateTilesList() {
        PackageManager packageManager = getPackageManager();
        boolean isAdminUser = UserManager.get(this).isAdminUser();
        String packageName = getPackageName();
        StringBuilder sb = new StringBuilder();
        boolean z = setTileEnabled(sb, new ComponentName(packageName, Settings.WifiDisplaySettingsActivity.class.getName()), WifiDisplaySettings.isAvailable(this), isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.DevelopmentSettingsDashboardActivity.class.getName()), DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(this) && !Utils.isMonkeyRunning(), isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.UserSettingsActivity.class.getName()), UserManager.supportsMultipleUsers() && !Utils.isMonkeyRunning(), isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.DataUsageSummaryActivity.class.getName()), Utils.isBandwidthControlEnabled(), isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.PowerUsageSummaryActivity.class.getName()), this.mBatteryPresent, isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.ConnectedDeviceDashboardActivity.class.getName()), UserManager.isDeviceInDemoMode(this) ^ true, isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.DataUsageSummaryActivity.class.getName()), Utils.isBandwidthControlEnabled(), isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.BluetoothSettingsActivity.class.getName()), packageManager.hasSystemFeature("android.hardware.bluetooth"), isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.WifiSettingsActivity.class.getName()), packageManager.hasSystemFeature("android.hardware.wifi"), isAdminUser)))))))));
        if (!isAdminUser) {
            List<DashboardCategory> allCategories = this.mDashboardFeatureProvider.getAllCategories();
            synchronized (allCategories) {
                for (DashboardCategory dashboardCategory : allCategories) {
                    int tilesCount = dashboardCategory.getTilesCount();
                    for (int i = 0; i < tilesCount; i++) {
                        ComponentName component = dashboardCategory.getTile(i).getIntent().getComponent();
                        boolean contains = ArrayUtils.contains(SettingsGateway.SETTINGS_FOR_RESTRICTED, component.getClassName());
                        if (packageName.equals(component.getPackageName()) && !contains) {
                            if (!setTileEnabled(sb, component, false, isAdminUser) && !z) {
                                z = false;
                            }
                            z = true;
                        }
                    }
                }
            }
        }
        if (!z) {
            Log.d("SettingsActivity", "No enabled state changed, skipping updateCategory call");
            return;
        }
        Log.d("SettingsActivity", "Enabled state changed for some tiles, reloading all categories " + sb.toString());
        this.mCategoryMixin.updateCategories();
    }

    private void getMetaData() {
        Bundle bundle;
        try {
            ActivityInfo activityInfo = getPackageManager().getActivityInfo(getComponentName(), 128);
            if (activityInfo != null && (bundle = activityInfo.metaData) != null) {
                this.mFragmentClass = bundle.getString("com.android.settings.FRAGMENT_CLASS");
            }
        } catch (PackageManager.NameNotFoundException unused) {
            Log.d("SettingsActivity", "Cannot get Metadata for: " + getComponentName().toString());
        }
    }

    private String getMetricsTag() {
        String initialFragmentName = (getIntent() == null || !getIntent().hasExtra(":settings:show_fragment")) ? null : getInitialFragmentName(getIntent());
        if (TextUtils.isEmpty(initialFragmentName)) {
            Log.w("SettingsActivity", "MetricsTag is invalid " + initialFragmentName);
            initialFragmentName = getClass().getName();
        }
        return initialFragmentName.startsWith("com.android.settings.") ? initialFragmentName.replace("com.android.settings.", "") : initialFragmentName;
    }

    private String getStartingFragmentClass(Intent intent) {
        String str = this.mFragmentClass;
        if (str != null) {
            return str;
        }
        String className = intent.getComponent().getClassName();
        if (className.equals(getClass().getName())) {
            return null;
        }
        if ("com.android.settings.RunningServices".equals(className) || "com.android.settings.applications.StorageUse".equals(className)) {
            className = ManageApplications.class.getName();
        }
        return "com.android.settings.wifi.WifiSettings".equals(className) ? MiuiWifiSettings.class.getName() : className;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1(View view) {
        setResult(0, null);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$2(View view) {
        setResult(-1, null);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$3(View view) {
        setResult(-1, null);
        finish();
    }

    private void prepareWindow() {
        Intent intent = getIntent();
        boolean booleanExtra = intent.getBooleanExtra(ExtraIntent.EXTRA_SHOW_ON_FINDDEVICE_KEYGUARD, false);
        if (!booleanExtra) {
            Bundle[] bundleArr = {intent.getBundleExtra(":android:show_fragment_args"), intent.getBundleExtra(":settings:show_fragment_args")};
            int i = 0;
            while (true) {
                if (i >= 2) {
                    break;
                }
                Bundle bundle = bundleArr[i];
                if (bundle != null && bundle.getBoolean(ExtraIntent.EXTRA_SHOW_ON_FINDDEVICE_KEYGUARD, false)) {
                    booleanExtra = true;
                    break;
                }
                i++;
            }
        }
        boolean z = (intent.getFlags() & 1048576) != 0;
        if (!booleanExtra || z) {
            return;
        }
        getWindow().setFormat(-1);
        final WindowManager.LayoutParams attributes = getWindow().getAttributes();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(attributes);
        layoutParams.flags |= 524288;
        layoutParams.extraFlags |= 4096;
        getWindow().setAttributes(layoutParams);
        this.mFindDeviceStatusChangeReceiver = new BroadcastReceiver() { // from class: com.android.settings.SettingsActivity.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent2) {
                SettingsActivity.this.getWindow().setAttributes(attributes);
                SettingsActivity.this.finish();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FindDeviceStatusManager.LAST_STATUS_CHANGED_ACTION);
        registerReceiver(this.mFindDeviceStatusChangeReceiver, intentFilter);
    }

    private boolean redirectStorageManagerActivity(Bundle bundle) {
        String action = getIntent().getAction();
        if (bundle == null && TextUtils.equals(action, "android.settings.INTERNAL_STORAGE_SETTINGS")) {
            startActivity(new Intent("android.intent.action.MAIN").setFlags(335544320).setClassName(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME, "com.miui.optimizecenter.storage.StorageActivity"));
            finish();
            return true;
        }
        return false;
    }

    private void redirectTabletActivity(Bundle bundle) {
        if (bundle != null && bundle.getBoolean("config_change_flag", false) && TabletUtils.IS_TABLET) {
            Intent intent = new Intent();
            intent.setClass(this, MiuiSettings.class);
            intent.putExtra(":settings:show_fragment", this.initialFragmentName);
            intent.addFlags(268435456);
            intent.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON);
            startActivity(intent);
            finish();
        }
    }

    private boolean setTileEnabled(StringBuilder sb, ComponentName componentName, boolean z, boolean z2) {
        if (!z2 && getPackageName().equals(componentName.getPackageName()) && !ArrayUtils.contains(SettingsGateway.SETTINGS_FOR_RESTRICTED, componentName.getClassName())) {
            z = false;
        }
        boolean tileEnabled = setTileEnabled(componentName, z);
        if (tileEnabled) {
            sb.append(componentName.toShortString());
            sb.append(",");
        }
        return tileEnabled;
    }

    private void setTitleFromBackStack() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount != 0) {
            setTitleFromBackStackEntry(getSupportFragmentManager().getBackStackEntryAt(backStackEntryCount - 1));
            return;
        }
        int i = this.mInitialTitleResId;
        if (i > 0) {
            setTitle(i);
        } else {
            setTitle(this.mInitialTitle);
        }
    }

    private void setTitleFromBackStackEntry(FragmentManager.BackStackEntry backStackEntry) {
        int breadCrumbTitleRes = backStackEntry.getBreadCrumbTitleRes();
        CharSequence text = breadCrumbTitleRes > 0 ? getText(breadCrumbTitleRes) : backStackEntry.getBreadCrumbTitle();
        if (text != null) {
            setTitle(text);
        }
    }

    private void setTitleFromIntent(Intent intent) {
        Log.d("SettingsActivity", "Starting to set activity title");
        int intExtra = intent.getIntExtra(":settings:show_fragment_title_resid", -1);
        CharSequence stringExtra = intent.getStringExtra(":settings:show_fragment_title");
        if (!TextUtils.isEmpty(stringExtra) || intExtra <= 0) {
            this.mInitialTitleResId = -1;
            if (stringExtra == null) {
                stringExtra = getTitle();
            }
            this.mInitialTitle = stringExtra;
            setTitle(stringExtra);
        } else {
            this.mInitialTitle = null;
            this.mInitialTitleResId = intExtra;
            String stringExtra2 = intent.getStringExtra(":settings:show_fragment_title_res_package_name");
            if (stringExtra2 != null) {
                try {
                    CharSequence text = createPackageContextAsUser(stringExtra2, 0, new UserHandle(UserHandle.myUserId())).getResources().getText(this.mInitialTitleResId);
                    this.mInitialTitle = text;
                    setTitle(text);
                    this.mInitialTitleResId = -1;
                    return;
                } catch (PackageManager.NameNotFoundException unused) {
                    Log.w("SettingsActivity", "Could not find package" + stringExtra2);
                }
            } else {
                setTitle(this.mInitialTitleResId);
            }
        }
        Log.d("SettingsActivity", "Done setting title");
    }

    private void switchToFragment(String str, Bundle bundle, boolean z, int i, CharSequence charSequence) {
        Log.d("SettingsActivity", "Switching to fragment " + str);
        if (str == null) {
            finish();
            return;
        }
        if ("com.android.settings.connecteddevice.BluetoothDashboardFragment".equals(str)) {
            str = "com.android.settings.bluetooth.MiuiBluetoothSettings";
        }
        if (str.equals("com.android.settings.sim.SimSettings")) {
            if (Utils.isSimSettingsApkAvailable()) {
                Log.i("SettingsActivity", "switchToFragment, launch simSettings");
                Intent intent = new Intent("com.android.settings.sim.SIM_SUB_INFO_SETTINGS");
                if (!getPackageManager().queryIntentActivities(intent, 0).isEmpty()) {
                    startActivity(intent);
                }
            }
            finish();
        } else if (z && !isValidFragment(str)) {
            throw new IllegalArgumentException("Invalid fragment for this activity: " + str);
        } else {
            if (!SecurityGateway.isSystemOrCredibleApp(getApplicationContext(), SecurityGateway.reflectGetAppReferrer(this)).booleanValue() && !SecurityGateway.isSecurityFragment(str)) {
                Log.e("SettingsActivity", "The current application is not allowed to jump to the fragment { fragment :" + str + "}, it is not a system application");
                finish();
                return;
            }
            try {
                Class.forName(str);
                if (bundle == null) {
                    bundle = new Bundle();
                    bundle.putParcelable(PaymentManager.KEY_INTENT, getIntent());
                }
                bundle.putInt(":android:show_fragment_title", i);
                bundle.putCharSequence(":settings:show_fragment_title", charSequence);
                Fragment targetFragment = Utils.getTargetFragment(this, str, bundle);
                if (targetFragment == null) {
                    return;
                }
                FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
                beginTransaction.replace(R.id.main_content, targetFragment);
                if (i > 0) {
                    beginTransaction.setBreadCrumbTitle(i);
                } else if (charSequence != null) {
                    beginTransaction.setBreadCrumbTitle(charSequence);
                }
                beginTransaction.commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();
                Log.d("SettingsActivity", "Executed frag manager pendingTransactions");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void unprepareWindow() {
        BroadcastReceiver broadcastReceiver = this.mFindDeviceStatusChangeReceiver;
        if (broadcastReceiver == null) {
            return;
        }
        unregisterReceiver(broadcastReceiver);
        this.mFindDeviceStatusChangeReceiver = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTilesList() {
        UpdateTask updateTask = this.mUpdateTilesTask;
        if (updateTask == null || updateTask.getStatus() == AsyncTask.Status.FINISHED) {
            UpdateTask updateTask2 = new UpdateTask(this);
            this.mUpdateTilesTask = updateTask2;
            updateTask2.execute(new Void[0]);
        }
    }

    public void finishPreferencePanel(int i, Intent intent) {
        setResult(i, intent);
        if (intent == null || !intent.getBooleanExtra(AppButtonsPreferenceController.KEY_REMOVE_TASK_WHEN_FINISHING, false)) {
            finish();
        } else {
            finishAndRemoveTask();
        }
    }

    public String getInitialFragmentName(Intent intent) {
        return intent.getStringExtra(":settings:show_fragment");
    }

    @Override // android.app.Activity
    public Intent getIntent() {
        Intent intent = super.getIntent();
        String startingFragmentClass = getStartingFragmentClass(intent);
        if (startingFragmentClass != null) {
            Intent intent2 = new Intent(intent);
            Bundle bundleExtra = intent.getBundleExtra(":settings:show_fragment_args");
            Bundle bundle = bundleExtra != null ? new Bundle(bundleExtra) : new Bundle();
            if (!bundle.getBoolean("extra_key_use_custom_fragment", false)) {
                intent2.putExtra(":settings:show_fragment", startingFragmentClass);
            }
            bundle.putParcelable(PaymentManager.KEY_INTENT, intent);
            intent2.putExtra(":settings:show_fragment_args", bundle);
            return intent2;
        }
        return intent;
    }

    @Override // com.android.settings.ButtonBarHandler
    public Button getNextButton() {
        return this.mNextButton;
    }

    public int getOwnerTheme() {
        return R.style.ThemeMiuiSettings_Main;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public SharedPreferences getSharedPreferences(String str, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append(getPackageName());
        sb.append("_preferences");
        return str.equals(sb.toString()) ? new SharedPreferencesLogger(this, getMetricsTag(), FeatureFactory.getFactory(this).getMetricsFeatureProvider()) : super.getSharedPreferences(str, i);
    }

    public SettingsMainSwitchBar getSwitchBar() {
        return this.mMainSwitch;
    }

    @Override // com.android.settings.ButtonBarHandler
    public boolean hasNextButton() {
        return this.mNextButton != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isValidFragment(String str) {
        int i = 0;
        while (true) {
            String[] strArr = SettingsGateway.ENTRY_FRAGMENTS;
            if (i >= strArr.length || strArr[i].equals(str)) {
                return true;
            }
            i++;
        }
    }

    void launchSettingFragment(String str, Intent intent) {
        if (str == null) {
            this.mInitialTitleResId = R.string.dashboard_title;
            startActivity(new Intent(SplitUtils.SETTINGS_MAIN_INTENT));
            finish();
            return;
        }
        setTitleFromIntent(intent);
        Bundle bundleExtra = intent.getBundleExtra(":android:show_fragment_args");
        if (bundleExtra == null) {
            bundleExtra = intent.getBundleExtra(":settings:show_fragment_args");
        }
        Bundle bundle = bundleExtra;
        if (TextUtils.isEmpty(str)) {
            str = this.mFragmentClass;
        }
        switchToFragment(str, bundle, true, this.mInitialTitleResId, this.mInitialTitle);
    }

    protected boolean needToLaunchSettingsFragment() {
        return true;
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof OnActivityResultListener) {
                    fragment.onActivityResult(i, i2, intent);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity, android.view.ContextThemeWrapper
    public void onApplyThemeResource(Resources.Theme theme, int i, boolean z) {
        theme.applyStyle(R.style.SetupWizardPartnerResource, true);
        super.onApplyThemeResource(theme, i, z);
    }

    @Override // androidx.fragment.app.FragmentManager.OnBackStackChangedListener
    public void onBackStackChanged() {
        setTitleFromBackStack();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        View findViewById;
        Uri data;
        getMetaData();
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra(":settings:show_fragment");
        this.initialFragmentName = stringExtra;
        if (TextUtils.isEmpty(stringExtra)) {
            this.initialFragmentName = intent.getStringExtra(":android:show_fragment");
        }
        if (TextUtils.equals(this.initialFragmentName, "com.android.settings.display.PaperModeFragment") && !MiuiUtils.supportPaperEyeCare()) {
            this.initialFragmentName = "com.android.settings.display.OldPaperModeFragment";
        }
        if (TextUtils.equals(this.initialFragmentName, "com.android.settings.bluetooth.MiuiBluetoothSettings")) {
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.SettingsActivity$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    SettingsActivity.this.lambda$onCreate$0();
                }
            });
        }
        if (TextUtils.equals(this.initialFragmentName, "com.android.settings.MiuiSecuritySettings")) {
            this.initialFragmentName = "com.android.settings.security.MiuiSecurityAndPrivacySettings";
            intent.putExtra(":settings:show_fragment_title", getString(R.string.password_and_security));
        }
        redirectTabletActivity(bundle);
        if (TextUtils.equals(this.initialFragmentName, "com.android.settings.notification.MiuiZenModeSettingsFragment") && !TextUtils.isEmpty(getCallingPackage()) && !TextUtils.equals(getPackageName(), getCallingPackage())) {
            this.initialFragmentName = "com.android.settings.MiuiSoundSettings";
        }
        if (TextUtils.equals(this.initialFragmentName, "com.android.settings.notification.zen.ZenModeSettings")) {
            this.initialFragmentName = "com.android.settings.MiuiSoundSettings";
        }
        if ("android.settings.MANAGE_UNKNOWN_APP_SOURCES".equals(intent.getAction()) && Build.IS_INTERNATIONAL_BUILD && (data = intent.getData()) != null && FunctionColumns.PACKAGE.equals(data.getScheme())) {
            this.initialFragmentName = "com.android.settings.applications.appinfo.ExternalSourcesDetails";
        }
        setTheme(getOwnerTheme());
        super.onCreate(bundle);
        Log.d("SettingsActivity", "Starting onCreate");
        if (redirectStorageManagerActivity(bundle)) {
            Log.d("SettingsActivity", "redirect to security center StorageActivity");
            return;
        }
        prepareWindow();
        System.currentTimeMillis();
        this.mDashboardFeatureProvider = FeatureFactory.getFactory(this).getDashboardFeatureProvider(this);
        if (intent.hasExtra("settings:ui_options")) {
            getWindow().setUiOptions(intent.getIntExtra("settings:ui_options", 0));
        }
        if ((this instanceof SubSettings) || intent.getBooleanExtra(":settings:show_fragment_as_subsetting", false)) {
            WizardManagerHelper.isAnySetupWizard(getIntent());
        }
        setContentView(R.layout.settings_main_prefs);
        if (SettingsFeatures.isSplitTablet(this)) {
            splitIfNeeded(bundle);
        }
        MiuiUtils.setNavigationBackground(this, MiuiUtils.isInFullWindowGestureMode(getApplicationContext()));
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (bundle != null) {
            setTitleFromIntent(intent);
            ArrayList parcelableArrayList = bundle.getParcelableArrayList(":settings:categories");
            if (parcelableArrayList != null) {
                this.mCategories.clear();
                this.mCategories.addAll(parcelableArrayList);
                setTitleFromBackStack();
            }
        } else if (needToLaunchSettingsFragment()) {
            launchSettingFragment(this.initialFragmentName, intent);
        }
        WizardManagerHelper.isAnySetupWizard(getIntent());
        View findViewById2 = findViewById(R.id.switch_bar);
        if (findViewById2 instanceof SettingsMainSwitchBar) {
            this.mMainSwitch = (SettingsMainSwitchBar) findViewById2;
        }
        SettingsMainSwitchBar settingsMainSwitchBar = this.mMainSwitch;
        if (settingsMainSwitchBar != null) {
            settingsMainSwitchBar.setMetricsTag(getMetricsTag());
            this.mMainSwitch.setTranslationZ(findViewById(R.id.main_content).getTranslationZ() + 1.0f);
        }
        if (intent.getBooleanExtra("extra_prefs_show_button_bar", false) && (findViewById = findViewById(R.id.button_bar)) != null) {
            findViewById.setVisibility(0);
            Button button = (Button) findViewById(R.id.back_button);
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.SettingsActivity$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SettingsActivity.this.lambda$onCreate$1(view);
                }
            });
            Button button2 = (Button) findViewById(R.id.skip_button);
            button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.SettingsActivity$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SettingsActivity.this.lambda$onCreate$2(view);
                }
            });
            Button button3 = (Button) findViewById(R.id.next_button);
            this.mNextButton = button3;
            button3.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.SettingsActivity$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SettingsActivity.this.lambda$onCreate$3(view);
                }
            });
            if (intent.hasExtra("extra_prefs_set_next_text")) {
                String stringExtra2 = intent.getStringExtra("extra_prefs_set_next_text");
                if (TextUtils.isEmpty(stringExtra2)) {
                    this.mNextButton.setVisibility(8);
                } else {
                    this.mNextButton.setText(stringExtra2);
                }
            }
            if (intent.hasExtra("extra_prefs_set_back_text")) {
                String stringExtra3 = intent.getStringExtra("extra_prefs_set_back_text");
                if (TextUtils.isEmpty(stringExtra3)) {
                    button.setVisibility(8);
                } else {
                    button.setText(stringExtra3);
                }
            }
            if (intent.getBooleanExtra("extra_prefs_show_skip", false)) {
                button2.setVisibility(0);
            }
        }
        if (this.mTrimMemoryUtils == null) {
            TrimMemoryUtils trimMemoryUtils = new TrimMemoryUtils();
            this.mTrimMemoryUtils = trimMemoryUtils;
            trimMemoryUtils.addIdleHandler();
        }
        this.mMemoryOptimizationUtil = new MemoryOptimizationUtil();
        if (SettingsFeatures.isSplitTablet(this)) {
            this.mMiuiCustSplitUtilsImpl = new MiuiCustSplitUtilsImpl(this);
            ActionBar appCompatActionBar = getAppCompatActionBar();
            if (appCompatActionBar != null) {
                if (this.mMiuiCustSplitUtilsImpl.isSecondStageActivity()) {
                    appCompatActionBar.setDisplayHomeAsUpEnabled(false);
                }
                appCompatActionBar.setExpandState(0);
                appCompatActionBar.setResizable(false);
            }
        }
    }

    @Override // com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mDevelopmentSettingsListener);
        this.mDevelopmentSettingsListener = null;
        unregisterReceiver(this.mBatteryInfoReceiver);
        unprepareWindow();
    }

    @Override // androidx.preference.PreferenceFragmentCompat.OnPreferenceStartFragmentCallback
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat preferenceFragmentCompat, Preference preference) {
        new SubSettingLauncher(this).setDestination(preference.getFragment()).setArguments(preference.getExtras()).setSourceMetricsCategory(preferenceFragmentCompat instanceof Instrumentable ? ((Instrumentable) preferenceFragmentCompat).getMetricsCategory() : 0).setTitleRes(-1).setTitleText(preference.getTitle()).launch();
        return true;
    }

    @Override // androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        this.mDevelopmentSettingsListener = new BroadcastReceiver() { // from class: com.android.settings.SettingsActivity.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                SettingsActivity.this.updateTilesList();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mDevelopmentSettingsListener, new IntentFilter("com.android.settingslib.development.DevelopmentSettingsEnabler.SETTINGS_CHANGED"));
        registerReceiver(this.mBatteryInfoReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        updateTilesList();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        saveState(bundle);
        bundle.putBoolean("config_change_flag", true);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        MemoryOptimizationUtil memoryOptimizationUtil = this.mMemoryOptimizationUtil;
        if (memoryOptimizationUtil != null) {
            memoryOptimizationUtil.bindMemoryOptimizationService(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        TrimMemoryUtils trimMemoryUtils = this.mTrimMemoryUtils;
        if (trimMemoryUtils != null) {
            trimMemoryUtils.removeIdleHandler();
            this.mTrimMemoryUtils = null;
        }
        MemoryOptimizationUtil memoryOptimizationUtil = this.mMemoryOptimizationUtil;
        if (memoryOptimizationUtil != null) {
            memoryOptimizationUtil.startMemoryOptimization(this);
        }
    }

    void saveState(Bundle bundle) {
        if (this.mCategories.size() > 0) {
            bundle.putParcelableArrayList(":settings:categories", this.mCategories);
        }
    }

    @Override // android.app.Activity
    public void setTaskDescription(ActivityManager.TaskDescription taskDescription) {
        taskDescription.setIcon(Icon.createWithResource(this, R.drawable.ic_launcher_settings));
        super.setTaskDescription(taskDescription);
    }

    protected void splitIfNeeded(Bundle bundle) {
    }

    @Deprecated
    public void startPreferencePanel(Fragment fragment, String str, Bundle bundle, int i, CharSequence charSequence, Fragment fragment2, int i2) {
        new SubSettingLauncher(this).setDestination(str).setTitleRes(i).setArguments(bundle).setResultListener(fragment2, i2).launch();
    }
}
