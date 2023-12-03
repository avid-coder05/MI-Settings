package miuix.appcompat.internal.view.menu;

import miuix.appcompat.internal.view.menu.MenuBuilder;

/* loaded from: classes5.dex */
public interface MenuView {

    /* loaded from: classes5.dex */
    public interface ItemView {
        MenuItemImpl getItemData();

        void initialize(MenuItemImpl menuItemImpl, int i);

        boolean prefersCondensedTitle();

        void setItemInvoker(MenuBuilder.ItemInvoker itemInvoker);
    }

    boolean filterLeftoverView(int i);

    boolean hasBackgroundView();

    boolean hasBlurBackgroundView();

    void initialize(MenuBuilder menuBuilder);
}
