package com.android.settings.wifi;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
public class WifiDetailLinearLayout extends LinearLayout {
    private Context mContext;
    private List<WifiDetailInfoBean> mWifiDetailList;

    public WifiDetailLinearLayout(Context context) {
        super(context);
        this.mWifiDetailList = new ArrayList();
        this.mContext = context;
    }

    public WifiDetailLinearLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mWifiDetailList = new ArrayList();
        this.mContext = context;
    }

    public WifiDetailLinearLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mWifiDetailList = new ArrayList();
        this.mContext = context;
    }

    private void createWifiDetailGridView() {
        removeAllViews();
        int count = getCount();
        if (count <= 0) {
            return;
        }
        int i = (count / 2) + (count % 2);
        for (int i2 = 0; i2 < i; i2++) {
            LinearLayout childLayout = getChildLayout();
            for (int i3 = 0; i3 < 2; i3++) {
                childLayout.addView(getBaseView((i2 * 2) + i3));
            }
            addView(childLayout);
        }
    }

    private View getBaseView(int i) {
        View inflate = LayoutInflater.from(this.mContext).inflate(R.layout.wifi_detail_description_item, (ViewGroup) null, false);
        inflate.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1.0f));
        inflate.setPadding(0, 0, 30, 0);
        if (i >= getCount()) {
            return inflate;
        }
        ((ImageView) inflate.findViewById(R.id.wifi_detail_description_image)).setImageDrawable(this.mContext.getResources().getDrawable(this.mWifiDetailList.get(i).getIconNameId()));
        ((TextView) inflate.findViewById(16908308)).setText(this.mWifiDetailList.get(i).getTitleId());
        final TextView textView = (TextView) inflate.findViewById(16908309);
        textView.setText(this.mWifiDetailList.get(i).getSummary());
        textView.post(new Runnable() { // from class: com.android.settings.wifi.WifiDetailLinearLayout.1
            @Override // java.lang.Runnable
            public void run() {
                if (textView.getLineCount() > 2) {
                    textView.setMarqueeRepeatLimit(Integer.MAX_VALUE);
                    textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    textView.setSingleLine();
                    textView.setFocusableInTouchMode(true);
                }
            }
        });
        return inflate;
    }

    private LinearLayout getChildLayout() {
        LinearLayout linearLayout = new LinearLayout(this.mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        linearLayout.setOrientation(0);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setWeightSum(2.0f);
        return linearLayout;
    }

    private int getCount() {
        List<WifiDetailInfoBean> list = this.mWifiDetailList;
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public void initWifiDetailGrid(HashMap<String, WifiDetailInfoBean> hashMap) {
        if (hashMap == null || hashMap.isEmpty()) {
            return;
        }
        this.mWifiDetailList.clear();
        Iterator<Map.Entry<String, WifiDetailInfoBean>> it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            this.mWifiDetailList.add(it.next().getValue());
        }
        createWifiDetailGridView();
    }
}
