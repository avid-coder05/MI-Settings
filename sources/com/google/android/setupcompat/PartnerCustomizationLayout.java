package com.google.android.setupcompat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.setupcompat.internal.LifecycleFragment;
import com.google.android.setupcompat.internal.PersistableBundles;
import com.google.android.setupcompat.internal.TemplateLayout;
import com.google.android.setupcompat.logging.CustomEvent;
import com.google.android.setupcompat.logging.MetricKey;
import com.google.android.setupcompat.logging.SetupMetricsLogger;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.template.StatusBarMixin;
import com.google.android.setupcompat.template.SystemNavBarMixin;
import com.google.android.setupcompat.util.BuildCompatUtils;
import com.google.android.setupcompat.util.Logger;
import com.google.android.setupcompat.util.WizardManagerHelper;

/* loaded from: classes2.dex */
public class PartnerCustomizationLayout extends TemplateLayout {
    private static final Logger LOG = new Logger("PartnerCustomizationLayout");
    private Activity activity;
    private boolean useDynamicColor;
    private boolean useFullDynamicColorAttr;
    private boolean usePartnerResourceAttr;

    public PartnerCustomizationLayout(Context context) {
        this(context, 0, 0);
    }

    public PartnerCustomizationLayout(Context context, int i) {
        this(context, i, 0);
    }

    public PartnerCustomizationLayout(Context context, int i, int i2) {
        super(context, i, i2);
        init(null, R$attr.sucLayoutTheme);
    }

    public PartnerCustomizationLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet, R$attr.sucLayoutTheme);
    }

    @TargetApi(11)
    public PartnerCustomizationLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(attributeSet, i);
    }

    private void init(AttributeSet attributeSet, int i) {
        if (isInEditMode()) {
            return;
        }
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.SucPartnerCustomizationLayout, i, 0);
        boolean z = obtainStyledAttributes.getBoolean(R$styleable.SucPartnerCustomizationLayout_sucLayoutFullscreen, true);
        obtainStyledAttributes.recycle();
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 21 && z) {
            setSystemUiVisibility(MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE);
        }
        registerMixin(StatusBarMixin.class, new StatusBarMixin(this, this.activity.getWindow(), attributeSet, i));
        registerMixin(SystemNavBarMixin.class, new SystemNavBarMixin(this, this.activity.getWindow()));
        registerMixin(FooterBarMixin.class, new FooterBarMixin(this, attributeSet, i));
        ((SystemNavBarMixin) getMixin(SystemNavBarMixin.class)).applyPartnerCustomizations(attributeSet, i);
        if (i2 >= 21) {
            this.activity.getWindow().addFlags(Integer.MIN_VALUE);
            this.activity.getWindow().clearFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
            this.activity.getWindow().clearFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
        }
    }

    public static Activity lookupActivityFromContext(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            return lookupActivityFromContext(((ContextWrapper) context).getBaseContext());
        }
        throw new IllegalArgumentException("Cannot find instance of Activity in parent tree");
    }

    protected boolean enablePartnerResourceLoading() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.setupcompat.internal.TemplateLayout
    public ViewGroup findContainer(int i) {
        if (i == 0) {
            i = R$id.suc_layout_content;
        }
        return super.findContainer(i);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LifecycleFragment.attachNow(this.activity);
        ((FooterBarMixin) getMixin(FooterBarMixin.class)).onAttachedToWindow();
    }

    @Override // com.google.android.setupcompat.internal.TemplateLayout
    protected void onBeforeTemplateInflated(AttributeSet attributeSet, int i) {
        boolean z = true;
        this.usePartnerResourceAttr = true;
        Activity lookupActivityFromContext = lookupActivityFromContext(getContext());
        this.activity = lookupActivityFromContext;
        boolean isAnySetupWizard = WizardManagerHelper.isAnySetupWizard(lookupActivityFromContext.getIntent());
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.SucPartnerCustomizationLayout, i, 0);
        int i2 = R$styleable.SucPartnerCustomizationLayout_sucUsePartnerResource;
        if (!obtainStyledAttributes.hasValue(i2)) {
            LOG.e("Attribute sucUsePartnerResource not found in " + this.activity.getComponentName());
        }
        if (!isAnySetupWizard && !obtainStyledAttributes.getBoolean(i2, true)) {
            z = false;
        }
        this.usePartnerResourceAttr = z;
        int i3 = R$styleable.SucPartnerCustomizationLayout_sucFullDynamicColor;
        this.useDynamicColor = obtainStyledAttributes.hasValue(i3);
        this.useFullDynamicColorAttr = obtainStyledAttributes.getBoolean(i3, false);
        obtainStyledAttributes.recycle();
        LOG.atDebug("activity=" + this.activity.getClass().getSimpleName() + " isSetupFlow=" + isAnySetupWizard + " enablePartnerResourceLoading=" + enablePartnerResourceLoading() + " usePartnerResourceAttr=" + this.usePartnerResourceAttr + " useDynamicColor=" + this.useDynamicColor + " useFullDynamicColorAttr=" + this.useFullDynamicColorAttr);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        int i = Build.VERSION.SDK_INT;
        if (i < 21 || i < 29 || !WizardManagerHelper.isAnySetupWizard(this.activity.getIntent())) {
            return;
        }
        FooterBarMixin footerBarMixin = (FooterBarMixin) getMixin(FooterBarMixin.class);
        footerBarMixin.onDetachedFromWindow();
        FooterButton primaryButton = footerBarMixin.getPrimaryButton();
        FooterButton secondaryButton = footerBarMixin.getSecondaryButton();
        SetupMetricsLogger.logCustomEvent(getContext(), CustomEvent.create(MetricKey.get("SetupCompatMetrics", this.activity), PersistableBundles.mergeBundles(footerBarMixin.getLoggingMetrics(), primaryButton != null ? primaryButton.getMetrics("PrimaryFooterButton") : PersistableBundle.EMPTY, secondaryButton != null ? secondaryButton.getMetrics("SecondaryFooterButton") : PersistableBundle.EMPTY)));
    }

    @Override // com.google.android.setupcompat.internal.TemplateLayout
    protected View onInflateTemplate(LayoutInflater layoutInflater, int i) {
        if (i == 0) {
            i = R$layout.partner_customization_layout;
        }
        return inflateTemplate(layoutInflater, 0, i);
    }

    public boolean shouldApplyDynamicColor() {
        return this.useDynamicColor && BuildCompatUtils.isAtLeastS() && PartnerConfigHelper.get(getContext()).isAvailable();
    }

    public boolean shouldApplyPartnerResource() {
        return enablePartnerResourceLoading() && this.usePartnerResourceAttr && Build.VERSION.SDK_INT >= 29 && PartnerConfigHelper.get(getContext()).isAvailable();
    }

    public boolean useFullDynamicColor() {
        return shouldApplyDynamicColor() && this.useFullDynamicColorAttr;
    }
}
