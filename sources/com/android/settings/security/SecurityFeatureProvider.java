package com.android.settings.security;

import android.content.Context;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.security.trustagent.TrustAgentManager;

/* loaded from: classes2.dex */
public interface SecurityFeatureProvider {
    LockPatternUtils getLockPatternUtils(Context context);

    TrustAgentManager getTrustAgentManager();
}
