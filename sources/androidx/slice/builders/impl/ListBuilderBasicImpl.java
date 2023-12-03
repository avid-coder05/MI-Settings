package androidx.slice.builders.impl;

import android.os.Bundle;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import androidx.slice.SliceSpec;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import java.util.Iterator;
import java.util.Set;

/* loaded from: classes.dex */
public class ListBuilderBasicImpl extends TemplateBuilderImpl implements ListBuilder {
    private Bundle mHostExtras;
    private IconCompat mIconCompat;
    boolean mIsError;
    private Set<String> mKeywords;
    private SliceAction mSliceAction;
    private CharSequence mSubtitle;
    private CharSequence mTitle;

    public ListBuilderBasicImpl(Slice.Builder b, SliceSpec spec) {
        super(b, spec);
    }

    @Override // androidx.slice.builders.impl.ListBuilder
    public void addAction(SliceAction action) {
    }

    @Override // androidx.slice.builders.impl.ListBuilder
    public void addInputRange(ListBuilder.InputRangeBuilder builder) {
        if (this.mTitle == null && builder.getTitle() != null) {
            this.mTitle = builder.getTitle();
        }
        if (this.mSubtitle == null && builder.getSubtitle() != null) {
            this.mSubtitle = builder.getSubtitle();
        }
        if (this.mSliceAction == null && builder.getPrimaryAction() != null) {
            this.mSliceAction = builder.getPrimaryAction();
        }
        if (this.mIconCompat != null || builder.getThumb() == null) {
            return;
        }
        this.mIconCompat = builder.getThumb();
    }

    @Override // androidx.slice.builders.impl.ListBuilder
    public void addRow(ListBuilder.RowBuilder builder) {
        if (this.mTitle == null && builder.getTitle() != null) {
            this.mTitle = builder.getTitle();
        }
        if (this.mSubtitle == null && builder.getSubtitle() != null) {
            this.mSubtitle = builder.getSubtitle();
        }
        if (this.mSliceAction == null && builder.getPrimaryAction() != null) {
            this.mSliceAction = builder.getPrimaryAction();
        }
        if (this.mSliceAction == null && builder.getTitleAction() != null) {
            this.mSliceAction = builder.getTitleAction();
        }
        if (this.mIconCompat != null || builder.getTitleIcon() == null) {
            return;
        }
        this.mIconCompat = builder.getTitleIcon();
    }

    @Override // androidx.slice.builders.impl.TemplateBuilderImpl
    public void apply(Slice.Builder builder) {
        if (this.mIsError) {
            builder.addHints("error");
        }
        if (this.mKeywords != null) {
            Slice.Builder builder2 = new Slice.Builder(getBuilder());
            Iterator<String> it = this.mKeywords.iterator();
            while (it.hasNext()) {
                builder2.addText(it.next(), (String) null, new String[0]);
            }
            builder.addSubSlice(builder2.addHints("keywords").build());
        }
        Slice.Builder builder3 = new Slice.Builder(getBuilder());
        SliceAction sliceAction = this.mSliceAction;
        if (sliceAction != null) {
            if (this.mTitle == null && sliceAction.getTitle() != null) {
                this.mTitle = this.mSliceAction.getTitle();
            }
            if (this.mIconCompat == null && this.mSliceAction.getIcon() != null) {
                this.mIconCompat = this.mSliceAction.getIcon();
            }
            this.mSliceAction.setPrimaryAction(builder3);
        }
        CharSequence charSequence = this.mTitle;
        if (charSequence != null) {
            builder3.addItem(new SliceItem(charSequence, "text", (String) null, new String[]{"title"}));
        }
        CharSequence charSequence2 = this.mSubtitle;
        if (charSequence2 != null) {
            builder3.addItem(new SliceItem(charSequence2, "text", (String) null, new String[0]));
        }
        IconCompat iconCompat = this.mIconCompat;
        if (iconCompat != null) {
            builder.addIcon(iconCompat, (String) null, "title");
        }
        Bundle bundle = this.mHostExtras;
        if (bundle != null) {
            builder3.addItem(new SliceItem(bundle, "bundle", "host_extras", new String[0]));
        }
        builder.addSubSlice(builder3.build());
    }

    @Override // androidx.slice.builders.impl.ListBuilder
    public void setColor(int color) {
        getBuilder().addInt(color, "color", new String[0]);
    }

    @Override // androidx.slice.builders.impl.ListBuilder
    public void setHeader(ListBuilder.HeaderBuilder builder) {
        if (builder.getTitle() != null) {
            this.mTitle = builder.getTitle();
        }
        if (builder.getSubtitle() != null) {
            this.mSubtitle = builder.getSubtitle();
        }
        if (builder.getPrimaryAction() != null) {
            this.mSliceAction = builder.getPrimaryAction();
        }
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
