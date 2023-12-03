package miuix.os;

import miuix.core.util.SystemProperties;

/* loaded from: classes5.dex */
public class Build {
    public static final boolean IS_INTERNATIONAL_BUILD = SystemProperties.get("ro.product.mod_device", "").contains("_global");
}
