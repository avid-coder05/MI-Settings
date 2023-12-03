package com.miui.maml.elements;

import android.text.Html;
import android.text.TextUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.regex.Pattern;
import miui.provider.ExtraContacts;
import miui.vip.VipService;

/* loaded from: classes2.dex */
public class MusicLyricParser {
    private static final Pattern TAG_EXTRA_LRC = Pattern.compile("<[0-9]{0,2}:[0-9]{0,2}:[0-9]{0,2}>");

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class EntityCompator implements Comparator<LyricEntity> {
        EntityCompator() {
        }

        @Override // java.util.Comparator
        public int compare(LyricEntity lyricEntity, LyricEntity lyricEntity2) {
            return lyricEntity.time - lyricEntity2.time;
        }
    }

    /* loaded from: classes2.dex */
    public static class Lyric {
        private final LyricEntity EMPTY_AFTER;
        private final ArrayList<LyricEntity> mEntityList;
        private final LyricHeader mHeader;
        private boolean mIsModified;
        private int mOriginHeaderOffset;
        private LyricLocator mLyricLocator = new LyricLocator();
        private final long mOpenTime = System.currentTimeMillis();
        private final LyricEntity EMPTY_BEFORE = new LyricEntity(-1, "\n");

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes2.dex */
        public class LyricLine {
            CharSequence lyric;
            int pos;

            LyricLine() {
            }
        }

        /* loaded from: classes2.dex */
        class LyricLocator {
            final int CRLF_LENGTH = 2;
            String mFullLyric;
            ArrayList<LyricLine> mLyricLines;
            int[] mTimeArr;

            LyricLocator() {
            }

            private int getLineNumber(long j) {
                int i = 0;
                while (true) {
                    int[] iArr = this.mTimeArr;
                    if (i >= iArr.length) {
                        return -1;
                    }
                    if (j >= iArr[i]) {
                        if (j < (i < iArr.length + (-1) ? iArr[i + 1] : Long.MAX_VALUE)) {
                            return i;
                        }
                    }
                    i++;
                }
            }

            private void inflateLyricLines(ArrayList<CharSequence> arrayList) {
                int[] iArr = this.mTimeArr;
                if (iArr == null || arrayList == null || iArr.length != arrayList.size()) {
                    this.mTimeArr = null;
                    this.mLyricLines = null;
                    return;
                }
                this.mLyricLines = new ArrayList<>();
                int i = 0;
                while (i < this.mTimeArr.length) {
                    CharSequence charSequence = arrayList.get(i);
                    LyricLine lyricLine = new LyricLine();
                    lyricLine.lyric = charSequence;
                    LyricLine lyricLine2 = i > 0 ? this.mLyricLines.get(i - 1) : null;
                    lyricLine.pos = lyricLine2 != null ? lyricLine2.pos + lyricLine2.lyric.length() + this.CRLF_LENGTH : 0;
                    this.mLyricLines.add(lyricLine);
                    i++;
                }
                this.mFullLyric = "";
                for (int i2 = 0; i2 < this.mLyricLines.size(); i2++) {
                    this.mFullLyric += ((Object) this.mLyricLines.get(i2).lyric) + "\r\n";
                }
            }

            String getAfterLines(long j) {
                if (this.mTimeArr == null) {
                    return null;
                }
                int lineNumber = getLineNumber(j);
                if (lineNumber < 0) {
                    return this.mFullLyric;
                }
                if (lineNumber < this.mTimeArr.length - 1) {
                    LyricLine lyricLine = this.mLyricLines.get(lineNumber);
                    return this.mFullLyric.substring(lyricLine.pos + lyricLine.lyric.length() + this.CRLF_LENGTH, this.mFullLyric.length());
                }
                return null;
            }

            String getBeforeLines(long j) {
                int lineNumber;
                if (this.mTimeArr != null && (lineNumber = getLineNumber(j)) > 0) {
                    return this.mFullLyric.substring(0, this.mLyricLines.get(lineNumber).pos - this.CRLF_LENGTH);
                }
                return null;
            }

            String getLastLine(long j) {
                int lineNumber;
                if (this.mTimeArr != null && (lineNumber = getLineNumber(j)) > 0) {
                    LyricLine lyricLine = this.mLyricLines.get(lineNumber - 1);
                    String str = this.mFullLyric;
                    int i = lyricLine.pos;
                    return str.substring(i, lyricLine.lyric.length() + i);
                }
                return null;
            }

            String getLine(long j) {
                int lineNumber;
                if (this.mTimeArr == null || (lineNumber = getLineNumber(j)) == -1) {
                    return null;
                }
                LyricLine lyricLine = this.mLyricLines.get(lineNumber);
                String str = this.mFullLyric;
                int i = lyricLine.pos;
                return str.substring(i, lyricLine.lyric.length() + i);
            }

