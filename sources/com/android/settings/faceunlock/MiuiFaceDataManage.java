package com.android.settings.faceunlock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.KeyguardSettingsPreferenceFragment;
import com.android.settings.R;
import com.android.settings.SettingsCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiFaceDataManage extends SettingsCompatActivity {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AlwaysClickablePreference extends Preference {
        private Preference.OnPreferenceClickListener mOnClickListener;

        public AlwaysClickablePreference(Context context) {
            super(context);
        }

        @Override // androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            super.onBindViewHolder(preferenceViewHolder);
            preferenceViewHolder.itemView.setEnabled(true);
        }

        @Override // androidx.preference.Preference
        public void performClick() {
            Preference.OnPreferenceClickListener onPreferenceClickListener = this.mOnClickListener;
            if (onPreferenceClickListener != null) {
                onPreferenceClickListener.onPreferenceClick(this);
            }
        }

        @Override // androidx.preference.Preference
        public void setOnPreferenceClickListener(Preference.OnPreferenceClickListener onPreferenceClickListener) {
            this.mOnClickListener = onPreferenceClickListener;
        }
    }

    /* loaded from: classes.dex */
    public static class FaceManageFragment extends KeyguardSettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
        private Preference mDeleteFaceData;
        private CheckBoxPreference mFaceDataApplyUnlockScreen;
        private PreferenceCategory mFaceDataCategory;
        private PreferenceCategory mFaceDataDeleteCategory;
        private PreferenceCategory mFaceDataListCategory;
        private CheckBoxPreference mFaceUnlockByNotificationPreference;
        private KeyguardSettingsFaceUnlockManager mFaceUnlockManager;
        private CheckBoxPreference mFaceUnlockSuccessShowMessage;
        private CheckBoxPreference mFaceUnlockSuccessStayScreen;
        private Toast mToast;
        private List<String> mEnrolledFaceIdList = new ArrayList();
        final FaceRemoveCallback callback = new FaceRemoveCallback() { // from class: com.android.settings.faceunlock.MiuiFaceDataManage.FaceManageFragment.4
            @Override // com.android.settings.faceunlock.FaceRemoveCallback
            public void onFailed() {
                Toast.makeText(FaceManageFragment.this.getActivity(), R.string.structure_face_data_delete_fail, 0).show();
                FaceManageFragment.this.finish();
            }

            @Override // com.android.settings.faceunlock.FaceRemoveCallback
            public void onRemoved() {
                FaceManageFragment.this.finish();
            }
        };

        /* JADX INFO: Access modifiers changed from: private */
        public void addFaceData() {
            List<String> list = this.mEnrolledFaceIdList;
            if (list == null || list.size() < 2) {
                Intent intent = new Intent();
                intent.setClassName("com.android.settings", "com.android.settings.faceunlock.MiuiFaceDataInput");
                startActivity(intent);
                finish();
                return;
            }
            Toast toast = this.mToast;
            if (toast != null) {
                toast.cancel();
            }
            Toast makeText = Toast.makeText(getActivity(), R.string.multi_face_number_reach_limit, 0);
            this.mToast = makeText;
            makeText.show();
        }

        private void handleFaceUnlockApplyForLock(boolean z) {
            boolean z2 = false;
            Settings.Secure.putIntForUser(getContentResolver(), "face_unlcok_apply_for_lock", z ? 1 : 0, 0);
            this.mFaceUnlockSuccessStayScreen.setEnabled(z);
            CheckBoxPreference checkBoxPreference = this.mFaceUnlockSuccessShowMessage;
            if (z && this.mFaceUnlockSuccessStayScreen.isChecked()) {
                z2 = true;
            }
            checkBoxPreference.setEnabled(z2);
            this.mFaceUnlockByNotificationPreference.setEnabled(z);
        }

        private void handleFaceUnlockSuccessShowMessage(boolean z) {
            Settings.Secure.putIntForUser(getContentResolver(), "face_unlock_success_show_message", z ? 1 : 0, 0);
            if (z) {
                if (Settings.Secure.getIntForUser(getContentResolver(), "lock_screen_show_notifications", 0, 0) != 0) {
                    return;
                }
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.faceunlock.MiuiFaceDataManage.FaceManageFragment.5
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == -1) {
                            Settings.Secure.putInt(FaceManageFragment.this.getContentResolver(), "lock_screen_show_notifications", 1);
                            Settings.Secure.putInt(FaceManageFragment.this.getContentResolver(), "lock_screen_allow_private_notifications", 0);
                            Toast.makeText(FaceManageFragment.this.getActivity(), R.string.face_unlock_success_show_message_toast, 0).show();
                        }
                    }
                };
                new AlertDialog.Builder(getActivity()).setCancelable(false).setIconAttribute(16843605).setTitle(R.string.face_unlock_success_show_message_dialog_title).setMessage(R.string.face_unlock_success_show_message_dialog_msg).setNegativeButton(R.string.face_unlock_success_show_message_dialog_negative_btn, onClickListener).setPositiveButton(R.string.face_unlock_success_show_message_dialog_positive_btn, onClickListener).create().show();
            }
        }

        private void handleFaceUnlockSuccessStayScreen(boolean z) {
            boolean z2 = false;
            Settings.Secure.putIntForUser(getContentResolver(), "face_unlock_success_stay_screen", z ? 1 : 0, 0);
            CheckBoxPreference checkBoxPreference = this.mFaceUnlockSuccessShowMessage;
            if (z && this.mFaceUnlockSuccessStayScreen.isEnabled()) {
                z2 = true;
            }
            checkBoxPreference.setEnabled(z2);
        }

        private void handleSecurityLockToggle() {
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.faceunlock.MiuiFaceDataManage.FaceManageFragment.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == -1) {
                        FaceManageFragment.this.mFaceUnlockManager.deleteFeature("0", FaceManageFragment.this.callback);
                    }
                }
            };
            new AlertDialog.Builder(getActivity()).setCancelable(false).setIconAttribute(16843605).setTitle(R.string.face_data_manage_delete).setMessage(R.string.face_data_manage_delete_sure).setPositiveButton(17039370, onClickListener).setNegativeButton(17039360, onClickListener).create().show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void showFaceDataDetails(String str, String str2) {
            Bundle bundle = new Bundle();
            bundle.putString("extra_face_key", str);
            bundle.putString("extra_face_title", str2);
            startFragment(this, MiuiFaceDetailFragment.class.getName(), 1097, bundle, R.string.empty_title);
        }

        private void sortFaceList() {
            final HashMap hashMap = new HashMap();
            for (String str : this.mEnrolledFaceIdList) {
                hashMap.put(str, Long.valueOf(KeyguardSettingsFaceUnlockUtils.getFaceDataCreateDate(getActivity(), str)));
            }
            Collections.sort(this.mEnrolledFaceIdList, new Comparator<String>() { // from class: com.android.settings.faceunlock.MiuiFaceDataManage.FaceManageFragment.2
                @Override // java.util.Comparator
                public int compare(String str2, String str3) {
                    try {
                        return (int) ((((Long) hashMap.get(str2)).longValue() - ((Long) hashMap.get(str3)).longValue()) / 1000);
                    } catch (Exception unused) {
                        return 0;
                    }
                }
            });
        }

        private void updateFaceList() {
            this.mEnrolledFaceIdList = KeyguardSettingsFaceUnlockUtils.getEnrolledFaceList(getActivity());
            sortFaceList();
            Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() { // from class: com.android.settings.faceunlock.MiuiFaceDataManage.FaceManageFragment.1
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    String key = preference.getKey();
                    String str = (String) preference.getTitle();
                    if ("add_face_data".equals(key)) {
                        FaceManageFragment.this.addFaceData();
                        return true;
                    }
                    FaceManageFragment.this.showFaceDataDetails(key, str);
                    return true;
                }
            };
            this.mFaceDataListCategory.removeAll();
            for (int i = 0; i < this.mEnrolledFaceIdList.size(); i++) {
                String str = this.mEnrolledFaceIdList.get(i);
                Preference preference = new Preference(getPreferenceManager().getContext());
                preference.setKey(str);
                String faceDataName = KeyguardSettingsFaceUnlockUtils.getFaceDataName(getActivity(), str);
                if (TextUtils.isEmpty(faceDataName)) {
                    faceDataName = KeyguardSettingsFaceUnlockUtils.generateFaceDataName(getActivity(), this.mEnrolledFaceIdList);
                    KeyguardSettingsFaceUnlockUtils.setFaceDataName(getActivity(), str, faceDataName);
                }
                preference.setTitle(faceDataName);
                preference.setOnPreferenceClickListener(onPreferenceClickListener);
                this.mFaceDataListCategory.addPreference(preference);
            }
            AlwaysClickablePreference alwaysClickablePreference = new AlwaysClickablePreference(getPreferenceManager().getContext());
            alwaysClickablePreference.setKey("add_face_data");
            alwaysClickablePreference.setTitle(R.string.multi_face_input);
            alwaysClickablePreference.setOnPreferenceClickListener(onPreferenceClickListener);
            this.mFaceDataListCategory.addPreference(alwaysClickablePreference);
            List<String> list = this.mEnrolledFaceIdList;
            if (list == null || list.size() < 2) {
                alwaysClickablePreference.setEnabled(true);
            } else {
                alwaysClickablePreference.setEnabled(false);
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment
        public String getName() {
            return FaceManageFragment.class.getName();
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            addPreferencesFromResource(R.xml.face_data_manage);
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            this.mFaceUnlockManager = KeyguardSettingsFaceUnlockManager.getInstance(getActivity());
            this.mFaceDataCategory = (PreferenceCategory) preferenceScreen.findPreference("lock_screen_face_data");
            this.mFaceDataListCategory = (PreferenceCategory) preferenceScreen.findPreference("multi_face_list");
            this.mFaceDataDeleteCategory = (PreferenceCategory) preferenceScreen.findPreference("delete_face_data");
            this.mDeleteFaceData = preferenceScreen.findPreference("delete_face_data_recoginition");
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preferenceScreen.findPreference("apply_face_data_lock");
            this.mFaceDataApplyUnlockScreen = checkBoxPreference;
            checkBoxPreference.setChecked(Settings.Secure.getIntForUser(getContentResolver(), "face_unlcok_apply_for_lock", 1, 0) == 1);
            this.mFaceDataApplyUnlockScreen.setOnPreferenceChangeListener(this);
            if ("perseus".equals(Build.DEVICE)) {
                this.mFaceDataApplyUnlockScreen.setSummary(R.string.face_data_manage_unlock_slide_msg);
            }
            CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) preferenceScreen.findPreference("face_unlock_success_stay_screen");
            this.mFaceUnlockSuccessStayScreen = checkBoxPreference2;
            checkBoxPreference2.setChecked(Settings.Secure.getIntForUser(getContentResolver(), "face_unlock_success_stay_screen", 0, 0) == 1);
            this.mFaceUnlockSuccessStayScreen.setOnPreferenceChangeListener(this);
            CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) preferenceScreen.findPreference("face_unlock_success_show_message");
            this.mFaceUnlockSuccessShowMessage = checkBoxPreference3;
            checkBoxPreference3.setChecked(Settings.Secure.getIntForUser(getContentResolver(), "face_unlock_success_show_message", 0, 0) == 1);
            this.mFaceUnlockSuccessShowMessage.setOnPreferenceChangeListener(this);
            CheckBoxPreference checkBoxPreference4 = (CheckBoxPreference) preferenceScreen.findPreference("face_unlock_by_notification_screen_on");
            this.mFaceUnlockByNotificationPreference = checkBoxPreference4;
            checkBoxPreference4.setChecked(Settings.Secure.getIntForUser(getContentResolver(), "face_unlock_by_notification_screen_on", 0, 0) == 1);
            this.mFaceUnlockByNotificationPreference.setOnPreferenceChangeListener(this);
            if (KeyguardSettingsFaceUnlockUtils.isSupportMultiFaceInput(getActivity())) {
                preferenceScreen.removePreference(this.mFaceDataDeleteCategory);
            } else {
                preferenceScreen.removePreference(this.mFaceDataListCategory);
            }
            if (KeyguardSettingsFaceUnlockUtils.isSupportLiftingCamera(getActivity())) {
                preferenceScreen.removePreference(this.mFaceDataCategory);
                this.mFaceDataApplyUnlockScreen.setSummary(R.string.face_data_manage_unlock_liftcamera_msg);
            }
            handleFaceUnlockApplyForLock(this.mFaceDataApplyUnlockScreen.isChecked());
            handleFaceUnlockSuccessStayScreen(this.mFaceUnlockSuccessStayScreen.isChecked());
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onPause() {
            super.onPause();
            Toast toast = this.mToast;
            if (toast != null) {
                toast.cancel();
                this.mToast = null;
            }
        }

        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            String key = preference.getKey();
            boolean booleanValue = ((Boolean) obj).booleanValue();
            if ("apply_face_data_lock".equals(key)) {
                handleFaceUnlockApplyForLock(booleanValue);
                return true;
            } else if ("face_unlock_success_stay_screen".equals(key)) {
                handleFaceUnlockSuccessStayScreen(booleanValue);
                return true;
            } else if ("face_unlock_success_show_message".equals(key)) {
                handleFaceUnlockSuccessShowMessage(booleanValue);
                return true;
            } else if ("face_unlock_by_notification_screen_on".equals(key)) {
                Settings.Secure.putIntForUser(getContentResolver(), "face_unlock_by_notification_screen_on", booleanValue ? 1 : 0, 0);
                return true;
            } else {
                return true;
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if ("delete_face_data_recoginition".equals(preference.getKey())) {
                handleSecurityLockToggle();
                return true;
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            updateFaceList();
        }
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", FaceManageFragment.class.getName());
        intent.putExtra(":settings:show_fragment_title_resid", R.string.face_unlock);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return true;
    }

    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(R.string.face_unlock);
    }
}
