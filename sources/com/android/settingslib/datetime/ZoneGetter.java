package com.android.settingslib.datetime;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.icu.text.TimeZoneFormat;
import android.icu.text.TimeZoneNames;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TtsSpan;
import android.util.Log;
import androidx.core.text.BidiFormatter;
import androidx.core.text.TextDirectionHeuristicsCompat;
import com.android.i18n.timezone.CountryTimeZones;
import com.android.i18n.timezone.TimeZoneFinder;
import com.android.settingslib.R$xml;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import miui.provider.ExtraContacts;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes2.dex */
public class ZoneGetter {

    /* loaded from: classes2.dex */
    public static final class ZoneGetterData {
        public final CharSequence[] gmtOffsetTexts;
        public final Set<String> localZoneIds;
        public final String[] olsonIdsToDisplay;
        public final TimeZone[] timeZones;
        public final int zoneCount;

        public ZoneGetterData(Context context) {
            Locale locale = context.getResources().getConfiguration().locale;
            TimeZoneFormat timeZoneFormat = TimeZoneFormat.getInstance(locale);
            Date date = new Date();
            List readTimezonesToDisplay = ZoneGetter.readTimezonesToDisplay(context);
            int size = readTimezonesToDisplay.size();
            this.zoneCount = size;
            this.olsonIdsToDisplay = new String[size];
            this.timeZones = new TimeZone[size];
            this.gmtOffsetTexts = new CharSequence[size];
            for (int i = 0; i < this.zoneCount; i++) {
                String str = (String) readTimezonesToDisplay.get(i);
                this.olsonIdsToDisplay[i] = str;
                TimeZone timeZone = TimeZone.getTimeZone(str);
                this.timeZones[i] = timeZone;
                this.gmtOffsetTexts[i] = ZoneGetter.getGmtOffsetText(timeZoneFormat, locale, timeZone, date);
            }
            List<String> lookupTimeZoneIdsByCountry = lookupTimeZoneIdsByCountry(locale.getCountry());
            this.localZoneIds = lookupTimeZoneIdsByCountry != null ? new HashSet(lookupTimeZoneIdsByCountry) : new HashSet();
        }

