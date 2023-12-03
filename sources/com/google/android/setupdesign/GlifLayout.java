package com.google.android.setupdesign;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.setupcompat.PartnerCustomizationLayout;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupcompat.template.StatusBarMixin;
import com.google.android.setupdesign.template.DescriptionMixin;
import com.google.android.setupdesign.template.HeaderMixin;
import com.google.android.setupdesign.template.IconMixin;
import com.google.android.setupdesign.template.IllustrationProgressMixin;
import com.google.android.setupdesign.template.ProgressBarMixin;
import com.google.android.setupdesign.template.RequireScrollMixin;
import com.google.android.setupdesign.template.ScrollViewScrollHandlingDelegate;
import com.google.android.setupdesign.util.DescriptionStyler;
import com.google.android.setupdesign.util.LayoutStyler;
import com.google.android.setupdesign.util.PartnerStyleHelper;

/* loaded from: classes2.dex */
public class GlifLayout extends PartnerCustomizationLayout {
    private boolean applyPartnerHeavyThemeResource;
    private ColorStateList backgroundBaseColor;
    private boolean backgroundPatterned;
    private ColorStateList primaryColor;

    public GlifLayout(Context context) {
        this(context, 0, 0);
    }

    public GlifLayout(Context context, int i) {
        this(context, i, 0);
    }

    public GlifLayout(Context context, int i, int i2) {
        super(context, i, i2);
        this.backgroundPatterned = true;
        this.applyPartnerHeavyThemeResource = false;
        init(null, R$attr.sudLayoutTheme);
    }

