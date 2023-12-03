package com.android.settings.edgesuppression;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.edgesuppression.LaySensorWrapper;
import com.android.settings.keys.RestrictedEdgeDescriptionPreference;
import com.android.settings.widget.SeekBarPreference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import miui.vip.VipService;
import miuix.preference.RadioButtonPreference;

/* loaded from: classes.dex */
public class EdgeSuppressionFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener {
    private static final List<String> CURRENT_SUPPORT_EDGE_MODE_LIST = Arrays.asList("default_suppression", "strong_suppression", "wake_suppression", "custom_suppression");
    private Context mContext;
    private EdgeSuppressionManager mEdgeSuppressionManager;
    private View mLeftView;
    private RadioButtonPreference mPrefCustomize;
    private PreferenceScreen mPreferenceScreen;
    private float mQFHDRatio;
    private Preference mRestrictedPreference;
    private SeekBarPreference mRestrictedSeekBar;
    private String mRestrictedType;
    private float mRestrictedValue;
    private View mRightView;
    private SuppressionTipAreaView mSuppressionTipAreaView;
    private int mTipAreaWidth;
    private int mLaySensorState = 0;
    private ArrayList<RadioButtonPreference> mEdgeModeSizePrefs = new ArrayList<>();
    private FrameLayout.LayoutParams mLeftLayoutParams = new FrameLayout.LayoutParams(-1, -1, 51);
    private FrameLayout.LayoutParams mRightLayoutParams = new FrameLayout.LayoutParams(-1, -1, 53);
    private final LaySensorWrapper.LaySensorChangeListener mLayListener = new LaySensorWrapper.LaySensorChangeListener() { // from class: com.android.settings.edgesuppression.EdgeSuppressionFragment.1
        @Override // com.android.settings.edgesuppression.LaySensorWrapper.LaySensorChangeListener
        public void onSensorChanged(int i) {
            if (i != EdgeSuppressionFragment.this.mLaySensorState) {
                EdgeSuppressionFragment.this.mLaySensorState = i;
                int i2 = EdgeSuppressionFragment.this.mLaySensorState;
                if (i2 == 0) {
                    EdgeSuppressionFragment edgeSuppressionFragment = EdgeSuppressionFragment.this;
                    edgeSuppressionFragment.mTipAreaWidth = edgeSuppressionFragment.mEdgeSuppressionManager.getConditionSize(2);
                } else if (i2 == 1) {
                    EdgeSuppressionFragment edgeSuppressionFragment2 = EdgeSuppressionFragment.this;
                    edgeSuppressionFragment2.mTipAreaWidth = edgeSuppressionFragment2.mEdgeSuppressionManager.getConditionSize(3);
                } else if (i2 == 2) {
                    EdgeSuppressionFragment.this.mTipAreaWidth = 0;
                }
                EdgeSuppressionFragment edgeSuppressionFragment3 = EdgeSuppressionFragment.this;
                edgeSuppressionFragment3.updateSuppreesionTipAreaView(edgeSuppressionFragment3.mSuppressionTipAreaView);
            }
        }
    };

    private void changeRestrictedTypeAndValue() {
        if (CURRENT_SUPPORT_EDGE_MODE_LIST.contains(this.mRestrictedType)) {
            return;
        }
        if ("diy_suppression".equals(this.mRestrictedType)) {
            this.mRestrictedType = "custom_suppression";
            this.mRestrictedValue *= this.mEdgeSuppressionManager.getOldMaxAdjustValue();
            Settings.System.putFloatForUser(getContentResolver(), "edge_size", this.mRestrictedValue, -2);
        } else {
            this.mRestrictedType = "default_suppression";
        }
        Settings.System.putStringForUser(getContentResolver(), "edge_type", this.mRestrictedType, -2);
    }

