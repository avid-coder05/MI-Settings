package com.android.settings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.android.settings.MiuiGxzwAnimSettingsInternalActivity;
import com.android.settings.utils.FingerprintUtils;
import com.android.settingslib.util.ToastUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import miui.util.HapticFeedbackUtil;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;
import miuix.pickerwidget.date.Calendar;

/* loaded from: classes.dex */
public class MiuiGxzwAnimSettingsInternalActivity extends SettingsCompatActivity {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class GxzwAnimItem {
        final int drawableChecked;
        final int drawableNormal;
        final int nameRes;
        final int rtpType;
        final int type;
        final String video;

        GxzwAnimItem(int i, int i2, String str, int i3, int i4, int i5) {
            this.type = i;
            this.rtpType = i2;
            this.video = str;
            this.nameRes = i3;
            this.drawableNormal = i4;
            this.drawableChecked = i5;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class GxzwAnimSettingHelper {
        static final List<GxzwAnimItem> sItemList;

        static {
            ArrayList arrayList = new ArrayList();
            sItemList = arrayList;
            arrayList.add(new GxzwAnimItem(6, 159, "gxzw_anim_light", R.string.gxzw_anim_light, R.drawable.gxzw_anim_light_normal, R.drawable.gxzw_anim_light_select));
            arrayList.add(new GxzwAnimItem(7, 158, "gxzw_anim_star", R.string.gxzw_anim_star, R.drawable.gxzw_anim_star_normal, R.drawable.gxzw_anim_star_select));
            arrayList.add(new GxzwAnimItem(8, 160, "gxzw_anim_aurora", R.string.gxzw_anim_aurora, R.drawable.gxzw_anim_aurora_normal, R.drawable.gxzw_anim_aurora_select));
            arrayList.add(new GxzwAnimItem(9, 157, "gxzw_anim_pulse", R.string.gxzw_anim_pulse, R.drawable.gxzw_anim_pulse_normal, R.drawable.gxzw_anim_pulse_select));
        }

        static int getDefaultAnimType() {
            return 6;
        }

        static List<GxzwAnimItem> getGxzwAnimItemList() {
            return sItemList;
        }

        static Set<Integer> getLegalAnimTypeSet() {
            HashSet hashSet = new HashSet();
            Iterator<GxzwAnimItem> it = getGxzwAnimItemList().iterator();
            while (it.hasNext()) {
                hashSet.add(Integer.valueOf(it.next().type));
            }
            return hashSet;
        }
    }

    /* loaded from: classes.dex */
    public static class MiuiGxzwAnimSettingsFragment extends KeyguardSettingsPreferenceFragment {
        private HapticFeedbackUtil mHapticFeedbackUtil;
        private MutedVideoView mVideoView;
        private List<GxzwAnimItem> mGxzwAnimItemList = GxzwAnimSettingHelper.getGxzwAnimItemList();
        private Activity mActivity = null;
        private FingerprintHelper mFingerprintHelper = null;
        private AlertDialog mFingerEnrollDialog = null;

        private GxzwAnimItem findItemByType(int i) {
            for (GxzwAnimItem gxzwAnimItem : this.mGxzwAnimItemList) {
                if (gxzwAnimItem.type == i) {
                    return gxzwAnimItem;
                }
            }
            return null;
        }

        private String formatTime(int i) {
            if (i >= 10) {
                return String.valueOf(i);
            }
            if (i > 0) {
                return "0" + String.valueOf(i);
            }
            return "00";
        }

        private Drawable genenalStateDrawable(Context context, int i, int i2) {
            StateListDrawable stateListDrawable = new StateListDrawable();
            Drawable drawable = context.getResources().getDrawable(i);
            stateListDrawable.addState(new int[]{16842912}, context.getResources().getDrawable(i2));
            stateListDrawable.addState(new int[0], drawable);
            return stateListDrawable;
        }

        private int getGxzwAnimType() {
            int defaultAnimType = GxzwAnimSettingHelper.getDefaultAnimType();
            Set<Integer> legalAnimTypeSet = GxzwAnimSettingHelper.getLegalAnimTypeSet();
            int intForUser = Settings.System.getIntForUser(this.mActivity.getContentResolver(), "fod_animation_type", defaultAnimType, 0);
            return !legalAnimTypeSet.contains(Integer.valueOf(intForUser)) ? defaultAnimType : intForUser;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void hideNavigationBar() {
            this.mActivity.getWindow().getDecorView().setSystemUiVisibility(4866);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateView$0(RadioGroup radioGroup, int i) {
            GxzwAnimItem findItemByType = findItemByType(i);
            if (findItemByType != null) {
                setVideoURI(findItemByType.video);
                saveGxzwAnimType(findItemByType.type);
                extHapticFeedback(findItemByType.rtpType);
            }
        }

        private void saveGxzwAnimType(int i) {
            Settings.System.putIntForUser(this.mActivity.getContentResolver(), "fod_animation_type", i, 0);
        }

        private void setVideoURI(String str) {
            this.mVideoView.setVideoURI(Uri.parse("android.resource://" + this.mActivity.getPackageName() + "/raw/" + str));
        }

        private void showGxzwAnimDialog() {
            Activity activity = this.mActivity;
            if (activity == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialog_Theme_DayNight);
            builder.setCancelable(false);
            builder.setTitle(R.string.gxzw_anim_dialog_title);
            builder.setMessage(R.string.gxzw_anim_dialog_message);
            builder.setNegativeButton(R.string.gxzw_anim_dialog_cancel_enroll, new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiGxzwAnimSettingsInternalActivity.MiuiGxzwAnimSettingsFragment.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    MiuiGxzwAnimSettingsFragment.this.hideNavigationBar();
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton(R.string.gxzw_anim_dialog_enroll, new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiGxzwAnimSettingsInternalActivity.MiuiGxzwAnimSettingsFragment.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent();
                    intent.setClassName("com.android.settings", "com.android.settings.NewFingerprintActivity");
                    intent.putExtra("need_to_manager", false);
                    MiuiGxzwAnimSettingsFragment.this.mActivity.startActivityForResult(intent, 101);
                }
            });
            AlertDialog create = builder.create();
            this.mFingerEnrollDialog = create;
            create.show();
        }

        private void showMiShowToast() {
            Activity activity = this.mActivity;
            if (activity == null) {
                return;
            }
            ToastUtil.show(activity.getApplicationContext(), R.string.mishow_disable_password_setting, 0);
        }

        public void extHapticFeedback(int i) {
            if (FingerprintUtils.IS_SUPPORT_LINEAR_MOTOR_VIBRATE && this.mHapticFeedbackUtil.isSupportExtHapticFeedback(i)) {
                this.mHapticFeedbackUtil.performExtHapticFeedback(i);
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment
        public String getName() {
            return MiuiGxzwAnimSettingsFragment.class.getName();
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
        }

        @Override // androidx.fragment.app.Fragment
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            if (this.mActivity == null) {
                this.mActivity = activity;
            }
        }

        @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onAttach(Context context) {
            super.onAttach(context);
            if ((context instanceof Activity) && this.mActivity == null) {
                this.mActivity = (Activity) context;
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            setThemeRes(R.style.Theme_Dark_Settings);
            this.mActivity.setRequestedOrientation(1);
            this.mActivity.getWindow().setStatusBarColor(0);
            this.mActivity.getWindow().addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
            this.mActivity.getWindow().addFlags(128);
        }

        @Override // com.android.settings.KeyguardSettingsPreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            View inflate = layoutInflater.inflate(R.layout.gxzw_anim, viewGroup, false);
            this.mVideoView = (MutedVideoView) inflate.findViewById(R.id.gxzw_anim_preview);
            this.mHapticFeedbackUtil = new HapticFeedbackUtil(this.mActivity.getApplicationContext(), false);
            Typeface createFromAsset = Typeface.createFromAsset(this.mActivity.getAssets(), "fonts/Mitype2018-clock.ttf");
            TextView textView = (TextView) inflate.findViewById(R.id.horizontal_hour);
            TextView textView2 = (TextView) inflate.findViewById(R.id.horizontal_dot);
            TextView textView3 = (TextView) inflate.findViewById(R.id.horizontal_min);
            textView.setTypeface(createFromAsset);
            textView2.setTypeface(createFromAsset);
            textView3.setTypeface(createFromAsset);
            Calendar calendar = new Calendar();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int i = calendar.get(18);
            int i2 = calendar.get(20);
            textView.setText(String.valueOf(i));
            textView3.setText(formatTime(i2));
            int gxzwAnimType = getGxzwAnimType();
            RadioGroup radioGroup = (RadioGroup) inflate.findViewById(R.id.gxzw_anim_radiogroup);
            for (GxzwAnimItem gxzwAnimItem : this.mGxzwAnimItemList) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2);
                layoutParams.weight = 1.0f;
                RadioButton radioButton = (RadioButton) layoutInflater.inflate(R.layout.gxzw_anim_radiobutton, (ViewGroup) null, false);
                radioButton.setText(gxzwAnimItem.nameRes);
                radioButton.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, genenalStateDrawable(this.mActivity, gxzwAnimItem.drawableNormal, gxzwAnimItem.drawableChecked), (Drawable) null, (Drawable) null);
                radioButton.setId(gxzwAnimItem.type);
                if (gxzwAnimItem.type == gxzwAnimType) {
                    radioButton.setChecked(true);
                    setVideoURI(gxzwAnimItem.video);
                }
                radioGroup.addView(radioButton, layoutParams);
            }
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: com.android.settings.MiuiGxzwAnimSettingsInternalActivity$MiuiGxzwAnimSettingsFragment$$ExternalSyntheticLambda1
                @Override // android.widget.RadioGroup.OnCheckedChangeListener
                public final void onCheckedChanged(RadioGroup radioGroup2, int i3) {
                    MiuiGxzwAnimSettingsInternalActivity.MiuiGxzwAnimSettingsFragment.this.lambda$onCreateView$0(radioGroup2, i3);
                }
            });
            this.mVideoView.start();
            this.mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.android.settings.MiuiGxzwAnimSettingsInternalActivity$MiuiGxzwAnimSettingsFragment$$ExternalSyntheticLambda0
                @Override // android.media.MediaPlayer.OnPreparedListener
                public final void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setLooping(true);
                }
            });
            return inflate;
        }

        @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onDestroy() {
            super.onDestroy();
            this.mVideoView.suspend();
            AlertDialog alertDialog = this.mFingerEnrollDialog;
            if (alertDialog == null || !alertDialog.isShowing()) {
                return;
            }
            this.mFingerEnrollDialog.dismiss();
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onPause() {
            super.onPause();
            if (this.mVideoView.isPlaying()) {
                this.mVideoView.pause();
            }
            AlertDialog alertDialog = this.mFingerEnrollDialog;
            if (alertDialog == null || !alertDialog.isShowing()) {
                return;
            }
            this.mFingerEnrollDialog.dismiss();
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            if (!this.mVideoView.isPlaying()) {
                this.mVideoView.start();
            }
            FingerprintHelper fingerprintHelper = new FingerprintHelper(this.mActivity);
            this.mFingerprintHelper = fingerprintHelper;
            if (fingerprintHelper.getFingerprintIds().size() != 0) {
                hideNavigationBar();
                this.mActivity.getWindow().setNavigationBarColor(0);
                this.mActivity.getWindow().addFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
            } else if (MiuiSecuritySettings.isMiShowMode(getActivity())) {
                showMiShowToast();
            } else {
                showGxzwAnimDialog();
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onStart() {
            ActionBar appCompatActionBar;
            super.onStart();
            if (!(getActivity() instanceof AppCompatActivity) || (appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar()) == null) {
                return;
            }
            appCompatActionBar.setBackgroundDrawable(this.mActivity.getDrawable(R.drawable.gxzw_anim_drawable));
        }
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", MiuiGxzwAnimSettingsFragment.class.getName());
        intent.putExtra(":settings:show_fragment_title", R.string.privacy_password_use_finger_dialog_title);
        return intent;
    }

    @Override // com.android.settings.SettingsActivity
    public int getOwnerTheme() {
        return R.style.Theme_Dark_Settings;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return true;
    }

    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        setTitle(R.string.gxzw_anim_title);
        super.onCreate(bundle);
    }
}
