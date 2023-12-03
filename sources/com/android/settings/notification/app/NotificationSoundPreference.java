package com.android.settings.notification.app;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import com.android.settings.R;
import com.android.settings.RingtonePreference;

/* loaded from: classes2.dex */
public class NotificationSoundPreference extends RingtonePreference {
    private Uri mRingtone;

    public NotificationSoundPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private void updateRingtoneName(final Uri uri) {
        new AsyncTask<Object, Void, CharSequence>() { // from class: com.android.settings.notification.app.NotificationSoundPreference.1
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.AsyncTask
            public CharSequence doInBackground(Object... objArr) {
                Uri uri2 = uri;
                return uri2 == null ? NotificationSoundPreference.this.getContext().getString(17041392) : RingtoneManager.isDefault(uri2) ? NotificationSoundPreference.this.getContext().getString(R.string.notification_sound_default) : "android.resource".equals(uri.getScheme()) ? NotificationSoundPreference.this.getContext().getString(R.string.notification_unknown_sound_title) : Ringtone.getTitle(NotificationSoundPreference.this.getContext(), uri, false, true);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(CharSequence charSequence) {
                NotificationSoundPreference.this.setSummary(charSequence);
            }
        }.execute(new Object[0]);
    }

    @Override // com.android.settings.RingtonePreference
    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (intent != null) {
            Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            setRingtone(uri);
            callChangeListener(uri);
            return true;
        }
        return true;
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
