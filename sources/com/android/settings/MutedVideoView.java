package com.android.settings;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.MediaController;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Vector;

/* loaded from: classes.dex */
public class MutedVideoView extends SurfaceView implements MediaController.MediaPlayerControl {
    private String TAG;
    private int mAudioSession;
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener;
    private boolean mCanPause;
    private boolean mCanSeekBack;
    private boolean mCanSeekForward;
    private MediaPlayer.OnCompletionListener mCompletionListener;
    private int mCurrentBufferPercentage;
    private int mCurrentState;
    private MediaPlayer.OnErrorListener mErrorListener;
    private Map<String, String> mHeaders;
    private MediaPlayer.OnInfoListener mInfoListener;
    private MediaController mMediaController;
    private MediaPlayer mMediaPlayer;
    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private MediaPlayer.OnErrorListener mOnErrorListener;
    private MediaPlayer.OnInfoListener mOnInfoListener;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private Vector<Pair<InputStream, MediaFormat>> mPendingSubtitleTracks;
    MediaPlayer.OnPreparedListener mPreparedListener;
    SurfaceHolder.Callback mSHCallback;
    private int mSeekWhenPrepared;
    MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener;
    private int mSurfaceHeight;
    private SurfaceHolder mSurfaceHolder;
    private int mSurfaceWidth;
    private int mTargetState;
    private Uri mUri;
    private int mVideoHeight;
    private int mVideoWidth;

