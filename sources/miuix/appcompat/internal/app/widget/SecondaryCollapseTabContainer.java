package miuix.appcompat.internal.app.widget;

import android.content.Context;
import android.widget.TextView;
import miuix.appcompat.R$attr;
import miuix.appcompat.R$dimen;
import miuix.appcompat.R$layout;
import miuix.appcompat.internal.util.EasyModeHelper;

/* loaded from: classes5.dex */
public class SecondaryCollapseTabContainer extends ScrollingTabContainerView {
    public SecondaryCollapseTabContainer(Context context) {
        super(context);
        setContentHeight(getTabContainerHeight());
    }

    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView
    int getDefaultTabTextStyle() {
        return R$attr.actionBarTabTextSecondaryStyle;
    }

    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView
    int getTabBarLayoutRes() {
        return R$layout.miuix_appcompat_action_bar_tabbar_collapse_secondary;
    }

    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView
    int getTabContainerHeight() {
        return -2;
    }

    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView
    int getTabViewLayoutRes() {
        return R$layout.miuix_appcompat_action_bar_tab_secondary;
    }

    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView
    int getTabViewMarginHorizontal() {
        return getContext().getResources().getDimensionPixelOffset(R$dimen.miuix_appcompat_action_bar_tab_secondary_margin);
    }

    @Override // miuix.appcompat.internal.app.widget.ScrollingTabContainerView
    void updateTabTextStyle(TextView textView) {
        EasyModeHelper.updateTextViewSize(textView);
    }
}
