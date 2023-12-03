package com.android.settings.notification.app;

import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.content.pm.ShortcutInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.service.notification.ConversationChannelWrapper;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.search.FunctionColumns;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/* loaded from: classes2.dex */
public class AppConversationListPreferenceController extends NotificationPreferenceController {
    protected Comparator<ConversationChannelWrapper> mConversationComparator;
    protected List<ConversationChannelWrapper> mConversations;
    protected PreferenceCategory mPreference;

    public AppConversationListPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
        this.mConversations = new ArrayList();
        this.mConversationComparator = new Comparator() { // from class: com.android.settings.notification.app.AppConversationListPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$new$0;
                lambda$new$0 = AppConversationListPreferenceController.lambda$new$0((ConversationChannelWrapper) obj, (ConversationChannelWrapper) obj2);
                return lambda$new$0;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$new$0(ConversationChannelWrapper conversationChannelWrapper, ConversationChannelWrapper conversationChannelWrapper2) {
        return conversationChannelWrapper.getNotificationChannel().isImportantConversation() != conversationChannelWrapper2.getNotificationChannel().isImportantConversation() ? Boolean.compare(conversationChannelWrapper2.getNotificationChannel().isImportantConversation(), conversationChannelWrapper.getNotificationChannel().isImportantConversation()) : conversationChannelWrapper.getNotificationChannel().getId().compareTo(conversationChannelWrapper2.getNotificationChannel().getId());
    }

    private void populateConversations() {
        for (ConversationChannelWrapper conversationChannelWrapper : this.mConversations) {
            if (!conversationChannelWrapper.getNotificationChannel().isDemoted()) {
                this.mPreference.addPreference(createConversationPref(conversationChannelWrapper));
            }
        }
    }

    protected Preference createConversationPref(ConversationChannelWrapper conversationChannelWrapper) {
        throw null;
    }

    protected List<ConversationChannelWrapper> filterAndSortConversations(List<ConversationChannelWrapper> list) {
        throw null;
    }

    protected int getTitleResId() {
        throw null;
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    boolean isIncludedInFilter() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void loadConversationsAndPopulate() {
        if (this.mAppRow == null) {
            return;
        }
        new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.notification.app.AppConversationListPreferenceController.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... voidArr) {
                AppConversationListPreferenceController appConversationListPreferenceController = AppConversationListPreferenceController.this;
                NotificationBackend notificationBackend = appConversationListPreferenceController.mBackend;
                NotificationBackend.AppRow appRow = appConversationListPreferenceController.mAppRow;
                ParceledListSlice<ConversationChannelWrapper> conversations = notificationBackend.getConversations(appRow.pkg, appRow.uid);
                if (conversations != null) {
                    AppConversationListPreferenceController appConversationListPreferenceController2 = AppConversationListPreferenceController.this;
                    appConversationListPreferenceController2.mConversations = appConversationListPreferenceController2.filterAndSortConversations(conversations.getList());
                    return null;
                }
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Void r1) {
                AppConversationListPreferenceController appConversationListPreferenceController = AppConversationListPreferenceController.this;
                if (((NotificationPreferenceController) appConversationListPreferenceController).mContext == null) {
                    return;
                }
                appConversationListPreferenceController.populateList();
            }
        }.execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void populateConversationPreference(ConversationChannelWrapper conversationChannelWrapper, Preference preference) {
        ShortcutInfo shortcutInfo = conversationChannelWrapper.getShortcutInfo();
        preference.setTitle(shortcutInfo != null ? shortcutInfo.getLabel() : conversationChannelWrapper.getNotificationChannel().getName());
        preference.setSummary(conversationChannelWrapper.getNotificationChannel().getGroup() != null ? ((NotificationPreferenceController) this).mContext.getString(R.string.notification_conversation_summary, conversationChannelWrapper.getParentChannelLabel(), conversationChannelWrapper.getGroupLabel()) : conversationChannelWrapper.getParentChannelLabel());
        if (shortcutInfo != null) {
            NotificationBackend notificationBackend = this.mBackend;
            Context context = ((NotificationPreferenceController) this).mContext;
            NotificationBackend.AppRow appRow = this.mAppRow;
            preference.setIcon(notificationBackend.getConversationDrawable(context, shortcutInfo, appRow.pkg, appRow.uid, conversationChannelWrapper.getNotificationChannel().isImportantConversation()));
        }
        preference.setKey(conversationChannelWrapper.getNotificationChannel().getId());
        Bundle bundle = new Bundle();
        bundle.putInt("uid", this.mAppRow.uid);
        bundle.putString(FunctionColumns.PACKAGE, this.mAppRow.pkg);
        bundle.putString("android.provider.extra.CHANNEL_ID", conversationChannelWrapper.getNotificationChannel().getParentChannelId());
        bundle.putString("android.provider.extra.CONVERSATION_ID", conversationChannelWrapper.getNotificationChannel().getConversationId());
        bundle.putBoolean("fromSettings", true);
        preference.setIntent(new SubSettingLauncher(((NotificationPreferenceController) this).mContext).setDestination(ChannelNotificationSettings.class.getName()).setArguments(bundle).setExtras(bundle).setTitleText(preference.getTitle()).setSourceMetricsCategory(72).toIntent());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void populateList() {
        if (this.mPreference == null || this.mConversations.isEmpty()) {
            return;
        }
        this.mPreference.removeAll();
        this.mPreference.setTitle(getTitleResId());
        populateConversations();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mPreference = (PreferenceCategory) preference;
        loadConversationsAndPopulate();
    }
}
