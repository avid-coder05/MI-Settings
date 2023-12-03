package com.google.android.setupdesign.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupdesign.R$attr;
import com.google.android.setupdesign.R$id;

/* loaded from: classes2.dex */
public final class LayoutStyler {
    @TargetApi(17)
    public static void applyPartnerCustomizationExtraPaddingStyle(View view) {
        if (view == null) {
            return;
        }
        Context context = view.getContext();
        PartnerConfigHelper partnerConfigHelper = PartnerConfigHelper.get(context);
        PartnerConfig partnerConfig = PartnerConfig.CONFIG_LAYOUT_MARGIN_START;
        boolean isPartnerConfigAvailable = partnerConfigHelper.isPartnerConfigAvailable(partnerConfig);
        PartnerConfigHelper partnerConfigHelper2 = PartnerConfigHelper.get(context);
        PartnerConfig partnerConfig2 = PartnerConfig.CONFIG_LAYOUT_MARGIN_END;
        boolean isPartnerConfigAvailable2 = partnerConfigHelper2.isPartnerConfigAvailable(partnerConfig2);
        if (PartnerStyleHelper.shouldApplyPartnerHeavyThemeResource(view)) {
            if (isPartnerConfigAvailable || isPartnerConfigAvailable2) {
                TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{R$attr.sudMarginStart, R$attr.sudMarginEnd});
                int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(0, 0);
                int dimensionPixelSize2 = obtainStyledAttributes.getDimensionPixelSize(1, 0);
                obtainStyledAttributes.recycle();
                int dimension = isPartnerConfigAvailable ? ((int) PartnerConfigHelper.get(context).getDimension(context, partnerConfig)) - dimensionPixelSize : view.getPaddingStart();
                int dimension2 = isPartnerConfigAvailable2 ? ((int) PartnerConfigHelper.get(context).getDimension(context, partnerConfig2)) - dimensionPixelSize2 : view.getPaddingEnd();
                if (dimension == view.getPaddingStart() && dimension2 == view.getPaddingEnd()) {
                    return;
                }
                int paddingTop = view.getPaddingTop();
                if (view.getId() == R$id.sud_layout_content) {
                    dimension2 = dimension;
                }
                view.setPadding(dimension, paddingTop, dimension2, view.getPaddingBottom());
            }
        }
    }

    @TargetApi(17)
    public static void applyPartnerCustomizationLayoutPaddingStyle(View view) {
        if (view == null) {
            return;
        }
        Context context = view.getContext();
        PartnerConfigHelper partnerConfigHelper = PartnerConfigHelper.get(context);
        PartnerConfig partnerConfig = PartnerConfig.CONFIG_LAYOUT_MARGIN_START;
        boolean isPartnerConfigAvailable = partnerConfigHelper.isPartnerConfigAvailable(partnerConfig);
        PartnerConfigHelper partnerConfigHelper2 = PartnerConfigHelper.get(context);
        PartnerConfig partnerConfig2 = PartnerConfig.CONFIG_LAYOUT_MARGIN_END;
        boolean isPartnerConfigAvailable2 = partnerConfigHelper2.isPartnerConfigAvailable(partnerConfig2);
        if (PartnerStyleHelper.shouldApplyPartnerHeavyThemeResource(view)) {
            if (isPartnerConfigAvailable || isPartnerConfigAvailable2) {
                int dimension = isPartnerConfigAvailable ? (int) PartnerConfigHelper.get(context).getDimension(context, partnerConfig) : view.getPaddingStart();
                int dimension2 = isPartnerConfigAvailable2 ? (int) PartnerConfigHelper.get(context).getDimension(context, partnerConfig2) : view.getPaddingEnd();
                if (dimension == view.getPaddingStart() && dimension2 == view.getPaddingEnd()) {
                    return;
                }
                view.setPadding(dimension, view.getPaddingTop(), dimension2, view.getPaddingBottom());
            }
        }
    }
}
