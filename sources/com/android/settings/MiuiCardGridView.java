package com.android.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import com.android.settings.sound.coolsound.RingtoneGridView;
import com.android.settings.utils.SettingsFeatures;
import java.util.List;

/* loaded from: classes.dex */
public class MiuiCardGridView extends RelativeLayout {
    private BaseAdapter mAdapter;
    protected Context mContext;
    private List<CardInfo> mData;
    private RingtoneGridView mGridView;

    public MiuiCardGridView(Context context) {
        super(context);
        this.mAdapter = null;
        this.mContext = context;
        init();
    }

    public MiuiCardGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mAdapter = null;
        this.mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(this.mContext).inflate(R.layout.ringtone_settings_layout, (ViewGroup) this, true);
        this.mGridView = (RingtoneGridView) findViewById(R.id.grid_view);
        if (SettingsFeatures.isSplitTabletDevice()) {
            this.mGridView.setNumColumns(MiuiUtils.isLandScape(this.mContext) ? 4 : 2);
        }
    }

    public void notifyDataChanged() {
        BaseAdapter baseAdapter = this.mAdapter;
        if (baseAdapter != null) {
            baseAdapter.notifyDataSetChanged();
        }
    }

    public void setData(List<CardInfo> list) {
        this.mData = list;
        SettingsCardAdapter settingsCardAdapter = new SettingsCardAdapter(this.mContext, this.mData);
        this.mAdapter = settingsCardAdapter;
        this.mGridView.setAdapter((ListAdapter) settingsCardAdapter);
        this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.settings.MiuiCardGridView.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                CardInfo cardInfo = (CardInfo) MiuiCardGridView.this.mData.get(i);
                if (cardInfo == null || cardInfo.isDisable() || cardInfo.getOnClickListener() == null) {
                    return;
                }
                cardInfo.getOnClickListener().onClick(view);
            }
        });
    }
}
