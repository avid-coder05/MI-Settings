package com.android.settings.security;

import android.content.Context;

/* loaded from: classes2.dex */
public class InstallCertificatePreferenceController extends RestrictedEncryptionPreferenceController {
    public InstallCertificatePreferenceController(Context context) {
        super(context, "no_config_credentials");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "install_certificate";
    }
}
