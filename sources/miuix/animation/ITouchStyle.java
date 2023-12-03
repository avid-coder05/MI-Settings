package miuix.animation;

import android.view.MotionEvent;
import android.view.View;
import miuix.animation.base.AnimConfig;

/* loaded from: classes5.dex */
public interface ITouchStyle extends IStateContainer {

    /* loaded from: classes5.dex */
    public enum TouchRectGravity {
        TOP_LEFT,
        TOP_CENTER,
        CENTER_LEFT,
        CENTER_IN_PARENT
    }

    /* loaded from: classes5.dex */
    public enum TouchType {
        UP,
        DOWN
    }

    ITouchStyle clearTintColor();

    void handleTouchOf(View view, View.OnClickListener onClickListener, AnimConfig... animConfigArr);

    void handleTouchOf(View view, boolean z, AnimConfig... animConfigArr);

    void handleTouchOf(View view, AnimConfig... animConfigArr);

    void onMotionEvent(MotionEvent motionEvent);

    ITouchStyle setAlpha(float f, TouchType... touchTypeArr);

    ITouchStyle setBackgroundColor(float f, float f2, float f3, float f4);

    ITouchStyle setBackgroundColor(int i);

    ITouchStyle setScale(float f, TouchType... touchTypeArr);

    ITouchStyle setTint(float f, float f2, float f3, float f4);

    ITouchStyle setTint(int i);

    ITouchStyle setTintMode(int i);

    void touchDown(AnimConfig... animConfigArr);

    void touchUp(AnimConfig... animConfigArr);
}
