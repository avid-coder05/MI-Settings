package com.miui.maml.elements;

import android.graphics.Canvas;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class MirrorScreenElement extends AnimatedScreenElement {
    private boolean mMirrorTranslation;
    private ScreenElement mTarget;
    private String mTargetName;

    public MirrorScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mTargetName = element.getAttribute("target");
        this.mMirrorTranslation = Boolean.parseBoolean(element.getAttribute("mirrorTranslation"));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void doRender(Canvas canvas) {
        ScreenElement screenElement = this.mTarget;
        if (screenElement != null) {
            if (this.mMirrorTranslation && (screenElement instanceof AnimatedScreenElement)) {
                ((AnimatedScreenElement) screenElement).doRenderWithTranslation(canvas);
            } else {
                screenElement.doRender(canvas);
            }
        }
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void init() {
        super.init();
        ScreenElement findElement = this.mRoot.findElement(this.mTargetName);
        this.mTarget = findElement;
        if (findElement == null) {
            Log.e("MirrorScreenElement", "the target does not exist: " + this.mTargetName);
        }
    }
}
