package com.android.settings.display.util;

import android.os.SystemProperties;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class PaperConstants {
    public static final float DEFAULT_TEXTURE_MODE_LEVEL;
    public static final int PAPER_MODE_MAX_LEVEL = SystemProperties.getInt("sys.paper_mode_max_level", Math.round(FeatureParser.getFloat("paper_mode_max_level", 8.0f).floatValue()));
    public static final int DEFAULT_TEXTURE_EYECARE_LEVEL = FeatureParser.getInteger("paper_eyecare_default_texture", 13);

    static {
        DEFAULT_TEXTURE_MODE_LEVEL = SystemProperties.getInt("sys.paper_mode_default_level", (r0 / 8) * 5);
    }
}
