package com.miui.maml.elements.filament;

import android.view.View;
import com.google.android.filament.Engine;
import com.google.android.filament.RenderTarget;
import com.google.android.filament.Texture;
import com.miui.maml.ResourceManager;
import com.miui.maml.ScreenElementRoot;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class CustFrameBuffer extends CustElement {
    private Texture mColor;
    private RenderTarget mTarget;

    public CustFrameBuffer(Element element, ResourceManager resourceManager, ScreenElementRoot screenElementRoot) {
        super(element, resourceManager, screenElementRoot);
    }

    public Texture getTexture() {
        return this.mColor;
    }

    @Override // com.miui.maml.elements.filament.CustElement
    public void init(Engine engine, ResourceManager resourceManager, View view) {
        super.init(engine, resourceManager, view);
        this.mFilamentView.setPostProcessingEnabled(false);
    }

    @Override // com.miui.maml.elements.filament.CustElement
    public void onDestroy(Engine engine) {
        Texture texture = this.mColor;
        if (texture != null) {
            engine.destroyTexture(texture);
            this.mColor = null;
        }
        RenderTarget renderTarget = this.mTarget;
        if (renderTarget != null) {
            engine.destroyRenderTarget(renderTarget);
            this.mTarget = null;
        }
        super.onDestroy(engine);
    }
}
