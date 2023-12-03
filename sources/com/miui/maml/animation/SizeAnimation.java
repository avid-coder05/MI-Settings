package com.miui.maml.animation;

import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.elements.ScreenElement;
import java.util.Iterator;
import miui.provider.MiCloudSmsCmd;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class SizeAnimation extends BaseAnimation {
    private double mMaxH;
    private double mMaxW;

    public SizeAnimation(Element element, ScreenElement screenElement) {
        super(element, "Size", new String[]{MiCloudSmsCmd.TYPE_WIPE, "h"}, screenElement);
        Iterator<BaseAnimation.AnimationItem> it = this.mItems.iterator();
        while (it.hasNext()) {
            BaseAnimation.AnimationItem next = it.next();
            if (next.get(0) > this.mMaxW) {
                this.mMaxW = next.get(0);
            }
            if (next.get(1) > this.mMaxH) {
                this.mMaxH = next.get(1);
            }
        }
    }

    public final double getHeight() {
        return getCurValue(1);
    }

    public final double getWidth() {
        return getCurValue(0);
    }
}
