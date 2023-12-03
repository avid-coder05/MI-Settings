package miuix.appcompat.app;

import java.util.Collection;
import miuix.animation.listener.UpdateInfo;

/* loaded from: classes5.dex */
public interface ActionBarTransitionListener {
    void onActionBarMove(float f, float f2);

    void onTransitionBegin(Object obj);

    void onTransitionComplete(Object obj);

    void onTransitionUpdate(Object obj, Collection<UpdateInfo> collection);
}
