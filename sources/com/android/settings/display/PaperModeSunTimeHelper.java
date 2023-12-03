package com.android.settings.display;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import miui.provider.Weather;

/* loaded from: classes.dex */
public class PaperModeSunTimeHelper {

    /* loaded from: classes.dex */
    public static class SunTime {
        private int state;
        private int sunrise;
        private int sunset;

        public SunTime(int i, int i2, int i3) {
            this.sunrise = i;
            this.sunset = i2;
            this.state = i3;
        }

        public int getState() {
            return this.state;
        }

        public int getSunrise() {
            return this.sunrise;
        }

        public int getSunset() {
            return this.sunset;
        }
    }

    public static void broadcastSunTime(Context context, SunTime sunTime) {
        if (context == null || sunTime == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.LOCATION_CHANGED");
        intent.putExtra(Weather.WeatherBaseColumns.SUNRISE, sunTime.getSunrise());
        intent.putExtra(Weather.WeatherBaseColumns.SUNSET, sunTime.getSunset());
        intent.putExtra("state", sunTime.getState());
        intent.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_SCREEN_PROJECTION);
        context.sendBroadcast(intent);
    }

    public static SunTime calculateTwilightTime(Location location) {
        CaculateTwilightUtil caculateTwilightUtil = new CaculateTwilightUtil();
        caculateTwilightUtil.calculateTwilight(System.currentTimeMillis(), location.getLatitude(), location.getLongitude());
        return new SunTime(caculateTwilightUtil.mSunrise, caculateTwilightUtil.mSunset, caculateTwilightUtil.mState);
    }

    private static void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    private static int formatToMinuteOfDay(long j) {
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

    public static SunTime getSunTwilightTime(Context context) {
        if (isLocationCity(context)) {
            return getTimeFromDB(context);
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0 */
    /* JADX WARN: Type inference failed for: r2v1, types: [android.database.Cursor] */
    /* JADX WARN: Type inference failed for: r2v2 */
    private static SunTime getTimeFromDB(Context context) {
        Cursor cursor;
        ?? r2 = 0;
        try {
            try {
                cursor = context.getContentResolver().query(Uri.parse("content://weather/actualWeatherData").buildUpon().appendPath("2").appendPath("1").build(), new String[]{Weather.WeatherBaseColumns.SUNRISE, Weather.WeatherBaseColumns.SUNSET}, null, null, null);
                if (cursor != null) {
                    try {
                        if (cursor.moveToNext()) {
                            long j = cursor.getLong(cursor.getColumnIndex(Weather.WeatherBaseColumns.SUNRISE));
                            long j2 = cursor.getLong(cursor.getColumnIndex(Weather.WeatherBaseColumns.SUNSET));
                            Log.d("PaperModeSunTimeHelper", "getTimeFromDB Success :sunrise=" + j + ",sunset=" + j2);
                            SunTime sunTime = new SunTime(formatToMinuteOfDay(j), formatToMinuteOfDay(j2), 0);
                            closeCursor(cursor);
                            return sunTime;
                        }
                    } catch (Exception e) {
                        e = e;
                        e.printStackTrace();
                        closeCursor(cursor);
                        return null;
                    }
                }
            } catch (Throwable th) {
                th = th;
                r2 = context;
                closeCursor(r2);
                throw th;
            }
        } catch (Exception e2) {
            e = e2;
            cursor = null;
        } catch (Throwable th2) {
            th = th2;
            closeCursor(r2);
            throw th;
        }
        closeCursor(cursor);
        return null;
    }

    private static boolean isLocationCity(Context context) {
        Cursor cursor = null;
        try {
            try {
                cursor = context.getContentResolver().query(Uri.parse("content://weather/selected_city"), new String[]{"flag"}, null, null, "position");
                if (cursor != null && cursor.moveToFirst()) {
                    return 1 == cursor.getInt(cursor.getColumnIndex("flag"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        } finally {
            closeCursor(cursor);
        }
    }
}
