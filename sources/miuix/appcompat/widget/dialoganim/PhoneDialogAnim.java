package miuix.appcompat.widget.dialoganim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.DecelerateInterpolator;
import java.lang.ref.WeakReference;
import miuix.animation.property.ViewProperty;
import miuix.animation.utils.EaseManager;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.widget.DialogAnimHelper;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.WindowUtils;

/* loaded from: classes5.dex */
public class PhoneDialogAnim implements IDialogAnim {
    private static WeakReference<ValueAnimator> sValueAnimatorWeakRef;
    private int mImeHeight = 0;

    /* loaded from: classes5.dex */
    class AnimLayoutChangeListener implements View.OnLayoutChangeListener {
        final WeakReference<View> decorView;
        final Rect windowVisibleFrame = new Rect();
        final Point screenSize = new Point();

        public AnimLayoutChangeListener(View view) {
            this.decorView = new WeakReference<>(view.getRootView());
        }

        public boolean isInMultiScreenBottom(Context context) {
            WindowUtils.getDisplay(context).getRealSize(this.screenSize);
            Rect rect = this.windowVisibleFrame;
            if (rect.left == 0) {
                int i = rect.right;
                Point point = this.screenSize;
                if (i == point.x) {
                    return rect.top >= ((int) (((float) point.y) * 0.2f));
                }
                return false;
            }
            return false;
        }

        public boolean isInMultiScreenMode(Context context) {
            return MiuixUIUtils.isInMultiWindowMode(context) && !MiuixUIUtils.isFreeformMode(context);
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            View view2 = this.decorView.get();
            if (view2 != null) {
                view2.getWindowVisibleDisplayFrame(this.windowVisibleFrame);
            }
        }
    }

    /* loaded from: classes5.dex */
    class WeakRefDismissListener implements Animator.AnimatorListener {
        WeakReference<DialogAnimHelper.OnDismiss> mOnDismiss;
        WeakReference<View> mView;

        WeakRefDismissListener(View view, DialogAnimHelper.OnDismiss onDismiss) {
            this.mOnDismiss = new WeakReference<>(onDismiss);
            this.mView = new WeakReference<>(view);
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
            DialogAnimHelper.OnDismiss onDismiss = this.mOnDismiss.get();
            if (onDismiss != null) {
                onDismiss.end();
            } else {
                Log.d("PhoneDialogAnim", "onCancel mOnDismiss get null");
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            DialogAnimHelper.OnDismiss onDismiss = this.mOnDismiss.get();
            if (onDismiss != null) {
                onDismiss.end();
            } else {
                Log.d("PhoneDialogAnim", "onComplete mOnDismiss get null");
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            View view = this.mView.get();
            if (view != null) {
                view.setTag("hide");
            }
        }
    }

    /* loaded from: classes5.dex */
    class WeakRefShowListener extends AnimatorListenerAdapter {
        int mEndTranslateY;
        View.OnLayoutChangeListener mOnLayoutChange;
        WeakReference<AlertDialog.OnDialogShowAnimListener> mOnShow;
        WeakReference<View> mView;

        WeakRefShowListener(AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener, View.OnLayoutChangeListener onLayoutChangeListener, View view, int i) {
            this.mOnShow = new WeakReference<>(onDialogShowAnimListener);
            this.mOnLayoutChange = onLayoutChangeListener;
            this.mView = new WeakReference<>(view);
            this.mEndTranslateY = i;
        }

        private void done() {
            View.OnLayoutChangeListener onLayoutChangeListener;
            View view = this.mView.get();
            if (view != null && (onLayoutChangeListener = this.mOnLayoutChange) != null) {
                view.removeOnLayoutChangeListener(onLayoutChangeListener);
                this.mOnLayoutChange = null;
            }
            AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener = this.mOnShow.get();
            if (onDialogShowAnimListener != null) {
                onDialogShowAnimListener.onShowAnimComplete();
            }
            if (PhoneDialogAnim.sValueAnimatorWeakRef != null) {
                PhoneDialogAnim.sValueAnimatorWeakRef.clear();
                WeakReference unused = PhoneDialogAnim.sValueAnimatorWeakRef = null;
            }
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
            super.onAnimationCancel(animator);
            done();
            View view = this.mView.get();
            if (view != null) {
                PhoneDialogAnim.relayoutView(view, this.mEndTranslateY);
            }
            this.mOnShow.clear();
            this.mView.clear();
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            super.onAnimationEnd(animator);
            done();
            this.mOnShow.clear();
            this.mView.clear();
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator, boolean z) {
            View view = this.mView.get();
            if (view != null) {
                view.setTag("show");
                View.OnLayoutChangeListener onLayoutChangeListener = this.mOnLayoutChange;
                if (onLayoutChangeListener != null) {
                    view.addOnLayoutChangeListener(onLayoutChangeListener);
                }
            }
            AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener = this.mOnShow.get();
            if (onDialogShowAnimListener != null) {
                onDialogShowAnimListener.onShowAnimStart();
            }
        }
    }

    /* loaded from: classes5.dex */
    class WeakRefUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        boolean mIsLandscape;
        WeakReference<View> mView;

        WeakRefUpdateListener(View view, boolean z) {
            this.mView = new WeakReference<>(view);
            this.mIsLandscape = z;
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            View view = this.mView.get();
            if (view == null) {
                return;
            }
            if ("hide".equals(view.getTag())) {
                valueAnimator.cancel();
            } else {
                PhoneDialogAnim.relayoutView(view, ((Integer) valueAnimator.getAnimatedValue()).intValue() - PhoneDialogAnim.this.mImeHeight);
            }
        }
    }

