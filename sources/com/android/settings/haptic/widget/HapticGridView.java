package com.android.settings.haptic.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.GridLayout;
import com.android.settings.R;
import com.android.settings.haptic.HapticDetailActivity;
import com.android.settings.haptic.SubtitleProcessor;
import com.android.settings.haptic.data.HapticResource;
import com.android.settings.haptic.data.ResourceWrapper;
import com.android.settings.usagestats.utils.CommonUtils;
import com.android.settingslib.util.MiStatInterfaceUtils;
import java.io.IOException;
import java.util.List;
import miui.util.HapticFeedbackUtil;
import miuix.util.HapticFeedbackCompat;
import miuix.view.HapticFeedbackConstants;

/* loaded from: classes.dex */
public class HapticGridView extends GridLayout {
    private ValueAnimator mAlphaAnimation;
    private AnimatorSet mAnimatorSet;
    private View[] mChildView;
    private List<HapticResource> mData;
    private int mFirstRowMarginBottom;
    private int mFirstRowMarginTop;
    private HapticFeedbackCompat mHapticCompat;
    private HapticFeedbackUtil mHapticUtil;
    boolean[] mIsFinishRenderingStart;
    private int mItemWidth;
    private int mLastPlayingIndex;
    private int mLeftItemMarginEnd;
    private int mLeftItemMarginStart;
    private MediaPlayer[] mMediaPlayerList;
    private int mPlayingIndex;
    private int mRightItemMarginEnd;
    private int mRightItemMarginStart;
    private Surface[] mSurfaceList;

    public HapticGridView(Context context) {
        super(context);
        this.mMediaPlayerList = new MediaPlayer[4];
        this.mSurfaceList = new Surface[4];
        this.mPlayingIndex = -1;
        this.mLastPlayingIndex = -1;
        this.mIsFinishRenderingStart = new boolean[4];
    }

