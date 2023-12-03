package miuix.appcompat.internal.view;

import android.content.Context;
import android.graphics.Rect;
import android.view.ActionMode;
import android.view.View;
import android.widget.EditText;
import java.lang.ref.WeakReference;
import miuix.appcompat.internal.app.widget.ActionModeView;
import miuix.appcompat.internal.app.widget.SearchActionModeView;
import miuix.view.SearchActionMode;

/* loaded from: classes5.dex */
public class SearchActionModeImpl extends ActionModeImpl implements SearchActionMode {
    public SearchActionModeImpl(Context context, ActionMode.Callback callback) {
        super(context, callback);
    }

    @Override // miuix.view.SearchActionMode
    public EditText getSearchInput() {
        return ((SearchActionModeView) this.mActionModeView.get()).getSearchInput();
    }

    @Override // miuix.view.SearchActionMode
    public void setAnchorView(View view) {
        ((SearchActionModeView) this.mActionModeView.get()).setAnchorView(view);
    }

    @Override // miuix.view.SearchActionMode
    public void setAnimateView(View view) {
        ((SearchActionModeView) this.mActionModeView.get()).setAnimateView(view);
    }

    public void setPendingInsets(Rect rect) {
        WeakReference<ActionModeView> weakReference = this.mActionModeView;
        SearchActionModeView searchActionModeView = weakReference != null ? (SearchActionModeView) weakReference.get() : null;
        if (searchActionModeView != null) {
            searchActionModeView.rePaddingAndRelayout(rect);
        }
    }

    @Override // miuix.view.SearchActionMode
    public void setResultView(View view) {
        ((SearchActionModeView) this.mActionModeView.get()).setResultView(view);
    }
}
