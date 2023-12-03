package androidx.recyclerview.widget;

import androidx.recyclerview.widget.AdapterHelper;
import java.util.List;

/* loaded from: classes.dex */
class OpReorderer {
    final Callback mCallback;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface Callback {
        AdapterHelper.UpdateOp obtainUpdateOp(int cmd, int startPosition, int itemCount, Object payload);

        void recycleUpdateOp(AdapterHelper.UpdateOp op);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public OpReorderer(Callback callback) {
        this.mCallback = callback;
    }

    private int getLastMoveOutOfOrder(List<AdapterHelper.UpdateOp> list) {
        boolean z = false;
        for (int size = list.size() - 1; size >= 0; size--) {
            if (list.get(size).cmd != 8) {
                z = true;
            } else if (z) {
                return size;
            }
        }
        return -1;
    }

    private void swapMoveAdd(List<AdapterHelper.UpdateOp> list, int move, AdapterHelper.UpdateOp moveOp, int add, AdapterHelper.UpdateOp addOp) {
        int i = moveOp.itemCount;
        int i2 = addOp.positionStart;
        int i3 = i < i2 ? -1 : 0;
        int i4 = moveOp.positionStart;
        if (i4 < i2) {
            i3++;
        }
        if (i2 <= i4) {
            moveOp.positionStart = i4 + addOp.itemCount;
        }
        int i5 = addOp.positionStart;
        if (i5 <= i) {
            moveOp.itemCount = i + addOp.itemCount;
        }
        addOp.positionStart = i5 + i3;
        list.set(move, addOp);
        list.set(add, moveOp);
    }

    private void swapMoveOp(List<AdapterHelper.UpdateOp> list, int badMove, int next) {
        AdapterHelper.UpdateOp updateOp = list.get(badMove);
        AdapterHelper.UpdateOp updateOp2 = list.get(next);
        int i = updateOp2.cmd;
        if (i == 1) {
            swapMoveAdd(list, badMove, updateOp, next, updateOp2);
        } else if (i == 2) {
            swapMoveRemove(list, badMove, updateOp, next, updateOp2);
        } else if (i != 4) {
        } else {
            swapMoveUpdate(list, badMove, updateOp, next, updateOp2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reorderOps(List<AdapterHelper.UpdateOp> ops) {
        while (true) {
            int lastMoveOutOfOrder = getLastMoveOutOfOrder(ops);
            if (lastMoveOutOfOrder == -1) {
                return;
            }
            swapMoveOp(ops, lastMoveOutOfOrder, lastMoveOutOfOrder + 1);
        }
    }

    void swapMoveRemove(List<AdapterHelper.UpdateOp> list, int movePos, AdapterHelper.UpdateOp moveOp, int removePos, AdapterHelper.UpdateOp removeOp) {
        boolean z;
        int i = moveOp.positionStart;
        int i2 = moveOp.itemCount;
        boolean z2 = false;
        if (i < i2) {
            if (removeOp.positionStart == i && removeOp.itemCount == i2 - i) {
                z = false;
                z2 = true;
            } else {
                z = false;
            }
        } else if (removeOp.positionStart == i2 + 1 && removeOp.itemCount == i - i2) {
            z = true;
            z2 = true;
        } else {
            z = true;
        }
        int i3 = removeOp.positionStart;
        if (i2 < i3) {
            removeOp.positionStart = i3 - 1;
        } else {
            int i4 = removeOp.itemCount;
            if (i2 < i3 + i4) {
                removeOp.itemCount = i4 - 1;
                moveOp.cmd = 2;
                moveOp.itemCount = 1;
                if (removeOp.itemCount == 0) {
                    list.remove(removePos);
                    this.mCallback.recycleUpdateOp(removeOp);
                    return;
                }
                return;
            }
        }
        int i5 = moveOp.positionStart;
        int i6 = removeOp.positionStart;
        AdapterHelper.UpdateOp updateOp = null;
        if (i5 <= i6) {
            removeOp.positionStart = i6 + 1;
        } else {
            int i7 = removeOp.itemCount;
            if (i5 < i6 + i7) {
                updateOp = this.mCallback.obtainUpdateOp(2, i5 + 1, (i6 + i7) - i5, null);
                removeOp.itemCount = moveOp.positionStart - removeOp.positionStart;
            }
        }
        if (z2) {
            list.set(movePos, removeOp);
            list.remove(removePos);
            this.mCallback.recycleUpdateOp(moveOp);
            return;
        }
        if (z) {
            if (updateOp != null) {
                int i8 = moveOp.positionStart;
                if (i8 > updateOp.positionStart) {
                    moveOp.positionStart = i8 - updateOp.itemCount;
                }
                int i9 = moveOp.itemCount;
                if (i9 > updateOp.positionStart) {
                    moveOp.itemCount = i9 - updateOp.itemCount;
                }
            }
            int i10 = moveOp.positionStart;
            if (i10 > removeOp.positionStart) {
                moveOp.positionStart = i10 - removeOp.itemCount;
            }
            int i11 = moveOp.itemCount;
            if (i11 > removeOp.positionStart) {
                moveOp.itemCount = i11 - removeOp.itemCount;
            }
        } else {
            if (updateOp != null) {
                int i12 = moveOp.positionStart;
                if (i12 >= updateOp.positionStart) {
                    moveOp.positionStart = i12 - updateOp.itemCount;
                }
                int i13 = moveOp.itemCount;
                if (i13 >= updateOp.positionStart) {
                    moveOp.itemCount = i13 - updateOp.itemCount;
                }
            }
            int i14 = moveOp.positionStart;
            if (i14 >= removeOp.positionStart) {
                moveOp.positionStart = i14 - removeOp.itemCount;
            }
            int i15 = moveOp.itemCount;
            if (i15 >= removeOp.positionStart) {
                moveOp.itemCount = i15 - removeOp.itemCount;
            }
        }
        list.set(movePos, removeOp);
        if (moveOp.positionStart != moveOp.itemCount) {
            list.set(removePos, moveOp);
        } else {
            list.remove(removePos);
        }
        if (updateOp != null) {
            list.add(movePos, updateOp);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0027  */
    /* JADX WARN: Removed duplicated region for block: B:12:0x002b  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x0048  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x004c  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0056  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x005b  */
    /* JADX WARN: Removed duplicated region for block: B:24:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void swapMoveUpdate(java.util.List<androidx.recyclerview.widget.AdapterHelper.UpdateOp> r9, int r10, androidx.recyclerview.widget.AdapterHelper.UpdateOp r11, int r12, androidx.recyclerview.widget.AdapterHelper.UpdateOp r13) {
        /*
            r8 = this;
            int r0 = r11.itemCount
            int r1 = r13.positionStart
            r2 = 4
            r3 = 0
            r4 = 1
            if (r0 >= r1) goto Ld
            int r1 = r1 - r4
            r13.positionStart = r1
            goto L20
        Ld:
            int r5 = r13.itemCount
            int r1 = r1 + r5
            if (r0 >= r1) goto L20
            int r5 = r5 - r4
            r13.itemCount = r5
            androidx.recyclerview.widget.OpReorderer$Callback r0 = r8.mCallback
            int r1 = r11.positionStart
            java.lang.Object r5 = r13.payload
            androidx.recyclerview.widget.AdapterHelper$UpdateOp r0 = r0.obtainUpdateOp(r2, r1, r4, r5)
            goto L21
        L20:
            r0 = r3
        L21:
            int r1 = r11.positionStart
            int r5 = r13.positionStart
            if (r1 > r5) goto L2b
            int r5 = r5 + r4
            r13.positionStart = r5
            goto L41
        L2b:
            int r6 = r13.itemCount
            int r7 = r5 + r6
            if (r1 >= r7) goto L41
            int r5 = r5 + r6
            int r5 = r5 - r1
            androidx.recyclerview.widget.OpReorderer$Callback r3 = r8.mCallback
            int r1 = r1 + r4
            java.lang.Object r4 = r13.payload
            androidx.recyclerview.widget.AdapterHelper$UpdateOp r3 = r3.obtainUpdateOp(r2, r1, r5, r4)
            int r1 = r13.itemCount
            int r1 = r1 - r5
            r13.itemCount = r1
        L41:
            r9.set(r12, r11)
            int r11 = r13.itemCount
            if (r11 <= 0) goto L4c
            r9.set(r10, r13)
            goto L54
        L4c:
            r9.remove(r10)
            androidx.recyclerview.widget.OpReorderer$Callback r8 = r8.mCallback
            r8.recycleUpdateOp(r13)
        L54:
            if (r0 == 0) goto L59
            r9.add(r10, r0)
        L59:
            if (r3 == 0) goto L5e
            r9.add(r10, r3)
        L5e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.OpReorderer.swapMoveUpdate(java.util.List, int, androidx.recyclerview.widget.AdapterHelper$UpdateOp, int, androidx.recyclerview.widget.AdapterHelper$UpdateOp):void");
    }
}
