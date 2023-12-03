package androidx.slice;

import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.util.Pair;
import androidx.versionedparcelable.CustomVersionedParcelable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public final class SliceItem extends CustomVersionedParcelable {
    String mFormat;
    protected String[] mHints;
    SliceItemHolder mHolder;
    Object mObj;
    CharSequence mSanitizedText;
    String mSubType;

    /* loaded from: classes.dex */
    public interface ActionHandler {
        void onAction(SliceItem item, Context context, Intent intent);
    }

    public SliceItem() {
        this.mHints = Slice.NO_HINTS;
        this.mFormat = "text";
        this.mSubType = null;
    }

    public SliceItem(PendingIntent intent, Slice slice, String format, String subType, String[] hints) {
        this(new Pair(intent, slice), format, subType, hints);
    }

    public SliceItem(Bundle in) {
        this.mHints = Slice.NO_HINTS;
        this.mFormat = "text";
        this.mSubType = null;
        this.mHints = in.getStringArray("hints");
        this.mFormat = in.getString("format");
        this.mSubType = in.getString("subtype");
        this.mObj = readObj(this.mFormat, in);
    }

    public SliceItem(Object obj, String format, String subType, List<String> hints) {
        this(obj, format, subType, (String[]) hints.toArray(new String[hints.size()]));
    }

    public SliceItem(Object obj, String format, String subType, String[] hints) {
        this.mHints = Slice.NO_HINTS;
        this.mFormat = "text";
        this.mSubType = null;
        this.mHints = hints;
        this.mFormat = format;
        this.mSubType = subType;
        this.mObj = obj;
    }

    private static boolean checkSpan(Object span) {
        return (span instanceof AlignmentSpan) || (span instanceof ForegroundColorSpan) || (span instanceof RelativeSizeSpan) || (span instanceof StyleSpan);
    }

    private static boolean checkSpannedText(Spanned text) {
        for (Object obj : text.getSpans(0, text.length(), Object.class)) {
            if (!checkSpan(obj)) {
                return false;
            }
        }
        return true;
    }

    private static Object fixSpan(Object span) {
        if (checkSpan(span)) {
            return span;
        }
        return null;
    }

    private static void fixSpannableText(Spannable text) {
        for (Object obj : text.getSpans(0, text.length(), Object.class)) {
            Object fixSpan = fixSpan(obj);
            if (fixSpan != obj) {
                if (fixSpan != null) {
                    text.setSpan(fixSpan, text.getSpanStart(obj), text.getSpanEnd(obj), text.getSpanFlags(obj));
                }
                text.removeSpan(obj);
            }
        }
    }

    private static String layoutDirectionToString(int layoutDirection) {
        return layoutDirection != 0 ? layoutDirection != 1 ? layoutDirection != 2 ? layoutDirection != 3 ? Integer.toString(layoutDirection) : "LOCALE" : "INHERIT" : "RTL" : "LTR";
    }

    private static Object readObj(String type, Bundle in) {
        type.hashCode();
        char c = 65535;
        switch (type.hashCode()) {
            case -1422950858:
                if (type.equals("action")) {
                    c = 0;
                    break;
                }
                break;
            case -1377881982:
                if (type.equals("bundle")) {
                    c = 1;
                    break;
                }
                break;
            case 104431:
                if (type.equals("int")) {
                    c = 2;
                    break;
                }
                break;
            case 3327612:
                if (type.equals("long")) {
                    c = 3;
                    break;
                }
                break;
            case 3556653:
                if (type.equals("text")) {
                    c = 4;
                    break;
                }
                break;
            case 100313435:
                if (type.equals(YellowPageContract.ImageLookup.DIRECTORY_IMAGE)) {
                    c = 5;
                    break;
                }
                break;
            case 100358090:
                if (type.equals("input")) {
                    c = 6;
                    break;
                }
                break;
            case 109526418:
                if (type.equals("slice")) {
                    c = 7;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return new Pair(in.getParcelable("obj"), new Slice(in.getBundle("obj_2")));
            case 1:
                return in.getBundle("obj");
            case 2:
                return Integer.valueOf(in.getInt("obj"));
            case 3:
                return Long.valueOf(in.getLong("obj"));
            case 4:
                return in.getCharSequence("obj");
            case 5:
                return IconCompat.createFromBundle(in.getBundle("obj"));
            case 6:
                return in.getParcelable("obj");
            case 7:
                return new Slice(in.getBundle("obj"));
            default:
                throw new RuntimeException("Unsupported type " + type);
        }
    }

    private static CharSequence sanitizeText(CharSequence text) {
        if (text instanceof Spannable) {
            fixSpannableText((Spannable) text);
            return text;
        } else if (!(text instanceof Spanned) || checkSpannedText((Spanned) text)) {
            return text;
        } else {
            SpannableString spannableString = new SpannableString(text);
            fixSpannableText(spannableString);
            return spannableString;
        }
    }

    public static String typeToString(String format) {
        format.hashCode();
        char c = 65535;
        switch (format.hashCode()) {
            case -1422950858:
                if (format.equals("action")) {
                    c = 0;
                    break;
                }
                break;
            case 104431:
                if (format.equals("int")) {
                    c = 1;
                    break;
                }
                break;
            case 3327612:
                if (format.equals("long")) {
                    c = 2;
                    break;
                }
                break;
            case 3556653:
                if (format.equals("text")) {
                    c = 3;
                    break;
                }
                break;
            case 100313435:
                if (format.equals(YellowPageContract.ImageLookup.DIRECTORY_IMAGE)) {
                    c = 4;
                    break;
                }
                break;
            case 100358090:
                if (format.equals("input")) {
                    c = 5;
                    break;
                }
                break;
            case 109526418:
                if (format.equals("slice")) {
                    c = 6;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return "Action";
            case 1:
                return "Int";
            case 2:
                return "Long";
            case 3:
                return "Text";
            case 4:
                return "Image";
            case 5:
                return "RemoteInput";
            case 6:
                return "Slice";
            default:
                return "Unrecognized format: " + format;
        }
    }

    private void writeObj(Bundle dest, Object obj, String type) {
        type.hashCode();
        char c = 65535;
        switch (type.hashCode()) {
            case -1422950858:
                if (type.equals("action")) {
                    c = 0;
                    break;
                }
                break;
            case -1377881982:
                if (type.equals("bundle")) {
                    c = 1;
                    break;
                }
                break;
            case 104431:
                if (type.equals("int")) {
                    c = 2;
                    break;
                }
                break;
            case 3327612:
                if (type.equals("long")) {
                    c = 3;
                    break;
                }
                break;
            case 3556653:
                if (type.equals("text")) {
                    c = 4;
                    break;
                }
                break;
            case 100313435:
                if (type.equals(YellowPageContract.ImageLookup.DIRECTORY_IMAGE)) {
                    c = 5;
                    break;
                }
                break;
            case 100358090:
                if (type.equals("input")) {
                    c = 6;
                    break;
                }
                break;
            case 109526418:
                if (type.equals("slice")) {
                    c = 7;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                Pair pair = (Pair) obj;
                dest.putParcelable("obj", (PendingIntent) pair.first);
                dest.putBundle("obj_2", ((Slice) pair.second).toBundle());
                return;
            case 1:
                dest.putBundle("obj", (Bundle) this.mObj);
                return;
            case 2:
                dest.putInt("obj", ((Integer) this.mObj).intValue());
                return;
            case 3:
                dest.putLong("obj", ((Long) this.mObj).longValue());
                return;
            case 4:
                dest.putCharSequence("obj", (CharSequence) obj);
                return;
            case 5:
                dest.putBundle("obj", ((IconCompat) obj).toBundle());
                return;
            case 6:
                dest.putParcelable("obj", (Parcelable) obj);
                return;
            case 7:
                dest.putParcelable("obj", ((Slice) obj).toBundle());
                return;
            default:
                return;
        }
    }

    public void addHint(String hint) {
        this.mHints = (String[]) ArrayUtils.appendElement(String.class, this.mHints, hint);
    }

    public void fireAction(Context context, Intent i) throws PendingIntent.CanceledException {
        fireActionInternal(context, i);
    }

    public boolean fireActionInternal(Context context, Intent i) throws PendingIntent.CanceledException {
        F f = ((Pair) this.mObj).first;
        if (f instanceof PendingIntent) {
            ((PendingIntent) f).send(context, 0, i, null, null);
            return false;
        }
        ((ActionHandler) f).onAction(this, context, i);
        return true;
    }

    public PendingIntent getAction() {
        F f = ((Pair) this.mObj).first;
        if (f instanceof PendingIntent) {
            return (PendingIntent) f;
        }
        return null;
    }

    public String getFormat() {
        return this.mFormat;
    }

    public List<String> getHints() {
        return Arrays.asList(this.mHints);
    }

    public IconCompat getIcon() {
        return (IconCompat) this.mObj;
    }

    public int getInt() {
        return ((Integer) this.mObj).intValue();
    }

    public long getLong() {
        return ((Long) this.mObj).longValue();
    }

    public RemoteInput getRemoteInput() {
        return (RemoteInput) this.mObj;
    }

    public CharSequence getSanitizedText() {
        if (this.mSanitizedText == null) {
            this.mSanitizedText = sanitizeText(getText());
        }
        return this.mSanitizedText;
    }

    public Slice getSlice() {
        return "action".equals(getFormat()) ? (Slice) ((Pair) this.mObj).second : (Slice) this.mObj;
    }

    public String getSubType() {
        return this.mSubType;
    }

    public CharSequence getText() {
        return (CharSequence) this.mObj;
    }

    public boolean hasAnyHints(String... hints) {
        if (hints == null) {
            return false;
        }
        for (String str : hints) {
            if (ArrayUtils.contains(this.mHints, str)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasHint(String hint) {
        return ArrayUtils.contains(this.mHints, hint);
    }

    public void onPostParceling() {
        SliceItemHolder sliceItemHolder = this.mHolder;
        if (sliceItemHolder != null) {
            this.mObj = sliceItemHolder.getObj(this.mFormat);
            this.mHolder.release();
        } else {
            this.mObj = null;
        }
        this.mHolder = null;
    }

    public void onPreParceling(boolean isStream) {
        this.mHolder = new SliceItemHolder(this.mFormat, this.mObj, isStream);
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putStringArray("hints", this.mHints);
        bundle.putString("format", this.mFormat);
        bundle.putString("subtype", this.mSubType);
        writeObj(bundle, this.mObj, this.mFormat);
        return bundle;
    }

    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent);
        sb.append(getFormat());
        if (getSubType() != null) {
            sb.append('<');
            sb.append(getSubType());
            sb.append('>');
        }
        sb.append(' ');
        String[] strArr = this.mHints;
        if (strArr.length > 0) {
            Slice.appendHints(sb, strArr);
            sb.append(' ');
        }
        String str = indent + "  ";
        String format = getFormat();
        format.hashCode();
        char c = 65535;
        switch (format.hashCode()) {
            case -1422950858:
                if (format.equals("action")) {
                    c = 0;
                    break;
                }
                break;
            case 104431:
                if (format.equals("int")) {
                    c = 1;
                    break;
                }
                break;
            case 3327612:
                if (format.equals("long")) {
                    c = 2;
                    break;
                }
                break;
            case 3556653:
                if (format.equals("text")) {
                    c = 3;
                    break;
                }
                break;
            case 100313435:
                if (format.equals(YellowPageContract.ImageLookup.DIRECTORY_IMAGE)) {
                    c = 4;
                    break;
                }
                break;
            case 109526418:
                if (format.equals("slice")) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                Object obj = ((Pair) this.mObj).first;
                sb.append('[');
                sb.append(obj);
                sb.append("] ");
                sb.append("{\n");
                sb.append(getSlice().toString(str));
                sb.append('\n');
                sb.append(indent);
                sb.append('}');
                break;
            case 1:
                if (!"color".equals(getSubType())) {
                    if (!"layout_direction".equals(getSubType())) {
                        sb.append(getInt());
                        break;
                    } else {
                        sb.append(layoutDirectionToString(getInt()));
                        break;
                    }
                } else {
                    int i = getInt();
                    sb.append(String.format("a=0x%02x r=0x%02x g=0x%02x b=0x%02x", Integer.valueOf(Color.alpha(i)), Integer.valueOf(Color.red(i)), Integer.valueOf(Color.green(i)), Integer.valueOf(Color.blue(i))));
                    break;
                }
            case 2:
                if (!"millis".equals(getSubType())) {
                    sb.append(getLong());
                    sb.append('L');
                    break;
                } else if (getLong() != -1) {
                    sb.append(DateUtils.getRelativeTimeSpanString(getLong(), Calendar.getInstance().getTimeInMillis(), 1000L, 262144));
                    break;
                } else {
                    sb.append("INFINITY");
                    break;
                }
            case 3:
                sb.append('\"');
                sb.append(getText());
                sb.append('\"');
                break;
            case 4:
                sb.append(getIcon());
                break;
            case 5:
                sb.append("{\n");
                sb.append(getSlice().toString(str));
                sb.append('\n');
                sb.append(indent);
                sb.append('}');
                break;
            default:
                sb.append(typeToString(getFormat()));
                break;
        }
        sb.append("\n");
        return sb.toString();
    }
}
