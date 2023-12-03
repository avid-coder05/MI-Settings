package com.miui.maml;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.util.Log;
import com.miui.maml.RendererController;
import java.lang.ref.WeakReference;

/* loaded from: classes2.dex */
public class RendererCore {
    private boolean mCleaned;
    private MultipleRenderable mMultipleRenderable;
    private WeakReference<OnReleaseListener> mOnReleaseListener;
    private boolean mReleased;
    private ScreenElementRoot mRoot;

    /* loaded from: classes2.dex */
    public interface OnReleaseListener {
        boolean OnRendererCoreReleased(RendererCore rendererCore);
    }

    public RendererCore(ScreenElementRoot screenElementRoot) {
        MultipleRenderable multipleRenderable = new MultipleRenderable();
        this.mMultipleRenderable = multipleRenderable;
        this.mRoot = screenElementRoot;
        screenElementRoot.setRenderControllerRenderable(multipleRenderable);
        this.mRoot.selfInit();
        this.mRoot.attachToVsync();
        this.mRoot.requestUpdate();
    }

    public synchronized void addRenderable(RendererController.IRenderable iRenderable) {
        if (this.mCleaned) {
            return;
        }
        this.mMultipleRenderable.add(iRenderable);
        Log.d("RendererCore", "add: " + iRenderable + " size:" + this.mMultipleRenderable.size());
        this.mRoot.selfResume();
        this.mReleased = false;
    }

    public void cleanUp() {
        this.mCleaned = true;
        Log.d("RendererCore", "cleanUp: " + toString());
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.detachFromVsync();
            this.mRoot.selfFinish();
            this.mRoot = null;
        }
    }

    protected void finalize() throws Throwable {
        cleanUp();
        super.finalize();
    }

    public ScreenElementRoot getRoot() {
        return this.mRoot;
    }

    public synchronized void pauseRenderable(RendererController.IRenderable iRenderable) {
        if (this.mCleaned) {
            return;
        }
        if (this.mMultipleRenderable.pause(iRenderable) == 0) {
            Log.d("RendererCore", "self pause: " + toString());
            this.mRoot.selfPause();
        }
    }

    public synchronized void removeRenderable(RendererController.IRenderable iRenderable) {
        WeakReference<OnReleaseListener> weakReference;
        if (this.mCleaned) {
            return;
        }
        this.mMultipleRenderable.remove(iRenderable);
        Log.d("RendererCore", "remove: " + iRenderable + " size:" + this.mMultipleRenderable.size());
        if (this.mMultipleRenderable.size() == 0) {
            this.mRoot.selfPause();
            if (!this.mReleased && (weakReference = this.mOnReleaseListener) != null && weakReference.get() != null && this.mOnReleaseListener.get().OnRendererCoreReleased(this)) {
                cleanUp();
            }
            this.mReleased = true;
        }
    }

    public void render(Canvas canvas) {
        if (this.mCleaned) {
            return;
        }
        this.mRoot.render(canvas);
    }

    public synchronized void resumeRenderable(RendererController.IRenderable iRenderable) {
        if (this.mCleaned) {
            return;
        }
        this.mMultipleRenderable.resume(iRenderable);
        Log.d("RendererCore", "self resume: " + toString());
        this.mRoot.selfResume();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.setColorFilter(colorFilter);
        }
    }

    public void setOnReleaseListener(OnReleaseListener onReleaseListener) {
        this.mOnReleaseListener = new WeakReference<>(onReleaseListener);
    }
}
