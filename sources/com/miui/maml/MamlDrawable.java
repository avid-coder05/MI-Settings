package com.miui.maml;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import java.lang.ref.WeakReference;

/* loaded from: classes2.dex */
public class MamlDrawable extends Drawable {
    private static WeakReference<Drawable> sLayerBadgeDrawableBmpRef;
    protected Drawable mBadgeDrawable;
    protected Rect mBadgeLocation;
    protected ColorFilter mColorFilter;
    protected int mHeight;
    protected int mIntrinsicHeight;
    protected int mIntrinsicWidth;
    protected Runnable mInvalidateSelf = new Runnable() { // from class: com.miui.maml.MamlDrawable.1
        @Override // java.lang.Runnable
        public void run() {
            MamlDrawable.this.invalidateSelf();
        }
    };
    protected MamlDrawableState mState;
    protected int mWidth;

    /* loaded from: classes2.dex */
    public static class MamlDrawableState extends Drawable.ConstantState {
        protected Drawable mStateBadgeDrawable;
        protected Rect mStateBadgeLocation;

        protected MamlDrawable createDrawable() {
            throw null;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return 0;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            MamlDrawable createDrawable = createDrawable();
            Rect rect = null;
            if (createDrawable == null) {
                return null;
            }
            Drawable drawable = this.mStateBadgeDrawable;
            Drawable mutate = drawable != null ? drawable.mutate() : null;
            if (this.mStateBadgeLocation != null) {
                Rect rect2 = this.mStateBadgeLocation;
                rect = new Rect(rect2.left, rect2.top, rect2.right, rect2.bottom);
            }
            createDrawable.setBadgeInfo(mutate, rect);
            return createDrawable;
        }
    }

    public void cleanUp() {
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        drawIcon(canvas);
        try {
            Drawable drawable = this.mBadgeDrawable;
            if (drawable != null) {
                Rect rect = this.mBadgeLocation;
                if (rect != null) {
                    drawable.setBounds(0, 0, rect.width(), this.mBadgeLocation.height());
                    canvas.save();
                    Rect rect2 = this.mBadgeLocation;
                    canvas.translate(rect2.left, rect2.top);
                    this.mBadgeDrawable.draw(canvas);
                    canvas.restore();
                } else {
                    drawable.setBounds(0, 0, this.mIntrinsicWidth, this.mIntrinsicHeight);
                    this.mBadgeDrawable.draw(canvas);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e2) {
            e2.printStackTrace();
        }
    }

    protected void drawIcon(Canvas canvas) {
        throw null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void finalize() throws Throwable {
        cleanUp();
        super.finalize();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return this.mState;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mIntrinsicHeight;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mIntrinsicWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    public void setBadgeInfo(Drawable drawable, Rect rect) {
        if (rect != null && (rect.left < 0 || rect.top < 0 || rect.width() > this.mIntrinsicWidth || rect.height() > this.mIntrinsicHeight)) {
            throw new IllegalArgumentException("Badge location " + rect + " not in badged drawable bounds " + new Rect(0, 0, this.mIntrinsicWidth, this.mIntrinsicHeight));
        }
        if (drawable instanceof LayerDrawable) {
            WeakReference<Drawable> weakReference = sLayerBadgeDrawableBmpRef;
            Drawable drawable2 = weakReference != null ? weakReference.get() : null;
            if (drawable2 != null) {
                drawable = drawable2.mutate();
            } else {
                Bitmap createBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                drawable = new BitmapDrawable(createBitmap);
                sLayerBadgeDrawableBmpRef = new WeakReference<>(drawable);
            }
        }
        ColorFilter colorFilter = this.mColorFilter;
        if (colorFilter != null && drawable != null) {
            drawable.setColorFilter(colorFilter);
        }
        this.mBadgeDrawable = drawable;
        this.mBadgeLocation = rect;
        MamlDrawableState mamlDrawableState = this.mState;
        mamlDrawableState.mStateBadgeDrawable = drawable;
        mamlDrawableState.mStateBadgeLocation = rect;
    }

    @Override // android.graphics.drawable.Drawable
    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        this.mWidth = i3 - i;
        this.mHeight = i4 - i2;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mColorFilter = colorFilter;
    }

    public void setIntrinsicSize(int i, int i2) {
        this.mIntrinsicWidth = i;
        this.mIntrinsicHeight = i2;
    }
}
