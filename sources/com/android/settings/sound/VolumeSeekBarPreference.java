package com.android.settings.sound;

import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioManager;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.widget.SeekBarPreference;
import com.android.settingslib.utils.ThreadUtils;
import miuix.androidbasewidget.widget.SeekBar;
import miuix.animation.Folme;

/* loaded from: classes2.dex */
public class VolumeSeekBarPreference extends SeekBarPreference {
    private static final String TAG = VolumeSeekBarPreference.class.getSimpleName();
    private View mLayout;
    private SeekBarVolumizer mSeekBarVolumizer;
    private int mStream;
    private boolean mTrackingTouch;

    public VolumeSeekBarPreference(Context context) {
        this(context, null);
    }

    public VolumeSeekBarPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VolumeSeekBarPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mStream = -1;
        setMax(1080);
        setLayoutResource(R.layout.preference_volume_seekbar);
    }

    private void enableSeekBarNormalColor(boolean z) {
        SeekBar seekBar;
        View view = this.mLayout;
        if (view == null || (seekBar = (SeekBar) view.findViewById(R.id.seekbar)) == null) {
            return;
        }
        Drawable progressDrawable = seekBar.getProgressDrawable();
        if (progressDrawable instanceof LayerDrawable) {
            float[] fArr = {1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.3f, 0.0f};
            Drawable findDrawableByLayerId = ((LayerDrawable) progressDrawable).findDrawableByLayerId(16908301);
            if (z) {
                findDrawableByLayerId.clearColorFilter();
            } else {
                findDrawableByLayerId.setColorFilter(new ColorMatrixColorFilter(fArr));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSeekBarDrawable$0() {
        boolean isSilenceModeOn = MiuiSettings.SoundMode.isSilenceModeOn(getContext());
        boolean z = Settings.System.getIntForUser(getContext().getContentResolver(), "mute_music_at_silent", 0, -3) == 1;
        if (this.mLayout == null) {
            return;
        }
        refreshIconState();
        if (!isSilenceModeOn) {
            enableSeekBarNormalColor(true);
            return;
        }
        String key = getKey();
        key.hashCode();
        char c = 65535;
        switch (key.hashCode()) {
            case -1527956125:
                if (key.equals("voice_assist_volume")) {
                    c = 0;
                    break;
                }
                break;
            case 252480469:
                if (key.equals("media_volume")) {
                    c = 1;
                    break;
                }
                break;
            case 1913824265:
                if (key.equals("ring_volume")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
                int streamVolume = ((AudioManager) getContext().getSystemService("audio")).getStreamVolume(this.mStream);
                Log.i(TAG, "updateSeekBarDrawable mediaVolume=" + streamVolume + ",mStream=" + this.mStream);
                enableSeekBarNormalColor((z && streamVolume == 0) ? false : true);
                return;
            case 2:
                enableSeekBarNormalColor(false);
                return;
            default:
                return;
        }
    }

    public SeekBarVolumizer getSeekBarVolumizer() {
        return this.mSeekBarVolumizer;
    }

    public int getStream() {
        return this.mStream;
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
        android.widget.SeekBar seekBar = (android.widget.SeekBar) view.findViewById(R.id.seekbar);
        VolumeStreamStateView volumeStreamStateView = (VolumeStreamStateView) view.findViewById(16908294);
        volumeStreamStateView.setStream(this.mStream);
        if (volumeStreamStateView.getDrawable() != null) {
            volumeStreamStateView.getDrawable().setAlpha(seekBar.getProgressDrawable().getAlpha());
        }
        SeekBarVolumizer seekBarVolumizer = this.mSeekBarVolumizer;
        if (seekBarVolumizer != null) {
            seekBarVolumizer.setSeekBar(seekBar);
        }
        if (isDisabledByAdmin()) {
            view.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.sound.VolumeSeekBarPreference.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    VolumeSeekBarPreference.this.performClick();
                }
            });
        }
    }

    @Override // com.android.settings.widget.SeekBarPreference, android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(android.widget.SeekBar seekBar, int i, boolean z) {
        setProgress(i, false);
        if (z) {
            updateSeekBarDrawable();
        }
    }

    @Override // com.android.settings.widget.SeekBarPreference, android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
        this.mTrackingTouch = true;
    }

    @Override // com.android.settings.widget.SeekBarPreference, android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
        this.mTrackingTouch = false;
    }

    public void refreshIconState() {
        ImageView imageView;
        View view = this.mLayout;
        if (view == null || (imageView = (ImageView) view.findViewById(16908294)) == null) {
            return;
        }
        imageView.refreshDrawableState();
    }

    public void setSeekBarVolumizer(SeekBarVolumizer seekBarVolumizer) {
        this.mSeekBarVolumizer = seekBarVolumizer;
    }

    public void setStream(int i) {
        this.mStream = i;
    }

    public void updateSeekBarDrawable() {
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.sound.VolumeSeekBarPreference$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                VolumeSeekBarPreference.this.lambda$updateSeekBarDrawable$0();
            }
        });
    }
}
