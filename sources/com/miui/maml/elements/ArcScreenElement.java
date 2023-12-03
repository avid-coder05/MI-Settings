package com.miui.maml.elements;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.data.Variables;
import com.miui.maml.elements.GeometryScreenElement;
import com.miui.maml.elements.ScreenElement;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class ArcScreenElement extends GeometryScreenElement {
    private float mAngle;
    private Expression mAngleExp;
    private Path mArcPath;
    private boolean mClose;
    RectF mOvalRect;
    private float mSweep;
    private Expression mSweepExp;

    public ArcScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mArcPath = new Path();
        this.mOvalRect = new RectF();
        Variables variables = screenElementRoot.getVariables();
        this.mAngleExp = Expression.build(variables, getAttr(element, "startAngle"));
        this.mSweepExp = Expression.build(variables, getAttr(element, "sweep"));
        this.mClose = Boolean.parseBoolean(getAttr(element, "close"));
        this.mAlign = ScreenElement.Align.CENTER;
        this.mAlignV = ScreenElement.AlignV.CENTER;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.GeometryScreenElement, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void doTick(long j) {
        super.doTick(j);
        if (isVisible()) {
            this.mRoot.getVariables();
            this.mAngle = (float) this.mAngleExp.evaluate();
            this.mSweep = (float) this.mSweepExp.evaluate();
        }
    }

    @Override // com.miui.maml.elements.GeometryScreenElement
    protected void onDraw(Canvas canvas, GeometryScreenElement.DrawMode drawMode) {
        float width = getWidth();
        float height = getHeight();
        float f = 0.0f - (width / 2.0f);
        float f2 = 0.0f - (height / 2.0f);
        this.mArcPath.reset();
        RectF rectF = this.mOvalRect;
        rectF.left = f;
        rectF.top = f2;
        rectF.right = f + width;
        rectF.bottom = f2 + height;
        if (Math.abs(this.mSweep) >= 360.0f) {
            canvas.drawOval(this.mOvalRect, ((GeometryScreenElement) this).mPaint);
            return;
        }
        if (this.mClose) {
            this.mArcPath.moveTo(this.mOvalRect.centerX(), this.mOvalRect.centerY());
        }
        this.mArcPath.arcTo(this.mOvalRect, this.mAngle, this.mSweep, !this.mClose);
        if (this.mClose) {
            this.mArcPath.close();
        }
        canvas.drawPath(this.mArcPath, ((GeometryScreenElement) this).mPaint);
    }
}
