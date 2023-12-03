package miuix.blurdrawable.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/* loaded from: classes5.dex */
public class MiBlurBackgroundView extends FrameLayout {
    private BlurBackgroundView mBackgroundView;

    public MiBlurBackgroundView(Context context) {
        this(context, null);
    }

    public MiBlurBackgroundView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        addBlurView(context);
    }

    private void addBlurView(Context context) {
        this.mBackgroundView = new BlurBackgroundView(context);
        this.mBackgroundView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.mBackgroundView, 0);
        setBlurBackground(false);
    }

    public boolean isSearchStubSupportBlur() {
        return this.mBackgroundView.isSupportBlur();
    }

    public boolean setBlurBackground(boolean z) {
        return this.mBackgroundView.setBlurBackground(z);
    }
}
