package com.milink.api.v1;

import android.os.RemoteException;
import com.milink.api.v1.aidl.IMcsDataSource;

/* loaded from: classes2.dex */
public class McsDataSource extends IMcsDataSource.Stub {
    MilinkClientManagerDataSource mDataSource = null;

    @Override // com.milink.api.v1.aidl.IMcsDataSource
    public String getNextPhoto(String str, boolean z) throws RemoteException {
        MilinkClientManagerDataSource milinkClientManagerDataSource = this.mDataSource;
        if (milinkClientManagerDataSource == null) {
            return null;
        }
        return milinkClientManagerDataSource.getNextPhoto(str, z);
    }

    @Override // com.milink.api.v1.aidl.IMcsDataSource
    public String getPrevPhoto(String str, boolean z) throws RemoteException {
        MilinkClientManagerDataSource milinkClientManagerDataSource = this.mDataSource;
        if (milinkClientManagerDataSource == null) {
            return null;
        }
        return milinkClientManagerDataSource.getPrevPhoto(str, z);
    }

    public void setDataSource(MilinkClientManagerDataSource milinkClientManagerDataSource) {
        this.mDataSource = milinkClientManagerDataSource;
    }
}
