package com.miui.maml;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.miui.maml.MamlDrawable;
import com.miui.maml.RendererController;
import com.miui.maml.util.Utils;
import java.util.Objects;

/* loaded from: classes2.dex */
public class FancyDrawable extends MamlDrawable implements RendererController.IRenderable {
    private boolean mPaused;
    private Drawable mQuietDrawable;
    private RendererCore mRendererCore;
    private Drawable mStartDrawable;
    private boolean mTimeOut;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Object mPauseLock = new Object();
    private Runnable mRenderTimeout = new Runnable() { // from class: com.miui.maml.FancyDrawable.1
        @Override // java.lang.Runnable
        public void run() {
            FancyDrawable.this.mTimeOut = true;
            FancyDrawable.this.doPause();
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class FancyDrawableState extends MamlDrawable.MamlDrawableState {
        RendererCore mRendererCore;

        public FancyDrawableState(RendererCore rendererCore) {
            this.mRendererCore = rendererCore;
        }

        @Override // com.miui.maml.MamlDrawable.MamlDrawableState
        protected MamlDrawable createDrawable() {
            return new FancyDrawable(this.mRendererCore);
        }
    }

    public FancyDrawable(RendererCore rendererCore) {
        init(rendererCore);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doPause() {
        synchronized (this.mPauseLock) {
            if (this.mPaused) {
                return;
            }
            logd("doPause: ");
            this.mPaused = true;
            this.mRendererCore.pauseRenderable(this);
        }
    }

    private void doResume() {
        synchronized (this.mPauseLock) {
            if (this.mPaused) {
                logd("doResume: ");
                this.mPaused = false;
                this.mRendererCore.resumeRenderable(this);
            }
        }
    }

    private void init(RendererCore rendererCore) {
        Objects.requireNonNull(rendererCore);
        this.mState = new FancyDrawableState(rendererCore);
        this.mRendererCore = rendererCore;
        rendererCore.addRenderable(this);
        setIntrinsicSize((int) this.mRendererCore.getRoot().getWidth(), (int) this.mRendererCore.getRoot().getHeight());
        ScreenContext context = this.mRendererCore.getRoot().getContext();
        Drawable drawable = context.mResourceManager.getDrawable(context.mContext.getResources(), "quietImage.png");
        this.mQuietDrawable = drawable;
        if (drawable != null) {
            Drawable mutate = drawable.mutate();
            this.mQuietDrawable = mutate;
            mutate.setBounds(0, 0, mutate.getIntrinsicWidth(), this.mQuietDrawable.getIntrinsicHeight());
        }
        Drawable drawable2 = context.mResourceManager.getDrawable(context.mContext.getResources(), "startImage.png");
        this.mStartDrawable = drawable2;
        if (drawable2 != null) {
            Drawable mutate2 = drawable2.mutate();
            this.mStartDrawable = mutate2;
            mutate2.setBounds(0, 0, mutate2.getIntrinsicWidth(), this.mStartDrawable.getIntrinsicHeight());
        }
    }

    private void logd(CharSequence charSequence) {
        Log.d("FancyDrawable", ((Object) charSequence) + "  [" + toString() + "]");
    }

    @Override // com.miui.maml.MamlDrawable
    public void cleanUp() {
        logd("cleanUp: ");
        this.mRendererCore.removeRenderable(this);
    }

    @Override // com.miui.maml.RendererController.IRenderable
    public void doRender() {
        this.mHandler.removeCallbacks(this.mRenderTimeout);
        this.mHandler.postDelayed(this.mRenderTimeout, 100L);
        this.mHandler.post(this.mInvalidateSelf);
    }

    @Override // com.miui.maml.MamlDrawable
    protected void drawIcon(Canvas canvas) {
        Drawable drawable;
        this.mHandler.removeCallbacks(this.mRenderTimeout);
        if (this.mTimeOut) {
            doResume();
            this.mTimeOut = false;
        }
        try {
            int save = canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            canvas.scale(this.mWidth / this.mIntrinsicWidth, this.mHeight / this.mIntrinsicHeight, 0.0f, 0.0f);
            if (Utils.getVariableNumber("useQuietImage", this.mRendererCore.getRoot().getVariables()) <= 0.0d || (drawable = this.mQuietDrawable) == null) {
                this.mRendererCore.render(canvas);
            } else {
                drawable.draw(canvas);
            }
            canvas.restoreToCount(save);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.MamlDrawable
    public void finalize() throws Throwable {
        cleanUp();
        super.finalize();
    }

    @Override // com.miui.maml.MamlDrawable, android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mIntrinsicHeight;
    }

    @Override // com.miui.maml.MamlDrawable, android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mIntrinsicWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        Drawable drawable = this.mQuietDrawable;
        if (drawable != null) {
            drawable.setAlpha(i);
        }
        Drawable drawable2 = this.mStartDrawable;
        if (drawable2 != null) {
            drawable2.setAlpha(i);
        }
    }

    @Override // com.miui.maml.MamlDrawable
    public void setBadgeInfo(Drawable drawable, Rect rect) {
        if (rect == null || (rect.left >= 0 && rect.top >= 0 && rect.width() <= this.mIntrinsicWidth && rect.height() <= this.mIntrinsicHeight)) {
            this.mBadgeDrawable = drawable;
            this.mBadgeLocation = rect;
            MamlDrawable.MamlDrawableState mamlDrawableState = this.mState;
            mamlDrawableState.mStateBadgeDrawable = drawable;
            mamlDrawableState.mStateBadgeLocation = rect;
            return;
        }
        throw new IllegalArgumentException("Badge location " + rect + " not in badged drawable bounds " + new Rect(0, 0, this.mIntrinsicWidth, this.mIntrinsicHeight));
    }

    @Override // com.miui.maml.MamlDrawable, android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
        Log.d("FancyDrawable", "setColorFilter");
        Drawable drawable = this.mQuietDrawable;
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
        }
        Drawable drawable2 = this.mStartDrawable;
        if (drawable2 != null) {
            drawable2.setColorFilter(colorFilter);
        }
        Drawable drawable3 = this.mBadgeDrawable;
        if (drawable3 != null) {
            drawable3.setColorFilter(colorFilter);
        }
        RendererCore rendererCore = this.mRendererCore;
        if (rendererCore != null) {
            rendererCore.setColorFilter(colorFilter);
        }
    }
}
