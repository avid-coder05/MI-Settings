package miui.cta;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.SparseArray;
import android.view.MiuiWindowManager$LayoutParams;
import com.miui.system.internal.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes3.dex */
public class CTAPermission {
    private static final String TAG = "CTAPermission";

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public enum Permission {
        PERMISSION_ACCESS_NETWORK(1),
        PERMISSION_READ_SMS(2),
        PERMISSION_WRITE_SMS(4),
        PERMISSION_RECEIVE_SMS(8),
        PERMISSION_SEND_SMS(16),
        PERMISSION_CALL_PHONE(32),
        PERMISSION_READ_CONTACTS(64),
        PERMISSION_WRITE_CONTACTS(128),
        PERMISSION_READ_CALL_LOG(256),
        PERMISSION_WRITE_CALL_LOG(512),
        PERMISSION_CAMERA(MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE),
        PERMISSION_ACCESS_LOCATION(MiuiWindowManager$LayoutParams.EXTRA_FLAG_FINDDEVICE_KEYGUARD);

        final int value;

        Permission(int i) {
            this.value = i;
        }

        @Override // java.lang.Enum
        public String toString() {
            return super.toString() + ", value=" + this.value;
        }
    }

    public static String getMessage(Context context, int i) {
        if (i == 0) {
            return null;
        }
        List<String> permissionNames = getPermissionNames(context, i);
        if (permissionNames.isEmpty()) {
            return null;
        }
        return getPermissionMessage(context, permissionNames);
    }

    private static SparseArray<String> getPermissionMap(Context context) {
        Resources resources = context.getResources();
        String[] stringArray = resources.getStringArray(R.array.cta_permissions);
        String[] stringArray2 = resources.getStringArray(R.array.cta_permission_names);
        SparseArray<String> sparseArray = new SparseArray<>();
        int min = Math.min(stringArray.length, stringArray2.length);
        for (int i = 0; i < min; i++) {
            int i2 = 0;
            for (String str : stringArray[i].split("\\|")) {
                try {
                    i2 |= Permission.valueOf(str).value;
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Unknown permission: " + str, e);
                }
            }
            if (i2 > 0) {
                sparseArray.append(i2, stringArray2[i]);
            }
        }
        return sparseArray;
    }

    private static String getPermissionMessage(Context context, List<String> list) {
        int size = list.size();
        int i = 0;
        if (size == 1) {
            return list.get(0);
        }
        if (size == 2) {
            return list.get(0) + context.getString(R.string.cta_permission_and) + list.get(1);
        }
        String string = context.getString(R.string.cta_permission_delimiter);
        String string2 = context.getString(R.string.cta_permission_and);
        StringBuilder sb = new StringBuilder();
        while (true) {
            int i2 = size - 2;
            if (i >= i2) {
                sb.append(list.get(i2));
                sb.append(string2);
                sb.append(list.get(size - 1));
                return sb.toString();
            }
            sb.append(list.get(i));
            sb.append(string);
            i++;
        }
    }

    private static List<String> getPermissionNames(Context context, int i) {
        ArrayList arrayList = new ArrayList();
        SparseArray<String> permissionMap = getPermissionMap(context);
        for (int size = permissionMap.size() - 1; size >= 0; size--) {
            int keyAt = permissionMap.keyAt(size);
            if ((i & keyAt) == keyAt) {
                arrayList.add(permissionMap.valueAt(size));
                i &= ~keyAt;
            }
        }
        Collections.reverse(arrayList);
        return arrayList;
    }
}
