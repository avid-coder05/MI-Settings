package miuix.animation.controller;

import android.graphics.Color;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import miuix.animation.Folme;
import miuix.animation.IAnimTarget;
import miuix.animation.IHoverStyle;
import miuix.animation.R$id;
import miuix.animation.ViewTarget;
import miuix.animation.base.AnimConfig;
import miuix.animation.internal.AnimValueUtils;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ViewProperty;
import miuix.animation.property.ViewPropertyExt;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.EaseManager;
import miuix.animation.utils.LogUtils;
import miuix.folme.R$color;

/* loaded from: classes5.dex */
public class FolmeHover extends FolmeBase implements IHoverStyle {
    private static WeakHashMap<View, InnerViewHoverListener> sHoverRecord = new WeakHashMap<>();
    private String HoverMoveType;
    private boolean isSetAutoTranslation;
    private WeakReference<View> mChildView;
    private boolean mClearTint;
    private IHoverStyle.HoverEffect mCurrentEffect;
    private TransitionListener mDefListener;
    private AnimConfig mEnterConfig;
    private AnimConfig mExitConfig;
    private WeakReference<View> mHoverView;
    private boolean mIsEnter;
    private int[] mLocation;
    private AnimConfig mMoveConfig;
    private WeakReference<View> mParentView;
    private float mRadius;
    private Map<IHoverStyle.HoverType, Boolean> mScaleSetMap;
    private boolean mSetTint;
    private int mTargetHeight;
    private int mTargetWidth;
    private float mTranslateDist;
    private Map<IHoverStyle.HoverType, Boolean> mTranslationSetMap;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: miuix.animation.controller.FolmeHover$2  reason: invalid class name */
    /* loaded from: classes5.dex */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$miuix$animation$IHoverStyle$HoverEffect;

