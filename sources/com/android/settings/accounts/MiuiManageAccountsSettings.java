package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AppGlobals;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedRotateDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.MiuiAnimationController;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settingslib.Utils;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import com.google.android.collect.Sets;
import com.xiaomi.micloudsdk.CommonSdk;
import com.xiaomi.micloudsdk.request.utils.Request;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import miui.accounts.ExtraAccountManager;
import miui.cloud.Constants;
import miui.cloud.sync.MiCloudStatusInfo;
import miui.cloud.sync.VipInfo;
import miui.content.ExtraIntent;
import miui.content.res.IconCustomizer;
import miui.provider.Notes;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiManageAccountsSettings extends MiuiAccountPreferenceBase implements Preference.OnPreferenceChangeListener {
    private static final Comparator<Preference> PREFERENCE_COMPARATOR = new Comparator<Preference>() { // from class: com.android.settings.accounts.MiuiManageAccountsSettings.1
        private final Collator sCollator = Collator.getInstance();

        @Override // java.util.Comparator
        public int compare(Preference preference, Preference preference2) {
            if (preference == null && preference2 == null) {
                return 0;
            }
            if (preference == null) {
                return -1;
            }
            if (preference2 == null) {
                return 1;
            }
            return this.sCollator.compare(preference.getTitle().toString(), preference2.getTitle().toString());
        }
    };
    protected AlertDialog dialog;
    private FragmentActivity mActivity;
    private String[] mAuthorities;
    private ValuePreference mCloudServicePref;
    private TextView mErrorInfoView;
    private PreferenceCategory mOtherAccountCategory;
    private PreferenceCategory mSyncAccountCategory;
    private MiuiAnimationController mSyncDrawable;
    private CheckBoxPreference mSyncEnable;
    private CheckBoxPreference mSyncWifiOnly;
    private boolean mSyncing;
    private PreferenceCategory mXiaoMiAccountCategory;

    /* loaded from: classes.dex */
    class LoadVIPNameTask extends AsyncTask<Void, Void, String> {
        private Context context;
        final /* synthetic */ MiuiManageAccountsSettings this$0;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public String doInBackground(Void... voidArr) {
            String str;
            MiCloudStatusInfo fromUserData = MiCloudStatusInfo.fromUserData(this.context);
            VipInfo vipInfo = null;
            if (fromUserData == null || fromUserData.getQuotaInfo() == null) {
                return null;
            }
            MiCloudStatusInfo.QuotaInfo quotaInfo = fromUserData.getQuotaInfo();
            Locale locale = this.context.getResources().getConfiguration().locale;
            try {
                Request.init(this.context);
                vipInfo = CommonSdk.getMiCloudMemberStatusInfo(fromUserData.getUserId(), locale != null ? locale.toString() : Locale.getDefault().toString());
            } catch (Exception e) {
                Log.e("MiuiManageAccountsSettings", "Fail to get vip info.", e);
            }
            StringBuilder sb = new StringBuilder();
            if (vipInfo != null) {
                str = vipInfo.vipName + " ";
            } else {
                str = "";
            }
            sb.append(str);
            sb.append(String.format("%s/%s", MiuiUtils.getQuantityStringWithUnit(locale, 1, quotaInfo.getUsed()), MiuiUtils.getQuantityStringWithUnit(locale, 1, quotaInfo.getTotal())));
            return sb.toString();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(String str) {
            if (str == null) {
                this.this$0.mCloudServicePref.setValue(R.string.xiaomi_cloud_service_unknown);
            } else {
                this.this$0.mCloudServicePref.setValue(str);
            }
        }
    }

    /* loaded from: classes.dex */
    private static class SyncDrawable extends MiuiAnimationController {
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

    private void checkAndSync(boolean z) {
        if (z) {
            this.dialog.show();
        } else {
            turnOnSyncs(false);
        }
    }

    public static boolean isUserVisible(String str) {
        for (SyncAdapterType syncAdapterType : ContentResolver.getSyncAdapterTypes()) {
            if (syncAdapterType.isUserVisible() && str.equals(syncAdapterType.accountType)) {
                return true;
            }
        }
        return false;
    }

    private void loadOtherAccount() {
        new AsyncTask<Void, Void, List<Preference>>() { // from class: com.android.settings.accounts.MiuiManageAccountsSettings.3
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public List<Preference> doInBackground(Void... voidArr) {
                CharSequence labelForType;
                ArrayList arrayList = new ArrayList();
                FragmentActivity activity = MiuiManageAccountsSettings.this.getActivity();
                if (activity == null) {
                    return arrayList;
                }
                MiuiManageAccountsSettings.this.mAuthenticatorHelper.onAccountsUpdated(null);
                for (String str : MiuiManageAccountsSettings.this.mAuthenticatorHelper.getEnabledAccountTypes()) {
                    if (!"com.xiaomi".equals(str) && !ExtraIntent.XIAOMI_ACCOUNT_TYPE_UNACTIVATED.equals(str) && (labelForType = MiuiManageAccountsSettings.this.mAuthenticatorHelper.getLabelForType(activity, str)) != null && MiuiManageAccountsSettings.isUserVisible(str)) {
                        Preference preference = new Preference(MiuiManageAccountsSettings.this.getPrefContext());
                        Account[] accountsByTypeAsUser = AccountManager.get(activity).getAccountsByTypeAsUser(str, MiuiManageAccountsSettings.this.mUserHandle);
                        boolean z = accountsByTypeAsUser.length == 1 && !MiuiManageAccountsSettings.this.mAuthenticatorHelper.hasAccountPreferences(str);
                        preference.getExtras().putString(Notes.Account.ACCOUNT_TYPE, str);
                        preference.getExtras().putParcelable("UserHandle", MiuiManageAccountsSettings.this.mUserHandle);
                        if (z) {
                            preference.setFragment(AccountSyncSettings.class.getName());
                            preference.getExtras().putParcelable("account", accountsByTypeAsUser[0]);
                        } else {
                            preference.setFragment(MiuiManageAccounts.class.getName());
                        }
                        Drawable drawableForType = MiuiManageAccountsSettings.this.mAuthenticatorHelper.getDrawableForType(activity, str);
                        if (drawableForType != null) {
                            drawableForType = IconCustomizer.generateIconStyleDrawable(drawableForType);
                        }
                        preference.setTitle(labelForType);
                        preference.setIcon(drawableForType);
                        preference.setLayoutResource(R.layout.preference_system_app);
                        arrayList.add(preference);
                    }
                }
                Collections.sort(arrayList, MiuiManageAccountsSettings.PREFERENCE_COMPARATOR);
                return arrayList;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(List<Preference> list) {
                if (list.isEmpty()) {
                    MiuiManageAccountsSettings.this.getPreferenceScreen().removePreference(MiuiManageAccountsSettings.this.mOtherAccountCategory);
                    return;
                }
                MiuiManageAccountsSettings.this.getPreferenceScreen().addPreference(MiuiManageAccountsSettings.this.mOtherAccountCategory);
                MiuiManageAccountsSettings.this.mOtherAccountCategory.removeAll();
                Iterator<Preference> it = list.iterator();
                while (it.hasNext()) {
                    MiuiManageAccountsSettings.this.mOtherAccountCategory.addPreference(it.next());
                }
            }
        }.execute(new Void[0]);
    }

    private void onSyncCancel() {
        if (this.mSyncing) {
            ContentResolver.cancelSync(null, null);
        }
    }

    private void onSyncRequest() {
        if (this.mSyncing) {
            return;
        }
        Bundle bundle = new Bundle();
        if (!ContentResolver.getMasterSyncAutomatically()) {
            bundle.putBoolean("force", true);
        }
        ContentResolver.requestSync(null, null, bundle);
    }

    private void playAnimation(MenuItem menuItem) {
        this.mSyncDrawable.playAnimation();
    }

    private void reportSimOnDevice(boolean z) {
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

    private void trackViewOpened() {
        trackEvent("account_settings_opened");
    }

    private void trackWifiOnlyEnabled(boolean z) {
        trackEvent(z ? "account_settings_wifi_only_enabled" : "account_settings_wifi_only_disabled");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void turnOnSyncs(boolean z) {
        Account[] accounts = ((AccountManager) getSystemService("account")).getAccounts();
        SyncAdapterType[] syncAdapterTypes = ContentResolver.getSyncAdapterTypes();
        Bundle bundle = new Bundle();
        HashSet<SyncAdapterType> newHashSet = Sets.newHashSet();
        for (Account account : accounts) {
            newHashSet.clear();
            for (SyncAdapterType syncAdapterType : syncAdapterTypes) {
                if (syncAdapterType.accountType.equals(account.type) && ContentResolver.getSyncAutomatically(account, syncAdapterType.authority)) {
                    newHashSet.add(syncAdapterType);
                }
            }
            for (SyncAdapterType syncAdapterType2 : newHashSet) {
                if (z) {
                    ContentResolver.requestSync(account, syncAdapterType2.authority, bundle);
                } else {
                    ContentResolver.cancelSync(account, syncAdapterType2.authority);
                }
            }
        }
    }

    private void updateSyncEnablePreference(boolean z) {
        ContentResolver.setMasterSyncAutomatically(z);
        checkAndSync(z);
        onSyncStateUpdated();
        reportSimOnDevice(z);
    }

    private void updateSyncWifiOnlyPreference(boolean z) {
        Settings.Secure.putInt(getContentResolver(), "sync_on_wifi_only", z ? 1 : 0);
        trackWifiOnlyEnabled(z);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiManageAccountsSettings.class.getName();
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase, com.android.settingslib.accounts.AuthenticatorHelper.OnAccountsUpdateListener
    public /* bridge */ /* synthetic */ void onAccountsUpdate(UserHandle userHandle) {
        super.onAccountsUpdate(userHandle);
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase, com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        TextView textView = (TextView) getView().findViewById(R.id.sync_settings_error_info);
        this.mErrorInfoView = textView;
        textView.setVisibility(8);
        this.mSyncEnable.setChecked(ContentResolver.getMasterSyncAutomatically());
        CheckBoxPreference checkBoxPreference = this.mSyncWifiOnly;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(Settings.Secure.getInt(getContentResolver(), "sync_on_wifi_only", 0) == 1);
        }
        this.mAuthorities = this.mActivity.getIntent().getStringArrayExtra("authorities");
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        FragmentActivity activity = getActivity();
        this.mActivity = activity;
        this.mSyncDrawable = new SyncDrawable(activity);
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.manage_accounts_settings_miui);
        setHasOptionsMenu(true);
        this.mSyncEnable = (CheckBoxPreference) findPreference("sync_enable");
        this.mSyncWifiOnly = (CheckBoxPreference) findPreference("wifi_only");
        this.mSyncEnable.setOnPreferenceChangeListener(this);
        this.mSyncWifiOnly.setOnPreferenceChangeListener(this);
        this.mCloudServicePref = (ValuePreference) findPreference("xiaomi_cloud_service");
        if (Utils.isWifiOnly(this.mActivity)) {
            getPreferenceScreen().removePreference(this.mSyncWifiOnly);
            this.mSyncWifiOnly = null;
        }
        this.mCloudServicePref.setShowRightArrow(true);
        this.mOtherAccountCategory = (PreferenceCategory) findPreference("account_other");
        try {
            ApplicationInfo applicationInfo = AppGlobals.getPackageManager().getApplicationInfo(Constants.CLOUDSERVICE_PACKAGE_NAME, 0, 0);
            if (applicationInfo != null) {
                this.mCloudServicePref.setIcon(applicationInfo.loadIcon(getPackageManager()));
            }
        } catch (RemoteException e) {
            Log.e("MiuiManageAccountsSettings", "RemoteException：", e);
        }
        this.mCloudServicePref.setLayoutResource(R.layout.preference_system_app);
        trackViewOpened();
        AlertDialog create = new AlertDialog.Builder(this.mActivity).setTitle(R.string.sure_sync_now).setPositiveButton(R.string.sync_now, new DialogInterface.OnClickListener() { // from class: com.android.settings.accounts.MiuiManageAccountsSettings.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MiuiManageAccountsSettings.this.turnOnSyncs(true);
            }
        }).setNegativeButton(R.string.dlg_cancel, (DialogInterface.OnClickListener) null).create();
        this.dialog = create;
        create.setCanceledOnTouchOutside(false);
        if (this.mUserHandle.getIdentifier() == 999) {
            this.mSyncAccountCategory = (PreferenceCategory) findPreference("account_sync");
            this.mXiaoMiAccountCategory = (PreferenceCategory) findPreference("account_xiaomi");
            getPreferenceScreen().removePreference(this.mSyncAccountCategory);
            getPreferenceScreen().removePreference(this.mXiaoMiAccountCategory);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.add(0, 1, 0, R.string.account_add).setIcon(R.drawable.action_button_new).setShowAsAction(1);
        menu.add(0, 2, 0, R.string.sync_menu_sync_now).setIcon(this.mSyncDrawable.getAnimationIcon()).setShowAsAction(5);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.manage_accounts_screen_miui, viewGroup, false);
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(R.id.prefs_container);
        viewGroup2.addView(super.onCreateView(layoutInflater, viewGroup2, bundle));
        return inflate;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.dialog.dismiss();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onDetach() {
        this.mActivity = null;
        super.onDetach();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            startFragment(this, MiuiChooseAccountFragment.class.getName(), -1, (Bundle) null, R.string.account_add);
            return true;
        } else if (itemId != 2) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            if (this.mSyncing) {
                onSyncCancel();
            } else {
                onSyncRequest();
            }
            return true;
        }
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        MiStatInterfaceUtils.trackPageEnd("account_list");
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        if ("sync_enable".equals(preference.getKey())) {
            updateSyncEnablePreference(((Boolean) obj).booleanValue());
            return true;
        } else if ("wifi_only".equals(key)) {
            updateSyncWifiOnlyPreference(((Boolean) obj).booleanValue());
            return true;
        } else {
            return false;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if ("xiaomi_cloud_service".equals(preference.getKey())) {
            MiStatInterfaceUtils.trackEvent("account_list_to_cloud_service");
            OneTrackInterfaceUtils.track("account_list_to_cloud_service", null);
            Intent intent = new Intent(Constants.Intents.ACTION_VIEW_CLOUD);
            intent.setPackage(Constants.CLOUDSERVICE_PACKAGE_NAME);
            preference.setIntent(intent);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem findItem = menu.findItem(2);
        if (findItem != null) {
            if (syncableAccountAvailable()) {
                findItem.setEnabled(true);
                if (this.mSyncing) {
                    findItem.setTitle(R.string.sync_in_progress);
                    playAnimation(findItem);
                } else {
                    findItem.setTitle(R.string.sync_menu_sync_now);
                    stopAnimation(findItem);
                }
            } else {
                findItem.setEnabled(false);
                findItem.setTitle(R.string.sync_menu_sync_now);
            }
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        loadOtherAccount();
        MiStatInterfaceUtils.trackPageStart("account_list");
    }

    @Override // com.android.settings.accounts.MiuiAccountPreferenceBase
    protected void onSyncStateUpdated() {
        if (this.mActivity == null) {
            return;
        }
        this.mSyncEnable.setChecked(ContentResolver.getMasterSyncAutomatically());
        boolean z = !ContentResolver.getCurrentSyncs().isEmpty();
        if (this.mSyncing != z) {
            this.mSyncing = z;
        }
        invalidateOptionsMenu();
    }
}
