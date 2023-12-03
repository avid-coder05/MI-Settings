package miuix.appcompat.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/* loaded from: classes5.dex */
public class TextAlignLayout extends LinearLayout {
    public TextAlignLayout(Context context) {
        super(context);
    }

    public TextAlignLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public TextAlignLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public TextAlignLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int childCount = getChildCount();
        boolean z = true;
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (z && (childAt instanceof TextView)) {
                TextView textView = (TextView) childAt;
                z = textView.getLineCount() <= 1;
                if (z) {
                    textView.setGravity(1);
                }
            }
        }
    }
}
