package com.android.settings;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Calendar;

/* loaded from: classes.dex */
public class TimerView extends FrameLayout {
    private Bitmap mBmpHour;
    private Bitmap mBmpMinute;
    private Calendar mCalendar;
    private TextView mDate;
    private int mHalfHeight;
    private int mHalfWidth;
    private int mHeight;
    private ImageView mHourIV;
    private Matrix mMatrix;
    private ImageView mMinuteIV;
    private TextView mTime;
    private int mWidth;

    public TimerView(Context context) {
        super(context);
        this.mMatrix = new Matrix();
        init(context);
    }

    public TimerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMatrix = new Matrix();
        init(context);
    }

    public TimerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMatrix = new Matrix();
        init(context);
    }

    private void init(Context context) {
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.usage_timer, this);
        this.mBmpHour = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.clock_hour)).getBitmap();
        this.mBmpMinute = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.clock_minutes)).getBitmap();
        this.mWidth = this.mBmpHour.getWidth();
        int height = this.mBmpHour.getHeight();
        this.mHeight = height;
        this.mHalfWidth = this.mWidth >> 1;
        this.mHalfHeight = height >> 1;
        this.mTime = (TextView) findViewById(R.id.usage_time);
        this.mDate = (TextView) findViewById(R.id.usage_date);
        this.mHourIV = (ImageView) findViewById(R.id.clock_hour);
        this.mMinuteIV = (ImageView) findViewById(R.id.clock_minute);
        this.mCalendar = Calendar.getInstance();
    }

    public void setTimer(Long l) {
        this.mCalendar.setTimeInMillis(l.longValue());
        this.mTime.setText(String.format("%d:%02d:%02d", Integer.valueOf(this.mCalendar.get(11)), Integer.valueOf(this.mCalendar.get(12)), Integer.valueOf(this.mCalendar.get(13))));
        this.mDate.setText(String.format("%d.%d.%d", Integer.valueOf(this.mCalendar.get(1)), Integer.valueOf(this.mCalendar.get(2) + 1), Integer.valueOf(this.mCalendar.get(5))));
        int i = this.mCalendar.get(10);
        this.mMatrix.reset();
        this.mMatrix.setRotate(i * 30, this.mHalfHeight, this.mHalfWidth);
        this.mHourIV.setImageBitmap(Bitmap.createBitmap(this.mBmpHour, 0, 0, this.mWidth, this.mHeight, this.mMatrix, true));
        this.mMatrix.reset();
        this.mMatrix.setRotate(r0 * 6, this.mHalfHeight, this.mHalfWidth);
        this.mMinuteIV.setImageBitmap(Bitmap.createBitmap(this.mBmpMinute, 0, 0, this.mWidth, this.mHeight, this.mMatrix, true));
    }
}
