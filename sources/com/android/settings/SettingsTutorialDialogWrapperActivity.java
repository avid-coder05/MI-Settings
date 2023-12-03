package com.android.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import com.android.settings.accessibility.AccessibilityGestureNavigationTutorial;

/* loaded from: classes.dex */
public class SettingsTutorialDialogWrapperActivity extends Activity {
    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showDialog$0(DialogInterface dialogInterface) {
        finish();
    }

    private void showDialog() {
        AccessibilityGestureNavigationTutorial.showGestureNavigationSettingsTutorialDialog(this, new DialogInterface.OnDismissListener() { // from class: com.android.settings.SettingsTutorialDialogWrapperActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                SettingsTutorialDialogWrapperActivity.this.lambda$showDialog$0(dialogInterface);
            }
        });
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        showDialog();
    }
}
