package com.android.settings.notification;

import android.content.Context;
import android.content.Intent;
import android.media.ExtraRingtone;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import com.android.settings.MiuiDefaultRingtonePreference;
import com.android.settings.RingtonePreference;

/* loaded from: classes2.dex */
public class MiuiNotificationSoundPreference extends RingtonePreference {
    private Uri mRingtone;

    public MiuiNotificationSoundPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private void updateRingtoneName(final Uri uri) {
        new AsyncTask<Object, Void, CharSequence>() { // from class: com.android.settings.notification.MiuiNotificationSoundPreference.1
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.AsyncTask
            public CharSequence doInBackground(Object... objArr) {
                return ExtraRingtone.getRingtoneTitle(MiuiNotificationSoundPreference.this.getContext(), uri, false);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(CharSequence charSequence) {
                MiuiNotificationSoundPreference.this.setSummary(charSequence);
            }
        }.execute(new Object[0]);
    }

    @Override // com.android.settings.RingtonePreference
    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (intent != null) {
            Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            try {
                if ((intent.getFlags() & 1) != 0) {
                    getContext().getContentResolver().takePersistableUriPermission(uri, 1);
                }
            } catch (Exception e) {
                Log.d("Ringtone", " failed to take persistableUriPermission " + uri, e);
            }
            setRingtone(uri);
            callChangeListener(uri);
        }
        return true;
    }

    @Override // com.android.settings.RingtonePreference
    public void onPrepareRingtonePickerIntent(Intent intent) {
        super.onPrepareRingtonePickerIntent(intent);
        intent.setPackage("com.android.thememanager");
        MiuiDefaultRingtonePreference.addMiuiNaturalSound(getContext(), intent);
    }

    @Override // com.android.settings.RingtonePreference
    protected Uri onRestoreRingtone() {
        return this.mRingtone;
    }

    public void setRingtone(Uri uri) {
        this.mRingtone = uri;
        setSummary(" ");
        updateRingtoneName(this.mRingtone);
    }
}
