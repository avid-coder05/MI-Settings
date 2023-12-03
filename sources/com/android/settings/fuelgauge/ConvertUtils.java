package com.android.settings.fuelgauge;

import android.content.ContentValues;
import android.content.Context;
import android.os.LocaleList;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/* loaded from: classes.dex */
public final class ConvertUtils {
    private static boolean sIs24HourFormat;
    static Locale sLocale;
    static Locale sLocaleForHour;
    static SimpleDateFormat sSimpleDateFormat;
    static SimpleDateFormat sSimpleDateFormatForHour;
    static String sZoneId;
    static String sZoneIdForHour;
    private static final Map<String, BatteryHistEntry> EMPTY_BATTERY_MAP = new HashMap();
    private static final BatteryHistEntry EMPTY_BATTERY_HIST_ENTRY = new BatteryHistEntry(new ContentValues());
    static double PERCENTAGE_OF_TOTAL_THRESHOLD = 1.0d;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ConsumerType {
    }

    private ConvertUtils() {
    }

    private static double getDiffValue(double d, double d2, double d3) {
        return (d2 > d ? d2 - d : 0.0d) + (d3 > d2 ? d3 - d2 : 0.0d);
    }

    private static long getDiffValue(long j, long j2, long j3) {
        return (j2 > j ? j2 - j : 0L) + (j3 > j2 ? j3 - j2 : 0L);
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x00ee, code lost:
    
        if (r5 == 0.0d) goto L30;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.util.Map<java.lang.Integer, java.util.List<com.android.settings.fuelgauge.BatteryDiffEntry>> getIndexedUsageMap(android.content.Context r37, int r38, long[] r39, java.util.Map<java.lang.Long, java.util.Map<java.lang.String, com.android.settings.fuelgauge.BatteryHistEntry>> r40, boolean r41) {
        /*
            Method dump skipped, instructions count: 396
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.fuelgauge.ConvertUtils.getIndexedUsageMap(android.content.Context, int, long[], java.util.Map, boolean):java.util.Map");
    }

    static Locale getLocale(Context context) {
        if (context == null) {
            return Locale.getDefault();
        }
        LocaleList locales = context.getResources().getConfiguration().getLocales();
        return (locales == null || locales.isEmpty()) ? Locale.getDefault() : locales.get(0);
    }

    private static void insert24HoursData(int i, Map<Integer, List<BatteryDiffEntry>> map) {
        HashMap hashMap = new HashMap();
        Iterator<List<BatteryDiffEntry>> it = map.values().iterator();
        double d = 0.0d;
        while (it.hasNext()) {
            for (BatteryDiffEntry batteryDiffEntry : it.next()) {
                String key = batteryDiffEntry.mBatteryHistEntry.getKey();
                BatteryDiffEntry batteryDiffEntry2 = (BatteryDiffEntry) hashMap.get(key);
                if (batteryDiffEntry2 == null) {
                    hashMap.put(key, batteryDiffEntry.m148clone());
                } else {
                    batteryDiffEntry2.mForegroundUsageTimeInMs += batteryDiffEntry.mForegroundUsageTimeInMs;
                    batteryDiffEntry2.mBackgroundUsageTimeInMs += batteryDiffEntry.mBackgroundUsageTimeInMs;
                    batteryDiffEntry2.mConsumePower += batteryDiffEntry.mConsumePower;
                }
                d += batteryDiffEntry.mConsumePower;
            }
        }
        ArrayList arrayList = new ArrayList(hashMap.values());
        Iterator<BatteryDiffEntry> it2 = arrayList.iterator();
        while (it2.hasNext()) {
            it2.next().setTotalConsumePower(d);
        }
        map.put(Integer.valueOf(i), arrayList);
    }

    private static void purgeLowPercentageAndFakeData(Map<Integer, List<BatteryDiffEntry>> map) {
        Iterator<List<BatteryDiffEntry>> it = map.values().iterator();
        while (it.hasNext()) {
            Iterator<BatteryDiffEntry> it2 = it.next().iterator();
            while (it2.hasNext()) {
                BatteryDiffEntry next = it2.next();
                if (next.getPercentOfTotal() < PERCENTAGE_OF_TOTAL_THRESHOLD || "fake_package".equals(next.getPackageName())) {
                    it2.remove();
                }
            }
        }
    }

    private static BatteryHistEntry selectBatteryHistEntry(BatteryHistEntry batteryHistEntry, BatteryHistEntry batteryHistEntry2, BatteryHistEntry batteryHistEntry3) {
        if (batteryHistEntry == null || batteryHistEntry == EMPTY_BATTERY_HIST_ENTRY) {
            if (batteryHistEntry2 == null || batteryHistEntry2 == EMPTY_BATTERY_HIST_ENTRY) {
                if (batteryHistEntry3 == null || batteryHistEntry3 == EMPTY_BATTERY_HIST_ENTRY) {
                    return null;
                }
                return batteryHistEntry3;
            }
            return batteryHistEntry2;
        }
        return batteryHistEntry;
    }

    public static String utcToLocalTime(Context context, long j) {
        Locale locale = getLocale(context);
        String id = TimeZone.getDefault().getID();
        if (!id.equals(sZoneId) || !locale.equals(sLocale) || sSimpleDateFormat == null) {
            sLocale = locale;
            sZoneId = id;
            sSimpleDateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss", locale);
        }
        return sSimpleDateFormat.format(new Date(j));
    }

    public static String utcToLocalTimeHour(Context context, long j, boolean z) {
        Locale locale = getLocale(context);
        String id = TimeZone.getDefault().getID();
        if (!id.equals(sZoneIdForHour) || !locale.equals(sLocaleForHour) || sIs24HourFormat != z || sSimpleDateFormatForHour == null) {
            sLocaleForHour = locale;
            sZoneIdForHour = id;
            sIs24HourFormat = z;
            sSimpleDateFormatForHour = new SimpleDateFormat(sIs24HourFormat ? "HH" : "h", locale);
        }
        return sSimpleDateFormatForHour.format(new Date(j)).toLowerCase(locale);
    }
}
