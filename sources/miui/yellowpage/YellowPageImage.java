package miui.yellowpage;

import android.content.Context;
import miui.util.CoderUtils;
import miui.yellowpage.YellowPageImgLoader;

@Deprecated
/* loaded from: classes4.dex */
public class YellowPageImage extends YellowPageImgLoader.Image {
    private String mName;

    public YellowPageImage(Context context, String str, int i, int i2, YellowPageImgLoader.Image.ImageFormat imageFormat) {
        super(HostManager.getImageUrl(context, str, i, i2, imageFormat));
        this.mName = str;
    }

    @Override // miui.yellowpage.YellowPageImgLoader.Image
    public String getName() {
        return CoderUtils.encodeSHA(this.mName);
    }
}
