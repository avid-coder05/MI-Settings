package com.xiaomi.mirror.synergy;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

/* loaded from: classes2.dex */
class CallMethod {
    static final String ARG_AP_CALLBACK = "apCallback";
    static final String ARG_AP_SSID = "apSsid";
    static final String ARG_CLIP_DATA = "clipData";
    static final String ARG_DISPLAY_ID = "displayId";
    static final String ARG_EXTENSION = "extension";
    static final String ARG_EXTRA_STRING = "extra";
    static final String ARG_ID = "id";
    static final String ARG_TITLE = "title";
    static final String ARG_URI = "uri";
    static final String CALL_PROVIDER_AUTHORITY = "com.xiaomi.mirror.callprovider";
    static final String METHOD_CHOOSE_FILE_FROM_SYNERGY = "chooseFileFromSynergy";
    static final String METHOD_CONNECT_SAME_ACCOUNT_AP = "connectSameAccountAp";
    static final String METHOD_GET = "get";
    static final String METHOD_GET_ALIVE_BINDER = "getAliveBinder";
    static final String METHOD_GET_CALL_RELAY_SERVICE = "getCallRelayService";
    static final String METHOD_GET_UPDATE_ICON = "getUpdateIcon";
    static final String METHOD_IS_FLOAT_WINDOW_SHOW = "isFloatWindowShow";
    static final String METHOD_IS_P2P_WORKING = "isP2PWorking";
    static final String METHOD_IS_SUPPORT_TAKE_PHOTO = "isSupportTakePhoto";
    static final String METHOD_IS_SYNERGY_ENABLE = "isSynergyEnable";
    static final String METHOD_OPEN_DIRECT = "openDirect";
    static final String METHOD_OPEN_MI_CLOUD_ON_SYNERGY = "openMiCloudOnSynergy";
    static final String METHOD_OPEN_ON_SYNERGY = "openOnSynergy";
    static final String METHOD_PERFORM_RELAY_ICON_CLICK = "performRelayIconClick";
    static final String METHOD_QUERY_OPEN_ON_SYNERGY = "queryOpenOnSynergy";
    static final String METHOD_QUERY_SAME_ACCOUNT_AP = "querySameAccountAp";
    static final String METHOD_REGISTER_AP_CALLBACK = "registerApCallback";
    static final String METHOD_SAVE_TO_SYNERGY = "saveToSynergy";
    static final String METHOD_TAKE_PHOTO_CANCEL = "takePhotoCancel";
    static final String METHOD_TAKE_PHOTO_FROM_SYNERGY = "takePhotoFromSynergy";
    static final String METHOD_UNREGISTER_AP_CALLBACK = "unRegisterApCallback";
    static final String METHOD_UPDATE_TITLE = "updateTitle";
    static final String RESULT_AP_IS5G = "apId5G";
    static final String RESULT_AP_SSID = "apSsid";
    static final String RESULT_BATTERY_PERCENT = "batteryPercent";
    static final String RESULT_BINDER = "binder";
    static final String RESULT_CLIP_DATA = "clipData";
    static final String RESULT_ENABLE_BOOLEAN = "enable";
    static final String RESULT_FILE_DESCRIPTOR = "fileDescriptor";
    static final String RESULT_ICON = "icon";
    static final String RESULT_ID = "id";
    static final String RESULT_IS_FLOAT_WINDOW_SHOW = "isFloatWindowShow";
    static final String RESULT_OPTION_LIST = "optionList";
    static final String RESULT_SOFTAP_STATE = "softApState";
    static final String RESULT_TITLE = "title";
    static final String RESULT_VALUE = "value";

    CallMethod() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Bundle doCall(ContentResolver contentResolver, String str, String str2, Bundle bundle) {
        return Build.VERSION.SDK_INT >= 29 ? contentResolver.call(CALL_PROVIDER_AUTHORITY, str, str2, bundle) : contentResolver.call(new Uri.Builder().scheme("content").authority(CALL_PROVIDER_AUTHORITY).build(), str, str2, bundle);
    }
}
