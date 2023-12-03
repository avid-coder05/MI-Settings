package com.android.settings.wifi;

import android.view.View;
import com.android.settings.R;

/* loaded from: classes2.dex */
public class ConnectedAccessPointPreference extends LongPressAccessPointPreference implements View.OnClickListener {
    private boolean mIsCaptivePortal;
    private OnGearClickListener mOnGearClickListener;

    /* loaded from: classes2.dex */
    public interface OnGearClickListener {
        void onGearClick(ConnectedAccessPointPreference connectedAccessPointPreference);
    }

    @Override // com.android.settingslib.wifi.AccessPointPreference
    protected int getWidgetLayoutResourceId() {
        return R.layout.preference_widget_gear_optional_background;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        OnGearClickListener onGearClickListener;
        if (view.getId() != R.id.settings_button || (onGearClickListener = this.mOnGearClickListener) == null) {
            return;
        }
        onGearClickListener.onGearClick(this);
    }

    @Override // com.android.settingslib.wifi.AccessPointPreference
    public void refresh() {
        super.refresh();
        setShowDivider(this.mIsCaptivePortal);
        if (this.mIsCaptivePortal) {
            setSummary(R.string.wifi_tap_to_sign_in);
        }
    }
}
