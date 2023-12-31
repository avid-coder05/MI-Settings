package com.android.settings.localepicker;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.app.LocalePicker;
import com.android.internal.app.LocaleStore;
import com.android.settings.R;
import com.android.settings.shortcut.ShortcutsUpdateTask;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class LocaleDragAndDropAdapter extends RecyclerView.Adapter<CustomViewHolder> {
    private final Context mContext;
    private final List<LocaleStore.LocaleInfo> mFeedItemList;
    private final ItemTouchHelper mItemTouchHelper;
    private RecyclerView mParentView = null;
    private boolean mRemoveMode = false;
    private boolean mDragEnabled = true;
    private NumberFormat mNumberFormatter = NumberFormat.getNumberInstance();
    private LocaleList mLocalesToSetNext = null;
    private LocaleList mLocalesSetLast = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        private final LocaleDragCell mLocaleDragCell;

        public CustomViewHolder(LocaleDragCell localeDragCell) {
            super(localeDragCell);
            this.mLocaleDragCell = localeDragCell;
            localeDragCell.getDragHandle().setOnTouchListener(this);
        }

        public LocaleDragCell getLocaleDragCell() {
            return this.mLocaleDragCell;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (LocaleDragAndDropAdapter.this.mDragEnabled && MotionEventCompat.getActionMasked(motionEvent) == 0) {
                LocaleDragAndDropAdapter.this.mItemTouchHelper.startDrag(this);
                return false;
            }
            return false;
        }
    }

    public LocaleDragAndDropAdapter(Context context, List<LocaleStore.LocaleInfo> list) {
        this.mFeedItemList = list;
        this.mContext = context;
        final float applyDimension = TypedValue.applyDimension(1, 8.0f, context.getResources().getDisplayMetrics());
        this.mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(3, 0) { // from class: com.android.settings.localepicker.LocaleDragAndDropAdapter.1
            private int mSelectionStatus = -1;

            @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
            public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
                super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
                int i2 = this.mSelectionStatus;
                if (i2 != -1) {
                    viewHolder.itemView.setElevation(i2 == 1 ? applyDimension : 0.0f);
                    this.mSelectionStatus = -1;
                }
            }

            @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
                LocaleDragAndDropAdapter.this.onItemMove(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
                return true;
            }

            @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
                super.onSelectedChanged(viewHolder, i);
                if (i == 2) {
                    this.mSelectionStatus = 1;
                } else if (i == 0) {
                    this.mSelectionStatus = 0;
                }
            }

            @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
            }
        });
    }

    private void setDragEnabled(boolean z) {
        this.mDragEnabled = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addLocale(LocaleStore.LocaleInfo localeInfo) {
        this.mFeedItemList.add(localeInfo);
        notifyItemInserted(this.mFeedItemList.size() - 1);
        doTheUpdate();
    }

    public void doTheUpdate() {
        int size = this.mFeedItemList.size();
        Locale[] localeArr = new Locale[size];
        for (int i = 0; i < size; i++) {
            localeArr[i] = this.mFeedItemList.get(i).getLocale();
        }
        updateLocalesWhenAnimationStops(new LocaleList(localeArr));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getCheckedCount() {
        Iterator<LocaleStore.LocaleInfo> it = this.mFeedItemList.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (it.next().getChecked()) {
                i++;
            }
        }
        return i;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        List<LocaleStore.LocaleInfo> list = this.mFeedItemList;
        int size = list != null ? list.size() : 0;
        if (size < 2 || this.mRemoveMode) {
            setDragEnabled(false);
        } else {
            setDragEnabled(true);
        }
        return size;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isFirstLocaleChecked() {
        List<LocaleStore.LocaleInfo> list = this.mFeedItemList;
        return list != null && list.get(0).getChecked();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isRemoveMode() {
        return this.mRemoveMode;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        LocaleStore.LocaleInfo localeInfo = this.mFeedItemList.get(i);
        final LocaleDragCell localeDragCell = customViewHolder.getLocaleDragCell();
        localeDragCell.setLabelAndDescription(localeInfo.getFullNameNative(), localeInfo.getFullNameInUiLanguage());
        localeDragCell.setLocalized(localeInfo.isTranslated());
        localeDragCell.setMiniLabel(this.mNumberFormatter.format(i + 1));
        localeDragCell.setShowCheckbox(this.mRemoveMode);
        localeDragCell.setShowMiniLabel(!this.mRemoveMode);
        localeDragCell.setShowHandle(!this.mRemoveMode && this.mDragEnabled);
        localeDragCell.setTag(localeInfo);
        CheckBox checkbox = localeDragCell.getCheckbox();
        checkbox.setOnCheckedChangeListener(null);
        checkbox.setChecked(this.mRemoveMode ? localeInfo.getChecked() : false);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.localepicker.LocaleDragAndDropAdapter.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                ((LocaleStore.LocaleInfo) localeDragCell.getTag()).setChecked(z);
            }
        });
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new CustomViewHolder((LocaleDragCell) LayoutInflater.from(this.mContext).inflate(R.layout.locale_drag_cell, viewGroup, false));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onItemMove(int i, int i2) {
        if (i < 0 || i2 < 0) {
            Log.e("LocaleDragAndDropAdapter", String.format(Locale.US, "Negative position in onItemMove %d -> %d", Integer.valueOf(i), Integer.valueOf(i2)));
        } else {
            LocaleStore.LocaleInfo localeInfo = this.mFeedItemList.get(i);
            this.mFeedItemList.remove(i);
            this.mFeedItemList.add(i2, localeInfo);
        }
        notifyItemChanged(i);
        notifyItemChanged(i2);
        notifyItemMoved(i, i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeChecked() {
        for (int size = this.mFeedItemList.size() - 1; size >= 0; size--) {
            if (this.mFeedItemList.get(size).getChecked()) {
                this.mFeedItemList.remove(size);
            }
        }
        notifyDataSetChanged();
        doTheUpdate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeItem(int i) {
        int size = this.mFeedItemList.size();
        if (size > 1 && i >= 0 && i < size) {
            this.mFeedItemList.remove(i);
            notifyDataSetChanged();
        }
    }

    public void restoreState(Bundle bundle) {
        ArrayList<String> stringArrayList;
        if (bundle == null || !this.mRemoveMode || (stringArrayList = bundle.getStringArrayList("selectedLocales")) == null || stringArrayList.isEmpty()) {
            return;
        }
        for (LocaleStore.LocaleInfo localeInfo : this.mFeedItemList) {
            localeInfo.setChecked(stringArrayList.contains(localeInfo.getId()));
        }
        notifyItemRangeChanged(0, this.mFeedItemList.size());
    }

    public void saveState(Bundle bundle) {
        if (bundle != null) {
            ArrayList<String> arrayList = new ArrayList<>();
            for (LocaleStore.LocaleInfo localeInfo : this.mFeedItemList) {
                if (localeInfo.getChecked()) {
                    arrayList.add(localeInfo.getId());
                }
            }
            bundle.putStringArrayList("selectedLocales", arrayList);
        }
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.mParentView = recyclerView;
        this.mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRemoveMode(boolean z) {
        this.mRemoveMode = z;
        int size = this.mFeedItemList.size();
        for (int i = 0; i < size; i++) {
            this.mFeedItemList.get(i).setChecked(false);
            notifyItemChanged(i);
        }
    }

    public void updateLocalesWhenAnimationStops(LocaleList localeList) {
        if (localeList.equals(this.mLocalesToSetNext)) {
            return;
        }
        LocaleList.setDefault(localeList);
        this.mLocalesToSetNext = localeList;
        this.mParentView.getItemAnimator().isRunning(new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() { // from class: com.android.settings.localepicker.LocaleDragAndDropAdapter.3
            @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator.ItemAnimatorFinishedListener
            public void onAnimationsFinished() {
                if (LocaleDragAndDropAdapter.this.mLocalesToSetNext == null || LocaleDragAndDropAdapter.this.mLocalesToSetNext.equals(LocaleDragAndDropAdapter.this.mLocalesSetLast)) {
                    return;
                }
                LocalePicker.updateLocales(LocaleDragAndDropAdapter.this.mLocalesToSetNext);
                LocaleDragAndDropAdapter localeDragAndDropAdapter = LocaleDragAndDropAdapter.this;
                localeDragAndDropAdapter.mLocalesSetLast = localeDragAndDropAdapter.mLocalesToSetNext;
                new ShortcutsUpdateTask(LocaleDragAndDropAdapter.this.mContext).execute(new Void[0]);
                LocaleDragAndDropAdapter.this.mLocalesToSetNext = null;
                LocaleDragAndDropAdapter.this.mNumberFormatter = NumberFormat.getNumberInstance(Locale.getDefault());
            }
        });
    }
}
