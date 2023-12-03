package com.android.settings.panel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.settings.R;
import com.android.settings.slices.CustomSliceRegistry;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class VolumePanel implements PanelContent, LifecycleObserver {
    private PanelContentCallback mCallback;
    private final Context mContext;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.panel.VolumePanel.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("com.android.settings.panel.action.CLOSE_PANEL".equals(intent.getAction())) {
                VolumePanel.this.mCallback.forceClose();
            }
        }
    };

    private VolumePanel(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static VolumePanel create(Context context) {
        return new VolumePanel(context);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1655;
    }

    @Override // com.android.settings.panel.PanelContent
    public Intent getSeeMoreIntent() {
        return new Intent("android.settings.SOUND_SETTINGS").addFlags(268435456);
    }

    @Override // com.android.settings.panel.PanelContent
    public List<Uri> getSlices() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(CustomSliceRegistry.REMOTE_MEDIA_SLICE_URI);
        arrayList.add(CustomSliceRegistry.VOLUME_MEDIA_URI);
        arrayList.add(CustomSliceRegistry.MEDIA_OUTPUT_INDICATOR_SLICE_URI);
        arrayList.add(CustomSliceRegistry.VOLUME_CALL_URI);
        arrayList.add(CustomSliceRegistry.VOLUME_RINGER_URI);
        arrayList.add(CustomSliceRegistry.VOLUME_ALARM_URI);
        return arrayList;
    }

    @Override // com.android.settings.panel.PanelContent
    public CharSequence getTitle() {
        return this.mContext.getText(R.string.sound_settings);
    }

    @Override // com.android.settings.panel.PanelContent
    public int getViewType() {
        return 1;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.settings.panel.action.CLOSE_PANEL");
        this.mContext.registerReceiver(this.mReceiver, intentFilter);
    }

    @Override // com.android.settings.panel.PanelContent
    public void registerCallback(PanelContentCallback panelContentCallback) {
        this.mCallback = panelContentCallback;
    }
}
