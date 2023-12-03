package androidx.slice.builders;

import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.util.Pair;
import androidx.slice.Slice;
import androidx.slice.SliceSpec;
import androidx.slice.SliceSpecs;
import androidx.slice.builders.impl.ListBuilderBasicImpl;
import androidx.slice.builders.impl.ListBuilderImpl;
import androidx.slice.builders.impl.TemplateBuilderImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public class ListBuilder extends TemplateSliceBuilder {
    private androidx.slice.builders.impl.ListBuilder mImpl;

    /* loaded from: classes.dex */
    public static class HeaderBuilder {
        private CharSequence mContentDescription;
        private int mLayoutDirection;
        private SliceAction mPrimaryAction;
        private CharSequence mSubtitle;
        private boolean mSubtitleLoading;
        private CharSequence mSummary;
        private boolean mSummaryLoading;
        private CharSequence mTitle;
        private boolean mTitleLoading;
        private final Uri mUri = null;

        public CharSequence getContentDescription() {
            return this.mContentDescription;
        }

        public int getLayoutDirection() {
            return this.mLayoutDirection;
        }

        public SliceAction getPrimaryAction() {
            return this.mPrimaryAction;
        }

        public CharSequence getSubtitle() {
            return this.mSubtitle;
        }

        public CharSequence getSummary() {
            return this.mSummary;
        }

        public CharSequence getTitle() {
            return this.mTitle;
        }

        public Uri getUri() {
            return this.mUri;
        }

        public boolean isSubtitleLoading() {
            return this.mSubtitleLoading;
        }

        public boolean isSummaryLoading() {
            return this.mSummaryLoading;
        }

        public boolean isTitleLoading() {
            return this.mTitleLoading;
        }

        public HeaderBuilder setPrimaryAction(SliceAction action) {
            this.mPrimaryAction = action;
            return this;
        }

        public HeaderBuilder setSubtitle(CharSequence subtitle) {
            return setSubtitle(subtitle, false);
        }

        public HeaderBuilder setSubtitle(CharSequence subtitle, boolean isLoading) {
            this.mSubtitle = subtitle;
            this.mSubtitleLoading = isLoading;
            return this;
        }

        public HeaderBuilder setTitle(CharSequence title) {
            return setTitle(title, false);
        }

        public HeaderBuilder setTitle(CharSequence title, boolean isLoading) {
            this.mTitle = title;
            this.mTitleLoading = isLoading;
            return this;
        }
    }

    /* loaded from: classes.dex */
    public static class InputRangeBuilder {
        private CharSequence mContentDescription;
        private PendingIntent mInputAction;
        private SliceAction mPrimaryAction;
        private CharSequence mSubtitle;
        private IconCompat mThumb;
        private CharSequence mTitle;
        private IconCompat mTitleIcon;
        private int mTitleImageMode;
        private boolean mTitleItemLoading;
        private int mMin = 0;
        private int mMax = 100;
        private int mValue = 0;
        private boolean mValueSet = false;
        private int mLayoutDirection = -1;
        private final List<Object> mEndItems = new ArrayList();
        private final List<Integer> mEndTypes = new ArrayList();
        private final List<Boolean> mEndLoads = new ArrayList();

        public CharSequence getContentDescription() {
            return this.mContentDescription;
        }

        public List<Object> getEndItems() {
            return this.mEndItems;
        }

        public List<Boolean> getEndLoads() {
            return this.mEndLoads;
        }

        public List<Integer> getEndTypes() {
            return this.mEndTypes;
        }

        public PendingIntent getInputAction() {
            return this.mInputAction;
        }

        public int getLayoutDirection() {
            return this.mLayoutDirection;
        }

        public int getMax() {
            return this.mMax;
        }

        public int getMin() {
            return this.mMin;
        }

        public SliceAction getPrimaryAction() {
            return this.mPrimaryAction;
        }

        public CharSequence getSubtitle() {
            return this.mSubtitle;
        }

        public IconCompat getThumb() {
            return this.mThumb;
        }

        public CharSequence getTitle() {
            return this.mTitle;
        }

        public IconCompat getTitleIcon() {
            return this.mTitleIcon;
        }

        public int getTitleImageMode() {
            return this.mTitleImageMode;
        }

        public int getValue() {
            return this.mValue;
        }

        public boolean isTitleItemLoading() {
            return this.mTitleItemLoading;
        }

        public boolean isValueSet() {
            return this.mValueSet;
        }

        public InputRangeBuilder setInputAction(PendingIntent action) {
            this.mInputAction = action;
            return this;
        }

        public InputRangeBuilder setMax(int max) {
            this.mMax = max;
            return this;
        }

        public InputRangeBuilder setMin(int min) {
            this.mMin = min;
            return this;
        }

        public InputRangeBuilder setPrimaryAction(SliceAction action) {
            this.mPrimaryAction = action;
            return this;
        }

        public InputRangeBuilder setSubtitle(CharSequence title) {
            this.mSubtitle = title;
            return this;
        }

        public InputRangeBuilder setTitle(CharSequence title) {
            this.mTitle = title;
            return this;
        }

        public InputRangeBuilder setTitleItem(IconCompat icon, int imageMode) {
            return setTitleItem(icon, imageMode, false);
        }

        public InputRangeBuilder setTitleItem(IconCompat icon, int imageMode, boolean isLoading) {
            this.mTitleIcon = icon;
            this.mTitleImageMode = imageMode;
            this.mTitleItemLoading = isLoading;
            return this;
        }

        public InputRangeBuilder setValue(int value) {
            this.mValueSet = true;
            this.mValue = value;
            return this;
        }
    }

    /* loaded from: classes.dex */
    public static class RangeBuilder {
    }

    /* loaded from: classes.dex */
    public static class RowBuilder {
        private CharSequence mContentDescription;
        private boolean mHasDefaultToggle;
        private boolean mHasEndActionOrToggle;
        private boolean mHasEndImage;
        private boolean mIsEndOfSection;
        private SliceAction mPrimaryAction;
        private CharSequence mSubtitle;
        private boolean mSubtitleLoading;
        private CharSequence mTitle;
        private SliceAction mTitleAction;
        private boolean mTitleActionLoading;
        private IconCompat mTitleIcon;
        private int mTitleImageMode;
        private boolean mTitleItemLoading;
        private boolean mTitleLoading;
        private long mTimeStamp = -1;
        private int mLayoutDirection = -1;
        private final List<Object> mEndItems = new ArrayList();
        private final List<Integer> mEndTypes = new ArrayList();
        private final List<Boolean> mEndLoads = new ArrayList();
        private final Uri mUri = null;

        public RowBuilder addEndItem(IconCompat icon, int imageMode) {
            return addEndItem(icon, imageMode, false);
        }

        public RowBuilder addEndItem(IconCompat icon, int imageMode, boolean isLoading) {
            if (this.mHasEndActionOrToggle) {
                throw new IllegalArgumentException("Trying to add an icon to end items when anaction has already been added. End items cannot have a mixture of actions and icons.");
            }
            this.mEndItems.add(new Pair(icon, Integer.valueOf(imageMode)));
            this.mEndTypes.add(1);
            this.mEndLoads.add(Boolean.valueOf(isLoading));
            this.mHasEndImage = true;
            return this;
        }

        public RowBuilder addEndItem(SliceAction action) {
            return addEndItem(action, false);
        }

        public RowBuilder addEndItem(SliceAction action, boolean isLoading) {
            if (this.mHasEndImage) {
                throw new IllegalArgumentException("Trying to add an action to end items when anicon has already been added. End items cannot have a mixture of actions and icons.");
            }
            if (this.mHasDefaultToggle) {
                throw new IllegalStateException("Only one non-custom toggle can be added in a single row. If you would like to include multiple toggles in a row, set a custom icon for each toggle.");
            }
            this.mEndItems.add(action);
            this.mEndTypes.add(2);
            this.mEndLoads.add(Boolean.valueOf(isLoading));
            this.mHasDefaultToggle = action.getImpl().isDefaultToggle();
            this.mHasEndActionOrToggle = true;
            return this;
        }

        public CharSequence getContentDescription() {
            return this.mContentDescription;
        }

        public List<Object> getEndItems() {
            return this.mEndItems;
        }

        public List<Boolean> getEndLoads() {
            return this.mEndLoads;
        }

        public List<Integer> getEndTypes() {
            return this.mEndTypes;
        }

        public int getLayoutDirection() {
            return this.mLayoutDirection;
        }

        public SliceAction getPrimaryAction() {
            return this.mPrimaryAction;
        }

        public CharSequence getSubtitle() {
            return this.mSubtitle;
        }

        public long getTimeStamp() {
            return this.mTimeStamp;
        }

        public CharSequence getTitle() {
            return this.mTitle;
        }

        public SliceAction getTitleAction() {
            return this.mTitleAction;
        }

        public IconCompat getTitleIcon() {
            return this.mTitleIcon;
        }

        public int getTitleImageMode() {
            return this.mTitleImageMode;
        }

        public Uri getUri() {
            return this.mUri;
        }

        public boolean isEndOfSection() {
            return this.mIsEndOfSection;
        }

        public boolean isSubtitleLoading() {
            return this.mSubtitleLoading;
        }

        public boolean isTitleActionLoading() {
            return this.mTitleActionLoading;
        }

        public boolean isTitleItemLoading() {
            return this.mTitleItemLoading;
        }

        public boolean isTitleLoading() {
            return this.mTitleLoading;
        }

        public RowBuilder setContentDescription(CharSequence description) {
            this.mContentDescription = description;
            return this;
        }

        public RowBuilder setPrimaryAction(SliceAction action) {
            this.mPrimaryAction = action;
            return this;
        }

        public RowBuilder setSubtitle(CharSequence subtitle) {
            return setSubtitle(subtitle, false);
        }

        public RowBuilder setSubtitle(CharSequence subtitle, boolean isLoading) {
            this.mSubtitle = subtitle;
            this.mSubtitleLoading = isLoading;
            return this;
        }

        public RowBuilder setTitle(CharSequence title) {
            return setTitle(title, false);
        }

        public RowBuilder setTitle(CharSequence title, boolean isLoading) {
            this.mTitle = title;
            this.mTitleLoading = isLoading;
            return this;
        }

        public RowBuilder setTitleItem(IconCompat icon, int imageMode) {
            return setTitleItem(icon, imageMode, false);
        }

        public RowBuilder setTitleItem(IconCompat icon, int imageMode, boolean isLoading) {
            this.mTitleAction = null;
            this.mTitleIcon = icon;
            this.mTitleImageMode = imageMode;
            this.mTitleItemLoading = isLoading;
            return this;
        }

        public RowBuilder setTitleItem(SliceAction action) {
            return setTitleItem(action, false);
        }

        public RowBuilder setTitleItem(SliceAction action, boolean isLoading) {
            this.mTitleAction = action;
            this.mTitleIcon = null;
            this.mTitleImageMode = 0;
            this.mTitleActionLoading = isLoading;
            return this;
        }
    }

    public ListBuilder(Context context, Uri uri, long ttl) {
        super(context, uri);
        this.mImpl.setTtl(ttl);
    }

    public ListBuilder addAction(SliceAction action) {
        this.mImpl.addAction(action);
        return this;
    }

    public ListBuilder addInputRange(InputRangeBuilder b) {
        this.mImpl.addInputRange(b);
        return this;
    }

    public ListBuilder addRow(RowBuilder builder) {
        this.mImpl.addRow(builder);
        return this;
    }

    public Slice build() {
        return ((TemplateBuilderImpl) this.mImpl).build();
    }

    @Override // androidx.slice.builders.TemplateSliceBuilder
    protected TemplateBuilderImpl selectImpl() {
        SliceSpec sliceSpec = SliceSpecs.LIST_V2;
        if (checkCompatible(sliceSpec)) {
            return new ListBuilderImpl(getBuilder(), sliceSpec, getClock());
        }
        SliceSpec sliceSpec2 = SliceSpecs.LIST;
        if (checkCompatible(sliceSpec2)) {
            return new ListBuilderImpl(getBuilder(), sliceSpec2, getClock());
        }
        SliceSpec sliceSpec3 = SliceSpecs.BASIC;
        if (checkCompatible(sliceSpec3)) {
            return new ListBuilderBasicImpl(getBuilder(), sliceSpec3);
        }
        return null;
    }

    public ListBuilder setAccentColor(int color) {
        this.mImpl.setColor(color);
        return this;
    }

    public ListBuilder setHeader(HeaderBuilder builder) {
        this.mImpl.setHeader(builder);
        return this;
    }

    @Override // androidx.slice.builders.TemplateSliceBuilder
    void setImpl(TemplateBuilderImpl impl) {
        this.mImpl = (androidx.slice.builders.impl.ListBuilder) impl;
    }

    public ListBuilder setIsError(boolean isError) {
        this.mImpl.setIsError(isError);
        return this;
    }

    public ListBuilder setKeywords(final Set<String> keywords) {
        this.mImpl.setKeywords(keywords);
        return this;
    }
}
