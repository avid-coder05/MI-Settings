package com.android.settings.utils;

import android.content.Context;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.JSONException;

/* loaded from: classes2.dex */
public class AssetsResourcesLoadUtil {
    public static String loadJson(Context context, String str) {
        try {
            return readJSONObject(context.getAssets().open(str));
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    private static String readJSONObject(InputStream inputStream) throws IOException, JSONException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        StringBuilder sb = new StringBuilder();
        char[] cArr = new char[MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE];
        while (true) {
            try {
                try {
                    int read = inputStreamReader.read(cArr, 0, MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE);
                    if (read <= 0) {
                        break;
                    }
                    sb.append(cArr, 0, read);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        inputStreamReader.close();
                        return null;
                    } catch (IOException unused) {
                        Log.e("AssetsResourcesLoadUtil", "close InputStream failed");
                        return null;
                    }
                }
            } catch (Throwable th) {
                try {
                    inputStreamReader.close();
                } catch (IOException unused2) {
                    Log.e("AssetsResourcesLoadUtil", "close InputStream failed");
                }
                throw th;
            }
        }
        String sb2 = sb.toString();
        try {
            inputStreamReader.close();
        } catch (IOException unused3) {
            Log.e("AssetsResourcesLoadUtil", "close InputStream failed");
        }
        return sb2;
    }
}