    public HapticGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMediaPlayerList = new MediaPlayer[4];
        this.mSurfaceList = new Surface[4];
        this.mPlayingIndex = -1;
        this.mLastPlayingIndex = -1;
        this.mIsFinishRenderingStart = new boolean[4];
        this.mHapticCompat = new HapticFeedbackCompat(getContext().getApplicationContext());
        this.mHapticUtil = new HapticFeedbackUtil(getContext(), true);
        int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.left_video_margin_start);
        int dimensionPixelSize2 = getResources().getDimensionPixelSize(R.dimen.left_video_margin_end);
        this.mLeftItemMarginStart = CommonUtils.isRtl() ? dimensionPixelSize2 : dimensionPixelSize;
        this.mLeftItemMarginEnd = CommonUtils.isRtl() ? dimensionPixelSize : dimensionPixelSize2;
        this.mRightItemMarginStart = CommonUtils.isRtl() ? dimensionPixelSize : dimensionPixelSize2;
        this.mRightItemMarginEnd = CommonUtils.isRtl() ? dimensionPixelSize2 : dimensionPixelSize;
        this.mFirstRowMarginTop = getContext().getResources().getDimensionPixelSize(R.dimen.first_row_video_margin_top);
        this.mFirstRowMarginBottom = getContext().getResources().getDimensionPixelSize(R.dimen.first_row_video_margin_bottom);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideImgHolder(int i, View view) {
        if (this.mIsFinishRenderingStart[i]) {
            this.mChildView[i].findViewById(R.id.vv_item).setVisibility(0);
            view.setVisibility(4);
            MiStatInterfaceUtils.trackPreferenceClick(HapticDetailActivity.class.getName(), getContext().getString(this.mData.get(i).getContentDescription()));
        }
    }

    private void initNormalView() {
        this.mChildView = new View[this.mData.size()];
        for (final int i = 0; i < this.mData.size(); i++) {
            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.haptic_demo_video_layout, (ViewGroup) null);
            this.mChildView[i] = inflate;
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(GridLayout.spec(i / 2, 1), GridLayout.spec(i % 2, 1));
            layoutParams.width = this.mItemWidth;
            if (i == 0) {
                layoutParams.setMargins(this.mLeftItemMarginStart, this.mFirstRowMarginTop, this.mLeftItemMarginEnd, this.mFirstRowMarginBottom);
            } else if (i == 1) {
                layoutParams.setMargins(this.mRightItemMarginStart, this.mFirstRowMarginTop, this.mRightItemMarginEnd, this.mFirstRowMarginBottom);
            } else if (i == 2) {
                layoutParams.setMargins(this.mLeftItemMarginStart, 0, this.mLeftItemMarginEnd, this.mFirstRowMarginTop);
            } else if (i == 3) {
                layoutParams.setMargins(this.mRightItemMarginStart, 0, this.mRightItemMarginEnd, this.mFirstRowMarginTop);
            }
            addView(inflate, layoutParams);
            TextureView textureView = (TextureView) inflate.findViewById(R.id.vv_item);
            final int showRes = this.mData.get(i).getShowRes();
            int videoBgRes = this.mData.get(i).getVideoBgRes();
            inflate.setContentDescription(getContext().getString(this.mData.get(i).getContentDescription()));
            final View findViewById = inflate.findViewById(R.id.img_holder);
            inflate.findViewById(R.id.img_item).setBackground(getResources().getDrawable(videoBgRes));
            textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() { // from class: com.android.settings.haptic.widget.HapticGridView.1
                @Override // android.view.TextureView.SurfaceTextureListener
                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i2, int i3) {
                    HapticGridView.this.mSurfaceList[i] = new Surface(surfaceTexture);
                    if (HapticGridView.this.mMediaPlayerList[i] == null) {
                        HapticGridView.this.mMediaPlayerList[i] = new MediaPlayer();
                    }
                    HapticGridView.this.mMediaPlayerList[i].setSurface(HapticGridView.this.mSurfaceList[i]);
                    try {
                        HapticGridView.this.mMediaPlayerList[i].setDataSource(HapticGridView.this.getContext(), Uri.parse("android.resource://" + HapticGridView.this.getContext().getPackageName() + "/" + showRes));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        HapticGridView.this.mMediaPlayerList[i].prepare();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }

                @Override // android.view.TextureView.SurfaceTextureListener
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    HapticGridView.this.mSurfaceList[i] = null;
                    if (HapticGridView.this.mMediaPlayerList[i] != null) {
                        HapticGridView.this.mMediaPlayerList[i].reset();
                        HapticGridView.this.mMediaPlayerList[i].release();
                        HapticGridView.this.mMediaPlayerList[i] = null;
                        return true;
                    }
                    return true;
                }

                @Override // android.view.TextureView.SurfaceTextureListener
                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i2, int i3) {
                }

                @Override // android.view.TextureView.SurfaceTextureListener
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                }
            });
            inflate.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.haptic.widget.HapticGridView.2
                private void playVideoAnimator(final View view) {
                    HapticGridView.this.mAnimatorSet = new AnimatorSet();
                    HapticGridView.this.mPlayingIndex = i;
                    for (int i2 = 0; i2 < HapticGridView.this.mData.size(); i2++) {
                        if (i2 != i) {
                            if (HapticGridView.this.mLastPlayingIndex == -1 || HapticGridView.this.mLastPlayingIndex == i2) {
                                HapticGridView hapticGridView = HapticGridView.this;
                                hapticGridView.startAlphaAnimation(hapticGridView.mChildView[i2], 1.0f, 0.4f, 300L);
                            } else {
                                HapticGridView.this.mChildView[i2].setAlpha(0.4f);
                            }
                        } else if (HapticGridView.this.mLastPlayingIndex != -1) {
                            HapticGridView hapticGridView2 = HapticGridView.this;
                            hapticGridView2.startAlphaAnimation(hapticGridView2.mChildView[i2], 0.4f, 1.0f, 300L);
                        } else {
                            HapticGridView.this.mChildView[i2].setAlpha(1.0f);
                        }
                    }
                    HapticGridView.this.mAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.settings.haptic.widget.HapticGridView.2.1
                        @Override // android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator, boolean z) {
                            AnonymousClass2 anonymousClass2 = AnonymousClass2.this;
                            HapticGridView.this.playVideo(view, i, findViewById, false, -1);
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationStart(Animator animator) {
                            super.onAnimationStart(animator);
                        }
                    });
                    ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.09f);
                    ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.09f);
                    HapticGridView.this.mAnimatorSet.setDuration(300L);
                    HapticGridView.this.mAnimatorSet.setInterpolator(new DecelerateInterpolator());
                    HapticGridView.this.mAnimatorSet.play(ofFloat).with(ofFloat2);
                    HapticGridView.this.mAnimatorSet.start();
                }

                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (HapticGridView.this.mAnimatorSet == null || !HapticGridView.this.mAnimatorSet.isRunning()) {
                        if (i == HapticGridView.this.mPlayingIndex && HapticGridView.this.mMediaPlayerList[i] != null && HapticGridView.this.mMediaPlayerList[i].isPlaying()) {
                            HapticGridView.this.playVideo(view, i, findViewById, true, -1);
                            return;
                        }
                        for (int i2 = 0; i2 < HapticGridView.this.mData.size(); i2++) {
                            if (HapticGridView.this.mMediaPlayerList[i2] != null && HapticGridView.this.mMediaPlayerList[i2].isPlaying()) {
                                HapticGridView.this.mLastPlayingIndex = i2;
                                HapticGridView hapticGridView = HapticGridView.this;
                                hapticGridView.playVideo(hapticGridView.mChildView[i2], i2, findViewById, true, i);
                            }
                        }
                        playVideoAnimator(view);
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$startAlphaAnimation$0(View view, ValueAnimator valueAnimator) {
        view.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playVideo(final View view, final int i, final View view2, boolean z, int i2) {
        if (z) {
            this.mMediaPlayerList[i].pause();
            this.mMediaPlayerList[i].seekTo(0);
            AnimatorSet animatorSet = new AnimatorSet();
            this.mAnimatorSet = animatorSet;
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.settings.haptic.widget.HapticGridView.3
                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator, boolean z2) {
                    HapticGridView.this.mLastPlayingIndex = -1;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    HapticGridView.this.mChildView[i].findViewById(R.id.img_holder).setVisibility(0);
                    HapticGridView.this.mChildView[i].findViewById(R.id.vv_item).setVisibility(4);
                    for (int i3 = 0; i3 < HapticGridView.this.mData.size(); i3++) {
                        if (i3 != i && HapticGridView.this.mLastPlayingIndex == -1) {
                            HapticGridView hapticGridView = HapticGridView.this;
                            hapticGridView.startAlphaAnimation(hapticGridView.mChildView[i3], 0.4f, 1.0f, 300L);
                        }
                    }
                    super.onAnimationStart(animator);
                }
            });
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "scaleX", 1.09f, 1.0f);
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view, "scaleY", 1.09f, 1.0f);
            this.mAnimatorSet.setDuration(300L);
            this.mAnimatorSet.setInterpolator(new DecelerateInterpolator());
            this.mAnimatorSet.play(ofFloat).with(ofFloat2);
            this.mAnimatorSet.start();
            this.mPlayingIndex = -1;
            return;
        }
        MediaPlayer[] mediaPlayerArr = this.mMediaPlayerList;
        if (mediaPlayerArr[i] == null) {
            mediaPlayerArr[i] = new MediaPlayer();
        }
        try {
            this.mMediaPlayerList[i].addTimedTextSource(SubtitleProcessor.getSubtitleFile(getContext(), this.mData.get(i).getSubTitleRes()), "application/x-subrip");
            int findTrackIndexFor = SubtitleProcessor.findTrackIndexFor(3, this.mMediaPlayerList[i].getTrackInfo());
            if (findTrackIndexFor >= 0) {
                this.mMediaPlayerList[i].selectTrack(findTrackIndexFor);
            } else {
                Log.w("HapticGridView", "Cannot find text track!");
            }
            this.mMediaPlayerList[i].setOnTimedTextListener(new MediaPlayer.OnTimedTextListener() { // from class: com.android.settings.haptic.widget.HapticGridView.4
                @Override // android.media.MediaPlayer.OnTimedTextListener
                public void onTimedText(MediaPlayer mediaPlayer, TimedText timedText) {
                    if (timedText != null) {
                        if (TextUtils.equals("MIUI_PICK_UP", timedText.getText().replace("\n", ""))) {
                            HapticGridView.this.playPatternById(HapticFeedbackConstants.MIUI_PICK_UP);
                        } else if (TextUtils.equals("MIUI_MESH_NORMAL", timedText.getText().replace("\n", ""))) {
                            HapticGridView.this.playPatternById(HapticFeedbackConstants.MIUI_MESH_NORMAL);
                        } else if (TextUtils.equals("MIUI_MESH_LIGHT", timedText.getText().replace("\n", ""))) {
                            HapticGridView.this.playPatternById(HapticFeedbackConstants.MIUI_MESH_LIGHT);
                        } else if (TextUtils.equals("MIUI_LONG_PRESS", timedText.getText().replace("\n", ""))) {
                            HapticGridView.this.playPatternById(HapticFeedbackConstants.MIUI_LONG_PRESS);
                        } else if (TextUtils.equals("MIUI_HOLD", timedText.getText().replace("\n", ""))) {
                            HapticGridView.this.playPatternById(HapticFeedbackConstants.MIUI_HOLD);
                        } else if (TextUtils.equals("MIUI_POPUP_LIGHT", timedText.getText().replace("\n", ""))) {
                            HapticGridView.this.playPatternById(HapticFeedbackConstants.MIUI_POPUP_LIGHT);
                        } else if (TextUtils.equals("MIUI_POPUP_NORMAL", timedText.getText().replace("\n", ""))) {
                            HapticGridView.this.playPatternById(HapticFeedbackConstants.MIUI_POPUP_NORMAL);
                        } else if (TextUtils.equals("MIUI_MESH_HEAVY", timedText.getText().replace("\n", ""))) {
                            HapticGridView.this.playPatternById(HapticFeedbackConstants.MIUI_MESH_HEAVY);
                        } else {
                            int parseInt = Integer.parseInt(timedText.getText().replace("\n", ""));
                            if (parseInt == -1) {
                                Log.d("HapticGridView", "not haptic time:" + mediaPlayer.getCurrentPosition() + Integer.parseInt(timedText.getText().replace("\n", "")));
                                return;
                            }
                            HapticGridView.this.playExtPatternById(parseInt);
                            Log.d("HapticGridView", "time:" + mediaPlayer.getCurrentPosition() + Integer.parseInt(timedText.getText().replace("\n", "")));
                        }
                    }
                }
            });
            this.mMediaPlayerList[i].setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.android.settings.haptic.widget.HapticGridView.5
                @Override // android.media.MediaPlayer.OnCompletionListener
                public void onCompletion(MediaPlayer mediaPlayer) {
                    HapticGridView.this.mAnimatorSet = new AnimatorSet();
                    HapticGridView.this.mAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.settings.haptic.widget.HapticGridView.5.1
                        @Override // android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator, boolean z2) {
                            HapticGridView.this.mChildView[i].findViewById(R.id.vv_item).setVisibility(4);
                            view2.setVisibility(0);
                            for (int i3 = 0; i3 < HapticGridView.this.mData.size(); i3++) {
                                AnonymousClass5 anonymousClass5 = AnonymousClass5.this;
                                if (i3 != i) {
                                    HapticGridView hapticGridView = HapticGridView.this;
                                    hapticGridView.startAlphaAnimation(hapticGridView.mChildView[i3], 0.4f, 1.0f, 300L);
                                }
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationStart(Animator animator) {
                            super.onAnimationStart(animator);
                        }
                    });
                    ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(view, "scaleX", 1.09f, 1.0f);
                    ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(view, "scaleY", 1.09f, 1.0f);
                    HapticGridView.this.mAnimatorSet.setDuration(300L);
                    HapticGridView.this.mAnimatorSet.setInterpolator(new DecelerateInterpolator());
                    HapticGridView.this.mAnimatorSet.play(ofFloat3).with(ofFloat4);
                    HapticGridView.this.mAnimatorSet.start();
                }
            });
            this.mMediaPlayerList[i].start();
            this.mMediaPlayerList[i].setOnInfoListener(new MediaPlayer.OnInfoListener() { // from class: com.android.settings.haptic.widget.HapticGridView.6
                @Override // android.media.MediaPlayer.OnInfoListener
                public boolean onInfo(MediaPlayer mediaPlayer, int i3, int i4) {
                    if (i3 == 3) {
                        HapticGridView hapticGridView = HapticGridView.this;
                        boolean[] zArr = hapticGridView.mIsFinishRenderingStart;
                        int i5 = i;
                        if (!zArr[i5]) {
                            zArr[i5] = true;
                            hapticGridView.hideImgHolder(i5, view2);
                        }
                        return true;
                    }
                    return false;
                }
            });
            hideImgHolder(i, view2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startAlphaAnimation(final View view, float f, float f2, long j) {
        ValueAnimator duration = ValueAnimator.ofFloat(f, f2).setDuration(j);
        this.mAlphaAnimation = duration;
        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.haptic.widget.HapticGridView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                HapticGridView.lambda$startAlphaAnimation$0(view, valueAnimator);
            }
        });
        this.mAlphaAnimation.setRepeatCount(0);
        this.mAlphaAnimation.start();
    }

    private void stopPlayingVideo() {
        for (int i = 0; i < this.mData.size(); i++) {
            MediaPlayer[] mediaPlayerArr = this.mMediaPlayerList;
            if (mediaPlayerArr[i] != null && mediaPlayerArr[i].isPlaying()) {
                playVideo(this.mChildView[i], i, this.mChildView[i].findViewById(R.id.img_holder), true, -1);
            }
        }
        cancelAnimation();
    }

    void cancelAnimation() {
        AnimatorSet animatorSet = this.mAnimatorSet;
        if (animatorSet != null) {
            animatorSet.end();
            this.mAnimatorSet.removeAllListeners();
            this.mAnimatorSet = null;
        }
        ValueAnimator valueAnimator = this.mAlphaAnimation;
        if (valueAnimator != null) {
            valueAnimator.end();
            this.mAlphaAnimation.removeAllListeners();
            this.mAlphaAnimation = null;
        }
    }

    public void onDestroy() {
    }

    public void onPageChange() {
        stopPlayingVideo();
    }

    public void onStop() {
        stopPlayingVideo();
    }

    public void playExtPatternById(int i) {
        if (!HapticFeedbackUtil.isSupportLinearMotorVibrate()) {
            Log.e("HapticGridView", "Not support linearMotor! id:" + i);
        } else if (!this.mHapticUtil.isSupportExtHapticFeedback(i)) {
            Log.e("HapticGridView", "Not support this rtp:$rtpEffectId! id:" + i);
        } else {
            Log.i("HapticGridView", "performExtHapticFeedback id:" + i);
            this.mHapticUtil.performExtHapticFeedback(i, true);
        }
    }

    public void playPatternById(int i) {
        if (!this.mHapticCompat.supportLinearMotor()) {
            Log.e("HapticGridView", "Not support linearMotor! id:" + i);
            return;
        }
        Log.i("HapticGridView", "performHapticFeedback id:" + i);
        this.mHapticCompat.performHapticFeedback(i, true);
    }

    public void setType(int i) {
        List<HapticResource> loadResource = ResourceWrapper.loadResource(getContext(), i);
        this.mData = loadResource;
        setRowCount((loadResource.size() / 2) + 1);
        setColumnCount(2);
        this.mItemWidth = ((getContext().getResources().getDisplayMetrics().widthPixels - (this.mLeftItemMarginStart * 2)) - (this.mLeftItemMarginEnd * 2)) / 2;
        initNormalView();
    }
}
