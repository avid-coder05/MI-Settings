package miui.cloud.sync;

/* loaded from: classes3.dex */
public class FamilyInfo {
    public static final String ROLE_NONE = "None";
    public static final String ROLE_ORGANIZER = "Organizer";
    public static final String ROLE_SHARER = "Sharer";
    public static final String STATUS_NOT_ALLOW_SHARE = "NotAllowShare";
    public static final String STATUS_NOT_SHARING = "NotSharing";
    public static final String STATUS_SHARING = "Sharing";
    public final String role;
    public final String status;

    public FamilyInfo(String str, String str2) {
        this.role = str;
        this.status = str2;
    }
}
