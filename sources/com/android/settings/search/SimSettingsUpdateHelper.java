package com.android.settings.search;

import android.content.ContentProviderOperation;
import android.content.Context;
import com.android.settings.MiuiUtils;
import com.android.settingslib.search.SearchUtils;
import java.util.ArrayList;
import miui.telephony.TelephonyManager;

/* loaded from: classes2.dex */
public class SimSettingsUpdateHelper {
    private static final String ALWAYS_ENABLE_MMS_RESOURCE = "always_enable_mms";
    private static final String DATA_ENABLED_RESOURCE = "data_enabled";
    private static final String DATA_USAGE_RESOURCE = "preference_data_usage_title";
    private static final String PHONE_PACKAGE = "com.android.phone";
    private static final String VOLTE_SWITCH_RESOURCE = "volte_switch_title";

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void update(Context context, ArrayList<ContentProviderOperation> arrayList) {
        if (TelephonyManager.getDefault().isVoiceCapable()) {
            try {
                Context packageContext = SearchUtils.getPackageContext(context, PHONE_PACKAGE);
                boolean hasSimCard = MiuiUtils.getInstance().hasSimCard(context);
                BaseSearchUpdateHelper.disableByResource(packageContext, arrayList, DATA_ENABLED_RESOURCE, !hasSimCard);
                BaseSearchUpdateHelper.disableByResource(packageContext, arrayList, VOLTE_SWITCH_RESOURCE, !hasSimCard);
                BaseSearchUpdateHelper.disableByResource(packageContext, arrayList, DATA_USAGE_RESOURCE, !hasSimCard);
                BaseSearchUpdateHelper.disableByResource(packageContext, arrayList, ALWAYS_ENABLE_MMS_RESOURCE, !hasSimCard);
            } catch (RuntimeException unused) {
            }
        }
    }
}
