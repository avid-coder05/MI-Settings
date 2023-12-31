package com.google.android.setupcompat.template;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import com.google.android.setupcompat.PartnerCustomizationLayout;
import com.google.android.setupcompat.R$id;
import com.google.android.setupcompat.R$layout;
import com.google.android.setupcompat.R$style;
import com.google.android.setupcompat.R$styleable;
import com.google.android.setupcompat.internal.FooterButtonPartnerConfig;
import com.google.android.setupcompat.internal.Preconditions;
import com.google.android.setupcompat.internal.TemplateLayout;
import com.google.android.setupcompat.logging.internal.FooterBarMixinMetrics;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupcompat.template.FooterButton;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes2.dex */
public class FooterBarMixin implements Mixin {
    private static final AtomicInteger nextGeneratedId = new AtomicInteger(1);
    final boolean applyDynamicColor;
    final boolean applyPartnerResources;
    private LinearLayout buttonContainer;
    private final Context context;
    int defaultPadding;
    private int footerBarPaddingBottom;
    private int footerBarPaddingTop;
    private final int footerBarPrimaryBackgroundColor;
    private final int footerBarSecondaryBackgroundColor;
    private final ViewStub footerStub;
    public final FooterBarMixinMetrics metrics;
    private FooterButton primaryButton;
    private int primaryButtonId;
    public FooterButtonPartnerConfig primaryButtonPartnerConfigForTesting;
    private FooterButton secondaryButton;
    private int secondaryButtonId;
    public FooterButtonPartnerConfig secondaryButtonPartnerConfigForTesting;
    final boolean useFullDynamicColor;
    ColorStateList primaryDefaultTextColor = null;
    ColorStateList secondaryDefaultTextColor = null;
    private boolean removeFooterBarWhenEmpty = true;
    private boolean isSecondaryButtonInPrimaryStyle = false;

