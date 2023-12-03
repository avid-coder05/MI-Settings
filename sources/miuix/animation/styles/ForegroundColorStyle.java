package miuix.animation.styles;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import miuix.animation.IAnimTarget;
import miuix.animation.R$id;
import miuix.animation.ViewTarget;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.utils.DeviceUtils;

/* loaded from: classes5.dex */
public class ForegroundColorStyle extends PropertyStyle {
    public static void end(IAnimTarget iAnimTarget, UpdateInfo updateInfo) {
        View view = getView(iAnimTarget);
        if (isInvalid(view)) {
            return;
        }
        TintDrawable tintDrawable = TintDrawable.get(view);
        int i = (int) updateInfo.animInfo.value;
        if (tintDrawable == null || Color.alpha(i) != 0) {
            return;
        }
        tintDrawable.restoreOriginalDrawable();
    }

    private static View getView(IAnimTarget iAnimTarget) {
        if (iAnimTarget instanceof ViewTarget) {
            return ((ViewTarget) iAnimTarget).getTargetObject();
        }
        return null;
    }

    private static boolean isInvalid(View view) {
        return view == null || Build.VERSION.SDK_INT < 23;
    }

    public static void start(IAnimTarget iAnimTarget, UpdateInfo updateInfo) {
        View view = getView(iAnimTarget);
        if (isInvalid(view)) {
            return;
        }
        int i = updateInfo.animInfo.tintMode;
        TintDrawable andGet = TintDrawable.setAndGet(view);
        Object tag = view.getTag(R$id.miuix_animation_tag_view_hover_corners);
        if ((tag instanceof Float) || (tag instanceof Integer)) {
            andGet.setHoverCorner(((Float) tag).floatValue());
        }
        if (DeviceUtils.getDeviceLevel() == 0 && i == -1) {
            i = 1;
        } else if (i == -1) {
            i = 0;
        }
        andGet.initTintBuffer(i & 3);
    }
}
