package com.miui.maml.elements;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.data.Variables;
import com.miui.maml.util.ColorParser;
import com.miui.maml.util.Utils;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class PaintScreenElement extends AnimatedScreenElement {
    private static float DEFAULT_WEIGHT = 1.0f;
    private Bitmap mCachedBitmap;
    private Canvas mCachedCanvas;
    private Paint mCachedPaint;
    private int mColor;
    private ColorParser mColorParser;
    private Paint mPaint;
    private Path mPath;
    private boolean mPendingMouseUp;
    private float mWeight;
    private Expression mWeightExp;
    private Xfermode mXfermode;

    public PaintScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        load(element, screenElementRoot);
        this.mPath = new Path();
        float scale = scale(DEFAULT_WEIGHT);
        DEFAULT_WEIGHT = scale;
        this.mWeight = scale;
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setXfermode(this.mXfermode);
        this.mPaint.setAntiAlias(true);
        Paint paint2 = new Paint();
        this.mCachedPaint = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.mCachedPaint.setStrokeWidth(DEFAULT_WEIGHT);
        this.mCachedPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mCachedPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mCachedPaint.setAntiAlias(true);
        this.mTouchable = true;
    }

    private void load(Element element, ScreenElementRoot screenElementRoot) {
        if (element == null) {
            return;
        }
        Variables variables = getVariables();
        this.mWeightExp = Expression.build(variables, element.getAttribute("weight"));
        this.mColorParser = ColorParser.fromElement(variables, element);
        this.mXfermode = new PorterDuffXfermode(Utils.getPorterDuffMode(element.getAttribute("xfermode")));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void doRender(Canvas canvas) {
        float width = getWidth();
        float height = getHeight();
        float left = getLeft(0.0f, width);
        float top = getTop(0.0f, height);
        float absoluteLeft = getAbsoluteLeft();
        float absoluteTop = getAbsoluteTop();
        if (this.mPendingMouseUp) {
            this.mCachedCanvas.save();
            this.mCachedCanvas.translate(-absoluteLeft, -absoluteTop);
            this.mCachedCanvas.drawPath(this.mPath, this.mCachedPaint);
            this.mCachedCanvas.restore();
            this.mPath.reset();
            this.mPendingMouseUp = false;
        }
        canvas.drawBitmap(this.mCachedBitmap, left, top, this.mPaint);
        if (this.mPressed) {
            float f = this.mWeight;
            if (f <= 0.0f || this.mAlpha <= 0) {
                return;
            }
            this.mCachedPaint.setStrokeWidth(f);
            this.mCachedPaint.setColor(this.mColor);
            Paint paint = this.mCachedPaint;
            paint.setAlpha(Utils.mixAlpha(paint.getAlpha(), this.mAlpha));
            canvas.save();
            canvas.translate((-absoluteLeft) + left, (-absoluteTop) + top);
            Xfermode xfermode = this.mCachedPaint.getXfermode();
            this.mCachedPaint.setXfermode(this.mXfermode);
            canvas.drawPath(this.mPath, this.mCachedPaint);
            this.mCachedPaint.setXfermode(xfermode);
            canvas.restore();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void doTick(long j) {
        super.doTick(j);
        if (isVisible()) {
            Expression expression = this.mWeightExp;
            if (expression != null) {
                this.mWeight = scale(expression.evaluate());
            }
            this.mColor = this.mColorParser.getColor();
        }
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void finish() {
        super.finish();
        this.mCachedBitmap.recycle();
        this.mCachedBitmap = null;
        this.mCachedCanvas = null;
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void init() {
        super.init();
        float width = getWidth();
        if (width < 0.0f) {
            width = scale(Utils.getVariableNumber("screen_width", getVariables()));
        }
        float height = getHeight();
        if (height < 0.0f) {
            height = scale(Utils.getVariableNumber("screen_height", getVariables()));
        }
        Bitmap createBitmap = Bitmap.createBitmap((int) Math.ceil(width), (int) Math.ceil(height), Bitmap.Config.ARGB_8888);
        this.mCachedBitmap = createBitmap;
        createBitmap.setDensity(this.mRoot.getTargetDensity());
        this.mCachedCanvas = new Canvas(this.mCachedBitmap);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement
    public void onActionCancel() {
        this.mPendingMouseUp = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement
    public void onActionDown(float f, float f2) {
        super.onActionDown(f, f2);
        this.mPath.reset();
        this.mPath.moveTo(f, f2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement
    public void onActionMove(float f, float f2) {
        super.onActionMove(f, f2);
        this.mPath.lineTo(f, f2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement
    public void onActionUp() {
        super.onActionUp();
        this.mPendingMouseUp = true;
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void reset(long j) {
        super.reset(j);
        this.mCachedCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        this.mPressed = false;
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void setColorFilter(ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
        Paint paint = this.mPaint;
        if (paint != null) {
            paint.setColorFilter(colorFilter);
        }
    }
}
