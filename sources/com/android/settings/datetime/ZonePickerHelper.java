package com.android.settings.datetime;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.PreferenceManager;
import com.android.settings.R;
import com.android.settingslib.datetime.ZoneGetter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import miui.provider.ExtraContacts;
import miuix.pinyin.utilities.ChinesePinyinConverter;

/* loaded from: classes.dex */
public class ZonePickerHelper {
    private HashMap<String, String> mChinese2Pinyin;
    private TimeZoneObj mSelectedZoneInfo;
    private SharedPreferences mSharedPreferences;
    private int mSortMode;
    private List<TimeZoneObj> mTimezoneItems;

    /* loaded from: classes.dex */
    public class AlphabetComparator implements Comparator<TimeZoneObj> {
        private final Collator mCollator = Collator.getInstance();

        public AlphabetComparator() {
        }

        @Override // java.util.Comparator
        public int compare(TimeZoneObj timeZoneObj, TimeZoneObj timeZoneObj2) {
            if (timeZoneObj == null && timeZoneObj2 == null) {
                return 0;
            }
            if (timeZoneObj == null) {
                return -1;
            }
            if (timeZoneObj2 == null) {
                return 1;
            }
            return this.mCollator.compare(timeZoneObj.getCityName(), timeZoneObj2.getCityName());
        }
    }

    /* loaded from: classes.dex */
    public class TimezoneComparator implements Comparator<TimeZoneObj> {
        private AlphabetComparator mNameComparator = null;

        public TimezoneComparator() {
        }

        private Comparator getAlphabetComparator() {
            if (this.mNameComparator == null) {
                this.mNameComparator = new AlphabetComparator();
            }
            return this.mNameComparator;
        }

        @Override // java.util.Comparator
        public int compare(TimeZoneObj timeZoneObj, TimeZoneObj timeZoneObj2) {
            if (timeZoneObj == null && timeZoneObj2 == null) {
                return 0;
            }
            if (timeZoneObj == null) {
                return -1;
            }
            if (timeZoneObj2 == null) {
                return 1;
            }
            int offset = timeZoneObj.getOffset();
            int offset2 = timeZoneObj2.getOffset();
            if (offset < offset2) {
                return -1;
            }
            if (offset > offset2) {
                return 1;
            }
            return getAlphabetComparator().compare(timeZoneObj, timeZoneObj2);
        }
    }

    public ZonePickerHelper(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.mSharedPreferences = defaultSharedPreferences;
        this.mSortMode = defaultSharedPreferences.getInt("timezone_sort_mode", 0);
        constructTimeZoneList(context);
    }

    private void buildChinese2PinYin(Context context) {
        this.mChinese2Pinyin = new HashMap<>();
        for (TimeZoneObj timeZoneObj : this.mTimezoneItems) {
            ArrayList<ChinesePinyinConverter.Token> arrayList = ChinesePinyinConverter.getInstance(context).get(timeZoneObj.getCityName());
            StringBuilder sb = new StringBuilder();
            Iterator<ChinesePinyinConverter.Token> it = arrayList.iterator();
            while (it.hasNext()) {
                sb.append(it.next().target);
            }
            this.mChinese2Pinyin.put(timeZoneObj.getCityName(), sb.toString());
        }
    }

    private boolean isChineseLocale() {
        Locale locale = Locale.getDefault();
        return locale.equals(Locale.CHINESE) || locale.equals(Locale.SIMPLIFIED_CHINESE) || locale.equals(Locale.TRADITIONAL_CHINESE);
    }

    private void sort(int i) {
        Comparator timezoneComparator = i == 1 ? new TimezoneComparator() : new AlphabetComparator();
        if (this.mSelectedZoneInfo == null) {
            Collections.sort(this.mTimezoneItems, timezoneComparator);
            Log.w("ZonePickerHelper", "Cannot find current timezone info");
            return;
        }
        this.mTimezoneItems.remove(0);
        Collections.sort(this.mTimezoneItems, timezoneComparator);
        this.mTimezoneItems.add(0, this.mSelectedZoneInfo);
    }

    public void constructTimeZoneList(Context context) {
        this.mTimezoneItems = new ArrayList();
        TimeZone timeZone = TimeZone.getDefault();
        List<Map<String, Object>> zonesList = ZoneGetter.getZonesList(context);
        for (int i = 0; i < zonesList.size(); i++) {
            Map<String, Object> map = zonesList.get(i);
            String str = (String) map.get("name");
            String str2 = (String) map.get("gmt");
            String str3 = (String) map.get("id");
            int intValue = ((Integer) map.get(ExtraContacts.ConferenceCalls.OFFSET_PARAM_KEY)).intValue();
            if (str3.equals(timeZone.getID())) {
                this.mSelectedZoneInfo = new TimeZoneObj(str + context.getResources().getString(R.string.current_zone), str2, str3, intValue);
            } else {
                this.mTimezoneItems.add(new TimeZoneObj(str, str2, str3, intValue));
            }
        }
        sort(this.mSortMode);
        if (isChineseLocale()) {
            buildChinese2PinYin(context);
        } else {
            this.mChinese2Pinyin = null;
        }
    }

    public int getSortMode() {
        return this.mSortMode;
    }

    public List<TimeZoneObj> queryTimezoneItems(String str) {
        if (TextUtils.isEmpty(str)) {
            return new ArrayList(this.mTimezoneItems);
        }
        ArrayList arrayList = new ArrayList();
        String lowerCase = str.toLowerCase();
        for (TimeZoneObj timeZoneObj : this.mTimezoneItems) {
            String lowerCase2 = timeZoneObj.getCityName().toLowerCase();
            boolean contains = lowerCase2.contains(lowerCase);
            if (!contains && isChineseLocale()) {
                contains = this.mChinese2Pinyin.get(lowerCase2).contains(str);
            }
            if (contains) {
                arrayList.add(timeZoneObj);
            }
        }
        return arrayList;
    }

    public void setSortMode(int i) {
        this.mSortMode = i;
        sort(i);
        this.mSharedPreferences.edit().putInt("timezone_sort_mode", this.mSortMode).apply();
    }
}
