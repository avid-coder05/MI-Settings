package com.android.settings.privacy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import java.util.ArrayList;
import java.util.List;
import miuix.slidingwidget.widget.SlidingButton;

/* loaded from: classes2.dex */
public class PrivacyRevocationAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private List<PrivacyItem> dataList = new ArrayList();
    private ClickListener listener;
    private Context mContext;

    /* loaded from: classes2.dex */
    public interface ClickListener {
        void onItemClick(PrivacyItem privacyItem);
    }

    /* loaded from: classes2.dex */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        SlidingButton slidingButton;
        TextView titleTextView;

        public MyViewHolder(View view) {
            super(view);
            this.titleTextView = (TextView) view.findViewById(R.id.title);
            this.iconImageView = (ImageView) view.findViewById(R.id.icon);
            this.slidingButton = (SlidingButton) view.findViewById(R.id.sliding_button);
        }
    }

    public PrivacyRevocationAdapter(Context context) {
        this.mContext = context;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        List<PrivacyItem> list = this.dataList;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        return i;
    }

    public PrivacyItem getPrivacyItemByPackageName(String str) {
        if (this.dataList == null || TextUtils.isEmpty(str)) {
            return null;
        }
        for (PrivacyItem privacyItem : this.dataList) {
            if (str.equals(privacyItem.packageName)) {
                return privacyItem;
            }
        }
        return null;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        List<PrivacyItem> list = this.dataList;
        if (list == null || i >= list.size()) {
            return;
        }
        final PrivacyItem privacyItem = this.dataList.get(i);
        myViewHolder.titleTextView.setText(privacyItem.label);
        Drawable drawable = privacyItem.drawable;
        if (drawable != null) {
            myViewHolder.iconImageView.setImageDrawable(drawable);
        } else {
            myViewHolder.iconImageView.setImageResource(R.drawable.card_icon_default);
        }
        myViewHolder.slidingButton.setOnCheckedChangeListener(null);
        myViewHolder.slidingButton.setChecked(privacyItem.enable);
        myViewHolder.slidingButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.privacy.PrivacyRevocationAdapter.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                ((PrivacyRevocationSettings) PrivacyRevocationAdapter.this.mContext).handleClick(privacyItem);
            }
        });
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.privacy.PrivacyRevocationAdapter.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (PrivacyRevocationAdapter.this.listener != null) {
                    PrivacyRevocationAdapter.this.listener.onItemClick(privacyItem);
                }
            }
        });
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.privacy_revocation_settings_item, viewGroup, false));
    }

    public void setListener(ClickListener clickListener) {
        this.listener = clickListener;
    }

    public void setPrivacyItemList(List<PrivacyItem> list) {
        this.dataList = list;
    }
}
