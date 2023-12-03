package miuix.androidbasewidget.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import miuix.androidbasewidget.R$attr;
import miuix.androidbasewidget.R$drawable;
import miuix.androidbasewidget.widget.StateEditText;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.ViewUtils;

/* loaded from: classes5.dex */
public class PasswordWidgetManager extends StateEditText.WidgetManager {
    private static final int[] CHECKED_STATE_SET = {16842912};
    private boolean mIsChecked;
    private StateEditText mMaster;
    private Drawable mWidgetDrawable;

    public PasswordWidgetManager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsChecked = false;
        Drawable resolveDrawable = AttributeResolver.resolveDrawable(context, R$attr.miuixAppcompatVisibilityIcon);
        this.mWidgetDrawable = resolveDrawable;
        if (resolveDrawable == null) {
            if (ViewUtils.isNightMode(context)) {
                this.mWidgetDrawable = context.getResources().getDrawable(R$drawable.miuix_appcompat_ic_visibility_selector_dark);
            } else {
                this.mWidgetDrawable = context.getResources().getDrawable(R$drawable.miuix_appcompat_ic_visibility_selector_light);
            }
        }
    }

    @Override // miuix.androidbasewidget.widget.StateEditText.WidgetManager
    public Drawable[] getWidgetDrawables() {
        return new Drawable[]{this.mWidgetDrawable};
    }

    @Override // miuix.androidbasewidget.widget.StateEditText.WidgetManager
    public void onAttached(StateEditText stateEditText) {
        this.mMaster = stateEditText;
    }

    @Override // miuix.androidbasewidget.widget.StateEditText.WidgetManager
    public void onWidgetClick(int i) {
        this.mIsChecked = !this.mIsChecked;
        StateEditText stateEditText = this.mMaster;
        if (stateEditText != null) {
            int selectionStart = stateEditText.getSelectionStart();
            int selectionEnd = this.mMaster.getSelectionEnd();
            this.mMaster.setTransformationMethod(this.mIsChecked ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
            this.mMaster.setTextDirection(2);
            this.mMaster.setSelection(selectionStart, selectionEnd);
        }
        this.mWidgetDrawable.setState(this.mIsChecked ? CHECKED_STATE_SET : new int[0]);
    }
}
