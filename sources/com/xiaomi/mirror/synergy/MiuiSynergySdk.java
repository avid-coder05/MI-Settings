package com.xiaomi.mirror.synergy;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import com.android.settings.network.telephony.ToggleSubscriptionDialogActivity;
import com.xiaomi.mirror.ISameAccountApCallback;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/* loaded from: classes2.dex */
public class MiuiSynergySdk {
    public static final int SYNERGY_ERR = -1;
    public static final int SYNERGY_OK = 0;
    public static final int SYNERGY_SOFTAP_ALREADY_CONNECTED = 2;
    public static final int SYNERGY_SOFTAP_ALREADY_CONNECTING = 1;
    private static final String TAG = "MiuiSynergy";
    private final Executor mExecutor = Executors.newCachedThreadPool();
    private final ISameAccountApCallback mISameAccountApCallback = new ISameAccountApCallback.Stub() { // from class: com.xiaomi.mirror.synergy.MiuiSynergySdk.4
        @Override // com.xiaomi.mirror.ISameAccountApCallback
        public void onApConnectedStatusUpdate(int i, Bundle bundle) {
            if (MiuiSynergySdk.this.mSameAccountApCallback != null) {
                MiuiSynergySdk.this.mSameAccountApCallback.onApConnectedStatusUpdate(i, bundle == null ? null : new SameAccountAccessPoint(bundle));
            }
        }

        @Override // com.xiaomi.mirror.ISameAccountApCallback
        public void onApInfoUpdate(Bundle bundle) {
            if (MiuiSynergySdk.this.mSameAccountApCallback != null) {
                MiuiSynergySdk.this.mSameAccountApCallback.onApInfoUpdate(bundle == null ? null : new SameAccountAccessPoint(bundle));
            }
        }
    };
    private SameAccountApCallback mSameAccountApCallback;

    /* loaded from: classes2.dex */
    public interface ChooseFileCallback {
        void onFileChosen(ClipData clipData);
    }

    /* loaded from: classes2.dex */
    private static final class Holder {
        private static final MiuiSynergySdk INSTANCE = new MiuiSynergySdk();

        private Holder() {
        }
    }

    /* loaded from: classes2.dex */
    public static class Option {
        public Bitmap icon;
        private String id;
        public String title;

