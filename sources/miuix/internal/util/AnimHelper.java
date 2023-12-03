package miuix.internal.util;

import android.text.TextUtils;
import android.view.View;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;

/* loaded from: classes5.dex */
public class AnimHelper {
    private static volatile boolean sIsDebugEnabled;

    public static void addPressAnim(View view) {
        addPressAnim(view, IHoverStyle.HoverEffect.NORMAL);
    }

    public static void addPressAnim(View view, IHoverStyle.HoverEffect hoverEffect) {
        Folme.useAt(view).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).handleTouchOf(view, new AnimConfig[0]);
        Folme.useAt(view).hover().setEffect(hoverEffect).handleHoverOf(view, new AnimConfig[0]);
    }

    public static void addPressAnimWithBg(View view) {
        ITouchStyle scale = Folme.useAt(view).touch().setTint(0.0f, 0.0f, 0.0f, 0.0f).setScale(1.0f, new ITouchStyle.TouchType[0]);
        IHoverStyle effect = Folme.useAt(view).hover().setTint(0.0f, 0.0f, 0.0f, 0.0f).setEffect(IHoverStyle.HoverEffect.NORMAL);
        if (ViewUtils.isNightMode(view.getContext())) {
            scale.setBackgroundColor(0.15f, 1.0f, 1.0f, 1.0f);
            effect.setBackgroundColor(0.15f, 1.0f, 1.0f, 1.0f);
        } else {
            scale.setBackgroundColor(0.08f, 0.0f, 0.0f, 0.0f);
            effect.setBackgroundColor(0.08f, 0.0f, 0.0f, 0.0f);
        }
        scale.handleTouchOf(view, new AnimConfig[0]);
        effect.handleHoverOf(view, new AnimConfig[0]);
    }

    public static boolean isDialogDebugInAndroidUIThreadEnabled() {
        return sIsDebugEnabled || TextUtils.equals("android.ui", Thread.currentThread().getName());
    }
}
