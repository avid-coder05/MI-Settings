package com.miui.maml.animation;

import com.miui.maml.elements.ScreenElement;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class RotationAnimation extends BaseAnimation {
    public RotationAnimation(Element element, ScreenElement screenElement) {
        super(element, "Rotation", "angle", screenElement);
    }

    public final float getAngle() {
        return (float) getCurValue(0);
    }
}
