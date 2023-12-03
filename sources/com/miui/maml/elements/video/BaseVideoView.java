package com.miui.maml.elements.video;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import java.io.IOException;

/* loaded from: classes2.dex */
public abstract class BaseVideoView extends SurfaceView {
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener;
    private int mAudioFocusType;
    private AudioManager mAudioManager;
    private MediaPlayer.OnCompletionListener mCompletionListener;
    protected Context mContext;
    private int mCurrentState;
    private MediaDataSource mDataSource;
    private IndexedVariable mDurationVar;
    private MediaPlayer.OnErrorListener mErrorListener;
    private MediaPlayer.OnInfoListener mInfoListener;
    private boolean mLooping;
    private MediaPlayer mMediaPlayer;
    private String mName;
    private IndexedVariable mPositionVar;
    private MediaPlayer.OnPreparedListener mPreparedListener;
    protected SurfaceHolder.Callback mSHCallback;
    protected int mScaleMode;
    private int mSeekWhenPrepared;
    private MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener;
    private IndexedVariable mStateVar;
    protected Surface mSurface;
    private int mTargetState;
    protected int mVideoHeight;
    protected int mVideoWidth;
    private float mVolume;

    public BaseVideoView(Context context) {
        super(context, null);
        this.mSurface = null;
        this.mScaleMode = 1;
        this.mCurrentState = 0;
        this.mTargetState = 0;
        this.mMediaPlayer = null;
        this.mAudioFocusType = 0;
        this.mSHCallback = new SurfaceHolder.Callback() { // from class: com.miui.maml.elements.video.BaseVideoView.1
            @Override // android.view.SurfaceHolder.Callback
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
                boolean z = BaseVideoView.this.mTargetState == 3;
                BaseVideoView baseVideoView = BaseVideoView.this;
                boolean z2 = baseVideoView.mVideoWidth == i2 && baseVideoView.mVideoHeight == i3;
                if (baseVideoView.mMediaPlayer != null && z && z2) {
                    if (BaseVideoView.this.mSeekWhenPrepared != 0) {
                        BaseVideoView baseVideoView2 = BaseVideoView.this;
                        baseVideoView2.seekTo(baseVideoView2.mSeekWhenPrepared);
                    }
                    BaseVideoView.this.start();
                }
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Log.d("BaseVideoView", "surfaceCreated " + surfaceHolder);
                BaseVideoView.this.onSurfaceCreated(surfaceHolder);
                BaseVideoView.this.openVideo();
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                BaseVideoView.this.onSurfaceDestroyed();
            }
        };
        this.mCompletionListener = new MediaPlayer.OnCompletionListener() { // from class: com.miui.maml.elements.video.BaseVideoView.2
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mediaPlayer) {
                BaseVideoView.this.mTargetState = 5;
                BaseVideoView.this.updateStateVar(5);
                BaseVideoView baseVideoView = BaseVideoView.this;
                baseVideoView.updatePositionVar(baseVideoView.getDuration());
                if (BaseVideoView.this.mAudioFocusType != 0) {
                    BaseVideoView.this.mAudioManager.abandonAudioFocus(null);
                }
            }
        };
        this.mInfoListener = new MediaPlayer.OnInfoListener() { // from class: com.miui.maml.elements.video.BaseVideoView.3
            @Override // android.media.MediaPlayer.OnInfoListener
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
                Log.d("BaseVideoView", "onInfo: " + i + "," + i2);
                return true;
            }
        };
        this.mErrorListener = new MediaPlayer.OnErrorListener() { // from class: com.miui.maml.elements.video.BaseVideoView.4
            @Override // android.media.MediaPlayer.OnErrorListener
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                Log.d("BaseVideoView", "Error: " + i + "," + i2);
                BaseVideoView.this.mTargetState = -1;
                BaseVideoView.this.updateStateVar(-1);
                return true;
            }
        };
        this.mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() { // from class: com.miui.maml.elements.video.BaseVideoView.5
            @Override // android.media.MediaPlayer.OnVideoSizeChangedListener
            public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i2) {
                BaseVideoView.this.mVideoWidth = mediaPlayer.getVideoWidth();
                BaseVideoView.this.mVideoHeight = mediaPlayer.getVideoHeight();
                BaseVideoView baseVideoView = BaseVideoView.this;
                if (baseVideoView.mVideoWidth == 0 || baseVideoView.mVideoHeight == 0) {
                    return;
                }
                baseVideoView.updateVideoSize();
            }
        };
        this.mPreparedListener = new MediaPlayer.OnPreparedListener() { // from class: com.miui.maml.elements.video.BaseVideoView.6
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mediaPlayer) {
                BaseVideoView.this.mVideoWidth = mediaPlayer.getVideoWidth();
                BaseVideoView.this.mVideoHeight = mediaPlayer.getVideoHeight();
                int i = BaseVideoView.this.mSeekWhenPrepared;
                if (i != 0) {
                    BaseVideoView.this.seekTo(i);
                }
                mediaPlayer.setLooping(BaseVideoView.this.mLooping);
                mediaPlayer.setVolume(BaseVideoView.this.mVolume, BaseVideoView.this.mVolume);
                BaseVideoView.this.updateStateVar(2);
                BaseVideoView baseVideoView = BaseVideoView.this;
                baseVideoView.updateDurationVar(baseVideoView.getDuration());
                if (BaseVideoView.this.mTargetState == 3) {
                    BaseVideoView.this.start();
                }
            }
        };
        this.mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() { // from class: com.miui.maml.elements.video.BaseVideoView.7
            @Override // android.media.AudioManager.OnAudioFocusChangeListener
            public void onAudioFocusChange(int i) {
                if (i != -1) {
                    return;
                }
                BaseVideoView.this.pause();
            }
        };
        this.mContext = context;
        this.mVideoWidth = 0;
        this.mVideoHeight = 0;
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
        this.mCurrentState = 0;
        this.mTargetState = 0;
    }

    private void checkAudioFocus() {
        if (this.mAudioFocusType == 0) {
            this.mAudioManager.abandonAudioFocus(this.mAudioFocusChangeListener);
        } else if (this.mMediaPlayer != null) {
            this.mAudioManager.requestAudioFocus(this.mAudioFocusChangeListener, 3, 1);
        }
    }

    private String getStateName(int i) {
        switch (i) {
            case -1:
                return "state_error";
            case 0:
                return "state_idle";
            case 1:
                return "state_preparing";
            case 2:
                return "state_prepared";
            case 3:
                return "state_playing";
            case 4:
                return "state_paused";
            case 5:
                return "state_playback_completed";
            default:
                return "";
        }
    }

    private void initIndexedVariable(Variables variables) {
        if (TextUtils.isEmpty(this.mName)) {
            return;
        }
        this.mDurationVar = new IndexedVariable(this.mName + ".duration", variables, true);
        this.mPositionVar = new IndexedVariable(this.mName + ".position", variables, true);
        IndexedVariable indexedVariable = new IndexedVariable(this.mName + ".playState", variables, false);
        this.mStateVar = indexedVariable;
        indexedVariable.set(getStateName(this.mCurrentState));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openVideo() {
        Surface surface;
        if (this.mDataSource == null || (surface = this.mSurface) == null || !surface.isValid()) {
            return;
        }
        releaseMedia(false);
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            this.mMediaPlayer = mediaPlayer;
            mediaPlayer.setOnPreparedListener(this.mPreparedListener);
            this.mMediaPlayer.setOnVideoSizeChangedListener(this.mSizeChangedListener);
            this.mMediaPlayer.setOnCompletionListener(this.mCompletionListener);
            this.mMediaPlayer.setOnErrorListener(this.mErrorListener);
            this.mMediaPlayer.setOnInfoListener(this.mInfoListener);
            this.mMediaPlayer.setDataSource(this.mDataSource);
            if (this.mScaleMode == 2) {
                this.mMediaPlayer.setVideoScalingMode(2);
            }
            this.mMediaPlayer.setAudioStreamType(3);
            this.mMediaPlayer.prepare();
            this.mMediaPlayer.setSurface(this.mSurface);
            updateStateVar(1);
        } catch (IOException e) {
            Log.w("BaseVideoView", "Unable to open content: " + this.mDataSource, e);
            this.mCurrentState = -1;
            this.mTargetState = -1;
            this.mErrorListener.onError(this.mMediaPlayer, 1, 0);
        } catch (IllegalArgumentException e2) {
            Log.w("BaseVideoView", "Unable to open content: " + this.mDataSource, e2);
            this.mCurrentState = -1;
            this.mTargetState = -1;
            this.mErrorListener.onError(this.mMediaPlayer, 1, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDurationVar(int i) {
        IndexedVariable indexedVariable = this.mDurationVar;
        if (indexedVariable != null) {
            indexedVariable.set(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePositionVar(int i) {
        IndexedVariable indexedVariable = this.mPositionVar;
        if (indexedVariable != null) {
            indexedVariable.set(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateStateVar(int i) {
        this.mCurrentState = i;
        IndexedVariable indexedVariable = this.mStateVar;
        if (indexedVariable != null) {
            indexedVariable.set(getStateName(i));
        }
    }

    protected abstract void addSurfaceHolderCallback();

    public void doTick() {
        if (isPlaying()) {
            updatePositionVar(getCurrentPosition());
        }
    }

    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return this.mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (isInPlaybackState()) {
            return this.mMediaPlayer.getDuration();
        }
        return -1;
    }

    public void init(Variables variables) {
        addSurfaceHolderCallback();
        initIndexedVariable(variables);
    }

    public boolean isInPlaybackState() {
        int i;
        return (this.mMediaPlayer == null || (i = this.mCurrentState) == -1 || i == 0 || i == 1) ? false : true;
    }

    public boolean isPlaying() {
        return isInPlaybackState() && this.mMediaPlayer.isPlaying();
    }

    @Override // android.view.SurfaceView, android.view.View
    protected void onDetachedFromWindow() {
        releaseMedia(true);
        super.onDetachedFromWindow();
    }

    protected abstract void onSurfaceCreated(SurfaceHolder surfaceHolder);

    protected abstract void onSurfaceDestroyed();

    public void pause() {
        if (isInPlaybackState() && this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.pause();
            updateStateVar(4);
        }
        this.mTargetState = 4;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void releaseMedia(boolean z) {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            this.mMediaPlayer = null;
            this.mCurrentState = 0;
            if (z) {
                this.mTargetState = 0;
            }
            if (this.mAudioFocusType != 0) {
                this.mAudioManager.abandonAudioFocus(null);
            }
        }
    }

    public void seekTo(int i) {
        if (!isInPlaybackState()) {
            this.mSeekWhenPrepared = i;
            return;
        }
        if (Build.VERSION.SDK_INT < 26) {
            this.mMediaPlayer.seekTo(i);
        } else if (this.mCurrentState == 4) {
            this.mMediaPlayer.seekTo(i, 0);
        } else {
            this.mMediaPlayer.seekTo(i, 2);
        }
        this.mSeekWhenPrepared = 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void setFormat(int i);

    public void setLooping(boolean z) {
        boolean z2 = this.mLooping;
        if (z2 != z) {
            MediaPlayer mediaPlayer = this.mMediaPlayer;
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(z2);
            }
            this.mLooping = z;
        }
    }

    public void setName(String str) {
        this.mName = str;
    }

    public void setScaleMode(int i) {
        if (i > 3 || i < 1) {
            return;
        }
        this.mScaleMode = i;
    }

    public void setVideoDataSource(MediaDataSource mediaDataSource) {
        this.mDataSource = mediaDataSource;
        this.mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void setVolume(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        } else if (f > 1.0f) {
            f = 1.0f;
        }
        int i = f == 0.0f ? 0 : 1;
        if (i != this.mAudioFocusType) {
            this.mAudioFocusType = i;
        }
        checkAudioFocus();
        if (this.mVolume != f) {
            MediaPlayer mediaPlayer = this.mMediaPlayer;
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(f, f);
            }
            this.mVolume = f;
        }
    }

    public void start() {
        if (isInPlaybackState()) {
            checkAudioFocus();
            this.mMediaPlayer.start();
            updateStateVar(3);
        }
        this.mTargetState = 3;
    }

    public void stopPlayback() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            updateStateVar(0);
            this.mTargetState = 0;
            this.mAudioManager.abandonAudioFocus(null);
        }
    }

    protected abstract void updateVideoSize();
}
