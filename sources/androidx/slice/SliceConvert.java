package androidx.slice;

import android.app.slice.Slice;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import androidx.collection.ArraySet;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import java.util.Iterator;
import java.util.Set;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class SliceConvert {
    public static android.app.slice.Slice unwrap(Slice slice) {
        if (slice == null || slice.getUri() == null) {
            return null;
        }
        Slice.Builder builder = new Slice.Builder(slice.getUri(), unwrap(slice.getSpec()));
        builder.addHints(slice.getHints());
        for (SliceItem sliceItem : slice.getItemArray()) {
            String format = sliceItem.getFormat();
            format.hashCode();
            char c = 65535;
            switch (format.hashCode()) {
                case -1422950858:
                    if (format.equals("action")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1377881982:
                    if (format.equals("bundle")) {
                        c = 1;
                        break;
                    }
                    break;
                case 104431:
                    if (format.equals("int")) {
                        c = 2;
                        break;
                    }
                    break;
                case 3327612:
                    if (format.equals("long")) {
                        c = 3;
                        break;
                    }
                    break;
                case 3556653:
                    if (format.equals("text")) {
                        c = 4;
                        break;
                    }
                    break;
                case 100313435:
                    if (format.equals(YellowPageContract.ImageLookup.DIRECTORY_IMAGE)) {
                        c = 5;
                        break;
                    }
                    break;
                case 100358090:
                    if (format.equals("input")) {
                        c = 6;
                        break;
                    }
                    break;
                case 109526418:
                    if (format.equals("slice")) {
                        c = 7;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    builder.addAction(sliceItem.getAction(), unwrap(sliceItem.getSlice()), sliceItem.getSubType());
                    break;
                case 1:
                    builder.addBundle((Bundle) sliceItem.mObj, sliceItem.getSubType(), sliceItem.getHints());
                    break;
                case 2:
                    builder.addInt(sliceItem.getInt(), sliceItem.getSubType(), sliceItem.getHints());
                    break;
                case 3:
                    builder.addLong(sliceItem.getLong(), sliceItem.getSubType(), sliceItem.getHints());
                    break;
                case 4:
                    builder.addText(sliceItem.getText(), sliceItem.getSubType(), sliceItem.getHints());
                    break;
                case 5:
                    builder.addIcon(sliceItem.getIcon().toIcon(), sliceItem.getSubType(), sliceItem.getHints());
                    break;
                case 6:
                    builder.addRemoteInput(sliceItem.getRemoteInput(), sliceItem.getSubType(), sliceItem.getHints());
                    break;
                case 7:
                    builder.addSubSlice(unwrap(sliceItem.getSlice()), sliceItem.getSubType());
                    break;
            }
        }
        return builder.build();
    }

    private static android.app.slice.SliceSpec unwrap(SliceSpec spec) {
        if (spec == null) {
            return null;
        }
        return new android.app.slice.SliceSpec(spec.getType(), spec.getRevision());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Set<android.app.slice.SliceSpec> unwrap(Set<SliceSpec> supportedSpecs) {
        ArraySet arraySet = new ArraySet();
        if (supportedSpecs != null) {
            Iterator<SliceSpec> it = supportedSpecs.iterator();
            while (it.hasNext()) {
                arraySet.add(unwrap(it.next()));
            }
        }
        return arraySet;
    }

    public static Slice wrap(android.app.slice.Slice slice, Context context) {
        if (slice == null || slice.getUri() == null) {
            return null;
        }
        Slice.Builder builder = new Slice.Builder(slice.getUri());
        builder.addHints(slice.getHints());
        builder.setSpec(wrap(slice.getSpec()));
        for (android.app.slice.SliceItem sliceItem : slice.getItems()) {
            String format = sliceItem.getFormat();
            format.hashCode();
            char c = 65535;
            switch (format.hashCode()) {
                case -1422950858:
                    if (format.equals("action")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1377881982:
                    if (format.equals("bundle")) {
                        c = 1;
                        break;
                    }
                    break;
                case 104431:
                    if (format.equals("int")) {
                        c = 2;
                        break;
                    }
                    break;
                case 3327612:
                    if (format.equals("long")) {
                        c = 3;
                        break;
                    }
                    break;
                case 3556653:
                    if (format.equals("text")) {
                        c = 4;
                        break;
                    }
                    break;
                case 100313435:
                    if (format.equals(YellowPageContract.ImageLookup.DIRECTORY_IMAGE)) {
                        c = 5;
                        break;
                    }
                    break;
                case 100358090:
                    if (format.equals("input")) {
                        c = 6;
                        break;
                    }
                    break;
                case 109526418:
                    if (format.equals("slice")) {
                        c = 7;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    builder.addAction(sliceItem.getAction(), wrap(sliceItem.getSlice(), context), sliceItem.getSubType());
                    break;
                case 1:
                    builder.addItem(new SliceItem(sliceItem.getBundle(), sliceItem.getFormat(), sliceItem.getSubType(), sliceItem.getHints()));
                    break;
                case 2:
                    builder.addInt(sliceItem.getInt(), sliceItem.getSubType(), sliceItem.getHints());
                    break;
                case 3:
                    builder.addLong(sliceItem.getLong(), sliceItem.getSubType(), sliceItem.getHints());
                    break;
                case 4:
                    builder.addText(sliceItem.getText(), sliceItem.getSubType(), sliceItem.getHints());
                    break;
                case 5:
                    try {
                        builder.addIcon(IconCompat.createFromIcon(context, sliceItem.getIcon()), sliceItem.getSubType(), sliceItem.getHints());
                        break;
                    } catch (Resources.NotFoundException e) {
                        Log.w("SliceConvert", "The icon resource isn't available.", e);
                        break;
                    } catch (IllegalArgumentException e2) {
                        Log.w("SliceConvert", "The icon resource isn't available.", e2);
                        break;
                    }
                case 6:
                    builder.addRemoteInput(sliceItem.getRemoteInput(), sliceItem.getSubType(), sliceItem.getHints());
                    break;
                case 7:
                    builder.addSubSlice(wrap(sliceItem.getSlice(), context), sliceItem.getSubType());
                    break;
            }
        }
        return builder.build();
    }

    private static SliceSpec wrap(android.app.slice.SliceSpec spec) {
        if (spec == null) {
            return null;
        }
        return new SliceSpec(spec.getType(), spec.getRevision());
    }

    public static Set<SliceSpec> wrap(Set<android.app.slice.SliceSpec> supportedSpecs) {
        ArraySet arraySet = new ArraySet();
        if (supportedSpecs != null) {
            Iterator<android.app.slice.SliceSpec> it = supportedSpecs.iterator();
            while (it.hasNext()) {
                arraySet.add(wrap(it.next()));
            }
        }
        return arraySet;
    }
}
