package com.google.android.setupcompat.template;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.StateSet;
import android.widget.Button;
import com.google.android.setupcompat.R$style;
import com.google.android.setupcompat.internal.FooterButtonPartnerConfig;
import com.google.android.setupcompat.internal.Preconditions;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;

/* loaded from: classes2.dex */
public class FooterButtonStyleUtils {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void applyButtonPartnerResources(Context context, Button button, boolean z, boolean z2, FooterButtonPartnerConfig footerButtonPartnerConfig) {
        if (!z) {
            if (button.isEnabled()) {
                updateButtonTextEnabledColorWithPartnerConfig(context, button, footerButtonPartnerConfig.getButtonTextColorConfig());
            }
            updateButtonBackgroundWithPartnerConfig(context, button, footerButtonPartnerConfig.getButtonBackgroundConfig(), footerButtonPartnerConfig.getButtonDisableAlphaConfig(), footerButtonPartnerConfig.getButtonDisableBackgroundConfig());
        }
        updateButtonRippleColorWithPartnerConfig(context, button, z, footerButtonPartnerConfig.getButtonTextColorConfig(), footerButtonPartnerConfig.getButtonRippleColorAlphaConfig());
        updateButtonTextSizeWithPartnerConfig(context, button, footerButtonPartnerConfig.getButtonTextSizeConfig());
        updateButtonMinHeightWithPartnerConfig(context, button, footerButtonPartnerConfig.getButtonMinHeightConfig());
        updateButtonTypeFaceWithPartnerConfig(context, button, footerButtonPartnerConfig.getButtonTextTypeFaceConfig(), footerButtonPartnerConfig.getButtonTextStyleConfig());
        updateButtonRadiusWithPartnerConfig(context, button, footerButtonPartnerConfig.getButtonRadiusConfig());
        updateButtonIconWithPartnerConfig(context, button, footerButtonPartnerConfig.getButtonIconConfig(), z2);
    }

