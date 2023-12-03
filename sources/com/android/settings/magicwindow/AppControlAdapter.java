package com.android.settings.magicwindow;

import android.content.Context;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import java.util.List;
import miuix.slidingwidget.widget.SlidingButton;

/* loaded from: classes.dex */
public class AppControlAdapter extends RecyclerView.Adapter<AppControlHolder> {
    private SwitchCallBack mCallBack;
    private List mChildAppItemInfoList;
    private Context mContext;
    private LayoutInflater mInflater;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class AppControlHolder extends RecyclerView.ViewHolder {
        ImageView image;
        SlidingButton switcher;
        TextView title;

        public AppControlHolder(View view) {
            super(view);
            this.image = (ImageView) view.findViewById(R.id.icon_imageView);
            this.title = (TextView) view.findViewById(R.id.app_name_textView);
            this.switcher = (SlidingButton) view.findViewById(R.id.sliding_button);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class OnCheckListener implements CompoundButton.OnCheckedChangeListener {
        int position;

        OnCheckListener() {
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            if (this.position < AppControlAdapter.this.mChildAppItemInfoList.size()) {
                ChildAppItemInfo childAppItemInfo = (ChildAppItemInfo) AppControlAdapter.this.mChildAppItemInfoList.get(this.position);
                if (childAppItemInfo.getMagicWinEnabled() != z) {
                    AppControlAdapter.this.mCallBack.onCheckedChangedListener(childAppItemInfo.getPkg(), z);
                }
                childAppItemInfo.setMagicWinEnabled(z);
            }
        }

        public void setPosition(int i) {
            this.position = i;
        }
    }

    public AppControlAdapter(Context context, SwitchCallBack switchCallBack, List list) {
        this.mContext = context;
        this.mCallBack = switchCallBack;
        this.mChildAppItemInfoList = list;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mChildAppItemInfoList.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        return i;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(AppControlHolder appControlHolder, int i) {
        OnCheckListener onCheckListener = new OnCheckListener();
        ChildAppItemInfo childAppItemInfo = (ChildAppItemInfo) this.mChildAppItemInfoList.get(i);
        appControlHolder.image.setImageDrawable(childAppItemInfo.getAppIcon());
        appControlHolder.title.setText(childAppItemInfo.getAppName());
        onCheckListener.setPosition(i);
        Slog.d("AppControlAdapter", "  childApp=" + childAppItemInfo + "   ,title=" + childAppItemInfo.getAppName());
        if (this.mContext == null) {
            appControlHolder.switcher.setOnCheckedChangeListener(null);
            Slog.d("AppControlAdapter", "cannot get item");
        } else {
            appControlHolder.switcher.setOnCheckedChangeListener(onCheckListener);
        }
        appControlHolder.switcher.setChecked(childAppItemInfo.getMagicWinEnabled());
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public AppControlHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(this.mContext).inflate(R.layout.magic_window_item, viewGroup, false);
        Slog.d("AppControlAdapter", "size=" + this.mChildAppItemInfoList.size());
        return new AppControlHolder(inflate);
    }
}
