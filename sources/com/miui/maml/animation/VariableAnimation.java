package com.miui.maml.animation;

import com.miui.maml.elements.ScreenElement;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class VariableAnimation extends BaseAnimation {
    public VariableAnimation(Element element, ScreenElement screenElement) {
        super(element, "AniFrame", screenElement);
    }

    public final double getValue() {
        return getCurValue(0);
    }
}
