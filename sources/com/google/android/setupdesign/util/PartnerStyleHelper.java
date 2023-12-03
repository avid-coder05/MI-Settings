package com.google.android.setupdesign.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.view.View;
import com.google.android.setupcompat.PartnerCustomizationLayout;
import com.google.android.setupcompat.R$styleable;
import com.google.android.setupcompat.internal.TemplateLayout;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.R$attr;
import com.google.android.setupdesign.R$id;
import java.util.Locale;

/* loaded from: classes2.dex */
public final class PartnerStyleHelper {
    private static TemplateLayout findLayoutFromActivity(Activity activity) {
        View findViewById;
        if (activity == null || (findViewById = activity.findViewById(R$id.suc_layout_status)) == null) {
            return null;
        }
        return (TemplateLayout) findViewById.getParent();
    }

    static boolean getDynamicColorAttributeFromTheme(Context context) {
        try {
            TemplateLayout findLayoutFromActivity = findLayoutFromActivity(PartnerCustomizationLayout.lookupActivityFromContext(context));
            if (findLayoutFromActivity instanceof GlifLayout) {
                return ((GlifLayout) findLayoutFromActivity).shouldApplyDynamicColor();
            }
        } catch (ClassCastException | IllegalArgumentException unused) {
        }
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{R$attr.sucFullDynamicColor});
        boolean hasValue = obtainStyledAttributes.hasValue(R$styleable.SucPartnerCustomizationLayout_sucFullDynamicColor);
        obtainStyledAttributes.recycle();
        return hasValue;
    }

    public static int getLayoutGravity(Context context) {
        String string = PartnerConfigHelper.get(context).getString(context, PartnerConfig.CONFIG_LAYOUT_GRAVITY);
        if (string == null) {
            return 0;
        }
        String lowerCase = string.toLowerCase(Locale.ROOT);
        lowerCase.hashCode();
        if (lowerCase.equals("center")) {
            return 17;
        }
        return !lowerCase.equals("start") ? 0 : 8388611;
    }

    public static boolean isPartnerHeavyThemeLayout(TemplateLayout templateLayout) {
        if (templateLayout instanceof GlifLayout) {
            return ((GlifLayout) templateLayout).shouldApplyPartnerHeavyThemeResource();
        }
        return false;
    }

    public static boolean isPartnerLightThemeLayout(TemplateLayout templateLayout) {
        if (templateLayout instanceof PartnerCustomizationLayout) {
            return ((PartnerCustomizationLayout) templateLayout).shouldApplyPartnerResource();
        }
        return false;
    }

    static boolean shouldApplyPartnerHeavyThemeResource(Context context) {
        try {
            TemplateLayout findLayoutFromActivity = findLayoutFromActivity(PartnerCustomizationLayout.lookupActivityFromContext(context));
            if (findLayoutFromActivity instanceof GlifLayout) {
                return ((GlifLayout) findLayoutFromActivity).shouldApplyPartnerHeavyThemeResource();
            }
        } catch (ClassCastException | IllegalArgumentException unused) {
        }
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{R$attr.sudUsePartnerHeavyTheme});
        boolean z = obtainStyledAttributes.getBoolean(0, false);
        obtainStyledAttributes.recycle();
        return shouldApplyPartnerResource(context) && (z || PartnerConfigHelper.shouldApplyExtendedPartnerConfig(context));
    }

    public static boolean shouldApplyPartnerHeavyThemeResource(View view) {
        if (view == null) {
            return false;
        }
        return view instanceof GlifLayout ? isPartnerHeavyThemeLayout((GlifLayout) view) : shouldApplyPartnerHeavyThemeResource(view.getContext());
    }

    private static boolean shouldApplyPartnerResource(Context context) {
        if (Build.VERSION.SDK_INT >= 29 && PartnerConfigHelper.get(context).isAvailable()) {
            Activity activity = null;
            try {
                activity = PartnerCustomizationLayout.lookupActivityFromContext(context);
                if (activity != null) {
                    TemplateLayout findLayoutFromActivity = findLayoutFromActivity(activity);
                    if (findLayoutFromActivity instanceof PartnerCustomizationLayout) {
                        return ((PartnerCustomizationLayout) findLayoutFromActivity).shouldApplyPartnerResource();
                    }
                }
            } catch (ClassCastException | IllegalArgumentException unused) {
            }
            boolean isAnySetupWizard = activity != null ? WizardManagerHelper.isAnySetupWizard(activity.getIntent()) : false;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{R$attr.sucUsePartnerResource});
            boolean z = obtainStyledAttributes.getBoolean(0, true);
            obtainStyledAttributes.recycle();
            return isAnySetupWizard || z;
        }
        return false;
    }

    public static boolean shouldApplyPartnerResource(View view) {
        if (view == null) {
            return false;
        }
        return view instanceof PartnerCustomizationLayout ? isPartnerLightThemeLayout((PartnerCustomizationLayout) view) : shouldApplyPartnerResource(view.getContext());
    }

    public static boolean useDynamicColor(View view) {
        if (view == null) {
            return false;
        }
        return getDynamicColorAttributeFromTheme(view.getContext());
    }
}
