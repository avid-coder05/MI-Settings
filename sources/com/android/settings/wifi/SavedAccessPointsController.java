package com.android.settings.wifi;

import android.content.Context;
import android.os.UserHandle;
import com.android.settingslib.core.AbstractPreferenceController;

/* loaded from: classes2.dex */
public class SavedAccessPointsController extends AbstractPreferenceController {
    public SavedAccessPointsController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "saved_wifi";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return UserHandle.myUserId() == 0;
    }
}
