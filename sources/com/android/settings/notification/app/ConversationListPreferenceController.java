package com.android.settings.notification.app;

import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.service.notification.ConversationChannelWrapper;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.search.FunctionColumns;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.AppPreference;
import java.text.Collator;
import java.util.Comparator;
import java.util.List;

/* loaded from: classes2.dex */
public abstract class ConversationListPreferenceController extends AbstractPreferenceController {
    protected final NotificationBackend mBackend;
    protected Comparator<ConversationChannelWrapper> mConversationComparator;

    public ConversationListPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context);
        this.mConversationComparator = new Comparator<ConversationChannelWrapper>() { // from class: com.android.settings.notification.app.ConversationListPreferenceController.1
            private final Collator sCollator = Collator.getInstance();

            @Override // java.util.Comparator
            public int compare(ConversationChannelWrapper conversationChannelWrapper, ConversationChannelWrapper conversationChannelWrapper2) {
                if (conversationChannelWrapper.getShortcutInfo() == null || conversationChannelWrapper2.getShortcutInfo() != null) {
                    if (conversationChannelWrapper.getShortcutInfo() != null || conversationChannelWrapper2.getShortcutInfo() == null) {
                        if (conversationChannelWrapper.getShortcutInfo() == null && conversationChannelWrapper2.getShortcutInfo() == null) {
                            return conversationChannelWrapper.getNotificationChannel().getId().compareTo(conversationChannelWrapper2.getNotificationChannel().getId());
                        }
                        if (conversationChannelWrapper.getShortcutInfo().getLabel() != null || conversationChannelWrapper2.getShortcutInfo().getLabel() == null) {
                            if (conversationChannelWrapper.getShortcutInfo().getLabel() == null || conversationChannelWrapper2.getShortcutInfo().getLabel() != null) {
                                return this.sCollator.compare(conversationChannelWrapper.getShortcutInfo().getLabel().toString(), conversationChannelWrapper2.getShortcutInfo().getLabel().toString());
                            }
                            return -1;
                        }
                        return 1;
                    }
                    return 1;
                }
                return -1;
            }
        };
        this.mBackend = notificationBackend;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createConversationPref$0(ConversationChannelWrapper conversationChannelWrapper, AppPreference appPreference, Preference preference) {
        getSubSettingLauncher(conversationChannelWrapper, appPreference.getTitle()).launch();
        return true;
    }

    protected Preference createConversationPref(final ConversationChannelWrapper conversationChannelWrapper, int i) {
        final AppPreference appPreference = new AppPreference(this.mContext);
        appPreference.setOrder(i);
        appPreference.setTitle(getTitle(conversationChannelWrapper));
        appPreference.setSummary(getSummary(conversationChannelWrapper));
        appPreference.setIcon(this.mBackend.getConversationDrawable(this.mContext, conversationChannelWrapper.getShortcutInfo(), conversationChannelWrapper.getPkg(), conversationChannelWrapper.getUid(), conversationChannelWrapper.getNotificationChannel().isImportantConversation()));
        appPreference.setKey(conversationChannelWrapper.getNotificationChannel().getId());
        appPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.app.ConversationListPreferenceController$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                boolean lambda$createConversationPref$0;
                lambda$createConversationPref$0 = ConversationListPreferenceController.this.lambda$createConversationPref$0(conversationChannelWrapper, appPreference, preference);
                return lambda$createConversationPref$0;
            }
        });
        return appPreference;
    }

    SubSettingLauncher getSubSettingLauncher(ConversationChannelWrapper conversationChannelWrapper, CharSequence charSequence) {
        Bundle bundle = new Bundle();
        bundle.putInt("uid", conversationChannelWrapper.getUid());
        bundle.putString(FunctionColumns.PACKAGE, conversationChannelWrapper.getPkg());
        bundle.putString("android.provider.extra.CHANNEL_ID", conversationChannelWrapper.getNotificationChannel().getId());
        bundle.putString("android.provider.extra.CONVERSATION_ID", conversationChannelWrapper.getNotificationChannel().getConversationId());
        return new SubSettingLauncher(this.mContext).setDestination(ChannelNotificationSettings.class.getName()).setArguments(bundle).setExtras(bundle).setUserHandle(UserHandle.getUserHandleForUid(conversationChannelWrapper.getUid())).setTitleText(charSequence).setSourceMetricsCategory(1834);
    }

    CharSequence getSummary(ConversationChannelWrapper conversationChannelWrapper) {
        return TextUtils.isEmpty(conversationChannelWrapper.getGroupLabel()) ? conversationChannelWrapper.getParentChannelLabel() : this.mContext.getString(R.string.notification_conversation_summary, conversationChannelWrapper.getParentChannelLabel(), conversationChannelWrapper.getGroupLabel());
    }

    abstract Preference getSummaryPreference();

    CharSequence getTitle(ConversationChannelWrapper conversationChannelWrapper) {
        ShortcutInfo shortcutInfo = conversationChannelWrapper.getShortcutInfo();
        return shortcutInfo != null ? shortcutInfo.getLabel() : conversationChannelWrapper.getNotificationChannel().getName();
    }

    abstract boolean matchesFilter(ConversationChannelWrapper conversationChannelWrapper);

    protected void populateConversations(List<ConversationChannelWrapper> list, PreferenceGroup preferenceGroup) {
        int i = 100;
        for (ConversationChannelWrapper conversationChannelWrapper : list) {
            if (!conversationChannelWrapper.getNotificationChannel().isDemoted() && matchesFilter(conversationChannelWrapper)) {
                preferenceGroup.addPreference(createConversationPref(conversationChannelWrapper, i));
                i++;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void populateList(List<ConversationChannelWrapper> list, PreferenceGroup preferenceGroup) {
        preferenceGroup.setVisible(false);
        preferenceGroup.removeAll();
        if (list != null) {
            populateConversations(list, preferenceGroup);
        }
        if (preferenceGroup.getPreferenceCount() != 0) {
            Preference summaryPreference = getSummaryPreference();
            if (summaryPreference != null) {
                preferenceGroup.addPreference(summaryPreference);
            }
            preferenceGroup.setVisible(true);
        }
    }
}
