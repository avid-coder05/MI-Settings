package miuix.appcompat.internal.app.widget.actionbar;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import miuix.appcompat.R$attr;
import miuix.appcompat.R$dimen;
import miuix.appcompat.R$drawable;
import miuix.appcompat.R$id;
import miuix.appcompat.R$style;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.DeviceHelper;

/* loaded from: classes5.dex */
public class CollapseTitle {
    private int mCollapseSubtitleStyle;
    private TextView mCollapseSubtitleView;
    private LinearLayout mCollapseTitleLayout;
    private int mCollapseTitleStyle;
    private TextView mCollapseTitleView;
    private Context mContext;
    private float mDefaultSubtitleSize = 0.0f;
    private boolean mIsTitleDirty = false;
    private float mTitleLength = 0.0f;
    private boolean mSubtitleSizeable = true;

    public CollapseTitle(Context context, int i, int i2) {
        this.mContext = context;
        this.mCollapseTitleStyle = i;
        this.mCollapseSubtitleStyle = i2;
    }

    private LinearLayout.LayoutParams getChildLayoutParams() {
        return new LinearLayout.LayoutParams(-2, -2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$init$0() {
        this.mCollapseTitleLayout.setBackground(AttributeResolver.resolveDrawable(this.mContext, 16843676));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$init$1() {
        this.mCollapseSubtitleView.setBackgroundResource(R$drawable.miuix_appcompat_action_bar_subtitle_bg_land);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onConfigurationChanged$2() {
        setSubTitleTextSize(getSubtitleAdjustSize());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onLandscapeChange() {
        Resources resources = this.mContext.getResources();
        this.mCollapseTitleLayout.setOrientation(0);
        this.mCollapseSubtitleView.setTextAppearance(this.mContext, R$style.Miuix_AppCompat_TextAppearance_WindowTitle);
        this.mCollapseSubtitleView.setBackgroundResource(R$drawable.miuix_appcompat_action_bar_subtitle_bg_land);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mCollapseSubtitleView.getLayoutParams();
        layoutParams.setMarginStart(resources.getDimensionPixelOffset(R$dimen.miuix_appcompat_action_bar_subtitle_start_margin));
        layoutParams.topMargin = 0;
        layoutParams.bottomMargin = 0;
        this.mCollapseSubtitleView.setLayoutParams(layoutParams);
        this.mSubtitleSizeable = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPortraitChange() {
        Resources resources = this.mContext.getResources();
        this.mCollapseTitleLayout.setOrientation(1);
        this.mCollapseSubtitleView.setTextAppearance(this.mContext, R$style.Miuix_AppCompat_TextAppearance_WindowTitle_Subtitle);
        this.mCollapseSubtitleView.setBackground(null);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mCollapseSubtitleView.getLayoutParams();
        layoutParams.setMarginStart(0);
        layoutParams.topMargin = resources.getDimensionPixelOffset(R$dimen.action_bar_subtitle_top_margin);
        layoutParams.bottomMargin = resources.getDimensionPixelOffset(R$dimen.action_bar_subtitle_bottom_margin);
        this.mCollapseSubtitleView.setPadding(0, 0, 0, 0);
        this.mCollapseSubtitleView.setLayoutParams(layoutParams);
        this.mSubtitleSizeable = true;
        setSubTitleTextSize(getSubtitleAdjustSize());
    }

    public boolean canTitleBeShown(String str) {
        if (this.mIsTitleDirty) {
            this.mTitleLength = this.mCollapseTitleView.getPaint().measureText(str);
            this.mIsTitleDirty = false;
        }
        return this.mTitleLength <= ((float) this.mCollapseTitleView.getMeasuredWidth());
    }

    public Rect getHitRect() {
        Rect rect = new Rect();
        this.mCollapseTitleLayout.getHitRect(rect);
        return rect;
    }

    public View getLayout() {
        return this.mCollapseTitleLayout;
    }

    public float getSubtitleAdjustSize() {
        float f = this.mDefaultSubtitleSize;
        Resources resources = this.mContext.getResources();
        int measuredHeight = ((this.mCollapseTitleLayout.getMeasuredHeight() - this.mCollapseTitleView.getMeasuredHeight()) - this.mCollapseSubtitleView.getPaddingTop()) - this.mCollapseSubtitleView.getPaddingBottom();
        if (measuredHeight <= 0) {
            return f;
        }
        TextPaint textPaint = new TextPaint(this.mCollapseSubtitleView.getPaint());
        textPaint.setTextSize(f);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        int ceil = (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
        float f2 = f / 2.0f;
        float f3 = resources.getDisplayMetrics().scaledDensity;
        while (ceil > measuredHeight && f >= f2) {
            f -= f3;
            textPaint.setTextSize(f);
            Paint.FontMetrics fontMetrics2 = textPaint.getFontMetrics();
            ceil = (int) Math.ceil(fontMetrics2.descent - fontMetrics2.ascent);
        }
        return f;
    }

    public ViewGroup getTitleParent() {
        return (ViewGroup) this.mCollapseTitleView.getParent();
    }

    public int getVisibility() {
        return this.mCollapseTitleLayout.getVisibility();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void init() {
        Resources resources = this.mContext.getResources();
        int i = (DeviceHelper.isTablet(this.mContext) || !(resources.getConfiguration().orientation == 2)) ? 0 : 1;
        this.mSubtitleSizeable = i ^ 1;
        this.mDefaultSubtitleSize = resources.getDimensionPixelSize(R$dimen.miuix_appcompat_subtitle_text_size);
        LinearLayout linearLayout = new LinearLayout(this.mContext);
        this.mCollapseTitleLayout = linearLayout;
        linearLayout.setImportantForAccessibility(2);
        Context context = this.mContext;
        int i2 = R$attr.collapseTitleTheme;
        this.mCollapseTitleView = new TextView(context, null, i2);
        int i3 = R$attr.collapseSubtitleTheme;
        if (i == 0) {
            i2 = i3;
        }
        this.mCollapseSubtitleView = new TextView(this.mContext, null, i2);
        this.mCollapseTitleLayout.setEnabled(false);
        this.mCollapseTitleLayout.setOrientation(i ^ 1);
        this.mCollapseTitleLayout.post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.actionbar.CollapseTitle$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                CollapseTitle.this.lambda$init$0();
            }
        });
        this.mCollapseTitleView.setId(R$id.action_bar_title);
        this.mCollapseTitleLayout.addView(this.mCollapseTitleView, getChildLayoutParams());
        this.mCollapseSubtitleView.setId(R$id.action_bar_subtitle);
        this.mCollapseSubtitleView.setVisibility(8);
        if (i != 0) {
            this.mCollapseSubtitleView.post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.actionbar.CollapseTitle$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    CollapseTitle.this.lambda$init$1();
                }
            });
        }
        this.mCollapseTitleLayout.addView(this.mCollapseSubtitleView, getChildLayoutParams());
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mCollapseSubtitleView.getLayoutParams();
        if (i != 0) {
            layoutParams.setMarginStart(resources.getDimensionPixelOffset(R$dimen.miuix_appcompat_action_bar_subtitle_start_margin));
            return;
        }
        layoutParams.topMargin = resources.getDimensionPixelOffset(R$dimen.action_bar_subtitle_top_margin);
        layoutParams.bottomMargin = resources.getDimensionPixelOffset(R$dimen.action_bar_subtitle_bottom_margin);
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (DeviceHelper.isTablet(this.mContext)) {
            this.mCollapseSubtitleView.post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.actionbar.CollapseTitle$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CollapseTitle.this.lambda$onConfigurationChanged$2();
                }
            });
        } else if (configuration.orientation == 2) {
            this.mCollapseSubtitleView.post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.actionbar.CollapseTitle$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    CollapseTitle.this.onLandscapeChange();
                }
            });
        } else {
            this.mCollapseSubtitleView.post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.actionbar.CollapseTitle$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    CollapseTitle.this.onPortraitChange();
                }
            });
        }
    }

    public void setEnabled(boolean z) {
        this.mCollapseTitleLayout.setEnabled(z);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mCollapseTitleLayout.setOnClickListener(onClickListener);
    }

    public void setSubTitle(CharSequence charSequence) {
        if (charSequence != null) {
            this.mCollapseSubtitleView.setText(charSequence);
        }
    }

    public void setSubTitleTextSize(float f) {
        if (this.mSubtitleSizeable) {
            this.mCollapseSubtitleView.setTextSize(0, f);
        }
    }

    public void setSubTitleVisibility(int i) {
        if (this.mCollapseSubtitleView.getVisibility() != i) {
            this.mCollapseSubtitleView.setVisibility(i);
        }
    }

    public void setTitle(CharSequence charSequence) {
        if (TextUtils.equals(charSequence, this.mCollapseTitleView.getText())) {
            return;
        }
        this.mCollapseTitleView.setText(charSequence);
        this.mIsTitleDirty = true;
    }

    public void setTitleVisibility(int i) {
        if (this.mCollapseTitleView.getVisibility() != i) {
            this.mCollapseTitleView.setVisibility(i);
        }
    }

    public void setVisibility(int i) {
        this.mCollapseTitleLayout.setVisibility(i);
    }

    public void updateTitleCenter(boolean z) {
        ViewGroup titleParent = getTitleParent();
        if (titleParent instanceof LinearLayout) {
            ((LinearLayout) titleParent).setGravity((z ? 1 : 8388611) | 16);
        }
        this.mCollapseTitleView.setGravity((z ? 1 : 8388611) | 16);
        this.mCollapseTitleView.setEllipsize(TextUtils.TruncateAt.END);
        this.mCollapseSubtitleView.setGravity((z ? 1 : 8388611) | 16);
        this.mCollapseSubtitleView.setEllipsize(TextUtils.TruncateAt.END);
    }
}
