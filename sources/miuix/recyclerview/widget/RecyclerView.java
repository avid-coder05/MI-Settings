package miuix.recyclerview.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.recyclerview.R$attr;
import androidx.recyclerview.widget.SpringRecyclerView;
import miuix.recyclerview.tool.GetSpeedForDynamicRefreshRate;

/* loaded from: classes5.dex */
public class RecyclerView extends SpringRecyclerView {
    private final GetSpeedForDynamicRefreshRate mGetSpeedForDynamicRefreshRate;

    public RecyclerView(Context context) {
        this(context, null);
    }

    public RecyclerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.recyclerViewStyle);
    }

    public RecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setItemAnimator(new MiuiDefaultItemAnimator());
        if (Build.VERSION.SDK_INT > 30) {
            this.mGetSpeedForDynamicRefreshRate = new GetSpeedForDynamicRefreshRate(this);
        } else {
            this.mGetSpeedForDynamicRefreshRate = null;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        GetSpeedForDynamicRefreshRate getSpeedForDynamicRefreshRate = this.mGetSpeedForDynamicRefreshRate;
        if (getSpeedForDynamicRefreshRate != null) {
            getSpeedForDynamicRefreshRate.touchEvent(motionEvent);
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public boolean fling(int i, int i2) {
        if (Math.abs(i) < 300) {
            i = 0;
        }
        if (Math.abs(i2) < 300) {
            i2 = 0;
        }
        if (i == 0 && i2 == 0) {
            return false;
        }
        return super.fling(i, i2);
    }

    @Override // android.view.View
    protected void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        GetSpeedForDynamicRefreshRate getSpeedForDynamicRefreshRate = this.mGetSpeedForDynamicRefreshRate;
        if (getSpeedForDynamicRefreshRate != null) {
            getSpeedForDynamicRefreshRate.onFocusChange(z);
        }
    }

    @Override // androidx.recyclerview.widget.SpringRecyclerView, androidx.recyclerview.widget.RecyclerView
    public void onScrollStateChanged(int i) {
        super.onScrollStateChanged(i);
        GetSpeedForDynamicRefreshRate getSpeedForDynamicRefreshRate = this.mGetSpeedForDynamicRefreshRate;
        if (getSpeedForDynamicRefreshRate != null) {
            getSpeedForDynamicRefreshRate.scrollState(this, i);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void onScrolled(int i, int i2) {
        GetSpeedForDynamicRefreshRate getSpeedForDynamicRefreshRate = this.mGetSpeedForDynamicRefreshRate;
        if (getSpeedForDynamicRefreshRate != null) {
            getSpeedForDynamicRefreshRate.calculateSpeed(i, i2);
        }
        super.onScrolled(i, i2);
    }
}
