package androidx.mediarouter.app;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.mediarouter.R$attr;
import androidx.mediarouter.R$color;
import androidx.mediarouter.R$drawable;
import androidx.mediarouter.R$style;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class MediaRouterThemeHelper {
    private static final int COLOR_DARK_ON_LIGHT_BACKGROUND_RES_ID = R$color.mr_dynamic_dialog_icon_light;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Context createThemedButtonContext(Context context) {
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, getRouterThemeId(context));
        int themeResource = getThemeResource(contextThemeWrapper, R$attr.mediaRouteTheme);
        return themeResource != 0 ? new ContextThemeWrapper(contextThemeWrapper, themeResource) : contextThemeWrapper;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Context createThemedDialogContext(Context context, int theme, boolean alertDialog) {
        if (theme == 0) {
            theme = getThemeResource(context, !alertDialog ? androidx.appcompat.R$attr.dialogTheme : androidx.appcompat.R$attr.alertDialogTheme);
        }
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, theme);
        return getThemeResource(contextThemeWrapper, R$attr.mediaRouteTheme) != 0 ? new ContextThemeWrapper(contextThemeWrapper, getRouterThemeId(contextThemeWrapper)) : contextThemeWrapper;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int createThemedDialogStyle(Context context) {
        int themeResource = getThemeResource(context, R$attr.mediaRouteTheme);
        return themeResource == 0 ? getRouterThemeId(context) : themeResource;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getButtonTextColor(Context context) {
        int themeColor = getThemeColor(context, 0, androidx.appcompat.R$attr.colorPrimary);
        return ColorUtils.calculateContrast(themeColor, getThemeColor(context, 0, 16842801)) < 3.0d ? getThemeColor(context, 0, androidx.appcompat.R$attr.colorAccent) : themeColor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Drawable getCheckBoxDrawableIcon(Context context) {
        return getIconByDrawableId(context, R$drawable.mr_cast_checkbox);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getControllerColor(Context context, int style) {
        return ColorUtils.calculateContrast(-1, getThemeColor(context, style, androidx.appcompat.R$attr.colorPrimary)) >= 3.0d ? -1 : -570425344;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Drawable getDefaultDrawableIcon(Context context) {
        return getIconByAttrId(context, R$attr.mediaRouteDefaultIconDrawable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static float getDisabledAlpha(Context context) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(16842803, typedValue, true)) {
            return typedValue.getFloat();
        }
        return 0.5f;
    }

    private static Drawable getIconByAttrId(Context context, int attrId) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{attrId});
        Drawable wrap = DrawableCompat.wrap(AppCompatResources.getDrawable(context, obtainStyledAttributes.getResourceId(0, 0)));
        if (isLightTheme(context)) {
            DrawableCompat.setTint(wrap, ContextCompat.getColor(context, COLOR_DARK_ON_LIGHT_BACKGROUND_RES_ID));
        }
        obtainStyledAttributes.recycle();
        return wrap;
    }

    private static Drawable getIconByDrawableId(Context context, int drawableId) {
        Drawable wrap = DrawableCompat.wrap(AppCompatResources.getDrawable(context, drawableId));
        if (isLightTheme(context)) {
            DrawableCompat.setTint(wrap, ContextCompat.getColor(context, COLOR_DARK_ON_LIGHT_BACKGROUND_RES_ID));
        }
        return wrap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Drawable getMuteButtonDrawableIcon(Context context) {
        return getIconByDrawableId(context, R$drawable.mr_cast_mute_button);
    }

    private static int getRouterThemeId(Context context) {
        return isLightTheme(context) ? getControllerColor(context, 0) == -570425344 ? R$style.Theme_MediaRouter_Light : R$style.Theme_MediaRouter_Light_DarkControlPanel : getControllerColor(context, 0) == -570425344 ? R$style.Theme_MediaRouter_LightControlPanel : R$style.Theme_MediaRouter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Drawable getSpeakerDrawableIcon(Context context) {
        return getIconByAttrId(context, R$attr.mediaRouteSpeakerIconDrawable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Drawable getSpeakerGroupDrawableIcon(Context context) {
        return getIconByAttrId(context, R$attr.mediaRouteSpeakerGroupIconDrawable);
    }

    private static int getThemeColor(Context context, int style, int attr) {
        if (style != 0) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(style, new int[]{attr});
            int color = obtainStyledAttributes.getColor(0, 0);
            obtainStyledAttributes.recycle();
            if (color != 0) {
                return color;
            }
        }
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.resourceId != 0 ? context.getResources().getColor(typedValue.resourceId) : typedValue.data;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getThemeResource(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(attr, typedValue, true)) {
            return typedValue.resourceId;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Drawable getTvDrawableIcon(Context context) {
        return getIconByAttrId(context, R$attr.mediaRouteTvIconDrawable);
    }

    private static boolean isLightTheme(Context context) {
        TypedValue typedValue = new TypedValue();
        return context.getTheme().resolveAttribute(androidx.appcompat.R$attr.isLightTheme, typedValue, true) && typedValue.data != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setDialogBackgroundColor(Context context, Dialog dialog) {
        dialog.getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(context, isLightTheme(context) ? R$color.mr_dynamic_dialog_background_light : R$color.mr_dynamic_dialog_background_dark));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setIndeterminateProgressBarColor(Context context, ProgressBar progressBar) {
        if (progressBar.isIndeterminate()) {
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, isLightTheme(context) ? R$color.mr_cast_progressbar_progress_and_thumb_light : R$color.mr_cast_progressbar_progress_and_thumb_dark), PorterDuff.Mode.SRC_IN);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setMediaControlsBackgroundColor(Context context, View mainControls, View groupControls, boolean hasGroup) {
        int themeColor = getThemeColor(context, 0, androidx.appcompat.R$attr.colorPrimary);
        int themeColor2 = getThemeColor(context, 0, androidx.appcompat.R$attr.colorPrimaryDark);
        if (hasGroup && getControllerColor(context, 0) == -570425344) {
            themeColor2 = themeColor;
            themeColor = -1;
        }
        mainControls.setBackgroundColor(themeColor);
        groupControls.setBackgroundColor(themeColor2);
        mainControls.setTag(Integer.valueOf(themeColor));
        groupControls.setTag(Integer.valueOf(themeColor2));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setVolumeSliderColor(Context context, MediaRouteVolumeSlider volumeSlider) {
        int color;
        int color2;
        if (isLightTheme(context)) {
            color = ContextCompat.getColor(context, R$color.mr_cast_progressbar_progress_and_thumb_light);
            color2 = ContextCompat.getColor(context, R$color.mr_cast_progressbar_background_light);
        } else {
            color = ContextCompat.getColor(context, R$color.mr_cast_progressbar_progress_and_thumb_dark);
            color2 = ContextCompat.getColor(context, R$color.mr_cast_progressbar_background_dark);
        }
        volumeSlider.setColor(color, color2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setVolumeSliderColor(Context context, MediaRouteVolumeSlider volumeSlider, View backgroundView) {
        int controllerColor = getControllerColor(context, 0);
        if (Color.alpha(controllerColor) != 255) {
            controllerColor = ColorUtils.compositeColors(controllerColor, ((Integer) backgroundView.getTag()).intValue());
        }
        volumeSlider.setColor(controllerColor);
    }
}
