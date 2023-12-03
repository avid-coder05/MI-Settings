package miui.mipub;

import android.net.Uri;
import android.provider.BaseColumns;

@Deprecated
/* loaded from: classes3.dex */
public final class HmsCommon implements BaseColumns {
    public static final Uri CONTENT_URI;
    public static final Uri CONTENT_URI_MIPUBINFO;
    public static final Uri CONTENT_URI_MIPUBINFO_LOCAL;
    public static final Uri CONTENT_URI_MIPUBINFO_NET;
    public static final String FOLLOW = "follow";
    public static final String FOLLOW_TIME = "follow_time";
    public static final String MIPUB_DESC = "desc";
    public static final String MIPUB_ICON_URL = "iconUrl";
    public static final String MIPUB_ID = "mipub_id";
    public static final String MIPUB_NAME = "name";

    static {
        Uri parse = Uri.parse("content://hmscommon/");
        CONTENT_URI = parse;
        CONTENT_URI_MIPUBINFO = Uri.withAppendedPath(parse, "mipub_info");
        CONTENT_URI_MIPUBINFO_LOCAL = Uri.withAppendedPath(parse, "mipub_info/local");
        CONTENT_URI_MIPUBINFO_NET = Uri.withAppendedPath(parse, "mipub_info/net");
    }
}
