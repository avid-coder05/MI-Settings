package miuix.appcompat.internal.view.menu.context;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import miuix.appcompat.R$attr;
import miuix.appcompat.R$dimen;
import miuix.appcompat.R$layout;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.internal.util.AnimHelper;
import miuix.internal.util.AttributeResolver;
import miuix.internal.widget.ListPopup;

/* loaded from: classes5.dex */
public class ContextMenuPopupWindowImpl extends ListPopup implements ContextMenuPopupWindow {
    private ContextMenuAdapter mAdapter;
    private View mLastAnchor;
    private ViewGroup mLastParent;
    private int mMarginScreen;
    private MenuBuilder mMenu;
    private LinearLayout mPopupContentView;
    private MenuItem mSeparateMenuItem;
    private View mSeparateMenuView;
    private float mX;
    private float mY;

    public ContextMenuPopupWindowImpl(Context context, MenuBuilder menuBuilder, PopupWindow.OnDismissListener onDismissListener) {
        super(context);
        this.mMenu = menuBuilder;
        ContextMenuAdapter contextMenuAdapter = new ContextMenuAdapter(context, this.mMenu);
        this.mAdapter = contextMenuAdapter;
        this.mSeparateMenuItem = contextMenuAdapter.getLastCategorySystemOrderMenuItem();
        prepareMultipleChoiceMenu(context);
        setAdapter(this.mAdapter);
        setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: miuix.appcompat.internal.view.menu.context.ContextMenuPopupWindowImpl.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                MenuItem item = ContextMenuPopupWindowImpl.this.mAdapter.getItem(i);
                ContextMenuPopupWindowImpl.this.mMenu.performItemAction(item, 0);
                if (item.hasSubMenu()) {
                    final SubMenu subMenu = item.getSubMenu();
                    ContextMenuPopupWindowImpl.this.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: miuix.appcompat.internal.view.menu.context.ContextMenuPopupWindowImpl.1.1
                        @Override // android.widget.PopupWindow.OnDismissListener
                        public void onDismiss() {
                            ContextMenuPopupWindowImpl.this.setOnDismissListener(null);
                            ContextMenuPopupWindowImpl.this.update(subMenu);
                            ContextMenuPopupWindowImpl contextMenuPopupWindowImpl = ContextMenuPopupWindowImpl.this;
                            contextMenuPopupWindowImpl.fastShowAsContextMenu(contextMenuPopupWindowImpl.mLastAnchor, ContextMenuPopupWindowImpl.this.mX, ContextMenuPopupWindowImpl.this.mY);
                        }
                    });
                }
                ContextMenuPopupWindowImpl.this.dismiss();
            }
        });
        if (onDismissListener != null) {
            setOnDismissListener(onDismissListener);
        }
        this.mMarginScreen = context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_context_menu_window_margin_screen);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fastShowAsContextMenu(View view, float f, float f2) {
        setWidth(computePopupContentWidth());
        setHeight(-2);
        this.mSeparateMenuView.setVisibility(8);
        showWithAnchor(view, f, f2);
        this.mContentView.forceLayout();
    }

    private int getListViewHeight() {
        ListView listView = (ListView) this.mContentView.findViewById(16908298);
        if (listView == null) {
            this.mContentView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
            return this.mContentView.getMeasuredHeight() + 0;
        }
        ListAdapter adapter = listView.getAdapter();
        int i = 0;
        for (int i2 = 0; i2 < adapter.getCount(); i2++) {
            View view = adapter.getView(i2, null, listView);
            view.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
            i += view.getMeasuredHeight();
        }
        return i;
    }

    private int getMultipleChoiceViewHeight() {
        if (this.mSeparateMenuView.getVisibility() == 0) {
            ViewGroup.LayoutParams layoutParams = this.mSeparateMenuView.getLayoutParams();
            int i = (layoutParams == null || !(layoutParams instanceof ViewGroup.MarginLayoutParams)) ? 0 : ((ViewGroup.MarginLayoutParams) this.mSeparateMenuView.getLayoutParams()).topMargin + 0;
            this.mSeparateMenuView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
            return i + this.mSeparateMenuView.getMeasuredHeight();
        }
        return 0;
    }

    private void prepareMultipleChoiceMenu(Context context) {
        if (this.mSeparateMenuItem == null) {
            this.mSeparateMenuView.setVisibility(8);
            return;
        }
        TextView textView = (TextView) this.mSeparateMenuView.findViewById(16908308);
        textView.setText(this.mSeparateMenuItem.getTitle());
        Drawable resolveDrawable = AttributeResolver.resolveDrawable(context, R$attr.contextMenuSeparateItemBackground);
        if (resolveDrawable != null) {
            textView.setBackground(resolveDrawable);
        }
        this.mSeparateMenuView.setOnClickListener(new View.OnClickListener() { // from class: miuix.appcompat.internal.view.menu.context.ContextMenuPopupWindowImpl.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ContextMenuPopupWindowImpl.this.mMenu.performItemAction(ContextMenuPopupWindowImpl.this.mSeparateMenuItem, 0);
                ContextMenuPopupWindowImpl.this.dismiss();
            }
        });
        AnimHelper.addPressAnim(this.mSeparateMenuView);
    }

    private void showWithAnchor(View view, float f, float f2) {
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        int i = iArr[0] + ((int) f);
        int i2 = iArr[1] + ((int) f2);
        View rootView = view.getRootView();
        boolean z = i <= getWidth();
        boolean z2 = i >= rootView.getWidth() - getWidth();
        int listViewHeight = getListViewHeight();
        float listViewHeight2 = i2 - (getListViewHeight() / 2);
        if (listViewHeight2 < rootView.getHeight() * 0.1f) {
            listViewHeight2 = rootView.getHeight() * 0.1f;
        }
        float multipleChoiceViewHeight = listViewHeight + getMultipleChoiceViewHeight();
        if (listViewHeight2 + multipleChoiceViewHeight > rootView.getHeight() * 0.9f) {
            listViewHeight2 = (rootView.getHeight() * 0.9f) - multipleChoiceViewHeight;
        }
        if (listViewHeight2 < rootView.getHeight() * 0.1f) {
            listViewHeight2 = rootView.getHeight() * 0.1f;
            setHeight((int) (rootView.getHeight() * 0.79999995f));
        }
        if (z) {
            i = this.mMarginScreen;
        } else if (z2) {
            i = (rootView.getWidth() - this.mMarginScreen) - getWidth();
        }
        showAtLocation(view, 0, i, (int) listViewHeight2);
        ListPopup.changeWindowBackground(this.mRootView.getRootView());
    }

    @Override // miuix.internal.widget.ListPopup
    protected int checkMaxHeight(View view) {
        return this.mMaxAllowedHeight;
    }

    @Override // miuix.internal.widget.ListPopup
    protected void prepareContentView(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        this.mPopupContentView = linearLayout;
        linearLayout.setOrientation(1);
        this.mSeparateMenuView = LayoutInflater.from(context).inflate(R$layout.miuix_appcompat_popup_menu_item, (ViewGroup) null, false);
        Drawable resolveDrawable = AttributeResolver.resolveDrawable(context, R$attr.immersionWindowBackground);
        if (resolveDrawable != null) {
            resolveDrawable.getPadding(this.mBackgroundPadding);
            this.mRootView.setBackground(resolveDrawable);
            this.mSeparateMenuView.setBackground(resolveDrawable.getConstantState().newDrawable());
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.setMargins(0, context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_context_menu_separate_item_margin_top), 0, 0);
        this.mPopupContentView.addView(this.mRootView, new LinearLayout.LayoutParams(-1, 0, 1.0f));
        this.mPopupContentView.addView(this.mSeparateMenuView, layoutParams);
        setBackgroundDrawable(null);
        super.setPopupWindowContentView(this.mPopupContentView);
    }

    @Override // miuix.appcompat.internal.view.menu.context.ContextMenuPopupWindow
    public void show(View view, ViewGroup viewGroup, float f, float f2) {
        this.mLastAnchor = view;
        this.mLastParent = viewGroup;
        this.mX = f;
        this.mY = f2;
        if (prepareShow(view, viewGroup)) {
            this.mSeparateMenuView.setElevation(this.mElevation);
            setPopupShadowAlpha(this.mSeparateMenuView);
            showWithAnchor(view, f, f2);
        }
    }

    public void update(Menu menu) {
        this.mAdapter.update(menu);
    }
}
