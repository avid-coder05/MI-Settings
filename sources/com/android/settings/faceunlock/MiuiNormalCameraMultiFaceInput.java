package com.android.settings.faceunlock;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.face.FaceManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.BaseEditFragment;
import com.android.settings.MiuiKeyguardSettingsUtils;
import com.android.settings.R;
import com.android.settings.SettingsCompatActivity;
import com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput;
import java.util.List;
import miui.os.Build;
import miuix.animation.Folme;
import miuix.animation.IVisibleStyle;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class MiuiNormalCameraMultiFaceInput extends SettingsCompatActivity {

    /* loaded from: classes.dex */
    public static class NewMultiFaceEnrollFragment extends BaseEditFragment {
        private LinearLayout mBackImage;
        private CameraPreviewCoverdView mCameraPreviewCoverdView;
        private float mCameraPreviewHeight;
        private ValueAnimator mCameraPreviewScaleAnimation;
        private float mCameraPreviewWidth;
        private float mCameraPrviewCircleRadius;
        private boolean mCanSetNextStep;
        protected View mContentView;
        private CountDownTimer mCountdownTimer;
        private float mCurrentDetectViewPosition;
        private int mCurrentFaceInputProgress;
        private float mEnrollDetectEnd;
        private float mEnrollDetectRepeatEnd;
        private String mEnrollFaceName;
        private boolean mEnrollHasStop;
        private boolean mEnrollProgressAnimationComplete;
        private float mEnrollSuggestionDetectEnd;
        private float mEnrollSuggestionDetectRepeatEnd;
        private ValueAnimator mFaceCameraPreviewCoveredAnimation;
        private ValueAnimator mFaceDetectAlphaAnimator;
        private ValueAnimator mFaceDetectAnimator;
        private FaceDetectView mFaceDetectView;
        private boolean mFaceEnrollFromNormal;
        private int mFaceEnrollStep;
        private boolean mFaceEnrollSucceed;
        private FaceEnrollSuccessView mFaceEnrollSuccessView;
        private byte[] mFaceEnrollToken;
        private TextureView mFaceInputCameraPreview;
        private SurfaceTexture mFaceInputCameraPreviewSurfaceTexture;
        private EditText mFaceInputEditNameEdit;
        private TextView mFaceInputEditNameTitle;
        private TextView mFaceInputFirstSuggestion;
        private ValueAnimator mFaceInputGridAnimation;
        private ValueAnimator mFaceInputGridPointAnimation;
        private FaceInputGridView mFaceInputGridView;
        private Button mFaceInputNextOrSuccessButton;
        private ValueAnimator mFaceInputProgressAlphaAnimation;
        private ValueAnimator mFaceInputProgressAnimation;
        private FaceInputProgressView mFaceInputProgressView;
        private TextView mFaceInputSuccessMsg;
        private TextView mFaceInputSuccessTitle;
        private TextureView mFaceInputSuccessVideo;
        private TextureView mFaceInputSuggestionVideo;
        private ImageView mFaceInputSuggestionVideoImage;
        private TextView mFaceInputTitle;
        private ValueAnimator mFaceSuccessCameraPreviewAnimation;
        private KeyguardSettingsFaceUnlockManager mFaceUnlockManager;
        private boolean mFinishDetectFace;
        private boolean mFinishFaceSuggestion;
        private boolean mHasClickNextBtn;
        private boolean mHasClickStartAddBtn;
        private boolean mHasSkipFrame;
        private boolean mHaseCompleteEnterDetectAnimation;
        private Handler mMainHandler;
        private MediaPlayer mMediaPlayer;
        private boolean mNeedStartFaceInputAnimation;
        private int mSkipFrame;
        private long mLastAnnounceTime = 0;
        private float mEveryEnrollStepProgress = 90.0f;
        private int mCurrentEnrollAnimationStep = 1;
        private int mEnrollAnimationStep = 1;
        protected Activity mActivity = null;
        private AlertDialog mRiskWarningDialog = null;
        private Runnable mEnterFaceInput = new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.6
            @Override // java.lang.Runnable
            public void run() {
                NewMultiFaceEnrollFragment newMultiFaceEnrollFragment = NewMultiFaceEnrollFragment.this;
                newMultiFaceEnrollFragment.setContentDescription(newMultiFaceEnrollFragment.getResources().getString(R.string.face_data_input_title));
            }
        };
        private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.7
            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                if (NewMultiFaceEnrollFragment.this.mFinishFaceSuggestion) {
                    return;
                }
                NewMultiFaceEnrollFragment.this.enterFaceSuggestionStepAnimation();
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
        FaceManager.EnrollmentCallback mEnrollCallback = new FaceManager.EnrollmentCallback() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.10
            public void onEnrollmentError(final int i, CharSequence charSequence) {
                Slog.i("miui_face", "enrollCallback, onEnrollmentError errMsgId:" + i + " errString:" + ((Object) charSequence));
                Activity activity = NewMultiFaceEnrollFragment.this.mActivity;
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.10.1
                        @Override // java.lang.Runnable
                        public void run() {
                            NewMultiFaceEnrollFragment.this.updateFaceErrorInfo(i);
                        }
                    });
                }
            }

            public void onEnrollmentHelp(final int i, CharSequence charSequence) {
                Slog.i("miui_face", "enrollCallback, onEnrollmentHelp helpMsgId:" + i + " helpString:" + ((Object) charSequence));
                Activity activity = NewMultiFaceEnrollFragment.this.mActivity;
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.10.2
                        @Override // java.lang.Runnable
                        public void run() {
                            NewMultiFaceEnrollFragment.this.updateFaceHelpInfo(i);
                        }
                    });
                }
            }

            public void onEnrollmentProgress(int i) {
                Slog.i("miui_face", "enrollCallback, onEnrollmentProgress :" + i);
                NewMultiFaceEnrollFragment newMultiFaceEnrollFragment = NewMultiFaceEnrollFragment.this;
                if (newMultiFaceEnrollFragment.mActivity != null) {
                    newMultiFaceEnrollFragment.mFaceEnrollStep = 5;
                    NewMultiFaceEnrollFragment.this.mFaceEnrollSucceed = true;
                    NewMultiFaceEnrollFragment.this.mActivity.runOnUiThread(new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.10.3
                        @Override // java.lang.Runnable
                        public void run() {
                            List<String> enrolledFaceList = KeyguardSettingsFaceUnlockUtils.getEnrolledFaceList(NewMultiFaceEnrollFragment.this.mActivity);
                            NewMultiFaceEnrollFragment newMultiFaceEnrollFragment2 = NewMultiFaceEnrollFragment.this;
                            newMultiFaceEnrollFragment2.mEnrollFaceName = KeyguardSettingsFaceUnlockUtils.generateFaceDataName(newMultiFaceEnrollFragment2.mActivity, enrolledFaceList);
                            NewMultiFaceEnrollFragment.this.mFaceInputEditNameEdit.setText(NewMultiFaceEnrollFragment.this.mEnrollFaceName);
                            Slog.i("miui_face", "facelistIds :" + enrolledFaceList + "  mEnrollFaceName=" + NewMultiFaceEnrollFragment.this.mEnrollFaceName);
                            KeyguardSettingsFaceUnlockUtils.setFaceUnlockSettingValues(NewMultiFaceEnrollFragment.this.mActivity, enrolledFaceList.size());
                            NewMultiFaceEnrollFragment.this.mFaceInputTitle.setText(R.string.structure_face_data_input_error_keep_inface);
                            if (NewMultiFaceEnrollFragment.this.mEnrollHasStop && !NewMultiFaceEnrollFragment.this.mEnrollProgressAnimationComplete) {
                                NewMultiFaceEnrollFragment newMultiFaceEnrollFragment3 = NewMultiFaceEnrollFragment.this;
                                newMultiFaceEnrollFragment3.startFaceInputProgressAnimation(newMultiFaceEnrollFragment3.mCurrentFaceInputProgress);
                            }
                            if (NewMultiFaceEnrollFragment.this.mEnrollProgressAnimationComplete) {
                                NewMultiFaceEnrollFragment.this.startFaceInputSuccessAnimation();
                            }
                        }
                    });
                }
            }
        };
        private final Runnable mResetFaceEnroll = new AnonymousClass31();
        private final Runnable mStartFaceInput = new AnonymousClass32();
        private final Runnable mStartFaceSuggestionVideo = new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.33
            @Override // java.lang.Runnable
            public void run() {
                NewMultiFaceEnrollFragment.this.mFaceInputSuggestionVideo.setAlpha(1.0f);
                NewMultiFaceEnrollFragment.this.playVideo(new Surface(NewMultiFaceEnrollFragment.this.mFaceInputSuggestionVideo.getSurfaceTexture()), true, R.raw.miui_face_input_suggestion_video);
            }
        };

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput$NewMultiFaceEnrollFragment$21  reason: invalid class name */
        /* loaded from: classes.dex */
        public class AnonymousClass21 implements ValueAnimator.AnimatorUpdateListener {
            AnonymousClass21() {
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onAnimationUpdate$0() {
                NewMultiFaceEnrollFragment.this.mFaceUnlockManager.sendEnrollCommand(0);
            }

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                NewMultiFaceEnrollFragment.this.mCurrentFaceInputProgress = intValue;
                NewMultiFaceEnrollFragment.this.mFaceInputProgressView.updateFaceInputProgress(intValue);
                NewMultiFaceEnrollFragment.this.mEnrollAnimationStep = (int) Math.ceil(intValue / r0.mEveryEnrollStepProgress);
                if (NewMultiFaceEnrollFragment.this.mEnrollAnimationStep > NewMultiFaceEnrollFragment.this.mCurrentEnrollAnimationStep) {
                    if (NewMultiFaceEnrollFragment.this.mEnrollAnimationStep > NewMultiFaceEnrollFragment.this.mFaceEnrollStep) {
                        NewMultiFaceEnrollFragment.this.stopFaceInputProgressAnimation();
                    } else {
                        NewMultiFaceEnrollFragment newMultiFaceEnrollFragment = NewMultiFaceEnrollFragment.this;
                        newMultiFaceEnrollFragment.mCurrentEnrollAnimationStep = newMultiFaceEnrollFragment.mEnrollAnimationStep;
                        NewMultiFaceEnrollFragment.this.mFaceUnlockManager.runOnFaceUnlockWorkerThread(new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput$NewMultiFaceEnrollFragment$21$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.AnonymousClass21.this.lambda$onAnimationUpdate$0();
                            }
                        });
                    }
                }
                if (intValue == 360 && NewMultiFaceEnrollFragment.this.mFaceEnrollSucceed) {
                    NewMultiFaceEnrollFragment.this.startFaceInputSuccessAnimation();
                }
                if (intValue < 320 || NewMultiFaceEnrollFragment.this.mFaceEnrollSucceed) {
                    return;
                }
                NewMultiFaceEnrollFragment.this.stopFaceInputProgressAnimation();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput$NewMultiFaceEnrollFragment$22  reason: invalid class name */
        /* loaded from: classes.dex */
        public class AnonymousClass22 implements Animator.AnimatorListener {
            AnonymousClass22() {
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onAnimationEnd$0() {
                NewMultiFaceEnrollFragment.this.mFaceUnlockManager.sendEnrollCommand(0);
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (NewMultiFaceEnrollFragment.this.mCurrentFaceInputProgress == 360) {
                    NewMultiFaceEnrollFragment.this.mEnrollProgressAnimationComplete = true;
                    if (NewMultiFaceEnrollFragment.this.mFaceEnrollSucceed || !NewMultiFaceEnrollFragment.this.mCanSetNextStep) {
                        return;
                    }
                    NewMultiFaceEnrollFragment.this.mFaceUnlockManager.runOnFaceUnlockWorkerThread(new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput$NewMultiFaceEnrollFragment$22$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.AnonymousClass22.this.lambda$onAnimationEnd$0();
                        }
                    });
                }
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput$NewMultiFaceEnrollFragment$31  reason: invalid class name */
        /* loaded from: classes.dex */
        public class AnonymousClass31 implements Runnable {
            AnonymousClass31() {
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$run$0() {
                NewMultiFaceEnrollFragment.this.mFaceUnlockManager.sendEnrollCommand(0);
            }

            @Override // java.lang.Runnable
            public void run() {
                NewMultiFaceEnrollFragment.this.mFaceUnlockManager.runOnFaceUnlockWorkerThread(new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput$NewMultiFaceEnrollFragment$31$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.AnonymousClass31.this.lambda$run$0();
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput$NewMultiFaceEnrollFragment$32  reason: invalid class name */
        /* loaded from: classes.dex */
        public class AnonymousClass32 implements Runnable {
            AnonymousClass32() {
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$run$0() {
                NewMultiFaceEnrollFragment.this.mFaceUnlockManager.sendEnrollCommand(0);
            }

            @Override // java.lang.Runnable
            public void run() {
                NewMultiFaceEnrollFragment.this.mFinishDetectFace = true;
                NewMultiFaceEnrollFragment.this.startFaceInputProgressAnimation(0);
                NewMultiFaceEnrollFragment.this.mFaceUnlockManager.runOnFaceUnlockWorkerThread(new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput$NewMultiFaceEnrollFragment$32$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.AnonymousClass32.this.lambda$run$0();
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void enterFaceDetectStepAnimation() {
            this.mFinishFaceSuggestion = true;
            this.mMainHandler.removeCallbacks(this.mStartFaceSuggestionVideo);
            MediaPlayer mediaPlayer = this.mMediaPlayer;
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            this.mFaceInputSuggestionVideoImage.setAlpha(0);
            float f = this.mCurrentDetectViewPosition;
            float f2 = this.mEnrollDetectEnd;
            faceDetectRectAnimation(f, f2, 500, f2, this.mEnrollDetectRepeatEnd, 1200, true, 16.0f);
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.mFaceInputSuggestionVideo, "scaleX", 1.0f, 0.278f);
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.mFaceInputSuggestionVideo, "scaleY", 1.0f, 0.278f);
            PathInterpolator pathInterpolator = KeyguardSettingsFaceUnlockUtils.SLOWDOWN_INTERPOLATOR;
            ofFloat.setInterpolator(pathInterpolator);
            ofFloat2.setInterpolator(pathInterpolator);
            ofFloat.setDuration(330L);
            ofFloat2.setDuration(330L);
            ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.mFaceInputSuggestionVideo, "alpha", 1.0f, 0.0f);
            ofFloat3.setDuration(183L);
            ofFloat3.setInterpolator(new LinearInterpolator());
            animatorSet.play(ofFloat).with(ofFloat2).with(ofFloat3);
            animatorSet.start();
            this.mFaceInputCameraPreview.setOpaque(false);
            ValueAnimator ofFloat4 = ValueAnimator.ofFloat(0.0f, this.mCameraPreviewWidth / 2.0f);
            this.mFaceCameraPreviewCoveredAnimation = ofFloat4;
            ofFloat4.setInterpolator(pathInterpolator);
            this.mFaceCameraPreviewCoveredAnimation.setDuration(530L);
            this.mFaceCameraPreviewCoveredAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.9
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    NewMultiFaceEnrollFragment.this.mCameraPreviewCoverdView.refreshCameraView(floatValue, NewMultiFaceEnrollFragment.this.mHasSkipFrame, false, true);
                    if (floatValue == NewMultiFaceEnrollFragment.this.mCameraPreviewWidth / 2.0f) {
                        NewMultiFaceEnrollFragment.this.mHaseCompleteEnterDetectAnimation = true;
                        if (NewMultiFaceEnrollFragment.this.mNeedStartFaceInputAnimation) {
                            NewMultiFaceEnrollFragment.this.enterFaceInputStepAnimation();
                        }
                    }
                }
            });
            this.mFaceCameraPreviewCoveredAnimation.setStartDelay(35L);
            this.mFaceCameraPreviewCoveredAnimation.start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void enterFaceInputStepAnimation() {
            ValueAnimator valueAnimator = this.mFaceCameraPreviewCoveredAnimation;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator valueAnimator2 = this.mFaceDetectAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            this.mFaceInputProgressView.updateFaceInputProgress(0);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
            this.mFaceDetectAlphaAnimator = ofFloat;
            PathInterpolator pathInterpolator = KeyguardSettingsFaceUnlockUtils.SLOWDOWN_INTERPOLATOR;
            ofFloat.setInterpolator(pathInterpolator);
            this.mFaceDetectAlphaAnimator.setDuration(230L);
            this.mFaceDetectAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.11
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    NewMultiFaceEnrollFragment.this.mFaceDetectView.setAlpha(((Float) valueAnimator3.getAnimatedValue()).floatValue());
                }
            });
            this.mFaceDetectAlphaAnimator.start();
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(0.0f, (this.mCameraPreviewWidth - (this.mCameraPrviewCircleRadius * 2.0f)) / 2.0f);
            this.mFaceCameraPreviewCoveredAnimation = ofFloat2;
            ofFloat2.setInterpolator(pathInterpolator);
            this.mFaceCameraPreviewCoveredAnimation.setDuration(500L);
            this.mFaceCameraPreviewCoveredAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.12
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    NewMultiFaceEnrollFragment.this.mCameraPreviewCoverdView.refreshCameraView(((Float) valueAnimator3.getAnimatedValue()).floatValue(), true, false, false);
                }
            });
            this.mFaceCameraPreviewCoveredAnimation.start();
            ValueAnimator ofFloat3 = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.mFaceInputProgressAnimation = ofFloat3;
            ofFloat3.setDuration(250L);
            this.mFaceInputProgressAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.13
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    NewMultiFaceEnrollFragment.this.mFaceInputProgressView.setAlpha(((Float) valueAnimator3.getAnimatedValue()).floatValue());
                }
            });
            this.mFaceInputProgressAnimation.setStartDelay(350L);
            this.mFaceInputProgressAnimation.setInterpolator(new LinearInterpolator());
            this.mFaceInputProgressAnimation.start();
            this.mFaceInputGridView.setAlpha(1.0f);
            ValueAnimator ofFloat4 = ValueAnimator.ofFloat(0.0f, 0.07f);
            this.mFaceInputGridAnimation = ofFloat4;
            ofFloat4.setDuration(100L);
            this.mFaceInputGridAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.14
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    NewMultiFaceEnrollFragment.this.mFaceInputGridView.setGridViewAlpha(((Float) valueAnimator3.getAnimatedValue()).floatValue());
                }
            });
            this.mFaceInputGridAnimation.setStartDelay(320L);
            this.mFaceInputGridAnimation.setInterpolator(new LinearInterpolator());
            this.mFaceInputGridAnimation.start();
            ValueAnimator ofFloat5 = ValueAnimator.ofFloat(0.0f, 1250.0f);
            this.mFaceInputGridPointAnimation = ofFloat5;
            ofFloat5.setInterpolator(pathInterpolator);
            this.mFaceInputGridPointAnimation.setDuration(1250L);
            this.mFaceInputGridPointAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.15
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    NewMultiFaceEnrollFragment.this.mFaceInputGridView.updateFaceInputPoint(((Float) valueAnimator3.getAnimatedValue()).floatValue());
                }
            });
            this.mFaceInputGridPointAnimation.setStartDelay(320L);
            this.mFaceInputGridPointAnimation.setRepeatCount(-1);
            this.mFaceInputGridPointAnimation.start();
            final Matrix matrix = new Matrix();
            ValueAnimator ofFloat6 = ValueAnimator.ofFloat(1.0f, 1.15f);
            this.mCameraPreviewScaleAnimation = ofFloat6;
            ofFloat6.setDuration(400L);
            this.mCameraPreviewScaleAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.16
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    float floatValue = ((Float) valueAnimator3.getAnimatedValue()).floatValue();
                    matrix.reset();
                    matrix.postScale(floatValue, floatValue, NewMultiFaceEnrollFragment.this.mCameraPreviewWidth / 2.0f, NewMultiFaceEnrollFragment.this.mCameraPreviewHeight / 2.0f);
                    NewMultiFaceEnrollFragment.this.mFaceInputCameraPreview.setTransform(matrix);
                }
            });
            this.mCameraPreviewScaleAnimation.setStartDelay(320L);
            this.mCameraPreviewScaleAnimation.start();
            this.mMainHandler.postDelayed(this.mStartFaceInput, 1000L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void enterFaceSuggestionStepAnimation() {
            this.mFaceInputSuggestionVideoImage.setImageBitmap(KeyguardSettingsFaceUnlockUtils.getFirstFrameOfVideo(this.mActivity, R.raw.miui_face_input_suggestion_video));
            this.mFaceInputSuggestionVideo.setAlpha(0.0f);
            float f = this.mEnrollSuggestionDetectEnd;
            faceDetectRectAnimation(0.0f, f, 500, f, this.mEnrollSuggestionDetectRepeatEnd, 1200, false, 0.0f);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.mFaceDetectAlphaAnimator = ofFloat;
            ofFloat.setInterpolator(KeyguardSettingsFaceUnlockUtils.SLOWDOWN_INTERPOLATOR);
            this.mFaceDetectAlphaAnimator.setDuration(500L);
            this.mFaceDetectAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.8
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    NewMultiFaceEnrollFragment.this.mFaceDetectView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
                }
            });
            this.mFaceDetectAlphaAnimator.start();
            this.mMainHandler.postDelayed(this.mStartFaceSuggestionVideo, 500L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void enterRiskWarningDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.mActivity);
            AlertDialog.Builder cancelable = builder.setCancelable(false);
            boolean z = Build.IS_INTERNATIONAL_BUILD;
            cancelable.setTitle(z ? R.string.miui_face_enroll_risk_warning_title_text : R.string.miui_face_enroll_risk_warning_title_text_cn).setMessage(z ? R.string.face_data_suggesstion_first : R.string.face_data_suggesstion_first_cn).setPositiveButton(R.string.face_data_introduction_next, new DialogInterface.OnClickListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (!NewMultiFaceEnrollFragment.this.mHasClickNextBtn) {
                        NewMultiFaceEnrollFragment.this.mHasClickNextBtn = true;
                        IVisibleStyle show = Folme.useAt(NewMultiFaceEnrollFragment.this.mFaceInputFirstSuggestion).visible().setShow();
                        AnimConfig animConfig = KeyguardSettingsFaceUnlockUtils.HIDE_ANIM_CONFING;
                        show.hide(animConfig);
                        Folme.useAt(NewMultiFaceEnrollFragment.this.mFaceInputNextOrSuccessButton).visible().setShow().hide(animConfig);
                        NewMultiFaceEnrollFragment.this.startEnrollFace();
                        NewMultiFaceEnrollFragment.this.enterFaceDetectStepAnimation();
                    }
                    dialogInterface.dismiss();
                }
            }).setNegativeButton(R.string.miui_face_enroll_risk_warning_btn_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    NewMultiFaceEnrollFragment.this.mHasClickStartAddBtn = false;
                    if (NewMultiFaceEnrollFragment.this.mCountdownTimer != null) {
                        NewMultiFaceEnrollFragment.this.mCountdownTimer.cancel();
                        NewMultiFaceEnrollFragment.this.mCountdownTimer = null;
                    }
                }
            });
            this.mRiskWarningDialog = builder.create();
            if (!z) {
                this.mRiskWarningDialog.setView(View.inflate(this.mActivity, R.layout.miui_face_enroll_warning_dialog_highlight, null));
            }
            this.mRiskWarningDialog.show();
            final Button button = this.mRiskWarningDialog.getButton(-1);
            button.setEnabled(false);
            this.mCountdownTimer = new CountDownTimer(5000L, 1000L) { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.5
                @Override // android.os.CountDownTimer
                public void onFinish() {
                    button.setEnabled(true);
                    button.setText(NewMultiFaceEnrollFragment.this.getResources().getString(Build.IS_INTERNATIONAL_BUILD ? R.string.face_data_siggesstion_next : R.string.face_data_siggesstion_next_cn));
                }

                @Override // android.os.CountDownTimer
                public void onTick(long j) {
                    button.setText(NewMultiFaceEnrollFragment.this.getResources().getString(Build.IS_INTERNATIONAL_BUILD ? R.string.face_data_siggesstion_next_time : R.string.face_data_siggesstion_next_time_cn, Long.valueOf(j / 1000)));
                }
            }.start();
        }

        private void faceDetectRectAnimation(final float f, final float f2, int i, final float f3, final float f4, final int i2, final boolean z, final float f5) {
            ValueAnimator valueAnimator = this.mFaceDetectAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(f, f2);
            this.mFaceDetectAnimator = ofFloat;
            ofFloat.setInterpolator(KeyguardSettingsFaceUnlockUtils.SLOWDOWN_INTERPOLATOR);
            this.mFaceDetectAnimator.setDuration(i);
            this.mFaceDetectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.25
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    float floatValue = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                    float f6 = f;
                    NewMultiFaceEnrollFragment.this.mFaceDetectView.updateFaceDetectPosition(floatValue, z, ((floatValue - f6) * f5) / (f2 - f6));
                    NewMultiFaceEnrollFragment.this.mCurrentDetectViewPosition = floatValue;
                }
            });
            this.mFaceDetectAnimator.addListener(new Animator.AnimatorListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.26
                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    NewMultiFaceEnrollFragment.this.repeatFaceDetectRectAnimation(f3, f4, i2);
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationRepeat(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                }
            });
            this.mFaceDetectAnimator.start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPause$0() {
            this.mFaceUnlockManager.stopEnrollFace();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$startEnrollFace$1(RectF rectF, RectF rectF2) {
            this.mFaceUnlockManager.startEnrollFace(this.mFaceEnrollToken, this.mFaceInputCameraPreviewSurfaceTexture, this.mEnrollCallback, rectF, rectF2, 5, 60000);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$startFaceInputSuccessAnimation$3() {
            this.mFaceUnlockManager.stopEnrollFace();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$updateFaceHelpInfo$2() {
            this.mFaceUnlockManager.sendEnrollCommand(0);
        }

        private void outerFaceInputStepAnimation() {
            stopFaceInputProgressAnimation();
            ValueAnimator valueAnimator = this.mFaceInputGridAnimation;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator valueAnimator2 = this.mFaceInputGridPointAnimation;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            this.mCurrentFaceInputProgress = 0;
            this.mFaceEnrollStep = 0;
            this.mCurrentEnrollAnimationStep = 1;
            this.mEnrollAnimationStep = 1;
            float f = this.mCameraPreviewWidth;
            ValueAnimator ofFloat = ValueAnimator.ofFloat((f - (this.mCameraPrviewCircleRadius * 2.0f)) / 2.0f, f / 2.0f);
            this.mFaceCameraPreviewCoveredAnimation = ofFloat;
            ofFloat.setDuration(500L);
            this.mFaceCameraPreviewCoveredAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.17
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    NewMultiFaceEnrollFragment.this.mCameraPreviewCoverdView.refreshCameraView(((Float) valueAnimator3.getAnimatedValue()).floatValue(), true, false, true);
                }
            });
            this.mFaceCameraPreviewCoveredAnimation.start();
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(1.0f, 0.0f);
            this.mFaceInputProgressAlphaAnimation = ofFloat2;
            ofFloat2.setDuration(250L);
            this.mFaceInputProgressAlphaAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.18
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    NewMultiFaceEnrollFragment.this.mFaceInputProgressView.setAlpha(((Float) valueAnimator3.getAnimatedValue()).floatValue());
                }
            });
            this.mFaceInputProgressAlphaAnimation.start();
            ValueAnimator ofFloat3 = ValueAnimator.ofFloat(1.0f, 0.0f);
            this.mFaceInputGridAnimation = ofFloat3;
            ofFloat3.setDuration(250L);
            this.mFaceInputGridAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.19
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    NewMultiFaceEnrollFragment.this.mFaceInputGridView.setAlpha(((Float) valueAnimator3.getAnimatedValue()).floatValue());
                }
            });
            this.mFaceInputGridAnimation.start();
            ValueAnimator ofFloat4 = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.mFaceDetectAlphaAnimator = ofFloat4;
            ofFloat4.setDuration(250L);
            this.mFaceDetectAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.20
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    float floatValue = ((Float) valueAnimator3.getAnimatedValue()).floatValue();
                    NewMultiFaceEnrollFragment.this.mFaceDetectView.setAlpha(floatValue);
                    if (floatValue == 1.0f) {
                        NewMultiFaceEnrollFragment.this.mFinishDetectFace = false;
                        NewMultiFaceEnrollFragment.this.mFaceDetectView.updateDetectImage(19.0f);
                        NewMultiFaceEnrollFragment newMultiFaceEnrollFragment = NewMultiFaceEnrollFragment.this;
                        newMultiFaceEnrollFragment.repeatFaceDetectRectAnimation(newMultiFaceEnrollFragment.mEnrollDetectEnd, NewMultiFaceEnrollFragment.this.mEnrollDetectRepeatEnd, 1200);
                        NewMultiFaceEnrollFragment.this.mMainHandler.postDelayed(NewMultiFaceEnrollFragment.this.mResetFaceEnroll, 2000L);
                    }
                }
            });
            this.mFaceDetectAlphaAnimator.setStartDelay(250L);
            this.mFaceDetectAlphaAnimator.start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void repeatFaceDetectRectAnimation(float f, float f2, int i) {
            ValueAnimator valueAnimator = this.mFaceDetectAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(f, f2, f);
            this.mFaceDetectAnimator = ofFloat;
            ofFloat.setDuration(i);
            this.mFaceDetectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.27
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    float floatValue = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                    NewMultiFaceEnrollFragment.this.mCurrentDetectViewPosition = floatValue;
                    NewMultiFaceEnrollFragment.this.mFaceDetectView.updateFaceDetectPosition(floatValue, false, 1.0f);
                }
            });
            this.mFaceDetectAnimator.setRepeatMode(1);
            this.mFaceDetectAnimator.setRepeatCount(-1);
            this.mFaceDetectAnimator.start();
        }

        private void resetFaceEnrollAnimation() {
            CountDownTimer countDownTimer = this.mCountdownTimer;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                this.mCountdownTimer = null;
            }
            MediaPlayer mediaPlayer = this.mMediaPlayer;
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                this.mMediaPlayer.release();
                this.mMediaPlayer = null;
            }
            ValueAnimator valueAnimator = this.mFaceDetectAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.mFaceDetectAnimator = null;
            }
            ValueAnimator valueAnimator2 = this.mFaceInputGridPointAnimation;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
                this.mFaceInputGridPointAnimation = null;
            }
            ValueAnimator valueAnimator3 = this.mFaceInputProgressAnimation;
            if (valueAnimator3 != null) {
                valueAnimator3.cancel();
                this.mFaceInputProgressAnimation = null;
            }
        }

        private void saveFaceName() {
            if (this.mFaceEnrollSucceed) {
                String obj = this.mFaceInputEditNameEdit.getText().toString();
                if (TextUtils.isEmpty(obj)) {
                    obj = this.mEnrollFaceName;
                }
                KeyguardSettingsFaceUnlockUtils.setFaceDataName(this.mActivity, KeyguardSettingsFaceUnlockUtils.getEnrolledFaceList(this.mActivity).get(r1.size() - 1), obj);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setContentDescription(String str) {
            this.mFaceInputTitle.setContentDescription(str);
            this.mFaceInputTitle.announceForAccessibility(str);
        }

        private void showOpenCameraAbnormalAnimation(int i, int i2) {
            ValueAnimator valueAnimator = this.mFaceDetectAlphaAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(i, i2);
            this.mFaceDetectAlphaAnimator = ofFloat;
            ofFloat.setDuration(500L);
            this.mFaceDetectAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.30
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    NewMultiFaceEnrollFragment.this.mFaceDetectView.setAlpha(((Float) valueAnimator2.getAnimatedValue()).floatValue());
                }
            });
            this.mFaceDetectAlphaAnimator.setInterpolator(new PathInterpolator(0.5f, 0.0f, 0.6f, 1.0f));
            this.mFaceDetectAlphaAnimator.start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startFaceInputProgressAnimation(int i) {
            this.mEnrollHasStop = false;
            ValueAnimator valueAnimator = this.mFaceInputProgressAnimation;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            int i2 = ((360 - i) * 1200) / 360;
            ValueAnimator ofInt = ValueAnimator.ofInt(i, 360);
            this.mFaceInputProgressAnimation = ofInt;
            ofInt.setDuration(i2);
            this.mFaceInputProgressAnimation.addUpdateListener(new AnonymousClass21());
            this.mFaceInputProgressAnimation.addListener(new AnonymousClass22());
            this.mFaceInputProgressAnimation.start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startFaceInputSuccessAnimation() {
            ValueAnimator valueAnimator = this.mFaceInputGridAnimation;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator valueAnimator2 = this.mFaceInputGridPointAnimation;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            this.mBackImage.setVisibility(4);
            this.mFaceInputTitle.setVisibility(4);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
            this.mFaceInputGridAnimation = ofFloat;
            PathInterpolator pathInterpolator = KeyguardSettingsFaceUnlockUtils.SLOWDOWN_INTERPOLATOR;
            ofFloat.setInterpolator(pathInterpolator);
            this.mFaceInputGridAnimation.setDuration(150L);
            this.mFaceInputGridAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.23
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    NewMultiFaceEnrollFragment.this.mFaceInputGridView.setAlpha(((Float) valueAnimator3.getAnimatedValue()).floatValue());
                }
            });
            this.mFaceInputGridAnimation.start();
            playVideo(new Surface(this.mFaceInputSuccessVideo.getSurfaceTexture()), false, R.raw.miui_facea_input_success);
            Bitmap bitmap = this.mFaceInputCameraPreview.getBitmap();
            if (bitmap != null) {
                Matrix matrix = new Matrix();
                matrix.reset();
                matrix.postScale(1.15f, 1.15f);
                this.mFaceEnrollSuccessView.updateFaceBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false));
                ValueAnimator ofFloat2 = ValueAnimator.ofFloat(this.mCameraPrviewCircleRadius, 0.0f);
                this.mFaceSuccessCameraPreviewAnimation = ofFloat2;
                ofFloat2.setDuration(480L);
                this.mFaceSuccessCameraPreviewAnimation.setInterpolator(pathInterpolator);
                this.mFaceSuccessCameraPreviewAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.24
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator valueAnimator3) {
                        float floatValue = ((Float) valueAnimator3.getAnimatedValue()).floatValue();
                        NewMultiFaceEnrollFragment.this.mFaceEnrollSuccessView.drawFaceSuccessView(floatValue);
                        NewMultiFaceEnrollFragment.this.mCameraPreviewCoverdView.refreshCameraView(floatValue, true, true, false);
                    }
                });
                this.mFaceSuccessCameraPreviewAnimation.start();
            }
            this.mFaceUnlockManager.runOnFaceUnlockWorkerThread(new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput$NewMultiFaceEnrollFragment$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.this.lambda$startFaceInputSuccessAnimation$3();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void stopFaceInputProgressAnimation() {
            this.mEnrollHasStop = true;
            ValueAnimator valueAnimator = this.mFaceInputProgressAnimation;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateFaceErrorInfo(int i) {
            if (i == 3 || i == 8) {
                finish();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Removed duplicated region for block: B:58:0x00ec  */
        /* JADX WARN: Removed duplicated region for block: B:62:0x00ff  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void updateFaceHelpInfo(int r9) {
            /*
                Method dump skipped, instructions count: 364
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.updateFaceHelpInfo(int):void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateFaceUnlockSuccessView() {
            IVisibleStyle hide = Folme.useAt(this.mFaceInputSuccessTitle).visible().setHide();
            AnimConfig animConfig = KeyguardSettingsFaceUnlockUtils.SHOW_ANIM_CONFING;
            hide.show(animConfig);
            this.mFaceInputSuccessTitle.announceForAccessibility(getResources().getString(R.string.face_data_input_ok_title));
            Folme.useAt(this.mFaceInputSuccessMsg).visible().setHide().show(animConfig);
            TextView textView = this.mFaceInputSuccessMsg;
            int i = R.string.miui_face_enroll_success;
            textView.setText(i);
            Folme.useAt(this.mFaceInputEditNameTitle).visible().setHide().show(animConfig);
            Folme.useAt(this.mFaceInputEditNameEdit).visible().setHide().show(animConfig);
            Folme.useAt(this.mFaceInputNextOrSuccessButton).visible().setHide().show(animConfig);
            this.mFaceInputNextOrSuccessButton.setText(R.string.face_data_input_ok);
            setContentDescription(getResources().getString(i));
        }

        @Override // androidx.fragment.app.Fragment
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            if (this.mActivity == null) {
                this.mActivity = activity;
            }
        }

        @Override // androidx.fragment.app.Fragment
        public void onAttach(Context context) {
            super.onAttach(context);
            if ((context instanceof Activity) && this.mActivity == null) {
                this.mActivity = (Activity) context;
            }
        }

        @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment, android.content.ComponentCallbacks
        public void onConfigurationChanged(Configuration configuration) {
            super.onConfigurationChanged(configuration);
            if (!KeyguardSettingsFaceUnlockUtils.isLargeScreen(this.mActivity.getApplicationContext()) || this.mFaceEnrollSucceed) {
                return;
            }
            this.mActivity.setResult(103);
            finish();
        }

        @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            setThemeRes(R.style.Theme_Dark_Settings_NoTitle);
            this.mFaceUnlockManager = KeyguardSettingsFaceUnlockManager.getInstance(this.mActivity);
            Activity activity = this.mActivity;
            KeyguardSettingsFaceUnlockUtils.setFaceEnrollViewStatus(activity, activity.getWindow());
            this.mMainHandler = new Handler(Looper.getMainLooper());
            if (MiuiKeyguardSettingsUtils.isInFullWindowGestureMode(getActivity().getApplicationContext())) {
                this.mActivity.getWindow().clearFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
            }
            this.mFaceEnrollToken = getIntent().getByteArrayExtra("for_face_enroll");
            boolean booleanExtra = getIntent().getBooleanExtra("for_face_enroll_from_normal", false);
            this.mFaceEnrollFromNormal = booleanExtra;
            if (booleanExtra) {
                return;
            }
            finish();
        }

        @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
        public void onDetach() {
            super.onDetach();
            this.mActivity = null;
        }

        @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
        public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            super.onInflateView(layoutInflater, viewGroup, bundle);
            View inflate = layoutInflater.inflate(R.layout.miui_face_enroll, viewGroup, false);
            this.mContentView = inflate;
            this.mBackImage = (LinearLayout) inflate.findViewById(R.id.back_image);
            this.mFaceInputTitle = (TextView) this.mContentView.findViewById(R.id.miui_face_input_title);
            this.mFaceInputFirstSuggestion = (TextView) this.mContentView.findViewById(R.id.miui_face_input_first_suggestion);
            this.mFaceInputCameraPreview = (TextureView) this.mContentView.findViewById(R.id.miui_face_input_camera_preview);
            this.mCameraPreviewCoverdView = (CameraPreviewCoverdView) this.mContentView.findViewById(R.id.miui_face_input_camera_preview_second_coverd);
            this.mFaceInputSuggestionVideoImage = (ImageView) this.mContentView.findViewById(R.id.miui_face_input_suggestion_video_image);
            TextureView textureView = (TextureView) this.mContentView.findViewById(R.id.miui_face_input_suggestion_video);
            this.mFaceInputSuggestionVideo = textureView;
            textureView.setSurfaceTextureListener(this.surfaceTextureListener);
            this.mFaceDetectView = (FaceDetectView) this.mContentView.findViewById(R.id.miui_face_input_detect);
            this.mFaceInputProgressView = (FaceInputProgressView) this.mContentView.findViewById(R.id.miui_face_input_progress_circle);
            this.mFaceInputGridView = (FaceInputGridView) this.mContentView.findViewById(R.id.miui_face_input_grid);
            this.mFaceInputSuccessVideo = (TextureView) this.mContentView.findViewById(R.id.miui_face_input_success_video);
            this.mFaceEnrollSuccessView = (FaceEnrollSuccessView) this.mContentView.findViewById(R.id.miui_face_input_success_image);
            this.mFaceInputSuccessTitle = (TextView) this.mContentView.findViewById(R.id.miui_face_input_success_title);
            this.mFaceInputSuccessMsg = (TextView) this.mContentView.findViewById(R.id.miui_face_input_success_message);
            this.mFaceInputEditNameTitle = (TextView) this.mContentView.findViewById(R.id.multi_face_name_text);
            this.mFaceInputEditNameEdit = (EditText) this.mContentView.findViewById(R.id.multi_face_name_edit);
            Button button = (Button) this.mContentView.findViewById(R.id.miui_face_input_nextorsuccess_button);
            this.mFaceInputNextOrSuccessButton = button;
            KeyguardSettingsFaceUnlockUtils.createCardFolmeTouchStyle(button);
            this.mFaceInputNextOrSuccessButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (!NewMultiFaceEnrollFragment.this.mFaceEnrollSucceed) {
                        if (NewMultiFaceEnrollFragment.this.mHasClickStartAddBtn) {
                            return;
                        }
                        NewMultiFaceEnrollFragment.this.mHasClickStartAddBtn = true;
                        NewMultiFaceEnrollFragment.this.enterRiskWarningDialog();
                        return;
                    }
                    if (KeyguardSettingsFaceUnlockUtils.isDeviceProvisioned(NewMultiFaceEnrollFragment.this.mActivity)) {
                        Intent intent = new Intent();
                        intent.setClassName("com.android.settings", "com.android.settings.faceunlock.MiuiFaceDataManage");
                        intent.putExtra("input_facedata_need_skip_password", true);
                        NewMultiFaceEnrollFragment.this.startActivity(intent);
                    } else {
                        NewMultiFaceEnrollFragment.this.setResult(-1);
                    }
                    NewMultiFaceEnrollFragment.this.finish();
                }
            });
            this.mBackImage.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    NewMultiFaceEnrollFragment.this.mActivity.setResult(0);
                    NewMultiFaceEnrollFragment.this.finish();
                }
            });
            this.mBackImage.setContentDescription(getString(R.string.structure_face_data_introduction_back));
            this.mFaceInputTitle.setText(R.string.face_data_input_title);
            this.mEnrollSuggestionDetectEnd = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_suggesiton_detect_image_end);
            this.mEnrollSuggestionDetectRepeatEnd = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_suggesiton_detect_image_repeta_end);
            this.mEnrollDetectEnd = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_detect_image_end);
            this.mEnrollDetectRepeatEnd = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_detect_image_repeta_end);
            this.mCameraPreviewWidth = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_camera_preview_width);
            this.mCameraPreviewHeight = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_camera_preview_height);
            this.mCameraPrviewCircleRadius = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_circle_radius);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mBackImage.getLayoutParams();
            marginLayoutParams.topMargin = KeyguardSettingsFaceUnlockUtils.getStatusBarHeight(this.mActivity);
            this.mBackImage.setLayoutParams(marginLayoutParams);
            return this.mContentView;
        }

        @Override // androidx.fragment.app.Fragment
        public void onPause() {
            super.onPause();
            AlertDialog alertDialog = this.mRiskWarningDialog;
            if (alertDialog != null && alertDialog.isShowing()) {
                this.mRiskWarningDialog.dismiss();
                this.mRiskWarningDialog = null;
            }
            if (this.mFinishFaceSuggestion && !this.mFaceEnrollSucceed) {
                Resources resources = getResources();
                int i = R.string.face_data_input_cancel_msg;
                setContentDescription(resources.getString(i));
                Toast.makeText(this.mActivity, i, 0).show();
            }
            this.mActivity.setResult(this.mFaceEnrollSucceed ? -1 : 0);
            if (this.mFaceEnrollSucceed) {
                Activity activity = this.mActivity;
                KeyguardSettingsFaceUnlockUtils.setFaceUnlockSettingValues(activity, KeyguardSettingsFaceUnlockUtils.getEnrolledFacesNumber(activity));
                saveFaceName();
            }
            if (this.mFinishFaceSuggestion) {
                this.mFaceUnlockManager.runOnFaceUnlockWorkerThread(new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput$NewMultiFaceEnrollFragment$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.this.lambda$onPause$0();
                    }
                });
                this.mMainHandler.removeCallbacks(this.mResetFaceEnroll);
                this.mMainHandler.removeCallbacks(this.mStartFaceInput);
            }
            this.mMainHandler.removeCallbacks(this.mStartFaceSuggestionVideo);
            this.mMainHandler.removeCallbacks(this.mEnterFaceInput);
            resetFaceEnrollAnimation();
            this.mFaceEnrollSucceed = false;
            finish();
        }

        @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            this.mActivity.getWindow().getDecorView().setSystemUiVisibility(4866);
            this.mFaceUnlockManager = KeyguardSettingsFaceUnlockManager.getInstance(this.mActivity);
            if (KeyguardSettingsFaceUnlockUtils.getEnrolledFacesNumber(this.mActivity) == 2) {
                Toast.makeText(this.mActivity.getApplicationContext(), R.string.multi_face_number_reach_limit, 0).show();
                finish();
            }
            this.mMainHandler.postDelayed(this.mEnterFaceInput, 5000L);
        }

        @Override // com.android.settings.BaseEditFragment, com.android.settings.BaseFragment, androidx.fragment.app.Fragment
        public void onStart() {
            ActionBar appCompatActionBar;
            super.onStart();
            if (!(getActivity() instanceof AppCompatActivity) || (appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar()) == null) {
                return;
            }
            appCompatActionBar.hide();
        }

        public void playVideo(Surface surface, boolean z, int i) {
            try {
                MediaPlayer mediaPlayer = this.mMediaPlayer;
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                new MediaPlayer();
                MediaPlayer create = MediaPlayer.create(this.mActivity, i);
                this.mMediaPlayer = create;
                create.setSurface(surface);
                this.mMediaPlayer.setLooping(z);
                this.mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.28
                    @Override // android.media.MediaPlayer.OnPreparedListener
                    public void onPrepared(MediaPlayer mediaPlayer2) {
                        NewMultiFaceEnrollFragment.this.mMediaPlayer.start();
                    }
                });
                this.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.29
                    @Override // android.media.MediaPlayer.OnCompletionListener
                    public void onCompletion(MediaPlayer mediaPlayer2) {
                        if (NewMultiFaceEnrollFragment.this.mFaceEnrollSucceed) {
                            NewMultiFaceEnrollFragment newMultiFaceEnrollFragment = NewMultiFaceEnrollFragment.this;
                            if (newMultiFaceEnrollFragment.mActivity != null) {
                                newMultiFaceEnrollFragment.updateFaceUnlockSuccessView();
                            }
                        }
                    }
                });
            } catch (Exception unused) {
            }
        }

        public void startEnrollFace() {
            this.mFaceUnlockManager = KeyguardSettingsFaceUnlockManager.getInstance(this.mActivity);
            if (this.mFaceInputCameraPreview.getSurfaceTexture() != null) {
                this.mFaceInputCameraPreviewSurfaceTexture = this.mFaceInputCameraPreview.getSurfaceTexture();
                this.mFaceEnrollSucceed = false;
                this.mFaceInputTitle.setText(R.string.face_data_input_camera_ok);
                final RectF rectF = new RectF(70.0f, 156.0f, 400.0f, 500.0f);
                final RectF rectF2 = new RectF(105.0f, 91.0f, 375.0f, 549.0f);
                this.mFaceUnlockManager.runOnFaceUnlockWorkerThread(new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput$NewMultiFaceEnrollFragment$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.this.lambda$startEnrollFace$1(rectF, rectF2);
                    }
                });
            }
        }
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", NewMultiFaceEnrollFragment.class.getName());
        intent.putExtra(":settings:show_fragment_title", R.string.add_facerecoginition_text);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTheme(R.style.Theme_Dark_Settings);
    }
}
