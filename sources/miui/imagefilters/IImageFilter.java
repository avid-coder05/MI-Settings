package miui.imagefilters;

import android.graphics.Bitmap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes3.dex */
public interface IImageFilter {

    /* loaded from: classes3.dex */
    public static abstract class AbstractImageFilter implements IImageFilter {
        private Map<String, List<Object>> mParams;

        private void fillRandomParams(ImageData imageData) {
            Map<String, List<Object>> map = this.mParams;
            if (map != null) {
                for (Map.Entry<String, List<Object>> entry : map.entrySet()) {
                    String key = entry.getKey();
                    List<Object> value = entry.getValue();
                    ImageFilterUtils.setProperty(this, key, value.get(imageData.generalRandomNum(value.size())));
                }
            }
        }

        @Override // miui.imagefilters.IImageFilter
        public boolean canConcurrence() {
            Map<String, List<Object>> map = this.mParams;
            return map == null || map.size() <= 1;
        }

        @Override // miui.imagefilters.IImageFilter
        public boolean initParams(String str, List<Object> list) {
            boolean z = false;
            if (list != null && list.size() != 0 && (z = ImageFilterUtils.setProperty(this, str, list.get(0))) && list.size() > 1) {
                if (this.mParams == null) {
                    this.mParams = new HashMap();
                }
                this.mParams.put(str, list);
            }
            return z;
        }

        @Override // miui.imagefilters.IImageFilter
        public void process(ImageData imageData) {
            if (canConcurrence()) {
                fillRandomParams(imageData);
                processData(imageData);
                return;
            }
            synchronized (this) {
                fillRandomParams(imageData);
                processData(imageData);
            }
        }

        public abstract void processData(ImageData imageData);

        @Override // miui.imagefilters.IImageFilter
        public void putOriginalImage(Bitmap bitmap) {
        }
    }

    /* loaded from: classes3.dex */
    public static class ImageFilterGroup {
        private IImageFilter[] mFilters;

        public ImageFilterGroup(IImageFilter[] iImageFilterArr) {
            this.mFilters = iImageFilterArr;
        }

        public ImageData processAll(Bitmap bitmap) {
            ImageData bitmapToImageData = ImageData.bitmapToImageData(bitmap);
            int length = this.mFilters.length;
            for (int i = 0; i < length; i++) {
                IImageFilter iImageFilter = this.mFilters[i];
                iImageFilter.putOriginalImage(bitmap);
                iImageFilter.process(bitmapToImageData);
            }
            return bitmapToImageData;
        }
    }

    boolean canConcurrence();

    boolean initParams(String str, List<Object> list);

    void process(ImageData imageData);

    void putOriginalImage(Bitmap bitmap);
}
