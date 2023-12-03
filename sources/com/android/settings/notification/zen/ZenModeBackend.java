package com.android.settings.notification.zen;

import android.app.ActivityManager;
import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.icu.text.MessageFormat;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.service.notification.ZenModeConfig;
import android.service.notification.ZenPolicy;
import android.util.Log;
import com.android.settings.R;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import miui.app.constants.ThemeManagerConstants;
import miui.provider.ExtraContacts;
import miui.provider.ExtraTelephony;
import miui.yellowpage.Tag;

/* loaded from: classes2.dex */
public class ZenModeBackend {
    public static final Comparator<Map.Entry<String, AutomaticZenRule>> RULE_COMPARATOR = new Comparator<Map.Entry<String, AutomaticZenRule>>() { // from class: com.android.settings.notification.zen.ZenModeBackend.1
        private String key(AutomaticZenRule automaticZenRule) {
            return (ZenModeConfig.isValidScheduleConditionId(automaticZenRule.getConditionId()) ? 1 : ZenModeConfig.isValidEventConditionId(automaticZenRule.getConditionId()) ? 2 : 3) + automaticZenRule.getName().toString();
        }

        @Override // java.util.Comparator
        public int compare(Map.Entry<String, AutomaticZenRule> entry, Map.Entry<String, AutomaticZenRule> entry2) {
            boolean contains = ZenModeBackend.access$000().contains(entry.getKey());
            if (contains != ZenModeBackend.access$000().contains(entry2.getKey())) {
                return contains ? -1 : 1;
            }
            int compare = Long.compare(entry.getValue().getCreationTime(), entry2.getValue().getCreationTime());
            return compare != 0 ? compare : key(entry.getValue()).compareTo(key(entry2.getValue()));
        }
    };
    protected static final String ZEN_MODE_FROM_ANYONE = "zen_mode_from_anyone";
    protected static final String ZEN_MODE_FROM_CONTACTS = "zen_mode_from_contacts";
    protected static final String ZEN_MODE_FROM_NONE = "zen_mode_from_none";
    protected static final String ZEN_MODE_FROM_STARRED = "zen_mode_from_starred";
    private static List<String> mDefaultRuleIds;
    private static ZenModeBackend sInstance;
    private String TAG = "ZenModeSettingsBackend";
    private final Context mContext;
    private final NotificationManager mNotificationManager;
    protected NotificationManager.Policy mPolicy;
    protected int mZenMode;

    public ZenModeBackend(Context context) {
        this.mContext = context;
        this.mNotificationManager = (NotificationManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION);
        updateZenMode();
        updatePolicy();
    }

    static /* synthetic */ List access$000() {
        return getDefaultRuleIds();
    }

    private int clearDeprecatedEffects(int i) {
        return i & (-4);
    }

    private static List<String> getDefaultRuleIds() {
        if (mDefaultRuleIds == null) {
            mDefaultRuleIds = ZenModeConfig.DEFAULT_RULE_IDS;
        }
        return mDefaultRuleIds;
    }

