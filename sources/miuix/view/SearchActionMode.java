package miuix.view;

import android.view.ActionMode;
import android.view.View;
import android.widget.EditText;

/* loaded from: classes5.dex */
public interface SearchActionMode {

    /* loaded from: classes5.dex */
    public interface Callback extends ActionMode.Callback {
    }

    EditText getSearchInput();

    void setAnchorView(View view);

    void setAnimateView(View view);

    void setResultView(View view);
}
