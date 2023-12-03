package androidx.slice.core;

import android.text.TextUtils;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/* loaded from: classes.dex */
public class SliceQuery {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface Filter<T> {
        boolean filter(T input);
    }

    static boolean checkFormat(SliceItem item, String format) {
        return format == null || format.equals(item.getFormat());
    }

    static boolean checkSubtype(SliceItem item, String subtype) {
        return subtype == null || subtype.equals(item.getSubType());
    }

    public static SliceItem find(Slice s, String format) {
        return find(s, format, (String[]) null, (String[]) null);
    }

    public static SliceItem find(Slice s, String format, String hints, String nonHints) {
        return find(s, format, new String[]{hints}, new String[]{nonHints});
    }

    public static SliceItem find(Slice s, final String format, final String[] hints, final String[] nonHints) {
        if (s == null) {
            return null;
        }
        return findSliceItem(toQueue(s), new Filter<SliceItem>() { // from class: androidx.slice.core.SliceQuery.4
            @Override // androidx.slice.core.SliceQuery.Filter
            public boolean filter(SliceItem item) {
                return SliceQuery.checkFormat(item, format) && SliceQuery.hasHints(item, hints) && !SliceQuery.hasAnyHints(item, nonHints);
            }
        });
    }

    public static SliceItem find(SliceItem s, String format) {
        return find(s, format, (String[]) null, (String[]) null);
    }

    public static SliceItem find(SliceItem s, String format, String hints, String nonHints) {
        return find(s, format, new String[]{hints}, new String[]{nonHints});
    }

    public static SliceItem find(SliceItem s, final String format, final String[] hints, final String[] nonHints) {
        if (s == null) {
            return null;
        }
        return findSliceItem(toQueue(s), new Filter<SliceItem>() { // from class: androidx.slice.core.SliceQuery.7
            @Override // androidx.slice.core.SliceQuery.Filter
            public boolean filter(SliceItem item) {
                return SliceQuery.checkFormat(item, format) && SliceQuery.hasHints(item, hints) && !SliceQuery.hasAnyHints(item, nonHints);
            }
        });
    }

    public static List<SliceItem> findAll(Slice s, final String format, final String[] hints, final String[] nonHints) {
        ArrayList arrayList = new ArrayList();
        findAll(toQueue(s), new Filter<SliceItem>() { // from class: androidx.slice.core.SliceQuery.2
            @Override // androidx.slice.core.SliceQuery.Filter
            public boolean filter(SliceItem item) {
                return SliceQuery.checkFormat(item, format) && SliceQuery.hasHints(item, hints) && !SliceQuery.hasAnyHints(item, nonHints);
            }
        }, arrayList);
        return arrayList;
    }

    public static List<SliceItem> findAll(SliceItem s, String format) {
        return findAll(s, format, (String[]) null, (String[]) null);
    }

    public static List<SliceItem> findAll(SliceItem s, String format, String hints, String nonHints) {
        return findAll(s, format, new String[]{hints}, new String[]{nonHints});
    }

    public static List<SliceItem> findAll(SliceItem s, final String format, final String[] hints, final String[] nonHints) {
        ArrayList arrayList = new ArrayList();
        findAll(toQueue(s), new Filter<SliceItem>() { // from class: androidx.slice.core.SliceQuery.3
            @Override // androidx.slice.core.SliceQuery.Filter
            public boolean filter(SliceItem item) {
                return SliceQuery.checkFormat(item, format) && SliceQuery.hasHints(item, hints) && !SliceQuery.hasAnyHints(item, nonHints);
            }
        }, arrayList);
        return arrayList;
    }

    private static void findAll(final Deque<SliceItem> items, Filter<SliceItem> f, List<SliceItem> out) {
        while (!items.isEmpty()) {
            SliceItem poll = items.poll();
            if (f.filter(poll)) {
                out.add(poll);
            }
            if ("slice".equals(poll.getFormat()) || "action".equals(poll.getFormat())) {
                Collections.addAll(items, poll.getSlice().getItemArray());
            }
        }
    }

    private static SliceItem findSliceItem(final Deque<SliceItem> items, Filter<SliceItem> f) {
        while (!items.isEmpty()) {
            SliceItem poll = items.poll();
            if (f.filter(poll)) {
                return poll;
            }
            if ("slice".equals(poll.getFormat()) || "action".equals(poll.getFormat())) {
                Collections.addAll(items, poll.getSlice().getItemArray());
            }
        }
        return null;
    }

    public static SliceItem findSubtype(Slice s, final String format, final String subtype) {
        if (s == null) {
            return null;
        }
        return findSliceItem(toQueue(s), new Filter<SliceItem>() { // from class: androidx.slice.core.SliceQuery.5
            @Override // androidx.slice.core.SliceQuery.Filter
            public boolean filter(SliceItem item) {
                return SliceQuery.checkFormat(item, format) && SliceQuery.checkSubtype(item, subtype);
            }
        });
    }

    public static SliceItem findSubtype(SliceItem s, final String format, final String subtype) {
        if (s == null) {
            return null;
        }
        return findSliceItem(toQueue(s), new Filter<SliceItem>() { // from class: androidx.slice.core.SliceQuery.6
            @Override // androidx.slice.core.SliceQuery.Filter
            public boolean filter(SliceItem item) {
                return SliceQuery.checkFormat(item, format) && SliceQuery.checkSubtype(item, subtype);
            }
        });
    }

    public static SliceItem findTopLevelItem(Slice s, final String format, final String subtype, final String[] hints, final String[] nonHints) {
        for (SliceItem sliceItem : s.getItemArray()) {
            if (checkFormat(sliceItem, format) && checkSubtype(sliceItem, subtype) && hasHints(sliceItem, hints) && !hasAnyHints(sliceItem, nonHints)) {
                return sliceItem;
            }
        }
        return null;
    }

    public static boolean hasAnyHints(SliceItem item, String... hints) {
        if (hints == null) {
            return false;
        }
        for (String str : hints) {
            if (item.hasHint(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasHints(SliceItem item, String... hints) {
        if (hints == null) {
            return true;
        }
        for (String str : hints) {
            if (!TextUtils.isEmpty(str) && !item.hasHint(str)) {
                return false;
            }
        }
        return true;
    }

    private static Deque<SliceItem> toQueue(Slice item) {
        ArrayDeque arrayDeque = new ArrayDeque();
        Collections.addAll(arrayDeque, item.getItemArray());
        return arrayDeque;
    }

    private static Deque<SliceItem> toQueue(SliceItem item) {
        ArrayDeque arrayDeque = new ArrayDeque();
        arrayDeque.add(item);
        return arrayDeque;
    }
}
