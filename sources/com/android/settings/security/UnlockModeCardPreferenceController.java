package com.android.settings.security;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.BaseCardViewController;
import com.android.settings.BluetoothUnlockStateController;
import com.android.settings.CardInfo;
import com.android.settings.FaceUnlockStateController;
import com.android.settings.FingerprintUnlockStateController;
import com.android.settings.PasswordUnlockStateController;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class UnlockModeCardPreferenceController extends BasePreferenceController {
    private static final String DISABLE_SECURITY_BY_MISHOW = "disable_security_by_mishow";
    private CardInfo mBluetoothUnlockCard;
    private BluetoothUnlockStateController mBluetoothUnlockController;
    private List<CardInfo> mCardList;
    private Context mContext;
    private List<BaseCardViewController> mControllerList;
    private CardInfo mFaceUnlockCard;
    private FaceUnlockStateController mFaceUnlockController;
    private CardInfo mFingerUnlockCard;
    private FingerprintUnlockStateController mFingerprintUnlockController;
    private Fragment mFragment;
    private CardInfo mPasswordUnlockCard;
    private PasswordUnlockStateController mPasswordUnlockController;
    private String mPerferenceKey;
    private UnlockModeCardPreference mPreferece;

    public UnlockModeCardPreferenceController(Context context, String str, Fragment fragment) {
        super(context, str);
        this.mPasswordUnlockCard = null;
        this.mFingerUnlockCard = null;
        this.mFaceUnlockCard = null;
        this.mBluetoothUnlockCard = null;
        this.mCardList = new ArrayList();
        this.mControllerList = new ArrayList();
        this.mContext = context;
        this.mPerferenceKey = str;
        this.mFragment = fragment;
    }

    private void init() {
        int i = R.drawable.ic_password_unlock;
        int i2 = R.string.password_unlock_title;
        int i3 = R.string.off;
        this.mPasswordUnlockCard = new CardInfo(i, i2, i3);
        this.mFingerUnlockCard = new CardInfo(R.drawable.ic_finger_unlock, R.string.privacy_password_use_finger_dialog_title, i3);
        this.mFaceUnlockCard = new CardInfo(R.drawable.ic_face_unlock, R.string.unlock_set_unlock_biometric_weak_title, i3);
        this.mBluetoothUnlockCard = new CardInfo(R.drawable.ic_bluetooth_unlock, R.string.bluetooth_unlock_title, i3);
        this.mPasswordUnlockController = new PasswordUnlockStateController(this.mContext, this.mPasswordUnlockCard, this.mFragment);
        this.mFingerprintUnlockController = new FingerprintUnlockStateController(this.mContext, this.mFingerUnlockCard, this.mFragment);
        this.mFaceUnlockController = new FaceUnlockStateController(this.mContext, this.mFaceUnlockCard, this.mFragment);
        this.mBluetoothUnlockController = new BluetoothUnlockStateController(this.mContext, this.mBluetoothUnlockCard, this.mFragment);
        if (this.mPasswordUnlockController.isAvailable()) {
            this.mPasswordUnlockCard.setCheckedIconResId(R.drawable.ic_password_unlock_checked);
            this.mPasswordUnlockCard.setOnClickListener(this.mPasswordUnlockController);
            this.mCardList.add(this.mPasswordUnlockCard);
            this.mControllerList.add(this.mPasswordUnlockController);
        }
        if (this.mFingerprintUnlockController.isAvailable()) {
            this.mFingerUnlockCard.setCheckedIconResId(R.drawable.ic_finger_unlock_checked);
            this.mFingerUnlockCard.setOnClickListener(this.mFingerprintUnlockController);
            this.mCardList.add(this.mFingerUnlockCard);
            this.mControllerList.add(this.mFingerprintUnlockController);
        }
        if (this.mFaceUnlockController.isAvailable()) {
            this.mFaceUnlockCard.setCheckedIconResId(R.drawable.ic_face_unlock_checked);
            this.mFaceUnlockCard.setOnClickListener(this.mFaceUnlockController);
            this.mCardList.add(this.mFaceUnlockCard);
            this.mControllerList.add(this.mFaceUnlockController);
        }
        if (this.mBluetoothUnlockController.isAvailable()) {
            this.mBluetoothUnlockCard.setCheckedIconResId(R.drawable.ic_bluetooth_unlock_checked);
            this.mBluetoothUnlockCard.setOnClickListener(this.mBluetoothUnlockController);
            this.mCardList.add(this.mBluetoothUnlockCard);
            this.mControllerList.add(this.mBluetoothUnlockController);
        }
        if (this.mControllerList.size() < 1) {
            this.mPreferece.setVisible(false);
        }
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), DISABLE_SECURITY_BY_MISHOW, 0) == 1) {
            Iterator<CardInfo> it = this.mCardList.iterator();
            while (it.hasNext()) {
                it.next().setDisable(true);
            }
        }
        this.mPreferece.setData(this.mCardList);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        UnlockModeCardPreference unlockModeCardPreference = (UnlockModeCardPreference) preferenceScreen.findPreference(this.mPerferenceKey);
        this.mPreferece = unlockModeCardPreference;
        if (unlockModeCardPreference == null || unlockModeCardPreference.getData() != null) {
            return;
        }
        init();
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

    public void handleActivityResult(int i, int i2, Intent intent) {
        FaceUnlockStateController faceUnlockStateController;
        PasswordUnlockStateController passwordUnlockStateController;
        FingerprintUnlockStateController fingerprintUnlockStateController;
        if (i == 1001 && (fingerprintUnlockStateController = this.mFingerprintUnlockController) != null) {
            fingerprintUnlockStateController.handleActivityResult(i, i2, intent);
        } else if (i == 107 && (passwordUnlockStateController = this.mPasswordUnlockController) != null) {
            passwordUnlockStateController.handleActivityResult(i, i2, intent);
        } else if ((i == 1002 || i == 1) && (faceUnlockStateController = this.mFaceUnlockController) != null) {
            faceUnlockStateController.handleActivityResult(i, i2, intent);
        }
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

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mPreferece == null) {
            return;
        }
        super.updateState(preference);
        Iterator<BaseCardViewController> it = this.mControllerList.iterator();
        while (it.hasNext()) {
            it.next().onResume();
        }
        this.mPreferece.refresh();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
