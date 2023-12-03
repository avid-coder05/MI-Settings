package com.android.settings.usagestats.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.android.settings.usagestats.model.AppUsageStats;
import com.android.settings.usagestats.model.AppValueData;
import com.android.settings.usagestats.model.DayAppUsageStats;
import com.android.settings.usagestats.model.DayDeviceUsageStats;
import com.android.settings.usagestats.widget.render.AppUsageViewRender;
import com.android.settings.usagestats.widget.render.BaseViewRender;
import com.android.settings.usagestats.widget.render.IRenderOneAppInterface;
import com.android.settings.usagestats.widget.render.ISetDeviceDataInterface;
import com.android.settings.usagestats.widget.render.NotificationCountViewRender;
import com.android.settings.usagestats.widget.render.OneAppViewRender;
import com.android.settings.usagestats.widget.render.UnLockCountViewRender;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class NewBarChartView extends View {
    public static final String TAG = NewBarChartView.class.getSimpleName();
    protected float mCenterX;
    protected float mCenterY;
    private int mDefaultHeight;
    protected int mHeight;
    protected int mPaddingBottom;
    protected int mPaddingEnd;
    protected int mPaddingLeft;
    protected int mPaddingRight;
    protected int mPaddingStart;
    protected int mPaddingTop;
    protected int mSartY;
    private BaseViewRender mViewRender;
    protected int mWidth;
    private int touchDownX;
    private int touchDownY;

    public NewBarChartView(Context context) {
        this(context, null);
    }

    public NewBarChartView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NewBarChartView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
    }

    private void initRenderValue() {
        BaseViewRender baseViewRender = this.mViewRender;
        if (baseViewRender == null) {
            return;
        }
        baseViewRender.setmHeight(this.mHeight);
        this.mViewRender.setmWidth(this.mWidth);
        this.mViewRender.setmPaddingBottom(this.mPaddingBottom);
        this.mViewRender.setmPaddingEnd(this.mPaddingEnd);
        this.mViewRender.setmPaddingTop(this.mPaddingTop);
        this.mViewRender.setmPaddingStart(this.mPaddingStart);
        this.mViewRender.setmPaddingEnd(this.mPaddingEnd);
        this.mViewRender.setmPaddingRight(this.mPaddingRight);
        this.mViewRender.setmPaddingLeft(this.mPaddingLeft);
        this.mViewRender.setMyView(this);
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            Log.d(TAG, "dispatchTouchEvent: down");
            this.touchDownX = (int) motionEvent.getX();
            this.touchDownY = (int) motionEvent.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (action == 2) {
            Log.d(TAG, "dispatchTouchEvent: move");
            if (Math.abs(((int) motionEvent.getX()) - this.touchDownX) > Math.abs(((int) motionEvent.getY()) - this.touchDownY)) {
                getParent().requestDisallowInterceptTouchEvent(true);
            } else {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        BaseViewRender baseViewRender = this.mViewRender;
        if (baseViewRender != null) {
            baseViewRender.draw(canvas);
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size2 = View.MeasureSpec.getSize(i2);
        if (mode == 1073741824) {
            this.mWidth = size;
        } else {
            this.mWidth = getMeasuredWidth();
            size = 0;
        }
        if (mode2 == 1073741824) {
            this.mHeight = size2;
        } else {
            this.mHeight = this.mDefaultHeight;
            size2 = 0;
        }
        setMeasuredDimension(size, size2);
        this.mCenterX = this.mWidth / 2.0f;
        this.mCenterY = this.mHeight / 2.0f;
        this.mPaddingBottom = getPaddingBottom();
        this.mPaddingLeft = getPaddingLeft();
        this.mPaddingRight = getPaddingRight();
        this.mPaddingTop = getPaddingTop();
        this.mPaddingStart = getPaddingStart();
        this.mPaddingEnd = getPaddingEnd();
        this.mHeight -= this.mPaddingBottom;
        this.mSartY = this.mPaddingTop;
        if (this.mViewRender != null) {
            initRenderValue();
            this.mViewRender.calculateOthers();
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        BaseViewRender baseViewRender = this.mViewRender;
        return baseViewRender != null ? baseViewRender.onTouchEvent(motionEvent) : super.onTouchEvent(motionEvent);
    }

    public void release() {
        BaseViewRender baseViewRender = this.mViewRender;
        if (baseViewRender != null) {
            baseViewRender.release();
        }
    }

    public void setAppUsageList(List<DayAppUsageStats> list) {
        BaseViewRender baseViewRender = this.mViewRender;
        if (baseViewRender instanceof AppUsageViewRender) {
            ((AppUsageViewRender) baseViewRender).setAppUsageList(list);
        } else {
            Log.d(TAG, "setAppUsageList: WOW ! Set week list fail !!!");
        }
    }

    public void setBarType(int i) {
        if (i == 1) {
            this.mViewRender = new AppUsageViewRender(getContext().getApplicationContext());
        } else if (i == 2) {
            this.mViewRender = new NotificationCountViewRender(getContext().getApplicationContext());
        } else if (i == 3) {
            this.mViewRender = new UnLockCountViewRender(getContext().getApplicationContext());
        } else if (i == 4) {
            this.mViewRender = new OneAppViewRender(getContext().getApplicationContext());
        }
        BaseViewRender baseViewRender = this.mViewRender;
        if (baseViewRender != null) {
            baseViewRender.init();
            initRenderValue();
            this.mViewRender.setmBarType(i);
        }
    }

    public void setDeviceUsageList(List<DayDeviceUsageStats> list) {
        BaseViewRender baseViewRender = this.mViewRender;
        if (baseViewRender instanceof ISetDeviceDataInterface) {
            ((ISetDeviceDataInterface) baseViewRender).setDeviceDataList(list);
        } else {
            Log.d(TAG, "setDeviceUsageList: WOW ! Set week list fail !!!");
        }
    }

    public void setOneAppOneDayList(List<AppUsageStats> list) {
        BaseViewRender baseViewRender = this.mViewRender;
        if (baseViewRender instanceof IRenderOneAppInterface) {
            ((IRenderOneAppInterface) baseViewRender).setOneDayList(list);
        } else {
            Log.d(TAG, "setOneAppOneDayList: WOW ! Set week list fail !!!");
        }
    }

    public void setOneAppWeekList(ArrayList<AppValueData> arrayList) {
        BaseViewRender baseViewRender = this.mViewRender;
        if (baseViewRender instanceof IRenderOneAppInterface) {
            ((IRenderOneAppInterface) baseViewRender).setWeekList(arrayList);
        } else {
            Log.d(TAG, "setOneAppWeekList: WOW ! Set week list fail !!!");
        }
    }

    public void setOneDayDataList(List<Integer> list) {
        BaseViewRender baseViewRender = this.mViewRender;
        if (baseViewRender instanceof ISetDeviceDataInterface) {
            ((ISetDeviceDataInterface) baseViewRender).setOneDayList(list);
        } else {
            Log.d(TAG, "setDeviceUsageList: WOW ! Set week list fail !!!");
        }
    }

    public void setWeekData(boolean z) {
        this.mViewRender.setWeekData(z);
        invalidate();
    }
}
