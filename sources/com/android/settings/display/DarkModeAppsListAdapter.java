package com.android.settings.display;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.ServiceManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import com.android.settings.display.util.DarkModeAppCacheManager;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import miui.os.UserHandle;
import miuix.slidingwidget.widget.SlidingButton;

/* loaded from: classes.dex */
public class DarkModeAppsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ApplicationInfo mAppInfo;
    private Context mContext;
    List<DarkModeAppInfo> mData;
    private PackageManager mPackageManager;
    private String mSearchInput;
    private OnAppCheckedListener onAppCheckedListener;
    private OnItemClickListener onItemClickListener;
    int userId = UserHandle.myUserId();
    private int mHeaderCount = 1;

    /* loaded from: classes.dex */
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView mAppListTitle;
        View mAppListTitleLine;

        public HeaderViewHolder(View view) {
            super(view);
            this.mAppListTitleLine = view.findViewById(R.id.dark_mode_apps_line);
            this.mAppListTitle = (TextView) view.findViewById(R.id.dark_mode_apps_title);
        }
    }

    /* loaded from: classes.dex */
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        SlidingButton sliding_button;
        TextView title;

        public ItemViewHolder(View view) {
            super(view);
            view.setTag(this);
            this.sliding_button = (SlidingButton) view.findViewById(R.id.sliding_button);
            this.icon = (ImageView) view.findViewById(R.id.icon);
            this.title = (TextView) view.findViewById(R.id.title);
        }
    }

    /* loaded from: classes.dex */
    public interface OnAppCheckedListener {
        void onAppChecked(CompoundButton compoundButton, boolean z, DarkModeAppInfo darkModeAppInfo);
    }

    /* loaded from: classes.dex */
    public interface OnItemClickListener {
        void onItemClick(SlidingButton slidingButton, int i);
    }

    public DarkModeAppsListAdapter(Context context, List<DarkModeAppInfo> list, OnAppCheckedListener onAppCheckedListener) {
        this.mContext = context;
        this.onAppCheckedListener = onAppCheckedListener;
        updateData(list);
        ServiceManager.getService("uimode");
    }

    private void setLabelTextView(TextView textView, String str, String str2) {
        if (TextUtils.isEmpty(str2)) {
            textView.setText(str);
        } else if (str.toLowerCase().contains(str2.toLowerCase())) {
            int indexOf = str.toLowerCase().indexOf(str2.toLowerCase());
            String substring = str.substring(indexOf, str2.length() + indexOf);
            textView.setText(Html.fromHtml(str.replaceFirst(substring, String.format(this.mContext.getString(R.string.search_input_txt_na), substring))));
        }
    }

    private void updateData(List<DarkModeAppInfo> list) {
        this.mData = list;
        if (list != null) {
            Collections.sort(list, new Comparator<DarkModeAppInfo>() { // from class: com.android.settings.display.DarkModeAppsListAdapter.3
                @Override // java.util.Comparator
                public int compare(DarkModeAppInfo darkModeAppInfo, DarkModeAppInfo darkModeAppInfo2) {
                    if (darkModeAppInfo.getLastTimeUsed() > darkModeAppInfo2.getLastTimeUsed()) {
                        return -1;
                    }
                    return darkModeAppInfo.getLastTimeUsed() < darkModeAppInfo2.getLastTimeUsed() ? 1 : 0;
                }
            });
        }
    }

    public int getContentItemCount() {
        return this.mData.size();
    }

    public List<DarkModeAppInfo> getData() {
        return this.mData;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mHeaderCount + getContentItemCount();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        return i;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return i == 0 ? 0 : 1;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof HeaderViewHolder) {
            List<DarkModeAppInfo> list = this.mData;
            if (list == null) {
                return;
            }
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
            if (headerViewHolder.mAppListTitleLine != null && headerViewHolder.mAppListTitle != null) {
                if (list.size() == 0) {
                    headerViewHolder.mAppListTitleLine.setVisibility(4);
                    headerViewHolder.mAppListTitle.setVisibility(4);
                } else {
                    headerViewHolder.mAppListTitleLine.setVisibility(0);
                    headerViewHolder.mAppListTitle.setVisibility(0);
                }
            }
        }
        if (viewHolder instanceof ItemViewHolder) {
            final int i2 = i - this.mHeaderCount;
            List<DarkModeAppInfo> list2 = this.mData;
            if (list2 == null || i2 >= list2.size()) {
                return;
            }
            final ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
            if (this.onItemClickListener != null) {
                itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.DarkModeAppsListAdapter.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        DarkModeAppsListAdapter.this.onItemClickListener.onItemClick(itemViewHolder.sliding_button, i2);
                    }
                });
            }
            final DarkModeAppInfo darkModeAppInfo = this.mData.get(i2);
            itemViewHolder.icon.setImageDrawable(DarkModeAppCacheManager.getInstance(this.mContext).loadAppIcon(this.mContext, darkModeAppInfo.getPkgName(), this.userId, this.mAppInfo, this.mPackageManager));
            itemViewHolder.title.setText(darkModeAppInfo.getLabel());
            setLabelTextView(itemViewHolder.title, darkModeAppInfo.getLabel().toString(), this.mSearchInput);
            itemViewHolder.sliding_button.setChecked(darkModeAppInfo.isEnabled());
            itemViewHolder.sliding_button.setOnPerformCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.display.DarkModeAppsListAdapter.2
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    DarkModeAppsListAdapter.this.onAppCheckedListener.onAppChecked(compoundButton, z, darkModeAppInfo);
                }
            });
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return i == 0 ? new HeaderViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.dark_mode_apps_header, viewGroup, false)) : new ItemViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.listitem_darkmode_app, viewGroup, false));
    }

    public void refreshAppList(List<DarkModeAppInfo> list) {
        updateData(list);
        notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAppDarkMode(DarkModeAppInfo darkModeAppInfo, boolean z) {
        darkModeAppInfo.setEnabled(z);
        DarkModeAppCacheManager.getInstance(this.mContext).setAppDarkMode(darkModeAppInfo.getPkgName(), z);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
