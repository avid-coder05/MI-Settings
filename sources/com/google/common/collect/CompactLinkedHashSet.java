package com.google.common.collect;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Arrays;
import java.util.Set;

/* loaded from: classes2.dex */
class CompactLinkedHashSet<E> extends CompactHashSet<E> {
    private transient int firstEntry;
    private transient int lastEntry;
    private transient int[] predecessor;
    private transient int[] successor;

    CompactLinkedHashSet() {
    }

    CompactLinkedHashSet(int i) {
        super(i);
    }

    public static <E> CompactLinkedHashSet<E> createWithExpectedSize(int i) {
        return new CompactLinkedHashSet<>(i);
    }

    private int getPredecessor(int i) {
        return this.predecessor[i] - 1;
    }

    private void setPredecessor(int i, int i2) {
        this.predecessor[i] = i2 + 1;
    }

    private void setSucceeds(int i, int i2) {
        if (i == -2) {
            this.firstEntry = i2;
        } else {
            setSuccessor(i, i2);
        }
        if (i2 == -2) {
            this.lastEntry = i;
        } else {
            setPredecessor(i2, i);
        }
    }

    private void setSuccessor(int i, int i2) {
        this.successor[i] = i2 + 1;
    }

    @Override // com.google.common.collect.CompactHashSet
    int adjustAfterRemove(int i, int i2) {
        return i >= size() ? i2 : i;
    }

    @Override // com.google.common.collect.CompactHashSet
    int allocArrays() {
        int allocArrays = super.allocArrays();
        this.predecessor = new int[allocArrays];
        this.successor = new int[allocArrays];
        return allocArrays;
    }

    @Override // com.google.common.collect.CompactHashSet, java.util.AbstractCollection, java.util.Collection, java.util.Set
    public void clear() {
        if (needsAllocArrays()) {
            return;
        }
        this.firstEntry = -2;
        this.lastEntry = -2;
        int[] iArr = this.predecessor;
        if (iArr != null) {
            Arrays.fill(iArr, 0, size(), 0);
            Arrays.fill(this.successor, 0, size(), 0);
        }
        super.clear();
    }

    @Override // com.google.common.collect.CompactHashSet
    @CanIgnoreReturnValue
    Set<E> convertToHashFloodingResistantImplementation() {
        Set<E> convertToHashFloodingResistantImplementation = super.convertToHashFloodingResistantImplementation();
        this.predecessor = null;
        this.successor = null;
        return convertToHashFloodingResistantImplementation;
    }

    @Override // com.google.common.collect.CompactHashSet
    int firstEntryIndex() {
        return this.firstEntry;
    }

    @Override // com.google.common.collect.CompactHashSet
    int getSuccessor(int i) {
        return this.successor[i] - 1;
    }

    @Override // com.google.common.collect.CompactHashSet
    void init(int i) {
        super.init(i);
        this.firstEntry = -2;
        this.lastEntry = -2;
    }

    @Override // com.google.common.collect.CompactHashSet
    void insertEntry(int i, E e, int i2, int i3) {
        super.insertEntry(i, e, i2, i3);
        setSucceeds(this.lastEntry, i);
        setSucceeds(i, -2);
    }

    @Override // com.google.common.collect.CompactHashSet
    void moveLastEntry(int i, int i2) {
        int size = size() - 1;
        super.moveLastEntry(i, i2);
        setSucceeds(getPredecessor(i), getSuccessor(i));
        if (i < size) {
            setSucceeds(getPredecessor(size), i);
            setSucceeds(i, getSuccessor(size));
        }
        this.predecessor[size] = 0;
        this.successor[size] = 0;
    }

    @Override // com.google.common.collect.CompactHashSet
    void resizeEntries(int i) {
        super.resizeEntries(i);
        this.predecessor = Arrays.copyOf(this.predecessor, i);
        this.successor = Arrays.copyOf(this.successor, i);
    }

    @Override // com.google.common.collect.CompactHashSet, java.util.AbstractCollection, java.util.Collection, java.util.Set
    public Object[] toArray() {
        return ObjectArrays.toArrayImpl(this);
    }

    @Override // com.google.common.collect.CompactHashSet, java.util.AbstractCollection, java.util.Collection, java.util.Set
    public <T> T[] toArray(T[] tArr) {
        return (T[]) ObjectArrays.toArrayImpl(this, tArr);
    }
}
