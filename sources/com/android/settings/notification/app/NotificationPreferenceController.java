package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.os.UserManager;
import android.util.Log;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.Comparator;
import java.util.List;
import miui.app.constants.ThemeManagerConstants;

/* loaded from: classes2.dex */
public abstract class NotificationPreferenceController extends AbstractPreferenceController {
    protected RestrictedLockUtils.EnforcedAdmin mAdmin;
    protected NotificationBackend.AppRow mAppRow;
    protected final NotificationBackend mBackend;
    protected NotificationChannel mChannel;
    protected NotificationChannelGroup mChannelGroup;
    protected final Context mContext;
    protected Drawable mConversationDrawable;
    protected ShortcutInfo mConversationInfo;
    protected final NotificationManager mNm;
    protected final PackageManager mPm;
    protected List<String> mPreferenceFilter;
    protected final UserManager mUm;
    public static final Comparator<NotificationChannelGroup> CHANNEL_GROUP_COMPARATOR = new Comparator<NotificationChannelGroup>() { // from class: com.android.settings.notification.app.NotificationPreferenceController.1
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
    public static final Comparator<NotificationChannel> CHANNEL_COMPARATOR = new Comparator() { // from class: com.android.settings.notification.app.NotificationPreferenceController$$ExternalSyntheticLambda0
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            int lambda$static$0;
            lambda$static$0 = NotificationPreferenceController.lambda$static$0((NotificationChannel) obj, (NotificationChannel) obj2);
            return lambda$static$0;
        }
    };

    public NotificationPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context);
        this.mContext = context;
        this.mNm = (NotificationManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION);
        this.mBackend = notificationBackend;
        this.mUm = (UserManager) context.getSystemService("user");
        this.mPm = context.getPackageManager();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$static$0(NotificationChannel notificationChannel, NotificationChannel notificationChannel2) {
        if (notificationChannel.isDeleted() != notificationChannel2.isDeleted()) {
            return Boolean.compare(notificationChannel.isDeleted(), notificationChannel2.isDeleted());
        }
        if (notificationChannel.getId().equals("miscellaneous")) {
            return 1;
        }
        if (notificationChannel2.getId().equals("miscellaneous")) {
            return -1;
        }
        return notificationChannel.getId().compareTo(notificationChannel2.getId());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean checkCanBeVisible(int i) {
        NotificationChannel notificationChannel = this.mChannel;
        if (notificationChannel == null) {
            Log.w("ChannelPrefContr", "No channel");
            return false;
        }
        int importance = notificationChannel.getImportance();
        return importance == -1000 || importance >= i;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (appRow == null || appRow.banned) {
            return false;
        }
        NotificationChannelGroup notificationChannelGroup = this.mChannelGroup;
        if (notificationChannelGroup == null || !notificationChannelGroup.isBlocked()) {
            if (this.mChannel != null) {
                return (this.mPreferenceFilter == null || isIncludedInFilter()) && this.mChannel.getImportance() != 0;
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isChannelConfigurable(NotificationChannel notificationChannel) {
        if (notificationChannel == null || this.mAppRow == null) {
            return false;
        }
        return !notificationChannel.isImportanceLockedByOEM();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean isDefaultChannel() {
        NotificationChannel notificationChannel = this.mChannel;
        if (notificationChannel == null) {
            return false;
        }
        return "miscellaneous".equals(notificationChannel.getId());
    }

    abstract boolean isIncludedInFilter();

    /* JADX INFO: Access modifiers changed from: protected */
    public void onResume(NotificationBackend.AppRow appRow, NotificationChannel notificationChannel, NotificationChannelGroup notificationChannelGroup, Drawable drawable, ShortcutInfo shortcutInfo, RestrictedLockUtils.EnforcedAdmin enforcedAdmin, List<String> list) {
        this.mAppRow = appRow;
        this.mChannel = notificationChannel;
        this.mChannelGroup = notificationChannelGroup;
        this.mAdmin = enforcedAdmin;
        this.mConversationDrawable = drawable;
        this.mConversationInfo = shortcutInfo;
        this.mPreferenceFilter = list;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void saveChannel() {
        NotificationBackend.AppRow appRow;
        NotificationChannel notificationChannel = this.mChannel;
        if (notificationChannel == null || (appRow = this.mAppRow) == null) {
            return;
        }
        this.mBackend.updateChannel(appRow.pkg, appRow.uid, notificationChannel);
    }
}
