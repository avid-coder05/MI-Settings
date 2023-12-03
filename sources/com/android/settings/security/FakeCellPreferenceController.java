package com.android.settings.security;

import android.content.Context;
import com.android.settings.FakeCellSettings;
import com.android.settings.search.tree.SecuritySettingsTree;
import com.android.settingslib.core.AbstractPreferenceController;

/* loaded from: classes2.dex */
public class FakeCellPreferenceController extends AbstractPreferenceController {
    public FakeCellPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return SecuritySettingsTree.MANAGE_FAKECELL_SETTINGS;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return FakeCellSettings.supportDetectFakecell();
    }
}
