package com.android.settings.display;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import miui.view.ScreenView;

/* loaded from: classes.dex */
public class PageLayoutScreenView extends ScreenView implements ScreenView.SnapScreenOnceNotification {
    private final int TOTAL_PAGES;
    private Activity mActivity;
    private final boolean[] mIsDarkMode;

    public PageLayoutScreenView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PageLayoutScreenView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.TOTAL_PAGES = 2;
        this.mIsDarkMode = r2;
        this.mActivity = (Activity) context;
        boolean[] zArr = {true, false};
    }

    private void setDarkMode(boolean z) {
        this.mActivity.getWindow().setExtraFlags(z ? 16 : 0, 16);
    }

    public void onSnapCancelled(ScreenView screenView) {
    }

    public void onSnapEnd(ScreenView screenView) {
        setDarkMode(this.mIsDarkMode[getCurrentScreenIndex()]);
    }

    protected void snapToScreen(int i, int i2, boolean z) {
        snapToScreen(i, i2, z, this);
    }
}
