package src.com.android.settings.emergency.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import com.android.settings.emergency.service.LocationService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

@SuppressLint({"MissingPermission"})
@TargetApi(21)
/* loaded from: classes5.dex */
public class CameraUtils {
    private static String TAG = "SOS-CameraUtils";
    private static final Object mObject = new Object();
    private Bitmap mBitmap;
    private String mBitmapName;
    private String mBitmapPathBehind;
    private String mBitmapPathFront;
    private CameraDevice mCameraDevice;
    private Handler mCameraHandler;
    private int mCameraId;
    private CameraManager mCameraManager;
    private CameraCaptureSession mCaptureSession;
    private Context mContext;
    private HandlerThread mHandlerThread;
    private Handler mMainHandler;
    private ImageReader mPhotoImageReader;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private Surface mPreviewSurface;
    private LocationService.ISosSnapListener mSosSnapListener;
    private SurfaceTexture mSurfaceTexture;
    private List<Surface> mSurfaces;
    private CameraCaptureSession.StateCallback mSessionCallback = new CameraCaptureSession.StateCallback() { // from class: src.com.android.settings.emergency.util.CameraUtils.3
        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
            Log.d(CameraUtils.TAG, "onConfigureFailed: ");
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
            synchronized (CameraUtils.this) {
                if (CameraUtils.this.mCameraDevice == null) {
                    Log.e(CameraUtils.TAG, "onConfigured: CameraDevice was already closed.");
                    cameraCaptureSession.close();
                    return;
                }
                CameraUtils.this.mCaptureSession = cameraCaptureSession;
                Log.d(CameraUtils.TAG, "startPreview");
                try {
                    CameraUtils.this.mCaptureSession.setRepeatingRequest(CameraUtils.this.mPreviewRequestBuilder.build(), CameraUtils.this.mCaptureCallback, CameraUtils.this.mCameraHandler);
                } catch (CameraAccessException e) {
                    Log.e(CameraUtils.TAG, e.toString());
                }
                CameraUtils.this.takePicture();
            }
        }
    };
    private final CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() { // from class: src.com.android.settings.emergency.util.CameraUtils.4
        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public void onCaptureCompleted(CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult) {
            Log.d(CameraUtils.TAG, "onCaptureCompleted: ");
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public void onCaptureFailed(CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, CaptureFailure captureFailure) {
            super.onCaptureFailed(cameraCaptureSession, captureRequest, captureFailure);
            Log.e(CameraUtils.TAG, "onCaptureFailed");
        }
    };
    private final ImageReader.OnImageAvailableListener mPhotoAvailableListener = new ImageReader.OnImageAvailableListener() { // from class: src.com.android.settings.emergency.util.CameraUtils.5
        @Override // android.media.ImageReader.OnImageAvailableListener
        public void onImageAvailable(ImageReader imageReader) {
            Image acquireNextImage;
            try {
                try {
                    acquireNextImage = imageReader.acquireNextImage();
                } catch (Exception e) {
                    Log.e(CameraUtils.TAG, e.toString());
                }
                if (acquireNextImage == null) {
                    if (acquireNextImage != null) {
                        acquireNextImage.close();
                    }
                    return;
                }
                try {
                    byte[] firstPlane = CameraUtils.getFirstPlane(acquireNextImage);
                    if (firstPlane != null) {
                        Log.d(CameraUtils.TAG, "onImageAvailable: ");
                        CameraUtils.this.mBitmap = CameraUtils.bytes2Bitmap(firstPlane);
                        CameraUtils cameraUtils = CameraUtils.this;
                        ThreadPool.execute(new SaveBitmapAndSendRunnable(cameraUtils, cameraUtils.changeBitmap(cameraUtils.mBitmap)));
                    }
                    acquireNextImage.close();
                } catch (Throwable th) {
                    try {
                        acquireNextImage.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } finally {
                CameraUtils.this.release();
            }
        }
    };
    private CameraDevice.StateCallback mCameraStateCallback = new CameraDevice.StateCallback() { // from class: src.com.android.settings.emergency.util.CameraUtils.6
        @Override // android.hardware.camera2.CameraDevice.StateCallback
        public void onDisconnected(CameraDevice cameraDevice) {
            Log.w(CameraUtils.TAG, "onDisconnected");
            CameraUtils.this.release();
        }

        @Override // android.hardware.camera2.CameraDevice.StateCallback
        public void onError(CameraDevice cameraDevice, int i) {
            Log.e(CameraUtils.TAG, "onError: " + i);
            CameraUtils.this.release();
        }

        @Override // android.hardware.camera2.CameraDevice.StateCallback
        public void onOpened(CameraDevice cameraDevice) {
            synchronized (CameraUtils.this) {
                Log.d(CameraUtils.TAG, "onOpened: ");
                CameraUtils.this.mCameraDevice = cameraDevice;
                CameraUtils.this.preparePhotoImageReader();
                CameraUtils.this.openCamera();
            }
        }
    };

    /* loaded from: classes5.dex */
    public static class SaveBitmapAndSendRunnable implements Runnable {
        private Bitmap bm;
        private WeakReference<CameraUtils> cameraUtilsWeakReference;

        public SaveBitmapAndSendRunnable(CameraUtils cameraUtils, Bitmap bitmap) {
            this.cameraUtilsWeakReference = new WeakReference<>(cameraUtils);
            this.bm = bitmap;
        }

        @Override // java.lang.Runnable
        public void run() {
            String str;
            String iOException;
            FileOutputStream fileOutputStream;
            synchronized (CameraUtils.mObject) {
                CameraUtils cameraUtils = this.cameraUtilsWeakReference.get();
                if (cameraUtils == null) {
                    return;
                }
                FileOutputStream fileOutputStream2 = null;
                try {
                    try {
                        Log.i(CameraUtils.TAG, "save bitmap, cameraUtils.mCameraId ：" + cameraUtils.mCameraId);
                        cameraUtils.mBitmapName = cameraUtils.mCameraId == 0 ? cameraUtils.mBitmapPathBehind : cameraUtils.mBitmapPathFront;
                        File file = new File(cameraUtils.mBitmapName);
                        if (file.exists()) {
                            file.delete();
                        }
                        fileOutputStream = new FileOutputStream(file);
                    } catch (Exception e) {
                        e = e;
                    }
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    this.bm.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                    fileOutputStream.flush();
                    Log.i(CameraUtils.TAG, "save bitmap finish");
                    cameraUtils.mSosSnapListener.executeSendSnap(cameraUtils.mBitmapName);
                    try {
                        fileOutputStream.close();
                    } catch (IOException e2) {
                        str = CameraUtils.TAG;
                        iOException = e2.toString();
                        Log.e(str, iOException);
                    }
                } catch (Exception e3) {
                    e = e3;
                    fileOutputStream2 = fileOutputStream;
                    Log.i(CameraUtils.TAG, "save bitmap error");
                    Log.e(CameraUtils.TAG, e.toString());
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.close();
                        } catch (IOException e4) {
                            str = CameraUtils.TAG;
                            iOException = e4.toString();
                            Log.e(str, iOException);
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    fileOutputStream2 = fileOutputStream;
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.close();
                        } catch (IOException e5) {
                            Log.e(CameraUtils.TAG, e5.toString());
                        }
                    }
                    throw th;
                }
            }
        }
    }

    public CameraUtils(Context context, int i, LocationService.ISosSnapListener iSosSnapListener, String str) {
        release();
        this.mSosSnapListener = iSosSnapListener;
        this.mCameraId = i;
        this.mContext = context;
        this.mBitmapPathFront = str + "/SOS0.jpg";
        this.mBitmapPathBehind = str + "/SOS1.jpg";
        HandlerThread handlerThread = new HandlerThread("SnapCameraThread");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mMainHandler = new Handler(this.mHandlerThread.getLooper());
        this.mCameraHandler = new Handler(this.mHandlerThread.getLooper()) { // from class: src.com.android.settings.emergency.util.CameraUtils.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (1 == message.what) {
                    Log.i(CameraUtils.TAG, "handleMessage: MSG_FOCUS_TIMEOUT");
                }
            }
        };
        SurfaceTexture surfaceTexture = new SurfaceTexture(false);
        this.mSurfaceTexture = surfaceTexture;
        surfaceTexture.setDefaultBufferSize(1080, 1960);
        this.mPreviewSurface = new Surface(this.mSurfaceTexture);
        this.mCameraManager = (CameraManager) this.mContext.getSystemService("camera");
    }

    public static Bitmap bytes2Bitmap(byte[] bArr) {
        return BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
    }

    public static void deleteFile(final String str) {
        ThreadPool.execute(new Runnable() { // from class: src.com.android.settings.emergency.util.CameraUtils.7
            @Override // java.lang.Runnable
            public void run() {
                synchronized (CameraUtils.mObject) {
                    File file = new File(str + "/SOS0.jpg");
                    if (file.exists()) {
                        file.delete();
                    }
                    File file2 = new File(str + "/SOS1.jpg");
                    if (file2.exists()) {
                        file2.delete();
                    }
                }
            }
        });
    }

    public static byte[] getFirstPlane(Image image) {
        Image.Plane[] planes = image.getPlanes();
        if (planes.length > 0) {
            ByteBuffer buffer = planes[0].getBuffer();
            byte[] bArr = new byte[buffer.remaining()];
            buffer.get(bArr);
            return bArr;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openCamera() {
        try {
            CaptureRequest.Builder createCaptureRequest = this.mCameraDevice.createCaptureRequest(1);
            this.mPreviewRequestBuilder = createCaptureRequest;
            createCaptureRequest.addTarget(this.mPreviewSurface);
            this.mCameraDevice.createCaptureSession(this.mSurfaces, this.mSessionCallback, this.mCameraHandler);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void preparePhotoImageReader() {
        ImageReader imageReader = this.mPhotoImageReader;
        if (imageReader != null) {
            imageReader.close();
        }
        ImageReader newInstance = ImageReader.newInstance(1080, 1960, 256, 2);
        this.mPhotoImageReader = newInstance;
        newInstance.setOnImageAvailableListener(this.mPhotoAvailableListener, this.mCameraHandler);
        this.mSurfaces = Arrays.asList(this.mPreviewSurface, this.mPhotoImageReader.getSurface());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void takePicture() {
        try {
            CaptureRequest.Builder createCaptureRequest = this.mCameraDevice.createCaptureRequest(2);
            createCaptureRequest.addTarget(this.mPhotoImageReader.getSurface());
            createCaptureRequest.set(CaptureRequest.JPEG_ORIENTATION, 0);
            CaptureRequest.Key key = CaptureRequest.CONTROL_AF_MODE;
            createCaptureRequest.set(key, (Integer) this.mPreviewRequestBuilder.get(key));
            this.mCaptureSession.capture(createCaptureRequest.build(), this.mCaptureCallback, this.mCameraHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, e.toString());
        }
    }

    public Bitmap changeBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.setRotate(this.mCameraId == 0 ? 90 : -90);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public void prepareCameraAndTakePhoto() {
        this.mHandlerThread.getThreadHandler().post(new Runnable() { // from class: src.com.android.settings.emergency.util.CameraUtils.2
            @Override // java.lang.Runnable
            public void run() {
                String valueOf = String.valueOf(CameraUtils.this.mCameraId);
                try {
                    Log.d(CameraUtils.TAG, "prepareCamera: mCameraId = " + CameraUtils.this.mCameraId);
                    CameraUtils.this.mCameraManager.openCamera(valueOf, CameraUtils.this.mCameraStateCallback, CameraUtils.this.mMainHandler);
                } catch (CameraAccessException e) {
                    Log.e(CameraUtils.TAG, e.toString());
                }
            }
        });
    }

    public synchronized void release() {
        Log.d(TAG, "release(): E");
        try {
            SurfaceTexture surfaceTexture = this.mSurfaceTexture;
            if (surfaceTexture != null) {
                surfaceTexture.release();
                this.mSurfaceTexture = null;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        Handler handler = this.mCameraHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quitSafely();
        }
        CameraCaptureSession cameraCaptureSession = this.mCaptureSession;
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            this.mCaptureSession = null;
        }
        CameraDevice cameraDevice = this.mCameraDevice;
        if (cameraDevice != null) {
            cameraDevice.close();
            this.mCameraDevice = null;
        }
        Log.d(TAG, "release(): X");
    }
}
