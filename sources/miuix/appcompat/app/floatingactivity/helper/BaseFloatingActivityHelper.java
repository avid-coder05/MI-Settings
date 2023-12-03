package miuix.appcompat.app.floatingactivity.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.floatingactivity.IActivitySwitcherAnimation;
import miuix.appcompat.app.floatingactivity.OnFloatingCallback;

/* loaded from: classes5.dex */
public abstract class BaseFloatingActivityHelper implements IActivitySwitcherAnimation {
    public static boolean isFloatingWindow(Context context) {
        return (context instanceof AppCompatActivity) && ((AppCompatActivity) context).isInFloatingWindowMode();
    }

    public abstract boolean delegateFinishFloatingActivityInternal();

    public abstract View getFloatingBrightPanel();

    public abstract ViewGroup.LayoutParams getFloatingLayoutParam();

    public abstract void hideFloatingBrightPanel();

    public abstract void hideFloatingDimBackground();

    @SuppressLint({"ClickableViewAccessibility"})
    public abstract void init(View view, boolean z);

    public abstract boolean isFloatingModeSupport();

    public abstract boolean onBackPressed();

    public abstract ViewGroup replaceSubDecor(View view, boolean z);

    public abstract void setEnableSwipToDismiss(boolean z);

    public abstract void setFloatingWindowMode(boolean z);

    public abstract void setOnFloatingCallback(OnFloatingCallback onFloatingCallback);

    public abstract void showFloatingBrightPanel();
}
