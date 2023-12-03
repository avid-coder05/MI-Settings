package miuix.pinyin.utilities;

import android.util.Log;
import com.googlecode.tesseract.android.TessBaseAPI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import miui.cloud.CloudPushConstants;
import miui.provider.MiCloudSmsCmd;
import miuix.core.util.Pools;
import miuix.core.util.SoftReferenceSingleton;

/* loaded from: classes5.dex */
public class HindiPinyinConverter {
    private String[] mAdditionalConsonantPinyins;
    private String[] mAdditionalConsonantUnicodes;
    private String[] mConsonantPinyins;
    private String[] mConsonantUnicodes;
    private String[] mDependentVowelSignPinyins;
    private String[] mDependentVowelSignUnicodes;
    private HashMap<String, String> mDoubleCharacters;
    private String[] mIndependentVowelPinyins;
    private String[] mIndependentVowelUnicodes;
    private HashMap<String, String> mSingleCharacters;
    private HashMap<String, String> mTripleCharacters;
    private String[] mVariousSignPinyins;
    private String[] mVariousSignUnicodes;
    private static final SoftReferenceSingleton<HindiPinyinConverter> INSTANCE = new SoftReferenceSingleton<HindiPinyinConverter>() { // from class: miuix.pinyin.utilities.HindiPinyinConverter.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // miuix.core.util.SoftReferenceSingleton
        public HindiPinyinConverter createInstance() {
            return new HindiPinyinConverter();
        }
    };
    private static Pools.SimplePool<StringBuilder> sStringBuilder = Pools.createSimplePool(new Pools.Manager<StringBuilder>() { // from class: miuix.pinyin.utilities.HindiPinyinConverter.2
        @Override // miuix.core.util.Pools.Manager
        public StringBuilder createInstance() {
            return new StringBuilder();
        }

        @Override // miuix.core.util.Pools.Manager
        public void onRelease(StringBuilder sb) {
            sb.setLength(0);
        }
    }, 4);
    private static Pools.SimplePool<ArrayList<String>> sArrayList = Pools.createSimplePool(new Pools.Manager<ArrayList<String>>() { // from class: miuix.pinyin.utilities.HindiPinyinConverter.3
        @Override // miuix.core.util.Pools.Manager
        public ArrayList<String> createInstance() {
            return new ArrayList<>();
        }

        @Override // miuix.core.util.Pools.Manager
        public void onRelease(ArrayList<String> arrayList) {
            arrayList.clear();
        }
    }, 4);

    private HindiPinyinConverter() {
        initUnicodeAndPinyin();
        mapUnicodeToPinyin();
    }

    private static String concat(String... strArr) {
        StringBuilder sb = (StringBuilder) sStringBuilder.acquire();
        for (String str : strArr) {
            sb.append(str);
        }
        String sb2 = sb.toString();
        sStringBuilder.release(sb);
        return sb2;
    }

    public static HindiPinyinConverter getInstance() {
        return INSTANCE.get();
    }

