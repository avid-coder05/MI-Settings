package com.android.settings.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.utils.EaseManager;

/* loaded from: classes2.dex */
public final class WaterBox extends RelativeLayout implements SensorEventListener {
    private final WaterData data;
    private AnimConfig edgeRotAnimConfig;
    private PointF endPoint;
    private boolean initialized;
    private float[] mAccValues;
    private float[][] mAccValuesForAverage;
    private int mAccValuesForAverageIndex;
    private Sensor mAccelerometer;
    private int mColor;
    private final Path mCornerPath;
    private float mCornerRadius;
    private boolean mDebug;
    private final Paint mDebugPaint;
    private boolean mIsValueSet;
    private boolean mIsVisible;
    private RectF mRectF;
    private float mSensorLastAngle;
    private long mSensorLastChangedTime;
    private SensorManager mSensorManager;
    private float mValue;
    private final Paint mWaterPaint;
    private final List<AnimConfig> pointAnimConfigs;
    private final List<PointF> points;
    private float preAngle;
    private PointF realEndPoint;
    private PointF realStartPoint;
    private AnimConfig rotAnimConfig;
    private PointF startPoint;
    private AnimConfig valueAnimConfig;
    private boolean varInitialized;
    private AnimConfig waterAlphaAnimConfig;

    /* loaded from: classes2.dex */
    public static final class LineEndPoints {
        private final PointF endPoint;
        private final PointF startPoint;

        public LineEndPoints(PointF pointF, PointF pointF2) {
            this.startPoint = pointF;
            this.endPoint = pointF2;
        }

        public final PointF getEndPoint() {
            return this.endPoint;
        }

        public final PointF getStartPoint() {
            return this.startPoint;
        }

        public int hashCode() {
            PointF pointF = this.startPoint;
            int hashCode = (pointF != null ? pointF.hashCode() : 0) * 31;
            PointF pointF2 = this.endPoint;
            return hashCode + (pointF2 != null ? pointF2.hashCode() : 0);
        }

        public String toString() {
            return "LineEndPoints(startPoint=" + this.startPoint + ", endPoint=" + this.endPoint + ")";
        }
    }

    /* loaded from: classes2.dex */
    public static final class WaterData {
        private float effectPer;
        private float edgeRot = 90.0f;
        private float rot = 90.0f;
        private float value = 1.0f;
        private float waterAlpha = 1.0f;

        public final float getEdgeRot() {
            return this.edgeRot;
        }

        public final float getEffectPer() {
            return this.effectPer;
        }

        public final float getRot() {
            return this.rot;
        }

        public final float getValue() {
            return this.value;
        }

        public final float getWaterAlpha() {
            return this.waterAlpha;
        }

        public final void setEdgeRot(float f) {
            this.edgeRot = f;
        }

        public final void setEffectPer(float f) {
            this.effectPer = f;
        }

        public final void setRot(float f) {
            this.rot = f;
        }

        public final void setValue(float f) {
            this.value = f;
        }

        public final void setWaterAlpha(float f) {
            this.waterAlpha = f;
        }
    }

    public WaterBox(Context context) {
        super(context);
        WaterData waterData = new WaterData();
        this.data = waterData;
        this.mAccValuesForAverage = new float[2];
        this.mAccValuesForAverageIndex = 0;
        this.mColor = Color.parseColor("#330084FF");
        this.mCornerPath = new Path();
        this.mCornerRadius = 48.0f;
        this.mDebug = new File(Environment.getExternalStorageDirectory(), "water_box").exists();
        this.mDebugPaint = new Paint();
        this.mRectF = new RectF();
        this.mValue = 1.0f;
        this.mWaterPaint = new Paint();
        this.pointAnimConfigs = new ArrayList();
        this.points = new ArrayList();
        this.preAngle = waterData.getRot();
        this.mSensorLastChangedTime = Long.MAX_VALUE;
    }

