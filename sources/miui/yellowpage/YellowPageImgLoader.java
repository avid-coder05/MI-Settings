package miui.yellowpage;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.ImageView;
import com.miui.internal.yellowpage.ImageLoader;
import com.miui.internal.yellowpage.YellowPageAvatar;
import java.security.MessageDigest;
import miui.provider.ExtraTelephony;
import miui.util.HashUtils;
import miui.yellowpage.Tag;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes4.dex */
public class YellowPageImgLoader {
    private static final String YELLOWPAGE_PHOTO_DOWNLOAD_WIFI_ONLY = "yellowpage_photo_download_wifi_only";

    /* loaded from: classes4.dex */
    public static class Image {
        private ImageFormat mFormat;
        private ImageProcessor mImageProcesser;
        protected String mUrl;

        /* loaded from: classes4.dex */
        public enum ImageFormat {
            JPG,
            PNG
        }

        /* loaded from: classes4.dex */
        public interface ImageProcessor {
            Bitmap processImage(Bitmap bitmap);
        }

        public Image(String str) {
            this.mUrl = str;
            this.mFormat = ImageFormat.JPG;
        }

        public Image(String str, ImageFormat imageFormat) {
            this.mUrl = str;
            this.mFormat = imageFormat;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Image) {
                return TextUtils.equals(((Image) obj).mUrl, this.mUrl);
            }
            return false;
        }

        public ImageFormat getFormat() {
            return this.mFormat;
        }

        public String getName() {
            return HashUtils.getSHA1(this.mUrl);
        }

        public String getUrl() {
            return this.mUrl;
        }

        public int hashCode() {
            String str = this.mUrl;
            if (str == null) {
                return 0;
            }
            return str.hashCode();
        }

        public boolean isValid() {
            return !TextUtils.isEmpty(this.mUrl);
        }

        public Bitmap proccessImage(Bitmap bitmap) {
            ImageProcessor imageProcessor = this.mImageProcesser;
            return imageProcessor != null ? imageProcessor.processImage(bitmap) : bitmap;
        }

