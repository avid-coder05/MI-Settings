package com.android.settings.notification;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.preference.SeekBarVolumizer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.widget.SeekBarPreference;
import java.util.Objects;

/* loaded from: classes2.dex */
public class VolumeSeekBarPreference extends SeekBarPreference {
    AudioManager mAudioManager;
    private Callback mCallback;
    private int mIconResId;
    private ImageView mIconView;
    private int mMuteIconResId;
    private boolean mMuted;
    protected SeekBar mSeekBar;
    private boolean mStopped;
    private int mStream;
    private String mSuppressionText;
    private TextView mSuppressionTextView;
    private SeekBarVolumizer mVolumizer;
    private boolean mZenMuted;

    /* loaded from: classes2.dex */
    public interface Callback {
        void onSampleStarting(SeekBarVolumizer seekBarVolumizer);

        void onStartTrackingTouch(SeekBarVolumizer seekBarVolumizer);

        void onStreamValueChanged(int i, int i2);
    }

    public VolumeSeekBarPreference(Context context) {
        super(context);
        setLayoutResource(R.layout.preference_volume_slider);
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
    }

    public VolumeSeekBarPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(R.layout.preference_volume_slider);
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
    }

    public VolumeSeekBarPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayoutResource(R.layout.preference_volume_slider);
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
    }

    public VolumeSeekBarPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setLayoutResource(R.layout.preference_volume_slider);
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
    }

    private Uri getMediaVolumeUri() {
        return Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.media_volume);
    }

    protected void init() {
        if (this.mSeekBar == null) {
            return;
        }
        SeekBarVolumizer.Callback callback = new SeekBarVolumizer.Callback() { // from class: com.android.settings.notification.VolumeSeekBarPreference.1
            public void onMuted(boolean z, boolean z2) {
                if (VolumeSeekBarPreference.this.mMuted == z && VolumeSeekBarPreference.this.mZenMuted == z2) {
                    return;
                }
                VolumeSeekBarPreference.this.mMuted = z;
                VolumeSeekBarPreference.this.mZenMuted = z2;
                VolumeSeekBarPreference.this.updateIconView();
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (VolumeSeekBarPreference.this.mCallback != null) {
                    VolumeSeekBarPreference.this.mCallback.onStreamValueChanged(VolumeSeekBarPreference.this.mStream, i);
                }
            }

            public void onSampleStarting(SeekBarVolumizer seekBarVolumizer) {
                if (VolumeSeekBarPreference.this.mCallback != null) {
                    VolumeSeekBarPreference.this.mCallback.onSampleStarting(seekBarVolumizer);
                }
            }

            public void onStartTrackingTouch(SeekBarVolumizer seekBarVolumizer) {
                if (VolumeSeekBarPreference.this.mCallback != null) {
                    VolumeSeekBarPreference.this.mCallback.onStartTrackingTouch(seekBarVolumizer);
                }
            }
        };
        Uri mediaVolumeUri = this.mStream == 3 ? getMediaVolumeUri() : null;
        if (this.mVolumizer == null) {
            this.mVolumizer = new SeekBarVolumizer(getContext(), this.mStream, mediaVolumeUri, callback);
        }
        this.mVolumizer.start();
        this.mVolumizer.setSeekBar(this.mSeekBar);
        updateIconView();
        updateSuppressionText();
        if (isEnabled()) {
            return;
        }
        this.mSeekBar.setEnabled(false);
        this.mVolumizer.stop();
    }

    public void onActivityPause() {
        this.mStopped = true;
        SeekBarVolumizer seekBarVolumizer = this.mVolumizer;
        if (seekBarVolumizer != null) {
            seekBarVolumizer.stop();
            this.mVolumizer = null;
        }
    }

    public void onActivityResume() {
        if (this.mStopped) {
            init();
        }
    }

    @Override // com.android.settings.widget.SeekBarPreference, com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mSeekBar = (SeekBar) preferenceViewHolder.findViewById(16909436);
        this.mIconView = (ImageView) preferenceViewHolder.findViewById(16908294);
        this.mSuppressionTextView = (TextView) preferenceViewHolder.findViewById(R.id.suppression_text);
        init();
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public void setMuteIcon(int i) {
        if (this.mMuteIconResId == i) {
            return;
        }
        this.mMuteIconResId = i;
        updateIconView();
    }

    public void setStream(int i) {
        this.mStream = i;
        setMax(this.mAudioManager.getStreamMaxVolume(i));
        setMin(this.mAudioManager.getStreamMinVolumeInt(this.mStream));
        setProgress(this.mAudioManager.getStreamVolume(this.mStream));
    }

    public void setSuppressionText(String str) {
        if (Objects.equals(str, this.mSuppressionText)) {
            return;
        }
        this.mSuppressionText = str;
        updateSuppressionText();
    }

    public void showIcon(int i) {
        if (this.mIconResId == i) {
            return;
        }
        this.mIconResId = i;
        updateIconView();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateIconView() {
        ImageView imageView = this.mIconView;
        if (imageView == null) {
            return;
        }
        int i = this.mIconResId;
        if (i != 0) {
            imageView.setImageResource(i);
            return;
        }
        int i2 = this.mMuteIconResId;
        if (i2 == 0 || !this.mMuted || this.mZenMuted) {
            imageView.setImageDrawable(getIcon());
        } else {
            imageView.setImageResource(i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateSuppressionText() {
        TextView textView = this.mSuppressionTextView;
        if (textView == null || this.mSeekBar == null) {
            return;
        }
        textView.setText(this.mSuppressionText);
        this.mSuppressionTextView.setVisibility(TextUtils.isEmpty(this.mSuppressionText) ^ true ? 0 : 8);
    }
}
