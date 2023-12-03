package com.android.settings.development.autofill;

import android.content.Context;
import android.util.AttributeSet;

/* loaded from: classes.dex */
public final class AutofillMaxPartitionsPreference extends AbstractGlobalSettingsPreference {
    public AutofillMaxPartitionsPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, "autofill_max_partitions_size", 10);
    }

    @Override // com.android.settings.development.autofill.AbstractGlobalSettingsPreference, com.android.settingslib.miuisettings.preference.EditTextPreference, androidx.preference.Preference
    public /* bridge */ /* synthetic */ void onAttached() {
        super.onAttached();
    }

    @Override // com.android.settings.development.autofill.AbstractGlobalSettingsPreference, com.android.settingslib.miuisettings.preference.EditTextPreference, androidx.preference.Preference
    public /* bridge */ /* synthetic */ void onDetached() {
        super.onDetached();
    }
}
