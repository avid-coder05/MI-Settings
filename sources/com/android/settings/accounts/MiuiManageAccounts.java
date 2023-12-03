package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.content.SyncInfo;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.location.MiuiLocationSettings;
import com.android.settings.search.SearchUpdater;
import java.util.ArrayList;
import java.util.List;
import miui.content.ExtraIntent;
import miui.provider.Notes;

/* loaded from: classes.dex */
public class MiuiManageAccounts extends MiuiAccountPreferenceBase {
    private String mAccountType;
    private String[] mAuthorities;
    private TextView mErrorInfoView;
    private Account mFirstAccount;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class FragmentStarter implements Preference.OnPreferenceClickListener {
        private final String mClass;
        private final int mTitleRes;

        public FragmentStarter(String str, int i) {
            this.mClass = str;
            this.mTitleRes = i;
        }

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            MiuiUtils.startPreferencePanel(MiuiManageAccounts.this.getActivity(), this.mClass, null, this.mTitleRes, null, null, 0);
            if (this.mClass.equals(MiuiLocationSettings.class.getName())) {
                MiuiManageAccounts.this.getActivity().sendBroadcast(new Intent("com.android.settings.accounts.LAUNCHING_LOCATION_SETTINGS"), "android.permission.WRITE_SECURE_SETTINGS");
                return true;
            }
            return true;
        }
    }

    private void addAuthenticatorSettings() {
        PreferenceScreen addPreferencesForType = addPreferencesForType(this.mAccountType, getPreferenceScreen());
        if (addPreferencesForType != null) {
            updatePreferenceIntents(addPreferencesForType);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSafeIntent(PackageManager packageManager, Intent intent) {
        String str;
        AuthenticatorDescription accountTypeDescription = this.mAuthenticatorHelper.getAccountTypeDescription(this.mAccountType);
        ResolveInfo resolveActivityAsUser = packageManager.resolveActivityAsUser(intent, 0, this.mUserHandle.getIdentifier());
        if (resolveActivityAsUser == null) {
            return false;
        }
        ActivityInfo activityInfo = resolveActivityAsUser.activityInfo;
        ApplicationInfo applicationInfo = activityInfo.applicationInfo;
        try {
            if (activityInfo.exported && ((str = activityInfo.permission) == null || packageManager.checkPermission(str, accountTypeDescription.packageName) == 0)) {
                return true;
            }
            return applicationInfo.uid == packageManager.getApplicationInfo(accountTypeDescription.packageName, 0).uid;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("AccountSettings", "Intent considered unsafe due to exception.", e);
            return false;
        }
    }

    private boolean isSyncEnabled(int i, Account account, String str) {
        return ContentResolver.getSyncAutomaticallyAsUser(account, str, i) && ContentResolver.getMasterSyncAutomaticallyAsUser(i) && ContentResolver.getIsSyncableAsUser(account, str, i) > 0;
    }

    private boolean isSyncing(List<SyncInfo> list, Account account, String str) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            SyncInfo syncInfo = list.get(i);
            if (syncInfo.account.equals(account) && syncInfo.authority.equals(str)) {
                return true;
            }
        }
        return false;
    }

    private void requestOrCancelSyncForAccounts(boolean z) {
        int identifier = this.mUserHandle.getIdentifier();
        SyncAdapterType[] syncAdapterTypesAsUser = ContentResolver.getSyncAdapterTypesAsUser(identifier);
        Bundle bundle = new Bundle();
        bundle.putBoolean("force", true);
        int preferenceCount = getPreferenceScreen().getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if (preference instanceof AccountPreference) {
                Account account = ((AccountPreference) preference).getAccount();
                for (int i2 = 0; i2 < syncAdapterTypesAsUser.length; i2++) {
                    SyncAdapterType syncAdapterType = syncAdapterTypesAsUser[i2];
                    if (syncAdapterTypesAsUser[i2].accountType.equals(this.mAccountType) && ContentResolver.getSyncAutomaticallyAsUser(account, syncAdapterType.authority, identifier)) {
                        if (z) {
                            ContentResolver.requestSyncAsUser(account, syncAdapterType.authority, identifier, bundle);
                        } else {
                            ContentResolver.cancelSyncAsUser(account, syncAdapterType.authority, identifier);
                        }
                    }
                }
            }
        }
    }

    public static boolean showAccount(Context context, String str) {
        String[] stringArray = context.getResources().getStringArray(R.array.hide_account_list);
        if (stringArray != null && stringArray.length != 0) {
            for (String str2 : stringArray) {
                if (str2.equals(str)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void showAccountsIfNeeded() {
        String str;
        if (getActivity() == null) {
            return;
        }
        Account[] accountsAsUser = AccountManager.get(getActivity()).getAccountsAsUser(this.mUserHandle.getIdentifier());
        getPreferenceScreen().removeAll();
        this.mFirstAccount = null;
        addPreferencesFromResource(R.xml.manage_accounts_settings);
        for (Account account : accountsAsUser) {
            if (showAccount(getActivity(), account.type) && ((str = this.mAccountType) == null || account.type.equals(str))) {
                ArrayList authoritiesForAccountType = getAuthoritiesForAccountType(account.type);
                String[] strArr = this.mAuthorities;
                boolean z = true;
                if (strArr != null && authoritiesForAccountType != null) {
                    int length = strArr.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            z = false;
                            break;
                        } else if (authoritiesForAccountType.contains(strArr[i])) {
                            break;
                        } else {
                            i++;
                        }
                    }
                }
                if (z) {
                    Drawable drawableForType = getDrawableForType(account.type);
                    getPreferenceScreen().addPreference(("com.xiaomi".equals(account.type) || ExtraIntent.XIAOMI_ACCOUNT_TYPE_UNACTIVATED.equals(account.type)) ? new XiaomiAccountPreference(getPrefContext(), account, drawableForType, authoritiesForAccountType) : new AccountPreference(getPrefContext(), account, drawableForType, authoritiesForAccountType, false));
                    if (this.mFirstAccount == null) {
                        this.mFirstAccount = account;
                        invalidateOptionsMenu();
                    }
                }
            }
        }
        if (this.mAccountType == null || this.mFirstAccount == null) {
            getActivity().finish();
        } else {
            addAuthenticatorSettings();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:40:0x00bc  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void showSyncState() {
        /*
            Method dump skipped, instructions count: 343
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.accounts.MiuiManageAccounts.showSyncState():void");
    }

    private void startAccountSettings(AccountPreference accountPreference) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("account", accountPreference.getAccount());
        bundle.putParcelable("android.intent.extra.USER", this.mUserHandle);
        Log.i(" TC ", " startAccountSettings");
        MiuiUtils.startPreferencePanel(getActivity(), AccountSyncSettings.class.getCanonicalName(), bundle, R.string.account_sync_settings_title, accountPreference.getAccount().name, this, 1);
    }

    private void updatePreferenceIntents(PreferenceGroup preferenceGroup) {
        final PackageManager packageManager = getActivity().getPackageManager();
        int i = 0;
        while (i < preferenceGroup.getPreferenceCount()) {
            Preference preference = preferenceGroup.getPreference(i);
            if (preference instanceof PreferenceGroup) {
                updatePreferenceIntents((PreferenceGroup) preference);
            }
            Intent intent = preference.getIntent();
            if (intent != null) {
                if (intent.getAction() != null && intent.getAction().equals("android.settings.LOCATION_SOURCE_SETTINGS")) {
                    preference.setOnPreferenceClickListener(new FragmentStarter(MiuiLocationSettings.class.getName(), R.string.location_settings_title));
                } else if (packageManager.resolveActivityAsUser(intent, SearchUpdater.GOOGLE, this.mUserHandle.getIdentifier()) == null) {
                    preferenceGroup.removePreference(preference);
                } else {
                    intent.putExtra("account", this.mFirstAccount);
                    intent.setFlags(intent.getFlags() | 268435456);
                    preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.accounts.MiuiManageAccounts.1
                        @Override // androidx.preference.Preference.OnPreferenceClickListener
                        public boolean onPreferenceClick(Preference preference2) {
                            Intent intent2 = preference2.getIntent();
                            if (MiuiManageAccounts.this.isSafeIntent(packageManager, intent2)) {
                                MiuiManageAccounts.this.getActivity().startActivityAsUser(intent2, MiuiManageAccounts.this.mUserHandle);
                                return true;
                            }
                            Log.e("AccountSettings", "Refusing to launch authenticator intent because it exploits Settings permissions: " + intent2);
                            return true;
                        }
                    });
                }
            }
            i++;
        }
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase
    public /* bridge */ /* synthetic */ PreferenceScreen addPreferencesForType(String str, PreferenceScreen preferenceScreen) {
        return super.addPreferencesForType(str, preferenceScreen);
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase
    public /* bridge */ /* synthetic */ ArrayList getAuthoritiesForAccountType(String str) {
        return super.getAuthoritiesForAccountType(str);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 11;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiManageAccounts.class.getName();
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase, com.android.settingslib.accounts.AuthenticatorHelper.OnAccountsUpdateListener
    public void onAccountsUpdate(UserHandle userHandle) {
        showAccountsIfNeeded();
        onSyncStateUpdated();
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase, com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        FragmentActivity activity = getActivity();
        TextView textView = (TextView) getView().findViewById(R.id.sync_settings_error_info);
        this.mErrorInfoView = textView;
        textView.setVisibility(8);
        this.mAuthorities = activity.getIntent().getStringArrayExtra("authorities");
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey("account_label")) {
            return;
        }
        getActivity().setTitle(arguments.getString("account_label"));
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase
    protected void onAuthDescriptionsUpdated() {
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if (preference instanceof AccountPreference) {
                AccountPreference accountPreference = (AccountPreference) preference;
                accountPreference.setSummary(getLabelForType(accountPreference.getAccount().type));
            }
        }
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(Notes.Account.ACCOUNT_TYPE)) {
            this.mAccountType = arguments.getString(Notes.Account.ACCOUNT_TYPE);
        }
        addPreferencesFromResource(R.xml.manage_accounts_settings);
        setHasOptionsMenu(true);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.add(0, 1, 0, getString(R.string.sync_menu_sync_now)).setIcon(R.drawable.ic_menu_refresh_holo_dark);
        menu.add(0, 2, 0, getString(R.string.sync_menu_sync_cancel)).setIcon(17301560);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.manage_accounts_screen, viewGroup, false);
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(R.id.prefs_container);
        Utils.prepareCustomPreferencesList(viewGroup, inflate, viewGroup2, false);
        viewGroup2.addView(super.onCreateView(layoutInflater, viewGroup2, bundle));
        return inflate;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            requestOrCancelSyncForAccounts(true);
            return true;
        } else if (itemId != 2) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            requestOrCancelSyncForAccounts(false);
            return true;
        }
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mAuthenticatorHelper.stopListeningToAccountUpdates();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (!(preference instanceof XiaomiAccountPreference) && (preference instanceof AccountPreference)) {
            startAccountSettings((AccountPreference) preference);
            return true;
        }
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        boolean z = !ContentResolver.getCurrentSyncsAsUser(this.mUserHandle.getIdentifier()).isEmpty();
        menu.findItem(1).setVisible(!z);
        menu.findItem(2).setVisible(z);
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mAuthenticatorHelper.listenToAccountUpdates();
        updateAuthDescriptions();
        showAccountsIfNeeded();
        showSyncState();
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase
    protected void onSyncStateUpdated() {
        showSyncState();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
        }
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase
    public /* bridge */ /* synthetic */ void updateAuthDescriptions() {
        super.updateAuthDescriptions();
    }
}
