package com.android.settings.homepage.contextualcards.conditional;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

/* loaded from: classes.dex */
public abstract class AbnormalRingerConditionController implements ConditionalCardController {
    private static final IntentFilter FILTER = new IntentFilter("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION");
    private final Context mAppContext;
    protected final AudioManager mAudioManager;
    private final ConditionManager mConditionManager;
    private final RingerModeChangeReceiver mReceiver = new RingerModeChangeReceiver();

    /* loaded from: classes.dex */
    class RingerModeChangeReceiver extends BroadcastReceiver {
        RingerModeChangeReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION".equals(intent.getAction())) {
                AbnormalRingerConditionController.this.mConditionManager.onConditionChanged();
            }
        }
    }

    public AbnormalRingerConditionController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onActionClick() {
        this.mAudioManager.setRingerModeInternal(2);
        this.mAudioManager.setStreamVolume(2, 1, 0);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onPrimaryClick(Context context) {
        context.startActivity(new Intent("android.settings.SOUND_SETTINGS"));
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void startMonitoringStateChange() {
        this.mAppContext.registerReceiver(this.mReceiver, FILTER);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void stopMonitoringStateChange() {
        this.mAppContext.unregisterReceiver(this.mReceiver);
    }
}
