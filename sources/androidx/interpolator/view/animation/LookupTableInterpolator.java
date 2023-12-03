package androidx.interpolator.view.animation;

/* loaded from: classes.dex */
final class LookupTableInterpolator {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static float interpolate(float[] values, float stepSize, float input) {
        if (input >= 1.0f) {
            return 1.0f;
        }
        if (input <= 0.0f) {
            return 0.0f;
        }
        int min = Math.min((int) (((float) (values.length - 1)) * input), values.length - 2);
        return values[min] + (((input - (min * stepSize)) / stepSize) * (values[min + 1] - values[min]));
    }
}
