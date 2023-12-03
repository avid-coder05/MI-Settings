package miuix.appcompat.internal.view.menu.context;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import miuix.internal.widget.PopupMenuAdapter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes5.dex */
public class ContextMenuAdapter extends PopupMenuAdapter {
    private MenuItem mLastCategorySystemOrderMenuItem;

    /* JADX INFO: Access modifiers changed from: protected */
    public ContextMenuAdapter(Context context, Menu menu) {
        super(context, menu);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.internal.widget.PopupMenuAdapter
    public boolean checkMenuItem(MenuItem menuItem) {
        boolean checkMenuItem = super.checkMenuItem(menuItem);
        if (checkMenuItem && menuItem.getOrder() == 131072) {
            if (this.mLastCategorySystemOrderMenuItem == null) {
                this.mLastCategorySystemOrderMenuItem = menuItem;
                return false;
            }
            throw new IllegalStateException("Only one menu item is allowed to have CATEGORY_SYSTEM order!");
        }
        return checkMenuItem;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MenuItem getLastCategorySystemOrderMenuItem() {
        return this.mLastCategorySystemOrderMenuItem;
    }
}
