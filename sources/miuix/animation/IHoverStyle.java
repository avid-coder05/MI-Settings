package miuix.animation;

import android.view.View;
import miuix.animation.base.AnimConfig;

/* loaded from: classes5.dex */
public interface IHoverStyle extends IStateContainer {

    /* loaded from: classes5.dex */
    public enum HoverEffect {
        NORMAL,
        FLOATED,
        FLOATED_WRAPPED
    }

    /* loaded from: classes5.dex */
    public enum HoverType {
        ENTER,
        EXIT
    }

    void handleHoverOf(View view, AnimConfig... animConfigArr);

    void hoverEnter(AnimConfig... animConfigArr);

    void hoverExit(AnimConfig... animConfigArr);

    IHoverStyle setBackgroundColor(float f, float f2, float f3, float f4);

    IHoverStyle setEffect(HoverEffect hoverEffect);

    void setFeedbackRadius(float f);

    IHoverStyle setTint(float f, float f2, float f3, float f4);
}
