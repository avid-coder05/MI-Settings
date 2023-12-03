package com.xiaomi.accountsdk.futureservice;

/* loaded from: classes2.dex */
public class SimpleClientFuture<DataType> extends ClientFuture<DataType, DataType> {
    public SimpleClientFuture() {
        super(null);
    }

    @Override // com.xiaomi.accountsdk.futureservice.ClientFuture
    protected DataType convertServerDataToClientData(DataType datatype) throws Throwable {
        return datatype;
    }
}