    private void initUnicodeAndPinyin() {
        this.mIndependentVowelUnicodes = new String[12];
        this.mIndependentVowelPinyins = new String[12];
        this.mDependentVowelSignUnicodes = new String[15];
        this.mDependentVowelSignPinyins = new String[15];
        this.mConsonantUnicodes = new String[35];
        this.mConsonantPinyins = new String[35];
        this.mAdditionalConsonantUnicodes = new String[8];
        this.mAdditionalConsonantPinyins = new String[8];
        this.mVariousSignUnicodes = new String[3];
        this.mVariousSignPinyins = new String[3];
        this.mSingleCharacters = new HashMap<>();
        this.mDoubleCharacters = new HashMap<>();
        this.mTripleCharacters = new HashMap<>();
        String[] strArr = this.mIndependentVowelUnicodes;
        strArr[0] = "अ";
        strArr[1] = "आ";
        strArr[2] = "इ";
        strArr[3] = "ई";
        strArr[4] = "उ";
        strArr[5] = "ऊ";
        strArr[6] = "ऋ";
        strArr[7] = "ए";
        strArr[8] = "ऐ";
        strArr[9] = "ऑ";
        strArr[10] = "ओ";
        strArr[11] = "औ";
        String[] strArr2 = this.mIndependentVowelPinyins;
        strArr2[0] = "a";
        strArr2[1] = "aa";
        strArr2[2] = "i";
        strArr2[3] = "ee";
        strArr2[4] = "u";
        strArr2[5] = "oo";
        strArr2[6] = "r";
        strArr2[7] = "e";
        strArr2[8] = "ai";
        strArr2[9] = "o";
        strArr2[10] = "o";
        strArr2[11] = "au";
        String[] strArr3 = this.mDependentVowelSignUnicodes;
        strArr3[0] = "ा";
        strArr3[1] = "ि";
        strArr3[2] = "ी";
        strArr3[3] = "ु";
        strArr3[4] = "ू";
        strArr3[5] = "ृ";
        strArr3[6] = "ॄ";
        strArr3[7] = "ॅ";
        strArr3[8] = "े";
        strArr3[9] = "ै";
        strArr3[10] = "ॉ";
        strArr3[11] = "ो";
        strArr3[12] = "ौ";
        strArr3[13] = "ॎ";
        strArr3[14] = "ॏ";
        String[] strArr4 = this.mDependentVowelSignPinyins;
        strArr4[0] = "aa";
        strArr4[1] = "i";
        strArr4[2] = "ee";
        strArr4[3] = "u";
        strArr4[4] = "oo";
        strArr4[5] = "r";
        strArr4[6] = "R";
        strArr4[7] = "e";
        strArr4[8] = "e";
        strArr4[9] = "ai";
        strArr4[10] = "o";
        strArr4[11] = "o";
        strArr4[12] = "au";
        strArr4[13] = "e";
        strArr4[14] = "aw";
        String[] strArr5 = this.mConsonantUnicodes;
        strArr5[0] = "क";
        strArr5[1] = "ख";
        strArr5[2] = "ग";
        strArr5[3] = "घ";
        strArr5[4] = "ङ";
        strArr5[5] = "च";
        strArr5[6] = "छ";
        strArr5[7] = "ज";
        strArr5[8] = "झ";
        strArr5[9] = "ञ";
        strArr5[10] = "ट";
        strArr5[11] = "ठ";
        strArr5[12] = "ड";
        strArr5[13] = "ढ";
        strArr5[14] = "ण";
        strArr5[15] = "त";
        strArr5[16] = "थ";
        strArr5[17] = "द";
        strArr5[18] = "ध";
        strArr5[19] = "न";
        strArr5[20] = "ऩ";
        strArr5[21] = "प";
        strArr5[22] = "फ";
        strArr5[23] = "ब";
        strArr5[24] = "भ";
        strArr5[25] = "म";
        strArr5[26] = "य";
        strArr5[27] = "र";
        strArr5[28] = "ऱ";
        strArr5[29] = "ल";
        strArr5[30] = "व";
        strArr5[31] = "श";
        strArr5[32] = "ष";
        strArr5[33] = "स";
        strArr5[34] = "ह";
        String[] strArr6 = this.mConsonantPinyins;
        strArr6[0] = MiCloudSmsCmd.TYPE_LOCK;
        strArr6[1] = "kh";
        strArr6[2] = CloudPushConstants.WATERMARK_TYPE.GLOBAL;
        strArr6[3] = "gh";
        strArr6[4] = "ng";
        strArr6[5] = "c";
        strArr6[6] = "ch";
        strArr6[7] = "j";
        strArr6[8] = "jh";
        strArr6[9] = "ny";
        strArr6[10] = TessBaseAPI.VAR_TRUE;
        strArr6[11] = "Th";
        strArr6[12] = "D";
        strArr6[13] = "Dh";
        strArr6[14] = "N";
        strArr6[15] = "t";
        strArr6[16] = "th";
        strArr6[17] = MiCloudSmsCmd.TYPE_DISCARD_TOKEN;
        strArr6[18] = "dh";
        strArr6[19] = MiCloudSmsCmd.TYPE_NOISE;
        strArr6[20] = "Nn";
        strArr6[21] = CloudPushConstants.WATERMARK_TYPE.PERSONAL;
        strArr6[22] = "ph";
        strArr6[23] = "b";
        strArr6[24] = "bh";
        strArr6[25] = "m";
        strArr6[26] = "y";
        strArr6[27] = "r";
        strArr6[28] = "R";
        strArr6[29] = MiCloudSmsCmd.TYPE_LOCATION;
        strArr6[30] = "v";
        strArr6[31] = "sh";
        strArr6[32] = "S";
        strArr6[33] = CloudPushConstants.WATERMARK_TYPE.SUBSCRIPTION;
        strArr6[34] = "h";
        String[] strArr7 = this.mAdditionalConsonantUnicodes;
        strArr7[0] = "क़";
        strArr7[1] = "ख़";
        strArr7[2] = "ग़";
        strArr7[3] = "ज़";
        strArr7[4] = "ड़";
        strArr7[5] = "ढ़";
        strArr7[6] = "फ़";
        strArr7[7] = "य़";
        String[] strArr8 = this.mAdditionalConsonantPinyins;
        strArr8[0] = "q";
        strArr8[1] = "khh";
        strArr8[2] = "ghh";
        strArr8[3] = "z";
        strArr8[4] = "Ddh";
        strArr8[5] = "rh";
        strArr8[6] = "f";
        strArr8[7] = "Y";
        String[] strArr9 = this.mVariousSignUnicodes;
        strArr9[0] = "ँ";
        strArr9[1] = "ं";
        strArr9[2] = "ः";
        String[] strArr10 = this.mVariousSignPinyins;
        strArr10[0] = "an";
        strArr10[1] = "an";
        strArr10[2] = "ah";
    }

