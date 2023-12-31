package com.google.android.material.elevation;

import android.content.Context;
import android.graphics.Color;
import androidx.core.graphics.ColorUtils;
import com.google.android.material.R$attr;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.resources.MaterialAttributes;

/* loaded from: classes2.dex */
public class ElevationOverlayProvider {
    private final int colorSurface;
    private final float displayDensity;
    private final int elevationOverlayColor;
    private final boolean elevationOverlayEnabled;

    public ElevationOverlayProvider(Context context) {
        this.elevationOverlayEnabled = MaterialAttributes.resolveBoolean(context, R$attr.elevationOverlayEnabled, false);
        this.elevationOverlayColor = MaterialColors.getColor(context, R$attr.elevationOverlayColor, 0);
        this.colorSurface = MaterialColors.getColor(context, R$attr.colorSurface, 0);
        this.displayDensity = context.getResources().getDisplayMetrics().density;
    }

    private boolean isThemeSurfaceColor(int i) {
        return ColorUtils.setAlphaComponent(i, 255) == this.colorSurface;
    }

    public float calculateOverlayAlphaFraction(float f) {
        if (this.displayDensity <= 0.0f || f <= 0.0f) {
            return 0.0f;
        }
        return Math.min(((((float) Math.log1p(f / r2)) * 4.5f) + 2.0f) / 100.0f, 1.0f);
    }

    public int compositeOverlay(int i, float f) {
        float calculateOverlayAlphaFraction = calculateOverlayAlphaFraction(f);
        return ColorUtils.setAlphaComponent(MaterialColors.layer(ColorUtils.setAlphaComponent(i, 255), this.elevationOverlayColor, calculateOverlayAlphaFraction), Color.alpha(i));
    }

    public int compositeOverlayIfNeeded(int i, float f) {
        return (this.elevationOverlayEnabled && isThemeSurfaceColor(i)) ? compositeOverlay(i, f) : i;
    }

    public int compositeOverlayWithThemeSurfaceColorIfNeeded(float f) {
        return compositeOverlayIfNeeded(this.colorSurface, f);
    }

    public boolean isThemeElevationOverlayEnabled() {
        return this.elevationOverlayEnabled;
    }
}
