package miuix.androidbasewidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import miuix.androidbasewidget.R$attr;
import miuix.androidbasewidget.R$style;
import miuix.androidbasewidget.R$styleable;
import miuix.reflect.Reflects;

/* loaded from: classes5.dex */
public class ProgressBar extends android.widget.ProgressBar {
    private Drawable mIndeterminateDrawableOrignal;

    public ProgressBar(Context context) {
        this(context, null);
    }

    public ProgressBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.progressBarStyle);
    }

    public ProgressBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        postConstruct(context, attributeSet, i);
    }

    public void postConstruct(Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.ProgressBar, i, R$style.Widget_ProgressBar_Horizontal_DayNight);
        Drawable drawable = this.mIndeterminateDrawableOrignal;
        if (drawable != null && drawable.getClass().getName().equals("android.graphics.drawable.AnimatedRotateDrawable")) {
            int i2 = obtainStyledAttributes.getInt(R$styleable.ProgressBar_indeterminateFramesCount, 48);
            Class<?> cls = drawable.getClass();
            Class cls2 = Integer.TYPE;
            Reflects.invoke(drawable, Reflects.getMethod(cls, "setFramesCount", cls2), Integer.valueOf(i2));
            Reflects.invoke(drawable, Reflects.getMethod(cls, "setFramesDuration", cls2), Integer.valueOf(obtainStyledAttributes.getInt(R$styleable.ProgressBar_indeterminateFramesDuration, 25)));
        }
        obtainStyledAttributes.recycle();
    }

    @Override // android.widget.ProgressBar
    public void setIndeterminateDrawable(Drawable drawable) {
        super.setIndeterminateDrawable(drawable);
        if (this.mIndeterminateDrawableOrignal != drawable) {
            this.mIndeterminateDrawableOrignal = drawable;
        }
    }
}
