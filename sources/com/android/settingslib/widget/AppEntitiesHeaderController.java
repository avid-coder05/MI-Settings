package com.android.settingslib.widget;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/* loaded from: classes2.dex */
public class AppEntitiesHeaderController {
    public static final int MAXIMUM_APPS = 3;
    private final View[] mAppEntityViews;
    private final View mAppViewsContainer;
    private final Context mContext;
    private View.OnClickListener mDetailsOnClickListener;
    private CharSequence mHeaderDetails;
    private int mHeaderDetailsRes;
    private final Button mHeaderDetailsView;
    private int mHeaderEmptyRes;
    private final TextView mHeaderEmptyView;
    private int mHeaderTitleRes;
    private final TextView mHeaderTitleView;
    private final AppEntityInfo[] mAppEntityInfos = new AppEntityInfo[3];
    private final ImageView[] mAppIconViews = new ImageView[3];
    private final TextView[] mAppTitleViews = new TextView[3];
    private final TextView[] mAppSummaryViews = new TextView[3];

    private AppEntitiesHeaderController(Context context, View view) {
        this.mContext = context;
        this.mHeaderTitleView = (TextView) view.findViewById(R$id.header_title);
        this.mHeaderDetailsView = (Button) view.findViewById(R$id.header_details);
        this.mHeaderEmptyView = (TextView) view.findViewById(R$id.empty_view);
        this.mAppViewsContainer = view.findViewById(R$id.app_views_container);
        this.mAppEntityViews = new View[]{view.findViewById(R$id.app1_view), view.findViewById(R$id.app2_view), view.findViewById(R$id.app3_view)};
        for (int i = 0; i < 3; i++) {
            View view2 = this.mAppEntityViews[i];
            this.mAppIconViews[i] = (ImageView) view2.findViewById(R$id.app_icon);
            this.mAppTitleViews[i] = (TextView) view2.findViewById(R$id.app_title);
            this.mAppSummaryViews[i] = (TextView) view2.findViewById(R$id.app_summary);
        }
    }

    private void bindAppEntityView(int i) {
        AppEntityInfo appEntityInfo = this.mAppEntityInfos[i];
        this.mAppEntityViews[i].setVisibility(appEntityInfo != null ? 0 : 8);
        if (appEntityInfo != null) {
            this.mAppEntityViews[i].setOnClickListener(appEntityInfo.getClickListener());
            this.mAppIconViews[i].setImageDrawable(appEntityInfo.getIcon());
            CharSequence title = appEntityInfo.getTitle();
            this.mAppTitleViews[i].setVisibility(TextUtils.isEmpty(title) ? 4 : 0);
            this.mAppTitleViews[i].setText(title);
            CharSequence summary = appEntityInfo.getSummary();
            this.mAppSummaryViews[i].setVisibility(TextUtils.isEmpty(summary) ? 4 : 0);
            this.mAppSummaryViews[i].setText(summary);
        }
    }

    private void bindHeaderDetailsView() {
        CharSequence charSequence = this.mHeaderDetails;
        if (TextUtils.isEmpty(charSequence)) {
            try {
                charSequence = this.mContext.getText(this.mHeaderDetailsRes);
            } catch (Resources.NotFoundException e) {
                Log.e("AppEntitiesHeaderCtl", "Resource of header details can't not be found!", e);
            }
        }
        this.mHeaderDetailsView.setText(charSequence);
        this.mHeaderDetailsView.setVisibility(TextUtils.isEmpty(charSequence) ? 8 : 0);
        this.mHeaderDetailsView.setOnClickListener(this.mDetailsOnClickListener);
    }

    private void bindHeaderTitleView() {
        CharSequence charSequence;
        try {
            charSequence = this.mContext.getText(this.mHeaderTitleRes);
        } catch (Resources.NotFoundException e) {
            Log.e("AppEntitiesHeaderCtl", "Resource of header title can't not be found!", e);
            charSequence = "";
        }
        this.mHeaderTitleView.setText(charSequence);
        this.mHeaderTitleView.setVisibility(TextUtils.isEmpty(charSequence) ? 8 : 0);
    }

    private boolean isAppEntityInfosEmpty() {
        for (AppEntityInfo appEntityInfo : this.mAppEntityInfos) {
            if (appEntityInfo != null) {
                return false;
            }
        }
        return true;
    }

    public static AppEntitiesHeaderController newInstance(Context context, View view) {
        return new AppEntitiesHeaderController(context, view);
    }

    private void setEmptyViewVisible(boolean z) {
        int i = this.mHeaderEmptyRes;
        if (i != 0) {
            this.mHeaderEmptyView.setText(i);
        }
        this.mHeaderEmptyView.setVisibility(z ? 0 : 8);
        this.mHeaderDetailsView.setVisibility(z ? 8 : 0);
        this.mAppViewsContainer.setVisibility(z ? 8 : 0);
    }

    public void apply() {
        bindHeaderTitleView();
        if (isAppEntityInfosEmpty()) {
            setEmptyViewVisible(true);
            return;
        }
        setEmptyViewVisible(false);
        bindHeaderDetailsView();
        for (int i = 0; i < 3; i++) {
            bindAppEntityView(i);
        }
    }

    public AppEntitiesHeaderController setAppEntity(int i, AppEntityInfo appEntityInfo) {
        this.mAppEntityInfos[i] = appEntityInfo;
        return this;
    }

    public AppEntitiesHeaderController setHeaderDetails(CharSequence charSequence) {
        this.mHeaderDetails = charSequence;
        return this;
    }

    public AppEntitiesHeaderController setHeaderDetailsClickListener(View.OnClickListener onClickListener) {
        this.mDetailsOnClickListener = onClickListener;
        return this;
    }

    public AppEntitiesHeaderController setHeaderTitleRes(int i) {
        this.mHeaderTitleRes = i;
        return this;
    }
}
