package androidx.slice.widget;

import android.text.TextUtils;
import android.util.Log;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceAction;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.core.SliceQuery;
import com.android.settings.search.FunctionColumns;
import java.util.ArrayList;
import java.util.List;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class RowContent extends SliceContent {
    private final ArrayList<SliceItem> mEndItems;
    private boolean mIsHeader;
    private int mLineCount;
    private SliceItem mPrimaryAction;
    private SliceItem mRange;
    private SliceItem mSelection;
    private boolean mShowActionDivider;
    private boolean mShowBottomDivider;
    private boolean mShowTitleItems;
    private SliceItem mStartItem;
    private SliceItem mSubtitleItem;
    private SliceItem mSummaryItem;
    private SliceItem mTitleItem;
    private final ArrayList<SliceAction> mToggleItems;

    public RowContent(SliceItem rowSlice, int position) {
        super(rowSlice, position);
        this.mEndItems = new ArrayList<>();
        this.mToggleItems = new ArrayList<>();
        this.mLineCount = 0;
        populate(rowSlice, position == 0);
    }

    private void determineStartAndPrimaryAction(SliceItem rowSlice) {
        List<SliceItem> findAll = SliceQuery.findAll(rowSlice, (String) null, "title", (String) null);
        if (findAll.size() > 0) {
            String format = findAll.get(0).getFormat();
            if (("action".equals(format) && SliceQuery.find(findAll.get(0), YellowPageContract.ImageLookup.DIRECTORY_IMAGE) != null) || "slice".equals(format) || "long".equals(format) || YellowPageContract.ImageLookup.DIRECTORY_IMAGE.equals(format)) {
                this.mStartItem = findAll.get(0);
            }
        }
        String[] strArr = {"shortcut", "title"};
        List<SliceItem> findAll2 = SliceQuery.findAll(rowSlice, "slice", strArr, (String[]) null);
        findAll2.addAll(SliceQuery.findAll(rowSlice, "action", strArr, (String[]) null));
        if (findAll2.isEmpty() && "action".equals(rowSlice.getFormat()) && rowSlice.getSlice().getItems().size() == 1) {
            this.mPrimaryAction = rowSlice;
        } else if (this.mStartItem != null && findAll2.size() > 1 && findAll2.get(0) == this.mStartItem) {
            this.mPrimaryAction = findAll2.get(1);
        } else if (findAll2.size() > 0) {
            this.mPrimaryAction = findAll2.get(0);
        }
    }

    private static ArrayList<SliceItem> filterInvalidItems(SliceItem rowSlice) {
        ArrayList<SliceItem> arrayList = new ArrayList<>();
        for (SliceItem sliceItem : rowSlice.getSlice().getItems()) {
            if (isValidRowContent(rowSlice, sliceItem)) {
                arrayList.add(sliceItem);
            }
        }
        return arrayList;
    }

    private static boolean hasText(SliceItem textSlice) {
        return textSlice != null && (textSlice.hasHint("partial") || !TextUtils.isEmpty(textSlice.getText()));
    }

    private static boolean isValidRow(SliceItem rowSlice) {
        if (rowSlice == null) {
            return false;
        }
        if ("slice".equals(rowSlice.getFormat()) || "action".equals(rowSlice.getFormat())) {
            List<SliceItem> items = rowSlice.getSlice().getItems();
            if (rowSlice.hasHint("see_more") && items.isEmpty()) {
                return true;
            }
            for (int i = 0; i < items.size(); i++) {
                if (isValidRowContent(rowSlice, items.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isValidRowContent(SliceItem slice, SliceItem item) {
        if (item.hasAnyHints("keywords", "ttl", "last_updated", "horizontal") || "content_description".equals(item.getSubType()) || "selection_option_key".equals(item.getSubType()) || "selection_option_value".equals(item.getSubType())) {
            return false;
        }
        String format = item.getFormat();
        return YellowPageContract.ImageLookup.DIRECTORY_IMAGE.equals(format) || "text".equals(format) || "long".equals(format) || "action".equals(format) || "input".equals(format) || "slice".equals(format) || ("int".equals(format) && "range".equals(slice.getSubType()));
    }

    private boolean populate(SliceItem rowSlice, boolean isHeader) {
        boolean z;
        if (rowSlice.hasHint("end_of_section")) {
            showBottomDivider(true);
        }
        this.mIsHeader = isHeader;
        if (!isValidRow(rowSlice)) {
            Log.w("RowContent", "Provided SliceItem is invalid for RowContent");
            return false;
        }
        determineStartAndPrimaryAction(rowSlice);
        ArrayList<SliceItem> filterInvalidItems = filterInvalidItems(rowSlice);
        if (filterInvalidItems.size() == 1 && (("action".equals(filterInvalidItems.get(0).getFormat()) || "slice".equals(filterInvalidItems.get(0).getFormat())) && !filterInvalidItems.get(0).hasAnyHints("shortcut", "title") && isValidRow(filterInvalidItems.get(0)))) {
            rowSlice = filterInvalidItems.get(0);
            filterInvalidItems = filterInvalidItems(rowSlice);
            z = true;
        } else {
            z = false;
        }
        if ("range".equals(rowSlice.getSubType())) {
            if (SliceQuery.findSubtype(rowSlice, "action", "range") == null || z) {
                this.mRange = rowSlice;
            } else {
                filterInvalidItems.remove(this.mStartItem);
                if (filterInvalidItems.size() != 1) {
                    SliceItem findSubtype = SliceQuery.findSubtype(rowSlice, "action", "range");
                    this.mRange = findSubtype;
                    ArrayList<SliceItem> filterInvalidItems2 = filterInvalidItems(findSubtype);
                    filterInvalidItems2.remove(getInputRangeThumb());
                    filterInvalidItems.remove(this.mRange);
                    filterInvalidItems.addAll(filterInvalidItems2);
                } else if (isValidRow(filterInvalidItems.get(0))) {
                    rowSlice = filterInvalidItems.get(0);
                    filterInvalidItems = filterInvalidItems(rowSlice);
                    this.mRange = rowSlice;
                    filterInvalidItems.remove(getInputRangeThumb());
                }
            }
        }
        if ("selection".equals(rowSlice.getSubType())) {
            this.mSelection = rowSlice;
        }
        if (filterInvalidItems.size() > 0) {
            SliceItem sliceItem = this.mStartItem;
            if (sliceItem != null) {
                filterInvalidItems.remove(sliceItem);
            }
            SliceItem sliceItem2 = this.mPrimaryAction;
            if (sliceItem2 != null) {
                filterInvalidItems.remove(sliceItem2);
            }
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < filterInvalidItems.size(); i++) {
                SliceItem sliceItem3 = filterInvalidItems.get(i);
                if ("text".equals(sliceItem3.getFormat())) {
                    SliceItem sliceItem4 = this.mTitleItem;
                    if ((sliceItem4 == null || !sliceItem4.hasHint("title")) && sliceItem3.hasHint("title") && !sliceItem3.hasHint(FunctionColumns.SUMMARY)) {
                        this.mTitleItem = sliceItem3;
                    } else if (this.mSubtitleItem == null && !sliceItem3.hasHint(FunctionColumns.SUMMARY)) {
                        this.mSubtitleItem = sliceItem3;
                    } else if (this.mSummaryItem == null && sliceItem3.hasHint(FunctionColumns.SUMMARY)) {
                        this.mSummaryItem = sliceItem3;
                    }
                } else {
                    arrayList.add(sliceItem3);
                }
            }
            if (hasText(this.mTitleItem)) {
                this.mLineCount++;
            }
            if (hasText(this.mSubtitleItem)) {
                this.mLineCount++;
            }
            SliceItem sliceItem5 = this.mStartItem;
            boolean z2 = sliceItem5 != null && "long".equals(sliceItem5.getFormat());
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                SliceItem sliceItem6 = (SliceItem) arrayList.get(i2);
                boolean z3 = SliceQuery.find(sliceItem6, "action") != null;
                if (!"long".equals(sliceItem6.getFormat())) {
                    processContent(sliceItem6, z3);
                } else if (!z2) {
                    this.mEndItems.add(sliceItem6);
                    z2 = true;
                }
            }
        }
        return isValid();
    }

    private void processContent(SliceItem item, boolean isAction) {
        if (isAction) {
            SliceActionImpl sliceActionImpl = new SliceActionImpl(item);
            if (sliceActionImpl.isToggle()) {
                this.mToggleItems.add(sliceActionImpl);
            }
        }
        this.mEndItems.add(item);
    }

    public List<SliceItem> getEndItems() {
        return this.mEndItems;
    }

    @Override // androidx.slice.widget.SliceContent
    public int getHeight(SliceStyle style, SliceViewPolicy policy) {
        return style.getRowHeight(this, policy);
    }

    public SliceItem getInputRangeThumb() {
        SliceItem sliceItem = this.mRange;
        if (sliceItem != null) {
            List<SliceItem> items = sliceItem.getSlice().getItems();
            for (int i = 0; i < items.size(); i++) {
                if (YellowPageContract.ImageLookup.DIRECTORY_IMAGE.equals(items.get(i).getFormat())) {
                    return items.get(i);
                }
            }
            return null;
        }
        return null;
    }

    public boolean getIsHeader() {
        return this.mIsHeader;
    }

    public int getLineCount() {
        return this.mLineCount;
    }

    public SliceItem getPrimaryAction() {
        return this.mPrimaryAction;
    }

    public SliceItem getRange() {
        return this.mRange;
    }

    public SliceItem getSelection() {
        return this.mSelection;
    }

    public SliceItem getStartItem() {
        if (!this.mIsHeader || this.mShowTitleItems) {
            return this.mStartItem;
        }
        return null;
    }

    public SliceItem getSubtitleItem() {
        return this.mSubtitleItem;
    }

    public SliceItem getSummaryItem() {
        SliceItem sliceItem = this.mSummaryItem;
        return sliceItem == null ? this.mSubtitleItem : sliceItem;
    }

    public SliceItem getTitleItem() {
        return this.mTitleItem;
    }

    public List<SliceAction> getToggleItems() {
        return this.mToggleItems;
    }

    public boolean hasActionDivider() {
        return this.mShowActionDivider;
    }

    public boolean hasBottomDivider() {
        return this.mShowBottomDivider;
    }

    public boolean hasTitleItems() {
        return this.mShowTitleItems;
    }

    public boolean isDefaultSeeMore() {
        return "action".equals(this.mSliceItem.getFormat()) && this.mSliceItem.getSlice().hasHint("see_more") && this.mSliceItem.getSlice().getItems().isEmpty();
    }

    @Override // androidx.slice.widget.SliceContent
    public boolean isValid() {
        return super.isValid() && !(this.mStartItem == null && this.mPrimaryAction == null && this.mTitleItem == null && this.mSubtitleItem == null && this.mEndItems.size() <= 0 && this.mRange == null && this.mSelection == null && !isDefaultSeeMore());
    }

    public void setIsHeader(boolean isHeader) {
        this.mIsHeader = isHeader;
    }

    public void showActionDivider(boolean enabled) {
        this.mShowActionDivider = enabled;
    }

    public void showBottomDivider(boolean enabled) {
        this.mShowBottomDivider = enabled;
    }

    public void showTitleItems(boolean enabled) {
        this.mShowTitleItems = enabled;
    }
}
