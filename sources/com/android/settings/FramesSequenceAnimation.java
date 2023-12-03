package com.android.settings;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import java.lang.ref.SoftReference;
import miui.vip.VipService;

/* loaded from: classes.dex */
public class FramesSequenceAnimation {
    private boolean mAnimationBackgroundChange;
    private AnimationListener mAnimationListener;
    private boolean mAnimationPlayed;
    private boolean mAnimationPlaying;
    private boolean mAnimationShouldPlay;
    private Bitmap mBitmap;
    private int mCurrFrameIndex;
    private int mDelayedMillis;
    private int[] mFrames;
    private Handler mHandler;
    private SoftReference<ImageView> mImageViewSoftReference;
    private BitmapFactory.Options mOptions;

    /* loaded from: classes.dex */
    public interface AnimationListener {
        void onAnimationBackgroundChange();

        void onAnimationPlayed();

        void onAnimationStarted();

        void onAnimationStopped();
    }

    public FramesSequenceAnimation(Context context, ImageView imageView, int i, int i2) {
        Resources resources = context.getResources();
        this.mHandler = new Handler();
        this.mCurrFrameIndex = -1;
        this.mDelayedMillis = VipService.VIP_SERVICE_FAILURE / i2;
        int[] framesByResId = getFramesByResId(resources, i);
        this.mFrames = framesByResId;
        if (framesByResId.length <= 0) {
            Log.i("FramesSequenceAnimation", "can't get frames from resource, framesResId is " + i);
        }
        SoftReference<ImageView> softReference = new SoftReference<>(imageView);
        this.mImageViewSoftReference = softReference;
        softReference.get().setImageResource(this.mFrames[0]);
        this.mAnimationPlayed = false;
        this.mAnimationPlaying = false;
        this.mAnimationShouldPlay = false;
        BitmapDrawable bitmapDrawable = (BitmapDrawable) this.mImageViewSoftReference.get().getDrawable();
        Bitmap bitmap = (bitmapDrawable == null ? (BitmapDrawable) context.getResources().getDrawable(R.drawable.keysettings_launcher) : bitmapDrawable).getBitmap();
        this.mBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        BitmapFactory.Options options = new BitmapFactory.Options();
        this.mOptions = options;
        options.inBitmap = this.mBitmap;
        options.inMutable = true;
        options.inSampleSize = 1;
    }

    private int[] getFramesByResId(Resources resources, int i) {
        TypedArray obtainTypedArray = resources.obtainTypedArray(i);
        int length = obtainTypedArray.length();
        int[] iArr = new int[length];
        for (int i2 = 0; i2 < length; i2++) {
            iArr[i2] = obtainTypedArray.getResourceId(i2, 0);
        }
        obtainTypedArray.recycle();
        return iArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getNextFrame() {
        int i = this.mCurrFrameIndex + 1;
        this.mCurrFrameIndex = i;
        int[] iArr = this.mFrames;
        if (i >= iArr.length) {
            this.mCurrFrameIndex = 0;
            this.mAnimationPlayed = true;
        }
        int i2 = this.mCurrFrameIndex;
        if (i2 >= iArr.length * 0.65d) {
            this.mAnimationBackgroundChange = true;
        }
        return iArr[i2];
    }

    public void setAnimationListener(AnimationListener animationListener) {
        this.mAnimationListener = animationListener;
    }

    public synchronized void start() {
        this.mAnimationShouldPlay = true;
        if (this.mAnimationPlaying) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.android.settings.FramesSequenceAnimation.1
            @Override // java.lang.Runnable
            public void run() {
                Bitmap bitmap;
                ImageView imageView = (ImageView) FramesSequenceAnimation.this.mImageViewSoftReference.get();
                if (!FramesSequenceAnimation.this.mAnimationShouldPlay || imageView == null) {
                    FramesSequenceAnimation.this.mAnimationPlaying = false;
                    if (FramesSequenceAnimation.this.mAnimationListener != null) {
                        FramesSequenceAnimation.this.mAnimationListener.onAnimationStopped();
                    }
                    Log.i("FramesSequenceAnimation", "animation stop");
                    return;
                }
                FramesSequenceAnimation.this.mAnimationPlaying = true;
                FramesSequenceAnimation.this.mHandler.postDelayed(this, FramesSequenceAnimation.this.mDelayedMillis);
                if (imageView.isShown()) {
                    int nextFrame = FramesSequenceAnimation.this.getNextFrame();
                    if (FramesSequenceAnimation.this.mAnimationPlayed) {
                        FramesSequenceAnimation.this.mAnimationListener.onAnimationPlayed();
                        FramesSequenceAnimation.this.mAnimationPlayed = false;
                    }
                    if (FramesSequenceAnimation.this.mAnimationBackgroundChange) {
                        FramesSequenceAnimation.this.mAnimationListener.onAnimationBackgroundChange();
                        FramesSequenceAnimation.this.mAnimationBackgroundChange = false;
                    }
                    if (FramesSequenceAnimation.this.mBitmap == null) {
                        imageView.setImageResource(nextFrame);
                        return;
                    }
                    try {
                        bitmap = BitmapFactory.decodeResource(imageView.getResources(), nextFrame, FramesSequenceAnimation.this.mOptions);
                    } catch (Exception e) {
                        e.printStackTrace();
                        bitmap = null;
                    }
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        return;
                    }
                    imageView.setImageResource(nextFrame);
                    FramesSequenceAnimation.this.mBitmap.recycle();
                    FramesSequenceAnimation.this.mBitmap = null;
                }
            }
        });
        AnimationListener animationListener = this.mAnimationListener;
        if (animationListener != null) {
            animationListener.onAnimationStarted();
        }
    }

    public synchronized void stop() {
        this.mAnimationPlaying = false;
        this.mAnimationShouldPlay = false;
        AnimationListener animationListener = this.mAnimationListener;
        if (animationListener != null) {
            animationListener.onAnimationStopped();
        }
    }
}
