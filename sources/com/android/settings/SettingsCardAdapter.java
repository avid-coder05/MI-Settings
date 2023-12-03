package com.android.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.android.settings.widget.SettingsStatusCard;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;

/* loaded from: classes.dex */
public class SettingsCardAdapter extends BaseAdapter {
    private List<CardInfo> cardList;
    private Context mContext;

    /* loaded from: classes.dex */
    class ViewHolder {
        SettingsStatusCard card;

        ViewHolder() {
        }
    }

    public SettingsCardAdapter(Context context, List<CardInfo> list) {
        this.mContext = context;
        this.cardList = list;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.cardList.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return this.cardList.get(i);
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return 0L;
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(this.mContext).inflate(R.layout.normal_grid_view, (ViewGroup) null);
            viewHolder = new ViewHolder();
            viewHolder.card = (SettingsStatusCard) view.findViewById(R.id.card);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        CardInfo cardInfo = this.cardList.get(i);
        if (cardInfo != null) {
            viewHolder.card.setCardTitle(cardInfo.getTitleResId());
            viewHolder.card.setCardValue(cardInfo.getValueResId());
            if (cardInfo.isChecked()) {
                viewHolder.card.setChecked(true);
                viewHolder.card.setCardImageView(cardInfo.getCheckedIconResId());
            } else {
                viewHolder.card.setChecked(false);
                viewHolder.card.setCardImageView(cardInfo.getIconResId());
            }
            viewHolder.card.setDisable(cardInfo.isDisable());
        }
        Folme.useAt(view).touch().handleTouchOf(view, new AnimConfig[0]);
        return view;
    }
}