    private void mapUnicodeToPinyin() {
        for (int i = 0; i < 35; i++) {
            this.mSingleCharacters.put(this.mConsonantUnicodes[i], this.mConsonantPinyins[i] + "a");
        }
        for (int i2 = 0; i2 < 12; i2++) {
            this.mSingleCharacters.put(this.mIndependentVowelUnicodes[i2], this.mIndependentVowelPinyins[i2]);
        }
        for (int i3 = 0; i3 < 8; i3++) {
            this.mSingleCharacters.put(this.mAdditionalConsonantUnicodes[i3], this.mAdditionalConsonantPinyins[i3]);
        }
        for (int i4 = 0; i4 < 35; i4++) {
            for (int i5 = 0; i5 < 15; i5++) {
                this.mDoubleCharacters.put(this.mConsonantUnicodes[i4] + this.mDependentVowelSignUnicodes[i5], this.mConsonantPinyins[i4] + this.mDependentVowelSignPinyins[i5]);
            }
            for (int i6 = 0; i6 < 3; i6++) {
                this.mDoubleCharacters.put(this.mConsonantUnicodes[i4] + this.mVariousSignUnicodes[i6], this.mConsonantPinyins[i4] + this.mVariousSignPinyins[i6]);
            }
        }
        for (int i7 = 0; i7 < 8; i7++) {
            for (int i8 = 0; i8 < 15; i8++) {
                this.mDoubleCharacters.put(this.mAdditionalConsonantUnicodes[i7] + this.mDependentVowelSignUnicodes[i8], this.mAdditionalConsonantPinyins[i7] + this.mDependentVowelSignPinyins[i8]);
            }
            for (int i9 = 0; i9 < 3; i9++) {
                this.mDoubleCharacters.put(this.mConsonantUnicodes[i7] + this.mVariousSignUnicodes[i9], this.mConsonantPinyins[i7] + this.mVariousSignPinyins[i9]);
            }
        }
        for (int i10 = 0; i10 < 35; i10++) {
            this.mDoubleCharacters.put(this.mConsonantUnicodes[i10] + "्", this.mConsonantPinyins[i10]);
        }
        for (int i11 = 0; i11 < 12; i11++) {
        }
        for (int i12 = 0; i12 < 35; i12++) {
            for (int i13 = 0; i13 < 15; i13++) {
                for (int i14 = 0; i14 < 3; i14++) {
                    this.mTripleCharacters.put(this.mConsonantUnicodes[i12] + this.mDependentVowelSignUnicodes[i13] + this.mVariousSignUnicodes[i14], this.mConsonantPinyins[i12] + this.mDependentVowelSignPinyins[i13] + this.mVariousSignPinyins[i14].substring(1));
                }
            }
        }
        this.mDoubleCharacters.put("अं", "am");
    }

    private static String stringToUnicode(String str) {
        StringBuilder sb = (StringBuilder) sStringBuilder.acquire();
        int length = str.length();
        int i = 0;
        while (i < length) {
            int codePointAt = Character.codePointAt(str, i);
            int charCount = Character.charCount(codePointAt);
            if (charCount > 1) {
                i += charCount - 1;
            }
            if (codePointAt < 128) {
                sb.appendCodePoint(codePointAt);
            } else {
                sb.append(String.format("\\u%04x", Integer.valueOf(codePointAt)));
            }
            i++;
        }
        String sb2 = sb.toString();
        sStringBuilder.release(sb);
        return sb2;
    }

