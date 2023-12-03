package miuix.smooth;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import java.io.IOException;
import miuix.smooth.internal.SmoothDrawHelper;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@Deprecated
/* loaded from: classes5.dex */
public class SmoothGradientDrawable extends GradientDrawable {
    private static final PorterDuffXfermode XFERMODE_DST_OUT = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    private SmoothDrawHelper mHelper;
    private Rect mLayer;
    private GradientDrawable mParentDrawable;
    private RectF mSavedLayer;
    protected SmoothConstantState mSmoothConstantState;

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes5.dex */
    public static class SmoothConstantState extends Drawable.ConstantState {
        int mLayerType;
        Drawable.ConstantState mParent;
        float[] mRadii;
        float mRadius;
        int mStrokeColor;
        int mStrokeWidth;

        public SmoothConstantState() {
            this.mStrokeWidth = 0;
            this.mStrokeColor = 0;
            this.mLayerType = 0;
        }

        public SmoothConstantState(SmoothConstantState smoothConstantState) {
            this.mStrokeWidth = 0;
            this.mStrokeColor = 0;
            this.mLayerType = 0;
            this.mRadius = smoothConstantState.mRadius;
            this.mRadii = smoothConstantState.mRadii;
            this.mStrokeWidth = smoothConstantState.mStrokeWidth;
            this.mStrokeColor = smoothConstantState.mStrokeColor;
            this.mParent = smoothConstantState.mParent;
            this.mLayerType = smoothConstantState.mLayerType;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public boolean canApplyTheme() {
            return true;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mParent.getChangingConfigurations();
        }

        /* JADX WARN: Type inference failed for: r1v0, types: [miuix.smooth.SmoothGradientDrawable$1, android.content.res.Resources] */
        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            ?? r1 = 0;
            if (this.mParent == null) {
                return null;
            }
            return new SmoothGradientDrawable(this, r1);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            if (this.mParent == null) {
                return null;
            }
            return new SmoothGradientDrawable(new SmoothConstantState(this), resources);
        }

        public void setConstantState(Drawable.ConstantState constantState) {
            this.mParent = constantState;
        }
    }

    public SmoothGradientDrawable() {
        this.mHelper = new SmoothDrawHelper();
        this.mSavedLayer = new RectF();
        this.mLayer = new Rect();
        SmoothConstantState smoothConstantState = new SmoothConstantState();
        this.mSmoothConstantState = smoothConstantState;
        smoothConstantState.setConstantState(super.getConstantState());
    }

    public SmoothGradientDrawable(GradientDrawable.Orientation orientation, int[] iArr) {
        super(orientation, iArr);
        this.mHelper = new SmoothDrawHelper();
        this.mSavedLayer = new RectF();
        this.mLayer = new Rect();
        SmoothConstantState smoothConstantState = new SmoothConstantState();
        this.mSmoothConstantState = smoothConstantState;
        smoothConstantState.setConstantState(super.getConstantState());
    }

    private SmoothGradientDrawable(SmoothConstantState smoothConstantState, Resources resources) {
        this.mHelper = new SmoothDrawHelper();
        this.mSavedLayer = new RectF();
        this.mLayer = new Rect();
        this.mSmoothConstantState = smoothConstantState;
        Drawable newDrawable = resources == null ? smoothConstantState.mParent.newDrawable() : smoothConstantState.mParent.newDrawable(resources);
        this.mSmoothConstantState.setConstantState(newDrawable.getConstantState());
        this.mParentDrawable = (GradientDrawable) newDrawable;
        this.mHelper.setRadii(smoothConstantState.mRadii);
        this.mHelper.setRadius(smoothConstantState.mRadius);
        this.mHelper.setStrokeWidth(smoothConstantState.mStrokeWidth);
        this.mHelper.setStrokeColor(smoothConstantState.mStrokeColor);
    }

