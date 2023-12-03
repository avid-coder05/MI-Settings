package miui.cloud.common;

import android.content.Intent;
import android.os.Bundle;

/* loaded from: classes3.dex */
public class XDebugUtils {
    public static String intent2string(Intent intent) {
        StringBuilder sb = new StringBuilder();
        sb.append(intent.toString());
        sb.append(" EXTRAS: [");
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String str : extras.keySet()) {
                sb.append(String.format(" '%s' => '%s' ", String.valueOf(str), String.valueOf(extras.get(str))));
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
