package miuix.internal.widget;

import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import androidx.appcompat.widget.ViewUtils;
import java.lang.ref.WeakReference;
import miuix.appcompat.R$attr;
import miuix.appcompat.R$dimen;
import miuix.appcompat.R$layout;
import miuix.appcompat.R$style;
import miuix.appcompat.internal.util.SinglePopControl;
import miuix.core.util.MiuixUIUtils;
import miuix.internal.util.AccessibilityUtil;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.DisplayHelper;
import miuix.internal.widget.ListPopup;
import miuix.springback.view.SpringBackLayout;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* loaded from: classes5.dex */
public class ListPopup extends PopupWindow {
    private ListAdapter mAdapter;
    private WeakReference<View> mAnchor;
    protected final Rect mBackgroundPadding;
    private ContentSize mContentSize;
    protected View mContentView;
    private Context mContext;
    private int mDropDownGravity;
    protected int mElevation;
    private boolean mHasShadow;
    private ListView mListView;
    protected int mMaxAllowedHeight;
    private int mMaxAllowedWidth;
    private int mMinAllowedWidth;
    private int mMinMarginScreen;
    private DataSetObserver mObserver;
    private int mOffsetFromStatusBar;
    private int mOffsetX;
    private boolean mOffsetXSet;
    private int mOffsetY;
    private boolean mOffsetYSet;
    private PopupWindow.OnDismissListener mOnDismissListener;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    protected FrameLayout mRootView;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: miuix.internal.widget.ListPopup$1  reason: invalid class name */
    /* loaded from: classes5.dex */
    public class AnonymousClass1 extends DataSetObserver {
        AnonymousClass1() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onChanged$0(View view) {
            int checkMaxHeight = ListPopup.this.checkMaxHeight(view);
            int computePopupContentWidth = ListPopup.this.computePopupContentWidth();
            int i = (checkMaxHeight <= 0 || ListPopup.this.mContentSize.mHeight <= checkMaxHeight) ? ListPopup.this.mContentSize.mHeight : checkMaxHeight;
            view.getLocationInWindow(new int[2]);
            ListPopup.this.update(view, ListPopup.this.calculateXoffset(view), ListPopup.this.calculateYoffset(view), computePopupContentWidth, i);
        }

