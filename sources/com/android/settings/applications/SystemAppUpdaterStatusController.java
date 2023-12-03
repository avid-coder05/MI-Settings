package com.android.settings.applications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.widget.TextView;
import com.android.settings.BaseSettingsController;
import com.android.settings.device.UpdateBroadcastManager;
import java.text.NumberFormat;
import java.util.Locale;

/* loaded from: classes.dex */
public class SystemAppUpdaterStatusController extends BaseSettingsController {
    private boolean mHasRegister;
    private IntentFilter mIntentFilter;
    private Locale mLocale;
    private BroadcastReceiver mReceiver;

    public SystemAppUpdaterStatusController(Context context, TextView textView, Locale locale) {
        super(context, textView);
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.applications.SystemAppUpdaterStatusController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("com.xiaomi.market.action.APP_UPDATE_CHECKED".equals(intent.getAction())) {
                    SystemAppUpdaterStatusController.this.updateAppCount(intent.getIntExtra("extra_need_update_app_count", 0));
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("com.xiaomi.market.action.APP_UPDATE_CHECKED");
        this.mLocale = locale;
    }

    @Override // com.android.settings.BaseSettingsController
    public void pause() {
        if (this.mHasRegister) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.mHasRegister = false;
        }
    }

    @Override // com.android.settings.BaseSettingsController
    public void resume() {
        this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
        this.mHasRegister = true;
        updateAppCount(Settings.System.getInt(this.mContext.getContentResolver(), "updatable_system_app_count", 0));
    }

    protected void updateAppCount(int i) {
        if (this.mStatusView != null) {
            int appsAutoUpdateSuperscript = UpdateBroadcastManager.getAppsAutoUpdateSuperscript(this.mContext) + i;
            this.mStatusView.setText(NumberFormat.getInstance(this.mLocale).format(appsAutoUpdateSuperscript));
            this.mStatusView.setVisibility(appsAutoUpdateSuperscript > 0 ? 0 : 8);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.BaseSettingsController
    public void updateStatus() {
    }
}
