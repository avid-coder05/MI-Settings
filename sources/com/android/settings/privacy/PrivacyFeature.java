package com.android.settings.privacy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.android.settings.search.FunctionColumns;

/* loaded from: classes2.dex */
public class PrivacyFeature {
    public static boolean startApplicationDetail(Context context, String str) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts(FunctionColumns.PACKAGE, str, null));
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
