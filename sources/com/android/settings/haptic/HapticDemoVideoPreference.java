package com.android.settings.haptic;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.haptic.HapticDemoVideoPreference;
import com.android.settings.haptic.utils.UiUtils;
import com.android.settings.sound.VibratorFeatureUtil;
import com.android.settingslib.util.MiStatInterfaceUtils;
import java.lang.ref.WeakReference;
import miui.util.HapticFeedbackUtil;

/* loaded from: classes.dex */
public class HapticDemoVideoPreference extends Preference {
    private View VideoContainer;
    private BgHandler mBgHandler;
    private HandlerThread mBgHandlerThread;
    private HapticFeedbackUtil mHapticCompat;
    private boolean mIsFinish;
    private boolean mIsFinishRenderingStart;
    private boolean mIsVisible;
    private MediaPlayer mMediaPlayer;
    private Runnable mPerformExtHapticFeedback;
    private View mPlayBtn;
    private View mRootView;
    private SharedPreferences mSharedPrefs;
    private boolean mSupportLinearMotorVibrate;
    private Surface mSurface;
    private TextureView mTextureView;
    private View mVideoBgImgHolder;
    private View mVideoBgImgItem;
    private IVideoState mVideoState;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.haptic.HapticDemoVideoPreference$3  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass3 implements MediaPlayer.OnInfoListener {
        AnonymousClass3() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onInfo$0() {
            HapticDemoVideoPreference.this.mVideoBgImgHolder.setVisibility(8);
        }

        @Override // android.media.MediaPlayer.OnInfoListener
        public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
            if (i == 3) {
                if (!HapticDemoVideoPreference.this.mIsFinishRenderingStart) {
                    HapticDemoVideoPreference.this.mIsFinishRenderingStart = true;
                    HapticDemoVideoPreference.this.mVideoBgImgHolder.post(new Runnable() { // from class: com.android.settings.haptic.HapticDemoVideoPreference$3$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            HapticDemoVideoPreference.AnonymousClass3.this.lambda$onInfo$0();
                        }
                    });
                    HapticDemoVideoPreference.this.performHapticFeedback();
                }
                return true;
            }
            return false;
        }
    }

    /* loaded from: classes.dex */
    private static class BgHandler extends Handler {
        private WeakReference<HapticDemoVideoPreference> mRefFrag;

        public BgHandler(HapticDemoVideoPreference hapticDemoVideoPreference, Looper looper) {
            super(looper);
            this.mRefFrag = new WeakReference<>(hapticDemoVideoPreference);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            HapticDemoVideoPreference hapticDemoVideoPreference = this.mRefFrag.get();
            int i = message.what;
            if (i == 1) {
                removeMessages(1);
                if (hapticDemoVideoPreference != null) {
                    hapticDemoVideoPreference.initMedia();
                }
            } else if (i == 2) {
                removeMessages(2);
                if (hapticDemoVideoPreference != null) {
                    hapticDemoVideoPreference.playMedia();
                }
            } else if (i == 3) {
                removeCallbacksAndMessages(null);
                if (hapticDemoVideoPreference != null) {
                    hapticDemoVideoPreference.stopPlayingVideo();
                }
            } else if (i != 4) {
            } else {
                removeCallbacksAndMessages(null);
                if (hapticDemoVideoPreference != null) {
                    hapticDemoVideoPreference.stopPlayingVideo();
                    hapticDemoVideoPreference.releaseMedia();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public interface IVideoState {
        void onHapticVideoStateChange(boolean z);
    }

    public HapticDemoVideoPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMediaPlayer = new MediaPlayer();
        this.mSharedPrefs = getContext().getSharedPreferences("IS_FIRST_START_HAPTIC_SP", 0);
        this.mIsFinish = true;
        this.mPerformExtHapticFeedback = new Runnable() { // from class: com.android.settings.haptic.HapticDemoVideoPreference$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                HapticDemoVideoPreference.this.lambda$new$0();
            }
        };
        setLayoutResource(R.layout.haptic_demo_main_video_layout);
        this.mHapticCompat = new HapticFeedbackUtil(getContext().getApplicationContext(), true);
        this.mSupportLinearMotorVibrate = UiUtils.isSupportLinearMotorVibrate();
        HandlerThread handlerThread = new HandlerThread("haptic_video", 5);
        this.mBgHandlerThread = handlerThread;
        handlerThread.start();
        this.mBgHandler = new BgHandler(this, this.mBgHandlerThread.getLooper());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initMedia() {
        Log.d("HapticDemoVideoPreferen", "initMedia");
        if (this.mMediaPlayer == null) {
            this.mMediaPlayer = new MediaPlayer();
        }
        this.mMediaPlayer.setSurface(this.mSurface);
        try {
            this.mMediaPlayer.setDataSource(getContext(), Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.main_haptic_video));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.android.settings.haptic.HapticDemoVideoPreference$$ExternalSyntheticLambda1
            @Override // android.media.MediaPlayer.OnPreparedListener
            public final void onPrepared(MediaPlayer mediaPlayer) {
                HapticDemoVideoPreference.this.lambda$initMedia$1(mediaPlayer);
            }
        });
        try {
            this.mMediaPlayer.prepare();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initMedia$1(MediaPlayer mediaPlayer) {
        if (this.mSharedPrefs.contains("IS_FIRST_START_HAPTIC")) {
            Log.d("HapticDemoVideoPreferen", "no need to play video");
        } else if (this.mVideoBgImgHolder != null) {
            Log.d("HapticDemoVideoPreferen", "first play video");
            playMedia();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        playExtPatternById(192);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$performHapticFeedback$6() {
        this.mPlayBtn.setVisibility(4);
        IVideoState iVideoState = this.mVideoState;
        if (iVideoState != null) {
            iVideoState.onHapticVideoStateChange(true);
        }
        getContext().getMainThreadHandler().removeCallbacks(this.mPerformExtHapticFeedback);
        getContext().getMainThreadHandler().postDelayed(this.mPerformExtHapticFeedback, 200L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$playMedia$4() {
        this.mPlayBtn.setVisibility(0);
        IVideoState iVideoState = this.mVideoState;
        if (iVideoState != null) {
            iVideoState.onHapticVideoStateChange(false);
        }
        this.mVideoBgImgItem.setBackground(getContext().getResources().getDrawable(R.drawable.img_main_video_bg));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$playMedia$5(MediaPlayer mediaPlayer) {
        this.mIsFinish = true;
        this.mMediaPlayer.seekTo(0);
        this.mPlayBtn.post(new Runnable() { // from class: com.android.settings.haptic.HapticDemoVideoPreference$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                HapticDemoVideoPreference.this.lambda$playMedia$4();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showVideoBgImgHolder$2() {
        View view = this.mVideoBgImgHolder;
        if (view != null) {
            view.setVisibility(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$stopPlayingVideo$3() {
        this.mPlayBtn.setVisibility(0);
        IVideoState iVideoState = this.mVideoState;
        if (iVideoState != null) {
            iVideoState.onHapticVideoStateChange(false);
        }
        this.mVideoBgImgItem.setBackground(getContext().getResources().getDrawable(R.drawable.img_main_video_bg));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performHapticFeedback() {
        if (this.mIsFinishRenderingStart) {
            if (!this.mSharedPrefs.contains("IS_FIRST_START_HAPTIC")) {
                this.mSharedPrefs.edit().putInt("IS_FIRST_START_HAPTIC", 1).apply();
            }
            getContext().getMainThreadHandler().post(new Runnable() { // from class: com.android.settings.haptic.HapticDemoVideoPreference$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    HapticDemoVideoPreference.this.lambda$performHapticFeedback$6();
                }
            });
            MiStatInterfaceUtils.trackPreferenceClick(HapticFragment.class.getName(), "haptic_main_video");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playMedia() {
        if (!stopPlayingVideo() && this.mIsFinish) {
            if (this.mMediaPlayer == null) {
                this.mMediaPlayer = new MediaPlayer();
            }
            try {
                this.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.android.settings.haptic.HapticDemoVideoPreference$$ExternalSyntheticLambda0
                    @Override // android.media.MediaPlayer.OnCompletionListener
                    public final void onCompletion(MediaPlayer mediaPlayer) {
                        HapticDemoVideoPreference.this.lambda$playMedia$5(mediaPlayer);
                    }
                });
                this.mIsFinish = false;
                this.mMediaPlayer.start();
                this.mMediaPlayer.setOnInfoListener(new AnonymousClass3());
                performHapticFeedback();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseMedia() {
        try {
            showVideoBgImgHolder();
            MediaPlayer mediaPlayer = this.mMediaPlayer;
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    this.mMediaPlayer.stop();
                }
                this.mMediaPlayer.setSurface(null);
                this.mMediaPlayer.release();
                this.mMediaPlayer = null;
                this.mIsFinishRenderingStart = false;
            }
        } catch (Exception e) {
            Log.e("HapticDemoVideoPreferen", "releaseMedia error " + e.getMessage());
        }
    }

    private void showVideoBgImgHolder() {
        getContext().getMainThreadHandler().post(new Runnable() { // from class: com.android.settings.haptic.HapticDemoVideoPreference$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                HapticDemoVideoPreference.this.lambda$showVideoBgImgHolder$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean stopPlayingVideo() {
        Log.d("HapticDemoVideoPreferen", "stopPlayingVideo");
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
            return false;
        }
        this.mPlayBtn.post(new Runnable() { // from class: com.android.settings.haptic.HapticDemoVideoPreference$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                HapticDemoVideoPreference.this.lambda$stopPlayingVideo$3();
            }
        });
        getContext().getMainThreadHandler().removeCallbacks(this.mPerformExtHapticFeedback);
        this.mMediaPlayer.seekTo(0);
        this.mMediaPlayer.pause();
        this.mIsFinish = true;
        this.mHapticCompat.release();
        return true;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        this.mRootView = view;
        view.setPadding(0, 0, 0, 0);
        this.mRootView.setBackgroundColor(0);
        this.VideoContainer = this.mRootView.findViewById(R.id.video_card_view);
        this.mTextureView = (TextureView) this.mRootView.findViewById(R.id.tv_item);
        View findViewById = this.mRootView.findViewById(R.id.img_item);
        this.mVideoBgImgItem = findViewById;
        findViewById.setBackground(getContext().getResources().getDrawable(R.drawable.img_main_video_bg));
        this.mVideoBgImgHolder = this.mRootView.findViewById(R.id.img_holder);
        this.mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() { // from class: com.android.settings.haptic.HapticDemoVideoPreference.1
            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                HapticDemoVideoPreference.this.mSurface = new Surface(surfaceTexture);
                if (!HapticDemoVideoPreference.this.mIsVisible || HapticDemoVideoPreference.this.mBgHandler == null) {
                    return;
                }
                HapticDemoVideoPreference.this.mBgHandler.sendEmptyMessage(1);
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                Log.d("HapticDemoVideoPreferen", "onSurfaceTextureDestroyed");
                HapticDemoVideoPreference.this.mSurface = null;
                if (HapticDemoVideoPreference.this.mBgHandler != null) {
                    HapticDemoVideoPreference.this.mBgHandler.sendEmptyMessage(4);
                    return true;
                }
                return true;
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }
        });
        this.mPlayBtn = this.mRootView.findViewById(R.id.play_button);
        this.VideoContainer.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.haptic.HapticDemoVideoPreference.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                if (HapticDemoVideoPreference.this.mBgHandler != null) {
                    HapticDemoVideoPreference.this.mBgHandler.sendEmptyMessage(2);
                }
            }
        });
    }

    public void onStart() {
        showVideoBgImgHolder();
    }

    public void onStop() {
        this.mIsVisible = false;
        Log.d("HapticDemoVideoPreferen", "onStop");
        BgHandler bgHandler = this.mBgHandler;
        if (bgHandler != null) {
            bgHandler.sendEmptyMessage(4);
        }
    }

    public void onVisible() {
        this.mIsVisible = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onVisible(boolean z) {
        this.mIsVisible = z;
        if (z) {
            BgHandler bgHandler = this.mBgHandler;
            if (bgHandler != null) {
                bgHandler.sendEmptyMessage(1);
                return;
            }
            return;
        }
        Log.d("HapticDemoVideoPreferen", "onVisible false");
        BgHandler bgHandler2 = this.mBgHandler;
        if (bgHandler2 != null) {
            bgHandler2.sendEmptyMessage(4);
        }
    }

    public void playExtPatternById(int i) {
        try {
            if (!HapticFeedbackUtil.isSupportLinearMotorVibrate()) {
                Log.e("HapticDemoVideoPreferen", "Not support linearMotor! id:" + i);
            } else if (this.mHapticCompat.isSupportExtHapticFeedback(i)) {
                Log.d("HapticDemoVideoPreferen", "performExtHapticFeedback id:" + i);
                this.mHapticCompat.performExtHapticFeedback(4, i, true);
                VibratorFeatureUtil.getInstance(getContext()).setAmplitude(Settings.System.getFloat(getContext().getContentResolver(), "haptic_feedback_infinite_intensity", 1.0f));
            } else {
                Log.e("HapticDemoVideoPreferen", "Not support this rtp:$rtpEffectId! id:" + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("HapticDemoVideoPreferen", "Not support this id:" + i + " exception:" + e.getMessage());
        }
    }

    public void setVideoState(IVideoState iVideoState) {
        this.mVideoState = iVideoState;
    }
}