        @Override // android.database.DataSetObserver
        public void onChanged() {
            final View anchor;
            ListPopup.this.mContentSize.mHasContentWidth = false;
            if (!ListPopup.this.isShowing() || (anchor = ListPopup.this.getAnchor()) == null) {
                return;
            }
            anchor.post(new Runnable() { // from class: miuix.internal.widget.ListPopup$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ListPopup.AnonymousClass1.this.lambda$onChanged$0(anchor);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class ContentSize {
        boolean mHasContentWidth;
        int mHeight;
        int mWidth;

        private ContentSize() {
        }

        /* synthetic */ ContentSize(AnonymousClass1 anonymousClass1) {
            this();
        }

        public void updateWidth(int i) {
            this.mWidth = i;
            this.mHasContentWidth = true;
        }
    }

    public ListPopup(Context context) {
        super(context);
        this.mDropDownGravity = 8388661;
        this.mOffsetFromStatusBar = 0;
        this.mHasShadow = true;
        this.mObserver = new AnonymousClass1();
        this.mContext = context;
        setHeight(-2);
        Resources resources = context.getResources();
        DisplayHelper displayHelper = new DisplayHelper(this.mContext);
        this.mMaxAllowedWidth = Math.min(displayHelper.getWidthPixels(), resources.getDimensionPixelSize(R$dimen.miuix_appcompat_list_menu_dialog_maximum_width));
        this.mMinAllowedWidth = resources.getDimensionPixelSize(R$dimen.miuix_appcompat_list_menu_dialog_minimum_width);
        this.mMaxAllowedHeight = Math.min(displayHelper.getHeightPixels(), resources.getDimensionPixelSize(R$dimen.miuix_appcompat_list_menu_dialog_maximum_height));
        int density = (int) (displayHelper.getDensity() * 8.0f);
        this.mOffsetX = density;
        this.mOffsetY = density;
        this.mBackgroundPadding = new Rect();
        this.mContentSize = new ContentSize(null);
        setFocusable(true);
        setOutsideTouchable(true);
        RoundFrameLayout roundFrameLayout = new RoundFrameLayout(context);
        this.mRootView = roundFrameLayout;
        roundFrameLayout.setOnClickListener(new View.OnClickListener() { // from class: miuix.internal.widget.ListPopup$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ListPopup.this.lambda$new$0(view);
            }
        });
        prepareContentView(context);
        setAnimationStyle(R$style.Animation_PopupWindow_ImmersionMenu);
        this.mElevation = AttributeResolver.resolveDimensionPixelSize(this.mContext, R$attr.popupWindowElevation);
        super.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: miuix.internal.widget.ListPopup$$ExternalSyntheticLambda2
            @Override // android.widget.PopupWindow.OnDismissListener
            public final void onDismiss() {
                ListPopup.this.lambda$new$1();
            }
        });
        this.mMinMarginScreen = context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_context_menu_window_margin_screen);
        this.mOffsetFromStatusBar = context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_context_menu_window_margin_statusbar);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int calculateXoffset(View view) {
        int width;
        int width2;
        int i;
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        boolean z = true;
        if (ViewUtils.isLayoutRtl(view)) {
            if ((iArr[0] - this.mOffsetX) + getWidth() + this.mMinMarginScreen > view.getRootView().getWidth()) {
                width = (view.getRootView().getWidth() - getWidth()) - this.mMinMarginScreen;
                width2 = iArr[0];
                i = width - width2;
            }
            i = 0;
            z = false;
        } else {
            if ((((iArr[0] + view.getWidth()) + this.mOffsetX) - getWidth()) - this.mMinMarginScreen < 0) {
                width = getWidth() + this.mMinMarginScreen;
                width2 = iArr[0] + view.getWidth();
                i = width - width2;
            }
            i = 0;
            z = false;
        }
        if (z) {
            return i;
        }
        boolean z2 = this.mOffsetXSet;
        int i2 = z2 ? this.mOffsetX : 0;
        return (i2 == 0 || z2) ? i2 : ViewUtils.isLayoutRtl(view) ? i2 - (this.mBackgroundPadding.left - this.mOffsetX) : i2 + (this.mBackgroundPadding.right - this.mOffsetX);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int calculateYoffset(View view) {
        int i = this.mOffsetYSet ? this.mOffsetY : ((-view.getHeight()) - this.mBackgroundPadding.top) + this.mOffsetY;
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        float f = iArr[1];
        int i2 = this.mContext.getResources().getDisplayMetrics().heightPixels;
        int checkMaxHeight = checkMaxHeight(view);
        int min = checkMaxHeight > 0 ? Math.min(this.mContentSize.mHeight, checkMaxHeight) : this.mContentSize.mHeight;
        if (min >= i2 || f + i + min + view.getHeight() <= i2) {
            return i;
        }
        return i - ((this.mOffsetYSet ? view.getHeight() : 0) + min);
    }

    public static void changeWindowBackground(View view) {
        WindowManager.LayoutParams layoutParams;
        if (view == null || (layoutParams = (WindowManager.LayoutParams) view.getLayoutParams()) == null) {
            return;
        }
        layoutParams.flags |= 2;
        layoutParams.dimAmount = 0.3f;
        ((WindowManager) view.getContext().getSystemService("window")).updateViewLayout(view, layoutParams);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View getAnchor() {
        WeakReference<View> weakReference = this.mAnchor;
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        PopupWindow.OnDismissListener onDismissListener = this.mOnDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$prepareShow$2(AdapterView adapterView, View view, int i, long j) {
        int headerViewsCount = i - this.mListView.getHeaderViewsCount();
        if (this.mOnItemClickListener == null || headerViewsCount < 0 || headerViewsCount >= this.mAdapter.getCount()) {
            return;
        }
        this.mOnItemClickListener.onItemClick(adapterView, view, headerViewsCount, j);
    }

    private void measureContentSize(ListAdapter listAdapter, ViewGroup viewGroup, Context context, int i) {
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(0, 0);
        int count = listAdapter.getCount();
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        View view = null;
        for (int i5 = 0; i5 < count; i5++) {
            int itemViewType = listAdapter.getItemViewType(i5);
            if (itemViewType != i2) {
                view = null;
                i2 = itemViewType;
            }
            if (viewGroup == null) {
                viewGroup = new FrameLayout(context);
            }
            view = listAdapter.getView(i5, view, viewGroup);
            view.measure(makeMeasureSpec, makeMeasureSpec2);
            i4 += view.getMeasuredHeight();
            if (!this.mContentSize.mHasContentWidth) {
                int measuredWidth = view.getMeasuredWidth();
                if (measuredWidth >= i) {
                    this.mContentSize.updateWidth(i);
                } else if (measuredWidth > i3) {
                    i3 = measuredWidth;
                }
            }
        }
        ContentSize contentSize = this.mContentSize;
        if (!contentSize.mHasContentWidth) {
            contentSize.updateWidth(i3);
        }
        this.mContentSize.mHeight = i4;
    }

    private boolean shouldSetElevation() {
        return this.mHasShadow && (Build.VERSION.SDK_INT > 29 || !AccessibilityUtil.isTalkBackActive(this.mContext));
    }

    private void showWithAnchor(View view) {
        showAsDropDown(view, calculateXoffset(view), calculateYoffset(view), this.mDropDownGravity);
        HapticCompat.performHapticFeedback(view, HapticFeedbackConstants.MIUI_POPUP_NORMAL);
        changeWindowBackground(this.mRootView.getRootView());
    }

    protected int checkMaxHeight(View view) {
        return Math.min(this.mMaxAllowedHeight, (new DisplayHelper(this.mContext).getHeightPixels() - MiuixUIUtils.getStatusBarHeight(this.mContext)) - (view == null ? 0 : view.getHeight()));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int computePopupContentWidth() {
        if (!this.mContentSize.mHasContentWidth) {
            measureContentSize(this.mAdapter, null, this.mContext, this.mMaxAllowedWidth);
        }
        int max = Math.max(this.mContentSize.mWidth, this.mMinAllowedWidth);
        Rect rect = this.mBackgroundPadding;
        return max + rect.left + rect.right;
    }

    @Override // android.widget.PopupWindow
    public void dismiss() {
        super.dismiss();
        SinglePopControl.hidePop(this.mContext, this);
    }

    public void fastShow(View view, ViewGroup viewGroup) {
        setWidth(computePopupContentWidth());
        showWithAnchor(view);
    }

    public int getHorizontalOffset() {
        return this.mOffsetX;
    }

    public ListView getListView() {
        return this.mListView;
    }

    public int getMinMarginScreen() {
        return this.mMinMarginScreen;
    }

    public int getOffsetFromStatusBar() {
        return this.mOffsetFromStatusBar;
    }

    public int getVerticalOffset() {
        return this.mOffsetY;
    }

    protected void prepareContentView(Context context) {
        Drawable resolveDrawable = AttributeResolver.resolveDrawable(this.mContext, R$attr.immersionWindowBackground);
        if (resolveDrawable != null) {
            resolveDrawable.getPadding(this.mBackgroundPadding);
            this.mRootView.setBackground(resolveDrawable);
        }
        setBackgroundDrawable(new ColorDrawable(0));
        setPopupWindowContentView(this.mRootView);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean prepareShow(View view, ViewGroup viewGroup) {
        if (view == null) {
            Log.e("ListPopupWindow", "show: anchor is null");
            return false;
        }
        if (this.mContentView == null) {
            View inflate = LayoutInflater.from(this.mContext).inflate(R$layout.miuix_appcompat_list_popup_list, (ViewGroup) null);
            this.mContentView = inflate;
            inflate.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: miuix.internal.widget.ListPopup.2
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View view2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    view2.removeOnLayoutChangeListener(this);
                    boolean z = false;
                    if (ListPopup.this.mListView.getAdapter() != null && ListPopup.this.mListView.getLastVisiblePosition() == ListPopup.this.mListView.getAdapter().getCount() - 1) {
                        z = true;
                    }
                    ((SpringBackLayout) ListPopup.this.mContentView).setEnabled(!z);
                }
            });
        }
        if (this.mRootView.getChildCount() != 1 || this.mRootView.getChildAt(0) != this.mContentView) {
            this.mRootView.removeAllViews();
            this.mRootView.addView(this.mContentView);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mContentView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -2;
            layoutParams.gravity = 16;
        }
        if (Build.VERSION.SDK_INT >= 21 && shouldSetElevation()) {
            this.mRootView.setElevation(this.mElevation);
            setElevation(this.mElevation);
            setPopupShadowAlpha(this.mRootView);
        }
        ListView listView = (ListView) this.mContentView.findViewById(16908298);
        this.mListView = listView;
        if (listView == null) {
            Log.e("ListPopupWindow", "list not found");
            return false;
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: miuix.internal.widget.ListPopup$$ExternalSyntheticLambda1
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view2, int i, long j) {
                ListPopup.this.lambda$prepareShow$2(adapterView, view2, i, j);
            }
        });
        this.mListView.setAdapter(this.mAdapter);
        setWidth(computePopupContentWidth());
        int checkMaxHeight = checkMaxHeight(view);
        if (checkMaxHeight > 0 && this.mContentSize.mHeight > checkMaxHeight) {
            setHeight(checkMaxHeight);
        }
        ((InputMethodManager) this.mContext.getApplicationContext().getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
        return true;
    }

    public void setAdapter(ListAdapter listAdapter) {
        ListAdapter listAdapter2 = this.mAdapter;
        if (listAdapter2 != null) {
            listAdapter2.unregisterDataSetObserver(this.mObserver);
        }
        this.mAdapter = listAdapter;
        if (listAdapter != null) {
            listAdapter.registerDataSetObserver(this.mObserver);
        }
    }

    public void setContentWidth(int i) {
        this.mContentSize.updateWidth(i);
    }

    public void setDropDownGravity(int i) {
        this.mDropDownGravity = i;
    }

    public void setHasShadow(boolean z) {
        this.mHasShadow = z;
    }

    public void setHorizontalOffset(int i) {
        this.mOffsetX = i;
        this.mOffsetXSet = true;
    }

    public void setMaxAllowedHeight(int i) {
        this.mMaxAllowedHeight = i;
    }

    @Override // android.widget.PopupWindow
    public void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setPopupShadowAlpha(View view) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (MiuixUIUtils.isFreeformMode(this.mContext)) {
                view.setOutlineProvider(null);
            } else {
                view.setOutlineProvider(new ViewOutlineProvider() { // from class: miuix.internal.widget.ListPopup.3
                    @Override // android.view.ViewOutlineProvider
                    public void getOutline(View view2, Outline outline) {
                        if (view2.getWidth() == 0 || view2.getHeight() == 0) {
                            return;
                        }
                        outline.setAlpha(AttributeResolver.resolveFloat(view2.getContext(), R$attr.popupWindowShadowAlpha, 0.0f));
                        if (view2.getBackground() != null) {
                            view2.getBackground().getOutline(outline);
                        }
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setPopupWindowContentView(View view) {
        super.setContentView(view);
    }

    public void setVerticalOffset(int i) {
        this.mOffsetY = i;
        this.mOffsetYSet = true;
    }

    public void show(View view, ViewGroup viewGroup) {
        if (prepareShow(view, viewGroup)) {
            showWithAnchor(view);
        }
    }

    @Override // android.widget.PopupWindow
    public void showAsDropDown(View view, int i, int i2, int i3) {
        super.showAsDropDown(view, i, i2, i3);
        this.mAnchor = new WeakReference<>(view);
        SinglePopControl.showPop(this.mContext, this);
    }

    @Override // android.widget.PopupWindow
    public void showAtLocation(View view, int i, int i2, int i3) {
        super.showAtLocation(view, i, i2, i3);
        SinglePopControl.showPop(this.mContext, this);
    }
}
