package miui.imagefilters;

import miui.imagefilters.IImageFilter;

/* loaded from: classes3.dex */
public class HslFilter extends IImageFilter.AbstractImageFilter {
    public boolean useHsv;
    private float mHueModify = Float.NaN;
    private float mSaturationModify = Float.NaN;
    private float mLightnessModify = Float.NaN;
    private float mHueAdjust = Float.NaN;
    private float mSaturationAdjust = Float.NaN;
    private float mLightnessAdjust = Float.NaN;

    @Override // miui.imagefilters.IImageFilter.AbstractImageFilter
    public void processData(ImageData imageData) {
        float f;
        int i = imageData.width;
        int i2 = imageData.height;
        int[] iArr = imageData.pixels;
        float[] fArr = new float[3];
        int i3 = 0;
        int i4 = 0;
        while (i4 < i) {
            int i5 = i3;
            while (i5 < i2) {
                int i6 = (i5 * i) + i4;
                int i7 = iArr[i6];
                if (this.useHsv) {
                    ImageFilterUtils.RgbToHsv(i7, fArr);
                } else {
                    ImageFilterUtils.RgbToHsl(i7, fArr);
                }
                float f2 = fArr[i3];
                float f3 = fArr[1];
                float f4 = fArr[2];
                if (!Float.isNaN(this.mHueModify)) {
                    f2 = this.mHueModify;
                } else if (!Float.isNaN(this.mHueAdjust)) {
                    f2 += this.mHueAdjust;
                    if (f2 >= 360.0f) {
                        f2 -= 360.0f;
                    } else if (f2 < 0.0f) {
                        f2 += 360.0f;
                    }
                }
                if (!Float.isNaN(this.mSaturationModify)) {
                    f3 = this.mSaturationModify;
                } else if (!Float.isNaN(this.mSaturationAdjust)) {
                    float f5 = this.mSaturationAdjust;
                    if (f5 <= 0.0f) {
                        f = f3 * (f5 + 1.0f);
                    } else {
                        float min = Math.min(1.0f, f5 * 2.0f);
                        float f6 = (this.mSaturationAdjust - 0.5f) * 2.0f;
                        f = f3 * (min + 1.0f);
                        if (f6 > 0.0f) {
                            f += f6;
                        }
                    }
                    f3 = ImageFilterUtils.clamp(0.0f, f, 1.0f);
                }
                if (!Float.isNaN(this.mLightnessModify)) {
                    f4 = this.mLightnessModify;
                } else if (!Float.isNaN(this.mLightnessAdjust)) {
                    float f7 = this.mLightnessAdjust;
                    f4 = f7 <= 0.0f ? f4 * (f7 + 1.0f) : 1.0f - ((1.0f - f4) * (1.0f - f7));
                }
                iArr[i6] = ((this.useHsv ? ImageFilterUtils.HsvToRgb(f2, f3, f4) : ImageFilterUtils.HslToRgb(f2, f3, f4)) & 16777215) | (iArr[i6] & (-16777216));
                i5++;
                i3 = 0;
            }
            i4++;
            i3 = 0;
        }
    }

    public void setHueAdjust(float f) {
        this.mHueAdjust = ImageFilterUtils.clamp(-180.0f, f, 180.0f);
    }

    public void setHueModify(float f) {
        this.mHueModify = ImageFilterUtils.clamp(0.0f, f, 359.9999f);
    }

    public void setLightnessAdjust(float f) {
        this.mLightnessAdjust = ImageFilterUtils.clamp(-1.0f, f / 100.0f, 1.0f);
    }

    public void setLightnessModify(float f) {
        this.mLightnessModify = ImageFilterUtils.clamp(0.0f, f / 100.0f, 1.0f);
    }

    public void setSaturationAdjust(float f) {
        this.mSaturationAdjust = ImageFilterUtils.clamp(-1.0f, f / 100.0f, 1.0f);
    }

    public void setSaturationModify(float f) {
        this.mSaturationModify = ImageFilterUtils.clamp(0.0f, f / 100.0f, 1.0f);
    }
}
