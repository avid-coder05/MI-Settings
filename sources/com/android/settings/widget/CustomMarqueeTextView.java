package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/* loaded from: classes2.dex */
public class CustomMarqueeTextView extends TextView {
    public CustomMarqueeTextView(Context context) {
        super(context);
    }

    public CustomMarqueeTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomMarqueeTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public CustomMarqueeTextView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    @Override // android.view.View
    public boolean isFocused() {
        return true;
    }
}
