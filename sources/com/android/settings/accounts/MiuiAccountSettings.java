package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncAdapterType;
import android.content.SyncStatusObserver;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedRotateDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.AccessiblePreferenceCategory;
import com.android.settings.MiuiAnimationController;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.users.UserDialogs;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.widget.TogglePreference;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.accounts.AuthenticatorHelper;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import com.google.android.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import miui.accounts.ExtraAccountManager;
import miui.cloud.Constants;
import miui.content.ExtraIntent;
import miui.content.res.IconCustomizer;
import miui.provider.Notes;
import miui.vip.VipService;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiAccountSettings extends SettingsPreferenceFragment implements AuthenticatorHelper.OnAccountsUpdateListener, Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.accounts.MiuiAccountSettings.5
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            Resources resources = context.getResources();
            String string = resources.getString(R.string.account_settings_title);
            List profiles = UserManager.get(context).getProfiles(UserHandle.myUserId());
            int size = profiles.size();
            for (int i = 0; i < size; i++) {
                UserInfo userInfo = (UserInfo) profiles.get(i);
                if (userInfo.isEnabled()) {
                    if (!RestrictedLockUtilsInternal.hasBaseUserRestriction(context, "no_modify_accounts", userInfo.id)) {
                        SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
                        searchIndexableRaw.title = resources.getString(R.string.add_account_label);
                        searchIndexableRaw.screenTitle = string;
                        arrayList.add(searchIndexableRaw);
                    }
                    if (userInfo.isManagedProfile()) {
                        if (!RestrictedLockUtilsInternal.hasBaseUserRestriction(context, "no_remove_managed_profile", UserHandle.myUserId())) {
                            SearchIndexableRaw searchIndexableRaw2 = new SearchIndexableRaw(context);
                            searchIndexableRaw2.title = resources.getString(R.string.remove_managed_profile_label);
                            searchIndexableRaw2.screenTitle = string;
                            arrayList.add(searchIndexableRaw2);
                        }
                        SearchIndexableRaw searchIndexableRaw3 = new SearchIndexableRaw(context);
                        searchIndexableRaw3.title = resources.getString(R.string.managed_profile_settings_title);
                        searchIndexableRaw3.screenTitle = string;
                        arrayList.add(searchIndexableRaw3);
                    }
                }
            }
            return arrayList;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = R.xml.account_settings;
            return Arrays.asList(searchIndexableResource);
        }
    };
    private Activity mActivity;
    private String[] mAuthorities;
    private AlertDialog mDialog;
    private ValuePreference mGmsCoreSettingsPref;
    private PreferenceCategory mGoogleCategory;
    private AccountRestrictionHelper mHelper;
    private Preference mProfileNotAvailablePreference;
    private Object mStatusChangeListenerHandle;
    private SyncDrawable mSyncDrawable;
    private CheckBoxPreference mSyncWifiOnly;
    private boolean mSyncing;
    private UserManager mUm;
    private SparseArray<ProfileData> mProfiles = new SparseArray<>();
    private ManagedProfileBroadcastReceiver mManagedProfileBroadcastReceiver = new ManagedProfileBroadcastReceiver();
    private int mAuthoritiesCount = 0;
    private final Handler mHandler = new Handler();
    public ConcurrentHashMap<String, Long> mPackageTimeMap = new ConcurrentHashMap<>();
    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() { // from class: com.android.settings.accounts.MiuiAccountSettings.1
        @Override // android.content.SyncStatusObserver
        public void onStatusChanged(int i) {
            MiuiAccountSettings.this.mHandler.post(new Runnable() { // from class: com.android.settings.accounts.MiuiAccountSettings.1.1
                @Override // java.lang.Runnable
                public void run() {
                    MiuiAccountSettings.this.onSyncStateUpdated();
                }
            });
        }
    };

    /* loaded from: classes.dex */
    private class AccountPreference extends com.android.settingslib.miuisettings.preference.Preference implements Preference.OnPreferenceClickListener {
        private final String mFragment;
        private final Bundle mFragmentArguments;
        private final CharSequence mTitle;

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            if (this.mFragment != null) {
                new SubSettingLauncher(getContext()).setDestination(this.mFragment).setArguments(this.mFragmentArguments).setTitleText(this.mTitle.toString()).launch();
                return true;
            }
            return false;
        }
    }

    /* loaded from: classes.dex */
    private class ManagedProfileBroadcastReceiver extends BroadcastReceiver {
        private boolean listeningToManagedProfileEvents;

        private ManagedProfileBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v("AccountSettings", "Received broadcast: " + action);
            if (!action.equals("android.intent.action.MANAGED_PROFILE_REMOVED") && !action.equals("android.intent.action.MANAGED_PROFILE_ADDED")) {
                Log.w("AccountSettings", "Cannot handle received broadcast: " + intent.getAction());
                return;
            }
            MiuiAccountSettings.this.stopListeningToAccountUpdates();
            MiuiAccountSettings.this.cleanUpPreferences();
            MiuiAccountSettings.this.updateUi();
            MiuiAccountSettings.this.listenToAccountUpdates();
            if (MiuiAccountSettings.this.getActivity() != null) {
                MiuiAccountSettings.this.getActivity().invalidateOptionsMenu();
            }
        }

        public void register(Context context) {
            if (this.listeningToManagedProfileEvents) {
                return;
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
            context.registerReceiver(this, intentFilter);
            this.listeningToManagedProfileEvents = true;
        }

        public void unregister(Context context) {
            if (this.listeningToManagedProfileEvents) {
                context.unregisterReceiver(this);
                this.listeningToManagedProfileEvents = false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MasterSyncStateClickListener implements TogglePreference.OnBeforeCheckedChangeListener {
        private final UserHandle mUserHandle;

        public MasterSyncStateClickListener(UserHandle userHandle) {
            this.mUserHandle = userHandle;
        }

        @Override // com.android.settings.widget.TogglePreference.OnBeforeCheckedChangeListener
        public boolean onBeforeCheckedChanged(final TogglePreference togglePreference, final boolean z) {
            if (ActivityManager.isUserAMonkey()) {
                Log.d("AccountSettings", "ignoring monkey's attempt to flip sync state");
                return true;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(MiuiAccountSettings.this.getContext());
            if (z) {
                builder.setTitle(R.string.data_usage_auto_sync_on_dialog_title);
                builder.setMessage(R.string.data_usage_auto_sync_on_dialog);
            } else {
                builder.setTitle(R.string.data_usage_auto_sync_off_dialog_title);
                builder.setMessage(R.string.data_usage_auto_sync_off_dialog);
            }
            builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.accounts.MiuiAccountSettings.MasterSyncStateClickListener.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    togglePreference.setCheckedInternal(z);
                    ContentResolver.setMasterSyncAutomaticallyAsUser(z, MasterSyncStateClickListener.this.mUserHandle.getIdentifier());
                    MasterSyncStateClickListener masterSyncStateClickListener = MasterSyncStateClickListener.this;
                    MiuiAccountSettings.this.syncOrCancel(z, masterSyncStateClickListener.mUserHandle.getIdentifier());
                    MiuiAccountSettings.this.onSyncStateUpdated();
                    MiuiAccountSettings.this.reportSimOnDevice(z);
                }
            });
            builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.accounts.MiuiAccountSettings.MasterSyncStateClickListener.2
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    MiuiAccountSettings.this.updateSyncPreference();
                }
            });
            MiuiAccountSettings.this.mDialog = builder.show();
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ProfileData {
        public RestrictedPreference addAccountPreference;
        public AuthenticatorHelper authenticatorHelper;
        public Preference managedProfilePreference;
        public PreferenceGroup preferenceGroup;
        public RestrictedPreference removeWorkProfilePreference;
        public UserInfo userInfo;

        private ProfileData() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SyncDrawable extends MiuiAnimationController {
        SyncDrawable(Context context) {
            super(context, R.drawable.action_button_refresh);
            AnimatedRotateDrawable animationDrawable = getAnimationDrawable();
            animationDrawable.setFramesCount(56);
            animationDrawable.setFramesDuration(32);
        }

        @Override // com.android.settings.MiuiAnimationController
        protected Animatable getAnimationDrawable(Drawable drawable) {
            StateListDrawable stateListDrawable = (StateListDrawable) drawable;
            return stateListDrawable.getStateDrawable(stateListDrawable.findStateDrawableIndex(new int[]{16842910}));
        }
    }

    private boolean accountTypeHasAnyRequestedAuthorities(AuthenticatorHelper authenticatorHelper, String str) {
        if (this.mAuthoritiesCount == 0) {
            return true;
        }
        ArrayList<String> authoritiesForAccountType = authenticatorHelper.getAuthoritiesForAccountType(str);
        if (authoritiesForAccountType == null) {
            Log.d("AccountSettings", "No sync authorities for account type: " + str);
            return false;
        }
        for (int i = 0; i < this.mAuthoritiesCount; i++) {
            if (authoritiesForAccountType.contains(this.mAuthorities[i])) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cleanUpPreferences() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.removeAll();
        }
        this.mProfiles.clear();
    }

    private NewAccountPreference getAccountPreferenceByAccountType(AuthenticatorHelper authenticatorHelper, UserHandle userHandle, String str) {
        CharSequence labelForType;
        NewAccountPreference newAccountPreference;
        if (!accountTypeHasAnyRequestedAuthorities(authenticatorHelper, str) || "com.xiaomi".equals(str) || ExtraIntent.XIAOMI_ACCOUNT_TYPE_UNACTIVATED.equals(str) || (labelForType = authenticatorHelper.getLabelForType(getActivity(), str)) == null) {
            return null;
        }
        String packageForType = authenticatorHelper.getPackageForType(str);
        int labelIdForType = authenticatorHelper.getLabelIdForType(str);
        Account[] accountsByTypeAsUser = AccountManager.get(getActivity()).getAccountsByTypeAsUser(str, userHandle);
        if (accountsByTypeAsUser.length == 1 && !authenticatorHelper.hasAccountPreferences(str)) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("account", accountsByTypeAsUser[0]);
            bundle.putParcelable("android.intent.extra.USER", userHandle);
            newAccountPreference = new NewAccountPreference(this, getPrefContext(), labelForType, packageForType, labelIdForType, AccountSyncSettings.class.getName(), bundle, IconCustomizer.generateIconStyleDrawable(authenticatorHelper.getDrawableForType(getActivity(), str)));
        } else {
            Bundle bundle2 = new Bundle();
            bundle2.putString(Notes.Account.ACCOUNT_TYPE, str);
            bundle2.putString("account_label", labelForType.toString());
            bundle2.putParcelable("android.intent.extra.USER", userHandle);
            newAccountPreference = new NewAccountPreference(this, getPrefContext(), labelForType, packageForType, labelIdForType, MiuiManageAccounts.class.getName(), bundle2, IconCustomizer.generateIconStyleDrawable(authenticatorHelper.getDrawableForType(getActivity(), str)));
        }
        authenticatorHelper.preloadDrawableForType(getActivity(), str);
        return newAccountPreference;
    }

    private ArrayList<NewAccountPreference> getAccountTypePreferences(AuthenticatorHelper authenticatorHelper, UserHandle userHandle) {
        NewAccountPreference accountPreferenceByAccountType;
        String[] enabledAccountTypes = authenticatorHelper.getEnabledAccountTypes();
        ArrayList<NewAccountPreference> arrayList = new ArrayList<>(enabledAccountTypes.length);
        for (String str : enabledAccountTypes) {
            if (!"com.xiaomi".equals(str) && !ExtraIntent.XIAOMI_ACCOUNT_TYPE_UNACTIVATED.equals(str) && !"com.google".equals(str) && (accountPreferenceByAccountType = getAccountPreferenceByAccountType(authenticatorHelper, userHandle, str)) != null) {
                arrayList.add(accountPreferenceByAccountType);
            }
        }
        Collections.sort(arrayList, new Comparator<NewAccountPreference>() { // from class: com.android.settings.accounts.MiuiAccountSettings.4
            @Override // java.util.Comparator
            public int compare(NewAccountPreference newAccountPreference, NewAccountPreference newAccountPreference2) {
                return newAccountPreference.mTitle.toString().compareTo(newAccountPreference2.mTitle.toString());
            }
        });
        return arrayList;
    }

    private NewAccountPreference getGoogleAccountTypePreferences(AuthenticatorHelper authenticatorHelper, UserHandle userHandle) {
        if (new ArrayList(Arrays.asList(authenticatorHelper.getEnabledAccountTypes())).contains("com.google")) {
            return getAccountPreferenceByAccountType(authenticatorHelper, userHandle, "com.google");
        }
        return null;
    }

    private String getWorkGroupSummary(Context context, UserInfo userInfo) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo adminApplicationInfo = Utils.getAdminApplicationInfo(context, userInfo.id);
        if (adminApplicationInfo == null) {
            return null;
        }
        return getString(R.string.managing_admin, packageManager.getApplicationLabel(adminApplicationInfo));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void listenToAccountUpdates() {
        int size = this.mProfiles.size();
        for (int i = 0; i < size; i++) {
            AuthenticatorHelper authenticatorHelper = this.mProfiles.valueAt(i).authenticatorHelper;
            if (authenticatorHelper != null) {
                authenticatorHelper.listenToAccountUpdates();
            }
        }
    }

    private RestrictedPreference newAddAccountPreference(Context context) {
        RestrictedPreference restrictedPreference = new RestrictedPreference(context);
        restrictedPreference.setTitle(Html.fromHtml("<font color=\"#0D84FF\">" + getString(R.string.add_account_label) + "</font>"));
        restrictedPreference.setOrder(VipService.VIP_SERVICE_FAILURE);
        return restrictedPreference;
    }

    private Preference newManagedProfileSettings(Context context) {
        Preference preference = new Preference(context);
        preference.setTitle(R.string.managed_profile_settings_title);
        preference.setIcon(R.drawable.ic_settings_24dp);
        preference.setOnPreferenceClickListener(this);
        preference.setOrder(1001);
        return preference;
    }

    private RestrictedPreference newRemoveWorkProfilePreference(Context context) {
        RestrictedPreference restrictedPreference = new RestrictedPreference(context);
        restrictedPreference.setShowIcon(true);
        restrictedPreference.setTitle(R.string.remove_managed_profile_label);
        restrictedPreference.setIcon(R.drawable.ic_menu_delete);
        restrictedPreference.setOnPreferenceClickListener(this);
        restrictedPreference.setOrder(1002);
        return restrictedPreference;
    }

    private void playAnimation(MenuItem menuItem) {
        this.mSyncDrawable.playAnimation();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reportSimOnDevice(boolean z) {
        Account xiaomiAccount = ExtraAccountManager.getXiaomiAccount(this.mActivity);
        if (xiaomiAccount == null) {
            return;
        }
        boolean syncAutomatically = ContentResolver.getSyncAutomatically(xiaomiAccount, "sms");
        Intent intent = new Intent(Constants.Intents.ACTION_UPLOAD_PHONE_LIST);
        intent.putExtra(ExtraIntent.EXTRA_UPLOAD_OPTION, (syncAutomatically && z) ? 1 : 2);
        intent.setPackage(Constants.CLOUDSERVICE_PACKAGE_NAME);
        this.mActivity.startService(intent);
    }

    private void stopAnimation(MenuItem menuItem) {
        this.mSyncDrawable.stopAnimation();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopListeningToAccountUpdates() {
        int size = this.mProfiles.size();
        for (int i = 0; i < size; i++) {
            AuthenticatorHelper authenticatorHelper = this.mProfiles.valueAt(i).authenticatorHelper;
            if (authenticatorHelper != null) {
                authenticatorHelper.stopListeningToAccountUpdates();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void syncOrCancel(boolean z, final int i) {
        if (z) {
            new AlertDialog.Builder(getContext()).setTitle(R.string.sure_sync_now).setPositiveButton(R.string.sync_now, new DialogInterface.OnClickListener() { // from class: com.android.settings.accounts.MiuiAccountSettings.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    MiuiAccountSettings.this.turnOnSyncs(true, i);
                }
            }).setNegativeButton(R.string.dlg_cancel, (DialogInterface.OnClickListener) null).show();
        } else {
            turnOnSyncs(false, i);
        }
    }

    private boolean syncableAccountAvailable() {
        Account[] accounts = ((AccountManager) getSystemService("account")).getAccounts();
        SyncAdapterType[] syncAdapterTypes = ContentResolver.getSyncAdapterTypes();
        boolean masterSyncAutomatically = ContentResolver.getMasterSyncAutomatically();
        for (Account account : accounts) {
            for (SyncAdapterType syncAdapterType : syncAdapterTypes) {
                if (syncAdapterType.accountType.equals(account.type) && (!masterSyncAutomatically || ContentResolver.getSyncAutomatically(account, syncAdapterType.authority))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void trackEvent(String str) {
        if (this.mActivity == null) {
            return;
        }
        Intent intent = new Intent("miui.intent.action.TRACK_EVENT");
        intent.putExtra("eventId", str);
        intent.setPackage(this.mActivity.getPackageName());
        this.mActivity.sendBroadcast(intent);
    }

    private void trackWifiOnlyEnabled(boolean z) {
        trackEvent(z ? "account_settings_wifi_only_enabled" : "account_settings_wifi_only_disabled");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void turnOnSyncs(boolean z, int i) {
        Account[] accounts = ((AccountManager) getSystemService("account")).getAccounts();
        SyncAdapterType[] syncAdapterTypesAsUser = ContentResolver.getSyncAdapterTypesAsUser(i);
        Bundle bundle = new Bundle();
        HashSet<SyncAdapterType> newHashSet = Sets.newHashSet();
        for (Account account : accounts) {
            newHashSet.clear();
            for (SyncAdapterType syncAdapterType : syncAdapterTypesAsUser) {
                if (syncAdapterType.accountType.equals(account.type) && ContentResolver.getSyncAutomaticallyAsUser(account, syncAdapterType.authority, i)) {
                    newHashSet.add(syncAdapterType);
                }
            }
            for (SyncAdapterType syncAdapterType2 : newHashSet) {
                if (z) {
                    ContentResolver.requestSyncAsUser(account, syncAdapterType2.authority, i, bundle);
                } else {
                    ContentResolver.cancelSyncAsUser(account, syncAdapterType2.authority, i);
                }
            }
        }
    }

    private void updateAccountTypes(ProfileData profileData) {
        profileData.preferenceGroup.removeAll();
        if (profileData.userInfo.isEnabled()) {
            ArrayList<NewAccountPreference> accountTypePreferences = getAccountTypePreferences(profileData.authenticatorHelper, profileData.userInfo.getUserHandle());
            int size = accountTypePreferences.size();
            for (int i = 0; i < size; i++) {
                profileData.preferenceGroup.addPreference(accountTypePreferences.get(i));
            }
            RestrictedPreference restrictedPreference = profileData.addAccountPreference;
            if (restrictedPreference != null) {
                profileData.preferenceGroup.addPreference(restrictedPreference);
            }
        } else {
            this.mProfileNotAvailablePreference.setEnabled(false);
            this.mProfileNotAvailablePreference.setIcon(R.drawable.empty_icon);
            this.mProfileNotAvailablePreference.setTitle((CharSequence) null);
            this.mProfileNotAvailablePreference.setSummary(R.string.managed_profile_not_available_label);
            profileData.preferenceGroup.addPreference(this.mProfileNotAvailablePreference);
        }
        RestrictedPreference restrictedPreference2 = profileData.removeWorkProfilePreference;
        if (restrictedPreference2 != null) {
            profileData.preferenceGroup.addPreference(restrictedPreference2);
        }
        Preference preference = profileData.managedProfilePreference;
        if (preference != null) {
            profileData.preferenceGroup.addPreference(preference);
        }
    }

    private void updateGoogle() {
        ActivityInfo activityInfo;
        NewAccountPreference googleAccountTypePreferences;
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("google_category");
        this.mGoogleCategory = preferenceCategory;
        if (preferenceCategory == null) {
            return;
        }
        this.mGmsCoreSettingsPref = (ValuePreference) preferenceCategory.findPreference("gmscore_settings");
        this.mGoogleCategory.removeAll();
        int size = this.mProfiles.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            ProfileData valueAt = this.mProfiles.valueAt(i2);
            if (valueAt != null && valueAt.userInfo.isEnabled() && (googleAccountTypePreferences = getGoogleAccountTypePreferences(valueAt.authenticatorHelper, valueAt.userInfo.getUserHandle())) != null) {
                googleAccountTypePreferences.setOrder(i);
                this.mGoogleCategory.addPreference(googleAccountTypePreferences);
                i++;
            }
        }
        this.mGmsCoreSettingsPref.setLayoutResource(R.layout.preference_system_app);
        this.mGmsCoreSettingsPref.setIcon(IconCustomizer.generateIconStyleDrawable(getContext().getResources().getDrawable(R.drawable.gmscore_icon)));
        PackageManager packageManager = getContext().getPackageManager();
        ResolveInfo resolveActivity = packageManager.resolveActivity(this.mGmsCoreSettingsPref.getIntent(), 0);
        if (resolveActivity != null && (activityInfo = resolveActivity.activityInfo) != null) {
            this.mGmsCoreSettingsPref.setTitle(activityInfo.loadLabel(packageManager));
        }
        if (!SettingsFeatures.isNeedRemoveGmsCoreSettigns(getContext())) {
            this.mGoogleCategory.addPreference(this.mGmsCoreSettingsPref);
        }
        if (this.mGoogleCategory.getPreferenceCount() == 0) {
            getPreferenceScreen().removePreference(this.mGoogleCategory);
        }
    }

    private void updateProfileUi(UserInfo userInfo, boolean z, PreferenceScreen preferenceScreen) {
        Context prefContext = getPrefContext();
        ProfileData profileData = new ProfileData();
        profileData.userInfo = userInfo;
        if (z) {
            profileData.preferenceGroup = new AccessiblePreferenceCategory(getPrefContext());
            if (userInfo.isManagedProfile()) {
                profileData.preferenceGroup.setLayoutResource(R.layout.work_profile_category);
                profileData.preferenceGroup.setTitle(R.string.category_work);
                String workGroupSummary = getWorkGroupSummary(prefContext, userInfo);
                profileData.preferenceGroup.setSummary(workGroupSummary);
                ((AccessiblePreferenceCategory) profileData.preferenceGroup).setContentDescription(getString(R.string.accessibility_category_work, workGroupSummary));
                RestrictedPreference newRemoveWorkProfilePreference = newRemoveWorkProfilePreference(prefContext);
                profileData.removeWorkProfilePreference = newRemoveWorkProfilePreference;
                this.mHelper.enforceRestrictionOnPreference(newRemoveWorkProfilePreference, "no_remove_managed_profile", UserHandle.myUserId());
                profileData.managedProfilePreference = newManagedProfileSettings(prefContext);
            } else {
                profileData.preferenceGroup.setTitle(R.string.category_personal);
                ((AccessiblePreferenceCategory) profileData.preferenceGroup).setContentDescription(getString(R.string.accessibility_category_personal));
            }
            preferenceScreen.addPreference(profileData.preferenceGroup);
        } else {
            profileData.preferenceGroup = (PreferenceCategory) preferenceScreen.findPreference("account_other");
        }
        if (userInfo.isEnabled()) {
            profileData.authenticatorHelper = new AuthenticatorHelper(prefContext, userInfo.getUserHandle(), this);
            if (!RestrictedLockUtilsInternal.hasBaseUserRestriction(prefContext, "no_modify_accounts", userInfo.id)) {
                RestrictedPreference newAddAccountPreference = newAddAccountPreference(prefContext);
                profileData.addAccountPreference = newAddAccountPreference;
                this.mHelper.enforceRestrictionOnPreference(newAddAccountPreference, "no_modify_accounts", userInfo.id);
            }
        }
        this.mProfiles.put(userInfo.id, profileData);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSyncPreference() {
        UserHandle myUserHandle = Process.myUserHandle();
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("wifi_only");
        this.mSyncWifiOnly = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this);
        if (com.android.settingslib.Utils.isWifiOnly(this.mActivity)) {
            getPreferenceScreen().removePreference(this.mSyncWifiOnly);
            this.mSyncWifiOnly = null;
        }
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("account_sync");
        if (this.mProfiles.size() == 1) {
            TogglePreference togglePreference = (TogglePreference) preferenceCategory.findPreference("account_settings_menu_auto_sync");
            togglePreference.setOnBeforeCheckedChangeListener(new MasterSyncStateClickListener(myUserHandle));
            togglePreference.setCheckedInternal(ContentResolver.getMasterSyncAutomaticallyAsUser(myUserHandle.getIdentifier()));
            Preference findPreference = preferenceCategory.findPreference("account_settings_menu_auto_sync_personal");
            if (findPreference != null) {
                preferenceCategory.removePreference(findPreference);
            }
            Preference findPreference2 = preferenceCategory.findPreference("account_settings_menu_auto_sync_work");
            if (findPreference2 != null) {
                preferenceCategory.removePreference(findPreference2);
            }
        } else if (this.mProfiles.size() > 1) {
            UserHandle userHandle = this.mProfiles.valueAt(1).userInfo.getUserHandle();
            TogglePreference togglePreference2 = (TogglePreference) preferenceCategory.findPreference("account_settings_menu_auto_sync_personal");
            togglePreference2.setOnBeforeCheckedChangeListener(new MasterSyncStateClickListener(myUserHandle));
            togglePreference2.setCheckedInternal(ContentResolver.getMasterSyncAutomaticallyAsUser(myUserHandle.getIdentifier()));
            TogglePreference togglePreference3 = (TogglePreference) preferenceCategory.findPreference("account_settings_menu_auto_sync_work");
            togglePreference3.setOnBeforeCheckedChangeListener(new MasterSyncStateClickListener(userHandle));
            togglePreference3.setCheckedInternal(ContentResolver.getMasterSyncAutomaticallyAsUser(userHandle.getIdentifier()));
            Preference findPreference3 = preferenceCategory.findPreference("account_settings_menu_auto_sync");
            if (findPreference3 != null) {
                preferenceCategory.removePreference(findPreference3);
            }
            Preference findPreference4 = findPreference("account_other");
            if (findPreference4 != null && ((PreferenceCategory) findPreference4).getPreferenceCount() == 0) {
                getPreferenceScreen().removePreference(findPreference4);
            }
        }
        CheckBoxPreference checkBoxPreference2 = this.mSyncWifiOnly;
        if (checkBoxPreference2 != null) {
            checkBoxPreference2.setChecked(Settings.Secure.getInt(getContentResolver(), "sync_on_wifi_only", 0) == 1);
            TogglePreference togglePreference4 = (TogglePreference) preferenceCategory.findPreference("account_settings_menu_auto_sync");
            if (togglePreference4 != null) {
                this.mSyncWifiOnly.setEnabled(togglePreference4.isChecked());
            }
        }
    }

    private void updateSyncWifiOnlyPreference(boolean z) {
        Settings.Secure.putInt(getContentResolver(), "sync_on_wifi_only", z ? 1 : 0);
        trackWifiOnlyEnabled(z);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 8;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiAccountSettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public int getPageIndex() {
        return 6;
    }

    @Override // com.android.settingslib.accounts.AuthenticatorHelper.OnAccountsUpdateListener
    public void onAccountsUpdate(UserHandle userHandle) {
        ProfileData profileData = this.mProfiles.get(userHandle.getIdentifier());
        if (profileData != null) {
            updateAccountTypes(profileData);
            return;
        }
        Log.w("AccountSettings", "Missing Settings screen for: " + userHandle.getIdentifier());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.delayToBuildRecommendLayout = true;
        this.mUm = (UserManager) getSystemService("user");
        this.mProfileNotAvailablePreference = new Preference(getPrefContext());
        String[] stringArrayExtra = getActivity().getIntent().getStringArrayExtra("authorities");
        this.mAuthorities = stringArrayExtra;
        if (stringArrayExtra != null) {
            this.mAuthoritiesCount = stringArrayExtra.length;
        }
        FragmentActivity activity = getActivity();
        this.mActivity = activity;
        this.mSyncDrawable = new SyncDrawable(activity);
        setHasOptionsMenu(true);
        this.mHelper = new AccountRestrictionHelper(this.mActivity);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.add(0, 1, 0, R.string.sync_menu_sync_now).setIcon(this.mSyncDrawable.getAnimationIcon()).setShowAsAction(5);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 1) {
            return super.onOptionsItemSelected(menuItem);
        }
        if (this.mSyncing) {
            ContentResolver.cancelSync(null, null);
        } else {
            Bundle bundle = new Bundle();
            if (!ContentResolver.getMasterSyncAutomatically()) {
                bundle.putBoolean("force", true);
            }
            ContentResolver.requestSync(null, null, bundle);
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        stopListeningToAccountUpdates();
        ContentResolver.removeStatusChangeListener(this.mStatusChangeListenerHandle);
        this.mManagedProfileBroadcastReceiver.unregister(getActivity());
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("wifi_only".equals(preference.getKey())) {
            updateSyncWifiOnlyPreference(((Boolean) obj).booleanValue());
            return true;
        }
        return false;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        int size = this.mProfiles.size();
        for (int i = 0; i < size; i++) {
            ProfileData valueAt = this.mProfiles.valueAt(i);
            if (preference == valueAt.removeWorkProfilePreference) {
                final int i2 = valueAt.userInfo.id;
                UserDialogs.createRemoveDialog(getActivity(), i2, new DialogInterface.OnClickListener() { // from class: com.android.settings.accounts.MiuiAccountSettings.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i3) {
                        MiuiAccountSettings.this.mUm.removeUser(i2);
                    }
                }).show();
                return true;
            } else if (preference == valueAt.managedProfilePreference) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("android.intent.extra.USER", valueAt.userInfo.getUserHandle());
                new SubSettingLauncher(getActivity()).setDestination(ManagedProfileSettings.class.getName()).setTitleRes(R.string.managed_profile_settings_title).setArguments(bundle).launch();
                return true;
            }
        }
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        int size = this.mProfiles.size();
        for (int i = 0; i < size; i++) {
            ProfileData valueAt = this.mProfiles.valueAt(i);
            if (preference == valueAt.addAccountPreference) {
                Intent intent = new Intent("android.settings.ADD_ACCOUNT_SETTINGS");
                intent.putExtra("android.intent.extra.USER", valueAt.userInfo.getUserHandle());
                intent.putExtra("authorities", this.mAuthorities);
                startActivity(intent);
                return true;
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem findItem = menu.findItem(1);
        if (findItem != null) {
            if (syncableAccountAvailable()) {
                findItem.setEnabled(true);
                if (this.mSyncing) {
                    findItem.setIcon(this.mSyncDrawable.getAnimationIcon());
                    findItem.setTitle(R.string.sync_in_progress);
                    playAnimation(findItem);
                } else {
                    findItem.setTitle(R.string.sync_menu_sync_now);
                    stopAnimation(findItem);
                    findItem.setIcon(R.drawable.action_button_refresh_normal_light);
                }
            } else {
                findItem.setEnabled(false);
                findItem.setTitle(R.string.sync_menu_sync_now);
            }
        }
        if (menu.size() > 0) {
            MiuiUtils.setNavigationBackground(getActivity(), false);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        cleanUpPreferences();
        updateUi();
        tryBuildRecommendLayout(0, true);
        this.mManagedProfileBroadcastReceiver.register(getActivity());
        listenToAccountUpdates();
        this.mStatusChangeListenerHandle = ContentResolver.addStatusChangeListener(13, this.mSyncStatusObserver);
        onSyncStateUpdated();
    }

    protected void onSyncStateUpdated() {
        if (this.mActivity == null) {
            return;
        }
        boolean z = !ContentResolver.getCurrentSyncs().isEmpty();
        if (this.mSyncing != z) {
            this.mSyncing = z;
        }
        updateSyncPreference();
        if (getActivity() != null) {
            getActivity().invalidateOptionsMenu();
        }
    }

    @TargetApi(5)
    public void requestOrCancelSync(String str, Account account, UserHandle userHandle, String str2, boolean z) {
        HashSet newHashSet = Sets.newHashSet();
        int identifier = userHandle.getIdentifier();
        SyncAdapterType[] syncAdapterTypesAsUser = ContentResolver.getSyncAdapterTypesAsUser(identifier);
        Bundle bundle = new Bundle();
        bundle.putBoolean("force", true);
        newHashSet.clear();
        for (SyncAdapterType syncAdapterType : syncAdapterTypesAsUser) {
            if (str.equals(syncAdapterType.getPackageName())) {
                if (z) {
                    ContentResolver.requestSyncAsUser(account, syncAdapterType.authority, identifier, bundle);
                    return;
                } else {
                    ContentResolver.cancelSyncAsUser(account, str2, identifier);
                    return;
                }
            }
        }
    }

    void updateUi() {
        addPreferencesFromResource(R.xml.account_settings);
        if (this.mUm.isManagedProfile()) {
            Log.e("AccountSettings", "We should not be showing settings for a managed profile");
            finish();
            return;
        }
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (this.mUm.isLinkedUser()) {
            updateProfileUi(this.mUm.getUserInfo(UserHandle.myUserId()), false, preferenceScreen);
        } else {
            List profiles = this.mUm.getProfiles(UserHandle.myUserId());
            int size = profiles.size();
            for (int i = size - 1; i >= 0; i--) {
                if (((UserInfo) profiles.get(i)).id == 999) {
                    profiles.remove(i);
                    size--;
                }
            }
            boolean z = size > 1;
            for (int i2 = 0; i2 < size; i2++) {
                updateProfileUi((UserInfo) profiles.get(i2), z, preferenceScreen);
            }
        }
        int size2 = this.mProfiles.size();
        updateGoogle();
        for (int i3 = 0; i3 < size2; i3++) {
            ProfileData valueAt = this.mProfiles.valueAt(i3);
            if (!valueAt.preferenceGroup.equals(preferenceScreen)) {
                preferenceScreen.addPreference(valueAt.preferenceGroup);
            }
            updateAccountTypes(valueAt);
        }
        updateSyncPreference();
    }
}
