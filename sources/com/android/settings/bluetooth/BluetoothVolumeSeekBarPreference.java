package com.android.settings.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.widget.SeekBarPreference;
import miuix.androidbasewidget.widget.SeekBar;
import miuix.animation.Folme;

/* loaded from: classes.dex */
public class BluetoothVolumeSeekBarPreference extends SeekBarPreference {
    private View mLayout;

    public BluetoothVolumeSeekBarPreference(Context context) {
        this(context, null);
    }

    public BluetoothVolumeSeekBarPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BluetoothVolumeSeekBarPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setMax(1080);
        setLayoutResource(R.layout.preference_bt_volume_seekbar);
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference, com.android.settingslib.miuisettings.preference.PreferenceFeature
    public boolean hasIcon() {
        return true;
    }

    @Override // com.android.settings.widget.SeekBarPreference, com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        this.mLayout = view;
        if (MiuiUtils.isMiuiSdkSupportFolme()) {
            Folme.clean(view);
        }
        view.setBackgroundColor(0);
        view.setPaddingRelative(0, view.getPaddingTop(), 0, view.getPaddingBottom());
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        int paddingStart = ((LinearLayout) view.findViewById(R.id.title_view)).getPaddingStart() - seekBar.getPaddingStart();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seekBar.getLayoutParams());
        layoutParams.setMarginsRelative(paddingStart, 0, paddingStart, getContext().getResources().getDimensionPixelSize(R.dimen.volume_seekbar_margin_bottom));
        seekBar.setLayoutParams(layoutParams);
        if (isDisabledByAdmin()) {
            view.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothVolumeSeekBarPreference.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    BluetoothVolumeSeekBarPreference.this.performClick();
                }
            });
        }
    }
}
