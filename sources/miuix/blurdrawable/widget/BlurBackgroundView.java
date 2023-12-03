package miuix.blurdrawable.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import com.miui.blur.sdk.drawable.BlurDrawable;
import miuix.blurdrawable.R;

/* loaded from: classes5.dex */
public class BlurBackgroundView extends FrameLayout {
    private BlurDrawable mBlurBackground;
    private Drawable mBlurForeground;

    public BlurBackgroundView(Context context) {
        this(context, null);
    }

    public BlurBackgroundView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private void createBlurBackground() {
        this.mBlurBackground = new BlurDrawable();
        if ((getResources().getConfiguration().uiMode & 48) == 32) {
            this.mBlurBackground.setMixColor(19, Color.argb(165, 92, 92, 92));
            this.mBlurForeground = new ColorDrawable(getResources().getColor(R.color.miuix_blurdrawable_view_fg_dark));
        } else {
            this.mBlurBackground.setMixColor(18, Color.argb(165, 107, 107, 107));
            this.mBlurForeground = new ColorDrawable(getResources().getColor(R.color.miuix_blurdrawable_view_fg_light));
        }
        this.mBlurBackground.setBlurRatio(1.0f);
    }

    public boolean isSupportBlur() {
        return Build.VERSION.SDK_INT > 27 && BlurDrawable.isSupportBlurStatic();
    }

    @Override // android.view.View
    public void setAlpha(float f) {
        super.setAlpha(f);
        int i = (int) (f * 255.0f);
        Drawable drawable = this.mBlurForeground;
        if (drawable != null) {
            drawable.setAlpha(i);
        }
        BlurDrawable blurDrawable = this.mBlurBackground;
        if (blurDrawable != null) {
            blurDrawable.setAlpha(i);
        }
    }

    public boolean setBlurBackground(boolean z) {
        if (isSupportBlur()) {
            if (!z) {
                if (getVisibility() == 0) {
                    setForeground(null);
                    setBackground(null);
                    this.mBlurForeground = null;
                    this.mBlurBackground = null;
                    setVisibility(8);
                    return true;
                }
                return true;
            }
            if (this.mBlurBackground == null) {
                try {
                    createBlurBackground();
                } catch (Exception e) {
                    Log.e("Blur", "Blur creat fail e:" + e);
                    this.mBlurBackground = null;
                    return false;
                }
            }
            if (this.mBlurBackground != null) {
                if (getVisibility() != 0 || getBackground() == null) {
                    setVisibility(0);
                    setForeground(this.mBlurForeground);
                    setBackground(this.mBlurBackground);
                    setAlpha(1.0f);
                    return true;
                }
                return true;
            }
            return true;
        }
        return false;
    }
}