    public WaterBox(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        WaterData waterData = new WaterData();
        this.data = waterData;
        this.mAccValuesForAverage = new float[2];
        this.mAccValuesForAverageIndex = 0;
        this.mColor = Color.parseColor("#330084FF");
        this.mCornerPath = new Path();
        this.mCornerRadius = 48.0f;
        this.mDebug = new File(Environment.getExternalStorageDirectory(), "water_box").exists();
        this.mDebugPaint = new Paint();
        this.mRectF = new RectF();
        this.mValue = 1.0f;
        this.mWaterPaint = new Paint();
        this.pointAnimConfigs = new ArrayList();
        this.points = new ArrayList();
        this.preAngle = waterData.getRot();
        this.mSensorLastChangedTime = Long.MAX_VALUE;
    }

    public WaterBox(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        WaterData waterData = new WaterData();
        this.data = waterData;
        this.mAccValuesForAverage = new float[2];
        this.mAccValuesForAverageIndex = 0;
        this.mColor = Color.parseColor("#330084FF");
        this.mCornerPath = new Path();
        this.mCornerRadius = 48.0f;
        this.mDebug = new File(Environment.getExternalStorageDirectory(), "water_box").exists();
        this.mDebugPaint = new Paint();
        this.mRectF = new RectF();
        this.mValue = 1.0f;
        this.mWaterPaint = new Paint();
        this.pointAnimConfigs = new ArrayList();
        this.points = new ArrayList();
        this.preAngle = waterData.getRot();
        this.mSensorLastChangedTime = Long.MAX_VALUE;
    }

    private final PointF avgPoints(PointF pointF, PointF pointF2) {
        return new PointF((pointF.x + pointF2.x) / 2.0f, (pointF.y + pointF2.y) / 2.0f);
    }

    private final void beginEnterAnim() {
        if (this.mIsValueSet) {
            this.data.setValue(Math.min(1.0f, this.mValue + 0.3f));
            setValue(this.mValue);
        }
    }

