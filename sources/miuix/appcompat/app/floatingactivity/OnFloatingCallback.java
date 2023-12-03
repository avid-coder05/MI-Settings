package miuix.appcompat.app.floatingactivity;

import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes5.dex */
public interface OnFloatingCallback extends OnFloatingActivityCallback {
    void closeAllPage();

    void getSnapShotAndSetPanel(AppCompatActivity appCompatActivity);

    boolean isFirstPageEnterAnimExecuteEnable();

    boolean isFirstPageExitAnimExecuteEnable();

    void markActivityOpenEnterAnimExecuted(AppCompatActivity appCompatActivity);

    void onDragEnd();

    void onDragStart();

    void onHideBehindPage();
}
