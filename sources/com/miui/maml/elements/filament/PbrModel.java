package com.miui.maml.elements.filament;

import android.view.View;
import com.google.android.filament.Engine;
import com.miui.maml.ResourceManager;
import com.miui.maml.ScreenElementRoot;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public abstract class PbrModel {
    protected Engine mEngine;
    protected ResourceManager mResMgr;

    public PbrModel(Element element, ResourceManager resourceManager, ScreenElementRoot screenElementRoot) {
        this.mResMgr = resourceManager;
    }

    public abstract void finish();

    public abstract void init(View view);

    public void pause() {
    }

    public abstract void render(long j);

    public void resume() {
    }
}
