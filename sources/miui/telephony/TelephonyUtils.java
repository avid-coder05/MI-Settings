package miui.telephony;

import android.content.Intent;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes4.dex */
public class TelephonyUtils {
    private static final String LOG_TAG = "TelephonyUtils";
    private static final HashMap<String, String> sNonRoamingMap;

    static {
        HashMap<String, String> hashMap = new HashMap<>();
        sNonRoamingMap = hashMap;
        hashMap.put("40401", "India");
        hashMap.put("40402", "India");
        hashMap.put("40403", "India");
        hashMap.put("40404", "India");
        hashMap.put("40405", "India");
        hashMap.put("40407", "India");
        hashMap.put("40409", "India");
        hashMap.put("40410", "India");
        hashMap.put("40411", "India");
        hashMap.put("40412", "India");
        hashMap.put("40413", "India");
        hashMap.put("40414", "India");
        hashMap.put("40415", "India");
        hashMap.put("40416", "India");
        hashMap.put("40417", "India");
        hashMap.put("40418", "India");
        hashMap.put("40419", "India");
        hashMap.put("40420", "India");
        hashMap.put("40421", "India");
        hashMap.put("40422", "India");
        hashMap.put("40424", "India");
        hashMap.put("40425", "India");
        hashMap.put("40427", "India");
        hashMap.put("40428", "India");
        hashMap.put("40429", "India");
        hashMap.put("40430", "India");
        hashMap.put("40431", "India");
        hashMap.put("40434", "India");
        hashMap.put("40436", "India");
        hashMap.put("40437", "India");
        hashMap.put("40438", "India");
        hashMap.put("40440", "India");
        hashMap.put("40441", "India");
        hashMap.put("40442", "India");
        hashMap.put("40443", "India");
        hashMap.put("40444", "India");
        hashMap.put("40445", "India");
        hashMap.put("40446", "India");
        hashMap.put("40448", "India");
        hashMap.put("40449", "India");
        hashMap.put("40450", "India");
        hashMap.put("40451", "India");
        hashMap.put("40452", "India");
        hashMap.put("40453", "India");
        hashMap.put("40454", "India");
        hashMap.put("40455", "India");
        hashMap.put("40456", "India");
        hashMap.put("40457", "India");
        hashMap.put("40458", "India");
        hashMap.put("40459", "India");
        hashMap.put("40460", "India");
        hashMap.put("40462", "India");
        hashMap.put("40464", "India");
        hashMap.put("40466", "India");
        hashMap.put("40467", "India");
        hashMap.put("40468", "India");
        hashMap.put("40469", "India");
        hashMap.put("40470", "India");
        hashMap.put("40471", "India");
        hashMap.put("40472", "India");
        hashMap.put("40473", "India");
        hashMap.put("40474", "India");
        hashMap.put("40475", "India");
        hashMap.put("40476", "India");
        hashMap.put("40477", "India");
        hashMap.put("40478", "India");
        hashMap.put("40479", "India");
        hashMap.put("40480", "India");
        hashMap.put("40481", "India");
        hashMap.put("40482", "India");
        hashMap.put("40483", "India");
        hashMap.put("40484", "India");
        hashMap.put("40485", "India");
        hashMap.put("40486", "India");
        hashMap.put("40487", "India");
        hashMap.put("40488", "India");
        hashMap.put("40489", "India");
        hashMap.put("40490", "India");
        hashMap.put("40491", "India");
        hashMap.put("40492", "India");
        hashMap.put("40493", "India");
        hashMap.put("40494", "India");
        hashMap.put("40495", "India");
        hashMap.put("40496", "India");
        hashMap.put("40497", "India");
        hashMap.put("40498", "India");
        hashMap.put("40501", "India");
        hashMap.put("40503", "India");
        hashMap.put("40504", "India");
        hashMap.put("40505", "India");
        hashMap.put("40506", "India");
        hashMap.put("40507", "India");
        hashMap.put("40508", "India");
        hashMap.put("40509", "India");
        hashMap.put("40510", "India");
        hashMap.put("40511", "India");
        hashMap.put("40512", "India");
        hashMap.put("40513", "India");
        hashMap.put("40514", "India");
        hashMap.put("40515", "India");
        hashMap.put("40517", "India");
        hashMap.put("40518", "India");
        hashMap.put("40519", "India");
        hashMap.put("40520", "India");
        hashMap.put("40521", "India");
        hashMap.put("40522", "India");
        hashMap.put("40523", "India");
        hashMap.put("40525", "India");
        hashMap.put("40526", "India");
        hashMap.put("40527", "India");
        hashMap.put("40528", "India");
        hashMap.put("40529", "India");
        hashMap.put("40530", "India");
        hashMap.put("40531", "India");
        hashMap.put("40532", "India");
        hashMap.put("40533", "India");
        hashMap.put("40534", "India");
        hashMap.put("40535", "India");
        hashMap.put("40536", "India");
        hashMap.put("40537", "India");
        hashMap.put("40538", "India");
        hashMap.put("40539", "India");
        hashMap.put("40541", "India");
        hashMap.put("40542", "India");
        hashMap.put("40543", "India");
        hashMap.put("40544", "India");
        hashMap.put("40545", "India");
        hashMap.put("40546", "India");
        hashMap.put("40547", "India");
        hashMap.put("40551", "India");
        hashMap.put("40552", "India");
        hashMap.put("40553", "India");
        hashMap.put("40554", "India");
        hashMap.put("40555", "India");
        hashMap.put("40556", "India");
        hashMap.put("40566", "India");
        hashMap.put("40570", "India");
        hashMap.put("405750", "India");
        hashMap.put("405751", "India");
        hashMap.put("405752", "India");
        hashMap.put("405753", "India");
        hashMap.put("405754", "India");
        hashMap.put("405755", "India");
        hashMap.put("405756", "India");
        hashMap.put("405799", "India");
        hashMap.put("405800", "India");
        hashMap.put("405801", "India");
        hashMap.put("405802", "India");
        hashMap.put("405803", "India");
        hashMap.put("405804", "India");
        hashMap.put("405805", "India");
        hashMap.put("405806", "India");
        hashMap.put("405807", "India");
        hashMap.put("405808", "India");
        hashMap.put("405809", "India");
        hashMap.put("405810", "India");
        hashMap.put("405811", "India");
        hashMap.put("405812", "India");
        hashMap.put("405819", "India");
        hashMap.put("405818", "India");
        hashMap.put("405820", "India");
        hashMap.put("405821", "India");
        hashMap.put("405822", "India");
        hashMap.put("405824", "India");
        hashMap.put("405827", "India");
        hashMap.put("405834", "India");
        hashMap.put("405844", "India");
        hashMap.put("405845", "India");
        hashMap.put("405846", "India");
        hashMap.put("405847", "India");
        hashMap.put("405848", "India");
        hashMap.put("405849", "India");
        hashMap.put("405850", "India");
        hashMap.put("405851", "India");
        hashMap.put("405852", "India");
        hashMap.put("405853", "India");
        hashMap.put("405854", "India");
        hashMap.put("405855", "India");
        hashMap.put("405856", "India");
        hashMap.put("405857", "India");
        hashMap.put("405858", "India");
        hashMap.put("405859", "India");
        hashMap.put("405860", "India");
        hashMap.put("405861", "India");
        hashMap.put("405862", "India");
        hashMap.put("405863", "India");
        hashMap.put("405864", "India");
        hashMap.put("405865", "India");
        hashMap.put("405866", "India");
        hashMap.put("405867", "India");
        hashMap.put("405868", "India");
        hashMap.put("405869", "India");
        hashMap.put("405870", "India");
        hashMap.put("405871", "India");
        hashMap.put("405872", "India");
        hashMap.put("405873", "India");
        hashMap.put("405874", "India");
        hashMap.put("405875", "India");
        hashMap.put("405880", "India");
        hashMap.put("405881", "India");
        hashMap.put("405908", "India");
        hashMap.put("405909", "India");
        hashMap.put("405910", "India");
        hashMap.put("405911", "India");
        hashMap.put("405912", "India");
        hashMap.put("405913", "India");
        hashMap.put("405914", "India");
        hashMap.put("405917", "India");
        hashMap.put("405927", "India");
        hashMap.put("405929", "India");
        hashMap.put("40475", "India");
        hashMap.put("40451", "India");
        hashMap.put("40458", "India");
        hashMap.put("40481", "India");
        hashMap.put("40474", "India");
        hashMap.put("40438", "India");
        hashMap.put("40457", "India");
        hashMap.put("40480", "India");
        hashMap.put("40473", "India");
        hashMap.put("40434", "India");
        hashMap.put("40466", "India");
        hashMap.put("40455", "India");
        hashMap.put("40472", "India");
        hashMap.put("40477", "India");
        hashMap.put("40464", "India");
        hashMap.put("40454", "India");
        hashMap.put("40471", "India");
        hashMap.put("40476", "India");
        hashMap.put("40462", "India");
        hashMap.put("40453", "India");
        hashMap.put("40459", "India");
    }

