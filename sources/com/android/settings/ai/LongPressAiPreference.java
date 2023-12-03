package com.android.settings.ai;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes.dex */
public class LongPressAiPreference extends Preference {
    private Handler mHandler;
    private VideoView mVideoView;

    public LongPressAiPreference(Context context) {
        super(context);
        this.mHandler = new Handler();
    }

    public LongPressAiPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mHandler = new Handler();
    }

    public LongPressAiPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mHandler = new Handler();
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(final View view) {
        super.onBindView(view);
        this.mVideoView = (VideoView) view.findViewById(R.id.img_long_press);
        this.mVideoView.setVideoURI(Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.aigesture));
        this.mVideoView.start();
        this.mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.android.settings.ai.LongPressAiPreference.1
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() { // from class: com.android.settings.ai.LongPressAiPreference.1.1
                    @Override // android.media.MediaPlayer.OnInfoListener
                    public boolean onInfo(MediaPlayer mediaPlayer2, int i, int i2) {
                        Log.d("LongPressAiPreference", "onInfo, what = " + i);
                        if (i == 3) {
                            view.findViewById(R.id.img_long_press_place_holder).setVisibility(8);
                            return true;
                        }
                        return false;
                    }
                });
                mediaPlayer.start();
            }
        });
        this.mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.android.settings.ai.LongPressAiPreference.2
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(final MediaPlayer mediaPlayer) {
                LongPressAiPreference.this.mHandler.removeCallbacksAndMessages(null);
                LongPressAiPreference.this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.ai.LongPressAiPreference.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            mediaPlayer.start();
                        } catch (IllegalStateException unused) {
                            Log.d("LongPressAiPreference", "release mp");
                            mediaPlayer.release();
                            LongPressAiPreference.this.mHandler.removeCallbacksAndMessages(null);
                        }
                    }
                }, 2000L);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.miuisettings.preference.Preference
    public View onCreateView(ViewGroup viewGroup) {
        super.onCreateView(viewGroup);
        setLayoutResource(R.layout.ai_settings_title_item);
        return null;
    }
}
