package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.notification.BaseNotificationSettings;
import com.android.settings.notification.NotificationSettingsHelper;
import com.android.settings.search.FunctionColumns;
import com.android.settings.utils.StatusBarUtils;
import com.android.settings.utils.XmsfUtils;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.miui.enterprise.ApplicationHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import miui.cloud.Constants;

/* loaded from: classes2.dex */
public class AppNotificationSettings extends BaseNotificationSettings {
    private ValuePreference mAggregate;
    private List<NotificationChannelGroup> mChannelGroupList;
    private ValuePreference mFold;
    private NotificationChannelGroup mXmsfFakeGroup;
    private List<PreferenceCategory> mChannelGroups = new ArrayList();
    private ContentObserver mFoldObserver = null;
    private ContentObserver mAggregateObserver = null;
    private int mOrder = 100;
    private Comparator<NotificationChannel> mChannelComparator = new Comparator<NotificationChannel>() { // from class: com.android.settings.notification.app.AppNotificationSettings.15
        @Override // java.util.Comparator
        public int compare(NotificationChannel notificationChannel, NotificationChannel notificationChannel2) {
            return notificationChannel.isDeleted() != notificationChannel2.isDeleted() ? Boolean.compare(notificationChannel.isDeleted(), notificationChannel2.isDeleted()) : notificationChannel.getId().compareTo(notificationChannel2.getId());
        }
    };
    private Comparator<NotificationChannelGroup> mChannelGroupComparator = new Comparator<NotificationChannelGroup>() { // from class: com.android.settings.notification.app.AppNotificationSettings.16
        @Override // java.util.Comparator
        public int compare(NotificationChannelGroup notificationChannelGroup, NotificationChannelGroup notificationChannelGroup2) {
            if (notificationChannelGroup.getId() != null || notificationChannelGroup2.getId() == null) {
                if (notificationChannelGroup2.getId() != null || notificationChannelGroup.getId() == null) {
                    return notificationChannelGroup.getId().compareTo(notificationChannelGroup2.getId());
                }
                return -1;
            }
            return 1;
        }
    };

