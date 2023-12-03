package miuix.pickerwidget.date;

import android.content.Context;
import android.content.res.Resources;
import java.util.Locale;
import miuix.core.util.SoftReferenceSingleton;
import miuix.pickerwidget.R$array;

/* loaded from: classes5.dex */
public class CalendarFormatSymbols {
    private static SoftReferenceSingleton<CalendarFormatSymbols> INSTANCE;
    private Resources mResources;

    private CalendarFormatSymbols(Context context) {
        this.mResources = context.getResources();
    }

    public static CalendarFormatSymbols getOrCreate(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SoftReferenceSingleton<CalendarFormatSymbols>() { // from class: miuix.pickerwidget.date.CalendarFormatSymbols.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // miuix.core.util.SoftReferenceSingleton
                public CalendarFormatSymbols createInstance(Object obj) {
                    return new CalendarFormatSymbols((Context) obj);
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // miuix.core.util.SoftReferenceSingleton
                public void updateInstance(CalendarFormatSymbols calendarFormatSymbols, Object obj) {
                    super.updateInstance((AnonymousClass1) calendarFormatSymbols, obj);
                    calendarFormatSymbols.updateResource((Context) obj);
                }
            };
        }
        return INSTANCE.get(context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateResource(Context context) {
        this.mResources = context.getResources();
    }

    public String[] getAmPms() {
        return this.mResources.getStringArray(R$array.am_pms);
    }

    public String[] getChineseDays() {
        return this.mResources.getStringArray(R$array.chinese_days);
    }

    public String[] getChineseDigits() {
        return this.mResources.getStringArray(R$array.chinese_digits);
    }

    public String[] getChineseLeapMonths() {
        return this.mResources.getStringArray(R$array.chinese_leap_months);
    }

    public String[] getChineseMonths() {
        return this.mResources.getStringArray(R$array.chinese_months);
    }

    public String[] getChineseSymbolAnimals() {
        return this.mResources.getStringArray(R$array.chinese_symbol_animals);
    }

    public String[] getDetailedAmPms() {
        return this.mResources.getStringArray(R$array.detailed_am_pms);
    }

    public String[] getEarthlyBranches() {
        return this.mResources.getStringArray(R$array.earthly_branches);
    }

    public String[] getEras() {
        return this.mResources.getStringArray(R$array.eras);
    }

    public String[] getHeavenlyStems() {
        return this.mResources.getStringArray(R$array.heavenly_stems);
    }

    public Locale getLocale() {
        return Locale.getDefault();
    }

    public String[] getMonths() {
        return this.mResources.getStringArray(R$array.months);
    }

    public String[] getShortMonths() {
        return this.mResources.getStringArray(R$array.months_short);
    }

    public String[] getShortWeekDays() {
        return this.mResources.getStringArray(R$array.week_days_short);
    }

    public String[] getShortestMonths() {
        return this.mResources.getStringArray(R$array.months_shortest);
    }

    public String[] getShortestWeekDays() {
        return this.mResources.getStringArray(R$array.week_days_shortest);
    }

    public String[] getSolarTerms() {
        return this.mResources.getStringArray(R$array.solar_terms);
    }

    public String[] getWeekDays() {
        return this.mResources.getStringArray(R$array.week_days);
    }
}
