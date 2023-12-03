package com.android.settings.aikey;

import android.app.ISearchManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class AidaemonService extends Service {

    /* loaded from: classes.dex */
    private class Chooser {
        private Context mContext;

        public Chooser(Context context) {
            this.mContext = context;
        }

        private IntentAction chooseInner(int i) {
            return new OpenVoiceAssistantIntentAction(i);
        }

        /* JADX WARN: Code restructure failed: missing block: B:9:0x0035, code lost:
        
            if (com.android.settings.aikey.PreferenceHelper.AiSettingsPreferenceHelper.getPressAiButtonSettings(r4.mContext, r5) == 1) goto L28;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public com.android.settings.aikey.AidaemonService.IntentAction choose(android.content.Intent r5) {
            /*
                r4 = this;
                java.lang.String r0 = "key_ai_button_settings"
                java.lang.String r5 = r5.getStringExtra(r0)
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r1 = "IntentAction----------choose-----------------pressType:"
                r0.append(r1)
                r0.append(r5)
                java.lang.String r0 = r0.toString()
                java.lang.String r1 = "AidaemonService"
                android.util.Log.i(r1, r0)
                r0 = 0
                if (r5 != 0) goto L25
                com.android.settings.aikey.AidaemonService$EmptyIntentAction r4 = new com.android.settings.aikey.AidaemonService$EmptyIntentAction
                r4.<init>()
                return r4
            L25:
                java.lang.String r1 = "key_single_click_ai_button_settings"
                boolean r1 = r1.equals(r5)
                r2 = -1
                r3 = 1
                if (r1 == 0) goto L38
                android.content.Context r1 = r4.mContext
                int r5 = com.android.settings.aikey.PreferenceHelper.AiSettingsPreferenceHelper.getPressAiButtonSettings(r1, r5)
                if (r5 != r3) goto L6f
                goto L70
            L38:
                java.lang.String r1 = "key_double_click_ai_button_settings"
                boolean r1 = r1.equals(r5)
                if (r1 == 0) goto L4b
                android.content.Context r1 = r4.mContext
                int r5 = com.android.settings.aikey.PreferenceHelper.AiSettingsPreferenceHelper.getPressAiButtonSettings(r1, r5)
                if (r5 != r3) goto L6f
                r5 = 5
            L49:
                r3 = r5
                goto L70
            L4b:
                java.lang.String r1 = "key_long_press_down_ai_button_settings"
                boolean r1 = r1.equals(r5)
                if (r1 == 0) goto L5d
                android.content.Context r1 = r4.mContext
                int r5 = com.android.settings.aikey.PreferenceHelper.AiSettingsPreferenceHelper.getPressAiButtonSettings(r1, r5)
                if (r5 != r3) goto L6f
                r5 = 3
                goto L49
            L5d:
                java.lang.String r1 = "key_long_press_up_ai_button_settings"
                boolean r1 = r1.equals(r5)
                if (r1 == 0) goto L6f
                android.content.Context r1 = r4.mContext
                int r5 = com.android.settings.aikey.PreferenceHelper.AiSettingsPreferenceHelper.getPressAiButtonSettings(r1, r5)
                if (r5 != r3) goto L6f
                r3 = 4
                goto L70
            L6f:
                r3 = r2
            L70:
                if (r3 != r2) goto L78
                com.android.settings.aikey.AidaemonService$EmptyIntentAction r4 = new com.android.settings.aikey.AidaemonService$EmptyIntentAction
                r4.<init>()
                return r4
            L78:
                com.android.settings.aikey.AidaemonService$IntentAction r4 = r4.chooseInner(r3)
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.aikey.AidaemonService.Chooser.choose(android.content.Intent):com.android.settings.aikey.AidaemonService$IntentAction");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class EmptyIntentAction implements IntentAction {
        private EmptyIntentAction() {
        }

        @Override // com.android.settings.aikey.AidaemonService.IntentAction
        public void go(Context context) {
            Log.e("AidaemonService", "EmptyIntentAction --------------");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface IntentAction {
        void go(Context context);
    }

    /* loaded from: classes.dex */
    public static class OpenVoiceAssistantIntentAction implements IntentAction {
        private int mLaunchMode;

        public OpenVoiceAssistantIntentAction(int i) {
            this.mLaunchMode = 1;
            this.mLaunchMode = i;
        }

        private Bundle getBundle() {
            Bundle bundle = new Bundle();
            bundle.putInt("assistant_launch_mode", this.mLaunchMode);
            return bundle;
        }

        @Override // com.android.settings.aikey.AidaemonService.IntentAction
        public void go(Context context) {
            Bundle bundle = getBundle();
            try {
                Log.e("AidaemonService", "OpenVoiceAssistantIntentAction start go launch_key:" + bundle.getInt("assistant_launch_mode", -10));
                ISearchManager.Stub.asInterface(ServiceManager.getService(YellowPageContract.Search.DIRECTORY)).launchAssist(UserHandle.myUserId(), bundle);
                Log.e("AidaemonService", "OpenVoiceAssistantIntentAction end go launch_key:" + bundle.getInt("assistant_launch_mode", -10));
            } catch (Exception e) {
                Log.e("AidaemonService", Log.getStackTraceString(e));
                e.printStackTrace();
            }
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        Log.i("AidaemonService", "AidaemonService---------------------------onStartCommand");
        if (intent == null) {
            return 2;
        }
        new Chooser(this).choose(intent).go(this);
        return 2;
    }
}
