package miuix.popupwidget.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import miuix.internal.util.DeviceHelper;
import miuix.popupwidget.R$dimen;
import miuix.popupwidget.R$style;
import miuix.smooth.SmoothFrameLayout;

/* loaded from: classes5.dex */
public class DropDownPopupWindow {
    private static int OFFSET_H = 40;
    private ValueAnimator mAnimator;
    private ContainerView mContainer;
    private ContainerController mContainerController;
    private ContentController mContentController;
    private View mContentView;
    private Context mContext;
    private boolean mDismissPending;
    private Controller mDropDownController;
    private int mElevation;
    private int mMaxWith;
    private int mMinWith;
    private PopupWindow mPopupWindow;
    private int mShowDuration = 300;
    private int mDismissDuration = 300;
    private ValueAnimator.AnimatorUpdateListener mValueUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.popupwidget.widget.DropDownPopupWindow.1
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float floatValue = ((Float) DropDownPopupWindow.this.mAnimator.getAnimatedValue()).floatValue();
            if (DropDownPopupWindow.this.mContainerController != null) {
                DropDownPopupWindow.this.mContainerController.onAniamtionUpdate(DropDownPopupWindow.this.mContainer, floatValue);
            }
            if (DropDownPopupWindow.this.mContentController != null) {
                DropDownPopupWindow.this.mContentController.onAniamtionUpdate(DropDownPopupWindow.this.mContentView, floatValue);
            }
        }
    };
    private Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener() { // from class: miuix.popupwidget.widget.DropDownPopupWindow.2
        private void tryDismiss() {
            if (DropDownPopupWindow.this.mDismissPending) {
                DropDownPopupWindow.this.realDismiss();
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
            tryDismiss();
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            tryDismiss();
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            if (!DropDownPopupWindow.this.mDismissPending || DropDownPopupWindow.this.mDropDownController == null) {
                return;
            }
            DropDownPopupWindow.this.mDropDownController.onDismiss();
        }
    };

    /* loaded from: classes5.dex */
    public interface ContainerController extends Controller {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class ContainerView extends SmoothFrameLayout {
        public ContainerView(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
            setCornerRadius(context.getResources().getDimension(R$dimen.miuix_appcompat_drop_down_menu_radius));
        }

        @Override // android.view.View
        public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
            if (i == 4 && keyEvent.getAction() == 1) {
                DropDownPopupWindow.this.dismiss();
                return true;
            }
            return false;
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!super.onTouchEvent(motionEvent) && motionEvent.getAction() == 1) {
                DropDownPopupWindow.this.dismiss();
            }
            return true;
        }
    }

    /* loaded from: classes5.dex */
    public interface ContentController extends Controller {
    }

    /* loaded from: classes5.dex */
    public interface Controller {
        void onAniamtionUpdate(View view, float f);

        void onDismiss();
    }

    public DropDownPopupWindow(Context context, AttributeSet attributeSet, int i) {
        this.mContext = context;
        this.mPopupWindow = new PopupWindow(context, attributeSet, 0, i);
        this.mContainer = new ContainerView(context, attributeSet, i);
        this.mPopupWindow.setAnimationStyle(DeviceHelper.isFeatureWholeAnim() ? R$style.Animation_PopupWindow_DropDown : 0);
        initPopupWindow();
    }

    private void initPopupWindow() {
        this.mElevation = this.mContext.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_drop_down_menu_elevation);
        this.mMinWith = this.mContext.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_drop_down_menu_min_width);
        this.mMaxWith = this.mContext.getResources().getDisplayMetrics().widthPixels - (OFFSET_H * 2);
        this.mPopupWindow.setWidth(-2);
        this.mPopupWindow.setHeight(-2);
        this.mPopupWindow.setSoftInputMode(3);
        this.mPopupWindow.setOutsideTouchable(false);
        this.mPopupWindow.setFocusable(true);
        this.mPopupWindow.setOutsideTouchable(true);
        this.mContainer.setFocusableInTouchMode(true);
        this.mPopupWindow.setContentView(this.mContainer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void realDismiss() {
        PopupWindow popupWindow = this.mPopupWindow;
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        ContainerController containerController = this.mContainerController;
        if (containerController != null) {
            containerController.onDismiss();
        }
        ContentController contentController = this.mContentController;
        if (contentController != null) {
            contentController.onDismiss();
        }
        Controller controller = this.mDropDownController;
        if (controller != null) {
            controller.onDismiss();
        }
        this.mDismissPending = false;
    }

    public void dismiss() {
        this.mDismissPending = true;
        realDismiss();
    }
}
