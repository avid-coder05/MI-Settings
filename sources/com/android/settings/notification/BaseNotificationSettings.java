package com.android.settings.notification;

import android.app.NotificationChannel;
import android.app.NotificationChannelCompat;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.BaseSettingsPreferenceFragment;
import com.android.settings.R;
import com.android.settings.notification.MiuiNotificationBackend;
import com.android.settings.search.FunctionColumns;
import com.android.settings.utils.XmsfUtils;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import java.util.Iterator;
import java.util.List;
import miui.content.res.ThemeResources;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AppCompatActivity;
import miuix.preference.DropDownPreference;

/* loaded from: classes2.dex */
public class BaseNotificationSettings extends BaseSettingsPreferenceFragment {
    protected CheckBoxPreference mAllowFloat;
    protected CheckBoxPreference mAllowKeyguard;
    protected CheckBoxPreference mAllowLights;
    protected CheckBoxPreference mAllowSound;
    protected CheckBoxPreference mAllowVibrate;
    protected MiuiNotificationBackend.AppRow mAppRow;
    protected CheckBoxPreference mBadge;
    protected CheckBoxPreference mBlock;
    protected NotificationChannel mChannel;
    protected Context mContext;
    private String mConversationId;
    protected boolean mCreated;
    protected DropDownPreference mImportance;
    protected LockPatternUtils mLockPatternUtils;
    protected NotificationManager mNm;
    protected String mPkg;
    protected PackageInfo mPkgInfo;
    protected PackageManager mPm;
    protected CheckBoxPreference mPriority;
    protected String mTargetPkg;
    protected int mUid;
    protected UserManager mUm;
    protected int mUserId;
    protected ValuePreference mVisibilityOverride;
    protected static final boolean DEBUG = Log.isLoggable("NotifiSettings", 3);
    private static final Intent APP_NOTIFICATION_PREFS_CATEGORY_INTENT = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.NOTIFICATION_PREFERENCES");
    protected MiuiNotificationBackend mBackend = new MiuiNotificationBackend();
    protected boolean mShowLegacyConfig = false;
    protected int mBackupImportance = -1000;

