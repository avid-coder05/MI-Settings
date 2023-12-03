package com.android.settings.search;

import android.content.ContentProviderOperation;
import android.content.Context;
import com.android.settings.R;
import java.util.ArrayList;
import miui.os.Build;

/* loaded from: classes2.dex */
class GoogleSettingsUpdateHelper extends BaseSearchUpdateHelper {
    GoogleSettingsUpdateHelper() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void update(Context context, ArrayList<ContentProviderOperation> arrayList) {
        if (Build.IS_INTERNATIONAL_BUILD) {
            BaseSearchUpdateHelper.insertSearchItem(arrayList, "com.android.settings31000", context.getResources().getString(R.string.google_title), null, null, null, null, "", "", "com.google.android.gms", "com.google.android.gms.app.settings.GoogleSettingsLink", "android.resource://com.android.settings/drawable/ic_google_settings", "settings_label-google_title", "", 0L);
        }
    }
}
