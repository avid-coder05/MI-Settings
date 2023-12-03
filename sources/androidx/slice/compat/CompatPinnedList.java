package androidx.slice.compat;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;
import androidx.collection.ArraySet;
import androidx.core.util.ObjectsCompat;
import androidx.slice.SliceSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public class CompatPinnedList {
    private final Context mContext;
    private final String mPrefsName;

    public CompatPinnedList(Context context, String prefsName) {
        this.mContext = context;
        this.mPrefsName = prefsName;
    }

    private static SliceSpec findSpec(Set<SliceSpec> specs, String type) {
        for (SliceSpec sliceSpec : specs) {
            if (ObjectsCompat.equals(sliceSpec.getType(), type)) {
                return sliceSpec;
            }
        }
        return null;
    }

    private Set<String> getPins(Uri uri) {
        return getPrefs().getStringSet("pinned_" + uri.toString(), new ArraySet());
    }

    private SharedPreferences getPrefs() {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(this.mPrefsName, 0);
        long j = sharedPreferences.getLong("last_boot", 0L);
        long bootTime = getBootTime();
        if (Math.abs(j - bootTime) > 2000) {
            sharedPreferences.edit().clear().putLong("last_boot", bootTime).apply();
        }
        return sharedPreferences;
    }

    private static ArraySet<SliceSpec> mergeSpecs(ArraySet<SliceSpec> specs, Set<SliceSpec> supportedSpecs) {
        int i;
        int i2 = 0;
        while (i2 < specs.size()) {
            SliceSpec valueAt = specs.valueAt(i2);
            SliceSpec findSpec = findSpec(supportedSpecs, valueAt.getType());
            if (findSpec == null) {
                i = i2 - 1;
                specs.removeAt(i2);
            } else if (findSpec.getRevision() < valueAt.getRevision()) {
                i = i2 - 1;
                specs.removeAt(i2);
                specs.add(findSpec);
            } else {
                i2++;
            }
            i2 = i;
            i2++;
        }
        return specs;
    }

    private void setPins(Uri uri, Set<String> pins) {
        getPrefs().edit().putStringSet("pinned_" + uri.toString(), pins).apply();
    }

    private void setSpecs(Uri uri, ArraySet<SliceSpec> specs) {
        String[] strArr = new String[specs.size()];
        String[] strArr2 = new String[specs.size()];
        for (int i = 0; i < specs.size(); i++) {
            strArr[i] = specs.valueAt(i).getType();
            strArr2[i] = String.valueOf(specs.valueAt(i).getRevision());
        }
        getPrefs().edit().putString("spec_names_" + uri.toString(), TextUtils.join(",", strArr)).putString("spec_revs_" + uri.toString(), TextUtils.join(",", strArr2)).apply();
    }

    public synchronized boolean addPin(Uri uri, String pkg, Set<SliceSpec> specs) {
        boolean isEmpty;
        Set<String> pins = getPins(uri);
        isEmpty = pins.isEmpty();
        pins.add(pkg);
        setPins(uri, pins);
        if (isEmpty) {
            setSpecs(uri, new ArraySet<>(specs));
        } else {
            setSpecs(uri, mergeSpecs(getSpecs(uri), specs));
        }
        return isEmpty;
    }

    protected long getBootTime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtime();
    }

    public List<Uri> getPinnedSlices() {
        ArrayList arrayList = new ArrayList();
        for (String str : getPrefs().getAll().keySet()) {
            if (str.startsWith("pinned_")) {
                Uri parse = Uri.parse(str.substring(7));
                if (!getPins(parse).isEmpty()) {
                    arrayList.add(parse);
                }
            }
        }
        return arrayList;
    }

    public synchronized ArraySet<SliceSpec> getSpecs(Uri uri) {
        ArraySet<SliceSpec> arraySet = new ArraySet<>();
        SharedPreferences prefs = getPrefs();
        String string = prefs.getString("spec_names_" + uri.toString(), null);
        String string2 = prefs.getString("spec_revs_" + uri.toString(), null);
        if (!TextUtils.isEmpty(string) && !TextUtils.isEmpty(string2)) {
            String[] split = string.split(",", -1);
            String[] split2 = string2.split(",", -1);
            if (split.length != split2.length) {
                return new ArraySet<>();
            }
            for (int i = 0; i < split.length; i++) {
                arraySet.add(new SliceSpec(split[i], Integer.parseInt(split2[i])));
            }
            return arraySet;
        }
        return new ArraySet<>();
    }

    public synchronized boolean removePin(Uri uri, String pkg) {
        Set<String> pins = getPins(uri);
        if (!pins.isEmpty() && pins.contains(pkg)) {
            pins.remove(pkg);
            setPins(uri, pins);
            setSpecs(uri, new ArraySet<>());
            return pins.size() == 0;
        }
        return false;
    }
}
