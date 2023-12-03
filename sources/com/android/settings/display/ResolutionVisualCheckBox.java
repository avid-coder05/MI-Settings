package com.android.settings.display;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import miuix.visual.check.VisualCheckBox;

/* loaded from: classes.dex */
public class ResolutionVisualCheckBox extends VisualCheckBox {
    private boolean mEnabled;

    public ResolutionVisualCheckBox(Context context) {
        this(context, null);
    }

    public ResolutionVisualCheckBox(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ResolutionVisualCheckBox(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // miuix.visual.check.VisualCheckBox, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mEnabled) {
            return super.onTouchEvent(motionEvent);
        }
        return false;
    }

    @Override // miuix.visual.check.VisualCheckBox, android.view.View
    public boolean performClick() {
        if (this.mEnabled) {
            return super.performClick();
        }
        return false;
    }

    public void setCheckEnabled(boolean z) {
        this.mEnabled = z;
    }
}
