package androidx.slice.widget;

import android.content.Context;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import androidx.slice.SliceMetadata;
import androidx.slice.core.SliceAction;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.core.SliceQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import miui.yellowpage.Tag;

/* loaded from: classes.dex */
public class ListContent extends SliceContent {
    private RowContent mHeaderContent;
    private SliceAction mPrimaryAction;
    private ArrayList<SliceContent> mRowItems;
    private RowContent mSeeMoreContent;
    private List<SliceAction> mSliceActions;

    public ListContent(Slice slice) {
        super(slice);
        this.mRowItems = new ArrayList<>();
        if (this.mSliceItem == null) {
            return;
        }
        populate(slice);
    }

    private static SliceItem findHeaderItem(Slice slice) {
        SliceItem find = SliceQuery.find(slice, "slice", (String[]) null, new String[]{"list_item", "shortcut", Tag.TagServicesData.SERVICE_ACTIONS, "keywords", "ttl", "last_updated", "horizontal", "selection_option"});
        if (find == null || !isValidHeader(find)) {
            return null;
        }
        return find;
    }

    private SliceAction findPrimaryAction() {
        RowContent rowContent = this.mHeaderContent;
        SliceItem primaryAction = rowContent != null ? rowContent.getPrimaryAction() : null;
        if (primaryAction == null) {
            primaryAction = SliceQuery.find(this.mSliceItem, "action", new String[]{"shortcut", "title"}, (String[]) null);
        }
        if (primaryAction == null) {
            primaryAction = SliceQuery.find(this.mSliceItem, "action", (String) null, (String) null);
        }
        if (primaryAction != null) {
            return new SliceActionImpl(primaryAction);
        }
        return null;
    }

    public static int getListHeight(List<SliceContent> listItems, SliceStyle style, SliceViewPolicy policy) {
        return style.getListItemsHeight(listItems, policy);
    }

    public static int getRowType(SliceContent content, boolean isHeader, List<SliceAction> actions) {
        if (content != null) {
            if (content instanceof GridContent) {
                return 1;
            }
            RowContent rowContent = (RowContent) content;
            SliceItem primaryAction = rowContent.getPrimaryAction();
            SliceActionImpl sliceActionImpl = primaryAction != null ? new SliceActionImpl(primaryAction) : null;
            if (rowContent.getRange() != null) {
                return "action".equals(rowContent.getRange().getFormat()) ? 4 : 5;
            } else if (rowContent.getSelection() != null) {
                return 6;
            } else {
                if (sliceActionImpl == null || !sliceActionImpl.isToggle()) {
                    if (!isHeader || actions == null) {
                        return rowContent.getToggleItems().size() > 0 ? 3 : 0;
                    }
                    for (int i = 0; i < actions.size(); i++) {
                        if (actions.get(i).isToggle()) {
                            return 3;
                        }
                    }
                    return 0;
                }
                return 3;
            }
        }
        return 0;
    }

    private static SliceItem getSeeMoreItem(Slice slice) {
        SliceItem findTopLevelItem = SliceQuery.findTopLevelItem(slice, null, null, new String[]{"see_more"}, null);
        if (findTopLevelItem == null || !"slice".equals(findTopLevelItem.getFormat())) {
            return null;
        }
        List<SliceItem> items = findTopLevelItem.getSlice().getItems();
        return (items.size() == 1 && "action".equals(items.get(0).getFormat())) ? items.get(0) : findTopLevelItem;
    }

    private static boolean isValidHeader(SliceItem sliceItem) {
        return (!"slice".equals(sliceItem.getFormat()) || sliceItem.hasAnyHints(Tag.TagServicesData.SERVICE_ACTIONS, "keywords", "see_more") || SliceQuery.find(sliceItem, "text", (String) null, (String) null) == null) ? false : true;
    }

