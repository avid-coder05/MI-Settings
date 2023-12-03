package com.android.settings.notification;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.notification.MiuiNotificationBackend;
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
    private Comparator<NotificationChannel> mChannelComparator = new Comparator<NotificationChannel>() { // from class: com.android.settings.notification.AppNotificationSettings.15
        @Override // java.util.Comparator
        public int compare(NotificationChannel notificationChannel, NotificationChannel notificationChannel2) {
            return notificationChannel.isDeleted() != notificationChannel2.isDeleted() ? Boolean.compare(notificationChannel.isDeleted(), notificationChannel2.isDeleted()) : notificationChannel.getId().compareTo(notificationChannel2.getId());
        }
    };
    private Comparator<NotificationChannelGroup> mChannelGroupComparator = new Comparator<NotificationChannelGroup>() { // from class: com.android.settings.notification.AppNotificationSettings.16
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
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.AppNotificationSettings.12
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                notificationChannelGroup.setBlocked(!((Boolean) obj).booleanValue());
                AppNotificationSettings appNotificationSettings = AppNotificationSettings.this;
                MiuiNotificationBackend miuiNotificationBackend = appNotificationSettings.mBackend;
                MiuiNotificationBackend.AppRow appRow = appNotificationSettings.mAppRow;
                miuiNotificationBackend.updateChannelGroup(appRow.pkg, appRow.uid, notificationChannelGroup);
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
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.AppNotificationSettings.13
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference2) {
                Bundle bundle = new Bundle();
                bundle.putString("android.provider.extra.CHANNEL_ID", notificationChannel.getId());
                bundle.putString(FunctionColumns.PACKAGE, str);
                bundle.putInt("uid", TextUtils.equals(AppNotificationSettings.this.mPkg, str) ^ true ? -1 : AppNotificationSettings.this.mUid);
                bundle.putString("miui.targetPkg", AppNotificationSettings.this.mPkg);
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
        this.mAggregate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.AppNotificationSettings.4
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
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.AppNotificationSettings.7
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                NotificationSettingsHelper.setFloat(AppNotificationSettings.this.getContext(), AppNotificationSettings.this.mPkg, null, ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }

    private void setupAllowKeyguard() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("allow_keyguard");
        this.mAllowKeyguard = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.AppNotificationSettings.8
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                NotificationSettingsHelper.setShowKeyguard(AppNotificationSettings.this.getContext(), AppNotificationSettings.this.mPkg, null, ((Boolean) obj).booleanValue());
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
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.AppNotificationSettings.11
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                NotificationSettingsHelper.setLights(AppNotificationSettings.this.getContext(), AppNotificationSettings.this.mPkg, null, ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }

    private void setupAllowSound() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("allow_sound");
        this.mAllowSound = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.AppNotificationSettings.9
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                NotificationSettingsHelper.setSound(AppNotificationSettings.this.getContext(), AppNotificationSettings.this.mPkg, null, ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }

    private void setupAllowVibrate() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("allow_vibrate");
        this.mAllowVibrate = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.AppNotificationSettings.10
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                NotificationSettingsHelper.setVibrate(AppNotificationSettings.this.getContext(), AppNotificationSettings.this.mPkg, null, ((Boolean) obj).booleanValue());
                return true;
            }
        });
    }

    private void setupAppLinkPref() {
        if (this.mAppRow.settingsIntent != null) {
            findPreference("app_link").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.AppNotificationSettings.14
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    AppNotificationSettings.this.getActivity().startActivityAsUser(AppNotificationSettings.this.mAppRow.settingsIntent, UserHandle.CURRENT);
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
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.AppNotificationSettings.5
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                AppNotificationSettings.this.mAppRow.showBadge = ((Boolean) obj).booleanValue();
                AppNotificationSettings appNotificationSettings = AppNotificationSettings.this;
                appNotificationSettings.mBackend.setShowBadge(appNotificationSettings.mContext, appNotificationSettings.mPkg, appNotificationSettings.mUid, appNotificationSettings.mAppRow.showBadge);
                AppNotificationSettings appNotificationSettings2 = AppNotificationSettings.this;
                NotificationSettingsHelper.setShowBadge(appNotificationSettings2.mContext, appNotificationSettings2.mPkg, appNotificationSettings2.mAppRow.showBadge);
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
        this.mBlock.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.AppNotificationSettings.2
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                NotificationChannel notificationChannel;
                boolean booleanValue = ((Boolean) obj).booleanValue();
                AppNotificationSettings appNotificationSettings = AppNotificationSettings.this;
                if (appNotificationSettings.mShowLegacyConfig && (notificationChannel = appNotificationSettings.mChannel) != null) {
                    notificationChannel.setImportance(booleanValue ? appNotificationSettings.mBackupImportance : 0);
                    AppNotificationSettings.this.mChannel.lockFields(4);
                    AppNotificationSettings appNotificationSettings2 = AppNotificationSettings.this;
                    appNotificationSettings2.mBackend.updateChannel(appNotificationSettings2.mPkg, appNotificationSettings2.mUid, appNotificationSettings2.mChannel);
                }
                NotificationSettingsHelper.setNotificationsEnabledForPackage(AppNotificationSettings.this.getContext(), AppNotificationSettings.this.mPkg, booleanValue);
                AppNotificationSettings appNotificationSettings3 = AppNotificationSettings.this;
                boolean z = !booleanValue;
                appNotificationSettings3.mAppRow.banned = z;
                appNotificationSettings3.updateDependents(z);
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
        this.mFold.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.AppNotificationSettings.3
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
            checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.AppNotificationSettings.6
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    boolean booleanValue = ((Boolean) obj).booleanValue();
                    AppNotificationSettings appNotificationSettings = AppNotificationSettings.this;
                    appNotificationSettings.mBackend.setPriority(appNotificationSettings.mPkg, appNotificationSettings.mUid, booleanValue ? 2 : 0);
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
        new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.notification.AppNotificationSettings.1
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Code restructure failed: missing block: B:7:0x0033, code lost:
            
                if (r1.onlyHasDefaultChannel(r0.pkg, r0.uid) != false) goto L8;
             */
            /* JADX WARN: Removed duplicated region for block: B:12:0x0041  */
            /* JADX WARN: Removed duplicated region for block: B:19:0x006c  */
            @Override // android.os.AsyncTask
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public java.lang.Void doInBackground(java.lang.Void... r6) {
                /*
                    r5 = this;
                    com.android.settings.notification.AppNotificationSettings r6 = com.android.settings.notification.AppNotificationSettings.this
                    android.content.Context r0 = r6.getContext()
                    com.android.settings.notification.AppNotificationSettings r1 = com.android.settings.notification.AppNotificationSettings.this
                    android.content.pm.PackageManager r2 = r1.mPm
                    com.android.settings.notification.MiuiNotificationBackend r3 = r1.mBackend
                    java.lang.String r4 = r1.mPkg
                    int r1 = r1.mUserId
                    android.app.NotificationChannelGroup r0 = com.android.settings.utils.XmsfUtils.getXmsfChannels(r0, r2, r3, r4, r1)
                    com.android.settings.notification.AppNotificationSettings.access$002(r6, r0)
                    com.android.settings.notification.AppNotificationSettings r6 = com.android.settings.notification.AppNotificationSettings.this
                    android.app.NotificationChannelGroup r0 = com.android.settings.notification.AppNotificationSettings.access$000(r6)
                    if (r0 != 0) goto L37
                    int r0 = android.os.Build.VERSION.SDK_INT
                    r1 = 26
                    if (r0 < r1) goto L35
                    com.android.settings.notification.AppNotificationSettings r0 = com.android.settings.notification.AppNotificationSettings.this
                    com.android.settings.notification.MiuiNotificationBackend r1 = r0.mBackend
                    com.android.settings.notification.MiuiNotificationBackend$AppRow r0 = r0.mAppRow
                    java.lang.String r2 = r0.pkg
                    int r0 = r0.uid
                    boolean r0 = r1.onlyHasDefaultChannel(r2, r0)
                    if (r0 == 0) goto L37
                L35:
                    r0 = 1
                    goto L38
                L37:
                    r0 = 0
                L38:
                    r6.mShowLegacyConfig = r0
                    com.android.settings.notification.AppNotificationSettings r6 = com.android.settings.notification.AppNotificationSettings.this
                    boolean r0 = r6.mShowLegacyConfig
                    r1 = 0
                    if (r0 == 0) goto L6c
                    com.android.settings.notification.MiuiNotificationBackend r0 = r6.mBackend
                    com.android.settings.notification.MiuiNotificationBackend$AppRow r2 = r6.mAppRow
                    java.lang.String r3 = r2.pkg
                    int r2 = r2.uid
                    java.lang.String r4 = "miscellaneous"
                    android.app.NotificationChannel r0 = r0.getChannel(r3, r2, r4, r1)
                    r6.mChannel = r0
                    com.android.settings.notification.AppNotificationSettings r6 = com.android.settings.notification.AppNotificationSettings.this
                    android.app.NotificationChannel r0 = r6.mChannel
                    if (r0 == 0) goto L67
                    boolean r0 = r6.isChannelBlocked(r0)
                    if (r0 != 0) goto L67
                    com.android.settings.notification.AppNotificationSettings r5 = com.android.settings.notification.AppNotificationSettings.this
                    android.app.NotificationChannel r5 = r5.mChannel
                    int r5 = r5.getImportance()
                    goto L69
                L67:
                    r5 = -1000(0xfffffffffffffc18, float:NaN)
                L69:
                    r6.mBackupImportance = r5
                    goto L8c
                L6c:
                    com.android.settings.notification.MiuiNotificationBackend r0 = r6.mBackend
                    java.lang.String r2 = r6.mPkg
                    int r3 = r6.mUid
                    android.content.pm.ParceledListSlice r0 = r0.getChannelGroups(r2, r3)
                    java.util.List r0 = r0.getList()
                    com.android.settings.notification.AppNotificationSettings.access$102(r6, r0)
                    com.android.settings.notification.AppNotificationSettings r6 = com.android.settings.notification.AppNotificationSettings.this
                    java.util.List r6 = com.android.settings.notification.AppNotificationSettings.access$100(r6)
                    com.android.settings.notification.AppNotificationSettings r5 = com.android.settings.notification.AppNotificationSettings.this
                    java.util.Comparator r5 = com.android.settings.notification.AppNotificationSettings.access$200(r5)
                    java.util.Collections.sort(r6, r5)
                L8c:
                    return r1
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.settings.notification.AppNotificationSettings.AnonymousClass1.doInBackground(java.lang.Void[]):java.lang.Void");
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Void r2) {
                if (AppNotificationSettings.this.getHost() == null) {
                    return;
                }
                AppNotificationSettings appNotificationSettings = AppNotificationSettings.this;
                if (appNotificationSettings.mShowLegacyConfig) {
                    appNotificationSettings.setupDefaultPrefs();
                } else {
                    appNotificationSettings.removeDefaultPrefs();
                    AppNotificationSettings.this.populateChannelList();
                }
                AppNotificationSettings appNotificationSettings2 = AppNotificationSettings.this;
                appNotificationSettings2.updateDependents(appNotificationSettings2.mAppRow.banned);
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
