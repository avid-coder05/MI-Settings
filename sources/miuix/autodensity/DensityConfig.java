package miuix.autodensity;

import android.util.DisplayMetrics;

/* loaded from: classes5.dex */
class DensityConfig {
    public float density;
    public int densityDpi;
    public float scaledDensity;

    public DensityConfig(DisplayMetrics displayMetrics) {
        this.densityDpi = displayMetrics.densityDpi;
        this.density = displayMetrics.density;
        this.scaledDensity = displayMetrics.scaledDensity;
    }

    public void copyValueToDM(DisplayMetrics displayMetrics) {
        displayMetrics.densityDpi = this.densityDpi;
        displayMetrics.density = this.density;
        displayMetrics.scaledDensity = this.scaledDensity;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DensityConfig) {
            DensityConfig densityConfig = (DensityConfig) obj;
            return Float.compare(this.density, densityConfig.density) == 0 && Float.compare(this.scaledDensity, densityConfig.scaledDensity) == 0 && this.densityDpi == densityConfig.densityDpi;
        }
        return false;
    }

    public String toString() {
        return "{densityDpi: " + this.densityDpi + ", density: " + this.density + ", scaledDensity: " + this.scaledDensity + "}";
    }
}
