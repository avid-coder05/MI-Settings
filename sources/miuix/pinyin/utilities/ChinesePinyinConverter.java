package miuix.pinyin.utilities;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import miuix.core.util.DirectIndexedFile;
import miuix.core.util.DirectIndexedFileExtractor;
import miuix.core.util.SoftReferenceSingleton;

/* loaded from: classes5.dex */
public class ChinesePinyinConverter {
    private ChinesePinyinDictionary mDictionary;
    private static HashMap<String, String[]> sHyphenatedNamePolyPhoneMap = new HashMap<>();
    private static HashMap<Character, String> sLastNamePolyPhoneMap = new HashMap<>();
    private static final SoftReferenceSingleton<ChinesePinyinConverter> INSTANCE = new SoftReferenceSingleton<ChinesePinyinConverter>() { // from class: miuix.pinyin.utilities.ChinesePinyinConverter.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // miuix.core.util.SoftReferenceSingleton
        public ChinesePinyinConverter createInstance(Object obj) {
            return new ChinesePinyinConverter((Context) obj);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class ChinesePinyinDictionary {
        private DirectIndexedFile.Reader mReader;

        private ChinesePinyinDictionary(Context context) {
            String directIndexedFilePath = DirectIndexedFileExtractor.getDirectIndexedFilePath(context, "pinyinindex.idf");
            if (directIndexedFilePath != null && new File(directIndexedFilePath).exists()) {
                try {
                    this.mReader = DirectIndexedFile.open(directIndexedFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (this.mReader == null) {
                try {
                    this.mReader = DirectIndexedFile.open(context.getResources().getAssets().open("pinyinindex.idf", 1));
                } catch (Exception unused) {
                    Log.e("ChinesePinyinConverter", "Init resource IOException");
                }
            }
        }

        protected void finalize() throws Throwable {
            DirectIndexedFile.Reader reader = this.mReader;
            if (reader != null) {
                reader.close();
            }
            super.finalize();
        }

        public String[] getPinyinString(char c) {
            DirectIndexedFile.Reader reader = this.mReader;
            if (reader == null) {
                return null;
            }
            String str = (String) reader.get(0, c - 19968, 0);
            if (TextUtils.isEmpty(str)) {
                Log.e("ChinesePinyinConverter", "The ChinesePinyinConverter dictionary is not correct, need rebuild or reset the ROM.");
                return null;
            }
            return str.split(",");
        }
    }

    /* loaded from: classes5.dex */
    public static class Token {
        public String[] polyPhones;
        public String source;
        public String target;
        public int type;

        public Token() {
        }

        public Token(int i, String str, String str2) {
            this.type = i;
            this.source = str;
            this.target = str2;
        }
    }

    static {
        sHyphenatedNamePolyPhoneMap.put("单于", new String[]{"CHAN", "YU"});
        sHyphenatedNamePolyPhoneMap.put("长孙", new String[]{"ZHANG", "SUN"});
        sHyphenatedNamePolyPhoneMap.put("子车", new String[]{"ZI", "JU"});
        sHyphenatedNamePolyPhoneMap.put("万俟", new String[]{"MO", "QI"});
        sHyphenatedNamePolyPhoneMap.put("澹台", new String[]{"TAN", "TAI"});
        sHyphenatedNamePolyPhoneMap.put("尉迟", new String[]{"YU", "CHI"});
        sLastNamePolyPhoneMap.put((char) 20040, "YAO");
        sLastNamePolyPhoneMap.put((char) 19969, "DING");
        sLastNamePolyPhoneMap.put((char) 20446, "YU");
        sLastNamePolyPhoneMap.put((char) 36158, "JIA");
        sLastNamePolyPhoneMap.put((char) 27784, "SHEN");
        sLastNamePolyPhoneMap.put((char) 21340, "BU");
        sLastNamePolyPhoneMap.put((char) 34180, "BO");
        sLastNamePolyPhoneMap.put((char) 23387, "BO");
        sLastNamePolyPhoneMap.put((char) 36146, "BEN");
        sLastNamePolyPhoneMap.put((char) 36153, "FEI");
        sLastNamePolyPhoneMap.put((char) 27850, "BAN");
        sLastNamePolyPhoneMap.put((char) 33536, "BI");
        sLastNamePolyPhoneMap.put((char) 35098, "CHU");
        sLastNamePolyPhoneMap.put((char) 20256, "CHUAN");
        sLastNamePolyPhoneMap.put((char) 21442, "CAN");
        sLastNamePolyPhoneMap.put((char) 21333, "SHAN");
        sLastNamePolyPhoneMap.put((char) 37079, "CHI");
        sLastNamePolyPhoneMap.put((char) 38241, "CHAN");
        sLastNamePolyPhoneMap.put((char) 26397, "CHAO");
        sLastNamePolyPhoneMap.put((char) 21852, "CHUAI");
        sLastNamePolyPhoneMap.put((char) 34928, "CUI");
        sLastNamePolyPhoneMap.put((char) 26216, "CHANG");
        sLastNamePolyPhoneMap.put((char) 19985, "CHOU");
        sLastNamePolyPhoneMap.put((char) 30259, "CHOU");
        sLastNamePolyPhoneMap.put((char) 38271, "CHANG");
        sLastNamePolyPhoneMap.put((char) 36710, "CHE");
        sLastNamePolyPhoneMap.put((char) 32735, "ZHAI");
        sLastNamePolyPhoneMap.put((char) 20291, "DIAN");
        sLastNamePolyPhoneMap.put((char) 20992, "DIAO");
        sLastNamePolyPhoneMap.put((char) 35843, "DIAO");
        sLastNamePolyPhoneMap.put((char) 36934, "DI");
        sLastNamePolyPhoneMap.put((char) 26123, "GUI");
        sLastNamePolyPhoneMap.put((char) 33445, "GAI");
        sLastNamePolyPhoneMap.put((char) 33554, "KUANG");
        sLastNamePolyPhoneMap.put((char) 37063, "HUAN");
        sLastNamePolyPhoneMap.put((char) 24055, "XIANG");
        sLastNamePolyPhoneMap.put((char) 25750, "HAN");
        sLastNamePolyPhoneMap.put((char) 35265, "JIAN");
        sLastNamePolyPhoneMap.put((char) 38477, "JIANG");
        sLastNamePolyPhoneMap.put((char) 35282, "JIAO");
        sLastNamePolyPhoneMap.put((char) 32564, "JIAO");
        sLastNamePolyPhoneMap.put((char) 35760, "JI");
        sLastNamePolyPhoneMap.put((char) 29722, "JU");
        sLastNamePolyPhoneMap.put((char) 21095, "JI");
        sLastNamePolyPhoneMap.put((char) 38589, "JUAN");
        sLastNamePolyPhoneMap.put((char) 38551, "KUI");
        sLastNamePolyPhoneMap.put((char) 38752, "KU");
        sLastNamePolyPhoneMap.put((char) 20048, "YUE");
        sLastNamePolyPhoneMap.put((char) 21895, "LA");
        sLastNamePolyPhoneMap.put((char) 38610, "LUO");
        sLastNamePolyPhoneMap.put((char) 20102, "LIAO");
        sLastNamePolyPhoneMap.put((char) 32554, "MIAO");
        sLastNamePolyPhoneMap.put((char) 20340, "MI");
        sLastNamePolyPhoneMap.put((char) 35884, "MIAO");
        sLastNamePolyPhoneMap.put((char) 20060, "NIE");
        sLastNamePolyPhoneMap.put((char) 36898, "PANG");
        sLastNamePolyPhoneMap.put((char) 34028, "PENG");
        sLastNamePolyPhoneMap.put((char) 26420, "PIAO");
        sLastNamePolyPhoneMap.put((char) 20167, "QIU");
        sLastNamePolyPhoneMap.put((char) 35203, "QIN");
        sLastNamePolyPhoneMap.put((char) 30655, "QU");
        sLastNamePolyPhoneMap.put((char) 20160, "SHI");
        sLastNamePolyPhoneMap.put((char) 25240, "SHE");
        sLastNamePolyPhoneMap.put((char) 30509, "SUI");
        sLastNamePolyPhoneMap.put((char) 35299, "XIE");
        sLastNamePolyPhoneMap.put((char) 31995, "XI");
        sLastNamePolyPhoneMap.put((char) 38500, "XU");
        sLastNamePolyPhoneMap.put((char) 21592, "YUAN");
        sLastNamePolyPhoneMap.put((char) 36128, "YUAN");
        sLastNamePolyPhoneMap.put((char) 26366, "ZENG");
        sLastNamePolyPhoneMap.put((char) 26597, "ZHA");
        sLastNamePolyPhoneMap.put((char) 20256, "CHUAN");
        sLastNamePolyPhoneMap.put((char) 21484, "SHAO");
        sLastNamePolyPhoneMap.put((char) 37325, "chong");
        sLastNamePolyPhoneMap.put((char) 21306, "OU");
        sLastNamePolyPhoneMap.put((char) 26044, "YU");
        sLastNamePolyPhoneMap.put((char) 31181, "CHONG");
    }

    private ChinesePinyinConverter(Context context) {
        this.mDictionary = new ChinesePinyinDictionary(context);
    }

    private void addToken(StringBuilder sb, ArrayList<Token> arrayList, int i) {
        String sb2 = sb.toString();
        Token token = new Token(i, sb2, sb2);
        if (4 == i) {
            String[] hindiToPinyins = HindiPinyinConverter.getInstance().hindiToPinyins(token.source);
            if (hindiToPinyins.length > 0) {
                if (hindiToPinyins.length == 1) {
                    String str = hindiToPinyins[0];
                    token.target = str;
                    token.polyPhones = new String[]{str};
                } else {
                    token.target = hindiToPinyins[0];
                    token.polyPhones = hindiToPinyins;
                }
            }
        }
        arrayList.add(token);
        sb.setLength(0);
    }

    public static ChinesePinyinConverter getInstance(Context context) {
        return INSTANCE.get(context);
    }

    private ArrayList<Token> getPolyPhoneLastNameTokens(String str) {
        String substring;
        String[] strArr;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        ArrayList<Token> arrayList = new ArrayList<>();
        if (str.length() >= 2 && (strArr = sHyphenatedNamePolyPhoneMap.get((substring = str.substring(0, 2)))) != null) {
            for (int i = 0; i < strArr.length; i++) {
                Token token = new Token();
                token.type = 2;
                token.source = String.valueOf(substring.charAt(i));
                token.target = strArr[i];
                arrayList.add(token);
            }
            return arrayList;
        }
        Character valueOf = Character.valueOf(str.charAt(0));
        String str2 = sLastNamePolyPhoneMap.get(valueOf);
        if (str2 != null) {
            Token token2 = new Token();
            token2.type = 2;
            token2.source = valueOf.toString();
            token2.target = str2;
            arrayList.add(token2);
            return arrayList;
        }
        return null;
    }

    public ArrayList<Token> get(String str) {
        return get(str, true, true);
    }

    public ArrayList<Token> get(String str, boolean z, boolean z2) {
        int i;
        ArrayList<Token> polyPhoneLastNameTokens;
        ArrayList<Token> arrayList = new ArrayList<>();
        if (TextUtils.isEmpty(str)) {
            return arrayList;
        }
        if (z2 || (polyPhoneLastNameTokens = getPolyPhoneLastNameTokens(str)) == null || polyPhoneLastNameTokens.size() <= 0) {
            i = 0;
        } else {
            arrayList.addAll(polyPhoneLastNameTokens);
            i = polyPhoneLastNameTokens.size();
        }
        int length = str.length();
        StringBuilder sb = new StringBuilder();
        int i2 = 1;
        while (i < length) {
            char charAt = str.charAt(i);
            int i3 = 2;
            if (charAt == ' ') {
                if (sb.length() > 0) {
                    addToken(sb, arrayList, i2);
                }
                if (!z) {
                    String valueOf = String.valueOf(' ');
                    arrayList.add(new Token(3, valueOf, valueOf));
                }
            } else {
                if (charAt < 256) {
                    if (i > 0) {
                        char charAt2 = str.charAt(i - 1);
                        if ((charAt2 >= '0' && charAt2 <= '9') != (charAt >= '0' && charAt <= '9') && sb.length() > 0) {
                            addToken(sb, arrayList, i2);
                        }
                    }
                    if (i2 != 1 && sb.length() > 0) {
                        addToken(sb, arrayList, i2);
                    }
                    sb.append(charAt);
                    i2 = 1;
                } else {
                    if (charAt == 12295) {
                        Token token = new Token();
                        token.type = 2;
                        token.target = "ling";
                        if (sb.length() > 0) {
                            addToken(sb, arrayList, i2);
                        }
                        arrayList.add(token);
                    } else if (charAt >= 19968 && charAt <= 40869) {
                        String[] pinyinString = this.mDictionary.getPinyinString(charAt);
                        Token token2 = new Token();
                        token2.source = Character.toString(charAt);
                        if (pinyinString == null) {
                            token2.type = 3;
                            token2.target = Character.toString(charAt);
                        } else {
                            token2.type = 2;
                            token2.target = pinyinString[0];
                            if (pinyinString.length > 1) {
                                token2.polyPhones = pinyinString;
                            }
                        }
                        int i4 = token2.type;
                        if (i4 == 2) {
                            if (sb.length() > 0) {
                                addToken(sb, arrayList, i2);
                            }
                            arrayList.add(token2);
                        } else {
                            if (i2 != i4 && sb.length() > 0) {
                                addToken(sb, arrayList, i2);
                            }
                            i3 = token2.type;
                            sb.append(charAt);
                        }
                    } else if (charAt < 2304 || charAt > 2431) {
                        if (i2 != 3 && sb.length() > 0) {
                            addToken(sb, arrayList, i2);
                        }
                        sb.append(charAt);
                    } else {
                        if (i2 != 4 && sb.length() > 0) {
                            addToken(sb, arrayList, i2);
                        }
                        sb.append(charAt);
                        i2 = 4;
                    }
                    i2 = i3;
                }
                i++;
            }
            i2 = 3;
            i++;
        }
        if (sb.length() > 0) {
            addToken(sb, arrayList, i2);
        }
        return arrayList;
    }
}
