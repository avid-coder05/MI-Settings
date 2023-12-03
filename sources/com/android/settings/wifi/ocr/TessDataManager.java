package com.android.settings.wifi.ocr;

import android.content.Context;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import com.android.settings.R;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes2.dex */
public class TessDataManager {
    private static final String TAG = "TessDataManager";
    private boolean isTessinitiated;
    private String tesseractFolder;
    private String trainedDataPath;

    /* loaded from: classes2.dex */
    private static class TessDataManagerHolder {
        static TessDataManager tessDataManagerInstance = new TessDataManager();
    }

    private TessDataManager() {
    }

    public static TessDataManager getInstance() {
        return TessDataManagerHolder.tessDataManagerInstance;
    }

    private byte[] readRawTrainingData(Context context) {
        try {
            InputStream openRawResource = context.getResources().openRawResource(R.raw.chi_sim);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bArr = new byte[MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE];
            while (true) {
                int read = openRawResource.read(bArr);
                if (read == -1) {
                    openRawResource.close();
                    return byteArrayOutputStream.toByteArray();
                }
                byteArrayOutputStream.write(bArr, 0, read);
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "readRawTrainingData FileNotFoundException" + e);
            return null;
        } catch (IOException e2) {
            Log.e(TAG, "readRawTrainingData IOException" + e2);
            return null;
        }
    }

    private void writeBytesToTessFolder(File file, Context context) {
        String str;
        StringBuilder sb;
        byte[] readRawTrainingData;
        if (file.exists()) {
            this.isTessinitiated = true;
            return;
        }
        FileOutputStream fileOutputStream = null;
        try {
            try {
                readRawTrainingData = readRawTrainingData(context);
            } catch (Throwable th) {
                th = th;
            }
        } catch (FileNotFoundException e) {
            e = e;
        } catch (IOException e2) {
            e = e2;
        }
        if (readRawTrainingData == null) {
            return;
        }
        FileOutputStream fileOutputStream2 = new FileOutputStream(file);
        try {
            fileOutputStream2.write(readRawTrainingData);
            this.isTessinitiated = true;
            try {
                fileOutputStream2.close();
            } catch (IOException e3) {
                e = e3;
                str = TAG;
                sb = new StringBuilder();
                sb.append("initTessTrainedData close fileOutputStream IOException");
                sb.append(e);
                Log.e(str, sb.toString());
            }
        } catch (FileNotFoundException e4) {
            e = e4;
            fileOutputStream = fileOutputStream2;
            Log.e(TAG, "initTessTrainedData FileNotFoundException" + e);
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e5) {
                    e = e5;
                    str = TAG;
                    sb = new StringBuilder();
                    sb.append("initTessTrainedData close fileOutputStream IOException");
                    sb.append(e);
                    Log.e(str, sb.toString());
                }
            }
        } catch (IOException e6) {
            e = e6;
            fileOutputStream = fileOutputStream2;
            Log.e(TAG, "initTessTrainedData IOException" + e);
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e7) {
                    e = e7;
                    str = TAG;
                    sb = new StringBuilder();
                    sb.append("initTessTrainedData close fileOutputStream IOException");
                    sb.append(e);
                    Log.e(str, sb.toString());
                }
            }
        } catch (Throwable th2) {
            th = th2;
            fileOutputStream = fileOutputStream2;
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e8) {
                    Log.e(TAG, "initTessTrainedData close fileOutputStream IOException" + e8);
                }
            }
            throw th;
        }
    }

    public String getTesseractFolder() {
        return this.tesseractFolder;
    }

    public void initTessTrainedData(Context context) {
        if (this.isTessinitiated) {
            return;
        }
        File file = new File(context.getFilesDir(), "tesseract");
        if (!file.exists()) {
            file.mkdir();
        }
        this.tesseractFolder = file.getAbsolutePath();
        File file2 = new File(file, "tessdata");
        if (!file2.exists()) {
            file2.mkdir();
        }
        File file3 = new File(file2, "chi_sim.traineddata");
        this.trainedDataPath = file3.getAbsolutePath();
        writeBytesToTessFolder(file3, context);
    }
}
