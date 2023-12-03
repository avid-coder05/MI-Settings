package androidx.slice.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.RecyclerView;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceAction;
import androidx.slice.core.SliceQuery;
import androidx.slice.view.R$layout;
import androidx.slice.widget.SliceActionView;
import androidx.slice.widget.SliceView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public class SliceAdapter extends RecyclerView.Adapter<SliceViewHolder> implements SliceActionView.SliceActionLoadingListener {
    boolean mAllowTwoLines;
    final Context mContext;
    int mInsetBottom;
    int mInsetEnd;
    int mInsetStart;
    int mInsetTop;
    long mLastUpdated;
    SliceView mParent;
    SliceViewPolicy mPolicy;
    boolean mShowLastUpdated;
    List<SliceAction> mSliceActions;
    SliceView.OnSliceActionListener mSliceObserver;
    SliceStyle mSliceStyle;
    TemplateView mTemplateView;
    int mTintColor;
    private final IdGenerator mIdGen = new IdGenerator();
    private List<SliceWrapper> mSlices = new ArrayList();
    Set<SliceItem> mLoadingActions = new HashSet();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class IdGenerator {
        private long mNextLong = 0;
        private final ArrayMap<String, Long> mCurrentIds = new ArrayMap<>();
        private final ArrayMap<String, Integer> mUsedIds = new ArrayMap<>();

        IdGenerator() {
        }

        private String genString(SliceItem item) {
            return ("slice".equals(item.getFormat()) || "action".equals(item.getFormat())) ? String.valueOf(item.getSlice().getItems().size()) : item.toString();
        }

        public long getId(SliceItem item) {
            String genString = genString(item);
            if (!this.mCurrentIds.containsKey(genString)) {
                ArrayMap<String, Long> arrayMap = this.mCurrentIds;
                long j = this.mNextLong;
                this.mNextLong = 1 + j;
                arrayMap.put(genString, Long.valueOf(j));
            }
            long longValue = this.mCurrentIds.get(genString).longValue();
            Integer num = this.mUsedIds.get(genString);
            this.mUsedIds.put(genString, Integer.valueOf((num != null ? num.intValue() : 0) + 1));
            return longValue + (r2 * 10000);
        }

        public void resetUsage() {
            this.mUsedIds.clear();
        }
    }

    /* loaded from: classes.dex */
    public class SliceViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, View.OnClickListener {
        public final SliceChildView mSliceChildView;

        public SliceViewHolder(View itemView) {
            super(itemView);
            this.mSliceChildView = itemView instanceof SliceChildView ? (SliceChildView) itemView : null;
        }

        void bind(SliceContent item, int position) {
            if (this.mSliceChildView == null || item == null) {
                return;
            }
            RowStyle rowStyle = SliceAdapter.this.mSliceStyle.getRowStyle(item.getSliceItem());
            this.mSliceChildView.setOnClickListener(this);
            this.mSliceChildView.setOnTouchListener(this);
            this.mSliceChildView.setSliceActionLoadingListener(SliceAdapter.this);
            boolean isHeader = item instanceof RowContent ? ((RowContent) item).getIsHeader() : position == 0;
            this.mSliceChildView.setLoadingActions(SliceAdapter.this.mLoadingActions);
            this.mSliceChildView.setPolicy(SliceAdapter.this.mPolicy);
            this.mSliceChildView.setTint(rowStyle.getTintColor());
            this.mSliceChildView.setStyle(SliceAdapter.this.mSliceStyle, rowStyle);
            this.mSliceChildView.setShowLastUpdated(isHeader && SliceAdapter.this.mShowLastUpdated);
            this.mSliceChildView.setLastUpdated(isHeader ? SliceAdapter.this.mLastUpdated : -1L);
            int i = position == 0 ? SliceAdapter.this.mInsetTop : 0;
            int i2 = position == SliceAdapter.this.getItemCount() - 1 ? SliceAdapter.this.mInsetBottom : 0;
            SliceChildView sliceChildView = this.mSliceChildView;
            SliceAdapter sliceAdapter = SliceAdapter.this;
            sliceChildView.setInsets(sliceAdapter.mInsetStart, i, sliceAdapter.mInsetEnd, i2);
            this.mSliceChildView.setAllowTwoLines(SliceAdapter.this.mAllowTwoLines);
            this.mSliceChildView.setSliceActions(isHeader ? SliceAdapter.this.mSliceActions : null);
            this.mSliceChildView.setSliceItem(item, isHeader, position, SliceAdapter.this.getItemCount(), SliceAdapter.this.mSliceObserver);
            this.mSliceChildView.setTag(new int[]{ListContent.getRowType(item, isHeader, SliceAdapter.this.mSliceActions), position});
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            SliceView sliceView = SliceAdapter.this.mParent;
            if (sliceView != null) {
                sliceView.setClickInfo((int[]) v.getTag());
                SliceAdapter.this.mParent.performClick();
            }
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            TemplateView templateView = SliceAdapter.this.mTemplateView;
            if (templateView != null) {
                templateView.onForegroundActivated(event);
                return false;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class SliceWrapper {
        final long mId;
        final SliceContent mItem;
        final int mType;

        public SliceWrapper(SliceContent item, IdGenerator idGen, int mode) {
            this.mItem = item;
            this.mType = getFormat(item.getSliceItem());
            this.mId = idGen.getId(item.getSliceItem());
        }

        public static int getFormat(SliceItem item) {
            if ("message".equals(item.getSubType())) {
                return SliceQuery.findSubtype(item, (String) null, "source") != null ? 4 : 5;
            } else if (item.hasHint("horizontal")) {
                return 3;
            } else {
                return !item.hasHint("list_item") ? 2 : 1;
            }
        }
    }

    public SliceAdapter(Context context) {
        this.mContext = context;
        setHasStableIds(true);
    }

    private View inflateForType(int viewType) {
        return viewType != 3 ? viewType != 4 ? viewType != 5 ? getRowView() : LayoutInflater.from(this.mContext).inflate(R$layout.abc_slice_message_local, (ViewGroup) null) : LayoutInflater.from(this.mContext).inflate(R$layout.abc_slice_message, (ViewGroup) null) : getGridRowView();
    }

    public GridRowView getGridRowView() {
        View inflate = LayoutInflater.from(this.mContext).inflate(R$layout.abc_slice_grid, (ViewGroup) null);
        return inflate instanceof GridRowView ? (GridRowView) inflate : new GridRowView(this.mContext, null);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mSlices.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int position) {
        return this.mSlices.get(position).mId;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int position) {
        return this.mSlices.get(position).mType;
    }

    public Set<SliceItem> getLoadingActions() {
        return this.mLoadingActions;
    }

    public RowView getRowView() {
        return new RowView(this.mContext);
    }

    public void notifyHeaderChanged() {
        if (getItemCount() > 0) {
            notifyItemChanged(0);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(SliceViewHolder holder, int position) {
        holder.bind(this.mSlices.get(position).mItem, position);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public SliceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflateForType = inflateForType(viewType);
        inflateForType.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        return new SliceViewHolder(inflateForType);
    }

    @Override // androidx.slice.widget.SliceActionView.SliceActionLoadingListener
    public void onSliceActionLoading(SliceItem actionItem, int position) {
        this.mLoadingActions.add(actionItem);
        if (getItemCount() > position) {
            notifyItemChanged(position);
        } else {
            notifyDataSetChanged();
        }
    }

    public void setAllowTwoLines(boolean allowTwoLines) {
        this.mAllowTwoLines = allowTwoLines;
        notifyHeaderChanged();
    }

    public void setInsets(int l, int t, int r, int b) {
        this.mInsetStart = l;
        this.mInsetTop = t;
        this.mInsetEnd = r;
        this.mInsetBottom = b;
    }

    public void setLastUpdated(long lastUpdated) {
        if (this.mLastUpdated != lastUpdated) {
            this.mLastUpdated = lastUpdated;
            notifyHeaderChanged();
        }
    }

    public void setLoadingActions(Set<SliceItem> actions) {
        if (actions == null) {
            this.mLoadingActions.clear();
        } else {
            this.mLoadingActions = actions;
        }
        notifyDataSetChanged();
    }

    public void setParents(SliceView parent, TemplateView templateView) {
        this.mParent = parent;
        this.mTemplateView = templateView;
    }

    public void setPolicy(SliceViewPolicy p) {
        this.mPolicy = p;
    }

    public void setShowLastUpdated(boolean showLastUpdated) {
        if (this.mShowLastUpdated != showLastUpdated) {
            this.mShowLastUpdated = showLastUpdated;
            notifyHeaderChanged();
        }
    }

    public void setSliceActions(List<SliceAction> actions) {
        this.mSliceActions = actions;
        notifyHeaderChanged();
    }

    public void setSliceItems(List<SliceContent> slices, int color, int mode) {
        if (slices == null) {
            this.mLoadingActions.clear();
            this.mSlices.clear();
        } else {
            this.mIdGen.resetUsage();
            this.mSlices = new ArrayList(slices.size());
            Iterator<SliceContent> it = slices.iterator();
            while (it.hasNext()) {
                this.mSlices.add(new SliceWrapper(it.next(), this.mIdGen, mode));
            }
        }
        this.mTintColor = color;
        notifyDataSetChanged();
    }

    public void setSliceObserver(SliceView.OnSliceActionListener observer) {
        this.mSliceObserver = observer;
    }

    public void setStyle(SliceStyle style) {
        this.mSliceStyle = style;
        notifyDataSetChanged();
    }
}
