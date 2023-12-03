package androidx.slice;

import android.net.Uri;
import java.util.Iterator;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class SliceStructure {
    private final String mStructure;
    private final Uri mUri;

    public SliceStructure(Slice s) {
        StringBuilder sb = new StringBuilder();
        getStructure(s, sb);
        this.mStructure = sb.toString();
        this.mUri = s.getUri();
    }

    public SliceStructure(SliceItem s) {
        StringBuilder sb = new StringBuilder();
        getStructure(s, sb);
        this.mStructure = sb.toString();
        if ("action".equals(s.getFormat()) || "slice".equals(s.getFormat())) {
            this.mUri = s.getSlice().getUri();
        } else {
            this.mUri = null;
        }
    }

    private static void getStructure(Slice s, StringBuilder str) {
        str.append("s{");
        Iterator<SliceItem> it = s.getItems().iterator();
        while (it.hasNext()) {
            getStructure(it.next(), str);
        }
        str.append("}");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static void getStructure(SliceItem item, StringBuilder str) {
        char c;
        String format = item.getFormat();
        switch (format.hashCode()) {
            case -1422950858:
                if (format.equals("action")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -1377881982:
                if (format.equals("bundle")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 104431:
                if (format.equals("int")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 3327612:
                if (format.equals("long")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 3556653:
                if (format.equals("text")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 100313435:
                if (format.equals(YellowPageContract.ImageLookup.DIRECTORY_IMAGE)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 100358090:
                if (format.equals("input")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 109526418:
                if (format.equals("slice")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            getStructure(item.getSlice(), str);
        } else if (c == 1) {
            str.append('a');
            if ("range".equals(item.getSubType())) {
                str.append('r');
            }
            getStructure(item.getSlice(), str);
        } else if (c == 2) {
            str.append('t');
        } else if (c != 3) {
        } else {
            str.append('i');
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof SliceStructure) {
            return this.mStructure.equals(((SliceStructure) obj).mStructure);
        }
        return false;
    }

    public Uri getUri() {
        return this.mUri;
    }

    public int hashCode() {
        return this.mStructure.hashCode();
    }
}
