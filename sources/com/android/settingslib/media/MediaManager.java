package com.android.settingslib.media;

import android.app.Notification;
import android.content.Context;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: classes2.dex */
public abstract class MediaManager {
    protected Context mContext;
    protected Notification mNotification;
    protected final Collection<MediaDeviceCallback> mCallbacks = new CopyOnWriteArrayList();
    protected final List<MediaDevice> mMediaDevices = new ArrayList();

    /* loaded from: classes2.dex */
    public interface MediaDeviceCallback {
        void onConnectedDeviceChanged(String str);

        void onDeviceAttributesChanged();

        void onDeviceListAdded(List<MediaDevice> list);

        void onRequestFailed(int i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaManager(Context context, Notification notification) {
        this.mContext = context;
        this.mNotification = notification;
    }

    private Collection<MediaDeviceCallback> getCallbacks() {
        return new CopyOnWriteArrayList(this.mCallbacks);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dispatchConnectedDeviceChanged(String str) {
        Iterator<MediaDeviceCallback> it = getCallbacks().iterator();
        while (it.hasNext()) {
            it.next().onConnectedDeviceChanged(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dispatchDataChanged() {
        Iterator<MediaDeviceCallback> it = getCallbacks().iterator();
        while (it.hasNext()) {
            it.next().onDeviceAttributesChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dispatchDeviceListAdded() {
        Iterator<MediaDeviceCallback> it = getCallbacks().iterator();
        while (it.hasNext()) {
            it.next().onDeviceListAdded(new ArrayList(this.mMediaDevices));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dispatchOnRequestFailed(int i) {
        Iterator<MediaDeviceCallback> it = getCallbacks().iterator();
        while (it.hasNext()) {
            it.next().onRequestFailed(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void registerCallback(MediaDeviceCallback mediaDeviceCallback) {
        if (this.mCallbacks.contains(mediaDeviceCallback)) {
            return;
        }
        this.mCallbacks.add(mediaDeviceCallback);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void unregisterCallback(MediaDeviceCallback mediaDeviceCallback) {
        if (this.mCallbacks.contains(mediaDeviceCallback)) {
            this.mCallbacks.remove(mediaDeviceCallback);
        }
    }
}
