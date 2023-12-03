package miuix.appcompat.internal.view.menu.context;

import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.appcompat.internal.view.menu.MenuPresenter;

/* loaded from: classes5.dex */
public class ContextMenuPopupWindowHelper implements PopupWindow.OnDismissListener {
    private ContextMenuPopupWindow mContextMenuPopupWindow;
    private MenuBuilder mMenu;
    private MenuPresenter.Callback mPresenterCallback;

    public ContextMenuPopupWindowHelper(MenuBuilder menuBuilder) {
        this.mMenu = menuBuilder;
    }

    public void dismiss() {
        ContextMenuPopupWindow contextMenuPopupWindow = this.mContextMenuPopupWindow;
        if (contextMenuPopupWindow != null) {
            contextMenuPopupWindow.dismiss();
            this.mContextMenuPopupWindow = null;
        }
    }

    @Override // android.widget.PopupWindow.OnDismissListener
    public void onDismiss() {
        MenuPresenter.Callback callback = this.mPresenterCallback;
        if (callback != null) {
            callback.onCloseMenu(this.mMenu, true);
        }
        this.mMenu.clearAll();
    }

    public void setPresenterCallback(MenuPresenter.Callback callback) {
        this.mPresenterCallback = callback;
    }

    public void show(IBinder iBinder, View view, float f, float f2) {
        ContextMenuPopupWindowImpl contextMenuPopupWindowImpl = new ContextMenuPopupWindowImpl(this.mMenu.getContext(), this.mMenu, this);
        this.mContextMenuPopupWindow = contextMenuPopupWindowImpl;
        contextMenuPopupWindowImpl.show(view, (ViewGroup) view.getParent(), f, f2);
    }
}
