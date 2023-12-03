package com.miui.maml;

import android.util.Log;
import com.miui.maml.RendererController;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public class MultipleRenderable implements RendererController.IRenderable {
    private int mActiveCount;
    private ArrayList<RenderableInfo> mList = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class RenderableInfo {
        public boolean paused;
        public WeakReference<RendererController.IRenderable> r;

        public RenderableInfo(RendererController.IRenderable iRenderable) {
            this.r = new WeakReference<>(iRenderable);
        }
    }

    private RenderableInfo find(RendererController.IRenderable iRenderable) {
        int size = this.mList.size();
        for (int i = 0; i < size; i++) {
            RenderableInfo renderableInfo = this.mList.get(i);
            if (renderableInfo.r.get() == iRenderable) {
                return renderableInfo;
            }
        }
        return null;
    }

    private int setPause(RendererController.IRenderable iRenderable, boolean z) {
        Log.d("MultipleRenderable", "setPause: " + z + " " + iRenderable);
        RenderableInfo find = find(iRenderable);
        if (find == null) {
            return this.mActiveCount;
        }
        if (find.paused != z) {
            find.paused = z;
            int i = this.mActiveCount;
            this.mActiveCount = z ? i - 1 : i + 1;
        }
        return this.mActiveCount;
    }

    public synchronized void add(RendererController.IRenderable iRenderable) {
        if (find(iRenderable) != null) {
            return;
        }
        Log.d("MultipleRenderable", "add: " + iRenderable);
        this.mList.add(new RenderableInfo(iRenderable));
        this.mActiveCount = this.mActiveCount + 1;
    }

    @Override // com.miui.maml.RendererController.IRenderable
    public synchronized void doRender() {
        this.mActiveCount = 0;
        for (int size = this.mList.size() - 1; size >= 0; size--) {
            RenderableInfo renderableInfo = this.mList.get(size);
            if (!renderableInfo.paused) {
                RendererController.IRenderable iRenderable = renderableInfo.r.get();
                if (iRenderable != null) {
                    iRenderable.doRender();
                    this.mActiveCount++;
                } else {
                    this.mList.remove(size);
                }
            }
        }
    }

    public synchronized int pause(RendererController.IRenderable iRenderable) {
        return setPause(iRenderable, true);
    }

    public synchronized void remove(RendererController.IRenderable iRenderable) {
        int size = this.mList.size();
        if (size == 0) {
            return;
        }
        for (int i = size - 1; i >= 0; i--) {
            RenderableInfo renderableInfo = this.mList.get(i);
            RendererController.IRenderable iRenderable2 = renderableInfo.r.get();
            if (iRenderable2 == null || iRenderable2 == iRenderable) {
                if (!renderableInfo.paused) {
                    this.mActiveCount--;
                }
                this.mList.remove(i);
                Log.d("MultipleRenderable", "remove: " + iRenderable2);
            }
        }
    }

    public synchronized int resume(RendererController.IRenderable iRenderable) {
        return setPause(iRenderable, false);
    }

    public synchronized int size() {
        return this.mList.size();
    }
}
