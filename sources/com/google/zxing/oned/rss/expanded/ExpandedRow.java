package com.google.zxing.oned.rss.expanded;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
final class ExpandedRow {
    private final List<ExpandedPair> pairs;
    private final int rowNumber;
    private final boolean wasReversed;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ExpandedRow(List<ExpandedPair> list, int i, boolean z) {
        this.pairs = new ArrayList(list);
        this.rowNumber = i;
        this.wasReversed = z;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ExpandedRow) {
            ExpandedRow expandedRow = (ExpandedRow) obj;
            return this.pairs.equals(expandedRow.getPairs()) && this.wasReversed == expandedRow.wasReversed;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<ExpandedPair> getPairs() {
        return this.pairs;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getRowNumber() {
        return this.rowNumber;
    }

    public int hashCode() {
        return Boolean.valueOf(this.wasReversed).hashCode() ^ this.pairs.hashCode();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEquivalent(List<ExpandedPair> list) {
        return this.pairs.equals(list);
    }

    public String toString() {
        return "{ " + this.pairs + " }";
    }
}