    private void applyConfigActivities(ArrayMap<String, MiuiNotificationBackend.AppRow> arrayMap, List<ResolveInfo> list) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("Found ");
            sb.append(list.size());
            sb.append(" preference activities");
            sb.append(list.size() == 0 ? " ;_;" : "");
            Log.d("NotifiSettings", sb.toString());
        }
        Iterator<ResolveInfo> it = list.iterator();
        while (it.hasNext()) {
            ActivityInfo activityInfo = it.next().activityInfo;
            MiuiNotificationBackend.AppRow appRow = arrayMap.get(activityInfo.applicationInfo.packageName);
            if (appRow == null) {
                if (DEBUG) {
                    Log.v("NotifiSettings", "Ignoring notification preference activity (" + activityInfo.name + ") for unknown package " + activityInfo.packageName);
                }
            } else if (appRow.settingsIntent == null) {
                Intent className = new Intent(APP_NOTIFICATION_PREFS_CATEGORY_INTENT).setClassName(activityInfo.packageName, activityInfo.name);
                appRow.settingsIntent = className;
                NotificationChannel notificationChannel = this.mChannel;
                if (notificationChannel != null) {
                    className.putExtra("android.intent.extra.CHANNEL_ID", notificationChannel.getId());
                }
            } else if (DEBUG) {
                Log.v("NotifiSettings", "Ignoring duplicate notification preference activity (" + activityInfo.name + ") for package " + activityInfo.packageName);
            }
        }
    }

    private PackageInfo findPackageInfo(String str, int i) {
        String[] packagesForUid;
        if (!TextUtils.isEmpty(str) && i >= 0 && (packagesForUid = this.mPm.getPackagesForUid(i)) != null) {
            for (String str2 : packagesForUid) {
                if (str.equals(str2)) {
                    try {
                        return this.mPm.getPackageInfo(str, 64);
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.w("NotifiSettings", "Failed to load package " + str, e);
                    }
                }
            }
        }
        return null;
    }

    private int getGlobalVisibility() {
        if (getLockscreenNotificationsEnabled()) {
            return !getLockscreenAllowPrivateNotifications() ? 0 : 1;
        }
        return -1;
    }

    private boolean getLockscreenAllowPrivateNotifications() {
        return Settings.Secure.getInt(getContentResolver(), "lock_screen_allow_private_notifications", 0) != 0;
    }

    private List<ResolveInfo> queryNotificationConfigActivities() {
        if (DEBUG) {
            Log.d("NotifiSettings", "APP_NOTIFICATION_PREFS_CATEGORY_INTENT is " + APP_NOTIFICATION_PREFS_CATEGORY_INTENT);
        }
        return this.mPm.queryIntentActivities(APP_NOTIFICATION_PREFS_CATEGORY_INTENT, 0);
    }

    private void setOverridePrefValue(int i) {
        if (i == -1000 || (i == 0 && !isLockScreenSecure())) {
            i = getGlobalVisibility();
        }
        this.mVisibilityOverride.setValue(i == 1 ? getContext().getString(R.string.lock_screen_notifications_summary_show) : i == 0 ? getContext().getString(R.string.lock_screen_notifications_summary_hide) : getContext().getString(R.string.lock_screen_notifications_summary_disable));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean canFloat() {
        return canFloat(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean canFloat(String str) {
        return NotificationSettingsHelper.canFloat(getContext(), this.mTargetPkg, str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean canLights() {
        return canLights(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean canLights(String str) {
        return NotificationSettingsHelper.canLights(getContext(), this.mTargetPkg, str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean canShowKeyguard() {
        return canShowKeyguard(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean canShowKeyguard(String str) {
        return NotificationSettingsHelper.canShowKeyguard(getContext(), this.mTargetPkg, str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean canSound() {
        return canSound(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean canSound(String str) {
        return NotificationSettingsHelper.canSound(getContext(), this.mTargetPkg, str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean canVibrate() {
        return canVibrate(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean canVibrate(String str) {
        return NotificationSettingsHelper.canVibrate(getContext(), this.mTargetPkg, str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean checkCanBeVisible(int i, int i2) {
        return i == -1000 || i >= i2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void collectConfigActivities() {
        ArrayMap<String, MiuiNotificationBackend.AppRow> arrayMap = new ArrayMap<>();
        MiuiNotificationBackend.AppRow appRow = this.mAppRow;
        arrayMap.put(appRow.pkg, appRow);
        applyConfigActivities(arrayMap, queryNotificationConfigActivities());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean getLockscreenNotificationsEnabled() {
        return Settings.Secure.getInt(getContentResolver(), "lock_screen_show_notifications", 0) != 0;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return "NotifiSettings";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isChannelBlockable(NotificationChannel notificationChannel) {
        return !this.mAppRow.systemApp || XmsfUtils.isMipushChannel(notificationChannel.getId()) || NotificationChannelCompat.isBlockable(notificationChannel) || notificationChannel.getImportance() == 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isChannelBlocked(NotificationChannel notificationChannel) {
        return notificationChannel.getImportance() == 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isChannelConfigurable(NotificationChannel notificationChannel) {
        return !notificationChannel.getId().equals(this.mAppRow.lockedChannelId);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isLockScreenSecure() {
        if (this.mLockPatternUtils == null) {
            this.mLockPatternUtils = new LockPatternUtils(getActivity());
        }
        boolean isSecure = this.mLockPatternUtils.isSecure(UserHandle.myUserId());
        UserInfo profileParent = this.mUm.getProfileParent(UserHandle.myUserId());
        return profileParent != null ? isSecure | this.mLockPatternUtils.isSecure(profileParent.id) : isSecure;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void log(String str, Object... objArr) {
        if (DEBUG) {
            Log.d("NotifiSettings", String.format(str, objArr));
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (DEBUG) {
            Log.d("NotifiSettings", "onActivityCreated mCreated=" + this.mCreated);
        }
        if (this.mCreated) {
            Log.w("NotifiSettings", "onActivityCreated: ignoring duplicate call");
        } else {
            this.mCreated = true;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getActivity();
        Intent intent = getActivity().getIntent();
        Bundle arguments = getArguments();
        if (DEBUG) {
            Log.d("NotifiSettings", "onCreate getIntent()=" + intent + ", args=" + arguments);
        }
        if (intent == null && arguments == null) {
            Log.w("NotifiSettings", "No intent");
            toastAndFinish();
            return;
        }
        this.mPm = getPackageManager();
        this.mUm = (UserManager) this.mContext.getSystemService("user");
        this.mNm = NotificationManager.from(this.mContext);
        String stringExtra = (arguments == null || !arguments.containsKey(FunctionColumns.PACKAGE)) ? (arguments == null || !arguments.containsKey("packageName")) ? intent.getStringExtra("android.provider.extra.APP_PACKAGE") : arguments.getString("packageName") : arguments.getString(FunctionColumns.PACKAGE);
        this.mPkg = stringExtra;
        if (TextUtils.isEmpty(stringExtra) && intent != null) {
            this.mPkg = intent.getStringExtra("packageName");
        }
        int intExtra = (arguments == null || !arguments.containsKey("uid")) ? intent.getIntExtra("app_uid", -1) : arguments.getInt("uid");
        this.mUid = intExtra;
        this.mUserId = UserHandle.getUserId(intExtra);
        if (intent != null && intent.getIntExtra("userId", -10000) == 999) {
            this.mUserId = 999;
            try {
                this.mUid = this.mPm.getPackageUidAsUser(this.mPkg, 999);
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        if (this.mUid < 0) {
            try {
                this.mUid = this.mPm.getPackageUid(this.mPkg, 0);
            } catch (PackageManager.NameNotFoundException unused2) {
            }
        }
        this.mPkgInfo = findPackageInfo(this.mPkg, this.mUid);
        if (this.mUid < 0 || TextUtils.isEmpty(this.mPkg) || this.mPkgInfo == null) {
            Log.w("NotifiSettings", "Missing package or uid or packageinfo");
            toastAndFinish();
            return;
        }
        String stringExtra2 = (arguments == null || !arguments.containsKey("miui.targetPkg")) ? intent.getStringExtra("miui.targetPkg") : arguments.getString("miui.targetPkg");
        this.mTargetPkg = stringExtra2;
        if (TextUtils.isEmpty(stringExtra2)) {
            this.mTargetPkg = this.mPkg;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        PackageInfo packageInfo;
        super.onResume();
        if (this.mUid < 0 || TextUtils.isEmpty(this.mPkg) || (packageInfo = this.mPkgInfo) == null) {
            Log.w("NotifiSettings", "Missing package or uid or packageinfo");
            finish();
            return;
        }
        this.mAppRow = this.mBackend.loadAppRow(this.mContext, this.mPm, packageInfo);
        Intent intent = getActivity().getIntent();
        Bundle arguments = getArguments();
        String str = null;
        String stringExtra = (arguments == null || !arguments.containsKey("android.provider.extra.CHANNEL_ID")) ? intent != null ? intent.getStringExtra("android.provider.extra.CHANNEL_ID") : null : arguments.getString("android.provider.extra.CHANNEL_ID");
        if (arguments != null && arguments.containsKey("android.provider.extra.CONVERSATION_ID")) {
            str = arguments.getString("android.provider.extra.CONVERSATION_ID");
        } else if (intent != null) {
            str = intent.getStringExtra("android.provider.extra.CONVERSATION_ID");
        }
        if (stringExtra != null) {
            this.mChannel = this.mBackend.getChannel(this.mPkg, this.mUid, stringExtra, str);
        }
        if (str != null) {
            this.mConversationId = str;
        }
        log("onResume " + this.mAppRow.toString() + ", targetPkg=" + this.mTargetPkg, new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void refreshNotificationShade(boolean z) {
        Intent intent = new Intent("com.miui.app.ExtraStatusBarManager.action_refresh_notification");
        intent.setPackage(ThemeResources.SYSTEMUI_NAME);
        intent.putExtra("com.miui.app.ExtraStatusBarManager.extra_forbid_notification", z);
        intent.putExtra("app_packageName", this.mTargetPkg);
        NotificationChannel notificationChannel = this.mChannel;
        intent.putExtra("channel_id", notificationChannel == null ? "" : notificationChannel.getId());
        getActivity().sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setActionBarTitle(CharSequence charSequence) {
        ActionBar appCompatActionBar;
        if (!(getActivity() instanceof AppCompatActivity) || (appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar()) == null) {
            return;
        }
        appCompatActionBar.setTitle(charSequence);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setAggregatePrefValue(ValuePreference valuePreference) {
        valuePreference.setValue(NotificationSettingsHelper.getAggregateConfig(this.mContext, this.mTargetPkg) == 0 ? getContext().getString(R.string.aggregate_rule_group_enabled) : getContext().getString(R.string.aggregate_rule_group_disabled));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setChecked(Preference preference, boolean z) {
        if (preference != null && (preference instanceof CheckBoxPreference)) {
            ((CheckBoxPreference) preference).setChecked(z);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setEnabled(Preference preference, boolean z) {
        if (preference == null) {
            return;
        }
        preference.setEnabled(z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setFoldRuleValue(ValuePreference valuePreference) {
        int foldImportance = NotificationSettingsHelper.getFoldImportance(this.mContext, this.mTargetPkg);
        valuePreference.setValue(foldImportance == 0 ? getContext().getString(R.string.fold_rule_default) : foldImportance == 1 ? getContext().getString(R.string.fold_rule_important) : getContext().getString(R.string.fold_rule_unimportant));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setPrefVisible(Preference preference, boolean z) {
        if (preference == null || findPreference("main_category") == null) {
            return;
        }
        if ((getPreferenceScreen().findPreference(preference.getKey()) != null) == z) {
            return;
        }
        if (z) {
            ((PreferenceCategory) findPreference("main_category")).addPreference(preference);
        } else {
            ((PreferenceCategory) findPreference("main_category")).removePreference(preference);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setupVisOverridePref(int i) {
        log("setupVisOverridePref sensitive=%d", Integer.valueOf(i));
        this.mVisibilityOverride = (ValuePreference) findPreference("visibility_override");
        setOverridePrefValue(i);
        this.mVisibilityOverride.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.BaseNotificationSettings.1
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                BaseNotificationSettings baseNotificationSettings = BaseNotificationSettings.this;
                baseNotificationSettings.startAppNotificationRuleActivity(4, baseNotificationSettings.mChannel.getId(), BaseNotificationSettings.this.mConversationId);
                return true;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void startAppNotificationRuleActivity(int i, String str, String str2) {
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, this.mPkg);
        bundle.putString("miui.targetPkg", this.mTargetPkg);
        bundle.putInt("uid", this.mUid);
        Intent intent = new Intent();
        intent.putExtra("notification_settings_page_type", i);
        intent.putExtra("android.provider.extra.CHANNEL_ID", str);
        intent.putExtra("android.provider.extra.CONVERSATION_ID", str2);
        intent.putExtra(":android:show_fragment_args", bundle);
        intent.setClassName("com.miui.notification", "miui.notification.management.activity.settings.AppNotificationRuleActivity");
        try {
            getActivity().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e("NotifiSettings", "Failed startActivityAsUser() ", e);
        }
    }

    protected void toastAndFinish() {
        Toast.makeText(this.mContext, R.string.app_not_found_dlg_text, 0).show();
        getActivity().finish();
    }
}
