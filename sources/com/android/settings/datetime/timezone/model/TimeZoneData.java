package com.android.settings.datetime.timezone.model;

import androidx.collection.ArraySet;
import com.android.i18n.timezone.CountryTimeZones;
import com.android.i18n.timezone.CountryZonesFinder;
import com.android.i18n.timezone.TimeZoneFinder;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/* loaded from: classes.dex */
public class TimeZoneData {
    private static WeakReference<TimeZoneData> sCache;
    private final CountryZonesFinder mCountryZonesFinder;
    private final Set<String> mRegionIds;

    public TimeZoneData(CountryZonesFinder countryZonesFinder) {
        this.mCountryZonesFinder = countryZonesFinder;
        this.mRegionIds = getNormalizedRegionIds(countryZonesFinder.lookupAllCountryIsoCodes());
    }

    public static synchronized TimeZoneData getInstance() {
        synchronized (TimeZoneData.class) {
            WeakReference<TimeZoneData> weakReference = sCache;
            TimeZoneData timeZoneData = weakReference == null ? null : weakReference.get();
            if (timeZoneData != null) {
                return timeZoneData;
            }
            TimeZoneData timeZoneData2 = new TimeZoneData(TimeZoneFinder.getInstance().getCountryZonesFinder());
            sCache = new WeakReference<>(timeZoneData2);
            return timeZoneData2;
        }
    }

    private static Set<String> getNormalizedRegionIds(List<String> list) {
        HashSet hashSet = new HashSet(list.size());
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            hashSet.add(normalizeRegionId(it.next()));
        }
        return Collections.unmodifiableSet(hashSet);
    }

    public static String normalizeRegionId(String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase(Locale.US);
    }

    public Set<String> getRegionIds() {
        return this.mRegionIds;
    }

    public Set<String> lookupCountryCodesForZoneId(String str) {
        if (str == null) {
            return Collections.emptySet();
        }
        List lookupCountryTimeZonesForZoneId = this.mCountryZonesFinder.lookupCountryTimeZonesForZoneId(str);
        ArraySet arraySet = new ArraySet();
        Iterator it = lookupCountryTimeZonesForZoneId.iterator();
        while (it.hasNext()) {
            FilteredCountryTimeZones filteredCountryTimeZones = new FilteredCountryTimeZones((CountryTimeZones) it.next());
            if (filteredCountryTimeZones.matches(str)) {
                arraySet.add(filteredCountryTimeZones.getRegionId());
            }
        }
        return arraySet;
    }

    public FilteredCountryTimeZones lookupCountryTimeZones(String str) {
        CountryTimeZones lookupCountryTimeZones = str == null ? null : this.mCountryZonesFinder.lookupCountryTimeZones(str);
        if (lookupCountryTimeZones == null) {
            return null;
        }
        return new FilteredCountryTimeZones(lookupCountryTimeZones);
    }
}