    private void dismissPanel(View view, WeakRefDismissListener weakRefDismissListener) {
        if (view == null) {
            return;
        }
        ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat(ViewProperty.TRANSLATION_Y, view.getTranslationY(), view.getHeight() + ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).bottomMargin));
        ofPropertyValuesHolder.setInterpolator(new DecelerateInterpolator(1.5f));
        ofPropertyValuesHolder.addListener(weakRefDismissListener);
        ofPropertyValuesHolder.setDuration(200L);
        ofPropertyValuesHolder.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void doExecuteShowAnim(View view, int i, int i2, boolean z, WeakRefShowListener weakRefShowListener, WeakRefUpdateListener weakRefUpdateListener) {
        ValueAnimator ofInt = ValueAnimator.ofInt(i, i2);
        ofInt.setDuration(350L);
        ofInt.setInterpolator(EaseManager.getInterpolator(0, 0.88f, 0.7f));
        ofInt.addUpdateListener(weakRefUpdateListener);
        ofInt.addListener(weakRefShowListener);
        ofInt.start();
        sValueAnimatorWeakRef = new WeakReference<>(ofInt);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void relayoutView(View view, int i) {
        view.setTranslationY(i);
    }

    @Override // miuix.appcompat.widget.dialoganim.IDialogAnim
    public void cancelAnimator() {
        ValueAnimator valueAnimator;
        WeakReference<ValueAnimator> weakReference = sValueAnimatorWeakRef;
        if (weakReference == null || (valueAnimator = weakReference.get()) == null) {
            return;
        }
        valueAnimator.cancel();
    }

    @Override // miuix.appcompat.widget.dialoganim.IDialogAnim
    public void executeDismissAnim(View view, View view2, DialogAnimHelper.OnDismiss onDismiss) {
        if ("hide".equals(view.getTag())) {
            return;
        }
        dismissPanel(view, new WeakRefDismissListener(view, onDismiss));
        DimAnimator.dismiss(view2);
    }

    @Override // miuix.appcompat.widget.dialoganim.IDialogAnim
    public void executeShowAnim(final View view, final View view2, final boolean z, final AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener) {
        this.mImeHeight = 0;
        final int i = ((ViewGroup.MarginLayoutParams) view2.getLayoutParams()).bottomMargin;
        if (view.getScaleX() != 1.0f) {
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
        }
        final AnimLayoutChangeListener animLayoutChangeListener = Build.VERSION.SDK_INT >= 30 ? new AnimLayoutChangeListener(view) { // from class: miuix.appcompat.widget.dialoganim.PhoneDialogAnim.1
            @Override // miuix.appcompat.widget.dialoganim.PhoneDialogAnim.AnimLayoutChangeListener, android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View view3, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
                super.onLayoutChange(view3, i2, i3, i4, i5, i6, i7, i8, i9);
                boolean isVisible = view3.getRootWindowInsets().isVisible(WindowInsets.Type.ime());
                Insets insets = view3.getRootWindowInsets().getInsets(WindowInsets.Type.ime());
                Insets insets2 = view3.getRootWindowInsets().getInsets(WindowInsets.Type.navigationBars());
                if (isVisible) {
                    PhoneDialogAnim.this.mImeHeight = insets.bottom - insets2.bottom;
                }
                Context context = view3.getContext();
                if (isInMultiScreenMode(context) && isInMultiScreenBottom(context)) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view2.getLayoutParams();
                    int i10 = i + insets.bottom;
                    if (marginLayoutParams.bottomMargin != i10) {
                        marginLayoutParams.bottomMargin = i10;
                        view2.setLayoutParams(marginLayoutParams);
                    }
                }
            }
        } : null;
        if (view.getHeight() > 0) {
            view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: miuix.appcompat.widget.dialoganim.PhoneDialogAnim.2
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View view3, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
                    view3.removeOnLayoutChangeListener(this);
                    int height = view.getHeight();
                    PhoneDialogAnim.relayoutView(view3, height);
                    PhoneDialogAnim.doExecuteShowAnim(view3, height, 0, z, new WeakRefShowListener(onDialogShowAnimListener, animLayoutChangeListener, view3, 0), new WeakRefUpdateListener(view3, z));
                    view3.setVisibility(0);
                }
            });
            view.setVisibility(4);
            view.setAlpha(1.0f);
        } else {
            view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: miuix.appcompat.widget.dialoganim.PhoneDialogAnim.3
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View view3, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
                    view3.removeOnLayoutChangeListener(this);
                    int i10 = i5 - i3;
                    PhoneDialogAnim.relayoutView(view3, i10);
                    PhoneDialogAnim.doExecuteShowAnim(view3, i10, 0, z, new WeakRefShowListener(onDialogShowAnimListener, animLayoutChangeListener, view3, 0), new WeakRefUpdateListener(view3, z));
                }
            });
        }
        DimAnimator.show(view2);
    }
}
