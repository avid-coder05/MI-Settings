package com.android.settings.widget;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.android.settings.R;
import com.android.settings.R$styleable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes2.dex */
public class MultiGradientDrawable extends Drawable {
    private ColorFilter mColorFilter;
    private int[] mColors;
    private LinearGradient mLinearGradient;
    private Rect mPaddingRect;
    private float[] mPositions;
    private float mRadius;
    private Paint mPaint = new Paint();
    private int mAlpha = 255;

    private void inflateChildElements(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth() + 1;
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1) {
                return;
            }
            int depth2 = xmlPullParser.getDepth();
            if (depth2 < depth && next == 3) {
                return;
            }
            if (next == 2 && depth2 <= depth) {
                String name = xmlPullParser.getName();
                if (name.equals("gradient")) {
                    TypedArray obtainAttributes = resources.obtainAttributes(attributeSet, R$styleable.GradientsList);
                    updateGradientDrawableGradient(resources, obtainAttributes);
                    obtainAttributes.recycle();
                } else if (name.equals("corners")) {
                    TypedArray obtainAttributes2 = resources.obtainAttributes(attributeSet, R$styleable.GradientsCorner);
                    updateDrawableCorners(obtainAttributes2);
                    obtainAttributes2.recycle();
                } else {
                    Log.w("MultiGradientDrawable", "Bad element under me: " + name);
                }
            }
        }
    }

    private void updateDrawableCorners(TypedArray typedArray) {
        this.mRadius = typedArray.getDimensionPixelSize(R$styleable.GradientsCorner_gradient_radius, 0);
    }

    private void updateGradientDrawableGradient(Resources resources, TypedArray typedArray) {
        this.mColors = resources.getIntArray(R.array.gradient_colors);
        CharSequence[] textArray = typedArray.getTextArray(R$styleable.GradientsList_gradient_colors);
        if (textArray != null) {
            this.mColors = new int[textArray.length];
            for (int i = 0; i < textArray.length; i++) {
                this.mColors[i] = Color.parseColor(textArray[i].toString());
            }
        }
        this.mPositions = null;
        CharSequence[] textArray2 = typedArray.getTextArray(R$styleable.GradientsList_gradient_positions);
        if (textArray2 != null) {
            this.mPositions = new float[textArray2.length];
            for (int i2 = 0; i2 < textArray2.length; i2++) {
                this.mPositions[i2] = Float.valueOf(textArray2[i2].toString()).floatValue();
            }
        }
    }

    private void updateStateFromTypedArray(TypedArray typedArray) {
        this.mPaddingRect.left = typedArray.getDimensionPixelSize(R$styleable.MultiGradientDrawable_left, 0);
        this.mPaddingRect.top = typedArray.getDimensionPixelSize(R$styleable.MultiGradientDrawable_top, 0);
        this.mPaddingRect.right = typedArray.getDimensionPixelSize(R$styleable.MultiGradientDrawable_right, 0);
        this.mPaddingRect.bottom = typedArray.getDimensionPixelSize(R$styleable.MultiGradientDrawable_bottom, 0);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        this.mPaint.setAlpha(this.mAlpha);
        this.mPaint.setColorFilter(this.mColorFilter);
        RectF rectF = new RectF(getBounds());
        float f = rectF.top;
        Rect rect = this.mPaddingRect;
        rectF.top = f - rect.top;
        rectF.bottom += rect.bottom;
        rectF.left -= rect.left;
        rectF.right += rect.right;
        float f2 = this.mRadius;
        canvas.drawRoundRect(rectF, f2, f2, this.mPaint);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws IOException, XmlPullParserException {
        super.inflate(resources, xmlPullParser, attributeSet, theme);
        this.mPaddingRect = new Rect();
        TypedArray obtainAttributes = resources.obtainAttributes(attributeSet, R$styleable.MultiGradientDrawable);
        updateStateFromTypedArray(obtainAttributes);
        obtainAttributes.recycle();
        inflateChildElements(resources, xmlPullParser, attributeSet, theme);
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, rect.width(), 0.0f, this.mColors, this.mPositions, Shader.TileMode.CLAMP);
        this.mLinearGradient = linearGradient;
        this.mPaint.setShader(linearGradient);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        if (this.mAlpha != i) {
            this.mAlpha = i;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        if (colorFilter != this.mColorFilter) {
            this.mColorFilter = colorFilter;
            invalidateSelf();
        }
    }
}
