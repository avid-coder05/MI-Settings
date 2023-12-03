package com.android.settings.faceunlock;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.Surface;
import android.view.TextureView;
import android.widget.TextView;
import com.android.settings.R;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class MiuiFaceDataPrompt extends AppCompatActivity {
    private TextureView mFaceInputPromptVideo;
    private TextView mFacePromptTitle;
    private MediaPlayer mMediaPlayer;
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() { // from class: com.android.settings.faceunlock.MiuiFaceDataPrompt.1
        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            MiuiFaceDataPrompt.this.playVideo();
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    };

    @Override // androidx.core.app.ComponentActivity, android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0 && keyEvent.getKeyCode() == 4) {
            setResult(0);
            finish();
            return true;
        }
        return true;
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (KeyguardSettingsFaceUnlockUtils.isLargeScreen(this)) {
            return;
        }
        setResult(-1);
        finish();
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.miui_face_largescreen_prompt);
        ActionBar actionBar = getActionBar();
        if (actionBar != null && actionBar.isShowing()) {
            actionBar.hide();
        }
        TextureView textureView = (TextureView) findViewById(R.id.miui_face_input_prompt_video);
        this.mFaceInputPromptVideo = textureView;
        textureView.setSurfaceTextureListener(this.surfaceTextureListener);
        TextView textView = (TextView) findViewById(R.id.miui_face_input_prompt_title);
        this.mFacePromptTitle = textView;
        textView.setText(R.string.face_unlock_prompt);
        KeyguardSettingsFaceUnlockUtils.setFaceEnrollViewStatus(this, getWindow());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.mMediaPlayer.release();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(13058);
        getWindow().setStatusBarColor(0);
        getWindow().setNavigationBarColor(0);
        getWindow().addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
        getWindow().addFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
    }

    public void playVideo() {
        new MediaPlayer();
        MediaPlayer create = MediaPlayer.create(this, R.raw.miui_face_prompt);
        this.mMediaPlayer = create;
        create.setSurface(new Surface(this.mFaceInputPromptVideo.getSurfaceTexture()));
        this.mMediaPlayer.setLooping(true);
        this.mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.android.settings.faceunlock.MiuiFaceDataPrompt.2
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (MiuiFaceDataPrompt.this.mMediaPlayer == null || MiuiFaceDataPrompt.this.mMediaPlayer.isPlaying()) {
                    return;
                }
                MiuiFaceDataPrompt.this.mMediaPlayer.start();
            }
        });
    }
}
