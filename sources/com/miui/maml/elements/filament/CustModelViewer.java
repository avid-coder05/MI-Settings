package com.miui.maml.elements.filament;

import android.util.ArrayMap;
import android.view.SurfaceView;
import android.view.TextureView;
import com.google.android.filament.Engine;
import com.google.android.filament.Renderer;
import com.google.android.filament.SwapChain;
import com.google.android.filament.android.DisplayHelper;
import com.google.android.filament.android.UiHelper;
import java.util.Iterator;
import java.util.Map;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: CustModelViewer.kt */
/* loaded from: classes2.dex */
public final class CustModelViewer {
    private DisplayHelper displayHelper;
    @NotNull
    private final Engine engine;
    @Nullable

    /* renamed from: final  reason: not valid java name */
    private CustFinal f0final;
    @Nullable
    private ArrayMap<String, CustFrameBuffer> frameBuffers;
    private final Renderer renderer;
    private SurfaceView surfaceView;
    private SwapChain swapChain;
    private TextureView textureView;
    private final UiHelper uiHelper;

    /* compiled from: CustModelViewer.kt */
    /* loaded from: classes2.dex */
    public final class SurfaceCallback implements UiHelper.RendererCallback {
        public SurfaceCallback() {
        }
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public CustModelViewer(@NotNull SurfaceView surfaceView, @NotNull Engine engine) {
        this(engine);
        Intrinsics.checkNotNullParameter(surfaceView, "surfaceView");
        Intrinsics.checkNotNullParameter(engine, "engine");
        this.surfaceView = surfaceView;
        this.displayHelper = new DisplayHelper(surfaceView.getContext());
        this.uiHelper.setRenderCallback(new SurfaceCallback());
        this.uiHelper.attachTo(surfaceView);
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public CustModelViewer(@NotNull TextureView textureView, @NotNull Engine engine) {
        this(engine);
        Intrinsics.checkNotNullParameter(textureView, "textureView");
        Intrinsics.checkNotNullParameter(engine, "engine");
        this.textureView = textureView;
        this.displayHelper = new DisplayHelper(textureView.getContext());
        this.uiHelper.setRenderCallback(new SurfaceCallback());
        this.uiHelper.attachTo(textureView);
    }

    public CustModelViewer(@NotNull Engine engine) {
        Intrinsics.checkNotNullParameter(engine, "engine");
        this.engine = engine;
        UiHelper uiHelper = new UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK);
        this.uiHelper = uiHelper;
        Renderer createRenderer = engine.createRenderer();
        Intrinsics.checkNotNullExpressionValue(createRenderer, "engine.createRenderer()");
        this.renderer = createRenderer;
        Renderer.ClearOptions clearOptions = createRenderer.getClearOptions();
        Intrinsics.checkNotNullExpressionValue(clearOptions, "renderer.clearOptions");
        clearOptions.clear = true;
        createRenderer.setClearOptions(clearOptions);
        uiHelper.setOpaque(false);
    }

    public final void destroy() {
        this.uiHelper.detach();
        this.engine.destroyRenderer(this.renderer);
    }

    public final void render(long j) {
        if (this.uiHelper.isReadyToRender()) {
            Renderer renderer = this.renderer;
            SwapChain swapChain = this.swapChain;
            Intrinsics.checkNotNull(swapChain);
            if (renderer.beginFrame(swapChain, j)) {
                ArrayMap<String, CustFrameBuffer> arrayMap = this.frameBuffers;
                if (arrayMap != null) {
                    Iterator<Map.Entry<String, CustFrameBuffer>> it = arrayMap.entrySet().iterator();
                    while (it.hasNext()) {
                        it.next().getValue().doFrame(this.renderer);
                    }
                }
                CustFinal custFinal = this.f0final;
                if (custFinal != null) {
                    custFinal.doFrame(this.renderer);
                }
                this.renderer.endFrame();
            }
        }
    }

    public final void setFinal(@Nullable CustFinal custFinal) {
        this.f0final = custFinal;
    }

    public final void setFrameBuffers(@Nullable ArrayMap<String, CustFrameBuffer> arrayMap) {
        this.frameBuffers = arrayMap;
    }
}
