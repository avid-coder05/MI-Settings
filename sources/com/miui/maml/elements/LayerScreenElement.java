package com.miui.maml.elements;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import com.miui.maml.ScreenElementRoot;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class LayerScreenElement extends ViewHolderScreenElement {
    private LayerView mView;

    /* loaded from: classes2.dex */
    private class LayerView extends View {
        public LayerView(Context context) {
            super(context);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            LayerScreenElement.this.doRender(canvas);
            LayerScreenElement.this.mController.doneRender();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return LayerScreenElement.this.getRoot().onTouch(motionEvent) || super.onTouchEvent(motionEvent);
        }
    }

    public LayerScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mView = new LayerView(screenElementRoot.getContext().mContext);
    }

    @Override // com.miui.maml.elements.ViewHolderScreenElement
    protected View getView() {
        return this.mView;
    }
}
