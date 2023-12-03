package com.android.settings.privacypassword;

import com.android.settings.R;
import java.util.HashMap;
import java.util.Map;
import miui.os.Build;

/* loaded from: classes2.dex */
public class BussinessPackageInfoCache {
    private static final String FILE_PACKAGE_NAME;
    private static Map<String, BussinessPackageInfo> sBussinessInfos;
    private static Map<String, String> sModifyandInstructions;
    private static Map<String, BussinessSpecificationInfo> specificationInfos;

    static {
        String str = Build.IS_INTERNATIONAL_BUILD ? "com.mi.android.globalFileexplorer" : "com.android.fileexplorer";
        FILE_PACKAGE_NAME = str;
        HashMap hashMap = new HashMap();
        sBussinessInfos = hashMap;
        int i = R.string.privacy_mms;
        hashMap.put("com.android.mms", new BussinessPackageInfo(i, "privacy_mms"));
        Map<String, BussinessPackageInfo> map = sBussinessInfos;
        int i2 = R.string.privacy_gallery;
        map.put("com.miui.gallery", new BussinessPackageInfo(i2, "privacy_gallery"));
        Map<String, BussinessPackageInfo> map2 = sBussinessInfos;
        int i3 = R.string.privacy_file;
        map2.put(str, new BussinessPackageInfo(i3, "privacy_file"));
        Map<String, BussinessPackageInfo> map3 = sBussinessInfos;
        int i4 = R.string.privacy_notes;
        map3.put("com.miui.notes", new BussinessPackageInfo(i4, "privacy_notes"));
        HashMap hashMap2 = new HashMap();
        specificationInfos = hashMap2;
        hashMap2.put("privacy_mms", new BussinessSpecificationInfo(i, R.string.mms_privacy_password_role_instruction, R.drawable.privacy_password_mms, "com.android.mms", false, "android.intent.action.MAIN"));
        specificationInfos.put("privacy_gallery", new BussinessSpecificationInfo(i2, R.string.gallery_privacy_password_role_instruction, R.drawable.privacy_password_gallery, "com.miui.gallery", false, "android.intent.action.MAIN"));
        specificationInfos.put("privacy_file", new BussinessSpecificationInfo(i3, R.string.file_privacy_password_role_instruction, R.drawable.privacy_password_file, str, false, "android.intent.action.MAIN"));
        specificationInfos.put("privacy_notes", new BussinessSpecificationInfo(i4, R.string.notes_privacy_password_role_instruction, R.drawable.privacy_password_notes, "com.miui.notes", false, "android.intent.action.MAIN"));
        HashMap hashMap3 = new HashMap();
        sModifyandInstructions = hashMap3;
        hashMap3.put("privacy_mms", "privacy_mms");
        sModifyandInstructions.put("privacy_gallery", "privacy_gallery");
        sModifyandInstructions.put("privacy_file", "privacy_file");
        sModifyandInstructions.put("privacy_notes", "privacy_notes");
    }

    public static Map<String, BussinessPackageInfo> getBussinessPackageInfo() {
        return sBussinessInfos;
    }

    public static Map<String, String> getModifyandInstructionsInfo() {
        return sModifyandInstructions;
    }

    public static Map<String, BussinessSpecificationInfo> getSpcificationInfos() {
        return specificationInfos;
    }
}
