package com.android.settings.accessibility.accessibilitymenu.utils;

import com.android.settings.R;
import com.android.settings.accessibility.accessibilitymenu.model.A11yMenuShortcut;
import miui.os.Build;

/* loaded from: classes.dex */
public final class A11yMenuUtils {
    private static final int[] GOOGLE_ASSISTANT_RESOURCE;
    private static final int[][] SHORTCUT_RESOURCE;
    private static final int[] VOICE_ASSIST_RESOURCE;
    private static final int[] XIAOAI_ASSISTANT_RESOURCE;

    static {
        int i = R.string.google_assistant_label;
        int[] iArr = {R.drawable.button_accessibility_menu_google_assistant_selector, i, i};
        GOOGLE_ASSISTANT_RESOURCE = iArr;
        int i2 = R.string.voice_assist;
        int[] iArr2 = {R.drawable.button_accessibility_menu_assistant_selector, i2, i2};
        XIAOAI_ASSISTANT_RESOURCE = iArr2;
        if (!Build.IS_GLOBAL_BUILD) {
            iArr = iArr2;
        }
        VOICE_ASSIST_RESOURCE = iArr;
        int i3 = R.string.a11y_settings_label;
        int[] iArr3 = {R.drawable.button_accessibility_menu_settings_accessibility_selector, i3, i3};
        int i4 = R.string.power_label;
        int[] iArr4 = {R.drawable.button_accessibility_menu_power_settings_selector, i4, i4};
        int i5 = R.string.quick_settings_label;
        int[] iArr5 = {R.drawable.button_accessibility_menu_quick_settings_selector, i5, i5};
        int i6 = R.string.notifications_label;
        int[] iArr6 = {R.drawable.button_accessibility_menu_notifications_selector, i6, i6};
        int i7 = R.string.screenshot_label;
        int[] iArr7 = {R.drawable.button_accessibility_menu_screenshot_selector, i7, i7};
        int i8 = R.string.lockscreen_label;
        int[] iArr8 = {R.drawable.button_accessibility_menu_lock_settings_selector, i8, i8};
        int i9 = R.string.recent_apps_label;
        int[] iArr9 = {R.drawable.button_accessibility_menu_view_carousel_selector, i9, i9};
        int i10 = R.string.volume_label;
        SHORTCUT_RESOURCE = new int[][]{iArr, iArr3, iArr4, iArr5, iArr6, iArr7, iArr8, iArr9, new int[]{R.drawable.button_accessibility_menu_volume_settings_selector, i10, i10}};
    }

    public static void setShortcutResByShortcutId(int i, A11yMenuShortcut a11yMenuShortcut) {
        int[][] iArr = SHORTCUT_RESOURCE;
        a11yMenuShortcut.imageSrc = iArr[i][0];
        a11yMenuShortcut.imgContentDescription = iArr[i][1];
        a11yMenuShortcut.labelText = iArr[i][2];
    }
}
