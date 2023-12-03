package com.android.settings.emergency.ui.view;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import com.android.settings.emergency.ui.EmergencyContactsActivity;
import com.android.settings.emergency.util.Config;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import miui.provider.ExtraContacts;
import org.apache.miui.commons.lang3.StringUtils;

/* loaded from: classes.dex */
public class ContactsListViewAdapter extends RecyclerView.Adapter<MyViewHolder> implements ItemTouchHelperAdapter, View.OnClickListener {
    private Context mContext;
    private boolean mEditMode;
    private ItemTouchHelper mItemTouchHelper;
    private List<Pair<String, String>> mDataList = new ArrayList();
    private List<Pair<String, String>> mCacheList = new ArrayList();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View view) {
            super(view);
        }
    }

    public ContactsListViewAdapter(Context context) {
        this.mContext = context;
    }

    public void addDataItems(List<Pair<String, String>> list) {
        this.mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public List<Pair<String, String>> getDataList() {
        return this.mDataList;
    }

    public boolean getEditMode() {
        return this.mEditMode;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mDataList.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        Pair<String, String> pair = this.mDataList.get(i);
        ContactsItemView contactsItemView = (ContactsItemView) myViewHolder.itemView;
        contactsItemView.setEditMode(this.mEditMode);
        contactsItemView.bindData(pair);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (R.id.delete_btn == view.getId()) {
            this.mDataList.remove((Pair) view.getTag());
            notifyDataSetChanged();
            updateEmergencyContacts();
            if (this.mDataList.size() == 0) {
                ((EmergencyContactsActivity) this.mContext).showAddContactsDialog();
            }
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ContactsItemView contactsItemView = (ContactsItemView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.emergency_contacts_item_view, viewGroup, false);
        final MyViewHolder myViewHolder = new MyViewHolder(contactsItemView);
        contactsItemView.setOnDeleteBtnClickListener(this);
        contactsItemView.setOnDragBtnClickListener(new View.OnTouchListener() { // from class: com.android.settings.emergency.ui.view.ContactsListViewAdapter.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == 0) {
                    ContactsListViewAdapter.this.mItemTouchHelper.startDrag(myViewHolder);
                    return false;
                }
                return false;
            }
        });
        return myViewHolder;
    }

    @Override // com.android.settings.emergency.ui.view.ItemTouchHelperAdapter
    public void onItemClear(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setScaleX(1.0f);
        viewHolder.itemView.setScaleY(1.0f);
    }

    @Override // com.android.settings.emergency.ui.view.ItemTouchHelperAdapter
    public void onItemMove(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
        int adapterPosition = viewHolder.getAdapterPosition();
        int adapterPosition2 = viewHolder2.getAdapterPosition();
        if (adapterPosition < this.mDataList.size() && adapterPosition2 < this.mDataList.size()) {
            Collections.swap(this.mDataList, adapterPosition, adapterPosition2);
            notifyItemMoved(adapterPosition, adapterPosition2);
            updateEmergencyContacts();
        }
        onItemClear(viewHolder);
    }

    @Override // com.android.settings.emergency.ui.view.ItemTouchHelperAdapter
    public void onItemSelect(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setScaleX(1.1f);
        viewHolder.itemView.setScaleY(1.1f);
    }

    public void setDataList(List<Pair<String, String>> list) {
        if (list != null) {
            this.mDataList.clear();
            this.mDataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void setEditMode(boolean z, boolean z2) {
        if (z) {
            this.mCacheList.clear();
            this.mCacheList.addAll(this.mDataList);
        } else if (!z2) {
            this.mDataList.clear();
            this.mDataList.addAll(this.mCacheList);
        }
        this.mEditMode = z;
        notifyDataSetChanged();
    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.mItemTouchHelper = itemTouchHelper;
    }

    public void updateEmergencyContacts() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (Pair<String, String> pair : this.mDataList) {
            arrayList2.add((String) pair.first);
            arrayList.add((String) pair.second);
        }
        Config.setSosEmergencyContacts(this.mContext, StringUtils.join(arrayList, ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION));
        Config.setSosEmergencyContactNames(this.mContext, StringUtils.join(arrayList2, ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION));
    }
}
