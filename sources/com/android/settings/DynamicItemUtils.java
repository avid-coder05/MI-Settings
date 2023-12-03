package com.android.settings;

import android.content.Context;
import android.util.Log;
import com.android.settings.dynamicitem.DynamicItem;
import com.android.settings.dynamicitem.SubScreenItem;
import com.android.settings.dynamicitem.UWBItem;
import com.android.settingslib.miuisettings.preference.PreferenceActivity;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class DynamicItemUtils {
    private final String TAG = DynamicItemUtils.class.getSimpleName();
    private List<DynamicItem> list = new ArrayList();

    public DynamicItemUtils() {
        init();
    }

    private void init() {
        this.list.add(new UWBItem());
        this.list.add(new SubScreenItem());
    }

    public boolean shouldShow(PreferenceActivity.Header header, Context context) {
        List<DynamicItem> list = this.list;
        if (list != null && !list.isEmpty()) {
            for (DynamicItem dynamicItem : this.list) {
                if (dynamicItem.shouldShow(context)) {
                    Log.d(this.TAG, "shouldShow: true");
                    dynamicItem.setDetail(context, header);
                    return true;
                }
                Log.d(this.TAG, "shouldShow: false");
            }
        }
        return false;
    }
}