            String getNextLine(long j) {
                int lineNumber;
                if (this.mTimeArr != null && (lineNumber = getLineNumber(j)) >= -1 && lineNumber < this.mTimeArr.length - 1) {
                    LyricLine lyricLine = this.mLyricLines.get(lineNumber + 1);
                    String str = this.mFullLyric;
                    int i = lyricLine.pos;
                    return str.substring(i, lyricLine.lyric.length() + i);
                }
                return null;
            }

            void set(int[] iArr, ArrayList<CharSequence> arrayList) {
                this.mTimeArr = iArr;
                inflateLyricLines(arrayList);
            }
        }

        public Lyric(LyricHeader lyricHeader, ArrayList<LyricEntity> arrayList, boolean z) {
            this.mHeader = lyricHeader;
            this.mOriginHeaderOffset = lyricHeader.offset;
            this.mEntityList = arrayList;
            this.mIsModified = z;
            this.EMPTY_AFTER = new LyricEntity(arrayList.size(), "\n");
        }

        public void decorate() {
            ArrayList<LyricEntity> arrayList;
            int size;
            if (!this.mEntityList.isEmpty() && (size = (arrayList = this.mEntityList).size()) > 0) {
                if (arrayList.get(0).isDecorated()) {
                    return;
                }
                for (int i = 0; i < size; i++) {
                    arrayList.get(i).decorate();
                }
            }
        }

        public String getAfterLines(long j) {
            return this.mLyricLocator.getAfterLines(j);
        }

        public String getBeforeLines(long j) {
            return this.mLyricLocator.getBeforeLines(j);
        }

        public String getLastLine(long j) {
            return this.mLyricLocator.getLastLine(j);
        }

        public String getLine(long j) {
            return this.mLyricLocator.getLine(j);
        }

        public LyricShot getLyricShot(long j) {
            int i = this.mHeader.offset;
            if (this.mEntityList.get(0).time + i > j) {
                return new LyricShot(0, 0.0d);
            }
            for (int i2 = 1; i2 < this.mEntityList.size(); i2++) {
                int i3 = this.mEntityList.get(i2).time + i;
                if (i3 > j) {
                    int i4 = i2 - 1;
                    return new LyricShot(i4, i3 > this.mEntityList.get(i4).time + i ? (j - r8) / (i3 - r8) : 0.0d);
                }
            }
            long j2 = j - (this.mEntityList.get(size() - 1).time + i);
            if (j2 < 8000) {
                return new LyricShot(size() - 1, j2 / 8000.0d);
            }
            return new LyricShot(this.mEntityList.size(), 0.0d);
        }

        public String getNextLine(long j) {
            return this.mLyricLocator.getNextLine(j);
        }

