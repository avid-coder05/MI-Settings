package androidx.slice.builders.impl;

import android.app.PendingIntent;
import android.os.Bundle;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.util.Pair;
import androidx.slice.Clock;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import androidx.slice.SliceSpec;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import androidx.slice.core.SliceQuery;
import com.android.settings.search.FunctionColumns;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import miui.yellowpage.Tag;

/* loaded from: classes.dex */
public class ListBuilderImpl extends TemplateBuilderImpl implements ListBuilder {
    private boolean mFirstRowChecked;
    private boolean mFirstRowHasText;
    private Bundle mHostExtras;
    private boolean mIsError;
    private boolean mIsFirstRowTypeValid;
    private Set<String> mKeywords;
    private List<Slice> mSliceActions;
    private Slice mSliceHeader;

    /* loaded from: classes.dex */
    public static class HeaderBuilderImpl extends TemplateBuilderImpl {
        private CharSequence mContentDescr;
        private SliceAction mPrimaryAction;
        private SliceItem mSubtitleItem;
        private SliceItem mSummaryItem;
        private SliceItem mTitleItem;

        HeaderBuilderImpl(ListBuilderImpl parent) {
            super(parent.createChildBuilder(), null);
        }

        private void setContentDescription(CharSequence description) {
            this.mContentDescr = description;
        }

        private void setLayoutDirection(int layoutDirection) {
            getBuilder().addInt(layoutDirection, "layout_direction", new String[0]);
        }

        private void setPrimaryAction(SliceAction action) {
            this.mPrimaryAction = action;
        }

        private void setSubtitle(CharSequence subtitle, boolean isLoading) {
            SliceItem sliceItem = new SliceItem(subtitle, "text", (String) null, new String[0]);
            this.mSubtitleItem = sliceItem;
            if (isLoading) {
                sliceItem.addHint("partial");
            }
        }

        private void setSummary(CharSequence summarySubtitle, boolean isLoading) {
            SliceItem sliceItem = new SliceItem(summarySubtitle, "text", (String) null, new String[]{FunctionColumns.SUMMARY});
            this.mSummaryItem = sliceItem;
            if (isLoading) {
                sliceItem.addHint("partial");
            }
        }

        private void setTitle(CharSequence title, boolean isLoading) {
            SliceItem sliceItem = new SliceItem(title, "text", (String) null, new String[]{"title"});
            this.mTitleItem = sliceItem;
            if (isLoading) {
                sliceItem.addHint("partial");
            }
        }

        @Override // androidx.slice.builders.impl.TemplateBuilderImpl
        public void apply(Slice.Builder b) {
            SliceItem sliceItem = this.mTitleItem;
            if (sliceItem != null) {
                b.addItem(sliceItem);
            }
            SliceItem sliceItem2 = this.mSubtitleItem;
            if (sliceItem2 != null) {
                b.addItem(sliceItem2);
            }
            SliceItem sliceItem3 = this.mSummaryItem;
            if (sliceItem3 != null) {
                b.addItem(sliceItem3);
            }
            CharSequence charSequence = this.mContentDescr;
            if (charSequence != null) {
                b.addText(charSequence, "content_description", new String[0]);
            }
            SliceAction sliceAction = this.mPrimaryAction;
            if (sliceAction != null) {
                sliceAction.setPrimaryAction(b);
            }
            if (this.mSubtitleItem == null && this.mTitleItem == null) {
                throw new IllegalStateException("Header requires a title or subtitle to be set.");
            }
        }

        void fillFrom(ListBuilder.HeaderBuilder builder) {
            if (builder.getUri() != null) {
                setBuilder(new Slice.Builder(builder.getUri()));
            }
            setPrimaryAction(builder.getPrimaryAction());
            if (builder.getLayoutDirection() != -1) {
                setLayoutDirection(builder.getLayoutDirection());
            }
            if (builder.getTitle() != null || builder.isTitleLoading()) {
                setTitle(builder.getTitle(), builder.isTitleLoading());
            }
            if (builder.getSubtitle() != null || builder.isSubtitleLoading()) {
                setSubtitle(builder.getSubtitle(), builder.isSubtitleLoading());
            }
            if (builder.getSummary() != null || builder.isSummaryLoading()) {
                setSummary(builder.getSummary(), builder.isSummaryLoading());
            }
            if (builder.getContentDescription() != null) {
                setContentDescription(builder.getContentDescription());
            }
        }
    }

