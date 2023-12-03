package com.googlecode.tesseract.android;

import android.util.Log;
import android.util.Pair;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class ResultIterator extends PageIterator {
    private final long mNativeResultIterator;

    static {
        System.loadLibrary("lept");
        System.loadLibrary("tess");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResultIterator(long j) {
        super(j);
        this.mNativeResultIterator = j;
    }

    private static native float nativeConfidence(long j, int i);

    private static native void nativeDelete(long j);

    private static native String[] nativeGetSymbolChoices(long j);

    private static native String nativeGetUTF8Text(long j, int i);

    private static native boolean nativeIsAtBeginningOf(long j, int i);

    private static native boolean nativeIsAtFinalElement(long j, int i, int i2);

    public float confidence(int i) {
        return nativeConfidence(this.mNativeResultIterator, i);
    }

    public void delete() {
        nativeDelete(this.mNativeResultIterator);
    }

    public List<Pair<String, Double>> getSymbolChoicesAndConfidence() {
        String[] nativeGetSymbolChoices = nativeGetSymbolChoices(this.mNativeResultIterator);
        ArrayList arrayList = new ArrayList();
        for (String str : nativeGetSymbolChoices) {
            int lastIndexOf = str.lastIndexOf(124);
            Double valueOf = Double.valueOf(0.0d);
            if (lastIndexOf > 0) {
                String substring = str.substring(0, lastIndexOf);
                try {
                    valueOf = Double.valueOf(Double.parseDouble(str.substring(lastIndexOf + 1)));
                } catch (NumberFormatException unused) {
                    Log.e("ResultIterator", "Invalid confidence level for " + str);
                }
                str = substring;
            }
            arrayList.add(new Pair(str, valueOf));
        }
        return arrayList;
    }

    public String getUTF8Text(int i) {
        return nativeGetUTF8Text(this.mNativeResultIterator, i);
    }

    public boolean isAtBeginningOf(int i) {
        return nativeIsAtBeginningOf(this.mNativeResultIterator, i);
    }

    public boolean isAtFinalElement(int i, int i2) {
        return nativeIsAtFinalElement(this.mNativeResultIterator, i, i2);
    }
}