        public ArrayList<CharSequence> getStringArr() {
            if (this.mEntityList.isEmpty()) {
                return null;
            }
            ArrayList<CharSequence> arrayList = new ArrayList<>(this.mEntityList.size());
            Iterator<LyricEntity> it = this.mEntityList.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next().lyric);
            }
            return arrayList;
        }

        public int[] getTimeArr() {
            if (this.mEntityList.isEmpty()) {
                return null;
            }
            int[] iArr = new int[this.mEntityList.size()];
            int i = 0;
            Iterator<LyricEntity> it = this.mEntityList.iterator();
            while (it.hasNext()) {
                iArr[i] = it.next().time + this.mHeader.offset;
                i++;
            }
            return iArr;
        }

        public void set(int[] iArr, ArrayList<CharSequence> arrayList) {
            this.mLyricLocator.set(iArr, arrayList);
        }

        public int size() {
            return this.mEntityList.size();
        }
    }

    /* loaded from: classes2.dex */
    public static class LyricEntity {
        public CharSequence lyric;
        public int time;

        public LyricEntity(int i, String str) {
            this.time = i;
            this.lyric = str;
        }

        public void decorate() {
            this.lyric = Html.fromHtml(String.format("%s<br/>", this.lyric));
        }

        public boolean isDecorated() {
            return !(this.lyric instanceof String);
        }
    }

    /* loaded from: classes2.dex */
    public static class LyricHeader {
        public String album;
        public String artist;
        public String editor;
        public int offset;
        public String title;
        public String version;
    }

    /* loaded from: classes2.dex */
    public static class LyricShot {
        public int lineIndex;
        public double percent;

        public LyricShot(int i, double d) {
            this.lineIndex = i;
            this.percent = d;
        }
    }

    private static void correctTime(Lyric lyric) {
        if (lyric == null) {
            return;
        }
        ArrayList arrayList = lyric.mEntityList;
        int size = arrayList.size();
        if (size > 1 && ((LyricEntity) arrayList.get(0)).time == ((LyricEntity) arrayList.get(1)).time) {
            ((LyricEntity) arrayList.get(0)).time = ((LyricEntity) arrayList.get(1)).time / 2;
        }
        int i = 1;
        while (i < size - 1) {
            int i2 = i + 1;
            if (((LyricEntity) arrayList.get(i)).time == ((LyricEntity) arrayList.get(i2)).time) {
                ((LyricEntity) arrayList.get(i)).time = (((LyricEntity) arrayList.get(i - 1)).time + ((LyricEntity) arrayList.get(i2)).time) / 2;
            }
            i = i2;
        }
    }

    private static Lyric doParse(String str) throws IOException {
        LyricHeader lyricHeader = new LyricHeader();
        ArrayList arrayList = new ArrayList();
        String[] split = str.split("\r\n");
        boolean z = false;
        if (split != null) {
            boolean z2 = false;
            for (String str2 : split) {
                int parseLine = parseLine(str2, lyricHeader, arrayList);
                if (parseLine == 0) {
                    break;
                }
                if (parseLine == 1) {
                    z2 = true;
                }
            }
            z = z2;
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        Collections.sort(arrayList, new EntityCompator());
        return new Lyric(lyricHeader, arrayList, z);
    }

    private static int parseEntity(String[] strArr, ArrayList<LyricEntity> arrayList, String str) {
        try {
            int parseDouble = (int) (Double.parseDouble(strArr[strArr.length - 1]) * 1000.0d);
            int i = 0;
            int i2 = 60;
            for (int length = strArr.length - 2; length >= 0; length--) {
                int parseInt = Integer.parseInt(strArr[length]) * i2;
                i2 *= 60;
                i += parseInt;
            }
            int i3 = parseDouble + (i * VipService.VIP_SERVICE_FAILURE);
            if (i3 < 18000000) {
                arrayList.add(new LyricEntity(i3, str));
            }
            return 2;
        } catch (NumberFormatException unused) {
            return 1;
        }
    }

    private static int parseHeader(String str, LyricHeader lyricHeader) {
        int indexOf = str.indexOf(":");
        if (indexOf < 0 || indexOf >= str.length() - 1) {
            return 1;
        }
        String substring = str.substring(0, indexOf);
        String substring2 = str.substring(indexOf + 1);
        if (substring.equals("al")) {
            lyricHeader.album = substring2;
        } else if (substring.equals("ar")) {
            lyricHeader.artist = substring2;
        } else if (substring.equals("ti")) {
            lyricHeader.title = substring2;
        } else if (substring.equals("by")) {
            lyricHeader.editor = substring2;
        } else if (substring.equals("ve")) {
            lyricHeader.version = substring2;
        } else if (!substring.equals(ExtraContacts.ConferenceCalls.OFFSET_PARAM_KEY)) {
            return 1;
        } else {
            try {
                lyricHeader.offset = Integer.parseInt(substring2);
            } catch (NumberFormatException unused) {
                return 1;
            }
        }
        return 2;
    }

    private static int parseLine(String str, LyricHeader lyricHeader, ArrayList<LyricEntity> arrayList) {
        String replaceAll;
        int lastIndexOf;
        String trim = str.trim();
        if (TextUtils.isEmpty(trim) || (lastIndexOf = (replaceAll = TAG_EXTRA_LRC.matcher(trim).replaceAll("")).lastIndexOf("]")) == -1) {
            return 1;
        }
        String substring = replaceAll.substring(lastIndexOf + 1);
        int indexOf = replaceAll.indexOf("[");
        if (indexOf == -1) {
            return 1;
        }
        String[] split = replaceAll.substring(indexOf, lastIndexOf).split("]");
        int i = 2;
        for (String str2 : split) {
            if (str2.startsWith("[")) {
                String substring2 = str2.substring(1);
                String[] split2 = substring2.split(":");
                if (split2.length >= 2) {
                    i = TextUtils.isDigitsOnly(split2[0]) ? parseEntity(split2, arrayList, substring) : parseHeader(substring2, lyricHeader);
                }
            }
        }
        return i;
    }

    public static Lyric parseLyric(String str) {
        Lyric lyric = null;
        if (str != null) {
            try {
                lyric = doParse(str);
                correctTime(lyric);
                return lyric;
            } catch (Exception e) {
                e.printStackTrace();
                return lyric;
            }
        }
        return null;
    }
}
