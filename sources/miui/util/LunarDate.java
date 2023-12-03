package miui.util;

import android.content.res.Resources;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.MiuiWindowManager$LayoutParams;
import com.android.settings.recommend.PageIndexManager;
import com.android.settings.search.SearchFeatureProvider;
import com.android.settings.search.SearchUpdater;
import com.miui.system.internal.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import miui.os.Build;

/* loaded from: classes4.dex */
public class LunarDate {
    public static final int MAX_LUNAR_YEAR = 2050;
    public static final int MIN_LUNAR_YEAR = 1900;
    private static final long[] luYearData = {19416, 19168, 42352, 21717, 53856, 55632, 91476, 22176, 39632, 21970, 19168, 42422, 42192, 53840, 119381, 46400, 54944, 44450, 38320, 84343, 18800, 42160, 46261, 27216, 27968, 109396, 11104, 38256, 21234, 18800, 25958, 54432, 59984, 28309, 23248, 11104, 100067, 37600, 116951, 51536, 54432, 120998, 46416, 22176, 107956, 9680, 37584, 53938, 43344, 46423, 27808, 46416, 86869, 19872, 42448, 83315, 21200, 43432, 59728, 27296, 44710, 43856, 19296, 43748, 42352, 21088, 62051, 55632, 23383, 22176, 38608, 19925, 19152, 42192, 54484, 53840, 54616, 46400, 46752, 103846, 38320, 18864, 43380, 42160, 45690, 27216, 27968, 44870, 43872, 38256, 19189, 18800, 25776, 29859, 59984, 27480, 23232, 43872, 38613, 37600, 51552, 55636, 54432, 55888, 30034, 22176, 43959, 9680, 37584, 51893, 43344, 46240, 47780, 44368, 21977, 19360, 42416, 86390, 21168, 43312, 31060, 27296, 44368, 23378, 19296, 42726, 42208, 53856, 60005, 54576, 23200, 30371, 38608, 19415, 19152, 42192, 118966, 53840, 54560, 56645, 46496, 22224, 21938, 18864, 42359, 42160, 43600, 111189, 27936, 44448};
    private static final char[] iSolarLunarOffsetTable = {'1', '&', 28, '.', '\"', 24, '+', ' ', 21, '(', 29, '0', '$', 25, ',', '!', 22, ')', 31, '2', '&', 27, '.', '#', 23, '+', ' ', 22, '(', 29, '/', '$', 25, ',', '\"', 23, ')', 30, '1', '&', 26, '-', '#', 24, '+', ' ', 21, '(', 28, '/', '$', 26, ',', '!', 23, '*', 30, '0', '&', 27, '-', '#', 24, '+', ' ', 20, '\'', 29, '/', '$', 26, '-', '!', 22, ')', 30, '0', '%', 27, '.', '#', 24, '+', ' ', '2', '\'', 28, '/', '$', 26, '-', '\"', 22, '(', 30, '1', '%', 27, '.', '#', 23, '*', 31, 21, '\'', 28, '0', '%', 25, ',', '!', 22, '(', 30, '1', '&', 27, '.', '#', 24, '*', 31, 21, '(', 28, '/', '$', 25, '+', '!', 22, ')', 30, '1', '&', 27, '-', '\"', 23, '*', 31, 21, '(', 29, '/', '$', 25, ',', ' ', 22};
    private static int[] lunarHolidaysTable = {101, 115, 505, 707, 815, 909, 1208};
    private static int[] solarHolidaysTable = {101, 214, 308, 312, 401, SearchFeatureProvider.REQUEST_CODE, 504, 601, 701, 801, 910, 1001, 1225};
    private static int[] solarHolidaysTable_TW = {101, 214, 228, 308, 312, 314, 329, 404, SearchFeatureProvider.REQUEST_CODE, 715, 903, 928, PageIndexManager.PAGE_ACCESSIBILITY_PHYSICAL, MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE, 1025, 1112, 1225};
    private static int[] solarHolidaysTable_HK = {101, 214, SearchFeatureProvider.REQUEST_CODE, 701, 1001, 1225};
    private static int[] lunarHolidays = {R.string.the_spring_festival, R.string.lantern_festival, R.string.the_dragon_boat_festival, R.string.double_seventh_day, R.string.the_mid_autumn_festival, R.string.the_double_ninth_festival, R.string.the_laba_rice_porridge_festival};
    private static int[] solarHolidays = {R.string.new_years_day, R.string.valentines_day, R.string.international_womens_day, R.string.arbor_day, R.string.fools_day, R.string.labour_day, R.string.chinese_youth_day, R.string.childrens_day, R.string.partys_day, R.string.the_armys_day, R.string.teachers_day, R.string.national_day, R.string.christmas_day};
    private static int[] solarHolidays_TW = {R.string.new_years_day, R.string.valentines_day, R.string.peace_day, R.string.international_womens_day, R.string.arbor_day, R.string.anti_aggression_day, R.string.tw_youth_day, R.string.tw_childrens_day, R.string.labour_day, R.string.anniversary_of_lifting_martial_law, R.string.armed_forces_day, R.string.teachers_day, R.string.national_day, R.string.united_nations_day, R.string.retrocession_day, R.string.national_father_day, R.string.christmas_day};
    private static int[] solarHolidays_HK = {R.string.new_years_day, R.string.valentines_day, R.string.labour_day, R.string.hksar_establishment_day, R.string.national_day, R.string.christmas_day};
    private static int[] solarTerms = {R.string.slight_cold, R.string.great_cold, R.string.spring_begins, R.string.the_rains, R.string.insects_awaken, R.string.vernal_equinox, R.string.clear_and_bright, R.string.grain_rain, R.string.summer_begins, R.string.grain_buds, R.string.grain_in_ear, R.string.summer_solstice, R.string.slight_heat, R.string.great_heat, R.string.autumn_begins, R.string.stopping_the_heat, R.string.white_dews, R.string.autumn_equinox, R.string.cold_dews, R.string.hoar_frost_falls, R.string.winter_begins, R.string.light_snow, R.string.heavy_snow, R.string.winter_solstice};
    private static char[] solarTermsTable = {150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 150, 150, 151, 135, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 135, 150, 135, 135, 'y', 'i', 'i', 'i', 'x', 'x', 134, 165, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 150, 150, 151, 151, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 135, 150, 135, 135, 'y', 'i', 'i', 'i', 'x', 'x', 134, 165, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 150, 150, 151, 151, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 135, 150, 135, 135, 'y', 'i', 'i', 'i', 'x', 'x', 134, 165, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 149, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 180, 150, 166, 151, 151, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 151, 150, 151, 135, 'y', 'y', 'i', 'i', 'x', 'x', 150, 165, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 150, 166, 150, 151, 'x', 'y', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'y', 'y', 'y', 'i', 'x', 'w', 150, 165, 151, 150, 151, 135, 'y', 'y', 'i', 'i', 'x', 'x', 150, 165, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 150, 165, 150, 151, 'x', 'y', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'y', 'y', 'y', 'i', 'x', 'w', 150, 164, 150, 150, 151, 135, 'y', 'y', 'i', 'i', 'x', 'x', 150, 165, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 150, 165, 150, 151, 'x', 'y', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 150, 150, 151, 135, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 150, 150, 151, 135, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 150, 150, 151, 151, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 150, 150, 151, 151, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 150, 150, 151, 151, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 150, 165, 166, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 150, 166, 151, 151, 'x', 'y', 'x', 'i', 'x', 'w', 150, 180, 150, 166, 151, 151, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 166, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 165, 180, 150, 165, 150, 151, 136, 'y', 'x', 'y', 'w', 135, 149, 180, 150, 165, 150, 151, 'x', 'y', 'x', 'i', 'x', 'w', 150, 180, 150, 166, 151, 151, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 166, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 165, 180, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 150, 165, 150, 151, 'x', 'y', 'x', 'h', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 165, 165, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 165, 180, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 165, 165, 166, 150, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 150, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 165, 165, 166, 150, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 165, 165, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 165, 165, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 181, 150, 165, 166, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'x', 'i', 'x', 'w', 150, 164, 165, 181, 166, 166, 136, 137, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 136, 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'x', 135, 150, 180, 150, 166, 150, 151, 'x', 'y', 'x', 'i', 'x', 'w', 150, 164, 165, 181, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 166, 150, 136, 136, 'x', 'x', 'w', 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 150, 165, 150, 151, 'x', 'y', 'x', 'i', 'x', 'w', 150, 180, 165, 181, 
    166, 166, 135, 136, 136, 'x', 135, 135, 165, 180, 166, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 165, 180, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 150, 165, 150, 151, 136, 'y', 'x', 'i', 'x', 135, 150, 180, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 134, 165, 180, 165, 165, 166, 150, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 150, 165, 134, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 134, 165, 179, 165, 165, 166, 150, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'v', 'x', 'i', 'x', 135, 150, 180, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 134, 165, 179, 165, 165, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 134, 165, 179, 165, 165, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 134, 165, 179, 165, 165, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 165, 181, 166, 166, 135, 136, 135, 'x', 135, 134, 165, 179, 165, 181, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'x', 135, 150, 180, 165, 181, 165, 166, 135, 136, 135, 'x', 135, 134, 165, 179, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 165, 180, 165, 166, 135, 136, 135, 'x', 135, 134, 165, 195, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 135, 165, 180, 166, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 165, 180, 165, 166, 151, 135, 135, 'x', 135, 134, 165, 195, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 134, 165, 180, 165, 165, 166, 150, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 165, 180, 165, 166, 151, 135, 135, 'x', 135, 150, 165, 195, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 134, 165, 179, 165, 165, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 165, 180, 165, 166, 151, 135, 135, 'x', 135, 150, 165, 195, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 134, 165, 179, 165, 165, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 165, 180, 165, 166, 151, 135, 135, 'x', 135, 150, 165, 195, 165, 181, 166, 166, 136, 136, 136, 'x', 135, 134, 165, 179, 165, 165, 166, 166, 136, 'x', 136, 'x', 135, 135, 165, 180, 150, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 149, 180, 165, 180, 165, 166, 151, 135, 135, 'x', 135, 150, 165, 195, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 134, 165, 179, 165, 165, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 149, 180, 165, 180, 165, 166, 151, 135, 135, 'x', 135, 150, 165, 195, 165, 181, 165, 166, 135, 136, 135, 'x', 135, 134, 165, 179, 165, 181, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 149, 180, 165, 180, 165, 166, 151, 135, 135, 136, 135, 150, 165, 195, 165, 180, 165, 166, 135, 136, 135, 'x', 135, 134, 165, 179, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 149, 180, 165, 180, 165, 165, 151, 135, 135, 136, 134, 150, 164, 195, 165, 165, 165, 166, 151, 135, 135, 'x', 135, 134, 165, 195, 165, 181, 166, 166, 135, 136, 'x', 'x', 135, 135};
    private static final HashMap<Integer, Integer> sEasterCache = new HashMap<>();

