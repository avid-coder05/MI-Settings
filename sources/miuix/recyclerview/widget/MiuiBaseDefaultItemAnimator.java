package miuix.recyclerview.widget;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.animation.Folme;

/* loaded from: classes5.dex */
public abstract class MiuiBaseDefaultItemAnimator extends SimpleItemAnimator {
    private static final List<RecyclerView.ViewHolder> sEmptyAddList = new ArrayList();
    private static final List<MoveInfo> sEmptyMoveList = new ArrayList();
    private static final List<ChangeInfo> sEmptyChangeList = new ArrayList();
    private ArrayList<RecyclerView.ViewHolder> mPendingRemovals = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mPendingAdditions = new ArrayList<>();
    private ArrayList<MoveInfo> mPendingMoves = new ArrayList<>();
    private ArrayList<ChangeInfo> mPendingChanges = new ArrayList<>();
    private ArrayList<ArrayList<RecyclerView.ViewHolder>> mAdditionsList = new ArrayList<>();
    private ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList<>();
    private ArrayList<ArrayList<ChangeInfo>> mChangesList = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mAddAnimations = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mMoveAnimations = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mChangeAnimations = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public static class ChangeInfo {
        int fromX;
        int fromY;
        RecyclerView.ViewHolder newHolder;
        RecyclerView.ViewHolder oldHolder;
        int toX;
        int toY;

        private ChangeInfo(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            this.oldHolder = viewHolder;
            this.newHolder = viewHolder2;
        }

        ChangeInfo(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2, int i, int i2, int i3, int i4) {
            this(viewHolder, viewHolder2);
            this.fromX = i;
            this.fromY = i2;
            this.toX = i3;
            this.toY = i4;
        }

        public String toString() {
            return "ChangeInfo{oldHolder=" + this.oldHolder + ", newHolder=" + this.newHolder + ", fromX=" + this.fromX + ", fromY=" + this.fromY + ", toX=" + this.toX + ", toY=" + this.toY + '}';
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public static class MoveInfo {
        int fromX;
        int fromY;
        RecyclerView.ViewHolder holder;
        int toX;
        int toY;

        MoveInfo(RecyclerView.ViewHolder viewHolder, int i, int i2, int i3, int i4) {
            this.holder = viewHolder;
            this.fromX = i;
            this.fromY = i2;
            this.toX = i3;
            this.toY = i4;
        }

        public String toString() {
            return "MoveInfo{holder=" + this.holder + ", fromX=" + this.fromX + ", fromY=" + this.fromY + ", toX=" + this.toX + ", toY=" + this.toY + '}';
        }
    }

    private void cancelAll(List<RecyclerView.ViewHolder> list) {
        for (int size = list.size() - 1; size >= 0; size--) {
            cancelAnimate(list.get(size).itemView);
        }
        list.clear();
    }

    private void cancelAnimate(View view) {
        Folme.end(view);
    }

    private void dispatchFinishedWhenDone() {
        if (isRunning()) {
            return;
        }
        dispatchAnimationsFinished();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doOtherAnimations() {
        ArrayList<MoveInfo> remove = this.mMovesList.isEmpty() ? sEmptyMoveList : this.mMovesList.remove(0);
        ArrayList<ChangeInfo> remove2 = this.mChangesList.isEmpty() ? sEmptyChangeList : this.mChangesList.remove(0);
        final ArrayList<RecyclerView.ViewHolder> remove3 = this.mAdditionsList.isEmpty() ? sEmptyAddList : this.mAdditionsList.remove(0);
        Iterator it = remove.iterator();
        while (it.hasNext()) {
            animateMoveImpl((MoveInfo) it.next());
        }
        Iterator it2 = remove2.iterator();
        while (it2.hasNext()) {
            animateChangeImpl((ChangeInfo) it2.next());
        }
        if (remove3.isEmpty()) {
            return;
        }
        Runnable runnable = new Runnable() { // from class: miuix.recyclerview.widget.MiuiBaseDefaultItemAnimator.2
            @Override // java.lang.Runnable
            public void run() {
                Iterator it3 = remove3.iterator();
                while (it3.hasNext()) {
                    MiuiBaseDefaultItemAnimator.this.animateAddImpl((RecyclerView.ViewHolder) it3.next());
                }
            }
        };
        if (remove.isEmpty() && remove2.isEmpty()) {
            runnable.run();
        } else {
            ((RecyclerView.ViewHolder) remove3.get(0)).itemView.postDelayed(runnable, 50L);
        }
    }

    private void endChangeAnimation(List<ChangeInfo> list, RecyclerView.ViewHolder viewHolder) {
        for (int size = list.size() - 1; size >= 0; size--) {
            ChangeInfo changeInfo = list.get(size);
            if (endChangeAnimationIfNecessary(changeInfo, viewHolder) && changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                list.remove(changeInfo);
            }
        }
    }

    private void endChangeAnimationIfNecessary(ChangeInfo changeInfo) {
        RecyclerView.ViewHolder viewHolder = changeInfo.oldHolder;
        if (viewHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, viewHolder);
        }
        RecyclerView.ViewHolder viewHolder2 = changeInfo.newHolder;
        if (viewHolder2 != null) {
            endChangeAnimationIfNecessary(changeInfo, viewHolder2);
        }
    }

    private boolean endChangeAnimationIfNecessary(ChangeInfo changeInfo, RecyclerView.ViewHolder viewHolder) {
        boolean z = false;
        if (changeInfo.newHolder == viewHolder) {
            changeInfo.newHolder = null;
        } else if (changeInfo.oldHolder != viewHolder) {
            return false;
        } else {
            changeInfo.oldHolder = null;
            z = true;
        }
        viewHolder.itemView.setAlpha(1.0f);
        viewHolder.itemView.setTranslationX(0.0f);
        viewHolder.itemView.setTranslationY(0.0f);
        dispatchChangeFinished(viewHolder, z);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void resetAnimation(View view) {
        view.setTranslationX(0.0f);
        view.setTranslationY(0.0f);
        view.setAlpha(1.0f);
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
    }

    @Override // androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
        prepareAdd(viewHolder);
        this.mPendingAdditions.add(viewHolder);
        return true;
    }

    abstract void animateAddImpl(RecyclerView.ViewHolder viewHolder);

    @Override // androidx.recyclerview.widget.SimpleItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public boolean animateAppearance(RecyclerView.ViewHolder viewHolder, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo2) {
        int i;
        int i2;
        return (itemHolderInfo == null || ((i = itemHolderInfo.left) == (i2 = itemHolderInfo2.left) && itemHolderInfo.top == itemHolderInfo2.top)) ? animateAdd(viewHolder) : animateMove(viewHolder, i, itemHolderInfo.top, i2, itemHolderInfo2.top);
    }

    @Override // androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateChange(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2, int i, int i2, int i3, int i4) {
        if (viewHolder == viewHolder2) {
            return animateMove(viewHolder2, i, i2, i3, i4);
        }
        ChangeInfo changeInfo = new ChangeInfo(viewHolder, viewHolder2, i, i2, i3, i4);
        prepareChange(changeInfo);
        animateChangeImpl(changeInfo);
        return true;
    }

