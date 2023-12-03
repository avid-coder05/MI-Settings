package com.android.settings.faceunlock;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.face.FaceManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.BaseEditFragment;
import com.android.settings.MiuiKeyguardSettingsUtils;
import com.android.settings.R;
import com.android.settings.SettingsCompatActivity;
import com.android.settings.faceunlock.MiuiNormalCameraFaceInput;
import miui.os.Build;
import miuix.animation.Folme;
import miuix.animation.IVisibleStyle;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class MiuiNormalCameraFaceInput extends SettingsCompatActivity {

    /* loaded from: classes.dex */
    public static class NewFaceEnrollFragment extends BaseEditFragment {
        private LinearLayout mBackImage;
        private CameraPreviewCoverdView mCameraPreviewCoverdView;
        private float mCameraPrviewCircleRadius;
        protected View mContentView;
        private CountDownTimer mCountdownTimer;
        private float mEnrollSuggestionDetectEnd;
        private float mEnrollSuggestionDetectRepeatEnd;
        private ValueAnimator mFaceCameraPreviewCoverAnimation;
        private ValueAnimator mFaceDetectAlphaAnimator;
        private ValueAnimator mFaceDetectAnimator;
        private FaceDetectView mFaceDetectView;
        private boolean mFaceEnrollFromNormal;
        private boolean mFaceEnrollSucceed;
        private FaceEnrollSuccessView mFaceEnrollSuccessView;
        private TextureView mFaceInputCameraPreview;
        private SurfaceTexture mFaceInputCameraPreviewSurfaceTexture;
        private TextView mFaceInputFirstSuggestion;
        private Button mFaceInputNextOrSuccessButton;
        private ValueAnimator mFaceInputProgressAnimation;
        private FaceInputProgressView mFaceInputProgressView;
        private TextView mFaceInputSuccessMsg;
        private TextView mFaceInputSuccessTitle;
        private TextureView mFaceInputSuccessVideo;
        private TextureView mFaceInputSuggestionVideo;
        private ImageView mFaceInputSuggestionVideoImage;
        private TextView mFaceInputTitle;
        private KeyguardSettingsFaceUnlockManager mFaceUnlockManager;
        private boolean mFinishFaceSuggestion;
        private boolean mHasClickNextBtn;
        private boolean mHasClickStartAddBtn;
        private Handler mMainHandler;
        private MediaPlayer mMediaPlayer;
        private long mLastAnnounceTime = 0;
        protected Activity mActivity = null;
        private AlertDialog mRiskWarningDialog = null;
        private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.7
            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                if (NewFaceEnrollFragment.this.mFinishFaceSuggestion) {
                    return;
                }
                NewFaceEnrollFragment.this.enterFaceSuggestionStepAnimation();
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
        FaceManager.EnrollmentCallback mEnrollCallback = new FaceManager.EnrollmentCallback() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.12
            public void onEnrollmentError(final int i, CharSequence charSequence) {
                Slog.i("miui_face", "enrollCallback, onEnrollmentError errMsgId:" + i + " errString:" + ((Object) charSequence));
                Activity activity = NewFaceEnrollFragment.this.mActivity;
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.12.1
                        @Override // java.lang.Runnable
                        public void run() {
                            NewFaceEnrollFragment.this.updateFaceErrorInfo(i);
                        }
                    });
                }
            }

            public void onEnrollmentHelp(final int i, CharSequence charSequence) {
                Slog.i("miui_face", "enrollCallback, onEnrollmentHelp helpMsgId:" + i + " helpString:" + ((Object) charSequence));
                Activity activity = NewFaceEnrollFragment.this.mActivity;
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.12.2
                        @Override // java.lang.Runnable
                        public void run() {
                            NewFaceEnrollFragment.this.updateFaceHelpInfo(i);
                        }
                    });
                }
            }

            public void onEnrollmentProgress(int i) {
                Slog.i("miui_face", "enrollCallback, onEnrollmentProgress :" + i);
                Activity activity = NewFaceEnrollFragment.this.mActivity;
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.12.3
                        @Override // java.lang.Runnable
                        public void run() {
                            NewFaceEnrollFragment.this.mFaceEnrollSucceed = true;
                            KeyguardSettingsFaceUnlockUtils.setFaceUnlockSettingValues(NewFaceEnrollFragment.this.mActivity, 1);
                            NewFaceEnrollFragment.this.mFaceInputTitle.setText(R.string.structure_face_data_input_error_keep_inface);
                            NewFaceEnrollFragment.this.startFaceInputProgressAnimation(0);
                        }
                    });
                }
            }
        };
        private final Runnable mStartFaceInput = new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.21
            @Override // java.lang.Runnable
            public void run() {
                NewFaceEnrollFragment.this.startEnrollFace();
            }
        };
        private final Runnable mStartFaceSuggestionVideo = new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.22
            @Override // java.lang.Runnable
            public void run() {
                NewFaceEnrollFragment.this.mFaceInputSuggestionVideo.setAlpha(1.0f);
                NewFaceEnrollFragment.this.playVideo(new Surface(NewFaceEnrollFragment.this.mFaceInputSuggestionVideo.getSurfaceTexture()), true, R.raw.miui_face_input_suggestion_video);
            }
        };

        /* JADX INFO: Access modifiers changed from: private */
        public void enterFaceInputStepAnimation() {
            ValueAnimator valueAnimator = this.mFaceDetectAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.mFinishFaceSuggestion = true;
            this.mFaceInputSuggestionVideoImage.setAlpha(0);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
            this.mFaceDetectAlphaAnimator = ofFloat;
            ofFloat.setDuration(230L);
            this.mFaceDetectAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.9
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    NewFaceEnrollFragment.this.mFaceDetectView.setAlpha(((Float) valueAnimator2.getAnimatedValue()).floatValue());
                }
            });
            this.mFaceDetectAlphaAnimator.setInterpolator(new PathInterpolator(0.5f, 0.0f, 0.6f, 1.0f));
            this.mFaceDetectAlphaAnimator.start();
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.mFaceInputSuggestionVideo, "scaleX", 1.0f, 0.278f);
            ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.mFaceInputSuggestionVideo, "scaleY", 1.0f, 0.278f);
            ofFloat2.setDuration(330L);
            ofFloat3.setDuration(330L);
            ofFloat2.setInterpolator(new PathInterpolator(0.5f, 0.0f, 0.6f, 1.0f));
            ofFloat3.setInterpolator(new PathInterpolator(0.5f, 0.0f, 0.6f, 1.0f));
            ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(this.mFaceInputSuggestionVideo, "alpha", 1.0f, 0.0f);
            ofFloat4.setDuration(183L);
            ofFloat4.setInterpolator(new LinearInterpolator());
            animatorSet.play(ofFloat2).with(ofFloat3).with(ofFloat4);
            animatorSet.start();
            this.mFaceInputCameraPreview.setOpaque(false);
            ValueAnimator ofFloat5 = ValueAnimator.ofFloat(0.0f, this.mCameraPrviewCircleRadius);
            this.mFaceCameraPreviewCoverAnimation = ofFloat5;
            ofFloat5.setDuration(530L);
            this.mFaceCameraPreviewCoverAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.10
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    NewFaceEnrollFragment.this.mCameraPreviewCoverdView.refreshCameraView(((Float) valueAnimator2.getAnimatedValue()).floatValue(), false, true, true);
                }
            });
            this.mFaceCameraPreviewCoverAnimation.setStartDelay(35L);
            this.mFaceCameraPreviewCoverAnimation.start();
            ValueAnimator ofFloat6 = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.mFaceInputProgressAnimation = ofFloat6;
            ofFloat6.setDuration(250L);
            this.mFaceInputProgressAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.11
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    NewFaceEnrollFragment.this.mFaceInputProgressView.setAlpha(((Float) valueAnimator2.getAnimatedValue()).floatValue());
                }
            });
            this.mFaceInputProgressAnimation.setStartDelay(250L);
            this.mFaceInputProgressAnimation.setInterpolator(new LinearInterpolator());
            this.mFaceInputProgressAnimation.start();
            this.mMainHandler.postDelayed(this.mStartFaceInput, 500L);
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
            this.mFaceDetectAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.8
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    NewFaceEnrollFragment.this.mFaceDetectView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
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
            cancelable.setTitle(z ? R.string.miui_face_enroll_risk_warning_title_text : R.string.miui_face_enroll_risk_warning_title_text_cn).setMessage(z ? R.string.face_data_suggesstion_first : R.string.face_data_suggesstion_first_cn).setPositiveButton(R.string.face_data_introduction_next, new DialogInterface.OnClickListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (!NewFaceEnrollFragment.this.mHasClickNextBtn) {
                        NewFaceEnrollFragment.this.mHasClickNextBtn = true;
                        IVisibleStyle show = Folme.useAt(NewFaceEnrollFragment.this.mFaceInputFirstSuggestion).visible().setShow();
                        AnimConfig animConfig = KeyguardSettingsFaceUnlockUtils.HIDE_ANIM_CONFING;
                        show.hide(animConfig);
                        Folme.useAt(NewFaceEnrollFragment.this.mFaceInputNextOrSuccessButton).visible().setShow().hide(animConfig);
                        NewFaceEnrollFragment.this.enterFaceInputStepAnimation();
                    }
                    dialogInterface.dismiss();
                }
            }).setNegativeButton(R.string.miui_face_enroll_risk_warning_btn_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    NewFaceEnrollFragment.this.mHasClickStartAddBtn = false;
                    if (NewFaceEnrollFragment.this.mCountdownTimer != null) {
                        NewFaceEnrollFragment.this.mCountdownTimer.cancel();
                        NewFaceEnrollFragment.this.mCountdownTimer = null;
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
            this.mCountdownTimer = new CountDownTimer(5000L, 1000L) { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.5
                @Override // android.os.CountDownTimer
                public void onFinish() {
                    button.setEnabled(true);
                    button.setText(NewFaceEnrollFragment.this.getResources().getString(Build.IS_INTERNATIONAL_BUILD ? R.string.face_data_siggesstion_next : R.string.face_data_siggesstion_next_cn));
                }

                @Override // android.os.CountDownTimer
                public void onTick(long j) {
                    button.setText(NewFaceEnrollFragment.this.getResources().getString(Build.IS_INTERNATIONAL_BUILD ? R.string.face_data_siggesstion_next_time : R.string.face_data_siggesstion_next_time_cn, Long.valueOf(j / 1000)));
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
            ofFloat.setDuration(i);
            this.mFaceDetectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.16
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    float floatValue = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                    float f6 = f;
                    NewFaceEnrollFragment.this.mFaceDetectView.updateFaceDetectPosition(floatValue, z, ((floatValue - f6) * f5) / (f2 - f6));
                }
            });
            this.mFaceDetectAnimator.addListener(new Animator.AnimatorListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.17
                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    NewFaceEnrollFragment.this.repeatFaceDetectRectAnimation(f3, f4, i2);
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationRepeat(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                }
            });
            this.mFaceDetectAnimator.setInterpolator(new PathInterpolator(0.5f, 0.0f, 0.6f, 1.0f));
            this.mFaceDetectAnimator.start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPause$0() {
            this.mFaceUnlockManager.stopEnrollFace();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$startEnrollFace$1(RectF rectF) {
            this.mFaceUnlockManager.startEnrollFace(null, this.mFaceInputCameraPreviewSurfaceTexture, this.mEnrollCallback, null, rectF, 1, 60000);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$startFaceInputSuccessAnimation$2() {
            this.mFaceUnlockManager.stopEnrollFace();
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
            this.mFaceDetectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.18
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    NewFaceEnrollFragment.this.mFaceDetectView.updateFaceDetectPosition(((Float) valueAnimator2.getAnimatedValue()).floatValue(), false, 1.0f);
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
            ValueAnimator valueAnimator2 = this.mFaceInputProgressAnimation;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
                this.mFaceInputProgressAnimation = null;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setContentDescription(String str) {
            this.mFaceInputTitle.setContentDescription(str);
            this.mFaceInputTitle.announceForAccessibility(str);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startFaceInputProgressAnimation(int i) {
            this.mBackImage.setVisibility(4);
            ValueAnimator ofInt = ValueAnimator.ofInt(i, 360);
            this.mFaceInputProgressAnimation = ofInt;
            ofInt.setDuration(1200L);
            this.mFaceInputProgressAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.13
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                    NewFaceEnrollFragment.this.mFaceInputProgressView.updateFaceInputProgress(intValue);
                    if (intValue == 360) {
                        NewFaceEnrollFragment.this.startFaceInputSuccessAnimation();
                    }
                }
            });
            this.mFaceInputProgressAnimation.start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startFaceInputSuccessAnimation() {
            this.mFaceInputTitle.setVisibility(4);
            Bitmap bitmap = this.mFaceInputCameraPreview.getBitmap();
            if (bitmap != null) {
                this.mFaceEnrollSuccessView.updateFaceBitmap(bitmap);
                ValueAnimator ofFloat = ValueAnimator.ofFloat(this.mCameraPrviewCircleRadius, 0.0f);
                this.mFaceCameraPreviewCoverAnimation = ofFloat;
                ofFloat.setDuration(480L);
                this.mFaceCameraPreviewCoverAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.14
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        NewFaceEnrollFragment.this.mFaceEnrollSuccessView.drawFaceSuccessView(((Float) valueAnimator.getAnimatedValue()).floatValue());
                    }
                });
                this.mFaceCameraPreviewCoverAnimation.addListener(new Animator.AnimatorListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.15
                    @Override // android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override // android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                    }

                    @Override // android.animation.Animator.AnimatorListener
                    public void onAnimationRepeat(Animator animator) {
                    }

                    @Override // android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animator) {
                        NewFaceEnrollFragment.this.playVideo(new Surface(NewFaceEnrollFragment.this.mFaceInputSuccessVideo.getSurfaceTexture()), false, R.raw.miui_facea_input_success);
                    }
                });
                this.mFaceCameraPreviewCoverAnimation.start();
            }
            this.mFaceUnlockManager.runOnFaceUnlockWorkerThread(new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput$NewFaceEnrollFragment$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MiuiNormalCameraFaceInput.NewFaceEnrollFragment.this.lambda$startFaceInputSuccessAnimation$2();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateFaceErrorInfo(int i) {
            if (i == 3 || i == 8) {
                finish();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateFaceHelpInfo(int i) {
            int i2;
            if (i == 33) {
                i2 = R.string.structure_face_data_input_error_keep_inface;
            } else if (i == 1000) {
                i2 = R.string.face_data_input_camera_fail;
            } else if (i != 1001) {
                switch (i) {
                    case 4:
                        i2 = R.string.face_unlock_quality;
                        break;
                    case 5:
                        i2 = R.string.face_unlock_not_found;
                        break;
                    case 6:
                        i2 = R.string.face_unlock_close_screen;
                        break;
                    case 7:
                        i2 = R.string.face_unlock_stay_away_screen;
                        break;
                    case 8:
                        i2 = R.string.face_unlock_offset_left;
                        break;
                    case 9:
                        i2 = R.string.face_unlock_offset_top;
                        break;
                    case 10:
                        i2 = R.string.face_unlock_offset_right;
                        break;
                    case 11:
                        i2 = R.string.face_unlock_offset_bottom;
                        break;
                    default:
                        switch (i) {
                            case 15:
                                i2 = R.string.face_unlock_rotated_left;
                                break;
                            case 16:
                                i2 = R.string.face_unlock_rise;
                                break;
                            case 17:
                                i2 = R.string.face_unlock_rotated_right;
                                break;
                            case 18:
                                i2 = R.string.face_unlock_down;
                                break;
                            default:
                                switch (i) {
                                    case 21:
                                        i2 = R.string.face_unlock_reveal_eye;
                                        break;
                                    case 22:
                                        i2 = R.string.face_unlock_open_eye;
                                        break;
                                    case 23:
                                        i2 = R.string.face_unlock_reveal_mouth;
                                        break;
                                    default:
                                        i2 = R.string.structure_face_data_input_error_keep_inface;
                                        break;
                                }
                        }
                }
            } else {
                i2 = R.string.structure_face_data_input_error_keep_inface;
                this.mCameraPreviewCoverdView.refreshCameraView(this.mCameraPrviewCircleRadius, true, true, true);
                this.mFaceInputCameraPreview.setOpaque(true);
                this.mFaceInputCameraPreview.setAlpha(1.0f);
            }
            if (i2 != 0) {
                this.mFaceInputTitle.setText(i2);
                if (System.currentTimeMillis() - this.mLastAnnounceTime > 1300) {
                    setContentDescription(getResources().getString(i2));
                    this.mLastAnnounceTime = System.currentTimeMillis();
                }
            }
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

        @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            setThemeRes(R.style.Theme_Dark_Settings_NoTitle);
            this.mMainHandler = new Handler(Looper.getMainLooper());
            this.mFaceUnlockManager = KeyguardSettingsFaceUnlockManager.getInstance(this.mActivity);
            Activity activity = this.mActivity;
            KeyguardSettingsFaceUnlockUtils.setFaceEnrollViewStatus(activity, activity.getWindow());
            if (MiuiKeyguardSettingsUtils.isInFullWindowGestureMode(getActivity().getApplicationContext())) {
                this.mActivity.getWindow().clearFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
            }
            boolean booleanExtra = getIntent().getBooleanExtra("for_face_enroll_from_normal", false);
            this.mFaceEnrollFromNormal = booleanExtra;
            if (booleanExtra) {
                return;
            }
            finish();
        }

        @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
        public void onDestroy() {
            super.onDestroy();
            this.mFinishFaceSuggestion = false;
            this.mFaceEnrollSucceed = false;
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
            this.mFaceInputSuccessVideo = (TextureView) this.mContentView.findViewById(R.id.miui_face_input_success_video);
            this.mFaceEnrollSuccessView = (FaceEnrollSuccessView) this.mContentView.findViewById(R.id.miui_face_input_success_image);
            this.mFaceInputSuccessTitle = (TextView) this.mContentView.findViewById(R.id.miui_face_input_success_title);
            this.mFaceInputSuccessMsg = (TextView) this.mContentView.findViewById(R.id.miui_face_input_success_message);
            Button button = (Button) this.mContentView.findViewById(R.id.miui_face_input_nextorsuccess_button);
            this.mFaceInputNextOrSuccessButton = button;
            KeyguardSettingsFaceUnlockUtils.createCardFolmeTouchStyle(button);
            this.mBackImage.setContentDescription(getString(R.string.structure_face_data_introduction_back));
            this.mFaceInputNextOrSuccessButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (!NewFaceEnrollFragment.this.mFaceEnrollSucceed) {
                        if (NewFaceEnrollFragment.this.mHasClickStartAddBtn) {
                            return;
                        }
                        NewFaceEnrollFragment.this.mHasClickStartAddBtn = true;
                        NewFaceEnrollFragment.this.enterRiskWarningDialog();
                        return;
                    }
                    if (KeyguardSettingsFaceUnlockUtils.isDeviceProvisioned(NewFaceEnrollFragment.this.mActivity)) {
                        Intent intent = new Intent();
                        intent.setClassName("com.android.settings", "com.android.settings.faceunlock.MiuiFaceDataManage");
                        intent.putExtra("input_facedata_need_skip_password", true);
                        NewFaceEnrollFragment.this.startActivity(intent);
                    } else {
                        NewFaceEnrollFragment.this.setResult(-1);
                    }
                    NewFaceEnrollFragment.this.finish();
                }
            });
            this.mBackImage.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    NewFaceEnrollFragment.this.mActivity.setResult(0);
                    NewFaceEnrollFragment.this.finish();
                }
            });
            this.mFaceInputTitle.setText(R.string.face_data_input_title);
            this.mEnrollSuggestionDetectEnd = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_suggesiton_detect_image_end);
            this.mEnrollSuggestionDetectRepeatEnd = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_suggesiton_detect_image_repeta_end);
            this.mCameraPrviewCircleRadius = getResources().getDimensionPixelSize(R.dimen.miui_face_enroll_circle_radius);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mBackImage.getLayoutParams();
            marginLayoutParams.topMargin = KeyguardSettingsFaceUnlockUtils.getStatusBarHeight(this.mActivity);
            this.mBackImage.setLayoutParams(marginLayoutParams);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mFaceInputCameraPreview.getLayoutParams();
            layoutParams.width = getResources().getDimensionPixelSize(R.dimen.miui_face_input_cameraview_width);
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.miui_face_input_cameraview_height);
            this.mFaceInputCameraPreview.setLayoutParams(layoutParams);
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
            resetFaceEnrollAnimation();
            if (this.mFinishFaceSuggestion) {
                this.mFaceUnlockManager.runOnFaceUnlockWorkerThread(new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput$NewFaceEnrollFragment$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        MiuiNormalCameraFaceInput.NewFaceEnrollFragment.this.lambda$onPause$0();
                    }
                });
                this.mMainHandler.removeCallbacks(this.mStartFaceInput);
            }
            this.mMainHandler.removeCallbacks(this.mStartFaceSuggestionVideo);
            finish();
        }

        @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            this.mActivity.getWindow().getDecorView().setSystemUiVisibility(4866);
            this.mFaceUnlockManager = KeyguardSettingsFaceUnlockManager.getInstance(this.mActivity);
            this.mMainHandler.postDelayed(new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.6
                @Override // java.lang.Runnable
                public void run() {
                    NewFaceEnrollFragment newFaceEnrollFragment = NewFaceEnrollFragment.this;
                    newFaceEnrollFragment.setContentDescription(newFaceEnrollFragment.getResources().getString(R.string.face_data_input_title));
                }
            }, 5000L);
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

        public void playVideo(Surface surface, final boolean z, int i) {
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
                this.mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.19
                    @Override // android.media.MediaPlayer.OnPreparedListener
                    public void onPrepared(MediaPlayer mediaPlayer2) {
                        NewFaceEnrollFragment.this.mMediaPlayer.start();
                    }
                });
                this.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput.NewFaceEnrollFragment.20
                    @Override // android.media.MediaPlayer.OnCompletionListener
                    public void onCompletion(MediaPlayer mediaPlayer2) {
                        if (z) {
                            return;
                        }
                        NewFaceEnrollFragment.this.updateFaceUnlockSuccessView();
                    }
                });
            } catch (Exception unused) {
            }
        }

        public void startEnrollFace() {
            this.mFaceUnlockManager = KeyguardSettingsFaceUnlockManager.getInstance(this.mActivity);
            if (this.mFaceInputCameraPreview.getSurfaceTexture() != null) {
                this.mFaceInputCameraPreviewSurfaceTexture = this.mFaceInputCameraPreview.getSurfaceTexture();
                this.mFaceInputTitle.setText(R.string.face_data_input_camera_ok);
                this.mFaceEnrollSucceed = false;
                final RectF rectF = new RectF(20.0f, 110.0f, 460.0f, 550.0f);
                this.mFaceUnlockManager.runOnFaceUnlockWorkerThread(new Runnable() { // from class: com.android.settings.faceunlock.MiuiNormalCameraFaceInput$NewFaceEnrollFragment$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        MiuiNormalCameraFaceInput.NewFaceEnrollFragment.this.lambda$startEnrollFace$1(rectF);
                    }
                });
            }
        }
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", NewFaceEnrollFragment.class.getName());
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