    /* loaded from: classes.dex */
    public static class InputRangeBuilderImpl extends RangeBuilderImpl {
        private final PendingIntent mAction;
        private final ArrayList<Slice> mEndItems;
        private Slice mStartItem;
        private final IconCompat mThumb;

        InputRangeBuilderImpl(Slice.Builder sb, ListBuilder.InputRangeBuilder builder) {
            super(sb, null);
            this.mEndItems = new ArrayList<>();
            this.mValueSet = builder.isValueSet();
            this.mMin = builder.getMin();
            this.mMax = builder.getMax();
            this.mValue = builder.getValue();
            this.mTitle = builder.getTitle();
            this.mSubtitle = builder.getSubtitle();
            this.mContentDescr = builder.getContentDescription();
            this.mPrimaryAction = builder.getPrimaryAction();
            this.mLayoutDir = builder.getLayoutDirection();
            this.mAction = builder.getInputAction();
            this.mThumb = builder.getThumb();
            if (builder.getTitleIcon() != null) {
                setTitleItem(builder.getTitleIcon(), builder.getTitleImageMode(), builder.isTitleItemLoading());
            }
            List<Object> endItems = builder.getEndItems();
            List<Integer> endTypes = builder.getEndTypes();
            List<Boolean> endLoads = builder.getEndLoads();
            for (int i = 0; i < endItems.size(); i++) {
                if (endTypes.get(i).intValue() == 2) {
                    addEndItem((SliceAction) endItems.get(i), endLoads.get(i).booleanValue());
                }
            }
        }

        private void addEndItem(SliceAction action, boolean isLoading) {
            Slice.Builder builder = new Slice.Builder(getBuilder());
            if (isLoading) {
                builder.addHints("partial");
            }
            this.mEndItems.add(action.buildSlice(builder));
        }

        @Override // androidx.slice.builders.impl.ListBuilderImpl.RangeBuilderImpl, androidx.slice.builders.impl.TemplateBuilderImpl
        public void apply(Slice.Builder builder) {
            if (this.mAction == null) {
                throw new IllegalStateException("Input ranges must have an associated action.");
            }
            Slice.Builder builder2 = new Slice.Builder(builder);
            super.apply(builder2);
            IconCompat iconCompat = this.mThumb;
            if (iconCompat != null) {
                builder2.addIcon(iconCompat, (String) null, new String[0]);
            }
            builder.addAction(this.mAction, builder2.build(), "range").addHints("list_item");
            Slice slice = this.mStartItem;
            if (slice != null) {
                builder.addSubSlice(slice);
            }
            for (int i = 0; i < this.mEndItems.size(); i++) {
                builder.addSubSlice(this.mEndItems.get(i));
            }
        }

        void setTitleItem(IconCompat icon, int imageMode, boolean isLoading) {
            Slice.Builder addIcon = new Slice.Builder(getBuilder()).addIcon(icon, (String) null, parseImageMode(imageMode, isLoading));
            if (isLoading) {
                addIcon.addHints("partial");
            }
            this.mStartItem = addIcon.addHints("title").build();
        }
    }

    /* loaded from: classes.dex */
    public static class RangeBuilderImpl extends TemplateBuilderImpl {
        protected CharSequence mContentDescr;
        protected int mLayoutDir;
        protected int mMax;
        protected int mMin;
        private int mMode;
        protected SliceAction mPrimaryAction;
        private Slice mStartItem;
        protected CharSequence mSubtitle;
        protected CharSequence mTitle;
        protected int mValue;
        protected boolean mValueSet;

        RangeBuilderImpl(Slice.Builder sb, ListBuilder.RangeBuilder builder) {
            super(sb, null);
            this.mMin = 0;
            this.mMax = 100;
            this.mValue = 0;
            this.mValueSet = false;
            this.mLayoutDir = -1;
            this.mMode = 0;
        }

