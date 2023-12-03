package com.android.settings.notification;

import android.content.Context;
import com.android.settings.Utils;

/* loaded from: classes2.dex */
public class PhoneRingtonePreferenceController extends RingtonePreferenceControllerBase {
    public PhoneRingtonePreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "phone_ringtone";
    }

    @Override // com.android.settings.notification.RingtonePreferenceControllerBase
    public int getRingtoneType() {
        return 1;
    }

    @Override // com.android.settings.notification.RingtonePreferenceControllerBase, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return Utils.isVoiceCapable(this.mContext);
    }
}