    private void checkInitVars() {
        if (this.varInitialized) {
            return;
        }
        this.varInitialized = true;
        this.edgeRotAnimConfig = new AnimConfig().setEase(EaseManager.getStyle(-2, 1.0f, 0.9f));
        this.rotAnimConfig = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.4f, 0.8f));
        this.waterAlphaAnimConfig = new AnimConfig().setEase(EaseManager.getStyle(-2, 1.0f, 1.0f));
        this.valueAnimConfig = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.9f, 1.0f));
    }

    private final void drawDebug(Canvas canvas) {
        double value = this.data.getValue();
        Double.isNaN(value);
        double abs = Math.abs(value - 0.5d);
        this.mDebugPaint.setStyle(Paint.Style.STROKE);
        float width = getWidth() / 2;
        float height = getHeight() / 2;
        double height2 = getHeight();
        Double.isNaN(height2);
        canvas.drawCircle(width, height, (float) (height2 * abs), this.mDebugPaint);
        this.mDebugPaint.setStyle(Paint.Style.FILL);
        this.mDebugPaint.setColor(-16711936);
        for (PointF pointF : this.points) {
            canvas.drawCircle(pointF.x, pointF.y, 8.0f, this.mDebugPaint);
        }
        this.mDebugPaint.setColor(-65536);
        PointF pointF2 = this.startPoint;
        canvas.drawCircle(pointF2.x, pointF2.y, 8.0f, this.mDebugPaint);
        PointF pointF3 = this.endPoint;
        canvas.drawCircle(pointF3.x, pointF3.y, 8.0f, this.mDebugPaint);
        this.mDebugPaint.setColor(-7829368);
        PointF pointF4 = this.realStartPoint;
        float f = pointF4.x;
        float f2 = pointF4.y;
        PointF pointF5 = this.realEndPoint;
        canvas.drawLine(f, f2, pointF5.x, pointF5.y, this.mDebugPaint);
        this.mDebugPaint.setColor(-65536);
        PointF pointF6 = this.startPoint;
        float f3 = pointF6.x;
        float f4 = pointF6.y;
        PointF pointF7 = this.endPoint;
        canvas.drawLine(f3, f4, pointF7.x, pointF7.y, this.mDebugPaint);
    }

    private final void drawWater(Canvas canvas) {
        List<PointF> asList;
        PointF pointF = new PointF(0.0f, 0.0f);
        PointF pointF2 = new PointF(0.0f, getHeight());
        PointF pointF3 = new PointF(getWidth(), getHeight());
        PointF pointF4 = new PointF(getWidth(), 0.0f);
        PointF pointF5 = this.startPoint;
        PointF pointF6 = this.endPoint;
        int edge = getEdge(pointF5);
        int edge2 = getEdge(pointF6);
        if (edge == 1 && edge2 == 1) {
            asList = Arrays.asList(pointF, pointF2, pointF3, pointF4);
        } else if (edge == 1 && edge2 == 2) {
            asList = Arrays.asList(pointF);
        } else if (edge == 1 && edge2 == 3) {
            asList = Arrays.asList(pointF, pointF2);
        } else if (edge == 1 && edge2 == 4) {
            asList = Arrays.asList(pointF, pointF2, pointF3);
        } else if (edge == 2 && edge2 == 2) {
            asList = Arrays.asList(pointF2, pointF3, pointF4, pointF);
        } else if (edge == 2 && edge2 == 3) {
            asList = Arrays.asList(pointF2);
        } else if (edge == 2 && edge2 == 4) {
            asList = Arrays.asList(pointF2, pointF3);
        } else if (edge == 2 && edge2 == 1) {
            asList = Arrays.asList(pointF2, pointF3, pointF4);
        } else if (edge == 3 && edge2 == 3) {
            asList = Arrays.asList(pointF2, pointF3);
        } else if (edge == 3 && edge2 == 4) {
            asList = Arrays.asList(pointF3);
        } else if (edge == 3 && edge2 == 1) {
            asList = Arrays.asList(pointF3, pointF4);
        } else if (edge == 3 && edge2 == 2) {
            asList = Arrays.asList(pointF3, pointF4, pointF);
        } else if (edge == 4 && edge2 == 4) {
            asList = Arrays.asList(pointF4, pointF, pointF2, pointF3);
        } else if (edge == 4 && edge2 == 1) {
            asList = Arrays.asList(pointF4);
        } else if (edge == 4 && edge2 == 2) {
            asList = Arrays.asList(pointF4, pointF);
        } else if (edge != 4 || edge2 != 3) {
            throw new IllegalStateException();
        } else {
            asList = Arrays.asList(pointF4, pointF, pointF2);
        }
        Collections.reverse(asList);
        Path path = new Path();
        if (Float.isNaN(pointF6.x) || Float.isInfinite(pointF6.x)) {
            Log.w("WaterBox", "endP.x error");
            pointF6.x = getWidth();
        }
        if (Float.isNaN(pointF5.x) || Float.isInfinite(pointF5.x)) {
            Log.w("WaterBox", "startP.x error");
            pointF5.x = 0.0f;
        }
        path.moveTo(pointF6.x, pointF6.y);
        for (PointF pointF7 : asList) {
            path.lineTo(pointF7.x, pointF7.y);
        }
        path.lineTo(pointF5.x, pointF5.y);
        ArrayList arrayList = new ArrayList();
        arrayList.add(pointF5);
        arrayList.addAll(this.points);
        arrayList.add(pointF6);
        PointF avgPoints = avgPoints((PointF) arrayList.get(0), (PointF) arrayList.get(1));
        path.lineTo(avgPoints.x, avgPoints.y);
        int size = arrayList.size();
        for (int i = 2; i < size; i++) {
            int i2 = i - 1;
            PointF avgPoints2 = avgPoints((PointF) arrayList.get(i2), (PointF) arrayList.get(i));
            path.quadTo(((PointF) arrayList.get(i2)).x, ((PointF) arrayList.get(i2)).y, avgPoints2.x, avgPoints2.y);
        }
        path.lineTo(pointF6.x, pointF6.y);
        updateWaterPaintColorAndAlpha();
        canvas.drawPath(path, this.mWaterPaint);
    }

    private final void followRot() {
        LineEndPoints lineEnd = getLineEnd(this.data.getValue(), this.data.getRot());
        this.realStartPoint.x = lineEnd.getStartPoint().x;
        this.realStartPoint.y = lineEnd.getStartPoint().y;
        this.realEndPoint.x = lineEnd.getEndPoint().x;
        this.realEndPoint.y = lineEnd.getEndPoint().y;
        Folme.useValue(this.data).setFlags(1L).to("edgeRot", Float.valueOf(this.data.getRot()), this.edgeRotAnimConfig);
        int i = 0;
        for (PointF pointF : this.points) {
            int i2 = i + 1;
            float pointPer = getPointPer(i);
            AnimConfig animConfig = this.pointAnimConfigs.get(i);
            Folme.useValue(pointF).setFlags(1L).to("x", Float.valueOf(valFromPer(pointPer, this.realStartPoint.x, this.realEndPoint.x)), animConfig);
            Folme.useValue(pointF).setFlags(1L).to("y", Float.valueOf(valFromPer(pointPer, this.realStartPoint.y, this.realEndPoint.y)), animConfig);
            i = i2;
        }
        LineEndPoints lineEnd2 = getLineEnd(this.data.getValue(), this.data.getEdgeRot());
        this.startPoint.x = lineEnd2.getStartPoint().x;
        this.startPoint.y = lineEnd2.getStartPoint().y;
        this.endPoint.x = lineEnd2.getEndPoint().x;
        this.endPoint.y = lineEnd2.getEndPoint().y;
    }

    private final int getEdge(PointF pointF) {
        if (near(pointF.x, 0.0f)) {
            return 2;
        }
        if (near(pointF.x, getWidth())) {
            return 4;
        }
        return (near(pointF.y, 0.0f) || !near(pointF.y, (float) getHeight())) ? 1 : 3;
    }

    private final LineEndPoints getLineEnd(float f, float f2) {
        PointF linePoint = getLinePoint(f, f2, -1);
        PointF linePoint2 = getLinePoint(f, f2, 1);
        float f3 = f2 % 360.0f;
        if (f3 < 0.0f) {
            f3 += 360.0f;
        }
        if (f3 > 180.0f) {
            linePoint = linePoint2;
        }
        return new LineEndPoints(linePoint, linePoint2);
    }

    private final PointF getLineEndFunc(PointF pointF, float f, float f2, float f3) {
        PointF pointF2 = new PointF(pointF.x + (((float) Math.cos(toRad(f))) * 35.0f), pointF.y + (((float) Math.sin(toRad(f))) * 35.0f));
        float f4 = pointF2.y;
        float f5 = pointF.y;
        float f6 = f4 - f5;
        float f7 = pointF.x;
        float f8 = pointF2.x;
        float f9 = f7 - f8;
        float f10 = -((f8 * f5) - (f7 * f4));
        return new PointF((f10 - (f3 * f9)) / f6, (f10 - (f6 * f2)) / f9);
    }

    private final PointF getLinePoint(float f, float f2, int i) {
        PointF pointF = new PointF();
        float height = (0.5f - f) * getHeight();
        pointF.x = (getWidth() / 2.0f) + (((float) Math.cos(toRad(f2))) * height);
        pointF.y = (getHeight() / 2.0f) + (((float) Math.sin(toRad(f2))) * height);
        PointF pointF2 = new PointF();
        float width = (getWidth() / 2.0f) + ((getWidth() * i) / 2.0f);
        pointF2.x = width;
        float f3 = f2 - (i * 90);
        pointF2.y = getLineEndFunc(pointF, f3, width, Float.NaN).y;
        if (Math.abs(this.data.getRot()) != 90.0f) {
            float max = Math.max(pointF2.y, (getHeight() / 2.0f) - (getHeight() / 2));
            pointF2.y = max;
            float min = Math.min(max, (getHeight() / 2.0f) + (getHeight() / 2));
            pointF2.y = min;
            pointF2.x = getLineEndFunc(pointF, f3, Float.NaN, min).x;
        }
        return pointF2;
    }

    private final float getPointPer(int i) {
        return i / 4.0f;
    }

    private final float getWaterAlphaByValue(float f) {
        return f == 0.0f ? 0.0f : 1.0f;
    }

    private void handleNewSensorAverageValue(float[] fArr) {
        this.mAccValues = fArr;
        float f = fArr[0] / 10.0f;
        float f2 = fArr[1] / 10.0f;
        float f3 = fArr[2] / 10.0f;
        float angle = toAngle(-((float) Math.atan2(-f2, -f)));
        if (angle < 0.0f) {
            angle += 360.0f;
        }
        boolean z = !isSensorNotChangedForAWhile();
        boolean isSensorAngleChanged = isSensorAngleChanged(angle);
        if (z || isSensorAngleChanged) {
            Log.i("WaterBox", "waterbox refresh anim: isActive=" + z + " sensorAngleChanged=" + isSensorAngleChanged);
            if (isSensorAngleChanged) {
                this.mSensorLastChangedTime = System.currentTimeMillis();
                this.mSensorLastAngle = angle;
            }
            rotToAngle(angle);
            Folme.useValue(this.data).setFlags(1L).to("effectPer", Float.valueOf(1.0f - Math.abs(f3)));
            invalidate();
        }
    }

    private final void init() {
        if (this.initialized) {
            return;
        }
        this.initialized = true;
        this.mDebugPaint.setStrokeWidth(2.0f);
        this.mDebugPaint.setTextSize(40.0f);
        this.mDebugPaint.setColor(-65536);
        this.mDebugPaint.setTextAlign(Paint.Align.LEFT);
        this.mDebugPaint.setAntiAlias(true);
        this.data.setWaterAlpha(getWaterAlphaByValue(this.mValue));
        updateWaterPaintColorAndAlpha();
        this.mWaterPaint.setAntiAlias(true);
        PointF pointF = new PointF();
        this.realStartPoint = pointF;
        pointF.x = 0.0f;
        pointF.y = getHeight() - (getHeight() * this.data.getValue());
        PointF pointF2 = new PointF();
        this.realEndPoint = pointF2;
        pointF2.x = getWidth();
        this.realEndPoint.y = getHeight() - (getHeight() * this.data.getValue());
        for (int i = 0; i < 5; i++) {
            float pointPer = getPointPer(i);
            this.points.add(new PointF(getWidth() * pointPer, getHeight() - (getHeight() * this.data.getValue())));
            double d = pointPer;
            Double.isNaN(d);
            this.pointAnimConfigs.add(new AnimConfig().setEase(EaseManager.getStyle(-2, 0.8f - (((float) Math.sin(d * 3.141592653589793d)) * 0.5f), 1.0f)));
        }
        PointF pointF3 = new PointF();
        this.startPoint = pointF3;
        pointF3.x = 0.0f;
        pointF3.y = getHeight() - (getHeight() * this.data.getValue());
        PointF pointF4 = new PointF();
        this.endPoint = pointF4;
        pointF4.x = getWidth();
        this.endPoint.y = getHeight() - (getHeight() * this.data.getValue());
    }

    private boolean isSensorAngleChanged(float f) {
        return Math.abs(this.mSensorLastAngle - f) > 9.0f;
    }

    private boolean isSensorNotChangedForAWhile() {
        return System.currentTimeMillis() - this.mSensorLastChangedTime > 2000;
    }

    private final boolean near(float f, float f2) {
        return ((double) Math.abs(f - f2)) < 3.0d;
    }

    private final float normalizeValue(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        if (f > 1.0f) {
            f = 1.0f;
        }
        double d = f;
        float f2 = (d < 0.0d || d > 0.01d) ? f : 0.0f;
        double d2 = f2;
        if (d2 > 0.01d && d2 < 0.03d) {
            f2 = 0.03f;
        }
        double d3 = f2;
        if (d3 > 0.97d && d3 < 0.99d) {
            f2 = 0.97f;
        }
        double d4 = f2;
        if (d4 < 0.99d || d4 > 1.0d) {
            return f2;
        }
        return 1.0f;
    }

    private final void onInvisible() {
        unregisterSensorListener();
    }

    private final void onVisible() {
        registerSensorListener();
        this.mSensorLastChangedTime = System.currentTimeMillis();
        beginEnterAnim();
    }

    private final void registerSensorListener() {
        Sensor sensor = this.mAccelerometer;
        if (sensor != null) {
            this.mSensorManager.registerListener(this, sensor, 2);
            Log.d("WaterBox", "registerListener");
        }
    }

    private final void resetPath() {
        this.mCornerPath.reset();
        Path path = this.mCornerPath;
        RectF rectF = this.mRectF;
        float f = this.mCornerRadius;
        path.addRoundRect(rectF, f, f, Path.Direction.CW);
        this.mCornerPath.close();
    }

    private final void rotToAngle(float f) {
        float f2 = f - (f > 270.0f ? 450 : 90);
        if (this.data.getValue() == 1.0f) {
            this.data.setEffectPer(0.0f);
        }
        float min = Math.min(this.data.getEffectPer() * 35.0f, Math.max(this.data.getEffectPer() * (-35.0f), f2)) + 90.0f;
        while (min > this.preAngle + 180.0f) {
            min -= 360.0f;
        }
        while (min < this.preAngle - 180.0f) {
            min += 360.0f;
        }
        Folme.useValue(this.data).setFlags(1L).to("rot", Float.valueOf(min), this.rotAnimConfig);
    }

    private final float toAngle(float f) {
        double d = f * 180.0f;
        Double.isNaN(d);
        return (float) (d / 3.141592653589793d);
    }

    private final float toRad(float f) {
        double d = f;
        Double.isNaN(d);
        Double.isNaN(180.0d);
        return (float) ((d * 3.141592653589793d) / 180.0d);
    }

    private final void unregisterSensorListener() {
        if (this.mAccelerometer != null) {
            this.mSensorManager.unregisterListener(this);
            Log.d("WaterBox", "unregisterListener");
        }
    }

    private final void updateWaterPaintColorAndAlpha() {
        this.mWaterPaint.setColor(this.mColor);
        this.mWaterPaint.setAlpha((int) (r0.getAlpha() * this.data.getWaterAlpha()));
    }

    private final float valFromPer(float f, float f2, float f3) {
        return ((1.0f - f) * f2) + (f * f3);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        int save = canvas.save();
        canvas.clipPath(this.mCornerPath);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(save);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        int save = canvas.save();
        canvas.clipPath(this.mCornerPath);
        super.draw(canvas);
        canvas.restoreToCount(save);
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Object systemService = getContext().getSystemService("sensor");
        if (systemService != null) {
            SensorManager sensorManager = (SensorManager) systemService;
            this.mSensorManager = sensorManager;
            this.mAccelerometer = sensorManager.getDefaultSensor(1);
            checkInitVars();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterSensorListener();
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        followRot();
        drawWater(canvas);
        if (this.mDebug) {
            drawDebug(canvas);
        }
        if (!isSensorNotChangedForAWhile()) {
            invalidate();
        }
        super.onDraw(canvas);
    }

    @Override // android.widget.RelativeLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        init();
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent != null) {
            float[] fArr = sensorEvent.values;
            float[][] fArr2 = this.mAccValuesForAverage;
            int i = this.mAccValuesForAverageIndex;
            fArr2[i] = fArr;
            int i2 = i + 1;
            this.mAccValuesForAverageIndex = i2;
            if (i2 == 2) {
                this.mAccValuesForAverageIndex = 0;
                float[] fArr3 = new float[3];
                for (int i3 = 0; i3 < 3; i3++) {
                    for (int i4 = 0; i4 < 2; i4++) {
                        fArr3[i3] = fArr3[i3] + fArr2[i4][i3];
                    }
                }
                for (int i5 = 0; i5 < 3; i5++) {
                    fArr3[i5] = fArr3[i5] / 2.0f;
                }
                handleNewSensorAverageValue(fArr3);
            }
        }
    }

    @Override // android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mRectF = new RectF(0.0f, 0.0f, i, i2);
        resetPath();
    }

    @Override // android.view.View
    public void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        if (i == 0) {
            if (!this.mIsVisible) {
                this.mIsVisible = true;
                onVisible();
            }
        } else if (this.mIsVisible) {
            this.mIsVisible = false;
            onInvisible();
        }
        Log.d("WaterBox", "onVisibilityChanged: " + i);
    }

    public final void setColor(int i) {
        this.mColor = i;
        updateWaterPaintColorAndAlpha();
    }

    public final void setCornerRadius(float f) {
        this.mCornerRadius = f;
        resetPath();
    }

    public final void setDebug(boolean z) {
        this.mDebug = z;
    }

    public final void setValue(float f) {
        this.mIsValueSet = true;
        checkInitVars();
        float normalizeValue = normalizeValue(f);
        this.mValue = normalizeValue;
        Folme.useValue(this.data).setFlags(1L).to("value", Float.valueOf(normalizeValue), this.valueAnimConfig);
        Folme.useValue(this.data).setFlags(1L).to("waterAlpha", Float.valueOf(getWaterAlphaByValue(normalizeValue)), this.waterAlphaAnimConfig);
        invalidate();
    }
}
