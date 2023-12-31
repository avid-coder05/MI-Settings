package com.android.settings.notification.zen;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import com.android.settingslib.CustomDialogPreferenceCompat;
import com.android.settingslib.notification.ZenDurationDialog;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class ZenDurationDialogPreference extends CustomDialogPreferenceCompat {
    public ZenDurationDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setSingleLineTitle(false);
    }

    public ZenDurationDialogPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setSingleLineTitle(false);
    }

    public ZenDurationDialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setSingleLineTitle(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        super.onPrepareDialogBuilder(builder, onClickListener);
        new ZenDurationDialog(getContext()).setupDialog(builder);
    }
}
