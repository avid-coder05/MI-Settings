package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Switch;
import androidx.preference.Preference;
import com.android.internal.widget.LockPatternUtils;
import com.android.security.AdbUtils;
import com.android.settings.R;
import com.android.settings.RegionUtils;
import com.android.settings.accessibility.AccessibilityServiceWarning;
import com.android.settings.cloud.AccessibilityDisableList;
import com.android.settings.password.ConfirmDeviceCredentialActivity;
import com.android.settings.recommend.PageIndexManager;
import com.android.settings.search.FunctionColumns;
import com.android.settings.search.tree.AccessibilitySettingsTree;
import com.android.settings.widget.SettingsMainSwitchPreference;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.android.settingslib.miuisettings.preference.CheckBoxPreference;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.os.Build;

/* loaded from: classes.dex */
public class ToggleAccessibilityServicePreferenceFragment extends ToggleFeaturePreferenceFragment implements Preference.OnPreferenceChangeListener {
    private CheckBoxPreference mAdditionalToggle;
    private boolean mBarrierFreeTop;
    private Dialog mDialog;
    private LockPatternUtils mLockPatternUtils;
    private BroadcastReceiver mPackageRemovedReceiver;
    private boolean mShowBarrierInterface;
    private AtomicBoolean mIsDialogShown = new AtomicBoolean(false);
    private final SettingsContentObserver mSettingsContentObserver = new SettingsContentObserver(new Handler()) { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment.1
        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            ToggleAccessibilityServicePreferenceFragment.this.updateSwitchBarToggleSwitch();
        }
    };

    private String createConfirmCredentialReasonMessage() {
        int i = R.string.enable_service_password_reason;
        int keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(UserHandle.myUserId());
        if (keyguardStoredPasswordQuality == 65536) {
            i = R.string.enable_service_pattern_reason;
        } else if (keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608) {
            i = R.string.enable_service_pin_reason;
        }
        return getString(i, getAccessibilityServiceInfo().getResolveInfo().loadLabel(getPackageManager()));
    }

    private Intent createUninstallPackageActivityIntent() {
        AccessibilityServiceInfo accessibilityServiceInfo = getAccessibilityServiceInfo();
        if (accessibilityServiceInfo == null) {
            Log.w("ToggleAccessibilityServicePreferenceFragment", "createUnInstallIntent -- invalid a11yServiceInfo");
            return null;
        }
        return new Intent("android.intent.action.UNINSTALL_PACKAGE", Uri.parse("package:" + accessibilityServiceInfo.getResolveInfo().serviceInfo.applicationInfo.packageName));
    }

    private void handleConfirmServiceEnabled(boolean z) {
        getArguments().putBoolean("checked", z);
        onPreferenceToggled(this.mPreferenceKey, z);
    }

    private boolean isAccessibilityServiceEnabled() {
        return AccessibilityUtils.getEnabledServicesFromSettings(getPrefContext()).contains(this.mComponentName);
    }

    private boolean isFullDiskEncrypted() {
        return StorageManager.isNonDefaultBlockEncrypted();
    }

    private boolean isServiceSupportAccessibilityButton() {
        ServiceInfo serviceInfo;
        for (AccessibilityServiceInfo accessibilityServiceInfo : ((AccessibilityManager) getPrefContext().getSystemService(AccessibilityManager.class)).getInstalledAccessibilityServiceList()) {
            if ((accessibilityServiceInfo.flags & 256) != 0 && (serviceInfo = accessibilityServiceInfo.getResolveInfo().serviceInfo) != null && TextUtils.equals(serviceInfo.name, getAccessibilityServiceInfo().getResolveInfo().serviceInfo.name)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onViewCreated$0(Preference preference, Object obj) {
        MiuiSettings.Secure.putBoolean(getContentResolver(), "talkback_watermark_enable", ((Boolean) obj).booleanValue());
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showPopupDialog$1(DialogInterface dialogInterface) {
        this.mIsDialogShown.compareAndSet(true, false);
    }

    private void onAllowButtonFromEnableToggleClicked() {
        if (isFullDiskEncrypted()) {
            startActivityForResult(ConfirmDeviceCredentialActivity.createIntent(createConfirmCredentialReasonMessage(), null), 1);
        } else {
            handleConfirmServiceEnabled(true);
            if (isServiceSupportAccessibilityButton()) {
                this.mIsDialogShown.set(false);
                showPopupDialog(PageIndexManager.PAGE_FACTORY_RESET);
            }
        }
        this.mDialog.dismiss();
    }

    private void onAllowButtonFromShortcutClicked() {
        this.mIsDialogShown.set(false);
        if (Build.IS_TABLET) {
            startAccessibilityShortcutTypeActivity();
        } else {
            showPopupDialog(1);
        }
        this.mDialog.dismiss();
    }

    private void onAllowButtonFromShortcutToggleClicked() {
        this.mShortcutPreference.setChecked(true);
        AccessibilityUtil.optInAllValuesToSettings(getPrefContext(), PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), this.mComponentName.flattenToString(), 1), this.mComponentName);
        this.mIsDialogShown.set(false);
        showPopupDialog(PageIndexManager.PAGE_FACTORY_RESET);
        Dialog dialog = this.mDialog;
        if (dialog != null && dialog.isShowing()) {
            this.mDialog.dismiss();
        }
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    private void onDenyButtonFromEnableToggleClicked() {
        handleConfirmServiceEnabled(false);
        this.mDialog.dismiss();
    }

    private void onDenyButtonFromShortcutClicked() {
        this.mDialog.dismiss();
    }

    private void onDenyButtonFromShortcutToggleClicked() {
        this.mShortcutPreference.setChecked(false);
        this.mDialog.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDialogButtonFromDisableToggleClicked(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            handleConfirmServiceEnabled(true);
        } else if (i != -1) {
            throw new IllegalArgumentException("Unexpected button identifier");
        } else {
            handleConfirmServiceEnabled(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDialogButtonFromEnableToggleClicked(View view) {
        int id = view.getId();
        if (id == R.id.permission_enable_allow_button) {
            onAllowButtonFromEnableToggleClicked();
        } else if (id != R.id.permission_enable_deny_button) {
            throw new IllegalArgumentException("Unexpected view id");
        } else {
            onDenyButtonFromEnableToggleClicked();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDialogButtonFromUninstallClicked() {
        this.mDialog.dismiss();
        Intent createUninstallPackageActivityIntent = createUninstallPackageActivityIntent();
        if (createUninstallPackageActivityIntent == null) {
            return;
        }
        startActivity(createUninstallPackageActivityIntent);
    }

    private void registerPackageRemoveReceiver() {
        if (this.mPackageRemovedReceiver != null || getContext() == null) {
            return;
        }
        this.mPackageRemovedReceiver = new BroadcastReceiver() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (TextUtils.equals(ToggleAccessibilityServicePreferenceFragment.this.mComponentName.getPackageName(), intent.getData().getSchemeSpecificPart())) {
                    ToggleAccessibilityServicePreferenceFragment.this.getActivity().finishAndRemoveTask();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme(FunctionColumns.PACKAGE);
        getContext().registerReceiver(this.mPackageRemovedReceiver, intentFilter);
    }

    private void showAppBarrierFreeTip(int i) {
        AccessibilityServiceInfo accessibilityServiceInfo = getAccessibilityServiceInfo();
        if (accessibilityServiceInfo == null || accessibilityServiceInfo.getComponentName() == null || TextUtils.isEmpty(accessibilityServiceInfo.getComponentName().getPackageName()) || "com.google.android.marvin.talkback".equals(accessibilityServiceInfo.getComponentName().getPackageName())) {
            showPopupDialog(i);
            return;
        }
        Intent interceptIntent = AdbUtils.getInterceptIntent(accessibilityServiceInfo.getComponentName().getPackageName(), "miui_barrier_free", "");
        if (isServiceSupportAccessibilityButton() || !AdbUtils.isIntentEnable(getActivity(), interceptIntent)) {
            showPopupDialog(i);
            return;
        }
        this.mShowBarrierInterface = true;
        startActivityForResult(interceptIntent, 10);
    }

    private void showPopupDialog(int i) {
        if (!this.mIsDialogShown.compareAndSet(false, true) || this.mShowBarrierInterface) {
            return;
        }
        showDialog(i);
        setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                ToggleAccessibilityServicePreferenceFragment.this.lambda$showPopupDialog$1(dialogInterface);
            }
        });
    }

    private void unregisterPackageRemoveReceiver() {
        if (this.mPackageRemovedReceiver == null || getContext() == null) {
            return;
        }
        getContext().unregisterReceiver(this.mPackageRemovedReceiver);
        this.mPackageRemovedReceiver = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AccessibilityServiceInfo getAccessibilityServiceInfo() {
        List<AccessibilityServiceInfo> installedAccessibilityServiceList = AccessibilityManager.getInstance(getPrefContext()).getInstalledAccessibilityServiceList();
        int size = installedAccessibilityServiceList.size();
        for (int i = 0; i < size; i++) {
            AccessibilityServiceInfo accessibilityServiceInfo = installedAccessibilityServiceList.get(i);
            ResolveInfo resolveInfo = accessibilityServiceInfo.getResolveInfo();
            if (this.mComponentName.getPackageName().equals(resolveInfo.serviceInfo.packageName) && this.mComponentName.getClassName().equals(resolveInfo.serviceInfo.name)) {
                return accessibilityServiceInfo;
            }
        }
        return null;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public int getUserShortcutTypes() {
        return AccessibilityUtil.getUserShortcutTypesFromSettings(getPrefContext(), this.mComponentName);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1) {
            if (i2 != -1) {
                handleConfirmServiceEnabled(false);
                return;
            }
            handleConfirmServiceEnabled(true);
            if (isFullDiskEncrypted()) {
                this.mLockPatternUtils.clearEncryptionPassword();
                Settings.Global.putInt(getContentResolver(), "require_password_to_decrypt", 0);
            }
        } else if (i == 10) {
            this.mShowBarrierInterface = false;
            if (i2 != -1) {
                if (i2 == 1) {
                    showPopupDialog(1004);
                }
            } else if (this.mBarrierFreeTop) {
                handleConfirmServiceEnabled(true);
            } else {
                onAllowButtonFromShortcutToggleClicked();
            }
        }
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mLockPatternUtils = new LockPatternUtils(getPrefContext());
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        setCancelable(false);
        switch (i) {
            case 1002:
                AccessibilityServiceInfo accessibilityServiceInfo = getAccessibilityServiceInfo();
                if (accessibilityServiceInfo != null) {
                    this.mDialog = AccessibilityServiceWarning.createCapabilitiesDialog(getPrefContext(), accessibilityServiceInfo, new View.OnClickListener() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment$$ExternalSyntheticLambda4
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            ToggleAccessibilityServicePreferenceFragment.this.onDialogButtonFromEnableToggleClicked(view);
                        }
                    }, new AccessibilityServiceWarning.UninstallActionPerformer() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment$$ExternalSyntheticLambda6
                        @Override // com.android.settings.accessibility.AccessibilityServiceWarning.UninstallActionPerformer
                        public final void uninstallPackage() {
                            ToggleAccessibilityServicePreferenceFragment.this.onDialogButtonFromUninstallClicked();
                        }
                    });
                    break;
                } else {
                    return null;
                }
            case 1003:
                AccessibilityServiceInfo accessibilityServiceInfo2 = getAccessibilityServiceInfo();
                if (accessibilityServiceInfo2 != null) {
                    this.mDialog = AccessibilityServiceWarning.createCapabilitiesDialog(getPrefContext(), accessibilityServiceInfo2, new View.OnClickListener() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment$$ExternalSyntheticLambda2
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            ToggleAccessibilityServicePreferenceFragment.this.onDialogButtonFromShortcutClicked(view);
                        }
                    }, new AccessibilityServiceWarning.UninstallActionPerformer() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment$$ExternalSyntheticLambda6
                        @Override // com.android.settings.accessibility.AccessibilityServiceWarning.UninstallActionPerformer
                        public final void uninstallPackage() {
                            ToggleAccessibilityServicePreferenceFragment.this.onDialogButtonFromUninstallClicked();
                        }
                    });
                    break;
                } else {
                    return null;
                }
            case 1004:
                AccessibilityServiceInfo accessibilityServiceInfo3 = getAccessibilityServiceInfo();
                if (accessibilityServiceInfo3 != null) {
                    this.mDialog = AccessibilityServiceWarning.createCapabilitiesDialog(getPrefContext(), accessibilityServiceInfo3, new View.OnClickListener() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment$$ExternalSyntheticLambda3
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            ToggleAccessibilityServicePreferenceFragment.this.onDialogButtonFromShortcutToggleClicked(view);
                        }
                    }, new AccessibilityServiceWarning.UninstallActionPerformer() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment$$ExternalSyntheticLambda6
                        @Override // com.android.settings.accessibility.AccessibilityServiceWarning.UninstallActionPerformer
                        public final void uninstallPackage() {
                            ToggleAccessibilityServicePreferenceFragment.this.onDialogButtonFromUninstallClicked();
                        }
                    });
                    break;
                } else {
                    return null;
                }
            case 1005:
                AccessibilityServiceInfo accessibilityServiceInfo4 = getAccessibilityServiceInfo();
                if (accessibilityServiceInfo4 != null) {
                    this.mDialog = AccessibilityServiceWarning.createDisableDialog(getPrefContext(), accessibilityServiceInfo4, new DialogInterface.OnClickListener() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment$$ExternalSyntheticLambda0
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i2) {
                            ToggleAccessibilityServicePreferenceFragment.this.onDialogButtonFromDisableToggleClicked(dialogInterface, i2);
                        }
                    });
                    break;
                } else {
                    return null;
                }
            default:
                this.mDialog = super.onCreateDialog(i);
                break;
        }
        return this.mDialog;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDialogButtonFromShortcutClicked(View view) {
        int id = view.getId();
        if (id == R.id.permission_enable_allow_button) {
            onAllowButtonFromShortcutClicked();
        } else if (id != R.id.permission_enable_deny_button) {
            throw new IllegalArgumentException("Unexpected view id");
        } else {
            onDenyButtonFromShortcutClicked();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDialogButtonFromShortcutToggleClicked(View view) {
        int id = view.getId();
        if (id == R.id.permission_enable_allow_button) {
            onAllowButtonFromShortcutToggleClicked();
        } else if (id != R.id.permission_enable_deny_button) {
            throw new IllegalArgumentException("Unexpected view id");
        } else {
            onDenyButtonFromShortcutToggleClicked();
        }
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mSettingsContentObserver.unregister(getContentResolver());
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (((Boolean) obj).booleanValue()) {
            this.mToggleServiceSwitchPreference.setChecked(false);
            getArguments().putBoolean("checked", false);
            if (this.mShortcutPreference.isChecked()) {
                handleConfirmServiceEnabled(true);
                if (isServiceSupportAccessibilityButton()) {
                    showPopupDialog(PageIndexManager.PAGE_FACTORY_RESET);
                }
            } else if (RegionUtils.IS_JP_KDDI) {
                showPopupDialog(1002);
            } else {
                this.mBarrierFreeTop = true;
                showAppBarrierFreeTip(1002);
            }
        } else {
            this.mToggleServiceSwitchPreference.setChecked(true);
            getArguments().putBoolean("checked", true);
            showDialog(1005);
        }
        return true;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void onPreferenceToggled(String str, boolean z) {
        ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
        AccessibilityStatsLogUtils.logAccessibilityServiceEnabled(unflattenFromString, z);
        AccessibilityUtils.setAccessibilityServiceState(getPrefContext(), unflattenFromString, z);
        ComponentName unflattenFromString2 = ComponentName.unflattenFromString(AccessibilitySettingsTree.SETTINGS_ACCESSIBILITY_ACCESSIBILITYMENU);
        ComponentName unflattenFromString3 = ComponentName.unflattenFromString("com.google.android.marvin.talkback/com.google.android.accessibility.accessibilitymenu.AccessibilityMenuService");
        if (unflattenFromString2.equals(this.mComponentName) && z) {
            Set<ComponentName> enabledServicesFromSettings = AccessibilityUtils.getEnabledServicesFromSettings(getContext());
            if (!enabledServicesFromSettings.isEmpty() && enabledServicesFromSettings.contains(unflattenFromString3)) {
                AccessibilityUtils.setAccessibilityServiceState(getActivity(), unflattenFromString3, false);
            }
        }
        MiStatInterfaceUtils.trackSwitchEvent(str, z);
        OneTrackInterfaceUtils.trackSwitchEvent(str, z);
        CheckBoxPreference checkBoxPreference = this.mAdditionalToggle;
        if (checkBoxPreference != null) {
            checkBoxPreference.setEnabled(z);
        }
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void onProcessArguments(Bundle bundle) {
        ComponentName unflattenFromString;
        super.onProcessArguments(bundle);
        String string = bundle.getString("settings_title");
        String string2 = bundle.getString("settings_component_name");
        if (!TextUtils.isEmpty(string) && !TextUtils.isEmpty(string2)) {
            Intent component = new Intent("android.intent.action.MAIN").setComponent(ComponentName.unflattenFromString(string2.toString()));
            if (!getPackageManager().queryIntentActivities(component, 0).isEmpty()) {
                this.mSettingsTitle = string;
                this.mSettingsIntent = component;
                setHasOptionsMenu(true);
            }
        }
        this.mComponentName = (ComponentName) bundle.getParcelable("component_name");
        int i = bundle.getInt("animated_image_res");
        if (i > 0) {
            this.mImageUri = new Uri.Builder().scheme("android.resource").authority(this.mComponentName.getPackageName()).appendPath(String.valueOf(i)).build();
        }
        if (getAccessibilityServiceInfo() != null) {
            this.mPackageName = getAccessibilityServiceInfo().getResolveInfo().loadLabel(getPackageManager());
        }
        if (this.mComponentName == null) {
            finish();
        }
        Set<String> cacheDisableSet = AccessibilityDisableList.getCacheDisableSet(getActivity());
        if (!TextUtils.isEmpty(this.mPreferenceKey) && (unflattenFromString = ComponentName.unflattenFromString(this.mPreferenceKey)) != null && cacheDisableSet.contains(unflattenFromString.getPackageName())) {
            getActivity().finish();
            return;
        }
        ComponentName componentName = this.mComponentName;
        if (componentName == null || !cacheDisableSet.contains(componentName.getPackageName())) {
            return;
        }
        getActivity().finish();
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateSwitchBarToggleSwitch();
        this.mSettingsContentObserver.register(getContentResolver());
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.accessibility.ShortcutPreference.OnClickCallback
    public void onSettingsClicked(ShortcutPreference shortcutPreference) {
        boolean z = this.mShortcutPreference.isChecked() || this.mToggleServiceSwitchPreference.isChecked();
        if (Build.IS_TABLET && z) {
            startAccessibilityShortcutTypeActivity();
        } else {
            showPopupDialog(z ? 1 : 1003);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        AccessibilityServiceInfo accessibilityServiceInfo = getAccessibilityServiceInfo();
        if (accessibilityServiceInfo == null) {
            getActivity().finishAndRemoveTask();
        } else if (AccessibilityUtil.isSystemApp(accessibilityServiceInfo)) {
        } else {
            registerPackageRemoveReceiver();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        unregisterPackageRemoveReceiver();
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        if (z != isAccessibilityServiceEnabled()) {
            onPreferenceChange(this.mToggleServiceSwitchPreference, Boolean.valueOf(z));
        }
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.accessibility.ShortcutPreference.OnClickCallback
    public void onToggleClicked(ShortcutPreference shortcutPreference) {
        int retrieveUserShortcutType = PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), this.mComponentName.flattenToString(), 1);
        if (!shortcutPreference.isChecked()) {
            AccessibilityUtil.optOutAllValuesFromSettings(getPrefContext(), retrieveUserShortcutType, this.mComponentName);
        } else if (this.mToggleServiceSwitchPreference.isChecked()) {
            AccessibilityUtil.optInAllValuesToSettings(getPrefContext(), retrieveUserShortcutType, this.mComponentName);
            showPopupDialog(PageIndexManager.PAGE_FACTORY_RESET);
        } else {
            shortcutPreference.setChecked(false);
            if (RegionUtils.IS_JP_KDDI) {
                showPopupDialog(1004);
            } else {
                this.mBarrierFreeTop = false;
                showAppBarrierFreeTip(1004);
            }
        }
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (ComponentName.unflattenFromString("com.google.android.marvin.talkback/com.google.android.marvin.talkback.TalkBackService").equals(this.mComponentName)) {
            if (this.mAdditionalToggle == null) {
                CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getPrefContext());
                this.mAdditionalToggle = checkBoxPreference;
                checkBoxPreference.setTitle(R.string.talkback_watermark_enable);
                this.mAdditionalToggle.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment$$ExternalSyntheticLambda5
                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        boolean lambda$onViewCreated$0;
                        lambda$onViewCreated$0 = ToggleAccessibilityServicePreferenceFragment.this.lambda$onViewCreated$0(preference, obj);
                        return lambda$onViewCreated$0;
                    }
                });
                this.mAdditionalToggle.setEnabled(false);
            }
            getPreferenceScreen().addPreference(this.mAdditionalToggle);
        }
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void updateSwitchBarToggleSwitch() {
        boolean isAccessibilityServiceEnabled = isAccessibilityServiceEnabled();
        CheckBoxPreference checkBoxPreference = this.mAdditionalToggle;
        if (checkBoxPreference != null) {
            checkBoxPreference.setEnabled(isAccessibilityServiceEnabled);
            this.mAdditionalToggle.setChecked(MiuiSettings.Secure.getBoolean(getContentResolver(), "talkback_watermark_enable", true));
        }
        if (this.mToggleServiceSwitchPreference.isChecked() == isAccessibilityServiceEnabled) {
            return;
        }
        this.mToggleServiceSwitchPreference.setChecked(isAccessibilityServiceEnabled);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void updateToggleServiceTitle(SettingsMainSwitchPreference settingsMainSwitchPreference) {
        AccessibilityServiceInfo accessibilityServiceInfo = getAccessibilityServiceInfo();
        settingsMainSwitchPreference.setTitle(accessibilityServiceInfo == null ? "" : getString(R.string.accessibility_service_primary_switch_title, accessibilityServiceInfo.getResolveInfo().loadLabel(getPackageManager())));
    }
}
