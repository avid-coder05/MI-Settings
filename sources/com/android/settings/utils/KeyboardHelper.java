package com.android.settings.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.provider.MiuiSettings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.widget.NestedScrollView;
import com.android.settings.R;
import java.util.Collection;
import miui.content.res.ThemeResources;
import miuix.animation.listener.UpdateInfo;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.ActionBarTransitionListener;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class KeyboardHelper implements TextView.OnEditorActionListener {
    private ActionBar mActionBar;
    private Activity mActivity;
    private View mChildOfContent;
    private int mDisplayHeight;
    private int mFocusedLocation;
    private boolean mIsInMulti;
    private boolean mKeyboardShow = false;
    private NestedScrollView mNestedScrollView;
    private int mStatusBarHeight;
    private int mUsableHeightPrevious;

    private KeyboardHelper(Activity activity) {
        this.mIsInMulti = false;
        this.mActivity = activity;
        this.mIsInMulti = activity.isInMultiWindowMode();
        refreshSoftInputMode();
        NestedScrollView nestedScrollView = (NestedScrollView) this.mActivity.findViewById(R.id.scrollview);
        this.mNestedScrollView = nestedScrollView;
        if (nestedScrollView == null) {
            Log.e("KeyboardHelper", "mNestedScrollView is empty !!!");
            return;
        }
        nestedScrollView.setFitsSystemWindows(MiuiSettings.Global.getBoolean(this.mActivity.getContentResolver(), "force_fsg_nav_bar"));
        int identifier = this.mActivity.getResources().getIdentifier("status_bar_height", "dimen", ThemeResources.FRAMEWORK_PACKAGE);
        if (identifier > 0) {
            this.mStatusBarHeight = this.mActivity.getResources().getDimensionPixelSize(identifier);
        }
        View childAt = ((FrameLayout) this.mActivity.findViewById(16908290)).getChildAt(0);
        this.mChildOfContent = childAt;
        childAt.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.android.settings.utils.KeyboardHelper.1
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                if (KeyboardHelper.this.mActivity == null) {
                    return;
                }
                KeyboardHelper.this.possiblyResizeChildOfContent();
            }
        });
        Window window = this.mActivity.getWindow();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.mDisplayHeight = displayMetrics.heightPixels;
        if (activity instanceof AppCompatActivity) {
            ActionBar appCompatActionBar = ((AppCompatActivity) activity).getAppCompatActionBar();
            this.mActionBar = appCompatActionBar;
            if (appCompatActionBar != null) {
                appCompatActionBar.setActionBarTransitionListener(new ActionBarTransitionListener() { // from class: com.android.settings.utils.KeyboardHelper.2
                    @Override // miuix.appcompat.app.ActionBarTransitionListener
                    public void onActionBarMove(float f, float f2) {
                        if (f == 0.0f || f2 == 0.0f) {
                            return;
                        }
                        View findFocus = KeyboardHelper.this.mNestedScrollView.findFocus();
                        if (!(findFocus instanceof EditText) || KeyboardHelper.this.mKeyboardShow) {
                            return;
                        }
                        findFocus.clearFocus();
                    }

                    @Override // miuix.appcompat.app.ActionBarTransitionListener
                    public void onTransitionBegin(Object obj) {
                    }

                    @Override // miuix.appcompat.app.ActionBarTransitionListener
                    public void onTransitionComplete(Object obj) {
                    }

                    @Override // miuix.appcompat.app.ActionBarTransitionListener
                    public void onTransitionUpdate(Object obj, Collection<UpdateInfo> collection) {
                    }
                });
            }
        }
        setEditActionListener(this.mNestedScrollView, this);
    }

    public static KeyboardHelper assistActivity(Activity activity) {
        return new KeyboardHelper(activity);
    }

    private int computeUsableHeight() {
        Rect rect = new Rect();
        this.mChildOfContent.getWindowVisibleDisplayFrame(rect);
        return rect.bottom - rect.top;
    }

    private int getFocusedLocation(View view) {
        int i;
        int[] iArr = new int[2];
        View view2 = null;
        while (view != null) {
            if (view.isFocused()) {
                view2 = view;
            }
            view = view instanceof ViewGroup ? ((ViewGroup) view).getFocusedChild() : null;
        }
        if (view2 != null) {
            view2.getLocationInWindow(iArr);
            i = view2.getHeight();
        } else {
            i = 0;
        }
        return (iArr[1] + i) - this.mStatusBarHeight;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void possiblyResizeChildOfContent() {
        boolean isInMultiWindowMode = this.mActivity.isInMultiWindowMode();
        if (isInMultiWindowMode != this.mIsInMulti) {
            this.mIsInMulti = isInMultiWindowMode;
            refreshSoftInputMode();
        }
        int computeUsableHeight = computeUsableHeight();
        Log.d("KeyboardHelper", "possiblyResizeChildOfContent -> " + computeUsableHeight);
        if (this.mIsInMulti || computeUsableHeight == this.mUsableHeightPrevious) {
            return;
        }
        int height = this.mChildOfContent.getRootView().getHeight();
        int i = height - computeUsableHeight;
        boolean z = i > height / 4;
        this.mKeyboardShow = z;
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null) {
            if (z) {
                actionBar.setResizable(false);
            } else {
                actionBar.setResizable(true);
            }
        }
        int focusedLocation = getFocusedLocation(this.mNestedScrollView);
        this.mFocusedLocation = focusedLocation;
        int scrollY = this.mKeyboardShow ? (focusedLocation - computeUsableHeight) + this.mNestedScrollView.getScrollY() : this.mNestedScrollView.getScrollY();
        int i2 = scrollY >= 0 ? scrollY : 0;
        Log.d("KeyboardHelper", "[" + isInMultiWindowMode + "][" + this.mFocusedLocation + "][" + height + "][" + computeUsableHeight + "][" + i + "][" + this.mDisplayHeight + "][" + this.mNestedScrollView.getScrollY() + "][" + i2 + "]");
        this.mNestedScrollView.setScrollY(i2);
        this.mChildOfContent.requestLayout();
        this.mUsableHeightPrevious = computeUsableHeight;
    }

    private void refreshSoftInputMode() {
        Window window = this.mActivity.getWindow();
        if (this.mIsInMulti) {
            window.setSoftInputMode(32);
        } else {
            window.setSoftInputMode(16);
        }
    }

    private void setEditActionListener(ViewGroup viewGroup, TextView.OnEditorActionListener onEditorActionListener) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof EditText) {
                ((EditText) childAt).setOnEditorActionListener(onEditorActionListener);
            } else if (childAt instanceof ViewGroup) {
                setEditActionListener((ViewGroup) childAt, onEditorActionListener);
            }
        }
    }

    public void destroy() {
        setEditActionListener(this.mNestedScrollView, null);
        this.mActivity = null;
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null) {
            actionBar.setActionBarTransitionListener(null);
            this.mActionBar = null;
        }
    }

    @Override // android.widget.TextView.OnEditorActionListener
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 || this.mActivity.isInMultiWindowMode()) {
            return false;
        }
        textView.postDelayed(new Runnable() { // from class: com.android.settings.utils.KeyboardHelper.3
            @Override // java.lang.Runnable
            public void run() {
                KeyboardHelper keyboardHelper = KeyboardHelper.this;
                keyboardHelper.relocateFocused(keyboardHelper.mNestedScrollView);
            }
        }, 50L);
        return false;
    }

    public void relocateFocused(ViewGroup viewGroup) {
        this.mNestedScrollView.setScrollY((getFocusedLocation(viewGroup) - computeUsableHeight()) + this.mNestedScrollView.getScrollY());
    }
}
