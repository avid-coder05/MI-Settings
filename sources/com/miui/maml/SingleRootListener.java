package com.miui.maml;

import android.view.MotionEvent;
import com.miui.maml.RendererController;
import java.lang.ref.WeakReference;
import java.util.Objects;

/* loaded from: classes2.dex */
public class SingleRootListener implements RendererController.Listener {
    private WeakReference<RendererController.IRenderable> mRenderable;
    private ScreenElementRoot mRoot;

    public SingleRootListener(ScreenElementRoot screenElementRoot, RendererController.IRenderable iRenderable) {
        setRoot(screenElementRoot);
        setRenderable(iRenderable);
    }

    @Override // com.miui.maml.RendererController.IRenderable
    public void doRender() {
        WeakReference<RendererController.IRenderable> weakReference = this.mRenderable;
        RendererController.IRenderable iRenderable = weakReference != null ? weakReference.get() : null;
        if (iRenderable != null) {
            iRenderable.doRender();
        }
    }

    @Override // com.miui.maml.RendererController.Listener
    public void finish() {
        this.mRoot.finish();
    }

    @Override // com.miui.maml.RendererController.ISelfUpdateRenderable
    public void forceUpdate() {
        WeakReference<RendererController.IRenderable> weakReference = this.mRenderable;
        RendererController.IRenderable iRenderable = weakReference != null ? weakReference.get() : null;
        if (iRenderable == null || !(iRenderable instanceof RendererController.ISelfUpdateRenderable)) {
            return;
        }
        ((RendererController.ISelfUpdateRenderable) iRenderable).forceUpdate();
    }

    @Override // com.miui.maml.RendererController.Listener
    public void init() {
        this.mRoot.init();
    }

    @Override // com.miui.maml.RendererController.Listener
    public void onHover(MotionEvent motionEvent) {
        this.mRoot.onHover(motionEvent);
        motionEvent.recycle();
    }

    @Override // com.miui.maml.RendererController.Listener
    public void onTouch(MotionEvent motionEvent) {
        this.mRoot.onTouch(motionEvent);
        motionEvent.recycle();
    }

    @Override // com.miui.maml.RendererController.Listener
    public void pause() {
        this.mRoot.pause();
    }

    @Override // com.miui.maml.RendererController.Listener
    public void resume() {
        this.mRoot.resume();
    }

    public void setRenderable(RendererController.IRenderable iRenderable) {
        this.mRenderable = iRenderable != null ? new WeakReference<>(iRenderable) : null;
    }

    public void setRoot(ScreenElementRoot screenElementRoot) {
        Objects.requireNonNull(screenElementRoot, "root is null");
        this.mRoot = screenElementRoot;
    }

    @Override // com.miui.maml.RendererController.Listener
    public void tick(long j) {
        this.mRoot.tick(j);
    }

    @Override // com.miui.maml.RendererController.ISelfUpdateRenderable
    public void triggerUpdate() {
        WeakReference<RendererController.IRenderable> weakReference = this.mRenderable;
        RendererController.IRenderable iRenderable = weakReference != null ? weakReference.get() : null;
        if (iRenderable == null || !(iRenderable instanceof RendererController.ISelfUpdateRenderable)) {
            return;
        }
        ((RendererController.ISelfUpdateRenderable) iRenderable).triggerUpdate();
    }
}
