package com.android.settings.password;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

/* loaded from: classes2.dex */
public class CredentialCheckResultTracker extends Fragment {
    private boolean mHasResult = false;

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }
}
