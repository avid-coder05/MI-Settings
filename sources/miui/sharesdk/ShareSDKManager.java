package miui.sharesdk;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import java.util.ArrayList;
import miui.cloud.common.XLogger;
import miui.sharesdk.constants.ShareEntranceConstants;
import miui.sharesdk.constants.ShareUserInfoConstants;
import miui.sharesdk.model.InviteServerExtension;
import miui.sharesdk.model.ShareResource;

/* loaded from: classes4.dex */
public class ShareSDKManager {
    private static final String SDK_PACKAGE_NAME = "com.miui.cloudservice";
    private static final int SDK_VERSION_NONE = -1;
    private static final String SDK_VERSION_STRING_KEY = "share_sdk_version";
    public static final int SDK_VERSION_V1 = 0;
    public static final int SDK_VERSION_V2 = 20;
    private static final String TAG = "ShareSDKManager";

    public static Intent getInvitationInfoIntent(Context context, String str, String str2, ShareResource shareResource, ArrayList<Integer> arrayList, ChooseMode chooseMode, String str3) {
        if (getSDKKernelVersion(context) >= 0) {
            Intent intent = new Intent();
            intent.setAction("com.miui.cloudservice.VIEW_INVITATION_INFO");
            intent.setPackage("com.miui.cloudservice");
            intent.putExtra("share_app_id", str);
            intent.putExtra("share_package_name", str2);
            intent.putExtra("share_resource", shareResource);
            intent.putExtra("share_permission_id_list", arrayList);
            intent.putExtra("share_permission_choose_mode", chooseMode.ordinal());
            intent.putExtra(ShareUserInfoConstants.INTENT_KEY_SHARE_INVITATION_ID, str3);
            return intent;
        }
        throw new IllegalStateException("Not support Share SDK V1");
    }

    public static int getSDKKernelVersion(Context context) {
        try {
            Context createPackageContext = context.createPackageContext("com.miui.cloudservice", 0);
            Resources resources = createPackageContext.getResources();
            int identifier = resources.getIdentifier("share_sdk_version", "integer", createPackageContext.getPackageName());
            if (identifier == 0) {
                Intent intent = new Intent();
                intent.setAction("com.miui.cloudservice.ADD_SHARE_MEMBER");
                intent.setPackage("com.miui.cloudservice");
                return !context.getPackageManager().queryIntentActivities(intent, 0).isEmpty() ? 0 : -1;
            }
            return resources.getInteger(identifier);
        } catch (PackageManager.NameNotFoundException unused) {
            XLogger.loge(TAG, "Error occur for create share sdk context");
            return -1;
        }
    }

    public static Intent getShareEntranceIntent(Context context, String str, String str2, ShareResource shareResource, ArrayList<Integer> arrayList, ChooseMode chooseMode, InviteServerExtension inviteServerExtension) {
        if (getSDKKernelVersion(context) >= 0) {
            Intent intent = new Intent();
            intent.setAction("com.miui.cloudservice.ADD_SHARE_MEMBER");
            intent.setPackage("com.miui.cloudservice");
            intent.putExtra("share_app_id", str);
            intent.putExtra("share_package_name", str2);
            intent.putExtra("share_resource", shareResource);
            intent.putIntegerArrayListExtra("share_permission_id_list", arrayList);
            intent.putExtra("share_permission_choose_mode", chooseMode.ordinal());
            intent.putExtra("share_server_extension", inviteServerExtension);
            return intent;
        }
        throw new IllegalStateException("Not support Share SDK V1");
    }

    public static Intent getShareEntranceIntentV2(Context context, String str, String str2, ShareResource shareResource, ArrayList<Integer> arrayList, ChooseMode chooseMode, InviteServerExtension inviteServerExtension, ArrayList<Integer> arrayList2) {
        if (getSDKKernelVersion(context) >= 20) {
            Intent shareEntranceIntent = getShareEntranceIntent(context, str, str2, shareResource, arrayList, chooseMode, inviteServerExtension);
            shareEntranceIntent.putExtra("share_sdk_version", 20);
            shareEntranceIntent.putIntegerArrayListExtra(ShareEntranceConstants.INTENT_KEY_SHARE_PERMISSION_DEFAULT_CHECKED_PERMISSION_ID_LIST, arrayList2);
            return shareEntranceIntent;
        }
        throw new IllegalStateException("Share SDK not support for this V2 request");
    }

    public static Intent getSharePrivacyIntent(Context context, String str) {
        if (getSDKKernelVersion(context) >= 0) {
            Intent intent = new Intent();
            intent.setAction("com.miui.cloudservice.GRANT_SHARING_PRIVACY");
            intent.setPackage("com.miui.cloudservice");
            intent.putExtra("share_package_name", str);
            return intent;
        }
        throw new IllegalStateException("Not support Share SDK V1");
    }

    public static Intent getSharingInfoIntent(Context context, String str, String str2, ShareResource shareResource, ArrayList<Integer> arrayList, ChooseMode chooseMode, String str3, boolean z, InviteServerExtension inviteServerExtension) {
        if (getSDKKernelVersion(context) >= 0) {
            Intent intent = new Intent();
            intent.setAction("com.miui.cloudservice.VIEW_SHARING_INFO");
            intent.setPackage("com.miui.cloudservice");
            intent.putExtra("share_app_id", str);
            intent.putExtra("share_package_name", str2);
            intent.putExtra("share_resource", shareResource);
            intent.putExtra("share_permission_id_list", arrayList);
            intent.putExtra("share_permission_choose_mode", chooseMode.ordinal());
            intent.putExtra(ShareUserInfoConstants.INTENT_KEY_SHARE_USER_ID, str3);
            intent.putExtra(ShareUserInfoConstants.INTENT_KEY_SHARE_USER_IS_CREATOR, z);
            intent.putExtra("share_server_extension", inviteServerExtension);
            return intent;
        }
        throw new IllegalStateException("Not support Share SDK V1");
    }
}
