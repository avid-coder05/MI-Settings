package miui.util;

import android.content.Context;
import android.net.Uri;
import android.view.MiuiWindowManager$LayoutParams;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

/* loaded from: classes4.dex */
public class InputStreamLoader {
    ByteArrayInputStream mByteArrayInputStream;
    private Context mContext;
    private FileAccessable mFileAccessable;
    private InputStream mInputStream;
    private String mPath;
    private Uri mUri;
    private ZipFile mZipFile;
    private String mZipPath;

    public InputStreamLoader(Context context, Uri uri) {
        if ("file".equals(uri.getScheme())) {
            this.mPath = uri.getPath();
            return;
        }
        this.mContext = context;
        this.mUri = uri;
    }

    public InputStreamLoader(String str) {
        this.mPath = str;
    }

    public InputStreamLoader(String str, String str2) {
        this.mZipPath = str;
        this.mPath = str2;
    }

    public InputStreamLoader(FileAccessable fileAccessable) {
        this.mFileAccessable = fileAccessable;
    }

    public InputStreamLoader(byte[] bArr) {
        this.mByteArrayInputStream = new ByteArrayInputStream(bArr);
    }

    public void close() {
        try {
            InputStream inputStream = this.mInputStream;
            if (inputStream != null) {
                inputStream.close();
            }
            ZipFile zipFile = this.mZipFile;
            if (zipFile != null) {
                zipFile.close();
            }
        } catch (IOException unused) {
        }
    }

    public InputStream get() {
        close();
        try {
            FileAccessable fileAccessable = this.mFileAccessable;
            if (fileAccessable != null) {
                this.mInputStream = fileAccessable.getInputStream();
            } else if (this.mUri != null) {
                this.mInputStream = this.mContext.getContentResolver().openInputStream(this.mUri);
            } else if (this.mZipPath != null) {
                ZipFile zipFile = new ZipFile(this.mZipPath);
                this.mZipFile = zipFile;
                this.mInputStream = zipFile.getInputStream(zipFile.getEntry(this.mPath));
            } else if (this.mPath != null) {
                this.mInputStream = new FileInputStream(this.mPath);
            } else {
                ByteArrayInputStream byteArrayInputStream = this.mByteArrayInputStream;
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.reset();
                    this.mInputStream = this.mByteArrayInputStream;
                }
            }
        } catch (Exception unused) {
        }
        InputStream inputStream = this.mInputStream;
        if (inputStream != null && !(inputStream instanceof ByteArrayInputStream)) {
            this.mInputStream = new BufferedInputStream(this.mInputStream, MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_CALL_SCREEN_PROJECTION);
        }
        return this.mInputStream;
    }
}
