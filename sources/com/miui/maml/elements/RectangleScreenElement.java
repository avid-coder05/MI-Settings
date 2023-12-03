package com.miui.maml.elements;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;
import com.android.settings.recommend.PageIndexManager;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.elements.GeometryScreenElement;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.maml.folme.AnimatedTarget;
import com.miui.maml.folme.PropertyWrapper;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class RectangleScreenElement extends GeometryScreenElement {
    public static final AnimatedProperty CORNER_RADIUS_X;
    public static final AnimatedProperty CORNER_RADIUS_Y;
    private float mCornerRadiusX;
    private float mCornerRadiusY;
    private PropertyWrapper mRXProperty;
    private PropertyWrapper mRYProperty;

    static {
        AnimatedProperty animatedProperty = new AnimatedProperty("cornerRadiusX") { // from class: com.miui.maml.elements.RectangleScreenElement.1
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                if (animatedScreenElement instanceof RectangleScreenElement) {
                    return (float) ((RectangleScreenElement) animatedScreenElement).mRXProperty.getValue();
                }
                return 0.0f;
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof RectangleScreenElement) {
                    ((RectangleScreenElement) animatedScreenElement).mRXProperty.setValue(f);
                }
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof RectangleScreenElement) {
                    ((RectangleScreenElement) animatedScreenElement).mRXProperty.setVelocity(f);
                }
            }
        };
        CORNER_RADIUS_X = animatedProperty;
        AnimatedProperty animatedProperty2 = new AnimatedProperty("cornerRadiusY") { // from class: com.miui.maml.elements.RectangleScreenElement.2
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                if (animatedScreenElement instanceof RectangleScreenElement) {
                    return (float) ((RectangleScreenElement) animatedScreenElement).mRYProperty.getValue();
                }
                return 0.0f;
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof RectangleScreenElement) {
                    ((RectangleScreenElement) animatedScreenElement).mRYProperty.setValue(f);
                }
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof RectangleScreenElement) {
                    ((RectangleScreenElement) animatedScreenElement).mRYProperty.setVelocity(f);
                }
            }
        };
        CORNER_RADIUS_Y = animatedProperty2;
        AnimatedProperty.sPropertyNameMap.put("cornerRadiusX", animatedProperty);
        AnimatedTarget.sPropertyMap.put(1004, animatedProperty);
        AnimatedTarget.sPropertyTypeMap.put(animatedProperty, 1006);
        AnimatedProperty.sPropertyNameMap.put("cornerRadiusY", animatedProperty2);
        AnimatedTarget.sPropertyMap.put(1005, animatedProperty2);
        AnimatedTarget.sPropertyTypeMap.put(animatedProperty2, Integer.valueOf((int) PageIndexManager.PAGE_BACKUP_AND_RESET));
    }

    public RectangleScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        resolveCornerRadius(element);
    }

    private void resolveCornerRadius(Element element) {
        Expression[] buildMultiple = Expression.buildMultiple(getVariables(), element.getAttribute("cornerRadiusExp"));
        if (buildMultiple == null) {
            String[] split = getAttr(element, "cornerRadius").split(",");
            try {
                if (split.length < 1) {
                    return;
                }
                if (split.length == 1) {
                    float scale = scale(Float.parseFloat(split[0]));
                    this.mCornerRadiusY = scale;
                    this.mCornerRadiusX = scale;
                } else {
                    this.mCornerRadiusX = scale(Float.parseFloat(split[0]));
                    this.mCornerRadiusY = scale(Float.parseFloat(split[1]));
                }
            } catch (NumberFormatException unused) {
                Log.w("RectangleScreenElement", "illegal number format of cornerRadius.");
            }
        }
        Expression expression = (buildMultiple == null || buildMultiple.length <= 0) ? null : buildMultiple[0];
        Expression expression2 = (buildMultiple == null || buildMultiple.length <= 1) ? expression : buildMultiple[1];
        this.mRXProperty = new PropertyWrapper(this.mName + ".cornerRadiusX", getVariables(), expression, isInFolmeMode(), descale(this.mCornerRadiusX));
        this.mRYProperty = new PropertyWrapper(this.mName + ".cornerRadiusY", getVariables(), expression2, isInFolmeMode(), descale(this.mCornerRadiusY));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.GeometryScreenElement, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void doTick(long j) {
        super.doTick(j);
        this.mCornerRadiusX = scale(this.mRXProperty.getValue());
        this.mCornerRadiusY = scale(this.mRYProperty.getValue());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.GeometryScreenElement, com.miui.maml.elements.AnimatedScreenElement
    public void initProperties() {
        super.initProperties();
        this.mRXProperty.init();
        this.mRYProperty.init();
    }

    @Override // com.miui.maml.elements.GeometryScreenElement
    protected void onDraw(Canvas canvas, GeometryScreenElement.DrawMode drawMode) {
        float width = getWidth();
        float height = getHeight();
        float left = getLeft(0.0f, width);
        float top = getTop(0.0f, height);
        if (width <= 0.0f) {
            width = 0.0f;
        }
        float f = width + left;
        if (height <= 0.0f) {
            height = 0.0f;
        }
        float f2 = height + top;
        if (drawMode == GeometryScreenElement.DrawMode.STROKE_OUTER) {
            float f3 = this.mWeight;
            left -= f3 / 2.0f;
            top -= f3 / 2.0f;
            f += f3 / 2.0f;
            f2 += f3 / 2.0f;
        } else if (drawMode == GeometryScreenElement.DrawMode.STROKE_INNER) {
            float f4 = this.mWeight;
            left += f4 / 2.0f;
            top += f4 / 2.0f;
            f -= f4 / 2.0f;
            f2 -= f4 / 2.0f;
        }
        float f5 = f;
        float f6 = f2;
        float f7 = left;
        float f8 = top;
        if (this.mCornerRadiusX <= 0.0f || this.mCornerRadiusY <= 0.0f) {
            canvas.drawRect(f7, f8, f5, f6, ((GeometryScreenElement) this).mPaint);
        } else {
            canvas.drawRoundRect(new RectF(f7, f8, f5, f6), this.mCornerRadiusX, this.mCornerRadiusY, ((GeometryScreenElement) this).mPaint);
        }
    }
}
