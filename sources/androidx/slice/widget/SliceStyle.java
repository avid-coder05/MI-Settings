package androidx.slice.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import androidx.slice.SliceItem;
import androidx.slice.view.R$dimen;
import androidx.slice.view.R$styleable;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SliceStyle {
    private final Context mContext;
    private final int mDefaultRowStyleRes;
    private final boolean mExpandToAvailableHeight;
    private final int mGridAllImagesHeight;
    private final int mGridBigPicMaxHeight;
    private final int mGridBigPicMinHeight;
    private final int mGridBottomPadding;
    private final int mGridImageTextHeight;
    private final int mGridMaxHeight;
    private final int mGridMinHeight;
    private final int mGridRawImageTextHeight;
    private final int mGridSubtitleSize;
    private final int mGridTitleSize;
    private final int mGridTopPadding;
    private final int mHeaderSubtitleSize;
    private final int mHeaderTitleSize;
    private final boolean mHideHeaderRow;
    private final float mImageCornerRadius;
    private final int mListLargeHeight;
    private final int mListMinScrollHeight;
    private final SparseArray<RowStyle> mResourceToRowStyle = new SparseArray<>();
    private final int mRowInlineRangeHeight;
    private final int mRowMaxHeight;
    private final int mRowMinHeight;
    private final int mRowRangeHeight;
    private final int mRowSelectionHeight;
    private final int mRowSingleTextWithRangeHeight;
    private final int mRowSingleTextWithSelectionHeight;
    private RowStyleFactory mRowStyleFactory;
    private final int mRowTextWithRangeHeight;
    private final int mRowTextWithSelectionHeight;
    private final int mSubtitleColor;
    private final int mSubtitleSize;
    private int mTintColor;
    private final int mTitleColor;
    private final int mTitleSize;
    private final int mVerticalGridTextPadding;
    private final int mVerticalHeaderTextPadding;
    private final int mVerticalTextPadding;

    public SliceStyle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.mTintColor = -1;
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attrs, R$styleable.SliceView, defStyleAttr, defStyleRes);
        try {
            int color = obtainStyledAttributes.getColor(R$styleable.SliceView_tintColor, -1);
            if (color == -1) {
                color = this.mTintColor;
            }
            this.mTintColor = color;
            this.mTitleColor = obtainStyledAttributes.getColor(R$styleable.SliceView_titleColor, 0);
            this.mSubtitleColor = obtainStyledAttributes.getColor(R$styleable.SliceView_subtitleColor, 0);
            this.mHeaderTitleSize = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_headerTitleSize, 0.0f);
            this.mHeaderSubtitleSize = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_headerSubtitleSize, 0.0f);
            this.mVerticalHeaderTextPadding = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_headerTextVerticalPadding, 0.0f);
            this.mTitleSize = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_titleSize, 0.0f);
            this.mSubtitleSize = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_subtitleSize, 0.0f);
            this.mVerticalTextPadding = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_textVerticalPadding, 0.0f);
            this.mGridTitleSize = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_gridTitleSize, 0.0f);
            this.mGridSubtitleSize = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_gridSubtitleSize, 0.0f);
            this.mVerticalGridTextPadding = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_gridTextVerticalPadding, context.getResources().getDimensionPixelSize(R$dimen.abc_slice_grid_text_inner_padding));
            this.mGridTopPadding = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_gridTopPadding, 0.0f);
            this.mGridBottomPadding = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_gridBottomPadding, 0.0f);
            this.mDefaultRowStyleRes = obtainStyledAttributes.getResourceId(R$styleable.SliceView_rowStyle, 0);
            Resources resources = context.getResources();
            int i = R$dimen.abc_slice_row_min_height;
            this.mRowMinHeight = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_rowMinHeight, resources.getDimensionPixelSize(i));
            this.mRowMaxHeight = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_rowMaxHeight, context.getResources().getDimensionPixelSize(R$dimen.abc_slice_row_max_height));
            this.mRowRangeHeight = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_rowRangeHeight, context.getResources().getDimensionPixelSize(R$dimen.abc_slice_row_range_height));
            this.mRowSingleTextWithRangeHeight = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_rowRangeSingleTextHeight, context.getResources().getDimensionPixelSize(R$dimen.abc_slice_row_range_single_text_height));
            this.mRowInlineRangeHeight = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_rowInlineRangeHeight, context.getResources().getDimensionPixelSize(R$dimen.abc_slice_row_range_inline_height));
            this.mExpandToAvailableHeight = obtainStyledAttributes.getBoolean(R$styleable.SliceView_expandToAvailableHeight, false);
            this.mHideHeaderRow = obtainStyledAttributes.getBoolean(R$styleable.SliceView_hideHeaderRow, false);
            this.mContext = context;
            this.mImageCornerRadius = obtainStyledAttributes.getDimension(R$styleable.SliceView_imageCornerRadius, 0.0f);
            obtainStyledAttributes.recycle();
            Resources resources2 = context.getResources();
            this.mRowTextWithRangeHeight = resources2.getDimensionPixelSize(R$dimen.abc_slice_row_range_multi_text_height);
            this.mRowSelectionHeight = resources2.getDimensionPixelSize(R$dimen.abc_slice_row_selection_height);
            this.mRowTextWithSelectionHeight = resources2.getDimensionPixelSize(R$dimen.abc_slice_row_selection_multi_text_height);
            this.mRowSingleTextWithSelectionHeight = resources2.getDimensionPixelSize(R$dimen.abc_slice_row_selection_single_text_height);
            this.mGridBigPicMinHeight = resources2.getDimensionPixelSize(R$dimen.abc_slice_big_pic_min_height);
            this.mGridBigPicMaxHeight = resources2.getDimensionPixelSize(R$dimen.abc_slice_big_pic_max_height);
            this.mGridAllImagesHeight = resources2.getDimensionPixelSize(R$dimen.abc_slice_grid_image_only_height);
            this.mGridImageTextHeight = resources2.getDimensionPixelSize(R$dimen.abc_slice_grid_image_text_height);
            this.mGridRawImageTextHeight = resources2.getDimensionPixelSize(R$dimen.abc_slice_grid_raw_image_text_offset);
            this.mGridMinHeight = resources2.getDimensionPixelSize(R$dimen.abc_slice_grid_min_height);
            this.mGridMaxHeight = resources2.getDimensionPixelSize(R$dimen.abc_slice_grid_max_height);
            this.mListMinScrollHeight = resources2.getDimensionPixelSize(i);
            this.mListLargeHeight = resources2.getDimensionPixelSize(R$dimen.abc_slice_large_height);
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    private boolean shouldSkipFirstListItem(List<SliceContent> rowItems) {
        return getHideHeaderRow() && rowItems.size() > 1 && (rowItems.get(0) instanceof RowContent) && ((RowContent) rowItems.get(0)).getIsHeader();
    }

    public boolean getApplyCornerRadiusToLargeImages() {
        return this.mImageCornerRadius > 0.0f;
    }

    public boolean getExpandToAvailableHeight() {
        return this.mExpandToAvailableHeight;
    }

    public int getGridBottomPadding() {
        return this.mGridBottomPadding;
    }

    public int getGridHeight(GridContent grid, SliceViewPolicy policy) {
        int i;
        int i2 = 0;
        boolean z = policy.getMode() == 1;
        if (grid.isValid()) {
            int largestImageMode = grid.getLargestImageMode();
            if (grid.isAllImages()) {
                i = grid.getGridContent().size() == 1 ? z ? this.mGridBigPicMinHeight : this.mGridBigPicMaxHeight : largestImageMode == 0 ? this.mGridMinHeight : largestImageMode == 4 ? grid.getFirstImageSize(this.mContext).y : this.mGridAllImagesHeight;
            } else {
                boolean z2 = grid.getMaxCellLineCount() > 1;
                boolean hasImage = grid.hasImage();
                boolean z3 = largestImageMode == 0 || largestImageMode == 5;
                if (largestImageMode == 4) {
                    i = grid.getFirstImageSize(this.mContext).y + ((z2 ? 2 : 1) * this.mGridRawImageTextHeight);
                } else {
                    i = (!z2 || z) ? z3 ? this.mGridMinHeight : this.mGridImageTextHeight : hasImage ? this.mGridMaxHeight : this.mGridMinHeight;
                }
            }
            int i3 = (grid.isAllImages() && grid.getRowIndex() == 0) ? this.mGridTopPadding : 0;
            if (grid.isAllImages() && grid.getIsLastIndex()) {
                i2 = this.mGridBottomPadding;
            }
            return i + i3 + i2;
        }
        return 0;
    }

    public int getGridSubtitleSize() {
        return this.mGridSubtitleSize;
    }

    public int getGridTitleSize() {
        return this.mGridTitleSize;
    }

    public int getGridTopPadding() {
        return this.mGridTopPadding;
    }

    public int getHeaderSubtitleSize() {
        return this.mHeaderSubtitleSize;
    }

    public int getHeaderTitleSize() {
        return this.mHeaderTitleSize;
    }

    public boolean getHideHeaderRow() {
        return this.mHideHeaderRow;
    }

    public float getImageCornerRadius() {
        return this.mImageCornerRadius;
    }

    public int getListHeight(ListContent list, SliceViewPolicy policy) {
        if (policy.getMode() == 1) {
            return list.getHeader().getHeight(this, policy);
        }
        int maxHeight = policy.getMaxHeight();
        boolean isScrollable = policy.isScrollable();
        int listItemsHeight = getListItemsHeight(list.getRowItems(), policy);
        if (maxHeight > 0) {
            maxHeight = Math.max(list.getHeader().getHeight(this, policy), maxHeight);
        }
        int i = maxHeight > 0 ? maxHeight : this.mListLargeHeight;
        if ((listItemsHeight - i >= this.mListMinScrollHeight) && !getExpandToAvailableHeight()) {
            listItemsHeight = i;
        } else if (maxHeight > 0) {
            listItemsHeight = Math.min(i, listItemsHeight);
        }
        return !isScrollable ? getListItemsHeight(getListItemsForNonScrollingList(list, listItemsHeight, policy).getDisplayedItems(), policy) : listItemsHeight;
    }

    public DisplayedListItems getListItemsForNonScrollingList(ListContent list, int availableHeight, SliceViewPolicy policy) {
        int i;
        ArrayList arrayList = new ArrayList();
        if (list.getRowItems() == null || list.getRowItems().size() == 0) {
            return new DisplayedListItems(arrayList, 0);
        }
        boolean shouldSkipFirstListItem = shouldSkipFirstListItem(list.getRowItems());
        int size = list.getRowItems().size();
        int i2 = 0;
        int i3 = 0;
        while (true) {
            if (i2 >= size) {
                i = 0;
                break;
            }
            SliceContent sliceContent = list.getRowItems().get(i2);
            if (i2 != 0 || !shouldSkipFirstListItem) {
                int height = sliceContent.getHeight(this, policy);
                if (availableHeight > 0 && i3 + height > availableHeight) {
                    i = size - i2;
                    break;
                }
                i3 += height;
                arrayList.add(sliceContent);
            }
            i2++;
        }
        int i4 = shouldSkipFirstListItem ? 1 : 2;
        if (list.getSeeMoreItem() != null && arrayList.size() >= i4 && i > 0) {
            int height2 = i3 + list.getSeeMoreItem().getHeight(this, policy);
            while (height2 > availableHeight && arrayList.size() >= i4) {
                int size2 = arrayList.size() - 1;
                height2 -= ((SliceContent) arrayList.get(size2)).getHeight(this, policy);
                arrayList.remove(size2);
                i++;
            }
            if (arrayList.size() >= i4) {
                arrayList.add(list.getSeeMoreItem());
            }
        }
        if (arrayList.size() == 0) {
            arrayList.add(list.getRowItems().get(0));
        }
        return new DisplayedListItems(arrayList, i);
    }

    public int getListItemsHeight(List<SliceContent> listItems, SliceViewPolicy policy) {
        if (listItems == null) {
            return 0;
        }
        int i = 0;
        for (int i2 = 0; i2 < listItems.size(); i2++) {
            SliceContent sliceContent = listItems.get(i2);
            if (i2 != 0 || !shouldSkipFirstListItem(listItems)) {
                i += sliceContent.getHeight(this, policy);
            }
        }
        return i;
    }

    public List<SliceContent> getListItemsToDisplay(ListContent list) {
        ArrayList<SliceContent> rowItems = list.getRowItems();
        return (rowItems.size() <= 0 || !shouldSkipFirstListItem(rowItems)) ? rowItems : rowItems.subList(1, rowItems.size());
    }

    public int getRowHeight(RowContent row, SliceViewPolicy policy) {
        int i;
        int i2;
        int maxSmallHeight = policy.getMaxSmallHeight() > 0 ? policy.getMaxSmallHeight() : this.mRowMaxHeight;
        if (row.getRange() == null && row.getSelection() == null && policy.getMode() != 2) {
            return maxSmallHeight;
        }
        if (row.getRange() != null) {
            if (row.getStartItem() != null) {
                return this.mRowInlineRangeHeight;
            }
            i = row.getLineCount() == 0 ? 0 : row.getLineCount() > 1 ? this.mRowTextWithRangeHeight : this.mRowSingleTextWithRangeHeight;
            i2 = this.mRowRangeHeight;
        } else if (row.getSelection() == null) {
            return (row.getLineCount() > 1 || row.getIsHeader()) ? maxSmallHeight : this.mRowMinHeight;
        } else {
            i = row.getLineCount() > 1 ? this.mRowTextWithSelectionHeight : this.mRowSingleTextWithSelectionHeight;
            i2 = this.mRowSelectionHeight;
        }
        return i + i2;
    }

    public int getRowMaxHeight() {
        return this.mRowMaxHeight;
    }

    public int getRowRangeHeight() {
        return this.mRowRangeHeight;
    }

    public int getRowSelectionHeight() {
        return this.mRowSelectionHeight;
    }

    public RowStyle getRowStyle(SliceItem sliceItem) {
        RowStyleFactory rowStyleFactory;
        int rowStyleRes;
        int i = this.mDefaultRowStyleRes;
        if (sliceItem != null && (rowStyleFactory = this.mRowStyleFactory) != null && (rowStyleRes = rowStyleFactory.getRowStyleRes(sliceItem)) != 0) {
            i = rowStyleRes;
        }
        if (i == 0) {
            return new RowStyle(this.mContext, this);
        }
        RowStyle rowStyle = this.mResourceToRowStyle.get(i);
        if (rowStyle == null) {
            RowStyle rowStyle2 = new RowStyle(this.mContext, i, this);
            this.mResourceToRowStyle.put(i, rowStyle2);
            return rowStyle2;
        }
        return rowStyle;
    }

    public int getSubtitleColor() {
        return this.mSubtitleColor;
    }

    public int getSubtitleSize() {
        return this.mSubtitleSize;
    }

    public int getTintColor() {
        return this.mTintColor;
    }

    public int getTitleColor() {
        return this.mTitleColor;
    }

    public int getTitleSize() {
        return this.mTitleSize;
    }

    public int getVerticalGridTextPadding() {
        return this.mVerticalGridTextPadding;
    }

    public int getVerticalHeaderTextPadding() {
        return this.mVerticalHeaderTextPadding;
    }

    public int getVerticalTextPadding() {
        return this.mVerticalTextPadding;
    }

    public void setRowStyleFactory(RowStyleFactory rowStyleFactory) {
        this.mRowStyleFactory = rowStyleFactory;
    }

    public void setTintColor(int tint) {
        this.mTintColor = tint;
    }
}