    public static ZenModeBackend getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ZenModeBackend(context);
        }
        return sInstance;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String getKeyFromZenPolicySetting(int i) {
        return i != 1 ? i != 2 ? i != 3 ? ZEN_MODE_FROM_NONE : ZEN_MODE_FROM_STARRED : ZEN_MODE_FROM_CONTACTS : ZEN_MODE_FROM_ANYONE;
    }

    private int getNewSuppressedEffects(boolean z, int i) {
        int i2 = this.mPolicy.suppressedVisualEffects;
        return clearDeprecatedEffects(z ? i2 | i : (~i) & i2);
    }

    private int getPrioritySenders(int i) {
        if (i == 8) {
            return getPriorityCallSenders();
        }
        if (i == 4) {
            return getPriorityMessageSenders();
        }
        if (i == 256) {
            return getPriorityConversationSenders();
        }
        return -1;
    }

    private List<String> getStarredContacts() {
        Cursor cursor;
        try {
            cursor = queryStarredContactsData();
        } catch (Throwable th) {
            th = th;
            cursor = null;
        }
        try {
            List<String> starredContacts = getStarredContacts(cursor);
            if (cursor != null) {
                cursor.close();
            }
            return starredContacts;
        } catch (Throwable th2) {
            th = th2;
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int getZenPolicySettingFromPrefKey(String str) {
        char c;
        switch (str.hashCode()) {
            case -946901971:
                if (str.equals(ZEN_MODE_FROM_NONE)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -423126328:
                if (str.equals(ZEN_MODE_FROM_CONTACTS)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 187510959:
                if (str.equals(ZEN_MODE_FROM_ANYONE)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 462773226:
                if (str.equals(ZEN_MODE_FROM_STARRED)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c != 0) {
            if (c != 1) {
                return c != 2 ? 4 : 3;
            }
            return 2;
        }
        return 1;
    }

    private Cursor queryAllContactsData() {
        return this.mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{"display_name"}, null, null, null);
    }

    private Cursor queryStarredContactsData() {
        return this.mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{"display_name"}, "starred=1", null, ExtraContacts.T9LookupColumns.TIMES_CONTACTED);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String addZenRule(AutomaticZenRule automaticZenRule) {
        try {
            return NotificationManager.from(this.mContext).addAutomaticZenRule(automaticZenRule);
        } catch (Exception unused) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getAlarmsTotalSilencePeopleSummary(int i) {
        return i == 4 ? R.string.zen_mode_none_messages : i == 8 ? R.string.zen_mode_none_calls : i == 256 ? R.string.zen_mode_from_no_conversations : R.string.zen_mode_from_no_conversations;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AutomaticZenRule getAutomaticZenRule(String str) {
        return NotificationManager.from(this.mContext).getAutomaticZenRule(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Map.Entry<String, AutomaticZenRule>[] getAutomaticZenRules() {
        Map<String, AutomaticZenRule> automaticZenRules = NotificationManager.from(this.mContext).getAutomaticZenRules();
        Map.Entry<String, AutomaticZenRule>[] entryArr = (Map.Entry[]) automaticZenRules.entrySet().toArray(new Map.Entry[automaticZenRules.size()]);
        Arrays.sort(entryArr, RULE_COMPARATOR);
        return entryArr;
    }

    public NotificationManager.Policy getConsolidatedPolicy() {
        return NotificationManager.from(this.mContext).getConsolidatedNotificationPolicy();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getContactsCallsSummary(ZenPolicy zenPolicy) {
        int priorityCallSenders = zenPolicy.getPriorityCallSenders();
        return priorityCallSenders != 1 ? priorityCallSenders != 2 ? priorityCallSenders != 3 ? R.string.zen_mode_none_calls : R.string.zen_mode_from_starred : R.string.zen_mode_from_contacts : R.string.zen_mode_from_anyone;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getContactsMessagesSummary(ZenPolicy zenPolicy) {
        int priorityMessageSenders = zenPolicy.getPriorityMessageSenders();
        return priorityMessageSenders != 1 ? priorityMessageSenders != 2 ? priorityMessageSenders != 3 ? R.string.zen_mode_none_messages : R.string.zen_mode_from_starred : R.string.zen_mode_from_contacts : R.string.zen_mode_from_anyone;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getContactsNumberSummary(Context context) {
        MessageFormat messageFormat = new MessageFormat(this.mContext.getString(R.string.zen_mode_contacts_count), Locale.getDefault());
        HashMap hashMap = new HashMap();
        hashMap.put(Tag.TagPhone.MARKED_COUNT, Integer.valueOf(queryAllContactsData().getCount()));
        return messageFormat.format(hashMap);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getConversationSummary() {
        int priorityConversationSenders = getPriorityConversationSenders();
        return priorityConversationSenders != 1 ? priorityConversationSenders != 2 ? priorityConversationSenders != 3 ? R.string.zen_mode_from_no_conversations : R.string.zen_mode_from_no_conversations : R.string.zen_mode_from_important_conversations : R.string.zen_mode_from_all_conversations;
    }

    protected int getNewDefaultPriorityCategories(boolean z, int i) {
        int i2 = this.mPolicy.priorityCategories;
        return z ? i2 | i : i2 & (~i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getPriorityCallSenders() {
        if (isPriorityCategoryEnabled(8)) {
            return this.mPolicy.priorityCallSenders;
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getPriorityConversationSenders() {
        if (isPriorityCategoryEnabled(256)) {
            return this.mPolicy.priorityConversationSenders;
        }
        return 3;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getPriorityMessageSenders() {
        if (isPriorityCategoryEnabled(4)) {
            return this.mPolicy.priorityMessageSenders;
        }
        return -1;
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x001d, code lost:
    
        r0.add(r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0024, code lost:
    
        if (r4.moveToNext() != false) goto L14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x000b, code lost:
    
        if (r4.moveToFirst() != false) goto L6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x000d, code lost:
    
        r1 = r4.getString(0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0012, code lost:
    
        if (r1 == null) goto L9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0015, code lost:
    
        r1 = r3.mContext.getString(com.android.settings.R.string.zen_mode_starred_contacts_empty_name);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    java.util.List<java.lang.String> getStarredContacts(android.database.Cursor r4) {
        /*
            r3 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            if (r4 == 0) goto L26
            boolean r1 = r4.moveToFirst()
            if (r1 == 0) goto L26
        Ld:
            r1 = 0
            java.lang.String r1 = r4.getString(r1)
            if (r1 == 0) goto L15
            goto L1d
        L15:
            android.content.Context r1 = r3.mContext
            int r2 = com.android.settings.R.string.zen_mode_starred_contacts_empty_name
            java.lang.String r1 = r1.getString(r2)
        L1d:
            r0.add(r1)
            boolean r1 = r4.moveToNext()
            if (r1 != 0) goto Ld
        L26:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.notification.zen.ZenModeBackend.getStarredContacts(android.database.Cursor):java.util.List");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getStarredContactsSummary(Context context) {
        List<String> starredContacts = getStarredContacts();
        int size = starredContacts.size();
        MessageFormat messageFormat = new MessageFormat(this.mContext.getString(R.string.zen_mode_starred_contacts_summary_contacts), Locale.getDefault());
        HashMap hashMap = new HashMap();
        hashMap.put(Tag.TagPhone.MARKED_COUNT, Integer.valueOf(size));
        if (size >= 1) {
            hashMap.put("contact_1", starredContacts.get(0));
            if (size >= 2) {
                hashMap.put("contact_2", starredContacts.get(1));
                if (size == 3) {
                    hashMap.put("contact_3", starredContacts.get(2));
                }
            }
        }
        return messageFormat.format(hashMap);
    }

    protected int getZenMode() {
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), ExtraTelephony.ZEN_MODE, this.mZenMode);
        this.mZenMode = i;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isPriorityCategoryEnabled(int i) {
        return (this.mPolicy.priorityCategories & i) != 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isVisualEffectSuppressed(int i) {
        return (this.mPolicy.suppressedVisualEffects & i) != 0;
    }

    public boolean removeZenRule(String str) {
        return NotificationManager.from(this.mContext).removeAutomaticZenRule(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void saveConversationSenders(int i) {
        int newDefaultPriorityCategories = getNewDefaultPriorityCategories(i != 3, 256);
        NotificationManager.Policy policy = this.mPolicy;
        savePolicy(newDefaultPriorityCategories, policy.priorityCallSenders, policy.priorityMessageSenders, policy.suppressedVisualEffects, i);
    }

    protected void savePolicy(int i, int i2, int i3, int i4, int i5) {
        NotificationManager.Policy policy = new NotificationManager.Policy(i, i2, i3, i4, i5);
        this.mPolicy = policy;
        this.mNotificationManager.setNotificationPolicy(policy);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void saveSenders(int i, int i2) {
        int i3;
        String str;
        int i4;
        int priorityCallSenders = getPriorityCallSenders();
        int priorityMessageSenders = getPriorityMessageSenders();
        int prioritySenders = getPrioritySenders(i);
        boolean z = i2 != -1;
        if (i2 == -1) {
            i2 = prioritySenders;
        }
        if (i == 8) {
            str = "Calls";
            i3 = i2;
        } else {
            i3 = priorityCallSenders;
            str = "";
        }
        if (i == 4) {
            str = "Messages";
            i4 = i2;
        } else {
            i4 = priorityMessageSenders;
        }
        int newDefaultPriorityCategories = getNewDefaultPriorityCategories(z, i);
        NotificationManager.Policy policy = this.mPolicy;
        savePolicy(newDefaultPriorityCategories, i3, i4, policy.suppressedVisualEffects, policy.priorityConversationSenders);
        if (ZenModeSettingsBase.DEBUG) {
            Log.d(this.TAG, "onPrefChange allow" + str + "=" + z + " allow" + str + "From=" + ZenModeConfig.sourceToString(i2));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void saveSoundPolicy(int i, boolean z) {
        int newDefaultPriorityCategories = getNewDefaultPriorityCategories(z, i);
        NotificationManager.Policy policy = this.mPolicy;
        savePolicy(newDefaultPriorityCategories, policy.priorityCallSenders, policy.priorityMessageSenders, policy.suppressedVisualEffects, policy.priorityConversationSenders);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void saveVisualEffectsPolicy(int i, boolean z) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), "zen_settings_updated", 1);
        int newSuppressedEffects = getNewSuppressedEffects(z, i);
        NotificationManager.Policy policy = this.mPolicy;
        savePolicy(policy.priorityCategories, policy.priorityCallSenders, policy.priorityMessageSenders, newSuppressedEffects, policy.priorityConversationSenders);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ZenPolicy setDefaultZenPolicy(ZenPolicy zenPolicy) {
        int zenPolicySenders = this.mPolicy.allowCalls() ? ZenModeConfig.getZenPolicySenders(this.mPolicy.allowCallsFrom()) : 4;
        return new ZenPolicy.Builder(zenPolicy).allowAlarms(this.mPolicy.allowAlarms()).allowCalls(zenPolicySenders).allowEvents(this.mPolicy.allowEvents()).allowMedia(this.mPolicy.allowMedia()).allowMessages(this.mPolicy.allowMessages() ? ZenModeConfig.getZenPolicySenders(this.mPolicy.allowMessagesFrom()) : 4).allowConversations(this.mPolicy.allowConversations() ? this.mPolicy.allowConversationsFrom() : 3).allowReminders(this.mPolicy.allowReminders()).allowRepeatCallers(this.mPolicy.allowRepeatCallers()).allowSystem(this.mPolicy.allowSystem()).showFullScreenIntent(this.mPolicy.showFullScreenIntents()).showLights(this.mPolicy.showLights()).showInAmbientDisplay(this.mPolicy.showAmbient()).showInNotificationList(this.mPolicy.showInNotificationList()).showBadges(this.mPolicy.showBadges()).showPeeking(this.mPolicy.showPeeking()).showStatusBarIcons(this.mPolicy.showStatusBarIcons()).build();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setZenMode(int i) {
        NotificationManager.from(this.mContext).setZenMode(i, null, this.TAG);
        this.mZenMode = getZenMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setZenModeForDuration(int i) {
        this.mNotificationManager.setZenMode(1, ZenModeConfig.toTimeCondition(this.mContext, i, ActivityManager.getCurrentUser(), true).id, this.TAG);
        this.mZenMode = getZenMode();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NotificationManager.Policy toNotificationPolicy(ZenPolicy zenPolicy) {
        return new ZenModeConfig().toNotificationPolicy(zenPolicy);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updatePolicy() {
        NotificationManager notificationManager = this.mNotificationManager;
        if (notificationManager != null) {
            this.mPolicy = notificationManager.getNotificationPolicy();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateZenMode() {
        this.mZenMode = Settings.Global.getInt(this.mContext.getContentResolver(), ExtraTelephony.ZEN_MODE, this.mZenMode);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean updateZenRule(String str, AutomaticZenRule automaticZenRule) {
        return NotificationManager.from(this.mContext).updateAutomaticZenRule(str, automaticZenRule);
    }
}
