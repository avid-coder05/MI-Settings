package com.miui.maml.elements;

import com.miui.maml.ScreenElementRoot;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class AutoScaleElementGroup extends ElementGroup {
    private float mInitRawHeight;
    private float mInitRawWidth;

    public AutoScaleElementGroup(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement
    public float getScaleX() {
        float widthRaw = getWidthRaw();
        float f = this.mInitRawWidth;
        return (f <= 0.0f || widthRaw <= 0.0f) ? super.getScaleX() : (widthRaw / f) * super.getScaleX();
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement
    public float getScaleY() {
        float heightRaw = getHeightRaw();
        float f = this.mInitRawHeight;
        return (f <= 0.0f || heightRaw <= 0.0f) ? super.getScaleY() : (heightRaw / f) * super.getScaleY();
    }

    @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void init() {
        super.init();
        this.mInitRawWidth = getWidthRaw();
        this.mInitRawHeight = getHeightRaw();
    }
}