    public GlifLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.backgroundPatterned = true;
        this.applyPartnerHeavyThemeResource = false;
        init(attributeSet, R$attr.sudLayoutTheme);
    }

    @TargetApi(11)
    public GlifLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.backgroundPatterned = true;
        this.applyPartnerHeavyThemeResource = false;
        init(attributeSet, i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @TargetApi(17)
    public static void applyPartnerCustomizationContentPaddingTopStyle(View view) {
        int dimension;
        Context context = view.getContext();
        PartnerConfigHelper partnerConfigHelper = PartnerConfigHelper.get(context);
        PartnerConfig partnerConfig = PartnerConfig.CONFIG_CONTENT_PADDING_TOP;
        boolean isPartnerConfigAvailable = partnerConfigHelper.isPartnerConfigAvailable(partnerConfig);
        if (PartnerStyleHelper.shouldApplyPartnerHeavyThemeResource(view) && isPartnerConfigAvailable && (dimension = (int) PartnerConfigHelper.get(context).getDimension(context, partnerConfig)) != view.getPaddingTop()) {
            view.setPadding(view.getPaddingStart(), dimension, view.getPaddingEnd(), view.getPaddingBottom());
        }
    }

    private void init(AttributeSet attributeSet, int i) {
        if (isInEditMode()) {
            return;
        }
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.SudGlifLayout, i, 0);
        this.applyPartnerHeavyThemeResource = shouldApplyPartnerResource() && obtainStyledAttributes.getBoolean(R$styleable.SudGlifLayout_sudUsePartnerHeavyTheme, false);
        registerMixin(HeaderMixin.class, new HeaderMixin(this, attributeSet, i));
        registerMixin(DescriptionMixin.class, new DescriptionMixin(this, attributeSet, i));
        registerMixin(IconMixin.class, new IconMixin(this, attributeSet, i));
        registerMixin(ProgressBarMixin.class, new ProgressBarMixin(this, attributeSet, i));
        registerMixin(IllustrationProgressMixin.class, new IllustrationProgressMixin(this));
        RequireScrollMixin requireScrollMixin = new RequireScrollMixin(this);
        registerMixin(RequireScrollMixin.class, requireScrollMixin);
        ScrollView scrollView = getScrollView();
        if (scrollView != null) {
            requireScrollMixin.setScrollHandlingDelegate(new ScrollViewScrollHandlingDelegate(requireScrollMixin, scrollView));
        }
        ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(R$styleable.SudGlifLayout_sudColorPrimary);
        if (colorStateList != null) {
            setPrimaryColor(colorStateList);
        }
        if (this.applyPartnerHeavyThemeResource) {
            updateContentBackgroundColorWithPartnerConfig();
            View findManagedViewById = findManagedViewById(R$id.sud_layout_content);
            if (findManagedViewById != null) {
                LayoutStyler.applyPartnerCustomizationExtraPaddingStyle(findManagedViewById);
                applyPartnerCustomizationContentPaddingTopStyle(findManagedViewById);
            }
        }
        updateLandscapeMiddleHorizontalSpacing();
        setBackgroundBaseColor(obtainStyledAttributes.getColorStateList(R$styleable.SudGlifLayout_sudBackgroundBaseColor));
        setBackgroundPatterned(obtainStyledAttributes.getBoolean(R$styleable.SudGlifLayout_sudBackgroundPatterned, true));
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.SudGlifLayout_sudStickyHeader, 0);
        if (resourceId != 0) {
            inflateStickyHeader(resourceId);
        }
        obtainStyledAttributes.recycle();
    }

    private void tryApplyPartnerCustomizationStyleToShortDescription() {
        TextView textView = (TextView) findManagedViewById(R$id.sud_layout_description);
        if (textView != null) {
            if (this.applyPartnerHeavyThemeResource) {
                DescriptionStyler.applyPartnerCustomizationHeavyStyle(textView);
            } else if (shouldApplyPartnerResource()) {
                DescriptionStyler.applyPartnerCustomizationLightStyle(textView);
            }
        }
    }

    private void updateBackground() {
        if (findManagedViewById(R$id.suc_layout_status) != null) {
            int i = 0;
            ColorStateList colorStateList = this.backgroundBaseColor;
            if (colorStateList != null) {
                i = colorStateList.getDefaultColor();
            } else {
                ColorStateList colorStateList2 = this.primaryColor;
                if (colorStateList2 != null) {
                    i = colorStateList2.getDefaultColor();
                }
            }
            ((StatusBarMixin) getMixin(StatusBarMixin.class)).setStatusBarBackground(this.backgroundPatterned ? new GlifPatternDrawable(i) : new ColorDrawable(i));
        }
    }

    private void updateContentBackgroundColorWithPartnerConfig() {
        if (useFullDynamicColor()) {
            return;
        }
        getRootView().setBackgroundColor(PartnerConfigHelper.get(getContext()).getColor(getContext(), PartnerConfig.CONFIG_LAYOUT_BACKGROUND_COLOR));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.setupcompat.PartnerCustomizationLayout, com.google.android.setupcompat.internal.TemplateLayout
    public ViewGroup findContainer(int i) {
        if (i == 0) {
            i = R$id.sud_layout_content;
        }
        return super.findContainer(i);
    }

    public ColorStateList getBackgroundBaseColor() {
        return this.backgroundBaseColor;
    }

    public CharSequence getDescriptionText() {
        return ((DescriptionMixin) getMixin(DescriptionMixin.class)).getText();
    }

    public TextView getDescriptionTextView() {
        return ((DescriptionMixin) getMixin(DescriptionMixin.class)).getTextView();
    }

    public ColorStateList getHeaderColor() {
        return ((HeaderMixin) getMixin(HeaderMixin.class)).getTextColor();
    }

    public CharSequence getHeaderText() {
        return ((HeaderMixin) getMixin(HeaderMixin.class)).getText();
    }

    public TextView getHeaderTextView() {
        return ((HeaderMixin) getMixin(HeaderMixin.class)).getTextView();
    }

    public Drawable getIcon() {
        return ((IconMixin) getMixin(IconMixin.class)).getIcon();
    }

    public ColorStateList getPrimaryColor() {
        return this.primaryColor;
    }

    public ScrollView getScrollView() {
        View findManagedViewById = findManagedViewById(R$id.sud_scroll_view);
        if (findManagedViewById instanceof ScrollView) {
            return (ScrollView) findManagedViewById;
        }
        return null;
    }

    public View inflateStickyHeader(int i) {
        ViewStub viewStub = (ViewStub) findManagedViewById(R$id.sud_layout_sticky_header);
        viewStub.setLayoutResource(i);
        return viewStub.inflate();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        ((IconMixin) getMixin(IconMixin.class)).tryApplyPartnerCustomizationStyle();
        ((HeaderMixin) getMixin(HeaderMixin.class)).tryApplyPartnerCustomizationStyle();
        ((DescriptionMixin) getMixin(DescriptionMixin.class)).tryApplyPartnerCustomizationStyle();
        tryApplyPartnerCustomizationStyleToShortDescription();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.setupcompat.PartnerCustomizationLayout, com.google.android.setupcompat.internal.TemplateLayout
    public View onInflateTemplate(LayoutInflater layoutInflater, int i) {
        if (i == 0) {
            i = R$layout.sud_glif_template;
        }
        return inflateTemplate(layoutInflater, R$style.SudThemeGlif_Light, i);
    }

    public void setBackgroundBaseColor(ColorStateList colorStateList) {
        this.backgroundBaseColor = colorStateList;
        updateBackground();
    }

    public void setBackgroundPatterned(boolean z) {
        this.backgroundPatterned = z;
        updateBackground();
    }

    public void setDescriptionText(int i) {
        ((DescriptionMixin) getMixin(DescriptionMixin.class)).setText(i);
    }

    public void setDescriptionText(CharSequence charSequence) {
        ((DescriptionMixin) getMixin(DescriptionMixin.class)).setText(charSequence);
    }

    public void setHeaderColor(ColorStateList colorStateList) {
        ((HeaderMixin) getMixin(HeaderMixin.class)).setTextColor(colorStateList);
    }

    public void setHeaderText(int i) {
        ((HeaderMixin) getMixin(HeaderMixin.class)).setText(i);
    }

    public void setHeaderText(CharSequence charSequence) {
        ((HeaderMixin) getMixin(HeaderMixin.class)).setText(charSequence);
    }

    public void setIcon(Drawable drawable) {
        ((IconMixin) getMixin(IconMixin.class)).setIcon(drawable);
    }

    @TargetApi(31)
    public void setLandscapeHeaderAreaVisible(boolean z) {
        View findManagedViewById = findManagedViewById(R$id.sud_landscape_header_area);
        if (findManagedViewById == null) {
            return;
        }
        if (z) {
            findManagedViewById.setVisibility(0);
        } else {
            findManagedViewById.setVisibility(8);
        }
        updateLandscapeMiddleHorizontalSpacing();
    }

    public void setPrimaryColor(ColorStateList colorStateList) {
        this.primaryColor = colorStateList;
        updateBackground();
        ((ProgressBarMixin) getMixin(ProgressBarMixin.class)).setColor(colorStateList);
    }

    public void setProgressBarShown(boolean z) {
        ((ProgressBarMixin) getMixin(ProgressBarMixin.class)).setShown(z);
    }

    public boolean shouldApplyPartnerHeavyThemeResource() {
        return this.applyPartnerHeavyThemeResource || (shouldApplyPartnerResource() && PartnerConfigHelper.shouldApplyExtendedPartnerConfig(getContext()));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateLandscapeMiddleHorizontalSpacing() {
        int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.sud_glif_land_middle_horizontal_spacing);
        View findManagedViewById = findManagedViewById(R$id.sud_landscape_header_area);
        if (findManagedViewById != null) {
            PartnerConfigHelper partnerConfigHelper = PartnerConfigHelper.get(getContext());
            PartnerConfig partnerConfig = PartnerConfig.CONFIG_LAYOUT_MARGIN_END;
            if (partnerConfigHelper.isPartnerConfigAvailable(partnerConfig)) {
                int dimension = (dimensionPixelSize / 2) - ((int) PartnerConfigHelper.get(getContext()).getDimension(getContext(), partnerConfig));
                if (Build.VERSION.SDK_INT >= 17) {
                    findManagedViewById.setPadding(findManagedViewById.getPaddingStart(), findManagedViewById.getPaddingTop(), dimension, findManagedViewById.getPaddingBottom());
                } else {
                    findManagedViewById.setPadding(findManagedViewById.getPaddingLeft(), findManagedViewById.getPaddingTop(), dimension, findManagedViewById.getPaddingBottom());
                }
            }
        }
        View findManagedViewById2 = findManagedViewById(R$id.sud_landscape_content_area);
        if (findManagedViewById2 != null) {
            PartnerConfigHelper partnerConfigHelper2 = PartnerConfigHelper.get(getContext());
            PartnerConfig partnerConfig2 = PartnerConfig.CONFIG_LAYOUT_MARGIN_START;
            if (partnerConfigHelper2.isPartnerConfigAvailable(partnerConfig2)) {
                int dimension2 = findManagedViewById != null ? (dimensionPixelSize / 2) - ((int) PartnerConfigHelper.get(getContext()).getDimension(getContext(), partnerConfig2)) : 0;
                if (Build.VERSION.SDK_INT >= 17) {
                    findManagedViewById2.setPadding(dimension2, findManagedViewById2.getPaddingTop(), findManagedViewById2.getPaddingEnd(), findManagedViewById2.getPaddingBottom());
                } else {
                    findManagedViewById2.setPadding(dimension2, findManagedViewById2.getPaddingTop(), findManagedViewById2.getPaddingRight(), findManagedViewById2.getPaddingBottom());
                }
            }
        }
    }
}
