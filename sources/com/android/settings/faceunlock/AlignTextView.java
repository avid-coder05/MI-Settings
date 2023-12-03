package com.android.settings.faceunlock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/* loaded from: classes.dex */
public class AlignTextView extends TextView {
    private Layout.Alignment align;

    public AlignTextView(Context context) {
        this(context, null);
    }

    public AlignTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.align = Layout.Alignment.ALIGN_CENTER;
        setTextIsSelectable(false);
    }

    @Override // android.widget.TextView, android.view.View
    @SuppressLint({"DrawAllocation"})
    protected void onDraw(Canvas canvas) {
        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        new StaticLayout(getText(), paint, (getMeasuredWidth() - getPaddingRight()) - getPaddingEnd(), this.align, 1.0f, 0.0f, false).draw(canvas);
    }

    public void setAlign(Layout.Alignment alignment) {
        this.align = alignment;
        invalidate();
    }
}