    /* loaded from: classes4.dex */
    public static class BirthHoroscope {
        private static final String BASE_DATE_STRING = "1900-1-31";
        private static final long DAY_IN_MILLS = 86400000;
        private static Date sBaseDate;
        private static final SimpleDateFormat sChineseDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        private static String[] sDiZhi;
        private static boolean sIsInitialized;
        private static String[] sJiaZi;
        private static String[] sTianGan;
        private Calendar mCalendar;
        private int mDay;
        private int mHour;
        private int mMonth;
        private int mYear;

        public BirthHoroscope(Calendar calendar, int i, int i2, int i3, int i4) {
            this.mCalendar = calendar;
            this.mYear = i;
            this.mMonth = i2;
            this.mDay = i3;
            this.mHour = i4;
        }

        private static int getLunarHourIndex(int i) {
            return ((i + 1) % 24) / 2;
        }

        public static BirthHoroscope newInstance(Resources resources, Calendar calendar) {
            try {
                if (!sIsInitialized) {
                    sTianGan = resources.getStringArray(R.array.tian_gan);
                    sDiZhi = resources.getStringArray(R.array.di_zhi);
                    sJiaZi = resources.getStringArray(R.array.jia_zi);
                    sBaseDate = sChineseDateFormat.parse(BASE_DATE_STRING);
                    sIsInitialized = true;
                }
                int time = (int) ((calendar.getTime().getTime() - sBaseDate.getTime()) / DAY_IN_MILLS);
                int i = LunarDate.MIN_LUNAR_YEAR;
                int i2 = 0;
                while (i < 2050 && time > 0) {
                    i2 = LunarDate.yrDays(i);
                    time -= i2;
                    i++;
                }
                if (time < 0) {
                    time += i2;
                    i--;
                }
                int i3 = i;
                int rMonth = LunarDate.rMonth(i3);
                int i4 = 1;
                boolean z = false;
                int i5 = 0;
                while (i4 < 13 && time > 0) {
                    if (rMonth <= 0 || i4 != rMonth + 1 || z) {
                        i5 = LunarDate.mthDays(i3, i4);
                    } else {
                        i4--;
                        i5 = LunarDate.rMthDays(i3);
                        z = true;
                    }
                    time -= i5;
                    if (z && i4 == rMonth + 1) {
                        z = false;
                    }
                    i4++;
                }
                if (time == 0 && rMonth > 0 && i4 == rMonth + 1 && !z) {
                    i4--;
                }
                if (time < 0) {
                    time += i5;
                    i4--;
                }
                return new BirthHoroscope(calendar, i3, i4, time + 1, calendar.get(11));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public String getBirthHoroscope() {
            int i = (this.mYear - 1864) % 60;
            String str = sJiaZi[i];
            int i2 = ((i % 5) + 1) * 2;
            if (i2 == 10) {
                i2 = 0;
            }
            String str2 = sTianGan[((i2 + this.mMonth) - 1) % 10] + sDiZhi[((this.mMonth + 2) - 1) % 12];
            int riZhu = getRiZhu();
            String str3 = sJiaZi[riZhu];
            int lunarHourIndex = getLunarHourIndex(this.mHour);
            return str + str2 + str3 + (sTianGan[(((riZhu % 5) * 2) + lunarHourIndex) % 10] + sDiZhi[lunarHourIndex]);
        }

        public int getRiZhu() {
            return (((int) ((this.mCalendar.getTime().getTime() - sBaseDate.getTime()) / DAY_IN_MILLS)) + 40) % sJiaZi.length;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:43:0x00c0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final long[] calLunar(int r17, int r18, int r19) {
        /*
            Method dump skipped, instructions count: 210
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.util.LunarDate.calLunar(int, int, int):long[]");
    }

    public static String formatLunarDate(int i, int i2, int i3) {
        StringBuilder sb = new StringBuilder();
        if (i > 0) {
            sb.append(i);
            sb.append("-");
        }
        sb.append(i2 + 1);
        sb.append("-");
        sb.append(i3);
        return sb.toString();
    }

    private static final int getDayOffset(int i, int i2, int i3) {
        GregorianCalendar gregorianCalendar = (GregorianCalendar) Calendar.getInstance();
        gregorianCalendar.clear();
        int i4 = 0;
        for (int i5 = 1900; i5 < i; i5++) {
            i4 = gregorianCalendar.isLeapYear(i5) ? i4 + 366 : i4 + 365;
        }
        gregorianCalendar.set(i, i2, i3);
        int i6 = i4 + gregorianCalendar.get(6);
        gregorianCalendar.set(MIN_LUNAR_YEAR, 0, 31);
        return i6 - gregorianCalendar.get(6);
    }

    public static final String getDayString(Resources resources, int i) {
        if (i == 10) {
            return resources.getString(R.string.lunar_chu_shi);
        }
        if (i == 20) {
            return resources.getString(R.string.lunar_er_shi);
        }
        if (i == 30) {
            return resources.getString(R.string.lunar_san_shi);
        }
        int i2 = i / 10;
        String string = i2 == 0 ? resources.getString(R.string.lunar_chu) : "";
        if (i2 == 1) {
            string = resources.getString(R.string.lunar_shi);
        }
        if (i2 == 2) {
            string = resources.getString(R.string.lunar_nian);
        }
        if (i2 == 3) {
            string = resources.getString(R.string.lunar_san);
        }
        switch (i % 10) {
            case 1:
                return string + resources.getString(R.string.lunar_yi);
            case 2:
                return string + resources.getString(R.string.lunar_er);
            case 3:
                return string + resources.getString(R.string.lunar_san);
            case 4:
                return string + resources.getString(R.string.lunar_si);
            case 5:
                return string + resources.getString(R.string.lunar_wu);
            case 6:
                return string + resources.getString(R.string.lunar_liu);
            case 7:
                return string + resources.getString(R.string.lunar_qi);
            case 8:
                return string + resources.getString(R.string.lunar_ba);
            case 9:
                return string + resources.getString(R.string.lunar_jiu);
            default:
                return string;
        }
    }

    private static String getDigitString(Resources resources, int i) {
        switch (i) {
            case 0:
                return resources.getString(R.string.lunar_ling);
            case 1:
                return resources.getString(R.string.lunar_yi);
            case 2:
                return resources.getString(R.string.lunar_er);
            case 3:
                return resources.getString(R.string.lunar_san);
            case 4:
                return resources.getString(R.string.lunar_si);
            case 5:
                return resources.getString(R.string.lunar_wu);
            case 6:
                return resources.getString(R.string.lunar_liu);
            case 7:
                return resources.getString(R.string.lunar_qi);
            case 8:
                return resources.getString(R.string.lunar_ba);
            case 9:
                return resources.getString(R.string.lunar_jiu);
            default:
                return "";
        }
    }

    public static String getHoliday(Resources resources, long[] jArr, Calendar calendar, String str) {
        int[] iArr;
        int[] iArr2;
        int i;
        try {
            int i2 = calendar.get(2) + 1;
            int i3 = calendar.get(5);
            if (Build.checkRegion("TW")) {
                iArr = solarHolidaysTable_TW;
                iArr2 = solarHolidays_TW;
            } else if (Build.checkRegion("HK")) {
                iArr = solarHolidaysTable_HK;
                iArr2 = solarHolidays_HK;
            } else {
                iArr = solarHolidaysTable;
                iArr2 = solarHolidays;
            }
            int length = iArr.length;
            for (int i4 = 0; i4 < length; i4++) {
                if (iArr[i4] / 100 == i2 && iArr[i4] % 100 == i3) {
                    return resources.getString(iArr2[i4]);
                }
            }
            if (Build.checkRegion("HK")) {
                String isEasterDay = isEasterDay(resources, calendar);
                if (!TextUtils.isEmpty(isEasterDay)) {
                    return isEasterDay;
                }
            }
        } catch (Exception unused) {
        }
        if (jArr[6] == 1) {
            return null;
        }
        int i5 = (int) jArr[1];
        int i6 = (int) jArr[2];
        int length2 = lunarHolidaysTable.length;
        for (i = 0; i < length2; i++) {
            int[] iArr3 = lunarHolidaysTable;
            if (iArr3[i] / 100 == i5 && iArr3[i] % 100 == i6) {
                return resources.getString(lunarHolidays[i]);
            }
        }
        return null;
    }

    public static int[][] getLunarBirthdays(int i, int i2, int i3) {
        int[][] iArr;
        int i4 = i2 + 1;
        if (i4 > 12) {
            if (i4 - 12 == rMonth(i)) {
                iArr = new int[2];
                int min = Math.min(rMthDays(i), i3);
                int[] lunarToSolar = lunarToSolar(i, i4, min);
                int[] iArr2 = new int[4];
                iArr2[0] = lunarToSolar[0];
                iArr2[1] = lunarToSolar[1] - 1;
                iArr2[2] = lunarToSolar[2];
                iArr2[3] = min == i3 ? 0 : 1;
                iArr[0] = iArr2;
            } else {
                iArr = new int[1];
            }
            i4 -= 12;
        } else {
            iArr = new int[1];
        }
        int min2 = Math.min(mthDays(i, i4), i3);
        int[] lunarToSolar2 = lunarToSolar(i, i4, min2);
        int length = iArr.length - 1;
        int[] iArr3 = new int[4];
        iArr3[0] = lunarToSolar2[0];
        iArr3[1] = lunarToSolar2[1] - 1;
        iArr3[2] = lunarToSolar2[2];
        iArr3[3] = min2 == i3 ? 0 : 1;
        iArr[length] = iArr3;
        return iArr;
    }

    private static int getLunarNewYearOffsetDays(int i, int i2, int i3) {
        int rMonth = rMonth(i);
        int i4 = 0;
        if (rMonth > 0 && rMonth == i2 - 12) {
            i4 = 0 + mthDays(i, rMonth);
            i2 = rMonth;
        }
        for (int i5 = 1; i5 < i2; i5++) {
            i4 += mthDays(i, i5);
            if (i5 == rMonth) {
                i4 += rMthDays(i);
            }
        }
        return i4 + (i3 - 1);
    }

    public static String getLunarString(Resources resources, int i, int i2, int i3) {
        StringBuilder sb = new StringBuilder();
        if (i > 0) {
            sb.append(Integer.toString(i));
            sb.append(resources.getString(R.string.lunar_year));
        }
        if (i2 >= 12) {
            sb.append(resources.getString(R.string.lunar_leap));
            i2 -= 12;
        }
        sb.append(getMonthString(resources, i2 + 1));
        sb.append(resources.getString(R.string.lunar_yue));
        sb.append(getDayString(resources, i3));
        return sb.toString();
    }

    public static String getMonthString(Resources resources, int i) {
        if (i > 12) {
            return null;
        }
        switch (i) {
            case 0:
                return "";
            case 1:
                return resources.getString(R.string.lunar_zheng);
            case 2:
                return resources.getString(R.string.lunar_er);
            case 3:
                return resources.getString(R.string.lunar_san);
            case 4:
                return resources.getString(R.string.lunar_si);
            case 5:
                return resources.getString(R.string.lunar_wu);
            case 6:
                return resources.getString(R.string.lunar_liu);
            case 7:
                return resources.getString(R.string.lunar_qi);
            case 8:
                return resources.getString(R.string.lunar_ba);
            case 9:
                return resources.getString(R.string.lunar_jiu);
            case 10:
                return resources.getString(R.string.lunar_shi);
            case 11:
                return resources.getString(R.string.lunar_shi_yi);
            case 12:
                return resources.getString(R.string.lunar_shi_er);
            default:
                return null;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x006b, code lost:
    
        return 0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static long getNextLunarBirthday(int r17, int r18) {
        /*
            android.text.format.Time r0 = new android.text.format.Time
            r0.<init>()
            r0.setToNow()
            int r1 = r0.year
            int r2 = r0.month
            int r3 = r0.monthDay
            long[] r1 = calLunar(r1, r2, r3)
            r2 = 0
            r3 = r1[r2]
            int r1 = (int) r3
            r0.second = r2
            r0.minute = r2
            r0.hour = r2
            long r3 = r0.normalize(r2)
            r5 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            r7 = r5
        L26:
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 != 0) goto L69
            r10 = 1900(0x76c, float:2.662E-42)
            if (r1 < r10) goto L69
            r10 = 2050(0x802, float:2.873E-42)
            if (r1 >= r10) goto L69
            r10 = r17
            r11 = r18
            int[][] r9 = getLunarBirthdays(r1, r10, r11)
            int r12 = r9.length
            r13 = r2
        L3c:
            if (r13 >= r12) goto L61
            r14 = r9[r13]
            r15 = 2
            r15 = r14[r15]
            r16 = 1
            r5 = r14[r16]
            r6 = r14[r2]
            r0.set(r15, r5, r6)
            long r5 = r0.normalize(r2)
            int r14 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r14 < 0) goto L59
            long r5 = java.lang.Math.min(r7, r5)
            r7 = r5
        L59:
            int r13 = r13 + 1
            r5 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            goto L3c
        L61:
            int r1 = r1 + 1
            r5 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            goto L26
        L69:
            if (r9 != 0) goto L6d
            r7 = 0
        L6d:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.util.LunarDate.getNextLunarBirthday(int, int):long");
    }

    public static String getSolarTerm(Resources resources, Calendar calendar) {
        int i = calendar.get(1);
        int i2 = calendar.get(2);
        int i3 = calendar.get(5);
        char c = solarTermsTable[((i - 1901) * 12) + i2];
        if (i3 == (c % 16) + 15) {
            return resources.getString(solarTerms[(i2 * 2) + 1]);
        }
        if (i3 == 15 - (c / 16)) {
            return resources.getString(solarTerms[i2 * 2]);
        }
        return null;
    }

    static int getSolarYearMonthDays(int i, int i2) {
        if (i2 == 1 || i2 == 3 || i2 == 5 || i2 == 7 || i2 == 8 || i2 == 10 || i2 == 12) {
            return 31;
        }
        if (i2 == 4 || i2 == 6 || i2 == 9 || i2 == 11) {
            return 30;
        }
        if (i2 == 2) {
            return isSolarLeapYear(i) ? 29 : 28;
        }
        return 0;
    }

    public static String getString(Resources resources, Calendar calendar) {
        return solar2lunar(resources, calendar.get(1), calendar.get(2), calendar.get(5));
    }

    public static String getYearString(Resources resources, int i) {
        StringBuffer stringBuffer = new StringBuffer();
        do {
            int i2 = i % 10;
            i /= 10;
            stringBuffer.insert(0, getDigitString(resources, i2));
        } while (i > 0);
        return stringBuffer.toString();
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0091  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x0095  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x009a A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.lang.String isEasterDay(android.content.res.Resources r10, java.util.Calendar r11) {
        /*
            r0 = 1
            int r1 = r11.get(r0)
            java.util.HashMap<java.lang.Integer, java.lang.Integer> r2 = miui.util.LunarDate.sEasterCache
            java.lang.Integer r3 = java.lang.Integer.valueOf(r1)
            boolean r3 = r2.containsKey(r3)
            r4 = 31
            r5 = 4
            if (r3 != 0) goto L45
            int r3 = r1 + (-1900)
            int r6 = r3 % 19
            int r7 = r3 / 4
            double r7 = (double) r7
            double r7 = java.lang.Math.floor(r7)
            int r7 = (int) r7
            int r8 = r6 * 7
            int r8 = r8 + r0
            int r8 = r8 / 19
            double r8 = (double) r8
            double r8 = java.lang.Math.floor(r8)
            int r8 = (int) r8
            int r6 = r6 * 11
            int r6 = r6 + r5
            int r6 = r6 - r8
            int r6 = r6 % 29
            int r3 = r3 + r7
            int r3 = r3 + r4
            int r3 = r3 - r6
            int r3 = r3 % 7
            int r6 = 25 - r6
            int r6 = r6 - r3
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r6)
            r2.put(r1, r3)
            goto L53
        L45:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            java.lang.Object r1 = r2.get(r1)
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r6 = r1.intValue()
        L53:
            r1 = 3
            if (r6 != 0) goto L58
        L56:
            r5 = r1
            goto L5f
        L58:
            if (r6 <= 0) goto L5c
            r4 = r6
            goto L5f
        L5c:
            int r4 = r6 + 31
            goto L56
        L5f:
            java.util.Date r1 = new java.util.Date
            r1.<init>()
            r2 = 2
            int r2 = r11.get(r2)
            r1.setMonth(r2)
            r2 = 5
            int r11 = r11.get(r2)
            r1.setDate(r11)
            java.util.Date r11 = new java.util.Date
            r11.<init>()
            int r5 = r5 - r0
            r11.setMonth(r5)
            r11.setDate(r4)
            long r2 = r11.getTime()
            long r0 = r1.getTime()
            long r2 = r2 - r0
            r0 = 86400000(0x5265c00, double:4.2687272E-316)
            long r2 = r2 / r0
            int r11 = (int) r2
            r0 = 0
            if (r11 != 0) goto L93
            int r0 = com.miui.system.internal.R.string.easter
        L93:
            if (r0 <= 0) goto L9a
            java.lang.String r10 = r10.getString(r0)
            return r10
        L9a:
            r10 = 0
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.util.LunarDate.isEasterDay(android.content.res.Resources, java.util.Calendar):java.lang.String");
    }

    static boolean isSolarLeapYear(int i) {
        return (i % 4 == 0 && i % 100 != 0) || i % 400 == 0;
    }

    public static int[] lunarToSolar(int i, int i2, int i3) {
        int[] iArr = new int[3];
        int lunarNewYearOffsetDays = getLunarNewYearOffsetDays(i, i2, i3) + iSolarLunarOffsetTable[i - 1901];
        int i4 = isSolarLeapYear(i) ? 366 : 365;
        if (lunarNewYearOffsetDays >= i4) {
            i++;
            lunarNewYearOffsetDays -= i4;
        }
        int i5 = lunarNewYearOffsetDays + 1;
        int i6 = 1;
        while (lunarNewYearOffsetDays >= 0) {
            i5 = lunarNewYearOffsetDays + 1;
            lunarNewYearOffsetDays -= getSolarYearMonthDays(i, i6);
            i6++;
        }
        iArr[0] = i;
        iArr[1] = i6 - 1;
        iArr[2] = i5;
        return iArr;
    }

    public static final int mthDays(int i, int i2) {
        return (((long) (SearchUpdater.GOOGLE >> i2)) & luYearData[i + (-1900)]) == 0 ? 29 : 30;
    }

    public static int[] parseLunarDate(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        int[] iArr = new int[3];
        try {
            String[] split = str.split("-");
            if (split.length == 2) {
                iArr[0] = Integer.parseInt(split[1].trim());
                iArr[1] = Integer.parseInt(split[0].trim()) - 1;
                iArr[2] = 0;
            } else if (split.length != 3) {
                return null;
            } else {
                iArr[0] = Integer.parseInt(split[2].trim());
                iArr[1] = Integer.parseInt(split[1].trim()) - 1;
                iArr[2] = Integer.parseInt(split[0].trim());
            }
            return iArr;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final int rMonth(int i) {
        return (int) (luYearData[i - 1900] & 15);
    }

    public static final int rMthDays(int i) {
        if (rMonth(i) != 0) {
            return (luYearData[i + (-1900)] & PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH) != 0 ? 30 : 29;
        }
        return 0;
    }

    public static String solar2lunar(Resources resources, int i, int i2, int i3) {
        long[] calLunar = calLunar(i, i2, i3);
        StringBuffer stringBuffer = new StringBuffer();
        if (calLunar[6] == 1) {
            stringBuffer.append(resources.getString(R.string.lunar_leap));
        }
        stringBuffer.append(getMonthString(resources, (int) calLunar[1]));
        stringBuffer.append(resources.getString(R.string.lunar_yue));
        stringBuffer.append(getDayString(resources, (int) calLunar[2]));
        return stringBuffer.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final int yrDays(int i) {
        int i2 = 348;
        for (int i3 = MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON; i3 > 8; i3 >>= 1) {
            if ((luYearData[i - 1900] & i3) != 0) {
                i2++;
            }
        }
        return i2 + rMthDays(i);
    }
}
