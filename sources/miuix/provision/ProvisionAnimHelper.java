package miuix.provision;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.android.provision.IAnimCallback;
import com.android.provision.IProvisionAnim;

/* loaded from: classes5.dex */
public class ProvisionAnimHelper {
    private AnimListener mAnimListener;
    private int mAnimY;
    private Context mContext;
    private Handler mHandler;
    private IProvisionAnim proxy;
    private int mSkipOrNext = 0;
    private IAnimCallback callback = new IAnimCallback.Stub() { // from class: miuix.provision.ProvisionAnimHelper.1
        @Override // com.android.provision.IAnimCallback
        public void onBackAnimStart() throws RemoteException {
            Log.d("OobeUtil2", "onBackAnimStart");
            if (ProvisionAnimHelper.this.mHandler == null) {
                return;
            }
            ProvisionAnimHelper.this.mHandler.postDelayed(new Runnable() { // from class: miuix.provision.ProvisionAnimHelper.1.2
                @Override // java.lang.Runnable
                public void run() {
                    if (ProvisionAnimHelper.this.mAnimListener != null) {
                        ProvisionAnimHelper.this.mAnimListener.onBackAnimStart();
                    }
                }
            }, 30L);
        }

        @Override // com.android.provision.IAnimCallback
        public void onNextAminStart() throws RemoteException {
            Log.d("OobeUtil2", "onNextAminStart:" + ProvisionAnimHelper.this.mSkipOrNext);
            if (ProvisionAnimHelper.this.mHandler == null) {
                return;
            }
            ProvisionAnimHelper.this.mHandler.post(new Runnable() { // from class: miuix.provision.ProvisionAnimHelper.1.1
                @Override // java.lang.Runnable
                public void run() {
                    if (ProvisionAnimHelper.this.mAnimListener != null) {
                        if (ProvisionAnimHelper.this.mSkipOrNext == 0) {
                            ProvisionAnimHelper.this.mAnimListener.onNextAminStart();
                        } else if (ProvisionAnimHelper.this.mSkipOrNext == 1) {
                            ProvisionAnimHelper.this.mAnimListener.onSkipAminStart();
                        }
                    }
                }
            });
        }
    };
    private ServiceConnection conn = new ServiceConnection() { // from class: miuix.provision.ProvisionAnimHelper.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ProvisionAnimHelper.this.proxy = IProvisionAnim.Stub.asInterface(iBinder);
            try {
                ProvisionAnimHelper.this.proxy.registerRemoteCallback(ProvisionAnimHelper.this.callback);
                ProvisionAnimHelper.this.mAnimListener.onAminServiceConnected();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() { // from class: miuix.provision.ProvisionAnimHelper.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent == null || !intent.getAction().equals("miui.action.PROVISION_ANIM_END") || ProvisionAnimHelper.this.mAnimListener == null) {
                return;
            }
            ProvisionAnimHelper.this.mAnimListener.onAminEnd();
        }
    };

    /* loaded from: classes5.dex */
    public interface AnimListener {
        void onAminEnd();

        void onAminServiceConnected();

        void onBackAnimStart();

        void onNextAminStart();

        void onSkipAminStart();
    }

    public ProvisionAnimHelper(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    public boolean goBackStep() {
        try {
            this.proxy.playBackAnim(this.mAnimY);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean goNextStep(int i) {
        try {
            this.mSkipOrNext = i;
            this.proxy.playNextAnim(this.mAnimY);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isAnimEnded() {
        try {
            return this.proxy.isAnimEnd();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public void registerAnimService() {
        if (this.mContext == null) {
            Log.e("OobeUtil2", "registerAnimService context is null");
            return;
        }
        this.mContext.registerReceiver(this.receiver, new IntentFilter("miui.action.PROVISION_ANIM_END"));
        Intent intent = new Intent("miui.intent.action.OOBSERVICE");
        intent.setPackage("com.android.provision");
        this.mContext.bindService(intent, this.conn, 1);
    }

    public void setAnimListener(AnimListener animListener) {
        this.mAnimListener = animListener;
    }

    public void setAnimY(int i) {
        this.mAnimY = i;
    }

    public void unregisterAnimService() {
        try {
            this.proxy.unregisterRemoteCallback(this.callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Context context = this.mContext;
        if (context != null) {
            context.unbindService(this.conn);
            this.mContext.unregisterReceiver(this.receiver);
        }
    }
}
