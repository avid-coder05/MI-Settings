package com.android.settings.ai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import com.android.settings.ai.PreferenceHelper;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: classes.dex */
public class AiSettingsSubAdapter extends RecyclerView.Adapter<ItemHolder> {
    private String mButtonType;
    private Context mContext;
    private List<AiSettingsItem> mData;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ItemHolder extends RecyclerView.ViewHolder {
        RelativeLayout itembg;
        ImageView selected;
        TextView tv;

        ItemHolder(View view) {
            super(view);
            this.tv = (TextView) view.findViewById(R.id.item_name);
            this.selected = (ImageView) view.findViewById(R.id.selected_image);
            this.itembg = (RelativeLayout) view.findViewById(R.id.item_layout);
        }
    }

    public AiSettingsSubAdapter(Context context, List<AiSettingsItem> list, String str) {
        this.mData = new CopyOnWriteArrayList();
        this.mContext = context;
        this.mData = list;
        this.mButtonType = str;
    }

    private void handleSelect(final AiSettingsItem aiSettingsItem, ItemHolder itemHolder) {
        itemHolder.selected.setVisibility(aiSettingsItem.selected ? 0 : 4);
        itemHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.ai.AiSettingsSubAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Iterator it = AiSettingsSubAdapter.this.mData.iterator();
                while (it.hasNext()) {
                    ((AiSettingsItem) it.next()).selected = false;
                }
                aiSettingsItem.selected = true;
                PreferenceHelper.AiSettingsPreferenceHelper.setPressAiButtonSettings(AiSettingsSubAdapter.this.mContext, AiSettingsSubAdapter.this.mButtonType, aiSettingsItem);
                AiSettingsSubAdapter.this.notifyDataSetChanged();
            }
        });
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mData.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return this.mData.get(i).type;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ItemHolder itemHolder, int i) {
        AiSettingsItem aiSettingsItem = this.mData.get(i);
        itemHolder.tv.setText(this.mContext.getResources().getStringArray(R.array.ai_key_item_name)[aiSettingsItem.mIndex]);
        itemHolder.itembg.setBackground(this.mContext.getDrawable(8 == aiSettingsItem.type ? R.drawable.ai_settings_sub_none_item_bg : R.drawable.ai_settings_sub_item_bg));
        handleSelect(aiSettingsItem, itemHolder);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(this.mContext).inflate(R.layout.ai_settings_sub_op_item, viewGroup, false);
        inflate.setTag(Integer.valueOf(i));
        return new ItemHolder(inflate);
    }
}
