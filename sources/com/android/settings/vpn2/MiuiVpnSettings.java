package com.android.settings.vpn2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.security.LegacyVpnProfileStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnProfile;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R;
import com.android.settings.report.InternationalCompat;
import com.android.settings.search.SearchUpdater;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.vpn2.MiuiVpnSettings;
import com.miui.enterprise.RestrictionsHelper;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import miui.yellowpage.YellowPageContract;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class MiuiVpnSettings extends VpnSettings implements Preference.OnPreferenceChangeListener {
    private static boolean isSecure;
    private ConfigureKeyGuardDialog mConfigureKeyGuardDialog;
    private boolean mDialogShow;
    private boolean mIsLegacyVpnSelected;
    private boolean mIsThridPartyVpnSelected;
    private LockPatternUtils mLockPatternUtils;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.vpn2.MiuiVpnSettings.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            MiuiVpnSettings.this.updateVpnState(intent.getBooleanExtra("vpn_on", false));
        }
    };
    private AppPreference mSelecedThirdPartyAppPreference;
    private LegacyVpnPreference mSelectedPreference;
    private String mSelectedPreferenceKey;
    private boolean mUnavailable;
    private UserManager mUserManager;
    private CheckBoxPreference mVpnEnable;
    private VpnManager mVpnProxyManager;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class ConfigureKeyGuardDialog implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener, DialogInterface.OnShowListener {
        private boolean mConfigureConfirmed;
        private View mDecorView;
        private AlertDialog mDialog;
        private WeakReference<MiuiVpnSettings> mOuter;

        private ConfigureKeyGuardDialog(MiuiVpnSettings miuiVpnSettings) {
            WeakReference<MiuiVpnSettings> weakReference = new WeakReference<>(miuiVpnSettings);
            this.mOuter = weakReference;
            if (weakReference.get() != null) {
                AlertDialog create = new AlertDialog.Builder(this.mOuter.get().getActivity()).setTitle(R.string.vpn_set_screen_lock_title).setIconAttribute(16843605).setMessage(R.string.vpn_set_screen_lock_content).setPositiveButton(17039370, this).setNegativeButton(17039360, this).create();
                this.mDialog = create;
                create.setOnDismissListener(this);
                this.mDialog.setCancelable(false);
                this.mDialog.setOnShowListener(this);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$show$0() {
            if (this.mDecorView.getParent() == null) {
                Window window = this.mDialog.getWindow();
                View view = this.mDecorView;
                window.addContentView(view, view.getLayoutParams());
            }
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            this.mConfigureConfirmed = i == -1;
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialogInterface) {
            MiuiVpnSettings.this.mDialogShow = false;
            MiuiVpnSettings miuiVpnSettings = this.mOuter.get();
            if (miuiVpnSettings == null) {
                return;
            }
            if (!this.mConfigureConfirmed) {
                if (SettingsFeatures.isSplitTablet(MiuiVpnSettings.this.getContext()) || miuiVpnSettings.getActivity() == null) {
                    return;
                }
                miuiVpnSettings.finish();
                return;
            }
            this.mConfigureConfirmed = false;
            Bundle bundle = new Bundle();
            bundle.putInt("minimum_quality", SearchUpdater.GOOGLE);
            if (miuiVpnSettings.getContext() != null) {
                MiuiVpnSettings.this.startFragment(miuiVpnSettings, "com.android.settings.MiuiSecurityChooseUnlock$MiuiSecurityChooseUnlockFragment", 100, bundle);
            }
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialogInterface) {
            this.mDecorView = this.mDialog.getWindow().getDecorView();
        }

        public void show(boolean z) {
            MiuiVpnSettings.this.mDialogShow = z;
            if (!z) {
                this.mDialog.dismiss();
                return;
            }
            this.mDialog.show();
            View view = this.mDecorView;
            if (view != null) {
                view.post(new Runnable() { // from class: com.android.settings.vpn2.MiuiVpnSettings$ConfigureKeyGuardDialog$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        MiuiVpnSettings.ConfigureKeyGuardDialog.this.lambda$show$0();
                    }
                });
            }
        }
    }

    private void closeAllVpnConnections() {
        String str = getContext().getResources().getStringArray(R.array.vpn_states)[3];
        try {
            LegacyVpnInfo legacyVpnInfo = this.mVpnProxyManager.getLegacyVpnInfo(UserHandle.myUserId());
            if (legacyVpnInfo != null) {
                this.mVpnProxyManager.disconnect(legacyVpnInfo.key);
            }
        } catch (Exception e) {
            Log.e("MiuiVpnSettings", "Error when disconnect vpn" + e);
        }
        for (int i = 0; i < this.mVpnCategory.getPreferenceCount(); i++) {
            Preference preference = this.mVpnCategory.getPreference(i);
            if (preference.getSummary() != null && str.equalsIgnoreCase(preference.getSummary().toString()) && (preference instanceof AppPreference)) {
                openThirdPartyVpnApp((AppPreference) preference);
            }
        }
    }

    private void initSelectedPreference() {
        LegacyVpnPreference legacyVpnPreference = this.mSelectedPreference;
        if (legacyVpnPreference != null) {
            legacyVpnPreference.setChecked(false);
        }
        this.mIsLegacyVpnSelected = false;
        this.mIsThridPartyVpnSelected = false;
        this.mSelectedPreference = null;
        this.mSelecedThirdPartyAppPreference = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openThirdPartyVpnApp(AppPreference appPreference) {
        try {
            UserHandle of = UserHandle.of(appPreference.getUserId());
            Context createPackageContextAsUser = getActivity().createPackageContextAsUser(getActivity().getPackageName(), 0, of);
            Intent launchIntentForPackage = createPackageContextAsUser.getPackageManager().getLaunchIntentForPackage(appPreference.getPackageName());
            if (launchIntentForPackage != null) {
                createPackageContextAsUser.startActivityAsUser(launchIntentForPackage, of);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("MiuiVpnSettings", "VPN provider does not exist: " + appPreference.getPackageName(), e);
        }
    }

    private void recoverVpnSelected() {
        LegacyVpnPreference legacyVpnPreference;
        if (!this.mIsLegacyVpnSelected || (legacyVpnPreference = this.mSelectedPreference) == null) {
            return;
        }
        legacyVpnPreference.setChecked(true);
    }

    private void refreshVpnEnableButton(int i) {
        if (i == 1 || i == 2 || i == 3) {
            this.mVpnEnable.setChecked(true);
        } else {
            this.mVpnEnable.setChecked(false);
        }
    }

    private boolean selectLegacyVpn(VpnProfile vpnProfile) {
        initSelectedPreference();
        this.mIsLegacyVpnSelected = true;
        this.mIsThridPartyVpnSelected = false;
        LegacyVpnPreference legacyVpnPreference = this.mLegacyVpnPreferences.get(vpnProfile.key);
        this.mSelectedPreference = legacyVpnPreference;
        if (legacyVpnPreference != null) {
            legacyVpnPreference.setChecked(true);
        }
        MiuiVpnUtils.setConnectedVpnKey(getContext().getApplicationContext(), vpnProfile);
        return (TextUtils.isEmpty(vpnProfile.name) || TextUtils.isEmpty(vpnProfile.password)) ? false : true;
    }

    private boolean selectThirdPartyVpn(AppPreference appPreference) {
        initSelectedPreference();
        this.mIsLegacyVpnSelected = false;
        this.mIsThridPartyVpnSelected = true;
        this.mSelecedThirdPartyAppPreference = appPreference;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateVpnState(boolean z) {
        LegacyVpnInfo legacyVpnInfo;
        LegacyVpnPreference legacyVpnPreference = this.mLegacyVpnPreferences.get(MiuiVpnUtils.getConnectedVpnKey(getContext().getApplicationContext()));
        try {
            legacyVpnInfo = this.mVpnProxyManager.getLegacyVpnInfo(UserHandle.myUserId());
        } catch (Exception e) {
            Log.e("MiuiVpnSettings", "Error when updateVpnState" + e);
            legacyVpnInfo = null;
        }
        if (!z) {
            if (legacyVpnInfo != null) {
                this.mVpnProxyManager.disconnect(legacyVpnInfo.key);
            }
        } else if (legacyVpnPreference != null) {
            VpnProfile profile = legacyVpnPreference.getProfile();
            if (legacyVpnInfo == null || !legacyVpnInfo.key.equals(profile.key)) {
                try {
                    this.mVpnProxyManager.connect(profile, getActivity());
                } catch (Exception e2) {
                    Log.e("MiuiVpnSettings", "Error when connect vpn" + e2);
                }
            }
        }
    }

    private void vpnEnableStateChange(Preference preference, boolean z) {
        AppPreference appPreference;
        if (!z) {
            closeAllVpnConnections();
        } else if (this.mIsLegacyVpnSelected) {
            updateVpnState(true);
        } else if (!this.mIsThridPartyVpnSelected || (appPreference = this.mSelecedThirdPartyAppPreference) == null) {
            Toast.makeText(getActivity(), R.string.vpn_no_selected, 1).show();
        } else {
            openThirdPartyVpnApp(appPreference);
        }
    }

    protected void editProfile(VpnProfile vpnProfile) {
        editProfile(vpnProfile, false);
    }

    protected void editProfile(VpnProfile vpnProfile, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putByteArray(YellowPageContract.Profile.DIRECTORY, vpnProfile.encode());
        bundle.putString("profile_key", vpnProfile.key);
        bundle.putBoolean("profile_add", z);
        startFragment(this, MiuiVpnEditFragment.class.getName(), 101, bundle, 0);
    }

    @Override // com.android.settings.vpn2.VpnSettings, com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiVpnSettings.class.getName();
    }

    @Override // com.android.settings.vpn2.VpnSettings
    protected void initAllPreferenceSummary() {
        for (int i = 0; i < this.mVpnCategory.getPreferenceCount(); i++) {
            this.mVpnCategory.getPreference(i).setSummary("");
        }
    }

    protected void initVpnPreferenceList() {
        PreferenceCategory preferenceCategory = this.mVpnCategory;
        preferenceCategory.removeAll();
        this.mLegacyVpnPreferences.clear();
        this.mAppPreferences.clear();
        for (VpnProfile vpnProfile : VpnSettings.loadVpnProfiles()) {
            LegacyVpnPreference findOrCreatePreference = findOrCreatePreference(vpnProfile, true);
            findOrCreatePreference.setOnPreferenceClickListener(this);
            findOrCreatePreference.setEditListener(new View.OnClickListener() { // from class: com.android.settings.vpn2.MiuiVpnSettings.3
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    MiuiVpnSettings.this.editProfile((VpnProfile) view.getTag());
                }
            });
            this.mLegacyVpnPreferences.put(vpnProfile.key, findOrCreatePreference);
            Log.d("MiuiVpnSettings", "show vpn config, key = " + vpnProfile.key);
            preferenceCategory.addPreference(findOrCreatePreference);
        }
        Iterator<AppVpnInfo> it = VpnSettings.getVpnApps(getActivity(), true).iterator();
        while (it.hasNext()) {
            AppPreference findOrCreatePreference2 = findOrCreatePreference(it.next());
            findOrCreatePreference2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.vpn2.MiuiVpnSettings.4
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    MiuiVpnSettings.this.openThirdPartyVpnApp((AppPreference) preference);
                    return true;
                }
            });
            preferenceCategory.addPreference(findOrCreatePreference2);
        }
        if (getPreferenceScreen().findPreference("vpn_configure_category") == null && this.mVpnCategory.getPreferenceCount() > 0) {
            getPreferenceScreen().addPreference(this.mVpnCategory);
        }
        initSelectedPreference();
    }

    @Override // com.android.settings.RestrictedSettingsFragment, androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 100 || intent == null) {
            return;
        }
        onFragmentResult(i, intent.getExtras());
    }

    @Override // com.android.settings.vpn2.VpnSettings, com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        InternationalCompat.trackReportEvent("setting_Connection_VPN");
        this.mUserManager = (UserManager) getSystemService("user");
        this.mConfigureKeyGuardDialog = new ConfigureKeyGuardDialog(this);
        if (this.mUserManager.hasUserRestriction("no_config_vpn")) {
            this.mUnavailable = true;
            setPreferenceScreen(new PreferenceScreen(getPrefContext(), null));
            setHasOptionsMenu(false);
            return;
        }
        setHasOptionsMenu(true);
        this.mVpnProxyManager = new VpnManager(getActivity());
        this.mLockPatternUtils = new LockPatternUtils(getActivity());
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("vpn_enable");
        this.mVpnEnable = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this);
        this.mVpnCategory = (PreferenceCategory) findPreference("vpn_configure_category");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.vpn.SETTINGS");
        getActivity().registerReceiver(this.mReceiver, intentFilter);
        Settings.Secure.putInt(getActivity().getContentResolver(), "vpn_password_enable", 1);
    }

    @Override // androidx.fragment.app.Fragment, android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
    }

    @Override // com.android.settings.vpn2.VpnSettings, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (UserHandle.myUserId() != 0) {
            return;
        }
        menu.add(0, 1, 0, R.string.vpn_create).setIcon(R.drawable.action_button_new).setShowAsAction(1);
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        if (this.mUnavailable) {
            return;
        }
        this.mConfigureKeyGuardDialog.show(false);
        getActivity().unregisterReceiver(this.mReceiver);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public void onFragmentResult(int i, Bundle bundle) {
        if (this.mLegacyVpnPreferences.size() == 0) {
            initVpnPreferenceList();
        }
        if (i == 101) {
            byte[] byteArray = bundle.getByteArray(YellowPageContract.Profile.DIRECTORY);
            String string = bundle.getString("profile_key");
            boolean z = bundle.getBoolean("profile_delete", false);
            if (TextUtils.isEmpty(string) || byteArray == null || this.mLegacyVpnPreferences == null) {
                return;
            }
            VpnProfile decode = VpnProfile.decode(string, byteArray);
            LegacyVpnPreference legacyVpnPreference = this.mLegacyVpnPreferences.get(string);
            if (z) {
                if (legacyVpnPreference != null) {
                    this.mVpnProxyManager.disconnect(string);
                    this.mVpnCategory.removePreference(legacyVpnPreference);
                    this.mLegacyVpnPreferences.remove(string);
                    LegacyVpnProfileStore.remove("VPN_" + decode.key);
                    if (this.mLegacyVpnPreferences.size() == 0) {
                        MiuiVpnUtils.saveVpnConfiguredStatus(getContext(), 0);
                    }
                    Log.d("MiuiVpnSettings", "delete vpn config, key = " + string + ", and Legacy Vpn size is : " + this.mLegacyVpnPreferences.size());
                    return;
                }
                return;
            }
            LegacyVpnProfileStore.put("VPN_" + decode.key, decode.encode());
            if (legacyVpnPreference != null) {
                this.mVpnProxyManager.disconnect(decode.key);
                legacyVpnPreference.setState(AppPreference.STATE_DISCONNECTED);
            } else {
                LegacyVpnPreference findOrCreatePreference = findOrCreatePreference(decode, true);
                findOrCreatePreference.setEditListener(new View.OnClickListener() { // from class: com.android.settings.vpn2.MiuiVpnSettings.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        MiuiVpnSettings.this.editProfile((VpnProfile) view.getTag());
                    }
                });
                this.mVpnCategory.addPreference(findOrCreatePreference);
                if (MiuiVpnUtils.getConfiguredVpnStatus(getContext()) < 1) {
                    MiuiVpnUtils.saveVpnConfiguredStatus(getContext(), 1);
                }
                Log.d("MiuiVpnSettings", "add vpn config, key = " + decode.key);
            }
            this.mSelectedPreferenceKey = decode.key;
        }
    }

    @Override // com.android.settings.vpn2.VpnSettings, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 1) {
            long currentTimeMillis = System.currentTimeMillis();
            while (this.mLegacyVpnPreferences.containsKey(Long.toHexString(currentTimeMillis))) {
                currentTimeMillis++;
            }
            editProfile(new VpnProfile(Long.toHexString(currentTimeMillis)), true);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("vpn_enable".equals(preference.getKey())) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            if (SettingsFeatures.isSplitTablet(getContext())) {
                boolean z = isSecure;
                if (z) {
                    vpnEnableStateChange(preference, booleanValue);
                } else {
                    this.mConfigureKeyGuardDialog.show(!z);
                }
            } else {
                vpnEnableStateChange(preference, booleanValue);
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settings.vpn2.VpnSettings, androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (preference instanceof LegacyVpnPreference) {
            selectLegacyVpn(((LegacyVpnPreference) preference).getProfile());
        }
        if (preference instanceof AppPreference) {
            selectThirdPartyVpn((AppPreference) preference);
            return true;
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        preference.getKey();
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.vpn2.VpnSettings, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
    }

    @Override // com.android.settings.vpn2.VpnSettings, com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mUnavailable) {
            TextView textView = (TextView) getView().findViewById(16908292);
            if (textView != null) {
                setEmptyView(textView);
                textView.setText(R.string.vpn_settings_not_available);
            }
        } else if (UserHandle.myUserId() != 0) {
            getPreferenceScreen().removeAll();
        } else if (RestrictionsHelper.hasRestriction(getActivity(), "disallow_vpn")) {
            getPreferenceScreen().removeAll();
        } else {
            isSecure = this.mLockPatternUtils.isSecure(UserHandle.myUserId());
            Bundle bundle = getArguments() != null ? getArguments().getBundle("saved_bundle") : null;
            if (bundle != null) {
                this.mDialogShow = bundle.getBoolean("show_dialog");
            }
            this.mConfigureKeyGuardDialog.show((this.mDialogShow || !isSecure) && !SettingsFeatures.isSplitTablet(getContext()));
            initVpnPreferenceList();
            HashMap<String, LegacyVpnPreference> hashMap = this.mLegacyVpnPreferences;
            String str = this.mSelectedPreferenceKey;
            if (str == null) {
                str = MiuiVpnUtils.getConnectedVpnKey(getContext().getApplicationContext());
            }
            LegacyVpnPreference legacyVpnPreference = hashMap.get(str);
            if (legacyVpnPreference != null) {
                selectLegacyVpn(legacyVpnPreference.getProfile());
            }
            if (this.mVpnCategory.getPreferenceCount() == 0) {
                getPreferenceScreen().removePreference(this.mVpnCategory);
            } else if (getPreferenceScreen().findPreference("vpn_configure_category") == null) {
                getPreferenceScreen().addPreference(this.mVpnCategory);
            }
        }
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("show_dialog", this.mDialogShow);
        getArguments().putBundle("saved_bundle", bundle);
    }

    @Override // com.android.settings.vpn2.VpnSettings
    protected void refresh() {
        this.mSelectedPreferenceKey = null;
        recoverVpnSelected();
        refreshVpnEnableButton(this.mVpnProxyManager.getVpnConnectionStatus());
    }
}
