package com.miui.maml.elements;

import android.graphics.Canvas;
import com.miui.maml.ScreenElementRoot;
import java.util.Collection;
import miuix.animation.controller.AnimState;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public abstract class StateElement extends ScreenElement {
    public StateElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void doRender(Canvas canvas) {
    }

    public abstract AnimState getAnimState(String str);

    public abstract Collection<String> getProperties();

    @Override // com.miui.maml.elements.ScreenElement
    public boolean isVisible() {
        return false;
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void tick(long j) {
    }
}
