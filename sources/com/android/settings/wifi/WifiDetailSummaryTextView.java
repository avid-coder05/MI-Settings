package com.android.settings.wifi;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/* loaded from: classes2.dex */
public class WifiDetailSummaryTextView extends TextView {
    public WifiDetailSummaryTextView(Context context) {
        super(context);
    }

    public WifiDetailSummaryTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public WifiDetailSummaryTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.view.View
    public boolean isFocused() {
        return true;
    }
}
