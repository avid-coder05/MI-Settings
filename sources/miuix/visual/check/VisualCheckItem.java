package miuix.visual.check;

import android.view.MotionEvent;

/* loaded from: classes5.dex */
public interface VisualCheckItem {
    void onChecked(boolean z);

    void onVisualCheckBoxTouchEvent(VisualCheckBox visualCheckBox, MotionEvent motionEvent);
}