    public String hindiToPinyin(String str) {
        String str2;
        String str3;
        int i;
        String str4 = str;
        long currentTimeMillis = System.currentTimeMillis();
        StringBuilder sb = (StringBuilder) sStringBuilder.acquire();
        int length = str.length();
        int i2 = 0;
        while (i2 < length) {
            int charCount = Character.charCount(Character.codePointAt(str4, i2));
            int i3 = i2 + charCount;
            String substring = str4.substring(i2, i3);
            String str5 = "";
            if (i3 < length) {
                charCount = Character.charCount(Character.codePointAt(str4, i3));
                str2 = str4.substring(i3, i3 + charCount);
            } else {
                str2 = "";
            }
            if (!str2.isEmpty()) {
                int i4 = i3 + charCount;
                if (i4 < length) {
                    charCount = Character.charCount(Character.codePointAt(str4, i4));
                    str3 = str4.substring(i4, i4 + charCount);
                } else {
                    str3 = "";
                }
                if (str3.isEmpty()) {
                    String concat = concat(substring, str2);
                    if (this.mDoubleCharacters.containsKey(concat)) {
                        str5 = this.mDoubleCharacters.get(concat);
                        i3 += charCount;
                    } else if (this.mSingleCharacters.containsKey(substring)) {
                        str5 = this.mSingleCharacters.get(substring);
                    } else {
                        Log.w("HindiPinyinConverter", String.format("Ignore unknown hindi: %s%s%s %s", substring, str2, str3, stringToUnicode(concat(substring, str2, str3))));
                    }
                } else {
                    String concat2 = concat(substring, str2);
                    i = length;
                    String concat3 = concat(concat2, str3);
                    if (this.mTripleCharacters.containsKey(concat3)) {
                        str5 = this.mTripleCharacters.get(concat3);
                        i2 = i4 + charCount;
                        sb.append(str5);
                        str4 = str;
                        length = i;
                    } else {
                        if (this.mDoubleCharacters.containsKey(concat2)) {
                            str5 = this.mDoubleCharacters.get(concat2);
                            i3 += charCount;
                        } else if (this.mSingleCharacters.containsKey(substring)) {
                            str5 = this.mSingleCharacters.get(substring);
                        } else {
                            Log.w("HindiPinyinConverter", String.format("Ignore unknown hindi: '%s%s%s' '%s'", substring, str2, str3, stringToUnicode(concat(substring, str2, str3))));
                        }
                        i2 = i3;
                        sb.append(str5);
                        str4 = str;
                        length = i;
                    }
                }
            } else if (this.mSingleCharacters.containsKey(substring)) {
                str5 = this.mSingleCharacters.get(substring);
            }
            i = length;
            i2 = i3;
            sb.append(str5);
            str4 = str;
            length = i;
        }
        String sb2 = sb.toString();
        sStringBuilder.release(sb);
        Log.d("HindiPinyinConverter", String.format("hindiToPinyin(): using time %d ms", Long.valueOf(System.currentTimeMillis() - currentTimeMillis)));
        return sb2;
    }

    public String[] hindiToPinyins(String str) {
        ArrayList arrayList = (ArrayList) sArrayList.acquire();
        String hindiToPinyin = hindiToPinyin(str);
        arrayList.add(hindiToPinyin);
        if (hindiToPinyin.contains("ee")) {
            arrayList.add(hindiToPinyin.replaceAll("ee", "ii"));
        }
        if (hindiToPinyin.contains("oo")) {
            arrayList.add(hindiToPinyin.replaceAll("oo", "uu"));
        }
        if (hindiToPinyin.contains("v")) {
            arrayList.add(hindiToPinyin.replaceAll("v", MiCloudSmsCmd.TYPE_WIPE));
        }
        ArrayList arrayList2 = (ArrayList) sArrayList.acquire();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            String str2 = (String) it.next();
            if (!str2.endsWith("aa") && str2.endsWith("a")) {
                arrayList2.add(str2.substring(0, str2.length() - 1));
            }
        }
        arrayList.addAll(arrayList2);
        String[] strArr = (String[]) arrayList.toArray(new String[0]);
        sArrayList.release(arrayList);
        sArrayList.release(arrayList2);
        return strArr;
    }
}
