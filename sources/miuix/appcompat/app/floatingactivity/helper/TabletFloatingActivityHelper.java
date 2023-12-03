package miuix.appcompat.app.floatingactivity.helper;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import java.lang.ref.WeakReference;
import java.util.Collection;
import miuix.animation.Folme;
import miuix.animation.IStateStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ViewProperty;
import miuix.appcompat.R$color;
import miuix.appcompat.R$dimen;
import miuix.appcompat.R$id;
import miuix.appcompat.R$layout;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.floatingactivity.FloatingAnimHelper;
import miuix.appcompat.app.floatingactivity.FloatingSwitcherAnimHelper;
import miuix.appcompat.app.floatingactivity.OnFloatingActivityCallback;
import miuix.appcompat.app.floatingactivity.OnFloatingCallback;
import miuix.appcompat.app.floatingactivity.SplitUtils;
import miuix.appcompat.widget.dialoganim.DimAnimator;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.ViewUtils;
import miuix.internal.widget.RoundFrameLayout;
import miuix.view.CompatViewMethod;

/* loaded from: classes5.dex */
public abstract class TabletFloatingActivityHelper extends BaseFloatingActivityHelper {
    protected AppCompatActivity mActivity;
    private View mBg;
    private final Drawable mDefaultPanelBg;
    private ViewGroup.LayoutParams mFloatingLayoutParam;
    private float mFloatingRadius;
    private View mFloatingRoot;
    private View mHandle;
    private float mLastMoveY;
    private float mMoveMaxY;
    private float mOffsetY;
    private OnFloatingActivityCallback mOnFloatingActivityCallback;
    private OnFloatingCallback mOnFloatingCallback;
    private View mPanel;
    private View mPanelParent;
    private GestureDetector mRootViewGestureDetector;
    private RoundFrameLayout mRoundFrameLayout;
    private float mTouchDownY;
    private final int PANEL_SHOW_DELAY_TIME = 90;
    private boolean mEnableSwipToDismiss = true;
    private final Handler mFloatingActivitySlidDownHandler = new Handler(Looper.getMainLooper());
    private boolean mAnimationDoing = false;
    private boolean mIsFloatingWindow = true;
    private int mFloatingActivityFinishingFlag = 0;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class FinishFloatingActivityDelegate implements Runnable {
        private WeakReference<AppCompatActivity> mActivity;
        private WeakReference<TabletFloatingActivityHelper> mRefs;

        public FinishFloatingActivityDelegate(TabletFloatingActivityHelper tabletFloatingActivityHelper, AppCompatActivity appCompatActivity) {
            this.mRefs = new WeakReference<>(tabletFloatingActivityHelper);
            this.mActivity = new WeakReference<>(appCompatActivity);
        }

        private void activityExitActuator(AppCompatActivity appCompatActivity, TabletFloatingActivityHelper tabletFloatingActivityHelper, boolean z, int i, boolean z2) {
            if (tabletFloatingActivityHelper.isFirstPageExitAnimExecuteEnable()) {
                tabletFloatingActivityHelper.singleFloatingSlipExit(z, i);
            } else if (appCompatActivity != null) {
                appCompatActivity.realFinish();
                preformFloatingExitAnimWithClip(appCompatActivity, tabletFloatingActivityHelper, z2);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void delegatePadPhoneFinishFloatingActivity(boolean z) {
            TabletFloatingActivityHelper tabletFloatingActivityHelper = this.mRefs.get();
            if (tabletFloatingActivityHelper != null) {
                tabletFloatingActivityHelper.updateFloatingActivityFinishingFlag(3);
            }
            AppCompatActivity appCompatActivity = this.mActivity.get();
            if (tabletFloatingActivityHelper != null) {
                activityExitActuator(appCompatActivity, tabletFloatingActivityHelper, true, 3, z);
            }
        }

        private void preformFloatingExitAnimWithClip(AppCompatActivity appCompatActivity, TabletFloatingActivityHelper tabletFloatingActivityHelper, boolean z) {
            if (z) {
                FloatingAnimHelper.preformFloatingExitAnimWithClip(appCompatActivity, tabletFloatingActivityHelper.mIsFloatingWindow);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            delegatePadPhoneFinishFloatingActivity(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class FloatingAnimTransitionListener extends TransitionListener {
        private boolean mAllActivityFinished;
        private boolean mDismiss;
        private WeakReference<TabletFloatingActivityHelper> mRefs;
        private int mTranslationY;
        private int mType;

        private FloatingAnimTransitionListener(TabletFloatingActivityHelper tabletFloatingActivityHelper, boolean z, int i, int i2) {
            this.mAllActivityFinished = false;
            this.mRefs = new WeakReference<>(tabletFloatingActivityHelper);
            this.mType = i2;
            this.mDismiss = z;
            this.mTranslationY = i;
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onCancel(Object obj) {
            super.onCancel(obj);
            WeakReference<TabletFloatingActivityHelper> weakReference = this.mRefs;
            TabletFloatingActivityHelper tabletFloatingActivityHelper = weakReference == null ? null : weakReference.get();
            if (tabletFloatingActivityHelper != null) {
                tabletFloatingActivityHelper.onEnd(obj);
            }
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onComplete(Object obj) {
            super.onComplete(obj);
            WeakReference<TabletFloatingActivityHelper> weakReference = this.mRefs;
            TabletFloatingActivityHelper tabletFloatingActivityHelper = weakReference == null ? null : weakReference.get();
            if (tabletFloatingActivityHelper != null) {
                tabletFloatingActivityHelper.onEnd(obj);
            }
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
            UpdateInfo findBy = UpdateInfo.findBy(collection, ViewProperty.TRANSLATION_Y);
            if (!this.mDismiss || findBy == null) {
                return;
            }
            TabletFloatingActivityHelper tabletFloatingActivityHelper = this.mRefs.get();
            if (this.mAllActivityFinished || findBy.getFloatValue() <= this.mTranslationY * 0.6f || tabletFloatingActivityHelper == null) {
                return;
            }
            this.mAllActivityFinished = true;
            tabletFloatingActivityHelper.finishAllPage();
        }
    }

    public TabletFloatingActivityHelper(AppCompatActivity appCompatActivity) {
        this.mActivity = appCompatActivity;
        this.mDefaultPanelBg = AttributeResolver.resolveDrawable(appCompatActivity, 16842836);
    }

    private void backOneByOne(int i) {
        updateFloatingActivityFinishingFlag(i);
        if (!isFirstPageExitAnimExecuteEnable()) {
            this.mActivity.realFinish();
            FloatingAnimHelper.singleAppFloatingActivityExit(this.mActivity);
        } else if (!this.mAnimationDoing) {
            triggerBottomExit(i);
        }
        execExitAnim();
    }

    private boolean delegateFinishTransWithClipAnimInternal() {
        new FinishFloatingActivityDelegate(this, this.mActivity).delegatePadPhoneFinishFloatingActivity(true);
        return true;
    }

    private void dimBg(float f) {
        this.mBg.setAlpha((1.0f - Math.max(0.0f, Math.min(f, 1.0f))) * 0.3f);
    }

    private void executeFolme(boolean z, int i) {
        float f;
        Object obj;
        int i2;
        if (this.mAnimationDoing && z) {
            return;
        }
        this.mAnimationDoing = true;
        if (z) {
            i2 = (int) this.mMoveMaxY;
            f = 0.0f;
            obj = "dismiss";
        } else {
            f = 0.3f;
            obj = "init";
            i2 = 0;
        }
        AnimConfig animConfig = FloatingSwitcherAnimHelper.getAnimConfig(1, null);
        animConfig.addListeners(new FloatingAnimTransitionListener(z, i2, i));
        AnimState add = new AnimState(obj).add(ViewProperty.TRANSLATION_Y, i2);
        AnimState add2 = new AnimState(obj).add(ViewProperty.ALPHA, f);
        Folme.useAt(getAnimPanel()).state().to(add, animConfig);
        Folme.useAt(this.mBg).state().to(add2, new AnimConfig[0]);
    }

    private void firstFloatingTranslationTop() {
        this.mPanel.post(new Runnable() { // from class: miuix.appcompat.app.floatingactivity.helper.TabletFloatingActivityHelper$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                TabletFloatingActivityHelper.this.lambda$firstFloatingTranslationTop$2();
            }
        });
    }

    private void folmeShow() {
        View animPanel = getAnimPanel();
        int height = animPanel.getHeight() + ((this.mFloatingRoot.getHeight() - animPanel.getHeight()) / 2);
        IStateStyle state = Folme.useAt(animPanel).state();
        ViewProperty viewProperty = ViewProperty.TRANSLATION_Y;
        state.setTo(viewProperty, Integer.valueOf(height)).to(viewProperty, 0, FloatingSwitcherAnimHelper.getAnimConfig(1, null));
        DimAnimator.show(this.mBg);
    }

    private View getAnimPanel() {
        View view = this.mPanelParent;
        return view == null ? this.mPanel : view;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getSnapShotAndSetPanel() {
        OnFloatingCallback onFloatingCallback;
        if (FloatingAnimHelper.isSupportTransWithClipAnim() || (onFloatingCallback = this.mOnFloatingCallback) == null || !this.mEnableSwipToDismiss) {
            return;
        }
        onFloatingCallback.getSnapShotAndSetPanel(this.mActivity);
    }

    private void handleFingerMove(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            notifyDragStart();
            float rawY = motionEvent.getRawY();
            this.mTouchDownY = rawY;
            this.mLastMoveY = rawY;
            this.mOffsetY = 0.0f;
            makeDownMoveMaxY();
        } else if (action == 1) {
            boolean z = motionEvent.getRawY() - this.mTouchDownY > ((float) this.mPanel.getHeight()) * 0.5f;
            updateFloatingActivityFinishingFlag(1);
            if (!z) {
                executeFolme(false, 1);
                return;
            }
            getSnapShotAndSetPanel();
            OnFloatingCallback onFloatingCallback = this.mOnFloatingCallback;
            executeFolme(onFloatingCallback == null || !onFloatingCallback.onFinish(1), 1);
        } else if (action != 2) {
        } else {
            float rawY2 = motionEvent.getRawY();
            float f = this.mOffsetY + (rawY2 - this.mLastMoveY);
            this.mOffsetY = f;
            if (f >= 0.0f) {
                movePanel(f);
                dimBg(this.mOffsetY / this.mMoveMaxY);
            }
            this.mLastMoveY = rawY2;
        }
    }

    private boolean isEnableFirstFloatingTranslationY() {
        return this.mIsFloatingWindow && isFirstPageEnterAnimExecuteEnable();
    }

    private boolean isFirstPageEnterAnimExecuteEnable() {
        OnFloatingCallback onFloatingCallback = this.mOnFloatingCallback;
        if (onFloatingCallback == null) {
            return true;
        }
        return onFloatingCallback.isFirstPageEnterAnimExecuteEnable();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isFirstPageExitAnimExecuteEnable() {
        OnFloatingCallback onFloatingCallback;
        return this.mIsFloatingWindow && ((onFloatingCallback = this.mOnFloatingCallback) == null || onFloatingCallback.isFirstPageExitAnimExecuteEnable());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$firstFloatingTranslationTop$2() {
        if (isEnableFirstFloatingTranslationY()) {
            markActivityOpenEnterAnimExecuted();
            folmeShow();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$init$1(View view, MotionEvent motionEvent) {
        if (this.mEnableSwipToDismiss) {
            handleFingerMove(motionEvent);
            return true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$panelDelayShow$0(float f) {
        this.mRoundFrameLayout.setAlpha(f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void makeDownMoveMaxY() {
        View animPanel = getAnimPanel();
        this.mMoveMaxY = animPanel.getHeight() + ((this.mFloatingRoot.getHeight() - animPanel.getHeight()) / 2);
    }

    private void markActivityOpenEnterAnimExecuted() {
        OnFloatingCallback onFloatingCallback = this.mOnFloatingCallback;
        if (onFloatingCallback != null) {
            onFloatingCallback.markActivityOpenEnterAnimExecuted(this.mActivity);
        }
    }

    private void movePanel(float f) {
        getAnimPanel().setTranslationY(f);
    }

    private void notifyDragEnd() {
        OnFloatingCallback onFloatingCallback = this.mOnFloatingCallback;
        if (onFloatingCallback != null) {
            onFloatingCallback.onDragEnd();
        }
    }

    private void notifyDragStart() {
        OnFloatingCallback onFloatingCallback = this.mOnFloatingCallback;
        if (onFloatingCallback != null) {
            onFloatingCallback.onDragStart();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyPageHide() {
        OnFloatingCallback onFloatingCallback = this.mOnFloatingCallback;
        if (onFloatingCallback != null) {
            onFloatingCallback.onHideBehindPage();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onEnd(Object obj) {
        if (TextUtils.equals("dismiss", obj.toString())) {
            this.mActivity.realFinish();
        } else if (TextUtils.equals("init", obj.toString())) {
            notifyDragEnd();
        }
        this.mAnimationDoing = false;
    }

    private void panelDelayShow() {
        if (this.mIsFloatingWindow) {
            final float alpha = this.mRoundFrameLayout.getAlpha();
            this.mRoundFrameLayout.setAlpha(0.0f);
            this.mRoundFrameLayout.postDelayed(new Runnable() { // from class: miuix.appcompat.app.floatingactivity.helper.TabletFloatingActivityHelper$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    TabletFloatingActivityHelper.this.lambda$panelDelayShow$0(alpha);
                }
            }, 90L);
        }
    }

    private void setPanelParent(View view) {
        this.mPanelParent = view;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void singleFloatingSlipExit(boolean z, int i) {
        if (!z || this.mAnimationDoing) {
            return;
        }
        makeDownMoveMaxY();
        notifyPageHide();
        executeFolme(true, i);
    }

    private void triggerBottomExit(int i) {
        makeDownMoveMaxY();
        notifyPageHide();
        executeFolme(true, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void triggerFinishCallback(boolean z, int i) {
        updateFloatingActivityFinishingFlag(i);
        if (!z) {
            executeFolme(false, i);
            return;
        }
        OnFloatingActivityCallback onFloatingActivityCallback = this.mOnFloatingActivityCallback;
        if (onFloatingActivityCallback != null && onFloatingActivityCallback.onFinish(i)) {
            executeFolme(false, i);
            return;
        }
        OnFloatingCallback onFloatingCallback = this.mOnFloatingCallback;
        executeFolme(onFloatingCallback == null || !onFloatingCallback.onFinish(i), i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFloatingActivityFinishingFlag(int i) {
        this.mFloatingActivityFinishingFlag = i;
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public boolean delegateFinishFloatingActivityInternal() {
        if (FloatingAnimHelper.isSupportTransWithClipAnim()) {
            return delegateFinishTransWithClipAnimInternal();
        }
        if (this.mIsFloatingWindow) {
            getSnapShotAndSetPanel();
            this.mFloatingActivitySlidDownHandler.postDelayed(new FinishFloatingActivityDelegate(this, this.mActivity), 110L);
            return true;
        }
        this.mActivity.realFinish();
        execExitAnim();
        return true;
    }

    public void execExitAnim() {
    }

    @Override // miuix.appcompat.app.floatingactivity.IActivitySwitcherAnimation
    public void executeCloseEnterAnimation() {
        if (this.mIsFloatingWindow) {
            FloatingSwitcherAnimHelper.executeCloseEnterAnimation(this.mPanel);
        }
    }

    @Override // miuix.appcompat.app.floatingactivity.IActivitySwitcherAnimation
    public void executeOpenEnterAnimation() {
        if (this.mIsFloatingWindow) {
            FloatingSwitcherAnimHelper.executeOpenEnterAnimation(this.mPanel);
        }
    }

    @Override // miuix.appcompat.app.floatingactivity.IActivitySwitcherAnimation
    public void executeOpenExitAnimation() {
        if (this.mIsFloatingWindow) {
            FloatingSwitcherAnimHelper.executeOpenExitAnimation(this.mPanel);
        }
    }

    public void finishAllPage() {
        OnFloatingCallback onFloatingCallback = this.mOnFloatingCallback;
        if (onFloatingCallback != null) {
            onFloatingCallback.closeAllPage();
        }
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public View getFloatingBrightPanel() {
        return this.mPanel;
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public ViewGroup.LayoutParams getFloatingLayoutParam() {
        return this.mFloatingLayoutParam;
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void hideFloatingBrightPanel() {
        this.mPanel.setVisibility(8);
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void hideFloatingDimBackground() {
        this.mBg.setVisibility(8);
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void init(View view, boolean z) {
        this.mHandle = view.findViewById(R$id.sliding_drawer_handle);
        View findViewById = view.findViewById(R$id.action_bar_overlay_bg);
        this.mBg = findViewById;
        findViewById.setAlpha(0.3f);
        this.mPanel = view.findViewById(R$id.action_bar_overlay_layout);
        this.mFloatingRoot = view.findViewById(R$id.action_bar_overlay_floating_root);
        this.mIsFloatingWindow = z;
        this.mEnableSwipToDismiss = false;
        this.mRootViewGestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() { // from class: miuix.appcompat.app.floatingactivity.helper.TabletFloatingActivityHelper.1
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                if (TabletFloatingActivityHelper.this.mEnableSwipToDismiss) {
                    TabletFloatingActivityHelper.this.getSnapShotAndSetPanel();
                    TabletFloatingActivityHelper.this.makeDownMoveMaxY();
                    TabletFloatingActivityHelper.this.notifyPageHide();
                    TabletFloatingActivityHelper.this.triggerFinishCallback(true, 2);
                }
                return true;
            }
        });
        this.mFloatingRoot.setOnTouchListener(new View.OnTouchListener() { // from class: miuix.appcompat.app.floatingactivity.helper.TabletFloatingActivityHelper.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view2, MotionEvent motionEvent) {
                TabletFloatingActivityHelper.this.mRootViewGestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
        this.mHandle.setOnTouchListener(new View.OnTouchListener() { // from class: miuix.appcompat.app.floatingactivity.helper.TabletFloatingActivityHelper$$ExternalSyntheticLambda0
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view2, MotionEvent motionEvent) {
                boolean lambda$init$1;
                lambda$init$1 = TabletFloatingActivityHelper.this.lambda$init$1(view2, motionEvent);
                return lambda$init$1;
            }
        });
        firstFloatingTranslationTop();
        this.mActivity.getWindow().setBackgroundDrawableResource(R$color.miuix_appcompat_transparent);
        if (this.mIsFloatingWindow || !ViewUtils.isNightMode(this.mActivity)) {
            this.mPanel.setBackground(this.mDefaultPanelBg);
        } else {
            this.mPanel.setBackground(new ColorDrawable(-16777216));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isFloatingWindow() {
        return this.mIsFloatingWindow;
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public boolean onBackPressed() {
        if (this.mIsFloatingWindow && !FloatingAnimHelper.isSupportTransWithClipAnim()) {
            getSnapShotAndSetPanel();
        }
        backOneByOne(4);
        return true;
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public ViewGroup replaceSubDecor(View view, boolean z) {
        ViewGroup viewGroup = (ViewGroup) View.inflate(this.mActivity, R$layout.miuix_appcompat_screen_floating_window, null);
        View findViewById = viewGroup.findViewById(R$id.action_bar_overlay_layout);
        View findViewById2 = viewGroup.findViewById(R$id.sliding_drawer_handle);
        if (findViewById2 != null && (findViewById2.getParent() instanceof ViewGroup)) {
            ((ViewGroup) findViewById2.getParent()).removeView(findViewById2);
        }
        if (view instanceof ViewGroup) {
            ((ViewGroup) view).addView(findViewById2);
        }
        ViewGroup.LayoutParams layoutParams = findViewById.getLayoutParams();
        this.mFloatingLayoutParam = layoutParams;
        if (z) {
            layoutParams.height = -2;
            layoutParams.width = -2;
        } else {
            layoutParams.width = -1;
            layoutParams.height = -1;
        }
        view.setLayoutParams(layoutParams);
        viewGroup.removeView(findViewById);
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
        this.mFloatingRadius = this.mActivity.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_floating_window_background_radius);
        RoundFrameLayout roundFrameLayout = new RoundFrameLayout(this.mActivity);
        this.mRoundFrameLayout = roundFrameLayout;
        roundFrameLayout.setLayoutParams(this.mFloatingLayoutParam);
        this.mRoundFrameLayout.addView(view);
        this.mRoundFrameLayout.setRadius(z ? this.mFloatingRadius : 0.0f);
        if (this.mIsFloatingWindow && ViewUtils.isNightMode(this.mActivity)) {
            this.mRoundFrameLayout.setBorder(this.mActivity.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_floating_window_background_border_width), this.mActivity.getResources().getColor(R$color.miuix_appcompat_floating_window_bg_color_dark));
        } else {
            this.mRoundFrameLayout.setBorder(0.0f, 0);
        }
        panelDelayShow();
        viewGroup.addView(this.mRoundFrameLayout);
        setPanelParent(this.mRoundFrameLayout);
        return viewGroup;
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void setEnableSwipToDismiss(boolean z) {
        this.mEnableSwipToDismiss = z;
        this.mHandle.setVisibility(z ? 0 : 8);
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void setFloatingWindowMode(boolean z) {
        this.mIsFloatingWindow = z;
        if (!SplitUtils.isIntentFromSettingsSplit(this.mActivity.getIntent())) {
            CompatViewMethod.setActivityTranslucent(this.mActivity, true);
        }
        if (this.mRoundFrameLayout != null) {
            float dimensionPixelSize = this.mActivity.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_floating_window_background_radius);
            this.mFloatingRadius = dimensionPixelSize;
            RoundFrameLayout roundFrameLayout = this.mRoundFrameLayout;
            if (!z) {
                dimensionPixelSize = 0.0f;
            }
            roundFrameLayout.setRadius(dimensionPixelSize);
            if (z && ViewUtils.isNightMode(this.mActivity)) {
                this.mRoundFrameLayout.setBorder(this.mActivity.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_floating_window_background_border_width), this.mActivity.getResources().getColor(R$color.miuix_appcompat_floating_window_bg_color_dark));
            } else {
                this.mRoundFrameLayout.setBorder(0.0f, 0);
            }
        }
        if (this.mPanel != null) {
            if (z || !ViewUtils.isNightMode(this.mActivity)) {
                this.mPanel.setBackground(this.mDefaultPanelBg);
            } else {
                this.mPanel.setBackground(new ColorDrawable(-16777216));
            }
        }
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void setOnFloatingCallback(OnFloatingCallback onFloatingCallback) {
        this.mOnFloatingCallback = onFloatingCallback;
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public void showFloatingBrightPanel() {
        this.mPanel.setVisibility(0);
    }
}
