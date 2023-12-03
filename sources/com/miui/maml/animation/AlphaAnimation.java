package com.miui.maml.animation;

import android.text.TextUtils;
import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.elements.ScreenElement;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class AlphaAnimation extends BaseAnimation {
    private int mDelayValue;

    public AlphaAnimation(Element element, ScreenElement screenElement) {
        super(element, "Alpha", "a", screenElement);
        String attribute = element.getAttribute("delayValue");
        if (!TextUtils.isEmpty(attribute)) {
            try {
                this.mDelayValue = Integer.parseInt(attribute);
                return;
            } catch (NumberFormatException unused) {
                return;
            }
        }
        BaseAnimation.AnimationItem item = getItem(0);
        if (item != null) {
            this.mDelayValue = (int) item.get(0);
        }
    }

    public final int getAlpha() {
        return (int) getCurValue(0);
    }

    @Override // com.miui.maml.animation.BaseAnimation
    protected double getDefaultValue() {
        return 255.0d;
    }

    @Override // com.miui.maml.animation.BaseAnimation
    protected double getDelayValue(int i) {
        return this.mDelayValue;
    }
}
