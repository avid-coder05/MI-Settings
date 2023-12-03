package miui.upnp.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/* loaded from: classes4.dex */
public abstract class Binding {
    private static final String TAG = "Binding";
    private ServiceConnection connection;
    private Context context;
    private final ServiceState serviceState = new ServiceState();

    /* loaded from: classes4.dex */
    private class MyServiceConnection implements ServiceConnection {
        private MyServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(Binding.TAG, String.format("onServiceConnected: %s", componentName.getShortClassName()));
            Binding.this.onServiceConnected(componentName, iBinder);
            synchronized (Binding.this.serviceState) {
                Binding.this.serviceState.value = State.BOUND;
                if (Binding.this.serviceState.waiting) {
                    Binding.this.serviceState.notify();
                }
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(Binding.TAG, String.format("onServiceDisconnected: %s", componentName.getShortClassName()));
            Binding.this.onServiceDisconnected(componentName);
            synchronized (Binding.this.serviceState) {
                Binding.this.serviceState.value = State.UNBIND;
                if (Binding.this.serviceState.waiting) {
                    Binding.this.serviceState.notify();
                }
            }
        }
    }

    /* loaded from: classes4.dex */
    private class ServiceState {
        State value;
        boolean waiting;

        private ServiceState() {
            this.waiting = false;
            this.value = State.UNBIND;
        }
    }

    /* loaded from: classes4.dex */
    private enum State {
        UNBIND,
        BINDING,
        BOUND
    }

    public Binding(Context context) {
        this.context = context.getApplicationContext();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public synchronized boolean bind(String str, String str2) {
        if (this.context == null) {
            Log.d(TAG, "context is null");
        } else {
            ServiceState serviceState = this.serviceState;
            State state = serviceState.value;
            State state2 = State.UNBIND;
            if (state != state2) {
                Log.e(TAG, String.format("bind, but serviceState is: %s", state.toString()));
            } else {
                State state3 = State.BINDING;
                serviceState.value = state3;
                MyServiceConnection myServiceConnection = new MyServiceConnection();
                Intent intent = new Intent(str2);
                intent.setPackage(str);
                if (this.context.bindService(intent, myServiceConnection, 1)) {
                    this.connection = myServiceConnection;
                    synchronized (this.serviceState) {
                        if (this.serviceState.value == state3) {
                            Log.d(TAG, String.format("(%s) waiting...", str2));
                            try {
                                ServiceState serviceState2 = this.serviceState;
                                serviceState2.waiting = true;
                                serviceState2.wait();
                                this.serviceState.waiting = false;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "bindService failed");
                    this.serviceState.value = state2;
                }
            }
        }
        return this.serviceState.value == State.BOUND;
    }

    public Context getContext() {
        return this.context;
    }

    public boolean isBound() {
        return this.serviceState.value == State.BOUND;
    }

    protected abstract void onServiceConnected(ComponentName componentName, IBinder iBinder);

    protected abstract void onServiceDisconnected(ComponentName componentName);

    public synchronized boolean unbind() {
        State state;
        String str = TAG;
        Log.d(str, "unbind");
        State state2 = this.serviceState.value;
        state = State.UNBIND;
        if (state2 == state) {
            Log.e(str, String.format("unbind, but serviceState is: %s", state2.toString()));
        } else {
            ServiceConnection serviceConnection = this.connection;
            if (serviceConnection != null) {
                this.context.unbindService(serviceConnection);
                this.connection = null;
                onServiceDisconnected(null);
            }
            this.serviceState.value = state;
        }
        return this.serviceState.value == state;
    }
}
