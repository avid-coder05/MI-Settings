package com.android.settings.display;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.R;

/* loaded from: classes.dex */
class ScreenColorBitMapBgView extends View {
    private Rect mAreaRect;
    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private int mOffset;

    public ScreenColorBitMapBgView(Context context) {
        this(context, null);
    }

    public ScreenColorBitMapBgView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ScreenColorBitMapBgView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        this.mBitmapPaint = new Paint();
        this.mBitmap = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.screen_color_preview)).getBitmap();
        this.mOffset = (int) context.getResources().getDimension(R.dimen.screen_color_preview_offset_new);
        this.mAreaRect = new Rect();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mBitmapPaint.setMaskFilter(null);
        this.mBitmapPaint.setAntiAlias(true);
        this.mBitmapPaint.setAlpha(255);
        Rect rect = this.mAreaRect;
        int i = this.mOffset;
        rect.set(i, i, getWidth() - this.mOffset, getHeight() - this.mOffset);
        canvas.drawBitmap(this.mBitmap, (Rect) null, this.mAreaRect, this.mBitmapPaint);
    }
}
