package miui.vip;

import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.ArrayList;

/* loaded from: classes4.dex */
public class VipMessageUtils {
    static final String KEY_VALUE = "value";

    private VipMessageUtils() {
    }

    public static String getErrMsg(int i, Object obj) {
        if (i == 0 || !(obj instanceof String[])) {
            return null;
        }
        String[] strArr = (String[]) obj;
        if (strArr.length >= 2) {
            return strArr[1];
        }
        return null;
    }

    public static String getRequestId(Message message) {
        Bundle data = message.getData();
        return data != null ? data.getString(VipService.REQUEST_ID, "") : "";
    }

    public static <T> T getValueFrom(Message message) {
        Bundle data = message.getData();
        if (data != null) {
            return (T) data.get("value");
        }
        return null;
    }

    public static String[] makeErrData(int i, Object obj, String str) {
        if (i != 0) {
            return new String[]{String.valueOf(obj), str};
        }
        return null;
    }

    public static void setData(Message message, Object obj) {
        if (obj == null) {
            return;
        }
        Bundle data = message.getData();
        if (data == null) {
            data = new Bundle();
        }
        if (obj instanceof String) {
            data.putString("value", (String) obj);
        } else if (obj instanceof Parcelable) {
            data.putParcelable("value", (Parcelable) obj);
        } else if (obj.getClass().isArray()) {
            Object[] objArr = (Object[]) obj;
            if (objArr.length > 0) {
                if (objArr[0] instanceof String) {
                    data.putStringArray("value", (String[]) obj);
                } else if (objArr[0] instanceof Parcelable) {
                    data.putParcelableArray("value", (Parcelable[]) obj);
                }
            }
        } else if (obj instanceof ArrayList) {
            ArrayList<? extends Parcelable> arrayList = (ArrayList) obj;
            if (!arrayList.isEmpty()) {
                Parcelable parcelable = arrayList.get(0);
                if (parcelable instanceof String) {
                    data.putStringArrayList("value", arrayList);
                } else if (parcelable instanceof Parcelable) {
                    data.putParcelableArrayList("value", arrayList);
                }
            }
        }
        message.setData(data);
    }

    public static void setRequestId(Message message, String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        Bundle data = message.getData();
        if (data == null) {
            data = new Bundle();
            message.setData(data);
        }
        data.putString(VipService.REQUEST_ID, str);
    }
}
