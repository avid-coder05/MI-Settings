package com.android.settings.haptic;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class SubtitleProcessor {
    public static void closeStreams(Closeable... closeableArr) {
        if (closeableArr != null) {
            for (Closeable closeable : closeableArr) {
                if (closeable != null) {
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE];
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                return;
            }
            outputStream.write(bArr, 0, read);
        }
    }

    public static int findTrackIndexFor(int i, MediaPlayer.TrackInfo[] trackInfoArr) {
        for (int i2 = 0; i2 < trackInfoArr.length; i2++) {
            if (trackInfoArr[i2].getTrackType() == i) {
                return i2;
            }
        }
        return -1;
    }

    public static String getSubtitleFile(Context context, int i) {
        FileOutputStream fileOutputStream;
        InputStream openRawResource;
        File fileStreamPath = context.getFileStreamPath(context.getResources().getResourceEntryName(i));
        if (fileStreamPath.exists()) {
            Log.d("SubtitleProcessor", "Subtitle already exists");
            return fileStreamPath.getAbsolutePath();
        }
        Log.d("SubtitleProcessor", "Subtitle does not exists, copy it from res/raw");
        InputStream inputStream = null;
        try {
            openRawResource = context.getResources().openRawResource(i);
            try {
                fileOutputStream = new FileOutputStream(fileStreamPath, false);
            } catch (Exception e) {
                e = e;
                fileOutputStream = null;
            } catch (Throwable th) {
                th = th;
                fileOutputStream = null;
            }
        } catch (Exception e2) {
            e = e2;
            fileOutputStream = null;
        } catch (Throwable th2) {
            th = th2;
            fileOutputStream = null;
        }
        try {
            copyFile(openRawResource, fileOutputStream);
            String absolutePath = fileStreamPath.getAbsolutePath();
            closeStreams(openRawResource, fileOutputStream);
            return absolutePath;
        } catch (Exception e3) {
            e = e3;
            inputStream = openRawResource;
            try {
                e.printStackTrace();
                closeStreams(inputStream, fileOutputStream);
                return "";
            } catch (Throwable th3) {
                th = th3;
                closeStreams(inputStream, fileOutputStream);
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            inputStream = openRawResource;
            closeStreams(inputStream, fileOutputStream);
            throw th;
        }
    }
}
