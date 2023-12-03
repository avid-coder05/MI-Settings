package miui.yellowpage;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import miui.graphics.BitmapFactory;
import miui.yellowpage.YellowPageImgLoader;

/* loaded from: classes4.dex */
public class ContactThumbnailProcessor implements YellowPageImgLoader.Image.ImageProcessor {
    private static final int sPhotoSize = 134;
    private int mBackgroundRes;
    private Context mContext;
    private boolean mDefaultPhoto = true;
    private int mForegroundRes;
    private int mMaskRes;

    public ContactThumbnailProcessor(Context context) {
        this.mContext = context;
    }

    public ContactThumbnailProcessor(Context context, int i, int i2, int i3) {
        this.mContext = context;
        this.mForegroundRes = i;
        this.mBackgroundRes = i2;
        this.mMaskRes = i3;
    }

    @Override // miui.yellowpage.YellowPageImgLoader.Image.ImageProcessor
    public Bitmap processImage(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        if (this.mDefaultPhoto) {
            return BitmapFactory.createPhoto(this.mContext, bitmap, (int) sPhotoSize);
        }
        Resources resources = this.mContext.getResources();
        return BitmapFactory.composeBitmap(bitmap, (Bitmap) null, resources.getDrawable(this.mMaskRes), resources.getDrawable(this.mForegroundRes), resources.getDrawable(this.mBackgroundRes), (int) sPhotoSize);
    }
}
