package com.android.settings.accessibility.haptic;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.accessibility.ScreenReaderController;
import com.android.settings.accessibility.utils.MiuiAccessibilityUtils;
import miuix.preference.FolmeAnimationController;

/* loaded from: classes.dex */
public class HapticDemoPreference extends Preference implements FolmeAnimationController, View.OnClickListener {
    public View mCheckBox;
    public View mClock;
    public View mHighLightButton;
    public View mHighLightList;
    public View mInput;
    public View mNoAction;
    public View mNormalButton;
    public View mNormalList;
    public View mPicker;
    public View mRootView;
    public View mSeekBar;
    private SharedPreferences mSharedPrefs;
    public View mTab;

    public HapticDemoPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSharedPrefs = context.getSharedPreferences(ScreenReaderController.ACCESSIBILITY_SCREEN_READER_SP, 0);
        setLayoutResource(R.layout.haptic_demo_layout);
    }

    public boolean isScreenReaderCheckboxOpen() {
        return this.mSharedPrefs.getInt(ScreenReaderController.IS_ACCESSIBILITY_SCREEN_READER_OPEN, 0) == 1;
    }

    @Override // miuix.preference.FolmeAnimationController
    public boolean isTouchAnimationEnable() {
        return false;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        this.mRootView = view;
        view.setPadding(0, 0, 0, 0);
        this.mRootView.setBackgroundColor(0);
        this.mRootView.setAlpha((isScreenReaderCheckboxOpen() && MiuiAccessibilityUtils.isTallBackActive(getContext())) ? 1.0f : 0.3f);
        View findViewById = this.mRootView.findViewById(R.id.time_container);
        this.mClock = findViewById;
        findViewById.setContentDescription(findViewById.getResources().getString(R.string.general_time, 8, 16));
        this.mClock.setOnClickListener(this);
        View findViewById2 = this.mRootView.findViewById(R.id.input_container);
        this.mInput = findViewById2;
        findViewById2.setOnClickListener(this);
        View findViewById3 = this.mRootView.findViewById(R.id.tab_container);
        this.mTab = findViewById3;
        findViewById3.setOnClickListener(this);
        View findViewById4 = this.mRootView.findViewById(R.id.picker_container);
        this.mPicker = findViewById4;
        findViewById4.setOnClickListener(this);
        View findViewById5 = this.mRootView.findViewById(R.id.seekbar_container);
        this.mSeekBar = findViewById5;
        findViewById5.setOnClickListener(this);
        View findViewById6 = this.mRootView.findViewById(R.id.normal_list_container);
        this.mNormalList = findViewById6;
        findViewById6.setOnClickListener(this);
        View findViewById7 = this.mRootView.findViewById(R.id.checkbox_container);
        this.mCheckBox = findViewById7;
        findViewById7.setOnClickListener(this);
        View findViewById8 = this.mRootView.findViewById(R.id.highlight_list_container);
        this.mHighLightList = findViewById8;
        findViewById8.setOnClickListener(this);
        View findViewById9 = this.mRootView.findViewById(R.id.no_action_container);
        this.mNoAction = findViewById9;
        findViewById9.setOnClickListener(this);
        View findViewById10 = this.mRootView.findViewById(R.id.button_normal_container);
        this.mNormalButton = findViewById10;
        findViewById10.setOnClickListener(this);
        View findViewById11 = this.mRootView.findViewById(R.id.button_highlight_container);
        this.mHighLightButton = findViewById11;
        findViewById11.setOnClickListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
    }
}
