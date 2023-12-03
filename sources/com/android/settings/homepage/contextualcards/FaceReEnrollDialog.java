package com.android.settings.homepage.contextualcards;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.face.Face;
import android.hardware.face.FaceManager;
import android.os.Bundle;
import android.util.Log;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.homepage.contextualcards.slices.FaceSetupSlice;

/* loaded from: classes.dex */
public class FaceReEnrollDialog extends AlertActivity implements DialogInterface.OnClickListener {
    private FaceManager mFaceManager;
    private int mReEnrollType;

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        removeFaceAndReEnroll();
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        int i = getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.fingerprint") ? R.string.security_settings_face_enroll_improve_face_alert_body_fingerprint : R.string.security_settings_face_enroll_improve_face_alert_body;
        AlertController.AlertParams alertParams = ((AlertActivity) this).mAlertParams;
        alertParams.mTitle = getText(R.string.security_settings_face_enroll_improve_face_alert_title);
        alertParams.mMessage = getText(i);
        alertParams.mPositiveButtonText = getText(R.string.storage_menu_set_up);
        alertParams.mNegativeButtonText = getText(R.string.cancel);
        alertParams.mPositiveButtonListener = this;
        this.mFaceManager = Utils.getFaceManagerOrNull(getApplicationContext());
        this.mReEnrollType = FaceSetupSlice.getReEnrollSetting(getApplicationContext(), getUserId());
        Log.d("FaceReEnrollDialog", "ReEnroll Type : " + this.mReEnrollType);
        int i2 = this.mReEnrollType;
        if (i2 == 1) {
            setupAlert();
        } else if (i2 == 3) {
            removeFaceAndReEnroll();
        } else {
            Log.d("FaceReEnrollDialog", "Error unsupported flow for : " + this.mReEnrollType);
            dismiss();
        }
    }

    public void removeFaceAndReEnroll() {
        int userId = getUserId();
        FaceManager faceManager = this.mFaceManager;
        if (faceManager == null || !faceManager.hasEnrolledTemplates(userId)) {
            finish();
        }
        this.mFaceManager.remove(new Face("", 0, 0L), userId, new FaceManager.RemovalCallback() { // from class: com.android.settings.homepage.contextualcards.FaceReEnrollDialog.1
            public void onRemovalError(Face face, int i, CharSequence charSequence) {
                super.onRemovalError(face, i, charSequence);
                FaceReEnrollDialog.this.finish();
            }

            public void onRemovalSucceeded(Face face, int i) {
                super.onRemovalSucceeded(face, i);
                if (i != 0) {
                    return;
                }
                Intent intent = new Intent("android.settings.BIOMETRIC_ENROLL");
                FaceReEnrollDialog.this.getApplicationContext();
                try {
                    FaceReEnrollDialog.this.startActivity(intent);
                } catch (Exception unused) {
                    Log.e("FaceReEnrollDialog", "Failed to startActivity");
                }
                FaceReEnrollDialog.this.finish();
            }
        });
    }
}
