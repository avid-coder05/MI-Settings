package com.android.settings.wifi.p2p;

import android.content.Context;

/* loaded from: classes2.dex */
public class P2pPersistentCategoryPreferenceController extends P2pCategoryPreferenceController {
    public P2pPersistentCategoryPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "p2p_persistent_group";
    }

    @Override // com.android.settings.wifi.p2p.P2pCategoryPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return false;
    }
}