    public FooterBarMixin(TemplateLayout templateLayout, AttributeSet attributeSet, int i) {
        FooterBarMixinMetrics footerBarMixinMetrics = new FooterBarMixinMetrics();
        this.metrics = footerBarMixinMetrics;
        Context context = templateLayout.getContext();
        this.context = context;
        this.footerStub = (ViewStub) templateLayout.findManagedViewById(R$id.suc_layout_footer);
        boolean z = templateLayout instanceof PartnerCustomizationLayout;
        this.applyPartnerResources = z && ((PartnerCustomizationLayout) templateLayout).shouldApplyPartnerResource();
        this.applyDynamicColor = z && ((PartnerCustomizationLayout) templateLayout).shouldApplyDynamicColor();
        this.useFullDynamicColor = z && ((PartnerCustomizationLayout) templateLayout).useFullDynamicColor();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.SucFooterBarMixin, i, 0);
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R$styleable.SucFooterBarMixin_sucFooterBarPaddingVertical, 0);
        this.defaultPadding = dimensionPixelSize;
        this.footerBarPaddingTop = obtainStyledAttributes.getDimensionPixelSize(R$styleable.SucFooterBarMixin_sucFooterBarPaddingTop, dimensionPixelSize);
        this.footerBarPaddingBottom = obtainStyledAttributes.getDimensionPixelSize(R$styleable.SucFooterBarMixin_sucFooterBarPaddingBottom, this.defaultPadding);
        this.footerBarPrimaryBackgroundColor = obtainStyledAttributes.getColor(R$styleable.SucFooterBarMixin_sucFooterBarPrimaryFooterBackground, 0);
        this.footerBarSecondaryBackgroundColor = obtainStyledAttributes.getColor(R$styleable.SucFooterBarMixin_sucFooterBarSecondaryFooterBackground, 0);
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.SucFooterBarMixin_sucFooterBarPrimaryFooterButton, 0);
        int resourceId2 = obtainStyledAttributes.getResourceId(R$styleable.SucFooterBarMixin_sucFooterBarSecondaryFooterButton, 0);
        obtainStyledAttributes.recycle();
        FooterButtonInflater footerButtonInflater = new FooterButtonInflater(context);
        if (resourceId2 != 0) {
            setSecondaryButton(footerButtonInflater.inflate(resourceId2));
            footerBarMixinMetrics.logPrimaryButtonInitialStateVisibility(true, true);
        }
        if (resourceId != 0) {
            setPrimaryButton(footerButtonInflater.inflate(resourceId));
            footerBarMixinMetrics.logSecondaryButtonInitialStateVisibility(true, true);
        }
    }

    private View addSpace() {
        LinearLayout ensureFooterInflated = ensureFooterInflated();
        View view = new View(ensureFooterInflated.getContext());
        view.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 1.0f));
        view.setVisibility(4);
        ensureFooterInflated.addView(view);
        return view;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void autoSetButtonBarVisibility() {
        Button primaryButtonView = getPrimaryButtonView();
        Button secondaryButtonView = getSecondaryButtonView();
        int i = 0;
        boolean z = primaryButtonView != null && primaryButtonView.getVisibility() == 0;
        boolean z2 = secondaryButtonView != null && secondaryButtonView.getVisibility() == 0;
        LinearLayout linearLayout = this.buttonContainer;
        if (linearLayout != null) {
            if (!z && !z2) {
                i = this.removeFooterBarWhenEmpty ? 8 : 4;
            }
            linearLayout.setVisibility(i);
        }
    }

    private FooterButton.OnButtonEventListener createButtonEventListener(final int i) {
        return new FooterButton.OnButtonEventListener() { // from class: com.google.android.setupcompat.template.FooterBarMixin.1
            @Override // com.google.android.setupcompat.template.FooterButton.OnButtonEventListener
            public void onEnabledChanged(boolean z) {
                Button button;
                if (FooterBarMixin.this.buttonContainer == null || (button = (Button) FooterBarMixin.this.buttonContainer.findViewById(i)) == null) {
                    return;
                }
                button.setEnabled(z);
                FooterBarMixin footerBarMixin = FooterBarMixin.this;
                if (!footerBarMixin.applyPartnerResources || footerBarMixin.applyDynamicColor) {
                    return;
                }
                footerBarMixin.updateButtonTextColorWithEnabledState(button, (i == footerBarMixin.primaryButtonId || FooterBarMixin.this.isSecondaryButtonInPrimaryStyle) ? PartnerConfig.CONFIG_FOOTER_PRIMARY_BUTTON_TEXT_COLOR : PartnerConfig.CONFIG_FOOTER_SECONDARY_BUTTON_TEXT_COLOR);
            }

            @Override // com.google.android.setupcompat.template.FooterButton.OnButtonEventListener
            public void onTextChanged(CharSequence charSequence) {
                Button button;
                if (FooterBarMixin.this.buttonContainer == null || (button = (Button) FooterBarMixin.this.buttonContainer.findViewById(i)) == null) {
                    return;
                }
                button.setText(charSequence);
            }

            @Override // com.google.android.setupcompat.template.FooterButton.OnButtonEventListener
            public void onVisibilityChanged(int i2) {
                Button button;
                if (FooterBarMixin.this.buttonContainer == null || (button = (Button) FooterBarMixin.this.buttonContainer.findViewById(i)) == null) {
                    return;
                }
                button.setVisibility(i2);
                FooterBarMixin.this.autoSetButtonBarVisibility();
            }
        };
    }

    private LinearLayout ensureFooterInflated() {
        if (this.buttonContainer == null) {
            if (this.footerStub == null) {
                throw new IllegalStateException("Footer stub is not found in this template");
            }
            LinearLayout linearLayout = (LinearLayout) inflateFooter(R$layout.suc_footer_button_bar);
            this.buttonContainer = linearLayout;
            onFooterBarInflated(linearLayout);
            onFooterBarApplyPartnerResource(this.buttonContainer);
        }
        return this.buttonContainer;
    }

    private static int generateViewId() {
        AtomicInteger atomicInteger;
        int i;
        int i2;
        do {
            atomicInteger = nextGeneratedId;
            i = atomicInteger.get();
            i2 = i + 1;
            if (i2 > 16777215) {
                i2 = 1;
            }
        } while (!atomicInteger.compareAndSet(i, i2));
        return i;
    }

    private static PartnerConfig getDrawablePartnerConfig(int i) {
        switch (i) {
            case 1:
                return PartnerConfig.CONFIG_FOOTER_BUTTON_ICON_ADD_ANOTHER;
            case 2:
                return PartnerConfig.CONFIG_FOOTER_BUTTON_ICON_CANCEL;
            case 3:
                return PartnerConfig.CONFIG_FOOTER_BUTTON_ICON_CLEAR;
            case 4:
                return PartnerConfig.CONFIG_FOOTER_BUTTON_ICON_DONE;
            case 5:
                return PartnerConfig.CONFIG_FOOTER_BUTTON_ICON_NEXT;
            case 6:
                return PartnerConfig.CONFIG_FOOTER_BUTTON_ICON_OPT_IN;
            case 7:
                return PartnerConfig.CONFIG_FOOTER_BUTTON_ICON_SKIP;
            case 8:
                return PartnerConfig.CONFIG_FOOTER_BUTTON_ICON_STOP;
            default:
                return null;
        }
    }

    private int getPartnerTheme(FooterButton footerButton, int i, PartnerConfig partnerConfig) {
        int theme = footerButton.getTheme();
        if (footerButton.getTheme() != 0 && !this.applyPartnerResources) {
            i = theme;
        }
        if (this.applyPartnerResources) {
            int color = PartnerConfigHelper.get(this.context).getColor(this.context, partnerConfig);
            return color == 0 ? R$style.SucPartnerCustomizationButton_Secondary : color != 0 ? R$style.SucPartnerCustomizationButton_Primary : i;
        }
        return i;
    }

    private FooterActionButton inflateButton(FooterButton footerButton, FooterButtonPartnerConfig footerButtonPartnerConfig) {
        FooterActionButton createThemedButton = createThemedButton(this.context, footerButtonPartnerConfig.getPartnerTheme());
        if (Build.VERSION.SDK_INT >= 17) {
            createThemedButton.setId(View.generateViewId());
        } else {
            createThemedButton.setId(generateViewId());
        }
        createThemedButton.setText(footerButton.getText());
        createThemedButton.setOnClickListener(footerButton);
        createThemedButton.setVisibility(footerButton.getVisibility());
        createThemedButton.setEnabled(footerButton.isEnabled());
        createThemedButton.setFooterButton(footerButton);
        footerButton.setOnButtonEventListener(createButtonEventListener(createThemedButton.getId()));
        return createThemedButton;
    }

    @TargetApi(29)
    private void onFooterButtonApplyPartnerResource(Button button, FooterButtonPartnerConfig footerButtonPartnerConfig) {
        if (this.applyPartnerResources) {
            FooterButtonStyleUtils.applyButtonPartnerResources(this.context, button, this.applyDynamicColor, button.getId() == this.primaryButtonId, footerButtonPartnerConfig);
            if (this.applyDynamicColor) {
                return;
            }
            updateButtonTextColorWithEnabledState(button, footerButtonPartnerConfig.getButtonTextColorConfig());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateButtonTextColorWithEnabledState(Button button, PartnerConfig partnerConfig) {
        if (button.isEnabled()) {
            FooterButtonStyleUtils.updateButtonTextEnabledColorWithPartnerConfig(this.context, button, partnerConfig);
        } else {
            FooterButtonStyleUtils.updateButtonTextDisableColor(button, (this.primaryButtonId == button.getId() || this.isSecondaryButtonInPrimaryStyle) ? this.primaryDefaultTextColor : this.secondaryDefaultTextColor);
        }
    }

    private void updateFooterBarPadding(LinearLayout linearLayout, int i, int i2, int i3, int i4) {
        if (linearLayout == null) {
            return;
        }
        linearLayout.setPadding(i, i2, i3, i4);
    }

    @SuppressLint({"InflateParams"})
    protected FooterActionButton createThemedButton(Context context, int i) {
        return (FooterActionButton) LayoutInflater.from(new ContextThemeWrapper(context, i)).inflate(R$layout.suc_button, (ViewGroup) null, false);
    }

    public LinearLayout getButtonContainer() {
        return this.buttonContainer;
    }

    @TargetApi(29)
    public PersistableBundle getLoggingMetrics() {
        return this.metrics.getMetrics();
    }

    int getPaddingBottom() {
        LinearLayout linearLayout = this.buttonContainer;
        return linearLayout != null ? linearLayout.getPaddingBottom() : this.footerStub.getPaddingBottom();
    }

    int getPaddingTop() {
        LinearLayout linearLayout = this.buttonContainer;
        return linearLayout != null ? linearLayout.getPaddingTop() : this.footerStub.getPaddingTop();
    }

    public FooterButton getPrimaryButton() {
        return this.primaryButton;
    }

    public Button getPrimaryButtonView() {
        LinearLayout linearLayout = this.buttonContainer;
        if (linearLayout == null) {
            return null;
        }
        return (Button) linearLayout.findViewById(this.primaryButtonId);
    }

    public FooterButton getSecondaryButton() {
        return this.secondaryButton;
    }

    public Button getSecondaryButtonView() {
        LinearLayout linearLayout = this.buttonContainer;
        if (linearLayout == null) {
            return null;
        }
        return (Button) linearLayout.findViewById(this.secondaryButtonId);
    }

    public int getVisibility() {
        return this.buttonContainer.getVisibility();
    }

    protected View inflateFooter(int i) {
        if (Build.VERSION.SDK_INT >= 16) {
            this.footerStub.setLayoutInflater(LayoutInflater.from(new ContextThemeWrapper(this.context, R$style.SucPartnerCustomizationButtonBar_Stackable)));
        }
        this.footerStub.setLayoutResource(i);
        return this.footerStub.inflate();
    }

    boolean isPrimaryButtonVisible() {
        return getPrimaryButtonView() != null && getPrimaryButtonView().getVisibility() == 0;
    }

    boolean isSecondaryButtonVisible() {
        return getSecondaryButtonView() != null && getSecondaryButtonView().getVisibility() == 0;
    }

    public void onAttachedToWindow() {
        this.metrics.logPrimaryButtonInitialStateVisibility(isPrimaryButtonVisible(), false);
        this.metrics.logSecondaryButtonInitialStateVisibility(isSecondaryButtonVisible(), false);
    }

    public void onDetachedFromWindow() {
        this.metrics.updateButtonVisibility(isPrimaryButtonVisible(), isSecondaryButtonVisible());
    }

    protected void onFooterBarApplyPartnerResource(LinearLayout linearLayout) {
        int dimension;
        if (linearLayout != null && this.applyPartnerResources) {
            if (!this.useFullDynamicColor) {
                linearLayout.setBackgroundColor(PartnerConfigHelper.get(this.context).getColor(this.context, PartnerConfig.CONFIG_FOOTER_BAR_BG_COLOR));
            }
            this.footerBarPaddingTop = (int) PartnerConfigHelper.get(this.context).getDimension(this.context, PartnerConfig.CONFIG_FOOTER_BUTTON_PADDING_TOP);
            this.footerBarPaddingBottom = (int) PartnerConfigHelper.get(this.context).getDimension(this.context, PartnerConfig.CONFIG_FOOTER_BUTTON_PADDING_BOTTOM);
            updateFooterBarPadding(linearLayout, linearLayout.getPaddingLeft(), this.footerBarPaddingTop, linearLayout.getPaddingRight(), this.footerBarPaddingBottom);
            PartnerConfigHelper partnerConfigHelper = PartnerConfigHelper.get(this.context);
            PartnerConfig partnerConfig = PartnerConfig.CONFIG_FOOTER_BAR_MIN_HEIGHT;
            if (!partnerConfigHelper.isPartnerConfigAvailable(partnerConfig) || (dimension = (int) PartnerConfigHelper.get(this.context).getDimension(this.context, partnerConfig)) <= 0) {
                return;
            }
            linearLayout.setMinimumHeight(dimension);
        }
    }

    protected void onFooterBarInflated(LinearLayout linearLayout) {
        if (linearLayout == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 17) {
            linearLayout.setId(View.generateViewId());
        } else {
            linearLayout.setId(generateViewId());
        }
        updateFooterBarPadding(linearLayout, linearLayout.getPaddingLeft(), this.footerBarPaddingTop, linearLayout.getPaddingRight(), this.footerBarPaddingBottom);
    }

    protected void onFooterButtonInflated(Button button, int i) {
        if (i != 0) {
            FooterButtonStyleUtils.updateButtonBackground(button, i);
        }
        this.buttonContainer.addView(button);
        autoSetButtonBarVisibility();
    }

    protected void repopulateButtons() {
        LinearLayout ensureFooterInflated = ensureFooterInflated();
        Button primaryButtonView = getPrimaryButtonView();
        Button secondaryButtonView = getSecondaryButtonView();
        ensureFooterInflated.removeAllViews();
        if (secondaryButtonView != null) {
            if (this.isSecondaryButtonInPrimaryStyle) {
                updateFooterBarPadding(ensureFooterInflated, ensureFooterInflated.getPaddingRight(), ensureFooterInflated.getPaddingTop(), ensureFooterInflated.getPaddingRight(), ensureFooterInflated.getPaddingBottom());
            }
            ensureFooterInflated.addView(secondaryButtonView);
        }
        addSpace();
        if (primaryButtonView != null) {
            ensureFooterInflated.addView(primaryButtonView);
        }
    }

    public void setPrimaryButton(FooterButton footerButton) {
        Preconditions.ensureOnMainThread("setPrimaryButton");
        ensureFooterInflated();
        FooterButtonPartnerConfig.Builder builder = new FooterButtonPartnerConfig.Builder(footerButton);
        int i = R$style.SucPartnerCustomizationButton_Primary;
        PartnerConfig partnerConfig = PartnerConfig.CONFIG_FOOTER_PRIMARY_BUTTON_BG_COLOR;
        FooterButtonPartnerConfig build = builder.setPartnerTheme(getPartnerTheme(footerButton, i, partnerConfig)).setButtonBackgroundConfig(partnerConfig).setButtonDisableAlphaConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_DISABLED_ALPHA).setButtonDisableBackgroundConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_DISABLED_BG_COLOR).setButtonIconConfig(getDrawablePartnerConfig(footerButton.getButtonType())).setButtonRadiusConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_RADIUS).setButtonRippleColorAlphaConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_RIPPLE_COLOR_ALPHA).setTextColorConfig(PartnerConfig.CONFIG_FOOTER_PRIMARY_BUTTON_TEXT_COLOR).setTextSizeConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_TEXT_SIZE).setButtonMinHeight(PartnerConfig.CONFIG_FOOTER_BUTTON_MIN_HEIGHT).setTextTypeFaceConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_FONT_FAMILY).setTextStyleConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_TEXT_STYLE).build();
        FooterActionButton inflateButton = inflateButton(footerButton, build);
        this.primaryButtonId = inflateButton.getId();
        this.primaryDefaultTextColor = inflateButton.getTextColors();
        this.primaryButton = footerButton;
        this.primaryButtonPartnerConfigForTesting = build;
        onFooterButtonInflated(inflateButton, this.footerBarPrimaryBackgroundColor);
        onFooterButtonApplyPartnerResource(inflateButton, build);
        repopulateButtons();
    }

    public void setSecondaryButton(FooterButton footerButton) {
        setSecondaryButton(footerButton, false);
    }

    public void setSecondaryButton(FooterButton footerButton, boolean z) {
        Preconditions.ensureOnMainThread("setSecondaryButton");
        this.isSecondaryButtonInPrimaryStyle = z;
        ensureFooterInflated();
        FooterButtonPartnerConfig build = new FooterButtonPartnerConfig.Builder(footerButton).setPartnerTheme(getPartnerTheme(footerButton, z ? R$style.SucPartnerCustomizationButton_Primary : R$style.SucPartnerCustomizationButton_Secondary, z ? PartnerConfig.CONFIG_FOOTER_PRIMARY_BUTTON_BG_COLOR : PartnerConfig.CONFIG_FOOTER_SECONDARY_BUTTON_BG_COLOR)).setButtonBackgroundConfig(z ? PartnerConfig.CONFIG_FOOTER_PRIMARY_BUTTON_BG_COLOR : PartnerConfig.CONFIG_FOOTER_SECONDARY_BUTTON_BG_COLOR).setButtonDisableAlphaConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_DISABLED_ALPHA).setButtonDisableBackgroundConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_DISABLED_BG_COLOR).setButtonIconConfig(getDrawablePartnerConfig(footerButton.getButtonType())).setButtonRadiusConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_RADIUS).setButtonRippleColorAlphaConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_RIPPLE_COLOR_ALPHA).setTextColorConfig(z ? PartnerConfig.CONFIG_FOOTER_PRIMARY_BUTTON_TEXT_COLOR : PartnerConfig.CONFIG_FOOTER_SECONDARY_BUTTON_TEXT_COLOR).setTextSizeConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_TEXT_SIZE).setButtonMinHeight(PartnerConfig.CONFIG_FOOTER_BUTTON_MIN_HEIGHT).setTextTypeFaceConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_FONT_FAMILY).setTextStyleConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_TEXT_STYLE).build();
        FooterActionButton inflateButton = inflateButton(footerButton, build);
        this.secondaryButtonId = inflateButton.getId();
        this.secondaryDefaultTextColor = inflateButton.getTextColors();
        this.secondaryButton = footerButton;
        this.secondaryButtonPartnerConfigForTesting = build;
        onFooterButtonInflated(inflateButton, this.footerBarSecondaryBackgroundColor);
        onFooterButtonApplyPartnerResource(inflateButton, build);
        repopulateButtons();
    }
}