    private TelephonyUtils() {
    }

    public static boolean isOperatorConsideredNonRoaming(String str, String str2, String str3) {
        if (TextUtils.isEmpty(str2) || TextUtils.isEmpty(str3)) {
            return false;
        }
        Rlog.i(LOG_TAG, "isOperatorConsideredNonRoaming for " + str2);
        return (str2.startsWith("404") || str2.startsWith("405")) && (str3.startsWith("404") || str3.startsWith("405"));
    }

    public static String pii(CharSequence charSequence, int i, int i2) {
        int i3 = 0;
        int length = charSequence == null ? 0 : charSequence.length();
        if (length != 0) {
            StringBuilder sb = new StringBuilder(length);
            while (i3 < length) {
                sb.append((i3 < i || i3 >= length - i2) ? charSequence.charAt(i3) : 'x');
                i3++;
            }
            return sb.toString();
        }
        return "";
    }

    public static String pii(String str) {
        int length = str == null ? 0 : str.length();
        return length == 0 ? "" : length >= 15 ? pii(str, 6, 2) : length >= 11 ? pii(str, 2, 2) : length >= 6 ? pii(str, 0, 2) : length >= 2 ? pii(str, 0, 1) : pii(str, 0, 0);
    }

    public static String piiIP(String str) {
        if (!TextUtils.isEmpty(str)) {
            Matcher matcher = Pattern.compile("(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}|(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)").matcher(str);
            while (matcher.find()) {
                try {
                    int i = 0;
                    String group = matcher.group(0);
                    int length = group == null ? 0 : group.length();
                    if (length != 0) {
                        StringBuilder sb = new StringBuilder();
                        while (i < length - 1) {
                            int i2 = i + 1;
                            char charAt = group.charAt(i2);
                            if (charAt != '.' && charAt != ':') {
                                sb.append(group.charAt(i));
                                i = i2;
                            }
                            sb.append('x');
                            i = i2;
                        }
                        sb.append('x');
                        str = str.replace(group, sb.toString());
                    }
                } catch (Exception e) {
                    Rlog.e(LOG_TAG, "piiIP e: " + e);
                }
            }
        }
        return str;
    }

    public static void putDialConferenceExtra(Intent intent, boolean z) {
        if (intent != null) {
            intent.putExtra(TelephonyConstants.EXTRA_DIAL_CONFERENCE_URI, z);
        } else {
            Rlog.e(LOG_TAG, "putDialConferenceExtra intent==null");
        }
    }

    public static void putVideoStateExtra(Intent intent, int i) {
        if (intent != null) {
            intent.putExtra(TelephonyConstants.EXTRA_START_CALL_WITH_VIDEO_STATE, i);
        } else {
            Rlog.e(LOG_TAG, "putVideoStateExtra intent==null");
        }
    }
}
