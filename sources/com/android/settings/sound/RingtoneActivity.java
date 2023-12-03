package com.android.settings.sound;

import com.android.settings.R;

/* loaded from: classes2.dex */
public class RingtoneActivity extends BaseSoundActivity {
    @Override // com.android.settings.sound.BaseSoundActivity
    protected int getRingtoneTitleId() {
        return R.string.ringtone_title;
    }

    @Override // com.android.settings.sound.BaseSoundActivity
    protected int getRingtoneType() {
        return 1;
    }
}
