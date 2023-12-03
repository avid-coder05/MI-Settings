package com.android.settings.wifi.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import com.googlecode.tesseract.android.TessBaseAPI;

/* loaded from: classes2.dex */
public class WifiOcrController {
    private static final String TAG = "WifiOcrController";

    /* loaded from: classes2.dex */
    private static class WifiOcrControllerHolder {
        static WifiOcrController wifiOcrControllerInstance = new WifiOcrController();
    }

    private WifiOcrController() {
    }

    public static WifiOcrController getInstance() {
        return WifiOcrControllerHolder.wifiOcrControllerInstance;
    }

    public String detectText(Bitmap bitmap, Context context) {
        TessDataManager.getInstance().initTessTrainedData(context.getApplicationContext());
        String tesseractFolder = TessDataManager.getInstance().getTesseractFolder();
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.setDebug(true);
        tessBaseAPI.init(tesseractFolder, "chi_sim");
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=-[]}{;:'\"\\|~`,./<>?");
        tessBaseAPI.setPageSegMode(1);
        tessBaseAPI.setImage(bitmap);
        String uTF8Text = tessBaseAPI.getUTF8Text();
        Log.d(TAG, "inspection = : " + uTF8Text);
        tessBaseAPI.end();
        return uTF8Text;
    }
}
