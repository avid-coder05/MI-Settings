package com.android.settings.locale;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import com.android.settings.R;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import miui.os.Build;
import miui.os.MiuiInit;
import miuix.pinyin.utilities.ChinesePinyinConverter;

/* loaded from: classes.dex */
public class LocaleSettingsHelper {
    private static LocaleSettingsHelper sInstance;
    private HashMap<String, String> mChinese2Pinyin;
    private String mCurrentLocale;
    private List<LocaleInfo> mLocaleInfoItems = new ArrayList();
    private String[] mLocales = MiuiInit.getCustVariants();

    /* loaded from: classes.dex */
    public class AlphabetComparator implements Comparator<LocaleInfo> {
        private Collator mCollator = Collator.getInstance();
        private Context mContext;
        private boolean mIsChinese;

        public AlphabetComparator(Context context, boolean z) {
            this.mIsChinese = z;
            this.mContext = context;
        }

        @Override // java.util.Comparator
        public int compare(LocaleInfo localeInfo, LocaleInfo localeInfo2) {
            if (localeInfo == null && localeInfo2 == null) {
                return 0;
            }
            if (localeInfo == null) {
                return -1;
            }
            if (localeInfo2 == null) {
                return 1;
            }
            return this.mIsChinese ? LocaleSettingsHelper.chinese2PinYin(this.mContext, localeInfo.getDisplayName()).compareTo(LocaleSettingsHelper.chinese2PinYin(this.mContext, localeInfo2.getDisplayName())) : this.mCollator.compare(localeInfo.getDisplayName(), localeInfo2.getDisplayName());
        }
    }

    private LocaleSettingsHelper() {
    }

    private void buildChinese2PinYin(Context context) {
        this.mChinese2Pinyin = new HashMap<>();
        for (LocaleInfo localeInfo : this.mLocaleInfoItems) {
            this.mChinese2Pinyin.put(localeInfo.getDisplayName(), chinese2PinYin(context, localeInfo.getDisplayName()));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String chinese2PinYin(Context context, String str) {
        ArrayList<ChinesePinyinConverter.Token> arrayList = ChinesePinyinConverter.getInstance(context).get(str);
        StringBuilder sb = new StringBuilder();
        Iterator<ChinesePinyinConverter.Token> it = arrayList.iterator();
        while (it.hasNext()) {
            sb.append(it.next().target);
        }
        return sb.toString();
    }

    public static LocaleSettingsHelper getInstance() {
        if (sInstance == null) {
            sInstance = new LocaleSettingsHelper();
        }
        return sInstance;
    }

    private boolean isChineseLocale() {
        return Locale.CHINESE.getLanguage().equals(Locale.getDefault().getLanguage());
    }

    public void constructLocaleList(Context context) {
        this.mLocaleInfoItems.clear();
        this.mCurrentLocale = Locale.getDefault().toString();
        LocaleInfo localeInfo = null;
        for (int i = 0; i < this.mLocales.length; i++) {
            LocaleInfo localeInfo2 = new LocaleInfo(this.mLocales[i], new Locale(Locale.getDefault().getLanguage(), this.mLocales[i]).getDisplayCountry());
            if (localeInfo2.getCountryCode().equalsIgnoreCase(Build.getRegion())) {
                localeInfo = new LocaleInfo(this.mLocales[i], context.getResources().getString(R.string.country_name, localeInfo2.getDisplayName()));
            } else {
                this.mLocaleInfoItems.add(localeInfo2);
            }
        }
        Collections.sort(this.mLocaleInfoItems, new AlphabetComparator(context, isChineseLocale()));
        if (localeInfo != null) {
            this.mLocaleInfoItems.add(0, localeInfo);
        }
        if (isChineseLocale()) {
            buildChinese2PinYin(context);
        } else {
            this.mChinese2Pinyin = null;
        }
    }

    public String getCurrentLocale() {
        return this.mCurrentLocale;
    }

    public List<LocaleInfo> queryLocaleInfoItems(AsyncTask asyncTask, String str, boolean z) {
        HashMap<String, String> hashMap;
        if (TextUtils.isEmpty(str)) {
            return this.mLocaleInfoItems;
        }
        ArrayList arrayList = new ArrayList();
        String lowerCase = str.toLowerCase();
        for (LocaleInfo localeInfo : this.mLocaleInfoItems) {
            if (z && (asyncTask == null || asyncTask.isCancelled())) {
                return Collections.emptyList();
            }
            String displayName = localeInfo.getDisplayName();
            boolean contains = displayName.toLowerCase().contains(lowerCase);
            if (!contains && isChineseLocale() && (hashMap = this.mChinese2Pinyin) != null) {
                contains = hashMap.get(displayName).toLowerCase().contains(lowerCase);
            }
            if (contains) {
                arrayList.add(localeInfo);
            }
        }
        return arrayList;
    }

    public void setLocales(String[] strArr) {
        this.mLocales = strArr;
    }
}
