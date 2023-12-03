package miuix.appcompat.internal.app.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Looper;
import android.os.MessageQueue;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import miuix.animation.Folme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.utils.EaseManager;
import miuix.appcompat.R$dimen;
import miuix.appcompat.R$id;
import miuix.internal.util.ActionBarUtils;
import miuix.internal.util.DeviceHelper;
import miuix.internal.util.ViewUtils;
import miuix.view.ActionModeAnimationListener;
import miuix.view.CompatViewMethod;
import miuix.view.inputmethod.InputMethodHelper;
import miuix.viewpager.widget.ViewPager;

/* loaded from: classes5.dex */
public class SearchActionModeView extends FrameLayout implements Animator.AnimatorListener, ActionModeView, TextWatcher, View.OnClickListener, MessageQueue.IdleHandler {
    private ActionBarContainer mActionBarContainer;
    private int mActionBarContainerLocationY;
    private int mActionBarTopMargin;
    private ActionBarView mActionBarView;
    private WeakReference<View> mAnchorView;
    private boolean mAnimateToVisible;
    private WeakReference<View> mAnimateView;
    private int mAnimateViewTranslationYLength;
    private int mAnimateViewTranslationYStart;
    private boolean mAnimationCanceled;
    private List<ActionModeAnimationListener> mAnimationListeners;
    private float mAnimationProgress;
    private int mContentOriginPaddingBottom;
    private int mContentOriginPaddingTop;
    private ObjectAnimator mCurrentAnimation;
    private View mDimView;
    private int mInputPaddingRight;
    private int mInputPaddingTop;
    private EditText mInputView;
    private int mInputViewTranslationYLength;
    private int mInputViewTranslationYStart;
    private int[] mLocation;
    private int mOriginalPaddingTop;
    private int mParentLocationY;
    private boolean mRequestAnimation;
    private WeakReference<View> mResultView;
    private int mResultViewOriginMarginBottom;
    private int mResultViewOriginMarginTop;
    private boolean mResultViewSet;
    private ViewGroup mSearchContainer;
    private int mSearchViewHeight;
    private ActionBarContainer mSplitActionBarContainer;
    private int mStatusBarPaddingTop;
    private TextView mTextCancel;
    private int mTextLengthBeforeChanged;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public class ActionBarAnimationProcessor implements ActionModeAnimationListener {
        ActionBarAnimationProcessor() {
        }

        @Override // miuix.view.ActionModeAnimationListener
        public void onStart(boolean z) {
            if (z) {
                SearchActionModeView.this.mActionBarContainer.setVisibility(4);
            } else {
                SearchActionModeView.this.mActionBarContainer.setVisibility(0);
            }
        }

        @Override // miuix.view.ActionModeAnimationListener
        public void onStop(boolean z) {
            View tabContainer;
            if (!z || (tabContainer = SearchActionModeView.this.getActionBarContainer().getTabContainer()) == null) {
                return;
            }
            tabContainer.setVisibility(8);
        }

        @Override // miuix.view.ActionModeAnimationListener
        public void onUpdate(boolean z, float f) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public class ContentViewAnimationProcessor implements ActionModeAnimationListener {
        boolean mDimViewVisible;
        int mTmpAnimAccessibilityMode = 0;
        int mTmpAnchorAccessibilityMode = 0;

        ContentViewAnimationProcessor() {
        }

        @Override // miuix.view.ActionModeAnimationListener
        public void onStart(boolean z) {
            if (SearchActionModeView.this.mParentLocationY == Integer.MAX_VALUE) {
                ((View) SearchActionModeView.this.getParent()).getLocationInWindow(SearchActionModeView.this.mLocation);
                SearchActionModeView searchActionModeView = SearchActionModeView.this;
                searchActionModeView.mParentLocationY = searchActionModeView.mLocation[1];
            }
            View contentView = SearchActionModeView.this.getContentView();
            View view = SearchActionModeView.this.mAnchorView != null ? (View) SearchActionModeView.this.mAnchorView.get() : null;
            View view2 = SearchActionModeView.this.mAnimateView != null ? (View) SearchActionModeView.this.mAnimateView.get() : null;
            View view3 = SearchActionModeView.this.mResultView != null ? (View) SearchActionModeView.this.mResultView.get() : null;
            if (view != null) {
                view.setAlpha(0.0f);
            }
            if (z) {
                if (contentView != null) {
                    SearchActionModeView.this.mContentOriginPaddingTop = contentView.getPaddingTop();
                    SearchActionModeView.this.mContentOriginPaddingBottom = contentView.getPaddingBottom();
                }
                if (view != null) {
                    view.getLocationInWindow(SearchActionModeView.this.mLocation);
                    this.mTmpAnchorAccessibilityMode = view.getImportantForAccessibility();
                    view.setImportantForAccessibility(4);
                    SearchActionModeView searchActionModeView2 = SearchActionModeView.this;
                    searchActionModeView2.mInputViewTranslationYStart = searchActionModeView2.mLocation[1];
                } else {
                    if (SearchActionModeView.this.mActionBarContainerLocationY == Integer.MAX_VALUE) {
                        SearchActionModeView.this.getActionBarContainer().getLocationInWindow(SearchActionModeView.this.mLocation);
                        SearchActionModeView searchActionModeView3 = SearchActionModeView.this;
                        searchActionModeView3.mActionBarContainerLocationY = searchActionModeView3.mLocation[1];
                    }
                    SearchActionModeView searchActionModeView4 = SearchActionModeView.this;
                    searchActionModeView4.mInputViewTranslationYStart = searchActionModeView4.mActionBarContainerLocationY + SearchActionModeView.this.getActionBarContainer().getHeight();
                }
                SearchActionModeView searchActionModeView5 = SearchActionModeView.this;
                SearchActionModeView.access$1620(searchActionModeView5, searchActionModeView5.mParentLocationY);
                SearchActionModeView searchActionModeView6 = SearchActionModeView.this;
                searchActionModeView6.mAnimateViewTranslationYStart = searchActionModeView6.mInputViewTranslationYStart - ((int) SearchActionModeView.this.getActionBarContainer().getY());
                SearchActionModeView searchActionModeView7 = SearchActionModeView.this;
                searchActionModeView7.mInputViewTranslationYLength = -searchActionModeView7.mInputViewTranslationYStart;
                SearchActionModeView searchActionModeView8 = SearchActionModeView.this;
                searchActionModeView8.mAnimateViewTranslationYLength = -searchActionModeView8.mAnimateViewTranslationYStart;
            } else {
                if (view != null) {
                    view.setImportantForAccessibility(this.mTmpAnchorAccessibilityMode);
                }
                this.mDimViewVisible = SearchActionModeView.this.mDimView != null && SearchActionModeView.this.mDimView.getVisibility() == 0;
                if (SearchActionModeView.this.mActionBarContainer == null || !SearchActionModeView.this.mActionBarContainer.isBlurEnable()) {
                    SearchActionModeView searchActionModeView9 = SearchActionModeView.this;
                    searchActionModeView9.setContentViewTranslation(searchActionModeView9.mStatusBarPaddingTop);
                    SearchActionModeView.this.setContentViewPadding(0, 0);
                } else {
                    SearchActionModeView searchActionModeView10 = SearchActionModeView.this;
                    searchActionModeView10.setContentViewTranslation(this.mDimViewVisible ? searchActionModeView10.mStatusBarPaddingTop : -searchActionModeView10.mContentOriginPaddingTop);
                }
            }
            if (!z) {
                if (view3 != null) {
                    if (view2 != null) {
                        view2.setImportantForAccessibility(this.mTmpAnimAccessibilityMode);
                    }
                    view3.setImportantForAccessibility(4);
                }
            } else if (view3 != null) {
                if (view2 != null) {
                    this.mTmpAnimAccessibilityMode = view2.getImportantForAccessibility();
                    view2.setImportantForAccessibility(4);
                }
                view3.setImportantForAccessibility(1);
            }
        }

        @Override // miuix.view.ActionModeAnimationListener
        public void onStop(boolean z) {
            if (!z) {
                View view = SearchActionModeView.this.mAnimateView != null ? (View) SearchActionModeView.this.mAnimateView.get() : null;
                if (view != null) {
                    view.setTranslationY(0.0f);
                }
            }
            View view2 = SearchActionModeView.this.mAnchorView != null ? (View) SearchActionModeView.this.mAnchorView.get() : null;
            if (view2 != null) {
                view2.setEnabled(!z);
            }
            if (SearchActionModeView.this.mStatusBarPaddingTop > 0) {
                SearchActionModeView.this.setContentViewTranslation(0);
                SearchActionModeView searchActionModeView = SearchActionModeView.this;
                searchActionModeView.setContentViewPadding(z ? searchActionModeView.mStatusBarPaddingTop : 0, 0);
            }
            if (z && SearchActionModeView.this.mActionBarContainer != null && SearchActionModeView.this.mActionBarContainer.isBlurEnable()) {
                SearchActionModeView searchActionModeView2 = SearchActionModeView.this;
                searchActionModeView2.setContentViewTranslation(-searchActionModeView2.mContentOriginPaddingTop);
            }
        }

        @Override // miuix.view.ActionModeAnimationListener
        public void onUpdate(boolean z, float f) {
            if (!z) {
                f = 1.0f - f;
            }
            View view = SearchActionModeView.this.mAnimateView != null ? (View) SearchActionModeView.this.mAnimateView.get() : null;
            SearchActionModeView.this.setContentViewTranslation((int) (r0.mStatusBarPaddingTop * f));
            if (view != null) {
                view.setTranslationY(SearchActionModeView.this.mAnimateViewTranslationYStart + (SearchActionModeView.this.mAnimateViewTranslationYLength * f));
            }
            SearchActionModeView.this.setTranslationY(r3.mInputViewTranslationYStart + (f * SearchActionModeView.this.mInputViewTranslationYLength));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public class DimViewAnimationProcessor implements ActionModeAnimationListener {
        DimViewAnimationProcessor() {
        }

        @Override // miuix.view.ActionModeAnimationListener
        public void onStart(boolean z) {
            if (z) {
                SearchActionModeView.this.mDimView.setOnClickListener(SearchActionModeView.this);
                SearchActionModeView.this.mDimView.setVisibility(0);
                SearchActionModeView.this.mDimView.setAlpha(0.0f);
            }
        }

        @Override // miuix.view.ActionModeAnimationListener
        public void onStop(boolean z) {
            if (z) {
                if (SearchActionModeView.this.mInputView.getText().length() > 0) {
                    SearchActionModeView.this.mDimView.setVisibility(8);
                    return;
                }
                return;
            }
            SearchActionModeView.this.mDimView.setVisibility(8);
            SearchActionModeView.this.mDimView.setAlpha(1.0f);
            SearchActionModeView.this.mDimView.setTranslationY(0.0f);
        }

        @Override // miuix.view.ActionModeAnimationListener
        public void onUpdate(boolean z, float f) {
            if (!z) {
                f = 1.0f - f;
            }
            SearchActionModeView.this.mDimView.setAlpha(f);
            if (SearchActionModeView.this.shouldAnimateContent()) {
                View view = (View) SearchActionModeView.this.mAnimateView.get();
                SearchActionModeView.this.mDimView.setTranslationY((view != null ? view.getTranslationY() + SearchActionModeView.this.mStatusBarPaddingTop : 0.0f) + (SearchActionModeView.this.mActionBarContainer != null ? SearchActionModeView.this.mContentOriginPaddingTop : 0));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public class SearchViewAnimationProcessor implements ActionModeAnimationListener {
        SearchViewAnimationProcessor() {
        }

        @Override // miuix.view.ActionModeAnimationListener
        public void onStart(boolean z) {
            if (z) {
                SearchActionModeView.this.mInputView.getText().clear();
                SearchActionModeView.this.mInputView.addTextChangedListener(SearchActionModeView.this);
                SearchActionModeView.this.mTextCancel.setTranslationX(SearchActionModeView.this.mTextCancel.getWidth());
            }
        }

        @Override // miuix.view.ActionModeAnimationListener
        public void onStop(boolean z) {
            if (z) {
                return;
            }
            SearchActionModeView.this.mInputView.removeTextChangedListener(SearchActionModeView.this);
        }

        @Override // miuix.view.ActionModeAnimationListener
        public void onUpdate(boolean z, float f) {
            if (!z) {
                f = 1.0f - f;
            }
            int i = SearchActionModeView.this.mStatusBarPaddingTop;
            SearchActionModeView searchActionModeView = SearchActionModeView.this;
            float f2 = i * f;
            searchActionModeView.setPadding(searchActionModeView.getPaddingLeft(), (int) (SearchActionModeView.this.mOriginalPaddingTop + f2), SearchActionModeView.this.getPaddingRight(), SearchActionModeView.this.getPaddingBottom());
            SearchActionModeView.this.getLayoutParams().height = SearchActionModeView.this.mSearchViewHeight + ((int) f2);
            SearchActionModeView.this.requestLayout();
            updateCancelView(f, SearchActionModeView.this.mInputPaddingRight);
        }

        public void updateCancelView(float f, int i) {
            float f2 = 1.0f - f;
            if (ViewUtils.isLayoutRtl(SearchActionModeView.this.mTextCancel)) {
                f2 = f - 1.0f;
            }
            SearchActionModeView.this.mTextCancel.setTranslationX(SearchActionModeView.this.mTextCancel.getWidth() * f2);
            if (SearchActionModeView.this.mSearchContainer.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) SearchActionModeView.this.mSearchContainer.getLayoutParams()).setMarginEnd((int) (((SearchActionModeView.this.mTextCancel.getWidth() - i) * f) + i));
            }
            SearchActionModeView.this.mSearchContainer.requestLayout();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public class SplitActionBarAnimationProcessor implements ActionModeAnimationListener {
        SplitActionBarAnimationProcessor() {
        }

        @Override // miuix.view.ActionModeAnimationListener
        public void onStart(boolean z) {
        }

        @Override // miuix.view.ActionModeAnimationListener
        public void onStop(boolean z) {
        }

        @Override // miuix.view.ActionModeAnimationListener
        public void onUpdate(boolean z, float f) {
            if (!z) {
                f = 1.0f - f;
            }
            ActionBarContainer splitActionBarContainer = SearchActionModeView.this.getSplitActionBarContainer();
            if (splitActionBarContainer != null) {
                splitActionBarContainer.setTranslationY(f * splitActionBarContainer.getHeight());
            }
        }
    }

    public SearchActionModeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mLocation = new int[2];
        this.mParentLocationY = Integer.MAX_VALUE;
        this.mActionBarContainerLocationY = Integer.MAX_VALUE;
        setAlpha(0.0f);
        this.mSearchViewHeight = context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_search_view_default_height);
        this.mInputPaddingTop = context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_search_mode_bg_padding_top);
        this.mInputPaddingRight = shouldHideCancelText() ? 0 : context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_search_mode_bg_padding);
    }

    static /* synthetic */ int access$1620(SearchActionModeView searchActionModeView, int i) {
        int i2 = searchActionModeView.mInputViewTranslationYStart - i;
        searchActionModeView.mInputViewTranslationYStart = i2;
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View getContentView() {
        ViewGroup actionBarOverlayLayout = ActionBarUtils.getActionBarOverlayLayout(this);
        if (actionBarOverlayLayout != null) {
            return actionBarOverlayLayout.findViewById(16908290);
        }
        return null;
    }

    private MessageQueue getMessageQueue() {
        return Looper.myQueue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAnimationEnd$0() {
        setResultViewMargin(this.mAnimateToVisible);
    }

    private void queueIdleHandler() {
        removeIdleHandler();
        getMessageQueue().addIdleHandler(this);
    }

    private void removeIdleHandler() {
        getMessageQueue().removeIdleHandler(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldAnimateContent() {
        return (this.mAnchorView == null || this.mAnimateView == null) ? false : true;
    }

    private boolean shouldHideCancelText() {
        String language = Locale.getDefault().getLanguage();
        return ("zh".equalsIgnoreCase(language) || "en".equalsIgnoreCase(language)) ? false : true;
    }

    @Override // miuix.appcompat.internal.app.widget.ActionModeView
    public void addAnimationListener(ActionModeAnimationListener actionModeAnimationListener) {
        if (actionModeAnimationListener == null) {
            return;
        }
        if (this.mAnimationListeners == null) {
            this.mAnimationListeners = new ArrayList();
        }
        if (this.mAnimationListeners.contains(actionModeAnimationListener)) {
            return;
        }
        this.mAnimationListeners.add(actionModeAnimationListener);
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable editable) {
        View view;
        if ((editable == null ? 0 : editable.length()) == 0) {
            View view2 = this.mDimView;
            if (view2 != null) {
                view2.setVisibility(0);
            }
            InputMethodHelper.getInstance(getContext()).showKeyBoard(this.mInputView);
        } else if (this.mTextLengthBeforeChanged != 0 || (view = this.mDimView) == null) {
        } else {
            view.setVisibility(8);
        }
    }

    @Override // miuix.appcompat.internal.app.widget.ActionModeView
    public void animateToVisibility(boolean z) {
        if (this.mAnimateToVisible == z) {
            this.mRequestAnimation = false;
            return;
        }
        pollViews();
        this.mAnimateToVisible = z;
        this.mCurrentAnimation = makeAnimation();
        if (z) {
            createAnimationListeners();
            setOverlayMode(true);
        }
        notifyAnimationStart(z);
        if (shouldAnimateContent()) {
            requestLayout();
            this.mRequestAnimation = true;
        } else {
            this.mCurrentAnimation.start();
        }
        if (this.mAnimateToVisible) {
            return;
        }
        this.mInputView.clearFocus();
        ((InputMethodManager) getContext().getSystemService("input_method")).hideSoftInputFromWindow(this.mInputView.getWindowToken(), 0);
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        this.mTextLengthBeforeChanged = charSequence == null ? 0 : charSequence.length();
    }

    @Override // miuix.appcompat.internal.app.widget.ActionModeView
    public void closeMode() {
        ObjectAnimator objectAnimator = this.mCurrentAnimation;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    protected void createAnimationListeners() {
        if (this.mAnimationListeners == null) {
            this.mAnimationListeners = new ArrayList();
        }
        this.mAnimationListeners.add(new SearchViewAnimationProcessor());
        if (shouldAnimateContent()) {
            this.mAnimationListeners.add(new ContentViewAnimationProcessor());
            this.mAnimationListeners.add(new ActionBarAnimationProcessor());
            this.mAnimationListeners.add(new SplitActionBarAnimationProcessor());
        }
        if (getDimView() != null) {
            this.mAnimationListeners.add(new DimViewAnimationProcessor());
        }
    }

    protected void finishAnimation() {
        ObjectAnimator objectAnimator = this.mCurrentAnimation;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.mCurrentAnimation = null;
        }
    }

    protected ActionBarContainer getActionBarContainer() {
        if (this.mActionBarContainer == null) {
            ActionBarOverlayLayout actionBarOverlayLayout = (ActionBarOverlayLayout) ActionBarUtils.getActionBarOverlayLayout(this);
            if (actionBarOverlayLayout != null) {
                int i = 0;
                while (true) {
                    if (i >= actionBarOverlayLayout.getChildCount()) {
                        break;
                    }
                    View childAt = actionBarOverlayLayout.getChildAt(i);
                    if (childAt.getId() == R$id.action_bar_container && (childAt instanceof ActionBarContainer)) {
                        this.mActionBarContainer = (ActionBarContainer) childAt;
                        break;
                    }
                    i++;
                }
            }
            ActionBarContainer actionBarContainer = this.mActionBarContainer;
            if (actionBarContainer != null) {
                int i2 = ((ViewGroup.MarginLayoutParams) actionBarContainer.getLayoutParams()).topMargin;
                this.mActionBarTopMargin = i2;
                if (i2 > 0) {
                    setPadding(getPaddingLeft(), this.mOriginalPaddingTop + this.mActionBarTopMargin, getPaddingRight(), getPaddingBottom());
                }
            }
        }
        return this.mActionBarContainer;
    }

    protected ActionBarView getActionBarView() {
        ViewGroup actionBarOverlayLayout;
        if (this.mActionBarView == null && (actionBarOverlayLayout = ActionBarUtils.getActionBarOverlayLayout(this)) != null) {
            this.mActionBarView = (ActionBarView) actionBarOverlayLayout.findViewById(R$id.action_bar);
        }
        return this.mActionBarView;
    }

    public float getAnimationProgress() {
        return this.mAnimationProgress;
    }

    protected View getDimView() {
        ViewGroup actionBarOverlayLayout;
        if (this.mDimView == null && (actionBarOverlayLayout = ActionBarUtils.getActionBarOverlayLayout(this)) != null) {
            ViewStub viewStub = (ViewStub) actionBarOverlayLayout.findViewById(R$id.search_mask_vs);
            if (viewStub != null) {
                this.mDimView = viewStub.inflate();
            } else {
                this.mDimView = actionBarOverlayLayout.findViewById(R$id.search_mask);
            }
        }
        return this.mDimView;
    }

    public EditText getSearchInput() {
        return this.mInputView;
    }

    protected ActionBarContainer getSplitActionBarContainer() {
        ViewGroup actionBarOverlayLayout;
        if (this.mSplitActionBarContainer == null && (actionBarOverlayLayout = ActionBarUtils.getActionBarOverlayLayout(this)) != null) {
            int i = 0;
            while (true) {
                if (i >= actionBarOverlayLayout.getChildCount()) {
                    break;
                }
                View childAt = actionBarOverlayLayout.getChildAt(i);
                if (childAt.getId() == R$id.split_action_bar && (childAt instanceof ActionBarContainer)) {
                    this.mSplitActionBarContainer = (ActionBarContainer) childAt;
                    break;
                }
                i++;
            }
        }
        return this.mSplitActionBarContainer;
    }

    protected ViewPager getViewPager() {
        ActionBarOverlayLayout actionBarOverlayLayout = (ActionBarOverlayLayout) ActionBarUtils.getActionBarOverlayLayout(this);
        if (((ActionBarImpl) actionBarOverlayLayout.getActionBar()).isFragmentViewPagerMode()) {
            return (ViewPager) actionBarOverlayLayout.findViewById(R$id.view_pager);
        }
        return null;
    }

    @Override // miuix.appcompat.internal.app.widget.ActionModeView
    public void initForMode(ActionMode actionMode) {
    }

    @Override // miuix.appcompat.internal.app.widget.ActionModeView
    public void killMode() {
        finishAnimation();
        ViewGroup viewGroup = (ViewGroup) getParent();
        if (viewGroup != null) {
            viewGroup.removeView(this);
        }
        this.mActionBarContainer = null;
        this.mActionBarView = null;
        List<ActionModeAnimationListener> list = this.mAnimationListeners;
        if (list != null) {
            list.clear();
            this.mAnimationListeners = null;
        }
        this.mSplitActionBarContainer = null;
    }

    protected ObjectAnimator makeAnimation() {
        ObjectAnimator objectAnimator = this.mCurrentAnimation;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.mCurrentAnimation = null;
            removeIdleHandler();
        }
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "AnimationProgress", 0.0f, 1.0f);
        ofFloat.addListener(this);
        ofFloat.setDuration(DeviceHelper.isFeatureWholeAnim() ? 400L : 0L);
        ofFloat.setInterpolator(obtainInterpolator());
        return ofFloat;
    }

    public void notifyAnimationEnd(boolean z) {
        List<ActionModeAnimationListener> list = this.mAnimationListeners;
        if (list == null) {
            return;
        }
        Iterator<ActionModeAnimationListener> it = list.iterator();
        while (it.hasNext()) {
            it.next().onStop(z);
        }
    }

    public void notifyAnimationStart(boolean z) {
        List<ActionModeAnimationListener> list = this.mAnimationListeners;
        if (list == null) {
            return;
        }
        Iterator<ActionModeAnimationListener> it = list.iterator();
        while (it.hasNext()) {
            it.next().onStart(z);
        }
    }

    public void notifyAnimationUpdate(boolean z, float f) {
        List<ActionModeAnimationListener> list = this.mAnimationListeners;
        if (list == null) {
            return;
        }
        Iterator<ActionModeAnimationListener> it = list.iterator();
        while (it.hasNext()) {
            it.next().onUpdate(z, f);
        }
    }

    public TimeInterpolator obtainInterpolator() {
        EaseManager.InterpolateEaseStyle interpolateEaseStyle = new EaseManager.InterpolateEaseStyle(0, new float[0]);
        interpolateEaseStyle.setFactors(0.98f, 0.75f);
        return EaseManager.getInterpolator(interpolateEaseStyle);
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationCancel(Animator animator) {
        this.mAnimationCanceled = true;
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationEnd(Animator animator) {
        ActionBarContainer actionBarContainer;
        if (this.mAnimationCanceled) {
            return;
        }
        this.mCurrentAnimation = null;
        notifyAnimationEnd(this.mAnimateToVisible);
        if (this.mAnimateToVisible) {
            InputMethodHelper.getInstance(getContext()).showKeyBoard(this.mInputView);
        } else {
            InputMethodHelper.getInstance(getContext()).hideKeyBoard(this.mInputView);
        }
        if (Settings.Global.getFloat(getContext().getContentResolver(), "animator_duration_scale", 1.0f) != 0.0f) {
            setResultViewMargin(this.mAnimateToVisible);
        } else {
            post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.SearchActionModeView$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SearchActionModeView.this.lambda$onAnimationEnd$0();
                }
            });
        }
        if (this.mAnimateToVisible && (actionBarContainer = this.mActionBarContainer) != null && actionBarContainer.isBlurEnable()) {
            setContentViewTranslation(-this.mContentOriginPaddingTop);
        } else {
            setContentViewTranslation(0);
            setContentViewPadding(this.mAnimateToVisible ? this.mStatusBarPaddingTop : 0, 0);
        }
        if (this.mAnimateToVisible) {
            return;
        }
        setOverlayMode(false);
        WeakReference<View> weakReference = this.mAnchorView;
        View view = weakReference != null ? weakReference.get() : null;
        if (view != null) {
            view.setAlpha(1.0f);
        }
        setAlpha(0.0f);
        killMode();
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationRepeat(Animator animator) {
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationStart(Animator animator) {
        this.mAnimationCanceled = false;
        if (this.mAnimateToVisible) {
            setAlpha(1.0f);
            return;
        }
        View tabContainer = getActionBarContainer().getTabContainer();
        if (tabContainer != null) {
            tabContainer.setVisibility(0);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == R$id.search_mask) {
            this.mTextCancel.performClick();
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTextCancel = (TextView) findViewById(R$id.search_text_cancel);
        ViewGroup viewGroup = (ViewGroup) findViewById(R$id.search_container);
        this.mSearchContainer = viewGroup;
        Folme.useAt(viewGroup).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).handleTouchOf(this.mSearchContainer, new AnimConfig[0]);
        CompatViewMethod.setForceDarkAllowed(this.mSearchContainer, false);
        if (shouldHideCancelText()) {
            this.mTextCancel.setVisibility(8);
            int max = Math.max(getPaddingStart(), getPaddingEnd());
            setPaddingRelative(max, getPaddingTop(), max, getPaddingBottom());
        }
        this.mInputView = (EditText) findViewById(16908297);
        this.mOriginalPaddingTop = getPaddingTop();
        View contentView = getContentView();
        if (contentView != null) {
            this.mContentOriginPaddingTop = contentView.getPaddingTop();
            this.mContentOriginPaddingBottom = contentView.getPaddingBottom();
        }
    }

    public void onFloatingModeChanged(boolean z) {
        this.mParentLocationY = Integer.MAX_VALUE;
        this.mActionBarContainerLocationY = Integer.MAX_VALUE;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mRequestAnimation) {
            WeakReference<View> weakReference = this.mAnimateView;
            View view = weakReference != null ? weakReference.get() : null;
            if (this.mAnimateToVisible && shouldAnimateContent() && view != null) {
                view.setTranslationY(this.mAnimateViewTranslationYStart);
            }
            queueIdleHandler();
            this.mRequestAnimation = false;
        }
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    protected void pollViews() {
        getActionBarView();
        getActionBarContainer();
        getSplitActionBarContainer();
    }

    @Override // android.os.MessageQueue.IdleHandler
    public boolean queueIdle() {
        this.mCurrentAnimation.start();
        return false;
    }

    public void rePaddingAndRelayout(Rect rect) {
        int i = this.mStatusBarPaddingTop;
        int i2 = rect.top;
        if (i != i2) {
            setStatusBarPaddingTop(i2);
            setPadding(getPaddingLeft(), this.mOriginalPaddingTop + this.mStatusBarPaddingTop, getPaddingRight(), getPaddingBottom());
            getLayoutParams().height = this.mSearchViewHeight + this.mStatusBarPaddingTop;
            requestLayout();
        }
    }

    public void setAnchorView(View view) {
        if (view != null) {
            this.mAnchorView = new WeakReference<>(view);
        }
    }

    public void setAnimateView(View view) {
        if (view != null) {
            this.mAnimateView = new WeakReference<>(view);
        }
    }

    public void setAnimationProgress(float f) {
        this.mAnimationProgress = f;
        notifyAnimationUpdate(this.mAnimateToVisible, f);
    }

    protected void setContentViewPadding(int i, int i2) {
        View contentView = getContentView();
        if (contentView != null) {
            contentView.setPadding(contentView.getPaddingLeft(), i + this.mContentOriginPaddingTop, contentView.getPaddingRight(), i2 + this.mContentOriginPaddingBottom);
        }
    }

    protected void setContentViewTranslation(int i) {
        View contentView = getContentView();
        if (contentView != null) {
            contentView.setTranslationY(i);
        }
    }

    public void setOnBackClickListener(View.OnClickListener onClickListener) {
        this.mTextCancel.setOnClickListener(onClickListener);
    }

    protected void setOverlayMode(boolean z) {
        ((ActionBarOverlayLayout) ActionBarUtils.getActionBarOverlayLayout(this)).setOverlayMode(z);
    }

    public void setResultView(View view) {
        if (view != null) {
            this.mResultView = new WeakReference<>(view);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                this.mResultViewOriginMarginTop = marginLayoutParams.topMargin;
                this.mResultViewOriginMarginBottom = marginLayoutParams.bottomMargin;
                this.mResultViewSet = true;
            }
        }
    }

    protected void setResultViewMargin(boolean z) {
        int i;
        int i2;
        WeakReference<View> weakReference = this.mResultView;
        View view = weakReference != null ? weakReference.get() : null;
        if (view == null || !this.mResultViewSet) {
            return;
        }
        if (z) {
            i = (getMeasuredHeight() - this.mStatusBarPaddingTop) - this.mActionBarTopMargin;
            i2 = 0;
        } else {
            i = this.mResultViewOriginMarginTop;
            i2 = this.mResultViewOriginMarginBottom;
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        marginLayoutParams.topMargin = i;
        marginLayoutParams.bottomMargin = i2;
        view.setLayoutParams(marginLayoutParams);
    }

    public void setStatusBarPaddingTop(int i) {
        this.mStatusBarPaddingTop = i;
    }
}