    private int getSeekBarProgress() {
        return (int) (((this.mRestrictedValue - this.mEdgeSuppressionManager.getConditionSize(0)) / this.mEdgeSuppressionManager.getAllowAdjustRange()) * 1000.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getSeekBarValue(int i) {
        return (((i * 1.0f) / 1000.0f) * this.mEdgeSuppressionManager.getAllowAdjustRange()) + this.mEdgeSuppressionManager.getConditionSize(0);
    }

    private void initFragment() {
        addPreferencesFromResource(R.xml.edge_settings_select_fragment);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        this.mPreferenceScreen = preferenceScreen;
        RadioButtonPreference radioButtonPreference = (RadioButtonPreference) preferenceScreen.findPreference("default_suppression");
        if (this.mEdgeSuppressionManager.isSupportSensor()) {
            radioButtonPreference.setTitle(R.string.intelligence_suppression_title);
            radioButtonPreference.setSummary(R.string.intelligence_suppression_summary);
        }
        RadioButtonPreference radioButtonPreference2 = (RadioButtonPreference) this.mPreferenceScreen.findPreference("strong_suppression");
        RadioButtonPreference radioButtonPreference3 = (RadioButtonPreference) this.mPreferenceScreen.findPreference("wake_suppression");
        this.mPrefCustomize = (RadioButtonPreference) this.mPreferenceScreen.findPreference("custom_suppression");
        RestrictedEdgeDescriptionPreference restrictedEdgeDescriptionPreference = (RestrictedEdgeDescriptionPreference) this.mPreferenceScreen.findPreference("restricted_info");
        this.mEdgeModeSizePrefs.add(radioButtonPreference);
        this.mEdgeModeSizePrefs.add(radioButtonPreference2);
        this.mEdgeModeSizePrefs.add(radioButtonPreference3);
        this.mEdgeModeSizePrefs.add(this.mPrefCustomize);
        Iterator<RadioButtonPreference> it = this.mEdgeModeSizePrefs.iterator();
        while (it.hasNext()) {
            RadioButtonPreference next = it.next();
            this.mPreferenceScreen.addPreference(next);
            next.setOnPreferenceClickListener(this);
        }
        this.mPreferenceScreen.addPreference(restrictedEdgeDescriptionPreference);
    }

    private void initQFHDRatio() {
        String str = SystemProperties.get("persist.sys.miui_default_resolution", (String) null);
        String str2 = SystemProperties.get("persist.sys.miui_resolution", (String) null);
        if (str2 == null || "".equals(str2) || str == null || "".equals(str)) {
            this.mQFHDRatio = 1.0f;
            return;
        }
        this.mQFHDRatio = Integer.parseInt(str2.split(",")[0]) / Integer.parseInt(str.split("x")[0]);
    }

    private void initSeekBarFragment() {
        this.mRestrictedPreference = this.mPreferenceScreen.findPreference("edge_mode_adjust_level");
        SeekBarPreference seekBarPreference = (SeekBarPreference) findPreference("edge_mode_adjust_level");
        this.mRestrictedSeekBar = seekBarPreference;
        seekBarPreference.setMax(VipService.VIP_SERVICE_FAILURE);
        this.mRestrictedSeekBar.setContinuousUpdates(true);
        this.mRestrictedSeekBar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.edgesuppression.EdgeSuppressionFragment.2
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                EdgeSuppressionFragment edgeSuppressionFragment = EdgeSuppressionFragment.this;
                edgeSuppressionFragment.mTipAreaWidth = (int) (edgeSuppressionFragment.getSeekBarValue(((Integer) obj).intValue()) * EdgeSuppressionFragment.this.mQFHDRatio);
                if (EdgeSuppressionFragment.this.mEdgeSuppressionManager.isSupportSensor()) {
                    EdgeSuppressionFragment edgeSuppressionFragment2 = EdgeSuppressionFragment.this;
                    edgeSuppressionFragment2.updateSuppreesionTipAreaView(edgeSuppressionFragment2.mSuppressionTipAreaView);
                    return true;
                }
                EdgeSuppressionFragment edgeSuppressionFragment3 = EdgeSuppressionFragment.this;
                edgeSuppressionFragment3.setRestrictedViewWidth(edgeSuppressionFragment3.mTipAreaWidth);
                return true;
            }
        });
        this.mRestrictedSeekBar.setStopTrackingTouchListener(new SeekBarPreference.StopTrackingTouchListener() { // from class: com.android.settings.edgesuppression.EdgeSuppressionFragment.3
            @Override // com.android.settings.widget.SeekBarPreference.StopTrackingTouchListener
            public void onStopTrackingTouch() {
                EdgeSuppressionFragment edgeSuppressionFragment = EdgeSuppressionFragment.this;
                edgeSuppressionFragment.setEdgeSuppression(edgeSuppressionFragment.mPrefCustomize);
            }
        });
    }

    private void initSuppressionTipAreaView() {
        getActivity().getWindow().addFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
        ViewGroup viewGroup = (ViewGroup) getActivity().getWindow().getDecorView();
        Context context = getContext();
        int i = this.mTipAreaWidth;
        EdgeSuppressionManager edgeSuppressionManager = this.mEdgeSuppressionManager;
        SuppressionTipAreaView suppressionTipAreaView = new SuppressionTipAreaView(context, i, edgeSuppressionManager.mScreenWidth, edgeSuppressionManager.mScreenHeight);
        this.mSuppressionTipAreaView = suppressionTipAreaView;
        viewGroup.addView(suppressionTipAreaView);
    }

    private void initTipView(Context context, int i) {
        FrameLayout.LayoutParams layoutParams = this.mLeftLayoutParams;
        layoutParams.width = i;
        layoutParams.height = this.mEdgeSuppressionManager.mScreenHeight;
        View view = new View(context);
        this.mLeftView = view;
        Resources resources = getContext().getResources();
        int i2 = R.color.restricted_tip_area_color;
        view.setBackgroundColor(resources.getColor(i2, null));
        FrameLayout.LayoutParams layoutParams2 = this.mRightLayoutParams;
        layoutParams2.width = i;
        layoutParams2.height = this.mEdgeSuppressionManager.mScreenHeight;
        View view2 = new View(context);
        this.mRightView = view2;
        view2.setBackgroundColor(getContext().getResources().getColor(i2, null));
        getActivity().getWindow().addFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
        ViewGroup viewGroup = (ViewGroup) getActivity().getWindow().getDecorView();
        viewGroup.addView(this.mLeftView, this.mLeftLayoutParams);
        viewGroup.addView(this.mRightView, this.mRightLayoutParams);
    }

    private void resetSensorState() {
        this.mTipAreaWidth = this.mEdgeSuppressionManager.getConditionSize(2);
        this.mLaySensorState = 0;
    }

    private void setDefaultValue() {
        String str = this.mRestrictedType;
        if (str == null) {
            ((RadioButtonPreference) this.mPreferenceScreen.findPreference("default_suppression")).setChecked(true);
            this.mRestrictedType = "default_suppression";
            return;
        }
        ((RadioButtonPreference) findPreference(str)).setChecked(true);
        if ("custom_suppression".equals(this.mRestrictedType)) {
            this.mRestrictedSeekBar.setProgress(getSeekBarProgress());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0057, code lost:
    
        if (r0.equals("default_suppression") == false) goto L11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void setEdgeSuppression(miuix.preference.RadioButtonPreference r8) {
        /*
            r7 = this;
            java.util.ArrayList<miuix.preference.RadioButtonPreference> r0 = r7.mEdgeModeSizePrefs
            java.util.Iterator r0 = r0.iterator()
        L6:
            boolean r1 = r0.hasNext()
            r2 = 0
            r3 = 1
            if (r1 == 0) goto L1b
            java.lang.Object r1 = r0.next()
            miuix.preference.RadioButtonPreference r1 = (miuix.preference.RadioButtonPreference) r1
            if (r1 != r8) goto L17
            r2 = r3
        L17:
            r1.setChecked(r2)
            goto L6
        L1b:
            java.lang.String r0 = r8.getKey()
            r0.hashCode()
            r1 = -1
            int r4 = r0.hashCode()
            r5 = 3
            r6 = 2
            switch(r4) {
                case -480291787: goto L51;
                case 1018504075: goto L45;
                case 1567494936: goto L39;
                case 1611383141: goto L2e;
                default: goto L2c;
            }
        L2c:
            r2 = r1
            goto L5a
        L2e:
            java.lang.String r2 = "custom_suppression"
            boolean r0 = r0.equals(r2)
            if (r0 != 0) goto L37
            goto L2c
        L37:
            r2 = r5
            goto L5a
        L39:
            java.lang.String r2 = "wake_suppression"
            boolean r0 = r0.equals(r2)
            if (r0 != 0) goto L43
            goto L2c
        L43:
            r2 = r6
            goto L5a
        L45:
            java.lang.String r2 = "strong_suppression"
            boolean r0 = r0.equals(r2)
            if (r0 != 0) goto L4f
            goto L2c
        L4f:
            r2 = r3
            goto L5a
        L51:
            java.lang.String r4 = "default_suppression"
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L5a
            goto L2c
        L5a:
            switch(r2) {
                case 0: goto L7f;
                case 1: goto L75;
                case 2: goto L6b;
                case 3: goto L5e;
                default: goto L5d;
            }
        L5d:
            goto L88
        L5e:
            com.android.settings.widget.SeekBarPreference r0 = r7.mRestrictedSeekBar
            int r0 = r0.getProgress()
            float r0 = r7.getSeekBarValue(r0)
            r7.mRestrictedValue = r0
            goto L88
        L6b:
            com.android.settings.edgesuppression.EdgeSuppressionManager r0 = r7.mEdgeSuppressionManager
            int r0 = r0.getConditionSize(r3)
            float r0 = (float) r0
            r7.mRestrictedValue = r0
            goto L88
        L75:
            com.android.settings.edgesuppression.EdgeSuppressionManager r0 = r7.mEdgeSuppressionManager
            int r0 = r0.getConditionSize(r5)
            float r0 = (float) r0
            r7.mRestrictedValue = r0
            goto L88
        L7f:
            com.android.settings.edgesuppression.EdgeSuppressionManager r0 = r7.mEdgeSuppressionManager
            int r0 = r0.getConditionSize(r6)
            float r0 = (float) r0
            r7.mRestrictedValue = r0
        L88:
            android.content.ContentResolver r0 = r7.getContentResolver()
            java.lang.String r8 = r8.getKey()
            java.lang.String r1 = "edge_type"
            r2 = -2
            android.provider.Settings.System.putStringForUser(r0, r1, r8, r2)
            com.android.settings.edgesuppression.EdgeSuppressionManager r8 = r7.mEdgeSuppressionManager
            boolean r8 = r8.isReflectionFailed()
            if (r8 == 0) goto Laa
            float r8 = r7.mRestrictedValue
            com.android.settings.edgesuppression.EdgeSuppressionManager r0 = r7.mEdgeSuppressionManager
            r1 = 4
            int r0 = r0.getConditionSize(r1)
            float r0 = (float) r0
            float r8 = r8 / r0
            goto Lac
        Laa:
            float r8 = r7.mRestrictedValue
        Lac:
            android.content.ContentResolver r7 = r7.getContentResolver()
            java.lang.String r0 = "edge_size"
            android.provider.Settings.System.putFloatForUser(r7, r0, r8, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.edgesuppression.EdgeSuppressionFragment.setEdgeSuppression(miuix.preference.RadioButtonPreference):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setRestrictedViewWidth(int i) {
        this.mLeftView.getLayoutParams().width = i;
        this.mRightView.getLayoutParams().width = i;
        this.mLeftView.setLayoutParams(this.mLeftLayoutParams);
        this.mRightView.setLayoutParams(this.mRightLayoutParams);
    }

    private void setSeekBarEnable() {
        this.mRestrictedSeekBar.setEnabled(this.mPrefCustomize.isChecked());
        if (!this.mPrefCustomize.isChecked()) {
            this.mPreferenceScreen.removePreference(this.mRestrictedPreference);
            return;
        }
        this.mPreferenceScreen.addPreference(this.mRestrictedPreference);
        this.mRestrictedSeekBar.setProgress(getSeekBarProgress());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSuppreesionTipAreaView(SuppressionTipAreaView suppressionTipAreaView) {
        if (suppressionTipAreaView != null) {
            int i = this.mTipAreaWidth;
            EdgeSuppressionManager edgeSuppressionManager = this.mEdgeSuppressionManager;
            suppressionTipAreaView.setTipWidth(i, edgeSuppressionManager.mScreenWidth, edgeSuppressionManager.mScreenHeight);
            suppressionTipAreaView.invalidate(true);
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(R.string.edge_mode_state_title);
        EdgeSuppressionManager edgeSuppressionManager = EdgeSuppressionManager.getInstance(this.mContext.getApplicationContext());
        this.mEdgeSuppressionManager = edgeSuppressionManager;
        if (edgeSuppressionManager.isReflectionFailed()) {
            this.mRestrictedValue = Settings.System.getFloatForUser(getContentResolver(), "edge_size", this.mEdgeSuppressionManager.getOldConditionSize(2), -2) * this.mEdgeSuppressionManager.getConditionSize(4);
        } else {
            this.mRestrictedValue = Settings.System.getFloatForUser(getContentResolver(), "edge_size", this.mEdgeSuppressionManager.getConditionSize(2), -2);
        }
        this.mRestrictedType = Settings.System.getStringForUser(getContentResolver(), "edge_type", -2);
        changeRestrictedTypeAndValue();
        initQFHDRatio();
        this.mTipAreaWidth = (int) (this.mRestrictedValue * this.mQFHDRatio);
        initFragment();
        initSeekBarFragment();
        setDefaultValue();
        setSeekBarEnable();
        this.mEdgeSuppressionManager.setScreenSize();
        if (this.mEdgeSuppressionManager.isSupportSensor()) {
            initSuppressionTipAreaView();
        } else {
            initTipView(this.mContext, this.mTipAreaWidth);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        Settings.Global.putInt(getContentResolver(), "vertical_edge_suppression_size", this.mEdgeSuppressionManager.getSizeOfInputMethod(this.mRestrictedValue, this.mRestrictedType));
        Settings.Global.putInt(getContentResolver(), "horizontal_edge_suppression_size", this.mEdgeSuppressionManager.getSizeOfInputMethod(this.mRestrictedValue, this.mRestrictedType));
        if (this.mEdgeSuppressionManager.isSupportSensor()) {
            this.mEdgeSuppressionManager.unRegisterLaySensor();
            resetSensorState();
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        setEdgeSuppression((RadioButtonPreference) preference);
        if (preference.getKey().equals(this.mRestrictedType)) {
            return true;
        }
        this.mRestrictedType = preference.getKey();
        setSeekBarEnable();
        if (this.mEdgeSuppressionManager.isSupportSensor()) {
            resetSensorState();
            if ("default_suppression".equals(this.mRestrictedType)) {
                this.mEdgeSuppressionManager.registerLaySensor(this.mLayListener);
            } else {
                this.mEdgeSuppressionManager.unRegisterLaySensor();
            }
        }
        this.mTipAreaWidth = (int) (this.mRestrictedValue * this.mQFHDRatio);
        if (this.mEdgeSuppressionManager.isSupportSensor()) {
            updateSuppreesionTipAreaView(this.mSuppressionTipAreaView);
        } else {
            setRestrictedViewWidth(this.mTipAreaWidth);
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mEdgeSuppressionManager.setScreenSize();
        if (!this.mEdgeSuppressionManager.isSupportSensor()) {
            setRestrictedViewWidth(this.mTipAreaWidth);
            return;
        }
        if ("default_suppression".equals(this.mRestrictedType)) {
            this.mEdgeSuppressionManager.registerLaySensor(this.mLayListener);
        }
        updateSuppreesionTipAreaView(this.mSuppressionTipAreaView);
    }
}
