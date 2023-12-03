package com.miui.maml.animation;

import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.elements.ScreenElement;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class ScaleAnimation extends BaseAnimation {
    private double mDelayValueX;
    private double mDelayValueY;

    public ScaleAnimation(Element element, ScreenElement screenElement) {
        super(element, "Item", new String[]{"value", "x", "y"}, screenElement);
        BaseAnimation.AnimationItem item = getItem(0);
        this.mDelayValueX = getItemX(item);
        this.mDelayValueY = getItemY(item);
    }

    private double getItemX(BaseAnimation.AnimationItem animationItem) {
        if (animationItem == null) {
            return 1.0d;
        }
        return animationItem.get(animationItem.attrExists(0) ? 0 : 1);
    }

    private double getItemY(BaseAnimation.AnimationItem animationItem) {
        if (animationItem == null) {
            return 1.0d;
        }
        return animationItem.get(animationItem.attrExists(0) ? 0 : 2);
    }

    @Override // com.miui.maml.animation.BaseAnimation
    protected double getDelayValue(int i) {
        return (i == 0 || i == 1) ? this.mDelayValueX : this.mDelayValueY;
    }

    public final double getScaleX() {
        return getCurValue(1);
    }

    public final double getScaleY() {
        return getCurValue(2);
    }

    @Override // com.miui.maml.animation.BaseAnimation
    protected void onTick(BaseAnimation.AnimationItem animationItem, BaseAnimation.AnimationItem animationItem2, float f) {
        if (animationItem == null && animationItem2 == null) {
            return;
        }
        double itemX = getItemX(animationItem);
        double d = f;
        setCurValue(1, itemX + ((getItemX(animationItem2) - itemX) * d));
        double itemY = getItemY(animationItem);
        setCurValue(2, itemY + ((getItemY(animationItem2) - itemY) * d));
    }
}
