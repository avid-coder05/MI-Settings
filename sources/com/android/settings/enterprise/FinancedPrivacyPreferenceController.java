package com.android.settings.enterprise;

import android.content.Context;
import androidx.preference.Preference;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.Objects;

/* loaded from: classes.dex */
public class FinancedPrivacyPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final String mPreferenceKey;
    private final PrivacyPreferenceControllerHelper mPrivacyPreferenceControllerHelper;

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public FinancedPrivacyPreferenceController(Context context) {
        this(context, "financed_privacy");
        Objects.requireNonNull(context);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    @VisibleForTesting
    FinancedPrivacyPreferenceController(Context context, PrivacyPreferenceControllerHelper privacyPreferenceControllerHelper, String str) {
        super(context);
        Objects.requireNonNull(context);
        Objects.requireNonNull(privacyPreferenceControllerHelper);
        this.mPrivacyPreferenceControllerHelper = privacyPreferenceControllerHelper;
        this.mPreferenceKey = str;
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public FinancedPrivacyPreferenceController(Context context, String str) {
        this(context, new PrivacyPreferenceControllerHelper(context), str);
        Objects.requireNonNull(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mPreferenceKey;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mPrivacyPreferenceControllerHelper.isFinancedDevice();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mPrivacyPreferenceControllerHelper.updateState(preference);
    }
}
