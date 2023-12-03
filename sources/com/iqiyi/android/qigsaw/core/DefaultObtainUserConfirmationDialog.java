package com.iqiyi.android.qigsaw.core;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import java.text.DecimalFormat;

/* loaded from: classes2.dex */
public class DefaultObtainUserConfirmationDialog extends ObtainUserConfirmationDialog {
    private boolean fromUserClick;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.iqiyi.android.qigsaw.core.ObtainUserConfirmationDialog, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (checkInternParametersIllegal()) {
            finish();
            return;
        }
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getWindow().setLayout(-1, -2);
        setFinishOnTouchOutside(false);
        new DecimalFormat("#.00");
        getRealTotalBytesNeedToDownload();
    }
}
