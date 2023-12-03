package com.android.settings.sound;

import android.content.Intent;
import android.media.RingtoneManager;
import com.android.settings.R;

/* loaded from: classes2.dex */
public class SmsReceivedActivity extends BaseSoundActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.sound.BaseSoundActivity
    public Intent getPickerIntent() {
        Intent pickerIntent = super.getPickerIntent();
        pickerIntent.putExtra("android.intent.extra.ringtone.TYPE", 2);
        pickerIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
        return pickerIntent;
    }

    @Override // com.android.settings.sound.BaseSoundActivity
    protected int getRingtoneTitleId() {
        return R.string.sms_received_sound_title;
    }

    @Override // com.android.settings.sound.BaseSoundActivity
    protected int getRingtoneType() {
        return 16;
    }
}
