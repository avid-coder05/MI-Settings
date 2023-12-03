package com.android.settings.network.helper;

import android.telephony.TelephonyManager;
import android.telephony.UiccSlotInfo;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/* loaded from: classes.dex */
public class QuerySimSlotIndex implements Callable<AtomicIntegerArray> {
    private boolean mDisabledSlotsIncluded;
    private boolean mOnlySlotWithSim;
    private TelephonyManager mTelephonyManager;

    public QuerySimSlotIndex(TelephonyManager telephonyManager, boolean z, boolean z2) {
        this.mTelephonyManager = telephonyManager;
        this.mDisabledSlotsIncluded = z;
        this.mOnlySlotWithSim = z2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$call$2(int i, int i2) {
        return i2 >= i;
    }

    @Override // java.util.concurrent.Callable
    public AtomicIntegerArray call() {
        UiccSlotInfo[] uiccSlotsInfo = this.mTelephonyManager.getUiccSlotsInfo();
        if (uiccSlotsInfo == null) {
            return new AtomicIntegerArray(0);
        }
        final int i = this.mOnlySlotWithSim ? 0 : -1;
        return new AtomicIntegerArray(Arrays.stream(uiccSlotsInfo).filter(new Predicate() { // from class: com.android.settings.network.helper.QuerySimSlotIndex$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$call$0;
                lambda$call$0 = QuerySimSlotIndex.this.lambda$call$0((UiccSlotInfo) obj);
                return lambda$call$0;
            }
        }).mapToInt(new ToIntFunction() { // from class: com.android.settings.network.helper.QuerySimSlotIndex$$ExternalSyntheticLambda2
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int lambda$call$1;
                lambda$call$1 = QuerySimSlotIndex.this.lambda$call$1((UiccSlotInfo) obj);
                return lambda$call$1;
            }
        }).filter(new IntPredicate() { // from class: com.android.settings.network.helper.QuerySimSlotIndex$$ExternalSyntheticLambda0
            @Override // java.util.function.IntPredicate
            public final boolean test(int i2) {
                boolean lambda$call$2;
                lambda$call$2 = QuerySimSlotIndex.lambda$call$2(i, i2);
                return lambda$call$2;
            }
        }).toArray());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: filterSlot  reason: merged with bridge method [inline-methods] */
    public boolean lambda$call$0(UiccSlotInfo uiccSlotInfo) {
        if (this.mDisabledSlotsIncluded) {
            return true;
        }
        if (uiccSlotInfo == null) {
            return false;
        }
        return uiccSlotInfo.getIsActive();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: mapToSlotIndex  reason: merged with bridge method [inline-methods] */
    public int lambda$call$1(UiccSlotInfo uiccSlotInfo) {
        if (uiccSlotInfo == null || uiccSlotInfo.getCardStateInfo() == 1) {
            return -1;
        }
        return uiccSlotInfo.getLogicalSlotIdx();
    }
}