    public static void applyPrimaryButtonPartnerResource(Context context, Button button, boolean z) {
        applyButtonPartnerResources(context, button, z, true, new FooterButtonPartnerConfig.Builder(null).setPartnerTheme(R$style.SucPartnerCustomizationButton_Primary).setButtonBackgroundConfig(PartnerConfig.CONFIG_FOOTER_PRIMARY_BUTTON_BG_COLOR).setButtonDisableAlphaConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_DISABLED_ALPHA).setButtonDisableBackgroundConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_DISABLED_BG_COLOR).setButtonRadiusConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_RADIUS).setButtonRippleColorAlphaConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_RIPPLE_COLOR_ALPHA).setTextColorConfig(PartnerConfig.CONFIG_FOOTER_PRIMARY_BUTTON_TEXT_COLOR).setTextSizeConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_TEXT_SIZE).setButtonMinHeight(PartnerConfig.CONFIG_FOOTER_BUTTON_MIN_HEIGHT).setTextTypeFaceConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_FONT_FAMILY).setTextStyleConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_TEXT_STYLE).build());
    }

    private static int convertRgbToArgb(int i, float f) {
        return Color.argb((int) (f * 255.0f), Color.red(i), Color.green(i), Color.blue(i));
    }

    public static GradientDrawable getGradientDrawable(Button button) {
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable background = button.getBackground();
            if (background instanceof InsetDrawable) {
                return (GradientDrawable) ((LayerDrawable) ((InsetDrawable) background).getDrawable()).getDrawable(0);
            }
            if (background instanceof RippleDrawable) {
                RippleDrawable rippleDrawable = (RippleDrawable) background;
                return rippleDrawable.getDrawable(0) instanceof GradientDrawable ? (GradientDrawable) rippleDrawable.getDrawable(0) : (GradientDrawable) ((InsetDrawable) rippleDrawable.getDrawable(0)).getDrawable();
            }
            return null;
        }
        return null;
    }

    static RippleDrawable getRippleDrawable(Button button) {
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable background = button.getBackground();
            if (background instanceof InsetDrawable) {
                return (RippleDrawable) ((InsetDrawable) background).getDrawable();
            }
            if (background instanceof RippleDrawable) {
                return (RippleDrawable) background;
            }
            return null;
        }
        return null;
    }

    private static void setButtonIcon(Button button, Drawable drawable, boolean z) {
        Drawable drawable2;
        if (button == null) {
            return;
        }
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
        if (z) {
            drawable2 = drawable;
            drawable = null;
        } else {
            drawable2 = null;
        }
        if (Build.VERSION.SDK_INT >= 17) {
            button.setCompoundDrawablesRelative(drawable, null, drawable2, null);
        } else {
            button.setCompoundDrawables(drawable, null, drawable2, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void updateButtonBackground(Button button, int i) {
        button.getBackground().mutate().setColorFilter(i, PorterDuff.Mode.SRC_ATOP);
    }

    @TargetApi(29)
    static void updateButtonBackgroundTintList(Context context, Button button, int i, float f, int i2) {
        int[] iArr = {-16842910};
        int[] iArr2 = new int[0];
        if (i != 0) {
            if (f <= 0.0f) {
                TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{16842803});
                f = obtainStyledAttributes.getFloat(0, 0.26f);
                obtainStyledAttributes.recycle();
            }
            if (i2 == 0) {
                i2 = i;
            }
            ColorStateList colorStateList = new ColorStateList(new int[][]{iArr, iArr2}, new int[]{convertRgbToArgb(i2, f), i});
            button.getBackground().mutate().setState(new int[0]);
            button.refreshDrawableState();
            button.setBackgroundTintList(colorStateList);
        }
    }

    @TargetApi(29)
    static void updateButtonBackgroundWithPartnerConfig(Context context, Button button, PartnerConfig partnerConfig, PartnerConfig partnerConfig2, PartnerConfig partnerConfig3) {
        Preconditions.checkArgument(Build.VERSION.SDK_INT >= 29, "Update button background only support on sdk Q or higher");
        updateButtonBackgroundTintList(context, button, PartnerConfigHelper.get(context).getColor(context, partnerConfig), PartnerConfigHelper.get(context).getFraction(context, partnerConfig2, 0.0f), PartnerConfigHelper.get(context).getColor(context, partnerConfig3));
    }

    static void updateButtonIconWithPartnerConfig(Context context, Button button, PartnerConfig partnerConfig, boolean z) {
        if (button == null) {
            return;
        }
        setButtonIcon(button, partnerConfig != null ? PartnerConfigHelper.get(context).getDrawable(context, partnerConfig) : null, z);
    }

    static void updateButtonMinHeightWithPartnerConfig(Context context, Button button, PartnerConfig partnerConfig) {
        if (PartnerConfigHelper.get(context).isPartnerConfigAvailable(partnerConfig)) {
            float dimension = PartnerConfigHelper.get(context).getDimension(context, partnerConfig);
            if (dimension > 0.0f) {
                button.setMinHeight((int) dimension);
            }
        }
    }

    static void updateButtonRadiusWithPartnerConfig(Context context, Button button, PartnerConfig partnerConfig) {
        if (Build.VERSION.SDK_INT >= 24) {
            float dimension = PartnerConfigHelper.get(context).getDimension(context, partnerConfig);
            GradientDrawable gradientDrawable = getGradientDrawable(button);
            if (gradientDrawable != null) {
                gradientDrawable.setCornerRadius(dimension);
            }
        }
    }

    private static void updateButtonRippleColor(Button button, int i, float f) {
        RippleDrawable rippleDrawable;
        if (Build.VERSION.SDK_INT < 21 || (rippleDrawable = getRippleDrawable(button)) == null) {
            return;
        }
        rippleDrawable.setColor(new ColorStateList(new int[][]{new int[]{16842919}, StateSet.NOTHING}, new int[]{convertRgbToArgb(i, f), 0}));
    }

    @TargetApi(29)
    static void updateButtonRippleColorWithPartnerConfig(Context context, Button button, boolean z, PartnerConfig partnerConfig, PartnerConfig partnerConfig2) {
        if (Build.VERSION.SDK_INT >= 21) {
            updateButtonRippleColor(button, z ? button.getTextColors().getDefaultColor() : PartnerConfigHelper.get(context).getColor(context, partnerConfig), PartnerConfigHelper.get(context).getFraction(context, partnerConfig2));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void updateButtonTextDisableColor(Button button, ColorStateList colorStateList) {
        button.setTextColor(colorStateList);
    }

    static void updateButtonTextEnabledColor(Button button, int i) {
        if (i != 0) {
            button.setTextColor(ColorStateList.valueOf(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void updateButtonTextEnabledColorWithPartnerConfig(Context context, Button button, PartnerConfig partnerConfig) {
        updateButtonTextEnabledColor(button, PartnerConfigHelper.get(context).getColor(context, partnerConfig));
    }

    static void updateButtonTextSizeWithPartnerConfig(Context context, Button button, PartnerConfig partnerConfig) {
        float dimension = PartnerConfigHelper.get(context).getDimension(context, partnerConfig);
        if (dimension > 0.0f) {
            button.setTextSize(0, dimension);
        }
    }

    static void updateButtonTypeFaceWithPartnerConfig(Context context, Button button, PartnerConfig partnerConfig, PartnerConfig partnerConfig2) {
        Typeface create = Typeface.create(PartnerConfigHelper.get(context).getString(context, partnerConfig), PartnerConfigHelper.get(context).isPartnerConfigAvailable(partnerConfig2) ? PartnerConfigHelper.get(context).getInteger(context, partnerConfig2, 0) : 0);
        if (create != null) {
            button.setTypeface(create);
        }
    }
}
