package com.android.settings.display;

import android.util.Log;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

/* loaded from: classes.dex */
public class CaculateTwilightUtil {
    public int mState;
    public int mSunrise;
    public int mSunset;

    private int formatToMinuteOfDay(long j) {
        int i;
        int i2 = 0;
        try {
            DateFormat dateTimeInstance = DateFormat.getDateTimeInstance();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateTimeInstance.parse(dateTimeInstance.format(Long.valueOf(j))));
            i = calendar.get(11);
            try {
                i2 = calendar.get(12);
            } catch (ParseException e) {
                e = e;
                e.printStackTrace();
                return (i * 60) + i2;
            }
        } catch (ParseException e2) {
            e = e2;
            i = 0;
        }
        return (i * 60) + i2;
    }

    public void calculateTwilight(long j, double d, double d2) {
        Log.w("CaculateTwilightUtil", "calculate twilight time:" + j + " latitude:" + d + " longitude:" + d2);
        float f = ((float) (j - 946728000000L)) / 8.64E7f;
        float f2 = (0.01720197f * f) + 6.24006f;
        double d3 = (double) f2;
        double sin = (Math.sin(d3) * 0.03341960161924362d) + d3 + (Math.sin((double) (2.0f * f2)) * 3.4906598739326E-4d) + (Math.sin((double) (f2 * 3.0f)) * 5.236000106378924E-6d) + 1.796593063d + 3.141592653589793d;
        double d4 = (-d2) / 360.0d;
        double round = ((double) (((float) Math.round(((double) (f - 9.0E-4f)) - d4)) + 9.0E-4f)) + d4 + (Math.sin(d3) * 0.0053d) + (Math.sin(2.0d * sin) * (-0.0069d));
        double asin = Math.asin(Math.sin(sin) * Math.sin(0.4092797040939331d));
        double d5 = d * 0.01745329238474369d;
        double sin2 = (0.0d - (Math.sin(d5) * Math.sin(asin))) / (Math.cos(d5) * Math.cos(asin));
        if (sin2 >= 1.0d) {
            this.mState = 1;
        } else if (sin2 <= -1.0d) {
            this.mState = 0;
        } else {
            double acos = (float) (Math.acos(sin2) / 6.283185307179586d);
            long round2 = Math.round((round + acos) * 8.64E7d) + 946728000000L;
            long round3 = Math.round((round - acos) * 8.64E7d) + 946728000000L;
            if (round3 >= j || round2 <= j) {
                this.mState = 1;
            } else {
                this.mState = 0;
            }
            this.mSunrise = formatToMinuteOfDay(round3);
            this.mSunset = formatToMinuteOfDay(round2);
        }
    }
}