        public void setImageProcessor(ImageProcessor imageProcessor) {
            this.mImageProcesser = imageProcessor;
        }
    }

    private YellowPageImgLoader() {
    }

    public static void cancelLoading(Context context, ImageView imageView) {
        ImageLoader.getInstance(context).cancelRequest(imageView);
    }

    public static String getDataSha1Digest(byte[] bArr) {
        if (bArr != null && bArr.length != 0) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.SHA1);
                messageDigest.update(bArr);
                return getHexString(messageDigest.digest());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String getHexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bArr.length; i++) {
            int i2 = (bArr[i] & 240) >> 4;
            sb.append((char) ((i2 < 0 || i2 > 9) ? (i2 + 97) - 10 : i2 + 48));
            int i3 = bArr[i] & 15;
            sb.append((char) ((i3 < 0 || i3 > 9) ? (i3 + 97) - 10 : i3 + 48));
        }
        return sb.toString();
    }

    private static String getImageUrl(Context context, String str, int i, int i2, Image.ImageFormat imageFormat) {
        Cursor query;
        Uri.Builder buildUpon = Uri.withAppendedPath(YellowPageContract.ImageLookup.CONTENT_URI_IMAGE_URL, Uri.encode(str)).buildUpon();
        buildUpon.appendQueryParameter(Tag.TagWebService.ContentGetImage.PARAM_IMAGE_WIDTH, String.valueOf(i));
        buildUpon.appendQueryParameter("height", String.valueOf(i2));
        buildUpon.appendQueryParameter("format", imageFormat == Image.ImageFormat.JPG ? "jpg" : "png");
        Uri build = buildUpon.build();
        if (YellowPageUtils.isContentProviderInstalled(context, build) && (query = context.getContentResolver().query(build, null, null, null, null)) != null) {
            try {
                if (query.moveToFirst()) {
                    return query.getString(0);
                }
            } finally {
                query.close();
            }
        }
        return null;
    }

    private static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (connectivityManager.isActiveNetworkMetered() || activeNetworkInfo == null || !activeNetworkInfo.isConnected()) ? false : true;
    }

    private static boolean isYellowPagePhotoDownloadWifiOnly(Context context) {
        return Settings.System.getInt(context.getContentResolver(), YELLOWPAGE_PHOTO_DOWNLOAD_WIFI_ONLY, 1) == 1;
    }

    public static void loadImage(Context context, ImageView imageView, Image.ImageProcessor imageProcessor, Image.ImageFormat imageFormat, String str, int i, int i2, int i3) {
        Image image = new Image(HostManager.getImageUrl(context, str, i, i2, imageFormat), imageFormat);
        image.setImageProcessor(imageProcessor);
        ImageLoader.getInstance(context).loadImage(imageView, image, i3);
    }

    public static void loadImage(Context context, ImageView imageView, Image image, int i) {
        ImageLoader.getInstance(context).loadImage(imageView, image, i);
    }

    public static Bitmap loadPhoneDisplayAd(Context context, long j, String str, boolean z) {
        String string;
        Bitmap bitmap;
        String str2;
        int i = z ? 1 : 2;
        Uri.Builder buildUpon = YellowPageContract.ImageLookup.CONTENT_URI_IMAGE_PHONE_AD.buildUpon();
        buildUpon.appendQueryParameter("number", str);
        buildUpon.appendQueryParameter("yid", String.valueOf(j));
        buildUpon.appendQueryParameter(ExtraTelephony.FirewallLog.CALL_TYPE, String.valueOf(i));
        Uri build = buildUpon.build();
        if (YellowPageUtils.isContentProviderInstalled(context, build)) {
            Cursor query = context.getContentResolver().query(build, null, null, null, null);
            if (query != null) {
                try {
                    string = query.moveToFirst() ? query.getString(0) : null;
                } finally {
                    query.close();
                }
            } else {
                string = null;
            }
            if (TextUtils.isEmpty(string)) {
                bitmap = null;
                str2 = string;
            } else {
                Bitmap loadImageBitmap = ImageLoader.getInstance(context).loadImageBitmap(new Image(string), true);
                str2 = Uri.parse(string).getLastPathSegment();
                bitmap = loadImageBitmap;
            }
            YellowPageStatistic.viewYellowPageInPhoneCall(context, str, i, true, String.valueOf(j), str2, bitmap != null);
            return bitmap;
        }
        return null;
    }

    public static Bitmap loadPhoto(Context context, long j, boolean z) {
        return ImageLoader.getInstance(context).loadImageBitmap(new YellowPageAvatar(context, String.valueOf(j), YellowPageAvatar.YellowPageAvatarFormat.PHOTO_YID), z && (!isYellowPagePhotoDownloadWifiOnly(context) || isWifiConnected(context)));
    }

    public static Bitmap loadPhotoByName(Context context, String str, boolean z) {
        return ImageLoader.getInstance(context).loadImageBitmap(new YellowPageAvatar(context, str, YellowPageAvatar.YellowPageAvatarFormat.PHOTO_NAME), z && (!isYellowPagePhotoDownloadWifiOnly(context) || isWifiConnected(context)));
    }

    public static Bitmap loadThumbnail(Context context, String str, boolean z) {
        return ImageLoader.getInstance(context).loadImageBitmap(new YellowPageAvatar(context, str, YellowPageAvatar.YellowPageAvatarFormat.THUMBNAIL_NUMBER), z);
    }

    public static void loadThumbnail(Context context, ImageView imageView, Image.ImageProcessor imageProcessor, long j, int i) {
        YellowPageAvatar yellowPageAvatar = new YellowPageAvatar(context, String.valueOf(j), YellowPageAvatar.YellowPageAvatarFormat.THUMBNAIL_YID);
        yellowPageAvatar.setImageProcessor(imageProcessor);
        ImageLoader.getInstance(context).loadImage(imageView, yellowPageAvatar, i);
    }

    public static void loadThumbnail(Context context, ImageView imageView, Image.ImageProcessor imageProcessor, String str, int i) {
        YellowPageAvatar yellowPageAvatar = new YellowPageAvatar(context, str, YellowPageAvatar.YellowPageAvatarFormat.THUMBNAIL_NUMBER);
        yellowPageAvatar.setImageProcessor(imageProcessor);
        ImageLoader.getInstance(context).loadImage(imageView, yellowPageAvatar, i);
    }

    public static void loadThumbnailByName(Context context, ImageView imageView, Image.ImageProcessor imageProcessor, String str, int i) {
        YellowPageAvatar yellowPageAvatar = new YellowPageAvatar(context, str, YellowPageAvatar.YellowPageAvatarFormat.THUMBNAIL_NAME);
        yellowPageAvatar.setImageProcessor(imageProcessor);
        ImageLoader.getInstance(context).loadImage(imageView, yellowPageAvatar, i);
    }

    public static byte[] loadThumbnailByName(Context context, String str, boolean z) {
        return ImageLoader.getInstance(context).loadImageBytes(new YellowPageAvatar(context, str, YellowPageAvatar.YellowPageAvatarFormat.THUMBNAIL_NAME), z, 0, 0);
    }

    public static byte[] loadThumbnailByName(Context context, String str, boolean z, int i, int i2) {
        return ImageLoader.getInstance(context).loadImageBytes(new YellowPageAvatar(context, str, YellowPageAvatar.YellowPageAvatarFormat.THUMBNAIL_NAME), z, i, i2);
    }

    public static void pauseLoading(Context context) {
        ImageLoader.getInstance(context).pauseLoading();
    }

    public static void resumeLoading(Context context) {
        ImageLoader.getInstance(context).resumeLoading();
    }
}
