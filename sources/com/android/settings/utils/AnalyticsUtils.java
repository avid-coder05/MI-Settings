package com.android.settings.utils;

import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;
import miui.content.res.ThemeResources;

/* loaded from: classes2.dex */
public class AnalyticsUtils {
    public static void trackClickLearnBtnEvent(Context context) {
        Intent intent = new Intent("com.android.systemui.action_track_fullscreen_event");
        intent.putExtra("event_name", "click_learn_button");
        intent.setPackage(ThemeResources.SYSTEMUI_NAME);
        context.sendBroadcast(intent);
    }

    public static void trackClickSingleTurorialEvent(Context context, String str) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<String> arrayList2 = new ArrayList<>();
        arrayList.add("tutorial_type");
        arrayList2.add(str);
        Intent intent = new Intent("com.android.systemui.action_track_fullscreen_event");
        intent.putExtra("event_name", "fullscreen_settings_state");
        intent.putStringArrayListExtra("event_param", arrayList);
        intent.putStringArrayListExtra("event_value", arrayList2);
        intent.setPackage(ThemeResources.SYSTEMUI_NAME);
        context.sendBroadcast(intent);
    }

    public static void trackLearnGesturesWindowEvent(Context context) {
        Intent intent = new Intent("com.android.systemui.action_track_fullscreen_event");
        intent.putExtra("event_name", "show_learn_gestures_popup_window");
        intent.setPackage(ThemeResources.SYSTEMUI_NAME);
        context.sendBroadcast(intent);
    }

    public static void trackModifiedFullscreenModeEvent(Context context, String str, String str2) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<String> arrayList2 = new ArrayList<>();
        arrayList.add("pkg");
        arrayList.add("modified_state");
        arrayList2.add(str);
        arrayList2.add(str2);
        Intent intent = new Intent("com.android.systemui.action_track_fullscreen_event");
        intent.putExtra("event_name", "modified_fullscreen_mode");
        intent.putStringArrayListExtra("event_param", arrayList);
        intent.putStringArrayListExtra("event_value", arrayList2);
        intent.setPackage(ThemeResources.SYSTEMUI_NAME);
        context.sendBroadcast(intent);
    }
}
