package com.android.settings.gestures;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.settings.R;

/* loaded from: classes.dex */
public class BackGestureIndicatorView extends LinearLayout {
    private ViewGroup mLayout;
    private BackGestureIndicatorDrawable mLeftDrawable;
    private ImageView mLeftIndicator;
    private BackGestureIndicatorDrawable mRightDrawable;
    private ImageView mRightIndicator;

    public BackGestureIndicatorView(Context context) {
        super(context);
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.back_gesture_indicator_container, (ViewGroup) this, false);
        this.mLayout = viewGroup;
        if (viewGroup == null) {
            return;
        }
        addView(viewGroup);
        this.mLeftDrawable = new BackGestureIndicatorDrawable(context, false);
        this.mRightDrawable = new BackGestureIndicatorDrawable(context, true);
        this.mLeftIndicator = (ImageView) this.mLayout.findViewById(R.id.indicator_left);
        this.mRightIndicator = (ImageView) this.mLayout.findViewById(R.id.indicator_right);
        this.mLeftIndicator.setImageDrawable(this.mLeftDrawable);
        this.mRightIndicator.setImageDrawable(this.mRightDrawable);
        int systemUiVisibility = getSystemUiVisibility() | MiuiWindowManager$LayoutParams.EXTRA_FLAG_FINDDEVICE_KEYGUARD | 256 | 512 | MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE | 2 | 4;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{16844140, 16844000});
        systemUiVisibility = obtainStyledAttributes.getBoolean(0, false) ? systemUiVisibility | 16 : systemUiVisibility;
        systemUiVisibility = obtainStyledAttributes.getBoolean(1, false) ? systemUiVisibility | 8192 : systemUiVisibility;
        obtainStyledAttributes.recycle();
        setSystemUiVisibility(systemUiVisibility);
    }

    public WindowManager.LayoutParams getLayoutParams(WindowManager.LayoutParams layoutParams) {
        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams(2038, (layoutParams.flags & Integer.MIN_VALUE) | 16777240, -3);
        layoutParams2.setTitle("BackGestureIndicatorView");
        layoutParams2.token = getContext().getActivityToken();
        return layoutParams2;
    }

    public void setIndicatorWidth(int i, boolean z) {
        (z ? this.mLeftDrawable : this.mRightDrawable).setWidth(i);
    }
}
