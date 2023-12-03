package com.android.settings.ai;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.ActivityOptions;
import android.app.IActivityManager;
import android.app.ISearchManager;
import android.app.KeyguardManager;
import android.app.SearchManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ParceledListSlice;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.ai.PreferenceHelper;
import java.util.List;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class AidaemonService extends Service {

    /* loaded from: classes.dex */
    private class Chooser {
        private final String PRESS_TYPE = "key_ai_button_settings";
        private Context mContext;

        public Chooser(Context context) {
            this.mContext = context;
        }

        private IntentAction chooseInner(AiSettingsItem aiSettingsItem) {
            int i = aiSettingsItem.type;
            if (1 == i) {
                return new OpenVoiceAssistantIntentAction(aiSettingsItem.voiceAssistantMode);
            }
            return 2 == i ? new OpenGoogleSearchIntentAction() : 3 == i ? new OpenCameraIntentAction.OpenCameraBackIntentAction() : 4 == i ? new OpenCameraIntentAction.OpenCameraFrontIntentAction() : 5 == i ? new OpenPreviousApplicationIntentAction() : 6 == i ? new OpenFlashLightIntentAction() : 7 == i ? new OpenProtectEyeModeIntentAction() : 8 == i ? new EmptyIntentAction() : new EmptyIntentAction();
        }

        public IntentAction choose(Intent intent) {
            String stringExtra;
            if (DataFactory.isDeviceProvisioned(this.mContext) && intent != null && (stringExtra = intent.getStringExtra("key_ai_button_settings")) != null) {
                AiSettingsItem pressAiButtonSettings = PreferenceHelper.AiSettingsPreferenceHelper.getPressAiButtonSettings(this.mContext, stringExtra);
                if (!stringExtra.equals("key_long_press_up_ai_button_settings") || pressAiButtonSettings.type == 1) {
                    DataFactory.record(this.mContext, stringExtra, pressAiButtonSettings);
                    return chooseInner(pressAiButtonSettings);
                }
                return new EmptyIntentAction();
            }
            return new EmptyIntentAction();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class EmptyIntentAction implements IntentAction {
        private EmptyIntentAction() {
        }

        @Override // com.android.settings.ai.AidaemonService.IntentAction
        public void go(Context context) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface IntentAction {
        void go(Context context);
    }

    /* loaded from: classes.dex */
    protected static abstract class OpenCameraIntentAction implements IntentAction {

        /* JADX INFO: Access modifiers changed from: protected */
        /* loaded from: classes.dex */
        public static class OpenCameraBackIntentAction extends OpenCameraIntentAction {
            protected OpenCameraBackIntentAction() {
            }

            @Override // com.android.settings.ai.AidaemonService.OpenCameraIntentAction
            protected void putDifferentExtraToCameraIntent(Intent intent) {
                intent.putExtra("android.intent.extras.CAMERA_FACING", 0);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* loaded from: classes.dex */
        public static class OpenCameraFrontIntentAction extends OpenCameraIntentAction {
            protected OpenCameraFrontIntentAction() {
            }

            @Override // com.android.settings.ai.AidaemonService.OpenCameraIntentAction
            protected void putDifferentExtraToCameraIntent(Intent intent) {
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            }
        }

        protected OpenCameraIntentAction() {
        }

        @Override // com.android.settings.ai.AidaemonService.IntentAction
        public void go(Context context) {
            Intent intent = new Intent();
            intent.setFlags(343932928);
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService("keyguard");
            if (keyguardManager != null && keyguardManager.isKeyguardLocked()) {
                intent.putExtra("ShowCameraWhenLocked", true);
                intent.putExtra("StartActivityWhenLocked", true);
            }
            putDifferentExtraToCameraIntent(intent);
            intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
            intent.setComponent(new ComponentName("com.android.camera", "com.android.camera.Camera"));
            context.startActivity(intent);
        }

        protected abstract void putDifferentExtraToCameraIntent(Intent intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class OpenFlashLightIntentAction implements IntentAction {
        private OpenFlashLightIntentAction() {
        }

        @Override // com.android.settings.ai.AidaemonService.IntentAction
        public void go(Context context) {
            Intent intent = new Intent("miui.intent.action.TOGGLE_TORCH");
            intent.putExtra("miui.intent.extra.IS_TOGGLE", true);
            context.sendBroadcast(intent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class OpenGoogleSearchIntentAction implements IntentAction {
        private OpenGoogleSearchIntentAction() {
        }

        private ComponentName getComponentName(Context context) {
            return new ComponentName(context, AidaemonService.class);
        }

        @Override // com.android.settings.ai.AidaemonService.IntentAction
        public void go(Context context) {
            ((SearchManager) context.getSystemService(YellowPageContract.Search.DIRECTORY)).startSearch(null, false, getComponentName(context), null, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class OpenPreviousApplicationIntentAction implements IntentAction {
        private OpenPreviousApplicationIntentAction() {
        }

        @Override // com.android.settings.ai.AidaemonService.IntentAction
        public void go(Context context) {
            IActivityManager iActivityManager = ActivityManagerNative.getDefault();
            if (iActivityManager == null) {
                return;
            }
            List list = null;
            try {
                ParceledListSlice recentTasks = iActivityManager.getRecentTasks(2, 0, UserHandle.myUserId());
                if (recentTasks != null) {
                    list = recentTasks.getList();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (list == null || list.size() < 2) {
                return;
            }
            try {
                iActivityManager.startActivityFromRecents(((ActivityManager.RecentTaskInfo) list.get(1)).persistentId, ActivityOptions.makeCustomAnimation(context, R.anim.recents_quick_switch_left_enter, R.anim.recents_quick_switch_left_exit).toBundle());
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class OpenProtectEyeModeIntentAction implements IntentAction {
        private OpenProtectEyeModeIntentAction() {
        }

        private void setPaperMode(Context context, boolean z) {
            MiuiSettings.System.putBoolean(context.getContentResolver(), "screen_paper_mode_enabled", z);
        }

        @Override // com.android.settings.ai.AidaemonService.IntentAction
        public void go(Context context) {
            setPaperMode(context, !MiuiUtils.isPaperModeEnable(context));
        }
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

        @Override // com.android.settings.ai.AidaemonService.IntentAction
        public void go(Context context) {
            try {
                ISearchManager.Stub.asInterface(ServiceManager.getService(YellowPageContract.Search.DIRECTORY)).launchAssist(UserHandle.myUserId(), getBundle());
            } catch (Exception e) {
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
        if (intent == null) {
            return 2;
        }
        new Chooser(this).choose(intent).go(this);
        return 2;
    }
}
