package com.android.settings.notification;

import android.app.INotificationManager;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationHistory;
import android.app.role.RoleManager;
import android.app.usage.IUsageStatsManager;
import android.app.usage.UsageEvents;
import android.companion.ICompanionDeviceManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ShortcutInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.service.notification.ConversationChannelWrapper;
import android.service.notification.NotificationListenerFilter;
import android.util.IconDrawableFactory;
import android.util.Log;
import com.android.settingslib.R$dimen;
import com.android.settingslib.R$plurals;
import com.android.settingslib.R$string;
import com.android.settingslib.Utils;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.notification.ConversationIconFactory;
import com.android.settingslib.utils.StringUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import miui.app.constants.ThemeManagerConstants;
import miui.mipub.MipubStat;

/* loaded from: classes2.dex */
public class NotificationBackend {
    static IUsageStatsManager sUsageStatsManager = IUsageStatsManager.Stub.asInterface(ServiceManager.getService("usagestats"));
    static INotificationManager sINM = INotificationManager.Stub.asInterface(ServiceManager.getService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION));

    /* loaded from: classes2.dex */
    public static class AppRow extends Row {
        public boolean banned;
        public int blockedChannelCount;
        public int bubblePreference = 0;
        public int channelCount;
        public Drawable icon;
        public CharSequence label;
        public boolean lockedImportance;
        public String pkg;
        public NotificationsSentState sentByApp;
        public Map<String, NotificationsSentState> sentByChannel;
        public Intent settingsIntent;
        public boolean showBadge;
        public boolean systemApp;
        public int uid;
        public int userId;
    }

    /* loaded from: classes2.dex */
    public static class NotificationsSentState {
        public int avgSentDaily = 0;
        public int avgSentWeekly = 0;
        public long lastSent = 0;
        public int sentCount = 0;
    }

    /* loaded from: classes2.dex */
    static class Row {
        Row() {
        }
    }

    private void calculateAvgSentCounts(NotificationsSentState notificationsSentState) {
        if (notificationsSentState != null) {
            notificationsSentState.avgSentDaily = Math.round(notificationsSentState.sentCount / 7.0f);
            int i = notificationsSentState.sentCount;
            if (i < 7) {
                notificationsSentState.avgSentWeekly = i;
            }
        }
    }

    public static CharSequence getDeviceList(ICompanionDeviceManager iCompanionDeviceManager, LocalBluetoothManager localBluetoothManager, String str, int i) {
        StringBuilder sb = new StringBuilder();
        try {
            List<String> associations = iCompanionDeviceManager.getAssociations(str, i);
            if (associations != null) {
                boolean z = false;
                for (String str2 : associations) {
                    for (CachedBluetoothDevice cachedBluetoothDevice : localBluetoothManager.getCachedDeviceManager().getCachedDevicesCopy()) {
                        if (Objects.equals(str2, cachedBluetoothDevice.getAddress())) {
                            if (z) {
                                sb.append(", ");
                            } else {
                                z = true;
                            }
                            sb.append(cachedBluetoothDevice.getName());
                        }
                    }
                }
            }
        } catch (RemoteException e) {
            Log.w("NotificationBackend", "Error calling CDM", e);
        }
        return sb.toString();
    }

    public static CharSequence getSentSummary(Context context, NotificationsSentState notificationsSentState, boolean z) {
        if (notificationsSentState == null) {
            return null;
        }
        if (z) {
            return notificationsSentState.lastSent == 0 ? context.getString(R$string.notifications_sent_never) : StringUtil.formatRelativeTime(context, System.currentTimeMillis() - notificationsSentState.lastSent, true);
        } else if (notificationsSentState.avgSentDaily > 0) {
            Resources resources = context.getResources();
            int i = R$plurals.notifications_sent_daily;
            int i2 = notificationsSentState.avgSentDaily;
            return resources.getQuantityString(i, i2, Integer.valueOf(i2));
        } else {
            Resources resources2 = context.getResources();
            int i3 = R$plurals.notifications_sent_weekly;
            int i4 = notificationsSentState.avgSentWeekly;
            return resources2.getQuantityString(i3, i4, Integer.valueOf(i4));
        }
    }

    static void markAppRowWithBlockables(String[] strArr, AppRow appRow, String str) {
        if (strArr != null) {
            int length = strArr.length;
            for (int i = 0; i < length; i++) {
                String str2 = strArr[i];
                if (str2 != null && !str2.contains(":") && str.equals(strArr[i])) {
                    appRow.lockedImportance = true;
                    appRow.systemApp = true;
                }
            }
        }
    }

    public boolean canShowBadge(String str, int i) {
        try {
            return sINM.canShowBadge(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public void createConversationNotificationChannel(String str, int i, NotificationChannel notificationChannel, String str2) {
        try {
            sINM.createConversationNotificationChannelForPackage(str, i, notificationChannel, str2);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
        }
    }

    public ComponentName getAllowedNotificationAssistant() {
        try {
            return sINM.getAllowedNotificationAssistant();
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return null;
        }
    }

    public int getBlockedChannelCount(String str, int i) {
        try {
            return sINM.getBlockedChannelCount(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return 0;
        }
    }

    public int getBubblePreference(String str, int i) {
        try {
            return sINM.getBubblePreferenceForPackage(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return -1;
        }
    }

    public NotificationChannel getChannel(String str, int i, String str2, String str3) {
        if (str2 == null) {
            return null;
        }
        try {
            return sINM.getNotificationChannelForPackage(str, i, str2, str3, true);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return null;
        }
    }

    public int getChannelCount(String str, int i) {
        try {
            return sINM.getNumNotificationChannelsForPackage(str, i, false);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return 0;
        }
    }

    public Drawable getConversationDrawable(Context context, ShortcutInfo shortcutInfo, String str, int i, boolean z) {
        if (shortcutInfo == null) {
            return null;
        }
        return new ConversationIconFactory(context, (LauncherApps) context.getSystemService(LauncherApps.class), context.getPackageManager(), IconDrawableFactory.newInstance(context, false), context.getResources().getDimensionPixelSize(R$dimen.conversation_icon_size)).getConversationDrawable(shortcutInfo, str, i, z);
    }

    public ShortcutInfo getConversationInfo(Context context, String str, int i, String str2) {
        List<ShortcutInfo> shortcuts = ((LauncherApps) context.getSystemService(LauncherApps.class)).getShortcuts(new LauncherApps.ShortcutQuery().setPackage(str).setQueryFlags(1041).setShortcutIds(Arrays.asList(str2)), UserHandle.of(UserHandle.getUserId(i)));
        if (shortcuts == null || shortcuts.isEmpty()) {
            return null;
        }
        return shortcuts.get(0);
    }

    public ParceledListSlice<ConversationChannelWrapper> getConversations(String str, int i) {
        try {
            return sINM.getConversationsForPackage(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return ParceledListSlice.emptyList();
        }
    }

    public ParceledListSlice<ConversationChannelWrapper> getConversations(boolean z) {
        try {
            return sINM.getConversations(z);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return ParceledListSlice.emptyList();
        }
    }

    public ComponentName getDefaultNotificationAssistant() {
        try {
            return sINM.getDefaultNotificationAssistant();
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return null;
        }
    }

    public NotificationChannelGroup getGroup(String str, int i, String str2) {
        if (str2 == null) {
            return null;
        }
        try {
            return sINM.getNotificationChannelGroupForPackage(str2, str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return null;
        }
    }

    public ParceledListSlice<NotificationChannelGroup> getGroups(String str, int i) {
        try {
            return sINM.getNotificationChannelGroupsForPackage(str, i, false);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return ParceledListSlice.emptyList();
        }
    }

    public NotificationListenerFilter getListenerFilter(ComponentName componentName, int i) {
        NotificationListenerFilter notificationListenerFilter;
        try {
            notificationListenerFilter = sINM.getListenerFilter(componentName, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            notificationListenerFilter = null;
        }
        return notificationListenerFilter != null ? notificationListenerFilter : new NotificationListenerFilter();
    }

    public ParceledListSlice<NotificationChannel> getNotificationChannelsBypassingDnd(String str, int i) {
        try {
            return sINM.getNotificationChannelsBypassingDnd(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return ParceledListSlice.emptyList();
        }
    }

    public NotificationHistory getNotificationHistory(String str, String str2) {
        try {
            return sINM.getNotificationHistory(str, str2);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return new NotificationHistory();
        }
    }

    public boolean getNotificationsBanned(String str, int i) {
        try {
            return !sINM.areNotificationsEnabledForPackage(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public boolean isInInvalidMsgState(String str, int i) {
        try {
            return sINM.isInInvalidMsgState(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public boolean isNotificationListenerAccessGranted(ComponentName componentName) {
        try {
            return sINM.isNotificationListenerAccessGranted(componentName);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public boolean isSystemApp(Context context, ApplicationInfo applicationInfo) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(applicationInfo.packageName, 64);
            RoleManager roleManager = (RoleManager) context.getSystemService(RoleManager.class);
            AppRow appRow = new AppRow();
            recordCanBeBlocked(context, context.getPackageManager(), roleManager, packageInfo, appRow);
            return appRow.systemApp;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public AppRow loadAppRow(Context context, PackageManager packageManager, RoleManager roleManager, PackageInfo packageInfo) {
        AppRow loadAppRow = loadAppRow(context, packageManager, packageInfo.applicationInfo);
        recordCanBeBlocked(context, packageManager, roleManager, packageInfo, loadAppRow);
        return loadAppRow;
    }

    public AppRow loadAppRow(Context context, PackageManager packageManager, ApplicationInfo applicationInfo) {
        AppRow appRow = new AppRow();
        appRow.pkg = applicationInfo.packageName;
        appRow.uid = applicationInfo.uid;
        try {
            appRow.label = applicationInfo.loadLabel(packageManager);
        } catch (Throwable th) {
            Log.e("NotificationBackend", "Error loading application label for " + appRow.pkg, th);
            appRow.label = appRow.pkg;
        }
        appRow.icon = IconDrawableFactory.newInstance(context).getBadgedIcon(applicationInfo);
        appRow.banned = getNotificationsBanned(appRow.pkg, appRow.uid);
        appRow.showBadge = canShowBadge(appRow.pkg, appRow.uid);
        appRow.bubblePreference = getBubblePreference(appRow.pkg, appRow.uid);
        appRow.userId = UserHandle.getUserId(appRow.uid);
        appRow.blockedChannelCount = getBlockedChannelCount(appRow.pkg, appRow.uid);
        appRow.channelCount = getChannelCount(appRow.pkg, appRow.uid);
        recordAggregatedUsageEvents(context, appRow);
        return appRow;
    }

    public boolean onlyHasDefaultChannel(String str, int i) {
        try {
            return sINM.onlyHasDefaultChannel(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    protected void recordAggregatedUsageEvents(UsageEvents usageEvents, AppRow appRow) {
        String str;
        appRow.sentByChannel = new HashMap();
        appRow.sentByApp = new NotificationsSentState();
        if (usageEvents != null) {
            UsageEvents.Event event = new UsageEvents.Event();
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == 12 && (str = event.mNotificationChannelId) != null) {
                    NotificationsSentState notificationsSentState = appRow.sentByChannel.get(str);
                    if (notificationsSentState == null) {
                        notificationsSentState = new NotificationsSentState();
                        appRow.sentByChannel.put(str, notificationsSentState);
                    }
                    if (event.getTimeStamp() > notificationsSentState.lastSent) {
                        notificationsSentState.lastSent = event.getTimeStamp();
                        appRow.sentByApp.lastSent = event.getTimeStamp();
                    }
                    notificationsSentState.sentCount++;
                    appRow.sentByApp.sentCount++;
                    calculateAvgSentCounts(notificationsSentState);
                }
            }
            calculateAvgSentCounts(appRow.sentByApp);
        }
    }

    protected void recordAggregatedUsageEvents(Context context, AppRow appRow) {
        UsageEvents usageEvents;
        long currentTimeMillis = System.currentTimeMillis();
        try {
            usageEvents = sUsageStatsManager.queryEventsForPackageForUser(currentTimeMillis - MipubStat.STAT_EXPIRY_DATA, currentTimeMillis, appRow.userId, appRow.pkg, context.getPackageName());
        } catch (RemoteException e) {
            e.printStackTrace();
            usageEvents = null;
        }
        recordAggregatedUsageEvents(usageEvents, appRow);
    }

    void recordCanBeBlocked(Context context, PackageManager packageManager, RoleManager roleManager, PackageInfo packageInfo, AppRow appRow) {
        appRow.systemApp = Utils.isSystemPackage(context.getResources(), packageManager, packageInfo);
        List heldRolesFromController = roleManager.getHeldRolesFromController(packageInfo.packageName);
        if (heldRolesFromController.contains("android.app.role.DIALER") || heldRolesFromController.contains("android.app.role.EMERGENCY")) {
            appRow.systemApp = true;
        }
        markAppRowWithBlockables(context.getResources().getStringArray(17236073), appRow, packageInfo.packageName);
    }

    public void resetNotificationImportance() {
        try {
            sINM.unlockAllNotificationChannels();
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
        }
    }

    public boolean setAllowBubbles(String str, int i, int i2) {
        try {
            sINM.setBubblesAllowed(str, i, i2);
            return true;
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public void setHideSilentStatusIcons(boolean z) {
        try {
            sINM.setHideSilentStatusIcons(z);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
        }
    }

    public void setListenerFilter(ComponentName componentName, int i, NotificationListenerFilter notificationListenerFilter) {
        try {
            sINM.setListenerFilter(componentName, i, notificationListenerFilter);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
        }
    }

    public void setNASMigrationDoneAndResetDefault(int i, boolean z) {
        try {
            sINM.setNASMigrationDoneAndResetDefault(i, z);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
        }
    }

    public boolean setNotificationAssistantGranted(ComponentName componentName) {
        try {
            sINM.setNotificationAssistantAccessGranted(componentName, true);
            return componentName == null ? sINM.getAllowedNotificationAssistant() == null : componentName.equals(sINM.getAllowedNotificationAssistant());
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public boolean setNotificationsEnabledForPackage(String str, int i, boolean z) {
        try {
            if (onlyHasDefaultChannel(str, i)) {
                NotificationChannel channel = getChannel(str, i, "miscellaneous", null);
                channel.setImportance(z ? -1000 : 0);
                updateChannel(str, i, channel);
            }
            sINM.setNotificationsEnabledForPackage(str, i, z);
            return true;
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public boolean setShowBadge(String str, int i, boolean z) {
        try {
            sINM.setShowBadge(str, i, z);
            return true;
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public boolean shouldHideSilentStatusBarIcons(Context context) {
        try {
            return sINM.shouldHideSilentStatusIcons(context.getPackageName());
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public void updateChannel(String str, int i, NotificationChannel notificationChannel) {
        try {
            sINM.updateNotificationChannelForPackage(str, i, notificationChannel);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
        }
    }
}
