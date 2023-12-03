package com.miui.maml.elements.video;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.miui.maml.component.MamlSurface;
import com.miui.maml.util.HideSdkDependencyUtils;
import java.lang.ref.WeakReference;

/* loaded from: classes2.dex */
public class SurfaceVideoView extends BaseVideoView {
    private int mFormat;
    private Handler mHandler;
    private int mHeight;
    private WeakReference<MamlSurface> mMamlSurfaceRef;
    private int mSubLayer;
    private SurfaceControl mSurfaceControl;
    private Runnable mUpdateRunnable;
    private int mVisibility;
    private int mWidth;
    private float mX;
    private float mY;

    public SurfaceVideoView(Context context, MamlSurface mamlSurface) {
        super(context);
        this.mFormat = -2;
        this.mSubLayer = -2;
        this.mVisibility = 0;
        this.mX = 0.0f;
        this.mY = 0.0f;
        this.mUpdateRunnable = new Runnable() { // from class: com.miui.maml.elements.video.SurfaceVideoView.1
            @Override // java.lang.Runnable
            public void run() {
                SurfaceVideoView.this.updateSurfaceInternal();
            }
        };
        this.mSurface = HideSdkDependencyUtils.Surface_getInstance();
        this.mMamlSurfaceRef = new WeakReference<>(mamlSurface);
        this.mHandler = new Handler();
    }

    private void initSize() {
        Display defaultDisplay = ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getRealSize(point);
        int width = getWidth();
        this.mWidth = width;
        if (width <= 0) {
            this.mWidth = point.x;
        }
        int height = getHeight();
        this.mHeight = height;
        if (height <= 0) {
            this.mHeight = point.y;
        }
        updateSize();
    }

    private void postUpdateRunnable() {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacks(this.mUpdateRunnable);
            this.mHandler.post(this.mUpdateRunnable);
        }
    }

    private void updateSize() {
        int i;
        int i2;
        if (this.mScaleMode != 3 || (i = this.mVideoHeight) <= 0 || (i2 = this.mVideoWidth) <= 0) {
            return;
        }
        int i3 = this.mHeight;
        int i4 = i2 * i3;
        int i5 = this.mWidth;
        if (i4 < i5 * i) {
            this.mWidth = (i3 * i2) / i;
        } else if (i3 * i2 > i5 * i) {
            this.mHeight = (i5 * i) / i2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSurfaceInternal() {
        if (this.mSurfaceControl != null) {
            HideSdkDependencyUtils.SurfaceControl_openTransaction();
            try {
                if (this.mVisibility == 0) {
                    HideSdkDependencyUtils.SurfaceControl_show(this.mSurfaceControl);
                } else {
                    HideSdkDependencyUtils.SurfaceControl_hide(this.mSurfaceControl);
                }
                HideSdkDependencyUtils.SurfaceControl_setLayer(this.mSurfaceControl, this.mSubLayer);
                HideSdkDependencyUtils.SurfaceControl_setPosition(this.mSurfaceControl, this.mX, this.mY);
                HideSdkDependencyUtils.SurfaceControl_setBufferSize(this.mSurfaceControl, this.mWidth, this.mHeight);
                HideSdkDependencyUtils.Surface_copyFrom(this.mSurface, this.mSurfaceControl);
            } finally {
                HideSdkDependencyUtils.SurfaceControl_closeTransaction();
            }
        }
    }

    @Override // com.miui.maml.elements.video.BaseVideoView
    protected void addSurfaceHolderCallback() {
        SurfaceHolder surfaceHolder;
        MamlSurface mamlSurface = this.mMamlSurfaceRef.get();
        if (mamlSurface == null || (surfaceHolder = mamlSurface.getSurfaceHolder()) == null) {
            return;
        }
        surfaceHolder.addCallback(this.mSHCallback);
        onSurfaceCreated(surfaceHolder);
    }

    @Override // com.miui.maml.elements.video.BaseVideoView
    protected void onSurfaceCreated(SurfaceHolder surfaceHolder) {
        initSize();
        if (surfaceHolder != null && surfaceHolder.getSurface().isValid() && this.mSurfaceControl == null) {
            MamlSurface mamlSurface = this.mMamlSurfaceRef.get();
            this.mSurfaceControl = HideSdkDependencyUtils.SurfaceControl_getInstance_with_params(surfaceHolder.getSurface(), mamlSurface != null ? mamlSurface.getParentSurfaceControl() : null, "SurfaceVideoView", this.mWidth, this.mHeight, this.mFormat);
            updateSurfaceInternal();
        }
    }

    @Override // com.miui.maml.elements.video.BaseVideoView
    protected void onSurfaceDestroyed() {
        Log.d("SurfaceVideoView", "onSurfaceDestroyed");
        releaseMedia(true);
        SurfaceControl surfaceControl = this.mSurfaceControl;
        if (surfaceControl != null) {
            surfaceControl.release();
            this.mSurfaceControl = null;
        }
        Surface surface = this.mSurface;
        if (surface != null) {
            surface.release();
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacks(this.mUpdateRunnable);
            this.mHandler = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.video.BaseVideoView
    public void setFormat(int i) {
        this.mFormat = i;
    }

    @Override // android.view.View
    public void setLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams != null) {
            this.mWidth = layoutParams.width;
            this.mHeight = layoutParams.height;
            updateSize();
            postUpdateRunnable();
        }
    }

    @Override // android.view.SurfaceView, android.view.View
    public void setVisibility(int i) {
        if (this.mVisibility != i) {
            this.mVisibility = i;
            postUpdateRunnable();
        }
    }

    @Override // android.view.View
    public void setX(float f) {
        if (this.mX != f) {
            this.mX = f;
            postUpdateRunnable();
        }
    }

    @Override // android.view.View
    public void setY(float f) {
        if (this.mY != f) {
            this.mY = f;
            postUpdateRunnable();
        }
    }

    @Override // android.view.SurfaceView
    public void setZOrderOnTop(boolean z) {
        if (z) {
            this.mSubLayer = 1;
        } else {
            this.mSubLayer = -2;
        }
        postUpdateRunnable();
    }

    @Override // com.miui.maml.elements.video.BaseVideoView
    protected void updateVideoSize() {
        updateSize();
        updateSurfaceInternal();
    }
}
