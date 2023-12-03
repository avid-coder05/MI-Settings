package miuix.androidbasewidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import miuix.androidbasewidget.R$styleable;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.base.AnimConfig;

/* loaded from: classes5.dex */
public class AppCompatCheckedTextView extends androidx.appcompat.widget.AppCompatCheckedTextView {
    public AppCompatCheckedTextView(Context context) {
        this(context, null);
    }

    public AppCompatCheckedTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AppCompatCheckedTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.CheckedTextView, i, 0);
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.CheckedTextView_checkMarkCompat, 0);
        if (resourceId != 0) {
            setCheckMarkDrawable(resourceId);
        }
        obtainStyledAttributes.recycle();
        Folme.useAt(this).hover().setEffect(IHoverStyle.HoverEffect.NORMAL).handleHoverOf(this, new AnimConfig[0]);
    }
}
