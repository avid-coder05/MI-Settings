package com.iqiyi.android.qigsaw.core.extension.fakecomponents;

import android.app.Activity;
import android.os.Bundle;

/* loaded from: classes2.dex */
public class FakeActivity extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        int releaseFixedOrientation = OrientationCompat.releaseFixedOrientation(this);
        super.onCreate(bundle);
        OrientationCompat.fixedOrientation(this, releaseFixedOrientation);
        if (getIntent() != null) {
            setIntent(null);
        }
        finish();
    }
}