        @Override // androidx.slice.builders.impl.TemplateBuilderImpl
        public void apply(Slice.Builder builder) {
            int i;
            if (!this.mValueSet) {
                this.mValue = this.mMin;
            }
            int i2 = this.mMin;
            int i3 = this.mValue;
            if (i2 > i3 || i3 > (i = this.mMax) || i2 >= i) {
                throw new IllegalArgumentException("Invalid range values, min=" + this.mMin + ", value=" + this.mValue + ", max=" + this.mMax + " ensure value falls within (min, max) and min < max.");
            }
            Slice slice = this.mStartItem;
            if (slice != null) {
                builder.addSubSlice(slice);
            }
            CharSequence charSequence = this.mTitle;
            if (charSequence != null) {
                builder.addText(charSequence, (String) null, "title");
            }
            CharSequence charSequence2 = this.mSubtitle;
            if (charSequence2 != null) {
                builder.addText(charSequence2, (String) null, new String[0]);
            }
            CharSequence charSequence3 = this.mContentDescr;
            if (charSequence3 != null) {
                builder.addText(charSequence3, "content_description", new String[0]);
            }
            SliceAction sliceAction = this.mPrimaryAction;
            if (sliceAction != null) {
                sliceAction.setPrimaryAction(builder);
            }
            int i4 = this.mLayoutDir;
            if (i4 != -1) {
                builder.addInt(i4, "layout_direction", new String[0]);
            }
            builder.addHints("list_item").addInt(this.mMin, "min", new String[0]).addInt(this.mMax, "max", new String[0]).addInt(this.mValue, "value", new String[0]).addInt(this.mMode, "range_mode", new String[0]);
        }

        boolean hasText() {
            return (this.mTitle == null && this.mSubtitle == null) ? false : true;
        }
    }

    /* loaded from: classes.dex */
    public static class RowBuilderImpl extends TemplateBuilderImpl {
        private CharSequence mContentDescr;
        private final ArrayList<Slice> mEndItems;
        private boolean mIsEndOfSection;
        private SliceAction mPrimaryAction;
        private Slice mStartItem;
        private SliceItem mSubtitleItem;
        private SliceItem mTitleItem;

        RowBuilderImpl(Slice.Builder builder) {
            super(builder, null);
            this.mEndItems = new ArrayList<>();
        }

        private void addEndItem(final IconCompat icon, final int imageMode, final boolean isLoading) {
            Slice.Builder addIcon = new Slice.Builder(getBuilder()).addIcon(icon, (String) null, parseImageMode(imageMode, isLoading));
            if (isLoading) {
                addIcon.addHints("partial");
            }
            this.mEndItems.add(addIcon.build());
        }

        private void addEndItem(final SliceAction action, final boolean isLoading) {
            Slice.Builder builder = new Slice.Builder(getBuilder());
            if (isLoading) {
                builder.addHints("partial");
            }
            this.mEndItems.add(action.buildSlice(builder));
        }

        private void setContentDescription(CharSequence description) {
            this.mContentDescr = description;
        }

        private void setLayoutDirection(int layoutDirection) {
            getBuilder().addInt(layoutDirection, "layout_direction", new String[0]);
        }

        private void setPrimaryAction(SliceAction action) {
            this.mPrimaryAction = action;
        }

        private void setSubtitle(final CharSequence subtitle, final boolean isLoading) {
            SliceItem sliceItem = new SliceItem(subtitle, "text", (String) null, new String[0]);
            this.mSubtitleItem = sliceItem;
            if (isLoading) {
                sliceItem.addHint("partial");
            }
        }

        private void setTitle(final CharSequence title, final boolean isLoading) {
            SliceItem sliceItem = new SliceItem(title, "text", (String) null, new String[]{"title"});
            this.mTitleItem = sliceItem;
            if (isLoading) {
                sliceItem.addHint("partial");
            }
        }

        private void setTitleItem(long timeStamp) {
            this.mStartItem = new Slice.Builder(getBuilder()).addTimestamp(timeStamp, null, new String[0]).addHints("title").build();
        }

        private void setTitleItem(final IconCompat icon, final int imageMode, final boolean isLoading) {
            Slice.Builder addIcon = new Slice.Builder(getBuilder()).addIcon(icon, (String) null, parseImageMode(imageMode, isLoading));
            if (isLoading) {
                addIcon.addHints("partial");
            }
            this.mStartItem = addIcon.addHints("title").build();
        }

        private void setTitleItem(final SliceAction action, final boolean isLoading) {
            Slice.Builder addHints = new Slice.Builder(getBuilder()).addHints("title");
            if (isLoading) {
                addHints.addHints("partial");
            }
            this.mStartItem = action.buildSlice(addHints);
        }

        protected void addEndItem(long timeStamp) {
            this.mEndItems.add(new Slice.Builder(getBuilder()).addTimestamp(timeStamp, null, new String[0]).build());
        }

