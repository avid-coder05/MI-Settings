package com.miui.maml.component;

import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.os.Build;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.SurfaceHolder;
import com.miui.maml.RendererController;
import com.miui.maml.ResourceManager;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.util.HideSdkDependencyUtils;

/* loaded from: classes2.dex */
public class MamlSurface implements RendererController.IRenderable {
    private WallpaperService.Engine mEngine;
    private volatile boolean mFinished;
    private ScreenElementRoot mRoot;
    private SurfaceHolder mSurfaceHolder;

    private void finish() {
        if (this.mFinished) {
            return;
        }
        this.mFinished = true;
        this.mEngine = null;
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.selfFinish();
            this.mRoot.detachFromVsync();
            this.mRoot.getVariables().reset();
            ResourceManager resourceManager = this.mRoot.getContext().mResourceManager;
            ResourceManager.clear();
            this.mRoot = null;
        }
    }

    @Override // com.miui.maml.RendererController.IRenderable
    public void doRender() {
        SurfaceHolder surfaceHolder;
        Canvas lockCanvas;
        if (this.mRoot == null || (surfaceHolder = this.mSurfaceHolder) == null) {
            return;
        }
        try {
            try {
                try {
                    lockCanvas = surfaceHolder.lockCanvas();
                } catch (Throwable th) {
                    if (0 != 0) {
                        try {
                            this.mSurfaceHolder.unlockCanvasAndPost(null);
                        } catch (Exception e) {
                            Log.e("MamlSurface", "unlockCanvasAndPost exception.", e);
                        }
                    }
                    throw th;
                }
            } catch (Exception e2) {
                Log.e("MamlSurface", "render exception.", e2);
                if (0 != 0) {
                    this.mSurfaceHolder.unlockCanvasAndPost(null);
                }
            } catch (OutOfMemoryError e3) {
                Log.e("MamlSurface", "render oom error.", e3);
                if (0 != 0) {
                    this.mSurfaceHolder.unlockCanvasAndPost(null);
                }
            }
        } catch (Exception e4) {
            Log.e("MamlSurface", "unlockCanvasAndPost exception.", e4);
        }
        if (lockCanvas != null) {
            lockCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            this.mRoot.render(lockCanvas);
            this.mSurfaceHolder.unlockCanvasAndPost(lockCanvas);
            this.mRoot.doneRender();
            return;
        }
        Log.d("MamlSurface", ":( fail to lock canvas.");
        if (lockCanvas != null) {
            try {
                this.mSurfaceHolder.unlockCanvasAndPost(lockCanvas);
            } catch (Exception e5) {
                Log.e("MamlSurface", "unlockCanvasAndPost exception.", e5);
            }
        }
    }

    protected void finalize() throws Throwable {
        finish();
        super.finalize();
    }

    public SurfaceControl getParentSurfaceControl() {
        WallpaperService.Engine engine;
        if (Build.VERSION.SDK_INT != 29 || (engine = this.mEngine) == null) {
            return null;
        }
        return HideSdkDependencyUtils.SurfaceControl_getInstance_with_engine(engine);
    }

    public SurfaceHolder getSurfaceHolder() {
        return this.mSurfaceHolder;
    }
}