    private void addChannelGroupBlockSwitch(PreferenceCategory preferenceCategory, final NotificationChannelGroup notificationChannelGroup) {
        CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getPreferenceManager().getContext());
        checkBoxPreference.setTitle(R.string.block_title);
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.app.AppNotificationSettings.12
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                notificationChannelGroup.setBlocked(!((Boolean) obj).booleanValue());
                ((BaseNotificationSettings) AppNotificationSettings.this).mBackend.updateChannelGroup(((BaseNotificationSettings) AppNotificationSettings.this).mAppRow.pkg, ((BaseNotificationSettings) AppNotificationSettings.this).mAppRow.uid, notificationChannelGroup);
                AppNotificationSettings.this.populateChannelList();
                return true;
            }
        });
        checkBoxPreference.setChecked(!notificationChannelGroup.isBlocked());
        preferenceCategory.addPreference(checkBoxPreference);
    }

    private void populateChannelGroup(NotificationChannelGroup notificationChannelGroup) {
        populateChannelGroup(notificationChannelGroup, this.mPkg);
    }

    private void populateChannelGroup(NotificationChannelGroup notificationChannelGroup, String str) {
        PreferenceCategory preferenceCategory = new PreferenceCategory(getPreferenceManager().getContext());
        int i = this.mOrder;
        this.mOrder = i + 1;
        preferenceCategory.setOrder(i);
        if (notificationChannelGroup.getId() == null) {
            preferenceCategory.setTitle(this.mChannelGroupList.size() > 1 ? R.string.notifi_channels_other : R.string.notifi_channels);
            preferenceCategory.setKey("categories");
        } else {
            preferenceCategory.setTitle(notificationChannelGroup.getName());
            preferenceCategory.setKey(notificationChannelGroup.getId());
        }
        preferenceCategory.setOrderingAsAdded(true);
        getPreferenceScreen().addPreference(preferenceCategory);
        this.mChannelGroups.add(preferenceCategory);
        if (notificationChannelGroup.getId() != null && !"xmsf_fake_channel_group".equals(notificationChannelGroup.getId())) {
            addChannelGroupBlockSwitch(preferenceCategory, notificationChannelGroup);
        }
        List<NotificationChannel> channels = notificationChannelGroup.getChannels();
        Collections.sort(channels, this.mChannelComparator);
        int size = channels.size();
        for (int i2 = 0; i2 < size; i2++) {
            populateSingleChannelPrefs(preferenceCategory, channels.get(i2), str);
        }
        for (int i3 = 1; i3 < preferenceCategory.getPreferenceCount(); i3++) {
            preferenceCategory.getPreference(i3).setEnabled(!notificationChannelGroup.isBlocked());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void populateChannelList() {
        XmsfUtils.filterChannels(this.mPkg, this.mChannelGroupList);
        if (!this.mChannelGroups.isEmpty()) {
            Log.w("NotifiSettings", "Notification channel group posted twice to settings - old size " + this.mChannelGroups.size() + ", new size " + this.mChannelGroupList.size());
            Iterator<PreferenceCategory> it = this.mChannelGroups.iterator();
            while (it.hasNext()) {
                getPreferenceScreen().removePreference(it.next());
            }
        }
        if (this.mChannelGroupList.isEmpty()) {
            PreferenceCategory preferenceCategory = new PreferenceCategory(getPreferenceManager().getContext());
            preferenceCategory.setTitle(R.string.notifi_channels);
            preferenceCategory.setKey("categories");
            getPreferenceScreen().addPreference(preferenceCategory);
            this.mChannelGroups.add(preferenceCategory);
            Preference preference = new Preference(getPreferenceManager().getContext());
            preference.setTitle(R.string.notifi_no_channels);
            preference.setEnabled(false);
            preferenceCategory.addPreference(preference);
        } else {
            Iterator<NotificationChannelGroup> it2 = this.mChannelGroupList.iterator();
            while (it2.hasNext()) {
                populateChannelGroup(it2.next());
            }
        }
        NotificationChannelGroup notificationChannelGroup = this.mXmsfFakeGroup;
        if (notificationChannelGroup != null) {
            populateChannelGroup(notificationChannelGroup, Constants.XMSF_PACKAGE_NAME);
        }
        updateDependents(this.mAppRow.banned);
    }

    private void populateSingleChannelPrefs(PreferenceCategory preferenceCategory, final NotificationChannel notificationChannel, final String str) {
        Preference preference = new Preference(getPreferenceManager().getContext());
        preference.setTitle(notificationChannel.getName());
        preference.setSummary(notificationChannel.getDescription());
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.app.AppNotificationSettings.13
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference2) {
                Bundle bundle = new Bundle();
                bundle.putString("android.provider.extra.CHANNEL_ID", notificationChannel.getId());
                bundle.putString(FunctionColumns.PACKAGE, str);
                bundle.putInt("uid", TextUtils.equals(((BaseNotificationSettings) AppNotificationSettings.this).mPkg, str) ^ true ? -1 : ((BaseNotificationSettings) AppNotificationSettings.this).mUid);
                bundle.putString("miui.targetPkg", ((BaseNotificationSettings) AppNotificationSettings.this).mPkg);
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.putExtra(":android:show_fragment_args", bundle);
                intent.putExtra(":android:show_fragment", "com.android.settings.notification.ChannelNotificationSettings");
                intent.setClassName("com.android.settings", "com.android.settings.SubSettings");
                try {
                    AppNotificationSettings.this.getActivity().startActivityAsUser(intent, UserHandle.CURRENT);
                } catch (ActivityNotFoundException e) {
                    Log.e("NotifiSettings", "Failed startActivityAsUser() ", e);
                }
                return true;
            }
        });
        preferenceCategory.addPreference(preference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeDefaultPrefs() {
        setPrefVisible(findPreference("priority"), false);
        setPrefVisible(findPreference("importance"), false);
        setPrefVisible(findPreference("visibility_override"), false);
    }

    private void setupAggregate() {
        if (!StatusBarUtils.isUserAggregate(getContext()) || !NotificationSettingsHelper.isFoldable(getContext(), this.mPkg)) {
            removePreference("aggregate_category");
            return;
        }
        ValuePreference valuePreference = (ValuePreference) findPreference("aggregate");
        this.mAggregate = valuePreference;
        setAggregatePrefValue(valuePreference);
        this.mAggregate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.app.AppNotificationSettings.4
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                AppNotificationSettings.this.startAppNotificationRuleActivity(2, "", "");
                return true;
            }
        });
    }

    private void setupAllowFloat() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("allow_float");
        this.mAllowFloat = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.app.AppNotificationSettings.7
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                NotificationSettingsHelper.setFloat(AppNotificationSettings.this.getContext(), ((BaseNotificationSettings) AppNotificationSettings.this).mPkg, null, ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }

    private void setupAllowKeyguard() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("allow_keyguard");
        this.mAllowKeyguard = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.app.AppNotificationSettings.8
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                NotificationSettingsHelper.setShowKeyguard(AppNotificationSettings.this.getContext(), ((BaseNotificationSettings) AppNotificationSettings.this).mPkg, null, ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }

    private void setupAllowLights() {
        if (!StatusBarUtils.IS_SUPPORT_LED) {
            setPrefVisible(findPreference("allow_lights"), false);
            return;
        }
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("allow_lights");
        this.mAllowLights = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.app.AppNotificationSettings.11
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                NotificationSettingsHelper.setLights(AppNotificationSettings.this.getContext(), ((BaseNotificationSettings) AppNotificationSettings.this).mPkg, null, ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }

    private void setupAllowSound() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("allow_sound");
        this.mAllowSound = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.app.AppNotificationSettings.9
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                NotificationSettingsHelper.setSound(AppNotificationSettings.this.getContext(), ((BaseNotificationSettings) AppNotificationSettings.this).mPkg, null, ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }

    private void setupAllowVibrate() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("allow_vibrate");
        this.mAllowVibrate = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.app.AppNotificationSettings.10
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                NotificationSettingsHelper.setVibrate(AppNotificationSettings.this.getContext(), ((BaseNotificationSettings) AppNotificationSettings.this).mPkg, null, ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }

    private void setupAppLinkPref() {
        if (this.mAppRow.settingsIntent != null) {
            findPreference("app_link").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.app.AppNotificationSettings.14
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    AppNotificationSettings.this.getActivity().startActivityAsUser(((BaseNotificationSettings) AppNotificationSettings.this).mAppRow.settingsIntent, UserHandle.CURRENT);
                    return true;
                }
            });
        } else {
            removePreference("app_category");
        }
    }

    private void setupBadge() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("badge");
        this.mBadge = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.app.AppNotificationSettings.5
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                ((BaseNotificationSettings) AppNotificationSettings.this).mAppRow.showBadge = ((Boolean) obj).booleanValue();
                ((BaseNotificationSettings) AppNotificationSettings.this).mBackend.setShowBadge(((BaseNotificationSettings) AppNotificationSettings.this).mContext, ((BaseNotificationSettings) AppNotificationSettings.this).mPkg, ((BaseNotificationSettings) AppNotificationSettings.this).mUid, ((BaseNotificationSettings) AppNotificationSettings.this).mAppRow.showBadge);
                NotificationSettingsHelper.setShowBadge(((BaseNotificationSettings) AppNotificationSettings.this).mContext, ((BaseNotificationSettings) AppNotificationSettings.this).mPkg, ((BaseNotificationSettings) AppNotificationSettings.this).mAppRow.showBadge);
                AppNotificationSettings.this.refreshNotificationShade(false);
                return true;
            }
        });
    }

    private void setupBlock() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("block");
        this.mBlock = checkBoxPreference;
        checkBoxPreference.setChecked(!this.mAppRow.banned);
        this.mBlock.setEnabled(!this.mAppRow.systemApp);
        this.mBlock.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.app.AppNotificationSettings.2
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                boolean booleanValue = ((Boolean) obj).booleanValue();
                if (((BaseNotificationSettings) AppNotificationSettings.this).mShowLegacyConfig && ((BaseNotificationSettings) AppNotificationSettings.this).mChannel != null) {
                    ((BaseNotificationSettings) AppNotificationSettings.this).mChannel.setImportance(booleanValue ? ((BaseNotificationSettings) AppNotificationSettings.this).mBackupImportance : 0);
                    ((BaseNotificationSettings) AppNotificationSettings.this).mChannel.lockFields(4);
                    ((BaseNotificationSettings) AppNotificationSettings.this).mBackend.updateChannel(((BaseNotificationSettings) AppNotificationSettings.this).mPkg, ((BaseNotificationSettings) AppNotificationSettings.this).mUid, ((BaseNotificationSettings) AppNotificationSettings.this).mChannel);
                }
                NotificationSettingsHelper.setNotificationsEnabledForPackage(AppNotificationSettings.this.getContext(), ((BaseNotificationSettings) AppNotificationSettings.this).mPkg, booleanValue);
                ((BaseNotificationSettings) AppNotificationSettings.this).mAppRow.banned = !booleanValue;
                AppNotificationSettings appNotificationSettings = AppNotificationSettings.this;
                appNotificationSettings.updateDependents(((BaseNotificationSettings) appNotificationSettings).mAppRow.banned);
                if (!booleanValue) {
                    AppNotificationSettings.this.refreshNotificationShade(true);
                }
                return true;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setupDefaultPrefs() {
        setupPriority();
        NotificationChannel notificationChannel = this.mChannel;
        setupVisOverridePref(notificationChannel != null ? notificationChannel.getLockscreenVisibility() : this.mAppRow.appVisOverride);
    }

    private void setupFold() {
        if (!StatusBarUtils.isUserFold(getContext()) || !NotificationSettingsHelper.isFoldable(getContext(), this.mPkg)) {
            removePreference("fold_category");
            return;
        }
        ValuePreference valuePreference = (ValuePreference) findPreference("fold");
        this.mFold = valuePreference;
        setFoldRuleValue(valuePreference);
        this.mFold.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.app.AppNotificationSettings.3
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                AppNotificationSettings.this.startAppNotificationRuleActivity(1, "", "");
                return true;
            }
        });
    }

    private void setupPriority() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("priority");
        this.mPriority = checkBoxPreference;
        if (StatusBarUtils.IS_SUPPORT_HIGH_PRIORITY) {
            checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.app.AppNotificationSettings.6
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ((BaseNotificationSettings) AppNotificationSettings.this).mBackend.setPriority(((BaseNotificationSettings) AppNotificationSettings.this).mPkg, ((BaseNotificationSettings) AppNotificationSettings.this).mUid, ((Boolean) obj).booleanValue() ? 2 : 0);
                    return true;
                }
            });
        } else {
            setPrefVisible(checkBoxPreference, false);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 72;
    }

    @Override // com.android.settings.notification.BaseNotificationSettings, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.mUid < 0 || TextUtils.isEmpty(this.mPkg) || this.mPkgInfo == null) {
            Log.w("NotifiSettings", "Missing package or uid or packageinfo");
            finish();
            return;
        }
        addPreferencesFromResource(R.xml.miui_app_notification_settings);
        getPreferenceScreen().setOrderingAsAdded(false);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        if (this.mFoldObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(this.mFoldObserver);
        }
        if (this.mAggregateObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(this.mAggregateObserver);
        }
    }

    @Override // com.android.settings.notification.BaseNotificationSettings, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        collectConfigActivities();
        setActionBarTitle(this.mAppRow.label);
        setupBlock();
        setupFold();
        setupAggregate();
        setupBadge();
        setupAllowFloat();
        setupAllowKeyguard();
        setupAllowSound();
        setupAllowVibrate();
        setupAllowLights();
        setupAppLinkPref();
        new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.notification.app.AppNotificationSettings.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... voidArr) {
                int i;
                AppNotificationSettings appNotificationSettings = AppNotificationSettings.this;
                appNotificationSettings.mXmsfFakeGroup = XmsfUtils.getXmsfChannels(appNotificationSettings.getContext(), ((BaseNotificationSettings) AppNotificationSettings.this).mPm, ((BaseNotificationSettings) AppNotificationSettings.this).mBackend, ((BaseNotificationSettings) AppNotificationSettings.this).mPkg, ((BaseNotificationSettings) AppNotificationSettings.this).mUserId);
                AppNotificationSettings appNotificationSettings2 = AppNotificationSettings.this;
                ((BaseNotificationSettings) appNotificationSettings2).mShowLegacyConfig = appNotificationSettings2.mXmsfFakeGroup == null && (Build.VERSION.SDK_INT < 26 || ((BaseNotificationSettings) AppNotificationSettings.this).mBackend.onlyHasDefaultChannel(((BaseNotificationSettings) AppNotificationSettings.this).mAppRow.pkg, ((BaseNotificationSettings) AppNotificationSettings.this).mAppRow.uid));
                if (((BaseNotificationSettings) AppNotificationSettings.this).mShowLegacyConfig) {
                    AppNotificationSettings appNotificationSettings3 = AppNotificationSettings.this;
                    ((BaseNotificationSettings) appNotificationSettings3).mChannel = ((BaseNotificationSettings) appNotificationSettings3).mBackend.getChannel(((BaseNotificationSettings) AppNotificationSettings.this).mAppRow.pkg, ((BaseNotificationSettings) AppNotificationSettings.this).mAppRow.uid, "miscellaneous", null);
                    AppNotificationSettings appNotificationSettings4 = AppNotificationSettings.this;
                    if (((BaseNotificationSettings) appNotificationSettings4).mChannel != null) {
                        AppNotificationSettings appNotificationSettings5 = AppNotificationSettings.this;
                        if (!appNotificationSettings5.isChannelBlocked(((BaseNotificationSettings) appNotificationSettings5).mChannel)) {
                            i = ((BaseNotificationSettings) AppNotificationSettings.this).mChannel.getImportance();
                            ((BaseNotificationSettings) appNotificationSettings4).mBackupImportance = i;
                        }
                    }
                    i = -1000;
                    ((BaseNotificationSettings) appNotificationSettings4).mBackupImportance = i;
                } else {
                    AppNotificationSettings appNotificationSettings6 = AppNotificationSettings.this;
                    appNotificationSettings6.mChannelGroupList = ((BaseNotificationSettings) appNotificationSettings6).mBackend.getChannelGroups(((BaseNotificationSettings) AppNotificationSettings.this).mPkg, ((BaseNotificationSettings) AppNotificationSettings.this).mUid).getList();
                    Collections.sort(AppNotificationSettings.this.mChannelGroupList, AppNotificationSettings.this.mChannelGroupComparator);
                }
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Void r1) {
                if (AppNotificationSettings.this.getHost() == null) {
                    return;
                }
                if (((BaseNotificationSettings) AppNotificationSettings.this).mShowLegacyConfig) {
                    AppNotificationSettings.this.setupDefaultPrefs();
                } else {
                    AppNotificationSettings.this.removeDefaultPrefs();
                    AppNotificationSettings.this.populateChannelList();
                }
                AppNotificationSettings appNotificationSettings = AppNotificationSettings.this;
                appNotificationSettings.updateDependents(((BaseNotificationSettings) appNotificationSettings).mAppRow.banned);
            }
        }.execute(new Void[0]);
    }

    protected void updateDependents(boolean z) {
        updateDependents(z, ApplicationHelper.shouldGrantPermission(getActivity(), this.mPkg, this.mUserId));
    }

    protected void updateDependents(boolean z, boolean z2) {
        boolean z3 = (z || z2) ? false : true;
        Iterator<PreferenceCategory> it = this.mChannelGroups.iterator();
        while (it.hasNext()) {
            it.next().setEnabled(z3);
        }
        setEnabled(this.mFold, z3);
        setEnabled(this.mAggregate, z3);
        setEnabled(this.mBadge, z3);
        setChecked(this.mBadge, !z && this.mAppRow.showBadge);
        setEnabled(this.mPriority, z3);
        setChecked(this.mPriority, !z && this.mBackend.getPriority(this.mPkg, this.mUid) == 2);
        CheckBoxPreference checkBoxPreference = this.mAllowFloat;
        if (checkBoxPreference != null) {
            setEnabled(checkBoxPreference, z3);
            setChecked(this.mAllowFloat, !z && canFloat());
        }
        if (this.mAllowKeyguard != null) {
            boolean lockscreenNotificationsEnabled = getLockscreenNotificationsEnabled();
            boolean z4 = !z && canShowKeyguard();
            setEnabled(this.mAllowKeyguard, z3 && lockscreenNotificationsEnabled);
            setChecked(this.mAllowKeyguard, z4);
            this.mAllowKeyguard.setSummary((z4 || lockscreenNotificationsEnabled) ? R.string.allow_keyguard_summary : R.string.disallow_lock_screen_summary);
        }
        setEnabled(this.mAllowSound, z3);
        setChecked(this.mAllowSound, !z && canSound());
        setEnabled(this.mAllowVibrate, z3);
        setChecked(this.mAllowVibrate, !z && canVibrate());
        setEnabled(this.mAllowLights, z3);
        setChecked(this.mAllowLights, !z && canLights());
        setEnabled(this.mVisibilityOverride, z3 && isLockScreenSecure());
    }
}
