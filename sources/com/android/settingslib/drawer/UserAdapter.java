package com.android.settingslib.drawer;

import android.app.ActivityManager;
import android.content.Context;
import android.os.UserHandle;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;
import com.android.settingslib.R$string;
import java.util.List;

/* loaded from: classes2.dex */
public class UserAdapter implements SpinnerAdapter, ListAdapter {
    public static String[] getUserItem(Context context, List<UserHandle> list) {
        String[] strArr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            int identifier = list.get(i).getIdentifier();
            strArr[i] = context.getString((identifier == -2 || identifier == ActivityManager.getCurrentUser()) ? R$string.category_personal : R$string.category_work);
        }
        return strArr;
    }
}
