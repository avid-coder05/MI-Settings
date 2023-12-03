package com.android.settings.notification.zen;

import android.app.Application;
import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.content.Context;
import android.icu.text.MessageFormat;
import android.service.notification.ZenModeConfig;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R;
import com.android.settings.notification.zen.ZenModeSettings;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.ObservablePreferenceFragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import miui.yellowpage.Tag;

/* loaded from: classes2.dex */
public class ZenModeSettings extends ZenModeSettingsBase {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.zen_mode_settings) { // from class: com.android.settings.notification.zen.ZenModeSettings.1
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return ZenModeSettings.buildPreferenceControllers(context, null, null, null, null);
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            nonIndexableKeys.add("zen_mode_duration_settings");
            return nonIndexableKeys;
        }
    };

    /* loaded from: classes2.dex */
    public static class SummaryBuilder {
        private static final int[] ALL_PRIORITY_CATEGORIES = {32, 64, 128, 4, 2, 1, 8, 16};
        private Context mContext;

        public SummaryBuilder(Context context) {
            this.mContext = context;
        }

        private String getCategory(int i, NotificationManager.Policy policy, boolean z) {
            if (i == 32) {
                return z ? this.mContext.getString(R.string.zen_mode_alarms_list_first) : this.mContext.getString(R.string.zen_mode_alarms_list);
            } else if (i == 64) {
                return z ? this.mContext.getString(R.string.zen_mode_media_list_first) : this.mContext.getString(R.string.zen_mode_media_list);
            } else if (i == 128) {
                return z ? this.mContext.getString(R.string.zen_mode_system_list_first) : this.mContext.getString(R.string.zen_mode_system_list);
            } else if (i == 4) {
                int i2 = policy.priorityMessageSenders;
                return i2 == 0 ? this.mContext.getString(R.string.zen_mode_from_anyone) : i2 == 1 ? this.mContext.getString(R.string.zen_mode_from_contacts) : this.mContext.getString(R.string.zen_mode_from_starred);
            } else if (i == 2) {
                return z ? this.mContext.getString(R.string.zen_mode_events_list_first) : this.mContext.getString(R.string.zen_mode_events_list);
            } else if (i == 1) {
                return z ? this.mContext.getString(R.string.zen_mode_reminders_list_first) : this.mContext.getString(R.string.zen_mode_reminders_list);
            } else if (i != 8) {
                return i == 16 ? z ? this.mContext.getString(R.string.zen_mode_repeat_callers) : this.mContext.getString(R.string.zen_mode_repeat_callers_list) : "";
            } else {
                int i3 = policy.priorityCallSenders;
                return i3 == 0 ? z ? this.mContext.getString(R.string.zen_mode_from_anyone) : this.mContext.getString(R.string.zen_mode_all_callers) : i3 == 1 ? z ? this.mContext.getString(R.string.zen_mode_from_contacts) : this.mContext.getString(R.string.zen_mode_contacts_callers) : z ? this.mContext.getString(R.string.zen_mode_from_starred) : this.mContext.getString(R.string.zen_mode_starred_callers);
            }
        }

        private List<String> getEnabledCategories(NotificationManager.Policy policy, Predicate<Integer> predicate, boolean z) {
            ArrayList arrayList = new ArrayList();
            for (int i : ALL_PRIORITY_CATEGORIES) {
                boolean z2 = z && arrayList.isEmpty();
                if (predicate.test(Integer.valueOf(i)) && isCategoryEnabled(policy, i) && (i != 16 || !isCategoryEnabled(policy, 8) || policy.priorityCallSenders != 0)) {
                    arrayList.add(getCategory(i, policy, z2));
                }
            }
            return arrayList;
        }

        private boolean isCategoryEnabled(NotificationManager.Policy policy, int i) {
            return (policy.priorityCategories & i) != 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$getCallsSettingSummary$1(Integer num) {
            return 8 == num.intValue() || 16 == num.intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$getMessagesSettingSummary$2(Integer num) {
            return 4 == num.intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$getOtherSoundCategoriesSummary$0(Integer num) {
            return 32 == num.intValue() || 64 == num.intValue() || 128 == num.intValue() || 1 == num.intValue() || 2 == num.intValue();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getAutomaticRulesSummary() {
            MessageFormat messageFormat = new MessageFormat(this.mContext.getString(R.string.zen_mode_settings_schedules_summary), Locale.getDefault());
            HashMap hashMap = new HashMap();
            hashMap.put(Tag.TagPhone.MARKED_COUNT, Integer.valueOf(getEnabledAutomaticRulesCount()));
            return messageFormat.format(hashMap);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getBlockedEffectsSummary(NotificationManager.Policy policy) {
            int i = policy.suppressedVisualEffects;
            return i == 0 ? this.mContext.getResources().getString(R.string.zen_mode_restrict_notifications_summary_muted) : NotificationManager.Policy.areAllVisualEffectsSuppressed(i) ? this.mContext.getResources().getString(R.string.zen_mode_restrict_notifications_summary_hidden) : this.mContext.getResources().getString(R.string.zen_mode_restrict_notifications_summary_custom);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getCallsSettingSummary(NotificationManager.Policy policy) {
            List<String> enabledCategories = getEnabledCategories(policy, new Predicate() { // from class: com.android.settings.notification.zen.ZenModeSettings$SummaryBuilder$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getCallsSettingSummary$1;
                    lambda$getCallsSettingSummary$1 = ZenModeSettings.SummaryBuilder.lambda$getCallsSettingSummary$1((Integer) obj);
                    return lambda$getCallsSettingSummary$1;
                }
            }, true);
            int size = enabledCategories.size();
            return size == 0 ? this.mContext.getString(R.string.zen_mode_none_calls) : size == 1 ? this.mContext.getString(R.string.zen_mode_calls_summary_one, enabledCategories.get(0)) : this.mContext.getString(R.string.zen_mode_calls_summary_two, enabledCategories.get(0), enabledCategories.get(1));
        }

        int getEnabledAutomaticRulesCount() {
            Map<String, AutomaticZenRule> automaticZenRules = NotificationManager.from(this.mContext).getAutomaticZenRules();
            int i = 0;
            if (automaticZenRules != null) {
                Iterator<Map.Entry<String, AutomaticZenRule>> it = automaticZenRules.entrySet().iterator();
                while (it.hasNext()) {
                    AutomaticZenRule value = it.next().getValue();
                    if (value != null && value.isEnabled()) {
                        i++;
                    }
                }
            }
            return i;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getMessagesSettingSummary(NotificationManager.Policy policy) {
            List<String> enabledCategories = getEnabledCategories(policy, new Predicate() { // from class: com.android.settings.notification.zen.ZenModeSettings$SummaryBuilder$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getMessagesSettingSummary$2;
                    lambda$getMessagesSettingSummary$2 = ZenModeSettings.SummaryBuilder.lambda$getMessagesSettingSummary$2((Integer) obj);
                    return lambda$getMessagesSettingSummary$2;
                }
            }, false);
            return enabledCategories.size() == 0 ? this.mContext.getString(R.string.zen_mode_none_messages) : enabledCategories.get(0);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getOtherSoundCategoriesSummary(NotificationManager.Policy policy) {
            List<String> enabledCategories = getEnabledCategories(policy, new Predicate() { // from class: com.android.settings.notification.zen.ZenModeSettings$SummaryBuilder$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getOtherSoundCategoriesSummary$0;
                    lambda$getOtherSoundCategoriesSummary$0 = ZenModeSettings.SummaryBuilder.lambda$getOtherSoundCategoriesSummary$0((Integer) obj);
                    return lambda$getOtherSoundCategoriesSummary$0;
                }
            }, true);
            int size = enabledCategories.size();
            MessageFormat messageFormat = new MessageFormat(this.mContext.getString(R.string.zen_mode_other_sounds_summary), Locale.getDefault());
            HashMap hashMap = new HashMap();
            hashMap.put(Tag.TagPhone.MARKED_COUNT, Integer.valueOf(size));
            if (size >= 1) {
                hashMap.put("sound_category_1", enabledCategories.get(0));
                if (size >= 2) {
                    hashMap.put("sound_category_2", enabledCategories.get(1));
                    if (size == 3) {
                        hashMap.put("sound_category_3", enabledCategories.get(2));
                    }
                }
            }
            return messageFormat.format(hashMap);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getSoundSummary() {
            if (NotificationManager.from(this.mContext).getZenMode() != 0) {
                String description = ZenModeConfig.getDescription(this.mContext, true, NotificationManager.from(this.mContext).getZenModeConfig(), false);
                return description == null ? this.mContext.getString(R.string.zen_mode_sound_summary_on) : this.mContext.getString(R.string.zen_mode_sound_summary_on_with_info, description);
            }
            MessageFormat messageFormat = new MessageFormat(this.mContext.getString(R.string.zen_mode_sound_summary_off), Locale.getDefault());
            HashMap hashMap = new HashMap();
            hashMap.put(Tag.TagPhone.MARKED_COUNT, Integer.valueOf(getEnabledAutomaticRulesCount()));
            return messageFormat.format(hashMap);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle, FragmentManager fragmentManager, Application application, ObservablePreferenceFragment observablePreferenceFragment) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ZenModeButtonPreferenceController(context, lifecycle, fragmentManager));
        arrayList.add(new ZenModePeoplePreferenceController(context, lifecycle, "zen_mode_behavior_people"));
        arrayList.add(new ZenModeBypassingAppsPreferenceController(context, application, observablePreferenceFragment, lifecycle));
        arrayList.add(new ZenModeSoundVibrationPreferenceController(context, lifecycle, "zen_sound_vibration_settings"));
        arrayList.add(new ZenModeAutomationPreferenceController(context));
        arrayList.add(new ZenModeDurationPreferenceController(context, lifecycle));
        arrayList.add(new ZenModeBlockedEffectsPreferenceController(context, lifecycle));
        arrayList.add(new ZenModeSettingsFooterPreferenceController(context, lifecycle, fragmentManager));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        FragmentActivity activity = getActivity();
        return buildPreferenceControllers(context, getSettingsLifecycle(), getFragmentManager(), activity != null ? activity.getApplication() : null, this);
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_uri_interruptions;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 76;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.zen_mode_settings;
    }

    @Override // com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }
}
