package com.android.settings.development.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import com.android.settings.R;

/* loaded from: classes.dex */
public class BluetoothChannelModeDialogPreference extends BaseBluetoothDialogPreference {
    public BluetoothChannelModeDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context);
    }

    public BluetoothChannelModeDialogPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initialize(context);
    }

    public BluetoothChannelModeDialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initialize(context);
    }

    private void initialize(Context context) {
        this.mRadioButtonIds.add(Integer.valueOf(R.id.bluetooth_audio_channel_mode_default));
        this.mRadioButtonIds.add(Integer.valueOf(R.id.bluetooth_audio_channel_mode_mono));
        this.mRadioButtonIds.add(Integer.valueOf(R.id.bluetooth_audio_channel_mode_stereo));
        for (String str : context.getResources().getStringArray(R.array.bluetooth_a2dp_codec_channel_mode_titles)) {
            this.mRadioButtonStrings.add(str);
        }
        for (String str2 : context.getResources().getStringArray(R.array.bluetooth_a2dp_codec_channel_mode_summaries)) {
            this.mSummaryStrings.add(str2);
        }
    }

    @Override // com.android.settings.development.bluetooth.BaseBluetoothDialogPreference
    protected int getRadioButtonGroupId() {
        return R.id.bluetooth_audio_channel_mode_radio_group;
    }
}
