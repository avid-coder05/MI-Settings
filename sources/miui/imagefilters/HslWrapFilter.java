package miui.imagefilters;

import miui.imagefilters.IImageFilter;

/* loaded from: classes3.dex */
public class HslWrapFilter extends IImageFilter.AbstractImageFilter {
    private HslFilter mHsl;
    private HslFilter mHsv;

    private void ensureHsl() {
        if (this.mHsl == null) {
            HslFilter hslFilter = new HslFilter();
            this.mHsl = hslFilter;
            hslFilter.useHsv = false;
        }
    }

    private void ensureHsv() {
        if (this.mHsv == null) {
            HslFilter hslFilter = new HslFilter();
            this.mHsv = hslFilter;
            hslFilter.useHsv = true;
        }
    }

    @Override // miui.imagefilters.IImageFilter.AbstractImageFilter
    public void processData(ImageData imageData) {
        HslFilter hslFilter = this.mHsl;
        if (hslFilter != null) {
            hslFilter.process(imageData);
        }
        HslFilter hslFilter2 = this.mHsv;
        if (hslFilter2 != null) {
            hslFilter2.process(imageData);
        }
    }

    public void setHueAdjust(float f) {
        ensureHsl();
        this.mHsl.setHueAdjust(f);
    }

    public void setHueModify(float f) {
        ensureHsl();
        this.mHsl.setHueModify(f);
    }

    public void setLightnessAdjust(float f) {
        if (f > 0.0f) {
            ensureHsl();
            this.mHsl.setLightnessAdjust(f);
            return;
        }
        ensureHsv();
        this.mHsv.setLightnessAdjust(f);
    }

    public void setLightnessModify(float f) {
        if (f > 0.0f) {
            ensureHsl();
            this.mHsl.setLightnessModify(f);
            return;
        }
        ensureHsv();
        this.mHsv.setLightnessModify(f);
    }

    public void setSaturationAdjust(float f) {
        ensureHsl();
        this.mHsl.setSaturationAdjust(f);
    }

    public void setSaturationModify(float f) {
        ensureHsl();
        this.mHsl.setSaturationModify(f);
    }
}
