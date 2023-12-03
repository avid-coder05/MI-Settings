package miuix.animation;

import miuix.animation.base.AnimConfig;

/* loaded from: classes5.dex */
public interface IVisibleStyle extends IStateContainer {

    /* loaded from: classes5.dex */
    public enum VisibleType {
        SHOW,
        HIDE
    }

    void hide(AnimConfig... animConfigArr);

    IVisibleStyle setAlpha(float f, VisibleType... visibleTypeArr);

    IVisibleStyle setFlags(long j);

    IVisibleStyle setHide();

    IVisibleStyle setScale(float f, VisibleType... visibleTypeArr);

    IVisibleStyle setShow();

    void show(AnimConfig... animConfigArr);
}