        static {
            int[] iArr = new int[IHoverStyle.HoverEffect.values().length];
            $SwitchMap$miuix$animation$IHoverStyle$HoverEffect = iArr;
            try {
                iArr[IHoverStyle.HoverEffect.NORMAL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$miuix$animation$IHoverStyle$HoverEffect[IHoverStyle.HoverEffect.FLOATED.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$miuix$animation$IHoverStyle$HoverEffect[IHoverStyle.HoverEffect.FLOATED_WRAPPED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class InnerViewHoverListener implements View.OnHoverListener {
        private WeakHashMap<FolmeHover, AnimConfig[]> mHoverMap;

        private InnerViewHoverListener() {
            this.mHoverMap = new WeakHashMap<>();
        }

        void addHover(FolmeHover folmeHover, AnimConfig... animConfigArr) {
            this.mHoverMap.put(folmeHover, animConfigArr);
        }

        @Override // android.view.View.OnHoverListener
        public boolean onHover(View view, MotionEvent motionEvent) {
            for (Map.Entry<FolmeHover, AnimConfig[]> entry : this.mHoverMap.entrySet()) {
                entry.getKey().handleMotionEvent(view, motionEvent, entry.getValue());
            }
            return false;
        }
    }

    public FolmeHover(IAnimTarget... iAnimTargetArr) {
        super(iAnimTargetArr);
        this.mTranslateDist = Float.MAX_VALUE;
        this.mMoveConfig = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.9f, 0.4f));
        this.mEnterConfig = new AnimConfig();
        this.mExitConfig = new AnimConfig();
        this.mScaleSetMap = new ArrayMap();
        this.mTranslationSetMap = new ArrayMap();
        this.mCurrentEffect = IHoverStyle.HoverEffect.NORMAL;
        this.isSetAutoTranslation = false;
        this.mClearTint = false;
        this.mLocation = new int[2];
        this.mRadius = 0.0f;
        this.mTargetWidth = 0;
        this.mTargetHeight = 0;
        this.HoverMoveType = "MOVE";
        this.mDefListener = new TransitionListener() { // from class: miuix.animation.controller.FolmeHover.1
            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj, Collection<UpdateInfo> collection) {
                if (obj.equals(IHoverStyle.HoverType.ENTER)) {
                    AnimState.alignState(FolmeHover.this.mState.getState(IHoverStyle.HoverType.EXIT), collection);
                }
            }
        };
        initDist(iAnimTargetArr.length > 0 ? iAnimTargetArr[0] : null);
        updateHoverState(this.mCurrentEffect);
        this.mEnterConfig.setEase(EaseManager.getStyle(-2, 0.99f, 0.6f));
        this.mEnterConfig.addListeners(this.mDefListener);
        this.mExitConfig.setEase(-2, 0.99f, 0.4f).setSpecial(ViewProperty.ALPHA, -2L, 0.9f, 0.2f);
    }

    private void actualTranslatDist(View view, MotionEvent motionEvent) {
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        view.getLocationOnScreen(this.mLocation);
        float max = Math.max(-1.0f, Math.min(1.0f, (rawX - (this.mLocation[0] + (view.getWidth() * 0.5f))) / view.getWidth()));
        float max2 = Math.max(-1.0f, Math.min(1.0f, (rawY - (this.mLocation[1] + (view.getHeight() * 0.5f))) / view.getHeight()));
        float f = this.mTranslateDist;
        this.mState.to(this.mState.getState(this.HoverMoveType).add(ViewProperty.TRANSLATION_X, max * (f == Float.MAX_VALUE ? 1.0f : f)).add(ViewProperty.TRANSLATION_Y, max2 * (f != Float.MAX_VALUE ? f : 1.0f)), this.mMoveConfig);
    }

    private void clearRound() {
    }

    private void clearScale() {
        IHoverStyle.HoverType hoverType = IHoverStyle.HoverType.ENTER;
        if (isScaleSet(hoverType)) {
            this.mState.getState(hoverType).remove(ViewProperty.SCALE_X);
            this.mState.getState(hoverType).remove(ViewProperty.SCALE_Y);
        }
        IHoverStyle.HoverType hoverType2 = IHoverStyle.HoverType.EXIT;
        if (isScaleSet(hoverType2)) {
            this.mState.getState(hoverType2).remove(ViewProperty.SCALE_X);
            this.mState.getState(hoverType2).remove(ViewProperty.SCALE_Y);
        }
        this.mScaleSetMap.clear();
    }

    private void clearTranslation() {
        this.isSetAutoTranslation = false;
        IHoverStyle.HoverType hoverType = IHoverStyle.HoverType.ENTER;
        if (isTranslationSet(hoverType)) {
            this.mState.getState(hoverType).remove(ViewProperty.TRANSLATION_X);
            this.mState.getState(hoverType).remove(ViewProperty.TRANSLATION_Y);
        }
        IHoverStyle.HoverType hoverType2 = IHoverStyle.HoverType.EXIT;
        if (isTranslationSet(hoverType2)) {
            this.mState.getState(hoverType2).remove(ViewProperty.TRANSLATION_X);
            this.mState.getState(hoverType2).remove(ViewProperty.TRANSLATION_Y);
        }
        this.mTranslationSetMap.clear();
    }

    private void doHandleHoverOf(View view, AnimConfig... animConfigArr) {
        handleViewHover(view, animConfigArr);
        if (setHoverView(view) && LogUtils.isLogEnabled()) {
            LogUtils.debug("handleViewHover for " + view, new Object[0]);
        }
    }

    private AnimConfig[] getEnterConfig(AnimConfig... animConfigArr) {
        return (AnimConfig[]) CommonUtils.mergeArray(animConfigArr, this.mEnterConfig);
    }

    private AnimConfig[] getExitConfig(AnimConfig... animConfigArr) {
        return (AnimConfig[]) CommonUtils.mergeArray(animConfigArr, this.mExitConfig);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMotionEvent(View view, MotionEvent motionEvent, AnimConfig... animConfigArr) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 7) {
            onEventMove(view, motionEvent, animConfigArr);
        } else if (actionMasked == 9) {
            onEventEnter(motionEvent, animConfigArr);
        } else if (actionMasked != 10) {
        } else {
            onEventExit(motionEvent, animConfigArr);
        }
    }

    private void handleViewHover(View view, AnimConfig... animConfigArr) {
        InnerViewHoverListener innerViewHoverListener = sHoverRecord.get(view);
        if (innerViewHoverListener == null) {
            innerViewHoverListener = new InnerViewHoverListener();
            sHoverRecord.put(view, innerViewHoverListener);
        }
        view.setOnHoverListener(innerViewHoverListener);
        innerViewHoverListener.addHover(this, animConfigArr);
    }

    private void hoverEnterAuto(boolean z, AnimConfig... animConfigArr) {
        this.isSetAutoTranslation = z;
        this.mIsEnter = true;
        if (this.mCurrentEffect == IHoverStyle.HoverEffect.FLOATED_WRAPPED) {
            WeakReference<View> weakReference = this.mHoverView;
            View view = weakReference != null ? weakReference.get() : null;
            if (view != null) {
                setMagicView(view, true);
                setWrapped(view, true);
            }
        }
        if (isHideHover()) {
            setMagicView(true);
            setPointerHide(true);
        }
        setCorner(this.mRadius);
        setTintColor();
        AnimConfig[] enterConfig = getEnterConfig(animConfigArr);
        IFolmeStateStyle iFolmeStateStyle = this.mState;
        IHoverStyle.HoverType hoverType = IHoverStyle.HoverType.ENTER;
        AnimState state = iFolmeStateStyle.getState(hoverType);
        if (isScaleSet(hoverType)) {
            IAnimTarget target = this.mState.getTarget();
            float max = Math.max(target.getValue(ViewProperty.WIDTH), target.getValue(ViewProperty.HEIGHT));
            double min = Math.min((12.0f + max) / max, 1.15f);
            state.add(ViewProperty.SCALE_X, min).add(ViewProperty.SCALE_Y, min);
        }
        WeakReference<View> weakReference2 = this.mParentView;
        if (weakReference2 != null) {
            Folme.useAt(weakReference2.get()).state().add(ViewProperty.SCALE_X, 1.0f).add(ViewProperty.SCALE_Y, 1.0f).to(enterConfig);
        }
        this.mState.to(state, enterConfig);
    }

    private void hoverEnterToolType(int i, AnimConfig... animConfigArr) {
        if (i == 1 || i == 3 || i == 0) {
            hoverEnter(animConfigArr);
        } else if (i == 4 || i == 2) {
            hoverEnterAuto(false, animConfigArr);
        }
    }

    private void initDist(IAnimTarget iAnimTarget) {
        View targetObject = iAnimTarget instanceof ViewTarget ? ((ViewTarget) iAnimTarget).getTargetObject() : null;
        if (targetObject != null) {
            float max = Math.max(iAnimTarget.getValue(ViewProperty.WIDTH), iAnimTarget.getValue(ViewProperty.HEIGHT));
            float min = Math.min((12.0f + max) / max, 1.15f);
            this.mTargetWidth = targetObject.getWidth();
            int height = targetObject.getHeight();
            this.mTargetHeight = height;
            this.mTranslateDist = min != 1.0f ? Math.min(Math.min(15.0f, valFromPer(Math.max(0.0f, Math.min(1.0f, perFromVal((float) (this.mTargetWidth - 40), 0.0f, 360.0f))), 15.0f, 0.0f)), Math.min(15.0f, valFromPer(Math.max(0.0f, Math.min(1.0f, perFromVal((float) (height - 40), 0.0f, 360.0f))), 15.0f, 0.0f))) : 0.0f;
            int i = this.mTargetWidth;
            int i2 = this.mTargetHeight;
            if (i != i2 || i >= 100 || i2 >= 100) {
                setCorner(36.0f);
            } else {
                setCorner((int) (i * 0.5f));
            }
        }
    }

    static boolean isOnHoverView(View view, int[] iArr, MotionEvent motionEvent) {
        if (view != null) {
            view.getLocationOnScreen(iArr);
            int rawX = (int) motionEvent.getRawX();
            int rawY = (int) motionEvent.getRawY();
            return rawX >= iArr[0] && rawX <= iArr[0] + view.getWidth() && rawY >= iArr[1] && rawY <= iArr[1] + view.getHeight();
        }
        return true;
    }

    private boolean isScaleSet(IHoverStyle.HoverType hoverType) {
        return Boolean.TRUE.equals(this.mScaleSetMap.get(hoverType));
    }

    private boolean isTranslationSet(IHoverStyle.HoverType hoverType) {
        return Boolean.TRUE.equals(this.mTranslationSetMap.get(hoverType));
    }

    private void onEventEnter(MotionEvent motionEvent, AnimConfig... animConfigArr) {
        if (LogUtils.isLogEnabled()) {
            LogUtils.debug("onEventEnter, touchEnter", new Object[0]);
        }
        hoverEnter(motionEvent, animConfigArr);
    }

    private void onEventExit(MotionEvent motionEvent, AnimConfig... animConfigArr) {
        if (this.mIsEnter) {
            if (LogUtils.isLogEnabled()) {
                LogUtils.debug("onEventExit, touchExit", new Object[0]);
            }
            hoverExit(motionEvent, animConfigArr);
            resetTouchStatus();
        }
    }

    private void onEventMove(View view, MotionEvent motionEvent, AnimConfig... animConfigArr) {
        if (this.mIsEnter && view != null && isTranslationSet(IHoverStyle.HoverType.ENTER) && this.isSetAutoTranslation) {
            actualTranslatDist(view, motionEvent);
        }
    }

    private float perFromVal(float f, float f2, float f3) {
        return (f - f2) / (f3 - f2);
    }

    private void resetTouchStatus() {
        this.mIsEnter = false;
    }

    private View resetView(WeakReference<View> weakReference) {
        View view = weakReference.get();
        if (view != null) {
            view.setOnHoverListener(null);
        }
        return view;
    }

    private void setAutoRound() {
    }

    private void setAutoScale() {
        Map<IHoverStyle.HoverType, Boolean> map = this.mScaleSetMap;
        IHoverStyle.HoverType hoverType = IHoverStyle.HoverType.ENTER;
        Boolean bool = Boolean.TRUE;
        map.put(hoverType, bool);
        Map<IHoverStyle.HoverType, Boolean> map2 = this.mScaleSetMap;
        IHoverStyle.HoverType hoverType2 = IHoverStyle.HoverType.EXIT;
        map2.put(hoverType2, bool);
        this.mState.getState(hoverType2).add(ViewProperty.SCALE_X, 1.0d).add(ViewProperty.SCALE_Y, 1.0d);
    }

    private void setAutoTranslation() {
        this.isSetAutoTranslation = true;
        Map<IHoverStyle.HoverType, Boolean> map = this.mTranslationSetMap;
        IHoverStyle.HoverType hoverType = IHoverStyle.HoverType.ENTER;
        Boolean bool = Boolean.TRUE;
        map.put(hoverType, bool);
        Map<IHoverStyle.HoverType, Boolean> map2 = this.mTranslationSetMap;
        IHoverStyle.HoverType hoverType2 = IHoverStyle.HoverType.EXIT;
        map2.put(hoverType2, bool);
        this.mState.getState(hoverType2).add(ViewProperty.TRANSLATION_X, 0.0d).add(ViewProperty.TRANSLATION_Y, 0.0d);
    }

    private static void setFeedbackRadius(View view, float f) {
        try {
            Class.forName("android.view.View").getMethod("setFeedbackRadius", Float.TYPE).invoke(view, Float.valueOf(f));
        } catch (Exception e) {
            Log.e("", "setFeedbackRadius failed , e:" + e.toString());
        }
    }

    private boolean setHoverView(View view) {
        WeakReference<View> weakReference = this.mHoverView;
        if ((weakReference != null ? weakReference.get() : null) == view) {
            return false;
        }
        this.mHoverView = new WeakReference<>(view);
        return true;
    }

    private static void setMagicView(View view, boolean z) {
        try {
            Class.forName("android.view.View").getMethod("setMagicView", Boolean.TYPE).invoke(view, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e("", "setMagicView failed , e:" + e.toString());
        }
    }

    private static void setPointerHide(View view, boolean z) {
        try {
            Class.forName("android.view.View").getMethod("setPointerHide", Boolean.TYPE).invoke(view, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e("", "setPointerHide failed , e:" + e.toString());
        }
    }

    private void setTintColor() {
        if (this.mSetTint || this.mClearTint) {
            return;
        }
        int argb = Color.argb(20, 0, 0, 0);
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            argb = ((View) targetObject).getResources().getColor(R$color.miuix_folme_color_touch_tint);
        }
        ViewPropertyExt.ForegroundProperty foregroundProperty = ViewPropertyExt.FOREGROUND;
        this.mState.getState(IHoverStyle.HoverType.ENTER).add(foregroundProperty, argb);
        this.mState.getState(IHoverStyle.HoverType.EXIT).add(foregroundProperty, 0.0d);
    }

    private static void setWrapped(View view, boolean z) {
        try {
            Class.forName("android.view.View").getMethod("setWrapped", Boolean.TYPE).invoke(view, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e("", "setWrapped failed , e:" + e.toString());
        }
    }

    private void updateHoverState(IHoverStyle.HoverEffect hoverEffect) {
        int i = AnonymousClass2.$SwitchMap$miuix$animation$IHoverStyle$HoverEffect[hoverEffect.ordinal()];
        if (i == 1) {
            IHoverStyle.HoverEffect hoverEffect2 = this.mCurrentEffect;
            if (hoverEffect2 == IHoverStyle.HoverEffect.FLOATED) {
                clearScale();
                clearTranslation();
            } else if (hoverEffect2 == IHoverStyle.HoverEffect.FLOATED_WRAPPED) {
                clearScale();
                clearTranslation();
                clearRound();
            }
            setTintColor();
            this.mCurrentEffect = hoverEffect;
        } else if (i == 2) {
            if (this.mCurrentEffect == IHoverStyle.HoverEffect.FLOATED_WRAPPED) {
                clearRound();
            }
            setTintColor();
            setAutoScale();
            setAutoTranslation();
            this.mCurrentEffect = hoverEffect;
        } else if (i != 3) {
        } else {
            IHoverStyle.HoverEffect hoverEffect3 = this.mCurrentEffect;
            if (hoverEffect3 == IHoverStyle.HoverEffect.NORMAL || hoverEffect3 == IHoverStyle.HoverEffect.FLOATED) {
                clearTintColor();
            }
            setAutoScale();
            setAutoTranslation();
            setAutoRound();
            this.mCurrentEffect = hoverEffect;
        }
    }

    private float valFromPer(float f, float f2, float f3) {
        return f2 + ((f3 - f2) * f);
    }

    @Override // miuix.animation.controller.FolmeBase, miuix.animation.IStateContainer
    public void clean() {
        super.clean();
        this.mScaleSetMap.clear();
        WeakReference<View> weakReference = this.mHoverView;
        if (weakReference != null) {
            resetView(weakReference);
            this.mHoverView = null;
        }
        WeakReference<View> weakReference2 = this.mChildView;
        if (weakReference2 != null) {
            resetView(weakReference2);
            this.mChildView = null;
        }
        WeakReference<View> weakReference3 = this.mParentView;
        if (weakReference3 != null) {
            resetView(weakReference3);
            this.mParentView = null;
        }
    }

    public IHoverStyle clearTintColor() {
        this.mClearTint = true;
        ViewPropertyExt.ForegroundProperty foregroundProperty = ViewPropertyExt.FOREGROUND;
        this.mState.getState(IHoverStyle.HoverType.ENTER).remove(foregroundProperty);
        this.mState.getState(IHoverStyle.HoverType.EXIT).remove(foregroundProperty);
        return this;
    }

    @Override // miuix.animation.IHoverStyle
    public void handleHoverOf(View view, AnimConfig... animConfigArr) {
        doHandleHoverOf(view, animConfigArr);
    }

    public void hoverEnter(MotionEvent motionEvent, AnimConfig... animConfigArr) {
        hoverEnterToolType(motionEvent.getToolType(0), animConfigArr);
    }

    @Override // miuix.animation.IHoverStyle
    public void hoverEnter(AnimConfig... animConfigArr) {
        hoverEnterAuto(true, animConfigArr);
    }

    public void hoverExit(MotionEvent motionEvent, AnimConfig... animConfigArr) {
        if (this.mParentView != null && !isOnHoverView(this.mHoverView.get(), this.mLocation, motionEvent)) {
            Folme.useAt(this.mParentView.get()).hover().hoverEnter(this.mEnterConfig);
        }
        IHoverStyle.HoverType hoverType = IHoverStyle.HoverType.EXIT;
        if (isTranslationSet(hoverType) && this.isSetAutoTranslation) {
            this.mState.getState(hoverType).add(ViewProperty.TRANSLATION_X, 0.0d).add(ViewProperty.TRANSLATION_Y, 0.0d);
        }
        hoverExit(animConfigArr);
    }

    @Override // miuix.animation.IHoverStyle
    public void hoverExit(AnimConfig... animConfigArr) {
        AnimConfig[] exitConfig = getExitConfig(animConfigArr);
        IFolmeStateStyle iFolmeStateStyle = this.mState;
        iFolmeStateStyle.to(iFolmeStateStyle.getState(IHoverStyle.HoverType.EXIT), exitConfig);
    }

    public boolean isHideHover() {
        boolean z;
        IHoverStyle.HoverEffect hoverEffect;
        return this.mTargetWidth < 100 && this.mTargetHeight < 100 && (!(z = this.isSetAutoTranslation) || (z && ((hoverEffect = this.mCurrentEffect) == IHoverStyle.HoverEffect.FLOATED || hoverEffect == IHoverStyle.HoverEffect.FLOATED_WRAPPED)));
    }

    @Override // miuix.animation.IHoverStyle
    public IHoverStyle setBackgroundColor(float f, float f2, float f3, float f4) {
        return setBackgroundColor(Color.argb((int) (f * 255.0f), (int) (f2 * 255.0f), (int) (f3 * 255.0f), (int) (f4 * 255.0f)));
    }

    public IHoverStyle setBackgroundColor(int i) {
        ViewPropertyExt.BackgroundProperty backgroundProperty = ViewPropertyExt.BACKGROUND;
        this.mState.getState(IHoverStyle.HoverType.ENTER).add(backgroundProperty, i);
        this.mState.getState(IHoverStyle.HoverType.EXIT).add(backgroundProperty, (int) AnimValueUtils.getValueOfTarget(this.mState.getTarget(), backgroundProperty, 0.0d));
        return this;
    }

    public IHoverStyle setCorner(float f) {
        this.mRadius = f;
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            ((View) targetObject).setTag(R$id.miuix_animation_tag_view_hover_corners, Float.valueOf(f));
        }
        return this;
    }

    @Override // miuix.animation.IHoverStyle
    public IHoverStyle setEffect(IHoverStyle.HoverEffect hoverEffect) {
        updateHoverState(hoverEffect);
        return this;
    }

    @Override // miuix.animation.IHoverStyle
    public void setFeedbackRadius(float f) {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            setFeedbackRadius((View) targetObject, f);
        }
    }

    public void setMagicView(boolean z) {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            setMagicView((View) targetObject, z);
        }
    }

    public void setPointerHide(boolean z) {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            setPointerHide((View) targetObject, z);
        }
    }

    @Override // miuix.animation.IHoverStyle
    public IHoverStyle setTint(float f, float f2, float f3, float f4) {
        return setTint(Color.argb((int) (f * 255.0f), (int) (f2 * 255.0f), (int) (f3 * 255.0f), (int) (f4 * 255.0f)));
    }

    public IHoverStyle setTint(int i) {
        this.mSetTint = true;
        this.mClearTint = i == 0;
        this.mState.getState(IHoverStyle.HoverType.ENTER).add(ViewPropertyExt.FOREGROUND, i);
        return this;
    }
}
