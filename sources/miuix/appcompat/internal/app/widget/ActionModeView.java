package miuix.appcompat.internal.app.widget;

import android.view.ActionMode;
import miuix.view.ActionModeAnimationListener;

/* loaded from: classes5.dex */
public interface ActionModeView {
    void addAnimationListener(ActionModeAnimationListener actionModeAnimationListener);

    void animateToVisibility(boolean z);

    void closeMode();

    void initForMode(ActionMode actionMode);

    void killMode();
}
