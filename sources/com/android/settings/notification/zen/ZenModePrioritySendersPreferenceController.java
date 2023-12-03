package com.android.settings.notification.zen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.RadioButtonPreference;
import java.util.ArrayList;
import java.util.List;
import miui.provider.ExtraContacts;

/* loaded from: classes2.dex */
public class ZenModePrioritySendersPreferenceController extends AbstractZenModePreferenceController {
    static final String KEY_ANY = "senders_anyone";
    static final String KEY_CONTACTS = "senders_contacts";
    static final String KEY_NONE = "senders_none";
    static final String KEY_STARRED = "senders_starred_contacts";
    private final boolean mIsMessages;
    private final PackageManager mPackageManager;
    private PreferenceCategory mPreferenceCategory;
    private RadioButtonPreference.OnClickListener mRadioButtonClickListener;
    private List<RadioButtonPreference> mRadioButtonPreferences;
    private static final Intent ALL_CONTACTS_INTENT = new Intent(ExtraContacts.Intents.UI.LIST_DEFAULT);
    private static final Intent STARRED_CONTACTS_INTENT = new Intent(ExtraContacts.Intents.UI.LIST_STARRED_ACTION);
    private static final Intent FALLBACK_INTENT = new Intent("android.intent.action.MAIN");

    public ZenModePrioritySendersPreferenceController(Context context, String str, Lifecycle lifecycle, boolean z) {
        super(context, str, lifecycle);
        this.mRadioButtonPreferences = new ArrayList();
        this.mRadioButtonClickListener = new RadioButtonPreference.OnClickListener() { // from class: com.android.settings.notification.zen.ZenModePrioritySendersPreferenceController.1
            @Override // com.android.settingslib.widget.RadioButtonPreference.OnClickListener
            public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
                int keyToSetting = ZenModePrioritySendersPreferenceController.keyToSetting(radioButtonPreference.getKey());
                if (keyToSetting != ZenModePrioritySendersPreferenceController.this.getPrioritySenders()) {
                    ZenModePrioritySendersPreferenceController zenModePrioritySendersPreferenceController = ZenModePrioritySendersPreferenceController.this;
                    zenModePrioritySendersPreferenceController.mBackend.saveSenders(zenModePrioritySendersPreferenceController.mIsMessages ? 4 : 8, keyToSetting);
                }
            }
        };
        this.mIsMessages = z;
        this.mPackageManager = this.mContext.getPackageManager();
        Intent intent = FALLBACK_INTENT;
        if (intent.hasCategory("android.intent.category.APP_CONTACTS")) {
            return;
        }
        intent.addCategory("android.intent.category.APP_CONTACTS");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getPrioritySenders() {
        return this.mIsMessages ? this.mBackend.getPriorityMessageSenders() : this.mBackend.getPriorityCallSenders();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private String getSummary(String str) {
        char c;
        switch (str.hashCode()) {
            case -1145842476:
                if (str.equals(KEY_STARRED)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -133103980:
                if (str.equals(KEY_CONTACTS)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1725241211:
                if (str.equals(KEY_ANY)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1767544313:
                if (str.equals(KEY_NONE)) {
                    c = 3;
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
                if (c != 2) {
                    return null;
                }
                return this.mContext.getResources().getString(this.mIsMessages ? R.string.zen_mode_all_messages_summary : R.string.zen_mode_all_calls_summary);
            }
            return this.mBackend.getContactsNumberSummary(this.mContext);
        }
        return this.mBackend.getStarredContactsSummary(this.mContext);
    }

    private View.OnClickListener getWidgetClickListener(final String str) {
        if (KEY_CONTACTS.equals(str) || KEY_STARRED.equals(str)) {
            if (!KEY_STARRED.equals(str) || isStarredIntentValid()) {
                if (!KEY_CONTACTS.equals(str) || isContactsIntentValid()) {
                    return new View.OnClickListener() { // from class: com.android.settings.notification.zen.ZenModePrioritySendersPreferenceController.2
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view) {
                            if (ZenModePrioritySendersPreferenceController.KEY_STARRED.equals(str) && ZenModePrioritySendersPreferenceController.STARRED_CONTACTS_INTENT.resolveActivity(ZenModePrioritySendersPreferenceController.this.mPackageManager) != null) {
                                ((AbstractPreferenceController) ZenModePrioritySendersPreferenceController.this).mContext.startActivity(ZenModePrioritySendersPreferenceController.STARRED_CONTACTS_INTENT);
                            } else if (!ZenModePrioritySendersPreferenceController.KEY_CONTACTS.equals(str) || ZenModePrioritySendersPreferenceController.ALL_CONTACTS_INTENT.resolveActivity(ZenModePrioritySendersPreferenceController.this.mPackageManager) == null) {
                                ((AbstractPreferenceController) ZenModePrioritySendersPreferenceController.this).mContext.startActivity(ZenModePrioritySendersPreferenceController.FALLBACK_INTENT);
                            } else {
                                ((AbstractPreferenceController) ZenModePrioritySendersPreferenceController.this).mContext.startActivity(ZenModePrioritySendersPreferenceController.ALL_CONTACTS_INTENT);
                            }
                        }
                    };
                }
                return null;
            }
            return null;
        }
        return null;
    }

    private boolean isContactsIntentValid() {
        return (ALL_CONTACTS_INTENT.resolveActivity(this.mPackageManager) == null && FALLBACK_INTENT.resolveActivity(this.mPackageManager) == null) ? false : true;
    }

    private boolean isStarredIntentValid() {
        return (STARRED_CONTACTS_INTENT.resolveActivity(this.mPackageManager) == null && FALLBACK_INTENT.resolveActivity(this.mPackageManager) == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int keyToSetting(String str) {
        char c;
        switch (str.hashCode()) {
            case -1145842476:
                if (str.equals(KEY_STARRED)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -133103980:
                if (str.equals(KEY_CONTACTS)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1725241211:
                if (str.equals(KEY_ANY)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1767544313:
                if (str.equals(KEY_NONE)) {
                    c = 3;
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
                return c != 2 ? -1 : 0;
            }
            return 1;
        }
        return 2;
    }

    private RadioButtonPreference makeRadioPreference(String str, int i) {
        RadioButtonPreference radioButtonPreference = new RadioButtonPreference(this.mPreferenceCategory.getContext());
        radioButtonPreference.setKey(str);
        radioButtonPreference.setTitle(i);
        radioButtonPreference.setOnClickListener(this.mRadioButtonClickListener);
        View.OnClickListener widgetClickListener = getWidgetClickListener(str);
        if (widgetClickListener != null) {
            radioButtonPreference.setExtraWidgetOnClickListener(widgetClickListener);
        }
        this.mPreferenceCategory.addPreference(radioButtonPreference);
        this.mRadioButtonPreferences.add(radioButtonPreference);
        return radioButtonPreference;
    }

    private void updateSummaries() {
        for (RadioButtonPreference radioButtonPreference : this.mRadioButtonPreferences) {
            radioButtonPreference.setSummary(getSummary(radioButtonPreference.getKey()));
        }
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreferenceCategory = preferenceCategory;
        if (preferenceCategory.findPreference(KEY_ANY) == null) {
            makeRadioPreference(KEY_STARRED, R.string.zen_mode_from_starred);
            makeRadioPreference(KEY_CONTACTS, R.string.zen_mode_from_contacts);
            makeRadioPreference(KEY_ANY, R.string.zen_mode_from_anyone);
            makeRadioPreference(KEY_NONE, this.mIsMessages ? R.string.zen_mode_none_messages : R.string.zen_mode_none_calls);
            updateSummaries();
        }
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.KEY;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        super.onResume();
        updateSummaries();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int prioritySenders = getPrioritySenders();
        for (RadioButtonPreference radioButtonPreference : this.mRadioButtonPreferences) {
            radioButtonPreference.setChecked(keyToSetting(radioButtonPreference.getKey()) == prioritySenders);
        }
    }
}