    abstract void animateChangeImpl(ChangeInfo changeInfo);

    @Override // androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateMove(RecyclerView.ViewHolder viewHolder, int i, int i2, int i3, int i4) {
        resetAnimation(viewHolder);
        int i5 = i4 - i2;
        if (i3 - i == 0 && i5 == 0) {
            dispatchMoveFinished(viewHolder);
            return false;
        }
        MoveInfo moveInfo = new MoveInfo(viewHolder, i, i2, i3, i4);
        prepareMove(moveInfo);
        this.mPendingMoves.add(moveInfo);
        return true;
    }

    abstract void animateMoveImpl(MoveInfo moveInfo);

    @Override // androidx.recyclerview.widget.SimpleItemAnimator
    public boolean animateRemove(RecyclerView.ViewHolder viewHolder) {
        resetAnimation(viewHolder);
        this.mPendingRemovals.add(viewHolder);
        return true;
    }

    abstract void animateRemoveImpl(RecyclerView.ViewHolder viewHolder);

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder, List<Object> list) {
        return !list.isEmpty() || super.canReuseUpdatedViewHolder(viewHolder, list);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public void endAnimation(RecyclerView.ViewHolder viewHolder) {
        View view = viewHolder.itemView;
        cancelAnimate(view);
        int size = this.mPendingMoves.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            } else if (this.mPendingMoves.get(size).holder == viewHolder) {
                view.setTranslationY(0.0f);
                view.setTranslationX(0.0f);
                dispatchMoveFinished(viewHolder);
                this.mPendingMoves.remove(size);
            }
        }
        endChangeAnimation(this.mPendingChanges, viewHolder);
        if (this.mPendingRemovals.remove(viewHolder)) {
            view.setAlpha(1.0f);
            dispatchRemoveFinished(viewHolder);
        }
        if (this.mPendingAdditions.remove(viewHolder)) {
            view.setAlpha(1.0f);
            dispatchAddFinished(viewHolder);
        }
        for (int size2 = this.mChangesList.size() - 1; size2 >= 0; size2--) {
            ArrayList<ChangeInfo> arrayList = this.mChangesList.get(size2);
            endChangeAnimation(arrayList, viewHolder);
            if (arrayList.isEmpty()) {
                this.mChangesList.remove(size2);
            }
        }
        for (int size3 = this.mMovesList.size() - 1; size3 >= 0; size3--) {
            ArrayList<MoveInfo> arrayList2 = this.mMovesList.get(size3);
            int size4 = arrayList2.size() - 1;
            while (true) {
                if (size4 < 0) {
                    break;
                } else if (arrayList2.get(size4).holder == viewHolder) {
                    view.setTranslationY(0.0f);
                    view.setTranslationX(0.0f);
                    dispatchMoveFinished(viewHolder);
                    arrayList2.remove(size4);
                    if (arrayList2.isEmpty()) {
                        this.mMovesList.remove(size3);
                    }
                } else {
                    size4--;
                }
            }
        }
        for (int size5 = this.mAdditionsList.size() - 1; size5 >= 0; size5--) {
            ArrayList<RecyclerView.ViewHolder> arrayList3 = this.mAdditionsList.get(size5);
            if (arrayList3.remove(viewHolder)) {
                view.setAlpha(1.0f);
                dispatchAddFinished(viewHolder);
                if (arrayList3.isEmpty()) {
                    this.mAdditionsList.remove(size5);
                }
            }
        }
        this.mRemoveAnimations.remove(viewHolder);
        this.mAddAnimations.remove(viewHolder);
        this.mChangeAnimations.remove(viewHolder);
        this.mMoveAnimations.remove(viewHolder);
        dispatchFinishedWhenDone();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public void endAnimations() {
        int size = this.mPendingMoves.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            }
            MoveInfo moveInfo = this.mPendingMoves.get(size);
            View view = moveInfo.holder.itemView;
            view.setTranslationY(0.0f);
            view.setTranslationX(0.0f);
            dispatchMoveFinished(moveInfo.holder);
            this.mPendingMoves.remove(size);
        }
        for (int size2 = this.mPendingRemovals.size() - 1; size2 >= 0; size2--) {
            dispatchRemoveFinished(this.mPendingRemovals.get(size2));
            this.mPendingRemovals.remove(size2);
        }
        int size3 = this.mPendingAdditions.size();
        while (true) {
            size3--;
            if (size3 < 0) {
                break;
            }
            RecyclerView.ViewHolder viewHolder = this.mPendingAdditions.get(size3);
            viewHolder.itemView.setAlpha(1.0f);
            dispatchAddFinished(viewHolder);
            this.mPendingAdditions.remove(size3);
        }
        for (int size4 = this.mPendingChanges.size() - 1; size4 >= 0; size4--) {
            endChangeAnimationIfNecessary(this.mPendingChanges.get(size4));
        }
        this.mPendingChanges.clear();
        if (isRunning()) {
            for (int size5 = this.mMovesList.size() - 1; size5 >= 0; size5--) {
                ArrayList<MoveInfo> arrayList = this.mMovesList.get(size5);
                for (int size6 = arrayList.size() - 1; size6 >= 0; size6--) {
                    MoveInfo moveInfo2 = arrayList.get(size6);
                    View view2 = moveInfo2.holder.itemView;
                    view2.setTranslationY(0.0f);
                    view2.setTranslationX(0.0f);
                    dispatchMoveFinished(moveInfo2.holder);
                    arrayList.remove(size6);
                    if (arrayList.isEmpty()) {
                        this.mMovesList.remove(arrayList);
                    }
                }
            }
            for (int size7 = this.mAdditionsList.size() - 1; size7 >= 0; size7--) {
                ArrayList<RecyclerView.ViewHolder> arrayList2 = this.mAdditionsList.get(size7);
                for (int size8 = arrayList2.size() - 1; size8 >= 0; size8--) {
                    RecyclerView.ViewHolder viewHolder2 = arrayList2.get(size8);
                    viewHolder2.itemView.setAlpha(1.0f);
                    dispatchAddFinished(viewHolder2);
                    arrayList2.remove(size8);
                    if (arrayList2.isEmpty()) {
                        this.mAdditionsList.remove(arrayList2);
                    }
                }
            }
            for (int size9 = this.mChangesList.size() - 1; size9 >= 0; size9--) {
                ArrayList<ChangeInfo> arrayList3 = this.mChangesList.get(size9);
                for (int size10 = arrayList3.size() - 1; size10 >= 0; size10--) {
                    endChangeAnimationIfNecessary(arrayList3.get(size10));
                    if (arrayList3.isEmpty()) {
                        this.mChangesList.remove(arrayList3);
                    }
                }
            }
            cancelAll(this.mRemoveAnimations);
            cancelAll(this.mMoveAnimations);
            cancelAll(this.mAddAnimations);
            cancelAll(this.mChangeAnimations);
            dispatchAnimationsFinished();
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public boolean isRunning() {
        return (this.mPendingAdditions.isEmpty() && this.mPendingChanges.isEmpty() && this.mPendingMoves.isEmpty() && this.mPendingRemovals.isEmpty() && this.mMoveAnimations.isEmpty() && this.mRemoveAnimations.isEmpty() && this.mAddAnimations.isEmpty() && this.mChangeAnimations.isEmpty() && this.mMovesList.isEmpty() && this.mAdditionsList.isEmpty() && this.mChangesList.isEmpty()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyAddFinished(RecyclerView.ViewHolder viewHolder) {
        dispatchAddFinished(viewHolder);
        this.mAddAnimations.remove(viewHolder);
        dispatchFinishedWhenDone();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyAddStarting(RecyclerView.ViewHolder viewHolder) {
        this.mAddAnimations.add(viewHolder);
        dispatchAddStarting(viewHolder);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyChangeFinished(RecyclerView.ViewHolder viewHolder, boolean z) {
        dispatchChangeFinished(viewHolder, z);
        this.mChangeAnimations.remove(viewHolder);
        dispatchFinishedWhenDone();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyChangeStarting(RecyclerView.ViewHolder viewHolder, boolean z) {
        this.mChangeAnimations.add(viewHolder);
        dispatchChangeStarting(viewHolder, z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyMoveFinished(RecyclerView.ViewHolder viewHolder) {
        dispatchMoveFinished(viewHolder);
        this.mMoveAnimations.remove(viewHolder);
        dispatchFinishedWhenDone();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyMoveStarting(RecyclerView.ViewHolder viewHolder) {
        this.mMoveAnimations.add(viewHolder);
        dispatchMoveStarting(viewHolder);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyRemoveFinished(RecyclerView.ViewHolder viewHolder) {
        dispatchRemoveFinished(viewHolder);
        this.mRemoveAnimations.remove(viewHolder);
        dispatchFinishedWhenDone();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyRemoveStarting(RecyclerView.ViewHolder viewHolder) {
        this.mRemoveAnimations.add(viewHolder);
        dispatchRemoveStarting(viewHolder);
    }

    abstract void prepareAdd(RecyclerView.ViewHolder viewHolder);

    abstract void prepareChange(ChangeInfo changeInfo);

    abstract void prepareMove(MoveInfo moveInfo);

    abstract void resetAnimation(RecyclerView.ViewHolder viewHolder);

    @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
    public void runPendingAnimations() {
        boolean z = !this.mPendingRemovals.isEmpty();
        boolean z2 = !this.mPendingMoves.isEmpty();
        boolean z3 = !this.mPendingChanges.isEmpty();
        boolean z4 = !this.mPendingAdditions.isEmpty();
        if (z || z2 || z3 || z4) {
            this.mMovesList.add(new ArrayList<>(this.mPendingMoves));
            this.mPendingMoves.clear();
            this.mChangesList.add(new ArrayList<>(this.mPendingChanges));
            this.mPendingChanges.clear();
            this.mAdditionsList.add(new ArrayList<>(this.mPendingAdditions));
            this.mPendingAdditions.clear();
            Runnable runnable = new Runnable() { // from class: miuix.recyclerview.widget.MiuiBaseDefaultItemAnimator.1
                @Override // java.lang.Runnable
                public void run() {
                    MiuiBaseDefaultItemAnimator.this.doOtherAnimations();
                }
            };
            if (!z) {
                runnable.run();
                return;
            }
            Iterator<RecyclerView.ViewHolder> it = this.mPendingRemovals.iterator();
            while (it.hasNext()) {
                animateRemoveImpl(it.next());
            }
            this.mPendingRemovals.get(0).itemView.postDelayed(runnable, 100L);
            this.mPendingRemovals.clear();
        }
    }
}
