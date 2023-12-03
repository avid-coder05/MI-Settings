package miuix.appcompat.internal.view;

import android.content.Context;
import android.view.ActionMode;
import android.view.View;
import miuix.appcompat.internal.app.widget.ActionBarContextView;
import miuix.view.EditActionMode;

/* loaded from: classes5.dex */
public class EditActionModeImpl extends ActionModeImpl implements EditActionMode {
    public EditActionModeImpl(Context context, ActionMode.Callback callback) {
        super(context, callback);
    }

    @Override // miuix.appcompat.internal.view.ActionModeImpl, android.view.ActionMode
    public CharSequence getTitle() {
        return ((ActionBarContextView) this.mActionModeView.get()).getTitle();
    }

    @Override // miuix.view.EditActionMode
    public void setButton(int i, CharSequence charSequence, int i2) {
        ((ActionBarContextView) this.mActionModeView.get()).setButton(i, charSequence, i2);
    }

    @Override // miuix.appcompat.internal.view.ActionModeImpl, android.view.ActionMode
    public void setCustomView(View view) {
    }

    @Override // miuix.appcompat.internal.view.ActionModeImpl, android.view.ActionMode
    public void setSubtitle(int i) {
    }

    @Override // miuix.appcompat.internal.view.ActionModeImpl, android.view.ActionMode
    public void setSubtitle(CharSequence charSequence) {
    }

    @Override // miuix.appcompat.internal.view.ActionModeImpl, android.view.ActionMode
    public void setTitle(int i) {
        setTitle(this.mContext.getResources().getString(i));
    }

    @Override // miuix.appcompat.internal.view.ActionModeImpl, android.view.ActionMode
    public void setTitle(CharSequence charSequence) {
        ((ActionBarContextView) this.mActionModeView.get()).setTitle(charSequence);
    }
}
