package com.android.settings.display;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.SeekBar;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.widget.MiuiSeekBarPreference;

/* loaded from: classes.dex */
public class TemperatureSeekBarPreference extends MiuiSeekBarPreference {
    private Context mContext;
    private SeekBar mSeekBar;

    public TemperatureSeekBarPreference(Context context) {
        super(context);
        init(context);
    }

    public TemperatureSeekBarPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    private void init(Context context) {
        setLayoutResource(R.layout.temperature_preference_seekbar);
        this.mContext = context;
    }

    @Override // com.android.settings.widget.SeekBarPreference, com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        SeekBar seekBar = (SeekBar) preferenceViewHolder.findViewById(R.id.seekbar);
        this.mSeekBar = seekBar;
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(this);
            SeekBar seekBar2 = this.mSeekBar;
            seekBar2.setPaddingRelative(0, seekBar2.getPaddingTop(), 0, this.mSeekBar.getPaddingBottom());
            Bitmap decodeResource = BitmapFactory.decodeResource(this.mContext.getResources(), R.drawable.thumb_icon);
            Resources resources = this.mContext.getResources();
            int i = R.dimen.expert_hue_thumb_height;
            this.mSeekBar.setThumb(new BitmapDrawable(this.mContext.getResources(), Bitmap.createScaledBitmap(decodeResource, resources.getDimensionPixelSize(i), this.mContext.getResources().getDimensionPixelSize(i), true)));
            this.mSeekBar.setThumbOffset(-1);
            int progress = this.mSeekBar.getProgress();
            this.mSeekBar.setProgress(0);
            this.mSeekBar.setProgress(progress);
        }
    }
}
