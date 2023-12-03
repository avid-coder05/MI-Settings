package com.android.settings.notification;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import com.android.settings.R;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class MiuiMusicMuteActivity extends Activity {
    private Context mContext;
    private AlertDialog mDialog;

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog_Theme_DayNight);
        builder.setTitle(R.string.title_miui_music_mute_by_user);
        builder.setMessage(R.string.content_miui_music_mute_by_user);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.notification.MiuiMusicMuteActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MiuiMusicMuteActivity miuiMusicMuteActivity = MiuiMusicMuteActivity.this;
                miuiMusicMuteActivity.updateMusicMute(miuiMusicMuteActivity.mContext, true);
                Log.d("MiuiMusicMuteActivity", "Niel---- [N]keep mute.");
                MiuiMusicMuteActivity.this.finish();
            }
        });
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() { // from class: com.android.settings.notification.MiuiMusicMuteActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MiuiMusicMuteActivity miuiMusicMuteActivity = MiuiMusicMuteActivity.this;
                miuiMusicMuteActivity.updateMusicMute(miuiMusicMuteActivity.mContext, false);
                Log.d("MiuiMusicMuteActivity", "Niel---- [Y]unmute.");
                MiuiMusicMuteActivity.this.finish();
            }
        });
        builder.setCancelable(false);
        this.mDialog = builder.create();
        if (isFinishing()) {
            return;
        }
        this.mDialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMusicMute(Context context, boolean z) {
        Settings.System.putIntForUser(context.getContentResolver(), "mute_music_at_silent", z ? 1 : 0, -3);
        MiuiSilentSettingsFragment.updateSilentMode(context, z);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getApplicationContext();
        initDialog();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        super.onDestroy();
    }
}
