package com.android.settings.faceunlock;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.MiuiSecurityChooseUnlock;
import com.android.settings.ProvisionSetUpMiuiSecurityChooseUnlock;
import com.android.settings.R;

/* loaded from: classes.dex */
public class MiuiFaceDataIntroduction extends Activity {
    private LinearLayout mBackImage;
    private TextureView mFaceInputIntroductiontVideo;
    private TextView mFaceIntroductionTitle;
    private MediaPlayer mMediaPlayer;
    private Button mSettingsPasswordBtn;
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() { // from class: com.android.settings.faceunlock.MiuiFaceDataIntroduction.3
        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            MiuiFaceDataIntroduction.this.playVideo();
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

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0 && keyEvent.getKeyCode() == 4) {
            setResult(0);
            finish();
            return true;
        }
        return true;
    }

    @Override // android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i != 1) {
            return;
        }
        if (i2 == -1) {
            setResult(-1, intent);
        } else {
            setResult(0);
        }
        finish();
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (KeyguardSettingsFaceUnlockUtils.isLargeScreen(this)) {
            setResult(0);
            finish();
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTheme(R.style.Theme_Dark_Settings);
        setContentView(R.layout.miui_face_introduction);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.back_image);
        this.mBackImage = linearLayout;
        linearLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.faceunlock.MiuiFaceDataIntroduction.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MiuiFaceDataIntroduction.this.finish();
            }
        });
        TextureView textureView = (TextureView) findViewById(R.id.miui_face_input_introduction_video);
        this.mFaceInputIntroductiontVideo = textureView;
        textureView.setSurfaceTextureListener(this.surfaceTextureListener);
        TextView textView = (TextView) findViewById(R.id.miui_face_input_introduction_title);
        this.mFaceIntroductionTitle = textView;
        textView.setText(R.string.face_data_input_title);
        Button button = (Button) findViewById(R.id.miui_face_recoginition_intorduction_next);
        this.mSettingsPasswordBtn = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.faceunlock.MiuiFaceDataIntroduction.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent(MiuiFaceDataIntroduction.this, KeyguardSettingsFaceUnlockUtils.isDeviceProvisioned(MiuiFaceDataIntroduction.this.getApplicationContext()) ? MiuiSecurityChooseUnlock.InternalActivity.class : ProvisionSetUpMiuiSecurityChooseUnlock.InternalActivity.class);
                intent.putExtra("add_keyguard_password_then_add_face_recoginition", true);
                intent.putExtra(":android:show_fragment_title", R.string.empty_title);
                MiuiFaceDataIntroduction.this.startActivityForResult(intent, 1);
            }
        });
        KeyguardSettingsFaceUnlockUtils.setFaceEnrollViewStatus(this, getWindow());
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mBackImage.getLayoutParams();
        marginLayoutParams.topMargin = KeyguardSettingsFaceUnlockUtils.getStatusBarHeight(getApplicationContext());
        this.mBackImage.setLayoutParams(marginLayoutParams);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.mMediaPlayer.release();
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(4866);
    }

    public void playVideo() {
        new MediaPlayer();
        MediaPlayer create = MediaPlayer.create(this, R.raw.miui_face_input_suggestion_video);
        this.mMediaPlayer = create;
        create.setSurface(new Surface(this.mFaceInputIntroductiontVideo.getSurfaceTexture()));
        this.mMediaPlayer.setLooping(true);
        this.mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.android.settings.faceunlock.MiuiFaceDataIntroduction.4
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (MiuiFaceDataIntroduction.this.mMediaPlayer == null || MiuiFaceDataIntroduction.this.mMediaPlayer.isPlaying()) {
                    return;
                }
                MiuiFaceDataIntroduction.this.mMediaPlayer.start();
            }
        });
    }
}
