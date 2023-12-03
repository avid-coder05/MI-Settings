package com.android.settings.sound;

import android.content.Context;
import android.content.Intent;
import android.media.ExtraRingtoneManager;
import android.net.Uri;
import android.util.AttributeSet;
import com.android.settings.MiuiDefaultRingtonePreference;

/* loaded from: classes2.dex */
public class MiuiWorkRingtonePreference extends MiuiDefaultRingtonePreference {
    public MiuiWorkRingtonePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.android.settings.DefaultRingtonePreference
    public Uri getUri() {
        return ExtraRingtoneManager.getDefaultSoundSettingUri(this.mUserContext, getRingtoneType());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.MiuiDefaultRingtonePreference, androidx.preference.Preference
    public void onClick() {
        Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
        onPrepareRingtonePickerIntent(intent);
        setIntent(intent);
    }

    @Override // com.android.settings.DefaultRingtonePreference, com.android.settings.RingtonePreference
    protected Uri onRestoreRingtone() {
        return ExtraRingtoneManager.getDefaultSoundSettingUri(this.mUserContext, getRingtoneType());
    }
}
