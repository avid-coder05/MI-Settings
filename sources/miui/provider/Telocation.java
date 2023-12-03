package miui.provider;

import android.net.Uri;

/* loaded from: classes3.dex */
public class Telocation {
    public static final String ACTION_ADD_CUSTOM_LOCATION = "action_add_custom_location";
    public static final String ACTION_EDIT_CUSTOM_LOCATION = "action_edit_custom_location";
    public static final String AUTHORITY = "telocation";
    public static final String CONTENT_CUSTOM_LOCATION_TYPE = "vnd.android.cursor.dir/custom_telocations";
    public static final Uri CONTENT_CUSTOM_LOCATION_URI = Uri.parse("content://telocation/customlocations");
    public static final int CUSTOM_ID_COLUMN_INDEX = 0;
    public static final String CUSTOM_LOCATION_COLUMN = "location";
    public static final int CUSTOM_LOCATION_COLUMN_INDEX = 2;
    public static final String CUSTOM_NUMBER_COLUMN = "number";
    public static final int CUSTOM_NUMBER_COLUMN_INDEX = 1;
    public static final String CUSTOM_TYPE_COLUMN = "type";
    public static final int CUSTOM_TYPE_COLUMN_INDEX = 3;
    public static final String LOCATION_MATCH = "customlocations";
    public static final int TYPE_JITUAN = 2;
    public static final int TYPE_MOB = 1;
    public static final int TYPE_TEL = 0;
}
