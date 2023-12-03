package miui.imagefilters;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes3.dex */
public class ImageFilterBuilder {
    private static final String TAG = "ImageFilterBuilder";
    private String mFilterName;
    private boolean mIgnoreWhenNotSupported = false;
    private List<ParamData> mParams = new ArrayList();

    /* loaded from: classes3.dex */
    public static class NoSupportException extends Exception {
        public NoSupportException(String str) {
            super(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class ParamData {
        boolean mIgnoreWhenNotSupported;
        String mParamName;
        List<Object> mParamValues;

        private ParamData() {
            this.mIgnoreWhenNotSupported = false;
        }
    }

    public void addParam(String str, List<Object> list, boolean z) {
        this.mParams.add(new ParamData(str, list, z) { // from class: miui.imagefilters.ImageFilterBuilder.1
            final /* synthetic */ boolean val$allowIgnore;
            final /* synthetic */ String val$paramName;
            final /* synthetic */ List val$paramValues;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                this.val$paramName = str;
                this.val$paramValues = list;
                this.val$allowIgnore = z;
                this.mParamName = str;
                this.mParamValues = list;
                this.mIgnoreWhenNotSupported = z;
            }
        });
    }

    public IImageFilter createImageFilter() throws NoSupportException {
        IImageFilter transformFilter;
        if ("Hsl".equalsIgnoreCase(this.mFilterName)) {
            transformFilter = new HslWrapFilter();
        } else if ("Edges".equalsIgnoreCase(this.mFilterName)) {
            transformFilter = new EdgesFilter();
        } else if ("Levels".equalsIgnoreCase(this.mFilterName)) {
            transformFilter = new LevelsFilter();
        } else if ("GrayScale".equalsIgnoreCase(this.mFilterName)) {
            transformFilter = new GrayScaleFilter();
        } else if ("BlendImage".equalsIgnoreCase(this.mFilterName)) {
            transformFilter = new BlendImageFilter();
        } else if ("ColorMatrix".equalsIgnoreCase(this.mFilterName)) {
            transformFilter = new ColorMatrixFilter();
        } else if ("Convolution".equalsIgnoreCase(this.mFilterName)) {
            transformFilter = new ConvolutionFilter();
        } else if ("Threshold".equalsIgnoreCase(this.mFilterName)) {
            transformFilter = new ThresholdFilter();
        } else if ("Spread".equalsIgnoreCase(this.mFilterName)) {
            transformFilter = new SpreadFilter();
        } else if (!"Transform".equalsIgnoreCase(this.mFilterName)) {
            Log.w(TAG, "unknown filter:" + this.mFilterName);
            if (this.mIgnoreWhenNotSupported) {
                return null;
            }
            throw new NoSupportException("filter:" + this.mFilterName + " not support.");
        } else {
            transformFilter = new TransformFilter();
        }
        for (ParamData paramData : this.mParams) {
            if (!transformFilter.initParams(paramData.mParamName, paramData.mParamValues) && !paramData.mIgnoreWhenNotSupported) {
                if (this.mIgnoreWhenNotSupported) {
                    return null;
                }
                throw new NoSupportException("param:" + paramData.mParamName + " not support.");
            }
        }
        return transformFilter;
    }

    public void setFilterName(String str) {
        this.mFilterName = str;
    }

    public void setIgnoreWhenNotSupported(boolean z) {
        this.mIgnoreWhenNotSupported = z;
    }
}
