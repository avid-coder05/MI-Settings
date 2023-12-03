package miuix.internal.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/* loaded from: classes5.dex */
public final class DisplayHelper {
    private float mDensity;
    private int mDensityDpi;
    private DisplayMetrics mDisplayMetrics;
    private int mHeightDps;
    private int mHeightPixels;
    private int mWidthDps;
    private int mWidthPixels;

    public DisplayHelper(Context context) {
        getAndroidScreenProperty(context);
    }

    private void getAndroidScreenProperty(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        this.mDisplayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(this.mDisplayMetrics);
        DisplayMetrics displayMetrics = this.mDisplayMetrics;
        int i = displayMetrics.widthPixels;
        this.mWidthPixels = i;
        int i2 = displayMetrics.heightPixels;
        this.mHeightPixels = i2;
        float f = displayMetrics.density;
        this.mDensity = f;
        this.mDensityDpi = displayMetrics.densityDpi;
        this.mWidthDps = (int) (i / f);
        this.mHeightDps = (int) (i2 / f);
    }

    public float getDensity() {
        return this.mDensity;
    }

    public int getHeightPixels() {
        return this.mHeightPixels;
    }

    public int getWidthPixels() {
        return this.mWidthPixels;
    }
}
