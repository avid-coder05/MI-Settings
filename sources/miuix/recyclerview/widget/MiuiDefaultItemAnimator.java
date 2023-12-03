package miuix.recyclerview.widget;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import miuix.animation.Folme;
import miuix.animation.IStateStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.property.ViewProperty;
import miuix.recyclerview.widget.MiuiBaseDefaultItemAnimator;

/* loaded from: classes5.dex */
public class MiuiDefaultItemAnimator extends MiuiBaseDefaultItemAnimator {
    public static View.OnAttachStateChangeListener sAttachedListener = new View.OnAttachStateChangeListener() { // from class: miuix.recyclerview.widget.MiuiDefaultItemAnimator.1
        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View view) {
            MiuiBaseDefaultItemAnimator.resetAnimation(view);
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View view) {
        }
    };
    public static AnimConfig sSpeedConfig = new AnimConfig().setFromSpeed(0.0f);

    @Override // miuix.recyclerview.widget.MiuiBaseDefaultItemAnimator
    void animateAddImpl(final RecyclerView.ViewHolder viewHolder) {
        notifyAddStarting(viewHolder);
        IStateStyle state = Folme.useAt(viewHolder.itemView).state();
        ViewProperty viewProperty = ViewProperty.ALPHA;
        Float valueOf = Float.valueOf(1.0f);
        state.to(viewProperty, valueOf, sSpeedConfig);
        viewHolder.itemView.postDelayed(new Runnable() { // from class: miuix.recyclerview.widget.MiuiDefaultItemAnimator.4
            @Override // java.lang.Runnable
            public void run() {
                MiuiDefaultItemAnimator.this.notifyAddFinished(viewHolder);
            }
        }, Folme.useAt(viewHolder.itemView).state().predictDuration(viewProperty, valueOf));
    }

    @Override // miuix.recyclerview.widget.MiuiBaseDefaultItemAnimator
    void animateChangeImpl(MiuiBaseDefaultItemAnimator.ChangeInfo changeInfo) {
        final RecyclerView.ViewHolder viewHolder = changeInfo.oldHolder;
        final View view = viewHolder == null ? null : viewHolder.itemView;
        final RecyclerView.ViewHolder viewHolder2 = changeInfo.newHolder;
        View view2 = viewHolder2 != null ? viewHolder2.itemView : null;
        if (view != null) {
            notifyChangeStarting(viewHolder, true);
            view.addOnAttachStateChangeListener(sAttachedListener);
            IStateStyle state = Folme.useAt(view).state();
            ViewProperty viewProperty = ViewProperty.TRANSLATION_X;
            ViewProperty viewProperty2 = ViewProperty.TRANSLATION_Y;
            state.to(viewProperty, Integer.valueOf(changeInfo.toX - changeInfo.fromX), viewProperty2, Integer.valueOf(changeInfo.toY - changeInfo.fromY), sSpeedConfig);
            view.postDelayed(new Runnable() { // from class: miuix.recyclerview.widget.MiuiDefaultItemAnimator.5
                @Override // java.lang.Runnable
                public void run() {
                    Folme.useAt(view).state().setTo(ViewProperty.TRANSLATION_X, 0, ViewProperty.TRANSLATION_Y, 0, ViewProperty.ALPHA, Float.valueOf(1.0f));
                    MiuiDefaultItemAnimator.this.notifyChangeFinished(viewHolder, true);
                }
            }, Folme.useAt(view).state().predictDuration(viewProperty, Integer.valueOf(changeInfo.toX - changeInfo.fromX), viewProperty2, Integer.valueOf(changeInfo.toY - changeInfo.fromY)));
        }
        if (view2 != null) {
            notifyChangeStarting(viewHolder2, false);
            IStateStyle state2 = Folme.useAt(view2).state();
            ViewProperty viewProperty3 = ViewProperty.TRANSLATION_X;
            ViewProperty viewProperty4 = ViewProperty.TRANSLATION_Y;
            state2.to(viewProperty3, 0, viewProperty4, 0, sSpeedConfig);
            view2.postDelayed(new Runnable() { // from class: miuix.recyclerview.widget.MiuiDefaultItemAnimator.6
                @Override // java.lang.Runnable
                public void run() {
                    Folme.useAt(view).state().setTo(ViewProperty.TRANSLATION_X, 0, ViewProperty.TRANSLATION_Y, 0);
                    MiuiDefaultItemAnimator.this.notifyChangeFinished(viewHolder2, false);
                }
            }, Folme.useAt(view2).state().predictDuration(viewProperty3, 0, viewProperty4, 0));
        }
    }

    @Override // miuix.recyclerview.widget.MiuiBaseDefaultItemAnimator
    void animateMoveImpl(MiuiBaseDefaultItemAnimator.MoveInfo moveInfo) {
        notifyMoveStarting(moveInfo.holder);
        final RecyclerView.ViewHolder viewHolder = moveInfo.holder;
        IStateStyle state = Folme.useAt(viewHolder.itemView).state();
        ViewProperty viewProperty = ViewProperty.TRANSLATION_X;
        ViewProperty viewProperty2 = ViewProperty.TRANSLATION_Y;
        state.to(viewProperty, 0, viewProperty2, 0, sSpeedConfig);
        moveInfo.holder.itemView.postDelayed(new Runnable() { // from class: miuix.recyclerview.widget.MiuiDefaultItemAnimator.3
            @Override // java.lang.Runnable
            public void run() {
                MiuiDefaultItemAnimator.this.notifyMoveFinished(viewHolder);
            }
        }, Folme.useAt(moveInfo.holder.itemView).state().predictDuration(viewProperty, 0, viewProperty2, 0));
    }

    @Override // miuix.recyclerview.widget.MiuiBaseDefaultItemAnimator
    void animateRemoveImpl(final RecyclerView.ViewHolder viewHolder) {
        notifyRemoveStarting(viewHolder);
        viewHolder.itemView.addOnAttachStateChangeListener(sAttachedListener);
        IStateStyle state = Folme.useAt(viewHolder.itemView).state();
        ViewProperty viewProperty = ViewProperty.ALPHA;
        Float valueOf = Float.valueOf(0.0f);
        state.to(viewProperty, valueOf, sSpeedConfig);
        viewHolder.itemView.postDelayed(new Runnable() { // from class: miuix.recyclerview.widget.MiuiDefaultItemAnimator.2
            @Override // java.lang.Runnable
            public void run() {
                Folme.useAt(viewHolder.itemView).state().setTo(ViewProperty.ALPHA, Float.valueOf(1.0f));
                MiuiDefaultItemAnimator.this.notifyRemoveFinished(viewHolder);
            }
        }, Folme.useAt(viewHolder.itemView).state().predictDuration(viewProperty, valueOf));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public long getMoveDuration() {
        return 300L;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public long getRemoveDuration() {
        return 300L;
    }

    @Override // miuix.recyclerview.widget.MiuiBaseDefaultItemAnimator
    void prepareAdd(RecyclerView.ViewHolder viewHolder) {
        resetAnimation(viewHolder);
        viewHolder.itemView.setAlpha(0.0f);
    }

    @Override // miuix.recyclerview.widget.MiuiBaseDefaultItemAnimator
    void prepareChange(MiuiBaseDefaultItemAnimator.ChangeInfo changeInfo) {
        float translationX = changeInfo.oldHolder.itemView.getTranslationX();
        float translationY = changeInfo.oldHolder.itemView.getTranslationY();
        resetAnimation(changeInfo.oldHolder);
        int i = (int) ((changeInfo.toX - changeInfo.fromX) - translationX);
        int i2 = (int) ((changeInfo.toY - changeInfo.fromY) - translationY);
        changeInfo.oldHolder.itemView.setTranslationX(translationX);
        changeInfo.oldHolder.itemView.setTranslationY(translationY);
        RecyclerView.ViewHolder viewHolder = changeInfo.newHolder;
        if (viewHolder != null) {
            resetAnimation(viewHolder);
            changeInfo.newHolder.itemView.setTranslationX(-i);
            changeInfo.newHolder.itemView.setTranslationY(-i2);
        }
    }

    @Override // miuix.recyclerview.widget.MiuiBaseDefaultItemAnimator
    void prepareMove(MiuiBaseDefaultItemAnimator.MoveInfo moveInfo) {
        moveInfo.holder.itemView.setTranslationX(moveInfo.fromX - moveInfo.toX);
        moveInfo.holder.itemView.setTranslationY(moveInfo.fromY - moveInfo.toY);
    }

    @Override // miuix.recyclerview.widget.MiuiBaseDefaultItemAnimator
    void resetAnimation(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder != null) {
            Folme.useAt(viewHolder.itemView).state().end(ViewProperty.TRANSLATION_X, ViewProperty.TRANSLATION_Y, ViewProperty.ALPHA);
            MiuiBaseDefaultItemAnimator.resetAnimation(viewHolder.itemView);
        }
    }
}
