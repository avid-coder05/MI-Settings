package com.iqiyi.android.qigsaw.core.extension;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.ProviderInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public abstract class ContentProviderProxy extends ContentProvider {
    private static final String NAME_INFIX = "_Decorated_";
    private ProviderInfo providerInfo;
    private ContentProvider realContentProvider;
    private String realContentProviderClassName;
    private String splitName;

    @Override // android.content.ContentProvider
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> arrayList) throws OperationApplicationException {
        return checkRealContentProviderInstallStatus(this.splitName) ? this.realContentProvider.applyBatch(arrayList) : super.applyBatch(arrayList);
    }

    @Override // android.content.ContentProvider
    public void attachInfo(Context context, ProviderInfo providerInfo) {
        String[] split = getClass().getName().split(NAME_INFIX);
        this.realContentProviderClassName = split[0];
        this.splitName = split[1];
        super.attachInfo(context, providerInfo);
        this.providerInfo = new ProviderInfo(providerInfo);
        AABExtension.getInstance().put(this.splitName, this);
    }

    @Override // android.content.ContentProvider
    public int bulkInsert(Uri uri, ContentValues[] contentValuesArr) {
        return checkRealContentProviderInstallStatus(this.splitName) ? this.realContentProvider.bulkInsert(uri, contentValuesArr) : super.bulkInsert(uri, contentValuesArr);
    }

    @Override // android.content.ContentProvider
    public Bundle call(String str, String str2, Bundle bundle) {
        return checkRealContentProviderInstallStatus(this.splitName) ? this.realContentProvider.call(str, str2, bundle) : super.call(str, str2, bundle);
    }

    @Override // android.content.ContentProvider
    public Uri canonicalize(Uri uri) {
        return getRealContentProvider() != null ? this.realContentProvider.canonicalize(uri) : super.canonicalize(uri);
    }

    protected abstract boolean checkRealContentProviderInstallStatus(String str);

    /* JADX INFO: Access modifiers changed from: package-private */
    public void createAndActivateRealContentProvider(ClassLoader classLoader) throws AABExtensionException {
        String str = this.realContentProviderClassName;
        if (str == null) {
            throw new AABExtensionException("Unable to read real content-provider for " + getClass().getName());
        }
        Throwable th = null;
        try {
            ContentProvider contentProvider = (ContentProvider) classLoader.loadClass(str).newInstance();
            this.realContentProvider = contentProvider;
            contentProvider.attachInfo(getContext(), this.providerInfo);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            th = e;
        }
        if (th != null) {
            throw new AABExtensionException(th);
        }
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        if (checkRealContentProviderInstallStatus(this.splitName)) {
            return this.realContentProvider.delete(uri, str, strArr);
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ContentProvider getRealContentProvider() {
        return this.realContentProvider;
    }

    @Override // android.content.ContentProvider
    public String[] getStreamTypes(Uri uri, String str) {
        return checkRealContentProviderInstallStatus(this.splitName) ? this.realContentProvider.getStreamTypes(uri, str) : super.getStreamTypes(uri, str);
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        if (checkRealContentProviderInstallStatus(this.splitName)) {
            return this.realContentProvider.getType(uri);
        }
        return null;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        if (checkRealContentProviderInstallStatus(this.splitName)) {
            return this.realContentProvider.insert(uri, contentValues);
        }
        return null;
    }

    @Override // android.content.ContentProvider, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (checkRealContentProviderInstallStatus(this.splitName)) {
            this.realContentProvider.onConfigurationChanged(configuration);
        }
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider, android.content.ComponentCallbacks
    public void onLowMemory() {
        super.onLowMemory();
        ContentProvider contentProvider = this.realContentProvider;
        if (contentProvider != null) {
            contentProvider.onLowMemory();
        }
    }

    @Override // android.content.ContentProvider, android.content.ComponentCallbacks2
    public void onTrimMemory(int i) {
        super.onTrimMemory(i);
        ContentProvider contentProvider = this.realContentProvider;
        if (contentProvider != null) {
            contentProvider.onTrimMemory(i);
        }
    }

    @Override // android.content.ContentProvider
    public AssetFileDescriptor openAssetFile(Uri uri, String str) throws FileNotFoundException {
        return checkRealContentProviderInstallStatus(this.splitName) ? this.realContentProvider.openAssetFile(uri, str) : super.openAssetFile(uri, str);
    }

    @Override // android.content.ContentProvider
    public AssetFileDescriptor openAssetFile(Uri uri, String str, CancellationSignal cancellationSignal) throws FileNotFoundException {
        return checkRealContentProviderInstallStatus(this.splitName) ? this.realContentProvider.openAssetFile(uri, str, cancellationSignal) : super.openAssetFile(uri, str, cancellationSignal);
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String str) throws FileNotFoundException {
        return checkRealContentProviderInstallStatus(this.splitName) ? this.realContentProvider.openFile(uri, str) : super.openFile(uri, str);
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String str, CancellationSignal cancellationSignal) throws FileNotFoundException {
        return checkRealContentProviderInstallStatus(this.splitName) ? this.realContentProvider.openFile(uri, str, cancellationSignal) : super.openFile(uri, str, cancellationSignal);
    }

    @Override // android.content.ContentProvider
    public <T> ParcelFileDescriptor openPipeHelper(Uri uri, String str, Bundle bundle, T t, ContentProvider.PipeDataWriter<T> pipeDataWriter) throws FileNotFoundException {
        return checkRealContentProviderInstallStatus(this.splitName) ? this.realContentProvider.openPipeHelper(uri, str, bundle, t, pipeDataWriter) : super.openPipeHelper(uri, str, bundle, t, pipeDataWriter);
    }

    @Override // android.content.ContentProvider
    public AssetFileDescriptor openTypedAssetFile(Uri uri, String str, Bundle bundle) throws FileNotFoundException {
        return checkRealContentProviderInstallStatus(this.splitName) ? this.realContentProvider.openTypedAssetFile(uri, str, bundle) : super.openTypedAssetFile(uri, str, bundle);
    }

    @Override // android.content.ContentProvider
    public AssetFileDescriptor openTypedAssetFile(Uri uri, String str, Bundle bundle, CancellationSignal cancellationSignal) throws FileNotFoundException {
        return checkRealContentProviderInstallStatus(this.splitName) ? this.realContentProvider.openTypedAssetFile(uri, str, bundle, cancellationSignal) : super.openTypedAssetFile(uri, str, bundle, cancellationSignal);
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, Bundle bundle, CancellationSignal cancellationSignal) {
        return checkRealContentProviderInstallStatus(this.splitName) ? this.realContentProvider.query(uri, strArr, bundle, cancellationSignal) : super.query(uri, strArr, bundle, cancellationSignal);
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        if (checkRealContentProviderInstallStatus(this.splitName)) {
            return this.realContentProvider.query(uri, strArr, str, strArr2, str2);
        }
        return null;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2, CancellationSignal cancellationSignal) {
        return checkRealContentProviderInstallStatus(this.splitName) ? this.realContentProvider.query(uri, strArr, str, strArr2, str2, cancellationSignal) : super.query(uri, strArr, str, strArr2, str2, cancellationSignal);
    }

    @Override // android.content.ContentProvider
    public boolean refresh(Uri uri, Bundle bundle, CancellationSignal cancellationSignal) {
        return checkRealContentProviderInstallStatus(this.splitName) ? this.realContentProvider.refresh(uri, bundle, cancellationSignal) : super.refresh(uri, bundle, cancellationSignal);
    }

    @Override // android.content.ContentProvider
    public Uri uncanonicalize(Uri uri) {
        return checkRealContentProviderInstallStatus(this.splitName) ? this.realContentProvider.uncanonicalize(uri) : super.uncanonicalize(uri);
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        if (checkRealContentProviderInstallStatus(this.splitName)) {
            return this.realContentProvider.update(uri, contentValues, str, strArr);
        }
        return 0;
    }
}
