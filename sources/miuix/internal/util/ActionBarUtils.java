package miuix.internal.util;

import android.view.View;
import android.view.ViewGroup;
import miuix.appcompat.internal.app.widget.ActionBarOverlayLayout;

/* loaded from: classes5.dex */
public class ActionBarUtils {
    public static ViewGroup getActionBarOverlayLayout(View view) {
        while (view != null) {
            if (view instanceof ActionBarOverlayLayout) {
                return (ActionBarOverlayLayout) view;
            }
            view = view.getParent() instanceof View ? (View) view.getParent() : null;
        }
        return null;
    }
}
