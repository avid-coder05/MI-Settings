package miuix.provision;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/* loaded from: classes5.dex */
public class CustomDispatchFrameLayout extends FrameLayout {
    protected ProvisionAnimHelper mProvisionAnimHelper;

    public CustomDispatchFrameLayout(Context context) {
        super(context);
    }

    public CustomDispatchFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomDispatchFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public CustomDispatchFrameLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (isAnimEnded()) {
            return super.dispatchTouchEvent(motionEvent);
        }
        Log.w("OobeUtil2", "anim not end, skip touch event");
        return true;
    }

    protected boolean isAnimEnded() {
        ProvisionAnimHelper provisionAnimHelper = this.mProvisionAnimHelper;
        if (provisionAnimHelper != null) {
            return provisionAnimHelper.isAnimEnded();
        }
        return true;
    }

    public void setProvisionAnimHelper(ProvisionAnimHelper provisionAnimHelper) {
        this.mProvisionAnimHelper = provisionAnimHelper;
    }
}
