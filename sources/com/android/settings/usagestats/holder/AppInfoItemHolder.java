package com.android.settings.usagestats.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.usagestats.model.AppValueData;
import com.android.settings.usagestats.utils.AppInfoUtils;
import com.android.settings.usagestats.widget.LevelBarView;

/* loaded from: classes2.dex */
public class AppInfoItemHolder extends BaseHolder {
    private ImageView ivAppIcon;
    private LevelBarView levelBar;
    private long mMaxCount;
    private AppValueData mValueData;
    private TextView tvAppName;
    private TextView tvUsageTime;

    public AppInfoItemHolder(Context context) {
        super(context);
    }

    @Override // com.android.settings.usagestats.holder.BaseHolder
    protected View inflateView() {
        return View.inflate(this.mContext, R.layout.widget_app_usage_item, null);
    }

    @Override // com.android.settings.usagestats.holder.BaseHolder
    public void renderView() {
        this.ivAppIcon = (ImageView) this.mContentView.findViewById(R.id.iv_app_icon);
        this.tvAppName = (TextView) this.mContentView.findViewById(R.id.tv_app_name);
        this.levelBar = (LevelBarView) this.mContentView.findViewById(R.id.seekbar_app_usage_time);
        this.tvUsageTime = (TextView) this.mContentView.findViewById(R.id.tv_app_usage_time);
        this.ivAppIcon.setImageDrawable(AppInfoUtils.getAppLaunchIcon(this.mContext, this.mValueData.getPackageName()));
        this.tvAppName.setText(AppInfoUtils.getAppName(this.mContext, this.mValueData.getPackageName()));
        this.tvUsageTime.setText(AppInfoUtils.formatTime(this.mContext, this.mValueData.getValue()));
        this.levelBar.setMaxLevel(this.mMaxCount);
        this.levelBar.setCurrentLevel(this.mValueData.getValue());
    }

    public void setmMaxCount(long j) {
        this.mMaxCount = j;
    }

    public void setmValueData(AppValueData appValueData) {
        this.mValueData = appValueData;
    }
}
