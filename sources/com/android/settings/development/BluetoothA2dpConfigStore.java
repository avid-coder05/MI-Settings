package com.android.settings.development;

import android.bluetooth.BluetoothCodecConfig;

/* loaded from: classes.dex */
public class BluetoothA2dpConfigStore {
    private long mCodecSpecific1Value;
    private long mCodecSpecific2Value;
    private long mCodecSpecific3Value;
    private long mCodecSpecific4Value;
    private long mLdacSpecificValue;
    private long mLhdcSpecificValue;
    private int mCodecType = 1000000;
    private int mCodecPriority = 0;
    private int mSampleRate = 0;
    private int mBitsPerSample = 0;
    private int mChannelMode = 0;

    public BluetoothCodecConfig createCodecConfig() {
        int i = this.mCodecType;
        if (i == 4) {
            this.mCodecSpecific1Value = this.mLdacSpecificValue;
        } else if (i == 9 || i == 10 || i == 11) {
            this.mCodecSpecific1Value = this.mLhdcSpecificValue;
        }
        return new BluetoothCodecConfig(this.mCodecType, this.mCodecPriority, this.mSampleRate, this.mBitsPerSample, this.mChannelMode, this.mCodecSpecific1Value, this.mCodecSpecific2Value, this.mCodecSpecific3Value, this.mCodecSpecific4Value);
    }

    public void setBitsPerSample(int i) {
        this.mBitsPerSample = i;
    }

    public void setChannelMode(int i) {
        this.mChannelMode = i;
    }

    public void setCodecPriority(int i) {
        this.mCodecPriority = i;
    }

    public void setCodecSpecific1Value(long j) {
        this.mCodecSpecific1Value = j;
    }

    public void setCodecSpecific2Value(int i) {
        this.mCodecSpecific2Value = i;
    }

    public void setCodecSpecific4Value(int i) {
        this.mCodecSpecific4Value = i;
    }

    public void setCodecType(int i) {
        this.mCodecType = i;
    }

    public void setLdacSpecificValue(int i) {
        this.mLdacSpecificValue = i;
    }

    public void setLhdcSpecificValue(int i) {
        this.mLhdcSpecificValue = i;
    }

    public void setSampleRate(int i) {
        this.mSampleRate = i;
    }
}
