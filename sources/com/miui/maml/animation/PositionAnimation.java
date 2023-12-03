package com.miui.maml.animation;

import com.miui.maml.elements.ScreenElement;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class PositionAnimation extends BaseAnimation {
    public PositionAnimation(Element element, ScreenElement screenElement) {
        this(element, "Position", screenElement);
    }

    public PositionAnimation(Element element, String str, ScreenElement screenElement) {
        super(element, str, new String[]{"x", "y"}, screenElement);
    }

    public final double getX() {
        return getCurValue(0);
    }

    public final double getY() {
        return getCurValue(1);
    }
}