    private TypedArray obtainAttributes(Resources resources, Resources.Theme theme, AttributeSet attributeSet, int[] iArr) {
        return theme == null ? resources.obtainAttributes(attributeSet, iArr) : theme.obtainStyledAttributes(attributeSet, iArr, 0, 0);
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public void applyTheme(Resources.Theme theme) {
        super.applyTheme(theme);
        this.mSmoothConstantState.setConstantState(super.getConstantState());
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public boolean canApplyTheme() {
        SmoothConstantState smoothConstantState = this.mSmoothConstantState;
        return (smoothConstantState != null && smoothConstantState.canApplyTheme()) || super.canApplyTheme();
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int saveLayer = getLayerType() != 2 ? canvas.saveLayer(this.mSavedLayer, null, 31) : -1;
        GradientDrawable gradientDrawable = this.mParentDrawable;
        if (gradientDrawable != null) {
            gradientDrawable.draw(canvas);
        } else {
            super.draw(canvas);
        }
        this.mHelper.drawMask(canvas, XFERMODE_DST_OUT);
        if (getLayerType() != 2) {
            canvas.restoreToCount(saveLayer);
        }
        this.mHelper.drawStroke(canvas);
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public int getAlpha() {
        GradientDrawable gradientDrawable = this.mParentDrawable;
        return gradientDrawable != null ? gradientDrawable.getAlpha() : super.getAlpha();
    }

    @Override // android.graphics.drawable.GradientDrawable
    public ColorStateList getColor() {
        GradientDrawable gradientDrawable = this.mParentDrawable;
        return (gradientDrawable == null || Build.VERSION.SDK_INT < 24) ? super.getColor() : gradientDrawable.getColor();
    }

    @Override // android.graphics.drawable.GradientDrawable
    public int[] getColors() {
        GradientDrawable gradientDrawable = this.mParentDrawable;
        return (gradientDrawable == null || Build.VERSION.SDK_INT < 24) ? super.getColors() : gradientDrawable.getColors();
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return this.mSmoothConstantState;
    }

    public int getLayerType() {
        return this.mSmoothConstantState.mLayerType;
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public void getOutline(Outline outline) {
        int i = Build.VERSION.SDK_INT;
        if (i >= 30) {
            outline.setPath(this.mHelper.getSmoothPath(this.mLayer));
        } else if (i >= 21) {
            outline.setRoundRect(this.mLayer, this.mHelper.getRadius());
        }
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws IOException, XmlPullParserException {
        TypedArray obtainAttributes = obtainAttributes(resources, theme, attributeSet, R$styleable.MiuixSmoothGradientDrawable);
        setStrokeWidth(obtainAttributes.getDimensionPixelSize(R$styleable.MiuixSmoothGradientDrawable_miuix_strokeWidth, 0));
        setStrokeColor(obtainAttributes.getColor(R$styleable.MiuixSmoothGradientDrawable_miuix_strokeColor, 0));
        setLayerType(obtainAttributes.getInt(R$styleable.MiuixSmoothGradientDrawable_android_layerType, 0));
        obtainAttributes.recycle();
        super.inflate(resources, xmlPullParser, attributeSet, theme);
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        GradientDrawable gradientDrawable = this.mParentDrawable;
        if (gradientDrawable != null) {
            gradientDrawable.setBounds(rect);
        }
        this.mHelper.onBoundsChange(rect);
        this.mLayer = rect;
        this.mSavedLayer.set(0.0f, 0.0f, rect.width(), rect.height());
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        GradientDrawable gradientDrawable = this.mParentDrawable;
        if (gradientDrawable != null) {
            gradientDrawable.setAlpha(i);
        } else {
            super.setAlpha(i);
        }
        invalidateSelf();
    }

    @Override // android.graphics.drawable.GradientDrawable
    public void setColor(int i) {
        GradientDrawable gradientDrawable = this.mParentDrawable;
        if (gradientDrawable != null) {
            gradientDrawable.setColor(i);
        } else {
            super.setColor(i);
        }
    }

    @Override // android.graphics.drawable.GradientDrawable
    public void setColor(ColorStateList colorStateList) {
        GradientDrawable gradientDrawable = this.mParentDrawable;
        if (gradientDrawable != null) {
            gradientDrawable.setColor(colorStateList);
        } else {
            super.setColor(colorStateList);
        }
    }

    @Override // android.graphics.drawable.GradientDrawable
    public void setColors(int[] iArr, float[] fArr) {
        GradientDrawable gradientDrawable = this.mParentDrawable;
        if (gradientDrawable == null || Build.VERSION.SDK_INT < 29) {
            super.setColors(iArr, fArr);
        } else {
            gradientDrawable.setColors(iArr, fArr);
        }
    }

    @Override // android.graphics.drawable.GradientDrawable
    public void setCornerRadii(float[] fArr) {
        super.setCornerRadii(fArr);
        this.mSmoothConstantState.mRadii = fArr;
        this.mHelper.setRadii(fArr);
        if (fArr == null) {
            this.mSmoothConstantState.mRadius = 0.0f;
            this.mHelper.setRadius(0.0f);
        }
    }

    @Override // android.graphics.drawable.GradientDrawable
    public void setCornerRadius(float f) {
        if (Float.isNaN(f)) {
            return;
        }
        super.setCornerRadius(f);
        if (f < 0.0f) {
            f = 0.0f;
        }
        SmoothConstantState smoothConstantState = this.mSmoothConstantState;
        smoothConstantState.mRadius = f;
        smoothConstantState.mRadii = null;
        this.mHelper.setRadius(f);
        this.mHelper.setRadii(null);
    }

    public void setLayerType(int i) {
        if (i < 0 || i > 2) {
            throw new IllegalArgumentException("Layer type can only be one of: LAYER_TYPE_NONE, LAYER_TYPE_SOFTWARE or LAYER_TYPE_HARDWARE");
        }
        SmoothConstantState smoothConstantState = this.mSmoothConstantState;
        if (smoothConstantState.mLayerType != i) {
            smoothConstantState.mLayerType = i;
            invalidateSelf();
        }
    }

    public void setStrokeColor(int i) {
        SmoothConstantState smoothConstantState = this.mSmoothConstantState;
        if (smoothConstantState.mStrokeColor != i) {
            smoothConstantState.mStrokeColor = i;
            this.mHelper.setStrokeColor(i);
            invalidateSelf();
        }
    }

    public void setStrokeWidth(int i) {
        SmoothConstantState smoothConstantState = this.mSmoothConstantState;
        if (smoothConstantState.mStrokeWidth != i) {
            smoothConstantState.mStrokeWidth = i;
            this.mHelper.setStrokeWidth(i);
            invalidateSelf();
        }
    }
}
