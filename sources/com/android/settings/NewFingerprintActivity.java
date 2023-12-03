package com.android.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.android.settingslib.util.ToastUtil;
import miui.system.Manifest;

/* loaded from: classes.dex */
public class NewFingerprintActivity extends Activity {
    private boolean mNeedToManager;

    private void startAnotherActivityForResult(Class<?> cls, int i) {
        Intent intent = new Intent(this, cls);
        intent.putExtra("need_to_manager", this.mNeedToManager);
        startActivityForResult(intent, i);
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
        if (MiuiSecuritySettings.isMiShowMode(this)) {
            ToastUtil.show(this, R.string.mishow_disable_password_setting, 0);
            finish();
            return;
        }
        this.mNeedToManager = getIntent().getBooleanExtra("need_to_manager", false);
        if (checkCallingOrSelfPermission(Manifest.permission.USE_INTERNAL_GENERAL_API) != 0) {
            throw new SecurityException("Need miui.permission.USE_INTERNAL_GENERAL_API permission to access");
        }
        startAnotherActivityForResult(NewFingerprintInternalActivity.class, 101);
    }
}
