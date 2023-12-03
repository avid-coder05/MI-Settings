package miuix.appcompat.widget;

import android.view.View;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.widget.dialoganim.IDialogAnim;
import miuix.appcompat.widget.dialoganim.PadDialogAnim;
import miuix.appcompat.widget.dialoganim.PhoneDialogAnim;
import miuix.internal.util.DeviceHelper;

/* loaded from: classes5.dex */
public class DialogAnimHelper {
    private static IDialogAnim sDialogAnim;

    /* loaded from: classes5.dex */
    public interface OnDismiss {
        void end();
    }

    public static void cancelAnimator() {
        IDialogAnim iDialogAnim = sDialogAnim;
        if (iDialogAnim != null) {
            iDialogAnim.cancelAnimator();
        }
    }

    public static void executeDismissAnim(View view, View view2, OnDismiss onDismiss) {
        IDialogAnim iDialogAnim = sDialogAnim;
        if (iDialogAnim != null) {
            iDialogAnim.executeDismissAnim(view, view2, onDismiss);
        }
    }

    public static void executeShowAnim(View view, View view2, boolean z, AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener) {
        if (sDialogAnim == null) {
            if (!DeviceHelper.isTablet(view.getContext()) || DeviceHelper.isFoldDevice()) {
                sDialogAnim = new PhoneDialogAnim();
            } else {
                sDialogAnim = new PadDialogAnim();
            }
        }
        sDialogAnim.executeShowAnim(view, view2, z, onDialogShowAnimListener);
    }
}
