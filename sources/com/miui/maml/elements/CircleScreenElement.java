package com.miui.maml.elements;

import android.graphics.Canvas;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.elements.GeometryScreenElement;
import com.miui.maml.elements.ScreenElement;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.maml.folme.AnimatedTarget;
import com.miui.maml.folme.PropertyWrapper;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class CircleScreenElement extends GeometryScreenElement {
    public static final AnimatedProperty R;
    private PropertyWrapper mRadiusProperty;

    static {
        AnimatedProperty animatedProperty = new AnimatedProperty("r") { // from class: com.miui.maml.elements.CircleScreenElement.1
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                if (animatedScreenElement instanceof CircleScreenElement) {
                    return (float) ((CircleScreenElement) animatedScreenElement).mRadiusProperty.getValue();
                }
                return 0.0f;
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof CircleScreenElement) {
                    ((CircleScreenElement) animatedScreenElement).mRadiusProperty.setValue(f);
                }
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof CircleScreenElement) {
                    ((CircleScreenElement) animatedScreenElement).mRadiusProperty.setVelocity(f);
                }
            }
        };
        R = animatedProperty;
        AnimatedProperty.sPropertyNameMap.put("r", animatedProperty);
        AnimatedTarget.sPropertyMap.put(1001, animatedProperty);
        AnimatedTarget.sPropertyTypeMap.put(animatedProperty, 1001);
    }

    public CircleScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        Expression build = Expression.build(screenElementRoot.getVariables(), getAttr(element, "r"));
        this.mAlign = ScreenElement.Align.CENTER;
        this.mAlignV = ScreenElement.AlignV.CENTER;
        this.mRadiusProperty = new PropertyWrapper(this.mName + ".r", screenElementRoot.getVariables(), build, isInFolmeMode(), 0.0d);
    }

    private final float getRadius() {
        return scale(this.mRadiusProperty.getValue());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.GeometryScreenElement, com.miui.maml.elements.AnimatedScreenElement
    public void initProperties() {
        super.initProperties();
        this.mRadiusProperty.init();
    }

    @Override // com.miui.maml.elements.GeometryScreenElement
    protected void onDraw(Canvas canvas, GeometryScreenElement.DrawMode drawMode) {
        float radius = getRadius();
        if (drawMode == GeometryScreenElement.DrawMode.STROKE_OUTER) {
            radius += this.mWeight / 2.0f;
        } else if (drawMode == GeometryScreenElement.DrawMode.STROKE_INNER) {
            radius -= this.mWeight / 2.0f;
        }
        canvas.drawCircle(0.0f, 0.0f, radius, ((GeometryScreenElement) this).mPaint);
    }
}
