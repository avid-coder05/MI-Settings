package androidx.slice.builders.impl;

import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import java.util.Set;

/* loaded from: classes.dex */
public interface ListBuilder {
    void addAction(SliceAction action);

    void addInputRange(ListBuilder.InputRangeBuilder builder);

    void addRow(ListBuilder.RowBuilder impl);

    void setColor(int color);

    void setHeader(ListBuilder.HeaderBuilder impl);

    void setIsError(boolean isError);

    void setKeywords(Set<String> keywords);

    void setTtl(long ttl);
}
