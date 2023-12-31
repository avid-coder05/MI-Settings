package androidx.slice.widget;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.slice.view.R$dimen;
import androidx.slice.view.R$styleable;

/* loaded from: classes.dex */
public class RowStyle {
    private int mActionDividerHeight;
    private int mBottomDividerEndPadding;
    private int mBottomDividerStartPadding;
    private int mContentEndPadding;
    private int mContentStartPadding;
    private boolean mDisableRecyclerViewItemAnimator;
    private int mEndItemEndPadding;
    private int mEndItemStartPadding;
    private int mIconSize;
    private int mImageSize;
    private int mProgressBarEndPadding;
    private int mProgressBarInlineWidth;
    private int mProgressBarStartPadding;
    private int mSeekBarInlineWidth;
    private final SliceStyle mSliceStyle;
    private int mSubContentEndPadding;
    private int mSubContentStartPadding;
    private Integer mSubtitleColor;
    private int mTextActionPadding;
    private Integer mTintColor;
    private Integer mTitleColor;
    private int mTitleEndPadding;
    private int mTitleItemEndPadding;
    private int mTitleItemStartPadding;
    private int mTitleStartPadding;

    public RowStyle(Context context, int resId, SliceStyle sliceStyle) {
        this.mTitleItemStartPadding = -1;
        this.mTitleItemEndPadding = -1;
        this.mContentStartPadding = -1;
        this.mContentEndPadding = -1;
        this.mTitleStartPadding = -1;
        this.mTitleEndPadding = -1;
        this.mSubContentStartPadding = -1;
        this.mSubContentEndPadding = -1;
        this.mEndItemStartPadding = -1;
        this.mEndItemEndPadding = -1;
        this.mBottomDividerStartPadding = -1;
        this.mBottomDividerEndPadding = -1;
        this.mActionDividerHeight = -1;
        this.mSeekBarInlineWidth = -1;
        this.mProgressBarInlineWidth = -1;
        this.mProgressBarStartPadding = -1;
        this.mProgressBarEndPadding = -1;
        this.mTextActionPadding = -1;
        this.mIconSize = -1;
        this.mDisableRecyclerViewItemAnimator = false;
        this.mSliceStyle = sliceStyle;
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(resId, R$styleable.RowStyle);
        try {
            this.mTitleItemStartPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_titleItemStartPadding, -1.0f);
            this.mTitleItemEndPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_titleItemEndPadding, -1.0f);
            this.mContentStartPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_contentStartPadding, -1.0f);
            this.mContentEndPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_contentEndPadding, -1.0f);
            this.mTitleStartPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_titleStartPadding, -1.0f);
            this.mTitleEndPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_titleEndPadding, -1.0f);
            this.mSubContentStartPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_subContentStartPadding, -1.0f);
            this.mSubContentEndPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_subContentEndPadding, -1.0f);
            this.mEndItemStartPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_endItemStartPadding, -1.0f);
            this.mEndItemEndPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_endItemEndPadding, -1.0f);
            this.mBottomDividerStartPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_bottomDividerStartPadding, -1.0f);
            this.mBottomDividerEndPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_bottomDividerEndPadding, -1.0f);
            this.mActionDividerHeight = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_actionDividerHeight, -1.0f);
            this.mSeekBarInlineWidth = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_seekBarInlineWidth, -1.0f);
            this.mProgressBarInlineWidth = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_progressBarInlineWidth, -1.0f);
            this.mProgressBarStartPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_progressBarStartPadding, -1.0f);
            this.mProgressBarEndPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_progressBarEndPadding, -1.0f);
            this.mTextActionPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_textActionPadding, 10.0f);
            this.mIconSize = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_iconSize, -1.0f);
            this.mDisableRecyclerViewItemAnimator = obtainStyledAttributes.getBoolean(R$styleable.RowStyle_disableRecyclerViewItemAnimator, false);
            this.mImageSize = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_imageSize, context.getResources().getDimensionPixelSize(R$dimen.abc_slice_small_image_size));
            this.mTintColor = getOptionalColor(obtainStyledAttributes, R$styleable.RowStyle_tintColor);
            this.mTitleColor = getOptionalColor(obtainStyledAttributes, R$styleable.RowStyle_titleColor);
            this.mSubtitleColor = getOptionalColor(obtainStyledAttributes, R$styleable.RowStyle_subtitleColor);
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    public RowStyle(Context context, SliceStyle sliceStyle) {
        this.mTitleItemStartPadding = -1;
        this.mTitleItemEndPadding = -1;
        this.mContentStartPadding = -1;
        this.mContentEndPadding = -1;
        this.mTitleStartPadding = -1;
        this.mTitleEndPadding = -1;
        this.mSubContentStartPadding = -1;
        this.mSubContentEndPadding = -1;
        this.mEndItemStartPadding = -1;
        this.mEndItemEndPadding = -1;
        this.mBottomDividerStartPadding = -1;
        this.mBottomDividerEndPadding = -1;
        this.mActionDividerHeight = -1;
        this.mSeekBarInlineWidth = -1;
        this.mProgressBarInlineWidth = -1;
        this.mProgressBarStartPadding = -1;
        this.mProgressBarEndPadding = -1;
        this.mTextActionPadding = -1;
        this.mIconSize = -1;
        this.mDisableRecyclerViewItemAnimator = false;
        this.mSliceStyle = sliceStyle;
        this.mImageSize = context.getResources().getDimensionPixelSize(R$dimen.abc_slice_small_image_size);
    }

    private static Integer getOptionalColor(TypedArray a, int colorRes) {
        if (a.hasValue(colorRes)) {
            return Integer.valueOf(a.getColor(colorRes, 0));
        }
        return null;
    }

    public int getActionDividerHeight() {
        return this.mActionDividerHeight;
    }

    public int getBottomDividerEndPadding() {
        return this.mBottomDividerEndPadding;
    }

    public int getBottomDividerStartPadding() {
        return this.mBottomDividerStartPadding;
    }

    public int getContentEndPadding() {
        return this.mContentEndPadding;
    }

    public int getContentStartPadding() {
        return this.mContentStartPadding;
    }

    public boolean getDisableRecyclerViewItemAnimator() {
        return this.mDisableRecyclerViewItemAnimator;
    }

    public int getEndItemEndPadding() {
        return this.mEndItemEndPadding;
    }

    public int getEndItemStartPadding() {
        return this.mEndItemStartPadding;
    }

    public int getIconSize() {
        return this.mIconSize;
    }

    public int getImageSize() {
        return this.mImageSize;
    }

    public int getProgressBarEndPadding() {
        return this.mProgressBarEndPadding;
    }

    public int getProgressBarInlineWidth() {
        return this.mProgressBarInlineWidth;
    }

    public int getProgressBarStartPadding() {
        return this.mProgressBarStartPadding;
    }

    public int getSeekBarInlineWidth() {
        return this.mSeekBarInlineWidth;
    }

    public int getSubContentEndPadding() {
        return this.mSubContentEndPadding;
    }

    public int getSubContentStartPadding() {
        return this.mSubContentStartPadding;
    }

    public int getSubtitleColor() {
        Integer num = this.mSubtitleColor;
        return num != null ? num.intValue() : this.mSliceStyle.getSubtitleColor();
    }

    public int getTextActionPadding() {
        return this.mTextActionPadding;
    }

    public int getTintColor() {
        Integer num = this.mTintColor;
        return num != null ? num.intValue() : this.mSliceStyle.getTintColor();
    }

    public int getTitleColor() {
        Integer num = this.mTitleColor;
        return num != null ? num.intValue() : this.mSliceStyle.getTitleColor();
    }

    public int getTitleEndPadding() {
        return this.mTitleEndPadding;
    }

    public int getTitleItemEndPadding() {
        return this.mTitleItemEndPadding;
    }

    public int getTitleItemStartPadding() {
        return this.mTitleItemStartPadding;
    }

    public int getTitleStartPadding() {
        return this.mTitleStartPadding;
    }
}
