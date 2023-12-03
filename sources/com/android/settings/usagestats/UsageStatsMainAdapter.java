package com.android.settings.usagestats;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.android.settings.usagestats.holder.AppUsageHolder;
import com.android.settings.usagestats.holder.AppUsageListHolder;
import com.android.settings.usagestats.holder.BaseHolder;
import com.android.settings.usagestats.holder.DivideHolder;
import com.android.settings.usagestats.holder.PagerHolder;
import com.android.settings.usagestats.holder.TabBarViewHolder;
import com.android.settings.usagestats.holder.UnUsedViewHolder;
import com.android.settings.usagestats.holder.UsageStatsHolder;
import com.android.settings.usagestats.holder.UsageStatsLineHolder;
import com.android.settings.usagestats.holder.UsageTimeRemindHolder;
import com.android.settings.usagestats.model.UsageFloorData;
import java.util.List;

/* loaded from: classes2.dex */
public class UsageStatsMainAdapter extends BaseAdapter {
    private Context mContext;
    private List<UsageFloorData> mList;

    public UsageStatsMainAdapter(Context context, List<UsageFloorData> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        List<UsageFloorData> list = this.mList;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return this.mList.get(i);
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int i) {
        return this.mList.get(i).getFloorType();
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        BaseHolder baseHolder;
        BaseHolder appUsageHolder;
        if (view == null) {
            switch (getItemViewType(i)) {
                case 0:
                    baseHolder = new DivideHolder(this.mContext);
                    break;
                case 1:
                    appUsageHolder = new AppUsageHolder(this.mContext, this.mList.get(i));
                    baseHolder = appUsageHolder;
                    break;
                case 2:
                    baseHolder = new TabBarViewHolder(this.mContext);
                    break;
                case 3:
                    appUsageHolder = new AppUsageListHolder(this.mContext, this.mList.get(i));
                    baseHolder = appUsageHolder;
                    break;
                case 4:
                    appUsageHolder = new UsageStatsHolder(this.mContext, 3, this.mList.get(i));
                    baseHolder = appUsageHolder;
                    break;
                case 5:
                    appUsageHolder = new UsageStatsHolder(this.mContext, 2, this.mList.get(i));
                    baseHolder = appUsageHolder;
                    break;
                case 6:
                    baseHolder = new UsageTimeRemindHolder(this.mContext);
                    break;
                case 7:
                    baseHolder = new UsageStatsLineHolder(this.mContext);
                    break;
                case 8:
                    baseHolder = new PagerHolder(this.mContext);
                    break;
                default:
                    baseHolder = new UnUsedViewHolder(this.mContext);
                    break;
            }
            view = baseHolder.getmContentView();
            view.setTag(baseHolder);
        } else {
            baseHolder = (BaseHolder) view.getTag();
        }
        baseHolder.renderView();
        return view;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        return 9;
    }
}
