package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SyncAdapterType;
import android.content.SyncInfo;
import android.content.SyncStatusInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.UserInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.widget.FooterPreference;
import com.android.settingslib.widget.LayoutPreference;
import com.google.android.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import miui.content.res.IconCustomizer;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class AccountSyncSettings extends MiuiAccountPreferenceBase {
    private Account mAccount;
    private ArrayList<SyncAdapterType> mInvisibleAdapters = Lists.newArrayList();
    private HashMap<Integer, Integer> mUidRequestCodeMap = new HashMap<>();

    private boolean accountExists(Account account) {
        if (account == null) {
            return false;
        }
        for (Account account2 : AccountManager.get(getActivity()).getAccountsByTypeAsUser(account.type, this.mUserHandle)) {
            if (account2.equals(account)) {
                return true;
            }
        }
        return false;
    }

    private void addSyncStateSwitch(Account account, String str, String str2, int i) {
        MiuiSyncStateSwitchPreference miuiSyncStateSwitchPreference = (MiuiSyncStateSwitchPreference) getCachedPreference(str);
        if (miuiSyncStateSwitchPreference == null) {
            miuiSyncStateSwitchPreference = new MiuiSyncStateSwitchPreference(getPrefContext(), account, str, str2, i);
            getPreferenceScreen().addPreference(miuiSyncStateSwitchPreference);
        } else {
            miuiSyncStateSwitchPreference.setup(account, str, str2, i);
        }
        PackageManager packageManager = getPackageManager();
        miuiSyncStateSwitchPreference.setPersistent(false);
        ProviderInfo resolveContentProviderAsUser = packageManager.resolveContentProviderAsUser(str, 0, this.mUserHandle.getIdentifier());
        if (resolveContentProviderAsUser == null) {
            return;
        }
        CharSequence loadLabel = resolveContentProviderAsUser.loadLabel(packageManager);
        if (!TextUtils.isEmpty(loadLabel)) {
            miuiSyncStateSwitchPreference.setTitle(loadLabel);
            miuiSyncStateSwitchPreference.setKey(str);
            return;
        }
        Log.e("AccountSettings", "Provider needs a label for authority '" + str + "'");
    }

    private int addUidAndGenerateRequestCode(int i) {
        if (this.mUidRequestCodeMap.containsKey(Integer.valueOf(i))) {
            return this.mUidRequestCodeMap.get(Integer.valueOf(i)).intValue();
        }
        int size = this.mUidRequestCodeMap.size() + 1;
        this.mUidRequestCodeMap.put(Integer.valueOf(i), Integer.valueOf(size));
        return size;
    }

    private void cancelSyncForEnabledProviders() {
        requestOrCancelSyncForEnabledProviders(false);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
        }
    }

    private static String formatSyncDate(Context context, Date date) {
        return DateUtils.formatDateTime(context, date.getTime(), 21);
    }

    private int getRequestCodeByUid(int i) {
        if (this.mUidRequestCodeMap.containsKey(Integer.valueOf(i))) {
            return this.mUidRequestCodeMap.get(Integer.valueOf(i)).intValue();
        }
        return -1;
    }

    private boolean isSyncing(List<SyncInfo> list, Account account, String str) {
        for (SyncInfo syncInfo : list) {
            if (syncInfo.account.equals(account) && syncInfo.authority.equals(str)) {
                return true;
            }
        }
        return false;
    }

    private boolean requestAccountAccessIfNeeded(String str) {
        IntentSender createRequestAccountAccessIntentSenderAsUser;
        if (str == null) {
            return false;
        }
        try {
            int packageUidAsUser = getContext().getPackageManager().getPackageUidAsUser(str, this.mUserHandle.getIdentifier());
            AccountManager accountManager = (AccountManager) getContext().getSystemService(AccountManager.class);
            if (!accountManager.hasAccountAccess(this.mAccount, str, this.mUserHandle) && (createRequestAccountAccessIntentSenderAsUser = accountManager.createRequestAccountAccessIntentSenderAsUser(this.mAccount, str, this.mUserHandle)) != null) {
                try {
                    startIntentSenderForResult(createRequestAccountAccessIntentSenderAsUser, addUidAndGenerateRequestCode(packageUidAsUser), null, 0, 0, 0, null);
                    return true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e("AccountSettings", "Error requesting account access", e);
                }
            }
            return false;
        } catch (PackageManager.NameNotFoundException e2) {
            Log.e("AccountSettings", "Invalid sync ", e2);
            return false;
        }
    }

    private void requestOrCancelSync(Account account, String str, boolean z) {
        if (!z) {
            ContentResolver.cancelSyncAsUser(account, str, this.mUserHandle.getIdentifier());
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("force", true);
        ContentResolver.requestSyncAsUser(account, str, this.mUserHandle.getIdentifier(), bundle);
    }

    private void requestOrCancelSyncForEnabledProviders(boolean z) {
        int preferenceCount = getPreferenceScreen().getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if (preference instanceof MiuiSyncStateSwitchPreference) {
                MiuiSyncStateSwitchPreference miuiSyncStateSwitchPreference = (MiuiSyncStateSwitchPreference) preference;
                if (miuiSyncStateSwitchPreference.isChecked()) {
                    requestOrCancelSync(miuiSyncStateSwitchPreference.getAccount(), miuiSyncStateSwitchPreference.getAuthority(), z);
                }
            }
        }
        if (this.mAccount != null) {
            Iterator<SyncAdapterType> it = this.mInvisibleAdapters.iterator();
            while (it.hasNext()) {
                requestOrCancelSync(this.mAccount, it.next().authority, z);
            }
        }
    }

    private void setAccessibilityTitle() {
        UserInfo userInfo = ((UserManager) getSystemService("user")).getUserInfo(this.mUserHandle.getIdentifier());
        boolean isManagedProfile = userInfo != null ? userInfo.isManagedProfile() : false;
        CharSequence title = getActivity().getTitle();
        getActivity().setTitle(Utils.createAccessibleSequence(title, getString(isManagedProfile ? R.string.accessibility_work_account_title : R.string.accessibility_personal_account_title, title)));
    }

    private void setFeedsState() {
        int i;
        List<SyncInfo> list;
        int i2;
        boolean z;
        AccountSyncSettings accountSyncSettings = this;
        Date date = new Date();
        int identifier = accountSyncSettings.mUserHandle.getIdentifier();
        List<SyncInfo> currentSyncsAsUser = ContentResolver.getCurrentSyncsAsUser(identifier);
        updateAccountSwitches();
        int preferenceCount = getPreferenceScreen().getPreferenceCount();
        int i3 = 0;
        boolean z2 = false;
        while (i3 < preferenceCount) {
            Preference preference = getPreferenceScreen().getPreference(i3);
            if (preference instanceof MiuiSyncStateSwitchPreference) {
                MiuiSyncStateSwitchPreference miuiSyncStateSwitchPreference = (MiuiSyncStateSwitchPreference) preference;
                String authority = miuiSyncStateSwitchPreference.getAuthority();
                Account account = miuiSyncStateSwitchPreference.getAccount();
                SyncStatusInfo syncStatusAsUser = ContentResolver.getSyncStatusAsUser(account, authority, identifier);
                boolean syncAutomaticallyAsUser = ContentResolver.getSyncAutomaticallyAsUser(account, authority, identifier);
                boolean z3 = syncStatusAsUser == null ? false : syncStatusAsUser.pending;
                boolean z4 = syncStatusAsUser == null ? false : syncStatusAsUser.initialize;
                boolean isSyncing = accountSyncSettings.isSyncing(currentSyncsAsUser, account, authority);
                i = i3;
                boolean z5 = (syncStatusAsUser == null || syncStatusAsUser.lastFailureTime == 0 || syncStatusAsUser.getLastFailureMesgAsInt(0) == 1) ? false : true;
                if (!syncAutomaticallyAsUser) {
                    z5 = false;
                }
                if (z5 && !isSyncing && !z3) {
                    z2 = true;
                }
                if (Log.isLoggable("AccountSettings", 3)) {
                    StringBuilder sb = new StringBuilder();
                    list = currentSyncsAsUser;
                    sb.append("Update sync status: ");
                    sb.append(account);
                    sb.append(" ");
                    sb.append(authority);
                    sb.append(" active = ");
                    sb.append(isSyncing);
                    sb.append(" pend =");
                    sb.append(z3);
                    Log.d("AccountSettings", sb.toString());
                } else {
                    list = currentSyncsAsUser;
                }
                int i4 = preferenceCount;
                long j = syncStatusAsUser == null ? 0L : syncStatusAsUser.lastSuccessTime;
                if (!syncAutomaticallyAsUser) {
                    miuiSyncStateSwitchPreference.setSummary(R.string.sync_disabled);
                } else if (isSyncing) {
                    miuiSyncStateSwitchPreference.setSummary(R.string.sync_in_progress);
                } else {
                    if (j != 0) {
                        date.setTime(j);
                        i2 = i4;
                        z = false;
                        miuiSyncStateSwitchPreference.setSummary(getResources().getString(R.string.last_synced, formatSyncDate(getContext(), date)));
                    } else {
                        i2 = i4;
                        z = false;
                        miuiSyncStateSwitchPreference.setSummary("");
                    }
                    int isSyncableAsUser = ContentResolver.getIsSyncableAsUser(account, authority, identifier);
                    miuiSyncStateSwitchPreference.setActive((isSyncing || isSyncableAsUser < 0 || z4) ? z : true);
                    miuiSyncStateSwitchPreference.setPending((z3 || isSyncableAsUser < 0 || z4) ? z : true);
                    miuiSyncStateSwitchPreference.setFailed(z5);
                    boolean z6 = !ContentResolver.getMasterSyncAutomaticallyAsUser(identifier);
                    miuiSyncStateSwitchPreference.setOneTimeSyncMode(z6);
                    miuiSyncStateSwitchPreference.setChecked((!z6 || syncAutomaticallyAsUser) ? true : z);
                }
                i2 = i4;
                z = false;
                int isSyncableAsUser2 = ContentResolver.getIsSyncableAsUser(account, authority, identifier);
                miuiSyncStateSwitchPreference.setActive((isSyncing || isSyncableAsUser2 < 0 || z4) ? z : true);
                miuiSyncStateSwitchPreference.setPending((z3 || isSyncableAsUser2 < 0 || z4) ? z : true);
                miuiSyncStateSwitchPreference.setFailed(z5);
                boolean z62 = !ContentResolver.getMasterSyncAutomaticallyAsUser(identifier);
                miuiSyncStateSwitchPreference.setOneTimeSyncMode(z62);
                miuiSyncStateSwitchPreference.setChecked((!z62 || syncAutomaticallyAsUser) ? true : z);
            } else {
                list = currentSyncsAsUser;
                i2 = preferenceCount;
                i = i3;
            }
            i3 = i + 1;
            accountSyncSettings = this;
            preferenceCount = i2;
            currentSyncsAsUser = list;
        }
        if (z2) {
            getPreferenceScreen().addPreference(new FooterPreference.Builder(getActivity()).setTitle(R.string.sync_is_failing).build());
        }
    }

    private void startSyncForEnabledProviders() {
        requestOrCancelSyncForEnabledProviders(true);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
        }
    }

    private void updateAccountSwitches() {
        this.mInvisibleAdapters.clear();
        SyncAdapterType[] syncAdapterTypesAsUser = ContentResolver.getSyncAdapterTypesAsUser(this.mUserHandle.getIdentifier());
        ArrayList arrayList = new ArrayList();
        for (SyncAdapterType syncAdapterType : syncAdapterTypesAsUser) {
            if (syncAdapterType.accountType.equals(this.mAccount.type)) {
                if (syncAdapterType.isUserVisible()) {
                    if (Log.isLoggable("AccountSettings", 3)) {
                        Log.d("AccountSettings", "updateAccountSwitches: added authority " + syncAdapterType.authority + " to accountType " + syncAdapterType.accountType);
                    }
                    arrayList.add(syncAdapterType);
                } else {
                    this.mInvisibleAdapters.add(syncAdapterType);
                }
            }
        }
        if (Log.isLoggable("AccountSettings", 3)) {
            Log.d("AccountSettings", "looking for sync adapters that match account " + this.mAccount);
        }
        cacheRemoveAllPrefs(getPreferenceScreen());
        getCachedPreference("pref_app_header");
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            SyncAdapterType syncAdapterType2 = (SyncAdapterType) arrayList.get(i);
            int isSyncableAsUser = ContentResolver.getIsSyncableAsUser(this.mAccount, syncAdapterType2.authority, this.mUserHandle.getIdentifier());
            if (Log.isLoggable("AccountSettings", 3)) {
                Log.d("AccountSettings", "  found authority " + syncAdapterType2.authority + " " + isSyncableAsUser);
            }
            if (isSyncableAsUser > 0) {
                try {
                    addSyncStateSwitch(this.mAccount, syncAdapterType2.authority, syncAdapterType2.getPackageName(), getContext().getPackageManager().getPackageUidAsUser(syncAdapterType2.getPackageName(), this.mUserHandle.getIdentifier()));
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e("AccountSettings", "No uid for package" + syncAdapterType2.getPackageName(), e);
                }
            }
        }
        removeCachedPrefs(getPreferenceScreen());
    }

    boolean enabledSyncNowMenu() {
        int preferenceCount = getPreferenceScreen().getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if ((preference instanceof MiuiSyncStateSwitchPreference) && ((MiuiSyncStateSwitchPreference) preference).isChecked()) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_accounts;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9;
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase, com.android.settingslib.accounts.AuthenticatorHelper.OnAccountsUpdateListener
    public void onAccountsUpdate(UserHandle userHandle) {
        super.onAccountsUpdate(userHandle);
        if (!accountExists(this.mAccount)) {
            getActivity().finish();
            return;
        }
        updateAccountSwitches();
        onSyncStateUpdated();
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase, com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        Bundle arguments = getArguments();
        if (arguments == null) {
            Log.e("AccountSettings", "No arguments provided when starting intent. ACCOUNT_KEY needed.");
            finish();
            return;
        }
        Account account = (Account) arguments.getParcelable("account");
        this.mAccount = account;
        if (!accountExists(account)) {
            Log.e("AccountSettings", "Account provided does not exist: " + this.mAccount);
            finish();
            return;
        }
        if (Log.isLoggable("AccountSettings", 2)) {
            Log.v("AccountSettings", "Got account: " + this.mAccount);
        }
        FragmentActivity activity = getActivity();
        LayoutPreference done = EntityHeaderController.newInstance(activity, this, null).setIcon(IconCustomizer.generateIconStyleDrawable(getDrawableForType(this.mAccount.type))).setLabel(this.mAccount.name).setSummary(getLabelForType(this.mAccount.type)).done(activity, getPrefContext());
        done.setOrder(0);
        getPreferenceScreen().addPreference(done);
        if (bundle == null || !bundle.containsKey("uid_request_code")) {
            return;
        }
        this.mUidRequestCodeMap = (HashMap) bundle.getSerializable("uid_request_code");
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i2 == -1) {
            int preferenceCount = getPreferenceScreen().getPreferenceCount();
            for (int i3 = 0; i3 < preferenceCount; i3++) {
                Preference preference = getPreferenceScreen().getPreference(i3);
                if (preference instanceof MiuiSyncStateSwitchPreference) {
                    MiuiSyncStateSwitchPreference miuiSyncStateSwitchPreference = (MiuiSyncStateSwitchPreference) preference;
                    if (getRequestCodeByUid(miuiSyncStateSwitchPreference.getUid()) == i) {
                        onPreferenceTreeClick(miuiSyncStateSwitchPreference);
                        return;
                    }
                }
            }
        }
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.account_sync_settings);
        getPreferenceScreen().setOrderingAsAdded(false);
        setAccessibilityTitle();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i == 100) {
            return new AlertDialog.Builder(getActivity()).setTitle(R.string.really_remove_account_title).setMessage(R.string.really_remove_account_message).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.remove_account_label, new DialogInterface.OnClickListener() { // from class: com.android.settings.accounts.AccountSyncSettings.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    FragmentActivity activity = AccountSyncSettings.this.getActivity();
                    AccountManager.get(activity).removeAccountAsUser(AccountSyncSettings.this.mAccount, activity, new AccountManagerCallback<Bundle>() { // from class: com.android.settings.accounts.AccountSyncSettings.1.1
                        @Override // android.accounts.AccountManagerCallback
                        public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                            Log.i("AccountSettings", "call remove account callback.");
                            if (AccountSyncSettings.this.isResumed()) {
                                boolean z = true;
                                try {
                                    z = true ^ accountManagerFuture.getResult().getBoolean("booleanResult");
                                } catch (AuthenticatorException | OperationCanceledException | IOException unused) {
                                }
                                if (!z || AccountSyncSettings.this.getActivity() == null || AccountSyncSettings.this.getActivity().isFinishing()) {
                                    AccountSyncSettings.this.finish();
                                } else {
                                    AccountSyncSettings.this.showDialog(101);
                                }
                            }
                        }
                    }, null, AccountSyncSettings.this.mUserHandle);
                }
            }).create();
        }
        if (i == 101) {
            return new AlertDialog.Builder(getActivity()).setTitle(R.string.really_remove_account_title).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).setMessage(R.string.remove_account_failed).create();
        }
        if (i == 102) {
            return new AlertDialog.Builder(getActivity()).setTitle(R.string.cant_sync_dialog_title).setMessage(R.string.cant_sync_dialog_message).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).create();
        }
        return null;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem add = menu.add(0, 1, 0, getString(R.string.sync_menu_sync_now));
        MenuItem add2 = menu.add(0, 2, 0, getString(R.string.sync_menu_sync_cancel));
        add.setShowAsAction(4);
        add2.setShowAsAction(4);
        if (!RestrictedLockUtilsInternal.hasBaseUserRestriction(getPrefContext(), "no_modify_accounts", this.mUserHandle.getIdentifier()) && accountExists(this.mAccount)) {
            MenuItem icon = menu.add(0, 3, 0, getString(R.string.remove_account_label)).setIcon(R.drawable.ic_menu_delete);
            icon.setShowAsAction(4);
            RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(getPrefContext(), "no_modify_accounts", this.mUserHandle.getIdentifier());
            if (checkIfRestrictionEnforced == null) {
                checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfAccountManagementDisabled(getPrefContext(), this.mAccount.type, this.mUserHandle.getIdentifier());
            }
            RestrictedLockUtilsInternal.setMenuItemAsDisabledByAdmin(getPrefContext(), icon, checkIfRestrictionEnforced);
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            startSyncForEnabledProviders();
            return true;
        } else if (itemId == 2) {
            cancelSyncForEnabledProviders();
            return true;
        } else if (itemId != 3) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            showDialog(100);
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
        if (getActivity() == null) {
            return false;
        }
        if (preference instanceof MiuiSyncStateSwitchPreference) {
            MiuiSyncStateSwitchPreference miuiSyncStateSwitchPreference = (MiuiSyncStateSwitchPreference) preference;
            String authority = miuiSyncStateSwitchPreference.getAuthority();
            if (TextUtils.isEmpty(authority)) {
                return false;
            }
            Account account = miuiSyncStateSwitchPreference.getAccount();
            int identifier = this.mUserHandle.getIdentifier();
            String packageName = miuiSyncStateSwitchPreference.getPackageName();
            boolean syncAutomaticallyAsUser = ContentResolver.getSyncAutomaticallyAsUser(account, authority, identifier);
            if (!miuiSyncStateSwitchPreference.isOneTimeSyncMode()) {
                boolean isChecked = miuiSyncStateSwitchPreference.isChecked();
                if (isChecked == syncAutomaticallyAsUser || (isChecked && requestAccountAccessIfNeeded(packageName))) {
                    return true;
                }
                ContentResolver.setSyncAutomaticallyAsUser(account, authority, isChecked, identifier);
                if (!ContentResolver.getMasterSyncAutomaticallyAsUser(identifier) || !isChecked) {
                    requestOrCancelSync(account, authority, isChecked);
                }
            } else if (requestAccountAccessIfNeeded(packageName)) {
                return true;
            } else {
                requestOrCancelSync(account, authority, true);
            }
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        boolean z = !ContentResolver.getCurrentSyncsAsUser(this.mUserHandle.getIdentifier()).isEmpty();
        menu.findItem(1).setVisible(!z).setEnabled(enabledSyncNowMenu());
        menu.findItem(2).setVisible(z);
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        this.mAuthenticatorHelper.listenToAccountUpdates();
        updateAuthDescriptions();
        onAccountsUpdate(Binder.getCallingUserHandle());
        super.onResume();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.mUidRequestCodeMap.isEmpty()) {
            return;
        }
        bundle.putSerializable("uid_request_code", this.mUidRequestCodeMap);
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase
    protected void onSyncStateUpdated() {
        if (isResumed()) {
            setFeedsState();
            if (getActivity() == null || !ContentResolver.getCurrentSyncs().isEmpty()) {
                return;
            }
            invalidateOptionsMenu();
        }
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase
    public /* bridge */ /* synthetic */ void updateAuthDescriptions() {
        super.updateAuthDescriptions();
    }
}
