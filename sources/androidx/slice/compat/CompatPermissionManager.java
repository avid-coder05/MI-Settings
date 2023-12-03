package androidx.slice.compat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import androidx.collection.ArraySet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/* loaded from: classes.dex */
public class CompatPermissionManager {
    private final String[] mAutoGrantPermissions;
    private final Context mContext;
    private final int mMyUid;
    private final String mPrefsName;

    /* loaded from: classes.dex */
    public static class PermissionState {
        private final String mKey;
        private final ArraySet<String[]> mPaths;

        PermissionState(Set<String> grant, String key, boolean hasAllPermissions) {
            ArraySet<String[]> arraySet = new ArraySet<>();
            this.mPaths = arraySet;
            if (hasAllPermissions) {
                arraySet.add(new String[0]);
            } else {
                Iterator<String> it = grant.iterator();
                while (it.hasNext()) {
                    this.mPaths.add(decodeSegments(it.next()));
                }
            }
            this.mKey = key;
        }

        private String[] decodeSegments(String s) {
            String[] split = s.split("/", -1);
            for (int i = 0; i < split.length; i++) {
                split[i] = Uri.decode(split[i]);
            }
            return split;
        }

        private String encodeSegments(String[] s) {
            String[] strArr = new String[s.length];
            for (int i = 0; i < s.length; i++) {
                strArr[i] = Uri.encode(s[i]);
            }
            return TextUtils.join("/", strArr);
        }

        private boolean isPathPrefixMatch(String[] prefix, String[] path) {
            int length = prefix.length;
            if (path.length < length) {
                return false;
            }
            for (int i = 0; i < length; i++) {
                if (!Objects.equals(path[i], prefix[i])) {
                    return false;
                }
            }
            return true;
        }

        boolean addPath(List<String> path) {
            String[] strArr = (String[]) path.toArray(new String[path.size()]);
            for (int size = this.mPaths.size() - 1; size >= 0; size--) {
                String[] valueAt = this.mPaths.valueAt(size);
                if (isPathPrefixMatch(valueAt, strArr)) {
                    return false;
                }
                if (isPathPrefixMatch(strArr, valueAt)) {
                    this.mPaths.removeAt(size);
                }
            }
            this.mPaths.add(strArr);
            return true;
        }

        public String getKey() {
            return this.mKey;
        }

        public boolean hasAccess(List<String> path) {
            String[] strArr = (String[]) path.toArray(new String[path.size()]);
            Iterator<String[]> it = this.mPaths.iterator();
            while (it.hasNext()) {
                if (isPathPrefixMatch(it.next(), strArr)) {
                    return true;
                }
            }
            return false;
        }

        public boolean hasAllPermissions() {
            return hasAccess(Collections.emptyList());
        }

        boolean removePath(List<String> path) {
            String[] strArr = (String[]) path.toArray(new String[path.size()]);
            boolean z = false;
            for (int size = this.mPaths.size() - 1; size >= 0; size--) {
                if (isPathPrefixMatch(strArr, this.mPaths.valueAt(size))) {
                    this.mPaths.removeAt(size);
                    z = true;
                }
            }
            return z;
        }

        public Set<String> toPersistable() {
            ArraySet arraySet = new ArraySet();
            Iterator<String[]> it = this.mPaths.iterator();
            while (it.hasNext()) {
                arraySet.add(encodeSegments(it.next()));
            }
            return arraySet;
        }
    }

    public CompatPermissionManager(Context context, String prefsName, int myUid, String[] autoGrantPermissions) {
        this.mContext = context;
        this.mPrefsName = prefsName;
        this.mMyUid = myUid;
        this.mAutoGrantPermissions = autoGrantPermissions;
    }

    private int checkSlicePermission(Uri uri, String pkg) {
        return getPermissionState(pkg, uri.getAuthority()).hasAccess(uri.getPathSegments()) ? 0 : -1;
    }

    private PermissionState getPermissionState(String pkg, String authority) {
        String str = pkg + "_" + authority;
        return new PermissionState(getPrefs().getStringSet(str, Collections.emptySet()), str, getPrefs().getBoolean(str + "_all", false));
    }

    private SharedPreferences getPrefs() {
        return this.mContext.getSharedPreferences(this.mPrefsName, 0);
    }

    private synchronized void persist(PermissionState state) {
        getPrefs().edit().putStringSet(state.getKey(), state.toPersistable()).putBoolean(state.getKey() + "_all", state.hasAllPermissions()).apply();
    }

    @SuppressLint({"WrongConstant"})
    public int checkSlicePermission(Uri uri, int pid, int uid) {
        if (uid == this.mMyUid) {
            return 0;
        }
        String[] packagesForUid = this.mContext.getPackageManager().getPackagesForUid(uid);
        for (String str : packagesForUid) {
            if (checkSlicePermission(uri, str) == 0) {
                return 0;
            }
        }
        for (String str2 : this.mAutoGrantPermissions) {
            if (this.mContext.checkPermission(str2, pid, uid) == 0) {
                for (String str3 : packagesForUid) {
                    grantSlicePermission(uri, str3);
                }
                return 0;
            }
        }
        return this.mContext.checkUriPermission(uri, pid, uid, 2);
    }

    public void grantSlicePermission(Uri uri, String toPkg) {
        PermissionState permissionState = getPermissionState(toPkg, uri.getAuthority());
        if (permissionState.addPath(uri.getPathSegments())) {
            persist(permissionState);
        }
    }

    public void revokeSlicePermission(Uri uri, String toPkg) {
        PermissionState permissionState = getPermissionState(toPkg, uri.getAuthority());
        if (permissionState.removePath(uri.getPathSegments())) {
            persist(permissionState);
        }
    }
}
