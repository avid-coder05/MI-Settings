package com.android.settingslib.development;

import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

/* loaded from: classes2.dex */
public class SystemPropPoker {
    private static final SystemPropPoker sInstance = new SystemPropPoker();
    private boolean mBlockPokes = false;

    /* loaded from: classes2.dex */
    public static class PokerTask extends AsyncTask<Void, Void, Void> {
        IBinder checkService(String str) {
            return ServiceManager.checkService(str);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            String[] listServices = listServices();
            if (listServices == null) {
                Log.e("SystemPropPoker", "There are no services, how odd");
                return null;
            }
            for (String str : listServices) {
                IBinder checkService = checkService(str);
                if (checkService != null) {
                    Parcel obtain = Parcel.obtain();
                    try {
                        checkService.transact(1599295570, obtain, null, 0);
                    } catch (RemoteException unused) {
                    } catch (Exception e) {
                        Log.i("SystemPropPoker", "Someone wrote a bad service '" + str + "' that doesn't like to be poked", e);
                    }
                    obtain.recycle();
                }
            }
            return null;
        }

        String[] listServices() {
            return ServiceManager.listServices();
        }
    }

    private SystemPropPoker() {
    }

    public static SystemPropPoker getInstance() {
        return sInstance;
    }

    public void blockPokes() {
        this.mBlockPokes = true;
    }

    PokerTask createPokerTask() {
        return new PokerTask();
    }

    public void poke() {
        if (this.mBlockPokes) {
            return;
        }
        createPokerTask().execute(new Void[0]);
    }

    public void unblockPokes() {
        this.mBlockPokes = false;
    }
}