    public MutedVideoView(Context context) {
        super(context);
        this.TAG = "MutedVideoView";
        this.mCurrentState = 0;
        this.mTargetState = 0;
        this.mSurfaceHolder = null;
        this.mMediaPlayer = null;
        this.mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() { // from class: com.android.settings.MutedVideoView.1
            @Override // android.media.MediaPlayer.OnVideoSizeChangedListener
            public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i2) {
                MutedVideoView.this.mVideoWidth = mediaPlayer.getVideoWidth();
                MutedVideoView.this.mVideoHeight = mediaPlayer.getVideoHeight();
                if (MutedVideoView.this.mVideoWidth == 0 || MutedVideoView.this.mVideoHeight == 0) {
                    return;
                }
                MutedVideoView.this.getHolder().setFixedSize(MutedVideoView.this.mVideoWidth, MutedVideoView.this.mVideoHeight);
                MutedVideoView.this.requestLayout();
            }
        };
        this.mPreparedListener = new MediaPlayer.OnPreparedListener() { // from class: com.android.settings.MutedVideoView.2
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mediaPlayer) {
                MutedVideoView.this.mCurrentState = 2;
                MutedVideoView mutedVideoView = MutedVideoView.this;
                mutedVideoView.mCanPause = mutedVideoView.mCanSeekBack = mutedVideoView.mCanSeekForward = true;
                if (MutedVideoView.this.mOnPreparedListener != null) {
                    MutedVideoView.this.mOnPreparedListener.onPrepared(MutedVideoView.this.mMediaPlayer);
                }
                if (MutedVideoView.this.mMediaController != null) {
                    MutedVideoView.this.mMediaController.setEnabled(true);
                }
                MutedVideoView.this.mVideoWidth = mediaPlayer.getVideoWidth();
                MutedVideoView.this.mVideoHeight = mediaPlayer.getVideoHeight();
                int i = MutedVideoView.this.mSeekWhenPrepared;
                if (i != 0) {
                    MutedVideoView.this.seekTo(i);
                }
                if (MutedVideoView.this.mVideoWidth == 0 || MutedVideoView.this.mVideoHeight == 0) {
                    if (MutedVideoView.this.mTargetState == 3) {
                        MutedVideoView.this.start();
                        return;
                    }
                    return;
                }
                MutedVideoView.this.getHolder().setFixedSize(MutedVideoView.this.mVideoWidth, MutedVideoView.this.mVideoHeight);
                if (MutedVideoView.this.mSurfaceWidth == MutedVideoView.this.mVideoWidth && MutedVideoView.this.mSurfaceHeight == MutedVideoView.this.mVideoHeight) {
                    if (MutedVideoView.this.mTargetState == 3) {
                        MutedVideoView.this.start();
                        if (MutedVideoView.this.mMediaController != null) {
                            MutedVideoView.this.mMediaController.show();
                        }
                    } else if (MutedVideoView.this.isPlaying()) {
                    } else {
                        if ((i != 0 || MutedVideoView.this.getCurrentPosition() > 0) && MutedVideoView.this.mMediaController != null) {
                            MutedVideoView.this.mMediaController.show(0);
                        }
                    }
                }
            }
        };
        this.mCompletionListener = new MediaPlayer.OnCompletionListener() { // from class: com.android.settings.MutedVideoView.3
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mediaPlayer) {
                MutedVideoView.this.mCurrentState = 5;
                MutedVideoView.this.mTargetState = 5;
                if (MutedVideoView.this.mMediaController != null) {
                    MutedVideoView.this.mMediaController.hide();
                }
                if (MutedVideoView.this.mOnCompletionListener != null) {
                    MutedVideoView.this.mOnCompletionListener.onCompletion(MutedVideoView.this.mMediaPlayer);
                }
            }
        };
        this.mInfoListener = new MediaPlayer.OnInfoListener() { // from class: com.android.settings.MutedVideoView.4
            @Override // android.media.MediaPlayer.OnInfoListener
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
                if (MutedVideoView.this.mOnInfoListener != null) {
                    MutedVideoView.this.mOnInfoListener.onInfo(mediaPlayer, i, i2);
                    return true;
                }
                return true;
            }
        };
        this.mErrorListener = new MediaPlayer.OnErrorListener() { // from class: com.android.settings.MutedVideoView.5
            @Override // android.media.MediaPlayer.OnErrorListener
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                Log.d(MutedVideoView.this.TAG, "Error: " + i + "," + i2);
                MutedVideoView.this.mCurrentState = -1;
                MutedVideoView.this.mTargetState = -1;
                if (MutedVideoView.this.mMediaController != null) {
                    MutedVideoView.this.mMediaController.hide();
                }
                if (MutedVideoView.this.mOnErrorListener != null) {
                    MutedVideoView.this.mOnErrorListener.onError(MutedVideoView.this.mMediaPlayer, i, i2);
                }
                return true;
            }
        };
        this.mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() { // from class: com.android.settings.MutedVideoView.6
            @Override // android.media.MediaPlayer.OnBufferingUpdateListener
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                MutedVideoView.this.mCurrentBufferPercentage = i;
            }
        };
        this.mSHCallback = new SurfaceHolder.Callback() { // from class: com.android.settings.MutedVideoView.7
            @Override // android.view.SurfaceHolder.Callback
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
                MutedVideoView.this.mSurfaceWidth = i2;
                MutedVideoView.this.mSurfaceHeight = i3;
                boolean z = MutedVideoView.this.mTargetState == 3;
                boolean z2 = MutedVideoView.this.mVideoWidth == i2 && MutedVideoView.this.mVideoHeight == i3;
                if (MutedVideoView.this.mMediaPlayer != null && z && z2) {
                    if (MutedVideoView.this.mSeekWhenPrepared != 0) {
                        MutedVideoView mutedVideoView = MutedVideoView.this;
                        mutedVideoView.seekTo(mutedVideoView.mSeekWhenPrepared);
                    }
                    MutedVideoView.this.start();
                }
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                MutedVideoView.this.mSurfaceHolder = surfaceHolder;
                MutedVideoView.this.openVideo();
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                MutedVideoView.this.mSurfaceHolder = null;
                if (MutedVideoView.this.mMediaController != null) {
                    MutedVideoView.this.mMediaController.hide();
                }
                MutedVideoView.this.release(true);
            }
        };
        initVideoView();
    }

    public MutedVideoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
        this.TAG = "MutedVideoView";
        this.mCurrentState = 0;
        this.mTargetState = 0;
        this.mSurfaceHolder = null;
        this.mMediaPlayer = null;
        this.mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() { // from class: com.android.settings.MutedVideoView.1
            @Override // android.media.MediaPlayer.OnVideoSizeChangedListener
            public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i2) {
                MutedVideoView.this.mVideoWidth = mediaPlayer.getVideoWidth();
                MutedVideoView.this.mVideoHeight = mediaPlayer.getVideoHeight();
                if (MutedVideoView.this.mVideoWidth == 0 || MutedVideoView.this.mVideoHeight == 0) {
                    return;
                }
                MutedVideoView.this.getHolder().setFixedSize(MutedVideoView.this.mVideoWidth, MutedVideoView.this.mVideoHeight);
                MutedVideoView.this.requestLayout();
            }
        };
        this.mPreparedListener = new MediaPlayer.OnPreparedListener() { // from class: com.android.settings.MutedVideoView.2
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mediaPlayer) {
                MutedVideoView.this.mCurrentState = 2;
                MutedVideoView mutedVideoView = MutedVideoView.this;
                mutedVideoView.mCanPause = mutedVideoView.mCanSeekBack = mutedVideoView.mCanSeekForward = true;
                if (MutedVideoView.this.mOnPreparedListener != null) {
                    MutedVideoView.this.mOnPreparedListener.onPrepared(MutedVideoView.this.mMediaPlayer);
                }
                if (MutedVideoView.this.mMediaController != null) {
                    MutedVideoView.this.mMediaController.setEnabled(true);
                }
                MutedVideoView.this.mVideoWidth = mediaPlayer.getVideoWidth();
                MutedVideoView.this.mVideoHeight = mediaPlayer.getVideoHeight();
                int i = MutedVideoView.this.mSeekWhenPrepared;
                if (i != 0) {
                    MutedVideoView.this.seekTo(i);
                }
                if (MutedVideoView.this.mVideoWidth == 0 || MutedVideoView.this.mVideoHeight == 0) {
                    if (MutedVideoView.this.mTargetState == 3) {
                        MutedVideoView.this.start();
                        return;
                    }
                    return;
                }
                MutedVideoView.this.getHolder().setFixedSize(MutedVideoView.this.mVideoWidth, MutedVideoView.this.mVideoHeight);
                if (MutedVideoView.this.mSurfaceWidth == MutedVideoView.this.mVideoWidth && MutedVideoView.this.mSurfaceHeight == MutedVideoView.this.mVideoHeight) {
                    if (MutedVideoView.this.mTargetState == 3) {
                        MutedVideoView.this.start();
                        if (MutedVideoView.this.mMediaController != null) {
                            MutedVideoView.this.mMediaController.show();
                        }
                    } else if (MutedVideoView.this.isPlaying()) {
                    } else {
                        if ((i != 0 || MutedVideoView.this.getCurrentPosition() > 0) && MutedVideoView.this.mMediaController != null) {
                            MutedVideoView.this.mMediaController.show(0);
                        }
                    }
                }
            }
        };
        this.mCompletionListener = new MediaPlayer.OnCompletionListener() { // from class: com.android.settings.MutedVideoView.3
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mediaPlayer) {
                MutedVideoView.this.mCurrentState = 5;
                MutedVideoView.this.mTargetState = 5;
                if (MutedVideoView.this.mMediaController != null) {
                    MutedVideoView.this.mMediaController.hide();
                }
                if (MutedVideoView.this.mOnCompletionListener != null) {
                    MutedVideoView.this.mOnCompletionListener.onCompletion(MutedVideoView.this.mMediaPlayer);
                }
            }
        };
        this.mInfoListener = new MediaPlayer.OnInfoListener() { // from class: com.android.settings.MutedVideoView.4
            @Override // android.media.MediaPlayer.OnInfoListener
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
                if (MutedVideoView.this.mOnInfoListener != null) {
                    MutedVideoView.this.mOnInfoListener.onInfo(mediaPlayer, i, i2);
                    return true;
                }
                return true;
            }
        };
        this.mErrorListener = new MediaPlayer.OnErrorListener() { // from class: com.android.settings.MutedVideoView.5
            @Override // android.media.MediaPlayer.OnErrorListener
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                Log.d(MutedVideoView.this.TAG, "Error: " + i + "," + i2);
                MutedVideoView.this.mCurrentState = -1;
                MutedVideoView.this.mTargetState = -1;
                if (MutedVideoView.this.mMediaController != null) {
                    MutedVideoView.this.mMediaController.hide();
                }
                if (MutedVideoView.this.mOnErrorListener != null) {
                    MutedVideoView.this.mOnErrorListener.onError(MutedVideoView.this.mMediaPlayer, i, i2);
                }
                return true;
            }
        };
        this.mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() { // from class: com.android.settings.MutedVideoView.6
            @Override // android.media.MediaPlayer.OnBufferingUpdateListener
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                MutedVideoView.this.mCurrentBufferPercentage = i;
            }
        };
        this.mSHCallback = new SurfaceHolder.Callback() { // from class: com.android.settings.MutedVideoView.7
            @Override // android.view.SurfaceHolder.Callback
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
                MutedVideoView.this.mSurfaceWidth = i2;
                MutedVideoView.this.mSurfaceHeight = i3;
                boolean z = MutedVideoView.this.mTargetState == 3;
                boolean z2 = MutedVideoView.this.mVideoWidth == i2 && MutedVideoView.this.mVideoHeight == i3;
                if (MutedVideoView.this.mMediaPlayer != null && z && z2) {
                    if (MutedVideoView.this.mSeekWhenPrepared != 0) {
                        MutedVideoView mutedVideoView = MutedVideoView.this;
                        mutedVideoView.seekTo(mutedVideoView.mSeekWhenPrepared);
                    }
                    MutedVideoView.this.start();
                }
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                MutedVideoView.this.mSurfaceHolder = surfaceHolder;
                MutedVideoView.this.openVideo();
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                MutedVideoView.this.mSurfaceHolder = null;
                if (MutedVideoView.this.mMediaController != null) {
                    MutedVideoView.this.mMediaController.hide();
                }
                MutedVideoView.this.release(true);
            }
        };
        initVideoView();
    }

    public MutedVideoView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.TAG = "MutedVideoView";
        this.mCurrentState = 0;
        this.mTargetState = 0;
        this.mSurfaceHolder = null;
        this.mMediaPlayer = null;
        this.mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() { // from class: com.android.settings.MutedVideoView.1
            @Override // android.media.MediaPlayer.OnVideoSizeChangedListener
            public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i2, int i22) {
                MutedVideoView.this.mVideoWidth = mediaPlayer.getVideoWidth();
                MutedVideoView.this.mVideoHeight = mediaPlayer.getVideoHeight();
                if (MutedVideoView.this.mVideoWidth == 0 || MutedVideoView.this.mVideoHeight == 0) {
                    return;
                }
                MutedVideoView.this.getHolder().setFixedSize(MutedVideoView.this.mVideoWidth, MutedVideoView.this.mVideoHeight);
                MutedVideoView.this.requestLayout();
            }
        };
        this.mPreparedListener = new MediaPlayer.OnPreparedListener() { // from class: com.android.settings.MutedVideoView.2
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mediaPlayer) {
                MutedVideoView.this.mCurrentState = 2;
                MutedVideoView mutedVideoView = MutedVideoView.this;
                mutedVideoView.mCanPause = mutedVideoView.mCanSeekBack = mutedVideoView.mCanSeekForward = true;
                if (MutedVideoView.this.mOnPreparedListener != null) {
                    MutedVideoView.this.mOnPreparedListener.onPrepared(MutedVideoView.this.mMediaPlayer);
                }
                if (MutedVideoView.this.mMediaController != null) {
                    MutedVideoView.this.mMediaController.setEnabled(true);
                }
                MutedVideoView.this.mVideoWidth = mediaPlayer.getVideoWidth();
                MutedVideoView.this.mVideoHeight = mediaPlayer.getVideoHeight();
                int i2 = MutedVideoView.this.mSeekWhenPrepared;
                if (i2 != 0) {
                    MutedVideoView.this.seekTo(i2);
                }
                if (MutedVideoView.this.mVideoWidth == 0 || MutedVideoView.this.mVideoHeight == 0) {
                    if (MutedVideoView.this.mTargetState == 3) {
                        MutedVideoView.this.start();
                        return;
                    }
                    return;
                }
                MutedVideoView.this.getHolder().setFixedSize(MutedVideoView.this.mVideoWidth, MutedVideoView.this.mVideoHeight);
                if (MutedVideoView.this.mSurfaceWidth == MutedVideoView.this.mVideoWidth && MutedVideoView.this.mSurfaceHeight == MutedVideoView.this.mVideoHeight) {
                    if (MutedVideoView.this.mTargetState == 3) {
                        MutedVideoView.this.start();
                        if (MutedVideoView.this.mMediaController != null) {
                            MutedVideoView.this.mMediaController.show();
                        }
                    } else if (MutedVideoView.this.isPlaying()) {
                    } else {
                        if ((i2 != 0 || MutedVideoView.this.getCurrentPosition() > 0) && MutedVideoView.this.mMediaController != null) {
                            MutedVideoView.this.mMediaController.show(0);
                        }
                    }
                }
            }
        };
        this.mCompletionListener = new MediaPlayer.OnCompletionListener() { // from class: com.android.settings.MutedVideoView.3
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mediaPlayer) {
                MutedVideoView.this.mCurrentState = 5;
                MutedVideoView.this.mTargetState = 5;
                if (MutedVideoView.this.mMediaController != null) {
                    MutedVideoView.this.mMediaController.hide();
                }
                if (MutedVideoView.this.mOnCompletionListener != null) {
                    MutedVideoView.this.mOnCompletionListener.onCompletion(MutedVideoView.this.mMediaPlayer);
                }
            }
        };
        this.mInfoListener = new MediaPlayer.OnInfoListener() { // from class: com.android.settings.MutedVideoView.4
            @Override // android.media.MediaPlayer.OnInfoListener
            public boolean onInfo(MediaPlayer mediaPlayer, int i2, int i22) {
                if (MutedVideoView.this.mOnInfoListener != null) {
                    MutedVideoView.this.mOnInfoListener.onInfo(mediaPlayer, i2, i22);
                    return true;
                }
                return true;
            }
        };
        this.mErrorListener = new MediaPlayer.OnErrorListener() { // from class: com.android.settings.MutedVideoView.5
            @Override // android.media.MediaPlayer.OnErrorListener
            public boolean onError(MediaPlayer mediaPlayer, int i2, int i22) {
                Log.d(MutedVideoView.this.TAG, "Error: " + i2 + "," + i22);
                MutedVideoView.this.mCurrentState = -1;
                MutedVideoView.this.mTargetState = -1;
                if (MutedVideoView.this.mMediaController != null) {
                    MutedVideoView.this.mMediaController.hide();
                }
                if (MutedVideoView.this.mOnErrorListener != null) {
                    MutedVideoView.this.mOnErrorListener.onError(MutedVideoView.this.mMediaPlayer, i2, i22);
                }
                return true;
            }
        };
        this.mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() { // from class: com.android.settings.MutedVideoView.6
            @Override // android.media.MediaPlayer.OnBufferingUpdateListener
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i2) {
                MutedVideoView.this.mCurrentBufferPercentage = i2;
            }
        };
        this.mSHCallback = new SurfaceHolder.Callback() { // from class: com.android.settings.MutedVideoView.7
            @Override // android.view.SurfaceHolder.Callback
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i2, int i22, int i3) {
                MutedVideoView.this.mSurfaceWidth = i22;
                MutedVideoView.this.mSurfaceHeight = i3;
                boolean z = MutedVideoView.this.mTargetState == 3;
                boolean z2 = MutedVideoView.this.mVideoWidth == i22 && MutedVideoView.this.mVideoHeight == i3;
                if (MutedVideoView.this.mMediaPlayer != null && z && z2) {
                    if (MutedVideoView.this.mSeekWhenPrepared != 0) {
                        MutedVideoView mutedVideoView = MutedVideoView.this;
                        mutedVideoView.seekTo(mutedVideoView.mSeekWhenPrepared);
                    }
                    MutedVideoView.this.start();
                }
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                MutedVideoView.this.mSurfaceHolder = surfaceHolder;
                MutedVideoView.this.openVideo();
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                MutedVideoView.this.mSurfaceHolder = null;
                if (MutedVideoView.this.mMediaController != null) {
                    MutedVideoView.this.mMediaController.hide();
                }
                MutedVideoView.this.release(true);
            }
        };
        initVideoView();
    }

    private void attachMediaController() {
        MediaController mediaController;
        if (this.mMediaPlayer == null || (mediaController = this.mMediaController) == null) {
            return;
        }
        mediaController.setMediaPlayer(this);
        this.mMediaController.setAnchorView(getParent() instanceof View ? (View) getParent() : this);
        this.mMediaController.setEnabled(isInPlaybackState());
    }

    private void initVideoView() {
        this.mVideoWidth = 0;
        this.mVideoHeight = 0;
        getHolder().addCallback(this.mSHCallback);
        getHolder().setType(3);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        this.mPendingSubtitleTracks = new Vector<>();
        this.mCurrentState = 0;
        this.mTargetState = 0;
    }

    private boolean isInPlaybackState() {
        int i;
        return (this.mMediaPlayer == null || (i = this.mCurrentState) == -1 || i == 0 || i == 1) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openVideo() {
        if (this.mUri == null || this.mSurfaceHolder == null) {
            return;
        }
        release(false);
        try {
            try {
                this.mMediaPlayer = new MediaPlayer();
                getContext();
                int i = this.mAudioSession;
                if (i != 0) {
                    this.mMediaPlayer.setAudioSessionId(i);
                } else {
                    this.mAudioSession = this.mMediaPlayer.getAudioSessionId();
                }
                this.mMediaPlayer.setOnPreparedListener(this.mPreparedListener);
                this.mMediaPlayer.setOnVideoSizeChangedListener(this.mSizeChangedListener);
                this.mMediaPlayer.setOnCompletionListener(this.mCompletionListener);
                this.mMediaPlayer.setOnErrorListener(this.mErrorListener);
                this.mMediaPlayer.setOnInfoListener(this.mInfoListener);
                this.mMediaPlayer.setOnBufferingUpdateListener(this.mBufferingUpdateListener);
                this.mCurrentBufferPercentage = 0;
                this.mMediaPlayer.setDataSource(getContext(), this.mUri, this.mHeaders);
                this.mMediaPlayer.setDisplay(this.mSurfaceHolder);
                this.mMediaPlayer.setScreenOnWhilePlaying(true);
                this.mMediaPlayer.prepareAsync();
                this.mCurrentState = 1;
                attachMediaController();
            } catch (IOException e) {
                Log.w(this.TAG, "Unable to open content: " + this.mUri, e);
                this.mCurrentState = -1;
                this.mTargetState = -1;
                this.mErrorListener.onError(this.mMediaPlayer, 1, 0);
            } catch (IllegalArgumentException e2) {
                Log.w(this.TAG, "Unable to open content: " + this.mUri, e2);
                this.mCurrentState = -1;
                this.mTargetState = -1;
                this.mErrorListener.onError(this.mMediaPlayer, 1, 0);
            }
        } finally {
            this.mPendingSubtitleTracks.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void release(boolean z) {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                this.mMediaPlayer.stop();
            }
            this.mMediaPlayer.reset();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            this.mPendingSubtitleTracks.clear();
            this.mCurrentState = 0;
            if (z) {
                this.mTargetState = 0;
            }
        }
    }

    private void toggleMediaControlsVisiblity() {
        if (this.mMediaController.isShowing()) {
            this.mMediaController.hide();
        } else {
            this.mMediaController.show();
        }
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public boolean canPause() {
        return this.mCanPause;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public boolean canSeekBackward() {
        return this.mCanSeekBack;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public boolean canSeekForward() {
        return this.mCanSeekForward;
    }

    @Override // android.view.SurfaceView, android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public int getAudioSessionId() {
        if (this.mAudioSession == 0) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            this.mAudioSession = mediaPlayer.getAudioSessionId();
            mediaPlayer.release();
        }
        return this.mAudioSession;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public int getBufferPercentage() {
        if (this.mMediaPlayer != null) {
            return this.mCurrentBufferPercentage;
        }
        return 0;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return this.mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public int getDuration() {
        if (isInPlaybackState()) {
            return this.mMediaPlayer.getDuration();
        }
        return -1;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public boolean isPlaying() {
        return isInPlaybackState() && this.mMediaPlayer.isPlaying();
    }

    @Override // android.view.SurfaceView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.view.SurfaceView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(MutedVideoView.class.getName());
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(MutedVideoView.class.getName());
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        boolean z = (i == 4 || i == 24 || i == 25 || i == 164 || i == 82 || i == 5 || i == 6) ? false : true;
        if (isInPlaybackState() && z && this.mMediaController != null) {
            if (i == 79 || i == 85) {
                if (this.mMediaPlayer.isPlaying()) {
                    pause();
                    this.mMediaController.show();
                } else {
                    start();
                    this.mMediaController.hide();
                }
                return true;
            } else if (i == 126) {
                if (!this.mMediaPlayer.isPlaying()) {
                    start();
                    this.mMediaController.hide();
                }
                return true;
            } else if (i == 86 || i == 127) {
                if (this.mMediaPlayer.isPlaying()) {
                    pause();
                    this.mMediaController.show();
                }
                return true;
            } else {
                toggleMediaControlsVisiblity();
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x005d, code lost:
    
        if (r1 > r6) goto L27;
     */
    @Override // android.view.SurfaceView, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onMeasure(int r6, int r7) {
        /*
            r5 = this;
            int r0 = r5.mVideoWidth
            int r0 = android.view.SurfaceView.getDefaultSize(r0, r6)
            int r1 = r5.mVideoHeight
            int r1 = android.view.SurfaceView.getDefaultSize(r1, r7)
            int r2 = r5.mVideoWidth
            if (r2 <= 0) goto L7a
            int r2 = r5.mVideoHeight
            if (r2 <= 0) goto L7a
            int r0 = android.view.View.MeasureSpec.getMode(r6)
            int r6 = android.view.View.MeasureSpec.getSize(r6)
            int r1 = android.view.View.MeasureSpec.getMode(r7)
            int r7 = android.view.View.MeasureSpec.getSize(r7)
            r2 = 1073741824(0x40000000, float:2.0)
            if (r0 != r2) goto L41
            if (r1 != r2) goto L41
            int r0 = r5.mVideoWidth
            int r1 = r0 * r7
            int r2 = r5.mVideoHeight
            int r3 = r6 * r2
            if (r1 >= r3) goto L37
            int r0 = r0 * r7
            int r0 = r0 / r2
            goto L62
        L37:
            int r1 = r0 * r7
            int r3 = r6 * r2
            if (r1 <= r3) goto L5f
            int r2 = r2 * r6
            int r1 = r2 / r0
            goto L51
        L41:
            r3 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r0 != r2) goto L53
            int r0 = r5.mVideoHeight
            int r0 = r0 * r6
            int r2 = r5.mVideoWidth
            int r0 = r0 / r2
            if (r1 != r3) goto L50
            if (r0 <= r7) goto L50
            goto L5f
        L50:
            r1 = r0
        L51:
            r0 = r6
            goto L7a
        L53:
            if (r1 != r2) goto L64
            int r1 = r5.mVideoWidth
            int r1 = r1 * r7
            int r2 = r5.mVideoHeight
            int r1 = r1 / r2
            if (r0 != r3) goto L61
            if (r1 <= r6) goto L61
        L5f:
            r0 = r6
            goto L62
        L61:
            r0 = r1
        L62:
            r1 = r7
            goto L7a
        L64:
            int r2 = r5.mVideoWidth
            int r4 = r5.mVideoHeight
            if (r1 != r3) goto L70
            if (r4 <= r7) goto L70
            int r1 = r7 * r2
            int r1 = r1 / r4
            goto L72
        L70:
            r1 = r2
            r7 = r4
        L72:
            if (r0 != r3) goto L61
            if (r1 <= r6) goto L61
            int r4 = r4 * r6
            int r1 = r4 / r2
            goto L51
        L7a:
            r5.setMeasuredDimension(r0, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.MutedVideoView.onMeasure(int, int):void");
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isInPlaybackState() || this.mMediaController == null) {
            return false;
        }
        toggleMediaControlsVisiblity();
        return false;
    }

    @Override // android.view.View
    public boolean onTrackballEvent(MotionEvent motionEvent) {
        if (!isInPlaybackState() || this.mMediaController == null) {
            return false;
        }
        toggleMediaControlsVisiblity();
        return false;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public void pause() {
        if (isInPlaybackState() && this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.pause();
            this.mCurrentState = 4;
        }
        this.mTargetState = 4;
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public void seekTo(int i) {
        if (!isInPlaybackState()) {
            this.mSeekWhenPrepared = i;
            return;
        }
        this.mMediaPlayer.seekTo(i);
        this.mSeekWhenPrepared = 0;
    }

    public void setMediaController(MediaController mediaController) {
        MediaController mediaController2 = this.mMediaController;
        if (mediaController2 != null) {
            mediaController2.hide();
        }
        this.mMediaController = mediaController;
        attachMediaController();
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
        this.mOnCompletionListener = onCompletionListener;
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener onErrorListener) {
        this.mOnErrorListener = onErrorListener;
    }

    public void setOnInfoListener(MediaPlayer.OnInfoListener onInfoListener) {
        this.mOnInfoListener = onInfoListener;
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
        this.mOnPreparedListener = onPreparedListener;
    }

    public void setVideoPath(String str) {
        setVideoURI(Uri.parse(str));
    }

    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }

    public void setVideoURI(Uri uri, Map<String, String> map) {
        this.mUri = uri;
        this.mHeaders = map;
        this.mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    @Override // android.widget.MediaController.MediaPlayerControl
    public void start() {
        if (isInPlaybackState()) {
            this.mMediaPlayer.start();
            this.mCurrentState = 3;
        }
        this.mTargetState = 3;
    }

    public void suspend() {
        release(false);
    }
}
