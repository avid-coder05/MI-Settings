package com.android.settings.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.android.settings.R;

/* loaded from: classes2.dex */
public class ScreenEnhanceEngineTopView extends RelativeLayout {
    private String currentPositionColor;
    private LinearLayout mShowPagePositionLayout;
    private TextView mSummaryTextView;
    private ViewFlipper mViewFlipper;
    private String otherPositionColor;
    private float radius;

    /* loaded from: classes2.dex */
    private static class MyVideoView extends TextureView {
        private MediaPlayer mediaPlayer;
        private Uri uri;

        public MyVideoView(Context context, int i) {
            super(context);
            this.mediaPlayer = null;
            this.uri = null;
            this.uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + i);
            mInit();
        }

        public MyVideoView(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.mediaPlayer = null;
            this.uri = null;
        }

        public MyVideoView(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
            this.mediaPlayer = null;
            this.uri = null;
        }

        private void mInit() {
            MediaPlayer mediaPlayer = new MediaPlayer();
            this.mediaPlayer = mediaPlayer;
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.android.settings.widget.ScreenEnhanceEngineTopView.MyVideoView.1
                @Override // android.media.MediaPlayer.OnPreparedListener
                public void onPrepared(MediaPlayer mediaPlayer2) {
                    MyVideoView.this.mediaPlayer.start();
                }
            });
            setSurfaceTextureListener(new TextureView.SurfaceTextureListener() { // from class: com.android.settings.widget.ScreenEnhanceEngineTopView.MyVideoView.2
                @Override // android.view.TextureView.SurfaceTextureListener
                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                    try {
                        MyVideoView.this.mediaPlayer.setDataSource(MyVideoView.this.getContext(), MyVideoView.this.uri);
                        MyVideoView.this.mediaPlayer.setSurface(new Surface(surfaceTexture));
                        MyVideoView.this.mediaPlayer.setLooping(true);
                        MyVideoView.this.mediaPlayer.prepareAsync();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override // android.view.TextureView.SurfaceTextureListener
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    if (MyVideoView.this.mediaPlayer.isPlaying()) {
                        MyVideoView.this.mediaPlayer.stop();
                        MyVideoView.this.mediaPlayer.release();
                        return false;
                    }
                    return false;
                }

                @Override // android.view.TextureView.SurfaceTextureListener
                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
                }

                @Override // android.view.TextureView.SurfaceTextureListener
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class MyViewFilpperOnTouchListener implements View.OnTouchListener {
        private int currentViewPosition;
        private Animation leftInAnimation;
        private Animation leftOutAnimation;
        private Animation rightInAnimation;
        private Animation rightOutAnimation;
        private float startX;

        private MyViewFilpperOnTouchListener() {
            this.leftInAnimation = null;
            this.leftOutAnimation = null;
            this.rightInAnimation = null;
            this.rightOutAnimation = null;
            this.startX = 0.0f;
            this.currentViewPosition = 0;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (ScreenEnhanceEngineTopView.this.mViewFlipper.getChildCount() <= 1) {
                return false;
            }
            if (this.leftInAnimation == null || this.leftOutAnimation == null || this.rightInAnimation == null || this.rightOutAnimation == null) {
                int width = ScreenEnhanceEngineTopView.this.mViewFlipper.getWidth();
                float f = -width;
                this.leftInAnimation = new TranslateAnimation(f, 0.0f, 0.0f, 0.0f);
                float f2 = width;
                this.leftOutAnimation = new TranslateAnimation(0.0f, f2, 0.0f, 0.0f);
                this.rightInAnimation = new TranslateAnimation(f2, 0.0f, 0.0f, 0.0f);
                this.rightOutAnimation = new TranslateAnimation(0.0f, f, 0.0f, 0.0f);
                this.leftInAnimation.setDuration(500L);
                this.leftOutAnimation.setDuration(500L);
                this.rightInAnimation.setDuration(500L);
                this.rightOutAnimation.setDuration(500L);
            }
            if ((motionEvent.getAction() & 255) == 0) {
                this.startX = motionEvent.getX();
            } else if ((motionEvent.getAction() & 255) == 1 || (motionEvent.getAction() & 255) == 3) {
                ((GradientDrawable) ScreenEnhanceEngineTopView.this.mShowPagePositionLayout.getChildAt(this.currentViewPosition).getBackground()).setColor(Color.parseColor(ScreenEnhanceEngineTopView.this.otherPositionColor));
                if (motionEvent.getX() - this.startX > 60.0f) {
                    ScreenEnhanceEngineTopView.this.mViewFlipper.setInAnimation(this.leftInAnimation);
                    ScreenEnhanceEngineTopView.this.mViewFlipper.setOutAnimation(this.leftOutAnimation);
                    this.currentViewPosition--;
                    ScreenEnhanceEngineTopView.this.mViewFlipper.showPrevious();
                }
                if (this.startX - motionEvent.getX() > 60.0f) {
                    ScreenEnhanceEngineTopView.this.mViewFlipper.setInAnimation(this.rightInAnimation);
                    ScreenEnhanceEngineTopView.this.mViewFlipper.setOutAnimation(this.rightOutAnimation);
                    this.currentViewPosition++;
                    ScreenEnhanceEngineTopView.this.mViewFlipper.showNext();
                }
                int i = this.currentViewPosition;
                if (i < 0) {
                    this.currentViewPosition = ScreenEnhanceEngineTopView.this.mViewFlipper.getChildCount() - 1;
                } else if (i >= ScreenEnhanceEngineTopView.this.mViewFlipper.getChildCount()) {
                    this.currentViewPosition = 0;
                }
                ((GradientDrawable) ScreenEnhanceEngineTopView.this.mShowPagePositionLayout.getChildAt(this.currentViewPosition).getBackground()).setColor(Color.parseColor(ScreenEnhanceEngineTopView.this.currentPositionColor));
            }
            return true;
        }
    }

    /* loaded from: classes2.dex */
    private static class TopViewOutlineProvider extends ViewOutlineProvider {
        private float mRadius;

        public TopViewOutlineProvider(float f) {
            this.mRadius = f;
        }

        @Override // android.view.ViewOutlineProvider
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(new Rect(0, 0, view.getWidth(), view.getHeight()), this.mRadius);
        }
    }

    public ScreenEnhanceEngineTopView(Context context) {
        super(context);
        this.currentPositionColor = "#D3D3D3";
        this.otherPositionColor = "#00FFFFFF";
        this.radius = 50.0f;
        mInit();
    }

    public ScreenEnhanceEngineTopView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.currentPositionColor = "#D3D3D3";
        this.otherPositionColor = "#00FFFFFF";
        this.radius = 50.0f;
        mInit();
    }

    public ScreenEnhanceEngineTopView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.currentPositionColor = "#D3D3D3";
        this.otherPositionColor = "#00FFFFFF";
        this.radius = 50.0f;
        mInit();
    }

    private void mInit() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.screen_enhance_engine_top_view, this);
        ViewFlipper viewFlipper = (ViewFlipper) inflate.findViewById(R.id.view_filpper);
        this.mViewFlipper = viewFlipper;
        viewFlipper.setOnTouchListener(new MyViewFilpperOnTouchListener());
        this.mShowPagePositionLayout = (LinearLayout) inflate.findViewById(R.id.show_page_position_layout);
        this.mSummaryTextView = (TextView) inflate.findViewById(R.id.screen_enhance_engine_top_summary);
        this.mViewFlipper.post(new Runnable() { // from class: com.android.settings.widget.ScreenEnhanceEngineTopView.1
            @Override // java.lang.Runnable
            public void run() {
                int width = ScreenEnhanceEngineTopView.this.mViewFlipper.getWidth();
                ScreenEnhanceEngineTopView.this.mViewFlipper.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (width * 0.5d)));
            }
        });
    }

    public void addImageView(int i) {
        if (this.mViewFlipper.getChildCount() >= 5) {
            return;
        }
        ImageView imageView = new ImageView(getContext());
        imageView.setOutlineProvider(new TopViewOutlineProvider(this.radius));
        imageView.setClipToOutline(true);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -1);
        layoutParams.gravity = 17;
        imageView.setImageResource(i);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        this.mViewFlipper.addView(imageView, layoutParams);
    }

    public void addVideoView(int i) {
        if (this.mViewFlipper.getChildCount() >= 5) {
            return;
        }
        MyVideoView myVideoView = new MyVideoView(getContext(), i);
        myVideoView.setOutlineProvider(new TopViewOutlineProvider(this.radius));
        myVideoView.setClipToOutline(true);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -1);
        layoutParams.gravity = 17;
        this.mViewFlipper.addView(myVideoView, layoutParams);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mViewFlipper.getChildCount() <= 1) {
            this.mShowPagePositionLayout.setVisibility(8);
            setClickable(false);
            setFocusable(false);
            return;
        }
        this.mShowPagePositionLayout.setVisibility(0);
        setClickable(true);
        setFocusable(true);
        int childCount = this.mShowPagePositionLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (i >= this.mViewFlipper.getChildCount()) {
                this.mShowPagePositionLayout.getChildAt(i).setVisibility(8);
            } else {
                ((GradientDrawable) this.mShowPagePositionLayout.getChildAt(i).getBackground()).setColor(Color.parseColor(this.otherPositionColor));
            }
        }
        ((GradientDrawable) this.mShowPagePositionLayout.getChildAt(0).getBackground()).setColor(Color.parseColor(this.currentPositionColor));
    }

    public void replaceImageView(int i, int i2) {
        if (i < this.mViewFlipper.getChildCount() && (this.mViewFlipper.getChildAt(i) instanceof ImageView)) {
            ((ImageView) this.mViewFlipper.getChildAt(i)).setImageResource(i2);
        }
    }

    public void setRadius(float f) {
        this.radius = f;
    }

    public void setSummaryText(int i) {
        if (i == 0) {
            this.mSummaryTextView.setVisibility(8);
            return;
        }
        String string = getContext().getResources().getString(i);
        this.mSummaryTextView.setVisibility(0);
        this.mSummaryTextView.setText(string);
    }
}
