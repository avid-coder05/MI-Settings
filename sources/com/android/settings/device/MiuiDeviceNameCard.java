package com.android.settings.device;

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.settings.MiuiDeviceNameEditFragment;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.report.InternationalCompat;
import com.android.settings.widget.BaseSettingsCard;

/* loaded from: classes.dex */
public class MiuiDeviceNameCard extends BaseSettingsCard implements View.OnClickListener {
    private TextView mDeviceNameText;
    private DashboardFragment mFragment;

    public MiuiDeviceNameCard(Context context) {
        super(context);
        initView();
    }

    public MiuiDeviceNameCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    private void initView() {
        addLayout(R.layout.device_name_card);
        this.mDeviceNameText = (TextView) findViewById(R.id.my_device_name);
        refreshDeviceName();
        setOnClickListener(this);
        if (UserHandle.myUserId() != 0) {
            setEnabled(false);
            setAlpha(0.3f);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(":miui:starting_window_label", "");
        DashboardFragment dashboardFragment = this.mFragment;
        dashboardFragment.startFragment(dashboardFragment, MiuiDeviceNameEditFragment.class.getName(), 0, bundle, 0);
        InternationalCompat.trackReportEvent("setting_About_phone_phonename");
    }

    public void refreshDeviceName() {
        TextView textView;
        String deviceName = MiuiSettings.System.getDeviceName(this.mContext);
        if (TextUtils.isEmpty(deviceName) || (textView = this.mDeviceNameText) == null) {
            return;
        }
        textView.setText(deviceName);
    }

    public void setFragment(DashboardFragment dashboardFragment) {
        this.mFragment = dashboardFragment;
    }
}
