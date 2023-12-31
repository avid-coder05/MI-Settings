package androidx.slice.widget;

import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class DisplayedListItems {
    private final List<SliceContent> mDisplayedItems;
    private final int mHiddenItemCount;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DisplayedListItems(List<SliceContent> displayedItems, int hiddenItemCount) {
        this.mDisplayedItems = displayedItems;
        this.mHiddenItemCount = hiddenItemCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<SliceContent> getDisplayedItems() {
        return this.mDisplayedItems;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getHiddenItemCount() {
        return this.mHiddenItemCount;
    }
}