        private static List<String> extractTimeZoneIds(List<CountryTimeZones.TimeZoneMapping> list) {
            ArrayList arrayList = new ArrayList(list.size());
            Iterator<CountryTimeZones.TimeZoneMapping> it = list.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next().getTimeZoneId());
            }
            return Collections.unmodifiableList(arrayList);
        }

        public List<String> lookupTimeZoneIdsByCountry(String str) {
            CountryTimeZones lookupCountryTimeZones = TimeZoneFinder.getInstance().lookupCountryTimeZones(str);
            if (lookupCountryTimeZones == null) {
                return null;
            }
            return extractTimeZoneIds(lookupCountryTimeZones.getTimeZoneMappings());
        }
    }

    private static void appendWithTtsSpan(SpannableStringBuilder spannableStringBuilder, CharSequence charSequence, TtsSpan ttsSpan) {
        int length = spannableStringBuilder.length();
        spannableStringBuilder.append(charSequence);
        spannableStringBuilder.setSpan(ttsSpan, length, spannableStringBuilder.length(), 0);
    }

    private static Map<String, Object> createDisplayEntry(TimeZone timeZone, CharSequence charSequence, CharSequence charSequence2, int i) {
        HashMap hashMap = new HashMap();
        hashMap.put("id", timeZone.getID());
        hashMap.put("name", charSequence2.toString());
        hashMap.put("display_label", charSequence2);
        hashMap.put("gmt", charSequence.toString());
        hashMap.put("offset_label", charSequence);
        hashMap.put(ExtraContacts.ConferenceCalls.OFFSET_PARAM_KEY, Integer.valueOf(i));
        return hashMap;
    }

    private static String formatDigits(int i, int i2, String str) {
        int i3 = i / 10;
        int i4 = i % 10;
        StringBuilder sb = new StringBuilder(i2);
        if (i >= 10 || i2 == 2) {
            sb.append(str.charAt(i3));
        }
        sb.append(str.charAt(i4));
        return sb.toString();
    }

    public static CharSequence getGmtOffsetText(TimeZoneFormat timeZoneFormat, Locale locale, TimeZone timeZone, Date date) {
        String substring;
        String str;
        TimeZoneFormat.GMTOffsetPatternType gMTOffsetPatternType;
        int i;
        String str2;
        int i2;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        String gMTPattern = timeZoneFormat.getGMTPattern();
        int indexOf = gMTPattern.indexOf("{0}");
        if (indexOf == -1) {
            str = "GMT";
            substring = "";
        } else {
            String substring2 = gMTPattern.substring(0, indexOf);
            substring = gMTPattern.substring(indexOf + 3);
            str = substring2;
        }
        if (!str.isEmpty()) {
            appendWithTtsSpan(spannableStringBuilder, str, new TtsSpan.TextBuilder(str).build());
        }
        int offset = timeZone.getOffset(date.getTime());
        if (offset < 0) {
            offset = -offset;
            gMTOffsetPatternType = TimeZoneFormat.GMTOffsetPatternType.NEGATIVE_HM;
        } else {
            gMTOffsetPatternType = TimeZoneFormat.GMTOffsetPatternType.POSITIVE_HM;
        }
        String gMTOffsetPattern = timeZoneFormat.getGMTOffsetPattern(gMTOffsetPatternType);
        String gMTOffsetDigits = timeZoneFormat.getGMTOffsetDigits();
        long j = offset;
        int i3 = (int) (j / 3600000);
        int abs = Math.abs((int) (j / 60000)) % 60;
        int i4 = 0;
        while (i4 < gMTOffsetPattern.length()) {
            char charAt = gMTOffsetPattern.charAt(i4);
            if (charAt == '+' || charAt == '-' || charAt == 8722) {
                String valueOf = String.valueOf(charAt);
                appendWithTtsSpan(spannableStringBuilder, valueOf, new TtsSpan.VerbatimBuilder(valueOf).build());
            } else if (charAt == 'H' || charAt == 'm') {
                int i5 = i4 + 1;
                if (i5 >= gMTOffsetPattern.length() || gMTOffsetPattern.charAt(i5) != charAt) {
                    i5 = i4;
                    i = 1;
                } else {
                    i = 2;
                }
                if (charAt == 'H') {
                    str2 = "hour";
                    i2 = i3;
                } else {
                    str2 = "minute";
                    i2 = abs;
                }
                appendWithTtsSpan(spannableStringBuilder, formatDigits(i2, i, gMTOffsetDigits), new TtsSpan.MeasureBuilder().setNumber(i2).setUnit(str2).build());
                i4 = i5;
            } else {
                spannableStringBuilder.append(charAt);
            }
            i4++;
        }
        if (!substring.isEmpty()) {
            appendWithTtsSpan(spannableStringBuilder, substring, new TtsSpan.TextBuilder(substring).build());
        }
        return BidiFormatter.getInstance().unicodeWrap(new SpannableString(spannableStringBuilder), TextUtils.getLayoutDirectionFromLocale(locale) == 1 ? TextDirectionHeuristicsCompat.RTL : TextDirectionHeuristicsCompat.LTR);
    }

    private static CharSequence getTimeZoneDisplayName(ZoneGetterData zoneGetterData, TimeZoneNames timeZoneNames, boolean z, TimeZone timeZone, String str) {
        Date date = new Date();
        if (zoneGetterData.localZoneIds.contains(str) && !z) {
            return getZoneLongName(timeZoneNames, timeZone, date);
        }
        String canonicalID = android.icu.util.TimeZone.getCanonicalID(timeZone.getID());
        if (canonicalID == null) {
            canonicalID = timeZone.getID();
        }
        String exemplarLocationName = timeZoneNames.getExemplarLocationName(canonicalID);
        return (exemplarLocationName == null || exemplarLocationName.isEmpty()) ? getZoneLongName(timeZoneNames, timeZone, date) : exemplarLocationName;
    }

    public static CharSequence getTimeZoneOffsetAndName(Context context, TimeZone timeZone, Date date) {
        Locale locale = context.getResources().getConfiguration().locale;
        CharSequence gmtOffsetText = getGmtOffsetText(TimeZoneFormat.getInstance(locale), locale, timeZone, date);
        String zoneLongName = getZoneLongName(TimeZoneNames.getInstance(locale), timeZone, date);
        return zoneLongName == null ? gmtOffsetText : TextUtils.concat(gmtOffsetText, " ", zoneLongName);
    }

    public static String getZoneLongName(TimeZoneNames timeZoneNames, TimeZone timeZone, Date date) {
        String displayName = timeZoneNames.getDisplayName(timeZone.getID(), timeZone.inDaylightTime(date) ? TimeZoneNames.NameType.LONG_DAYLIGHT : TimeZoneNames.NameType.LONG_STANDARD, date.getTime());
        if (TextUtils.isEmpty(displayName)) {
            String canonicalID = android.icu.util.TimeZone.getCanonicalID(timeZone.getID());
            if (!TextUtils.isEmpty(canonicalID)) {
                displayName = TimeZoneNames.getInstance(Locale.getDefault()).getExemplarLocationName(canonicalID);
            }
        }
        return TextUtils.isEmpty(displayName) ? timeZone.getDisplayName() : displayName;
    }

    public static List<Map<String, Object>> getZonesList(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        Date date = new Date();
        TimeZoneNames timeZoneNames = TimeZoneNames.getInstance(locale);
        ZoneGetterData zoneGetterData = new ZoneGetterData(context);
        boolean shouldUseExemplarLocationForLocalNames = shouldUseExemplarLocationForLocalNames(zoneGetterData, timeZoneNames);
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < zoneGetterData.zoneCount; i++) {
            TimeZone timeZone = zoneGetterData.timeZones[i];
            CharSequence charSequence = zoneGetterData.gmtOffsetTexts[i];
            CharSequence timeZoneDisplayName = getTimeZoneDisplayName(zoneGetterData, timeZoneNames, shouldUseExemplarLocationForLocalNames, timeZone, zoneGetterData.olsonIdsToDisplay[i]);
            if (TextUtils.isEmpty(timeZoneDisplayName)) {
                timeZoneDisplayName = charSequence;
            }
            arrayList.add(createDisplayEntry(timeZone, charSequence, timeZoneDisplayName, timeZone.getOffset(date.getTime())));
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<String> readTimezonesToDisplay(Context context) {
        ArrayList arrayList = new ArrayList();
        try {
            XmlResourceParser xml = context.getResources().getXml(R$xml.timezones);
            do {
                try {
                } catch (Throwable th) {
                    if (xml != null) {
                        try {
                            xml.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } while (xml.next() != 2);
            xml.next();
            while (xml.getEventType() != 3) {
                while (xml.getEventType() != 2) {
                    if (xml.getEventType() == 1) {
                        xml.close();
                        return arrayList;
                    }
                    xml.next();
                }
                if (xml.getName().equals("timezone")) {
                    arrayList.add(xml.getAttributeValue(0));
                }
                while (xml.getEventType() != 3) {
                    xml.next();
                }
                xml.next();
            }
            xml.close();
        } catch (IOException unused) {
            Log.e("ZoneGetter", "Unable to read timezones.xml file");
        } catch (XmlPullParserException unused2) {
            Log.e("ZoneGetter", "Ill-formatted timezones.xml file");
        }
        return arrayList;
    }

    private static boolean shouldUseExemplarLocationForLocalNames(ZoneGetterData zoneGetterData, TimeZoneNames timeZoneNames) {
        HashSet hashSet = new HashSet();
        Date date = new Date();
        for (int i = 0; i < zoneGetterData.zoneCount; i++) {
            if (zoneGetterData.localZoneIds.contains(zoneGetterData.olsonIdsToDisplay[i])) {
                CharSequence zoneLongName = getZoneLongName(timeZoneNames, zoneGetterData.timeZones[i], date);
                if (zoneLongName == null) {
                    zoneLongName = zoneGetterData.gmtOffsetTexts[i];
                }
                if (!hashSet.add(zoneLongName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
