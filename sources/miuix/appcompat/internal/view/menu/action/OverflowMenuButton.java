package miuix.appcompat.internal.view.menu.action;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import miuix.appcompat.R$dimen;
import miuix.appcompat.R$styleable;
import miuix.appcompat.internal.view.menu.action.ActionMenuView;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes5.dex */
public class OverflowMenuButton extends LinearLayout implements ActionMenuView.ActionMenuChildView {
    private ActionMenuItemViewChildren mChildren;
    private OnOverflowMenuButtonClickListener mOnOverflowMenuButtonClickListener;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public interface OnOverflowMenuButtonClickListener {
        void onOverflowMenuButtonClick();
    }

    public OverflowMenuButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public OverflowMenuButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.OverflowMenuButton, i, 0);
        Drawable drawable = obtainStyledAttributes.getDrawable(R$styleable.OverflowMenuButton_android_drawableTop);
        CharSequence text = obtainStyledAttributes.getText(R$styleable.OverflowMenuButton_android_text);
        obtainStyledAttributes.recycle();
        ActionMenuItemViewChildren actionMenuItemViewChildren = new ActionMenuItemViewChildren(this);
        this.mChildren = actionMenuItemViewChildren;
        actionMenuItemViewChildren.setIcon(drawable);
        this.mChildren.setText(text);
        setClickable(true);
        setFocusable(true);
        setVisibility(0);
        setEnabled(true);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0, types: [miuix.appcompat.internal.view.menu.action.OverflowMenuButton] */
    /* JADX WARN: Type inference failed for: r2v1, types: [android.view.View] */
    /* JADX WARN: Type inference failed for: r2v5, types: [android.view.ViewGroup] */
    /* JADX WARN: Type inference failed for: r2v6 */
    /* JADX WARN: Type inference failed for: r2v7 */
    private boolean isVisible() {
        while (this != 0 && this.getVisibility() == 0) {
            ViewParent parent = this.getParent();
            this = parent instanceof ViewGroup ? (ViewGroup) parent : 0;
        }
        return this == 0;
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView.ActionMenuChildView
    public boolean needsDividerAfter() {
        return false;
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView.ActionMenuChildView
    public boolean needsDividerBefore() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mChildren.onConfigurationChanged(getContext().getResources().getConfiguration());
        setPaddingRelative(getPaddingStart(), getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_action_button_bg_top_padding), getPaddingEnd(), getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_action_button_bg_bottom_padding));
    }

    @Override // android.view.View
    public boolean performClick() {
        if (!super.performClick() && isVisible()) {
            playSoundEffect(0);
            OnOverflowMenuButtonClickListener onOverflowMenuButtonClickListener = this.mOnOverflowMenuButtonClickListener;
            if (onOverflowMenuButtonClickListener != null) {
                onOverflowMenuButtonClickListener.onOverflowMenuButtonClick();
            }
            return true;
        }
        return true;
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.mChildren.setEnabled(z);
    }

    public void setOnOverflowMenuButtonClickListener(OnOverflowMenuButtonClickListener onOverflowMenuButtonClickListener) {
        this.mOnOverflowMenuButtonClickListener = onOverflowMenuButtonClickListener;
    }
}
