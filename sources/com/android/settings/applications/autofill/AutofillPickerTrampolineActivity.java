package com.android.settings.applications.autofill;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.autofill.AutofillManager;

/* loaded from: classes.dex */
public class AutofillPickerTrampolineActivity extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        AutofillManager autofillManager = (AutofillManager) getSystemService(AutofillManager.class);
        if (autofillManager == null || !autofillManager.hasAutofillFeature() || !autofillManager.isAutofillSupported()) {
            setResult(0);
            finish();
            return;
        }
        Intent intent = getIntent();
        String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
        ComponentName autofillServiceComponentName = autofillManager.getAutofillServiceComponentName();
        if (autofillServiceComponentName == null || !autofillServiceComponentName.getPackageName().equals(schemeSpecificPart)) {
            startActivity(new Intent(this, AutofillPickerActivity.class).setFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_PIP_SCREEN_PROJECTION).setData(intent.getData()));
            finish();
            return;
        }
        setResult(-1);
        finish();
    }
}
