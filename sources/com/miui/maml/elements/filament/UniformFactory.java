package com.miui.maml.elements.filament;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Surface;
import com.google.android.filament.Engine;
import com.google.android.filament.MaterialInstance;
import com.google.android.filament.Stream;
import com.google.android.filament.Texture;
import com.google.android.filament.TextureSampler;
import com.google.android.filament.android.TextureHelper;
import com.miui.maml.ResourceManager;
import com.miui.maml.data.Expression;
import com.miui.maml.data.Variables;
import com.miui.maml.util.MamlMediaDataSource;
import com.miui.maml.util.Utils;
import java.lang.ref.WeakReference;
import miui.yellowpage.YellowPageContract;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class UniformFactory {

    /* loaded from: classes2.dex */
    public static class BoolUniform extends Uniform {
        public BoolUniform(Element element, Variables variables, Context context, int i) {
            super(element, variables, context, i);
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.Uniform
        protected void doRefresh() {
            try {
                Expression[] expressionArr = this.mParams;
                int length = expressionArr.length;
                if (length == 1) {
                    this.mMaterial.setParameter(this.mName, expressionArr[0].evaluate() > 0.0d);
                } else if (length == 2) {
                    this.mMaterial.setParameter(this.mName, expressionArr[0].evaluate() > 0.0d, this.mParams[1].evaluate() > 0.0d);
                } else if (length == 3) {
                    this.mMaterial.setParameter(this.mName, expressionArr[0].evaluate() > 0.0d, this.mParams[1].evaluate() > 0.0d, this.mParams[2].evaluate() > 0.0d);
                } else if (length == 4) {
                    this.mMaterial.setParameter(this.mName, expressionArr[0].evaluate() > 0.0d, this.mParams[1].evaluate() > 0.0d, this.mParams[2].evaluate() > 0.0d, this.mParams[3].evaluate() > 0.0d);
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.Uniform
        protected void doUpdateParams() {
        }
    }

    /* loaded from: classes2.dex */
    public static class FloatUniform extends Uniform {
        public FloatUniform(Element element, Variables variables, Context context, int i) {
            super(element, variables, context, i);
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.Uniform
        protected void doRefresh() {
            try {
                Expression[] expressionArr = this.mParams;
                int length = expressionArr.length;
                if (length == 1) {
                    this.mMaterial.setParameter(this.mName, (float) expressionArr[0].evaluate());
                } else if (length == 2) {
                    this.mMaterial.setParameter(this.mName, (float) expressionArr[0].evaluate(), (float) this.mParams[1].evaluate());
                } else if (length == 3) {
                    this.mMaterial.setParameter(this.mName, (float) expressionArr[0].evaluate(), (float) this.mParams[1].evaluate(), (float) this.mParams[2].evaluate());
                } else if (length == 4) {
                    this.mMaterial.setParameter(this.mName, (float) expressionArr[0].evaluate(), (float) this.mParams[1].evaluate(), (float) this.mParams[2].evaluate(), (float) this.mParams[3].evaluate());
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.Uniform
        protected void doUpdateParams() {
        }
    }

    /* loaded from: classes2.dex */
    public static class ImageUniform extends TextureUniform {
        private String mFinalStr;
        private Expression mId;
        private int mImageSrcType;
        private Expression mSrc;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class setImageTextureTask extends AsyncTask<Void, Void, Bitmap> {
            private String mFilePath;
            private WeakReference<ResourceManager> mManagerRef;
            private WeakReference<ImageUniform> mUniformRef;

            public setImageTextureTask(String str, ResourceManager resourceManager, ImageUniform imageUniform) {
                this.mFilePath = str;
                this.mManagerRef = new WeakReference<>(resourceManager);
                this.mUniformRef = new WeakReference<>(imageUniform);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Bitmap doInBackground(Void... voidArr) {
                ResourceManager resourceManager = this.mManagerRef.get();
                if (resourceManager != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPremultiplied = true;
                    Bitmap decodeStream = BitmapFactory.decodeStream(resourceManager.getInputStream(this.mFilePath), null, options);
                    if (decodeStream == null) {
                        Log.w("Uniform", "Wrong bitmap path " + this.mFilePath);
                    }
                    return decodeStream;
                }
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Bitmap bitmap) {
                ImageUniform imageUniform = this.mUniformRef.get();
                if (imageUniform != null) {
                    imageUniform.setImageTexture(bitmap);
                }
            }
        }

        public ImageUniform(Element element, Variables variables, Context context, int i) {
            super(element, variables, context, i);
            this.mImageSrcType = 2;
            doUpdateParamsImpl();
        }

        private void doUpdateParamsImpl() {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParams;
                if (expressionArr.length > 0) {
                    this.mSrc = expressionArr[0];
                }
                if (expressionArr.length > 1) {
                    String evaluateStr = expressionArr[1].evaluateStr();
                    if ("file".equals(evaluateStr)) {
                        this.mImageSrcType = 2;
                    } else if ("bitmap".equals(evaluateStr)) {
                        this.mImageSrcType = 1;
                    }
                }
                Expression[] expressionArr2 = this.mParams;
                if (expressionArr2.length > 2) {
                    this.mId = expressionArr2[2];
                }
            }
        }

        private void setImage() {
            Object obj;
            Expression expression = this.mSrc;
            if (expression != null) {
                int i = this.mImageSrcType;
                if (i == 2) {
                    String evaluateStr = expression.evaluateStr();
                    Expression expression2 = this.mId;
                    if (expression2 != null) {
                        evaluateStr = Utils.addFileNameSuffix(evaluateStr, String.valueOf((long) expression2.evaluate()));
                    }
                    if (!TextUtils.isEmpty(evaluateStr) && !evaluateStr.equals(this.mFinalStr)) {
                        new setImageTextureTask(evaluateStr, this.mManager, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    }
                    this.mFinalStr = evaluateStr;
                } else if (i == 1) {
                    String evaluateStr2 = expression.evaluateStr();
                    if (TextUtils.isEmpty(evaluateStr2) || (obj = this.mVariables.get(evaluateStr2)) == null || !(obj instanceof Bitmap)) {
                        return;
                    }
                    setImageTexture((Bitmap) obj);
                    this.mVariables.put(evaluateStr2, (Object) null);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setImageTexture(Bitmap bitmap) {
            Engine engine = this.mEngine;
            if (engine == null || !engine.isValid() || bitmap == null) {
                return;
            }
            if (this.mTexture == null) {
                this.mTexture = new Texture.Builder().width(bitmap.getWidth()).height(bitmap.getHeight()).sampler(Texture.Sampler.SAMPLER_2D).format(Texture.InternalFormat.SRGB8_A8).levels(255).build(this.mEngine);
            }
            TextureHelper.setBitmap(this.mEngine, this.mTexture, 0, bitmap);
            this.mTexture.generateMipmaps(this.mEngine);
            MaterialInstance materialInstance = this.mMaterial;
            if (materialInstance != null) {
                try {
                    materialInstance.setParameter(this.mName, this.mTexture, this.mSampler);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.Uniform
        protected void doRefresh() {
            setImage();
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.Uniform
        protected void doUpdateParams() {
            this.mImageSrcType = 2;
            this.mSrc = null;
            this.mId = null;
            doUpdateParamsImpl();
            setImage();
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.TextureUniform, com.miui.maml.elements.filament.UniformFactory.Uniform
        public /* bridge */ /* synthetic */ void finish() {
            super.finish();
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.Uniform
        public void init(ResourceManager resourceManager, Engine engine, MaterialInstance materialInstance) {
            super.init(resourceManager, engine, materialInstance);
            setImage();
        }
    }

    /* loaded from: classes2.dex */
    public static class IntUniform extends Uniform {
        public IntUniform(Element element, Variables variables, Context context, int i) {
            super(element, variables, context, i);
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.Uniform
        protected void doRefresh() {
            try {
                Expression[] expressionArr = this.mParams;
                int length = expressionArr.length;
                if (length == 1) {
                    this.mMaterial.setParameter(this.mName, (int) expressionArr[0].evaluate());
                } else if (length == 2) {
                    this.mMaterial.setParameter(this.mName, (int) expressionArr[0].evaluate(), (int) this.mParams[1].evaluate());
                } else if (length == 3) {
                    this.mMaterial.setParameter(this.mName, (int) expressionArr[0].evaluate(), (int) this.mParams[1].evaluate(), (int) this.mParams[2].evaluate());
                } else if (length == 4) {
                    this.mMaterial.setParameter(this.mName, (int) expressionArr[0].evaluate(), (int) this.mParams[1].evaluate(), (int) this.mParams[2].evaluate(), (int) this.mParams[3].evaluate());
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.Uniform
        protected void doUpdateParams() {
        }
    }

    /* loaded from: classes2.dex */
    public static class OffscreenUniform extends TextureUniform {
        private String mOffscreenName;
        private Expression mOffscreenNameExp;
        private ArrayMap<String, CustFrameBuffer> mOffscreens;

        public OffscreenUniform(Element element, Variables variables, Context context, int i) {
            super(element, variables, context, i);
            doUpdateParamsImpl();
        }

        private void doUpdateParamsImpl() {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParams;
                if (expressionArr.length > 0) {
                    this.mOffscreenNameExp = expressionArr[0];
                }
            }
        }

        private void setOffscreen() {
            CustFrameBuffer custFrameBuffer;
            Expression expression = this.mOffscreenNameExp;
            if (expression != null) {
                String evaluateStr = expression.evaluateStr();
                if (TextUtils.isEmpty(evaluateStr) && !evaluateStr.equals(this.mOffscreenName) && (custFrameBuffer = this.mOffscreens.get(evaluateStr)) != null) {
                    Texture texture = custFrameBuffer.getTexture();
                    this.mTexture = texture;
                    MaterialInstance materialInstance = this.mMaterial;
                    if (materialInstance != null && texture != null) {
                        try {
                            materialInstance.setParameter(this.mName, texture, this.mSampler);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                }
                this.mOffscreenName = evaluateStr;
            }
        }

        public void checkOffscreen(ArrayMap<String, CustFrameBuffer> arrayMap) {
            CustFrameBuffer custFrameBuffer;
            this.mOffscreens = arrayMap;
            Expression expression = this.mOffscreenNameExp;
            if (expression != null) {
                String evaluateStr = expression.evaluateStr();
                if (TextUtils.isEmpty(evaluateStr) || (custFrameBuffer = arrayMap.get(evaluateStr)) == null) {
                    return;
                }
                Texture texture = custFrameBuffer.getTexture();
                this.mTexture = texture;
                MaterialInstance materialInstance = this.mMaterial;
                if (materialInstance == null || texture == null) {
                    return;
                }
                try {
                    materialInstance.setParameter(this.mName, texture, this.mSampler);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.Uniform
        protected void doRefresh() {
            setOffscreen();
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.Uniform
        protected void doUpdateParams() {
            this.mOffscreenNameExp = null;
            doUpdateParamsImpl();
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.TextureUniform, com.miui.maml.elements.filament.UniformFactory.Uniform
        public void finish() {
            this.mEngine = null;
            this.mManager = null;
            this.mMaterial = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class TextureUniform extends Uniform {
        protected TextureSampler mSampler;
        protected Texture mTexture;

        public TextureUniform(Element element, Variables variables, Context context, int i) {
            super(element, variables, context, i);
            this.mSampler = new TextureSampler(TextureSampler.MinFilter.LINEAR, TextureSampler.MagFilter.LINEAR, TextureSampler.WrapMode.CLAMP_TO_EDGE);
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.Uniform
        public void finish() {
            Texture texture = this.mTexture;
            if (texture != null) {
                this.mEngine.destroyTexture(texture);
                this.mTexture = null;
            }
            super.finish();
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Uniform {
        protected boolean mAutoRefresh;
        protected Context mContext;
        protected Engine mEngine;
        protected boolean mIsParamsValid;
        protected ResourceManager mManager;
        protected MaterialInstance mMaterial;
        protected String mName;
        protected Expression[] mParams;
        protected int mType;
        protected Variables mVariables;

        public Uniform(Element element, Variables variables, Context context, int i) {
            this.mContext = context.getApplicationContext();
            this.mVariables = variables;
            this.mName = element.getAttribute("name");
            this.mType = i;
            Utils.getAttrAsInt(element, "type", 1);
            Expression[] buildMultiple = Expression.buildMultiple(variables, element.getAttribute(YellowPageContract.HttpRequest.PARAMS));
            this.mParams = buildMultiple;
            this.mIsParamsValid = isExpressionsValid(buildMultiple);
            this.mAutoRefresh = Boolean.parseBoolean(element.getAttribute("refresh"));
        }

        private boolean isExpressionsValid(Expression[] expressionArr) {
            if (expressionArr != null) {
                int length = expressionArr.length;
                int i = 0;
                while (i < length && expressionArr[i] != null) {
                    i++;
                }
                return i == expressionArr.length;
            }
            return false;
        }

        protected abstract void doRefresh();

        protected abstract void doUpdateParams();

        public void finish() {
            this.mEngine = null;
            this.mManager = null;
            this.mMaterial = null;
        }

        public String getName() {
            return this.mName;
        }

        public void init(ResourceManager resourceManager, Engine engine, MaterialInstance materialInstance) {
            this.mEngine = engine;
            this.mMaterial = materialInstance;
            this.mManager = resourceManager;
        }

        public boolean isAutoRefresh() {
            return this.mAutoRefresh;
        }

        public void refresh() {
            if (TextUtils.isEmpty(this.mName) || this.mMaterial == null || !this.mIsParamsValid) {
                return;
            }
            doRefresh();
        }

        public void tryPause() {
        }

        public void tryResume() {
        }

        public void updateUniform(boolean z, Expression[] expressionArr) {
            this.mAutoRefresh = z;
            if (this.mParams != null) {
                if (isExpressionsValid(expressionArr)) {
                    this.mParams = expressionArr;
                } else {
                    this.mParams = null;
                }
                doUpdateParams();
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class VideoUniform extends TextureUniform {
        private int mDuration;
        private int mLastPosition;
        private boolean mLoopPlay;
        private MediaPlayer mMediaPlayer;
        private String mPath;
        private Expression mPercent;
        private MamlMediaDataSource mVideoDataSource;
        private Stream mVideoStream;
        private Surface mVideoSurface;
        private SurfaceTexture mVideoSurfaceTexture;

        public VideoUniform(Element element, Variables variables, Context context, int i) {
            super(element, variables, context, i);
            this.mLoopPlay = true;
            this.mLastPosition = -1;
            doUpdateParamsImpl();
        }

        private void doUpdateParamsImpl() {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParams;
                if (expressionArr.length > 0) {
                    this.mPath = expressionArr[0].evaluateStr();
                    Expression[] expressionArr2 = this.mParams;
                    if (expressionArr2.length > 1) {
                        this.mLoopPlay = expressionArr2[1].evaluate() > 0.0d;
                    }
                    Expression[] expressionArr3 = this.mParams;
                    if (expressionArr3.length > 2) {
                        this.mPercent = expressionArr3[2];
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void seekToPosition() {
            if (this.mMediaPlayer != null) {
                Expression expression = this.mPercent;
                float evaluate = expression != null ? (float) expression.evaluate() : 0.0f;
                int i = this.mDuration;
                int i2 = (int) (i * evaluate);
                if (i2 <= i) {
                    i = i2 < 0 ? 0 : i2;
                }
                if (i != this.mLastPosition) {
                    if (Build.VERSION.SDK_INT >= 26) {
                        this.mMediaPlayer.seekTo(i, 3);
                    } else {
                        this.mMediaPlayer.seekTo(i);
                    }
                    this.mLastPosition = i;
                }
            }
        }

        private void setPlayer() {
            MamlMediaDataSource mamlMediaDataSource = this.mVideoDataSource;
            if (mamlMediaDataSource != null) {
                mamlMediaDataSource.close();
            }
            MamlMediaDataSource mamlMediaDataSource2 = new MamlMediaDataSource(this.mContext, this.mManager, this.mPath);
            this.mVideoDataSource = mamlMediaDataSource2;
            mamlMediaDataSource2.tryToGenerateMemoryFile();
            try {
                MediaPlayer mediaPlayer = this.mMediaPlayer;
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    this.mMediaPlayer.release();
                }
                MediaPlayer mediaPlayer2 = new MediaPlayer();
                this.mMediaPlayer = mediaPlayer2;
                mediaPlayer2.setVolume(0.0f, 0.0f);
                this.mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.miui.maml.elements.filament.UniformFactory.VideoUniform.1
                    @Override // android.media.MediaPlayer.OnPreparedListener
                    public void onPrepared(MediaPlayer mediaPlayer3) {
                        if (VideoUniform.this.mLoopPlay) {
                            VideoUniform.this.mMediaPlayer.start();
                            VideoUniform.this.mMediaPlayer.setLooping(true);
                            return;
                        }
                        VideoUniform videoUniform = VideoUniform.this;
                        videoUniform.mDuration = videoUniform.mMediaPlayer.getDuration();
                        VideoUniform.this.seekToPosition();
                    }
                });
                this.mMediaPlayer.setDataSource(this.mVideoDataSource);
                this.mMediaPlayer.setVideoScalingMode(2);
                this.mMediaPlayer.prepare();
                this.mMediaPlayer.setSurface(this.mVideoSurface);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void setVideoTexture() {
            this.mTexture = new Texture.Builder().sampler(Texture.Sampler.SAMPLER_EXTERNAL).format(Texture.InternalFormat.RGB8).build(this.mEngine);
            try {
                this.mVideoSurfaceTexture = new SurfaceTexture(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SurfaceTexture surfaceTexture = this.mVideoSurfaceTexture;
            if (surfaceTexture != null) {
                surfaceTexture.detachFromGLContext();
                this.mVideoSurface = new Surface(this.mVideoSurfaceTexture);
                Stream build = new Stream.Builder().stream(this.mVideoSurfaceTexture).build(this.mEngine);
                this.mVideoStream = build;
                this.mTexture.setExternalStream(this.mEngine, build);
                try {
                    this.mMaterial.setParameter(this.mName, this.mTexture, this.mSampler);
                } catch (IllegalStateException e2) {
                    e2.printStackTrace();
                }
                setPlayer();
            }
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.Uniform
        protected void doRefresh() {
            if (this.mLoopPlay) {
                return;
            }
            seekToPosition();
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.Uniform
        protected void doUpdateParams() {
            this.mPath = null;
            this.mLoopPlay = true;
            this.mLastPosition = -1;
            this.mPercent = null;
            doUpdateParamsImpl();
            setPlayer();
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.TextureUniform, com.miui.maml.elements.filament.UniformFactory.Uniform
        public void finish() {
            MediaPlayer mediaPlayer = this.mMediaPlayer;
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                this.mMediaPlayer.release();
                this.mMediaPlayer = null;
            }
            SurfaceTexture surfaceTexture = this.mVideoSurfaceTexture;
            if (surfaceTexture != null) {
                surfaceTexture.release();
                this.mVideoSurfaceTexture = null;
            }
            Surface surface = this.mVideoSurface;
            if (surface != null) {
                surface.release();
                this.mVideoSurface = null;
            }
            Stream stream = this.mVideoStream;
            if (stream != null) {
                this.mEngine.destroyStream(stream);
            }
            MamlMediaDataSource mamlMediaDataSource = this.mVideoDataSource;
            if (mamlMediaDataSource != null) {
                mamlMediaDataSource.close();
            }
            super.finish();
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.Uniform
        public void init(ResourceManager resourceManager, Engine engine, MaterialInstance materialInstance) {
            super.init(resourceManager, engine, materialInstance);
            setVideoTexture();
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.Uniform
        public void tryPause() {
            MediaPlayer mediaPlayer = this.mMediaPlayer;
            if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
                return;
            }
            this.mMediaPlayer.pause();
        }

        @Override // com.miui.maml.elements.filament.UniformFactory.Uniform
        public void tryResume() {
            MediaPlayer mediaPlayer = this.mMediaPlayer;
            if (mediaPlayer == null || mediaPlayer.isPlaying() || !this.mLoopPlay) {
                return;
            }
            this.mMediaPlayer.start();
        }
    }

    public static Uniform createUniform(Element element, Variables variables, Context context) {
        String attribute = element.getAttribute("type");
        attribute.hashCode();
        char c = 65535;
        switch (attribute.hashCode()) {
            case -1962582373:
                if (attribute.equals("offscreen")) {
                    c = 0;
                    break;
                }
                break;
            case 104431:
                if (attribute.equals("int")) {
                    c = 1;
                    break;
                }
                break;
            case 3029738:
                if (attribute.equals("bool")) {
                    c = 2;
                    break;
                }
                break;
            case 97526364:
                if (attribute.equals("float")) {
                    c = 3;
                    break;
                }
                break;
            case 100313435:
                if (attribute.equals(YellowPageContract.ImageLookup.DIRECTORY_IMAGE)) {
                    c = 4;
                    break;
                }
                break;
            case 112202875:
                if (attribute.equals("video")) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return new OffscreenUniform(element, variables, context, 5);
            case 1:
                return new IntUniform(element, variables, context, 1);
            case 2:
                return new BoolUniform(element, variables, context, 3);
            case 3:
                return new FloatUniform(element, variables, context, 2);
            case 4:
                return new ImageUniform(element, variables, context, 4);
            case 5:
                return new VideoUniform(element, variables, context, 6);
            default:
                return null;
        }
    }
}
