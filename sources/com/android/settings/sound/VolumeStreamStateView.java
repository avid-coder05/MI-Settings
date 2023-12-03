package com.android.settings.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.android.settings.R;

/* loaded from: classes2.dex */
public class VolumeStreamStateView extends ImageView {
    private final int DEVICE_OUT_ALL_WIRED;
    private int mStream;
    private static final int[] MUTED_STATE_SET = {R.attr.state_muted};
    private static final int[] WIRED_STATE_SET = {R.attr.state_wired};
    private static final int[] A2DP_STATE_SET = {R.attr.state_a2dp};

    public VolumeStreamStateView(Context context) {
        super(context);
        this.mStream = -1;
        this.DEVICE_OUT_ALL_WIRED = 12;
    }

    public VolumeStreamStateView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mStream = -1;
        this.DEVICE_OUT_ALL_WIRED = 12;
    }

    public VolumeStreamStateView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mStream = -1;
        this.DEVICE_OUT_ALL_WIRED = 12;
    }

    public VolumeStreamStateView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mStream = -1;
        this.DEVICE_OUT_ALL_WIRED = 12;
    }

    @Override // android.widget.ImageView, android.view.View
    public int[] onCreateDrawableState(int i) {
        int[] onCreateDrawableState = super.onCreateDrawableState(i + 3);
        if (this.mStream != -1) {
            AudioManager audioManager = (AudioManager) getContext().getSystemService("audio");
            int devicesForStream = audioManager.getDevicesForStream(this.mStream);
            if (audioManager.getStreamVolume(this.mStream) == 0) {
                ImageView.mergeDrawableStates(onCreateDrawableState, MUTED_STATE_SET);
            }
            if ((devicesForStream & 12) != 0) {
                ImageView.mergeDrawableStates(onCreateDrawableState, WIRED_STATE_SET);
            } else if (AudioSystem.DEVICE_OUT_ALL_A2DP_SET.contains(Integer.valueOf(devicesForStream))) {
                ImageView.mergeDrawableStates(onCreateDrawableState, A2DP_STATE_SET);
            }
        }
        return onCreateDrawableState;
    }

    public void setStream(int i) {
        this.mStream = i;
    }
}
