package com.android.settings.bluetooth.tws;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.widget.ImageView;
import com.android.bluetooth.ble.app.IMiuiHeadsetService;
import com.android.settings.R;
import com.android.settings.bluetooth.HeadsetIDConstants;
import com.android.settings.utils.EncryptionUtil;
import com.milink.api.v1.type.DeviceType;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class MiuiHeadsetAnimation {
    private WeakReference<Context> mContext;
    private String mDeviceId;
    private WeakReference<Handler> mHandler;
    private WeakReference<View> mRootView;
    private WeakReference<IMiuiHeadsetService> mService;
    private WeakReference<Handler> mWorkHandler;
    private String mSuffix = "";
    public String mRemoteVersion = "";
    public boolean mInited = false;

    public MiuiHeadsetAnimation(String str, Context context, View view, Handler handler, IMiuiHeadsetService iMiuiHeadsetService, Handler handler2) {
        this.mDeviceId = "";
        this.mRootView = null;
        this.mContext = null;
        this.mHandler = null;
        this.mService = null;
        this.mWorkHandler = null;
        this.mDeviceId = str;
        this.mContext = new WeakReference<>(context);
        this.mRootView = new WeakReference<>(view);
        this.mHandler = new WeakReference<>(handler);
        this.mService = new WeakReference<>(iMiuiHeadsetService);
        this.mWorkHandler = new WeakReference<>(handler2);
    }

    public static boolean checkLocalCached(String str, Context context) {
        try {
            Log.d("MiuiHeadsetAnimation", "check local cached");
            if (context != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(context.getFilesDir().getAbsolutePath());
                String str2 = File.separator;
                sb.append(str2);
                sb.append(DeviceType.BLUETOOTH);
                sb.append(str2);
                sb.append("fc_resources");
                sb.append(str2);
                sb.append(str);
                sb.append(str2);
                sb.append("0");
                if (new File(sb.toString()).exists()) {
                    Log.d("MiuiHeadsetAnimation", "local cached");
                    return true;
                }
                return false;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkRemoteUpdate(String str) {
        WeakReference<IMiuiHeadsetService> weakReference;
        Log.e("MiuiHeadsetAnimation", "checkRemoteUpdate " + str);
        try {
            WeakReference<Context> weakReference2 = this.mContext;
            if (weakReference2 != null && weakReference2.get() != null && (weakReference = this.mService) != null && weakReference.get() != null) {
                String str2 = "";
                try {
                    str2 = this.mService.get().getDeviceInfo(str);
                } catch (Exception e) {
                    Log.e("MiuiHeadsetAnimation", "error " + e);
                }
                Log.d("MiuiHeadsetAnimation", " file remote version " + str2);
                if (TextUtils.isEmpty(str2)) {
                    return false;
                }
                String[] split = str2.split("\\:");
                if (split.length == 2 && !TextUtils.isEmpty(split[0]) && !TextUtils.isEmpty(split[1])) {
                    this.mSuffix = split[0];
                    this.mRemoteVersion = split[1];
                    StringBuilder sb = new StringBuilder();
                    sb.append(this.mContext.get().getFilesDir().getAbsolutePath());
                    String str3 = File.separator;
                    sb.append(str3);
                    sb.append(DeviceType.BLUETOOTH);
                    sb.append(str3);
                    sb.append("fc_resources");
                    sb.append(str3);
                    sb.append(str);
                    sb.append(str3);
                    sb.append("0");
                    return needCopy(split[1], new File(sb.toString()));
                }
                return false;
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return false;
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception unused) {
            }
        }
    }

    private void delete(String str) {
        try {
            WeakReference<Context> weakReference = this.mContext;
            if (weakReference == null || weakReference.get() == null || TextUtils.isEmpty(str)) {
                return;
            }
            Log.d("MiuiHeadsetAnimation", "delete " + str);
            StringBuilder sb = new StringBuilder();
            sb.append(this.mContext.get().getFilesDir().getAbsolutePath());
            String str2 = File.separator;
            sb.append(str2);
            sb.append(DeviceType.BLUETOOTH);
            sb.append(str2);
            sb.append("fc_resources");
            sb.append(str2);
            sb.append(str);
            File file = new File(sb.toString());
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fetchOnlineResource() {
        try {
            WeakReference<Handler> weakReference = this.mWorkHandler;
            if (weakReference == null || weakReference.get() == null) {
                return;
            }
            this.mWorkHandler.get().sendMessage(this.mWorkHandler.get().obtainMessage(201));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static BitmapDrawable getDrawableFromFile(String str, Context context, String str2) {
        File[] listFiles;
        try {
            if (context == null) {
                Log.e("MiuiHeadsetAnimation", "getDrawableFromFile");
                return null;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(context.getFilesDir());
            String str3 = File.separator;
            sb.append(str3);
            sb.append(DeviceType.BLUETOOTH);
            sb.append(str3);
            sb.append("fc_resources");
            sb.append(str3);
            sb.append(str);
            File file = new File(sb.toString());
            if (file.exists()) {
                String str4 = "";
                File file2 = new File(context.getFilesDir() + str3 + DeviceType.BLUETOOTH + str3 + "fc_resources" + str3 + str + str3);
                if (file2.exists() && (listFiles = file2.listFiles()) != null && listFiles.length != 0) {
                    int length = listFiles.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            break;
                        }
                        File file3 = listFiles[i];
                        if (file3 != null) {
                            String name = file3.getName();
                            if (TextUtils.isEmpty(name)) {
                                continue;
                            } else {
                                if (name.startsWith(str2 + ".")) {
                                    str4 = name.substring(name.lastIndexOf(".") + 1);
                                    break;
                                }
                            }
                        }
                        i++;
                    }
                }
                Bitmap decodeFile = BitmapFactory.decodeFile(file.getAbsolutePath() + File.separator + str2 + "." + str4);
                if (decodeFile == null) {
                    return null;
                }
                return new BitmapDrawable(context.getResources(), decodeFile);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BitmapDrawable getFindDeviceDrawable(String str, Context context) {
        return getDrawableFromFile(str, context, "1");
    }

    public static BitmapDrawable getFitnessDeviceDrawable(String str, Context context) {
        return getDrawableFromFile(str, context, "2");
    }

    private void mkdir(String str) {
        WeakReference<Context> weakReference;
        try {
            if (TextUtils.isEmpty(str) || (weakReference = this.mContext) == null || weakReference.get() == null) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(this.mContext.get().getFilesDir().getAbsolutePath());
            String str2 = File.separator;
            sb.append(str2);
            sb.append(DeviceType.BLUETOOTH);
            sb.append(str2);
            sb.append("fc_resources");
            sb.append(str2);
            sb.append(str);
            String sb2 = sb.toString();
            Log.d("MiuiHeadsetAnimation", "mkdir " + sb2);
            File file = new File(sb2);
            if (file.exists()) {
                file.delete();
            }
            new File(sb2).mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean needCopy(String str, File file) {
        StringBuilder sb;
        FileInputStream fileInputStream;
        if (TextUtils.isEmpty(str) || "-1".equals(str)) {
            return false;
        }
        if (file == null || !file.exists()) {
            Log.d("MiuiHeadsetAnimation", "need copy");
            return true;
        }
        FileInputStream fileInputStream2 = null;
        try {
            try {
                fileInputStream = new FileInputStream(file);
            } catch (Exception e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            byte[] bArr = new byte[100];
            int read = fileInputStream.read(bArr);
            if (read > 0) {
                if (Integer.valueOf(new String(bArr, 0, read, "UTF-8")).intValue() < Integer.valueOf(str).intValue()) {
                    try {
                        fileInputStream.close();
                    } catch (Exception e2) {
                        Log.d("MiuiHeadsetAnimation", "error " + e2);
                    }
                    return true;
                }
            }
            try {
                fileInputStream.close();
            } catch (Exception e3) {
                e = e3;
                sb = new StringBuilder();
                sb.append("error ");
                sb.append(e);
                Log.d("MiuiHeadsetAnimation", sb.toString());
                return false;
            }
        } catch (Exception e4) {
            e = e4;
            fileInputStream2 = fileInputStream;
            Log.e("MiuiHeadsetAnimation", " exception " + e);
            if (fileInputStream2 != null) {
                try {
                    fileInputStream2.close();
                } catch (Exception e5) {
                    e = e5;
                    sb = new StringBuilder();
                    sb.append("error ");
                    sb.append(e);
                    Log.d("MiuiHeadsetAnimation", sb.toString());
                    return false;
                }
            }
            return false;
        } catch (Throwable th2) {
            th = th2;
            fileInputStream2 = fileInputStream;
            if (fileInputStream2 != null) {
                try {
                    fileInputStream2.close();
                } catch (Exception e6) {
                    Log.d("MiuiHeadsetAnimation", "error " + e6);
                }
            }
            throw th;
        }
        return false;
    }

    private void writeLocalVersion(String str, String str2) {
        FileOutputStream fileOutputStream;
        WeakReference<Context> weakReference = this.mContext;
        if (weakReference == null || weakReference.get() == null || TextUtils.isEmpty(str)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.mContext.get().getFilesDir().getAbsolutePath());
        String str3 = File.separator;
        sb.append(str3);
        sb.append(DeviceType.BLUETOOTH);
        sb.append(str3);
        sb.append("fc_resources");
        sb.append(str3);
        sb.append(str);
        sb.append(str3);
        sb.append("0");
        File file = new File(sb.toString());
        FileOutputStream fileOutputStream2 = null;
        try {
            try {
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    fileOutputStream = new FileOutputStream(file);
                } catch (Throwable th) {
                    th = th;
                }
            } catch (Exception e) {
                e = e;
            }
            try {
                fileOutputStream.write(str2.getBytes());
                fileOutputStream.close();
            } catch (Exception e2) {
                e = e2;
                fileOutputStream2 = fileOutputStream;
                e.printStackTrace();
                if (fileOutputStream2 != null) {
                    fileOutputStream2.close();
                }
            } catch (Throwable th2) {
                th = th2;
                fileOutputStream2 = fileOutputStream;
                if (fileOutputStream2 != null) {
                    try {
                        fileOutputStream2.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                throw th;
            }
        } catch (IOException e4) {
            e4.printStackTrace();
        }
    }

    public void checkAndDoCopy() {
        Closeable closeable;
        Closeable closeable2;
        WeakReference<Context> weakReference;
        FileOutputStream fileOutputStream;
        Log.d("MiuiHeadsetAnimation", "check and do copy");
        FileInputStream fileInputStream = null;
        try {
            try {
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
            closeable2 = null;
        } catch (Throwable th2) {
            th = th2;
            closeable = null;
        }
        if (!TextUtils.isEmpty(this.mDeviceId) && (weakReference = this.mContext) != null && weakReference.get() != null) {
            boolean checkLocalCached = checkLocalCached(this.mDeviceId, this.mContext.get());
            boolean checkRemoteUpdate = checkRemoteUpdate(this.mDeviceId);
            if (checkLocalCached && !checkRemoteUpdate) {
                closeable2 = null;
                closeQuietly(fileInputStream);
                closeQuietly(closeable2);
                return;
            }
            delete(this.mDeviceId);
            mkdir(this.mDeviceId);
            String str = "content://com.android.bluetooth.ble.app.headset.provider/headset_resource_update/" + this.mDeviceId + "/" + this.mSuffix;
            closeable2 = null;
            int i = 0;
            while (i < 4) {
                try {
                    String str2 = str + i;
                    fileInputStream = this.mContext.get().getContentResolver().openAssetFileDescriptor(Uri.parse(str2), "r").createInputStream();
                    str2.lastIndexOf(47);
                    StringBuilder sb = new StringBuilder();
                    sb.append(this.mContext.get().getFilesDir().getAbsolutePath());
                    String str3 = File.separator;
                    sb.append(str3);
                    sb.append(DeviceType.BLUETOOTH);
                    sb.append(str3);
                    sb.append("fc_resources");
                    sb.append(str3);
                    sb.append(this.mDeviceId);
                    sb.append(str3);
                    sb.append(i);
                    sb.append(".");
                    sb.append(this.mSuffix);
                    fileOutputStream = new FileOutputStream(new File(sb.toString()));
                } catch (Exception e2) {
                    e = e2;
                }
                try {
                    byte[] bArr = new byte[MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE];
                    while (true) {
                        int read = fileInputStream.read(bArr);
                        if (read == -1) {
                            break;
                        }
                        fileOutputStream.write(bArr, 0, read);
                    }
                    fileOutputStream.flush();
                    closeQuietly(fileInputStream);
                    closeQuietly(fileOutputStream);
                    i++;
                    closeable2 = fileOutputStream;
                } catch (Exception e3) {
                    e = e3;
                    closeable2 = fileOutputStream;
                    e.printStackTrace();
                    delete(this.mDeviceId);
                    closeQuietly(fileInputStream);
                    closeQuietly(closeable2);
                    return;
                } catch (Throwable th3) {
                    th = th3;
                    closeable = fileOutputStream;
                    closeQuietly(fileInputStream);
                    closeQuietly(closeable);
                    throw th;
                }
            }
            writeLocalVersion(this.mDeviceId, this.mRemoteVersion);
            closeQuietly(fileInputStream);
            closeQuietly(closeable2);
            return;
        }
        Log.e("MiuiHeadsetAnimation", "failed to get DeviceID");
        closeQuietly(null);
        closeQuietly(null);
    }

    public void loadDefault() {
        if (this.mInited) {
            return;
        }
        this.mInited = true;
        loadDefaultInternal();
    }

    public void loadDefaultInternal() {
        WeakReference<Handler> weakReference;
        WeakReference<Context> weakReference2;
        try {
            WeakReference<View> weakReference3 = this.mRootView;
            if (weakReference3 != null && weakReference3.get() != null && (weakReference = this.mHandler) != null && weakReference.get() != null && (weakReference2 = this.mContext) != null && weakReference2.get() != null) {
                if (!"0201010000".equals(this.mDeviceId) && !"0201010001".equals(this.mDeviceId)) {
                    if (HeadsetIDConstants.isTWS01GrayHeadset(this.mDeviceId)) {
                        this.mHandler.get().postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.tws.MiuiHeadsetAnimation.2
                            @Override // java.lang.Runnable
                            public void run() {
                                ImageView imageView = (ImageView) ((View) MiuiHeadsetAnimation.this.mRootView.get()).findViewById(R.id.tic);
                                imageView.setImageResource(R.drawable.TWS01_list_Gray);
                                AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
                                animationDrawable.setOneShot(true);
                                animationDrawable.start();
                            }
                        }, 50L);
                    } else if (HeadsetIDConstants.isTWS01BlackHeadset(this.mDeviceId)) {
                        this.mHandler.get().postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.tws.MiuiHeadsetAnimation.3
                            @Override // java.lang.Runnable
                            public void run() {
                                ImageView imageView = (ImageView) ((View) MiuiHeadsetAnimation.this.mRootView.get()).findViewById(R.id.tic);
                                imageView.setImageResource(R.drawable.TWS01_list_Black);
                                AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
                                animationDrawable.setOneShot(true);
                                animationDrawable.start();
                            }
                        }, 50L);
                    } else if (HeadsetIDConstants.isTWS01YellowHeadset(this.mDeviceId)) {
                        this.mHandler.get().postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.tws.MiuiHeadsetAnimation.4
                            @Override // java.lang.Runnable
                            public void run() {
                                ImageView imageView = (ImageView) ((View) MiuiHeadsetAnimation.this.mRootView.get()).findViewById(R.id.tic);
                                imageView.setImageResource(R.drawable.TWS01_list_Yellow);
                                AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
                                animationDrawable.setOneShot(true);
                                animationDrawable.start();
                            }
                        }, 50L);
                    } else if (HeadsetIDConstants.isK77sHeadset(this.mDeviceId)) {
                        this.mHandler.get().postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.tws.MiuiHeadsetAnimation.5
                            @Override // java.lang.Runnable
                            public void run() {
                                ((ImageView) ((View) MiuiHeadsetAnimation.this.mRootView.get()).findViewById(R.id.tic)).setImageResource(R.drawable.headset_k77s_white);
                            }
                        }, 50L);
                    } else if (HeadsetIDConstants.isK73WhiteHeadset(this.mDeviceId)) {
                        this.mHandler.get().postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.tws.MiuiHeadsetAnimation.6
                            @Override // java.lang.Runnable
                            public void run() {
                                ((ImageView) ((View) MiuiHeadsetAnimation.this.mRootView.get()).findViewById(R.id.tic)).setImageResource(R.drawable.headset_default_k73_white);
                            }
                        }, 50L);
                    } else if (HeadsetIDConstants.isK73BlackHeadset(this.mDeviceId)) {
                        this.mHandler.get().postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.tws.MiuiHeadsetAnimation.7
                            @Override // java.lang.Runnable
                            public void run() {
                                ((ImageView) ((View) MiuiHeadsetAnimation.this.mRootView.get()).findViewById(R.id.tic)).setImageResource(R.drawable.headset_default_k73_black);
                            }
                        }, 50L);
                    } else if (HeadsetIDConstants.isK73GreenHeadset(this.mDeviceId)) {
                        this.mHandler.get().postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.tws.MiuiHeadsetAnimation.8
                            @Override // java.lang.Runnable
                            public void run() {
                                ((ImageView) ((View) MiuiHeadsetAnimation.this.mRootView.get()).findViewById(R.id.tic)).setImageResource(R.drawable.headset_default_k73_green);
                            }
                        }, 50L);
                    } else if (HeadsetIDConstants.isK73LBlueHeadset(this.mDeviceId)) {
                        this.mHandler.get().postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.tws.MiuiHeadsetAnimation.9
                            @Override // java.lang.Runnable
                            public void run() {
                                ((ImageView) ((View) MiuiHeadsetAnimation.this.mRootView.get()).findViewById(R.id.tic)).setImageResource(R.drawable.headset_default_k73_lblue);
                            }
                        }, 50L);
                    } else if (HeadsetIDConstants.isK73AWhiteHeadset(this.mDeviceId)) {
                        this.mHandler.get().postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.tws.MiuiHeadsetAnimation.10
                            @Override // java.lang.Runnable
                            public void run() {
                                ImageView imageView = (ImageView) ((View) MiuiHeadsetAnimation.this.mRootView.get()).findViewById(R.id.tic);
                                Bitmap decodeImageResourceByKey = EncryptionUtil.decodeImageResourceByKey((Context) MiuiHeadsetAnimation.this.mContext.get(), R.drawable.headset_default_k73a_white_enc, "k73@GL_default_white");
                                if (decodeImageResourceByKey != null) {
                                    imageView.setImageDrawable(new BitmapDrawable(((Context) MiuiHeadsetAnimation.this.mContext.get()).getResources(), decodeImageResourceByKey));
                                } else {
                                    Log.d("MiuiHeadsetAnimation", "bitmap null");
                                }
                            }
                        }, 20L);
                    } else if (HeadsetIDConstants.isK73ABlackHeadset(this.mDeviceId)) {
                        this.mHandler.get().postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.tws.MiuiHeadsetAnimation.11
                            @Override // java.lang.Runnable
                            public void run() {
                                ImageView imageView = (ImageView) ((View) MiuiHeadsetAnimation.this.mRootView.get()).findViewById(R.id.tic);
                                Bitmap decodeImageResourceByKey = EncryptionUtil.decodeImageResourceByKey((Context) MiuiHeadsetAnimation.this.mContext.get(), R.drawable.headset_default_k73a_black_enc, "k73@GL_default_black");
                                if (decodeImageResourceByKey != null) {
                                    imageView.setImageDrawable(new BitmapDrawable(((Context) MiuiHeadsetAnimation.this.mContext.get()).getResources(), decodeImageResourceByKey));
                                } else {
                                    Log.d("MiuiHeadsetAnimation", "bitmap null");
                                }
                            }
                        }, 20L);
                    } else if (HeadsetIDConstants.isK73AGreenHeadset(this.mDeviceId)) {
                        this.mHandler.get().postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.tws.MiuiHeadsetAnimation.12
                            @Override // java.lang.Runnable
                            public void run() {
                                ImageView imageView = (ImageView) ((View) MiuiHeadsetAnimation.this.mRootView.get()).findViewById(R.id.tic);
                                Bitmap decodeImageResourceByKey = EncryptionUtil.decodeImageResourceByKey((Context) MiuiHeadsetAnimation.this.mContext.get(), R.drawable.headset_default_k73a_green_enc, "k73@GL_default_green");
                                if (decodeImageResourceByKey != null) {
                                    imageView.setImageDrawable(new BitmapDrawable(((Context) MiuiHeadsetAnimation.this.mContext.get()).getResources(), decodeImageResourceByKey));
                                } else {
                                    Log.d("MiuiHeadsetAnimation", "bitmap null");
                                }
                            }
                        }, 20L);
                    } else {
                        if (!HeadsetIDConstants.isK75WhiteHeadset(this.mDeviceId) && !HeadsetIDConstants.isK75AWhiteHeadset(this.mDeviceId)) {
                            if (!HeadsetIDConstants.isK75BlackHeadset(this.mDeviceId) && !HeadsetIDConstants.isK75ABlackHeadset(this.mDeviceId)) {
                                if (HeadsetIDConstants.isK76sHeadset(this.mDeviceId)) {
                                    this.mHandler.get().postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.tws.MiuiHeadsetAnimation.15
                                        @Override // java.lang.Runnable
                                        public void run() {
                                            ImageView imageView = (ImageView) ((View) MiuiHeadsetAnimation.this.mRootView.get()).findViewById(R.id.tic);
                                            Bitmap decodeImageResource = HeadsetIDConstants.decodeImageResource((Context) MiuiHeadsetAnimation.this.mContext.get(), R.drawable.headset_TWS01S);
                                            if (decodeImageResource != null) {
                                                imageView.setImageDrawable(new BitmapDrawable(((Context) MiuiHeadsetAnimation.this.mContext.get()).getResources(), decodeImageResource));
                                            } else {
                                                Log.d("MiuiHeadsetAnimation", "bitmap null");
                                            }
                                        }
                                    }, 5L);
                                } else if (HeadsetIDConstants.isTWS200(this.mDeviceId)) {
                                    this.mHandler.get().postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.tws.MiuiHeadsetAnimation.16
                                        @Override // java.lang.Runnable
                                        public void run() {
                                            ((ImageView) ((View) MiuiHeadsetAnimation.this.mRootView.get()).findViewById(R.id.tic)).setImageResource(R.drawable.headset_TWS200);
                                        }
                                    }, 5L);
                                } else {
                                    this.mDeviceId.hashCode();
                                    final int i = R.drawable.bt_headset_find_detail;
                                    this.mHandler.get().post(new Runnable() { // from class: com.android.settings.bluetooth.tws.MiuiHeadsetAnimation.17
                                        @Override // java.lang.Runnable
                                        public void run() {
                                            ImageView imageView = (ImageView) ((View) MiuiHeadsetAnimation.this.mRootView.get()).findViewById(R.id.tic);
                                            if (!MiuiHeadsetAnimation.checkLocalCached(MiuiHeadsetAnimation.this.mDeviceId, (Context) MiuiHeadsetAnimation.this.mContext.get())) {
                                                MiuiHeadsetAnimation.this.fetchOnlineResource();
                                                imageView.setImageResource(i);
                                                return;
                                            }
                                            BitmapDrawable drawableFromFile = MiuiHeadsetAnimation.getDrawableFromFile(MiuiHeadsetAnimation.this.mDeviceId, (Context) MiuiHeadsetAnimation.this.mContext.get(), "0");
                                            if (drawableFromFile != null) {
                                                imageView.setImageDrawable(drawableFromFile);
                                            }
                                        }
                                    });
                                }
                            }
                            this.mHandler.get().postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.tws.MiuiHeadsetAnimation.14
                                @Override // java.lang.Runnable
                                public void run() {
                                    ImageView imageView = (ImageView) ((View) MiuiHeadsetAnimation.this.mRootView.get()).findViewById(R.id.tic);
                                    Bitmap decodeImageResourceByKey = EncryptionUtil.decodeImageResourceByKey((Context) MiuiHeadsetAnimation.this.mContext.get(), R.drawable.headset_default_k75_black_enc, "k75_black");
                                    if (decodeImageResourceByKey != null) {
                                        imageView.setImageDrawable(new BitmapDrawable(((Context) MiuiHeadsetAnimation.this.mContext.get()).getResources(), decodeImageResourceByKey));
                                    } else {
                                        Log.d("MiuiHeadsetAnimation", "bitmap null");
                                    }
                                }
                            }, 20L);
                        }
                        this.mHandler.get().postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.tws.MiuiHeadsetAnimation.13
                            @Override // java.lang.Runnable
                            public void run() {
                                ImageView imageView = (ImageView) ((View) MiuiHeadsetAnimation.this.mRootView.get()).findViewById(R.id.tic);
                                Bitmap decodeImageResourceByKey = EncryptionUtil.decodeImageResourceByKey((Context) MiuiHeadsetAnimation.this.mContext.get(), R.drawable.headset_default_k75_white_enc, "k75_white");
                                if (decodeImageResourceByKey != null) {
                                    imageView.setImageDrawable(new BitmapDrawable(((Context) MiuiHeadsetAnimation.this.mContext.get()).getResources(), decodeImageResourceByKey));
                                } else {
                                    Log.d("MiuiHeadsetAnimation", "bitmap null");
                                }
                            }
                        }, 20L);
                    }
                }
                this.mHandler.get().postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.tws.MiuiHeadsetAnimation.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ImageView imageView = (ImageView) ((View) MiuiHeadsetAnimation.this.mRootView.get()).findViewById(R.id.tic);
                        imageView.setImageResource(R.drawable.abunation_list_0000010000);
                        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
                        animationDrawable.setOneShot(true);
                        animationDrawable.start();
                    }
                }, 50L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateService(IMiuiHeadsetService iMiuiHeadsetService) {
        this.mService = new WeakReference<>(iMiuiHeadsetService);
        loadDefault();
    }
}
