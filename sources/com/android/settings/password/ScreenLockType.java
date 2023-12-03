package com.android.settings.password;

import com.android.settings.search.SearchUpdater;

/* loaded from: classes2.dex */
public enum ScreenLockType {
    NONE(0, "unlock_set_off"),
    SWIPE(0, "unlock_set_none"),
    PATTERN(SearchUpdater.GOOGLE, "unlock_set_pattern"),
    PIN(131072, 196608, "unlock_set_pin"),
    PASSWORD(262144, 393216, "unlock_set_password"),
    MANAGED(524288, "unlock_set_managed");

    public final int defaultQuality;
    public final int maxQuality;
    public final String preferenceKey;

    ScreenLockType(int i, int i2, String str) {
        this.defaultQuality = i;
        this.maxQuality = i2;
        this.preferenceKey = str;
    }

    ScreenLockType(int i, String str) {
        this(i, i, str);
    }

    public static ScreenLockType fromKey(String str) {
        for (ScreenLockType screenLockType : values()) {
            if (screenLockType.preferenceKey.equals(str)) {
                return screenLockType;
            }
        }
        return null;
    }

    public static ScreenLockType fromQuality(int i) {
        if (i != 0) {
            if (i != 65536) {
                if (i == 131072 || i == 196608) {
                    return PIN;
                }
                if (i == 262144 || i == 327680 || i == 393216) {
                    return PASSWORD;
                }
                if (i != 524288) {
                    return null;
                }
                return MANAGED;
            }
            return PATTERN;
        }
        return SWIPE;
    }
}
