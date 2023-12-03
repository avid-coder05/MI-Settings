package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MiuiSettings;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.device.MiuiAboutPhoneUtils;
import com.xiaomi.accountsdk.account.IXiaomiAccountService;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import miui.accounts.ExtraAccountManager;
import miui.content.ExtraIntent;

/* loaded from: classes.dex */
public class XiaomiAccountUtils {
    private static final String TAG = "XiaomiAccountUtils";
    private static XiaomiAccountUtils sXiaomiAccountUtils;
    private Bitmap mAccountAvatar;
    private int mAccountIconSize;
    private AccountManager mAccountManager;
    private String mAccountName;
    private Context mContext;
    private String mDeviceMarketName;
    private IntentFilter mIntentFilter;
    private ArrayList<UpdateAccountListener> mListerList;
    private MainHandler mMainHandler;
    private IXiaomiAccountService mMiAccountService;
    private AccountServiceConnection mServiceConnection;
    private AccountServiceHandler mXiaomiAccountHandler;
    private HandlerThread mXiaomiAccountThread;
    private final String SYSTEM_LOGIN_ACCOUNTS_POST_CHANGED_ACTION = ExtraAccountManager.LOGIN_ACCOUNTS_POST_CHANGED_ACTION;
    private boolean mIsServiceReg = false;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.accounts.XiaomiAccountUtils.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (XiaomiAccountUtils.this.mXiaomiAccountHandler != null) {
                XiaomiAccountUtils.this.mXiaomiAccountHandler.removeMessages(3);
                XiaomiAccountUtils.this.mXiaomiAccountHandler.sendEmptyMessageDelayed(3, 1200L);
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class AccountInfo {
        public Bitmap mAccountAvator;
        public String mAccountName;

        private AccountInfo() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AccountServiceConnection implements ServiceConnection {
        private WeakReference<XiaomiAccountUtils> mWeakRef;

        public AccountServiceConnection(XiaomiAccountUtils xiaomiAccountUtils) {
            this.mWeakRef = new WeakReference<>(xiaomiAccountUtils);
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            WeakReference<XiaomiAccountUtils> weakReference = this.mWeakRef;
            if (weakReference == null || weakReference.get() == null) {
                return;
            }
            this.mWeakRef.get().mMiAccountService = null;
            Log.e(XiaomiAccountUtils.TAG, "onBindingDied: ");
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                WeakReference<XiaomiAccountUtils> weakReference = this.mWeakRef;
                if (weakReference == null || weakReference.get() == null) {
                    return;
                }
                XiaomiAccountUtils xiaomiAccountUtils = this.mWeakRef.get();
                xiaomiAccountUtils.mMiAccountService = IXiaomiAccountService.Stub.asInterface(iBinder);
                Log.i(XiaomiAccountUtils.TAG, "bindAccountService success");
                if (xiaomiAccountUtils.mXiaomiAccountHandler != null) {
                    xiaomiAccountUtils.mXiaomiAccountHandler.sendEmptyMessage(3);
                }
            } catch (Exception unused) {
                Log.w(XiaomiAccountUtils.TAG, "fail getAccountService");
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            WeakReference<XiaomiAccountUtils> weakReference = this.mWeakRef;
            if (weakReference == null || weakReference.get() == null) {
                return;
            }
            this.mWeakRef.get().mMiAccountService = null;
            Log.e(XiaomiAccountUtils.TAG, "onServiceDisconnected: ");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AccountServiceHandler extends Handler {
        private WeakReference<XiaomiAccountUtils> mOuterUtilsRef;

        public AccountServiceHandler(XiaomiAccountUtils xiaomiAccountUtils, Looper looper) {
            super(looper);
            this.mOuterUtilsRef = new WeakReference<>(xiaomiAccountUtils);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            XiaomiAccountUtils xiaomiAccountUtils = this.mOuterUtilsRef.get();
            int i = message.what;
            if (i == 1) {
                Log.i(XiaomiAccountUtils.TAG, "handle ASYNC_MSG_BIND_SERVICE ");
                removeCallbacksAndMessages(null);
                if (xiaomiAccountUtils != null) {
                    xiaomiAccountUtils.bindAccountService();
                }
            } else if (i == 2) {
                Log.i(XiaomiAccountUtils.TAG, "handle ASYNC_MSG_UNBIND_SERVICE");
                removeCallbacksAndMessages(null);
                if (xiaomiAccountUtils != null) {
                    xiaomiAccountUtils.unbindAccountService();
                }
            } else if (i == 3) {
                Log.i(XiaomiAccountUtils.TAG, "handle ASYNC_MSG_GET_ACCOUNT_INFO,state=" + Thread.currentThread().getState().toString());
                removeMessages(3);
                if (xiaomiAccountUtils != null) {
                    xiaomiAccountUtils.updateAccountInfo();
                }
            } else if (i != 4) {
            } else {
                Log.i(XiaomiAccountUtils.TAG, "handle ASYNC_MSG_GET_ACCOUNT_INFO_FROM_LOCAL,state=" + Thread.currentThread().getState().toString());
                removeMessages(4);
                if (xiaomiAccountUtils != null) {
                    xiaomiAccountUtils.updateAccountInfoFromLocal();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MainHandler extends Handler {
        private WeakReference<XiaomiAccountUtils> mOuterUtilsRef;

        public MainHandler(XiaomiAccountUtils xiaomiAccountUtils) {
            this.mOuterUtilsRef = new WeakReference<>(xiaomiAccountUtils);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            XiaomiAccountUtils xiaomiAccountUtils = this.mOuterUtilsRef.get();
            int i = message.what;
            if (i == 1) {
                removeCallbacksAndMessages(null);
                if (xiaomiAccountUtils != null) {
                    xiaomiAccountUtils.clear();
                }
            } else if (i != 2) {
            } else {
                removeMessages(2);
                if (xiaomiAccountUtils == null || !(message.obj instanceof AccountInfo)) {
                    return;
                }
                if (xiaomiAccountUtils.mAccountAvatar != null && !xiaomiAccountUtils.mAccountAvatar.isRecycled()) {
                    xiaomiAccountUtils.mAccountAvatar.recycle();
                }
                Log.i(XiaomiAccountUtils.TAG, "MainHandler handle MSG_UPDATE_ACCOUNT_INFO");
                AccountInfo accountInfo = (AccountInfo) message.obj;
                xiaomiAccountUtils.mAccountName = accountInfo.mAccountName;
                xiaomiAccountUtils.mAccountAvatar = accountInfo.mAccountAvator;
                xiaomiAccountUtils.updateXiaomiAccountInfo();
            }
        }
    }

    /* loaded from: classes.dex */
    public interface UpdateAccountListener {
        void onXiaomiAccountUpdate();
    }

    private XiaomiAccountUtils(Context context) {
        this.mAccountIconSize = 0;
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mAccountManager = (AccountManager) applicationContext.getSystemService("account");
        this.mDeviceMarketName = MiuiAboutPhoneUtils.getDeviceMarketName();
        Drawable drawable = this.mContext.getDrawable(R.drawable.ic_account_avatar);
        if (drawable != null) {
            if (drawable instanceof VectorDrawable) {
                this.mAccountIconSize = ((VectorDrawable) drawable).getIntrinsicWidth();
            } else if (drawable instanceof BitmapDrawable) {
                this.mAccountIconSize = ((BitmapDrawable) drawable).getIntrinsicWidth();
            }
        }
        if (this.mAccountIconSize <= 0) {
            this.mAccountIconSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.header_icon_xiaomi_account_size);
        }
    }

    private void autoSetDeviceName(String str) {
        String defaultLoginDeviceName = getDefaultLoginDeviceName(str);
        if (defaultLoginDeviceName.getBytes().length > 31) {
            MiuiUtils.setDeviceName(this.mContext, this.mDeviceMarketName);
            setPreferenceRename(this.mDeviceMarketName);
            Log.w(TAG, "account name too long");
        } else {
            MiuiUtils.setDeviceName(this.mContext, defaultLoginDeviceName);
            setPreferenceRename(defaultLoginDeviceName);
            Log.i(TAG, "rename success");
        }
        this.mContext.sendBroadcast(new Intent("com.miui.action.edit_device_name"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bindAccountService() {
        if (this.mIsServiceReg) {
            return;
        }
        Intent intent = new Intent(getXiaomiAccountServiceActionName(this.mContext));
        intent.setPackage(ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME);
        AccountServiceConnection accountServiceConnection = new AccountServiceConnection(this);
        this.mServiceConnection = accountServiceConnection;
        try {
            this.mIsServiceReg = this.mContext.bindService(intent, accountServiceConnection, 1);
        } catch (Exception unused) {
            Log.w(TAG, "fail bindAccountService");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clear() {
        AccountServiceHandler accountServiceHandler = this.mXiaomiAccountHandler;
        if (accountServiceHandler != null) {
            accountServiceHandler.removeCallbacksAndMessages(null);
        }
        HandlerThread handlerThread = this.mXiaomiAccountThread;
        if (handlerThread != null) {
            handlerThread.quit();
            this.mXiaomiAccountThread = null;
        }
        MainHandler mainHandler = this.mMainHandler;
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
    }

    private Bitmap getAvatorFromLocal() {
        Bitmap bitmap = null;
        try {
            FileInputStream openFileInput = this.mContext.openFileInput("account_avator.png");
            bitmap = BitmapFactory.decodeStream(openFileInput);
            openFileInput.close();
            return bitmap;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "getAvatorFromLocal FileNotFoundException " + e);
            return bitmap;
        } catch (IOException e2) {
            Log.e(TAG, "getAvatorFromLocal IOException " + e2);
            return bitmap;
        }
    }

    private String getDefaultLoginDeviceName(String str) {
        return this.mContext.getString(R.string.device_of_someone, str, this.mDeviceMarketName);
    }

    public static XiaomiAccountUtils getInstance(Context context) {
        synchronized (XiaomiAccountUtils.class) {
            if (sXiaomiAccountUtils == null) {
                sXiaomiAccountUtils = new XiaomiAccountUtils(context);
            }
        }
        return sXiaomiAccountUtils;
    }

    private String getPreferenceRename() {
        return MiuiAboutPhoneUtils.getStringPreference(this.mContext, "auto_renamed");
    }

    private String getXiaomiAccountServiceActionName(Context context) {
        return context.getPackageManager().resolveService(new Intent("com.xiaomi.account.action.BIND_XIAOMI_ACCOUNT_SERVICE"), 0) == null ? ExtraIntent.ACTION_BIND_XIAOMI_ACCOUNT_SERVICE : "com.xiaomi.account.action.BIND_XIAOMI_ACCOUNT_SERVICE";
    }

    private void saveAccountInfoToLocal(String str, Bitmap bitmap) {
        if (getAccount(this.mContext) == null) {
            Log.i(TAG, "saveAccountInfoToLocal getAccount is Null");
            return;
        }
        MiuiAboutPhoneUtils.setStringPreference(this.mContext, "account_name", str);
        if (bitmap == null) {
            File file = new File(this.mContext.getFilesDir() + File.separator + "account_avator.png");
            if (file.exists() && file.isFile()) {
                file.delete();
                Log.i(TAG, "saveAccountInfoToLocal accountAvatar delete");
                return;
            }
            return;
        }
        try {
            FileOutputStream openFileOutput = this.mContext.openFileOutput("account_avator.png", 0);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, openFileOutput);
            openFileOutput.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "saveAccountInfoToLocal FileNotFoundException " + e);
        } catch (IOException e2) {
            Log.e(TAG, "saveAccountInfoToLocal IOException " + e2);
        }
    }

    private void setPreferenceRename(String str) {
        MiuiAboutPhoneUtils.setStringPreference(this.mContext, "auto_renamed", str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unbindAccountService() {
        Log.i(TAG, "unbindAccountService");
        if (this.mIsServiceReg) {
            try {
                this.mIsServiceReg = false;
                this.mMiAccountService = null;
                this.mContext.unbindService(this.mServiceConnection);
            } catch (Exception unused) {
                Log.w(TAG, "fail unbindAccountService");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAccountInfo() {
        Log.i(TAG, "updateAccountInfo thread=" + Thread.currentThread() + ",state=" + Thread.currentThread().getState().toString());
        String updateAccountName = updateAccountName();
        Bitmap updateAvatarBitmap = updateAvatarBitmap();
        saveAccountInfoToLocal(updateAccountName, updateAvatarBitmap);
        renameDevice();
        MainHandler mainHandler = this.mMainHandler;
        if (mainHandler != null) {
            Message obtainMessage = mainHandler.obtainMessage(2);
            AccountInfo accountInfo = new AccountInfo();
            accountInfo.mAccountName = updateAccountName;
            accountInfo.mAccountAvator = updateAvatarBitmap;
            obtainMessage.obj = accountInfo;
            this.mMainHandler.sendMessage(obtainMessage);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAccountInfoFromLocal() {
        String str = TAG;
        Log.i(str, "updateAccountInfoFromLocal thread=" + Thread.currentThread() + ",state=" + Thread.currentThread().getState().toString());
        if (getAccount(this.mContext) == null) {
            Log.i(str, "updateAccountInfoFromLocal getAccount is Null");
            return;
        }
        Bitmap avatorFromLocal = getAvatorFromLocal();
        String stringPreference = MiuiAboutPhoneUtils.getStringPreference(this.mContext, "account_name");
        if (TextUtils.isEmpty(stringPreference)) {
            Log.i(str, "updateAccountInfoFromLocal userName is Null");
        }
        MainHandler mainHandler = this.mMainHandler;
        if (mainHandler != null) {
            Message obtainMessage = mainHandler.obtainMessage(2);
            AccountInfo accountInfo = new AccountInfo();
            accountInfo.mAccountName = stringPreference;
            accountInfo.mAccountAvator = avatorFromLocal;
            obtainMessage.obj = accountInfo;
            this.mMainHandler.sendMessage(obtainMessage);
        }
    }

    private String updateAccountName() {
        AccountManager accountManager;
        Account account = getAccount(this.mContext);
        String str = null;
        if (account == null) {
            Log.i(TAG, "updateAccountName account is null");
            return null;
        }
        IXiaomiAccountService iXiaomiAccountService = this.mMiAccountService;
        if (iXiaomiAccountService != null) {
            try {
                str = iXiaomiAccountService.getUserName(account);
                if (TextUtils.isEmpty(str)) {
                    Log.i(TAG, "updateAccountName getUserName success");
                }
            } catch (Exception unused) {
                Log.w(TAG, "failed getXiaoAccountName");
            }
        }
        if (!TextUtils.isEmpty(str) || (accountManager = this.mAccountManager) == null) {
            return str;
        }
        String userData = accountManager.getUserData(account, "acc_user_name");
        Log.i(TAG, "updateAccountName getUserName from account");
        return TextUtils.isEmpty(userData) ? account.name : userData;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v6 */
    /* JADX WARN: Type inference failed for: r2v12 */
    /* JADX WARN: Type inference failed for: r2v13 */
    /* JADX WARN: Type inference failed for: r2v15, types: [android.graphics.Bitmap] */
    /* JADX WARN: Type inference failed for: r2v16 */
    /* JADX WARN: Type inference failed for: r2v18 */
    /* JADX WARN: Type inference failed for: r2v6 */
    /* JADX WARN: Type inference failed for: r2v7 */
    /* JADX WARN: Type inference failed for: r2v9 */
    /* JADX WARN: Type inference failed for: r9v0 */
    private Bitmap updateAvatarBitmap() {
        ?? r1;
        ParcelFileDescriptor avatarFd;
        Account account = getAccount(this.mContext);
        Bitmap bitmap = null;
        bitmap = 0;
        ParcelFileDescriptor parcelFileDescriptor = null;
        bitmap = 0;
        if (account == null) {
            Log.i(TAG, "updateAvatarBitmap account is null");
            return null;
        }
        String str = TAG;
        Log.i(str, "updateAvatarBitmap: mMiAccountService=" + this.mMiAccountService);
        IXiaomiAccountService iXiaomiAccountService = this.mMiAccountService;
        try {
            if (iXiaomiAccountService != null) {
                try {
                    avatarFd = iXiaomiAccountService.getAvatarFd(account);
                } catch (Exception e) {
                    e = e;
                    r1 = null;
                }
                try {
                    Log.i(str, "updateAvatarBitmap: getAvatarFd=" + avatarFd);
                    FileDescriptor fileDescriptor = avatarFd != null ? avatarFd.getFileDescriptor() : null;
                    bitmap = fileDescriptor != null ? BitmapFactory.decodeFileDescriptor(fileDescriptor) : null;
                    if (bitmap != null) {
                        if (this.mAccountIconSize <= 0) {
                            Drawable drawable = this.mContext.getDrawable(R.drawable.ic_account_avatar);
                            if (drawable instanceof VectorDrawable) {
                                this.mAccountIconSize = ((VectorDrawable) drawable).getIntrinsicWidth();
                            }
                        }
                        int i = this.mAccountIconSize;
                        bitmap = MiuiUtils.toRoundCorner(bitmap, i, i, i / 2, -1, true);
                    }
                    if (avatarFd != null) {
                        try {
                            avatarFd.close();
                        } catch (Exception e2) {
                            Log.e(TAG, "close file error", e2);
                        }
                    }
                } catch (Exception e3) {
                    e = e3;
                    ?? r9 = bitmap;
                    parcelFileDescriptor = avatarFd;
                    r1 = r9;
                    Log.e(TAG, "Fail getAvatarFd", e);
                    if (parcelFileDescriptor != null) {
                        try {
                            parcelFileDescriptor.close();
                        } catch (Exception e4) {
                            Log.e(TAG, "close file error", e4);
                        }
                    }
                    bitmap = r1;
                    return bitmap;
                } catch (Throwable th) {
                    th = th;
                    bitmap = avatarFd;
                    if (bitmap != null) {
                        try {
                            bitmap.close();
                        } catch (Exception e5) {
                            Log.e(TAG, "close file error", e5);
                        }
                    }
                    throw th;
                }
            }
            return bitmap;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void destroy(UpdateAccountListener updateAccountListener) {
        unRegisterAccountChangeLister(updateAccountListener);
        unbindAccountService();
        clear();
    }

    public Account getAccount(Context context) {
        return ExtraAccountManager.getXiaomiAccount(context.getApplicationContext());
    }

    public String getXiaoAccountName() {
        return this.mAccountName;
    }

    public Bitmap getXiaomiAccountAvatar() {
        return this.mAccountAvatar;
    }

    public void init(UpdateAccountListener updateAccountListener) {
        Log.i(TAG, "init");
        registerAccountChangeLister(updateAccountListener);
        if (this.mMainHandler == null) {
            this.mMainHandler = new MainHandler(this);
        }
        if (this.mXiaomiAccountThread == null) {
            HandlerThread handlerThread = new HandlerThread("settings_xiaomi_account", 5);
            this.mXiaomiAccountThread = handlerThread;
            handlerThread.start();
            this.mXiaomiAccountHandler = new AccountServiceHandler(this, this.mXiaomiAccountThread.getLooper());
        }
        IntentFilter intentFilter = new IntentFilter();
        this.mIntentFilter = intentFilter;
        intentFilter.addAction(ExtraAccountManager.LOGIN_ACCOUNTS_POST_CHANGED_ACTION);
        this.mIntentFilter.addAction(ExtraIntent.ACTION_XIAOMI_USER_INFO_CHANGED);
        this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
        this.mXiaomiAccountHandler.sendEmptyMessage(4);
        this.mXiaomiAccountHandler.sendEmptyMessageDelayed(1, 200L);
    }

    public void registerAccountChangeLister(UpdateAccountListener updateAccountListener) {
        if (this.mListerList == null) {
            this.mListerList = new ArrayList<>();
        }
        if (this.mListerList.contains(updateAccountListener)) {
            return;
        }
        this.mListerList.add(updateAccountListener);
    }

    public void renameDevice() {
        Account account = getAccount(this.mContext);
        String preferenceRename = getPreferenceRename();
        if (account == null && !TextUtils.isEmpty(this.mDeviceMarketName)) {
            if (preferenceRename != null) {
                String deviceName = MiuiSettings.System.getDeviceName(this.mContext);
                if (deviceName.equals(this.mDeviceMarketName) || !deviceName.equals(preferenceRename)) {
                    return;
                }
                MiuiUtils.setDeviceName(this.mContext, this.mDeviceMarketName);
                setPreferenceRename(this.mDeviceMarketName);
                Log.i(TAG, "restore default name success");
                this.mContext.sendBroadcast(new Intent("com.miui.action.edit_device_name"));
            }
        } else if (this.mAccountManager == null || TextUtils.isEmpty(this.mDeviceMarketName)) {
        } else {
            String userData = this.mAccountManager.getUserData(account, "acc_user_name");
            if (TextUtils.isEmpty(userData)) {
                return;
            }
            if (!(TextUtils.isEmpty(preferenceRename) && this.mDeviceMarketName.equals(MiuiSettings.System.getDeviceName(this.mContext))) && (preferenceRename == null || !preferenceRename.equals(MiuiSettings.System.getDeviceName(this.mContext)) || preferenceRename.equals(getDefaultLoginDeviceName(userData)))) {
                return;
            }
            autoSetDeviceName(userData);
        }
    }

    public void resume(UpdateAccountListener updateAccountListener) {
        Log.i(TAG, "resume mMiAccountService=" + this.mMiAccountService);
        if (this.mMiAccountService == null) {
            init(updateAccountListener);
            return;
        }
        AccountServiceHandler accountServiceHandler = this.mXiaomiAccountHandler;
        if (accountServiceHandler != null) {
            accountServiceHandler.removeMessages(3);
            this.mXiaomiAccountHandler.sendEmptyMessageDelayed(3, 2500L);
        }
    }

    public void unRegisterAccountChangeLister(UpdateAccountListener updateAccountListener) {
        ArrayList<UpdateAccountListener> arrayList = this.mListerList;
        if (arrayList != null && arrayList.contains(updateAccountListener)) {
            this.mListerList.remove(updateAccountListener);
        }
        ArrayList<UpdateAccountListener> arrayList2 = this.mListerList;
        if (arrayList2 != null && arrayList2.isEmpty()) {
            this.mListerList = null;
        }
        try {
            this.mContext.unregisterReceiver(this.mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateXiaomiAccountInfo() {
        ArrayList<UpdateAccountListener> arrayList = this.mListerList;
        if (arrayList != null) {
            Iterator<UpdateAccountListener> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().onXiaomiAccountUpdate();
            }
        }
    }
}
