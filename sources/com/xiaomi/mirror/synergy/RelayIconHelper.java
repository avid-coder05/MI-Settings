package com.xiaomi.mirror.synergy;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

/* loaded from: classes2.dex */
public class RelayIconHelper implements IBinder.DeathRecipient {
    private IBinder mAliveBinder;
    private RelayIconCallback mCallback;
    private ContentObserver mUpdateObserver;
    private ContentObserver mVisibleObserver;
    private final Uri uriVisible = MiuiSynergySdk.getUriFor("mirror_relay_icon_visible");
    private final Uri uriUpdate = MiuiSynergySdk.getUriFor("mirror_relay_icon_update");

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class RelayIcon {
        private final Bitmap bitmap;
        private final String packageName;

        public RelayIcon(String str, Bitmap bitmap) {
            this.packageName = str;
            this.bitmap = bitmap;
        }

        public Bitmap getBitmap() {
            return this.bitmap;
        }

        public String getPackageName() {
            return this.packageName;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkRemoteProcess(Context context) {
        if (this.mAliveBinder != null) {
            return;
        }
        IBinder aliveBinder = MiuiSynergySdk.getInstance().getAliveBinder(context);
        this.mAliveBinder = aliveBinder;
        if (aliveBinder != null) {
            try {
                aliveBinder.linkToDeath(this, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public RelayIcon getRelayIcon(Context context, Uri uri) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("uri", uri);
        try {
            Bundle doCall = CallMethod.doCall(context.getContentResolver(), "getUpdateIcon", null, bundle);
            if (doCall == null) {
                return null;
            }
            return new RelayIcon(doCall.getString("value"), (Bitmap) doCall.getParcelable("icon"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void performRelayIconClick(Context context, String str) {
        Bundle bundle = new Bundle();
        bundle.putString("extra", str);
        try {
            CallMethod.doCall(context.getContentResolver(), "performRelayIconClick", null, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        RelayIconCallback relayIconCallback = this.mCallback;
        if (relayIconCallback != null) {
            relayIconCallback.onIconHide();
        }
        IBinder iBinder = this.mAliveBinder;
        if (iBinder != null) {
            iBinder.unlinkToDeath(this, 0);
            this.mAliveBinder = null;
        }
    }

    public void performIconClick(Context context, String str) {
        performRelayIconClick(context, str);
    }

    public void registerCallback(final Context context, RelayIconCallback relayIconCallback, Handler handler) {
        if (relayIconCallback == null) {
            return;
        }
        this.mCallback = relayIconCallback;
        this.mVisibleObserver = new ContentObserver(handler) { // from class: com.xiaomi.mirror.synergy.RelayIconHelper.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                if (RelayIconHelper.this.mCallback != null) {
                    if (MiuiSynergySdk.getInstance().getInt(context, RelayIconHelper.this.uriVisible, 0) != 0) {
                        RelayIconHelper.this.mCallback.onIconShow();
                    } else {
                        RelayIconHelper.this.mCallback.onIconHide();
                    }
                }
                RelayIconHelper.this.checkRemoteProcess(context);
            }
        };
        context.getContentResolver().registerContentObserver(this.uriVisible, false, this.mVisibleObserver);
        this.mUpdateObserver = new ContentObserver(handler) { // from class: com.xiaomi.mirror.synergy.RelayIconHelper.2
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                if (RelayIconHelper.this.mCallback != null) {
                    RelayIconHelper relayIconHelper = RelayIconHelper.this;
                    RelayIcon relayIcon = relayIconHelper.getRelayIcon(context, relayIconHelper.uriUpdate);
                    if (relayIcon != null) {
                        RelayIconHelper.this.mCallback.onIconUpdate(relayIcon.getPackageName(), relayIcon.getBitmap());
                    }
                }
            }
        };
        context.getContentResolver().registerContentObserver(this.uriUpdate, false, this.mUpdateObserver);
        checkRemoteProcess(context);
    }

    public void unRegisterCallback(Context context) {
        this.mCallback = null;
        if (this.mVisibleObserver != null) {
            context.getContentResolver().unregisterContentObserver(this.mVisibleObserver);
        }
        if (this.mUpdateObserver != null) {
            context.getContentResolver().unregisterContentObserver(this.mUpdateObserver);
        }
        IBinder iBinder = this.mAliveBinder;
        if (iBinder != null) {
            iBinder.unlinkToDeath(this, 0);
        }
    }
}
