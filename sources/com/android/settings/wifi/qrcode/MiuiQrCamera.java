package com.android.settings.wifi.qrcode;

import android.content.Context;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import com.android.settings.wifi.ocr.WifiOcrController;
import com.android.settings.wifi.qrcode.MiuiQrCamera;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/* loaded from: classes2.dex */
public class MiuiQrCamera extends Handler {
    private static List<BarcodeFormat> FORMATS;
    private static Map<DecodeHintType, List<BarcodeFormat>> HINTS = new ArrayMap();
    private int curScanMode = -1;
    Camera mCamera;
    private int mCameraOrientation;
    private WeakReference<Context> mContext;
    private DecodingTask mDecodeTask;
    private boolean mIsReleasing;
    Camera.Parameters mParameters;
    private boolean mPaused;
    private Size mPreviewSize;
    private MultiFormatReader mReader;
    private ScannerCallback mScannerCallback;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class DecodingTask extends AsyncTask<Void, Void, String> {
        private byte[] mData;

        private DecodingTask() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$doInBackground$0(Semaphore semaphore, byte[] bArr, Camera camera) {
            this.mData = bArr;
            semaphore.release();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public String doInBackground(Void... voidArr) {
            if (MiuiQrCamera.this.mCamera == null) {
                return null;
            }
            final Semaphore semaphore = new Semaphore(0);
            while (true) {
                MiuiQrCamera.this.mCamera.setOneShotPreviewCallback(new Camera.PreviewCallback() { // from class: com.android.settings.wifi.qrcode.MiuiQrCamera$DecodingTask$$ExternalSyntheticLambda0
                    @Override // android.hardware.Camera.PreviewCallback
                    public final void onPreviewFrame(byte[] bArr, Camera camera) {
                        MiuiQrCamera.DecodingTask.this.lambda$doInBackground$0(semaphore, bArr, camera);
                    }
                });
                try {
                    semaphore.acquire();
                    MiuiQrCamera miuiQrCamera = MiuiQrCamera.this;
                    Result decode = miuiQrCamera.decode(this.mData, miuiQrCamera.mPreviewSize.getWidth(), MiuiQrCamera.this.mPreviewSize.getHeight(), 0);
                    if (decode != null && MiuiQrCamera.this.mScannerCallback.isValidQrcode(decode.getText())) {
                        MiuiQrCamera.this.curScanMode = 0;
                        return decode.getText();
                    }
                } catch (InterruptedException unused) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(String str) {
            if (str != null) {
                if (MiuiQrCamera.this.curScanMode == 0) {
                    MiuiQrCamera.this.mScannerCallback.handleSuccessfulResult(str);
                } else if (MiuiQrCamera.this.curScanMode == 1) {
                    MiuiQrCamera.this.mScannerCallback.handleOcrSuccessfulResult(str);
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public interface ScannerCallback {
        Size getViewSize();

        void handleCameraFailure();

        void handleOcrSuccessfulResult(String str);

        void handleSuccessfulResult(String str);

        boolean isValidQrcode(String str);

        void setTransform(Matrix matrix);
    }

    static {
        ArrayList arrayList = new ArrayList();
        FORMATS = arrayList;
        arrayList.add(BarcodeFormat.QR_CODE);
        HINTS.put(DecodeHintType.POSSIBLE_FORMATS, FORMATS);
    }

    public MiuiQrCamera(Context context, ScannerCallback scannerCallback) {
        this.mContext = new WeakReference<>(context);
        this.mScannerCallback = scannerCallback;
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        this.mReader = multiFormatReader;
        multiFormatReader.setHints(HINTS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Result decode(byte[] bArr, int i, int i2, int i3) {
        byte[] bArr2 = new byte[i * i2];
        Arrays.fill(bArr2, (byte) 0);
        for (int i4 = 0; i4 < i2; i4++) {
            for (int i5 = 0; i5 < i; i5++) {
                int i6 = (i4 * i) + i5;
                if (i6 >= bArr.length) {
                    break;
                }
                bArr2[(((i5 * i2) + i2) - i4) - 1] = bArr[i6];
            }
        }
        Result result = null;
        try {
        } catch (ReaderException unused) {
        } catch (Throwable th) {
            this.mReader.reset();
            throw th;
        }
        if (i3 != 0) {
            if (i3 == 1) {
                String detectText = WifiOcrController.getInstance().detectText(new MiuiPlanarYUVLuminanceSource(bArr2, i2, i, i2, i, false).renderCroppedGreyscaleBitmap(), this.mContext.get());
                if (!TextUtils.isEmpty(detectText)) {
                    result = new Result(detectText, null, null, null);
                }
            }
            this.mReader.reset();
            return result;
        }
        result = this.mReader.decode(new BinaryBitmap(new HybridBinarizer(new MiuiQrYUVLuminanceSource(bArr2, i2, i, 0, 0, i2, i, false))), HINTS);
        this.mReader.reset();
        return result;
    }

    private Size getBestPictureSize(Camera.Parameters parameters) {
        Camera.Size previewSize = parameters.getPreviewSize();
        double ratio = getRatio(previewSize.width, previewSize.height);
        ArrayList<Size> arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            double ratio2 = getRatio(size.width, size.height);
            if (ratio2 == ratio) {
                arrayList.add(new Size(size.width, size.height));
            } else if (Math.abs(ratio2 - ratio) < 0.1d) {
                arrayList2.add(new Size(size.width, size.height));
            }
        }
        if (arrayList.size() == 0 && arrayList2.size() == 0) {
            Log.d("MiuiQrCamera", "No proper picture size, return default picture size");
            Camera.Size pictureSize = parameters.getPictureSize();
            return new Size(pictureSize.width, pictureSize.height);
        }
        if (arrayList.size() == 0) {
            arrayList = arrayList2;
        }
        int i = Integer.MAX_VALUE;
        Size size2 = null;
        int i2 = previewSize.width * previewSize.height;
        for (Size size3 : arrayList) {
            int abs = Math.abs((size3.getWidth() * size3.getHeight()) - i2);
            if (abs < i) {
                size2 = size3;
                i = abs;
            }
        }
        return size2;
    }

    private Size getBestPreviewSize(Camera.Parameters parameters) {
        Size viewSize = this.mScannerCallback.getViewSize();
        double ratio = getRatio(viewSize.getWidth(), viewSize.getHeight());
        Size size = new Size(0, 0);
        double d = 1.0d;
        for (Camera.Size size2 : parameters.getSupportedPreviewSizes()) {
            double ratio2 = getRatio(size2.width, size2.height) - ratio;
            if (Math.abs(ratio2) / ratio <= d) {
                size = new Size(size2.width, size2.height);
                getRatio(size2.width, size2.height);
                d = Math.abs(ratio2) / ratio;
            }
        }
        return size;
    }

    private double getRatio(double d, double d2) {
        return d < d2 ? d / d2 : d2 / d;
    }

    private boolean initCamera(SurfaceHolder surfaceHolder) {
        if (this.mPaused) {
            Log.e("MiuiQrCamera", "Now is paused, do not open camera!");
            return false;
        }
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            try {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == 0) {
                    releaseCamera();
                    this.mCamera = Camera.open(i);
                    this.mCameraOrientation = cameraInfo.orientation;
                    break;
                }
            } catch (RuntimeException e) {
                Log.e("MiuiQrCamera", "Fail to open camera: " + e);
                this.mCamera = null;
                this.mScannerCallback.handleCameraFailure();
                return false;
            }
        }
        try {
            Camera camera = this.mCamera;
            if (camera != null) {
                camera.setPreviewDisplay(surfaceHolder);
                setCameraParameter();
                setTransformationMatrix();
                if (startPreview()) {
                    return true;
                }
                throw new IOException("Lost contex");
            }
            throw new IOException("Cannot find available back camera");
        } catch (IOException e2) {
            Log.e("MiuiQrCamera", "Fail to startPreview camera: " + e2);
            this.mCamera = null;
            this.mScannerCallback.handleCameraFailure();
            return false;
        }
    }

    private void setTransformationMatrix() {
        float f;
        boolean z = this.mContext.get().getResources().getConfiguration().orientation == 1;
        Size size = this.mPreviewSize;
        int width = z ? size.getWidth() : size.getHeight();
        int height = z ? this.mPreviewSize.getHeight() : this.mPreviewSize.getWidth();
        float ratio = (float) getRatio(width, height);
        float f2 = 1.0f;
        if (width > height) {
            f = 1.0f / ratio;
        } else {
            f2 = 1.0f / ratio;
            f = 1.0f;
        }
        Matrix matrix = new Matrix();
        matrix.setScale(f2, f);
        this.mScannerCallback.setTransform(matrix);
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x0057  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean startPreview() {
        /*
            r5 = this;
            java.lang.ref.WeakReference<android.content.Context> r0 = r5.mContext
            java.lang.Object r0 = r0.get()
            r1 = 0
            if (r0 != 0) goto La
            return r1
        La:
            java.lang.ref.WeakReference<android.content.Context> r0 = r5.mContext
            java.lang.Object r0 = r0.get()
            android.content.Context r0 = (android.content.Context) r0
            java.lang.String r2 = "window"
            java.lang.Object r0 = r0.getSystemService(r2)
            android.view.WindowManager r0 = (android.view.WindowManager) r0
            android.view.Display r0 = r0.getDefaultDisplay()
            int r0 = r0.getRotation()
            r2 = 1
            if (r0 == 0) goto L2e
            if (r0 == r2) goto L36
            r3 = 2
            if (r0 == r3) goto L33
            r3 = 3
            if (r0 == r3) goto L30
        L2e:
            r0 = r1
            goto L38
        L30:
            r0 = 270(0x10e, float:3.78E-43)
            goto L38
        L33:
            r0 = 180(0xb4, float:2.52E-43)
            goto L38
        L36:
            r0 = 90
        L38:
            int r3 = r5.mCameraOrientation
            int r3 = r3 - r0
            int r3 = r3 + 360
            int r3 = r3 % 360
            android.hardware.Camera r0 = r5.mCamera
            r0.setDisplayOrientation(r3)
            android.hardware.Camera r0 = r5.mCamera     // Catch: java.lang.RuntimeException -> L67
            r0.startPreview()     // Catch: java.lang.RuntimeException -> L67
            android.hardware.Camera$Parameters r0 = r5.mParameters
            java.lang.String r0 = r0.getFocusMode()
            java.lang.String r1 = "auto"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L66
            android.hardware.Camera r0 = r5.mCamera
            r1 = 0
            r0.autoFocus(r1)
            android.os.Message r0 = r5.obtainMessage(r2)
            r3 = 1500(0x5dc, double:7.41E-321)
            r5.sendMessageDelayed(r0, r3)
        L66:
            return r2
        L67:
            r5 = move-exception
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Fail to start preview: "
            r0.append(r2)
            r0.append(r5)
            java.lang.String r5 = r0.toString()
            java.lang.String r0 = "MiuiQrCamera"
            android.util.Log.e(r0, r5)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.qrcode.MiuiQrCamera.startPreview():boolean");
    }

    protected void decodeImage(BinaryBitmap binaryBitmap) {
        Result result;
        try {
            result = this.mReader.decodeWithState(binaryBitmap);
            this.mReader.reset();
        } catch (ReaderException unused) {
            this.mReader.reset();
            result = null;
        } catch (Throwable th) {
            this.mReader.reset();
            throw th;
        }
        if (result != null) {
            this.mScannerCallback.handleSuccessfulResult(result.getText());
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (message.what == 1) {
            this.mCamera.autoFocus(null);
            sendMessageDelayed(obtainMessage(1), 1500L);
            return;
        }
        Log.d("MiuiQrCamera", "Unexpected Message: " + message.what);
    }

    public boolean isDecodeTaskAlive() {
        return this.mDecodeTask != null;
    }

    public void onPause(boolean z) {
        this.mPaused = z;
    }

    public void releaseCamera() {
        Camera camera = this.mCamera;
        if (camera != null) {
            this.mIsReleasing = true;
            camera.release();
            this.mCamera = null;
            this.mIsReleasing = false;
        }
    }

    void setCameraParameter() {
        Camera.Parameters parameters = this.mCamera.getParameters();
        this.mParameters = parameters;
        Size bestPreviewSize = getBestPreviewSize(parameters);
        this.mPreviewSize = bestPreviewSize;
        this.mParameters.setPreviewSize(bestPreviewSize.getWidth(), this.mPreviewSize.getHeight());
        Size bestPictureSize = getBestPictureSize(this.mParameters);
        this.mParameters.setPictureSize(bestPictureSize.getWidth(), bestPictureSize.getHeight());
        List<String> supportedFlashModes = this.mParameters.getSupportedFlashModes();
        if (supportedFlashModes != null && supportedFlashModes.contains("off")) {
            this.mParameters.setFlashMode("off");
        }
        List<String> supportedFocusModes = this.mParameters.getSupportedFocusModes();
        if (supportedFocusModes.contains("continuous-picture")) {
            this.mParameters.setFocusMode("continuous-picture");
        } else if (supportedFocusModes.contains("auto")) {
            this.mParameters.setFocusMode("auto");
        }
        this.mCamera.setParameters(this.mParameters);
    }

    public void start(SurfaceHolder surfaceHolder) {
        if (initCamera(surfaceHolder) && this.mDecodeTask == null) {
            DecodingTask decodingTask = new DecodingTask();
            this.mDecodeTask = decodingTask;
            decodingTask.executeOnExecutor(Executors.newSingleThreadExecutor(), new Void[0]);
        }
    }

    public void stop() {
        Camera camera;
        removeMessages(1);
        DecodingTask decodingTask = this.mDecodeTask;
        if (decodingTask != null) {
            decodingTask.cancel(true);
            this.mDecodeTask = null;
        }
        if (this.mIsReleasing || (camera = this.mCamera) == null) {
            return;
        }
        try {
            camera.stopPreview();
        } catch (RuntimeException e) {
            Log.e("MiuiQrCamera", "Fail to stop preview: " + e);
        }
    }
}
