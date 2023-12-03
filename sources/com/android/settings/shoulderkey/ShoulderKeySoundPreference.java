package com.android.settings.shoulderkey;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import java.util.HashMap;
import java.util.Map;
import miui.provider.Weather;
import miuix.visual.check.VisualCheckBox;
import miuix.visual.check.VisualCheckGroup;

/* loaded from: classes2.dex */
public class ShoulderKeySoundPreference extends Preference {
    private VisualCheckBox mBulletCheckBox;
    private Map<String, VisualCheckBox> mCheckBoxMap;
    private String mCheckedBoxType;
    private VisualCheckGroup.OnCheckedChangeListener mCheckedListener;
    private VisualCheckBox mClassicCheckBox;
    Context mContext;
    private VisualCheckBox mCurrentCheckBox;
    private View mRootView;
    private VisualCheckBox mWindCheckBox;

    public ShoulderKeySoundPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCheckBoxMap = new HashMap();
        this.mContext = context;
        setLayoutResource(R.layout.shoulderkey_sound_view);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        if (this.mRootView == null) {
            this.mRootView = preferenceViewHolder.itemView;
        }
        VisualCheckGroup visualCheckGroup = (VisualCheckGroup) this.mRootView.findViewById(R.id.checkgroup);
        VisualCheckBox visualCheckBox = (VisualCheckBox) this.mRootView.findViewById(R.id.sound_classic);
        this.mClassicCheckBox = visualCheckBox;
        this.mCheckBoxMap.put("classic", visualCheckBox);
        VisualCheckBox visualCheckBox2 = (VisualCheckBox) this.mRootView.findViewById(R.id.sound_bullet);
        this.mBulletCheckBox = visualCheckBox2;
        this.mCheckBoxMap.put("bullet", visualCheckBox2);
        VisualCheckBox visualCheckBox3 = (VisualCheckBox) this.mRootView.findViewById(R.id.sound_current);
        this.mCurrentCheckBox = visualCheckBox3;
        this.mCheckBoxMap.put("current", visualCheckBox3);
        VisualCheckBox visualCheckBox4 = (VisualCheckBox) this.mRootView.findViewById(R.id.sound_wind);
        this.mWindCheckBox = visualCheckBox4;
        this.mCheckBoxMap.put(Weather.WeatherBaseColumns.WIND, visualCheckBox4);
        if (this.mCheckBoxMap.get(this.mCheckedBoxType) != null) {
            this.mCheckBoxMap.get(this.mCheckedBoxType).setChecked(true);
        }
        visualCheckGroup.setOnCheckedChangeListener(this.mCheckedListener);
    }

    public void setCheckBoxCheckedType(String str) {
        this.mCheckedBoxType = str;
        if (this.mCheckBoxMap.get(str) != null) {
            this.mCheckBoxMap.get(str).setChecked(true);
        }
    }

    public void setOnCheckedChangeListener(VisualCheckGroup.OnCheckedChangeListener onCheckedChangeListener) {
        this.mCheckedListener = onCheckedChangeListener;
    }
}
