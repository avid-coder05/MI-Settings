package miuix.appcompat.internal.app.widget;

import android.content.Context;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;
import miuix.appcompat.R$attr;
import miuix.appcompat.R$dimen;
import miuix.appcompat.R$layout;
import miuix.appcompat.internal.app.widget.ScrollingTabContainerView;

/* loaded from: classes5.dex */
public class ExpandTabContainer extends ScrollingTabContainerView {
    private int[] mTabSizeStages;

    public ExpandTabContainer(Context context) {
        super(context);
        setContentHeight(getTabContainerHeight());
        this.mTabSizeStages = r0;
        int[] iArr = {context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_action_bar_tab_expand_text_size)};
        this.mTabSizeStages[1] = context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_action_bar_tab_expand_text_size_1);
        this.mTabSizeStages[2] = context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_action_bar_tab_expand_text_size_2);
    }

    private void measureTabViewSizeStage2(int[] iArr, int i, int i2) {
        int i3 = iArr[0];
        int tabViewMarginHorizontal = getTabViewMarginHorizontal();
        for (int i4 = 0; i4 < iArr.length; i4++) {
            i3 = iArr[i4];
            TextPaint textPaint = null;
            int i5 = 0;
            for (int i6 = 0; i6 < this.mTabLayout.getChildCount(); i6++) {
                TextView textView = ((ScrollingTabContainerView.TabView) this.mTabLayout.getChildAt(i6)).getTextView();
                if (textView != null) {
                    if (textPaint == null) {
                        textPaint = new TextPaint(textView.getPaint());
                        i5 += tabViewMarginHorizontal;
                    }
                    textPaint.setTextSize(i3);
                    i5 = (int) (i5 + textPaint.measureText(textView.getText().toString()));
                }
            }
            if (i5 <= Math.max(getMeasuredWidth(), View.MeasureSpec.getSize(i))) {
                break;
            }
        }
        for (int i7 = 0; i7 < this.mTabLayout.getChildCount(); i7++) {
            TextView textView2 = ((ScrollingTabContainerView.TabView) this.mTabLayout.getChildAt(i7)).getTextView();
            if (textView2 != null) {
                textView2.setTextSize(0, i3);
            }
        }
    }

    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView
    int getDefaultTabTextStyle() {
        return R$attr.actionBarTabTextExpandStyle;
    }

    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView
    int getTabBarLayoutRes() {
        return R$layout.miuix_appcompat_action_bar_tabbar_expand;
    }

    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView
    int getTabContainerHeight() {
        return -2;
    }

    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView
    int getTabViewLayoutRes() {
        return R$layout.miuix_appcompat_action_bar_tab_expand;
    }

    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView
    int getTabViewMarginHorizontal() {
        return getContext().getResources().getDimensionPixelOffset(R$dimen.miuix_appcompat_action_bar_tab_expand_margin);
    }

    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView, android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        measureTabViewSizeStage2(this.mTabSizeStages, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 0), i2);
        super.onMeasure(i, i2);
    }
}
