package com.miui.maml.animation;

import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.elements.ScreenElement;
import miui.provider.Weather;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class SourcesAnimation extends PositionAnimation {
    private String mCurrentSrc;

    /* loaded from: classes2.dex */
    public static class Source extends BaseAnimation.AnimationItem {
        public String mSrc;

        public Source(BaseAnimation baseAnimation, Element element) {
            super(baseAnimation, element);
            this.mSrc = element.getAttribute(Weather.AQIInfo.SRC);
        }
    }

    public SourcesAnimation(Element element, ScreenElement screenElement) {
        super(element, "Source", screenElement);
    }

    public final String getSrc() {
        return this.mCurrentSrc;
    }

    @Override // com.miui.maml.animation.BaseAnimation
    protected BaseAnimation.AnimationItem onCreateItem(BaseAnimation baseAnimation, Element element) {
        return new Source(baseAnimation, element);
    }

    @Override // com.miui.maml.animation.BaseAnimation
    protected void onTick(BaseAnimation.AnimationItem animationItem, BaseAnimation.AnimationItem animationItem2, float f) {
        if (animationItem2 == null) {
            setCurValue(0, 0.0d);
            setCurValue(1, 0.0d);
            return;
        }
        setCurValue(0, animationItem2.get(0));
        setCurValue(1, animationItem2.get(1));
        this.mCurrentSrc = ((Source) animationItem2).mSrc;
    }
}
