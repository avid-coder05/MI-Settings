package com.android.settings.biometrics.face;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.hardware.face.Face;
import android.hardware.face.FaceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.SettingsActivity;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.widget.LayoutPreference;
import com.google.android.setupdesign.util.ButtonStyler;
import com.google.android.setupdesign.util.PartnerStyleHelper;
import java.util.List;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class FaceSettingsRemoveButtonPreferenceController extends BasePreferenceController implements View.OnClickListener {
    static final String KEY = "security_settings_face_delete_faces_container";
    private static final String TAG = "FaceSettings/Remove";
    private SettingsActivity mActivity;
    private Button mButton;
    private final Context mContext;
    private final FaceManager mFaceManager;
    private Listener mListener;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final DialogInterface.OnClickListener mOnClickListener;
    private Preference mPreference;
    private final FaceManager.RemovalCallback mRemovalCallback;
    private boolean mRemoving;
    private int mUserId;

    /* loaded from: classes.dex */
    public static class ConfirmRemoveDialog extends InstrumentedDialogFragment {
        private DialogInterface.OnClickListener mOnClickListener;

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 1693;
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.security_settings_face_settings_remove_dialog_title).setMessage(R.string.security_settings_face_settings_remove_dialog_details).setPositiveButton(R.string.delete, this.mOnClickListener).setNegativeButton(R.string.cancel, this.mOnClickListener);
            AlertDialog create = builder.create();
            create.setCanceledOnTouchOutside(false);
            return create;
        }

        public void setOnClickListener(DialogInterface.OnClickListener onClickListener) {
            this.mOnClickListener = onClickListener;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface Listener {
        void onRemoved();
    }

    public FaceSettingsRemoveButtonPreferenceController(Context context) {
        this(context, KEY);
    }

    public FaceSettingsRemoveButtonPreferenceController(Context context, String str) {
        super(context, str);
        this.mRemovalCallback = new FaceManager.RemovalCallback() { // from class: com.android.settings.biometrics.face.FaceSettingsRemoveButtonPreferenceController.1
            public void onRemovalError(Face face, int i, CharSequence charSequence) {
                Log.e(FaceSettingsRemoveButtonPreferenceController.TAG, "Unable to remove face: " + face.getBiometricId() + " error: " + i + " " + ((Object) charSequence));
                Toast.makeText(FaceSettingsRemoveButtonPreferenceController.this.mContext, charSequence, 0).show();
                FaceSettingsRemoveButtonPreferenceController.this.mRemoving = false;
            }

            public void onRemovalSucceeded(Face face, int i) {
                if (i != 0) {
                    Log.v(FaceSettingsRemoveButtonPreferenceController.TAG, "Remaining: " + i);
                } else if (!FaceSettingsRemoveButtonPreferenceController.this.mFaceManager.getEnrolledFaces(FaceSettingsRemoveButtonPreferenceController.this.mUserId).isEmpty()) {
                    FaceSettingsRemoveButtonPreferenceController.this.mButton.setEnabled(true);
                } else {
                    FaceSettingsRemoveButtonPreferenceController.this.mRemoving = false;
                    FaceSettingsRemoveButtonPreferenceController.this.mListener.onRemoved();
                }
            }
        };
        this.mOnClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.biometrics.face.FaceSettingsRemoveButtonPreferenceController.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i != -1) {
                    FaceSettingsRemoveButtonPreferenceController.this.mButton.setEnabled(true);
                    FaceSettingsRemoveButtonPreferenceController.this.mRemoving = false;
                    return;
                }
                FaceSettingsRemoveButtonPreferenceController.this.mButton.setEnabled(false);
                List enrolledFaces = FaceSettingsRemoveButtonPreferenceController.this.mFaceManager.getEnrolledFaces(FaceSettingsRemoveButtonPreferenceController.this.mUserId);
                if (enrolledFaces.isEmpty()) {
                    Log.e(FaceSettingsRemoveButtonPreferenceController.TAG, "No faces");
                    return;
                }
                if (enrolledFaces.size() > 1) {
                    Log.e(FaceSettingsRemoveButtonPreferenceController.TAG, "Multiple enrollments: " + enrolledFaces.size());
                }
                FaceSettingsRemoveButtonPreferenceController.this.mFaceManager.remove((Face) enrolledFaces.get(0), FaceSettingsRemoveButtonPreferenceController.this.mUserId, FaceSettingsRemoveButtonPreferenceController.this.mRemovalCallback);
            }
        };
        this.mContext = context;
        this.mFaceManager = (FaceManager) context.getSystemService(FaceManager.class);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view == this.mButton) {
            this.mMetricsFeatureProvider.logClickedPreference(this.mPreference, getMetricsCategory());
            this.mRemoving = true;
            new ConfirmRemoveDialog().setOnClickListener(this.mOnClickListener);
        }
    }

    public void setActivity(SettingsActivity settingsActivity) {
        this.mActivity = settingsActivity;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void setUserId(int i) {
        this.mUserId = i;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mPreference = preference;
        Button button = (Button) ((LayoutPreference) preference).findViewById(R.id.security_settings_face_settings_remove_button);
        this.mButton = button;
        if (PartnerStyleHelper.shouldApplyPartnerResource(button)) {
            ButtonStyler.applyPartnerCustomizationPrimaryButtonStyle(this.mContext, this.mButton);
        }
        this.mButton.setOnClickListener(this);
        if (FaceSettings.isFaceHardwareDetected(this.mContext)) {
            this.mButton.setEnabled(!this.mRemoving);
        } else {
            this.mButton.setEnabled(false);
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
