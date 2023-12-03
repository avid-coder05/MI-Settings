package com.android.settings.search;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.view.IWindowManager;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import java.util.ArrayList;
import java.util.Collections;
import miui.util.FeatureParser;

/* loaded from: classes2.dex */
class KeySettingsUpdateHelper extends BaseSearchUpdateHelper {
    private static final String AI_TOUCH_RESOURCE_GLOBAL = "ai_button_title_global";
    private static final String LONG_PRESS_VOLUME_DOWN_RESOURCE = "long_press_volume_down";
    private static final String SCREEN_BUTTON_HIDE_RESOURCE = "status_bar_screen_button_key_force_immersive";
    private static final String SCREEN_MAX_ASPECT_RATIO_RESOURCE = "screen_max_aspect_ratio_title";
    private static final String SWITCH_SCREEN_BUTTON_ORDER_RESOURCE = "switch_screen_button_order";

    KeySettingsUpdateHelper() {
    }

    private static boolean removeLongPressVolumeDown(Context context) {
        ArrayList arrayList = new ArrayList();
        Collections.addAll(arrayList, context.getResources().getStringArray(R.array.long_press_volume_down_action_value));
        return !FeatureParser.getBoolean("support_camera_quick_snap", false) && arrayList.contains("Street-snap-picture") && arrayList.contains("Street-snap-movie") && Settings.Secure.getInt(context.getContentResolver(), "key_trans_card_in_ese", 0) == 0 && arrayList.contains("public_transportation_shortcuts");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void update(Context context, ArrayList<ContentProviderOperation> arrayList) {
        try {
            if (!IWindowManager.Stub.asInterface(ServiceManager.getService("window")).hasNavigationBar(0)) {
                BaseSearchUpdateHelper.hideByResource(context, arrayList, SCREEN_BUTTON_HIDE_RESOURCE);
                BaseSearchUpdateHelper.hideByResource(context, arrayList, "switch_screen_button_order");
                BaseSearchUpdateHelper.hideByResource(context, arrayList, SCREEN_MAX_ASPECT_RATIO_RESOURCE);
            }
        } catch (RemoteException unused) {
        }
        if (removeLongPressVolumeDown(context)) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, LONG_PRESS_VOLUME_DOWN_RESOURCE);
        }
        if (MiuiUtils.shouldShowAiButton()) {
            return;
        }
        BaseSearchUpdateHelper.hideByResource(context, arrayList, AI_TOUCH_RESOURCE_GLOBAL);
    }
}
