package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import com.android.settings.R;

/* loaded from: classes.dex */
public class AccessibilityServiceUtils {
    private AccessibilityServiceUtils() {
    }

    public static String getAccessibilityServiceFragmentTypeName(AccessibilityServiceInfo accessibilityServiceInfo) {
        String name = VolumeShortcutToggleAccessibilityServicePreferenceFragment.class.getName();
        int accessibilityServiceFragmentType = AccessibilityUtil.getAccessibilityServiceFragmentType(accessibilityServiceInfo);
        if (accessibilityServiceFragmentType != 0) {
            if (accessibilityServiceFragmentType != 1) {
                if (accessibilityServiceFragmentType == 2) {
                    return ToggleAccessibilityServicePreferenceFragment.class.getName();
                }
                throw new AssertionError();
            }
            return InvisibleToggleAccessibilityServicePreferenceFragment.class.getName();
        }
        return name;
    }

    public static CharSequence getServiceDescription(Context context, AccessibilityServiceInfo accessibilityServiceInfo, boolean z) {
        return (z && accessibilityServiceInfo.crashed) ? context.getText(R.string.accessibility_description_state_stopped) : accessibilityServiceInfo.loadDescription(context.getPackageManager());
    }
}