    private void populate(Slice slice) {
        if (slice == null) {
            return;
        }
        this.mSliceActions = SliceMetadata.getSliceActions(slice);
        SliceItem findHeaderItem = findHeaderItem(slice);
        if (findHeaderItem != null) {
            RowContent rowContent = new RowContent(findHeaderItem, 0);
            this.mHeaderContent = rowContent;
            this.mRowItems.add(rowContent);
        }
        SliceItem seeMoreItem = getSeeMoreItem(slice);
        if (seeMoreItem != null) {
            this.mSeeMoreContent = new RowContent(seeMoreItem, -1);
        }
        List<SliceItem> items = slice.getItems();
        for (int i = 0; i < items.size(); i++) {
            SliceItem sliceItem = items.get(i);
            String format = sliceItem.getFormat();
            if (!sliceItem.hasAnyHints(Tag.TagServicesData.SERVICE_ACTIONS, "see_more", "keywords", "ttl", "last_updated") && ("action".equals(format) || "slice".equals(format))) {
                if (this.mHeaderContent == null && !sliceItem.hasHint("list_item")) {
                    RowContent rowContent2 = new RowContent(sliceItem, 0);
                    this.mHeaderContent = rowContent2;
                    this.mRowItems.add(0, rowContent2);
                } else if (sliceItem.hasHint("list_item")) {
                    if (sliceItem.hasHint("horizontal")) {
                        this.mRowItems.add(new GridContent(sliceItem, i));
                    } else {
                        this.mRowItems.add(new RowContent(sliceItem, i));
                    }
                }
            }
        }
        if (this.mHeaderContent == null && this.mRowItems.size() >= 1) {
            RowContent rowContent3 = (RowContent) this.mRowItems.get(0);
            this.mHeaderContent = rowContent3;
            rowContent3.setIsHeader(true);
        }
        if (this.mRowItems.size() > 0) {
            ArrayList<SliceContent> arrayList = this.mRowItems;
            if (arrayList.get(arrayList.size() - 1) instanceof GridContent) {
                ArrayList<SliceContent> arrayList2 = this.mRowItems;
                ((GridContent) arrayList2.get(arrayList2.size() - 1)).setIsLastIndex(true);
            }
        }
        this.mPrimaryAction = findPrimaryAction();
    }

    public RowContent getHeader() {
        return this.mHeaderContent;
    }

    public int getHeaderTemplateType() {
        return getRowType(this.mHeaderContent, true, this.mSliceActions);
    }

    @Override // androidx.slice.widget.SliceContent
    public int getHeight(SliceStyle style, SliceViewPolicy policy) {
        return style.getListHeight(this, policy);
    }

    public DisplayedListItems getRowItems(int availableHeight, SliceStyle style, SliceViewPolicy policy) {
        return policy.getMode() == 1 ? new DisplayedListItems(new ArrayList(Arrays.asList(getHeader())), this.mRowItems.size() - 1) : (policy.isScrollable() || availableHeight <= 0) ? new DisplayedListItems(style.getListItemsToDisplay(this), 0) : style.getListItemsForNonScrollingList(this, availableHeight, policy);
    }

    public ArrayList<SliceContent> getRowItems() {
        return this.mRowItems;
    }

    public SliceContent getSeeMoreItem() {
        return this.mSeeMoreContent;
    }

    @Override // androidx.slice.widget.SliceContent
    public SliceAction getShortcut(Context context) {
        SliceAction sliceAction = this.mPrimaryAction;
        return sliceAction != null ? sliceAction : super.getShortcut(context);
    }

    public List<SliceAction> getSliceActions() {
        return this.mSliceActions;
    }

    @Override // androidx.slice.widget.SliceContent
    public boolean isValid() {
        return super.isValid() && this.mRowItems.size() > 0;
    }

    public void showActionDividers(boolean enabled) {
        Iterator<SliceContent> it = this.mRowItems.iterator();
        while (it.hasNext()) {
            SliceContent next = it.next();
            if (next instanceof RowContent) {
                ((RowContent) next).showActionDivider(enabled);
            }
        }
    }

    public void showHeaderDivider(boolean enabled) {
        if (this.mHeaderContent == null || this.mRowItems.size() <= 1) {
            return;
        }
        this.mHeaderContent.showBottomDivider(enabled);
    }

    public void showTitleItems(boolean enabled) {
        RowContent rowContent = this.mHeaderContent;
        if (rowContent != null) {
            rowContent.showTitleItems(enabled);
        }
    }
}