        @Override // androidx.slice.builders.impl.TemplateBuilderImpl
        public void apply(Slice.Builder b) {
            Slice slice = this.mStartItem;
            if (slice != null) {
                b.addSubSlice(slice);
            }
            SliceItem sliceItem = this.mTitleItem;
            if (sliceItem != null) {
                b.addItem(sliceItem);
            }
            SliceItem sliceItem2 = this.mSubtitleItem;
            if (sliceItem2 != null) {
                b.addItem(sliceItem2);
            }
            for (int i = 0; i < this.mEndItems.size(); i++) {
                b.addSubSlice(this.mEndItems.get(i));
            }
            CharSequence charSequence = this.mContentDescr;
            if (charSequence != null) {
                b.addText(charSequence, "content_description", new String[0]);
            }
            SliceAction sliceAction = this.mPrimaryAction;
            if (sliceAction != null) {
                sliceAction.setPrimaryAction(b);
            }
        }

        void fillFrom(ListBuilder.RowBuilder builder) {
            if (builder.getUri() != null) {
                setBuilder(new Slice.Builder(builder.getUri()));
            }
            setPrimaryAction(builder.getPrimaryAction());
            this.mIsEndOfSection = builder.isEndOfSection();
            if (builder.getLayoutDirection() != -1) {
                setLayoutDirection(builder.getLayoutDirection());
            }
            if (builder.getTitleAction() != null || builder.isTitleActionLoading()) {
                setTitleItem(builder.getTitleAction(), builder.isTitleActionLoading());
            } else if (builder.getTitleIcon() != null || builder.isTitleItemLoading()) {
                setTitleItem(builder.getTitleIcon(), builder.getTitleImageMode(), builder.isTitleItemLoading());
            } else if (builder.getTimeStamp() != -1) {
                setTitleItem(builder.getTimeStamp());
            }
            if (builder.getTitle() != null || builder.isTitleLoading()) {
                setTitle(builder.getTitle(), builder.isTitleLoading());
            }
            if (builder.getSubtitle() != null || builder.isSubtitleLoading()) {
                setSubtitle(builder.getSubtitle(), builder.isSubtitleLoading());
            }
            if (builder.getContentDescription() != null) {
                setContentDescription(builder.getContentDescription());
            }
            List<Object> endItems = builder.getEndItems();
            List<Integer> endTypes = builder.getEndTypes();
            List<Boolean> endLoads = builder.getEndLoads();
            for (int i = 0; i < endItems.size(); i++) {
                int intValue = endTypes.get(i).intValue();
                if (intValue == 0) {
                    addEndItem(((Long) endItems.get(i)).longValue());
                } else if (intValue == 1) {
                    Pair pair = (Pair) endItems.get(i);
                    addEndItem((IconCompat) pair.first, ((Integer) pair.second).intValue(), endLoads.get(i).booleanValue());
                } else if (intValue == 2) {
                    addEndItem((SliceAction) endItems.get(i), endLoads.get(i).booleanValue());
                }
            }
        }

        boolean hasText() {
            return (this.mTitleItem == null && this.mSubtitleItem == null) ? false : true;
        }

        public boolean isEndOfSection() {
            return this.mIsEndOfSection;
        }
    }

    public ListBuilderImpl(final Slice.Builder b, final SliceSpec spec, final Clock clock) {
        super(b, spec, clock);
    }

    private void checkRow(boolean isTypeValid, boolean hasText) {
        if (this.mFirstRowChecked) {
            return;
        }
        this.mFirstRowChecked = true;
        this.mIsFirstRowTypeValid = isTypeValid;
        this.mFirstRowHasText = hasText;
    }

    @Override // androidx.slice.builders.impl.ListBuilder
    public void addAction(SliceAction action) {
        if (this.mSliceActions == null) {
            this.mSliceActions = new ArrayList();
        }
        this.mSliceActions.add(action.buildSlice(new Slice.Builder(getBuilder()).addHints(Tag.TagServicesData.SERVICE_ACTIONS)));
    }

    @Override // androidx.slice.builders.impl.ListBuilder
    public void addInputRange(final ListBuilder.InputRangeBuilder builder) {
        InputRangeBuilderImpl inputRangeBuilderImpl = new InputRangeBuilderImpl(createChildBuilder(), builder);
        checkRow(true, inputRangeBuilderImpl.hasText());
        getBuilder().addSubSlice(inputRangeBuilderImpl.build(), "range");
    }

