package com.android.settings.support.actionbar;

import com.android.settings.R;

/* loaded from: classes2.dex */
public interface HelpResourceProvider {
    default int getHelpResource() {
        return R.string.help_uri_default;
    }
}
