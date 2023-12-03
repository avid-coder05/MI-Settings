package com.miui.maml.elements;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.text.TextUtils;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.data.Variables;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.maml.folme.AnimatedTarget;
import com.miui.maml.folme.PropertyWrapper;
import com.miui.maml.shader.ShadersElement;
import com.miui.maml.util.ColorParser;
import com.miui.maml.util.Utils;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public abstract class GeometryScreenElement extends AnimatedScreenElement {
    public static final AnimatedProperty.AnimatedColorProperty FILL_COLOR;
    public static final AnimatedProperty.AnimatedColorProperty STROKE_COLOR;
    public static final AnimatedProperty STROKE_WEIGHT;
    private int mFillColor;
    protected ColorParser mFillColorParser;
    private PropertyWrapper mFillColorProperty;
    protected ShadersElement mFillShadersElement;
    protected Paint mPaint;
    private final DrawMode mStrokeAlign;
    private int mStrokeColor;
    protected ColorParser mStrokeColorParser;
    private PropertyWrapper mStrokeColorProperty;
    protected ShadersElement mStrokeShadersElement;
    private PropertyWrapper mStrokeWeightProperty;
    protected float mWeight;
    protected Expression mXfermodeNumExp;

    /* loaded from: classes2.dex */
    protected enum DrawMode {
        STROKE_CENTER,
        STROKE_OUTER,
        STROKE_INNER,
        FILL;

        public static DrawMode getStrokeAlign(String str) {
            return "inner".equalsIgnoreCase(str) ? STROKE_INNER : "center".equalsIgnoreCase(str) ? STROKE_CENTER : STROKE_OUTER;
        }
    }

    static {
        AnimatedProperty.AnimatedColorProperty animatedColorProperty = new AnimatedProperty.AnimatedColorProperty("fillColor") { // from class: com.miui.maml.elements.GeometryScreenElement.1
            @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
            public int getIntValue(AnimatedScreenElement animatedScreenElement) {
                if (animatedScreenElement instanceof GeometryScreenElement) {
                    return (int) ((GeometryScreenElement) animatedScreenElement).mFillColorProperty.getValue();
                }
                return 0;
            }

            @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
            public void setIntValue(AnimatedScreenElement animatedScreenElement, int i) {
                if (animatedScreenElement instanceof GeometryScreenElement) {
                    ((GeometryScreenElement) animatedScreenElement).mFillColorProperty.setValue(i);
                }
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof GeometryScreenElement) {
                    ((GeometryScreenElement) animatedScreenElement).mFillColorProperty.setVelocity(f);
                }
            }
        };
        FILL_COLOR = animatedColorProperty;
        AnimatedProperty.AnimatedColorProperty animatedColorProperty2 = new AnimatedProperty.AnimatedColorProperty("strokeColor") { // from class: com.miui.maml.elements.GeometryScreenElement.2
            @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
            public int getIntValue(AnimatedScreenElement animatedScreenElement) {
                if (animatedScreenElement instanceof GeometryScreenElement) {
                    return (int) ((GeometryScreenElement) animatedScreenElement).mStrokeColorProperty.getValue();
                }
                return 0;
            }

            @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
            public void setIntValue(AnimatedScreenElement animatedScreenElement, int i) {
                if (animatedScreenElement instanceof GeometryScreenElement) {
                    ((GeometryScreenElement) animatedScreenElement).mStrokeColorProperty.setValue(i);
                }
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof GeometryScreenElement) {
                    ((GeometryScreenElement) animatedScreenElement).mStrokeColorProperty.setVelocity(f);
                }
            }
        };
        STROKE_COLOR = animatedColorProperty2;
        AnimatedProperty animatedProperty = new AnimatedProperty("strokeWeight") { // from class: com.miui.maml.elements.GeometryScreenElement.3
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                if (animatedScreenElement instanceof GeometryScreenElement) {
                    return (float) ((GeometryScreenElement) animatedScreenElement).mStrokeWeightProperty.getValue();
                }
                return 0.0f;
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof GeometryScreenElement) {
                    ((GeometryScreenElement) animatedScreenElement).mStrokeWeightProperty.setValue(f);
                }
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof GeometryScreenElement) {
                    ((GeometryScreenElement) animatedScreenElement).mStrokeWeightProperty.setVelocity(f);
                }
            }
        };
        STROKE_WEIGHT = animatedProperty;
        AnimatedProperty.sPropertyNameMap.put("fillColor", animatedColorProperty);
        AnimatedTarget.sPropertyMap.put(1004, animatedColorProperty);
        AnimatedTarget.sPropertyTypeMap.put(animatedColorProperty, 1004);
        AnimatedProperty.sPropertyNameMap.put("strokeColor", animatedColorProperty2);
        AnimatedTarget.sPropertyMap.put(1005, animatedColorProperty2);
        AnimatedTarget.sPropertyTypeMap.put(animatedColorProperty2, 1005);
        AnimatedProperty.sPropertyNameMap.put("strokeWeight", animatedProperty);
        AnimatedTarget.sPropertyMap.put(1012, animatedProperty);
        AnimatedTarget.sPropertyTypeMap.put(animatedProperty, 1012);
    }

    public GeometryScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mPaint = new Paint();
        this.mWeight = scale(1.0d);
        String attr = getAttr(element, "strokeColor");
        Variables variables = getVariables();
        if (!TextUtils.isEmpty(attr)) {
            this.mStrokeColorParser = new ColorParser(variables, attr);
        }
        String attr2 = getAttr(element, "fillColor");
        if (!TextUtils.isEmpty(attr2)) {
            this.mFillColorParser = new ColorParser(variables, attr2);
        }
        Expression build = Expression.build(variables, getAttr(element, "weight"));
        this.mPaint.setStrokeCap(getCap(getAttr(element, "cap")));
        float[] resolveDashIntervals = resolveDashIntervals(element);
        if (resolveDashIntervals != null) {
            this.mPaint.setPathEffect(new DashPathEffect(resolveDashIntervals, 0.0f));
        }
        this.mStrokeAlign = DrawMode.getStrokeAlign(getAttr(element, "strokeAlign"));
        Expression build2 = Expression.build(variables, getAttr(element, "xfermodeNum"));
        this.mXfermodeNumExp = build2;
        if (build2 == null) {
            this.mPaint.setXfermode(new PorterDuffXfermode(Utils.getPorterDuffMode(getAttr(element, "xfermode"))));
        }
        this.mPaint.setAntiAlias(true);
        ColorParser colorParser = this.mStrokeColorParser;
        this.mStrokeColor = colorParser != null ? colorParser.getColor() : 0;
        ColorParser colorParser2 = this.mFillColorParser;
        this.mFillColor = colorParser2 != null ? colorParser2.getColor() : 0;
        this.mFillColorProperty = new PropertyWrapper(this.mName + ".fillColor", getVariables(), null, isInFolmeMode(), this.mFillColor);
        this.mStrokeColorProperty = new PropertyWrapper(this.mName + ".strokeColor", getVariables(), null, isInFolmeMode(), this.mStrokeColor);
        this.mStrokeWeightProperty = new PropertyWrapper(this.mName + ".strokeWeight", getVariables(), build, isInFolmeMode(), 0.0d);
        loadShadersElement(element, screenElementRoot);
    }

    private final Paint.Cap getCap(String str) {
        Paint.Cap cap = Paint.Cap.BUTT;
        return TextUtils.isEmpty(str) ? cap : str.equalsIgnoreCase("round") ? Paint.Cap.ROUND : str.equalsIgnoreCase("square") ? Paint.Cap.SQUARE : cap;
    }

    private void loadShadersElement(Element element, ScreenElementRoot screenElementRoot) {
        Element child = Utils.getChild(element, "StrokeShaders");
        if (child != null) {
            this.mStrokeShadersElement = new ShadersElement(child, screenElementRoot);
        }
        Element child2 = Utils.getChild(element, "FillShaders");
        if (child2 != null) {
            this.mFillShadersElement = new ShadersElement(child2, screenElementRoot);
        }
    }

    private float[] resolveDashIntervals(Element element) {
        String attr = getAttr(element, "dash");
        if (TextUtils.isEmpty(attr)) {
            return null;
        }
        String[] split = attr.split(",");
        if (split.length < 2 || split.length % 2 != 0) {
            return null;
        }
        float[] fArr = new float[split.length];
        for (int i = 0; i < split.length; i++) {
            try {
                fArr[i] = Float.parseFloat(split[i]);
            } catch (NumberFormatException unused) {
                return null;
            }
        }
        return fArr;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void doRender(Canvas canvas) {
        if (this.mFillShadersElement != null || this.mFillColor != 0) {
            this.mPaint.setStyle(Paint.Style.FILL);
            ShadersElement shadersElement = this.mFillShadersElement;
            if (shadersElement != null) {
                this.mPaint.setShader(shadersElement.getShader());
                this.mPaint.setAlpha(this.mAlpha);
            } else {
                this.mPaint.setShader(null);
                this.mPaint.setColor(this.mFillColor);
                Paint paint = this.mPaint;
                paint.setAlpha(Utils.mixAlpha(paint.getAlpha(), this.mAlpha));
            }
            onDraw(canvas, DrawMode.FILL);
        }
        if (this.mWeight > 0.0f) {
            if (this.mStrokeShadersElement == null && this.mStrokeColor == 0) {
                return;
            }
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setStrokeWidth(this.mWeight);
            ShadersElement shadersElement2 = this.mStrokeShadersElement;
            if (shadersElement2 != null) {
                this.mPaint.setShader(shadersElement2.getShader());
                this.mPaint.setAlpha(this.mAlpha);
            } else {
                this.mPaint.setShader(null);
                this.mPaint.setColor(this.mStrokeColor);
                Paint paint2 = this.mPaint;
                paint2.setAlpha(Utils.mixAlpha(paint2.getAlpha(), this.mAlpha));
            }
            onDraw(canvas, this.mStrokeAlign);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void doTick(long j) {
        super.doTick(j);
        if (isVisible()) {
            if (isInFolmeMode()) {
                this.mStrokeColor = (int) this.mStrokeColorProperty.getValue();
                this.mFillColor = (int) this.mFillColorProperty.getValue();
            } else {
                ColorParser colorParser = this.mStrokeColorParser;
                if (colorParser != null) {
                    this.mStrokeColor = colorParser.getColor();
                }
                ColorParser colorParser2 = this.mFillColorParser;
                if (colorParser2 != null) {
                    this.mFillColor = colorParser2.getColor();
                }
            }
            ShadersElement shadersElement = this.mStrokeShadersElement;
            if (shadersElement != null) {
                shadersElement.updateShader();
            }
            ShadersElement shadersElement2 = this.mFillShadersElement;
            if (shadersElement2 != null) {
                shadersElement2.updateShader();
            }
            this.mWeight = scale(this.mStrokeWeightProperty.getValue());
            Expression expression = this.mXfermodeNumExp;
            if (expression != null) {
                this.mPaint.setXfermode(new PorterDuffXfermode(Utils.getPorterDuffMode((int) expression.evaluate())));
            }
            if (this.mTintChanged) {
                this.mPaint.setColorFilter(this.mTintFilter);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement
    public void initProperties() {
        super.initProperties();
        this.mFillColorProperty.init();
        this.mStrokeColorProperty.init();
        this.mStrokeWeightProperty.init();
    }

    protected abstract void onDraw(Canvas canvas, DrawMode drawMode);

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void setColorFilter(ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
        Paint paint = this.mPaint;
        if (paint != null) {
            paint.setColorFilter(colorFilter);
        }
    }
}
