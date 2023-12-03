package miuix.stretchablewidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/* loaded from: classes5.dex */
public class WidgetContainer extends LinearLayout {
    public WidgetContainer(Context context) {
        this(context, null);
    }

    public WidgetContainer(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WidgetContainer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public float getWidgetHeight() {
        return getHeight();
    }

    public void setWidgetHeight(float f) {
        if (f < 0.0f) {
            return;
        }
        getLayoutParams().height = (int) f;
        requestLayout();
    }
}
