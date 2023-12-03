package com.android.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import miui.system.Manifest;

/* loaded from: classes.dex */
public class MiuiGxzwAnimSettingsActivity extends Activity {
    private void startAnotherActivityForResult() {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.MiuiGxzwAnimSettingsInternalActivity");
        startActivityForResult(intent, 101);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        setResult(i2);
        finish();
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (checkCallingOrSelfPermission(Manifest.permission.USE_INTERNAL_GENERAL_API) != 0) {
            throw new SecurityException("Need miui.permission.USE_INTERNAL_GENERAL_API permission to access");
        }
        startAnotherActivityForResult();
    }
}
