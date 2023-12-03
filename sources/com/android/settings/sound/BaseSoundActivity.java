package com.android.settings.sound;

import android.app.Activity;
import android.content.Intent;
import android.media.ExtraRingtoneManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import miui.os.Build;

/* loaded from: classes2.dex */
public abstract class BaseSoundActivity extends Activity {
    /* JADX INFO: Access modifiers changed from: protected */
    public Intent getPickerIntent() {
        Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
        intent.setClassName("com.android.thememanager", "com.android.thememanager.activity.ThemeTabActivity");
        intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", false);
        int ringtoneType = getRingtoneType();
        intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", (Build.IS_HONGMI && ringtoneType == 1) ? false : true);
        intent.putExtra("android.intent.extra.ringtone.TYPE", ringtoneType);
        intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(ringtoneType));
        intent.putExtra("android.intent.extra.ringtone.TITLE", getString(getRingtoneTitleId()));
        intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", ExtraRingtoneManager.getDefaultSoundSettingUri(this, ringtoneType));
        return intent;
    }

    protected abstract int getRingtoneTitleId();

    protected abstract int getRingtoneType();

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (intent != null && i == 1) {
            ExtraRingtoneManager.saveDefaultSound(this, getRingtoneType(), (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI"));
        }
        finish();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        startActivityForResult(getPickerIntent(), 1);
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        setVisible(true);
    }
}
