package miuix.animation.styles;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import miuix.animation.Folme;
import miuix.animation.ITouchStyle;
import miuix.animation.R$id;
import miuix.animation.property.ViewPropertyExt;
import miuix.animation.utils.CommonUtils;

/* loaded from: classes5.dex */
public class TintDrawable extends Drawable {
    private static final View.OnAttachStateChangeListener sListener = new View.OnAttachStateChangeListener() { // from class: miuix.animation.styles.TintDrawable.1
        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View view) {
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View view) {
            TintDrawable tintDrawable = TintDrawable.get(view);
            if (tintDrawable == null || Build.VERSION.SDK_INT < 23) {
                return;
            }
            Drawable drawable = tintDrawable.mOriDrawable;
            if (drawable != null) {
                view.setForeground(drawable);
            }
            tintDrawable.clear();
            view.removeOnAttachStateChangeListener(this);
        }
    };
    private Bitmap mBitmap;
    private Drawable mOriDrawable;
    private View mView;
    private final int TINT_STYLE_VIEW_SHAPE = 1;
    private final int TINT_STYLE_RECT_ROUND = 2;
    private final int TINT_STYLE_HOVER = 4;
    private final float TOUCH_RADIUS_RECT_ROUND_DEFAULT = 26.0f;
    private Paint mPaint = new Paint();
    private RectF mBounds = new RectF();
    private Rect mSrcRect = new Rect();
    private RectF mCornerBounds = new RectF();
    private int mTintStyle = 1;
    private float mHoverRadius = 0.0f;
    private float[] mTouchRadius = {0.0f};
    private RectF mTouchRectRoundRect = null;
    private RectF mTouchRectRoundPadding = new RectF();
    private ITouchStyle.TouchRectGravity mTouchRectGravity = ITouchStyle.TouchRectGravity.CENTER_IN_PARENT;
    private int mTouchRectLocationMode = 1;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: miuix.animation.styles.TintDrawable$3  reason: invalid class name */
    /* loaded from: classes5.dex */
    public static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$miuix$animation$ITouchStyle$TouchRectGravity;

        static {
            int[] iArr = new int[ITouchStyle.TouchRectGravity.values().length];
            $SwitchMap$miuix$animation$ITouchStyle$TouchRectGravity = iArr;
            try {
                iArr[ITouchStyle.TouchRectGravity.TOP_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$miuix$animation$ITouchStyle$TouchRectGravity[ITouchStyle.TouchRectGravity.TOP_CENTER.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$miuix$animation$ITouchStyle$TouchRectGravity[ITouchStyle.TouchRectGravity.CENTER_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$miuix$animation$ITouchStyle$TouchRectGravity[ITouchStyle.TouchRectGravity.CENTER_IN_PARENT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clear() {
        recycleBitmap();
    }

    private void createBitmap(int i, int i2) {
        if (Build.VERSION.SDK_INT >= 23) {
            Bitmap bitmap = this.mBitmap;
            if (bitmap != null && bitmap.getWidth() == i && this.mBitmap.getHeight() == this.mView.getHeight()) {
                return;
            }
            recycleBitmap();
            this.mPaint.setAntiAlias(true);
            try {
                this.mBitmap = Bitmap.createBitmap(this.mView.getResources().getDisplayMetrics(), i, i2, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError unused) {
                Log.w("miuix_anim", "TintDrawable.createBitmap failed, out of memory");
            }
        }
    }

    private void drawBlackeningByExtractingBg(Canvas canvas, int i) {
        Bitmap bitmap = this.mBitmap;
        if (bitmap != null && !bitmap.isRecycled()) {
            this.mPaint.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(this.mBitmap, this.mSrcRect, this.mBounds, this.mPaint);
        } else if (Build.VERSION.SDK_INT >= 23) {
            this.mView.setForeground(this.mOriDrawable);
        }
    }

    private void drawHoverTint(Canvas canvas, int i) {
        this.mCornerBounds.set(this.mSrcRect);
        this.mPaint.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
        RectF rectF = this.mCornerBounds;
        float f = this.mHoverRadius;
        canvas.drawRoundRect(rectF, f, f, this.mPaint);
    }

    private void drawRectRound(Canvas canvas, int i) {
        this.mPaint.setAntiAlias(true);
        this.mPaint.setShader(null);
        this.mPaint.setColorFilter(null);
        this.mPaint.setColor(i);
        int i2 = this.mTouchRectLocationMode;
        if (i2 == 1) {
            setBounds(this.mView.getWidth(), this.mView.getHeight());
            realPerFromDrawRoundRect(canvas, i, this.mBounds);
        } else if (i2 != 4) {
            if (i2 != 4104) {
                return;
            }
            perFromDrawRoundRectRelative(canvas, i);
        } else {
            setBounds(this.mView.getWidth(), this.mView.getHeight());
            RectF rectF = this.mCornerBounds;
            float f = this.mSrcRect.left;
            RectF rectF2 = this.mTouchRectRoundPadding;
            rectF.left = f + rectF2.left;
            rectF.top = r1.top + rectF2.top;
            rectF.right = r1.right - rectF2.right;
            rectF.bottom = r1.bottom - rectF2.bottom;
            if (rectF.width() < 0.0f) {
                RectF rectF3 = this.mCornerBounds;
                rectF3.right = rectF3.left;
            }
            if (this.mCornerBounds.height() < 0.0f) {
                RectF rectF4 = this.mCornerBounds;
                rectF4.bottom = rectF4.top;
            }
            realPerFromDrawRoundRect(canvas, i, this.mCornerBounds);
        }
    }

    public static TintDrawable get(View view) {
        if (Build.VERSION.SDK_INT >= 23) {
            Drawable foreground = view.getForeground();
            if (foreground instanceof TintDrawable) {
                return (TintDrawable) foreground;
            }
            return null;
        }
        return null;
    }

    private void getRectRoundEnableFromView(int i) {
        this.mTintStyle = i == 3 ? 2 : this.mTintStyle;
    }

    private void getRectRoundGravityFromView() {
        Object tag = this.mView.getTag(R$id.miuix_animation_tag_view_touch_rect_gravity);
        if (tag instanceof ITouchStyle.TouchRectGravity) {
            this.mTouchRectGravity = (ITouchStyle.TouchRectGravity) tag;
        }
    }

    private void getRectRoundPaddingsFromView() {
        Object tag = this.mView.getTag(R$id.miuix_animation_tag_view_touch_padding_rect);
        if (!(tag instanceof RectF)) {
            RectF rectF = this.mTouchRectRoundPadding;
            rectF.left = 0.0f;
            rectF.top = 0.0f;
            rectF.right = 0.0f;
            rectF.bottom = 0.0f;
            return;
        }
        RectF rectF2 = (RectF) tag;
        this.mTouchRectRoundPadding.left = Math.max(rectF2.left, 0.0f);
        this.mTouchRectRoundPadding.top = Math.max(rectF2.top, 0.0f);
        this.mTouchRectRoundPadding.right = Math.max(rectF2.right, 0.0f);
        this.mTouchRectRoundPadding.bottom = Math.max(rectF2.bottom, 0.0f);
    }

    private void getRectRoundRadiusFromView() {
        Object tag = this.mView.getTag(R$id.miuix_animation_tag_view_touch_corners);
        if ((tag instanceof Float) || (tag instanceof Integer)) {
            this.mTouchRadius = new float[]{((Float) tag).floatValue()};
        } else if (!(tag instanceof RectF)) {
            this.mTouchRadius = new float[]{26.0f};
        } else {
            float[] fArr = {26.0f, 26.0f, 26.0f, 26.0f, 26.0f, 26.0f, 26.0f, 26.0f};
            this.mTouchRadius = fArr;
            RectF rectF = (RectF) tag;
            fArr[0] = Math.max(rectF.left, 0.0f);
            this.mTouchRadius[1] = Math.max(rectF.left, 0.0f);
            this.mTouchRadius[2] = Math.max(rectF.top, 0.0f);
            this.mTouchRadius[3] = Math.max(rectF.top, 0.0f);
            this.mTouchRadius[4] = Math.max(rectF.right, 0.0f);
            this.mTouchRadius[5] = Math.max(rectF.right, 0.0f);
            this.mTouchRadius[6] = Math.max(rectF.bottom, 0.0f);
            this.mTouchRadius[7] = Math.max(rectF.bottom, 0.0f);
        }
    }

    private void getTouchLocationModeFromView() {
        Object tag = this.mView.getTag(R$id.miuix_animation_tag_view_touch_rect_location_mode);
        if (tag instanceof Integer) {
            this.mTouchRectLocationMode = ((Integer) tag).intValue();
        } else {
            this.mTouchRectLocationMode = 1;
        }
    }

    private void getTouchRectFromView() {
        Object tag = this.mView.getTag(R$id.miuix_animation_tag_view_touch_rect);
        if (tag instanceof RectF) {
            this.mTouchRectRoundRect = new RectF((RectF) tag);
        } else {
            this.mTouchRectRoundRect = null;
        }
    }

    private void initBitmap(int i) {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        Bitmap bitmap = this.mBitmap;
        if (bitmap == null || bitmap.isRecycled()) {
            this.mView.setForeground(this.mOriDrawable);
            return;
        }
        try {
            this.mBitmap.eraseColor(0);
            Canvas canvas = new Canvas(this.mBitmap);
            canvas.translate(-this.mView.getScrollX(), -this.mView.getScrollY());
            this.mView.setForeground(this.mOriDrawable);
            this.mView.draw(canvas);
            this.mView.setForeground(this);
            if (i == 0) {
                try {
                    this.mPaint.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Float.MAX_VALUE, 0.0f})));
                    canvas.drawBitmap(this.mBitmap, 0.0f, 0.0f, this.mPaint);
                } catch (Exception unused) {
                    Log.w("miuix_anim", "the Bitmap empty or Recycled");
                }
            }
        } catch (Exception e) {
            Log.w("miuix_anim", "TintDrawable.initBitmap failed, " + e);
        }
    }

    private void perFromDrawRoundRectRelative(Canvas canvas, int i) {
        int i2 = AnonymousClass3.$SwitchMap$miuix$animation$ITouchStyle$TouchRectGravity[this.mTouchRectGravity.ordinal()];
        if (i2 == 1) {
            setBounds(0.0f, 0.0f, Math.max(0.0f, this.mTouchRectRoundRect.width()), Math.max(0.0f, this.mTouchRectRoundRect.height()));
        } else if (i2 == 2) {
            setBounds((this.mView.getWidth() - Math.max(0.0f, this.mTouchRectRoundRect.width())) * 0.5f, 0.0f, Math.max(0.0f, this.mTouchRectRoundRect.width()), Math.max(0.0f, this.mTouchRectRoundRect.height()));
        } else if (i2 != 3) {
            setBounds((this.mView.getWidth() - Math.max(0.0f, this.mTouchRectRoundRect.width())) * 0.5f, (this.mView.getHeight() - Math.max(0.0f, this.mTouchRectRoundRect.height())) * 0.5f, Math.max(0.0f, this.mTouchRectRoundRect.width()), Math.max(0.0f, this.mTouchRectRoundRect.height()));
        } else {
            setBounds(0.0f, (this.mView.getHeight() - Math.max(0.0f, this.mTouchRectRoundRect.height())) * 0.5f, Math.max(0.0f, this.mTouchRectRoundRect.width()), Math.max(0.0f, this.mTouchRectRoundRect.height()));
        }
        realPerFromDrawRoundRect(canvas, i, this.mBounds);
    }

    private void processingOOMForDrawBitmap(RuntimeException runtimeException, Canvas canvas) {
        if (runtimeException.getMessage() == null || runtimeException.getMessage().length() <= 0 || !runtimeException.getMessage().contains("Canvas: trying to draw too large")) {
            return;
        }
        try {
            Bitmap compressImage = CommonUtils.compressImage(this.mBitmap, 50, 2);
            this.mBitmap = compressImage;
            canvas.drawBitmap(compressImage, this.mSrcRect, this.mBounds, this.mPaint);
        } catch (Exception e) {
            Log.w("miuix_anim", "TintDrawable.processingOOMForDrawBitmap failed, " + e);
        }
    }

    private void realPerFromDrawRoundRect(Canvas canvas, int i, RectF rectF) {
        float[] fArr = this.mTouchRadius;
        if (fArr.length == 1) {
            canvas.drawRoundRect(rectF, fArr[0], fArr[0], this.mPaint);
        } else if (fArr.length == 8) {
            Path path = new Path();
            path.addRoundRect(rectF, this.mTouchRadius, Path.Direction.CCW);
            canvas.drawPath(path, this.mPaint);
        }
    }

    private void recycleBitmap() {
        Bitmap bitmap = this.mBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.mBitmap = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TintDrawable setAndGet(final View view) {
        TintDrawable tintDrawable = get(view);
        if (tintDrawable != null || Build.VERSION.SDK_INT < 23) {
            return tintDrawable;
        }
        final TintDrawable tintDrawable2 = new TintDrawable();
        tintDrawable2.mView = view;
        tintDrawable2.setOriDrawable(view.getForeground());
        view.addOnAttachStateChangeListener(sListener);
        Folme.post(view, new Runnable() { // from class: miuix.animation.styles.TintDrawable.2
            @Override // java.lang.Runnable
            public void run() {
                view.setForeground(tintDrawable2);
            }
        });
        return tintDrawable2;
    }

    private void setBounds(float f, float f2) {
        float scrollX = this.mView.getScrollX();
        float scrollY = this.mView.getScrollY();
        this.mBounds.set(scrollX, scrollY, scrollX + f, scrollY + f2);
        this.mSrcRect.set(0, 0, (int) f, (int) f2);
    }

    private void setBounds(float f, float f2, float f3, float f4) {
        this.mBounds.set(f, f2, f + f3, f2 + f4);
        this.mSrcRect.set(0, 0, (int) f3, (int) f4);
    }

    private void setOriDrawable(Drawable drawable) {
        this.mOriDrawable = drawable;
    }

    private void tintStyleLoadData() {
        getTouchLocationModeFromView();
        int i = this.mTouchRectLocationMode;
        if (i == 1) {
            getRectRoundRadiusFromView();
        } else if (i == 2) {
            getRectRoundRadiusFromView();
            getTouchRectFromView();
        } else if (i == 4) {
            getRectRoundRadiusFromView();
            getRectRoundPaddingsFromView();
        } else if (i != 4104) {
        } else {
            getRectRoundRadiusFromView();
            getTouchRectFromView();
            getRectRoundGravityFromView();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int scrollX = this.mView.getScrollX();
        int scrollY = this.mView.getScrollY();
        int width = this.mView.getWidth();
        int height = this.mView.getHeight();
        this.mBounds.set(scrollX, scrollY, scrollX + width, scrollY + height);
        this.mSrcRect.set(0, 0, width, height);
        canvas.save();
        int intValue = ViewPropertyExt.FOREGROUND.getIntValue(this.mView);
        try {
            try {
                canvas.clipRect(this.mBounds);
                canvas.drawColor(0);
                Drawable drawable = this.mOriDrawable;
                if (drawable != null) {
                    drawable.draw(canvas);
                }
                int i = this.mTintStyle;
                if (i == 2) {
                    drawRectRound(canvas, intValue);
                } else if (i != 4) {
                    drawBlackeningByExtractingBg(canvas, intValue);
                } else {
                    drawHoverTint(canvas, intValue);
                }
            } catch (RuntimeException e) {
                processingOOMForDrawBitmap(e, canvas);
            }
        } finally {
            canvas.restore();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initTintBuffer(int i) {
        if (this.mView == null) {
            return;
        }
        getRectRoundEnableFromView(i);
        int i2 = this.mTintStyle;
        if (i2 == 2) {
            tintStyleLoadData();
        } else if (i2 != 4) {
            int width = this.mView.getWidth();
            int height = this.mView.getHeight();
            if (width == 0 || height == 0) {
                recycleBitmap();
                return;
            }
            createBitmap(width, height);
            initBitmap(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void restoreOriginalDrawable() {
        clear();
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHoverCorner(float f) {
        this.mTintStyle = f != 0.0f ? 4 : this.mTintStyle;
        this.mHoverRadius = f;
    }
}
