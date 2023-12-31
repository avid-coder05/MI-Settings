package com.android.settings.development;

import android.content.Context;
import android.content.om.IOverlayManager;
import android.content.pm.PackageManager;
import android.os.ServiceManager;

/* loaded from: classes.dex */
public class EmulateDisplayCutoutPreferenceController extends OverlayCategoryPreferenceController {
    public EmulateDisplayCutoutPreferenceController(Context context) {
        this(context, context.getPackageManager(), IOverlayManager.Stub.asInterface(ServiceManager.getService("overlay")));
    }

    EmulateDisplayCutoutPreferenceController(Context context, PackageManager packageManager, IOverlayManager iOverlayManager) {
        super(context, packageManager, iOverlayManager, "com.android.internal.display_cutout_emulation");
    }

    @Override // com.android.settings.development.OverlayCategoryPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "display_cutout_emulation";
    }
}
