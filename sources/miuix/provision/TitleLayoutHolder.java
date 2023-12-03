package miuix.provision;

import android.view.View;

/* loaded from: classes5.dex */
public class TitleLayoutHolder {
    boolean adjusted;
    private View titleLayout;

    public TitleLayoutHolder(View view, boolean z) {
        this.titleLayout = view;
        this.adjusted = z;
    }

    public static void adjustPaddingTop(TitleLayoutHolder titleLayoutHolder, int i) {
        if (titleLayoutHolder == null || i == -1) {
            return;
        }
        View titleLayout = titleLayoutHolder.getTitleLayout();
        if (titleLayoutHolder.isAdjusted() || titleLayout == null) {
            return;
        }
        titleLayout.setPadding(titleLayout.getPaddingLeft(), i + titleLayout.getPaddingTop(), titleLayout.getPaddingRight(), titleLayout.getBottom() + titleLayout.getPaddingBottom());
        titleLayoutHolder.setAdjusted(true);
    }

    public View getTitleLayout() {
        return this.titleLayout;
    }

    public boolean isAdjusted() {
        return this.adjusted;
    }

    public void setAdjusted(boolean z) {
        this.adjusted = z;
    }
}
