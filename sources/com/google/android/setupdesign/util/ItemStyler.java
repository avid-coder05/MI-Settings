package com.google.android.setupdesign.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupdesign.R$id;
import com.google.android.setupdesign.util.TextViewPartnerStyler;

/* loaded from: classes2.dex */
public final class ItemStyler {
    @TargetApi(17)
    public static void applyPartnerCustomizationItemStyle(View view) {
        if (view != null && PartnerStyleHelper.shouldApplyPartnerHeavyThemeResource(view)) {
            applyPartnerCustomizationItemTitleStyle((TextView) view.findViewById(R$id.sud_items_title));
            TextView textView = (TextView) view.findViewById(R$id.sud_items_summary);
            if (textView.getVisibility() == 8 && (view instanceof LinearLayout)) {
                ((LinearLayout) view).setGravity(16);
            }
            applyPartnerCustomizationItemSummaryStyle(textView);
            applyPartnerCustomizationItemViewLayoutStyle(view);
        }
    }

    public static void applyPartnerCustomizationItemSummaryStyle(TextView textView) {
        if (PartnerStyleHelper.shouldApplyPartnerHeavyThemeResource(textView)) {
            TextViewPartnerStyler.applyPartnerCustomizationStyle(textView, new TextViewPartnerStyler.TextPartnerConfigs(null, null, PartnerConfig.CONFIG_ITEMS_SUMMARY_TEXT_SIZE, PartnerConfig.CONFIG_ITEMS_SUMMARY_FONT_FAMILY, PartnerConfig.CONFIG_ITEMS_SUMMARY_MARGIN_TOP, null, PartnerStyleHelper.getLayoutGravity(textView.getContext())));
        }
    }

    public static void applyPartnerCustomizationItemTitleStyle(TextView textView) {
        if (PartnerStyleHelper.shouldApplyPartnerHeavyThemeResource(textView)) {
            TextViewPartnerStyler.applyPartnerCustomizationStyle(textView, new TextViewPartnerStyler.TextPartnerConfigs(null, null, PartnerConfig.CONFIG_ITEMS_TITLE_TEXT_SIZE, PartnerConfig.CONFIG_ITEMS_TITLE_FONT_FAMILY, null, null, PartnerStyleHelper.getLayoutGravity(textView.getContext())));
        }
    }

    private static void applyPartnerCustomizationItemViewLayoutStyle(View view) {
        Context context = view.getContext();
        PartnerConfigHelper partnerConfigHelper = PartnerConfigHelper.get(context);
        PartnerConfig partnerConfig = PartnerConfig.CONFIG_ITEMS_PADDING_TOP;
        float dimension = partnerConfigHelper.isPartnerConfigAvailable(partnerConfig) ? PartnerConfigHelper.get(context).getDimension(context, partnerConfig) : view.getPaddingTop();
        PartnerConfigHelper partnerConfigHelper2 = PartnerConfigHelper.get(context);
        PartnerConfig partnerConfig2 = PartnerConfig.CONFIG_ITEMS_PADDING_BOTTOM;
        float dimension2 = partnerConfigHelper2.isPartnerConfigAvailable(partnerConfig2) ? PartnerConfigHelper.get(context).getDimension(context, partnerConfig2) : view.getPaddingBottom();
        if (dimension != view.getPaddingTop() || dimension2 != view.getPaddingBottom()) {
            if (Build.VERSION.SDK_INT >= 17) {
                view.setPadding(view.getPaddingStart(), (int) dimension, view.getPaddingEnd(), (int) dimension2);
            } else {
                view.setPadding(view.getPaddingLeft(), (int) dimension, view.getPaddingRight(), (int) dimension2);
            }
        }
        PartnerConfigHelper partnerConfigHelper3 = PartnerConfigHelper.get(context);
        PartnerConfig partnerConfig3 = PartnerConfig.CONFIG_ITEMS_MIN_HEIGHT;
        if (partnerConfigHelper3.isPartnerConfigAvailable(partnerConfig3)) {
            view.setMinimumHeight((int) PartnerConfigHelper.get(context).getDimension(context, partnerConfig3));
        }
    }
}