    @Override // androidx.slice.builders.impl.ListBuilder
    public void addRow(ListBuilder.RowBuilder builder) {
        RowBuilderImpl rowBuilderImpl = new RowBuilderImpl(createChildBuilder());
        rowBuilderImpl.fillFrom(builder);
        checkRow(true, rowBuilderImpl.hasText());
        addRow(rowBuilderImpl);
    }

    public void addRow(RowBuilderImpl builder) {
        checkRow(true, builder.hasText());
        builder.getBuilder().addHints("list_item");
        if (builder.isEndOfSection()) {
            builder.getBuilder().addHints("end_of_section");
        }
        getBuilder().addSubSlice(builder.build());
    }

    @Override // androidx.slice.builders.impl.TemplateBuilderImpl
    public void apply(final Slice.Builder builder) {
        builder.addLong(getClock().currentTimeMillis(), "millis", "last_updated");
        Slice slice = this.mSliceHeader;
        if (slice != null) {
            builder.addSubSlice(slice);
        }
        if (this.mSliceActions != null) {
            Slice.Builder builder2 = new Slice.Builder(builder);
            for (int i = 0; i < this.mSliceActions.size(); i++) {
                builder2.addSubSlice(this.mSliceActions.get(i));
            }
            builder.addSubSlice(builder2.addHints(Tag.TagServicesData.SERVICE_ACTIONS).build());
        }
        if (this.mIsError) {
            builder.addHints("error");
        }
        if (this.mKeywords != null) {
            Slice.Builder builder3 = new Slice.Builder(getBuilder());
            Iterator<String> it = this.mKeywords.iterator();
            while (it.hasNext()) {
                builder3.addText(it.next(), (String) null, new String[0]);
            }
            getBuilder().addSubSlice(builder3.addHints("keywords").build());
        }
        Bundle bundle = this.mHostExtras;
        if (bundle != null) {
            builder.addItem(new SliceItem(bundle, "bundle", "host_extras", new String[0]));
        }
    }

    @Override // androidx.slice.builders.impl.TemplateBuilderImpl
    public Slice build() {
        Slice build = super.build();
        boolean z = SliceQuery.find(build, (String) null, "partial", (String) null) != null;
        boolean z2 = SliceQuery.find(build, "slice", "list_item", (String) null) == null;
        String[] strArr = {"shortcut", "title"};
        SliceItem find = SliceQuery.find(build, "action", strArr, (String[]) null);
        List<SliceItem> findAll = SliceQuery.findAll(build, "slice", strArr, (String[]) null);
        if (z || z2 || find != null || !(findAll == null || findAll.isEmpty())) {
            boolean z3 = this.mFirstRowChecked;
            if (!z3 || this.mIsFirstRowTypeValid) {
                if (!z3 || this.mFirstRowHasText) {
                    return build;
                }
                throw new IllegalStateException("A slice requires the first row to have some text.");
            }
            throw new IllegalStateException("A slice cannot have the first row be constructed from a GridRowBuilder, consider using #setHeader.");
        }
        throw new IllegalStateException("A slice requires a primary action; ensure one of your builders has called #setPrimaryAction with a valid SliceAction.");
    }

    @Override // androidx.slice.builders.impl.ListBuilder
    public void setColor(int color) {
        getBuilder().addInt(color, "color", new String[0]);
    }

    @Override // androidx.slice.builders.impl.ListBuilder
    public void setHeader(ListBuilder.HeaderBuilder builder) {
        this.mIsFirstRowTypeValid = true;
        this.mFirstRowHasText = true;
        this.mFirstRowChecked = true;
        HeaderBuilderImpl headerBuilderImpl = new HeaderBuilderImpl(this);
        headerBuilderImpl.fillFrom(builder);
        this.mSliceHeader = headerBuilderImpl.build();
    }

    @Override // androidx.slice.builders.impl.ListBuilder
    public void setIsError(boolean isError) {
        this.mIsError = isError;
    }

    @Override // androidx.slice.builders.impl.ListBuilder
    public void setKeywords(Set<String> keywords) {
        this.mKeywords = keywords;
    }

    @Override // androidx.slice.builders.impl.ListBuilder
    public void setTtl(long ttl) {
        getBuilder().addTimestamp(ttl != -1 ? getClock().currentTimeMillis() + ttl : -1L, "millis", "ttl");
    }
}