        public int invoke(Activity activity, Uri uri, String str) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("uri", uri);
            bundle.putString("extra", str);
            bundle.putString("id", this.id);
            bundle.putInt("displayId", activity.getWindow().getDecorView().getDisplay().getDisplayId());
            try {
                CallMethod.doCall(activity.getContentResolver(), "openOnSynergy", null, bundle);
                return 0;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    /* loaded from: classes2.dex */
    public interface QueryOpenCallback {
        void onQueryResult(List<Option> list);
    }

    /* loaded from: classes2.dex */
    public static class SameAccountAccessPoint {
        private int batteryPercent;
        private boolean is5G;
        private String ssid;

        public SameAccountAccessPoint(Bundle bundle) {
            this.batteryPercent = -1;
            if (bundle == null) {
                return;
            }
            this.ssid = bundle.getString("apSsid");
            this.is5G = bundle.getBoolean("apId5G");
            this.batteryPercent = bundle.getInt("batteryPercent", -1);
        }

        public int getBatteryPercent() {
            return this.batteryPercent;
        }

        public String getSsid() {
            return this.ssid;
        }

        public boolean isIs5G() {
            return this.is5G;
        }
    }

    /* loaded from: classes2.dex */
    public interface SameAccountApCallback {
        void onApConnectedStatusUpdate(int i, SameAccountAccessPoint sameAccountAccessPoint);

        void onApInfoUpdate(SameAccountAccessPoint sameAccountAccessPoint);
    }

    public static MiuiSynergySdk getInstance() {
        return Holder.INSTANCE;
    }

    public static Uri getUriFor(String str) {
        return new Uri.Builder().scheme("content").authority("com.xiaomi.mirror.callprovider").path(str).build();
    }

    public int chooseFileOnSynergy(Activity activity, final ChooseFileCallback chooseFileCallback) {
        final Bundle bundle = new Bundle();
        bundle.putInt("displayId", activity.getWindow().getDecorView().getDisplay().getDisplayId());
        final ContentResolver contentResolver = activity.getContentResolver();
        this.mExecutor.execute(new Runnable() { // from class: com.xiaomi.mirror.synergy.MiuiSynergySdk.2
            @Override // java.lang.Runnable
            public void run() {
                Bundle doCall = CallMethod.doCall(contentResolver, "chooseFileFromSynergy", null, bundle);
                chooseFileCallback.onFileChosen(doCall != null ? (ClipData) doCall.getParcelable("clipData") : null);
            }
        });
        return 0;
    }

    public int connectSameAccountAp(Context context, String str) {
        Bundle bundle = new Bundle();
        bundle.putString("apSsid", str);
        try {
            Bundle doCall = CallMethod.doCall(context.getContentResolver(), "connectSameAccountAp", null, bundle);
            if (doCall == null) {
                return -1;
            }
            return doCall.getInt("softApState", -1);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public IBinder getAliveBinder(Context context) {
        Bundle doCall = CallMethod.doCall(context.getContentResolver(), "getAliveBinder", null, null);
        if (doCall == null) {
            return null;
        }
        return doCall.getBinder("binder");
    }

    public CallRelayService getCallRelayService(Context context) {
        IBinder binder;
        Bundle doCall = CallMethod.doCall(context.getContentResolver(), "getCallRelayService", null, null);
        if (doCall == null || (binder = doCall.getBinder("binder")) == null) {
            return null;
        }
        return new CallRelayService(binder);
    }

    public int getInt(Context context, Uri uri, int i) {
        String string = getString(context, uri, null);
        if (string != null) {
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException unused) {
            }
        }
        return i;
    }

    public String getString(Context context, Uri uri, String str) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("uri", uri);
        try {
            Bundle doCall = CallMethod.doCall(context.getContentResolver(), "get", null, bundle);
            return doCall == null ? str : doCall.getString("value");
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    public boolean isFloatWindowShow(Context context) {
        try {
            Bundle doCall = CallMethod.doCall(context.getContentResolver(), "isFloatWindowShow", null, null);
            if (doCall != null) {
                if (doCall.getBoolean("isFloatWindowShow")) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isP2PWorking(Context context) {
        try {
            Bundle doCall = CallMethod.doCall(context.getContentResolver(), "isP2PWorking", null, null);
            if (doCall != null) {
                return doCall.getBoolean(ToggleSubscriptionDialogActivity.ARG_enable);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isSupportTakePhoto(Context context) {
        try {
            Bundle doCall = CallMethod.doCall(context.getContentResolver(), "isSupportTakePhoto", null, null);
            if (doCall != null) {
                return doCall.getBoolean(ToggleSubscriptionDialogActivity.ARG_enable);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isSynergyEnable(Context context) {
        try {
            Bundle doCall = CallMethod.doCall(context.getContentResolver(), "isSynergyEnable", null, null);
            if (doCall != null) {
                return doCall.getBoolean(ToggleSubscriptionDialogActivity.ARG_enable);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ParcelFileDescriptor openDirect(Context context, Uri uri) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("uri", uri);
        try {
            Bundle doCall = CallMethod.doCall(context.getContentResolver(), "openDirect", null, bundle);
            if (doCall == null) {
                return null;
            }
            return (ParcelFileDescriptor) doCall.getParcelable("fileDescriptor");
        } catch (Exception e) {
            throw new IOException("open failed", e);
        }
    }

    public int openMiCloudOnSynergy(Context context) {
        try {
            CallMethod.doCall(context.getContentResolver(), "openMiCloudOnSynergy", null, null);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int openOnSynergy(Activity activity, Uri uri, String str) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("uri", uri);
        bundle.putString("extra", str);
        bundle.putInt("displayId", activity.getWindow().getDecorView().getDisplay().getDisplayId());
        try {
            CallMethod.doCall(activity.getContentResolver(), "openOnSynergy", null, bundle);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int queryOpenOnSynergy(Context context, String str, final QueryOpenCallback queryOpenCallback) {
        final Bundle bundle = new Bundle();
        bundle.putString("extension", str);
        final ContentResolver contentResolver = context.getContentResolver();
        this.mExecutor.execute(new Runnable() { // from class: com.xiaomi.mirror.synergy.MiuiSynergySdk.1
            @Override // java.lang.Runnable
            public void run() {
                Bundle doCall = CallMethod.doCall(contentResolver, "queryOpenOnSynergy", null, bundle);
                if (doCall == null) {
                    queryOpenCallback.onQueryResult(null);
                    return;
                }
                ArrayList parcelableArrayList = doCall.getParcelableArrayList("optionList");
                if (parcelableArrayList == null) {
                    queryOpenCallback.onQueryResult(null);
                    return;
                }
                ArrayList arrayList = new ArrayList(parcelableArrayList.size());
                Iterator it = parcelableArrayList.iterator();
                while (it.hasNext()) {
                    Bundle bundle2 = (Bundle) it.next();
                    Option option = new Option();
                    option.id = bundle2.getString("id");
                    option.title = bundle2.getString("title");
                    option.icon = (Bitmap) bundle2.getParcelable("icon");
                    arrayList.add(option);
                }
                queryOpenCallback.onQueryResult(arrayList);
            }
        });
        return 0;
    }

    public SameAccountAccessPoint querySameAccountApInfo(Context context) {
        try {
            Bundle doCall = CallMethod.doCall(context.getContentResolver(), "querySameAccountAp", null, null);
            if (doCall != null && !TextUtils.isEmpty(doCall.getString("apSsid"))) {
                return new SameAccountAccessPoint(doCall);
            }
        } catch (Exception unused) {
        }
        return null;
    }

    public int registerSameAccountApCallback(Context context, SameAccountApCallback sameAccountApCallback) {
        try {
            Bundle bundle = new Bundle();
            bundle.putBinder("apCallback", this.mISameAccountApCallback.asBinder());
            this.mSameAccountApCallback = sameAccountApCallback;
            CallMethod.doCall(context.getContentResolver(), "registerApCallback", null, bundle);
            return 0;
        } catch (Exception unused) {
            return -1;
        }
    }

    public int saveToSynergy(Activity activity, ClipData clipData, String str) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("clipData", clipData);
        bundle.putString("extra", str);
        bundle.putInt("displayId", activity.getWindow().getDecorView().getDisplay().getDisplayId());
        try {
            CallMethod.doCall(activity.getContentResolver(), "saveToSynergy", null, bundle);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int takePhotoCancel(Activity activity) {
        Bundle bundle = new Bundle();
        bundle.putInt("displayId", activity.getWindow().getDecorView().getDisplay().getDisplayId());
        try {
            CallMethod.doCall(activity.getContentResolver(), "takePhotoCancel", null, bundle);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int takePhotoOnSynergy(Activity activity, final ChooseFileCallback chooseFileCallback) {
        final Bundle bundle = new Bundle();
        bundle.putInt("displayId", activity.getWindow().getDecorView().getDisplay().getDisplayId());
        final ContentResolver contentResolver = activity.getContentResolver();
        this.mExecutor.execute(new Runnable() { // from class: com.xiaomi.mirror.synergy.MiuiSynergySdk.3
            @Override // java.lang.Runnable
            public void run() {
                Bundle doCall = CallMethod.doCall(contentResolver, "takePhotoFromSynergy", null, bundle);
                chooseFileCallback.onFileChosen(doCall != null ? (ClipData) doCall.getParcelable("clipData") : null);
            }
        });
        return 0;
    }

    public int unRegisterSameAccountApCallback(Context context) {
        try {
            CallMethod.doCall(context.getContentResolver(), "unRegisterApCallback", null, null);
            this.mSameAccountApCallback = null;
            return 0;
        } catch (Exception unused) {
            return -1;
        }
    }

    public int updateTitle(Context context, String str) {
        Bundle bundle = new Bundle();
        bundle.putString("title", str);
        try {
            CallMethod.doCall(context.getContentResolver(), "updateTitle", null, bundle);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
